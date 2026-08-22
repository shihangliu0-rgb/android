package com.example.myapplication.data

data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val desc: String,
    /** true = 打卡式（用户点击「我做到了」解锁）；false = 事件自动解锁。 */
    val claimable: Boolean,
)

val ACHIEVEMENTS = listOf(
    Achievement("first_day", "🎮", "第一滴血", "正式进入暑假模式", claimable = false),
    Achievement("night_owl", "🦉", "时间旅行者", "凌晨 3 点仍未睡觉", claimable = false),
    Achievement("task_cleared", "🎈", "自由之翼", "在开启仪式中清空所有任务", claimable = false),
    Achievement("second_sun", "🔥", "后羿的对手", "成功制造第二个太阳", claimable = false),
    Achievement("card_ur", "🌟", "天选之人", "抽到一张 UR 或隐藏卡", claimable = false),
    Achievement("bed_guardian", "🛏️", "床之守护者", "一天内躺床超过 8 小时", claimable = true),
    Achievement("wind_traveler", "🚴", "风之旅人", "完成一次 20km 以上的骑行", claimable = true),
    Achievement("watermelon", "🍉", "夏日限定", "吃到今年夏天的第一口西瓜", claimable = true),
    Achievement("digital_resident", "💻", "数字世界居民", "连续使用电脑超过 12 小时", claimable = true),
    Achievement("day_wasted", "😌", "时间的主人", "一整天什么都没干，但特别开心", claimable = true),
)
