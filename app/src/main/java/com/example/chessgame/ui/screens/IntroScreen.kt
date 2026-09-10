package com.example.chessgame.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.chessgame.R
import com.example.chessgame.theme.ArenaColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 1: Cinematic Video Loading & Splash Screen
 * Plays R.raw.loading video animation seamlessly with skip on tap
 * and elegant royal gold loading telemetry.
 */
@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isFinished by remember { mutableStateOf(false) }

    val screenAlpha = remember { Animatable(1f) }
    val progressAnim = remember { Animatable(0f) }

    fun finishIntro() {
        if (!isFinished) {
            isFinished = true
            coroutineScope.launch {
                screenAlpha.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
                onFinish()
            }
        }
    }

    LaunchedEffect(Unit) {
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        delay(150)
        finishIntro()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                finishIntro()
            }
            .graphicsLayer {
                alpha = screenAlpha.value
            }
    ) {
        // 1. Loading Video Animation
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = Uri.parse("android.resource://${ctx.packageName}/${R.raw.loading}")
                    setVideoURI(uri)
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        try {
                            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                        } catch (_: Exception) {}
                        start()
                    }
                    setOnCompletionListener {
                        finishIntro()
                    }
                    setOnErrorListener { _, _, _ ->
                        finishIntro()
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Cinematic Vignette & Bottom Loading HUD
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33000000),
                            Color.Transparent,
                            Color(0x99000000),
                            Color(0xEE080D18)
                        )
                    )
                )
        )

        // 3. Bottom Progress Bar & Tap Prompt
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TAP TO START",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = ArenaColors.RoyalGoldBright
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Elegant Gold Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.60f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0x40101827))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progressAnim.value.coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        ArenaColors.RoyalGoldDark,
                                        ArenaColors.RoyalGold,
                                        ArenaColors.RoyalGoldBright
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

