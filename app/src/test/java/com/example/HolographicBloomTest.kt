package com.example

import android.app.Application
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.model.GestureEvent
import com.example.model.HandGestureData
import com.example.service.GestureManager
import com.example.service.PinchEvent
import com.example.ui.components.HolographicBloom
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HolographicBloomTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testGestureManagerPinchSimulation() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val gestureManager = GestureManager(app)

        assertNotNull(gestureManager)
        assertFalse(gestureManager.gestureState.value.isPinching)

        // Trigger pinch
        gestureManager.triggerPinch(progress = 0.85f, handX = 0.5f, handY = 0.5f)
        val stateAfterPinch = gestureManager.gestureState.value
        assertTrue(stateAfterPinch.isPinching)
        assertTrue(stateAfterPinch.isHandDetected)
        assertEquals(0.85f, stateAfterPinch.pinchProgress, 0.01f)

        // Trigger release burst
        gestureManager.triggerPinchReleaseBurst()
        val stateAfterRelease = gestureManager.gestureState.value
        assertFalse(stateAfterRelease.isPinching)
        assertEquals(GestureEvent.PINCH_RELEASE, stateAfterRelease.recentEvent)

        gestureManager.close()
    }

    @Test
    fun testHolographicBloomComposableRendering() {
        val testGestureData = HandGestureData(
            isTrackingActive = true,
            isHandDetected = true,
            isPinching = true,
            pinchProgress = 0.9f,
            smoothedPinchDistance = 0.05f,
            handX = 0.5f,
            handY = 0.5f,
            recentEvent = GestureEvent.PINCH_START
        )

        composeTestRule.setContent {
            HolographicBloom(
                modifier = Modifier.testTag("holographic_bloom_root"),
                handGestureData = testGestureData,
                lowEffectsMode = false,
                showControlsOverlay = true
            )
        }

        // Verify root container and elements are present
        composeTestRule.onNodeWithTag("holographic_bloom_root").assertExists()
        composeTestRule.onNodeWithTag("holographic_bloom_canvas").assertExists()
        composeTestRule.onNodeWithTag("bloom_pinch_meter").assertExists()
        composeTestRule.onNodeWithTag("bloom_burst_button").assertExists()

        // Test triggering manual burst button
        composeTestRule.onNodeWithTag("bloom_burst_button").performClick()

        // Test triggering simulate pinch button
        composeTestRule.onNodeWithTag("bloom_simulate_pinch_btn").assertExists()
        composeTestRule.onNodeWithTag("bloom_simulate_pinch_btn").performClick()
    }
}
