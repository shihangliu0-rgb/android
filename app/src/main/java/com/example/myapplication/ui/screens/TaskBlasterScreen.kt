package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.TASKS
import com.example.myapplication.data.TASK_ANCHORS
import com.example.myapplication.data.TaskItem
import com.example.myapplication.ui.components.ParticleCanvas
import com.example.myapplication.ui.components.ParticleSystem
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Lemon
import com.example.myapplication.ui.theme.SunCore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

private data class FloatingTask(
    val task: TaskItem,
    val fx: Float,
    val fy: Float,
)

private data class FlyingItem(
    val id: Long,
    val task: TaskItem,
    val x: Float,
    val y: Float,
    val dirX: Float,
    val dirY: Float,
)

private data class Pop(val id: Long, val x: Float, val y: Float)

@Composable
fun TaskBlasterScreen(onFinish: () -> Unit) {
    val floating = remember {
        TASK_ANCHORS.mapIndexed { i, (x, y) ->
            FloatingTask(
                TASKS[i],
                x + Random.nextFloat() * 0.05f - 0.025f,
                y + Random.nextFloat() * 0.05f - 0.025f,
            )
        }
    }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var aliveCount by remember { mutableIntStateOf(TASKS.size) }
    val flying = remember { mutableStateListOf<FlyingItem>() }
    val pops = remember { mutableStateListOf<Pop>() }
    val particles = remember { ParticleSystem() }
    var idSeq by remember { mutableStateOf(0L) }
    val finished = aliveCount <= 0

    // 粒子帧循环
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameMillis { now ->
                val dt = if (last == 0L) 0f else (now - last) / 1000f
                last = now
                if (dt > 0f && dt < 0.1f) particles.step(dt)
            }
        }
    }

    fun blowUp(task: TaskItem, center: Offset) {
        idSeq++
        val dir = Random.nextFloat() * Math.PI.toFloat() * 2f
        val dist = (700f + Random.nextFloat() * 500f)
        flying.add(
            FlyingItem(
                id = idSeq,
                task = task,
                x = center.x - 20f,
                y = center.y - 20f,
                dirX = kotlin.math.cos(dir) * dist,
                dirY = kotlin.math.sin(dir) * dist,
            )
        )
        pops.add(Pop(id = idSeq, x = center.x, y = center.y))
        particles.burst(
            x = center.x,
            y = center.y,
            colors = listOf(task.color, Color.White, Lemon, SunCore),
            count = 16,
        )
        aliveCount--
    }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it },
    ) {
        SkyBackground(Modifier.fillMaxSize())

        // 顶部标题 + 进度
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "🎈 把它们统统放飞！",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "点击这些家伙，把它们从暑假里赶出去",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            ProgressBar(progress = 1f - aliveCount.toFloat() / TASKS.size.toFloat())
            Spacer(Modifier.height(6.dp))
            Text(
                "已消灭 ${TASKS.size - aliveCount} / ${TASKS.size}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // 悬浮的任务
        if (containerSize != IntSize.Zero) {
            for (ft in floating) {
                FloatingTaskView(
                    floatingTask = ft,
                    container = containerSize,
                    onBoom = { center -> blowUp(ft.task, center) },
                )
            }
        }

        // 飞出去的
        for (item in flying) {
            key(item.id) {
                FlyingItemView(item) { flying.remove(item) }
            }
        }

        // 💥
        for (pop in pops) {
            key(pop.id) {
                PopView(pop) { pops.remove(pop) }
            }
        }

        // 粒子
        ParticleCanvas(particles, Modifier.fillMaxSize())

        // 完成遮罩
        AnimatedVisibility(
            visible = finished,
            enter = fadeIn() + scaleIn(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Column(
                Modifier
                    .background(Color(0xE61A2B4A), RoundedCornerShape(28.dp))
                    .padding(horizontal = 32.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("✅", fontSize = 44.sp)
                Text("所有任务已清空", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text("自由度 +100%", color = Lemon, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(22.dp))
                Button(
                    onClick = onFinish,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = Color.White),
                    modifier = Modifier.size(width = 200.dp, height = 52.dp),
                ) {
                    Text("进入暑假", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(Color.White.copy(alpha = 0.35f), RoundedCornerShape(5.dp)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(Lemon, RoundedCornerShape(5.dp)),
        )
    }
}

@Composable
private fun FloatingTaskView(
    floatingTask: FloatingTask,
    container: IntSize,
    onBoom: (Offset) -> Unit,
) {
    val idle = rememberInfiniteTransition(label = "idle${floatingTask.task.id}")
    val bob by idle.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob${floatingTask.task.id}",
    )

    var pos by remember { mutableStateOf(Offset.Zero) }
    var sizePx by remember { mutableStateOf(IntSize.Zero) }

    Box(
        Modifier
            .offset {
                IntOffset(
                    (floatingTask.fx * container.width).roundToInt(),
                    (floatingTask.fy * container.height).roundToInt() + (bob * 6f).roundToInt(),
                )
            }
            .graphicsLayer { rotationZ = bob * 2f }
            .onGloballyPositioned { coords ->
                pos = coords.positionInRoot()
                sizePx = coords.size
            }
            .background(floatingTask.task.color, RoundedCornerShape(50))
            .clickable {
                val center = Offset(
                    pos.x + sizePx.width / 2f,
                    pos.y + sizePx.height / 2f,
                )
                onBoom(center)
            }
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            "${floatingTask.task.emoji} ${floatingTask.task.label}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun FlyingItemView(item: FlyingItem, onDone: () -> Unit) {
    val p = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(item.id) {
        coroutineScope {
            launch { rotation.animateTo(if (item.dirX > 0f) 720f else -720f, tween(640, easing = FastOutSlowInEasing)) }
            launch { p.animateTo(1f, tween(640, easing = FastOutSlowInEasing)) }
        }
        onDone()
    }
    val x = item.x + p.value * item.dirX
    val y = item.y + p.value * item.dirY - (p.value * p.value) * 260f
    Text(
        "${item.task.emoji} ${item.task.label}",
        color = item.task.color,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .graphicsLayer {
                rotationZ = rotation.value
                alpha = 1f - p.value
            },
    )
}

@Composable
private fun PopView(pop: Pop, onDone: () -> Unit) {
    val s = remember { Animatable(0.3f) }
    val a = remember { Animatable(1f) }
    LaunchedEffect(pop.id) {
        coroutineScope {
            launch { s.animateTo(1.9f, spring(dampingRatio = 0.4f)) }
            launch { a.animateTo(0f, tween(380, easing = LinearEasing)) }
        }
        onDone()
    }
    Text(
        "💥",
        fontSize = (46f * s.value).sp,
        modifier = Modifier
            .offset { IntOffset((pop.x - 26f).roundToInt(), (pop.y - 26f).roundToInt()) }
            .graphicsLayer { alpha = a.value },
    )
}
