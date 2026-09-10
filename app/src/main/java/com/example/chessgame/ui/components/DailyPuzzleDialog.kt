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
import androidx.compose.ui.text.font.FontFamily
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
 * Grandmaster Arena: Daily Tactical Mission Dialog.
 * Interactive puzzle board with instant tactical evaluation, clue extraction,
 * and XP rank progression rewards upon completion.
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
                .padding(18.dp)
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
                                text = "DAILY MISSION",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Serif,
                                color = ArenaColors.CyberCyan
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ArenaColors.SolarAmber.copy(alpha = 0.2f))
                                    .border(0.8.dp, ArenaColors.SolarAmber, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = puzzle.theme.uppercase(),
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ArenaColors.SolarAmber
                                )
                            }
                        }
                        Text(
                            text = puzzle.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ArenaColors.TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .border(1.dp, ArenaColors.TitaniumBorder, CircleShape)
                            .clickable {
                                SoundManager.playClick()
                                onClose()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                    }
                }

                // Description
                Text(
                    text = puzzle.description,
                    fontSize = 11.5.sp,
                    color = ArenaColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Interactive Chess Board View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(20.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF000000))
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(14.dp))
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
                                errorMessage = "Suboptimal move. Telemetry rejected."
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
                        color = ArenaColors.CrimsonAlert,
                        textAlign = TextAlign.Center
                    )
                }

                // Solved Celebration Banner
                AnimatedVisibility(visible = isSolved, enter = fadeIn()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x3500E676))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ TACTICAL OBJECTIVE SECURED! +$xpEarned XP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFF69F0AE)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = puzzle.explanation,
                            fontSize = 11.sp,
                            color = ArenaColors.TextPrimary,
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
                            .background(ArenaColors.CyberCyan.copy(alpha = 0.15f))
                            .border(1.dp, ArenaColors.CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "💡 Tactical Clue: ${puzzle.hint}",
                            fontSize = 11.5.sp,
                            color = ArenaColors.CyberCyan,
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
                        ArenaPill(
                            icon = "💡",
                            label = "Tactical Clue",
                            onClick = {
                                SoundManager.playClick()
                                showHint = true
                            },
                            modifier = Modifier.weight(1f)
                        )

                        ArenaPill(
                            icon = "🔄",
                            label = "Reset Board",
                            onClick = {
                                SoundManager.playClick()
                                gameState = parseFEN(puzzle.fen)
                                errorMessage = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        ArenaButton(
                            text = "CLAIM REWARD & RETURN",
                            icon = "⚡",
                            isPrimary = true,
                            onClick = {
                                SoundManager.playClick()
                                onClose()
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        )
                    }
                }
            }
        }
    }
}
