package com.example.chessgame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.ai.MoveSuggestion
import com.example.chessgame.ai.MoveSuggestionEngine
import com.example.chessgame.ai.PIECE_VALUES
import com.example.chessgame.ai.calculateAIMoveAsync
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.audio.VoiceAnnouncer
import com.example.chessgame.engine.*
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 6: Chess Gameplay Screen.
 * Dominant physical tabletop board sitting on a wooden desk.
 * Features tournament parchment scorecards, compact wood & brass controls,
 * cinematic "YOUR MOVE" overlays, and dramatic match conclusion scenes.
 */
@Composable
fun GameScreen(
    mode: String, // "ai" or "local"
    aiDifficulty: AIDifficulty,
    humanColor: PieceColor,
    soundEnabled: Boolean,
    onSoundChanged: (Boolean) -> Unit,
    voiceEnabled: Boolean,
    onVoiceChanged: (Boolean) -> Unit,
    showCoords: Boolean,
    onShowCoordsChanged: (Boolean) -> Unit,
    highlightMoves: Boolean,
    onHighlightMovesChanged: (Boolean) -> Unit,
    autoFlipLocal: Boolean,
    onAutoFlipChanged: (Boolean) -> Unit,
    showMoveHints: Boolean = true,
    onShowMoveHintsChanged: ((Boolean) -> Unit)? = null,
    currentBoardTheme: BoardTheme,
    currentPieceTheme: PieceTheme,
    onOpenThemeGallery: () -> Unit,
    onBackToMenu: () -> Unit,
    player1Name: String = "Player 1",
    player2Name: String = "Player 2",
    onAnalyseGame: ((List<Move>, GameOutcome, Int, String, AIDifficulty?, PieceColor) -> Unit)? = null
) {
    var gameState by remember { mutableStateOf(createInitialGameState()) }
    val gameStateHistory = remember { mutableStateListOf(createInitialGameState()) }
    var isAIThinking by remember { mutableStateOf(false) }
    var outcome by remember { mutableStateOf(GameOutcome(isOver = false)) }

    // Move Hints & Best Move Suggestions State
    var showHints by remember { mutableStateOf(false) }
    var isCalculatingHints by remember { mutableStateOf(false) }
    var hintSuggestions by remember { mutableStateOf<List<MoveSuggestion>>(emptyList()) }
    var previewMove by remember { mutableStateOf<MoveSuggestion?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun toggleHints() {
        if (showHints) {
            showHints = false
            previewMove = null
        } else {
            if (isAIThinking || outcome.isOver) return
            showHints = true
            previewMove = null
            isCalculatingHints = true
            coroutineScope.launch {
                val startTime = System.currentTimeMillis()
                val suggestions = MoveSuggestionEngine.generateSuggestionsAsync(gameState, depth = 2)
                val elapsed = System.currentTimeMillis() - startTime
                val minWait = 250L
                if (elapsed < minWait) {
                    delay(minWait - elapsed)
                }
                hintSuggestions = suggestions
                isCalculatingHints = false
            }
        }
    }

    // Dialog & Overlay States
    var showGameOverOverlay by remember { mutableStateOf(false) }
    var showGamePauseMenu by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showHowToPlayDialog by remember { mutableStateOf(false) }
    var showYourMoveBanner by remember { mutableStateOf(false) }
    var confirmDialogType by remember { mutableStateOf<String?>(null) } // "restart", "resign", "draw", "back"

    // Match Duration Clock
    var gameDurationSeconds by remember { mutableStateOf(0) }
    var whiteTimeSeconds by remember { mutableStateOf(0) }
    var blackTimeSeconds by remember { mutableStateOf(0) }
    LaunchedEffect(gameState.turn, outcome.isOver) {
        while (!outcome.isOver) {
            delay(1000)
            gameDurationSeconds++
            if (gameState.turn == PieceColor.WHITE) {
                whiteTimeSeconds++
            } else {
                blackTimeSeconds++
            }
        }
    }

    val isAIMode = mode == "ai"
    val oppName = if (isAIMode) "AI (${aiDifficulty.name})" else player2Name
    val aiColor = humanColor.opponent()
    val isHumanTurn = if (isAIMode) gameState.turn == humanColor else true
    val isAITurn = isAIMode && gameState.turn == aiColor && !outcome.isOver

    // Board flip logic
    val isFlipped = if (isAIMode) {
        humanColor == PieceColor.BLACK
    } else {
        autoFlipLocal && gameState.turn == PieceColor.BLACK
    }

    // Announce game start
    LaunchedEffect(Unit) {
        VoiceAnnouncer.announceGameStart(isAIMode)
    }

    // Trigger "YOUR MOVE" subtle notice when turn becomes human in AI mode
    LaunchedEffect(gameState.turn) {
        if (isAIMode && gameState.turn == humanColor && !outcome.isOver && gameState.history.isNotEmpty()) {
            showYourMoveBanner = true
            delay(2400)
            showYourMoveBanner = false
        }
    }

    // Captured pieces calculation
    val capturedWhite = mutableListOf<PieceType>()
    val capturedBlack = mutableListOf<PieceType>()
    for (m in gameState.history) {
        if (m.captured != null) {
            if (m.color == PieceColor.WHITE) {
                capturedWhite.add(m.captured)
            } else {
                capturedBlack.add(m.captured)
            }
        }
    }

    val whiteVal = capturedWhite.sumOf { PIECE_VALUES[it] ?: 0 }
    val blackVal = capturedBlack.sumOf { PIECE_VALUES[it] ?: 0 }
    val whiteAdvantage = (whiteVal - blackVal) / 100
    val blackAdvantage = (blackVal - whiteVal) / 100

    fun executeMove(move: Move) {
        // Clear any active move hints or board preview
        showHints = false
        previewMove = null
        hintSuggestions = emptyList()
        isCalculatingHints = false

        val san = moveToSAN(gameState, move)
        move.san = san

        // Procedural tactile sound effects
        if (move.captured != null) {
            SoundManager.playCapture()
        } else if (move.isCastling != null) {
            SoundManager.playCastle()
        } else {
            SoundManager.playMove()
        }

        val next = makeMove(gameState, move)
        gameState = next
        gameStateHistory.add(next)

        val newOutcome = getGameOutcome(next)
        if (newOutcome.isOver) {
            outcome = newOutcome
            showGameOverOverlay = true
            if (newOutcome.winner == humanColor) {
                SoundManager.playVictory()
                VoiceAnnouncer.announceCheckmate(newOutcome.winner, humanColor, isAIMode)
            } else if (newOutcome.winner != null) {
                SoundManager.playDefeat()
                VoiceAnnouncer.announceCheckmate(newOutcome.winner, humanColor, isAIMode)
            } else if (newOutcome.reason == OutcomeReason.STALEMATE) {
                SoundManager.playDraw()
                VoiceAnnouncer.announceStalemate()
            } else {
                SoundManager.playDraw()
                VoiceAnnouncer.announceDraw(newOutcome.reason?.name ?: "DRAW")
            }
        } else if (isInCheck(next.board, next.turn)) {
            SoundManager.playCheck()
            VoiceAnnouncer.announceCheck()
        } else if (move.isCastling != null) {
            VoiceAnnouncer.announceCastle(move.isCastling == CastlingType.KINGSIDE)
        } else if (move.promotion != null) {
            VoiceAnnouncer.announcePromotion(move.promotion)
        }
    }

    // AI Move execution with natural pacing
    LaunchedEffect(gameState.turn, isAITurn, outcome.isOver) {
        if (isAITurn && !isAIThinking && !outcome.isOver) {
            isAIThinking = true
            val aiMove = calculateAIMoveAsync(gameState, aiDifficulty)
            delay(250) // Deliberate moment so eyes register move
            isAIThinking = false
            if (aiMove != null && !outcome.isOver) {
                executeMove(aiMove)
            }
        }
    }

    // Undo logic
    fun handleUndo() {
        if (isAIThinking || gameStateHistory.size <= 1) return
        SoundManager.playClick()

        showHints = false
        previewMove = null
        hintSuggestions = emptyList()
        isCalculatingHints = false

        if (isAIMode) {
            val steps = if (gameState.turn == humanColor) 2 else 1
            val targetIdx = (gameStateHistory.size - 1 - steps).coerceAtLeast(0)
            val targetState = gameStateHistory[targetIdx]
            gameState = targetState
            while (gameStateHistory.size > targetIdx + 1) {
                gameStateHistory.removeAt(gameStateHistory.size - 1)
            }
            outcome = GameOutcome(isOver = false)
            showGameOverOverlay = false
        } else {
            val targetIdx = gameStateHistory.size - 2
            val targetState = gameStateHistory[targetIdx]
            gameState = targetState
            gameStateHistory.removeAt(gameStateHistory.size - 1)
            outcome = GameOutcome(isOver = false)
            showGameOverOverlay = false
        }
    }

    fun handleRestart() {
        showHints = false
        previewMove = null
        hintSuggestions = emptyList()
        isCalculatingHints = false

        val fresh = createInitialGameState()
        gameState = fresh
        gameStateHistory.clear()
        gameStateHistory.add(fresh)
        outcome = GameOutcome(isOver = false)
        showGameOverOverlay = false
        isAIThinking = false
        gameDurationSeconds = 0
        whiteTimeSeconds = 0
        blackTimeSeconds = 0
        VoiceAnnouncer.announceGameStart(isAIMode)
    }

    fun handleResign() {
        val resignWinner = gameState.turn.opponent()
        outcome = GameOutcome(isOver = true, winner = resignWinner, isDraw = false, reason = OutcomeReason.RESIGNATION)
        showGameOverOverlay = true
        VoiceAnnouncer.announceResignation(gameState.turn)
    }

    fun handleDrawOffer() {
        outcome = GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.MUTUAL_AGREEMENT)
        showGameOverOverlay = true
        SoundManager.playDraw()
        VoiceAnnouncer.announceDraw("MUTUAL AGREEMENT")
    }

    val topColor = if (isFlipped) PieceColor.WHITE else PieceColor.BLACK
    val bottomColor = if (isFlipped) PieceColor.BLACK else PieceColor.WHITE
    val lastMove = if (gameState.history.isNotEmpty()) gameState.history.last() else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF221913),
                        Color(0xFF140E0A),
                        Color(0xFF090604)
                    ),
                    radius = 1200f
                )
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // TOP TABLETOP NAVIGATION BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button & Match Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (gameState.history.isNotEmpty() && !outcome.isOver) {
                                confirmDialogType = "back"
                            } else {
                                onBackToMenu()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAIMode) "VS AI" else "LOCAL 2P",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = StudyParchmentCream
                            )
                            if (isAIMode) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StudyAmberAccent)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = aiDifficulty.name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1B1207)
                                    )
                                }
                            }
                        }
                        val min = gameDurationSeconds / 60
                        val sec = gameDurationSeconds % 60
                        Text(
                            text = "Move ${gameState.fullmoveNumber} • %02d:%02d".format(min, sec),
                            fontSize = 11.sp,
                            color = Color(0xFFAFA293)
                        )
                    }
                }

                // Quick Header Actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Quick Hints icon in top bar as well
                    if (showMoveHints && !outcome.isOver) {
                        IconButton(
                            onClick = { toggleHints() },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (showHints) StudyAmberAccent else Color(0x18FFFFFF))
                        ) {
                            Text(
                                text = "💡",
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Voice commentary
                    IconButton(
                        onClick = {
                            val next = !voiceEnabled
                            VoiceAnnouncer.setVoiceEnabled(next)
                            onVoiceChanged(next)
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Text(text = if (voiceEnabled) "🎙️" else "🔇", fontSize = 15.sp)
                    }

                    // Move History
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Text("📜", fontSize = 15.sp)
                    }

                    // Pause / Game Menu
                    IconButton(
                        onClick = { showGamePauseMenu = true },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Text("☰", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ==========================================
            // OPPONENT SCORECARD (TOP)
            // ==========================================
            val topName = if (isAIMode) {
                if (topColor == aiColor) "AI Adversary" else "You"
            } else {
                if (topColor == PieceColor.WHITE) player1Name else player2Name
            }
            val topSubtitle = if (isAIMode && topColor == aiColor) "Tier: ${aiDifficulty.name}" else topColor.name

            PlayerCardView(
                name = topName,
                subtitle = topSubtitle,
                color = topColor,
                isAI = isAIMode && topColor == aiColor,
                isTurn = gameState.turn == topColor,
                isThinking = isAIMode && topColor == aiColor && isAIThinking,
                isInCheck = gameState.turn == topColor && isInCheck(gameState.board, topColor),
                capturedPieces = if (topColor == PieceColor.WHITE) capturedWhite else capturedBlack,
                materialAdvantage = if (topColor == PieceColor.WHITE) whiteAdvantage else blackAdvantage,
                timerSeconds = if (topColor == PieceColor.WHITE) whiteTimeSeconds else blackTimeSeconds,
                pieceTheme = currentPieceTheme,
                boardTheme = currentBoardTheme
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // PHYSICAL CHESS BOARD TABLETOP
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(22.dp, RoundedCornerShape(16.dp), spotColor = Color(0xDD000000)),
                contentAlignment = Alignment.Center
            ) {
                ChessBoardView(
                    state = gameState,
                    flipped = isFlipped,
                    isInteractable = isHumanTurn && !isAIThinking && !outcome.isOver,
                    showCoordinates = showCoords,
                    highlightMoves = highlightMoves,
                    boardTheme = currentBoardTheme,
                    pieceTheme = currentPieceTheme,
                    lastMove = lastMove,
                    onMove = { executeMove(it) },
                    previewFrom = previewMove?.from,
                    previewTo = previewMove?.to,
                    modifier = Modifier.fillMaxSize()
                )

                // "YOUR MOVE" Overlay Banner
                if (showYourMoveBanner) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xE61A120B))
                            .border(1.5.dp, StudyAmberAccent, RoundedCornerShape(14.dp))
                            .clickable { showYourMoveBanner = false }
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "YOUR MOVE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = StudyAmberAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Think ahead.",
                                fontSize = 12.sp,
                                color = StudyParchmentCream
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BEST MOVE SUGGESTIONS (HINTS)
            // ==========================================
            AnimatedVisibility(
                visible = showHints,
                enter = fadeIn(tween(250)) + slideInVertically(tween(250)),
                exit = fadeOut(tween(180)) + slideOutVertically(tween(180))
            ) {
                MoveSuggestionSection(
                    suggestions = hintSuggestions,
                    selectedSuggestion = previewMove,
                    isCalculating = isCalculatingHints,
                    pieceTheme = currentPieceTheme,
                    onSelectSuggestion = { previewMove = it },
                    onCloseHints = {
                        showHints = false
                        previewMove = null
                    },
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // PLAYER SCORECARD (BOTTOM)
            // ==========================================
            val bottomName = if (isAIMode) {
                if (bottomColor == aiColor) "AI Adversary" else "You"
            } else {
                if (bottomColor == PieceColor.WHITE) player1Name else player2Name
            }
            val bottomSubtitle = if (isAIMode && bottomColor == humanColor) "Tabletop Commander" else bottomColor.name

            PlayerCardView(
                name = bottomName,
                subtitle = bottomSubtitle,
                color = bottomColor,
                isAI = isAIMode && bottomColor == aiColor,
                isTurn = gameState.turn == bottomColor,
                isThinking = isAIMode && bottomColor == aiColor && isAIThinking,
                isInCheck = gameState.turn == bottomColor && isInCheck(gameState.board, bottomColor),
                capturedPieces = if (bottomColor == PieceColor.WHITE) capturedWhite else capturedBlack,
                materialAdvantage = if (bottomColor == PieceColor.WHITE) whiteAdvantage else blackAdvantage,
                timerSeconds = if (bottomColor == PieceColor.WHITE) whiteTimeSeconds else blackTimeSeconds,
                pieceTheme = currentPieceTheme,
                boardTheme = currentBoardTheme
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // COMPACT TACTILE CONTROLS (UNDO, HINT, DRAW, RESIGN, OR POST-GAME REVIEW)
            // ==========================================
            if (outcome.isOver) {
                // When game concludes and player is reviewing the board:
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ANALYSE GAME Button
                    Button(
                        onClick = {
                            onAnalyseGame?.invoke(
                                gameState.history,
                                outcome,
                                gameDurationSeconds,
                                oppName,
                                if (isAIMode) aiDifficulty else null,
                                humanColor
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                        modifier = Modifier.weight(1.3f).height(44.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔍 ", fontSize = 12.sp)
                            Text(
                                text = "ANALYSE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Color(0xFF1B140E)
                            )
                        }
                    }

                    // RESULT CARD Button
                    OutlinedButton(
                        onClick = { showGameOverOverlay = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudyParchmentCream),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40D4A373)),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("RESULT", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    // RESTART Button
                    Button(
                        onClick = { handleRestart() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38291F)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x35FFFFFF)),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("RESTART", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // UNDO
                    OutlinedButton(
                        onClick = { handleUndo() },
                        enabled = !isAIThinking && gameStateHistory.size > 1 && !outcome.isOver,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudyParchmentCream),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x35FFFFFF)),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text("UNDO", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    // HINT
                    if (showMoveHints) {
                        Button(
                            onClick = { toggleHints() },
                            enabled = !isAIThinking && !outcome.isOver,
                            shape = RoundedCornerShape(12.dp),
                            colors = if (showHints) {
                                ButtonDefaults.buttonColors(containerColor = StudyAmberAccent)
                            } else {
                                ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2118))
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (showHints) StudyAmberAccent else Color(0x40D4A373)
                            ),
                            modifier = Modifier.weight(1.15f).height(44.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isCalculatingHints) "⏳ " else "💡 ",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (isCalculatingHints) "THINKING" else if (showHints) "HINTS ON" else "HINT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    color = if (showHints) Color(0xFF1B140E) else StudyParchmentCream
                                )
                            }
                        }
                    }

                    // DRAW
                    OutlinedButton(
                        onClick = { confirmDialogType = "draw" },
                        enabled = !isAIThinking && !outcome.isOver,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StudyParchmentCream),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x35FFFFFF)),
                        modifier = Modifier.weight(0.95f).height(44.dp)
                    ) {
                        Text("DRAW", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }

                    // RESIGN
                    Button(
                        onClick = { confirmDialogType = "resign" },
                        enabled = !isAIThinking && !outcome.isOver,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x35EF4444)),
                        modifier = Modifier.weight(0.95f).height(44.dp)
                    ) {
                        Text(
                            text = "RESIGN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }
            }
        }

        // ==========================================
        // OVERLAYS & MODALS
        // ==========================================

        // Dramatic Match Conclusion Overlay (Checkmate / Draw)
        if (showGameOverOverlay) {
            MatchResultOverlay(
                outcome = outcome,
                humanColor = humanColor,
                isAIMode = isAIMode,
                opponentName = oppName,
                moveCount = gameState.history.size,
                gameDurationSeconds = gameDurationSeconds,
                pieceTheme = currentPieceTheme,
                onAnalyseGame = {
                    showGameOverOverlay = false
                    onAnalyseGame?.invoke(
                        gameState.history,
                        outcome,
                        gameDurationSeconds,
                        oppName,
                        if (isAIMode) aiDifficulty else null,
                        humanColor
                    )
                },
                onPlayAgain = {
                    handleRestart()
                    showGameOverOverlay = false
                },
                onReviewBoard = { showGameOverOverlay = false },
                onMainMenu = onBackToMenu
            )
        }

        // Parchment Chess Notebook Pause Menu
        if (showGamePauseMenu) {
            GameMenuOverlay(
                onResume = { showGamePauseMenu = false },
                onNewGame = {
                    showGamePauseMenu = false
                    handleRestart()
                },
                onOpenThemes = {
                    showGamePauseMenu = false
                    onOpenThemeGallery()
                },
                onOpenSettings = {
                    showGamePauseMenu = false
                    showSettingsDialog = true
                },
                onOpenHowToPlay = {
                    showGamePauseMenu = false
                    showHowToPlayDialog = true
                },
                onOpenHistory = {
                    showGamePauseMenu = false
                    showHistorySheet = true
                },
                onMainMenu = {
                    showGamePauseMenu = false
                    onBackToMenu()
                }
            )
        }

        // Move History Sheet
        if (showHistorySheet) {
            MoveHistorySheet(
                moves = gameState.history,
                onClose = { showHistorySheet = false }
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                soundEnabled = soundEnabled,
                onSoundChanged = onSoundChanged,
                voiceEnabled = voiceEnabled,
                onVoiceChanged = onVoiceChanged,
                showCoords = showCoords,
                onShowCoordsChanged = onShowCoordsChanged,
                highlightMoves = highlightMoves,
                onHighlightMovesChanged = onHighlightMovesChanged,
                autoFlipLocal = autoFlipLocal,
                onAutoFlipChanged = onAutoFlipChanged,
                showMoveHints = showMoveHints,
                onShowMoveHintsChanged = onShowMoveHintsChanged,
                currentBoardTheme = currentBoardTheme,
                currentPieceTheme = currentPieceTheme,
                onOpenThemes = onOpenThemeGallery,
                onClose = { showSettingsDialog = false }
            )
        }

        // How To Play Dialog
        if (showHowToPlayDialog) {
            HowToPlayDialog(
                pieceTheme = currentPieceTheme,
                onClose = { showHowToPlayDialog = false }
            )
        }

        // Confirmation Dialog
        confirmDialogType?.let { type ->
            AlertDialog(
                onDismissRequest = { confirmDialogType = null },
                title = {
                    Text(
                        text = when (type) {
                            "restart" -> "Restart Match?"
                            "resign" -> "Resign Match?"
                            "draw" -> "Offer Draw?"
                            else -> "Leave Table?"
                        },
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentCream
                    )
                },
                text = {
                    Text(
                        text = when (type) {
                            "restart" -> "The board layout will be reset to the opening position."
                            "resign" -> "You will surrender this match to your opponent."
                            "draw" -> "Both players agree to conclude this match in an honorable draw."
                            else -> "Your current tabletop game progress will be lost."
                        },
                        color = Color(0xFFC2B6A8)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            when (type) {
                                "restart" -> handleRestart()
                                "resign" -> handleResign()
                                "draw" -> handleDrawOffer()
                                "back" -> onBackToMenu()
                            }
                            confirmDialogType = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "resign") Color(0xFFC93B2B) else StudyAmberAccent
                        )
                    ) {
                        Text(
                            text = "Confirm",
                            fontWeight = FontWeight.Bold,
                            color = if (type == "resign") Color.White else Color(0xFF1B1207)
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { confirmDialogType = null }) {
                        Text("Cancel", color = StudyParchmentCream)
                    }
                },
                containerColor = Color(0xFF221A14)
            )
        }
    }
}
