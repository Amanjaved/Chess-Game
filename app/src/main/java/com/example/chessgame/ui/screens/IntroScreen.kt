package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.theme.ArenaColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 1: Game Splash & Boot Sequence
 * "Grandmaster Arena / Tactical Monolith" Game Identity:
 * - Futuristic atmospheric arena environment
 * - High-impact metallic Knight-King game emblem with cyan/amber halo
 * - GRANDMASTER ARENA wordmark & tactical combat subtitle
 * - Dynamic energy loading bar with multi-phase engine boot status
 * - Tap to fast-skip into Game Lobby
 */
@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isFinished by remember { mutableStateOf(false) }

    // Visual entrance & exit animation states
    val screenAlpha = remember { Animatable(0f) }
    val contentScale = remember { Animatable(0.95f) }
    val progressAnim = remember { Animatable(0f) }

    // Ambient emblem breathing pulse
    val infiniteTransition = rememberInfiniteTransition(label = "emblem_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    fun finishIntro() {
        if (!isFinished) {
            isFinished = true
            coroutineScope.launch {
                screenAlpha.animateTo(0f, animationSpec = tween(250, easing = FastOutSlowInEasing))
                onFinish()
            }
        }
    }

    LaunchedEffect(Unit) {
        launch {
            screenAlpha.animateTo(1f, animationSpec = tween(400, easing = LinearOutSlowInEasing))
        }
        launch {
            contentScale.animateTo(1.0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }

        // Multi-stage tactical engine boot sequence
        delay(150)
        progressAnim.animateTo(0.32f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        delay(100)
        progressAnim.animateTo(0.68f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        delay(100)
        progressAnim.animateTo(0.92f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        delay(80)
        progressAnim.animateTo(1.0f, animationSpec = tween(250, easing = FastOutSlowInEasing))
        delay(200)
        finishIntro()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaColors.VoidAbyss)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isFinished) {
                    coroutineScope.launch {
                        progressAnim.animateTo(1f, animationSpec = tween(80))
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
        // LAYER 1: Arena Monolith Background
        Image(
            painter = painterResource(id = R.drawable.bg_arena_monolith),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 1.5: Deep Scrim Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xEE090D14),
                            Color(0xCC0B111A),
                            Color(0x55070A0F)
                        ),
                        radius = 1200f
                    )
                )
        )

        // LAYER 2: Hero Emblem & Branding (Centered)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emblem with glowing halo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(170.dp)
            ) {
                // Radial energy halo
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ArenaColors.CyberCyan.copy(alpha = pulseGlow * 0.45f),
                                    ArenaColors.SolarAmber.copy(alpha = pulseGlow * 0.20f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Master Game Emblem
                Image(
                    painter = painterResource(id = R.drawable.logo_game_emblem),
                    contentDescription = "Grandmaster Arena Emblem",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(136.dp)
                        .shadow(24.dp, RoundedCornerShape(20.dp), spotColor = ArenaColors.CyberCyan)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Game Wordmark
            Text(
                text = "GRANDMASTER",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.SansSerif,
                color = ArenaColors.TextPrimary
            )
            Text(
                text = "ARENA",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 8.sp,
                fontFamily = FontFamily.SansSerif,
                color = ArenaColors.CyberCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "TACTICAL CHESS COMBAT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                color = ArenaColors.TextSecondary
            )
        }

        // LAYER 3: Energy Progress Bar & Boot Status
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val progress = progressAnim.value

                // Status text based on boot phase
                val statusText = when {
                    progress < 0.35f -> "INITIALIZING TACTICAL CHESS ENGINE..."
                    progress < 0.70f -> "CALIBRATING MINIMAX HEURISTICS..."
                    progress < 0.95f -> "SYNCHRONIZING GRANDMASTER ARENA..."
                    else -> "ARENA ONLINE • TAP TO COMMENCE"
                }

                Text(
                    text = statusText,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.8.sp,
                    color = if (progress >= 0.95f) ArenaColors.CyberCyan else ArenaColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // High-tech energy progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(6.dp)
                        .shadow(8.dp, RoundedCornerShape(3.dp), spotColor = ArenaColors.CyberCyan.copy(alpha = 0.5f))
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF131A24))
                        .border(0.8.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(3.dp))
                ) {
                    if (progress > 0.01f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            ArenaColors.CyberCyanDim,
                                            ArenaColors.CyberCyan,
                                            ArenaColors.SolarAmber
                                        )
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}
