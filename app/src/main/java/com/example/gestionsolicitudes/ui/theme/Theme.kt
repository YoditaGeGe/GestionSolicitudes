package com.example.gestionsolicitudes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = TealDark,
    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = CoralContainer,
    onSecondaryContainer = Color(0xFF7A2E1D),
    tertiary = Color(0xFF4C6EF5),
    background = SoftBackground,
    onBackground = Ink,
    surface = SoftSurface,
    onSurface = Ink,
    surfaceVariant = SoftSurfaceVariant,
    onSurfaceVariant = MutedInk,
    outline = SoftOutline,
    error = Color(0xFFD64545)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7DD3C5),
    onPrimary = Color(0xFF003730),
    primaryContainer = Color(0xFF1F5C54),
    secondary = Color(0xFFFFB4A2),
    background = Color(0xFF12201D),
    onBackground = Color(0xFFE6F2EF),
    surface = Color(0xFF18302B),
    onSurface = Color(0xFFE6F2EF),
    surfaceVariant = Color(0xFF24423C),
    onSurfaceVariant = Color(0xFFC5D8D2)
)

@Composable
fun GestionSolicitudesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
