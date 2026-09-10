package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*

private enum class HowToPlayChapter {
    BASICS,
    MOVES,
    SPECIAL,
    CHECKMATE,
    DRAWS
}

/**
 * Illustrated Chess Player's Handbook.
 * Styled as a vintage parchment study treatise with chapter tabs,
 * piece movement diagrams, and tactical rules.
 */
@Composable
fun HowToPlayDialog(
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onClose: () -> Unit
) {
    var activeChapter by remember { mutableStateOf(HowToPlayChapter.BASICS) }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 580.dp)
                .shadow(28.dp, RoundedCornerShape(20.dp), spotColor = Color(0xCC000000))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF231A14),
                            Color(0xFF16100C),
                            Color(0xFF0F0B08)
                        )
                    )
                )
                .border(1.5.dp, Color(0x38D4A373), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CHESS HANDBOOK",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = StudyParchmentCream
                        )
                        Text(
                            text = "A Guide for the Thoughtful Player",
                            fontSize = 11.sp,
                            color = StudyAmberAccent
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF))
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Chapter Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Pair(HowToPlayChapter.BASICS, "Basics"),
                        Pair(HowToPlayChapter.MOVES, "Moves"),
                        Pair(HowToPlayChapter.SPECIAL, "Special"),
                        Pair(HowToPlayChapter.CHECKMATE, "Mate"),
                        Pair(HowToPlayChapter.DRAWS, "Draws")
                    ).forEach { (chapter, label) ->
                        val isSelected = activeChapter == chapter
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) StudyAmberAccent else Color(0x18FFFFFF))
                                .clickable {
                                    SoundManager.playClick()
                                    activeChapter = chapter
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFF140E06) else StudyParchmentCream
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chapter Content
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    when (activeChapter) {
                        HowToPlayChapter.BASICS -> {
                            item {
                                HandbookArticleCard(
                                    title = "The Objective",
                                    body = "Chess is a war of two minds played on an 8×8 grid. Your goal is not merely to capture pieces, but to trap the enemy King into Checkmate — where he is attacked and cannot escape."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "The Board & Setup",
                                    body = "White moves first. White begins on ranks 1 & 2; Black begins on ranks 7 & 8. Bottom-right corner is always a light square: 'White on the right'. Queens sit on their own matching color (White Queen on d1, Black Queen on d8)."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Tempo & Initiative",
                                    body = "Control the 4 central squares (d4, d5, e4, e5). Develop your Knights and Bishops early before launching an assault with your heavy artillery (Queen & Rooks)."
                                )
                            }
                        }

                        HowToPlayChapter.MOVES -> {
                            item {
                                IllustratedPieceMoveCard(
                                    name = "KING ♚",
                                    symbol = "1 square any direction",
                                    description = "The heart of your army. Moves one square in any direction. Must never move into check or remain under threat.",
                                    type = PieceType.KING,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "QUEEN ♛",
                                    symbol = "Ranks, files & diagonals",
                                    description = "The most powerful piece on the board. Can sweep any number of unoccupied squares horizontally, vertically, or diagonally.",
                                    type = PieceType.QUEEN,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "ROOK ♜",
                                    symbol = "Straight lines",
                                    description = "Moves any distance along files (columns) and ranks (rows). Controls open files and delivers powerful endgames.",
                                    type = PieceType.ROOK,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "BISHOP ♝",
                                    symbol = "Diagonals only",
                                    description = "Travels any distance diagonally. Each player begins with one light-squared Bishop and one dark-squared Bishop.",
                                    type = PieceType.BISHOP,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "KNIGHT ♞",
                                    symbol = "L-shape leap",
                                    description = "Moves in an 'L' shape: 2 squares in one cardinal direction, then 1 square perpendicular. The only piece capable of jumping over other pieces.",
                                    type = PieceType.KNIGHT,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "PAWN ♟",
                                    symbol = "Forward 1 (or 2 initially)",
                                    description = "Advances forward 1 square (or optionally 2 squares on its very first move). Captures diagonally forward 1 square.",
                                    type = PieceType.PAWN,
                                    pieceTheme = pieceTheme
                                )
                            }
                        }

                        HowToPlayChapter.SPECIAL -> {
                            item {
                                HandbookArticleCard(
                                    title = "Castling (O-O / O-O-O)",
                                    body = "A dual move where the King moves 2 squares toward a Rook, and that Rook hops over to sit beside the King. Neither piece may have moved previously, and the King cannot castle out of, through, or into check."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "En Passant",
                                    body = "If an enemy Pawn advances 2 squares past your pawn on an adjacent file, you may capture it as if it had only moved 1 square forward. Must be claimed immediately on the very next move."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Pawn Promotion",
                                    body = "When a Pawn reaches the far rank (rank 8 for White, rank 1 for Black), it is immediately promoted to a Queen, Rook, Bishop, or Knight of your choice."
                                )
                            }
                        }

                        HowToPlayChapter.CHECKMATE -> {
                            item {
                                HandbookArticleCard(
                                    title = "Check",
                                    body = "When the King is directly targeted by an enemy piece. The player must escape check immediately: by moving the King, blocking the attack, or capturing the threatening piece."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Checkmate",
                                    body = "The King is under attack and has no legal moves to escape. The game ends instantly in a decisive victory for the attacking side."
                                )
                            }
                        }

                        HowToPlayChapter.DRAWS -> {
                            item {
                                HandbookArticleCard(
                                    title = "Stalemate",
                                    body = "The player to move is NOT in check, but has zero legal moves left on the entire board. Result is an immediate draw."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Threefold Repetition",
                                    body = "The exact same board position occurs 3 times with the same player to move and the same rights."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Insufficient Material",
                                    body = "Neither side has enough firepower to theoretically force checkmate (e.g., King vs King, or King & Bishop vs King)."
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“Strategy without tactics is the slowest route to victory.”",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFFAFA293),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HandbookArticleCard(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1611))
            .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StudyAmberAccent
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                color = StudyParchmentCream,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun IllustratedPieceMoveCard(
    name: String,
    symbol: String,
    description: String,
    type: PieceType,
    pieceTheme: PieceTheme
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1611))
            .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF140F0A))
                .border(1.dp, StudyAmberAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            PieceView(
                type = type,
                color = PieceColor.WHITE,
                modifier = Modifier.size(40.dp),
                pieceTheme = pieceTheme
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = StudyParchmentCream
                )
                Text(
                    text = symbol,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = StudyAmberAccent
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFFB5A99B),
                lineHeight = 15.sp
            )
        }
    }
}
