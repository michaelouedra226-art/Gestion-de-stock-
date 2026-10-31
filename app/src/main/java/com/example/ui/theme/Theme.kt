package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalBrandColor = staticCompositionLocalOf { Color(0xFF5EEAD4) }

private val StockFlowDarkColorScheme = darkColorScheme(
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF0A0E1A),
    primaryContainer = Color(0xFF133834),
    onPrimaryContainer = Color(0xFF99F6E4),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF1E1145),
    secondaryContainer = Color(0xFF332066),
    onSecondaryContainer = Color(0xFFDDD6FE),
    tertiary = Color(0xFF38BDF8),
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = RedDanger,
    onError = Color.White
)

private val StockFlowLightColorScheme = lightColorScheme(
    primary = Color(0xFF0D9488),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = Color(0xFF7C3AED),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = RedDanger,
    onError = Color.White
)

@Composable
fun StockFlowTheme(
    brandColor: Color = Color(0xFF5EEAD4),
    darkTheme: Boolean = true, // Default to premium dark mode
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) StockFlowDarkColorScheme else StockFlowLightColorScheme
    val dynamicScheme = baseScheme.copy(
        primary = brandColor,
        onPrimary = if (darkTheme) Color(0xFF0A0E1A) else Color.White
    )

    CompositionLocalProvider(LocalBrandColor provides brandColor) {
        MaterialTheme(
            colorScheme = dynamicScheme,
            typography = Typography,
            content = content
        )
    }
}
