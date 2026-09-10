package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.GameOutcome
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*

/**
 * Dramatic Tabletop Game-Ending Scene for Checkmate and Draw.
 * Features standing victor king and knocked-over defeated king,
 * warm dramatic table lighting, stats scorecard, and rematch options.
 */
@Composable
fun MatchResultOverlay(
    outcome: GameOutcome,
    humanColor: PieceColor,
    isAIMode: Boolean,
    opponentName: String,
    moveCount: Int,
    gameDurationSeconds: Int,
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
                .fillMaxWidth(0.94f)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0xCC000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF281C13), Color(0xFF19110B), Color(0xFF0F0A06))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(StudyAmberAccent, StudyTableFrame, StudyAmberAccent)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(22.dp)
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
                        .height(100.dp)
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
                                modifier = Modifier.size(64.dp)
                            )
                            PieceView(
                                type = PieceType.KING,
                                color = PieceColor.BLACK,
                                pieceTheme = pieceTheme,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    } else {
                        val winnerColor = outcome.winner
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
                                modifier = Modifier.size(72.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Defeated King Knocked Sideways
                            PieceView(
                                type = PieceType.KING,
                                color = loserColor,
                                pieceTheme = pieceTheme,
                                modifier = Modifier
                                    .size(60.dp)
                                    .rotate(75f)
                                    .offset(y = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Result Title
                Text(
                    text = if (isDraw) "DRAW" else "CHECKMATE",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = StudyParchmentCream
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Subtitle
                Text(
                    text = when {
                        isWin -> "YOU HAVE CLAIMED THE BOARD"
                        isLoss -> "THE OPPONENT HAS PREVAILED"
                        else -> "Neither side could claim the board."
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyAmberAccent
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Physical Scorecard Stats Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudyParchmentCream)
                        .border(1.dp, StudyParchmentBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StudyParchmentInkFaded)
                        Text(text = "$moveCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StudyParchmentInk)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StudyParchmentInkFaded)
                        Text(text = timeFormatted, fontSize = 16.sp, fontWeight = FontWeight.Black, color = StudyParchmentInk)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "OPPONENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StudyParchmentInkFaded)
                        Text(text = opponentName, fontSize = 13.sp, fontWeight = FontWeight.Black, color = StudyParchmentInk)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Performance & Engine Analysis Teaser Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22D4A373))
                        .border(1.dp, Color(0x40D4A373), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📊", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GAME ANALYSIS READY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyAmberAccent
                        )
                        Text(
                            text = "Review accuracy, mistakes, and key turning moments.",
                            fontSize = 10.sp,
                            color = StudyParchmentCream.copy(alpha = 0.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Action: ANALYSE GAME
                Button(
                    onClick = {
                        SoundManager.playClick()
                        onAnalyseGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔍 ", fontSize = 14.sp)
                        Text(
                            text = "ANALYSE GAME",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF1B140E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary Action: PLAY AGAIN
                Button(
                    onClick = {
                        SoundManager.playClick()
                        onPlayAgain()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38291F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44D4A373)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(
                        text = "PLAY AGAIN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = StudyParchmentCream
                    )
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
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Review Board", fontSize = 12.sp, color = StudyParchmentCream)
                    }

                    OutlinedButton(
                        onClick = {
                            SoundManager.playClick()
                            onMainMenu()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Main Menu", fontSize = 12.sp, color = StudyParchmentCream)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom quote matching Screens 10 & 11
                val quote = when {
                    isWin -> "“Every master was once a beginner.”"
                    isDraw -> "“A peaceful end to a fierce battle.”"
                    else -> "“Defeat is merely the first lesson in victory.”"
                }
                Text(
                    text = quote,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFFAFA293),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
