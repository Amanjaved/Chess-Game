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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*

@Composable
fun PromotionDialog(
    color: PieceColor,
    onSelect: (PieceType) -> Unit
) {
    Dialog(onDismissRequest = { /* Must select */ }) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(1.5.dp, GoldAccent, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pawn Promotion",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose a piece to promote your pawn:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val options = listOf(
                        Pair(PieceType.QUEEN, "Queen"),
                        Pair(PieceType.ROOK, "Rook"),
                        Pair(PieceType.BISHOP, "Bishop"),
                        Pair(PieceType.KNIGHT, "Knight")
                    )

                    options.forEach { (type, name) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceHover)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { onSelect(type) }
                                .padding(vertical = 12.dp, horizontal = 4.dp)
                        ) {
                            PieceView(
                                type = type,
                                color = color,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
