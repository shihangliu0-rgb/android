package com.example.myapplication.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.CARDS
import com.example.myapplication.data.SummerCard
import com.example.myapplication.data.Store
import com.example.myapplication.data.randomCard
import com.example.myapplication.ui.components.BackHeader
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink
import kotlinx.coroutines.launch

@Composable
fun CardDrawScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    // 今日抽卡：结果本地保存，每天固定；当天重开显示同一张。
    val savedIndex = remember { Store.todayCardIndex(context) }
    var card by remember { mutableStateOf(if (savedIndex != null) CARDS[savedIndex] else randomCard()) }
    var drawnToday by remember { mutableStateOf(savedIndex != null) }

    val rotation = remember { Animatable(if (savedIndex != null) 180f else 0f) }
    val scope = rememberCoroutineScope()

    fun draw() {
        if (drawnToday) return
        card = randomCard()
        val index = CARDS.indexOf(card)
        if (index >= 0) Store.setTodayCard(context, index)
        if (card.rarity == com.example.myapplication.data.CardRarity.UR ||
            card.rarity == com.example.myapplication.data.CardRarity.HIDDEN
        ) {
            Store.unlock(context, "card_ur")
        }
        drawnToday = true
        scope.launch {
            rotation.snapTo(0f)
            rotation.animateTo(180f, tween(700, easing = FastOutSlowInEasing))
        }
    }

    Box(Modifier.fillMaxSize()) {
        SkyBackground(modifier = Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            BackHeader("🎴 今日抽卡", onBack)

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp),
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

                Spacer(Modifier.height(20.dp))

                if (!drawnToday) {
                    DrawButton("🎴 开始今天") { draw() }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatusPill("今日已抽")
                        StatusPill("明天再来 ☀️")
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    if (drawnToday) "今天的卡片已固定，明天会刷新。\n（想重置？见主页「关于」→ 开发者选项）" else "点击抽卡，看看今天的运气。",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 40.dp),
                )
            }
        }
    }
}

@Composable
private fun DrawButton(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = Color.White,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(Coral, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 34.dp, vertical = 14.dp),
    )
}

@Composable
private fun StatusPill(text: String) {
    Text(
        text,
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(Color(0x33FFFFFF), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
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
