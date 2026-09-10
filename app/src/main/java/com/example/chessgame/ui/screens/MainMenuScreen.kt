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
 * Screen 2: Game Home Screen
 * Redesigned from a standard menu into a real Game Home Screen:
 * - Top Player Profile Header: Avatar, Level Badge, XP progress bar, Quick stats (Wins, Win Rate %, Streak)
 * - Main Play Area: Visually dominant Primary CTA (Play vs AI), Local 2 Player, and Daily Tactical Challenge
 * - Game Hub: Analyse Game, Theme Room, Honors & Achievements, How to Play, Settings
 * - Full Android safe area insets handling (WindowInsets.systemBars)
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

    // Entrance animations
    val bgAlpha = remember { Animatable(0f) }
    val heroAlpha = remember { Animatable(0f) }
    val heroScale = remember { Animatable(0.96f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { bgAlpha.animateTo(1f, animationSpec = tween(400)) }
        launch { heroAlpha.animateTo(1f, animationSpec = tween(500)) }
        launch { heroScale.animateTo(1f, animationSpec = tween(550, easing = FastOutSlowInEasing)) }
        launch {
            delay(120)
            contentAlpha.animateTo(1f, animationSpec = tween(450))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080A0D))
    ) {
        // LAYER 1: Atmospheric Room Background
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = "Study Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = bgAlpha.value }
        )

        // LAYER 1.5: Radial Darkening Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xCC07090C),
                            Color(0x7506080A),
                            Color(0x20050709)
                        ),
                        radius = 1250f
                    )
                )
        )

        // LAYER 2: Native Compose UI with safe area insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ==========================================
                // 1. TOP SECTION: PLAYER IDENTITY & MOTTO
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Player Profile Bar
                    PlayerHomeProfileCard(
                        profile = profile,
                        onClick = {
                            SoundManager.playClick()
                            showProfileDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtle Motto Quotes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "SAME GAME.\nNEW STORIES.",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.4.sp,
                                lineHeight = 10.sp,
                                color = Color(0x99B8AEA0)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(1.dp)
                                    .background(Color(0x40DFB36E))
                            )
                        }

                        // Compact Center Knight Branding
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_menu_knight),
                                contentDescription = "Chess Knight",
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "CHESS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp,
                                color = Color(0xFFF3E2C4),
                                fontFamily = FontFamily.Serif
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "DISCIPLINE\nCREATES FREEDOM.",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.4.sp,
                                lineHeight = 10.sp,
                                color = Color(0x99B8AEA0),
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(1.dp)
                                    .background(Color(0x40DFB36E))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 2. MAIN PLAY AREA (Dominant Primary CTA)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .graphicsLayer {
                            alpha = heroAlpha.value
                            scaleX = heroScale.value
                            scaleY = heroScale.value
                        },
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // HERO CTA: PLAY VS AI (Visually dominant, glowing gold/amber gradient)
                    HeroPlayCard(
                        title = "PLAY VS AI",
                        subtitle = "Tactical Match • 4 Engine Difficulties",
                        badge = "PRIMARY MATCH",
                        onClick = {
                            SoundManager.playClick()
                            onPlayAI()
                        }
                    )

                    // Secondary Play Row: Local 2 Player & Daily Challenge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Local 2 Player Card
                        SubPlayCard(
                            title = "Local 2 Player",
                            subtitle = "One board. Two minds.",
                            badge = "PASS & PLAY",
                            icon = "♟️",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onPlayLocal()
                            }
                        )

                        // Daily Challenge Card
                        SubPlayCard(
                            title = "Daily Puzzle",
                            subtitle = "Tactical mate in 1 or 2",
                            badge = "+75 XP",
                            badgeColor = Color(0xFF26C6DA),
                            icon = "🧩",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                showDailyChallengeDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==========================================
                // 3. GAME HUB UTILITIES (Compact 2x2 Grid + Full Settings)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .graphicsLayer { alpha = contentAlpha.value },
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameHubPill(
                            title = "Analyse Game",
                            subtitle = "Accuracy & mistakes",
                            iconRes = R.drawable.ic_menu_analyse,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onOpenAnalysis()
                            }
                        )

                        GameHubPill(
                            title = "Theme Vault",
                            subtitle = "Boards & pieces",
                            iconRes = R.drawable.ic_menu_themes,
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
                        GameHubPill(
                            title = "Honors & Stats",
                            subtitle = "${profile.achievements.count { it.isUnlocked }} of ${profile.achievements.size} trophies",
                            icon = "🏆",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                showProfileDialog = true
                            }
                        )

                        GameHubPill(
                            title = "How to Play",
                            subtitle = "Illustrated rules",
                            iconRes = R.drawable.ic_menu_how_to_play,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                onOpenHowToPlay()
                            }
                        )
                    }

                    // Settings Quick Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x35160F0B))
                            .border(1.dp, Color(0x25DFB36E), RoundedCornerShape(12.dp))
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_menu_settings),
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Game Settings & Audio",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StudyParchmentCream
                                )
                            }
                            Text(
                                text = "Configure →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyAmberAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ==========================================
                // 4. FOOTER STATUS
                // ==========================================
                Text(
                    text = "100% OFFLINE  •  FIDE RULES  •  LOCAL PROGRESSION",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.8.sp,
                    color = Color(0x77D5C7B2),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        // ==========================================
        // DIALOGS
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
 * Top Player Profile Card displaying Avatar, Level, XP bar, and Quick Stats.
 */
@Composable
private fun PlayerHomeProfileCard(
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
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xDD2A1C12), Color(0xDD1A110B))
                )
            )
            .border(1.2.dp, Color(0x45DFB36E), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Level Plinth Seal
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(StudyAmberAccent, Color(0xFF8B5E2B))
                        )
                    )
                    .border(1.dp, Color(0xFFF7EFE4), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LVL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color(0xFF2A1B0E)
                    )
                    Text(
                        text = "%02d".format(profile.level),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF140D07)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyParchmentCream
                        )
                        Text(
                            text = profile.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = StudyAmberAccent
                        )
                    }

                    // Quick Stats Pills (Wins & Win Rate)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x35588157))
                                .border(0.8.dp, Color(0x66588157), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${profile.wins}W",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8DA378)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x35DFB36E))
                                .border(0.8.dp, Color(0x66DFB36E), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${profile.winRate}%",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyAmberAccent
                            )
                        }

                        if (profile.currentStreak >= 2) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x35E5A93C))
                                    .border(0.8.dp, Color(0x66E5A93C), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${profile.currentStreak}🔥",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE5A93C)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                // XP Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF140D09))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(profile.levelProgress.coerceIn(0.05f, 1f))
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFDFB36E), Color(0xFFF3D58C))
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${profile.currentXp} / ${profile.xpForNextLevel} XP",
                        fontSize = 9.sp,
                        color = Color(0xFF9E8F7F)
                    )
                    Text(
                        text = "Profile & Honors ➔",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudyAmberAccent
                    )
                }
            }
        }
    }
}

/**
 * Hero Play Card: The visually dominant CTA for starting a match.
 */
@Composable
private fun HeroPlayCard(
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    // Subtle pulsing amber border glow
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .scale(scale)
            .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = Color(0xAA000000))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF382516),
                        Color(0xFF28180E),
                        Color(0xFF1E1008)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        StudyAmberAccent.copy(alpha = glowAlpha),
                        Color(0xFF946830).copy(alpha = glowAlpha),
                        StudyAmberAccent.copy(alpha = glowAlpha)
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
                // Golden Knight Play Icon Shield
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(StudyAmberAccent, Color(0xFF8A5D29))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFF2D6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_menu_play_ai),
                        contentDescription = "Play AI",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x35DFB36E))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = StudyAmberAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = StudyParchmentCream
                    )

                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = Color(0xFFC7B7A5)
                    )
                }
            }

            // Right Play Arrow Pill
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(StudyAmberAccent)
                    .border(1.dp, Color(0xFFFFF2D6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶",
                    fontSize = 14.sp,
                    color = Color(0xFF1B1107),
                    modifier = Modifier.offset(x = 1.dp)
                )
            }
        }
    }
}

/**
 * Sub Play Card for Local 2 Player and Daily Challenge.
 */
@Composable
private fun SubPlayCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color = StudyAmberAccent,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    Box(
        modifier = modifier
            .height(88.dp)
            .scale(scale)
            .shadow(6.dp, RoundedCornerShape(16.dp), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xEE241911), Color(0xEE160F09))
                )
            )
            .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(10.dp),
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
                Text(text = icon, fontSize = 20.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyParchmentCream
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = Color(0xFFAFA293),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Compact Game Hub Pill Card.
 */
@Composable
private fun GameHubPill(
    title: String,
    subtitle: String,
    iconRes: Int? = null,
    icon: String? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "press")

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x301E140D))
            .border(1.dp, Color(0x28DFB36E), RoundedCornerShape(12.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(22.dp)
                )
            } else if (icon != null) {
                Text(text = icon, fontSize = 18.sp)
            }

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyParchmentCream
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = Color(0xFFA59786),
                    maxLines = 1
                )
            }
        }
    }
}
