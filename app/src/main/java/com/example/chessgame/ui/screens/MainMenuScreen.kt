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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.progression.PlayerProfile
import com.example.chessgame.progression.PlayerProgressionManager
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.DailyPuzzleDialog
import com.example.chessgame.ui.components.PlayerProfileDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 2: Game Lobby (Main Menu)
 * Redesigned from scratch as a true AAA Mobile Game Hub:
 * - High-contrast obsidian slate surfaces with cyber-gold highlights
 * - Top Gamer HUD: Level badge, animated XP bar, win stats, and streak flame
 * - Dominant Hero Play Banner: "PLAY VS AI" with glowing gold border & tactile play trigger
 * - Secondary Game Modes: Local 2-Player duel & Daily Puzzle with XP rewards
 * - 4-Tile Game Utility Hub: Analysis Lab, Theme Vault, Trophy Hall, Battle Academy
 * - Quick Settings Bar with instant sound/game configuration
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

    // Smooth Entrance Animations
    val heroAlpha = remember { Animatable(0f) }
    val heroScale = remember { Animatable(0.95f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { heroAlpha.animateTo(1f, animationSpec = tween(400)) }
        launch { heroScale.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing)) }
        launch {
            delay(100)
            contentAlpha.animateTo(1f, animationSpec = tween(400))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A10))
    ) {
        // LAYER 1: ATMOSPHERIC GAME LOBBY BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.35f }
        )

        // Radial obsidian darkening vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x990A0F1D),
                            Color(0xEE070A10),
                            Color(0xFF070A10)
                        ),
                        radius = 1200f
                    )
                )
        )

        // LAYER 2: NATIVE COMPOSE GAME UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ==========================================
                // 1. TOP SECTION: GAMER PROFILE HUD & TITLE
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Gamer HUD Card
                    GamerProfileHudCard(
                        profile = profile,
                        onClick = {
                            SoundManager.playClick()
                            showProfileDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Grand Title Branding with Golden Crown
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_crown_gold),
                            contentDescription = "Crown",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CHESS",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 5.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                    }

                    Text(
                        text = "THE ULTIMATE BATTLE OF MINDS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontFamily = GameFont,
                        color = Color(0xFFFFB703)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 2. MAIN PLAY AREA (Dominant Game Mode Cards)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .graphicsLayer {
                            alpha = heroAlpha.value
                            scaleX = heroScale.value
                            scaleY = heroScale.value
                        },
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // HERO CTA: PLAY VS AI (Glowing Arcade Grandmaster Card)
                    HeroGameModeCard(
                        title = "PLAY VS AI",
                        subtitle = "4 Ranked Boss Engines • Tactical Combat",
                        badge = "RANKED MATCH",
                        onClick = {
                            SoundManager.playClick()
                            onPlayAI()
                        }
                    )

                    // Secondary Play Row: Local 2-Player & Daily Puzzle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Local 2-Player Pass & Play
                        SecondaryGameModeCard(
                            title = "Local 2 Player",
                            subtitle = "One Device • Pass & Play",
                            badge = "OFFLINE DUEL",
                            badgeColor = Color(0xFF38BDF8),
                            icon = "⚔️",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onPlayLocal()
                            }
                        )

                        // Daily Tactical Challenge
                        SecondaryGameModeCard(
                            title = "Daily Puzzle",
                            subtitle = "Tactical Mate Mission",
                            badge = "+75 XP BONUS",
                            badgeColor = Color(0xFF00E5FF),
                            icon = "🧩",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                showDailyChallengeDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 3. GAME UTILITIES HUB (4 Interactive Tiles)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .graphicsLayer { alpha = contentAlpha.value },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameHubActionTile(
                            title = "Analysis Lab",
                            subtitle = "Blunders & Accuracy",
                            iconRes = R.drawable.ic_menu_analyse,
                            accentColor = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onOpenAnalysis()
                            }
                        )

                        GameHubActionTile(
                            title = "Theme Vault",
                            subtitle = "Custom Boards & Pieces",
                            iconRes = R.drawable.ic_menu_themes,
                            accentColor = Color(0xFFFFB703),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onOpenThemes()
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameHubActionTile(
                            title = "Trophy Hall",
                            subtitle = "${profile.achievements.count { it.isUnlocked }} of ${profile.achievements.size} Unlocked",
                            icon = "🏆",
                            accentColor = Color(0xFFFFD166),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                showProfileDialog = true
                            }
                        )

                        GameHubActionTile(
                            title = "Battle Academy",
                            subtitle = "Rules & Tactics",
                            iconRes = R.drawable.ic_menu_how_to_play,
                            accentColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onOpenHowToPlay()
                            }
                        )
                    }

                    // Settings & Audio Quick Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF101726))
                            .border(1.dp, Color(0xFF243248), RoundedCornerShape(14.dp))
                            .clickable {
                                SoundManager.playClick()
                                onOpenSettings()
                            }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_menu_settings),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Game Settings & Audio Controls",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GameFont,
                                    color = Color(0xFFF1F5F9)
                                )
                            }
                            Text(
                                text = "CONFIGURE ▶",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                fontFamily = GameFont,
                                color = Color(0xFFFFB703)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 4. FOOTER STATUS
                // ==========================================
                Text(
                    text = "100% OFFLINE • FIDE RULES • LOCAL PROGRESSION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                    fontFamily = GameFont,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }

        // ==========================================
        // DIALOGS & OVERLAYS
        // ==========================================
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

/**
 * Top Gamer HUD Card displaying Avatar, Rank Crest, Level, XP Bar, and Stat Badges.
 */
@Composable
private fun GamerProfileHudCard(
    profile: PlayerProfile,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, label = "press")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = Color(0xFF000000))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF131C2D), Color(0xFF0E1624))
                )
            )
            .border(1.5.dp, Color(0xFF2A3A54), RoundedCornerShape(18.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Level Hexagon / Crest Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                        )
                    )
                    .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LVL",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = GameFont,
                        color = Color(0xFF1E1304)
                    )
                    Text(
                        text = "%02d".format(profile.level),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = GameFont,
                        color = Color(0xFF0F0A02)
                    )
                }
            }

            // Player Info & XP Bar
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = profile.playerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                        Text(
                            text = profile.title.uppercase(),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFFFB703)
                        )
                    }

                    // Stat Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Wins Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2210B981))
                                .border(0.8.dp, Color(0x6610B981), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🏆 ${profile.wins}W",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = GameFont,
                                color = Color(0xFF10B981)
                            )
                        }

                        // Win Rate Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x22FFB703))
                                .border(0.8.dp, Color(0x66FFB703), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ ${profile.winRate}%",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = GameFont,
                                color = Color(0xFFFFB703)
                            )
                        }

                        // Streak Badge
                        if (profile.currentStreak >= 2) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x25EF4444))
                                    .border(0.8.dp, Color(0x66EF4444), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔥 ${profile.currentStreak}",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GameFont,
                                    color = Color(0xFFFF5252)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // XP Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF090D14))
                        .border(0.5.dp, Color(0xFF1E293B), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(profile.levelProgress.coerceIn(0.06f, 1f))
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFB703), Color(0xFFFFD166), Color(0xFF00E5FF))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${profile.currentXp} / ${profile.xpForNextLevel} XP",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = GameFont,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "PROFILE & HONORS ▶",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = GameFont,
                        color = Color(0xFFFFB703)
                    )
                }
            }
        }
    }
}

/**
 * Hero Game Mode Card: The dominant Arcade Match CTA.
 */
@Composable
private fun HeroGameModeCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale)
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFB703))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF22160C),
                        Color(0xFF1B1812),
                        Color(0xFF101726)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFB703).copy(alpha = glowAlpha),
                        Color(0xFFFFD166).copy(alpha = glowAlpha),
                        Color(0xFFFF9800).copy(alpha = glowAlpha)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Golden Knight Badge Shield
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFF9C4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_menu_play_ai),
                        contentDescription = "Play AI",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x35FFB703))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFFFB703)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        fontFamily = GameFont,
                        color = Color(0xFFF8FAFC)
                    )

                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        fontFamily = GameFont,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // Right Play Button Pill
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                        )
                    )
                    .border(1.dp, Color(0xFFFFF9C4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 15.sp,
                    color = Color(0xFF1E1304),
                    modifier = Modifier.offset(x = 1.dp)
                )
            }
        }
    }
}

/**
 * Secondary Game Mode Card for Local 2-Player and Daily Puzzle.
 */
@Composable
private fun SecondaryGameModeCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    Box(
        modifier = modifier
            .height(92.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF000000))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF141C2B), Color(0xFF0F1726))
                )
            )
            .border(1.2.dp, Color(0xFF26354E), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 22.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(0.8.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(5.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = GameFont,
                        color = badgeColor
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFont,
                    color = Color(0xFFF8FAFC)
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontFamily = GameFont,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Compact Game Hub Action Tile (Analysis, Themes, Honors, Academy).
 */
@Composable
private fun GameHubActionTile(
    title: String,
    subtitle: String,
    iconRes: Int? = null,
    icon: String? = null,
    accentColor: Color = Color(0xFFFFB703),
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    Box(
        modifier = modifier
            .height(58.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF111827))
            .border(1.dp, Color(0xFF222E42), RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(22.dp)
                )
            } else if (icon != null) {
                Text(text = icon, fontSize = 20.sp)
            }

            Column {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFont,
                    color = Color(0xFFF8FAFC)
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontFamily = GameFont,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
        }
    }
}
