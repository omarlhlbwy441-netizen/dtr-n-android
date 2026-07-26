package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = OnyxDark,
    primaryContainer = OnyxCard,
    onPrimaryContainer = GoldLight,
    secondary = StatusPurple,
    onSecondary = Color.White,
    background = OnyxDark,
    onBackground = Color.White,
    surface = OnyxSurface,
    onSurface = Color.White,
    surfaceVariant = OnyxCard,
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F2937),
    onPrimary = Color.White,
    primaryContainer = StatusPurpleBg,
    onPrimaryContainer = StatusPurple,
    secondary = StatusBlue,
    onSecondary = Color.White,
    background = MarbleBackground,
    onBackground = MarbleTextPrimary,
    surface = MarbleSurface,
    onSurface = MarbleTextPrimary,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = MarbleTextSecondary,
    outline = MarbleCardBorder
)

@Composable
fun SmartAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
