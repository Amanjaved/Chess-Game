package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
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
 * Screen 2: Game Home / Arena Lobby
 * "Grandmaster Arena" Competitive Game Lobby:
 * - Top Player Command HUD: Avatar, Level, XP Gauge, Rank Title, Win Rate %, Streak Flame
 * - Hero Battle Arena Card: Dominant "PLAY VS AI" Primary Action with cyber cyan glow
 * - Game Modes Grid: "PASS & PLAY" (The Duel) and "DAILY TACTICAL MISSION" (Daily Puzzle)
 * - Tactical Action Dock: Armory (Themes), Debrief (Analysis), Academy (Tutorial), System (Settings)
 * - Safe area padding (WindowInsets.systemBars)
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

    // Smooth staggered entrance animations
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer { alpha = entranceAlpha.value },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ==========================================
                // 1. TOP PLAYER COMMAND HUD
                // ==========================================
                ArenaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    isHighlighted = false,
                    onClick = {
                        showProfileDialog = true
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar + Level + Name + Title
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF16202E))
                                    .border(1.2.dp, ArenaColors.CyberCyan, RoundedCornerShape(10.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_game_emblem),
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = profile.playerName,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ArenaColors.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    ArenaBadge(
                                        text = "LVL ${profile.level}",
                                        color = ArenaColors.SolarAmber,
                                        fontSize = 9
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = profile.title.uppercase(),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ArenaColors.CyberCyan
                                )
                            }
                        }

                        // Combat Stats (Win Rate & Streak)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (profile.currentStreak > 0) {
                                ArenaBadge(
                                    text = "🔥 ${profile.currentStreak}",
                                    color = ArenaColors.CrimsonAlert,
                                    fontSize = 10
                                )
                            }
                            ArenaBadge(
                                text = "${profile.winRate}% WIN",
                                color = ArenaColors.EmeraldVictory,
                                fontSize = 10
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 2. HERO BATTLE ARENA CTA (PRIMARY ACTION)
                // ==========================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(heroScale.value)
                        .shadow(16.dp, RoundedCornerShape(18.dp), spotColor = ArenaColors.CyberCyan.copy(alpha = 0.45f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1E2C3D),
                                    Color(0xFF141E2B)
                                )
                            )
                        )
                        .border(1.8.dp, ArenaColors.CyberCyan, RoundedCornerShape(18.dp))
                        .clickable {
                            SoundManager.playClick()
                            onPlayAI()
                        }
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ArenaColors.CyberCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PRIMARY BATTLE ARENA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.8.sp,
                                    color = ArenaColors.CyberCyan
                                )
                            }

                            ArenaBadge(
                                text = "4 TIERS",
                                color = ArenaColors.SolarAmber,
                                fontSize = 9
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PLAY VS AI",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = ArenaColors.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Duel tactical AI from Recruit initiate to Grandmaster Oracle.",
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp,
                                    color = ArenaColors.TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Large Glowing Emblem Thumbnail
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x3300E5FF))
                                    .border(1.dp, ArenaColors.CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_game_emblem),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Big Tactical Deploy Button
                        ArenaButton(
                            text = "COMMENCE BATTLE",
                            icon = "⚔️",
                            onClick = onPlayAI,
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==========================================
                // 3. GAME MODES ROW (LOCAL 2P & DAILY MISSION)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Mode 1: Pass & Play (Local Duel)
                    ArenaCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        onClick = onPlayLocal
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "👥", fontSize = 18.sp)
                                ArenaBadge(text = "OFFLINE", color = ArenaColors.TextSecondary, fontSize = 8)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "PASS & PLAY",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = ArenaColors.TextPrimary
                            )
                            Text(
                                text = "2 Players • 1 Device",
                                fontSize = 10.sp,
                                color = ArenaColors.TextSecondary
                            )
                        }
                    }

                    // Mode 2: Daily Tactical Mission
                    ArenaCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        isHighlighted = true,
                        highlightColor = ArenaColors.SolarAmber,
                        onClick = {
                            showDailyChallengeDialog = true
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎯", fontSize = 18.sp)
                                ArenaBadge(text = "+150 XP", color = ArenaColors.SolarAmber, fontSize = 8)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "DAILY PUZZLE",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = ArenaColors.TextPrimary
                            )
                            Text(
                                text = "Tactical Checkmate",
                                fontSize = 10.sp,
                                color = ArenaColors.SolarAmber
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==========================================
                // 4. TACTICAL NAVIGATION DOCK (BOTTOM ACTIONS)
                // [ 🛡️ ARMORY ] [ 📊 DEBRIEF ] [ 📜 ACADEMY ] [ ⚙️ SYSTEM ]
                // ==========================================
                ArenaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TacticalDockItem(
                            icon = "🛡️",
                            label = "ARMORY",
                            onClick = onOpenThemes
                        )
                        TacticalDockItem(
                            icon = "📊",
                            label = "DEBRIEF",
                            onClick = onOpenAnalysis
                        )
                        TacticalDockItem(
                            icon = "📜",
                            label = "ACADEMY",
                            onClick = onOpenHowToPlay
                        )
                        TacticalDockItem(
                            icon = "⚙️",
                            label = "SYSTEM",
                            onClick = onOpenSettings
                        )
                    }
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
 * Individual icon + label button in the bottom tactical dock.
 */
@Composable
private fun TacticalDockItem(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                SoundManager.playClick()
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            color = ArenaColors.TextSecondary
        )
    }
}
