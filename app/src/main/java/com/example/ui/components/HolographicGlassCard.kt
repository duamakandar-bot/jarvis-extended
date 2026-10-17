package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GlassBorder

@Composable
fun HolographicGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GlassBorder,
    glowEffect: Boolean = true,
    cornerRounding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRounding)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0x55111D33),
            Color(0x330B1324)
        )
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = if (glowEffect) {
                        Brush.linearGradient(
                            listOf(
                                borderColor.copy(alpha = 0.8f),
                                borderColor.copy(alpha = 0.2f),
                                borderColor.copy(alpha = 0.6f)
                            )
                        )
                    } else {
                        Brush.linearGradient(listOf(borderColor.copy(alpha = 0.3f), borderColor.copy(alpha = 0.1f)))
                    }
                ),
                shape = shape
            ),
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(14.dp),
            content = content
        )
    }
}
