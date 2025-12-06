package com.trellorewind.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Custom dark color scheme with purple accents
private val DarkColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = Color.Black,
    secondary = PurpleDark,
    onSecondary = Color.White,
    tertiary = Pink80,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0B0B0),
    primaryContainer = Color(0xFF3D3D4E),
    onPrimaryContainer = PurpleAccent
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Data class to hold theme-aware colors for Kanban columns
data class KanheroColors(
    val todoColumn: Color,
    val doingColumn: Color,
    val doneColumn: Color,
    val cardBackground: Color,
    val cardShadow: Color
)

// CompositionLocal for custom Kanhero colors
val LocalKanheroColors = staticCompositionLocalOf {
    KanheroColors(
        todoColumn = TodoColumnColorLight,
        doingColumn = DoingColumnColorLight,
        doneColumn = DoneColumnColorLight,
        cardBackground = CardBackgroundLight,
        cardShadow = CardShadowLight
    )
}

@Composable
fun KanheroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val kanheroColors = if (darkTheme) {
        KanheroColors(
            todoColumn = TodoColumnColorDark,
            doingColumn = DoingColumnColorDark,
            doneColumn = DoneColumnColorDark,
            cardBackground = CardBackgroundDark,
            cardShadow = CardShadowDark
        )
    } else {
        KanheroColors(
            todoColumn = TodoColumnColorLight,
            doingColumn = DoingColumnColorLight,
            doneColumn = DoneColumnColorLight,
            cardBackground = CardBackgroundLight,
            cardShadow = CardShadowLight
        )
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (darkTheme) DarkBackground.toArgb() else colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalKanheroColors provides kanheroColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Extension to easily access Kanhero colors
object KanheroTheme {
    val colors: KanheroColors
        @Composable
        get() = LocalKanheroColors.current
}

