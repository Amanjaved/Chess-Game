package com.example.chessgame

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.audio.VoiceAnnouncer
import com.example.chessgame.engine.*
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.HowToPlayDialog
import com.example.chessgame.ui.components.SettingsDialog
import com.example.chessgame.ui.screens.*
import kotlin.random.Random

enum class Screen {
    INTRO,
    MENU,
    AI_DIFFICULTY,
    COLOR_SELECT,
    LOCAL_SETUP,
    GAME,
    THEME_GALLERY,
    THEME_PREVIEW,
    GAME_ANALYSIS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize offline TTS voice announcer
        VoiceAnnouncer.init(this)

        // Initialize offline player progression & achievement system
        com.example.chessgame.progression.PlayerProgressionManager.init(this)

        val prefs = getSharedPreferences("chess_game_prefs", Context.MODE_PRIVATE)

        setContent {
            ChessGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF080A0D)
                ) {
                    // True Jetpack Compose navigation back-stack
                    val backStack = remember { mutableStateListOf(Screen.INTRO) }
                    val currentScreen = backStack.lastOrNull() ?: Screen.MENU

                    fun navigateTo(screen: Screen) {
                        if (screen == Screen.MENU && backStack.size == 1 && backStack.first() == Screen.INTRO) {
                            backStack.clear()
                            backStack.add(Screen.MENU)
                        } else {
                            backStack.add(screen)
                        }
                    }

                    fun navigateBack(): Boolean {
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                            return true
                        }
                        return false
                    }

                    // System Back gesture/button handling
                    // Disabled when at root (Screen.MENU) so Android standard back behavior exits the app
                    BackHandler(enabled = backStack.size > 1) {
                        navigateBack()
                    }

                    var gameMode by remember { mutableStateOf("ai") }
                    var aiDifficulty by remember { mutableStateOf(AIDifficulty.MEDIUM) }
                    var humanColor by remember { mutableStateOf(PieceColor.WHITE) }
                    var localPlayer1Name by remember { mutableStateOf("Player 1") }
                    var localPlayer2Name by remember { mutableStateOf("Player 2") }

                    // Post-Game Analysis Session State
                    var analysisMoves by remember { mutableStateOf(emptyList<Move>()) }
                    var analysisOutcome by remember { mutableStateOf(GameOutcome(isOver = false)) }
                    var analysisOpponent by remember { mutableStateOf("Opponent") }
                    var analysisDifficulty by remember { mutableStateOf<AIDifficulty?>(null) }
                    var analysisDuration by remember { mutableIntStateOf(0) }
                    var analysisPlayerColor by remember { mutableStateOf(PieceColor.WHITE) }

                    // Settings & Preferences
                    var soundEnabled by remember {
                        mutableStateOf(prefs.getBoolean("sound_enabled", true))
                    }
                    var voiceEnabled by remember {
                        mutableStateOf(prefs.getBoolean("voice_enabled", true))
                    }
                    var showCoords by remember {
                        mutableStateOf(prefs.getBoolean("show_coords", true))
                    }
                    var highlightMoves by remember {
                        mutableStateOf(prefs.getBoolean("highlight_moves", true))
                    }
                    var autoFlipLocal by remember {
                        mutableStateOf(prefs.getBoolean("auto_flip_local", false))
                    }
                    var showMoveHints by remember {
                        mutableStateOf(prefs.getBoolean("show_move_hints", true))
                    }

                    // Theme State
                    var currentBoardTheme by remember {
                        val boardId = prefs.getString("board_theme_id", ThemeRegistry.ARTISAN_WOOD.id) ?: ThemeRegistry.ARTISAN_WOOD.id
                        mutableStateOf(ThemeRegistry.findBoardTheme(boardId) ?: ThemeRegistry.ARTISAN_WOOD)
                    }
                    var currentPieceTheme by remember {
                        val pieceId = prefs.getString("piece_theme_id", ThemeRegistry.STORYBOOK_HANDCRAFTED.id) ?: ThemeRegistry.STORYBOOK_HANDCRAFTED.id
                        mutableStateOf(ThemeRegistry.findPieceTheme(pieceId) ?: ThemeRegistry.STORYBOOK_HANDCRAFTED)
                    }

                    // Temporary preview states for ThemePreviewScreen
                    var previewBoardTheme by remember { mutableStateOf(currentBoardTheme) }
                    var previewPieceTheme by remember { mutableStateOf(currentPieceTheme) }
                    var previewTarget by remember { mutableStateOf(ThemePreviewTarget.BOARD) }

                    var showMenuSettingsDialog by remember { mutableStateOf(false) }
                    var showMenuHowToPlayDialog by remember { mutableStateOf(false) }

                    LaunchedEffect(soundEnabled) {
                        prefs.edit().putBoolean("sound_enabled", soundEnabled).apply()
                        SoundManager.setSoundEnabled(soundEnabled)
                    }
                    LaunchedEffect(voiceEnabled) {
                        prefs.edit().putBoolean("voice_enabled", voiceEnabled).apply()
                        VoiceAnnouncer.setVoiceEnabled(voiceEnabled)
                    }
                    LaunchedEffect(showCoords) {
                        prefs.edit().putBoolean("show_coords", showCoords).apply()
                    }
                    LaunchedEffect(highlightMoves) {
                        prefs.edit().putBoolean("highlight_moves", highlightMoves).apply()
                    }
                    LaunchedEffect(autoFlipLocal) {
                        prefs.edit().putBoolean("auto_flip_local", autoFlipLocal).apply()
                    }
                    LaunchedEffect(showMoveHints) {
                        prefs.edit().putBoolean("show_move_hints", showMoveHints).apply()
                    }
                    LaunchedEffect(currentBoardTheme) {
                        prefs.edit().putString("board_theme_id", currentBoardTheme.id).apply()
                    }
                    LaunchedEffect(currentPieceTheme) {
                        prefs.edit().putString("piece_theme_id", currentPieceTheme.id).apply()
                    }

                    Crossfade(
                        targetState = currentScreen,
                        animationSpec = tween(300),
                        label = "screen_crossfade"
                    ) { screen ->
                        when (screen) {
                            Screen.INTRO -> {
                                IntroScreen(onFinish = { navigateTo(Screen.MENU) })
                            }

                            Screen.MENU -> {
                                MainMenuScreen(
                                    onPlayAI = { navigateTo(Screen.AI_DIFFICULTY) },
                                    onPlayLocal = { navigateTo(Screen.LOCAL_SETUP) },
                                    onOpenAnalysis = {
                                        if (analysisMoves.isEmpty()) {
                                            analysisMoves = com.example.chessgame.ai.GameAnalyzer.createSampleOperaGame()
                                            analysisOutcome = GameOutcome(
                                                isOver = true,
                                                winner = PieceColor.WHITE,
                                                reason = OutcomeReason.CHECKMATE
                                            )
                                            analysisOpponent = "Duke & Count"
                                            analysisDifficulty = AIDifficulty.HARD
                                            analysisDuration = 420
                                            analysisPlayerColor = PieceColor.WHITE
                                        }
                                        navigateTo(Screen.GAME_ANALYSIS)
                                    },
                                    onOpenThemes = { navigateTo(Screen.THEME_GALLERY) },
                                    onOpenHowToPlay = { showMenuHowToPlayDialog = true },
                                    onOpenSettings = { showMenuSettingsDialog = true }
                                )

                                if (showMenuSettingsDialog) {
                                    SettingsDialog(
                                        soundEnabled = soundEnabled,
                                        onSoundChanged = { soundEnabled = it },
                                        voiceEnabled = voiceEnabled,
                                        onVoiceChanged = { voiceEnabled = it },
                                        showCoords = showCoords,
                                        onShowCoordsChanged = { showCoords = it },
                                        highlightMoves = highlightMoves,
                                        onHighlightMovesChanged = { highlightMoves = it },
                                        autoFlipLocal = autoFlipLocal,
                                        onAutoFlipChanged = { autoFlipLocal = it },
                                        showMoveHints = showMoveHints,
                                        onShowMoveHintsChanged = { showMoveHints = it },
                                        currentBoardTheme = currentBoardTheme,
                                        currentPieceTheme = currentPieceTheme,
                                        onOpenThemes = {
                                            showMenuSettingsDialog = false
                                            navigateTo(Screen.THEME_GALLERY)
                                        },
                                        onClose = { showMenuSettingsDialog = false }
                                    )
                                }

                                if (showMenuHowToPlayDialog) {
                                    HowToPlayDialog(
                                        pieceTheme = currentPieceTheme,
                                        onClose = { showMenuHowToPlayDialog = false }
                                    )
                                }
                            }

                            Screen.AI_DIFFICULTY -> {
                                AISetupScreen(
                                    onBack = { navigateBack() },
                                    onSelectDifficulty = { diff ->
                                        aiDifficulty = diff
                                        navigateTo(Screen.COLOR_SELECT)
                                    }
                                )
                            }

                            Screen.COLOR_SELECT -> {
                                ColorSelectScreen(
                                    pieceTheme = currentPieceTheme,
                                    onBack = { navigateBack() },
                                    onStartGame = { colorChoice ->
                                        gameMode = "ai"
                                        humanColor = when (colorChoice) {
                                            PlayerColorChoice.WHITE -> PieceColor.WHITE
                                            PlayerColorChoice.BLACK -> PieceColor.BLACK
                                            PlayerColorChoice.RANDOM -> if (Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
                                        }
                                        // Clear setup steps from backStack so back from game returns cleanly to Menu
                                        backStack.removeAll { it == Screen.AI_DIFFICULTY || it == Screen.COLOR_SELECT }
                                        navigateTo(Screen.GAME)
                                    }
                                )
                            }

                            Screen.LOCAL_SETUP -> {
                                LocalSetupScreen(
                                    pieceTheme = currentPieceTheme,
                                    onBack = { navigateBack() },
                                    onStartMatch = { p1, p2 ->
                                        gameMode = "local"
                                        humanColor = PieceColor.WHITE
                                        localPlayer1Name = p1
                                        localPlayer2Name = p2
                                        backStack.removeAll { it == Screen.LOCAL_SETUP }
                                        navigateTo(Screen.GAME)
                                    }
                                )
                            }

                            Screen.THEME_GALLERY -> {
                                ThemeGalleryScreen(
                                    currentBoardTheme = currentBoardTheme,
                                    currentPieceTheme = currentPieceTheme,
                                    onSelectBoardTheme = { currentBoardTheme = it },
                                    onSelectPieceTheme = { currentPieceTheme = it },
                                    onPreviewTheme = { board, piece, target ->
                                        previewBoardTheme = board
                                        previewPieceTheme = piece
                                        previewTarget = target
                                        navigateTo(Screen.THEME_PREVIEW)
                                    },
                                    onBack = { navigateBack() }
                                )
                            }

                            Screen.THEME_PREVIEW -> {
                                ThemePreviewScreen(
                                    boardTheme = previewBoardTheme,
                                    pieceTheme = previewPieceTheme,
                                    previewTarget = previewTarget,
                                    isEquipped = if (previewTarget == ThemePreviewTarget.BOARD) {
                                        previewBoardTheme.id == currentBoardTheme.id
                                    } else {
                                        previewPieceTheme.id == currentPieceTheme.id
                                    },
                                    onApplyTheme = { board, piece ->
                                        currentBoardTheme = board
                                        currentPieceTheme = piece
                                        navigateBack()
                                    },
                                    onBack = { navigateBack() }
                                )
                            }

                            Screen.GAME -> {
                                GameScreen(
                                    mode = gameMode,
                                    aiDifficulty = aiDifficulty,
                                    humanColor = humanColor,
                                    soundEnabled = soundEnabled,
                                    onSoundChanged = { soundEnabled = it },
                                    voiceEnabled = voiceEnabled,
                                    onVoiceChanged = { voiceEnabled = it },
                                    showCoords = showCoords,
                                    onShowCoordsChanged = { showCoords = it },
                                    highlightMoves = highlightMoves,
                                    onHighlightMovesChanged = { highlightMoves = it },
                                    autoFlipLocal = autoFlipLocal,
                                    onAutoFlipChanged = { autoFlipLocal = it },
                                    showMoveHints = showMoveHints,
                                    onShowMoveHintsChanged = { showMoveHints = it },
                                    currentBoardTheme = currentBoardTheme,
                                    currentPieceTheme = currentPieceTheme,
                                    onOpenThemeGallery = { navigateTo(Screen.THEME_GALLERY) },
                                    onBackToMenu = { navigateBack() },
                                    player1Name = localPlayer1Name,
                                    player2Name = localPlayer2Name,
                                    onAnalyseGame = { moves, outcome, duration, opp, diff, color ->
                                        analysisMoves = moves
                                        analysisOutcome = outcome
                                        analysisDuration = duration
                                        analysisOpponent = opp
                                        analysisDifficulty = diff
                                        analysisPlayerColor = color
                                        navigateTo(Screen.GAME_ANALYSIS)
                                    }
                                )
                            }

                            Screen.GAME_ANALYSIS -> {
                                GameAnalysisScreen(
                                    moves = analysisMoves,
                                    outcome = analysisOutcome,
                                    opponentName = analysisOpponent,
                                    difficulty = analysisDifficulty,
                                    gameDurationSeconds = analysisDuration,
                                    humanColor = analysisPlayerColor,
                                    boardTheme = currentBoardTheme,
                                    pieceTheme = currentPieceTheme,
                                    onBack = { navigateBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VoiceAnnouncer.shutdown()
    }
}
