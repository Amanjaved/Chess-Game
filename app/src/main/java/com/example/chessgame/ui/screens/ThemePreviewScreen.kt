package com.example.chessgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.createInitialGameState
import com.example.chessgame.engine.makeMove
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.ChessBoardView

enum class ThemePreviewTarget {
    BOARD,
    PIECE
}

/**
 * Grandmaster Arena: Full-Screen Interactive Theme Showcase.
 * Displays an active 8x8 tactical board showcasing how the selected BoardTheme
 * and PieceTheme look and feel together in arena combat conditions.
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
        "Paired Pieces: ${pieceTheme.name} (${pieceTheme.subtitle})"
    } else {
        "Paired Board: ${boardTheme.name} (${boardTheme.subtitle})"
    }

    ArenaBackgroundScaffold {
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
            ArenaHeader(
                title = "THEME SHOWCASE",
                subtitle = if (previewTarget == ThemePreviewTarget.BOARD) "Board Showcase" else "Piece Set Showcase",
                onBack = onBack,
                rightContent = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ArenaColors.TitaniumSurface)
                            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                SoundManager.playClick()
                                isFlipped = !isFlipped
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⟲", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                    }
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Theme Spec Card with Equipped status and Material / Style info
            ArenaCard(
                modifier = Modifier.fillMaxWidth(),
                isHighlighted = isEquipped,
                highlightColor = ArenaColors.SolarAmber
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
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
                                color = ArenaColors.TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ArenaColors.CyberCyan.copy(alpha = 0.2f))
                                    .border(0.8.dp, ArenaColors.CyberCyan, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = categoryLabel,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = ArenaColors.CyberCyan
                                )
                            }
                        }

                        if (isEquipped) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ArenaColors.SolarAmber)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "EQUIPPED ✓",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color(0xFF090D13)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = activeThemeDescription,
                        fontSize = 11.5.sp,
                        color = ArenaColors.TextSecondary,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = pairedDetail,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = ArenaColors.CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 8x8 Interactive Tabletop Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF000000))
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp))
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
                    text = "Tip: Tap pieces to test movement in arena",
                    fontSize = 11.sp,
                    color = ArenaColors.TextMuted
                )

                OutlinedButton(
                    onClick = {
                        previewState = createInitialGameState()
                        SoundManager.playClick()
                    },
                    modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ArenaColors.TextPrimary)
                ) {
                    Text("Reset Arena", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Apply / Equipped Button
            ArenaButton(
                text = if (isEquipped) "CURRENTLY EQUIPPED ✓" else "EQUIP THIS LOADOUT",
                icon = if (isEquipped) "✓" else "⚡",
                isPrimary = !isEquipped,
                onClick = {
                    SoundManager.playClick()
                    onApplyTheme(boardTheme, pieceTheme)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
        }
    }
}
