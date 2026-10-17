package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.CrimsonWeb
import com.example.ui.theme.TextMuted

@Composable
fun CryptoChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = CyanNeon,
    isBullish: Boolean = true
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Awaiting market telemetry...", color = TextMuted, fontSize = 12.sp)
        }
        return
    }

    val minVal = points.minOrNull() ?: 0f
    val maxVal = points.maxOrNull() ?: 1f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33080E1C))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            // Draw horizontal grid lines
            for (i in 1..3) {
                val y = height * (i / 4f)
                drawLine(
                    color = Color(0x1A00F0FF),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            val path = Path()
            val fillPath = Path()

            val coordinates = points.mapIndexed { index, value ->
                val x = index * stepX
                val normalizedY = 1f - ((value - minVal) / range)
                val y = (normalizedY * (height - 30f)) + 15f
                Offset(x, y)
            }

            if (coordinates.isNotEmpty()) {
                path.moveTo(coordinates[0].x, coordinates[0].y)
                fillPath.moveTo(coordinates[0].x, height)
                fillPath.lineTo(coordinates[0].x, coordinates[0].y)

                for (i in 0 until coordinates.size - 1) {
                    val p0 = coordinates[i]
                    val p1 = coordinates[i + 1]
                    val controlX1 = (p0.x + p1.x) / 2f
                    val controlY1 = p0.y
                    val controlX2 = (p0.x + p1.x) / 2f
                    val controlY2 = p1.y

                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                }

                fillPath.lineTo(coordinates.last().x, height)
                fillPath.close()

                // Draw gradient under curve
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        (if (isBullish) lineColor else CrimsonWeb).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
                drawPath(path = fillPath, brush = gradientBrush)

                // Draw line stroke
                drawPath(
                    path = path,
                    color = if (isBullish) lineColor else CrimsonWeb,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw point on current price (last coordinate)
                val lastPoint = coordinates.last()
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = lastPoint
                )
                drawCircle(
                    color = if (isBullish) lineColor else CrimsonWeb,
                    radius = 8.dp.toPx(),
                    center = lastPoint,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // High & Low indicators
        Text(
            text = "H: $${String.format("%,.0f", maxVal)}",
            color = CyanNeon,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        )
        Text(
            text = "L: $${String.format("%,.0f", minVal)}",
            color = CrimsonWeb,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(4.dp)
        )
    }
}
