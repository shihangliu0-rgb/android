package com.example.myapplication.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.CloudWhite
import com.example.myapplication.ui.theme.HeatBottom
import com.example.myapplication.ui.theme.HeatOverlay
import com.example.myapplication.ui.theme.HeatTop
import com.example.myapplication.ui.theme.SkyBottom
import com.example.myapplication.ui.theme.SkyTop
import com.example.myapplication.ui.theme.SunCore
import com.example.myapplication.ui.theme.SunGlow
import com.example.myapplication.ui.theme.SunRay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// ───────────────────────── 天空背景 ─────────────────────────

@Composable
fun SkyBackground(
    heat: Float = 0f,
    modifier: Modifier = Modifier,
) {
    val top = lerp(SkyTop, HeatTop, heat.coerceIn(0f, 1f))
    val bottom = lerp(SkyBottom, HeatBottom, heat.coerceIn(0f, 1f))
    Box(modifier = modifier.background(Brush.verticalGradient(listOf(top, bottom)))) {
        Clouds(Modifier.fillMaxSize())
        if (heat > 0.01f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(HeatOverlay.copy(alpha = 0.26f * heat.coerceIn(0f, 1f)))
            )
        }
    }
}

// ───────────────────────── 云 ─────────────────────────

@Composable
private fun Clouds(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier) {
        val w = with(density) { maxWidth.toPx() }
        val h = with(density) { maxHeight.toPx() }
        val t = rememberInfiniteTransition(label = "clouds")

        val d1 by t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(95_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "drift1",
        )
        val d2 by t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(140_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "drift2",
        )
        val d3 by t.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(115_000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "drift3",
        )

        Cloud(
            modifier = Modifier.offset { IntOffset((((d1 * 1.45f) - 0.25f) * w).roundToInt(), (h * 0.10f).roundToInt()) },
            scale = 1.0f,
            alpha = 0.95f,
        )
        Cloud(
            modifier = Modifier.offset { IntOffset((((d2 * 1.5f) - 0.35f) * w).roundToInt(), (h * 0.24f).roundToInt()) },
            scale = 0.72f,
            alpha = 0.65f,
        )
        Cloud(
            modifier = Modifier.offset { IntOffset((((d3 * 1.45f) - 0.2f) * w).roundToInt(), (h * 0.42f).roundToInt()) },
            scale = 0.55f,
            alpha = 0.8f,
        )
    }
}

@Composable
private fun Cloud(
    modifier: Modifier = Modifier,
    scale: Float,
    alpha: Float,
) {
    Canvas(
        modifier = modifier
            .size(width = 150.dp * scale, height = 62.dp * scale)
            .graphicsLayer { this.alpha = alpha },
    ) {
        val w = size.width
        val h = size.height
        drawCircle(CloudWhite, radius = h * 0.45f, center = Offset(w * 0.28f, h * 0.55f))
        drawCircle(CloudWhite, radius = h * 0.60f, center = Offset(w * 0.52f, h * 0.42f))
        drawCircle(CloudWhite, radius = h * 0.50f, center = Offset(w * 0.74f, h * 0.58f))
        drawOval(CloudWhite, topLeft = Offset(w * 0.12f, h * 0.50f), size = Size(w * 0.80f, h * 0.52f))
    }
}

// ───────────────────────── 太阳 ─────────────────────────

@Composable
fun Sun(
    modifier: Modifier = Modifier,
    diameter: Dp = 120.dp,
    heat: Float = 0f,
    spin: Boolean = true,
    contentAlignment: Alignment = Alignment.Center,
) {
    val t = rememberInfiniteTransition(label = "sun")
    val rot by t.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(90_000, easing = LinearEasing), RepeatMode.Restart),
        label = "rot",
    )
    val pulse by t.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse",
    )

    val core = lerp(SunCore, Color(0xFFFF6A3D), heat.coerceIn(0f, 1f))
    val ray = lerp(SunRay, Color(0xFFFF3D2E), heat.coerceIn(0f, 1f))
    val glow = lerp(SunGlow, Color(0xFFFFC49A), heat.coerceIn(0f, 1f))

    Box(modifier = modifier.size(diameter), contentAlignment = contentAlignment) {
        Canvas(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = pulse
                    scaleY = pulse
                    if (spin) rotationZ = rot
                },
        ) {
            val r = this.size.minDimension / 2f
            val c = Offset(this.size.width / 2f, this.size.height / 2f)

            // 光晕
            drawCircle(glow.copy(alpha = 0.55f), radius = r * 0.98f, center = c)

            // 光芒
            val rayPath = Path()
            val rayCount = 12
            for (i in 0 until rayCount) {
                val a = i * (360.0 / rayCount)
                val a1 = a - 10.0
                val a2 = a + 10.0
                val r1 = r * 0.46f
                val r2 = r * 0.62f
                rayPath.moveTo(
                    c.x + (cos(Math.toRadians(a1)) * r1).toFloat(),
                    c.y + (sin(Math.toRadians(a1)) * r1).toFloat(),
                )
                rayPath.lineTo(
                    c.x + (cos(Math.toRadians(a)) * r2).toFloat(),
                    c.y + (sin(Math.toRadians(a)) * r2).toFloat(),
                )
                rayPath.lineTo(
                    c.x + (cos(Math.toRadians(a2)) * r1).toFloat(),
                    c.y + (sin(Math.toRadians(a2)) * r1).toFloat(),
                )
                rayPath.close()
            }
            drawPath(rayPath, ray)

            // 本体
            drawCircle(core, radius = r * 0.40f, center = c)

            // 笑脸
            val face = Color(0xFF5B3A00)
            val eye = r * 0.06f
            val eyeY = c.y - r * 0.05f
            val eyeDx = r * 0.14f
            drawCircle(face, radius = eye, center = Offset(c.x - eyeDx, eyeY))
            drawCircle(face, radius = eye, center = Offset(c.x + eyeDx, eyeY))
            val smileRect = Rect(
                Offset(c.x - r * 0.18f, c.y + r * 0.02f),
                Size(r * 0.36f, r * 0.20f),
            )
            drawArc(
                color = face,
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = smileRect.topLeft,
                size = smileRect.size,
                style = Stroke(width = r * 0.045f, cap = StrokeCap.Round),
            )
        }
    }
}
