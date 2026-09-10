package com.example.chessgame.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.chessgame.ai.PIECE_VALUES
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*

/**
 * Physical Tournament Scorecard Player Card.
 * Crafted from warm parchment, fountain pen ink typography, wax-seal status badges,
 * and carved wooden avatars.
 */
@Composable
fun PlayerCardView(
    name: String,
    subtitle: String,
    color: PieceColor,
    isAI: Boolean,
    isTurn: Boolean,
    isThinking: Boolean,
    isInCheck: Boolean,
    capturedPieces: List<PieceType>,
    materialAdvantage: Int,
    timerSeconds: Int? = null,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    boardTheme: BoardTheme = ThemeRegistry.ARTISAN_WOOD,
    modifier: Modifier = Modifier
) {
    val sortedCaptures = capturedPieces.sortedByDescending { PIECE_VALUES[it] ?: 0 }
    val capturedColor = color.opponent()

    // Warm lamp turn glow
    val infiniteTransition = rememberInfiniteTransition(label = "turn_glow")
    val turnGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isTurn) 8.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isTurn) StudyAmberAccent else Color.Black
            )
            .clip(RoundedCornerShape(12.dp))
            .background(boardTheme.scorepadPaper)
            .border(
                width = if (isTurn) 2.dp else 1.dp,
                color = if (isTurn) StudyAmberAccent else boardTheme.scorepadBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // Left: Avatar & Player Details
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Miniature Plinth Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (color == PieceColor.WHITE) Color(0xFFFBF8F2) else Color(0xFF38251A))
                    .border(
                        width = 1.dp,
                        color = if (isTurn) StudyAmberAccent else boardTheme.scorepadBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                PieceView(
                    type = if (isAI) PieceType.KNIGHT else PieceType.KING,
                    color = color,
                    pieceTheme = pieceTheme,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = boardTheme.scorepadInk
                    )

                    if (materialAdvantage > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudyAmberAccent.copy(alpha = 0.25f))
                                .border(1.dp, StudyAmberAccent, RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "+$materialAdvantage",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = boardTheme.scorepadInk
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = StudyParchmentInkFaded
                )

                // Captured pieces tray
                if (sortedCaptures.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(16.dp)
                    ) {
                        sortedCaptures.take(8).forEachIndexed { idx, pieceType ->
                            PieceView(
                                type = pieceType,
                                color = capturedColor,
                                pieceTheme = pieceTheme,
                                modifier = Modifier
                                    .size(16.dp)
                                    .offset(x = (-3 * idx).dp)
                            )
                        }
                        if (sortedCaptures.size > 8) {
                            Text(
                                text = "+${sortedCaptures.size - 8}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyParchmentInkFaded,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Right: Turn & Status Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isInCheck) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudyWaxSealRed.copy(alpha = 0.15f))
                        .border(1.5.dp, StudyWaxSealRed, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "⚠️", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "CHECK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = StudyWaxSealRed
                    )
                }
            }

            if (isThinking) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudyAmberAccent.copy(alpha = 0.15f))
                        .border(1.dp, StudyAmberAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = StudyAmberAccent,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "THINKING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = boardTheme.scorepadInk
                    )
                }
            } else if (isTurn && !isInCheck && timerSeconds == null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StudyAmberAccent.copy(alpha = 0.25f))
                        .border(1.dp, StudyAmberAccent, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "YOUR MOVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = boardTheme.scorepadInk
                    )
                }
            }

            if (timerSeconds != null) {
                val mins = timerSeconds / 60
                val secs = timerSeconds % 60
                val timeStr = "%02d:%02d".format(mins, secs)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isTurn) StudyAmberAccent.copy(alpha = 0.25f) else Color(0x142B2118))
                        .border(
                            width = 1.dp,
                            color = if (isTurn) StudyAmberAccent else boardTheme.scorepadBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = boardTheme.scorepadInk
                    )
                }
            }
        }
    }
}
