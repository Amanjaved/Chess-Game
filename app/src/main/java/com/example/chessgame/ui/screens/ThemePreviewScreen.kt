package com.example.chessgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.GameState
import com.example.chessgame.engine.Move
import com.example.chessgame.engine.createInitialGameState
import com.example.chessgame.engine.makeMove
import com.example.chessgame.theme.BoardTheme
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.StudyAmberAccent
import com.example.chessgame.theme.StudyParchmentCream
import com.example.chessgame.ui.components.ChessBoardView

/**
 * Full-screen interactive Theme Preview.
 * Displays an active 8x8 board showcasing how the selected BoardTheme
 * and PieceTheme look and feel together in realistic tabletop conditions.
 */
@Composable
fun ThemePreviewScreen(
    boardTheme: BoardTheme,
    pieceTheme: PieceTheme,
    isEquipped: Boolean = false,
    onApplyTheme: (BoardTheme, PieceTheme) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var isFlipped by remember { mutableStateOf(false) }
    var previewState by remember(boardTheme, pieceTheme) { mutableStateOf(createInitialGameState()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF231B15),
                        Color(0xFF140F0C),
                        Color(0xFF0F0A07)
                    ),
                    radius = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================
            // HEADER BAR (Safely below status bar)
            // ==========================================
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "THEME PREVIEW",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "Tabletop Examination Room",
                        fontSize = 10.sp,
                        color = StudyAmberAccent
                    )
                }

                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        isFlipped = !isFlipped
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("⟲", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // THEME SPECIFICATION CARD
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x24FFFFFF))
                    .border(1.dp, if (isEquipped) StudyAmberAccent else Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${boardTheme.name} & ${pieceTheme.name}".uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = StudyParchmentCream
                            )
                            Text(
                                text = boardTheme.description,
                                fontSize = 11.sp,
                                color = Color(0xFFC4B5A5),
                                maxLines = 2
                            )
                        }

                        if (isEquipped) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudyAmberAccent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "EQUIPPED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFF1B140E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x30FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "BOARD: ${boardTheme.name}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyAmberAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x30FFFFFF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "PIECES: ${pieceTheme.name}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyParchmentCream
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // 8x8 INTERACTIVE TABLETOP BOARD
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                ChessBoardView(
                    state = previewState,
                    flipped = isFlipped,
                    isInteractable = true,
                    showCoordinates = true,
                    highlightMoves = true,
                    boardTheme = boardTheme,
                    pieceTheme = pieceTheme,
                    lastMove = if (previewState.history.isNotEmpty()) previewState.history.last() else null,
                    onMove = { move ->
                        previewState = makeMove(previewState, move)
                        SoundManager.playMove(move.captured != null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Test interaction guide & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tip: Tap pieces to test moves on this tabletop",
                    fontSize = 11.sp,
                    color = Color(0xFFAAA095)
                )

                OutlinedButton(
                    onClick = {
                        previewState = createInitialGameState()
                        SoundManager.playClick()
                    },
                    modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StudyParchmentCream)
                ) {
                    Text("Reset Board", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // APPLY / USE THIS THEME BUTTON
            // ==========================================
            Button(
                onClick = {
                    SoundManager.playClick()
                    onApplyTheme(boardTheme, pieceTheme)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(10.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEquipped) Color(0xFF2E2319) else StudyAmberAccent
                ),
                border = if (isEquipped) androidx.compose.foundation.BorderStroke(1.5.dp, StudyAmberAccent) else null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isEquipped) "CURRENTLY EQUIPPED ✓" else "USE THIS THEME",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = if (isEquipped) StudyAmberAccent else Color(0xFF1B1309)
                )
            }
        }
    }
}
