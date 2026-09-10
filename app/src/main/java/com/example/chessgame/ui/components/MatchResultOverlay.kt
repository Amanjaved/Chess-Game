package com.example.chessgame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.GameOutcome
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.progression.XpGainSummary
import com.example.chessgame.theme.*

/**
 * Grandmaster Arena Match Result Screen.
 * Displays triumphant golden victory crest or defeat insignia, victor king vs fallen king confrontation,
 * live XP rewards with level-up notifications, match stats, and tactical debrief actions.
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

    val outcomeColor = when {
        isWin -> ArenaColors.SolarAmber
        isLoss -> ArenaColors.CrimsonAlert
        else -> ArenaColors.CyberCyan
    }

    Dialog(onDismissRequest = onReviewBoard) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f)
                .shadow(32.dp, RoundedCornerShape(24.dp), spotColor = Color(0xDD000000))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF131822),
                            Color(0xFF0C0F16),
                            Color(0xFF07090E)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(outcomeColor.copy(alpha = 0.8f), ArenaColors.TitaniumBorder, outcomeColor.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Victory Banner / Laurel Crest
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isWin) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(1.5.dp, ArenaColors.SolarAmber, RoundedCornerShape(18.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner_victory_crest),
                                contentDescription = "Victory Crest",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Two Kings Confrontation Scene
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                    ) {
                        if (isDraw) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PieceView(
                                    type = PieceType.KING,
                                    color = PieceColor.WHITE,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.size(52.dp)
                                )
                                PieceView(
                                    type = PieceType.KING,
                                    color = PieceColor.BLACK,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.size(52.dp)
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
                                // Victor King Standing
                                PieceView(
                                    type = PieceType.KING,
                                    color = winnerColor,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.size(62.dp)
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                // Defeated King Tilted
                                PieceView(
                                    type = PieceType.KING,
                                    color = loserColor,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .rotate(75f)
                                        .offset(y = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Result Title
                    Text(
                        text = when {
                            isWin -> "VICTORY"
                            isLoss -> "DEFEAT"
                            else -> "STALEMATE"
                        },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        fontFamily = FontFamily.Serif,
                        color = outcomeColor
                    )

                    Text(
                        text = when {
                            isWin -> "ARENA SECURED • CHECKMATE"
                            isLoss -> "OPPONENT HAS PREVAILED • CHECKMATE"
                            else -> "HONORABLE DRAW • COMBAT RESOLVED"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = ArenaColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progression & XP Reward Box
                if (xpSummary != null && xpSummary.totalXpGained > 0) {
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        isHighlighted = true,
                        highlightColor = ArenaColors.SolarAmber
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
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
                                        letterSpacing = 1.sp,
                                        color = ArenaColors.SolarAmber
                                    )
                                }

                                if (xpSummary.didLevelUp) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ArenaColors.SolarAmber)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "LEVEL UP! LVL ${xpSummary.newLevel}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF090C12)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Rank ${xpSummary.newLevel}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.TextPrimary
                                    )
                                }
                            }

                            // XP breakdown text
                            val details = mutableListOf<String>()
                            if (xpSummary.matchXp > 0) details.add("Match +${xpSummary.matchXp}")
                            if (xpSummary.difficultyBonus > 0) details.add("Difficulty +${xpSummary.difficultyBonus}")
                            if (xpSummary.streakBonus > 0) details.add("Streak +${xpSummary.streakBonus}")
                            if (xpSummary.achievementsUnlocked.isNotEmpty()) {
                                details.add("Honors +${xpSummary.achievementsUnlocked.sumOf { it.xpReward }}")
                            }

                            if (details.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = details.joinToString(" • "),
                                    fontSize = 10.sp,
                                    color = ArenaColors.TextMuted
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Match Statistics Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TURNS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted)
                        Text(text = "$moveCount", fontSize = 15.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "DURATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted)
                        Text(text = timeFormatted, fontSize = 15.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "ADVERSARY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted)
                        Text(text = opponentName.take(12), fontSize = 12.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tactical Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Play Again / Rematch
                    ArenaButton(
                        text = "DEPLOY AGAIN",
                        icon = "⚔️",
                        isPrimary = true,
                        onClick = {
                            SoundManager.playClick()
                            onPlayAgain()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    // Tactical Analysis
                    ArenaButton(
                        text = "DEBRIEF & ANALYSE",
                        icon = "🔍",
                        isPrimary = false,
                        onClick = {
                            SoundManager.playClick()
                            onAnalyseGame()
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    )

                    // Bottom dual buttons: Review Arena & Command Dock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ArenaPill(
                            label = "Review Arena",
                            icon = "👁️",
                            onClick = {
                                SoundManager.playClick()
                                onReviewBoard()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ArenaPill(
                            label = "Command Dock",
                            icon = "🏛️",
                            onClick = {
                                SoundManager.playClick()
                                onMainMenu()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
