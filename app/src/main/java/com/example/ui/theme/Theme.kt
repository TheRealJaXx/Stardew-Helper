package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WalnutGoldLight,
    onPrimary = DarkBackground,
    primaryContainer = WalnutGoldDark,
    onPrimaryContainer = Color.White,
    secondary = IslandGreenLight,
    onSecondary = Color.White,
    secondaryContainer = IslandGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = WoodBrownLight,
    background = DarkBackground,
    onBackground = Color(0xFFEDE9E3),
    surface = DarkSurface,
    onSurface = Color(0xFFEDE9E3),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCCC5B9),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = WalnutGoldDark,
    onPrimary = Color.White,
    primaryContainer = WalnutGoldLight,
    onPrimaryContainer = WoodBrownDark,
    secondary = IslandGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6EAD7),
    onSecondaryContainer = IslandGreenDark,
    tertiary = WoodBrown,
    background = ParchmentLight,
    onBackground = Color(0xFF28231D),
    surface = ParchmentSurface,
    onSurface = Color(0xFF28231D),
    surfaceVariant = Color(0xFFEFE8DA),
    onSurfaceVariant = Color(0xFF5A5248),
    outline = ParchmentBorder
)

@Composable
fun StardewHelperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to custom cozy Stardew palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = StardewHelperTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
