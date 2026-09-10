package com.example.chessgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.ai.MoveSuggestion
import com.example.chessgame.ai.MoveSuggestionEngine
import com.example.chessgame.ai.PIECE_VALUES
import com.example.chessgame.ai.calculateAIMoveAsync
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.audio.VoiceAnnouncer
import com.example.chessgame.engine.*
import com.example.chessgame.progression.PlayerProgressionManager
import com.example.chessgame.progression.XpGainSummary
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen 6: Chess Gameplay Screen.
 * Immersive tabletop chess experience redesigned to match the reference design:
 * - Full-screen atmospheric desk background with central darkening
 * - Elegant top header: [ < ] CHESS (CLASSIC STRATEGY. REIMAGINED.) [ ⚙️ ] [ ••• ]
 * - 3-Compartment Player Information Bar: [ You / White ] [ ⏱ 09:48 ] [ AI / Black ]
 * - Centered physical ChessBoardView with smooth piece animations & legal moves
 * - Best Moves hint system with 3 candidate move cards & selected move detail box
 * - Board directional arrow and square highlights for move preview (pure visual preview)
 * - 4 Bottom compact pill controls: [ ↩ Undo ] [ 💡 Hint ] [ 🔄 New Game ] [ ⇄ Flip Board ]
 * - Strict Android status bar and navigation bar inset handling
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

    // Board Flip state: supports manual flip toggle as well as default perspective
    var manualFlip by remember { mutableStateOf(false) }

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
                if (suggestions.isNotEmpty()) {
                    // Pre-select the #1 best move for board preview
                    previewMove = suggestions.first()
                }
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
    var xpSummary by remember { mutableStateOf<XpGainSummary?>(null) }

    // System Back Gesture handling: dismiss dialogs first, then prompt to leave active match, or navigate back
    BackHandler {
        when {
            confirmDialogType != null -> confirmDialogType = null
            showSettingsDialog -> showSettingsDialog = false
            showHowToPlayDialog -> showHowToPlayDialog = false
            showHistorySheet -> showHistorySheet = false
            showGamePauseMenu -> showGamePauseMenu = false
            showGameOverOverlay -> showGameOverOverlay = false
            gameState.history.isNotEmpty() && !outcome.isOver -> confirmDialogType = "back"
            else -> onBackToMenu()
        }
    }

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

    // Board flip logic: combines player color preference and manual flip toggle
    val defaultFlipped = if (isAIMode) {
        humanColor == PieceColor.BLACK
    } else {
        autoFlipLocal && gameState.turn == PieceColor.BLACK
    }
    val isFlipped = defaultFlipped xor manualFlip

    // Announce game start
    LaunchedEffect(Unit) {
        VoiceAnnouncer.announceGameStart(isAIMode)
    }

    // Trigger "YOUR MOVE" notice when turn becomes human in AI mode (fast 750ms duration)
    LaunchedEffect(gameState.turn) {
        if (isAIMode && gameState.turn == humanColor && !outcome.isOver && gameState.history.isNotEmpty()) {
            showYourMoveBanner = true
            delay(750)
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

    fun finalizeMatch(finalOutcome: GameOutcome) {
        outcome = finalOutcome
        showGameOverOverlay = true
        xpSummary = PlayerProgressionManager.recordMatchConclusion(
            outcome = finalOutcome,
            humanColor = humanColor,
            isAIMode = isAIMode,
            difficulty = if (isAIMode) aiDifficulty else null,
            durationSeconds = gameDurationSeconds
        )
    }

    fun executeMove(move: Move) {
        // Clear any active move hints or board preview
        showHints = false
        previewMove = null
        hintSuggestions = emptyList()
        isCalculatingHints = false
        showYourMoveBanner = false

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
            finalizeMatch(newOutcome)
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
        xpSummary = null
        isAIThinking = false
        gameDurationSeconds = 0
        whiteTimeSeconds = 0
        blackTimeSeconds = 0
        VoiceAnnouncer.announceGameStart(isAIMode)
    }

    fun handleResign() {
        val resignWinner = gameState.turn.opponent()
        val resignOutcome = GameOutcome(isOver = true, winner = resignWinner, isDraw = false, reason = OutcomeReason.RESIGNATION)
        finalizeMatch(resignOutcome)
        VoiceAnnouncer.announceResignation(gameState.turn)
    }

    fun handleDrawOffer() {
        val drawOutcome = GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.MUTUAL_AGREEMENT)
        finalizeMatch(drawOutcome)
        SoundManager.playDraw()
        VoiceAnnouncer.announceDraw("MUTUAL AGREEMENT")
    }

    val lastMove = if (gameState.history.isNotEmpty()) gameState.history.last() else null

    // Root Container with Atmospheric Room Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0806))
    ) {
        // ==========================================
        // LAYER 1: ATMOSPHERIC CHESS DESK BACKGROUND
        // ==========================================
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = "Chess Desk Environment",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ==========================================
        // LAYER 1.5: CENTRAL SCRIM OVERLAY
        // Keeps center behind board dark and crisp while ambient desk elements frame edges
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xEB0A0705), // ~92% dark center for perfect contrast
                            Color(0xC4080503), // ~77% mid falloff
                            Color(0x66060403)  // ~40% ambient desk edge
                        ),
                        radius = 1350f
                    )
                )
        )

        // ==========================================
        // LAYER 2: NATIVE COMPOSE GAMEPLAY UI
        // ==========================================
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // TOP HEADER: [ < ] CHESS [ ⚙️ ] [ ••• ]
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                // Left: Back button (rounded square, dark translucent)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x28FFFFFF))
                        .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(10.dp))
                        .clickable {
                            SoundManager.playClick()
                            if (gameState.history.isNotEmpty() && !outcome.isOver) {
                                confirmDialogType = "back"
                            } else {
                                onBackToMenu()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "←",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF8FAFC)
                    )
                }

                // Center: Gold Knight + CHESS + RATED ARENA
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_diff_knight),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHESS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                    }
                    Text(
                        text = if (isAIMode) "RATED ARENA • VS ${aiDifficulty.name}" else "LOCAL ARENA • 2 PLAYER DUEL",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        fontFamily = GameFont,
                        color = Color(0xFFFFB703)
                    )
                }

                // Right: Settings [ ⚙️ ] and More [ ••• ]
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Settings button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x28FFFFFF))
                            .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(10.dp))
                            .clickable {
                                SoundManager.playClick()
                                showSettingsDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚙️", fontSize = 15.sp)
                    }

                    // More button (Pause overlay / game actions)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x28FFFFFF))
                            .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(10.dp))
                            .clickable {
                                SoundManager.playClick()
                                showGamePauseMenu = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "•••",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = StudyParchmentCream
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // PLAYER INFORMATION BAR (3-COMPARTMENT ROW MATCHING REFERENCE)
            // [ You / White ] [ ⏱ 09:48 ] [ AI / Black ]
            // ==========================================
            val isWhiteTurn = gameState.turn == PieceColor.WHITE
            val isBlackTurn = gameState.turn == PieceColor.BLACK

            val youColor = if (isAIMode) humanColor else PieceColor.WHITE
            val oppColor = if (isAIMode) aiColor else PieceColor.BLACK

            val isYouActive = gameState.turn == youColor && !outcome.isOver
            val isOppActive = gameState.turn == oppColor && !outcome.isOver

            val youAdvantage = if (youColor == PieceColor.WHITE) whiteAdvantage else blackAdvantage
            val oppAdvantage = if (oppColor == PieceColor.WHITE) whiteAdvantage else blackAdvantage

            val youInCheck = gameState.turn == youColor && isInCheck(gameState.board, youColor)
            val oppInCheck = gameState.turn == oppColor && isInCheck(gameState.board, oppColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Compartment: You
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .shadow(if (isYouActive) 8.dp else 1.dp, RoundedCornerShape(12.dp), spotColor = if (isYouActive) Color(0xFF00E5FF) else Color.Black)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isYouActive) {
                                Brush.verticalGradient(
                                    listOf(Color(0xFF132338), Color(0xFF0F1726))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0D1420), Color(0xFF0D1420))
                                )
                            }
                        )
                        .border(
                            width = if (isYouActive) 1.8.dp else 1.dp,
                            color = if (isYouActive) Color(0xFF00E5FF) else Color(0xFF1E2B3E),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A2638))
                                .border(0.8.dp, if (isYouActive) Color(0xFF00E5FF) else Color(0xFF2C3C56), RoundedCornerShape(8.dp))
                        ) {
                            Text(text = "👑", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(7.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAIMode) "You" else player1Name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GameFont,
                                    color = Color(0xFFF8FAFC),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (youAdvantage > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+$youAdvantage",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = GameFont,
                                        color = Color(0xFFFFB703)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (youColor == PieceColor.WHITE) Color.White else Color(0xFF1E293B))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (youInCheck) "CHECK!" else youColor.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 9.5.sp,
                                    fontWeight = if (youInCheck) FontWeight.Black else FontWeight.Normal,
                                    fontFamily = GameFont,
                                    color = if (youInCheck) Color(0xFFFF3366) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Center Compartment: Match Clock (e.g. 09:48)
                val totalSecs = gameDurationSeconds
                val clockMins = totalSecs / 60
                val clockSecs = totalSecs % 60
                val clockStr = "%02d:%02d".format(clockMins, clockSecs)

                Box(
                    modifier = Modifier
                        .width(92.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0A0F1A))
                        .border(1.dp, Color(0xFF1E2B3E), RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⏱", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = clockStr,
                                fontSize = 14.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF8FAFC)
                            )
                        }
                        Text(
                            text = "MOVE ${gameState.history.size / 2 + 1}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = GameFont,
                            letterSpacing = 0.8.sp,
                            color = Color(0xFFFFB703)
                        )
                    }
                }

                // Right Compartment: AI / Opponent
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .shadow(if (isOppActive) 8.dp else 1.dp, RoundedCornerShape(12.dp), spotColor = if (isOppActive) Color(0xFFFFB703) else Color.Black)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isOppActive) {
                                Brush.verticalGradient(
                                    listOf(Color(0xFF24180E), Color(0xFF16100B))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0D1420), Color(0xFF0D1420))
                                )
                            }
                        )
                        .border(
                            width = if (isOppActive) 1.8.dp else 1.dp,
                            color = if (isOppActive) Color(0xFFFFB703) else Color(0xFF1E2B3E),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Opponent avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A2638))
                                .border(0.8.dp, if (isOppActive) Color(0xFFFFB703) else Color(0xFF2C3C56), RoundedCornerShape(8.dp))
                        ) {
                            Text(text = if (isAIMode) "🤖" else "⚔️", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(7.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAIMode) "AI (${aiDifficulty.name.take(3)})" else player2Name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GameFont,
                                    color = Color(0xFFF8FAFC),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (oppAdvantage > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+$oppAdvantage",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = GameFont,
                                        color = Color(0xFFFFB703)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (oppColor == PieceColor.WHITE) Color.White else Color(0xFF1E293B))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (isAIMode && isAIThinking) {
                                    Text(
                                        text = "Thinking...",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = GameFont,
                                        color = Color(0xFFFFB703)
                                    )
                                } else {
                                    Text(
                                        text = if (oppInCheck) "CHECK!" else oppColor.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 9.5.sp,
                                        fontWeight = if (oppInCheck) FontWeight.Black else FontWeight.Normal,
                                        fontFamily = GameFont,
                                        color = if (oppInCheck) Color(0xFFFF3366) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ==========================================
            // CHESSBOARD CENTERPIECE (ChessBoardView)
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
                    onMove = {
                        showYourMoveBanner = false
                        executeMove(it)
                    },
                    previewFrom = previewMove?.from,
                    previewTo = previewMove?.to,
                    modifier = Modifier.fillMaxSize()
                )

                // "YOUR MOVE" Overlay Banner (fast 750ms duration, smooth fade)
                androidx.compose.animation.AnimatedVisibility(
                    visible = showYourMoveBanner,
                    enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.85f, animationSpec = tween(180)),
                    exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.85f, animationSpec = tween(180)),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(12.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xF21A120B))
                            .border(1.5.dp, StudyAmberAccent, RoundedCornerShape(14.dp))
                            .clickable { showYourMoveBanner = false }
                            .padding(horizontal = 22.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "YOUR MOVE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = StudyAmberAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Think ahead.",
                                fontSize = 11.sp,
                                color = StudyParchmentCream
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // BEST MOVES / HINT SYSTEM (3 CARDS + DETAIL CARD)
            // ==========================================
            MoveSuggestionSection(
                suggestions = hintSuggestions,
                selectedSuggestion = previewMove,
                isCalculating = isCalculatingHints,
                pieceTheme = currentPieceTheme,
                onSelectSuggestion = { previewMove = it },
                onToggleHints = { toggleHints() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // BOTTOM GAMEPLAY CONTROLS (4 COMPACT PILL BUTTONS)
            // [ ↩ Undo ] [ 💡 Hint ] [ 🔄 New Game ] [ ⇄ Flip Board ]
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // UNDO
                GameControlPill(
                    icon = "↩",
                    label = "Undo",
                    enabled = !isAIThinking && gameStateHistory.size > 1 && !outcome.isOver,
                    isActive = false,
                    onClick = { handleUndo() },
                    modifier = Modifier.weight(1f)
                )

                // HINT
                GameControlPill(
                    icon = if (isCalculatingHints) "⏳" else "💡",
                    label = if (isCalculatingHints) "Thinking" else "Hint",
                    enabled = !isAIThinking && !outcome.isOver,
                    isActive = showHints || isCalculatingHints,
                    onClick = { toggleHints() },
                    modifier = Modifier.weight(1f)
                )

                // NEW GAME
                GameControlPill(
                    icon = "🔄",
                    label = "New Game",
                    enabled = !isAIThinking,
                    isActive = false,
                    onClick = { confirmDialogType = "restart" },
                    modifier = Modifier.weight(1.15f)
                )

                // FLIP BOARD
                GameControlPill(
                    icon = "⇄",
                    label = "Flip Board",
                    enabled = true,
                    isActive = manualFlip,
                    onClick = {
                        manualFlip = !manualFlip
                        SoundManager.playClick()
                    },
                    modifier = Modifier.weight(1.15f)
                )
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
                xpSummary = xpSummary,
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

/**
 * Compact Game Control Pill Button matching reference styling:
 * [ icon label ] with rounded pill shape, dark translucent wood surface,
 * warm gold border, and gold highlight when active.
 */
@Composable
private fun GameControlPill(
    icon: String,
    label: String,
    enabled: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .shadow(if (isActive) 6.dp else 2.dp, RoundedCornerShape(12.dp), spotColor = if (isActive) Color(0xFFFFB703) else Color.Black)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isActive) {
                    Brush.verticalGradient(
                        listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0xFF141C2B), Color(0xFF0F1726))
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (isActive) Color(0xFFFFF9C4) else Color(0xFF26354E),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled) {
                onClick()
            }
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 12.sp,
                color = if (isActive) Color(0xFF140D04) else if (enabled) Color(0xFFFFB703) else Color(0xFF475569)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = GameFont,
                color = if (isActive) Color(0xFF140D04) else if (enabled) Color(0xFFF8FAFC) else Color(0xFF475569),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
