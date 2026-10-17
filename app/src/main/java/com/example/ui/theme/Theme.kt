package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JarvisColorScheme = darkColorScheme(
    primary = CyanNeon,
    onPrimary = Color(0xFF040A12),
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = Color(0xFF70F5FF),
    secondary = CrimsonWeb,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF49000C),
    onSecondaryContainer = Color(0xFFFFB3B8),
    tertiary = HologramViolet,
    background = MidnightNavy,
    onBackground = TextWhite,
    surface = MidnightNavyLight,
    onSurface = TextWhite,
    surfaceVariant = GlassSurfaceBright,
    onSurfaceVariant = TextMuted,
    outline = GlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisColorScheme,
        typography = Typography,
        content = content
    )
}
