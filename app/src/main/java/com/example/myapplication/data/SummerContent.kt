package com.example.myapplication.data

import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.RarityHidden
import com.example.myapplication.ui.theme.RarityN
import com.example.myapplication.ui.theme.RarityR
import com.example.myapplication.ui.theme.RaritySR
import com.example.myapplication.ui.theme.RaritySSR
import com.example.myapplication.ui.theme.RarityUR
import java.util.Calendar

// ───────────────────────── 消灭任务（考试/作业） ─────────────────────────

data class TaskItem(
    val id: Int,
    val label: String,
    val emoji: String,
    val color: Color,
)

val TASKS = listOf(
    TaskItem(0, "高数", "📐", Color(0xFF4C8BFF)),
    TaskItem(1, "英语", "📖", Color(0xFF2DD4BF)),
    TaskItem(2, "作业", "📝", Color(0xFFFFB300)),
    TaskItem(3, "考试", "📄", Color(0xFFFF5C7A)),
    TaskItem(4, "早八", "⏰", Color(0xFF8E5BFF)),
    TaskItem(5, "实验报告", "🧪", Color(0xFF2E9BFF)),
    TaskItem(6, "DDL", "🚨", Color(0xFFFF4D4D)),
    TaskItem(7, "绩点", "🎯", Color(0xFF00B8A9)),
)

// 每个任务在屏幕上的大致位置（比例 0..1，避开顶部标题与底部提示）
val TASK_ANCHORS = listOf(
    0.14f to 0.22f, 0.62f to 0.18f, 0.82f to 0.32f, 0.28f to 0.36f,
    0.55f to 0.40f, 0.12f to 0.52f, 0.74f to 0.50f, 0.44f to 0.60f,
)

// ───────────────────────── 今日抽卡 ─────────────────────────

enum class CardRarity(val label: String, val color: Color, val weight: Int) {
    N("N", RarityN, 40),
    R("R", RarityR, 28),
    SR("SR", RaritySR, 18),
    SSR("SSR", RaritySSR, 8),
    UR("UR", RarityUR, 3),
    HIDDEN("隐藏", RarityHidden, 1),
}

data class SummerCard(
    val title: String,
    val desc: String,
    val reward: String,
    val difficulty: String,
    val rarity: CardRarity,
    val emoji: String,
)

val CARDS = listOf(
    SummerCard("什么都不干", "今天唯一的目标是：保持静止。", "快乐 +10", "难度：无", CardRarity.N, "🛋️"),
    SummerCard("去便利店买根冰棍", "出门 5 分钟，快乐一下午。", "快乐 +5", "难度：⭐", CardRarity.N, "🍦"),
    SummerCard("骑车出去玩", "风在耳边，夏天在路上。", "好心情 +20", "难度：⭐⭐", CardRarity.R, "🚴"),
    SummerCard("睡到自然醒", "没有闹钟的世界，才是暑假。", "精力 +100", "难度：无", CardRarity.R, "🛏️"),
    SummerCard("看一整天动漫", "现实世界暂时离线。", "快乐 +30", "难度：⭐", CardRarity.R, "📺"),
    SummerCard("和朋友约一顿饭", "见面才是夏天的正经事。", "好心情 +15", "难度：⭐", CardRarity.R, "🍉"),
    SummerCard("打游戏到深夜", "赢不赢无所谓，快乐就行。", "快乐 +25 / 第二天困 +50", "难度：⭐⭐⭐", CardRarity.SR, "🎮"),
    SummerCard("去海边看日落", "把夏天的颜色收进眼里。", "好心情 +40", "难度：⭐⭐", CardRarity.SR, "🌊"),
    SummerCard("学会做一道菜", "妈妈再也不用担心我饿着。", "技能 +1", "难度：⭐⭐⭐", CardRarity.SR, "🍳"),
    SummerCard("突然决定出去旅行", "说走就走，这就是夏天的特权。", "自由 +100", "难度：⭐⭐⭐", CardRarity.SSR, "🧳"),
    SummerCard("凌晨三点还在看星星", "星空不说话，但什么都懂。", "浪漫 +50", "难度：⭐⭐", CardRarity.SSR, "🌌"),
    SummerCard("下午睡醒发现天黑了", "时间管理大师（反向）。", "成就：一觉到天黑", "难度：无", CardRarity.UR, "🌇"),
    SummerCard("一整天什么都没干但特别开心", "浪费时间的最高境界：心安理得。", "快乐 +999", "难度：无", CardRarity.UR, "😌"),
    SummerCard("凌晨 3 点：我明天一定早睡", "经典谎言，百试不爽。", "隐藏成就解锁", "难度：???", CardRarity.HIDDEN, "🌙"),
    SummerCard("暑假作业完成度：0%", "最后一天再看。", "隐藏成就解锁", "难度：???", CardRarity.HIDDEN, "🤡"),
    SummerCard("连续 8 小时没有离开床", "床是最好的朋友。", "隐藏成就解锁", "难度：???", CardRarity.HIDDEN, "🛌"),
)

fun randomCard(): SummerCard {
    val total = CARDS.sumOf { it.rarity.weight }
    var r = (0 until total).random()
    for (c in CARDS) {
        if (r < c.rarity.weight) return c
        r -= c.rarity.weight
    }
    return CARDS.first()
}

// ───────────────────────── 暑假倒计时 ─────────────────────────

object SummerClock {
    fun startMillis(): Long {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val start = Calendar.getInstance()
        start.clear()
        start.set(year, Calendar.JULY, 1, 0, 0, 0)
        return start.timeInMillis
    }

    fun endMillis(): Long {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val end = Calendar.getInstance()
        end.clear()
        end.set(year, Calendar.AUGUST, 31, 23, 59, 59)
        return end.timeInMillis
    }
}

data class SummerRemaining(
    val days: Long,
    val hours: Long,
    val minutes: Long,
    val seconds: Long,
    val totalMillis: Long,
    val fractionElapsed: Float,
)

fun computeRemaining(now: Long): SummerRemaining {
    val start = SummerClock.startMillis()
    val end = SummerClock.endMillis()
    val remaining = (end - now).coerceAtLeast(0L)
    val total = (end - start).coerceAtLeast(1L)
    val elapsed = (now - start).coerceIn(0L, total)
    return SummerRemaining(
        days = remaining / (24 * 60 * 60 * 1000L),
        hours = (remaining / (60 * 60 * 1000L)) % 24L,
        minutes = (remaining / (60 * 1000L)) % 60L,
        seconds = (remaining / 1000L) % 60L,
        totalMillis = remaining,
        fractionElapsed = elapsed.toFloat() / total.toFloat(),
    )
}
