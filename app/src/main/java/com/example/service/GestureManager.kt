package com.example.service

import android.content.Context
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import com.example.model.GestureEvent
import com.example.model.GestureTrackingConfig
import com.example.model.HandGestureData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class representing a discrete or continuous pinch event.
 */
data class PinchEvent(
    val event: GestureEvent,
    val progress: Float,
    val handX: Float,
    val handY: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Manager coordinating gesture detection, pinch event streaming, and hand tracking.
 * Provides StateFlow and SharedFlow interfaces for UI components like HolographicBloom.
 */
class GestureManager(
    private val context: Context,
    val config: GestureTrackingConfig = GestureTrackingConfig()
) : AutoCloseable {

    companion object {
        @Volatile
        private var INSTANCE: GestureManager? = null

        fun getInstance(context: Context): GestureManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GestureManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Internal HandTrackingController instance (lazy loaded)
    val handTrackingController by lazy {
        HandTrackingController(context, config)
    }

    // Mutable state that can be fed from HandTrackingController OR synthetic/touch gestures
    private val _gestureState = MutableStateFlow(HandGestureData())
    val gestureState: StateFlow<HandGestureData> = _gestureState.asStateFlow()

    // Event bus for discrete pinch transitions (PINCH_START, PINCH_HOLD, PINCH_RELEASE)
    private val _pinchEvents = MutableSharedFlow<PinchEvent>(extraBufferCapacity = 64)
    val pinchEvents: SharedFlow<PinchEvent> = _pinchEvents.asSharedFlow()

    // Convenient reactive flows
    val pinchProgress: StateFlow<Float> = _gestureState
        .map { it.pinchProgress }
        .stateIn(scope, SharingStarted.Eagerly, 0f)

    val isPinching: StateFlow<Boolean> = _gestureState
        .map { it.isPinching }
        .stateIn(scope, SharingStarted.Eagerly, false)

    private var previousEvent: GestureEvent = GestureEvent.IDLE

    init {
        // Collect real camera/MediaPipe telemetry from HandTrackingController if tracking
        scope.launch {
            handTrackingController.gestureState.collect { cameraState ->
                if (cameraState.isTrackingActive) {
                    _gestureState.value = cameraState
                    dispatchPinchEventIfNeeded(cameraState)
                }
            }
        }
    }

    private fun dispatchPinchEventIfNeeded(data: HandGestureData) {
        val currentEvent = data.recentEvent
        if (currentEvent != GestureEvent.IDLE && currentEvent != previousEvent) {
            previousEvent = currentEvent
            val event = PinchEvent(
                event = currentEvent,
                progress = data.pinchProgress,
                handX = data.handX,
                handY = data.handY
            )
            _pinchEvents.tryEmit(event)
        } else if (currentEvent == GestureEvent.PINCH_HOLD) {
            val event = PinchEvent(
                event = currentEvent,
                progress = data.pinchProgress,
                handX = data.handX,
                handY = data.handY
            )
            _pinchEvents.tryEmit(event)
        }
    }

    /**
     * Start live camera tracking via MediaPipe.
     */
    fun startTracking(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider? = null,
        onStarted: ((Boolean) -> Unit)? = null
    ) {
        handTrackingController.startTracking(lifecycleOwner, surfaceProvider, onStarted)
    }

    /**
     * Stop live camera tracking.
     */
    fun stopTracking() {
        handTrackingController.stopTracking()
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
    }

    /**
     * Manually emit or simulate a pinch event.
     * Useful for touch screen drag/pinch interactions, desktop testing, and previews.
     */
    fun triggerPinch(
        progress: Float,
        handX: Float = 0.5f,
        handY: Float = 0.5f,
        event: GestureEvent? = null
    ) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        val determinedEvent = event ?: when {
            clampedProgress >= 0.95f -> GestureEvent.PINCH_START
            clampedProgress > 0.1f -> GestureEvent.PINCH_HOLD
            else -> GestureEvent.PINCH_RELEASE
        }

        val updated = _gestureState.value.copy(
            isHandDetected = true,
            isPinching = clampedProgress > 0.5f,
            pinchProgress = clampedProgress,
            handX = handX.coerceIn(0f, 1f),
            handY = handY.coerceIn(0f, 1f),
            recentEvent = determinedEvent,
            confidence = 1.0f
        )
        _gestureState.value = updated

        val pinchEvent = PinchEvent(
            event = determinedEvent,
            progress = clampedProgress,
            handX = handX,
            handY = handY
        )
        _pinchEvents.tryEmit(pinchEvent)
    }

    /**
     * Simulates a pinch burst release, causing the bloom to explode outward.
     */
    fun triggerPinchReleaseBurst(handX: Float = 0.5f, handY: Float = 0.5f) {
        triggerPinch(progress = 0f, handX = handX, handY = handY, event = GestureEvent.PINCH_RELEASE)
    }

    override fun close() {
        stopTracking()
        handTrackingController.close()
    }
}
