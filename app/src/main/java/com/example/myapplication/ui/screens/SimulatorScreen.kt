package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.SIM_EVENTS
import com.example.myapplication.data.SimEvent
import com.example.myapplication.data.resolveNext
import com.example.myapplication.ui.components.BackHeader
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink

@Composable
fun SimulatorScreen(onBack: () -> Unit) {
    var eventId by remember { mutableStateOf<String?>(null) }

    fun start() {
        eventId = "start"
    }

    fun choose(next: String) {
        eventId = resolveNext(next)
    }

    Box(Modifier.fillMaxSize()) {
        SkyBackground(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            BackHeader("🏝️ 今日模拟器", onBack)

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (eventId == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏝️", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("暑假的每一天，都是一场随机事件。", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("醒来之后怎么选，全看你自己。", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        Spacer(Modifier.height(28.dp))
                        PillButton("▶ 开始今天") { start() }
                    }
                } else {
                    val event = SIM_EVENTS[eventId] ?: SIM_EVENTS["start"]!!
                    EventPanel(event, onChoose = { choose(it) }, onRestart = { start() })
                }
            }
        }
    }
}

@Composable
private fun EventPanel(
    event: SimEvent,
    onChoose: (String) -> Unit,
    onRestart: () -> Unit,
) {
    AnimatedContent(
        targetState = event,
        transitionSpec = { fadeIn(tween(280)) togetherWith fadeOut(tween(180)) },
        label = "simEvent",
    ) { e ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (e.time.isNotEmpty()) {
                Text(e.time, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.height(4.dp))
            }
            Text(e.emoji, fontSize = 46.sp)
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .background(CardCream, RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Text(
                    e.text,
                    color = Ink,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(22.dp))

            if (e.ending != null) {
                AnimatedVisibility(visible = true, enter = fadeIn(tween(300))) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("— ${e.ending} —", color = Color.White.copy(alpha = 0.95f), fontSize = 14.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        PillButton("↻ 再过一天") { onRestart() }
                    }
                }
            } else {
                e.choices.forEach { c ->
                    ChoiceRow(c.label) { onChoose(c.next) }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ChoiceRow(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
    )
}

@Composable
private fun PillButton(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(Coral, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 34.dp, vertical = 14.dp),
    )
}
