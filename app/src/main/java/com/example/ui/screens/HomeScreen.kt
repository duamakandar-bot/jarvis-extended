package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ArachneAvatarView
import com.example.ui.components.HolographicBloom
import com.example.ui.components.HolographicGlassCard
import com.example.ui.components.NeonBarTheme
import com.example.ui.components.NeonCyanTrendLineCard
import com.example.ui.components.NeonScanlineProgressBar
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisScreen
import com.example.viewmodel.JarvisUiState

@Composable
fun HomeScreen(
    uiState: JarvisUiState,
    isSpeaking: Boolean,
    onNavigate: (JarvisScreen) -> Unit,
    onOptimizeMemory: () -> Unit,
    onQuickVoiceTrigger: () -> Unit
) {
    val btc = uiState.cryptoAssets.find { it.symbol == "BTC" }
    val telemetry = uiState.telemetry

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Holographic Command Center Banner
            HolographicGlassCard(
                borderColor = CyanNeon,
                glowEffect = !uiState.config.lowEffectsMode,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "J.A.R.V.I.S. // NEURAL CORE",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Spider Command Center",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.statusBanner,
                            color = TextMuted,
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }

                    ArachneAvatarView(
                        avatarSize = 72.dp,
                        isSpeaking = isSpeaking,
                        isThinking = uiState.isAiThinking,
                        lowEffectsMode = uiState.config.lowEffectsMode,
                        onAvatarClick = { onNavigate(JarvisScreen.AI_ASSISTANT) }
                    )
                }
            }
        }

        // Real-Time Bitcoin Retrofit Market Card with Neon Cyan Trend Line
        item {
            NeonCyanTrendLineCard(
                asset = btc,
                chartHeight = 130.dp,
                lowEffectsMode = uiState.config.lowEffectsMode,
                onCardClick = { onNavigate(JarvisScreen.BTC_MARKETS) }
            )
        }

        // Hardware Telemetry Matrix (Tailored to Intel Core i5 & 4GB RAM)
        item {
            Text(
                text = "HARDWARE TELEMETRY // INTEL i5-5200U",
                color = CyanNeon,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // CPU Gauge
                HolographicGlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(JarvisScreen.SYSTEM) }
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "CPU",
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CPU LOAD", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${telemetry.cpuUsagePercent}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("Core i5 (2.2 GHz)", fontSize = 10.sp, color = TextCyan)
                    }
                }

                // RAM Gauge
                HolographicGlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(JarvisScreen.SYSTEM) }
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "RAM",
                                tint = CrimsonWeb,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4GB RAM", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${telemetry.usedRamMb} MB",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("${(telemetry.usedRamMb * 100) / telemetry.totalRamMb}% occupied", fontSize = 10.sp, color = CrimsonWeb)
                    }
                }

                // Ping & Network
                HolographicGlassCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(JarvisScreen.SYSTEM) }
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Ping",
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LATENCY", fontSize = 10.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${telemetry.networkPingMs} ms",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("SSL Secure", fontSize = 10.sp, color = Color(0xFF00E676))
                    }
                }
            }
        }

        // Hardware Diagnostics Quick Card with Neon Scanline Progress Bars
        item {
            HolographicGlassCard(
                borderColor = CyanNeon,
                glowEffect = !uiState.config.lowEffectsMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(JarvisScreen.SYSTEM) }
                    .testTag("home_diagnostics_neon_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DIAGNOSTIC MATRIX // SCANLINE HUD",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "FULL MATRIX >",
                            color = TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    NeonScanlineProgressBar(
                        label = "CPU i5-5200U (2.20 GHz)",
                        progress = telemetry.cpuUsagePercent / 100f,
                        subLabel = "Core Temp: ${telemetry.cpuTempC}°C • Fan: ${telemetry.fanSpeedRpm} RPM",
                        barHeight = 13.dp,
                        barTheme = NeonBarTheme.AUTO_THRESHOLD,
                        lowEffectsMode = uiState.config.lowEffectsMode
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeonScanlineProgressBar(
                        label = "4GB PHYSICAL RAM",
                        progress = telemetry.usedRamMb.toFloat() / telemetry.totalRamMb.toFloat(),
                        subLabel = "${telemetry.usedRamMb} MB / ${telemetry.totalRamMb} MB • Free: ${telemetry.totalRamMb - telemetry.usedRamMb} MB",
                        barHeight = 13.dp,
                        barTheme = NeonBarTheme.AUTO_THRESHOLD,
                        lowEffectsMode = uiState.config.lowEffectsMode
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeonScanlineProgressBar(
                        label = "GPU HD 5500 3D ENGINE",
                        progress = telemetry.gpuUsagePercent / 100f,
                        subLabel = "Clock: ${telemetry.gpuClockMhz} MHz • VRAM: ${telemetry.gpuVramUsedMb} MB",
                        barHeight = 13.dp,
                        barTheme = NeonBarTheme.AUTO_THRESHOLD,
                        lowEffectsMode = uiState.config.lowEffectsMode
                    )
                }
            }
        }

        // Active Automations & Integrations Hub
        item {
            HolographicGlassCard(
                borderColor = GlassBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(JarvisScreen.AUTOMATIONS) }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Automations",
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTOMATION BRIDGES",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GlassSurfaceBright
                        ) {
                            Text(
                                text = "3 ACTIVE",
                                color = CyanNeon,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Meta / Instagram Auto-DM Bot", color = TextCyan, fontSize = 12.sp)
                        Text("ONLINE", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• MT5 EA Bridge (127.0.0.1:8222)", color = TextCyan, fontSize = 12.sp)
                        Text("CONNECTED", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Spider Web Security Watchdog", color = TextCyan, fontSize = 12.sp)
                        Text("GUARDING", color = CrimsonWeb, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Holographic Neural Bloom Interactive Gesture Lab
        item {
            HolographicGlassCard(
                borderColor = CyanNeon,
                glowEffect = !uiState.config.lowEffectsMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_holographic_bloom_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HOLOGRAPHIC BLOOM // PINCH LAB",
                                color = CyanNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = if (uiState.gestureData.isPinching) "PINCH ACTIVE" else "READY",
                            color = if (uiState.gestureData.isPinching) CrimsonWeb else TextCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MidnightNavy)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
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

        // Quick Tactical Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onQuickVoiceTrigger,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("voice_command_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Voice AI", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOptimizeMemory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x33FF1744),
                        contentColor = CrimsonWeb
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonWeb),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("optimize_ram_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Clean RAM",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (uiState.isOptimizingMemory) "Purging..." else "Purge RAM",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
