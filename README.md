# ☀️ SUMMER START · 暑假启动器

一个「打开就开心」的互动式暑假开启仪式 —— 不是普通 App，而是一段
**一步一步进入暑假** 的小体验。

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
☀️ 主界面
   ├── 🕶️ 暑假倒计时（XX 天 XX 小时 XX 分钟，进度条）
   ├── 🎴 今日暑假计划（抽卡 + 翻牌动画，含 N/R/SR/SSR/UR/隐藏 稀有度）
   ├── 🏆 / 🏝️ / 🍾 成就·模拟器·漂流瓶（Version 2 敬请期待）
   └── 🥚 两个隐藏彩蛋
```

---

## 🥚 隐藏彩蛋

1. **疯狂点太阳**：第 10 次「太阳：别点了，我真的很热。」→ 温度飙升、天空变热
   色调；点满 20 次「你成功制造了第二个太阳。」（天空会出现第二颗太阳 🔥）。
2. **点「暑假剩余」卡片**：弹出「你确定要知道吗？」——选「看看吧」，剩余数字
   60→0 疯狂倒数，最后「开玩笑的。别紧张 😄」。

另外：首次完成后会把「已开启暑假」记在本地，下次打开直接进入主界面，
底部有「↺ 再玩一次：放飞考试」。

---

## 📁 代码结构

```
app/src/main/java/com/example/myapplication/
├── MainActivity.kt              # 入口（edge-to-edge + SummerTheme）
├── SummerApp.kt                 # BOOT → TASK → HOME 状态机
├── data/
│   ├── SummerContent.kt         # 任务 / 抽卡卡池 / 暑假倒计时
│   └── Prefs.kt                 # 本地记录「已开启」
├── ui/
│   ├── theme/                   # 夏日配色 + 主题
│   ├── components/
│   │   ├── Sky.kt               # 天空渐变 / 云朵漂移 / 太阳（可随热度变红）
│   │   └── Particles.kt         # 点击粒子爆炸
│   └── screens/
│       ├── BootScreen.kt        # 开场封面 + 系统启动
│       ├── TaskBlasterScreen.kt # 放飞考试/作业小游戏
│       └── HomeScreen.kt        # 主界面（倒计时/抽卡/彩蛋）
└── res/                         # 启动图标（太阳）+ 字符串 + 主题
```

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

## 🗺️ 下一步（Version 2 / 3 / 4）

按「一版一小步」的思路，预留了入口但暂未实现：

- **Version 2**：🏆 暑假成就系统（第一滴血 / 床之守护者 / 风之旅人…）
- **Version 3**：🏝️ 今日模拟器（醒来后 A/B/C/D 选择分支）
- **Version 4**：🍾 愿望漂流瓶（写下愿望、投入夏日漂流瓶）
