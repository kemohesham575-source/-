package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    secondary = EmeraldSecondaryLight,
    tertiary = GoldAccentLight,
    background = CreamBackgroundLight,
    surface = CreamSurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextDarkLight,
    onSurface = TextDarkLight,
    surfaceVariant = Color(0xFFECE5D8),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldSecondaryDark,
    secondary = EmeraldPrimaryDark,
    tertiary = GoldAccentDark,
    background = DeepBackgroundDark,
    surface = CardBackgroundDark,
    onPrimary = TextLightDark,
    onSecondary = TextLightDark,
    onTertiary = DeepBackgroundDark,
    onBackground = TextLightDark,
    onSurface = TextLightDark,
    surfaceVariant = Color(0xFF243B30),
    onSurfaceVariant = Color(0xFFECE5D8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
