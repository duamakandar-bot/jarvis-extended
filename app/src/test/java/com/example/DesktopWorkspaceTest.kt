package com.example

import android.app.Application
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.model.CryptoAsset
import com.example.ui.screens.DesktopWorkspace
import com.example.viewmodel.JarvisUiState
import com.example.viewmodel.JarvisViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DesktopWorkspaceTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testDexModeToggleInViewModel() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = JarvisViewModel(app)

        assertFalse(vm.uiState.value.isDexMode)
        assertFalse(vm.uiState.value.showDexHelpDialog)

        // Toggle DeX mode ON
        vm.toggleDexMode()
        assertTrue(vm.uiState.value.isDexMode)
        assertEquals("DeX Desktop Workspace Activated", vm.uiState.value.statusBanner)

        // Toggle DeX mode OFF
        vm.toggleDexMode()
        assertFalse(vm.uiState.value.isDexMode)
        assertEquals("Handheld Mobile View Activated", vm.uiState.value.statusBanner)

        // Set DeX Help dialog visible
        vm.setDexHelpDialogVisible(true)
        assertTrue(vm.uiState.value.showDexHelpDialog)
        vm.setDexHelpDialogVisible(false)
        assertFalse(vm.uiState.value.showDexHelpDialog)
    }

    @Test
    fun testDesktopWorkspaceRendersPanels() {
        val mockBtc = CryptoAsset(
            id = "btc",
            symbol = "BTC",
            name = "Bitcoin",
            currentPriceUsd = 67420.0,
            change24hPercent = 3.42,
            high24h = 68000.0,
            low24h = 65000.0,
            marketCapUsd = "$1.32T",
            volume24hUsd = "$28.4B",
            rsi14 = 63.8,
            macdSignal = "+412.5",
            priceHistory = listOf(66000f, 66500f, 67200f, 67420f)
        )

        val state = JarvisUiState(
            isDexMode = true,
            cryptoAssets = listOf(mockBtc),
            selectedCrypto = mockBtc
        )

        var dexToggled = false
        var dexHelpOpened = false
        var quickTradeCalled = false

        composeTestRule.setContent {
            DesktopWorkspace(
                uiState = state,
                isSpeaking = false,
                onSendMessage = {},
                onLaunchSpeech = {},
                onToggleVoiceTts = {},
                onStopSpeech = {},
                onRefreshCrypto = {},
                onAddAlert = { _, _, _ -> },
                onDeleteAlert = {},
                onQuickTrade = { _, _ -> quickTradeCalled = true },
                onToggleWorkflow = {},
                onOptimizeMemory = {},
                onTriggerDiagnostics = {},
                onToggleDexMode = { dexToggled = true },
                onOpenDexHelp = { dexHelpOpened = true },
                onDismissDexHelp = {}
            )
        }

        // Verify root and major panels exist and are composed
        composeTestRule.onNodeWithTag("desktop_workspace").assertExists()
        composeTestRule.onNodeWithTag("desktop_market_panel").assertExists()
        composeTestRule.onNodeWithTag("desktop_telemetry_panel").assertExists()
        composeTestRule.onNodeWithTag("desktop_bloom_panel").assertExists()
        composeTestRule.onNodeWithTag("desktop_ai_chat_panel").assertExists()

        // Verify DeX toggle button
        composeTestRule.onNodeWithTag("desktop_dex_toggle_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("desktop_dex_toggle_btn").performClick()
        assertTrue(dexToggled)

        // Verify Connect to DeX button
        composeTestRule.onNodeWithTag("connect_dex_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("connect_dex_btn").performClick()
        assertTrue(dexHelpOpened)

        // Verify Buy BTC trade button
        composeTestRule.onNodeWithTag("buy_btc_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("buy_btc_btn").performClick()
        assertTrue(quickTradeCalled)
    }
}
