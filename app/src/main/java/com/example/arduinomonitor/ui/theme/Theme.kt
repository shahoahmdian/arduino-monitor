package com.example.arduinomonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentPurple,
    tertiary = AccentAmber,
    background = DeepSpace,
    surface = SurfaceDark,
    error = DangerRed,
    onPrimary = DeepSpace,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ArduinoMonitorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
