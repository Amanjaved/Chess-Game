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
 * Illustrated Parchment Move History Ledger.
 * Styled as a player's official scoresheet with:
 * - Move numbers (e.g. 18.)
 * - White and Black move SAN
 * - Tap-to-jump capability
 * - Dynamic indicators (✓, !, +, #)
 * - Current move highlight
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
                .shadow(28.dp, RoundedCornerShape(20.dp), spotColor = Color(0xCC000000))
                .clip(RoundedCornerShape(20.dp))
                .background(StudyParchmentCream)
                .border(2.dp, StudyTableFrame, RoundedCornerShape(20.dp))
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
                            text = "MOVE RECORD",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = StudyParchmentInk
                        )
                        Text(
                            text = "${moves.size} plies recorded • Tap move to inspect",
                            fontSize = 11.sp,
                            color = StudyParchmentInkFaded
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
                            .background(Color(0x18000000))
                    ) {
                        Text(
                            text = "✕",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudyParchmentInk
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Column Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x182B2118))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentInkFaded,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = "WHITE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentInk,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "BLACK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentInk,
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
                            text = "No moves made yet.\nBegin the match by moving White.",
                            fontSize = 13.sp,
                            color = StudyParchmentInkFaded,
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
                                        if (pair.number % 2 == 0) Color(0x0A000000) else Color.Transparent
                                    )
                                    .padding(vertical = 4.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Move Number
                                Text(
                                    text = "${pair.number}.",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = StudyParchmentInkFaded,
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
                                            if (isWhiteActive) Color(0x35D4A373) else Color.Transparent
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
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isWhiteActive) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (isWhiteActive) Color(0xFF6E4314) else StudyParchmentInk
                                    )
                                    Text(
                                        text = whiteIndicator,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (whiteIndicator == "#") Color(0xFFC62828) else StudyParchmentInkFaded
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
                                                if (isBlackActive) Color(0x35D4A373) else Color.Transparent
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
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (isBlackActive) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (isBlackActive) Color(0xFF6E4314) else StudyParchmentInk
                                        )
                                        Text(
                                            text = blackIndicator,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (blackIndicator == "#") Color(0xFFC62828) else StudyParchmentInkFaded
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
                androidx.compose.material3.Button(
                    onClick = {
                        SoundManager.playClick()
                        onClose()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(
                        text = "CLOSE RECORD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color(0xFF1B140E)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "“Every move tells a story.”",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = StudyParchmentInkFaded,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
