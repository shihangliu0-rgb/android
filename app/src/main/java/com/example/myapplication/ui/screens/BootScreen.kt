package com.example.myapplication.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// V3 极简暖白配色
private val BootBackground = Color(0xFFFFFDF7)
private val SunBodyLight = Color(0xFFFFD08A)
private val SunBody = Color(0xFFFF7A45)
private val SunGlow = Color(0xFFFFB74D)
private val TextPrimary = Color(0xFF2B2B2B)
private val TextMuted = Color(0xFF9B968B)

@Composable
fun BootScreen(
    alreadyStarted: Boolean,
    onEnter: () -> Unit,
) {
    // 分阶段时间轴：太阳 → 光晕 → Summer → 2026 → 按钮
    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        phase = 1
        delay(200)
        phase = 2
        delay(350)
        phase = 3
        delay(200)
        phase = 4
        delay(550)
        phase = 5
    }

    val sunScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.8f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "sunScale",
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "glowAlpha",
    )
    val summerAlpha by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "summerAlpha",
    )
    val summerSlide by animateDpAsState(
        targetValue = if (phase >= 3) 0.dp else 14.dp,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "summerSlide",
    )
    val yearAlpha by animateFloatAsState(
        targetValue = if (phase >= 4) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "yearAlpha",
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (phase >= 5) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "buttonAlpha",
    )

    // 太阳轻微漂浮
    val floatTransition = rememberInfiniteTransition(label = "bootSunFloat")
    val floatPhase by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bootSunFloatPhase",
    )
    val sunBob = 8.dp * (floatPhase - 0.5f)

    val pressInteraction = remember { MutableInteractionSource() }
    val pressed by pressInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "buttonScale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = BootBackground)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // 太阳（光晕 + 圆体）
            Box(
                modifier = Modifier
                    .offset(y = sunBob)
                    .size(size = 160.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 150.dp)
                        .graphicsLayer { alpha = glowAlpha }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(SunGlow, SunGlow.copy(alpha = 0f)),
                            ),
                            shape = CircleShape,
                        ),
                )
                Box(
                    modifier = Modifier
                        .size(size = 104.dp)
                        .graphicsLayer {
                            scaleX = sunScale
                            scaleY = sunScale
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(SunBodyLight, SunBody),
                            ),
                            shape = CircleShape,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = summerAlpha }
                    .offset(y = summerSlide),
            ) {
                Text(
                    text = "Summer",
                    color = TextPrimary,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier.graphicsLayer { alpha = yearAlpha },
            ) {
                Text(
                    text = "2026",
                    color = TextMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 6.sp,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 底部圆形按钮
            Box(
                modifier = Modifier
                    .size(size = 64.dp)
                    .graphicsLayer {
                        alpha = buttonAlpha
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .clip(shape = CircleShape)
                    .background(color = SunBody)
                    .clickable(
                        interactionSource = pressInteraction,
                        indication = null,
                        enabled = phase >= 5,
                        onClick = onEnter,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "→",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
