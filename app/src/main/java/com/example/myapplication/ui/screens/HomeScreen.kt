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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar

// V3 极简暖白配色
private val HomeBackground = Color(0xFFFFFDF7)
private val SunLight = Color(0xFFFFD08A)
private val SunCore = Color(0xFFFF7A45)
private val SunGlow = Color(0xFFFFB74D)
private val Ink = Color(0xFF2B2B2B)
private val Muted = Color(0xFF9B968B)
private val ToastBg = Color(0xFFFFE9D8)

// 每天随机变化的一句小文字
private val DAILY_LINES = listOf(
    "今天适合什么都不做。",
    "你的暑假余额还有很多。",
    "早八系统已永久关闭。",
    "今天的风没有 KPI。",
    "允许自己浪费一点时间。",
)

// 点击太阳时的随机回应
private val SUN_LINES = listOf(
    "你点击了一下太阳。",
    "今天也没有人催你。",
    "恭喜，成功浪费了 0.3 秒。",
)

@Composable
fun HomeScreen(
    onEnterToday: () -> Unit,
) {
    // 一天只变一次
    val dayLine = remember {
        DAILY_LINES[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % DAILY_LINES.size]
    }

    var sunLine by remember { mutableStateOf<String?>(null) }
    var lastSunIndex by remember { mutableIntStateOf(-1) }

    // 太阳轻微漂浮
    val floatTransition = rememberInfiniteTransition(label = "homeSunFloat")
    val floatPhase by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "homeSunFloatPhase",
    )
    val sunBob = 6.dp * (floatPhase - 0.5f)

    // 点击反馈：1.0 -> 0.96 -> 1.0
    val sunInteraction = remember { MutableInteractionSource() }
    val sunPressed by sunInteraction.collectIsPressedAsState()
    val sunScale by animateFloatAsState(
        targetValue = if (sunPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "sunScale",
    )

    val buttonInteraction = remember { MutableInteractionSource() }
    val buttonPressed by buttonInteraction.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "buttonScale",
    )

    // 文案区域：太阳回应淡入、每日句子淡出
    val showToast = sunLine != null
    val dayAlpha by animateFloatAsState(
        targetValue = if (showToast) 0f else 1f,
        animationSpec = tween(durationMillis = 220),
        label = "dayAlpha",
    )
    val toastAlpha by animateFloatAsState(
        targetValue = if (showToast) 1f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "toastAlpha",
    )
    val toastSlide by animateDpAsState(
        targetValue = if (showToast) 0.dp else 6.dp,
        animationSpec = tween(durationMillis = 220),
        label = "toastSlide",
    )

    LaunchedEffect(sunLine) {
        if (sunLine != null) {
            delay(2200)
            sunLine = null
        }
    }

    fun pokeSun() {
        var index = SUN_LINES.indices.random()
        while (SUN_LINES.size > 1 && index == lastSunIndex) {
            index = SUN_LINES.indices.random()
        }
        lastSunIndex = index
        sunLine = SUN_LINES[index]
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = HomeBackground)
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
                    .size(size = 160.dp)
                    .graphicsLayer {
                        scaleX = sunScale
                        scaleY = sunScale
                    }
                    .clickable(
                        interactionSource = sunInteraction,
                        indication = null,
                        onClick = { pokeSun() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 150.dp)
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
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(SunLight, SunCore),
                            ),
                            shape = CircleShape,
                        ),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Summer",
                color = Ink,
                fontSize = 46.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "2026",
                color = Muted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 每日句子 / 太阳回应
            Box(
                modifier = Modifier.height(height = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = dayLine,
                    color = Muted,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer { alpha = dayAlpha },
                )
                Box(
                    modifier = Modifier
                        .graphicsLayer { alpha = toastAlpha }
                        .offset(y = toastSlide),
                ) {
                    Text(
                        text = sunLine ?: "",
                        color = SunCore,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(color = ToastBg, shape = RoundedCornerShape(50))
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 底部圆形按钮：进入今天的暑假
            Box(
                modifier = Modifier
                    .size(size = 64.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .clip(shape = CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(SunLight, SunCore),
                        ),
                    )
                    .clickable(
                        interactionSource = buttonInteraction,
                        indication = null,
                        onClick = onEnterToday,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "↓",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
