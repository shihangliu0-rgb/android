package com.example.myapplication.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ACHIEVEMENTS
import com.example.myapplication.data.Achievement
import com.example.myapplication.data.Store
import com.example.myapplication.ui.components.BackHeader
import com.example.myapplication.ui.components.SkyBackground
import com.example.myapplication.ui.components.TipPill
import com.example.myapplication.ui.theme.CardCream
import com.example.myapplication.ui.theme.Coral
import com.example.myapplication.ui.theme.Ink
import com.example.myapplication.ui.theme.Lemon
import kotlinx.coroutines.delay

@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val unlocked = remember { Store.unlockedAchievements(context).toMutableSet() }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2400)
            toast = null
        }
    }

    Box(Modifier.fillMaxSize()) {
        SkyBackground(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            BackHeader("🏆 暑假成就", onBack)

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("解锁 ${unlocked.size} / ${ACHIEVEMENTS.size}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("点击打卡类成就可手动解锁", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
            }

            Spacer(Modifier.height(10.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(ACHIEVEMENTS, key = { it.id }) { a ->
                    val isUnlocked = unlocked.contains(a.id)
                    AchievementRow(
                        achievement = a,
                        unlocked = isUnlocked,
                        onClick = {
                            if (!isUnlocked && a.claimable) {
                                if (Store.unlock(context, a.id)) {
                                    unlocked.add(a.id)
                                    toast = "🎉 解锁成就：${a.title}"
                                }
                            }
                        },
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        AnimatedVisibility(
            visible = toast != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
        ) {
            TipPill(toast ?: "")
        }
    }
}

@Composable
private fun AchievementRow(
    achievement: Achievement,
    unlocked: Boolean,
    onClick: () -> Unit,
) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(unlocked) {
        if (unlocked) {
            scale.snapTo(1.25f)
            scale.animateTo(1f, spring(dampingRatio = 0.35f))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (unlocked) CardCream else Color.White.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .size(46.dp)
                .background(if (unlocked) Lemon.copy(alpha = 0.35f) else Color(0x1AFFFFFF), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(achievement.emoji, fontSize = 24.sp, modifier = if (unlocked) Modifier else Modifier.alpha(0.45f))
        }

        Column(
            Modifier
                .weight(1f)
                .padding(start = 14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(achievement.title, color = if (unlocked) Ink else Ink.copy(alpha = 0.6f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                if (unlocked) {
                    Text("  ✔", color = Coral, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(achievement.desc, color = Ink.copy(alpha = 0.55f), fontSize = 13.sp)
        }

        if (!unlocked) {
            Text(
                if (achievement.claimable) "去解锁" else "🔒",
                color = Coral,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
            )
        }
    }
}
