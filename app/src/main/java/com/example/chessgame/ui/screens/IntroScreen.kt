package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 1: Splash / Intro
 * Minimalist luxury chess branding:
 * - Deep charcoal / near-black background (#080A0D)
 * - Minimalist gold & ivory knight emblem with ambient studio lighting
 * - Elegant CHESS wordmark and "CLASSIC STRATEGY. REIMAGINED." subtitle
 * - Perspective dark chessboard on lower section fading into darkness
 * - REAL Jetpack Compose rounded loading bar with warm gold gradient & subtle glow
 * - "GOOD MOVES TAKE TIME" subtitle
 * - Safe area padding for edge-to-edge Android gesture navigation bar
 */
@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isFinished by remember { mutableStateOf(false) }

    // Visual entrance & exit animation states
    val screenAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.97f) }
    val progressAnim = remember { Animatable(0f) }

    // Subtle ambient gold highlight breathing
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.16f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow_anim"
    )

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
        // Entrance fade & subtle scale
        launch {
            screenAlpha.animateTo(1f, animationSpec = tween(650, easing = LinearOutSlowInEasing))
        }
        launch {
            contentScale.animateTo(1.0f, animationSpec = tween(950, easing = FastOutSlowInEasing))
        }

        // Realistic multi-stage loading progression
        delay(250)
        progressAnim.animateTo(0.25f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(120)
        progressAnim.animateTo(0.58f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        delay(150)
        progressAnim.animateTo(0.85f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(120)
        progressAnim.animateTo(1.0f, animationSpec = tween(400, easing = FastOutSlowInEasing))

        // Intentional brief hold at 100%
        delay(250)
        finishIntro()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080A0D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isFinished) {
                    coroutineScope.launch {
                        progressAnim.animateTo(1f, animationSpec = tween(120))
                        finishIntro()
                    }
                }
            }
            .graphicsLayer {
                alpha = screenAlpha.value
                scaleX = contentScale.value
                scaleY = contentScale.value
            }
    ) {
        // Master minimalist chess background artwork
        Image(
            painter = painterResource(id = R.drawable.splash_chess_minimal),
            contentDescription = "Chess Splash",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle ambient warm gold breathing light overlay
        if (ambientGlow > 0.001f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFDFB36E).copy(alpha = ambientGlow),
                                Color(0xFFC69B56).copy(alpha = ambientGlow * 0.4f),
                                Color.Transparent
                            ),
                            radius = 900f
                        )
                    )
            )
        }

        // REAL Jetpack Compose Loading Bar & Subtitle (pinned cleanly above navigation safe area)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Real Jetpack Compose loading bar with rounded pill shape, dark track, warm gold progress & subtle glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(5.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(99.dp),
                            spotColor = Color(0xFFDFB36E).copy(alpha = 0.45f),
                            ambientColor = Color(0xFFDFB36E).copy(alpha = 0.25f)
                        )
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFF1B1E24))
                ) {
                    val currentProgress = progressAnim.value
                    if (currentProgress > 0.01f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(currentProgress.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(99.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFC69B56),
                                            Color(0xFFF3D28E),
                                            Color(0xFFDFB36E)
                                        )
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Typography under loading bar
                Text(
                    text = "GOOD MOVES TAKE TIME",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 3.5.sp,
                    color = Color(0xFF9E9B95)
                )
            }
        }
    }
}

@Preview(name = "Phone 390x844", widthDp = 390, heightDp = 844)
@Composable
private fun IntroScreenPreview390x844() {
    IntroScreen(onFinish = {})
}

@Preview(name = "Phone 430x932", widthDp = 430, heightDp = 932)
@Composable
private fun IntroScreenPreview430x932() {
    IntroScreen(onFinish = {})
}

@Preview(name = "Phone 375x667", widthDp = 375, heightDp = 667)
@Composable
private fun IntroScreenPreview375x667() {
    IntroScreen(onFinish = {})
}
