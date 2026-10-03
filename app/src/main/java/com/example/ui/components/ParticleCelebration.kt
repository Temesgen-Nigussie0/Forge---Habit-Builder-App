package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.ElectricOrange
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.SunsetAmber
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ParticleStyle {
    FIREWORKS,
    GOLDEN_SPARKS,
    WATER_BUBBLES
}

private data class Particle(
    val startX: Float,
    val startY: Float,
    val angle: Double,
    val speed: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ParticleCelebration(
    trigger: Boolean,
    style: ParticleStyle = ParticleStyle.FIREWORKS,
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }

    val particles = remember(style) {
        val count = when (style) {
            ParticleStyle.FIREWORKS -> 55
            ParticleStyle.GOLDEN_SPARKS -> 32
            ParticleStyle.WATER_BUBBLES -> 28
        }
        val palette = when (style) {
            ParticleStyle.FIREWORKS -> listOf(ElectricOrange, NeonOrange, GoldYellow, SuccessEmerald, CyanBlue)
            ParticleStyle.GOLDEN_SPARKS -> listOf(GoldYellow, SunsetAmber, ElectricOrange)
            ParticleStyle.WATER_BUBBLES -> listOf(CyanBlue, Color(0xFF64D2FF), Color(0xFF007AFF))
        }

        List(count) {
            val angle = when (style) {
                ParticleStyle.WATER_BUBBLES -> Math.toRadians((Random.nextFloat() * 80 + 230).toDouble()) // upward cone
                ParticleStyle.GOLDEN_SPARKS -> Math.toRadians((Random.nextFloat() * 120 + 210).toDouble()) // upward fountain
                else -> Math.toRadians(Random.nextFloat() * 360.toDouble()) // 360 burst
            }
            Particle(
                startX = 0.5f,
                startY = if (style == ParticleStyle.WATER_BUBBLES) 0.85f else 0.45f,
                angle = angle,
                speed = Random.nextFloat() * 320f + 120f,
                size = Random.nextFloat() * 7f + 3f,
                color = palette.random()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = if (style == ParticleStyle.FIREWORKS) 1200 else 850,
                easing = LinearEasing
            )
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val t = progress.value

        particles.forEach { p ->
            val originX = width * p.startX
            val originY = height * p.startY

            // Physics arc with gentle gravity
            val currentDistance = p.speed * t
            val gravity = if (style == ParticleStyle.WATER_BUBBLES) -60f * t else 220f * t * t

            val currentX = (originX + currentDistance * cos(p.angle)).toFloat()
            val currentY = (originY + currentDistance * sin(p.angle) + gravity).toFloat()

            val alpha = (1f - t).coerceIn(0f, 1f)

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.size * (1f - t * 0.4f),
                center = Offset(currentX, currentY)
            )
        }
    }
}
