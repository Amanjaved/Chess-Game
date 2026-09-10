package com.example.chessgame.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
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
 * The Armory: Dedicated Theme Gallery Screen.
 * Lets players independently choose, mix and preview Board Themes and Piece Themes.
 * Features real miniature 8x8 board previews with actual assets, clear separation
 * between boards and pieces, and tactile equipment actions.
 */
@Composable
fun ThemeGalleryScreen(
    currentBoardTheme: BoardTheme,
    currentPieceTheme: PieceTheme,
    onSelectBoardTheme: (BoardTheme) -> Unit,
    onSelectPieceTheme: (PieceTheme) -> Unit,
    onPreviewTheme: (BoardTheme, PieceTheme, ThemePreviewTarget) -> Unit,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf(ThemeTab.BOARDS) }

    ArenaBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Bar with centered title
            ArenaHeader(
                title = "THE ARMORY",
                subtitle = "BOARD TEXTURES & PIECE ARTIFACTS",
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
                                onPreviewTheme(
                                    currentBoardTheme,
                                    currentPieceTheme,
                                    if (activeTab == ThemeTab.BOARDS) ThemePreviewTarget.BOARD else ThemePreviewTarget.PIECE
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👁️", fontSize = 16.sp)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Currently Equipped Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaColors.TitaniumSurface)
                    .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛡️", fontSize = 14.sp)
                    Column {
                        Text(
                            text = "EQUIPPED LOADOUT",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "${currentBoardTheme.name} + ${currentPieceTheme.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ArenaColors.TextPrimary
                        )
                    }
                }

                Text(
                    text = "INSPECT ↗",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = ArenaColors.SolarAmber,
                    modifier = Modifier
                        .clickable {
                            SoundManager.playClick()
                            onPreviewTheme(currentBoardTheme, currentPieceTheme, ThemePreviewTarget.BOARD)
                        }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Two-tab selector (BOARD THEMES / PIECE THEMES)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ArenaColors.TitaniumSurface)
                    .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(14.dp))
                    .padding(3.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (activeTab == ThemeTab.BOARDS) ArenaColors.CyberCyan else Color.Transparent
                        )
                        .clickable {
                            SoundManager.playClick()
                            activeTab = ThemeTab.BOARDS
                        }
                        .padding(vertical = 9.dp)
                ) {
                    Text(
                        text = "BOARDS (${ThemeRegistry.ALL_BOARD_THEMES.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = if (activeTab == ThemeTab.BOARDS) Color(0xFF070B10) else ArenaColors.TextSecondary
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (activeTab == ThemeTab.PIECES) ArenaColors.CyberCyan else Color.Transparent
                        )
                        .clickable {
                            SoundManager.playClick()
                            activeTab = ThemeTab.PIECES
                        }
                        .padding(vertical = 9.dp)
                ) {
                    Text(
                        text = "PIECES (${ThemeRegistry.ALL_PIECE_THEMES.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = if (activeTab == ThemeTab.PIECES) Color(0xFF070B10) else ArenaColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Section Description
            Text(
                text = if (activeTab == ThemeTab.BOARDS) {
                    "Equip high-durability boards or tap card to launch 8x8 tactical preview."
                } else {
                    "Equip handcrafted and cyber piece sets. Seamlessly mix with any board."
                },
                fontSize = 11.sp,
                color = ArenaColors.TextMuted,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                            onCardClick = {
                                SoundManager.playClick()
                                onPreviewTheme(board, currentPieceTheme, ThemePreviewTarget.BOARD)
                            },
                            onEquip = {
                                SoundManager.playClick()
                                onSelectBoardTheme(board)
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
                            onCardClick = {
                                SoundManager.playClick()
                                onPreviewTheme(currentBoardTheme, pieces, ThemePreviewTarget.PIECE)
                            },
                            onEquip = {
                                SoundManager.playClick()
                                onSelectPieceTheme(pieces)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Full Preview Button
            ArenaButton(
                text = "LAUNCH FULL 8×8 PREVIEW",
                icon = "👁️",
                isPrimary = true,
                onClick = {
                    SoundManager.playClick()
                    onPreviewTheme(
                        currentBoardTheme,
                        currentPieceTheme,
                        if (activeTab == ThemeTab.BOARDS) ThemePreviewTarget.BOARD else ThemePreviewTarget.PIECE
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            )
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
            .border(1.dp, boardTheme.frameInnerBorder, RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        // If board has an illustrated raster asset (e.g. Artisan Wood), render it as background
        if (boardTheme.boardDrawableRes != null) {
            Image(
                painter = painterResource(id = boardTheme.boardDrawableRes),
                contentDescription = boardTheme.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

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
                                .background(
                                    if (boardTheme.boardDrawableRes != null) Color.Transparent
                                    else if (isLight) boardTheme.lightSquare
                                    else boardTheme.darkSquare
                                )
                        ) {
                            if (piece != null) {
                                PieceView(
                                    type = piece.first,
                                    color = piece.second,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(0.5.dp)
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
    onCardClick: () -> Unit,
    onEquip: () -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(14.dp), spotColor = ArenaColors.CyberCyan)
            .clip(RoundedCornerShape(14.dp))
            .background(ArenaColors.TitaniumSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCardClick() }
            .padding(10.dp)
    ) {
        // Real miniature 8x8 chessboard
        RealMiniChessBoard(
            boardTheme = theme,
            pieceTheme = pieceTheme,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = theme.name,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ArenaColors.CyberCyan else ArenaColors.TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ArenaColors.SolarAmber)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "EQUIPPED ✓",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF090D13)
                    )
                }
            }
        }

        Text(
            text = theme.subtitle,
            fontSize = 9.5.sp,
            color = ArenaColors.TextSecondary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSelected) "EQUIPPED" else "TAP TO EQUIP",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = if (isSelected) ArenaColors.SolarAmber else ArenaColors.CyberCyan,
                modifier = Modifier.clickable { onEquip() }
            )
            Text(
                text = "Preview ↗",
                fontSize = 9.5.sp,
                color = ArenaColors.TextMuted,
                modifier = Modifier.clickable { onCardClick() }
            )
        }
    }
}

@Composable
private fun PieceThemeCard(
    pieceTheme: PieceTheme,
    boardTheme: BoardTheme,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onEquip: () -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(14.dp), spotColor = ArenaColors.CyberCyan)
            .clip(RoundedCornerShape(14.dp))
            .background(ArenaColors.TitaniumSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCardClick() }
            .padding(10.dp)
    ) {
        // Real miniature 8x8 chessboard showing this piece set
        RealMiniChessBoard(
            boardTheme = boardTheme,
            pieceTheme = pieceTheme,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pieceTheme.name,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ArenaColors.CyberCyan else ArenaColors.TextPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ArenaColors.SolarAmber)
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "EQUIPPED ✓",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF090D13)
                    )
                }
            }
        }

        Text(
            text = pieceTheme.subtitle,
            fontSize = 9.5.sp,
            color = ArenaColors.TextSecondary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isSelected) "EQUIPPED" else "TAP TO EQUIP",
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = if (isSelected) ArenaColors.SolarAmber else ArenaColors.CyberCyan,
                modifier = Modifier.clickable { onEquip() }
            )
            Text(
                text = "Preview ↗",
                fontSize = 9.5.sp,
                color = ArenaColors.TextMuted,
                modifier = Modifier.clickable { onCardClick() }
            )
        }
    }
}
