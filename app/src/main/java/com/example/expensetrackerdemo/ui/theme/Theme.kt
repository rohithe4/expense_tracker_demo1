package com.example.expensetrackerdemo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    onPrimary = ColorSurface,
    primaryContainer = ColorPrimaryLight,
    onPrimaryContainer = ColorText,
    secondary = ColorPrimarySubtle,
    onSecondary = ColorText,
    secondaryContainer = ColorSurface2,
    onSecondaryContainer = ColorText,
    tertiary = ColorChart1,
    onTertiary = ColorSurface,
    tertiaryContainer = ColorChart2,
    onTertiaryContainer = ColorText,
    error = ColorError,
    onError = ColorSurface,
    errorContainer = ColorError,
    onErrorContainer = ColorSurface,
    background = ColorBg,
    onBackground = ColorText,
    surface = ColorSurface,
    onSurface = ColorText,
    surfaceVariant = ColorSurface2,
    onSurfaceVariant = ColorTextMuted,
    outline = ColorDivider,
    inverseOnSurface = ColorBg,
    inverseSurface = ColorText,
    inversePrimary = ColorPrimaryLight,
    surfaceTint = ColorPrimary,
    outlineVariant = ColorBorder,
    scrim = Color(0x33000000)
)

@Composable
fun ExpenseTrackerDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Ignoring dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}