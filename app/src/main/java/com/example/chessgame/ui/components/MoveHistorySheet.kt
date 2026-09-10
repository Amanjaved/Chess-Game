package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.chessgame.engine.Move
import com.example.chessgame.theme.*

/**
 * Tactical Combat Log (Move History Ledger).
 * Displays full SAN record, ply numbers, capture/check indicators, and current step highlight.
 */
@Composable
fun MoveHistorySheet(
    moves: List<Move>,
    currentMoveIndex: Int? = null,
    onJumpToMove: ((Int) -> Unit)? = null,
    onClose: () -> Unit
) {
    val listState = rememberLazyListState()

    // Group into pairs: (moveNumber, (whiteIndex, whiteMove), (blackIndex, blackMove)?)
    data class MovePair(
        val number: Int,
        val whiteIdx: Int,
        val whiteMove: Move,
        val blackIdx: Int?,
        val blackMove: Move?
    )

    val pairs = mutableListOf<MovePair>()
    var i = 0
    while (i < moves.size) {
        val num = (i / 2) + 1
        val wMove = moves[i]
        val bMove = if (i + 1 < moves.size) moves[i + 1] else null
        pairs.add(
            MovePair(
                number = num,
                whiteIdx = i,
                whiteMove = wMove,
                blackIdx = if (bMove != null) i + 1 else null,
                blackMove = bMove
            )
        )
        i += 2
    }

    val activeIdx = currentMoveIndex ?: (moves.size - 1)
    val activePairRow = (activeIdx / 2).coerceAtLeast(0)

    LaunchedEffect(moves.size, activeIdx) {
        if (pairs.isNotEmpty() && activePairRow < pairs.size) {
            listState.animateScrollToItem(activePairRow)
        }
    }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 540.dp)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0xDD000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF141A25),
                            Color(0xFF0C1018),
                            Color(0xFF080B10)
                        )
                    )
                )
                .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Scoresheet Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COMBAT LOG",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "${moves.size} plies recorded • Real-time telemetry",
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
                        Text(
                            text = "✕",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArenaColors.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Column Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.TextMuted,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = "WHITE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "BLACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (pairs.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Text(
                            text = "No tactical moves recorded yet.\nDeploy forces to initiate telemetry.",
                            fontSize = 13.sp,
                            color = ArenaColors.TextMuted,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        itemsIndexed(pairs) { _, pair ->
                            val isWhiteActive = pair.whiteIdx == activeIdx
                            val isBlackActive = pair.blackIdx == activeIdx

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (pair.number % 2 == 0) Color(0x0CFFFFFF) else Color.Transparent
                                    )
                                    .padding(vertical = 4.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Move Number
                                Text(
                                    text = "${pair.number}.",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = ArenaColors.TextMuted,
                                    modifier = Modifier.width(36.dp)
                                )

                                // White Move Cell
                                val whiteIndicator = when {
                                    pair.whiteMove.san.contains("#") -> "#"
                                    pair.whiteMove.san.contains("+") -> "+"
                                    pair.whiteMove.captured != null -> "⚔"
                                    else -> "✓"
                                }

                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isWhiteActive) ArenaColors.CyberCyan.copy(alpha = 0.2f) else Color.Transparent
                                        )
                                        .border(
                                            width = if (isWhiteActive) 1.dp else 0.dp,
                                            color = if (isWhiteActive) ArenaColors.CyberCyan else Color.Transparent,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            SoundManager.playClick()
                                            onJumpToMove?.invoke(pair.whiteIdx)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = pair.whiteMove.san.ifEmpty { "..." },
                                        fontSize = 12.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isWhiteActive) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (isWhiteActive) ArenaColors.CyberCyan else ArenaColors.TextPrimary
                                    )
                                    Text(
                                        text = whiteIndicator,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (whiteIndicator == "#") ArenaColors.CrimsonAlert else ArenaColors.TextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Black Move Cell
                                if (pair.blackMove != null && pair.blackIdx != null) {
                                    val blackIndicator = when {
                                        pair.blackMove.san.contains("#") -> "#"
                                        pair.blackMove.san.contains("+") -> "+"
                                        pair.blackMove.captured != null -> "⚔"
                                        else -> "✓"
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isBlackActive) ArenaColors.SolarAmber.copy(alpha = 0.2f) else Color.Transparent
                                            )
                                            .border(
                                                width = if (isBlackActive) 1.dp else 0.dp,
                                                color = if (isBlackActive) ArenaColors.SolarAmber else Color.Transparent,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                SoundManager.playClick()
                                                onJumpToMove?.invoke(pair.blackIdx)
                                            }
                                            .padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = pair.blackMove.san,
                                            fontSize = 12.5.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isBlackActive) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (isBlackActive) ArenaColors.SolarAmber else ArenaColors.TextPrimary
                                        )
                                        Text(
                                            text = blackIndicator,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (blackIndicator == "#") ArenaColors.CrimsonAlert else ArenaColors.TextMuted
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button
                ArenaButton(
                    text = "DISMISS LOG",
                    icon = "✕",
                    isPrimary = true,
                    onClick = {
                        SoundManager.playClick()
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )
            }
        }
    }
}
