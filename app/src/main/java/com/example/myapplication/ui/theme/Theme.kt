package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 夏日专属配色：清爽、明亮、治愈
private val SummerColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3DE),
    onPrimaryContainer = Color(0xFF6B2113),
    secondary = SkyDeep,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD8ECFF),
    onSecondaryContainer = Color(0xFF0F2C52),
    tertiary = Lemon,
    onTertiary = Color(0xFF4A3200),
    background = SkyBottom,
    onBackground = Ink,
    surface = CardCream,
    onSurface = Ink,
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF3D4A5C),
    outline = Color(0xFF9AA7B5),
)

@Composable
fun SummerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SummerColorScheme,
        typography = Typography,
        content = content,
    )
}
