package com.example.chessgame.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.createInitialGameState
import com.example.chessgame.engine.makeMove
import com.example.chessgame.theme.BoardTheme
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.StudyAmberAccent
import com.example.chessgame.theme.StudyParchmentCream
import com.example.chessgame.ui.components.ChessBoardView

enum class ThemePreviewTarget {
    BOARD,
    PIECE
}

/**
 * Full-screen interactive Theme Preview.
 * Displays an active 8x8 board showcasing how the selected BoardTheme
 * and PieceTheme look and feel together in realistic tabletop conditions.
 */
@Composable
fun ThemePreviewScreen(
    boardTheme: BoardTheme,
    pieceTheme: PieceTheme,
    previewTarget: ThemePreviewTarget = ThemePreviewTarget.BOARD,
    isEquipped: Boolean = false,
    onApplyTheme: (BoardTheme, PieceTheme) -> Unit,
    onBack: () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }
    var previewState by remember(boardTheme, pieceTheme) { mutableStateOf(createInitialGameState()) }

    val activeThemeName = if (previewTarget == ThemePreviewTarget.BOARD) boardTheme.name else pieceTheme.name
    val activeThemeDescription = if (previewTarget == ThemePreviewTarget.BOARD) boardTheme.description else pieceTheme.description
    val categoryLabel = if (previewTarget == ThemePreviewTarget.BOARD) "BOARD THEME" else "PIECE THEME"
    val pairedDetail = if (previewTarget == ThemePreviewTarget.BOARD) {
        "Pieces: ${pieceTheme.name} (${pieceTheme.subtitle})"
    } else {
        "Board: ${boardTheme.name} (${boardTheme.subtitle})"
    }

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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar with centered title and safe-area margins
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "THEME PREVIEW",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = if (previewTarget == ThemePreviewTarget.BOARD) "Board Showcase" else "Piece Set Showcase",
                        fontSize = 10.sp,
                        color = StudyAmberAccent
                    )
                }

                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        isFlipped = !isFlipped
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                ) {
                    Text("⟲", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Theme Spec Card with Equipped status and Material / Style info
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = activeThemeName.uppercase(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = StudyParchmentCream
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x33DFB36E))
                                    .border(0.8.dp, StudyAmberAccent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = categoryLabel,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = StudyAmberAccent
                                )
                            }
                        }

                        if (isEquipped) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StudyAmberAccent)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "EQUIPPED ✓",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFF1B140E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = activeThemeDescription,
                        fontSize = 11.5.sp,
                        color = Color(0xFFC4B5A5),
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = pairedDetail,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = StudyAmberAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 8x8 Interactive Tabletop Board
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

            // Apply / Equipped Button
            Button(
                onClick = {
                    SoundManager.playClick()
                    onApplyTheme(boardTheme, pieceTheme)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(10.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEquipped) Color(0xFF2E2319) else StudyAmberAccent
                ),
                border = if (isEquipped) androidx.compose.foundation.BorderStroke(1.5.dp, StudyAmberAccent) else null,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isEquipped) "CURRENTLY EQUIPPED ✓" else "USE THIS THEME",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = if (isEquipped) StudyAmberAccent else Color(0xFF1B1309)
                )
            }
        }
    }
}
