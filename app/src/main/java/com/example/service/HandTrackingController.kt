package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.model.GestureEvent
import com.example.model.GestureTrackingConfig
import com.example.model.HandGestureData
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Controller managing MediaPipe HandLandmarker and CameraX frame analysis.
 * Designed for low-memory constraints (e.g. 4GB RAM devices) by:
 * - Lazy loading the neural model only upon explicit tracking activation.
 * - Confining image acquisition and landmark inference strictly to a background thread.
 * - Throttling inference to 10-15 FPS.
 * - Tracking a single hand with hysteresis and exponential moving average (EMA) smoothing.
 */
class HandTrackingController(
    private val context: Context,
    val config: GestureTrackingConfig = GestureTrackingConfig()
) : AutoCloseable {

    companion object {
        private const val TAG = "HandTrackingController"
        private const val MODEL_ASSET = "hand_landmarker.task"
        private const val THUMB_TIP_INDEX = 4
        private const val INDEX_TIP_INDEX = 8
    }

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    private val _gestureState = MutableStateFlow(HandGestureData())
    val gestureState: StateFlow<HandGestureData> = _gestureState.asStateFlow()

    // Lazy background executor: initialized only when tracking starts
    @Volatile
    private var backgroundExecutor: ExecutorService? = null

    // Lazy MediaPipe HandLandmarker instance
    @Volatile
    private var handLandmarker: HandLandmarker? = null

    @Volatile
    private var cameraProvider: ProcessCameraProvider? = null

    // Tracking math state (EMA and hysteresis)
    private var smoothedDistance: Float = 1.0f
    private var smoothedHandX: Float = 0.5f
    private var smoothedHandY: Float = 0.5f
    private var isCurrentlyPinching: Boolean = false
    private var lastInferenceTimestampMs: Long = 0L
    private var lastBloomReleaseTimestampMs: Long = 0L

    // FPS measurement
    private var frameCount: Int = 0
    private var lastFpsTimestampMs: Long = System.currentTimeMillis()
    private var currentFps: Float = 0f

    /**
     * Lazy-loads the MediaPipe HandLandmarker model on a background thread.
     * Does nothing if already initialized.
     */
    suspend fun initializeModel(): Boolean = withContext(Dispatchers.IO) {
        if (handLandmarker != null) return@withContext true

        try {
            Log.d(TAG, "Lazy-loading MediaPipe HandLandmarker model ($MODEL_ASSET)...")
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(MODEL_ASSET)
                .setDelegate(Delegate.CPU) // CPU delegate ensures maximum stability on low-RAM & emulators
                .build()

            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setNumHands(1) // Single hand for bounded CPU/RAM footprint
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, mpImage ->
                    processLandmarksResult(result, mpImage)
                }
                .setErrorListener { error ->
                    Log.e(TAG, "MediaPipe error: ${error.message}", error)
                    _gestureState.update { it.copy(errorMessage = "Inference: ${error.message}") }
                }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
            _gestureState.update { it.copy(isModelLoaded = true, errorMessage = null) }
            Log.d(TAG, "MediaPipe HandLandmarker successfully initialized")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize HandLandmarker", e)
            _gestureState.update {
                it.copy(
                    isModelLoaded = false,
                    errorMessage = "Model load failed: ${e.localizedMessage ?: "Unknown error"}"
                )
            }
            false
        }
    }

    /**
     * Starts tracking with CameraX and MediaPipe.
     * Must be called with a valid LifecycleOwner (e.g. from an Activity or Composable).
     */
    fun startTracking(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        onStarted: ((Boolean) -> Unit)? = null
    ) {
        scope.launch {
            val modelReady = initializeModel()
            if (!modelReady) {
                withContext(Dispatchers.Main) { onStarted?.invoke(false) }
                return@launch
            }

            if (backgroundExecutor == null || backgroundExecutor!!.isShutdown) {
                backgroundExecutor = Executors.newSingleThreadExecutor { runnable ->
                    Thread(runnable, "Jarvis-HandTracker-Worker").apply {
                        priority = Thread.NORM_PRIORITY - 1 // Lower priority to leave headroom for UI
                    }
                }
            }

            withContext(Dispatchers.Main) {
                bindCamera(lifecycleOwner, surfaceProvider, onStarted)
            }
        }
    }

    private fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider?,
        onStarted: ((Boolean) -> Unit)?
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                provider.unbindAll()

                val cameraSelector = if (config.useFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                // Low-resolution analysis stream (320x240) to minimize CPU & memory bandwidth
                val resolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(320, 240),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                    )
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()

                val executor = backgroundExecutor ?: Executors.newSingleThreadExecutor().also {
                    backgroundExecutor = it
                }

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    analyzeFrame(imageProxy)
                }

                if (surfaceProvider != null) {
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = surfaceProvider
                    }
                    provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
                } else {
                    provider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
                }

                _gestureState.update {
                    it.copy(
                        isTrackingActive = true,
                        isCameraActive = true,
                        errorMessage = null
                    )
                }
                onStarted?.invoke(true)
            } catch (e: Exception) {
                Log.e(TAG, "Camera binding error", e)
                _gestureState.update {
                    it.copy(
                        isTrackingActive = false,
                        isCameraActive = false,
                        errorMessage = "Camera binding: ${e.message}"
                    )
                }
                onStarted?.invoke(false)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Analyzes incoming frame off the UI thread.
     * Enforces strict frame-rate throttling (10-15 FPS target).
     */
    private fun analyzeFrame(imageProxy: ImageProxy) {
        try {
            val landmarker = handLandmarker
            if (landmarker == null) {
                imageProxy.close()
                return
            }

            val currentTime = System.currentTimeMillis()
            val minFrameIntervalMs = 1000L / config.targetFps

            // Throttle inference rate to protect low-RAM devices
            if (currentTime - lastInferenceTimestampMs < minFrameIntervalMs) {
                imageProxy.close()
                return
            }
            lastInferenceTimestampMs = currentTime

            // Convert ImageProxy to MPImage
            val bitmap = imageProxy.toBitmap()
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees

            val processedBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }

            val mpImage = BitmapImageBuilder(processedBitmap).build()
            landmarker.detectAsync(mpImage, currentTime)
        } catch (e: Exception) {
            Log.e(TAG, "Frame analysis exception", e)
        } finally {
            imageProxy.close()
        }
    }

    /**
     * Processes landmark results from MediaPipe. Runs on background thread.
     */
    private fun processLandmarksResult(
        result: HandLandmarkerResult,
        @Suppress("UNUSED_PARAMETER") mpImage: MPImage
    ) {
        val currentTime = System.currentTimeMillis()

        // Calculate actual FPS
        frameCount++
        if (currentTime - lastFpsTimestampMs >= 1000L) {
            currentFps = (frameCount * 1000f) / (currentTime - lastFpsTimestampMs)
            frameCount = 0
            lastFpsTimestampMs = currentTime
        }

        val allLandmarks = result.landmarks()
        if (allLandmarks.isEmpty()) {
            // Hand lost
            _gestureState.update {
                it.copy(
                    isHandDetected = false,
                    isPinching = false,
                    pinchProgress = 0f,
                    fps = currentFps,
                    recentEvent = if (isCurrentlyPinching) GestureEvent.PINCH_RELEASE else GestureEvent.IDLE
                )
            }
            isCurrentlyPinching = false
            return
        }

        val landmarks = allLandmarks[0]
        if (landmarks.size <= INDEX_TIP_INDEX) return

        val thumbTip = landmarks[THUMB_TIP_INDEX]
        val indexTip = landmarks[INDEX_TIP_INDEX]

        // Raw Euclidean distance between thumb and index tips
        val dx = thumbTip.x() - indexTip.x()
        val dy = thumbTip.y() - indexTip.y()
        val rawDistance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        // Exponential moving average smoothing for pinch distance
        val alpha = config.smoothingFactor
        smoothedDistance = (alpha * rawDistance) + ((1f - alpha) * smoothedDistance)

        // Raw coordinates for flower anchor (midpoint)
        var rawX = (thumbTip.x() + indexTip.x()) / 2f
        val rawY = (thumbTip.y() + indexTip.y()) / 2f

        // Mirror coordinates for front-facing camera intuitive guiding
        if (config.mirrorCoordinates && config.useFrontCamera) {
            rawX = 1f - rawX
        }

        // Coordinate smoothing
        smoothedHandX = (alpha * rawX) + ((1f - alpha) * smoothedHandX)
        smoothedHandY = (alpha * rawY) + ((1f - alpha) * smoothedHandY)

        // Pinch progress normalized from releaseThreshold (0.0) down to engageThreshold (1.0)
        val normalizedProgress = ((config.releaseThreshold - smoothedDistance) /
                max(0.001f, config.releaseThreshold - config.engageThreshold))
            .coerceIn(0f, 1f)

        // Hysteresis logic to prevent flickering
        var event = GestureEvent.IDLE
        if (!isCurrentlyPinching) {
            if (smoothedDistance <= config.engageThreshold) {
                isCurrentlyPinching = true
                event = GestureEvent.PINCH_START
            } else {
                event = GestureEvent.HAND_DETECTED
            }
        } else {
            if (smoothedDistance >= config.releaseThreshold) {
                isCurrentlyPinching = false
                val timeSinceLastBloom = currentTime - lastBloomReleaseTimestampMs
                if (timeSinceLastBloom >= config.cooldownMs) {
                    event = GestureEvent.PINCH_RELEASE
                    lastBloomReleaseTimestampMs = currentTime
                } else {
                    event = GestureEvent.IDLE
                }
            } else {
                event = GestureEvent.PINCH_HOLD
            }
        }

        // Confidence score
        val confidence = result.handedness().firstOrNull()?.firstOrNull()?.score() ?: 0.85f

        _gestureState.update {
            it.copy(
                isHandDetected = true,
                isPinching = isCurrentlyPinching,
                pinchProgress = normalizedProgress,
                rawPinchDistance = rawDistance,
                smoothedPinchDistance = smoothedDistance,
                handX = smoothedHandX,
                handY = smoothedHandY,
                confidence = confidence,
                fps = currentFps,
                recentEvent = event
            )
        }
    }

    /**
     * Stops camera acquisition and tracking, releasing live stream resources.
     */
    fun stopTracking() {
        try {
            cameraProvider?.unbindAll()
            cameraProvider = null
            _gestureState.update {
                it.copy(
                    isTrackingActive = false,
                    isCameraActive = false,
                    isHandDetected = false,
                    isPinching = false,
                    pinchProgress = 0f,
                    recentEvent = GestureEvent.IDLE
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping tracking", e)
        }
    }

    /**
     * Fully releases camera and neural landmarker memory.
     */
    override fun close() {
        stopTracking()
        try {
            handLandmarker?.close()
            handLandmarker = null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing HandLandmarker", e)
        }
        try {
            backgroundExecutor?.shutdown()
            backgroundExecutor = null
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down backgroundExecutor", e)
        }
        _gestureState.update {
            it.copy(
                isModelLoaded = false,
                isTrackingActive = false,
                isCameraActive = false
            )
        }
    }
}
