package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceBright
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class NeonBarTheme {
    CYAN,
    CRIMSON,
    AMBER,
    AUTO_THRESHOLD // Cyan < 75%, Amber 75-89%, Crimson >= 90%
}

@Composable
fun NeonScanlineProgressBar(
    modifier: Modifier = Modifier,
    label: String,
    progress: Float, // 0.0f .. 1.0f
    subLabel: String = "",
    barHeight: Dp = 16.dp,
    barTheme: NeonBarTheme = NeonBarTheme.AUTO_THRESHOLD,
    lowEffectsMode: Boolean = false,
    showStatusBadge: Boolean = true
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val percentageInt = (clampedProgress * 100).toInt()

    // Determine color based on theme / threshold
    val (primaryColor, glowColor, secondaryColor) = when (barTheme) {
        NeonBarTheme.CYAN -> Triple(CyanNeon, CyanGlow, Color(0xFF0090A8))
        NeonBarTheme.CRIMSON -> Triple(CrimsonWeb, CrimsonGlow, Color(0xFFB71C1C))
        NeonBarTheme.AMBER -> Triple(Color(0xFFFFAB00), Color(0x40FFAB00), Color(0xFFFF6D00))
        NeonBarTheme.AUTO_THRESHOLD -> when {
            percentageInt >= 90 -> Triple(CrimsonWeb, CrimsonGlow, Color(0xFFB71C1C))
            percentageInt >= 75 -> Triple(Color(0xFFFFAB00), Color(0x40FFAB00), Color(0xFFFF6D00))
            else -> Triple(CyanNeon, CyanGlow, Color(0xFF0090A8))
        }
    }

    val statusText = when {
        percentageInt >= 90 -> "CRITICAL LOAD"
        percentageInt >= 75 -> "ELEVATED"
        percentageInt >= 45 -> "NOMINAL"
        else -> "OPTIMAL"
    }

    // Scanline traversing laser beam animation
    val transition = rememberInfiniteTransition(label = "scanline_laser")
    val laserOffsetNorm by if (!lowEffectsMode && clampedProgress > 0.05f) {
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "laser_pos"
        )
    } else {
        remember { mutableFloatStateOf(0.5f) }
    }

    val pulseGlow by if (!lowEffectsMode) {
        transition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_glow"
        )
    } else {
        remember { mutableFloatStateOf(0.85f) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label & Percentage Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                if (showStatusBadge) {
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = glowColor
                    ) {
                        Text(
                            text = statusText,
                            color = primaryColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = "$percentageInt%",
                color = primaryColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Glassmorphic Track with Neon Scanline Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(barHeight / 2))
                .background(Color(0x66080F1E))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            primaryColor.copy(alpha = 0.6f * pulseGlow),
                            primaryColor.copy(alpha = 0.2f),
                            primaryColor.copy(alpha = 0.5f * pulseGlow)
                        )
                    ),
                    shape = RoundedCornerShape(barHeight / 2)
                )
                .testTag("neon_bar_${label.lowercase().replace(" ", "_")}")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val cornerRadius = CornerRadius(height / 2f, height / 2f)
                val fillWidth = width * clampedProgress

                // 1. Draw Unfilled Background Scanline Lattice (Translucent)
                val scanlineSpacing = 4.dp.toPx()
                var scanX = 0f
                while (scanX < width) {
                    drawLine(
                        color = Color(0x1500F0FF),
                        start = Offset(scanX, 0f),
                        end = Offset(scanX + height * 0.8f, height),
                        strokeWidth = 1f
                    )
                    scanX += scanlineSpacing
                }

                if (fillWidth > 0f) {
                    // 2. Draw Filled Neon Gradient Progress Body
                    val progressBrush = Brush.horizontalGradient(
                        colors = listOf(
                            secondaryColor.copy(alpha = 0.85f),
                            primaryColor.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.85f)
                        ),
                        startX = 0f,
                        endX = fillWidth
                    )

                    drawRoundRect(
                        brush = progressBrush,
                        topLeft = Offset.Zero,
                        size = Size(fillWidth, height),
                        cornerRadius = cornerRadius
                    )

                    // 3. Dense High-Tech Neon Scanlines inside filled region
                    val filledScanSpacing = 3.dp.toPx()
                    var fx = 0f
                    while (fx < fillWidth) {
                        drawLine(
                            color = Color(0x33000000),
                            start = Offset(fx, 0f),
                            end = Offset(fx + height * 0.6f, height),
                            strokeWidth = 1.2f
                        )
                        fx += filledScanSpacing
                    }

                    // 4. Horizontal Luminous Center Core Line
                    drawLine(
                        color = Color.White.copy(alpha = 0.5f),
                        start = Offset(height / 4f, height / 2f),
                        end = Offset(fillWidth - height / 4f, height / 2f),
                        strokeWidth = 1.5f
                    )

                    // 5. Traveling Neon Laser Beam Scanline
                    if (!lowEffectsMode) {
                        val laserX = fillWidth * laserOffsetNorm
                        val laserBeamWidth = 18.dp.toPx()

                        val laserBrush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.8f),
                                Color.White,
                                Color.White.copy(alpha = 0.8f),
                                Color.Transparent
                            ),
                            startX = (laserX - laserBeamWidth / 2).coerceAtLeast(0f),
                            endX = (laserX + laserBeamWidth / 2).coerceAtMost(fillWidth)
                        )

                        drawRect(
                            brush = laserBrush,
                            topLeft = Offset((laserX - laserBeamWidth / 2).coerceAtLeast(0f), 0f),
                            size = Size(laserBeamWidth, height)
                        )
                    }

                    // 6. Glowing End-Cap Laser Boundary Head
                    if (fillWidth > 6.dp.toPx()) {
                        drawCircle(
                            color = Color.White,
                            radius = (height / 2f) * 0.72f,
                            center = Offset(fillWidth - height / 2f, height / 2f)
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = (height / 2f) * 0.95f,
                            center = Offset(fillWidth - height / 2f, height / 2f),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }

        // Sub-label (e.g. detailed memory allocation or frequency)
        if (subLabel.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = subLabel,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.1f", clampedProgress * 100)}%",
                    color = TextCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
