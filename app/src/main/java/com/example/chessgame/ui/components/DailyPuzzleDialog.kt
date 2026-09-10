package com.example.chessgame.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.*
import com.example.chessgame.progression.DailyPuzzleRepository
import com.example.chessgame.progression.PlayerProgressionManager
import com.example.chessgame.theme.*

/**
 * Daily Tactical Chess Challenge Dialog.
 * Presents curated chess tactics with real board interaction, instant evaluation,
 * and XP rewards for mastery.
 */
@Composable
fun DailyPuzzleDialog(
    boardTheme: BoardTheme = ThemeRegistry.ARTISAN_WOOD,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onSolved: () -> Unit,
    onClose: () -> Unit
) {
    val puzzle = remember { DailyPuzzleRepository.getTodayPuzzle() }
    var gameState by remember { mutableStateOf(parseFEN(puzzle.fen)) }
    var isSolved by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var xpEarned by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0x99000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF241A13), Color(0xFF16100C), Color(0xFF0F0A07))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(StudyAmberAccent, StudyTableFrame, StudyAmberAccent)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "DAILY CHALLENGE",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.8.sp,
                                color = StudyParchmentCream
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33DFB36E))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = puzzle.theme.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyAmberAccent
                                )
                            }
                        }
                        Text(
                            text = puzzle.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFAFA293)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33DFB36E))
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                    }
                }

                // Description
                Text(
                    text = puzzle.description,
                    fontSize = 11.5.sp,
                    color = StudyParchmentCream,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Interactive Chess Board View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33DFB36E), RoundedCornerShape(12.dp))
                ) {
                    ChessBoardView(
                        state = gameState,
                        flipped = puzzle.playerColor == PieceColor.BLACK,
                        isInteractable = !isSolved,
                        showCoordinates = true,
                        highlightMoves = true,
                        boardTheme = boardTheme,
                        pieceTheme = pieceTheme,
                        lastMove = null,
                        onMove = { move ->
                            if (move.from == puzzle.fromSquare && move.to == puzzle.toSquare) {
                                // Correct Solution!
                                SoundManager.playCapture()
                                SoundManager.playVictory()
                                val nextState = makeMove(gameState, move)
                                gameState = nextState
                                isSolved = true
                                errorMessage = null
                                xpEarned = PlayerProgressionManager.recordPuzzleSolved()
                                onSolved()
                            } else {
                                // Incorrect move
                                SoundManager.playClick()
                                errorMessage = "Not the best tactical line. Try again!"
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Error message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC93B2B),
                        textAlign = TextAlign.Center
                    )
                }

                // Solved Celebration Banner
                AnimatedVisibility(visible = isSolved, enter = fadeIn()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x35588157))
                            .border(1.dp, Color(0x66588157), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉 PUZZLE SOLVED! +$xpEarned XP",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF8DA378)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = puzzle.explanation,
                            fontSize = 11.sp,
                            color = StudyParchmentCream,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Hint section
                if (showHint && !isSolved) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x25DFB36E))
                            .border(1.dp, Color(0x40DFB36E), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "💡 Clue: ${puzzle.hint}",
                            fontSize = 11.5.sp,
                            color = StudyAmberAccent,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isSolved) {
                        OutlinedButton(
                            onClick = { showHint = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = StudyAmberAccent
                            )
                        ) {
                            Text("💡 Hint", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                gameState = parseFEN(puzzle.fen)
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF38271D),
                                contentColor = StudyParchmentCream
                            )
                        ) {
                            Text("🔄 Reset", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = onClose,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudyAmberAccent,
                                contentColor = Color(0xFF1B1207)
                            )
                        ) {
                            Text("Claim Reward & Return", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
