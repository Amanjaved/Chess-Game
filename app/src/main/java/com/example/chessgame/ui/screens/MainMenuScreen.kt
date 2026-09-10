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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 2: Main Menu
 * Two-Layer Architecture:
 * - Layer 1: Clean atmospheric study background asset (bg_main_menu.jpg)
 * - Layer 1.5: Subtle central darkening overlay scrim for contrast
 * - Layer 2: Native Jetpack Compose UI with custom tactile cards,
 *            gold knight branding, and responsive safe-area layout.
 */
@Composable
fun MainMenuScreen(
    onPlayAI: () -> Unit,
    onPlayLocal: () -> Unit,
    onOpenAnalysis: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Entrance animations
    val bgAlpha = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.95f) }
    val cardAlphas = remember { List(6) { Animatable(0f) } }
    val cardOffsets = remember { List(6) { Animatable(16f) } }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, animationSpec = tween(450)) }
        launch { logoAlpha.animateTo(1f, animationSpec = tween(550)) }
        launch { logoScale.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing)) }

        cardAlphas.forEachIndexed { index, anim ->
            launch {
                delay((100 + index * 45).toLong())
                anim.animateTo(1f, animationSpec = tween(340, easing = FastOutSlowInEasing))
            }
        }
        cardOffsets.forEachIndexed { index, anim ->
            launch {
                delay((100 + index * 45).toLong())
                anim.animateTo(0f, animationSpec = tween(340, easing = FastOutSlowInEasing))
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
                            Color(0x6006080A), // gentle falloff
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
                // TOP HEADER: Subtle Motto + Knight Emblem + CHESS Wordmark
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 410.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Subtle Top Motto Quotes (matching reference screenshot)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "SAME GAME.\nNEW STORIES.",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.6.sp,
                                lineHeight = 11.sp,
                                color = Color(0x99B8AEA0)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(1.dp)
                                    .background(Color(0x40DFB36E))
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "DISCIPLINE\nCREATES FREEDOM.",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.6.sp,
                                lineHeight = 11.sp,
                                color = Color(0x99B8AEA0),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(1.dp)
                                    .background(Color(0x40DFB36E))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Knight Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_menu_knight),
                        contentDescription = "Chess Logo",
                        modifier = Modifier
                            .size(62.dp)
                            .graphicsLayer {
                                alpha = logoAlpha.value
                                scaleX = logoScale.value
                                scaleY = logoScale.value
                            }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // CHESS Wordmark
                    Text(
                        text = "CHESS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 7.sp,
                        color = Color(0xFFF3E2C4),
                        fontFamily = FontFamily.Serif
                    )

                    // Subtitle
                    Text(
                        text = "CLASSIC STRATEGY. REIMAGINED.",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.4.sp,
                        color = Color(0xFFC79E66)
                    )
                }

                // ----------------------------------------------------
                // CENTRAL MENU BUTTONS (6 Cards with responsive spacing)
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 410.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Play vs AI (Primary Warm Gold / Amber Card)
                    MainMenuCard(
                        title = "Play vs AI",
                        subtitle = "Challenge yourself",
                        iconRes = R.drawable.ic_menu_play_ai,
                        isPrimary = true,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[0].value
                            translationY = cardOffsets[0].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onPlayAI()
                        }
                    )

                    // 2. Local 2 Player
                    MainMenuCard(
                        title = "Local 2 Player",
                        subtitle = "One board. Two minds.",
                        iconRes = R.drawable.ic_menu_local_player,
                        isPrimary = false,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[1].value
                            translationY = cardOffsets[1].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onPlayLocal()
                        }
                    )

                    // 3. Analyse Game
                    MainMenuCard(
                        title = "Analyse Game",
                        subtitle = "Review your matches",
                        iconRes = R.drawable.ic_menu_analyse,
                        isPrimary = false,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[2].value
                            translationY = cardOffsets[2].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onOpenAnalysis()
                        }
                    )

                    // 4. Themes
                    MainMenuCard(
                        title = "Themes",
                        subtitle = "Customize your board",
                        iconRes = R.drawable.ic_menu_themes,
                        isPrimary = false,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[3].value
                            translationY = cardOffsets[3].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onOpenThemes()
                        }
                    )

                    // 5. How to Play
                    MainMenuCard(
                        title = "How to Play",
                        subtitle = "Learn the game",
                        iconRes = R.drawable.ic_menu_how_to_play,
                        isPrimary = false,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[4].value
                            translationY = cardOffsets[4].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onOpenHowToPlay()
                        }
                    )

                    // 6. Settings
                    MainMenuCard(
                        title = "Settings",
                        subtitle = "Make it yours",
                        iconRes = R.drawable.ic_menu_settings,
                        isPrimary = false,
                        modifier = Modifier.graphicsLayer {
                            alpha = cardAlphas[5].value
                            translationY = cardOffsets[5].value
                        },
                        onClick = {
                            SoundManager.playClick()
                            onOpenSettings()
                        }
                    )
                }

                // ----------------------------------------------------
                // BOTTOM AREA: Understated Status Line
                // ----------------------------------------------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "100% OFFLINE   •   FIDE RULES   •   LOCAL PLAY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        color = Color(0x99D5C7B2)
                    )
                }
            }
        }
    }
}

/**
 * Custom tactile menu action card with distinct styling for Primary vs Secondary actions.
 */
@Composable
private fun MainMenuCard(
    title: String,
    subtitle: String,
    iconRes: Int,
    isPrimary: Boolean,
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

    val shape = RoundedCornerShape(15.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isPrimary) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = shape,
                        spotColor = Color(0xFFDFB36E).copy(alpha = 0.5f),
                        ambientColor = Color(0xFFDFB36E).copy(alpha = 0.25f)
                    )
                } else {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = shape,
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    )
                }
            )
            .clip(shape)
            .then(
                if (isPrimary) {
                    Modifier
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
                } else {
                    Modifier
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
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = if (isPrimary) Color(0xFF1D140C) else Color(0xFFDFB36E),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Title and Subtitle
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrimary) Color(0xFF1D140C) else Color(0xFFF3ECE1),
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isPrimary) Color(0xFF4A3828) else Color(0xFF9E9992)
                    )
                }
            }

            // Right Chevron arrow
            Text(
                text = "›",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) Color(0xFF332012) else Color(0xFF8A847C),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(name = "Phone 390x844", widthDp = 390, heightDp = 844)
@Composable
private fun MainMenuScreenPreview390x844() {
    MainMenuScreen(
        onPlayAI = {},
        onPlayLocal = {},
        onOpenAnalysis = {},
        onOpenThemes = {},
        onOpenHowToPlay = {},
        onOpenSettings = {}
    )
}

@Preview(name = "Phone 430x932", widthDp = 430, heightDp = 932)
@Composable
private fun MainMenuScreenPreview430x932() {
    MainMenuScreen(
        onPlayAI = {},
        onPlayLocal = {},
        onOpenAnalysis = {},
        onOpenThemes = {},
        onOpenHowToPlay = {},
        onOpenSettings = {}
    )
}

@Preview(name = "Phone 375x667", widthDp = 375, heightDp = 667)
@Composable
private fun MainMenuScreenPreview375x667() {
    MainMenuScreen(
        onPlayAI = {},
        onPlayLocal = {},
        onOpenAnalysis = {},
        onOpenThemes = {},
        onOpenHowToPlay = {},
        onOpenSettings = {}
    )
}
