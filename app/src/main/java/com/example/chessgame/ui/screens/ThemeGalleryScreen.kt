package com.example.chessgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.PieceView

enum class ThemeTab {
    BOARDS,
    PIECES
}

/**
 * Dedicated Theme Gallery Screen.
 * Lets players independently choose and preview Board Themes and Piece Themes.
 */
@Composable
fun ThemeGalleryScreen(
    currentBoardTheme: BoardTheme,
    currentPieceTheme: PieceTheme,
    onSelectBoardTheme: (BoardTheme) -> Unit,
    onSelectPieceTheme: (PieceTheme) -> Unit,
    onPreviewTheme: (BoardTheme, PieceTheme) -> Unit,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf(ThemeTab.BOARDS) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1712),
                        Color(0xFF140F0B),
                        Color(0xFF0C0906)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x20FFFFFF))
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "THEME ROOM",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "Curated Boards & Handcrafted Pieces",
                        fontSize = 11.sp,
                        color = StudyAmberAccent
                    )
                }

                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onPreviewTheme(currentBoardTheme, currentPieceTheme)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x20FFFFFF))
                ) {
                    Text("👁️", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-tab selector (BOARD COLLECTION / PIECE COLLECTION)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF281E16))
                    .border(1.dp, StudyTableFrame, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == ThemeTab.BOARDS) StudyAmberAccent else Color.Transparent)
                        .clickable {
                            SoundManager.playClick()
                            activeTab = ThemeTab.BOARDS
                        }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "BOARD COLLECTION (${ThemeRegistry.ALL_BOARD_THEMES.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (activeTab == ThemeTab.BOARDS) Color(0xFF1E140C) else StudyParchmentCream
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (activeTab == ThemeTab.PIECES) StudyAmberAccent else Color.Transparent)
                        .clickable {
                            SoundManager.playClick()
                            activeTab = ThemeTab.PIECES
                        }
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = "PIECE COLLECTION (${ThemeRegistry.ALL_PIECE_THEMES.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (activeTab == ThemeTab.PIECES) Color(0xFF1E140C) else StudyParchmentCream
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2-Column Grid of Theme Cards
            if (activeTab == ThemeTab.BOARDS) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ThemeRegistry.ALL_BOARD_THEMES) { board ->
                        val isSelected = board.id == currentBoardTheme.id
                        BoardThemeCard(
                            theme = board,
                            pieceTheme = currentPieceTheme,
                            isSelected = isSelected,
                            onSelect = {
                                SoundManager.playClick()
                                onSelectBoardTheme(board)
                            },
                            onPreview = {
                                SoundManager.playClick()
                                onPreviewTheme(board, currentPieceTheme)
                            }
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ThemeRegistry.ALL_PIECE_THEMES) { pieces ->
                        val isSelected = pieces.id == currentPieceTheme.id
                        PieceThemeCard(
                            pieceTheme = pieces,
                            boardTheme = currentBoardTheme,
                            isSelected = isSelected,
                            onSelect = {
                                SoundManager.playClick()
                                onSelectPieceTheme(pieces)
                            },
                            onPreview = {
                                SoundManager.playClick()
                                onPreviewTheme(currentBoardTheme, pieces)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Full Preview Button
            Button(
                onClick = {
                    SoundManager.playClick()
                    onPreviewTheme(currentBoardTheme, currentPieceTheme)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "OPEN FULL 8x8 PREVIEW",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color(0xFF1B140E)
                )
            }
        }
    }
}

/**
 * Real miniature 8x8 chess board displaying actual theme assets
 * rather than simple flat color swatches.
 */
@Composable
private fun RealMiniChessBoard(
    boardTheme: BoardTheme,
    pieceTheme: PieceTheme,
    modifier: Modifier = Modifier
) {
    // A classic opening tableau position for rich visual showcase
    val sampleBoard = remember {
        val grid = Array(8) { arrayOfNulls<Pair<PieceType, PieceColor>>(8) }
        // Black pieces
        grid[0][0] = PieceType.ROOK to PieceColor.BLACK
        grid[0][1] = PieceType.KNIGHT to PieceColor.BLACK
        grid[0][2] = PieceType.BISHOP to PieceColor.BLACK
        grid[0][3] = PieceType.QUEEN to PieceColor.BLACK
        grid[0][4] = PieceType.KING to PieceColor.BLACK
        grid[0][7] = PieceType.ROOK to PieceColor.BLACK
        grid[1][0] = PieceType.PAWN to PieceColor.BLACK
        grid[1][1] = PieceType.PAWN to PieceColor.BLACK
        grid[1][2] = PieceType.PAWN to PieceColor.BLACK
        grid[1][5] = PieceType.PAWN to PieceColor.BLACK
        grid[1][6] = PieceType.PAWN to PieceColor.BLACK
        grid[1][7] = PieceType.PAWN to PieceColor.BLACK
        grid[2][2] = PieceType.KNIGHT to PieceColor.BLACK
        grid[3][3] = PieceType.PAWN to PieceColor.BLACK

        // White pieces
        grid[3][4] = PieceType.PAWN to PieceColor.WHITE
        grid[4][2] = PieceType.BISHOP to PieceColor.WHITE
        grid[5][5] = PieceType.KNIGHT to PieceColor.WHITE
        grid[6][0] = PieceType.PAWN to PieceColor.WHITE
        grid[6][1] = PieceType.PAWN to PieceColor.WHITE
        grid[6][2] = PieceType.PAWN to PieceColor.WHITE
        grid[6][5] = PieceType.PAWN to PieceColor.WHITE
        grid[6][6] = PieceType.PAWN to PieceColor.WHITE
        grid[6][7] = PieceType.PAWN to PieceColor.WHITE
        grid[7][0] = PieceType.ROOK to PieceColor.WHITE
        grid[7][3] = PieceType.QUEEN to PieceColor.WHITE
        grid[7][4] = PieceType.KING to PieceColor.WHITE
        grid[7][7] = PieceType.ROOK to PieceColor.WHITE
        grid
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(boardTheme.frameBorder)
            .padding(2.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0 until 8) {
                Row(modifier = Modifier.weight(1f)) {
                    for (c in 0 until 8) {
                        val isLight = (r + c) % 2 == 0
                        val piece = sampleBoard[r][c]

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(if (isLight) boardTheme.lightSquare else boardTheme.darkSquare)
                        ) {
                            if (piece != null) {
                                PieceView(
                                    type = piece.first,
                                    color = piece.second,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.fillMaxSize().padding(0.5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardThemeCard(
    theme: BoardTheme,
    pieceTheme: PieceTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF221A14))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x33FFFFFF),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(10.dp)
    ) {
        // Real miniature 8x8 chessboard
        RealMiniChessBoard(
            boardTheme = theme,
            pieceTheme = pieceTheme,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = theme.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) StudyAmberAccent else StudyParchmentCream
        )

        Text(
            text = theme.subtitle,
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSelected) "EQUIPPED" else "SELECT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) StudyAmberAccent else StudyParchmentCream
            )
            Text(
                text = "Preview ↗",
                fontSize = 10.sp,
                color = StudyAmberAccent,
                modifier = Modifier.clickable { onPreview() }
            )
        }
    }
}

@Composable
private fun PieceThemeCard(
    pieceTheme: PieceTheme,
    boardTheme: BoardTheme,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF221A14))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x33FFFFFF),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(10.dp)
    ) {
        // Real miniature 8x8 chessboard showing this piece set
        RealMiniChessBoard(
            boardTheme = boardTheme,
            pieceTheme = pieceTheme,
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = pieceTheme.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) StudyAmberAccent else StudyParchmentCream
        )

        Text(
            text = pieceTheme.subtitle,
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSelected) "EQUIPPED" else "SELECT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) StudyAmberAccent else StudyParchmentCream
            )
            Text(
                text = "Preview ↗",
                fontSize = 10.sp,
                color = StudyAmberAccent,
                modifier = Modifier.clickable { onPreview() }
            )
        }
    }
}
