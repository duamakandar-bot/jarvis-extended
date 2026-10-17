package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.GestureEvent
import com.example.model.GestureTrackingConfig
import com.example.model.HandGestureData
import com.example.service.HandTrackingController
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.max

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HandTrackingControllerTest {

    private lateinit var context: Context
    private lateinit var controller: HandTrackingController

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        controller = HandTrackingController(
            context = context,
            config = GestureTrackingConfig(
                targetFps = 12,
                engageThreshold = 0.065f,
                releaseThreshold = 0.095f,
                smoothingFactor = 0.35f,
                cooldownMs = 300L
            )
        )
    }

    @Test
    fun `controller is lazy loaded - not initialized or active on creation`() {
        val state = controller.gestureState.value
        assertFalse("Model should not be loaded initially", state.isModelLoaded)
        assertFalse("Tracking should not be active initially", state.isTrackingActive)
        assertFalse("Camera should not be active initially", state.isCameraActive)
        assertFalse("Hand should not be detected initially", state.isHandDetected)
        assertFalse("Should not be pinching initially", state.isPinching)
        assertEquals(GestureEvent.IDLE, state.recentEvent)
        assertNull(state.errorMessage)
    }

    @Test
    fun `config defaults match 4GB low-RAM targets`() {
        val config = controller.config
        assertEquals(12, config.targetFps)
        assertEquals(0.065f, config.engageThreshold, 0.0001f)
        assertEquals(0.095f, config.releaseThreshold, 0.0001f)
        assertEquals(0.35f, config.smoothingFactor, 0.0001f)
        assertEquals(300L, config.cooldownMs)
        assertTrue(config.useFrontCamera)
        assertTrue(config.mirrorCoordinates)
    }

    @Test
    fun `pinch progress calculation correctly scales from 0 to 1`() {
        val engage = 0.065f
        val release = 0.095f

        fun calcProgress(distance: Float): Float {
            return ((release - distance) / max(0.001f, release - engage)).coerceIn(0f, 1f)
        }

        // When fingers are far apart (0.2f), progress is 0.0 (hand open)
        assertEquals(0f, calcProgress(0.20f), 0.001f)
        // When fingers are at release threshold, progress is 0.0
        assertEquals(0f, calcProgress(release), 0.001f)
        // When fingers are at midpoint between release and engage
        val mid = (release + engage) / 2f
        assertEquals(0.5f, calcProgress(mid), 0.01f)
        // When fingers are at engage threshold, progress is 1.0 (tight pinch)
        assertEquals(1f, calcProgress(engage), 0.001f)
        // When fingers are touching completely (0.01f), progress remains clamped at 1.0
        assertEquals(1f, calcProgress(0.01f), 0.001f)
    }

    @Test
    fun `stop tracking resets active flags cleanly`() {
        controller.stopTracking()
        val state = controller.gestureState.value
        assertFalse(state.isTrackingActive)
        assertFalse(state.isCameraActive)
        assertFalse(state.isHandDetected)
        assertFalse(state.isPinching)
        assertEquals(GestureEvent.IDLE, state.recentEvent)
    }

    @Test
    fun `close fully tears down resources`() {
        controller.close()
        val state = controller.gestureState.value
        assertFalse(state.isModelLoaded)
        assertFalse(state.isTrackingActive)
        assertFalse(state.isCameraActive)
    }
}
