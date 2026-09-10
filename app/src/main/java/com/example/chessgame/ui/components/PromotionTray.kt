package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

/**
 * Handcrafted Wooden/Parchment Promotion Tray.
 * Displays Queen, Rook, Bishop, Knight sitting on carved wooden plinths.
 */
@Composable
fun PromotionTray(
    color: PieceColor,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onSelect: (PieceType) -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(24.dp, RoundedCornerShape(20.dp), spotColor = Color(0xAA000000))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2E2016), Color(0xFF1E150F), Color(0xFF140D09))
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(StudyAmberAccent, StudyTableFrame, StudyAmberAccent)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PAWN PROMOTION",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = StudyParchmentCream
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Choose your new piece",
                    fontSize = 12.sp,
                    color = StudyAmberAccent
                )

                Spacer(modifier = Modifier.height(20.dp))

                val choices = listOf(
                    Pair(PieceType.QUEEN, "QUEEN"),
                    Pair(PieceType.ROOK, "ROOK"),
                    Pair(PieceType.BISHOP, "BISHOP"),
                    Pair(PieceType.KNIGHT, "KNIGHT")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val row1 = listOf(Pair(PieceType.QUEEN, "QUEEN"), Pair(PieceType.ROOK, "ROOK"))
                    val row2 = listOf(Pair(PieceType.BISHOP, "BISHOP"), Pair(PieceType.KNIGHT, "KNIGHT"))

                    listOf(row1, row2).forEach { rowChoices ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowChoices.forEach { (type, label) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(8.dp, RoundedCornerShape(14.dp))
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF422E21), Color(0xFF241810))
                                            )
                                        )
                                        .border(1.5.dp, Color(0x35D4A373), RoundedCornerShape(14.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            onSelect(type)
                                        }
                                        .padding(vertical = 16.dp, horizontal = 8.dp)
                                ) {
                                    PieceView(
                                        type = type,
                                        color = color,
                                        pieceTheme = pieceTheme,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = StudyParchmentCream
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
