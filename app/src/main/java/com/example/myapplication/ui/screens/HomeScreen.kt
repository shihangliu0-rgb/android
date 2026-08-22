package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.Store
import com.example.myapplication.data.computeRemaining
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.components.Sun
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.Lemon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.util.Calendar

private val TIPS = listOf(
    "请合理浪费时间。",
    "今天也是适合什么都不干的一天。",
    "你的床正在召唤你。",
    "学习可以，但不是今天。",
    "检测到用户仍然拥有自由时间。",
)

@Composable
fun HomeScreen(
    onOpenCard: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenSimulator: () -> Unit,
    onOpenWishes: () -> Unit,
    onReplay: () -> Unit,
) {
    val context = LocalContext.current

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var sunClicks by remember { mutableIntStateOf(0) }
    var titleClicks by remember { mutableIntStateOf(0) }
    var sunMessage by remember { mutableStateOf<String?>(null) }
    var titleMessage by remember { mutableStateOf<String?>(null) }
    var rapidMsg by remember { mutableStateOf<String?>(null) }
    var showCountdownDialog by remember { mutableStateOf(false) }
    var panicMode by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    val sunScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val tip = remember { TIPS.random() }

    val heat = when {
        sunClicks < 5 -> 0f
        sunClicks >= 15 -> 1f
        else -> (sunClicks - 5) / 10f
    }
    val temp = (30 + heat * 18).roundToInt()

    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val isLateNight = hour in 0..4

    val remaining = remember(now) { computeRemaining(now) }
    val isLastDays = remaining.days in 1..5 && !Store.eggSeen(context, "last_days")

    LaunchedEffect(Unit) {
        // 首次进入主页即算「正式进入暑假」
        Store.unlock(context, "first_day")
        // 彩蛋 4：凌晨打开 → 顺带解锁「时间旅行者」
        if (isLateNight) Store.unlock(context, "night_owl")
        while (true) {
            delay(1000)
            now = System.currentTimeMillis()
        }
    }

    LaunchedEffect(sunMessage) {
        if (sunMessage != null) { delay(2400); sunMessage = null }
    }
    LaunchedEffect(titleMessage) {
        if (titleMessage != null) { delay(2400); titleMessage = null }
    }
    LaunchedEffect(rapidMsg) {
        if (rapidMsg != null) { delay(2200); rapidMsg = null }
    }
    LaunchedEffect(isLastDays) {
        if (isLastDays) Store.markEgg(context, "last_days")
    }

    fun pokeSun() {
        val next = sunClicks + 1
        sunClicks = next
        scope.launch {
            sunScale.snapTo(1.22f)
            sunScale.animateTo(1f, spring(dampingRatio = 0.35f))
        }
        when (next) {
            1 -> sunMessage = "太阳：今天很热。"
            5 -> sunMessage = "太阳：真的很热。"
            10 -> sunMessage = "太阳：别点了。"
            15 -> {
                Store.unlock(context, "second_sun")
                sunMessage = "系统提示：你成功制造了第二个太阳。"
            }
        }
    }

    fun pokeTitle() {
        titleClicks++
        if (titleClicks == 5) titleMessage = "你真的很喜欢这个标题。"
    }

    Box(
        Modifier
            .fillMaxSize()
            // 彩蛋 3：观察整页点击（不拦截任何交互）
            .pointerInput(Unit) {
                var last = 0L
                var count = 0
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.type == PointerEventType.Press) {
                            val t = System.currentTimeMillis()
                            count = if (t - last < 2500L) count + 1 else 1
                            last = t
                            if (count >= 8) {
                                count = 0
                                rapidMsg = "冷静一点 😂"
                            }
                        }
                    }
                }
            },
    ) {
        SkyBackground(heat = heat, modifier = Modifier.fillMaxSize())

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

            Spacer(Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { pokeTitle() },
            ) {
                Text("SUMMER", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp)
                Text("2026 ☀️", color = Color.White.copy(alpha = 0.92f), fontSize = 16.sp, letterSpacing = 8.sp)
            }

            Spacer(Modifier.height(12.dp))

            StatusBadge("SUMMER MODE · ENABLED")

            Spacer(Modifier.height(12.dp))

            TipLine(tip)

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(visible = sunMessage != null, enter = fadeIn() + slideInVertically { it / 3 }) {
                Text(
                    sunMessage ?: "",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(Color(0x55000000), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }

            Spacer(Modifier.height(10.dp))

            CountdownCard(remaining = remaining, onClick = { showCountdownDialog = true })

            Spacer(Modifier.height(14.dp))

            DrawCardEntry(onClick = onOpenCard)

            Spacer(Modifier.height(14.dp))

            ModulesGrid(
                onAchievements = onOpenAchievements,
                onSimulator = onOpenSimulator,
                onWishes = onOpenWishes,
            )

            Spacer(Modifier.height(18.dp))

            Text(
                "关于 · 设置",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { showAbout = true }
                    .padding(8.dp),
            )

            Spacer(Modifier.height(6.dp))
            Text(
                "↺ 再玩一次：放飞考试",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { onReplay() }
                    .padding(8.dp),
            )
            Spacer(Modifier.height(16.dp))
        }

        AnimatedVisibility(
            visible = isLateNight,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 14.dp),
        ) {
            TipBanner("🌙 这个时间……你确定这是暑假吗？")
        }

        AnimatedVisibility(
            visible = isLastDays,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp),
        ) {
            TipBanner("📉 系统检测到自由时间正在减少。")
        }

        AnimatedVisibility(
            visible = titleMessage != null,
            enter = fadeIn() + slideInVertically { it / 3 },
            modifier = Modifier.align(Alignment.Center),
        ) {
            TipBanner(titleMessage ?: "")
        }

        AnimatedVisibility(
            visible = rapidMsg != null,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(rapidMsg ?: "", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }

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

    if (panicMode) {
        PanicOverlay(onDone = { panicMode = false })
    }

    if (showAbout) {
        AboutDialog(
            onDismiss = { showAbout = false },
            onResetCard = {
                Store.resetTodayCard(context)
                showAbout = false
            },
        )
    }
}

// ───────────────────────── 主页小组件 ─────────────────────────

@Composable
private fun StatusBadge(text: String) {
    Text(
        text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier
            .background(Color(0x33FFFFFF), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}

@Composable
private fun TipLine(text: String) {
    Text(
        "「$text」",
        color = Color.White.copy(alpha = 0.95f),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TipBanner(text: String) {
    Text(
        text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(Color(0xCC2B3A55), RoundedCornerShape(50))
            .padding(horizontal = 18.dp, vertical = 10.dp),
    )
}

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
                Text("每天只抽一张，今天看运气", color = Ink.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Text("抽一张 →", color = Coral, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ModulesGrid(
    onAchievements: () -> Unit,
    onSimulator: () -> Unit,
    onWishes: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ModuleChip("🏆", "成就", Modifier.weight(1f)) { onAchievements() }
        ModuleChip("🏝️", "模拟器", Modifier.weight(1f)) { onSimulator() }
        ModuleChip("🍾", "漂流瓶", Modifier.weight(1f)) { onWishes() }
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

@Composable
private fun AboutDialog(onDismiss: () -> Unit, onResetCard: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardCream,
        title = { Text("关于", color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("☀️ SUMMER START · 暑假启动器", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("一个用来庆祝「终于放假了」的小世界。", color = Ink.copy(alpha = 0.8f), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                Text("开发者选项", color = Ink.copy(alpha = 0.55f), fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    "重置今日抽卡",
                    color = Coral,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onResetCard() }.padding(vertical = 6.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("好的 ☀️", color = Coral) }
        },
    )
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
                "开玩笑的。\n别紧张，暑假还没结束 😄",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
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
