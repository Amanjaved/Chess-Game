package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.audio.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PlayerColorChoice {
    WHITE,
    RANDOM,
    BLACK
}

enum class DifficultyCardStyle {
    PRIMARY_PARCHMENT,
    CHARCOAL,
    EMBER_BURGUNDY
}

/**
 * Screen 3: Choose Difficulty
 * Two-Layer Architecture:
 * - Layer 1: Atmospheric study background (bg_main_menu.jpg)
 * - Layer 1.5: Subtle central darkening overlay scrim
 * - Layer 2: Native Jetpack Compose UI with 3D sculpted piece cards,
 *            gold crown header, tactile feedback, and responsive layout.
 */
@Composable
fun AISetupScreen(
    onBack: () -> Unit,
    onSelectDifficulty: (AIDifficulty) -> Unit,
    onStartGame: ((AIDifficulty, PlayerColorChoice) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var isNavigating by remember { mutableStateOf(false) }

    // Entrance animations
    val bgAlpha = remember { Animatable(0f) }
    val headerAlpha = remember { Animatable(0f) }
    val headerScale = remember { Animatable(0.95f) }
    val cardAlphas = remember { List(4) { Animatable(0f) } }
    val cardOffsets = remember { List(4) { Animatable(18f) } }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, animationSpec = tween(450)) }
        launch { headerAlpha.animateTo(1f, animationSpec = tween(550)) }
        launch { headerScale.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing)) }

        cardAlphas.forEachIndexed { index, anim ->
            launch {
                delay((100 + index * 55).toLong())
                anim.animateTo(1f, animationSpec = tween(350, easing = FastOutSlowInEasing))
            }
        }
        cardOffsets.forEachIndexed { index, anim ->
            launch {
                delay((100 + index * 55).toLong())
                anim.animateTo(0f, animationSpec = tween(350, easing = FastOutSlowInEasing))
            }
        }
    }

    fun handleDifficultyChoice(diff: AIDifficulty) {
        if (!isNavigating) {
            isNavigating = true
            SoundManager.playClick()
            coroutineScope.launch {
                delay(120)
                if (onStartGame != null) {
                    onStartGame(diff, PlayerColorChoice.WHITE)
                } else {
                    onSelectDifficulty(diff)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080A0D))
    ) {
        // ==========================================
        // LAYER 1: ATMOSPHERIC ROOM BACKGROUND
        // ==========================================
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = "Study Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha.value }
        )

        // ==========================================
        // LAYER 1.5: SUBTLE CENTRAL SCRIM OVERLAY
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xBA07090C), // ~73% central darkening for card readability
                            Color(0x6006080A),
                            Color(0x10050709)  // ~6% at perimeter so desk lamp, window, mug & chessboard pop
                        ),
                        radius = 1250f
                    )
                )
        )

        // ==========================================
        // LAYER 2: NATIVE COMPOSE UI
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ----------------------------------------------------
                // TOP BAR: Circular Back Button & Top Motto
                // ----------------------------------------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 410.dp)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Circular Translucent Back Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(4.dp, CircleShape, spotColor = Color.Black)
                            .clip(CircleShape)
                            .background(Color(0x7314181D))
                            .border(BorderStroke(1.dp, Color(0x35DFB36E)), CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                SoundManager.playClick()
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_chevron),
                            contentDescription = "Back",
                            tint = Color(0xFFF3ECE1),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Top-Right Motto Quotes
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "CHESS\nBUILDS\nA CALMER\nYOU",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.6.sp,
                            lineHeight = 10.5.sp,
                            color = Color(0x99B8AEA0),
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(1.dp)
                                .background(Color(0x40DFB36E))
                        )
                    }
                }

                // ----------------------------------------------------
                // HEADER: Glowing Crown + Choose Difficulty + Subtitle
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 410.dp)
                        .graphicsLayer {
                            alpha = headerAlpha.value
                            scaleX = headerScale.value
                            scaleY = headerScale.value
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gold Crown Icon
                    Image(
                        painter = painterResource(id = R.drawable.ic_crown_gold),
                        contentDescription = "Difficulty Crown",
                        modifier = Modifier.size(38.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Title
                    Text(
                        text = "Choose Difficulty",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = Color(0xFFF3E2C4),
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // Subtitle
                    Text(
                        text = "Find your challenge.\nSharpen your mind.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFC5BEB4)
                    )
                }

                // ----------------------------------------------------
                // 4 DIFFICULTY CARDS (Easy, Medium, Hard, Expert)
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 410.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Easy (Warm Parchment Primary Card)
                    DifficultyActionCard(
                        title = "Easy",
                        subtitle = "Relax and learn\nthe basics.",
                        pieceRes = R.drawable.ic_diff_pawn,
                        cardStyle = DifficultyCardStyle.PRIMARY_PARCHMENT,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[0].value
                            translationY = cardOffsets[0].value
                        },
                        onClick = { handleDifficultyChoice(AIDifficulty.EASY) }
                    )

                    // 2. Medium (Dark Translucent Charcoal)
                    DifficultyActionCard(
                        title = "Medium",
                        subtitle = "Test your tactics.",
                        pieceRes = R.drawable.ic_diff_knight,
                        cardStyle = DifficultyCardStyle.CHARCOAL,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[1].value
                            translationY = cardOffsets[1].value
                        },
                        onClick = { handleDifficultyChoice(AIDifficulty.MEDIUM) }
                    )

                    // 3. Hard (Dark Translucent Charcoal)
                    DifficultyActionCard(
                        title = "Hard",
                        subtitle = "Think several\nmoves ahead.",
                        pieceRes = R.drawable.ic_diff_rook,
                        cardStyle = DifficultyCardStyle.CHARCOAL,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[2].value
                            translationY = cardOffsets[2].value
                        },
                        onClick = { handleDifficultyChoice(AIDifficulty.HARD) }
                    )

                    // 4. Expert (Deep Burgundy / Ember Charcoal)
                    DifficultyActionCard(
                        title = "Expert",
                        subtitle = "Master the board.",
                        pieceRes = R.drawable.ic_diff_king,
                        cardStyle = DifficultyCardStyle.EMBER_BURGUNDY,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[3].value
                            translationY = cardOffsets[3].value
                        },
                        onClick = { handleDifficultyChoice(AIDifficulty.EXPERT) }
                    )
                }

                // ----------------------------------------------------
                // BOTTOM AREA: Italic Quote + Accent Divider
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“A sharper mind\nfor a brighter tomorrow.”",
                        fontSize = 11.5.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color(0x99D5C7B2),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(1.dp)
                            .background(Color(0x40DFB36E))
                    )
                }
            }
        }
    }
}

/**
 * Custom tactile difficulty selection card with 3D sculpted piece icon,
 * parchment or dark charcoal material styling, and smooth press animation.
 */
@Composable
private fun DifficultyActionCard(
    title: String,
    subtitle: String,
    pieceRes: Int,
    cardStyle: DifficultyCardStyle,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.975f else 1f,
        animationSpec = tween(120),
        label = "press_scale"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(82.dp)
            .scale(scale)
            .then(
                when (cardStyle) {
                    DifficultyCardStyle.PRIMARY_PARCHMENT -> Modifier.shadow(
                        elevation = 8.dp,
                        shape = shape,
                        spotColor = Color(0xFFDFB36E).copy(alpha = 0.5f),
                        ambientColor = Color(0xFFDFB36E).copy(alpha = 0.25f)
                    )
                    DifficultyCardStyle.EMBER_BURGUNDY -> Modifier.shadow(
                        elevation = 6.dp,
                        shape = shape,
                        spotColor = Color(0xFF8B2626).copy(alpha = 0.4f),
                        ambientColor = Color.Black.copy(alpha = 0.5f)
                    )
                    DifficultyCardStyle.CHARCOAL -> Modifier.shadow(
                        elevation = 4.dp,
                        shape = shape,
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    )
                }
            )
            .clip(shape)
            .then(
                when (cardStyle) {
                    DifficultyCardStyle.PRIMARY_PARCHMENT -> Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFF3D299),
                                    Color(0xFFDFB36E),
                                    Color(0xFFC79552)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(Color(0x90FFFFFF), Color(0x40FFFFFF))
                                )
                            ),
                            shape
                        )
                    DifficultyCardStyle.EMBER_BURGUNDY -> Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xD92E1617), // Rich warm dark burgundy
                                    Color(0xD91E1316),
                                    Color(0xD914141A)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(Color(0x50C25050), Color(0x25DFB36E), Color(0x20FFFFFF))
                                )
                            ),
                            shape
                        )
                    DifficultyCardStyle.CHARCOAL -> Modifier
                        .background(Color(0xD912161A)) // Dark translucent charcoal
                        .border(
                            BorderStroke(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(Color(0x40DFB36E), Color(0x18DFB36E), Color(0x20FFFFFF))
                                )
                            ),
                            shape
                        )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // 3D Sculpted Chess Piece Icon
                Image(
                    painter = painterResource(id = pieceRes),
                    contentDescription = "$title piece",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(54.dp)
                        .padding(end = 4.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Title and Subtitle
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cardStyle == DifficultyCardStyle.PRIMARY_PARCHMENT) Color(0xFF1D140C) else Color(0xFFF3ECE1),
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 14.sp,
                        color = if (cardStyle == DifficultyCardStyle.PRIMARY_PARCHMENT) Color(0xFF4A3828) else Color(0xFF9E9992)
                    )
                }
            }

            // Right Chevron arrow
            Text(
                text = "›",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (cardStyle == DifficultyCardStyle.PRIMARY_PARCHMENT) Color(0xFF332012) else Color(0xFF8A847C),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(name = "Phone 390x844", widthDp = 390, heightDp = 844)
@Composable
private fun AISetupScreenPreview390x844() {
    AISetupScreen(
        onBack = {},
        onSelectDifficulty = {}
    )
}

@Preview(name = "Phone 430x932", widthDp = 430, heightDp = 932)
@Composable
private fun AISetupScreenPreview430x932() {
    AISetupScreen(
        onBack = {},
        onSelectDifficulty = {}
    )
}

@Preview(name = "Phone 375x667", widthDp = 375, heightDp = 667)
@Composable
private fun AISetupScreenPreview375x667() {
    AISetupScreen(
        onBack = {},
        onSelectDifficulty = {}
    )
}
