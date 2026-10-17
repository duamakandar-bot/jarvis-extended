package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.ui.components.ArachneAvatarView
import com.example.ui.components.ArachneHolographicCore
import com.example.ui.components.HolographicGlassCard
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

@Composable
fun AiAssistantScreen(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Holographic AI Avatar Header
        HolographicGlassCard(
            borderColor = if (showCoreExpanded) CrimsonWeb else CyanNeon,
            glowEffect = !uiState.config.lowEffectsMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCoreExpanded = !showCoreExpanded }
                ) {
                    ArachneAvatarView(
                        avatarSize = 56.dp,
                        isSpeaking = isSpeaking,
                        isThinking = uiState.isAiThinking,
                        isListening = uiState.isListeningVoice,
                        lowEffectsMode = uiState.config.lowEffectsMode,
                        showCoreShaderMode = showCoreExpanded
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ARACHNE AI PROTOCOL",
                            color = CyanNeon,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isSpeaking) "Vocalizing Response..." else if (uiState.isAiThinking) "Neural Computing..." else "Voice & Neural Link Active",
                            color = TextWhite,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (showCoreExpanded) "Shader Core: Expanded" else "Tap avatar for Shader Core",
                            color = CrimsonWeb,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Toggle Core Shader View button
                    IconButton(
                        onClick = { showCoreExpanded = !showCoreExpanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Holographic Shader Core",
                            tint = if (showCoreExpanded) CrimsonWeb else CyanNeon
                        )
                    }

                    if (isSpeaking) {
                        IconButton(
                            onClick = onStopSpeech,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Speech",
                                tint = CrimsonWeb
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleVoiceTts(!uiState.config.voiceTtsEnabled) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.config.voiceTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Voice TTS",
                            tint = if (uiState.config.voiceTtsEnabled) CyanNeon else TextMuted
                        )
                    }
                }
            }
        }

        // Expanded Holographic Animated AI Core Canvas Shader Section
        AnimatedVisibility(
            visible = showCoreExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                HolographicGlassCard(
                    borderColor = CyanNeon,
                    glowEffect = !uiState.config.lowEffectsMode,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                    text = "CANVAS SHADER // NEURAL SINGULARITY",
                                    color = CyanNeon,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CrimsonGlow
                            ) {
                                Text(
                                    text = if (isSpeaking) "AUDIO WAVE OSCILLATING" else if (uiState.isAiThinking) "THINKING" else "HARMONIC",
                                    color = CrimsonWeb,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Canvas Shader Component
                        ArachneHolographicCore(
                            size = 170.dp,
                            isSpeaking = isSpeaking,
                            isThinking = uiState.isAiThinking,
                            isListening = uiState.isListeningVoice,
                            lowEffectsMode = uiState.config.lowEffectsMode,
                            onClick = {
                                onSendMessage("Run neural core diagnostic scan")
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Shader Telemetry Readouts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CORE FLUX", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("432.8 THz", fontSize = 12.sp, color = CyanNeon, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("STABILITY", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("99.8%", fontSize = 12.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("INTERFERENCE", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("CRIMSON/CYAN", fontSize = 12.sp, color = CrimsonWeb, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("NODES", fontSize = 9.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
                                Text("64 ACTIVE", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Command Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33101E36),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                    modifier = Modifier.clickable { onSendMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        color = TextCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.chatMessages, key = { it.id }) { msg ->
                ChatBubble(msg = msg)
            }

            if (uiState.isAiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = CyanNeon,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ARACHNE is analyzing neural pathways...",
                            color = TextCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field & Voice Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Command ARACHNE or speak...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = Color(0x4408101E),
                    unfocusedContainerColor = Color(0x4408101E)
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Voice button
            Surface(
                shape = CircleShape,
                color = if (isSpeaking) CrimsonGlow else CyanGlow,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) CrimsonWeb else CyanNeon),
                modifier = Modifier
                    .size(46.dp)
                    .clickable { onVoiceClick() }
                    .testTag("mic_voice_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isSpeaking) CrimsonWeb else CyanNeon,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Send button
            Surface(
                shape = CircleShape,
                color = CyanNeon,
                modifier = Modifier
                    .size(46.dp)
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
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.sender == MessageSender.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "COMMANDER" else "ARACHNE // TACTICAL",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (isUser) CrimsonWeb else CyanNeon
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = msg.timestamp,
                fontSize = 9.sp,
                color = TextMuted
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Color(0x55301018) else Color(0x550A223D),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) CrimsonWeb.copy(alpha = 0.5f) else CyanNeon.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(
                text = msg.text,
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}
