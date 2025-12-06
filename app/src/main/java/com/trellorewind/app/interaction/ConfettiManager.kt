package com.trellorewind.app.interaction

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ConfettiManager {
    
    var isEnabled: Boolean = true
    
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying
    
    fun trigger() {
        if (!isEnabled) return
        _isPlaying.value = true
    }
    
    fun stop() {
        _isPlaying.value = false
    }
}

@Composable
fun rememberConfettiManager(): ConfettiManager {
    return remember { ConfettiManager() }
}

@Composable
fun ConfettiAnimation(
    confettiManager: ConfettiManager,
    modifier: Modifier = Modifier
) {
    val isPlaying by confettiManager.isPlaying
    
    if (isPlaying) {
        var particles by remember { mutableStateOf(createConfettiParticles()) }
        val infiniteTransition = rememberInfiniteTransition(label = "confetti")
        
        val animationProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "progress"
        )
        
        LaunchedEffect(isPlaying) {
            if (isPlaying) {
                particles = createConfettiParticles()
                delay(2000)
                confettiManager.stop()
            }
        }
        
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                drawConfettiParticle(particle, animationProgress)
            }
        }
    }
}

private fun createConfettiParticles(): List<ConfettiParticle> {
    return List(50) {
        ConfettiParticle(
            x = Random.nextFloat(),
            y = -0.1f,
            vx = (Random.nextFloat() - 0.5f) * 2f,
            vy = Random.nextFloat() * 2f + 1f,
            rotation = Random.nextFloat() * 360f,
            rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
            color = confettiColors.random(),
            size = Random.nextFloat() * 8f + 4f
        )
    }
}

private fun DrawScope.drawConfettiParticle(particle: ConfettiParticle, progress: Float) {
    val x = (particle.x + particle.vx * progress) * size.width
    val y = (particle.y + particle.vy * progress) * size.height
    
    if (y > size.height) return
    
    val currentRotation = particle.rotation + particle.rotationSpeed * progress * 360f
    val alpha = (1f - progress).coerceIn(0f, 1f)
    
    // Draw simple circle confetti
    drawCircle(
        color = particle.color.copy(alpha = alpha),
        radius = particle.size,
        center = Offset(x, y)
    )
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float
)

private val confettiColors = listOf(
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Purple
    Color(0xFF2196F3), // Blue
    Color(0xFF4CAF50), // Green
    Color(0xFFFFEB3B), // Yellow
    Color(0xFFFF9800), // Orange
    Color(0xFFF44336), // Red
)

