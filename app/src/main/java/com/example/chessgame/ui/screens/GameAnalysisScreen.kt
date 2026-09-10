package com.example.chessgame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.ai.*
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.*
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.ChessBoardView

enum class AnalysisTab {
    SUMMARY,
    MOVES,
    KEY_MOMENTS
}

/**
 * Game Analysis Screen matching allpagelayout.png Screens 12, 13, 14:
 * - 3 top pill tabs: [Summary] | [Moves] | [Key Moments]
 * - Screen 12 (Summary): Game Result card with accuracy %, View Move Analysis button, Move Breakdown counts
 * - Screen 13 (Moves Detail): Move header, quality badge, eval swing, explanation with best move, board, |◀ ◀ ▶ ▶| bar
 * - Screen 14 (Key Moments): List of turning point cards with 1-tap jump to Moves tab
 */
@Composable
fun GameAnalysisScreen(
    moves: List<Move>,
    outcome: GameOutcome,
    opponentName: String,
    difficulty: AIDifficulty?,
    gameDurationSeconds: Int,
    humanColor: PieceColor,
    boardTheme: BoardTheme,
    pieceTheme: PieceTheme,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf(AnalysisTab.SUMMARY) }
    var analysisReport by remember { mutableStateOf<GameAnalysisReport?>(null) }
    var analysisProgress by remember { mutableFloatStateOf(0f) }
    var isAnalyzing by remember { mutableStateOf(true) }

    // -1 = starting position before move 1
    var currentMoveIndex by remember { mutableIntStateOf(if (moves.isNotEmpty()) 0 else -1) }

    // Run analysis asynchronously
    LaunchedEffect(moves) {
        isAnalyzing = true
        analysisReport = GameAnalyzer.analyzeGameAsync(moves) { p ->
            analysisProgress = p
        }
        isAnalyzing = false
    }

    val report = analysisReport
    val currentMoveAnalysis = if (report != null && currentMoveIndex in report.moves.indices) {
        report.moves[currentMoveIndex]
    } else null

    val currentBoardState = remember(currentMoveIndex, report) {
        if (currentMoveIndex == -1 || report == null || report.moves.isEmpty()) {
            createInitialGameState()
        } else {
            report.moves[currentMoveIndex.coerceIn(0, report.moves.size - 1)].stateAfterMove
        }
    }

    val lastMove = if (currentMoveIndex >= 0 && moves.isNotEmpty()) {
        moves[currentMoveIndex.coerceIn(0, moves.size - 1)]
    } else null

    val currentEvalCp = currentMoveAnalysis?.evalAfterCp ?: 0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF241A13),
                        Color(0xFF16100C),
                        Color(0xFF0C0806)
                    ),
                    radius = 1200f
                )
            )
    ) {
        if (isAnalyzing) {
            // Loading Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    progress = { analysisProgress },
                    color = StudyAmberAccent,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "ANALYSING GAME...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = StudyParchmentCream
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${(analysisProgress * 100).toInt()}% • Calculating evaluations & tactics",
                    fontSize = 12.sp,
                    color = Color(0xFFAFA293)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Header Bar (Visually Centered)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onBack()
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(36.dp)
                    ) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                    }

                    Text(
                        text = "Game Analysis",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = StudyParchmentCream
                    )

                    IconButton(
                        onClick = { SoundManager.playClick() },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                    ) {
                        Text("☰", fontSize = 18.sp, color = StudyParchmentCream)
                    }
                }

                // 3 Top Tabs: [Summary] | [Moves] | [Key Moments]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x18FFFFFF))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AnalysisTabButton(
                        label = "Summary",
                        isSelected = activeTab == AnalysisTab.SUMMARY,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            SoundManager.playClick()
                            activeTab = AnalysisTab.SUMMARY
                        }
                    )
                    AnalysisTabButton(
                        label = "Moves",
                        isSelected = activeTab == AnalysisTab.MOVES,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            SoundManager.playClick()
                            activeTab = AnalysisTab.MOVES
                        }
                    )
                    AnalysisTabButton(
                        label = "Key Moments",
                        isSelected = activeTab == AnalysisTab.KEY_MOMENTS,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            SoundManager.playClick()
                            activeTab = AnalysisTab.KEY_MOMENTS
                        }
                    )
                }

                // Tab Content Switcher
                when (activeTab) {
                    AnalysisTab.SUMMARY -> {
                        SummaryTabView(
                            report = report,
                            outcome = outcome,
                            opponentName = opponentName,
                            difficulty = difficulty,
                            gameDurationSeconds = gameDurationSeconds,
                            humanColor = humanColor,
                            totalMoves = moves.size,
                            onViewMoveAnalysis = {
                                activeTab = AnalysisTab.MOVES
                                currentMoveIndex = 0
                            }
                        )
                    }
                    AnalysisTab.MOVES -> {
                        MovesDetailTabView(
                            moves = moves,
                            currentMoveIndex = currentMoveIndex,
                            currentMoveAnalysis = currentMoveAnalysis,
                            currentBoardState = currentBoardState,
                            currentEvalCp = currentEvalCp,
                            lastMove = lastMove,
                            humanColor = humanColor,
                            boardTheme = boardTheme,
                            pieceTheme = pieceTheme,
                            onFirst = { currentMoveIndex = -1; SoundManager.playClick() },
                            onPrev = { if (currentMoveIndex > -1) currentMoveIndex--; SoundManager.playClick() },
                            onNext = { if (currentMoveIndex < moves.size - 1) currentMoveIndex++; SoundManager.playClick() },
                            onLast = { currentMoveIndex = moves.size - 1; SoundManager.playClick() }
                        )
                    }
                    AnalysisTab.KEY_MOMENTS -> {
                        KeyMomentsTabView(
                            moments = report?.keyMoments ?: emptyList(),
                            onSelectMoment = { idx ->
                                currentMoveIndex = idx
                                activeTab = AnalysisTab.MOVES
                                SoundManager.playClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisTabButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) StudyAmberAccent else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) Color(0xFF160E05) else StudyParchmentCream
        )
    }
}

/**
 * Screen 12: Summary Tab View
 */
@Composable
private fun SummaryTabView(
    report: GameAnalysisReport?,
    outcome: GameOutcome,
    opponentName: String,
    difficulty: AIDifficulty?,
    gameDurationSeconds: Int,
    humanColor: PieceColor,
    totalMoves: Int,
    onViewMoveAnalysis: () -> Unit
) {
    val isWin = outcome.winner == humanColor
    val isDraw = outcome.isDraw || outcome.winner == null
    val resultText = when {
        isWin -> "You Win"
        isDraw -> "Draw"
        else -> "$opponentName Won"
    }

    val accuracy = if (humanColor == PieceColor.WHITE) {
        report?.whiteAccuracy ?: 85
    } else {
        report?.blackAccuracy ?: 85
    }

    val durationFormatted = "%02d:%02d".format(gameDurationSeconds / 60, gameDurationSeconds % 60)
    val oppDisplay = if (difficulty != null) "AI (${difficulty.name.lowercase().replaceFirstChar { it.uppercase() }})" else opponentName

    val wBrilliant = report?.whiteBrilliantCount ?: 0
    val wBest = report?.whiteBestCount ?: 0
    val wGood = report?.whiteGoodCount ?: 0
    val wInacc = report?.whiteInaccuracyCount ?: 0
    val wMistake = report?.whiteMistakeCount ?: 0
    val wBlunder = report?.whiteBlunderCount ?: 0

    val bBrilliant = report?.blackBrilliantCount ?: 0
    val bBest = report?.blackBestCount ?: 0
    val bGood = report?.blackGoodCount ?: 0
    val bInacc = report?.blackInaccuracyCount ?: 0
    val bMistake = report?.blackMistakeCount ?: 0
    val bBlunder = report?.blackBlunderCount ?: 0

    val brilliantCount = if (humanColor == PieceColor.WHITE) wBrilliant else bBrilliant
    val bestCount = if (humanColor == PieceColor.WHITE) wBest else bBest
    val goodCount = if (humanColor == PieceColor.WHITE) wGood else bGood
    val inaccCount = if (humanColor == PieceColor.WHITE) wInacc else bInacc
    val mistakeCount = if (humanColor == PieceColor.WHITE) wMistake else bMistake
    val blunderCount = if (humanColor == PieceColor.WHITE) wBlunder else bBlunder

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Game Result Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF261C14), Color(0xFF1A120D))
                        )
                    )
                    .border(1.dp, Color(0x35D4A373), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Game Result",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFAFA293)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = resultText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWin) Color(0xFF66BB6A) else if (isDraw) StudyAmberAccent else Color(0xFFEF5350)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Opponent: ", fontSize = 11.sp, color = Color(0xFFAFA293))
                                Text(oppDisplay, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Moves: ", fontSize = 11.sp, color = Color(0xFFAFA293))
                                Text("$totalMoves", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Duration: ", fontSize = 11.sp, color = Color(0xFFAFA293))
                                Text(durationFormatted, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Accuracy",
                                fontSize = 10.sp,
                                color = Color(0xFFAFA293)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$accuracy%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = StudyAmberAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            SoundManager.playClick()
                            onViewMoveAnalysis()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudyAmberAccent,
                            contentColor = Color(0xFF140D05)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = StudyAmberAccent)
                    ) {
                        Text(
                            text = "View Move Analysis",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // 2. Move Breakdown Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C130D))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Move Breakdown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentCream
                    )

                    BreakdownRow("✦", "Brilliant", brilliantCount, Color(0xFF26C6DA))
                    BreakdownRow("★", "Best Moves", bestCount, Color(0xFF66BB6A))
                    BreakdownRow("✓", "Good Moves", goodCount, Color(0xFF9CCC65))
                    BreakdownRow("?!", "Inaccuracies", inaccCount, Color(0xFFFFEE58))
                    BreakdownRow("?", "Mistakes", mistakeCount, Color(0xFFFFA726))
                    BreakdownRow("??", "Blunders", blunderCount, Color(0xFFEF5350))
                }
            }
        }
    }
}

@Composable
private fun BreakdownRow(
    symbol: String,
    name: String,
    count: Int,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x10FFFFFF))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.22f))
                    .border(1.dp, badgeColor, CircleShape)
            ) {
                Text(
                    text = symbol,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = badgeColor
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = StudyParchmentCream
            )
        }

        Text(
            text = "$count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = StudyParchmentCream
        )
    }
}

/**
 * Screen 13: Move Analysis (Detail) Tab View
 */
@Composable
private fun MovesDetailTabView(
    moves: List<Move>,
    currentMoveIndex: Int,
    currentMoveAnalysis: MoveAnalysis?,
    currentBoardState: GameState,
    currentEvalCp: Int,
    lastMove: Move?,
    humanColor: PieceColor,
    boardTheme: BoardTheme,
    pieceTheme: PieceTheme,
    onFirst: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLast: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
            ) {
                Text(
                    text = if (currentMoveIndex == -1) "Starting Position" else "Move ${currentMoveIndex + 1} of ${moves.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAFA293)
                )
            }
        }

        item {
            MoveDetailCard(analysis = currentMoveAnalysis)
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(16.dp, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                ChessBoardView(
                    state = currentBoardState,
                    flipped = humanColor == PieceColor.BLACK,
                    isInteractable = false,
                    showCoordinates = true,
                    highlightMoves = false,
                    boardTheme = boardTheme,
                    pieceTheme = pieceTheme,
                    lastMove = lastMove,
                    onMove = {}
                )
            }
        }

        item {
            EvaluationBar(evalCp = currentEvalCp)
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E150F))
                    .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onFirst,
                    enabled = currentMoveIndex > -1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("|◀", fontSize = 14.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                IconButton(
                    onClick = onPrev,
                    enabled = currentMoveIndex > -1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("◀", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                IconButton(
                    onClick = onNext,
                    enabled = currentMoveIndex < moves.size - 1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("▶", fontSize = 16.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                IconButton(
                    onClick = onLast,
                    enabled = currentMoveIndex < moves.size - 1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("▶|", fontSize = 14.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }
            }
        }
    }
}

@Composable
private fun MoveDetailCard(analysis: MoveAnalysis?) {
    if (analysis == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF221710))
                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Initial setup. Use navigation buttons below to step through moves.",
                fontSize = 11.sp,
                color = Color(0xFFAFA293)
            )
        }
        return
    }

    val badgeColor = when (analysis.classification) {
        MoveClassification.BRILLIANT -> Color(0xFF26C6DA)
        MoveClassification.BEST -> Color(0xFF66BB6A)
        MoveClassification.STRONG -> Color(0xFFD4A373)
        MoveClassification.GOOD -> Color(0xFF9CCC65)
        MoveClassification.BOOK -> Color(0xFF81D4FA)
        MoveClassification.INACCURACY -> Color(0xFFFFEE58)
        MoveClassification.MISTAKE -> Color(0xFFFFA726)
        MoveClassification.BLUNDER -> Color(0xFFEF5350)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF221710))
            .border(1.5.dp, badgeColor.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = analysis.san,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = StudyParchmentCream
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (analysis.moveColor == PieceColor.WHITE) "(White)" else "(Black)",
                                fontSize = 11.sp,
                                color = Color(0xFFAFA293)
                            )
                        }
                        val moveDesc = GameAnalyzer.describeSan(analysis.san)
                        if (moveDesc.isNotEmpty()) {
                            Text(
                                text = moveDesc,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = StudyAmberAccent.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${analysis.classification.icon} ${analysis.classification.label}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF140D06)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val beforeStr = GameAnalyzer.formatEval(analysis.evalBeforeCp)
            val afterStr = GameAnalyzer.formatEval(analysis.evalAfterCp)
            Text(
                text = "Evaluation: $beforeStr → $afterStr",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = StudyAmberAccent
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = analysis.explanation,
                fontSize = 12.sp,
                color = StudyParchmentCream,
                lineHeight = 16.sp
            )

            if (analysis.bestEngineSan.isNotEmpty() &&
                analysis.classification != MoveClassification.BEST &&
                analysis.classification != MoveClassification.BOOK
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x18E5A93C))
                        .border(1.dp, Color(0x30E5A93C), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Best Move: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyAmberAccent
                    )
                    val bestDesc = GameAnalyzer.describeSan(analysis.bestEngineSan)
                    val bestText = if (bestDesc.isNotEmpty()) "${analysis.bestEngineSan} ($bestDesc)" else analysis.bestEngineSan
                    Text(
                        text = bestText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = StudyParchmentCream
                    )
                }
            }
        }
    }
}

/**
 * Screen 14: Key Moments Tab View
 */
@Composable
private fun KeyMomentsTabView(
    moments: List<KeyMoment>,
    onSelectMoment: (Int) -> Unit
) {
    if (moments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No critical key moments detected in this match.",
                fontSize = 13.sp,
                color = Color(0xFFAFA293)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(moments) { moment ->
            val badgeColor = when (moment.classification) {
                MoveClassification.BRILLIANT -> Color(0xFF26C6DA)
                MoveClassification.BEST -> Color(0xFF66BB6A)
                MoveClassification.STRONG -> Color(0xFFD4A373)
                MoveClassification.GOOD -> Color(0xFF9CCC65)
                MoveClassification.BOOK -> Color(0xFF81D4FA)
                MoveClassification.INACCURACY -> Color(0xFFFFEE58)
                MoveClassification.MISTAKE -> Color(0xFFFFA726)
                MoveClassification.BLUNDER -> Color(0xFFEF5350)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF221710))
                    .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { onSelectMoment(moment.moveIndex) }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor, CircleShape)
                        ) {
                            Text(
                                text = moment.classification.icon,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = badgeColor
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = moment.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudyParchmentCream
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = moment.description,
                                fontSize = 11.sp,
                                color = Color(0xFFAFA293),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Text(
                        text = "VIEW →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyAmberAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun EvaluationBar(evalCp: Int) {
    val clamped = evalCp.coerceIn(-1000, 1000)
    val whiteRatio = (0.5f + (clamped / 2000f)).coerceIn(0.05f, 0.95f)
    val evalText = GameAnalyzer.formatEval(evalCp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "EVALUATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color(0xFFAFA293)
            )
            Text(
                text = evalText,
                fontSize = 12.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (evalCp >= 0) StudyParchmentCream else Color(0xFFFF9E80)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF332319))
                .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(whiteRatio)
                    .fillMaxHeight()
                    .background(Color(0xFFF7F3EB))
            )
        }
    }
}
