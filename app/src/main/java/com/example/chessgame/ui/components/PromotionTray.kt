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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*

/**
 * Grandmaster Arena Tactical Promotion Tray.
 * Displays Queen, Rook, Bishop, Knight resting in energized titanium chambers.
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
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(ArenaColors.CyberCyan, ArenaColors.TitaniumBorder)
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "UNIT PROMOTION",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = ArenaColors.CyberCyan
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Select battlefield reinforcement unit",
                    fontSize = 11.5.sp,
                    color = ArenaColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                val row1 = listOf(Pair(PieceType.QUEEN, "QUEEN"), Pair(PieceType.ROOK, "ROOK"))
                val row2 = listOf(Pair(PieceType.BISHOP, "BISHOP"), Pair(PieceType.KNIGHT, "KNIGHT"))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(row1, row2).forEach { rowChoices ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowChoices.forEach { (type, label) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = ArenaColors.CyberCyan)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(ArenaColors.TitaniumSurface)
                                        .border(1.2.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(14.dp))
                                        .clickable {
                                            SoundManager.playClick()
                                            onSelect(type)
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp)
                                ) {
                                    PieceView(
                                        type = type,
                                        color = color,
                                        modifier = Modifier.size(52.dp),
                                        pieceTheme = pieceTheme
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = ArenaColors.TextPrimary
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
