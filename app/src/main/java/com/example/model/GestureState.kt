package com.example.model

/**
 * Representation of real-time gesture telemetry and hand tracking state.
 */
enum class GestureEvent {
    IDLE,
    HAND_DETECTED,
    PINCH_START,
    PINCH_HOLD,
    PINCH_RELEASE
}

data class HandGestureData(
    val isTrackingActive: Boolean = false,
    val isHandDetected: Boolean = false,
    val isPinching: Boolean = false,
    val pinchProgress: Float = 0f, // 0.0f (hand open) to 1.0f (full pinch)
    val rawPinchDistance: Float = 1.0f,
    val smoothedPinchDistance: Float = 1.0f,
    val handX: Float = 0.5f, // Normalized 0..1 (midpoint of thumb & index)
    val handY: Float = 0.5f, // Normalized 0..1
    val confidence: Float = 0f,
    val fps: Float = 0f,
    val recentEvent: GestureEvent = GestureEvent.IDLE,
    val isModelLoaded: Boolean = false,
    val isCameraActive: Boolean = false,
    val errorMessage: String? = null
)

data class GestureTrackingConfig(
    val targetFps: Int = 12, // 10-15 FPS target for low-RAM performance
    val engageThreshold: Float = 0.065f, // Distance below which pinch is engaged
    val releaseThreshold: Float = 0.095f, // Distance above which pinch is released (hysteresis)
    val smoothingFactor: Float = 0.35f, // EMA alpha
    val cooldownMs: Long = 300L, // Cooldown between bloom bursts
    val useFrontCamera: Boolean = true,
    val mirrorCoordinates: Boolean = true
)
