package com.iptvplayer.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AccentCyan = Color(0xFF34D8E0)
val BackgroundDark = Color(0xFF0A1520)
val SurfaceDark = Color(0xFF101E2E)
val SurfaceVariantDark = Color(0xFF15222F)
val TextPrimary = Color(0xFFEAF2F8)
val TextMuted = Color(0xFF7C8B9C)
val LiveRed = Color(0xFFC23B3B)

private val AppColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextMuted,
    error = LiveRed,
)

@Composable
fun NextAIPtvPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColorScheme, content = content)
}
