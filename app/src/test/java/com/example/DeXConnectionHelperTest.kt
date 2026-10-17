package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.components.DeXConnectionHelper
import com.example.ui.components.DeXConnectionHelperDialog
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeXConnectionHelperTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDeXConnectionHelper_stepNavigationAndInteractions() {
        var desktopActivated = false
        var dismissed = false

        composeTestRule.setContent {
            DeXConnectionHelper(
                onDismiss = { dismissed = true },
                onActivateDesktopMode = { desktopActivated = true }
            )
        }

        // 1. Verify component and header controls
        composeTestRule.onNodeWithTag("dex_connection_helper").assertExists()
        composeTestRule.onNodeWithTag("close_dex_help_btn").assertExists()

        // 2. Step 1: Verify Swipe Down simulation exists and can be clicked
        composeTestRule.onNodeWithTag("simulate_swipe_down_btn").assertExists().performScrollTo().performClick()

        // 3. Navigate to Step 2 via step tab
        composeTestRule.onNodeWithTag("dex_step_tab_2").assertExists().performClick()

        // 4. Step 2: Verify DeX Quick Tile exists and can be clicked
        composeTestRule.onNodeWithTag("dex_quick_settings_tile").assertExists().performScrollTo().performClick()

        // Verify missing tile accordion
        composeTestRule.onNodeWithTag("missing_dex_tile_help_toggle").assertExists().performScrollTo().performClick()

        // 5. Navigate to Step 3 via step tab
        composeTestRule.onNodeWithTag("dex_step_tab_3").assertExists().performClick()
        composeTestRule.onNodeWithTag("mode_tv_monitor_btn").assertExists().performScrollTo().performClick()
        composeTestRule.onNodeWithTag("mode_pc_wireless_btn").assertExists().performScrollTo().performClick()

        // 6. Navigate to Step 4 via step tab
        composeTestRule.onNodeWithTag("dex_step_tab_4").assertExists().performClick()
        composeTestRule.onNodeWithTag("dex_device_item_0").assertExists().performScrollTo().performClick()

        // 7. Navigate to Step 5 via step tab
        composeTestRule.onNodeWithTag("dex_step_tab_5").assertExists().performClick()
        composeTestRule.onNodeWithTag("dex_launch_desktop_btn").assertExists().performScrollTo().performClick()
        assertTrue(desktopActivated)

        // 8. Test Close button
        composeTestRule.onNodeWithTag("close_dex_help_btn").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun testDeXConnectionHelper_tabsSwitching() {
        var desktopActivated = false

        composeTestRule.setContent {
            DeXConnectionHelper(
                onDismiss = {},
                onActivateDesktopMode = { desktopActivated = true }
            )
        }

        // Test switching to Quick Ref tab
        composeTestRule.onNodeWithTag("dex_tab_quick_ref").assertExists().performClick()
        composeTestRule.onNodeWithTag("quick_ref_switch_btn").assertExists().performScrollTo().performClick()
        assertTrue(desktopActivated)

        // Test switching to Troubleshoot tab
        composeTestRule.onNodeWithTag("dex_tab_troubleshoot").assertExists().performClick()

        // Switch back to Tutorial tab
        composeTestRule.onNodeWithTag("dex_tab_tutorial").assertExists().performClick()
        composeTestRule.onNodeWithTag("dex_step_tab_1").assertExists()
    }

    @Test
    fun testDeXConnectionHelperDialog_rendersProperly() {
        var dismissed = false

        composeTestRule.setContent {
            DeXConnectionHelperDialog(
                onDismiss = { dismissed = true },
                onActivateDesktopMode = {}
            )
        }

        composeTestRule.onNodeWithTag("dex_connection_help_dialog").assertExists()
        composeTestRule.onNodeWithTag("close_dex_help_btn").assertExists().performClick()
        assertTrue(dismissed)
    }
}
