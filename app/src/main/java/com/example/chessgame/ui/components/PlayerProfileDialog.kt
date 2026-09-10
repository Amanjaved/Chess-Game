package com.example.chessgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.progression.Achievement
import com.example.chessgame.progression.PlayerProfile
import com.example.chessgame.progression.PlayerProgressionManager
import com.example.chessgame.theme.*

/**
 * Grandmaster Arena: Player Profile & Tactical Honors Hub.
 * Features Rank badge, Military Title, animated XP telemetry, combat statistics, and achievement honors cabinet.
 */
@Composable
fun PlayerProfileDialog(
    profile: PlayerProfile,
    onProfileUpdated: () -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Stats, 1: Achievements
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(profile.playerName) }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.86f)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0xDD000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF141A25),
                            Color(0xFF0C1018),
                            Color(0xFF070A0F)
                        )
                    )
                )
                .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMMANDER DOSSIER",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "Tactical record & master achievements",
                            fontSize = 11.sp,
                            color = ArenaColors.TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, ArenaColors.TitaniumBorder, CircleShape)
                            .clickable {
                                SoundManager.playClick()
                                onClose()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArenaColors.TextPrimary
                        )
                    }
                }

                // Player Identity Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Level / Rank Shield
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(ArenaColors.SolarAmber, Color(0xFF8B5E2B))
                                    )
                                )
                                .border(1.5.dp, ArenaColors.SolarAmber, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "RANK",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF140D07)
                                )
                                Text(
                                    text = "${profile.level}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF140D07)
                                )
                            }
                        }

                        // Player Name, Title & XP Bar
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isEditingName) {
                                    OutlinedTextField(
                                        value = editedName,
                                        onValueChange = { editedName = it.take(20) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = ArenaColors.TextPrimary,
                                            unfocusedTextColor = ArenaColors.TextPrimary,
                                            focusedBorderColor = ArenaColors.CyberCyan,
                                            unfocusedBorderColor = ArenaColors.TitaniumBorder
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "✓",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ArenaColors.CyberCyan,
                                        modifier = Modifier.clickable {
                                            if (editedName.isNotBlank()) {
                                                PlayerProgressionManager.setPlayerName(editedName)
                                                onProfileUpdated()
                                            }
                                            isEditingName = false
                                        }
                                    )
                                } else {
                                    Text(
                                        text = profile.playerName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.TextPrimary
                                    )
                                    Text(
                                        text = "✎",
                                        fontSize = 12.sp,
                                        color = ArenaColors.CyberCyan,
                                        modifier = Modifier.clickable { isEditingName = true }
                                    )
                                }
                            }

                            Text(
                                text = profile.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ArenaColors.SolarAmber,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // XP Progress Bar
                            val progressAnim by animateFloatAsState(
                                targetValue = profile.levelProgress,
                                animationSpec = tween(600),
                                label = "xp"
                            )

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF090D14))
                                        .border(0.8.dp, ArenaColors.TitaniumBorder, CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(progressAnim)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(ArenaColors.CyberCyan, ArenaColors.SolarAmber)
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
                                        text = "${profile.currentXp} XP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.CyberCyan
                                    )
                                    Text(
                                        text = "${profile.xpForNextLevel} XP needed for Rank ${profile.level + 1}",
                                        fontSize = 10.sp,
                                        color = ArenaColors.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Sub-Tabs: [STATISTICS] [HONORS (N/M)]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val unlockedCount = profile.achievements.count { it.isUnlocked }

                    TabPill(
                        label = "STATISTICS",
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        label = "HONORS ($unlockedCount/${profile.achievements.size})",
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Tab Content
                if (selectedTab == 0) {
                    // Statistics Grid
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(title = "VICTORIES", value = "${profile.wins}", color = Color(0xFF00E676), modifier = Modifier.weight(1f))
                            StatCard(title = "DEFEATS", value = "${profile.losses}", color = ArenaColors.CrimsonAlert, modifier = Modifier.weight(1f))
                            StatCard(title = "STALEMATES", value = "${profile.draws}", color = ArenaColors.CyberCyan, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(title = "WIN RATE", value = "${profile.winRate}%", color = ArenaColors.SolarAmber, modifier = Modifier.weight(1f))
                            StatCard(title = "STREAK", value = "${profile.currentStreak} 🔥", color = ArenaColors.SolarAmber, modifier = Modifier.weight(1f))
                            StatCard(title = "BEST STREAK", value = "${profile.bestStreak}", color = ArenaColors.CyberCyan, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(title = "TOTAL ENGAGEMENTS", value = "${profile.totalGames}", color = ArenaColors.TextPrimary, modifier = Modifier.weight(1f))
                            StatCard(title = "MISSIONS SOLVED", value = "${profile.puzzlesSolved}", color = ArenaColors.CyberCyan, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Game Integrity Note
                        Text(
                            text = "All tactical progression is computed and stored offline.",
                            fontSize = 10.sp,
                            color = ArenaColors.TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // Achievements List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(profile.achievements) { ach ->
                            AchievementCard(achievement = ach)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) ArenaColors.CyberCyan else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            color = if (selected) Color(0xFF070B10) else ArenaColors.TextSecondary
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ArenaColors.TitaniumSurface)
            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = ArenaColors.TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
private fun AchievementCard(achievement: Achievement) {
    val isUnlocked = achievement.isUnlocked
    val cardBorder = if (isUnlocked) ArenaColors.SolarAmber.copy(alpha = 0.5f) else ArenaColors.TitaniumBorder
    val bg = if (isUnlocked) ArenaColors.TitaniumSurface else Color(0xFF0B1017)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) ArenaColors.SolarAmber.copy(alpha = 0.2f) else Color(0x15FFFFFF))
                .border(1.dp, if (isUnlocked) ArenaColors.SolarAmber else ArenaColors.TitaniumBorder, CircleShape)
        ) {
            Text(
                text = if (isUnlocked) achievement.badgeIcon else "🔒",
                fontSize = 18.sp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = if (isUnlocked) ArenaColors.SolarAmber else ArenaColors.TextSecondary
            )
            Text(
                text = achievement.description,
                fontSize = 10.5.sp,
                color = if (isUnlocked) ArenaColors.TextPrimary else ArenaColors.TextMuted,
                lineHeight = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (isUnlocked) ArenaColors.CyberCyan.copy(alpha = 0.2f) else Color(0x15FFFFFF))
                .border(0.8.dp, if (isUnlocked) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text = "+${achievement.xpReward} XP",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) ArenaColors.CyberCyan else ArenaColors.TextMuted
            )
        }
    }
}
