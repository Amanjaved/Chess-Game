package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import com.example.chessgame.engine.GameOutcome
import com.example.chessgame.engine.OutcomeReason
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.theme.*

@Composable
fun GameOverDialog(
    outcome: GameOutcome,
    humanColor: PieceColor,
    isAIMode: Boolean,
    moveCount: Int,
    onAnalyseGame: (() -> Unit)? = null,
    onPlayAgain: () -> Unit,
    onReviewBoard: () -> Unit,
    onMainMenu: () -> Unit
) {
    val isWinner = if (isAIMode) outcome.winner == humanColor else outcome.winner != null && !outcome.isDraw
    val isDraw = outcome.isDraw

    val title = when (outcome.reason) {
        OutcomeReason.CHECKMATE -> "CHECKMATE"
        OutcomeReason.STALEMATE -> "STALEMATE"
        OutcomeReason.INSUFFICIENT_MATERIAL,
        OutcomeReason.FIFTY_MOVE_RULE,
        OutcomeReason.THREEFOLD_REPETITION,
        OutcomeReason.MUTUAL_AGREEMENT -> "DRAW"
        OutcomeReason.RESIGNATION -> "RESIGNED"
        null -> "GAME OVER"
    }

    val subtitle = when (outcome.reason) {
        OutcomeReason.CHECKMATE -> {
            if (isAIMode) {
                if (outcome.winner == humanColor) "YOU WIN!" else "AI WINS!"
            } else {
                if (outcome.winner == PieceColor.WHITE) "WHITE WINS!" else "BLACK WINS!"
            }
        }
        OutcomeReason.STALEMATE -> "Game drawn by stalemate"
        OutcomeReason.INSUFFICIENT_MATERIAL -> "Draw by insufficient material"
        OutcomeReason.FIFTY_MOVE_RULE -> "Draw by 50-move rule"
        OutcomeReason.THREEFOLD_REPETITION -> "Draw by threefold repetition"
        OutcomeReason.MUTUAL_AGREEMENT -> "Draw agreed by both players"
        OutcomeReason.RESIGNATION -> {
            if (isAIMode) {
                if (outcome.winner == humanColor) "AI Resigned — You Win!" else "You Resigned — AI Wins!"
            } else {
                "${if (outcome.winner == PieceColor.WHITE) "White" else "Black"} wins by resignation"
            }
        }
        null -> "Match concluded"
    }

    Dialog(onDismissRequest = { /* Must choose an action */ }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF131C2D), Color(0xFF0C1320), Color(0xFF070A10))
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = if (isWinner) Color(0xFFFFB703) else if (isDraw) Color(0xFF38BDF8) else Color(0xFF26354E),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Large Trophy or Crown Emblem
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isWinner) Color(0x30FFB703) else if (isDraw) Color(0x3038BDF8) else Color(0x30FF5252))
                        .border(1.dp, if (isWinner) Color(0x66FFB703) else if (isDraw) Color(0x6638BDF8) else Color(0x66FF5252), RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = if (isWinner) "🏆" else if (isDraw) "🤝" else "⚔️",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp,
                    fontFamily = GameFont,
                    color = if (isWinner) Color(0xFFFFB703) else if (isDraw) Color(0xFF38BDF8) else Color(0xFFF8FAFC)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = GameFont,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0D1420))
                        .border(1.dp, Color(0xFF1E2B3E), RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = GameFont, color = Color(0xFF64748B))
                        Text(text = "$moveCount", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = GameFont, color = Color(0xFFF8FAFC))
                    }
                    Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFF1E2B3E)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "RESULT", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = GameFont, color = Color(0xFF64748B))
                        Text(
                            text = if (outcome.isDraw) "½ - ½" else if (outcome.winner == PieceColor.WHITE) "1 - 0" else "0 - 1",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = GameFont,
                            color = if (isWinner) Color(0xFF10B981) else if (isDraw) Color(0xFF38BDF8) else Color(0xFFFF5252)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                if (onAnalyseGame != null) {
                    Button(
                        onClick = onAnalyseGame,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB703)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔍 ", fontSize = 14.sp)
                            Text("ANALYSE GAME", fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = GameFont, color = Color(0xFF1B140E))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = if (onAnalyseGame == null) Color(0xFFFFB703) else Color(0xFF162133)),
                    border = if (onAnalyseGame != null) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)) else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(
                        "PLAY AGAIN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = GameFont,
                        color = if (onAnalyseGame == null) Color(0xFF111418) else Color(0xFFF8FAFC)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReviewBoard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Review", color = Color(0xFF94A3B8), fontFamily = GameFont, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onMainMenu,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26354E)),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Menu", color = Color(0xFF94A3B8), fontFamily = GameFont, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
