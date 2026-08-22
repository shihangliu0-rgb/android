package com.example.myapplication

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.data.Store
import com.example.myapplication.ui.screens.AchievementsScreen
import com.example.myapplication.ui.screens.BootScreen
import com.example.myapplication.ui.screens.CardDrawScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.SimulatorScreen
import com.example.myapplication.ui.screens.TaskBlasterScreen
import com.example.myapplication.ui.screens.WishesScreen

enum class Screen { BOOT, TASK, HOME, CARD, ACHIEVEMENTS, SIMULATOR, WISHES }

@Composable
fun SummerApp() {
    val context = LocalContext.current
    val started = remember { Store.summerStarted(context) }
    var screen by rememberSaveable { mutableStateOf(Screen.BOOT) }

    Crossfade(targetState = screen, animationSpec = tween(500)) { s ->
        when (s) {
            Screen.BOOT -> BootScreen(
                alreadyStarted = started,
                onEnter = { screen = if (started) Screen.HOME else Screen.TASK },
            )

            Screen.TASK -> TaskBlasterScreen(
                onFinish = {
                    Store.setSummerStarted(context, true)
                    screen = Screen.HOME
                },
            )

            Screen.HOME -> HomeScreen(
                onEnterToday = { screen = Screen.SIMULATOR },
            )

            Screen.CARD -> CardDrawScreen(onBack = { screen = Screen.HOME })

            Screen.ACHIEVEMENTS -> AchievementsScreen(onBack = { screen = Screen.HOME })

            Screen.SIMULATOR -> SimulatorScreen(onBack = { screen = Screen.HOME })

            Screen.WISHES -> WishesScreen(onBack = { screen = Screen.HOME })
        }
    }
}
