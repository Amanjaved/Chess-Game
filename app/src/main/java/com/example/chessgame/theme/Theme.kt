package com.example.chessgame.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LuxuryChessColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = BlueAccent,
    tertiary = CyanAccent,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color(0xFF111418),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun ChessGameTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LuxuryChessColorScheme,
        typography = Typography,
        content = content
    )
}
