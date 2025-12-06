package com.trellorewind.app.interaction

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class HapticsManager(private val context: Context) {
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    var isEnabled: Boolean = true
    
    /**
     * Light vibration for card drag start
     */
    fun lightTap() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
    
    /**
     * Medium vibration for card drop
     */
    fun mediumTap() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(25)
        }
    }
    
    /**
     * Success vibration for completing a card (moving to DONE)
     */
    fun successTap() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 50, 100)
            val amplitudes = intArrayOf(0, 128, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
    
    /**
     * Heavy vibration for delete actions
     */
    fun heavyTap() {
        if (!isEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
}

@Composable
fun rememberHapticsManager(): HapticsManager {
    val context = LocalContext.current
    return remember { HapticsManager(context) }
}

