package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ResolvitosLightColorScheme = lightColorScheme(
    primary = ResolvitosSlate,
    onPrimary = ResolvitosYellow,
    primaryContainer = ResolvitosYellow,
    onPrimaryContainer = ResolvitosSlate,
    secondary = ResolvitosSlateSurface,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = ResolvitosSlate,
    tertiary = StatusEmerald,
    onTertiary = Color.White,
    background = ResolvitosBackground,
    onBackground = ResolvitosSlate,
    surface = ResolvitosCardWhite,
    onSurface = ResolvitosSlate,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = ResolvitosSlateMuted,
    error = StatusCoral,
    onError = Color.White
)

private val ResolvitosDarkColorScheme = darkColorScheme(
    primary = ResolvitosYellow,
    onPrimary = ResolvitosSlate,
    primaryContainer = Color(0xFF332B00),
    onPrimaryContainer = ResolvitosYellow,
    secondary = Color(0xFF94A3B8),
    onSecondary = ResolvitosSlate,
    tertiary = StatusEmerald,
    onTertiary = Color.White,
    background = ResolvitosSlate,
    onBackground = Color.White,
    surface = ResolvitosSlateSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = StatusCoral,
    onError = Color.White
)

@Composable
fun ResolvitosTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ResolvitosDarkColorScheme else ResolvitosLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
