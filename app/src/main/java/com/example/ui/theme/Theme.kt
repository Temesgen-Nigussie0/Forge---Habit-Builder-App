package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricOrange,
    onPrimary = Color.White,
    primaryContainer = OrangeSubtle,
    onPrimaryContainer = BrightOrange,
    secondary = SunsetAmber,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = SunsetAmber,
    tertiary = CoralRed,
    onTertiary = Color.White,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderHighlight
)

@Composable
fun ForgeHabitsTheme(
    darkTheme: Boolean = true, // Default to sleek premium dark theme as requested
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards-compatible alias for existing references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ForgeHabitsTheme(content = content)
}
