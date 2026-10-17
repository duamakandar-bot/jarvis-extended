package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlertCondition
import com.example.model.AutomationWorkflow
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.ui.components.ArachneAvatarView
import com.example.ui.components.ArachneHolographicCore
import com.example.ui.components.CryptoChart
import com.example.ui.components.DexConnectionHelpDialog
import com.example.ui.components.HolographicBloom
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.NeonBarTheme
import com.example.ui.components.NeonScanlineProgressBar
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisUiState

/**
 * Responsive multi-panel desktop workspace for JARVIS when in Samsung DeX or Desktop mode.
 * Integrates BTC Market Radar, ARACHNE AI Chat, Telemetry, and Automation streams
 * into an ergonomic, large-screen command center.
 */
@Composable
fun DesktopWorkspace(
    uiState: JarvisUiState,
    isSpeaking: Boolean,
    onSendMessage: (String) -> Unit,
    onLaunchSpeech: () -> Unit,
    onToggleVoiceTts: (Boolean) -> Unit,
    onStopSpeech: () -> Unit,
    onRefreshCrypto: () -> Unit,
    onAddAlert: (String, Double, AlertCondition) -> Unit,
    onDeleteAlert: (String) -> Unit,
    onQuickTrade: (String, Double) -> Unit,
    onToggleWorkflow: (String) -> Unit,
    onOptimizeMemory: () -> Unit,
    onTriggerDiagnostics: () -> Unit,
    onToggleDexMode: () -> Unit,
    onOpenDexHelp: () -> Unit,
    onDismissDexHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.showDexHelpDialog) {
        DexConnectionHelpDialog(
            onDismiss = onDismissDexHelp,
            onActivateDesktopMode = { /* already in desktop */ }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .testTag("desktop_workspace")
    ) {
        // Desktop Taskbar & Holographic Command Bar
        DesktopTaskbar(
            uiState = uiState,
            isSpeaking = isSpeaking,
            onOptimizeMemory = onOptimizeMemory,
            onToggleDexMode = onToggleDexMode,
            onOpenDexHelp = onOpenDexHelp,
            onLaunchSpeech = onLaunchSpeech,
            onToggleVoiceTts = onToggleVoiceTts,
            onStopSpeech = onStopSpeech
        )

        // Main Multi-Panel Workspace Body
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
            val isUltraWide = maxWidth >= 1200.dp

            if (isUltraWide) {
                // 3-Column Canonical Desktop Layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Column 1: Market Radar & MT5 Execution (34% width)
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DesktopBtcMarketPanel(
                            uiState = uiState,
                            onRefresh = onRefreshCrypto,
                            onAddAlert = onAddAlert,
                            onDeleteAlert = onDeleteAlert,
                            onQuickTrade = onQuickTrade
                        )
                    }

                    // Column 2: System Telemetry & Automation Matrix (31% width)
                    Column(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DesktopTelemetryPanel(
                            uiState = uiState,
                            onOptimizeMemory = onOptimizeMemory,
                            onTriggerDiagnostics = onTriggerDiagnostics
                        )
                        DesktopHolographicBloomPanel(
                            uiState = uiState
                        )
                        DesktopAutomationsPanel(
                            uiState = uiState,
                            onToggleWorkflow = onToggleWorkflow
                        )
                    }

                    // Column 3: ARACHNE AI Command Center (35% width)
                    Box(
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                    ) {
                        DesktopAiChatPanel(
                            uiState = uiState,
                            isSpeaking = isSpeaking,
                            onSendMessage = onSendMessage,
                            onVoiceClick = onLaunchSpeech,
                            onToggleVoiceTts = onToggleVoiceTts,
                            onStopSpeech = onStopSpeech
                        )
                    }
                }
            } else {
                // 2-Column Desktop Grid (Left: Multi-function Dashboard, Right: Full AI Chat)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column (Scrollable Dashboard: Market + Vitals + Automations)
                    Column(
                        modifier = Modifier
                            .weight(1.15f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DesktopBtcMarketPanel(
                            uiState = uiState,
                            onRefresh = onRefreshCrypto,
                            onAddAlert = onAddAlert,
                            onDeleteAlert = onDeleteAlert,
                            onQuickTrade = onQuickTrade
                        )
                        DesktopTelemetryPanel(
                            uiState = uiState,
                            onOptimizeMemory = onOptimizeMemory,
                            onTriggerDiagnostics = onTriggerDiagnostics
                        )
                        DesktopHolographicBloomPanel(
                            uiState = uiState
                        )
                        DesktopAutomationsPanel(
                            uiState = uiState,
                            onToggleWorkflow = onToggleWorkflow
                        )
                    }

                    // Right Column: Dedicated Full-Height AI Command Center
                    Box(
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxHeight()
                    ) {
                        DesktopAiChatPanel(
                            uiState = uiState,
                            isSpeaking = isSpeaking,
                            onSendMessage = onSendMessage,
                            onVoiceClick = onLaunchSpeech,
                            onToggleVoiceTts = onToggleVoiceTts,
                            onStopSpeech = onStopSpeech
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top Desktop Command Taskbar with system status, DeX controls, and memory optimization.
 */
@Composable
private fun DesktopTaskbar(
    uiState: JarvisUiState,
    isSpeaking: Boolean,
    onOptimizeMemory: () -> Unit,
    onToggleDexMode: () -> Unit,
    onOpenDexHelp: () -> Unit,
    onLaunchSpeech: () -> Unit,
    onToggleVoiceTts: (Boolean) -> Unit,
    onStopSpeech: () -> Unit
) {
    Surface(
        color = Color(0xFF070B14),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Identity Brand
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanGlow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ViewQuilt,
                            contentDescription = "Desktop Workspace",
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "J.A.R.V.I.S.",
                            color = CyanNeon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• DESKTOP WORKSPACE",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = uiState.statusBanner,
                        color = TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            // Controls & DeX Toggles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // RAM Footprint indicator + Purge button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x3300E676),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                    modifier = Modifier.clickable { onOptimizeMemory() }.testTag("purge_ram_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        if (uiState.isOptimizingMemory) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF00E676),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Purge RAM",
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "RAM: ${uiState.telemetry.usedRamMb}MB / 4GB",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Connect to DeX Help Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x330A192F),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onOpenDexHelp() }.testTag("connect_dex_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CastConnected,
                            contentDescription = "Connect DeX",
                            tint = CyanNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Connect to DeX",
                            color = CyanNeon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // DeX Mode Toggle (Active Indicator)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (uiState.isDexMode) CyanGlow else Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                    modifier = Modifier.clickable { onToggleDexMode() }.testTag("desktop_dex_toggle_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DesktopWindows,
                            contentDescription = "Toggle DeX",
                            tint = CyanNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.isDexMode) "DeX MODE: ON" else "DeX MODE",
                            color = CyanNeon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Audio Voice TTS Toggle
                IconButton(
                    onClick = { onToggleVoiceTts(!uiState.config.voiceTtsEnabled) },
                    modifier = Modifier.size(32.dp).testTag("desktop_tts_toggle")
                ) {
                    Icon(
                        imageVector = if (uiState.config.voiceTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Toggle Voice TTS",
                        tint = if (uiState.config.voiceTtsEnabled) CyanNeon else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Stop Speech (if vocalizing)
                if (isSpeaking) {
                    IconButton(
                        onClick = onStopSpeech,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Speech",
                            tint = CrimsonWeb,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Voice Comm trigger
                Surface(
                    shape = CircleShape,
                    color = if (isSpeaking) CrimsonGlow else CyanGlow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) CrimsonWeb else CyanNeon),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onLaunchSpeech() }
                        .testTag("desktop_mic_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Comm",
                            tint = if (isSpeaking) CrimsonWeb else CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Panel 1: Crypto Market Radar with CryptoChart, technical indicators, alerts, and MT5 direct execution.
 */
@Composable
private fun DesktopBtcMarketPanel(
    uiState: JarvisUiState,
    onRefresh: () -> Unit,
    onAddAlert: (String, Double, AlertCondition) -> Unit,
    onDeleteAlert: (String) -> Unit,
    onQuickTrade: (String, Double) -> Unit
) {
    val btc = uiState.cryptoAssets.find { it.symbol == "BTC" } ?: uiState.cryptoAssets.firstOrNull()
    var selectedTimeframe by remember { mutableStateOf("24H") }
    var showAddAlert by remember { mutableStateOf(false) }
    var targetPriceInput by remember { mutableStateOf("") }
    var alertCondition by remember { mutableStateOf(AlertCondition.ABOVE) }

    val timeframes = listOf("1H", "24H", "7D", "30D", "1Y")

    HolographicGlassCard(
        borderColor = CyanNeon,
        glowEffect = !uiState.config.lowEffectsMode,
        modifier = Modifier.fillMaxWidth().testTag("desktop_market_panel")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with BTC price and live refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF7931A).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF7931A)),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "₿",
                                color = Color(0xFFF7931A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BTC / USD",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RADAR",
                                color = CyanNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "MetaTrader 5 Direct Bridge: 127.0.0.1:8222",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Current Price & 24h Delta
            if (btc != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$${String.format("%,.2f", btc.currentPriceUsd)}",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (btc.change24hPercent >= 0) Color(0x3300E676) else Color(0x33FF1744),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (btc.change24hPercent >= 0) Color(0xFF00E676) else CrimsonWeb
                        )
                    ) {
                        Text(
                            text = "${if (btc.change24hPercent >= 0) "+" else ""}${String.format("%.2f", btc.change24hPercent)}%",
                            color = if (btc.change24hPercent >= 0) Color(0xFF00E676) else CrimsonWeb,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Timeframe selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEach { tf ->
                    val isSelected = tf == selectedTimeframe
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) CyanNeon else Color(0x220B172E),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanNeon else GlassBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTimeframe = tf }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = tf,
                                color = if (isSelected) Color.Black else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Resized & Responsive CryptoChart
            if (btc != null) {
                CryptoChart(
                    points = btc.priceHistory,
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    isBullish = btc.change24hPercent >= 0
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical Indicators row (RSI, MACD, 200 EMA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("RSI (14)", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("63.8", color = CyanNeon, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Bullish", color = Color(0xFF00E676), fontSize = 9.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("MACD (12,26,9)", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("+412.5", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Golden Cross", color = CyanNeon, fontSize = 9.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("200 EMA", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("Above Trend", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Strong", color = TextCyan, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Direct MT5 Execution Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onQuickTrade("BUY", 0.05) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("buy_btc_btn")
                ) {
                    Text("BUY 0.05 BTC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onQuickTrade("SELL", 0.05) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonWeb,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("sell_btc_btn")
                ) {
                    Text("SELL 0.05 BTC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Alert Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alerts",
                        tint = CrimsonWeb,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PRICE ALERTS",
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { showAddAlert = !showAddAlert },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x33FF1744),
                        contentColor = CrimsonWeb
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("add_alert_btn")
                ) {
                    Icon(Icons.Default.AddAlert, contentDescription = "Add", modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddAlert) "Cancel" else "New Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Inline Add Alert Field
            if (showAddAlert) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = targetPriceInput,
                        onValueChange = { targetPriceInput = it },
                        placeholder = { Text("Price (e.g. 70000)", color = TextMuted, fontSize = 11.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("alert_price_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanNeon,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val price = targetPriceInput.toDoubleOrNull()
                            if (price != null) {
                                onAddAlert("BTC", price, alertCondition)
                                targetPriceInput = ""
                                showAddAlert = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Price Alerts Mini-List
            uiState.priceAlerts.take(3).forEach { alert ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (alert.isTriggered) CrimsonWeb else Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${alert.symbol} ${if (alert.condition == AlertCondition.ABOVE) ">=" else "<="} $${String.format("%,.0f", alert.targetPrice)}",
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (alert.isTriggered) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CrimsonGlow
                            ) {
                                Text(
                                    text = "TRIGGERED",
                                    color = CrimsonWeb,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { onDeleteAlert(alert.id) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

/**
 * Panel 2: Neural Telemetry & 4GB Low-RAM Performance Monitor.
 */
@Composable
private fun DesktopTelemetryPanel(
    uiState: JarvisUiState,
    onOptimizeMemory: () -> Unit,
    onTriggerDiagnostics: () -> Unit
) {
    val telemetry = uiState.telemetry

    HolographicGlassCard(
        borderColor = GlassBorder,
        glowEffect = !uiState.config.lowEffectsMode,
        modifier = Modifier.fillMaxWidth().testTag("desktop_telemetry_panel")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Telemetry",
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HARDWARE TELEMETRY // 4GB RAM TUNED",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x3300E676),
                    modifier = Modifier.clickable { onTriggerDiagnostics() }
                ) {
                    Text(
                        text = "PULSE DIAGNOSTICS",
                        color = Color(0xFF00E676),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CPU & RAM progress bars
            NeonScanlineProgressBar(
                progress = telemetry.cpuUsagePercent / 100f,
                label = "Intel Core i5-5200U CPU",
                subLabel = "${telemetry.cpuUsagePercent}% (${telemetry.cpuTempC}°C)",
                barTheme = NeonBarTheme.CYAN,
                lowEffectsMode = uiState.config.lowEffectsMode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            NeonScanlineProgressBar(
                progress = telemetry.usedRamMb.toFloat() / telemetry.totalRamMb,
                label = "DDR3 System Memory",
                subLabel = "${telemetry.usedRamMb}MB / ${telemetry.totalRamMb}MB",
                barTheme = NeonBarTheme.CRIMSON,
                lowEffectsMode = uiState.config.lowEffectsMode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Spec Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("GPU ENGINE", color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("${telemetry.gpuUsagePercent}%", color = CyanNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("HD 5500", color = TextWhite, fontSize = 9.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("FAN SPEED", color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("${telemetry.fanSpeedRpm} RPM", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Quiet Flow", color = TextMuted, fontSize = 9.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x330B172E),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("LATENCY", color = TextMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("${telemetry.networkPingMs} ms", color = CyanNeon, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("DeX Socket", color = TextWhite, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

/**
 * Holographic Bloom Neural Pinch Lab Panel for Desktop/DeX Mode.
 */
@Composable
private fun DesktopHolographicBloomPanel(
    uiState: JarvisUiState
) {
    HolographicGlassCard(
        borderColor = CyanNeon,
        glowEffect = !uiState.config.lowEffectsMode,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("desktop_bloom_panel")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Holographic Bloom",
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HOLOGRAPHIC BLOOM // PINCH LAB",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (uiState.gestureData.isPinching) Color(0x33FF1744) else Color(0x3300F0FF)
                ) {
                    Text(
                        text = if (uiState.gestureData.isPinching) "PINCH ACTIVE" else "STANDBY",
                        color = if (uiState.gestureData.isPinching) CrimsonWeb else CyanNeon,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF070B14))
                    .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
            ) {
                HolographicBloom(
                    modifier = Modifier.fillMaxSize(),
                    handGestureData = uiState.gestureData,
                    lowEffectsMode = uiState.config.lowEffectsMode,
                    showControlsOverlay = true
                )
            }
        }
    }
}

/**
 * Panel 3: Automation Matrix & MT5 Live Feeds.
 */
@Composable
private fun DesktopAutomationsPanel(
    uiState: JarvisUiState,
    onToggleWorkflow: (String) -> Unit
) {
    HolographicGlassCard(
        borderColor = GlassBorder,
        modifier = Modifier.fillMaxWidth().testTag("desktop_automations_panel")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Automations",
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AUTOMATION MATRIX & EA BRIDGE",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "${uiState.automations.count { it.isRunning }} ACTIVE",
                    color = Color(0xFF00E676),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Top workflows
            uiState.automations.take(3).forEach { wf ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = wf.name,
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = wf.statusText,
                            color = if (wf.isRunning) CyanNeon else TextMuted,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = { onToggleWorkflow(wf.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (wf.isRunning) Color(0x3300E676) else Color(0x33FF1744),
                            contentColor = if (wf.isRunning) Color(0xFF00E676) else CrimsonWeb
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(if (wf.isRunning) "RUNNING" else "PAUSED", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recent Live Log stream
            Text(
                text = "RECENT BRIDGE LOGS",
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            uiState.automationLogs.take(2).forEach { log ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.timestamp,
                        color = CyanNeon,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.message,
                        color = TextWhite,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Panel 4: ARACHNE AI Command Assistant (Interactive full-height chat with message feed,
 * quick prompt chips, input bar, and holographic core avatar).
 */
@Composable
private fun DesktopAiChatPanel(
    uiState: JarvisUiState,
    isSpeaking: Boolean,
    onSendMessage: (String) -> Unit,
    onVoiceClick: () -> Unit,
    onToggleVoiceTts: (Boolean) -> Unit,
    onStopSpeech: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var showCoreExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val quickPrompts = listOf(
        "Hardware Health Check",
        "BTC Technical Outlook",
        "Instagram DM Bot Status",
        "MT5 EA Bridge Check",
        "Purge Unused Memory",
        "Spider Web Threat Scan"
    )

    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    HolographicGlassCard(
        borderColor = if (showCoreExpanded) CrimsonWeb else CyanNeon,
        glowEffect = !uiState.config.lowEffectsMode,
        modifier = Modifier.fillMaxSize().testTag("desktop_ai_chat_panel")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: ARACHNE Identity & Audio Shader Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showCoreExpanded = !showCoreExpanded }
                ) {
                    ArachneAvatarView(
                        avatarSize = 44.dp,
                        isSpeaking = isSpeaking,
                        isThinking = uiState.isAiThinking,
                        isListening = uiState.isListeningVoice,
                        lowEffectsMode = uiState.config.lowEffectsMode,
                        showCoreShaderMode = showCoreExpanded
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ARACHNE AI PROTOCOL",
                                color = CyanNeon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isAiThinking) CrimsonWeb else Color(0xFF00E676))
                            )
                        }
                        Text(
                            text = if (isSpeaking) "Vocalizing Output..." else if (uiState.isAiThinking) "Neural Computing..." else "Voice & Neural Link Active",
                            color = TextWhite,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showCoreExpanded = !showCoreExpanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Shader Core",
                            tint = if (showCoreExpanded) CrimsonWeb else CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (isSpeaking) {
                        IconButton(
                            onClick = onStopSpeech,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Speech",
                                tint = CrimsonWeb,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleVoiceTts(!uiState.config.voiceTtsEnabled) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.config.voiceTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Voice TTS",
                            tint = if (uiState.config.voiceTtsEnabled) CyanNeon else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expanded Holographic Canvas Shader Singularity
            AnimatedVisibility(
                visible = showCoreExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x55070D18),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    ) {
                        ArachneHolographicCore(
                            isSpeaking = isSpeaking,
                            isThinking = uiState.isAiThinking,
                            isListening = uiState.isListeningVoice,
                            lowEffectsMode = uiState.config.lowEffectsMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Prompt Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x330B172E),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.clickable { onSendMessage(prompt) }
                    ) {
                        Text(
                            text = prompt,
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat Messages Feed (weight to take available space)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.chatMessages) { msg ->
                    ChatBubble(msg = msg)
                }

                if (uiState.isAiThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = CyanNeon,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ARACHNE is formulating tactical analysis...",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sticky Bottom Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Command ARACHNE (e.g. 'Run system scan')...",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = Color(0x4408101E),
                        unfocusedContainerColor = Color(0x4408101E)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Voice Mic Button
                Surface(
                    shape = CircleShape,
                    color = if (isSpeaking) CrimsonGlow else CyanGlow,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) CrimsonWeb else CyanNeon),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onVoiceClick() }
                        .testTag("mic_voice_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = if (isSpeaking) CrimsonWeb else CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                Surface(
                    shape = CircleShape,
                    color = CyanNeon,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("send_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
