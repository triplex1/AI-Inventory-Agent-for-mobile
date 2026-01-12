package com.vibeinventory.motorparts.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = IndustrialOrange,
    onPrimary = IndustrialBlack,
    primaryContainer = IndustrialGray,
    onPrimaryContainer = IndustrialWhite,
    
    secondary = IndustrialAmber,
    onSecondary = IndustrialBlack,
    secondaryContainer = IndustrialLightGray,
    onSecondaryContainer = IndustrialWhite,
    
    tertiary = IndustrialBlue,
    onTertiary = IndustrialBlack,
    tertiaryContainer = IndustrialGray,
    onTertiaryContainer = IndustrialWhite,
    
    error = IndustrialRed,
    onError = IndustrialWhite,
    errorContainer = Color(0xFF601410),
    onErrorContainer = IndustrialWhite,
    
    background = IndustrialBlack,
    onBackground = IndustrialWhite,
    
    surface = IndustrialSurface,
    onSurface = IndustrialWhite,
    surfaceVariant = IndustrialSurfaceVariant,
    onSurfaceVariant = IndustrialTextSecondary,
    
    outline = IndustrialBorder,
    outlineVariant = IndustrialLightGray,
    
    scrim = Color.Black,
)

@Composable
fun VibeInventoryTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IndustrialDarkColorScheme,
        typography = IndustrialTypography,
        content = content
    )
}
