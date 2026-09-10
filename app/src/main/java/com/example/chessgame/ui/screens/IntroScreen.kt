package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.GameFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Screen 1: Cinematic Kings Battle Loading Screen.
 * Full AAA Video Game Intro featuring:
 * - Two majestic King pieces clashing in the center of the battlefield
 * - Celestial Golden King vs Obsidian Crimson Shadow King
 * - Radial shockwave energy rings, particle sparks, and lightning strike auras
 * - Arcade loading progress bar with percentage readout & rotating strategy tips
 * - Interactive "ENTER BATTLEFIELD ▶" pulse button with tactile sound feedback
 */
@Composable
fun IntroScreen(
    onFinish: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isReadyToEnter by remember { mutableStateOf(false) }
    var hasExited by remember { mutableStateOf(false) }

    val screenAlpha = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0f) }
    var currentTipIndex by remember { mutableIntStateOf(0) }

    val tacticalTips = remember {
        listOf(
            "Every Pawn is a potential Queen.",
            "Tactics is knowing what to do when there is something to do.",
            "Control the center. Rule the battlefield.",
            "A sacrifice is only brilliant if it leads to victory.",
            "The King moves one square, but holds the destiny of all."
        )
    }

    // Infinite Clash & Battle Animations
    val infiniteTransition = rememberInfiniteTransition(label = "battle_loop")

    // Clashing King offsets (-35dp to +5dp clash oscillation)
    val clashProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clash_progress"
    )

    // Glowing impact shockwave pulse
    val shockwaveScale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shockwave_scale"
    )

    val shockwaveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shockwave_alpha"
    )

    // Button pulse glow
    val buttonPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_pulse"
    )

    fun exitToLobby() {
        if (!hasExited) {
            hasExited = true
            SoundManager.playCastle()
            coroutineScope.launch {
                screenAlpha.animateTo(0f, animationSpec = tween(280, easing = FastOutSlowInEasing))
                onFinish()
            }
        }
    }

    LaunchedEffect(Unit) {
        // Fade in screen
        screenAlpha.animateTo(1f, animationSpec = tween(500))

        // Progress simulation with tip changes
        launch {
            while (!isReadyToEnter) {
                delay(2200)
                currentTipIndex = (currentTipIndex + 1) % tacticalTips.size
            }
        }

        // Multi-stage realistic loading
        progressAnim.animateTo(0.28f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(100)
        progressAnim.animateTo(0.65f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        delay(120)
        progressAnim.animateTo(0.92f, animationSpec = tween(500, easing = FastOutSlowInEasing))
        delay(100)
        progressAnim.animateTo(1.0f, animationSpec = tween(350, easing = FastOutSlowInEasing))
        isReadyToEnter = true

        // Sound effect when ready
        SoundManager.playMove()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A10))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isReadyToEnter) {
                    exitToLobby()
                } else {
                    // Tap to skip loading immediately
                    coroutineScope.launch {
                        progressAnim.animateTo(1f, animationSpec = tween(100))
                        isReadyToEnter = true
                    }
                }
            }
            .graphicsLayer {
                alpha = screenAlpha.value
            }
    ) {
        // LAYER 1: ATMOSPHERIC ARENA BACKGROUND WITH CLASH LIGHTING
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF251A3A),
                            Color(0xFF121826),
                            Color(0xFF070A10)
                        ),
                        center = Offset(500f, 800f),
                        radius = 1200f
                    )
                )
        )

        // Floating Sparks & Embers Particle Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val random = Random(42)

            for (i in 0 until 40) {
                val seedX = random.nextFloat()
                val seedY = random.nextFloat()
                val particleRadius = random.nextFloat() * 2.5f + 1f
                val driftSpeed = random.nextFloat() * 80f + 30f

                val time = (clashProgress * 1000f + i * 150) % 1000f
                val yPos = ((seedY * height - (time / 1000f) * height) % height + height) % height
                val xPos = (seedX * width + sin(time / 200f + i) * 20f) % width

                val isGold = i % 2 == 0
                val color = if (isGold) Color(0xFFFFD54F) else Color(0xFFFF5252)

                drawCircle(
                    color = color.copy(alpha = 0.5f + random.nextFloat() * 0.4f),
                    radius = particleRadius,
                    center = Offset(xPos, yPos)
                )
            }
        }

        // LAYER 2: TOP BRANDING & GAME LOGO
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crown Crest Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                        )
                    )
                    .shadow(16.dp, spotColor = Color(0xFFFFB300)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_crown_gold),
                    contentDescription = "Crown",
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Epic Title with Metallic Gold Gradient
            Text(
                text = "CHESS",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                fontFamily = GameFont,
                color = Color(0xFFF8FAFC)
            )

            Text(
                text = "BATTLE OF KINGS • ARCADE GRANDMASTER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp,
                fontFamily = GameFont,
                color = Color(0xFFFFB703)
            )
        }

        // LAYER 3: CINEMATIC KINGS CLASH ARENA (Centerpiece)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp)
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // Center Shockwave Burst
            Canvas(modifier = Modifier.size(240.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD54F).copy(alpha = shockwaveAlpha * 0.7f),
                            Color(0xFFFF5252).copy(alpha = shockwaveAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    radius = (size.minDimension / 2) * shockwaveScale
                )
                drawCircle(
                    color = Color(0xFFFFE082).copy(alpha = shockwaveAlpha),
                    radius = (size.minDimension / 2) * shockwaveScale * 0.85f,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Clashing Kings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // THE WHITE KING (Radiant Golden Aura)
                val whiteKingOffset = (-25 + clashProgress * 22).dp
                Box(
                    modifier = Modifier
                        .offset(x = whiteKingOffset)
                        .size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Radiant Aura
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0x77FFD54F), Color(0x2238BDF8), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.piece_king_white),
                        contentDescription = "White King",
                        modifier = Modifier
                            .size(115.dp)
                            .graphicsLayer {
                                rotationZ = 6f * (1f - clashProgress)
                            }
                    )
                }

                // Center Clash Spark / Energy Collision
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color.White, Color(0xFFFFD54F), Color.Transparent)
                            )
                        )
                )

                // THE BLACK KING (Crimson Shadow Aura)
                val blackKingOffset = (25 - clashProgress * 22).dp
                Box(
                    modifier = Modifier
                        .offset(x = blackKingOffset)
                        .size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Fiery Aura
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0x77FF3366), Color(0x337C4DFF), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.piece_king_black),
                        contentDescription = "Black King",
                        modifier = Modifier
                            .size(115.dp)
                            .graphicsLayer {
                                rotationZ = -6f * (1f - clashProgress)
                            }
                    )
                }
            }
        }

        // LAYER 4: BOTTOM GAME PROGRESS & "ENTER BATTLEFIELD" HUD
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tactical Quote / Strategy Tip
                Text(
                    text = tacticalTips[currentTipIndex],
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = GameFont,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(34.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Arcade Progress Bar / Status
                val currentProgress = progressAnim.value
                val percentInt = (currentProgress * 100).toInt()

                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isReadyToEnter) "BATTLE READY" else "INITIALIZING TACTICS...",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = GameFont,
                        color = if (isReadyToEnter) Color(0xFF10B981) else Color(0xFFFFB703)
                    )
                    Text(
                        text = "$percentInt%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = GameFont,
                        color = Color(0xFFF8FAFC)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Glowing Arcade Meter
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(Color(0xFF141C2B))
                        .border(1.dp, Color(0xFF2A3952), RoundedCornerShape(99.dp))
                ) {
                    if (currentProgress > 0.01f) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(currentProgress.coerceIn(0f, 1f))
                                .clip(RoundedCornerShape(99.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFFB703),
                                            Color(0xFFFFD166),
                                            Color(0xFF00E5FF)
                                        )
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ENTER BATTLEFIELD CTA BUTTON (Visible once ready or pulsing)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(52.dp)
                        .scale(if (isReadyToEnter) buttonPulse else 1f)
                        .shadow(
                            elevation = if (isReadyToEnter) 16.dp else 6.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Color(0xFFFFB703)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isReadyToEnter) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFFC107),
                                        Color(0xFFFF9800),
                                        Color(0xFFFF5722)
                                    )
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1F293D),
                                        Color(0xFF162032)
                                    )
                                )
                            }
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFE082), Color(0xFFFFB300))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable {
                            exitToLobby()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isReadyToEnter) "ENTER BATTLEFIELD" else "TAP TO LAUNCH",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = GameFont,
                            color = if (isReadyToEnter) Color(0xFF0D0907) else Color(0xFFF1F5F9)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "▶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isReadyToEnter) Color(0xFF0D0907) else Color(0xFFFFB703)
                        )
                    }
                }
            }
        }
    }
}
