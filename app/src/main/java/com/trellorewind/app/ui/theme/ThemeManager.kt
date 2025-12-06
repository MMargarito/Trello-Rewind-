package com.trellorewind.app.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/**
 * Manager class to handle dark mode state with persistence
 */
class ThemeManager(private val context: Context) {
    
    private val prefs = context.getSharedPreferences("kanhero_prefs", Context.MODE_PRIVATE)
    
    private var _isDarkMode by mutableStateOf(prefs.getBoolean("dark_mode", false))
    
    var isDarkMode: Boolean
        get() = _isDarkMode
        set(value) {
            _isDarkMode = value
            prefs.edit().putBoolean("dark_mode", value).apply()
        }
}

@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager(context) }
}


