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
    primary = Emerald80,
    onPrimary = Color(0xFF003828),
    primaryContainer = Emerald40,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Teal80,
    onSecondary = Color(0xFF003733),
    tertiary = Gold80,
    onTertiary = Color(0xFF422000),
    background = DarkBackground,
    onBackground = Color(0xFFECFDF5),
    surface = DarkSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD1E7DD),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF002117),
    secondary = Teal40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF042F2C),
    tertiary = Gold40,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    background = LightBackground,
    onBackground = Color(0xFF0F2922),
    surface = LightSurface,
    onSurface = Color(0xFF0F2922),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF234E43),
    error = ExpenseRed,
    onError = Color.White
)

@Composable
fun BudgetPakistanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep emerald brand identity solid
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
