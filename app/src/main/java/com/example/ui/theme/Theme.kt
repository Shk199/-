package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldSoft,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = EmeraldLight,
    secondary = GoldPrimary,
    onSecondary = DarkBg,
    secondaryContainer = GoldDark,
    onSecondaryContainer = GoldLight,
    background = DarkBg,
    onBackground = Color(0xFFE5EDE8),
    surface = DarkSurface,
    onSurface = Color(0xFFE5EDE8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBDCFC5),
    error = RoseSoft,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldLight,
    onPrimaryContainer = EmeraldDark,
    secondary = GoldPrimary,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = GoldDark,
    background = SandBackground,
    onBackground = TextPrimaryDark,
    surface = SurfaceCream,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantWarm,
    onSurfaceVariant = TextSecondaryDark,
    error = RoseSoft,
    onError = Color.White
)

@Composable
fun RaqebTheme(
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
