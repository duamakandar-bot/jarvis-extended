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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.CrimsonGlow
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun NeonCyanTrendLineCard(
    modifier: Modifier = Modifier,
    asset: CryptoAsset?,
    chartHeight: Dp = 150.dp,
    isRefreshing: Boolean = false,
    lowEffectsMode: Boolean = false,
    onRefreshClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    val btc = asset ?: return

    val isPositive = btc.change24hPercent >= 0
    val trendColor = CyanNeon // Requested: neon cyan trend line

    // Live beacon pulse
    val transition = rememberInfiniteTransition(label = "beacon_pulse")
    val beaconAlpha by if (!lowEffectsMode) {
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "beacon_alpha"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    val nodeGlowRadius by if (!lowEffectsMode) {
        transition.animateFloat(
            initialValue = 6f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "node_glow"
        )
    } else {
        remember { mutableFloatStateOf(6f) }
    }

    HolographicGlassCard(
        borderColor = CyanNeon,
        glowEffect = !lowEffectsMode,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("neon_cyan_btc_card")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Live Feed Header Status Bar
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
                            .background(Color(0xFF00E676).copy(alpha = beaconAlpha))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE RETROFIT FEED // BTCUSDT",
                        color = CyanNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0x3300F0FF),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, CyanNeon)
                    ) {
                        Text(
                            text = "REAL-TIME",
                            color = CyanNeon,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onRefreshClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                color = CyanNeon,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price & 24h Change Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BITCOIN PRICE (USD)",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$${String.format("%,.2f", btc.currentPriceUsd)}",
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPositive) Color(0x3300E676) else CrimsonGlow,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPositive) Color(0xFF00E676) else CrimsonWeb
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = "Trend",
                            tint = if (isPositive) Color(0xFF00E676) else CrimsonWeb,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.2f", btc.change24hPercent)}%",
                            color = if (isPositive) Color(0xFF00E676) else CrimsonWeb,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // NEON CYAN TREND LINE CANVAS
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33060E1D))
                    .border(1.dp, Color(0x2200F0FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val points = btc.priceHistory
                if (points.isNotEmpty()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        val minVal = points.minOrNull() ?: 0f
                        val maxVal = points.maxOrNull() ?: 1f
                        val range = (maxVal - minVal).coerceAtLeast(1f)
                        val stepX = width / (points.size - 1).coerceAtLeast(1)

                        // 1. Draw horizontal neon cyan grid guide lines
                        for (i in 1..3) {
                            val gy = height * (i / 4f)
                            drawLine(
                                color = Color(0x1800F0FF),
                                start = Offset(0f, gy),
                                end = Offset(width, gy),
                                strokeWidth = 1f
                            )
                        }

                        // 2. Map Coordinates
                        val coords = points.mapIndexed { index, value ->
                            val x = index * stepX
                            val normalizedY = 1f - ((value - minVal) / range)
                            val y = (normalizedY * (height - 30f)) + 15f
                            Offset(x, y)
                        }

                        val linePath = Path()
                        val fillPath = Path()

                        linePath.moveTo(coords[0].x, coords[0].y)
                        fillPath.moveTo(coords[0].x, height)
                        fillPath.lineTo(coords[0].x, coords[0].y)

                        for (i in 0 until coords.size - 1) {
                            val p0 = coords[i]
                            val p1 = coords[i + 1]
                            val cX1 = (p0.x + p1.x) / 2f
                            val cY1 = p0.y
                            val cX2 = (p0.x + p1.x) / 2f
                            val cY2 = p1.y

                            linePath.cubicTo(cX1, cY1, cX2, cY2, p1.x, p1.y)
                            fillPath.cubicTo(cX1, cY1, cX2, cY2, p1.x, p1.y)
                        }

                        fillPath.lineTo(coords.last().x, height)
                        fillPath.close()

                        // 3. Neon Cyan Gradient Fill
                        val fillGradient = Brush.verticalGradient(
                            colors = listOf(
                                CyanNeon.copy(alpha = 0.35f),
                                CyanNeon.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                        drawPath(path = fillPath, brush = fillGradient)

                        // 4. Outer Neon Glow Path
                        if (!lowEffectsMode) {
                            drawPath(
                                path = linePath,
                                color = CyanNeon.copy(alpha = 0.4f),
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // 5. Core Radiant Neon Cyan Trend Line
                        drawPath(
                            path = linePath,
                            color = CyanNeon,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 6. Real-time Live Price Node at tip
                        val tip = coords.last()
                        // Outer pulsating bloom
                        drawCircle(
                            color = CyanNeon.copy(alpha = 0.5f),
                            radius = nodeGlowRadius.dp.toPx(),
                            center = tip
                        )
                        // Inner solid white-hot node
                        drawCircle(
                            color = Color.White,
                            radius = 4.5.dp.toPx(),
                            center = tip
                        )
                        // Border ring
                        drawCircle(
                            color = CyanNeon,
                            radius = 4.5.dp.toPx(),
                            center = tip,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }

                // Range High/Low HUD markers
                Text(
                    text = "HIGH: $${String.format("%,.0f", btc.high24h)}",
                    color = CyanNeon,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )

                Text(
                    text = "LOW: $${String.format("%,.0f", btc.low24h)}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "24H VOL: ${btc.volume24hUsd}",
                    color = TextCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "CAP: ${btc.marketCapUsd}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SIGNAL: ${btc.macdSignal}",
                    color = if (isPositive) Color(0xFF00E676) else CrimsonWeb,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
