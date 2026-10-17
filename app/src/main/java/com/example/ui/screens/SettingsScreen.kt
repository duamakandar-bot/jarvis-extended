package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LaptopWindows
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.components.HolographicGlassCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.viewmodel.JarvisUiState

@Composable
fun SettingsScreen(
    uiState: JarvisUiState,
    onToggleLowEffects: (Boolean) -> Unit,
    onToggleVoiceTts: (Boolean) -> Unit,
    onSpeechPitchChange: (Float) -> Unit,
    onSpeechSpeedChange: (Float) -> Unit
) {
    var pitch by remember { mutableFloatStateOf(uiState.config.speechPitch) }
    var speed by remember { mutableFloatStateOf(uiState.config.speechSpeed) }
    var showDeployGuide by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CORE CONFIGURATION",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Command Settings",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x3300F0FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon)
                ) {
                    Text(
                        text = "v2.5 DUAL-BUILD",
                        color = CyanNeon,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Low-Memory & Effects Optimization (For 4GB RAM Intel i5)
        item {
            HolographicGlassCard(
                borderColor = if (uiState.config.lowEffectsMode) Color(0xFF00E676) else CyanNeon,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = if (uiState.config.lowEffectsMode) Color(0x3300E676) else CyanGlow,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Tune, contentDescription = "Low Effects", tint = if (uiState.config.lowEffectsMode) Color(0xFF00E676) else CyanNeon, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LOW-EFFECTS MODE (4GB RAM)",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Disables heavy canvas blur, particle orbits, and continuous animation loops to preserve laptop battery and 4GB RAM headroom.",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Switch(
                            checked = uiState.config.lowEffectsMode,
                            onCheckedChange = { onToggleLowEffects(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color(0xFF00E676),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Color(0x33202E4A)
                            ),
                            modifier = Modifier.testTag("switch_low_effects")
                        )
                    }
                }
            }
        }

        // ARACHNE Voice Synthesis Settings
        item {
            HolographicGlassCard(
                borderColor = CyanNeon,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = "Voice", tint = CyanNeon, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ARACHNE VOICE SYNTHESIS (TTS)",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Switch(
                            checked = uiState.config.voiceTtsEnabled,
                            onCheckedChange = { onToggleVoiceTts(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyanNeon,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = Color(0x33202E4A)
                            ),
                            modifier = Modifier.testTag("switch_voice_tts")
                        )
                    }

                    if (uiState.config.voiceTtsEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Voice Pitch (${String.format("%.2f", pitch)}x):", color = TextCyan, fontSize = 11.sp)
                        Slider(
                            value = pitch,
                            onValueChange = {
                                pitch = it
                                onSpeechPitchChange(it)
                            },
                            valueRange = 0.8f..1.4f,
                            colors = SliderDefaults.colors(
                                thumbColor = CyanNeon,
                                activeTrackColor = CyanNeon,
                                inactiveTrackColor = Color(0x33101E36)
                            )
                        )

                        Text("Speech Rate (${String.format("%.2f", speed)}x):", color = TextCyan, fontSize = 11.sp)
                        Slider(
                            value = speed,
                            onValueChange = {
                                speed = it
                                onSpeechSpeedChange(it)
                            },
                            valueRange = 0.8f..1.4f,
                            colors = SliderDefaults.colors(
                                thumbColor = CrimsonWeb,
                                activeTrackColor = CrimsonWeb,
                                inactiveTrackColor = Color(0x33101E36)
                            )
                        )
                    }
                }
            }
        }

        // Windows 10 & Desktop Deployment Guide
        item {
            HolographicGlassCard(
                borderColor = CrimsonWeb,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LaptopWindows, contentDescription = "Windows", tint = CrimsonWeb, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WINDOWS 10 & DESKTOP RUNTIME",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = { showDeployGuide = !showDeployGuide },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF1744), contentColor = CrimsonWeb),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (showDeployGuide) "Hide" else "View Guide", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (showDeployGuide) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "1. Browser Mode (Chrome / Edge):\n" +
                                    "• The responsive command dashboard runs seamlessly in Chrome and Edge with hardware acceleration.\n" +
                                    "• Low-effects mode ensures zero memory strain on 4GB RAM.\n\n" +
                                    "2. Standalone Windows Desktop (Tauri):\n" +
                                    "• Recommended: Tauri with WebView2 (built into Windows 10).\n" +
                                    "• Consumes only ~35MB RAM compared to 500MB+ in Electron.\n" +
                                    "• Run: `cargo tauri build` to generate `jarvis-command.exe`.\n\n" +
                                    "3. Shared Backend & Accounts:\n" +
                                    "• Both browser and desktop modes talk to the same unified REST & WebSocket endpoints at 127.0.0.1:8222 for MT5 and Graph API for Meta.\n\n" +
                                    "4. Low-RAM Intel Core i5 Tips:\n" +
                                    "• Keep Low-Effects Mode toggled ON to disable canvas glow rendering.\n" +
                                    "• Use 'Purge RAM' button on System screen when multitasking.",
                            color = TextWhite,
                            fontSize = 11.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // API Keys & Tokens Status
        item {
            HolographicGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VpnKey, contentDescription = "Keys", tint = CyanNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "INTEGRATION KEYS & CREDENTIALS",
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Meta Graph API:", color = TextMuted, fontSize = 11.sp)
                        Text("Configured (v19.0)", color = Color(0xFF00E676), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("MT5 EA Bridge Port:", color = TextMuted, fontSize = 11.sp)
                        Text("8222 (Listening)", color = Color(0xFF00E676), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gemini AI API Key:", color = TextMuted, fontSize = 11.sp)
                        Text("Injected via Secrets / Fallback Active", color = TextCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
