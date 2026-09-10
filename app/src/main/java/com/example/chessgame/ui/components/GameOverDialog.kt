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
                        listOf(Color(0xFF281C13), Color(0xFF19110B), Color(0xFF0F0A06))
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = if (isWinner) StudyAmberAccent else if (isDraw) Color(0xFF64B5F6) else StudyTableFrame,
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
                        .background(if (isWinner) StudyAmberAccent.copy(alpha = 0.2f) else if (isDraw) Color(0x3064B5F6) else Color(0x30E53935))
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
                    color = if (isWinner) StudyAmberAccent else StudyParchmentCream
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyParchmentCream.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudyParchmentCream)
                        .border(1.dp, StudyParchmentBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StudyParchmentInkFaded)
                        Text(text = "$moveCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StudyParchmentInk)
                    }
                    Box(modifier = Modifier.width(1.dp).height(28.dp).background(StudyParchmentBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "RESULT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = StudyParchmentInkFaded)
                        Text(
                            text = if (outcome.isDraw) "½ - ½" else if (outcome.winner == PieceColor.WHITE) "1 - 0" else "0 - 1",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isWinner) Color(0xFF2E7D32) else if (isDraw) Color(0xFF1565C0) else Color(0xFFC62828)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                if (onAnalyseGame != null) {
                    Button(
                        onClick = onAnalyseGame,
                        colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔍 ", fontSize = 14.sp)
                            Text("ANALYSE GAME", fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = Color(0xFF1B140E))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(containerColor = if (onAnalyseGame == null) StudyAmberAccent else Color(0xFF38291F)),
                    border = if (onAnalyseGame != null) androidx.compose.foundation.BorderStroke(1.dp, Color(0x44D4A373)) else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(
                        "PLAY AGAIN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (onAnalyseGame == null) Color(0xFF111418) else StudyParchmentCream
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
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Review", color = StudyParchmentCream, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onMainMenu,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Text("Menu", color = StudyParchmentCream, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
