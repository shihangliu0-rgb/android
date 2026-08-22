# ☀️ SUMMER START · 暑假启动器

一个「打开就开心」的互动式暑假开启仪式 —— 不是普通 App，而是一个
**属于暑假的小世界**。

基于 Android + Jetpack Compose（Kotlin / Material 3），全程中文、夏日配色、
治愈系动画。

---

## 🎬 使用流程

```
🌅 开场封面（Loading freedom... / 系统启动动画）
   │
   ▼
🎉 开启暑假（点击放飞「高数 / DDL / 早八…」，粒子爆炸 💥）
   │
   ▼
☀️ 暑假主页（SUMMER MODE · ENABLED）
   ├── 🕶️ 暑假倒计时（XX 天 XX 小时 XX 分钟，进度条 + 随机趣味提示）
   ├── 🎴 今日抽卡（每天固定一张，N/R/SR/SSR/UR/隐藏 稀有度，翻牌动画）
   ├── 🏆 暑假成就（10 个成就，本地保存）
   ├── 🏝️ 人生模拟器（事件池 + 分支 + 随机结果）
   ├── 🍾 愿望漂流瓶（写愿望 / 标记完成，本地保存）
   └── 关于 · 设置（含开发者选项）
```

---

## 🥚 隐藏彩蛋（≥5 个）

1. **连续点太阳**：第 1 次「今天很热」→ 第 5 次「真的很热」→ 第 10 次「别点了」
   → 温度飙升、天空变热色调 → 第 15 次「你成功制造了第二个太阳」（天空出现第二颗太阳）。
2. **连点标题 SUMMER**：第 5 次「你真的很喜欢这个标题。」
3. **主页快速连点**（2.5 秒内 8 次）：「冷静一点 😂」
4. **凌晨打开**（0–4 点）：「🌙 这个时间……你确定这是暑假吗？」
5. **暑假最后几天**（剩余 1–5 天）：「📉 系统检测到自由时间正在减少。」（只提示一次）
6. **点倒计时卡片**：「你确定要知道吗？」→ 选「看看吧」→ 60→0 疯狂倒数 →
   「开玩笑的。别紧张，暑假还没结束 😄」

---

## 📁 代码结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt              # 入口（edge-to-edge + SummerTheme）
├── SummerApp.kt                 # BOOT → TASK → HOME → 子页面 状态机
├── data/
│   ├── SummerContent.kt         # 任务 / 抽卡卡池 / 暑假倒计时
│   ├── Store.kt                 # 统一本地存储（SharedPreferences + JSON）
│   ├── Achievements.kt          # 成就定义
│   ├── Simulator.kt             # 模拟器事件池 + 分支
│   └── Wishes.kt                # 漂流瓶数据模型
├── ui/
│   ├── theme/                   # 夏日配色 + 主题
│   ├── components/
│   │   ├── Sky.kt               # 天空渐变 / 云朵漂移 / 太阳（可随热度变红）
│   │   ├── Particles.kt         # 点击粒子爆炸
│   │   └── Common.kt            # 返回头 / 提示气泡
│   └── screens/
│       ├── BootScreen.kt        # 开场封面 + 系统启动
│       ├── TaskBlasterScreen.kt # 放飞考试/作业小游戏
│       ├── HomeScreen.kt        # 主页（倒计时 / 入口 / 彩蛋）
│       ├── CardDrawScreen.kt    # 今日抽卡
│       ├── AchievementsScreen.kt# 成就列表
│       ├── SimulatorScreen.kt   # 人生模拟器
│       └── WishesScreen.kt      # 愿望漂流瓶
└── res/                         # 启动图标（太阳）+ 字符串 + 主题
```

---

## 💾 本地持久化

以下数据关闭 App 后仍然保留（`SharedPreferences` + JSON）：

- 已开启暑假状态
- 今日抽卡结果（每天固定一张，当天重开不变）
- 已解锁成就
- 愿望漂流瓶
- 彩蛋触发记录

> 调试：主页「关于」→ 开发者选项 → 重置今日抽卡（仅调试用，不在普通界面）。

---

## 🛠️ 如何运行

1. 用 **Android Studio** 打开本目录（`/home/user/android`）。
2. 等待 Gradle Sync 完成（需要联网下载依赖）。
3. 选择设备 / 模拟器，点击 Run ▶。

环境要求：

- Android SDK **API 36**（`compileSdk = 36`，`minSdk = 24`）
- JDK 17（AGP 9.1 需要）
- 项目使用 Compose BOM `2026.02.01`、Kotlin `2.2.10`

> 命令行构建：`./gradlew assembleDebug`

---

## 🗺️ 下一步（V3+ 可选）

- 🌦️ 根据真实天气改变主页
- 📅 真正的暑假日历
- 📸 夏日照片墙
- 🎵 根据时间变化的背景音乐
- 🌙 深夜模式
- 🗺️ 暑假足迹地图
- 👥 双人暑假 PK / 分享卡片
