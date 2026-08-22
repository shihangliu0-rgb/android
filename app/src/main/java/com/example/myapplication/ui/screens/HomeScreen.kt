package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.SummerCard
import com.example.myapplication.data.computeRemaining
import com.example.myapplication.data.randomCard
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.components.Sun
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.Lemon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HomeScreen(onReplay: () -> Unit) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var sunClicks by remember { mutableIntStateOf(0) }
    var sunMessage by remember { mutableStateOf<String?>(null) }
    var showCountdownDialog by remember { mutableStateOf(false) }
    var panicMode by remember { mutableStateOf(false) }
    var showCardDraw by remember { mutableStateOf(false) }
    var moduleMsg by remember { mutableStateOf<String?>(null) }
    val sunScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    val heat = when {
        sunClicks < 10 -> 0f
        sunClicks >= 20 -> 1f
        else -> (sunClicks - 10) / 10f
    }
    val temp = (30 + heat * 18).roundToInt()

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    LaunchedEffect(sunMessage) {
        if (sunMessage != null) {
            delay(2400)
            sunMessage = null
        }
    }

    fun pokeSun() {
        val next = sunClicks + 1
        sunClicks = next
        scope.launch {
            sunScale.snapTo(1.22f)
            sunScale.animateTo(1f, spring(dampingRatio = 0.35f))
        }
        when (next) {
            10 -> sunMessage = "太阳：别点了，我真的很热。"
            20 -> sunMessage = "系统提示：你成功制造了第二个太阳。"
            else -> {}
        }
    }

    val remaining = remember(now) { computeRemaining(now) }

    Box(Modifier.fillMaxSize()) {
        SkyBackground(heat = heat, modifier = Modifier.fillMaxSize())

        // 第二个太阳（彩蛋）
        if (heat >= 1f) {
            Sun(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 18.dp, top = 56.dp),
                diameter = 60.dp,
                heat = 1f,
                spin = false,
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(6.dp))

            // 太阳 + 温度
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .graphicsLayer {
                            scaleX = sunScale.value
                            scaleY = sunScale.value
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { pokeSun() },
                ) {
                    Sun(diameter = 96.dp, heat = heat)
                }
                AnimatedVisibility(visible = heat > 0f, enter = fadeIn() + slideInVertically { it / 2 }) {
                    Text(
                        "🔥 温度：${temp}℃",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("SUMMER", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp)
            Text("2026 ☀️", color = Color.White.copy(alpha = 0.92f), fontSize = 16.sp, letterSpacing = 8.sp)

            Spacer(Modifier.height(16.dp))

            // 太阳的碎碎念
            AnimatedVisibility(visible = sunMessage != null, enter = fadeIn() + slideInVertically { it / 3 }) {
                Text(
                    sunMessage ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0x55000000), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }

            Spacer(Modifier.height(18.dp))

            CountdownCard(remaining = remaining, onClick = { showCountdownDialog = true })

            Spacer(Modifier.height(14.dp))

            DrawCardEntry(onClick = { showCardDraw = true })

            Spacer(Modifier.height(14.dp))

            ModulesRow(onTap = { moduleMsg = it })

            Spacer(Modifier.height(24.dp))
            Text(
                "☀️ 请合理浪费这个夏天",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "↺ 再玩一次：放飞考试",
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable(onClick = onReplay)
                    .padding(8.dp),
            )
            Spacer(Modifier.height(12.dp))
        }
    }

    // ── 倒计时彩蛋弹窗 ──
    if (showCountdownDialog) {
        AlertDialog(
            onDismissRequest = { showCountdownDialog = false },
            containerColor = CardCream,
            title = { Text("你确定要知道吗？", color = Ink, fontWeight = FontWeight.Bold) },
            text = { Text("有些数字，知道了只会焦虑。", color = Ink.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(onClick = {
                    showCountdownDialog = false
                    panicMode = true
                }) { Text("……看看吧", color = Coral) }
            },
            dismissButton = {
                TextButton(onClick = { showCountdownDialog = false }) { Text("我不想知道", color = Ink.copy(alpha = 0.7f)) }
            },
        )
    }

    // ── 数字疯狂减少彩蛋 ──
    if (panicMode) {
        PanicOverlay(onDone = { panicMode = false })
    }

    // ── 抽卡 ──
    if (showCardDraw) {
        CardDrawOverlay(onClose = { showCardDraw = false })
    }

    // ── 敬请期待 ──
    if (moduleMsg != null) {
        AlertDialog(
            onDismissRequest = { moduleMsg = null },
            containerColor = CardCream,
            title = { Text(moduleMsg ?: "", color = Ink, fontWeight = FontWeight.Bold) },
            text = { Text("这个功能还躺在暑假的沙滩上 🏖️\nVersion 2 敬请期待！", color = Ink.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(onClick = { moduleMsg = null }) { Text("好耶 ☀️", color = Coral) }
            },
        )
    }
}

// ───────────────────────── 卡片组件 ─────────────────────────

@Composable
private fun GlassCard(
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    val base = Modifier
        .fillMaxWidth()
        .shadow(10.dp, shape, spotColor = Color(0x33000000), clip = false)
        .background(Color.White.copy(alpha = 0.94f), shape)
    Column(
        modifier = if (onClick != null) base.clickable(onClick = onClick).padding(20.dp) else base.padding(20.dp),
        content = content,
    )
}

@Composable
private fun CountdownCard(
    remaining: com.example.myapplication.data.SummerRemaining,
    onClick: () -> Unit,
) {
    GlassCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🕶️", fontSize = 28.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text("暑假剩余", color = Ink.copy(alpha = 0.6f), fontSize = 13.sp)
                if (remaining.totalMillis <= 0L) {
                    Text("暑假已被你偷偷延长 🎉", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                } else {
                    Row(verticalAlignment = Alignment.Bottom) {
                        NumberUnit(remaining.days.toString(), "天")
                        NumberUnit(remaining.hours.toString(), "小时")
                        NumberUnit(remaining.minutes.toString(), "分钟")
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0x14000000), RoundedCornerShape(4.dp)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(remaining.fractionElapsed.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(Brush.horizontalGradient(listOf(Coral, Lemon)), RoundedCornerShape(4.dp)),
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("暑假进度 ${(remaining.fractionElapsed * 100).roundToInt()}%", color = Ink.copy(alpha = 0.55f), fontSize = 12.sp)
            Text("⚠️ 请合理浪费。", color = Coral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NumberUnit(value: String, unit: String) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(value, color = Ink, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text(" $unit ", color = Ink.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.padding(bottom = 4.dp))
    }
}

@Composable
private fun DrawCardEntry(onClick: () -> Unit) {
    GlassCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎴", fontSize = 38.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("今日暑假计划", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("点击抽取今日任务", color = Ink.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Text("抽一张 →", color = Coral, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ModulesRow(onTap: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ModuleChip("🏆", "成就", Modifier.weight(1f)) { onTap("🏆 暑假成就") }
        ModuleChip("🏝️", "模拟器", Modifier.weight(1f)) { onTap("🏝️ 今日模拟器") }
        ModuleChip("🍾", "漂流瓶", Modifier.weight(1f)) { onTap("🍾 愿望漂流瓶") }
    }
}

@Composable
private fun ModuleChip(emoji: String, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .shadow(6.dp, shape, spotColor = Color(0x33000000), clip = false)
            .background(Color.White.copy(alpha = 0.9f), shape)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 26.sp)
        Spacer(Modifier.height(4.dp))
        Text(title, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ───────────────────────── 数字暴减彩蛋 ─────────────────────────

@Composable
private fun PanicOverlay(onDone: () -> Unit) {
    var n by remember { mutableIntStateOf(60) }
    var showJoke by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (n > 0) {
            delay(55)
            n--
        }
        showJoke = true
        delay(2200)
        onDone()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xE6000000)),
        contentAlignment = Alignment.Center,
    ) {
        if (showJoke) {
            Text(
                "开玩笑的。\n别紧张 😄",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
            )
        } else {
            Text(
                n.toString(),
                color = Color(0xFFFF6A3D),
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

// ───────────────────────── 抽卡翻牌 ─────────────────────────

@Composable
private fun CardDrawOverlay(onClose: () -> Unit) {
    var card by remember { mutableStateOf(randomCard()) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun draw() {
        card = randomCard()
        scope.launch {
            rotation.snapTo(0f)
            rotation.animateTo(180f, tween(700, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(Unit) { draw() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClose() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { /* 吞掉点击，避免误关 */ },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(width = 264.dp, height = 380.dp)
                    .graphicsLayer {
                        rotationY = rotation.value
                        cameraDistance = 12f * density
                    },
            ) {
                if (rotation.value <= 90f) {
                    CardBack()
                } else {
                    Box(Modifier.graphicsLayer { rotationY = 180f }) {
                        CardFront(card)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Row {
                OutlinedButton(
                    onClick = { draw() },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White),
                ) {
                    Text("再抽一次", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = onClose,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = Color.White),
                ) {
                    Text("收下", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CardBack() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Coral, Color(0xFFFFA07A), Color(0xFFFFD29D))), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎴", fontSize = 72.sp)
            Spacer(Modifier.height(12.dp))
            Text("今日暑假计划", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            Text("SUMMER START", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, letterSpacing = 3.sp)
        }
    }
}

@Composable
private fun CardFront(card: SummerCard) {
    Column(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(CardCream),
    ) {
        // 稀有度头
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Brush.horizontalGradient(listOf(card.rarity.color, card.rarity.color.copy(alpha = 0.75f)))),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(card.rarity.label, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.width(8.dp))
                Text("·", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text("今日任务", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(card.emoji, fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(card.title, color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(card.desc, color = Ink.copy(alpha = 0.7f), fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x14000000)),
            )
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("奖励", color = Ink.copy(alpha = 0.55f), fontSize = 13.sp)
                Text(card.reward, color = Coral, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("经验", color = Ink.copy(alpha = 0.55f), fontSize = 13.sp)
                Text("无", color = Ink.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(card.difficulty, color = Ink.copy(alpha = 0.55f), fontSize = 13.sp)
                Text(if (card.rarity.weight <= 8) "✨ 稀有卡" else "💰 金币：无", color = Ink.copy(alpha = 0.6f), fontSize = 13.sp)
            }
        }
    }
}
