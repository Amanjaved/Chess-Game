package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.progression.PlayerProgressionManager
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.DailyPuzzleDialog
import com.example.chessgame.ui.components.PlayerProfileDialog
import kotlinx.coroutines.launch

/**
 * Screen 2: Main Game Lobby / Home Screen
 * Premium Royal Chess Game presentation:
 * - Top Player HUD with Level, XP & 3D Settings Jewel Button
 * - Hero 3D Card: PLAY VS COMPUTER (using menu_play_chess)
 * - 2x2 Visual Game Mode Grid:
 *   - Pass & Play (menu_pass_play)
 *   - Daily Puzzles (menu_puzzles)
 *   - Game Analysis (menu_analysis)
 *   - Themes & Boards (menu_themes)
 * - Quick Access Dock: Rules & Profile
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
    var profile by remember { mutableStateOf(PlayerProgressionManager.getProfile()) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showDailyChallengeDialog by remember { mutableStateOf(false) }

    fun refreshProfile() {
        profile = PlayerProgressionManager.getProfile()
    }

    val entranceAlpha = remember { Animatable(0f) }
    val heroScale = remember { Animatable(0.96f) }

    LaunchedEffect(Unit) {
        launch { entranceAlpha.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing)) }
        launch { heroScale.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing)) }
    }

    ArenaBackgroundScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .graphicsLayer { alpha = entranceAlpha.value },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ==========================================
                // 1. TOP BAR: PLAYER PROFILE HUD & SETTINGS
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Player Profile Pill (Clickable)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ArenaColors.TitaniumSurfaceRaised)
                            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                SoundManager.playClick()
                                showProfileDialog = true
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1B283D))
                                        .border(1.dp, ArenaColors.RoyalGold, CircleShape)
                                ) {
                                    Text(text = "👑", fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.playerName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ArenaColors.TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        ArenaBadge(
                                            text = "LVL ${profile.level}",
                                            color = ArenaColors.RoyalGold,
                                            fontSize = 9
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "${profile.winRate}% Win Rate • ${profile.wins} Wins",
                                        fontSize = 10.sp,
                                        color = ArenaColors.TextSecondary
                                    )
                                }
                            }

                            if (profile.currentStreak > 0) {
                                ArenaBadge(
                                    text = "🔥 ${profile.currentStreak}",
                                    color = ArenaColors.CrimsonAlert,
                                    fontSize = 10
                                )
                            }
                        }
                    }

                    // 3D Settings Jewel Button (Generated Asset)
                    RoyalGameIconButton(
                        onClick = {
                            SoundManager.playClick()
                            onOpenSettings()
                        },
                        size = 46.dp,
                        iconRes = R.drawable.icon_settings_game,
                        contentDescription = "Settings"
                    )
                }

                // ==========================================
                // 2. HERO CARD: PLAY VS COMPUTER (menu_play_chess)
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .scale(heroScale.value)
                        .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color(0x66FFD700))
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.8.dp, ArenaColors.RoyalGold, RoundedCornerShape(20.dp))
                        .clickable {
                            SoundManager.playClick()
                            onPlayAI()
                        }
                ) {
                    // Generated 3D Artwork
                    Image(
                        painter = painterResource(id = R.drawable.menu_play_chess),
                        contentDescription = "Play Chess",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient Overlay for crisp text & button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x33000000),
                                        Color(0x77060A14),
                                        Color(0xF0080D18)
                                    )
                                )
                            )
                    )

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArenaBadge(
                                text = "★ SINGLE PLAYER",
                                color = ArenaColors.RoyalGold,
                                fontSize = 9
                            )
                            ArenaBadge(
                                text = "4 DIFFICULTIES",
                                color = ArenaColors.TextPrimary,
                                fontSize = 9
                            )
                        }

                        Column {
                            Text(
                                text = "PLAY VS COMPUTER",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.White
                            )
                            Text(
                                text = "Challenge AI opponents from Novice to Grandmaster.",
                                fontSize = 11.5.sp,
                                color = ArenaColors.TextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            ArenaButton(
                                text = "PLAY NOW",
                                icon = "▶",
                                onClick = onPlayAI,
                                isPrimary = true,
                                height = 40.dp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 3. GAME MODES 2x2 GRID (USING 3D GENERATED ART)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // PASS & PLAY
                    GameModeCard(
                        title = "PASS & PLAY",
                        subtitle = "2 Players • 1 Device",
                        imageRes = R.drawable.menu_pass_play,
                        badge = "LOCAL DUEL",
                        badgeColor = ArenaColors.RoyalGold,
                        onClick = onPlayLocal,
                        modifier = Modifier.weight(1f)
                    )

                    // DAILY PUZZLES
                    GameModeCard(
                        title = "DAILY PUZZLES",
                        subtitle = "Tactics • +150 XP",
                        imageRes = R.drawable.menu_puzzles,
                        badge = "REWARDS",
                        badgeColor = ArenaColors.SolarAmber,
                        onClick = { showDailyChallengeDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // GAME ANALYSIS
                    GameModeCard(
                        title = "ANALYSIS",
                        subtitle = "Review & Insights",
                        imageRes = R.drawable.menu_analysis,
                        badge = "TELEMETRY",
                        badgeColor = ArenaColors.EmeraldVictory,
                        onClick = onOpenAnalysis,
                        modifier = Modifier.weight(1f)
                    )

                    // THEMES & BOARDS
                    GameModeCard(
                        title = "ARMORY",
                        subtitle = "Custom Boards & Pieces",
                        imageRes = R.drawable.menu_themes,
                        badge = "COLLECTIBLES",
                        badgeColor = ArenaColors.RoyalPurple,
                        onClick = onOpenThemes,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 4. BOTTOM QUICK ACCESS: RULES & PROFILE
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArenaButton(
                        text = "HOW TO PLAY",
                        icon = "📖",
                        onClick = onOpenHowToPlay,
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        height = 42.dp
                    )

                    ArenaButton(
                        text = "ACHIEVEMENTS",
                        icon = "🏆",
                        onClick = { showProfileDialog = true },
                        isPrimary = false,
                        modifier = Modifier.weight(1f),
                        height = 42.dp
                    )
                }
            }

            // Player Profile Dialog Modal
            if (showProfileDialog) {
                PlayerProfileDialog(
                    profile = profile,
                    onProfileUpdated = { refreshProfile() },
                    onClose = {
                        showProfileDialog = false
                        refreshProfile()
                    }
                )
            }

            // Daily Puzzle Challenge Modal
            if (showDailyChallengeDialog) {
                DailyPuzzleDialog(
                    onSolved = { refreshProfile() },
                    onClose = {
                        showDailyChallengeDialog = false
                        refreshProfile()
                    }
                )
            }
        }
    }
}

/**
 * Rich Visual Game Mode Card with 3D Generated Artwork & Gradient Scrim
 */
@Composable
private fun GameModeCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "card_press"
    )

    Box(
        modifier = modifier
            .height(145.dp)
            .graphicsLayer {
                scaleX = scaleAnim
                scaleY = scaleAnim
            }
            .shadow(10.dp, RoundedCornerShape(16.dp), spotColor = Color(0x33000000))
            .clip(RoundedCornerShape(16.dp))
            .background(ArenaColors.TitaniumSurface)
            .border(1.2.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                SoundManager.playClick()
                onClick()
            }
    ) {
        // 3D Generated Card Image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Scrim for readable text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x22000000),
                            Color(0x88080D18),
                            Color(0xF5080D18)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ArenaBadge(
                    text = badge,
                    color = badgeColor,
                    fontSize = 8
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = ArenaColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
