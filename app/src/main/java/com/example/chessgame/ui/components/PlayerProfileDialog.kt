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
 * Game-Like Player Profile & Achievements Hub.
 * Features Level badge, Title, animated XP bar, lifetime stats, and achievement trophy cabinet.
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
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .shadow(28.dp, RoundedCornerShape(22.dp), spotColor = Color(0x99000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF221710), Color(0xFF160F0A), Color(0xFF0F0A07))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(StudyAmberAccent, StudyTableFrame, StudyAmberAccent)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
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
                            text = "PLAYER PROFILE",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = StudyParchmentCream
                        )
                        Text(
                            text = "Tactical record & master honors",
                            fontSize = 11.sp,
                            color = Color(0xFFAFA293)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x33DFB36E))
                            .border(1.dp, Color(0x55DFB36E), CircleShape)
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
                            color = StudyParchmentCream
                        )
                    }
                }

                // Player Identity Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2E2016), Color(0xFF1E140E))
                            )
                        )
                        .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Level Seal / Crown
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(StudyAmberAccent, Color(0xFF8B5E2B))
                                    )
                                )
                                .border(1.5.dp, Color(0xFFF7EFE4), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "LVL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF2A1B0E)
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
                                            focusedTextColor = StudyParchmentCream,
                                            unfocusedTextColor = StudyParchmentCream,
                                            focusedBorderColor = StudyAmberAccent,
                                            unfocusedBorderColor = Color(0x55DFB36E)
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "✓",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = StudyAmberAccent,
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
                                        color = StudyParchmentCream
                                    )
                                    Text(
                                        text = "✎",
                                        fontSize = 12.sp,
                                        color = StudyAmberAccent,
                                        modifier = Modifier.clickable { isEditingName = true }
                                    )
                                }
                            }

                            Text(
                                text = profile.title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudyAmberAccent,
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
                                        .background(Color(0xFF140D09))
                                        .border(0.8.dp, Color(0x35DFB36E), CircleShape)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(progressAnim)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFFE5A93C), Color(0xFFF3D58C))
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
                                        color = Color(0xFFD4C7B5)
                                    )
                                    Text(
                                        text = "${profile.xpForNextLevel} XP needed for Lvl ${profile.level + 1}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFA59786)
                                    )
                                }
                            }
                        }
                    }
                }

                // Sub-Tabs: [Overview & Stats] [Achievements (N/M)]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33100A06))
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
                            StatCard(title = "WINS", value = "${profile.wins}", color = Color(0xFF588157), modifier = Modifier.weight(1f))
                            StatCard(title = "LOSSES", value = "${profile.losses}", color = Color(0xFFC93B2B), modifier = Modifier.weight(1f))
                            StatCard(title = "DRAWS", value = "${profile.draws}", color = Color(0xFF8DA378), modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(title = "WIN RATE", value = "${profile.winRate}%", color = StudyAmberAccent, modifier = Modifier.weight(1f))
                            StatCard(title = "CURRENT STREAK", value = "${profile.currentStreak} 🔥", color = Color(0xFFE5A93C), modifier = Modifier.weight(1f))
                            StatCard(title = "BEST STREAK", value = "${profile.bestStreak}", color = Color(0xFFEBD18C), modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatCard(title = "TOTAL MATCHES", value = "${profile.totalGames}", color = StudyParchmentCream, modifier = Modifier.weight(1f))
                            StatCard(title = "PUZZLES SOLVED", value = "${profile.puzzlesSolved}", color = Color(0xFF26C6DA), modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Game Integrity Note
                        Text(
                            text = "All tactical progression is computed and stored offline.",
                            fontSize = 10.sp,
                            color = Color(0x66B8AEA0),
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
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) StudyAmberAccent else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = if (selected) Color(0xFF1E140B) else Color(0xFFAFA293)
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
            .background(Color(0x35140D09))
            .border(1.dp, Color(0x25DFB36E), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color(0xFFAFA293)
            )
            Spacer(modifier = Modifier.height(3.dp))
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUnlocked) Color(0x352E2016) else Color(0x1A140D09))
            .border(
                1.dp,
                if (isUnlocked) Color(0x40DFB36E) else Color(0x1A8C7D70),
                RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) Color(0x40DFB36E) else Color(0x22140D09))
                .border(1.dp, if (isUnlocked) StudyAmberAccent else Color(0x338C7D70), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement.badgeIcon,
                fontSize = 20.sp,
                color = if (isUnlocked) Color.Unspecified else Color.Gray
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) StudyParchmentCream else Color(0x88D4C7B5)
                )

                Text(
                    text = "+${achievement.xpReward} XP",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) StudyAmberAccent else Color(0x55DFB36E)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = achievement.description,
                fontSize = 11.sp,
                color = if (isUnlocked) Color(0xFFAFA293) else Color(0x55AFA293),
                lineHeight = 13.sp
            )

            if (isUnlocked && achievement.unlockedDate != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Unlocked on ${achievement.unlockedDate}",
                    fontSize = 9.sp,
                    color = Color(0xFF7E8B9B)
                )
            }
        }
    }
}
