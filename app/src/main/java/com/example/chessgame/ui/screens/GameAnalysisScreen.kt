package com.example.chessgame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
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
 * Grandmaster Arena Game Analysis Screen.
 * Tactical debrief suite featuring:
 * - 3 top tabs: [Summary] | [Moves] | [Key Moments]
 * - Summary: Combat result card with Accuracy %, Telemetry jump button, Tactical Breakdown counts
 * - Moves Detail: Move header, classification badge, evaluation swing, best move recommendation, board view, |◀ ◀ ▶ ▶| stepper
 * - Key Moments: List of turning point cards with 1-tap jump to Moves tab
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

    ArenaBackgroundScaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            ArenaHeader(
                title = "TACTICAL ANALYSIS",
                subtitle = "DEBRIEF & ENGINE ACCURACY",
                onBack = onBack,
                rightContent = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ArenaColors.TitaniumSurface)
                            .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_game_emblem),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        if (isAnalyzing) {
            // Loading Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    progress = { analysisProgress },
                    color = ArenaColors.CyberCyan,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "COMPUTING TELEMETRY...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = ArenaColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${(analysisProgress * 100).toInt()}% • Calculating minimax swings & accuracy",
                    fontSize = 12.sp,
                    color = ArenaColors.TextSecondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 3 Top Tabs: [Summary] | [Moves] | [Key Moments]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(20.dp))
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
            .background(if (isSelected) ArenaColors.CyberCyan else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
            color = if (isSelected) Color(0xFF05080E) else ArenaColors.TextSecondary
        )
    }
}

/**
 * Summary Tab View: Combat Outcome, Accuracy, & Move Breakdown
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
        isWin -> "ARENA VICTORY"
        isDraw -> "TACTICAL DRAW"
        else -> "$opponentName WON"
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
        // 1. Combat Result Card
        item {
            ArenaCard(
                modifier = Modifier.fillMaxWidth(),
                isHighlighted = isWin,
                highlightColor = ArenaColors.CyberCyan
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = "ENGAGEMENT OUTCOME",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ArenaColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = resultText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        color = if (isWin) ArenaColors.SolarAmber else if (isDraw) ArenaColors.CyberCyan else ArenaColors.CrimsonAlert
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Opponent: ", fontSize = 11.sp, color = ArenaColors.TextMuted)
                                Text(oppDisplay, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Plies: ", fontSize = 11.sp, color = ArenaColors.TextMuted)
                                Text("$totalMoves", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Duration: ", fontSize = 11.sp, color = ArenaColors.TextMuted)
                                Text(durationFormatted, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ACCURACY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArenaColors.TextMuted
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$accuracy%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = ArenaColors.CyberCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ArenaButton(
                        text = "LAUNCH MOVE TELEMETRY",
                        icon = "🔍",
                        isPrimary = true,
                        onClick = {
                            SoundManager.playClick()
                            onViewMoveAnalysis()
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    )
                }
            }
        }

        // 2. Move Breakdown Section
        item {
            ArenaCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "TACTICAL BREAKDOWN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = ArenaColors.TextPrimary
                    )

                    BreakdownRow("✦", "Brilliant Moves", brilliantCount, Color(0xFF00E5FF))
                    BreakdownRow("★", "Best Moves", bestCount, Color(0xFF00E676))
                    BreakdownRow("✓", "Good Moves", goodCount, Color(0xFF69F0AE))
                    BreakdownRow("?!", "Inaccuracies", inaccCount, Color(0xFFFFD700))
                    BreakdownRow("?", "Mistakes", mistakeCount, Color(0xFFFF9100))
                    BreakdownRow("??", "Blunders", blunderCount, Color(0xFFFF3D00))
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
            .background(ArenaColors.TitaniumSurface)
            .border(1.dp, ArenaColors.TitaniumBorderSubtle, RoundedCornerShape(8.dp))
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
                    .background(badgeColor.copy(alpha = 0.2f))
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
                color = ArenaColors.TextPrimary
            )
        }

        Text(
            text = "$count",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = ArenaColors.TextPrimary
        )
    }
}

/**
 * Move Analysis (Detail) Tab View
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
                    text = if (currentMoveIndex == -1) "Initial Battle Line" else "Move ${currentMoveIndex + 1} of ${moves.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArenaColors.TextSecondary
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
                    .shadow(24.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF000000))
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp)),
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
                    .background(ArenaColors.TitaniumSurface)
                    .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onFirst,
                    enabled = currentMoveIndex > -1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("|◀", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                }

                IconButton(
                    onClick = onPrev,
                    enabled = currentMoveIndex > -1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("◀", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                }

                IconButton(
                    onClick = onNext,
                    enabled = currentMoveIndex < moves.size - 1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("▶", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
                }

                IconButton(
                    onClick = onLast,
                    enabled = currentMoveIndex < moves.size - 1,
                    modifier = Modifier.size(38.dp)
                ) {
                    Text("▶|", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ArenaColors.TextPrimary)
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
                .background(ArenaColors.TitaniumSurface)
                .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Initial setup. Use navigation buttons below to step through telemetry.",
                fontSize = 11.sp,
                color = ArenaColors.TextMuted
            )
        }
        return
    }

    val badgeColor = when (analysis.classification) {
        MoveClassification.BRILLIANT -> Color(0xFF00E5FF)
        MoveClassification.BEST -> Color(0xFF00E676)
        MoveClassification.STRONG -> Color(0xFF69F0AE)
        MoveClassification.GOOD -> Color(0xFF81D4FA)
        MoveClassification.BOOK -> Color(0xFF4FC3F7)
        MoveClassification.INACCURACY -> Color(0xFFFFD700)
        MoveClassification.MISTAKE -> Color(0xFFFF9100)
        MoveClassification.BLUNDER -> Color(0xFFFF3D00)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArenaColors.TitaniumSurface)
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
                                color = ArenaColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (analysis.moveColor == PieceColor.WHITE) "(White)" else "(Black)",
                                fontSize = 11.sp,
                                color = ArenaColors.TextSecondary
                            )
                        }
                        val moveDesc = GameAnalyzer.describeSan(analysis.san)
                        if (moveDesc.isNotEmpty()) {
                            Text(
                                text = moveDesc,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ArenaColors.CyberCyan
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
                        color = Color(0xFF070B10)
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
                color = ArenaColors.SolarAmber
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = analysis.explanation,
                fontSize = 12.sp,
                color = ArenaColors.TextPrimary,
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
                        .background(ArenaColors.CyberCyan.copy(alpha = 0.15f))
                        .border(1.dp, ArenaColors.CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Optimal Move: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.CyberCyan
                    )
                    val bestDesc = GameAnalyzer.describeSan(analysis.bestEngineSan)
                    val bestText = if (bestDesc.isNotEmpty()) "${analysis.bestEngineSan} ($bestDesc)" else analysis.bestEngineSan
                    Text(
                        text = bestText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = ArenaColors.TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Key Moments Tab View
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
                color = ArenaColors.TextMuted
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
                MoveClassification.BRILLIANT -> Color(0xFF00E5FF)
                MoveClassification.BEST -> Color(0xFF00E676)
                MoveClassification.STRONG -> Color(0xFF69F0AE)
                MoveClassification.GOOD -> Color(0xFF81D4FA)
                MoveClassification.BOOK -> Color(0xFF4FC3F7)
                MoveClassification.INACCURACY -> Color(0xFFFFD700)
                MoveClassification.MISTAKE -> Color(0xFFFF9100)
                MoveClassification.BLUNDER -> Color(0xFFFF3D00)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ArenaColors.TitaniumSurface)
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
                                color = ArenaColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = moment.description,
                                fontSize = 11.sp,
                                color = ArenaColors.TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Text(
                        text = "INSPECT ›",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.CyberCyan
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
                text = "ENGINE EVALUATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = ArenaColors.TextMuted
            )
            Text(
                text = evalText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (evalCp >= 0) ArenaColors.CyberCyan else ArenaColors.CrimsonAlert
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF0A0F16))
                .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(whiteRatio)
                    .fillMaxHeight()
                    .background(Color(0xFFE2EDF8))
            )
        }
    }
}
