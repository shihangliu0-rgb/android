package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.components.Sun
import com.example.myapplication.ui.theme.Coral
import kotlinx.coroutines.delay

private val BOOT_LINES = listOf(
    "检测到学期已结束。",
    "正在解除早八限制……",
    "正在删除 DDL 压力……",
    "正在启动暑假模式……",
)

@Composable
fun BootScreen(
    alreadyStarted: Boolean,
    onEnter: () -> Unit,
) {
    var loaded by remember { mutableStateOf(false) }
    var linesShown by remember { mutableIntStateOf(0) }
    var showBanner by remember { mutableStateOf(false) }
    var showPrompt by remember { mutableStateOf(false) }

    fun skipAll() {
        loaded = true
        linesShown = BOOT_LINES.size
        showBanner = true
        showPrompt = true
    }

    LaunchedEffect(Unit) {
        delay(300)
        loaded = true
        BOOT_LINES.forEach { _ ->
            delay(620)
            linesShown++
        }
        delay(900)
        showBanner = true
        delay(1200)
        showPrompt = true
    }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { skipAll() }
            },
    ) {
        SkyBackground(modifier = Modifier.fillMaxSize())

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(visible = loaded, enter = fadeIn() + slideInVertically { it / 2 }) {
                Sun(diameter = 120.dp)
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(visible = loaded, enter = fadeIn()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SUMMER",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                    )
                    Text(
                        text = "2026",
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 12.sp,
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // 控制台区域
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    showBanner -> AnimatedVisibility(
                        visible = true,
                        modifier = Modifier.fillMaxWidth(),
                        enter = scaleIn() + fadeIn(),
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("SUMMER MODE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                            Text("ENABLED", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
                            Text("☀️ 2026", color = Color.White, fontSize = 14.sp, letterSpacing = 3.sp)
                        }
                    }

                    linesShown > 0 -> Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        for (index in BOOT_LINES.indices) {
                            AnimatedVisibility(
                                visible = linesShown > index,
                                enter = fadeIn() + slideInVertically { it / 2 },
                            ) {
                                Text(
                                    text = "▸ ${BOOT_LINES[index]}",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 16.sp,
                                    lineHeight = 26.sp,
                                    modifier = Modifier.padding(vertical = 2.dp),
                                )
                            }
                        }
                    }

                    else -> LoadingDots()
                }
            }

            Spacer(Modifier.height(24.dp))

            AnimatedVisibility(visible = showPrompt, enter = fadeIn()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (alreadyStarted) {
                        Text("欢迎回来，暑假早已开启。", color = Color.White, fontSize = 15.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("继续浪费这个夏天吧 ☀️", color = Color.White.copy(alpha = 0.9f), fontSize = 15.sp)
                    } else {
                        Text("暑假已经到账。", color = Color.White, fontSize = 15.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("但在正式开启之前……", color = Color.White, fontSize = 15.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "你需要完成最后一个任务。",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = onEnter,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = Color.White),
                        modifier = Modifier.size(width = 200.dp, height = 54.dp),
                    ) {
                        Text(
                            text = if (alreadyStarted) "进入暑假" else "开启暑假",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingDots() {
    var dotCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(350)
            dotCount = (dotCount + 1) % 4
        }
    }
    Text(
        text = "Loading freedom" + ".".repeat(dotCount),
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 16.sp,
        letterSpacing = 1.sp,
    )
}
