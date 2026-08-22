package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,
    val maxLife: Float,
    val color: Color,
    val radius: Float,
)

/**
 * 一个极简的粒子系统：burst() 在指定位置炸出一堆彩色小点，
 * step() 每帧推进物理（重力 + 减速 + 衰减）。
 */
class ParticleSystem {
    var particles by mutableStateOf(emptyList<Particle>())
        private set

    fun burst(x: Float, y: Float, colors: List<Color>, count: Int = 14, speed: Float = 420f) {
        val new = (0 until count).map {
            val angle = Math.random() * Math.PI * 2.0
            val v = speed * (0.4 + Math.random() * 0.8)
            val life = 0.55f + (Math.random() * 0.45).toFloat()
            Particle(
                x = x,
                y = y,
                vx = (cos(angle) * v).toFloat(),
                vy = (sin(angle) * v).toFloat() - 140f,
                life = life,
                maxLife = life,
                color = colors[(Math.random() * colors.size).toInt()],
                radius = 3f + (Math.random() * 5).toFloat(),
            )
        }
        particles = particles + new
    }

    fun step(dt: Float) {
        if (particles.isEmpty()) return
        particles = particles.map { p ->
            p.copy(
                x = p.x + p.vx * dt,
                y = p.y + p.vy * dt,
                vy = p.vy + 520f * dt,
                vx = p.vx * (1f - 2.4f * dt),
                life = p.life - dt,
            )
        }.filter { it.life > 0f }
    }
}

@Composable
fun ParticleCanvas(system: ParticleSystem, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        system.particles.forEach { p ->
            val a = (p.life / p.maxLife).coerceIn(0f, 1f)
            drawCircle(p.color.copy(alpha = a), radius = p.radius, center = Offset(p.x, p.y))
        }
    }
}
