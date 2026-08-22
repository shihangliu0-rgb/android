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
import com.example.myapplication.data.Prefs
import com.example.myapplication.ui.screens.BootScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.TaskBlasterScreen

enum class Screen { BOOT, TASK, HOME }

@Composable
fun SummerApp() {
    val context = LocalContext.current
    val started = remember { Prefs.summerStarted(context) }
    var screen by rememberSaveable { mutableStateOf(Screen.BOOT) }

    Crossfade(targetState = screen, animationSpec = tween(500)) { s ->
        when (s) {
            Screen.BOOT -> BootScreen(
                alreadyStarted = started,
                onEnter = { screen = if (started) Screen.HOME else Screen.TASK },
            )

            Screen.TASK -> TaskBlasterScreen(
                onFinish = {
                    Prefs.setSummerStarted(context, true)
                    screen = Screen.HOME
                },
            )

            Screen.HOME -> HomeScreen(
                onReplay = { screen = Screen.TASK },
            )
        }
    }
}
