package com.example.myapplication.data

data class SimChoice(
    val label: String,
    /** 下一个事件 id，或 "@pool:键" 表示从事件池中随机抽取。 */
    val next: String,
)

data class SimEvent(
    val id: String,
    val time: String,
    val emoji: String,
    val text: String,
    val choices: List<SimChoice> = emptyList(),
    /** 非空表示这一天结束，展示总结语。 */
    val ending: String? = null,
)

private fun e(
    id: String,
    time: String,
    emoji: String,
    text: String,
    ending: String? = null,
    vararg choices: SimChoice,
) = SimEvent(id, time, emoji, text, choices.toList(), ending)

// 事件池：键 -> 候选事件 id
val SIM_POOLS: Map<String, List<String>> = mapOf(
    "bike" to listOf("bike_1", "bike_2", "bike_3"),
    "walk" to listOf("walk_1", "walk_2"),
)

val SIM_EVENTS: Map<String, SimEvent> = listOf(
    e(
        "start", "", "😴", "你醒来了。暑假的一天，正式开始。",
        choices = *arrayOf(
            SimChoice("A. 再睡一会儿", "sleep"),
            SimChoice("B. 出去玩", "go_out"),
            SimChoice("C. 学习", "study"),
            SimChoice("D. 玩游戏", "game"),
        ),
    ),
    e(
        "sleep", "14:37", "😪", "时间跳过……\n你醒来了。你开始怀疑：\n今天是不是已经结束了？",
        choices = *arrayOf(
            SimChoice("A. 出门透透气", "go_out"),
            SimChoice("B. 继续躺着", "deep_sleep"),
            SimChoice("C. 吃个「早午饭」", "brunch"),
        ),
    ),
    e("deep_sleep", "18:52", "🛏️", "天快黑了。你终于下定决心离开床。", ending = "今天与床达成了战略同盟，未失一城。"),
    e(
        "brunch", "15:20", "🍜", "你把早餐和午餐合并成一场伟大的仪式。",
        choices = *arrayOf(
            SimChoice("A. 打开电视", "tv"),
            SimChoice("B. 出门逛逛", "go_out"),
        ),
    ),
    e(
        "tv", "17:05", "📺", "电视里在放动画片重播。你看了三集。",
        choices = *arrayOf(
            SimChoice("A. 继续看", "tv_end"),
            SimChoice("B. 出门", "go_out"),
        ),
    ),
    e("tv_end", "20:15", "📺", "你突然发现，今天只看电视也不错。", ending = "心满意足的一天。"),
    e(
        "go_out", "", "🌤️", "天气很好，太阳晒得人睁不开眼。",
        choices = *arrayOf(
            SimChoice("A. 骑车", "@pool:bike"),
            SimChoice("B. 散步", "@pool:walk"),
            SimChoice("C. 找朋友", "friend"),
        ),
    ),
    e("bike_1", "", "🚴", "你骑到一个从没去过的路口，然后成功迷路了。", ending = "迷路也是一种探险。"),
    e("bike_2", "", "🚴", "风很大，你觉得自己像个少年。", ending = "好心情 +20。"),
    e("bike_3", "", "🚴", "你骑了很远，在路边买了瓶冰汽水。", ending = "自由的味道是橘子味。"),
    e("walk_1", "", "🚶", "你沿着河边慢慢走，发现了一朵很好看的云。", ending = "云也是免费的快乐。"),
    e("walk_2", "", "🐈", "散步时遇见一只猫，它看了你一眼，走了。", ending = "被猫短暂地认可了。"),
    e(
        "friend", "", "👋", "朋友：『出来吗？』\n你：『走！』",
        choices = *arrayOf(
            SimChoice("A. 去打球", "ball"),
            SimChoice("B. 去逛街", "shopping"),
            SimChoice("C. 就坐会儿", "sit"),
        ),
    ),
    e("ball", "", "🏀", "你们在球场挥汗如雨。『最后一球！』（两小时后）", ending = "运动量超标，但很开心。"),
    e("shopping", "", "🛍️", "你们逛了一下午，最后只买了一杯奶茶。", ending = "快乐其实不贵。"),
    e("sit", "", "🪑", "你们在便利店门口坐了一下午，聊到天黑。", ending = "朋友就是最好的风景。"),
    e(
        "study", "", "💻", "你打开了 Python 教程。\n10 分钟后，推荐视频：『为什么程序员都喜欢这个键盘？』\n学习计划发生偏移。",
        choices = *arrayOf(
            SimChoice("A. 关掉视频，继续学", "study_ok"),
            SimChoice("B. 看看这个视频", "youtube"),
            SimChoice("C. 关掉电脑", "quit"),
        ),
    ),
    e("study_ok", "", "📚", "你坚持学了 40 分钟。这已经是暑假奇迹。", ending = "未来的你会感谢现在的你（大概）。"),
    e("youtube", "", "🐱", "3 小时后，你已经在看『猫咪第一次见到西瓜』。", ending = "学习失败，但快乐成功。"),
    e("quit", "", "🚪", "你合上电脑，把学习留给未来的自己。", ending = "明天再说。"),
    e(
        "game", "", "🎮", "你打开了游戏。『就打一局。』",
        choices = *arrayOf(
            SimChoice("A. 手感火热", "win"),
            SimChoice("B. 手气不佳", "lose"),
        ),
    ),
    e("win", "", "🏆", "你赢了一局，见好就收，快乐加倍。", ending = "胜利的滋味。"),
    e("lose", "", "🌙", "『再来一局。』……再抬头，天已经黑了。", ending = "快乐就行，输赢无所谓。"),
).associateBy { it.id }

/** 解析选择指向：支持直接事件 id 与 "@pool:键" 事件池。 */
fun resolveNext(next: String): String {
    if (next.startsWith("@pool:")) {
        val key = next.removePrefix("@pool:")
        val pool = SIM_POOLS[key].orEmpty()
        return if (pool.isEmpty()) next else pool.random()
    }
    return next
}
