package com.example.chessgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.GameOutcome
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.progression.XpGainSummary
import com.example.chessgame.theme.*

/**
 * Dramatic Tabletop Game-Ending Scene for Checkmate and Draw.
 * Features standing victor king and knocked-over defeated king,
 * XP progress reward breakdown, level-up milestones, scorecard, and rematch actions.
 */
@Composable
fun MatchResultOverlay(
    outcome: GameOutcome,
    humanColor: PieceColor,
    isAIMode: Boolean,
    opponentName: String,
    moveCount: Int,
    gameDurationSeconds: Int,
    xpSummary: XpGainSummary? = null,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onAnalyseGame: () -> Unit,
    onPlayAgain: () -> Unit,
    onReviewBoard: () -> Unit,
    onMainMenu: () -> Unit
) {
    val isWin = outcome.winner == humanColor
    val isLoss = outcome.winner != null && !isWin
    val isDraw = outcome.isDraw || outcome.winner == null

    val minutes = gameDurationSeconds / 60
    val seconds = gameDurationSeconds % 60
    val timeFormatted = "%02d:%02d".format(minutes, seconds)

    Dialog(onDismissRequest = onReviewBoard) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = if (isWin) Color(0xFFFFB703) else Color.Black)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF131C2D), Color(0xFF0C1320), Color(0xFF070A10))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        if (isWin) listOf(Color(0xFFFFD54F), Color(0xFFFFB703), Color(0xFFFF8F00))
                        else listOf(Color(0xFF26354E), Color(0xFF1A2638), Color(0xFF26354E))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Tabletop Scene: Victor King & Tilted Defeated King
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                ) {
                    if (isDraw) {
                        // Calm balanced kings
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            PieceView(
                                type = PieceType.KING,
                                color = PieceColor.WHITE,
                                pieceTheme = pieceTheme,
                                modifier = Modifier.size(56.dp)
                            )
                            PieceView(
                                type = PieceType.KING,
                                color = PieceColor.BLACK,
                                pieceTheme = pieceTheme,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    } else {
                        val winnerColor = outcome.winner ?: PieceColor.WHITE
                        val loserColor = winnerColor.opponent()

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Victor King Standing Tall
                            PieceView(
                                type = PieceType.KING,
                                color = winnerColor,
                                pieceTheme = pieceTheme,
                                modifier = Modifier.size(66.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Defeated King Knocked Sideways
                            PieceView(
                                type = PieceType.KING,
                                color = loserColor,
                                pieceTheme = pieceTheme,
                                modifier = Modifier
                                    .size(54.dp)
                                    .rotate(75f)
                                    .offset(y = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Result Title
                Text(
                    text = if (isDraw) "STALEMATE / DRAW" else if (isWin) "VICTORY!" else "DEFEAT",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    fontFamily = GameFont,
                    color = if (isWin) Color(0xFFFFB703) else if (isDraw) Color(0xFF38BDF8) else Color(0xFFFF3366)
                )

                Text(
                    text = when {
                        isWin -> "CHECKMATE • YOU HAVE CLAIMED THE BOARD"
                        isLoss -> "CHECKMATE • THE ENGINE HAS PREVAILED"
                        else -> "PEACEFUL CONCLUSION • DRAW"
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GameFont,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // XP Progress & Reward Box
                if (xpSummary != null && xpSummary.totalXpGained > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1A2638), Color(0xFF101726))
                                )
                            )
                            .border(1.dp, Color(0xFF2C3C56), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "⚡", fontSize = 14.sp)
                                    Text(
                                        text = "+${xpSummary.totalXpGained} XP EARNED",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = GameFont,
                                        color = Color(0xFFFFB703)
                                    )
                                }

                                if (xpSummary.didLevelUp) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFFB703))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "LEVEL UP! LVL ${xpSummary.newLevel}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = GameFont,
                                            color = Color(0xFF1B1107)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Level ${xpSummary.newLevel}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = GameFont,
                                        color = Color(0xFFF8FAFC)
                                    )
                                }
                            }

                            // XP breakdown text
                            val details = mutableListOf<String>()
                            if (xpSummary.matchXp > 0) details.add("Match: +${xpSummary.matchXp}")
                            if (xpSummary.difficultyBonus > 0) details.add("Difficulty: +${xpSummary.difficultyBonus}")
                            if (xpSummary.streakBonus > 0) details.add("Streak: +${xpSummary.streakBonus}")
                            if (xpSummary.achievementsUnlocked.isNotEmpty()) {
                                details.add("Achievements: +${xpSummary.achievementsUnlocked.sumOf { it.xpReward }}")
                            }

                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = details.joinToString(" • "),
                                    fontSize = 9.5.sp,
                                    fontFamily = GameFont,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Achievements Unlocked Banner
                if (xpSummary != null && xpSummary.achievementsUnlocked.isNotEmpty()) {
                    val ach = xpSummary.achievementsUnlocked.first()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x25FFB703))
                            .border(1.dp, Color(0x66FFB703), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = ach.badgeIcon, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "ACHIEVEMENT UNLOCKED: ${ach.title.uppercase()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = GameFont,
                                    color = Color(0xFFFFB703)
                                )
                                Text(
                                    text = ach.description,
                                    fontSize = 9.5.sp,
                                    fontFamily = GameFont,
                                    color = Color(0xFFF8FAFC)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Scorecard Stats Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0D1420))
                        .border(1.dp, Color(0xFF1E2B3E), RoundedCornerShape(12.dp))
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = GameFont, color = Color(0xFF64748B))
                        Text(text = "$moveCount", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = GameFont, color = Color(0xFFF8FAFC))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "DURATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = GameFont, color = Color(0xFF64748B))
                        Text(text = timeFormatted, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = GameFont, color = Color(0xFFF8FAFC))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "OPPONENT", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = GameFont, color = Color(0xFF64748B))
                        Text(text = opponentName, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = GameFont, color = Color(0xFFFFB703))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Action: PLAY AGAIN
                Button(
                    onClick = {
                        SoundManager.playClick()
                        onPlayAgain()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔄 ", fontSize = 14.sp)
                        Text(
                            text = "PLAY AGAIN",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontFamily = GameFont,
                            color = Color(0xFF1B140E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Action: ANALYSE GAME
                Button(
                    onClick = {
                        SoundManager.playClick()
                        onAnalyseGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162133)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔍 ", fontSize = 13.sp)
                        Text(
                            text = "ANALYSE GAME",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Review Board & Main Menu buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            SoundManager.playClick()
                            onReviewBoard()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Review Board", fontSize = 11.5.sp, fontFamily = GameFont, color = Color(0xFF94A3B8))
                    }

                    OutlinedButton(
                        onClick = {
                            SoundManager.playClick()
                            onMainMenu()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Main Menu", fontSize = 11.5.sp, fontFamily = GameFont, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}

