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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * Grandmaster Arena Tactical Manual & Chess Doctrines.
 * Interactive tactical handbook featuring doctrine chapters, piece movement schematics,
 * special gambits, and endgame rules.
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
                .fillMaxWidth(0.96f)
                .heightIn(max = 580.dp)
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
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TACTICAL MANUAL",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "Grandmaster Doctrines & Movement Rules",
                            fontSize = 11.sp,
                            color = ArenaColors.TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Text("✕", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Chapter Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isSelected) ArenaColors.CyberCyan else Color.Transparent)
                                .clickable {
                                    SoundManager.playClick()
                                    activeChapter = chapter
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color(0xFF070B10) else ArenaColors.TextSecondary
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
                                    body = "Chess is a war of two minds played on an 8×8 tactical grid. Your goal is not merely to capture pieces, but to trap the enemy King into Checkmate — where he is directly threatened and has no legal path of escape."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "The Arena & Deployment",
                                    body = "White Legion moves first. White deploys on ranks 1 & 2; Black Dynasty deploys on ranks 7 & 8. The bottom-right corner is always a light square: 'White on the right'. Queens sit on their matching color (White Queen on d1, Black Queen on d8)."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Tempo & Center Control",
                                    body = "Seize control of the 4 central nexus squares (d4, d5, e4, e5). Mobilize your Knights and Bishops early to project force before maneuvering your heavy batteries (Queen & Rooks)."
                                )
                            }
                        }

                        HowToPlayChapter.MOVES -> {
                            item {
                                IllustratedPieceMoveCard(
                                    name = "KING ♚",
                                    symbol = "1 square any direction",
                                    description = "The supreme commander. Advances 1 square in any direction. Must never step into check or remain under threat.",
                                    type = PieceType.KING,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "QUEEN ♛",
                                    symbol = "Ranks, files & diagonals",
                                    description = "The most lethal unit on the board. Can sweep any number of unoccupied squares horizontally, vertically, or diagonally.",
                                    type = PieceType.QUEEN,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "ROOK ♜",
                                    symbol = "Straight lines",
                                    description = "Strikes along files (columns) and ranks (rows). Controls open operational corridors and delivers crushing endgame checkmates.",
                                    type = PieceType.ROOK,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "BISHOP ♝",
                                    symbol = "Diagonals only",
                                    description = "Strikes diagonally across the arena. Each commander commands one light-squared Bishop and one dark-squared Bishop.",
                                    type = PieceType.BISHOP,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "KNIGHT ♞",
                                    symbol = "L-shape leap",
                                    description = "Moves in an 'L' shape: 2 squares cardinal, then 1 square perpendicular. The only unit capable of jumping over friendly or enemy forces.",
                                    type = PieceType.KNIGHT,
                                    pieceTheme = pieceTheme
                                )
                            }
                            item {
                                IllustratedPieceMoveCard(
                                    name = "PAWN ♟",
                                    symbol = "Forward 1 (or 2 initially)",
                                    description = "Advances forward 1 square (or optionally 2 squares on initial march). Attacks diagonally forward 1 square. Crucial for structure.",
                                    type = PieceType.PAWN,
                                    pieceTheme = pieceTheme
                                )
                            }
                        }

                        HowToPlayChapter.SPECIAL -> {
                            item {
                                HandbookArticleCard(
                                    title = "Castling (O-O / O-O-O)",
                                    body = "A coordinated tactical move where the King moves 2 squares toward a Rook, and that Rook hops over to flank the King. Neither unit may have previously moved, and the King cannot castle out of, through, or into check."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "En Passant",
                                    body = "If an opposing Pawn advances 2 squares past your pawn on an adjacent file, you may strike diagonally behind it as if it had only moved 1 square forward. Must be claimed on that exact turn."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Pawn Promotion",
                                    body = "When a Pawn breaches the enemy baseline (rank 8 for White, rank 1 for Black), it immediately promotes to a Queen, Rook, Bishop, or Knight of your choice."
                                )
                            }
                        }

                        HowToPlayChapter.CHECKMATE -> {
                            item {
                                HandbookArticleCard(
                                    title = "Check",
                                    body = "When the King is directly locked in the crosshairs of an enemy piece. Escape check immediately: by evading with the King, interposing a shield piece, or neutralizing the attacking unit."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Checkmate",
                                    body = "The enemy King is under attack and has zero legal escapes remaining. Immediate and decisive victory."
                                )
                            }
                        }

                        HowToPlayChapter.DRAWS -> {
                            item {
                                HandbookArticleCard(
                                    title = "Stalemate",
                                    body = "The commander to move is NOT in check, but has zero legal moves left on the entire board. Match concludes immediately in an honorable draw."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Threefold Repetition",
                                    body = "The exact same tactical board configuration occurs 3 times with the same player to move and identical rights."
                                )
                            }
                            item {
                                HandbookArticleCard(
                                    title = "Insufficient Firepower",
                                    body = "Neither side retains sufficient forces to theoretically deliver checkmate (e.g. King vs King, or King & Bishop vs King)."
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
                    color = ArenaColors.TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
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
            .background(ArenaColors.TitaniumSurface)
            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ArenaColors.CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                color = ArenaColors.TextPrimary,
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
            .background(ArenaColors.TitaniumSurface)
            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF090D14))
                .border(1.dp, ArenaColors.CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
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
                    color = ArenaColors.TextPrimary
                )
                Text(
                    text = symbol,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = ArenaColors.CyberCyan
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = ArenaColors.TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
