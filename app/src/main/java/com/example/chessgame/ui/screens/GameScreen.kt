package com.example.chessgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
 * Screen: Grandmaster Arena Chess Gameplay.
 * Tactical Monolith aesthetic featuring:
 * - Arena Monolith background environment with cyber-cyan atmospheric rim
 * - Command Top HUD: [ ← ] CHESS ARENA [ ⚙️ ] [ ••• ]
 * - 3-Compartment Combatant Bar with live illustrated AI avatars, match clock, check auras & advantage counters
 * - Centered monolithic ChessBoardView with smooth piece animations, legal highlights & preview arrows
 * - Best Moves hint system with candidate move cards & selected move detail box
 * - Board directional arrow and square highlights for move preview (pure visual preview)
 * - 4 Bottom tactical pill controls: [ ↩ Undo ] [ 💡 Hint ] [ 🔄 New Game ] [ ⇄ Flip ]
 * - Strict Android status bar and navigation bar safe area inset handling
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
    val oppName = if (isAIMode) {
        when (aiDifficulty) {
            AIDifficulty.EASY -> "Cadet Valen"
            AIDifficulty.MEDIUM -> "Strategist Lyra"
            AIDifficulty.HARD -> "Commander Voron"
            AIDifficulty.EXPERT -> "Oracle Kairos"
        }
    } else player2Name

    val aiAvatarRes = when (aiDifficulty) {
        AIDifficulty.EASY -> R.drawable.avatar_opponent_recruit
        AIDifficulty.MEDIUM -> R.drawable.avatar_opponent_tactician
        AIDifficulty.HARD -> R.drawable.avatar_opponent_warmaster
        AIDifficulty.EXPERT -> R.drawable.avatar_opponent_grandmaster
    }

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

    // Root Container with Arena Monolith Environment Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ArenaColors.VoidAbyss)
    ) {
        // LAYER 1: ARENA ENVIRONMENT BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_arena_monolith),
            contentDescription = "Monolith Arena Environment",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 1.5: HIGH-CONTRAST SCRIM OVERLAY
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF007090E),
                            Color(0xD9080C14),
                            Color(0xF505070B)
                        )
                    )
                )
        )

        // LAYER 2: NATIVE COMPOSE GAMEPLAY UI
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
            // TOP HUD: [ ← ] CHESS ARENA [ ⚙️ ] [ ••• ]
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                // Left: Back button
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    RoyalGameIconButton(
                        onClick = {
                            SoundManager.playClick()
                            if (gameState.history.isNotEmpty() && !outcome.isOver) {
                                confirmDialogType = "back"
                            } else {
                                onBackToMenu()
                            }
                        },
                        size = 38.dp,
                        iconGlyph = "◀",
                        glyphSize = 15.sp,
                        contentDescription = "Back"
                    )
                }

                // Center: Emblem + CHESS + Mode
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_game_emblem),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CHESS",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.RoyalGoldBright
                        )
                    }
                    Text(
                        text = if (isAIMode) "VS COMPUTER • ${aiDifficulty.name}" else "PASS & PLAY • 2 PLAYERS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = ArenaColors.TextSecondary
                    )
                }

                // Right: Settings [ ⚙️ ] and Menu [ ☰ ]
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoyalGameIconButton(
                        onClick = {
                            SoundManager.playClick()
                            showSettingsDialog = true
                        },
                        size = 38.dp,
                        iconRes = R.drawable.icon_settings_game,
                        contentDescription = "Settings"
                    )

                    RoyalGameIconButton(
                        onClick = {
                            SoundManager.playClick()
                            showGamePauseMenu = true
                        },
                        size = 38.dp,
                        iconGlyph = "☰",
                        glyphSize = 17.sp,
                        contentDescription = "Menu"
                    )
                }

            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // PLAYER COMBATANT BAR (3-COMPARTMENT ROW)
            // [ You / White ] [ ⏱ 09:48 ] [ AI / Black ]
            // ==========================================
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
                // Left Compartment: You / Commander 1
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .shadow(if (isYouActive) 8.dp else 1.dp, RoundedCornerShape(12.dp), spotColor = ArenaColors.CyberCyan)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isYouActive) {
                                Brush.verticalGradient(
                                    listOf(Color(0xE6141D2D), Color(0xFA0B111A))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(ArenaColors.TitaniumSurface, ArenaColors.TitaniumSurface)
                                )
                            }
                        )
                        .border(
                            width = if (isYouActive) 1.5.dp else 1.dp,
                            color = if (isYouActive) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder,
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2200F0FF))
                                .border(1.dp, if (isYouActive) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder, RoundedCornerShape(8.dp))
                        ) {
                            Text(text = "👤", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAIMode) "You" else player1Name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ArenaColors.TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (youAdvantage > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+$youAdvantage",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ArenaColors.CyberCyan
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.5.dp)
                                        .clip(CircleShape)
                                        .background(if (youColor == PieceColor.WHITE) Color.White else Color(0xFF222222))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (youInCheck) "CHECK!" else youColor.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 9.5.sp,
                                    fontWeight = if (youInCheck) FontWeight.Black else FontWeight.Normal,
                                    color = if (youInCheck) ArenaColors.CrimsonAlert else ArenaColors.TextSecondary
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
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ArenaColors.TitaniumSurface)
                        .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏱", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = clockStr,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = ArenaColors.TextPrimary
                        )
                    }
                }

                // Right Compartment: AI / Adversary
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .shadow(if (isOppActive) 8.dp else 1.dp, RoundedCornerShape(12.dp), spotColor = ArenaColors.SolarAmber)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isOppActive) {
                                Brush.verticalGradient(
                                    listOf(Color(0xE6261C14), Color(0xFA150E09))
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(ArenaColors.TitaniumSurface, ArenaColors.TitaniumSurface)
                                )
                            }
                        )
                        .border(
                            width = if (isOppActive) 1.5.dp else 1.dp,
                            color = if (isOppActive) ArenaColors.SolarAmber else ArenaColors.TitaniumBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Opponent avatar: Real Illustrated Image for AI!
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isOppActive) ArenaColors.SolarAmber else ArenaColors.TitaniumBorder, RoundedCornerShape(8.dp))
                        ) {
                            if (isAIMode) {
                                Image(
                                    painter = painterResource(id = aiAvatarRes),
                                    contentDescription = oppName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(text = "👥", fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = oppName.take(12),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ArenaColors.TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (oppAdvantage > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+$oppAdvantage",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ArenaColors.SolarAmber
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.5.dp)
                                        .clip(CircleShape)
                                        .background(if (oppColor == PieceColor.WHITE) Color.White else Color(0xFF222222))
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (isAIMode && isAIThinking) {
                                    Text(
                                        text = "Thinking...",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.SolarAmber
                                    )
                                } else {
                                    Text(
                                        text = if (oppInCheck) "CHECK!" else oppColor.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontSize = 9.5.sp,
                                        fontWeight = if (oppInCheck) FontWeight.Black else FontWeight.Normal,
                                        color = if (oppInCheck) ArenaColors.CrimsonAlert else ArenaColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // CHESSBOARD CENTERPIECE (ChessBoardView)
            // Encased in Monolithic Titanium Frame with Cyber Glow
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(28.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF000000))
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(16.dp)),
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

                // "YOUR MOVE" Overlay Banner (fast 750ms duration)
                if (showYourMoveBanner) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .shadow(16.dp, RoundedCornerShape(14.dp), spotColor = ArenaColors.CyberCyan)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xF00A101A))
                            .border(1.5.dp, ArenaColors.CyberCyan, RoundedCornerShape(14.dp))
                            .clickable { showYourMoveBanner = false }
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "YOUR TURN",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = ArenaColors.CyberCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Command the Arena.",
                                fontSize = 11.sp,
                                color = ArenaColors.TextSecondary
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
            // BOTTOM TACTICAL CONTROLS (4 COMPACT PILL BUTTONS)
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
                    label = if (isCalculatingHints) "Tactics" else "Hint",
                    enabled = !isAIThinking && !outcome.isOver,
                    isActive = showHints || isCalculatingHints,
                    onClick = { toggleHints() },
                    modifier = Modifier.weight(1f)
                )

                // NEW GAME
                GameControlPill(
                    icon = "🔄",
                    label = "Restart",
                    enabled = !isAIThinking,
                    isActive = false,
                    onClick = { confirmDialogType = "restart" },
                    modifier = Modifier.weight(1.15f)
                )

                // FLIP BOARD
                GameControlPill(
                    icon = "⇄",
                    label = "Flip",
                    enabled = true,
                    isActive = manualFlip,
                    onClick = {
                        manualFlip = !manualFlip
                        SoundManager.playClick()
                    },
                    modifier = Modifier.weight(1f)
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

        // Tactical Combat Pause Menu
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

        // Move History Combat Log
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

        // How To Play Handbook Dialog
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
                            "restart" -> "Restart Engagement?"
                            "resign" -> "Surrender Position?"
                            "draw" -> "Offer Tactical Draw?"
                            else -> "Depart Arena?"
                        },
                        fontWeight = FontWeight.Bold,
                        color = ArenaColors.TextPrimary
                    )
                },
                text = {
                    Text(
                        text = when (type) {
                            "restart" -> "Both forces will reset to standard opening positions."
                            "resign" -> "You will surrender this match to your opponent."
                            "draw" -> "Both commanders agree to conclude this engagement in a draw."
                            else -> "Your active match progress will be lost."
                        },
                        color = ArenaColors.TextSecondary
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
                            containerColor = if (type == "resign") ArenaColors.CrimsonAlert else ArenaColors.CyberCyan
                        )
                    ) {
                        Text(
                            text = "Confirm",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070A0F)
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { confirmDialogType = null }) {
                        Text("Cancel", color = ArenaColors.TextPrimary)
                    }
                },
                containerColor = Color(0xFF141924)
            )
        }
    }
}

/**
 * Compact Tactical Control Pill Button matching Grandmaster Arena styling:
 * [ icon label ] with rounded pill shape, brushed titanium surface,
 * cyber-cyan border and active highlight.
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
            .height(40.dp)
            .shadow(if (isActive) 8.dp else 2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x66FFD700))
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isActive) {
                    Brush.verticalGradient(
                        listOf(ArenaColors.RoyalGold, ArenaColors.RoyalGoldDark)
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(ArenaColors.TitaniumSurfaceRaised, ArenaColors.TitaniumSurface)
                    )
                }
            )
            .border(
                width = 1.2.dp,
                color = if (isActive) ArenaColors.RoyalGoldBright else ArenaColors.TitaniumBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(enabled = enabled) {
                SoundManager.playClick()
                onClick()
            }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 13.sp,
                color = if (isActive) Color(0xFF080D18) else if (enabled) ArenaColors.RoyalGold else Color(0x55FFFFFF)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isActive) Color(0xFF080D18) else if (enabled) ArenaColors.TextPrimary else Color(0x55FFFFFF),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

