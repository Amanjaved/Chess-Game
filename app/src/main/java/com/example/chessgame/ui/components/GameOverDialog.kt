package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
                if (outcome.winner == humanColor) "TACTICAL VICTORY" else "TACTICAL DEFEAT"
            } else {
                if (outcome.winner == PieceColor.WHITE) "WHITE FORCES VICTORIOUS" else "BLACK FORCES VICTORIOUS"
            }
        }
        OutcomeReason.STALEMATE -> "Gridlock by stalemate"
        OutcomeReason.INSUFFICIENT_MATERIAL -> "Draw by depleted material"
        OutcomeReason.FIFTY_MOVE_RULE -> "Draw by 50-move doctrine"
        OutcomeReason.THREEFOLD_REPETITION -> "Draw by threefold cycle"
        OutcomeReason.MUTUAL_AGREEMENT -> "Ceasefire agreed by both commanders"
        OutcomeReason.RESIGNATION -> {
            if (isAIMode) {
                if (outcome.winner == humanColor) "Enemy commander resigned" else "You surrendered the sector"
            } else {
                "${if (outcome.winner == PieceColor.WHITE) "White" else "Black"} triumphs by surrender"
            }
        }
        null -> "Engagement terminated"
    }

    val accentColor = if (isWinner) ArenaColors.EmeraldVictory else if (isDraw) ArenaColors.CyberCyan else ArenaColors.CrimsonAlert

    Dialog(onDismissRequest = { /* Must choose an action */ }) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(ArenaColors.TitaniumSurface)
                .border(
                    width = 1.5.dp,
                    color = accentColor.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = if (isWinner) "🏆" else if (isDraw) "🤝" else "⚔️",
                        fontSize = 30.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = accentColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = ArenaColors.TextSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Combat Telemetry Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaColors.TitaniumSurfaceRaised)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TOTAL PLIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted, letterSpacing = 1.sp)
                        Text(text = "$moveCount", fontSize = 18.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(ArenaColors.TitaniumBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "OUTCOME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted, letterSpacing = 1.sp)
                        Text(
                            text = if (outcome.isDraw) "½ - ½" else if (outcome.winner == PieceColor.WHITE) "1 - 0" else "0 - 1",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                if (onAnalyseGame != null) {
                    ArenaButton(
                        text = "DEBRIEF & ANALYZE",
                        icon = "🔍",
                        onClick = onAnalyseGame,
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                ArenaButton(
                    text = "RE-DEPLOY / REMATCH",
                    onClick = onPlayAgain,
                    isPrimary = (onAnalyseGame == null),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArenaButton(
                        text = "REVIEW",
                        onClick = onReviewBoard,
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )

                    ArenaButton(
                        text = "LOBBY",
                        onClick = onMainMenu,
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

