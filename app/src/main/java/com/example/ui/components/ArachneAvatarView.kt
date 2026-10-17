package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextWhite

@Composable
fun ArachneAvatarView(
    modifier: Modifier = Modifier,
    avatarSize: Dp = 100.dp,
    isSpeaking: Boolean = false,
    isThinking: Boolean = false,
    isListening: Boolean = false,
    lowEffectsMode: Boolean = false,
    showCoreShaderMode: Boolean = false,
    onAvatarClick: () -> Unit = {}
) {
    var displayShaderCore by remember { mutableStateOf(showCoreShaderMode) }

    val infiniteTransition = rememberInfiniteTransition(label = "energy_pulse")
    val pulseScale by if (!lowEffectsMode) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = if (isSpeaking || isThinking) 1.14f else 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = if (isSpeaking) 600 else 1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    val stateColor = when {
        isSpeaking -> CyanNeon
        isThinking -> CrimsonWeb
        isListening -> Color(0xFF00E676)
        else -> CyanNeon
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize + 28.dp)
                .clickable {
                    displayShaderCore = !displayShaderCore
                    onAvatarClick()
                }
                .testTag("arachne_avatar_button"),
            contentAlignment = Alignment.Center
        ) {
            if (displayShaderCore) {
                // Pure Canvas Shader Core Representation
                ArachneHolographicCore(
                    size = avatarSize + 24.dp,
                    isSpeaking = isSpeaking,
                    isThinking = isThinking,
                    isListening = isListening,
                    lowEffectsMode = lowEffectsMode
                )
            } else {
                // Portrait representation with energetic pulsing rings & shader aura
                if (!lowEffectsMode) {
                    Box(
                        modifier = Modifier
                            .size(avatarSize + 24.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        CyanNeon.copy(alpha = 0.85f),
                                        CrimsonWeb.copy(alpha = 0.4f),
                                        CrimsonWeb.copy(alpha = 0.85f),
                                        CyanNeon.copy(alpha = 0.85f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                }

                // Energy Ring 2
                Box(
                    modifier = Modifier
                        .size(avatarSize + 10.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = stateColor.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                )

                // Holographic Avatar Portrait
                Image(
                    painter = painterResource(id = R.drawable.avatar_arachne),
                    contentDescription = "ARACHNE Holographic AI Avatar",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = CrimsonWeb.copy(alpha = 0.6f),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            // Audio Wave indicator icon if speaking
            if (isSpeaking) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp),
                    shape = CircleShape,
                    color = CyanNeon,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speaking",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else if (isListening) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp),
                    shape = CircleShape,
                    color = Color(0xFF00E676),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Listening",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Name & Status Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ARACHNE",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextWhite,
                letterSpacing = 1.2.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSpeaking) CyanGlow else if (isThinking) CrimsonGlow else GlassSurfaceBright
            ) {
                Text(
                    text = when {
                        isSpeaking -> "SYNTHESIZING"
                        isThinking -> "PROCESSING"
                        isListening -> "LISTENING"
                        displayShaderCore -> "CORE ACTIVE"
                        else -> "ONLINE"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = stateColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
