package com.example.chessgame.ai

import com.example.chessgame.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.math.abs
import kotlin.math.exp

/**
 * Move Quality Classification for Game Analysis.
 */
enum class MoveClassification(val label: String, val icon: String) {
    BRILLIANT("BRILLIANT", "✦"),
    BEST("BEST MOVE", "★"),
    STRONG("STRONG", "✦"),
    GOOD("GOOD MOVE", "✓"),
    BOOK("BOOK", "📖"),
    INACCURACY("INACCURACY", "?!"),
    MISTAKE("MISTAKE", "?"),
    BLUNDER("BLUNDER", "??")
}

/**
 * Key Moment in the match (biggest mistake, turning point, brilliant move, etc.).
 */
data class KeyMoment(
    val moveIndex: Int,
    val title: String,
    val description: String,
    val classification: MoveClassification
)

/**
 * Single analyzed move data point.
 */
data class MoveAnalysis(
    val moveIndex: Int,
    val ply: Int,
    val move: Move,
    val moveColor: PieceColor,
    val san: String,
    val evalBeforeCp: Int,
    val evalAfterCp: Int,
    val evalDeltaCp: Int,
    val centipawnLoss: Int,
    val classification: MoveClassification,
    val explanation: String,
    val bestEngineMove: Move?,
    val bestEngineSan: String,
    val stateAfterMove: GameState
)

/**
 * Complete game analysis report.
 */
data class GameAnalysisReport(
    val moves: List<MoveAnalysis>,
    val openingName: String,
    val whiteAccuracy: Int,
    val blackAccuracy: Int,
    val whiteBrilliantCount: Int = 0,
    val whiteBestCount: Int,
    val whiteGoodCount: Int,
    val whiteInaccuracyCount: Int,
    val whiteMistakeCount: Int,
    val whiteBlunderCount: Int,
    val blackBrilliantCount: Int = 0,
    val blackBestCount: Int,
    val blackGoodCount: Int,
    val blackInaccuracyCount: Int,
    val blackMistakeCount: Int,
    val blackBlunderCount: Int,
    val openingAccuracy: Int,
    val middlegameAccuracy: Int,
    val endgameAccuracy: Int,
    val keyMoments: List<KeyMoment>
)

object GameAnalyzer {

    // Common Opening Sequences
    private val OPENING_BOOK = listOf(
        listOf("e4", "e5", "Nf3", "Nc6", "Bb5") to "Ruy Lopez",
        listOf("e4", "e5", "Nf3", "Nc6", "Bc4") to "Italian Game",
        listOf("e4", "c5") to "Sicilian Defense",
        listOf("e4", "e6") to "French Defense",
        listOf("e4", "c6") to "Caro-Kann Defense",
        listOf("d4", "d5", "c4") to "Queen's Gambit",
        listOf("d4", "Nf6", "c4", "g6") to "King's Indian Defense",
        listOf("c4") to "English Opening",
        listOf("Nf3") to "Reti Opening",
        listOf("e4", "d5") to "Scandinavian Defense",
        listOf("d4", "d5", "Bf4") to "London System",
        listOf("e4", "e5", "f4") to "King's Gambit",
        listOf("e4", "e5", "Nf3", "Nc6", "Nc3", "Nf6") to "Four Knights Game",
        listOf("e4", "d6") to "Pirc Defense",
        listOf("d4", "d5", "c4", "c6") to "Slav Defense"
    )

    /**
     * Check if the sequence played so far strictly adheres to theoretical opening book.
     */
    fun isBookMove(sansSoFar: List<String>): Boolean {
        if (sansSoFar.isEmpty()) return false
        return OPENING_BOOK.any { (seq, _) ->
            sansSoFar.size <= seq.size && seq.take(sansSoFar.size) == sansSoFar
        }
    }

    /**
     * Identify opening name from played move sequence.
     */
    fun detectOpening(moves: List<Move>): String {
        if (moves.isEmpty()) return "Opening: Unknown"
        val sans = moves.map { it.san }
        var bestMatchName = "Opening: Unknown"
        var bestMatchLen = 0

        for ((seq, name) in OPENING_BOOK) {
            val matchLen = sans.zip(seq).takeWhile { (a, b) -> a == b }.count()
            if (matchLen >= 2 && matchLen > bestMatchLen) {
                bestMatchLen = matchLen
                bestMatchName = name
            }
        }
        return bestMatchName
    }

    /**
     * Format centipawn score to readable string (e.g. +1.4, -0.8, +M1, -M1).
     */
    fun formatEval(evalCp: Int): String {
        val absVal = kotlin.math.abs(evalCp)
        if (absVal >= 90000) {
            val plies = 100000 - absVal
            val movesToMate = maxOf(1, (plies + 1) / 2)
            return if (evalCp > 0) "+M$movesToMate" else "-M$movesToMate"
        }
        return if (evalCp >= 0) {
            "+%.1f".format(evalCp / 100.0)
        } else {
            "%.1f".format(evalCp / 100.0)
        }
    }

    /**
     * Convert standard algebraic notation (SAN) into friendly, plain English.
     * Examples:
     * - "Qxc1" -> "Queen takes on c1"
     * - "Qxh3#" -> "Queen takes on h3 (Checkmate!)"
     * - "Nf3" -> "Knight to f3"
     * - "exd5" -> "Pawn takes on d5"
     * - "O-O" -> "Castles Kingside"
     */
    fun describeSan(san: String): String {
        if (san.isEmpty()) return ""
        if (san == "O-O") return "Castles Kingside"
        if (san == "O-O-O") return "Castles Queenside"

        val isCheck = san.endsWith("+")
        val isMate = san.endsWith("#")
        val clean = san.trimEnd('+', '#')

        val pieceName = when (clean.firstOrNull()) {
            'K' -> "King"
            'Q' -> "Queen"
            'R' -> "Rook"
            'B' -> "Bishop"
            'N' -> "Knight"
            else -> "Pawn"
        }

        val isCapture = clean.contains('x')
        val targetSquare = if (clean.length >= 2) clean.takeLast(2) else ""

        val action = if (isCapture) {
            if (targetSquare.isNotEmpty()) "takes on $targetSquare" else "captures"
        } else {
            if (targetSquare.isNotEmpty()) "to $targetSquare" else ""
        }

        val suffix = when {
            isMate -> " (Checkmate!)"
            isCheck -> " (Check)"
            else -> ""
        }

        return "$pieceName $action$suffix".trim()
    }

    /**
     * Replay and analyze full game asynchronously using lookahead search.
     */
    suspend fun analyzeGameAsync(
        moves: List<Move>,
        depth: Int = 2,
        onProgress: (Float) -> Unit
    ): GameAnalysisReport = withContext(Dispatchers.Default) {
        var replayState = createInitialGameState()
        val analyzedList = mutableListOf<MoveAnalysis>()
        val totalMoves = moves.size

        if (totalMoves == 0) {
            return@withContext GameAnalysisReport(
                moves = emptyList(),
                openingName = "Opening: Unknown",
                whiteAccuracy = 100,
                blackAccuracy = 100,
                whiteBrilliantCount = 0,
                whiteBestCount = 0,
                whiteGoodCount = 0,
                whiteInaccuracyCount = 0,
                whiteMistakeCount = 0,
                whiteBlunderCount = 0,
                blackBrilliantCount = 0,
                blackBestCount = 0,
                blackGoodCount = 0,
                blackInaccuracyCount = 0,
                blackMistakeCount = 0,
                blackBlunderCount = 0,
                openingAccuracy = 100,
                middlegameAccuracy = 100,
                endgameAccuracy = 100,
                keyMoments = emptyList()
            )
        }

        val openingName = detectOpening(moves)
        val sansPlayed = mutableListOf<String>()

        for (idx in moves.indices) {
            val move = moves[idx]
            val mover = move.color
            val moveSan = move.san.ifEmpty { moveToSAN(replayState, move) }
            sansPlayed.add(moveSan)

            // Search best engine move from this position with lookahead
            val engineSearch = searchPosition(replayState, depth = depth)
            val bestMove = engineSearch.bestMove
            val bestScore = engineSearch.score // from mover's perspective
            val bestMoveSan = if (bestMove != null) moveToSAN(replayState, bestMove) else ""

            // Check if played move is the best move
            val isSameAsBest = bestMove != null &&
                    move.from == bestMove.from &&
                    move.to == bestMove.to &&
                    move.promotion == bestMove.promotion

            val playedScore = if (isSameAsBest) {
                bestScore
            } else {
                evaluateMove(replayState, move, depth = depth)
            }

            // Centipawn loss is how much worse the played move was compared to best move
            val centipawnLoss = (bestScore - playedScore).coerceAtLeast(0)

            val nextState = makeMove(replayState, move)

            // Centipawns from White's perspective
            val evalBeforeCp = if (replayState.turn == PieceColor.WHITE) bestScore else -bestScore
            val evalAfterCp = if (replayState.turn == PieceColor.WHITE) playedScore else -playedScore
            val evalDeltaCp = if (mover == PieceColor.WHITE) (evalAfterCp - evalBeforeCp) else (evalBeforeCp - evalAfterCp)

            val isBook = idx < 10 && isBookMove(sansPlayed)
            val isMate = isCheckmate(nextState)

            val classification = classifyMove(
                move = move,
                isSameAsBest = isSameAsBest,
                centipawnLoss = centipawnLoss,
                evalAfterMoverCp = playedScore,
                isBook = isBook,
                isCheckmate = isMate
            )

            val isMissedMate = bestMoveSan.endsWith("#") || centipawnLoss >= 50000

            // Generate contextual explanation with best move recommendations
            val explanation = generateExplanation(
                move = move,
                classification = classification,
                bestMoveSan = bestMoveSan,
                centipawnLoss = centipawnLoss,
                isCheck = isInCheck(nextState.board, nextState.turn),
                isMate = isMate,
                isMissedMate = isMissedMate
            )

            analyzedList.add(
                MoveAnalysis(
                    moveIndex = idx,
                    ply = idx + 1,
                    move = move,
                    moveColor = mover,
                    san = move.san.ifEmpty { moveToSAN(replayState, move) },
                    evalBeforeCp = evalBeforeCp,
                    evalAfterCp = evalAfterCp,
                    evalDeltaCp = evalDeltaCp,
                    centipawnLoss = centipawnLoss,
                    classification = classification,
                    explanation = explanation,
                    bestEngineMove = bestMove,
                    bestEngineSan = bestMoveSan,
                    stateAfterMove = nextState
                )
            )

            replayState = nextState
            onProgress((idx + 1).toFloat() / totalMoves)
            yield()
        }

        // Calculate statistics
        var whiteLossSum = 0.0
        var blackLossSum = 0.0
        var whiteMoveCount = 0
        var blackMoveCount = 0

        var wBrilliant = 0
        var wBest = 0
        var wGood = 0
        var wInacc = 0
        var wMistake = 0
        var wBlunder = 0

        var bBrilliant = 0
        var bBest = 0
        var bGood = 0
        var bInacc = 0
        var bMistake = 0
        var bBlunder = 0

        var openLossSum = 0.0
        var openCount = 0
        var midLossSum = 0.0
        var midCount = 0
        var endLossSum = 0.0
        var endCount = 0

        for (item in analyzedList) {
            val loss = item.centipawnLoss.toDouble()

            if (item.moveColor == PieceColor.WHITE) {
                whiteMoveCount++
                whiteLossSum += loss
                when (item.classification) {
                    MoveClassification.BRILLIANT -> wBrilliant++
                    MoveClassification.BEST, MoveClassification.STRONG, MoveClassification.BOOK -> wBest++
                    MoveClassification.GOOD -> wGood++
                    MoveClassification.INACCURACY -> wInacc++
                    MoveClassification.MISTAKE -> wMistake++
                    MoveClassification.BLUNDER -> wBlunder++
                }
            } else {
                blackMoveCount++
                blackLossSum += loss
                when (item.classification) {
                    MoveClassification.BRILLIANT -> bBrilliant++
                    MoveClassification.BEST, MoveClassification.STRONG, MoveClassification.BOOK -> bBest++
                    MoveClassification.GOOD -> bGood++
                    MoveClassification.INACCURACY -> bInacc++
                    MoveClassification.MISTAKE -> bMistake++
                    MoveClassification.BLUNDER -> bBlunder++
                }
            }

            // Phase tracking
            when {
                item.moveIndex < 10 -> {
                    openLossSum += loss
                    openCount++
                }
                item.moveIndex < 24 -> {
                    midLossSum += loss
                    midCount++
                }
                else -> {
                    endLossSum += loss
                    endCount++
                }
            }
        }

        fun toAccuracy(lossSum: Double, count: Int): Int {
            if (count == 0) return 95
            val avgLoss = lossSum / count
            val acc = (100.0 * exp(-0.007 * avgLoss)).toInt()
            return acc.coerceIn(30, 99)
        }

        val whiteAcc = toAccuracy(whiteLossSum, whiteMoveCount)
        val blackAcc = toAccuracy(blackLossSum, blackMoveCount)
        val openAcc = toAccuracy(openLossSum, openCount)
        val midAcc = toAccuracy(midLossSum, midCount)
        val endAcc = toAccuracy(endLossSum, endCount)

        // Key Moments Extraction
        val keyMoments = extractKeyMoments(analyzedList)

        return@withContext GameAnalysisReport(
            moves = analyzedList,
            openingName = openingName,
            whiteAccuracy = whiteAcc,
            blackAccuracy = blackAcc,
            whiteBrilliantCount = wBrilliant,
            whiteBestCount = wBest,
            whiteGoodCount = wGood,
            whiteInaccuracyCount = wInacc,
            whiteMistakeCount = wMistake,
            whiteBlunderCount = wBlunder,
            blackBrilliantCount = bBrilliant,
            blackBestCount = bBest,
            blackGoodCount = bGood,
            blackInaccuracyCount = bInacc,
            blackMistakeCount = bMistake,
            blackBlunderCount = bBlunder,
            openingAccuracy = openAcc,
            middlegameAccuracy = midAcc,
            endgameAccuracy = endAcc,
            keyMoments = keyMoments
        )
    }

    private fun classifyMove(
        move: Move,
        isSameAsBest: Boolean,
        centipawnLoss: Int,
        evalAfterMoverCp: Int,
        isBook: Boolean,
        isCheckmate: Boolean
    ): MoveClassification {
        if (isCheckmate) return MoveClassification.BEST
        if (isBook) return MoveClassification.BOOK

        // Brilliant: Sacrifice piece but retaining a dominant winning evaluation
        val isSacrifice = move.captured == null && move.piece != PieceType.PAWN && centipawnLoss <= 25 && evalAfterMoverCp > 180
        if (isSacrifice) return MoveClassification.BRILLIANT

        // Best move match or near-zero centipawn loss
        if (isSameAsBest || centipawnLoss <= 12) {
            return MoveClassification.BEST
        }
        if (centipawnLoss <= 35) return MoveClassification.GOOD
        if (centipawnLoss <= 100) return MoveClassification.INACCURACY
        if (centipawnLoss <= 220) return MoveClassification.MISTAKE
        return MoveClassification.BLUNDER
    }

    private fun generateExplanation(
        move: Move,
        classification: MoveClassification,
        bestMoveSan: String,
        centipawnLoss: Int,
        isCheck: Boolean,
        isMate: Boolean,
        isMissedMate: Boolean = false
    ): String {
        if (isMate) {
            return "Delivers decisive checkmate! Flawless conclusion to the attack."
        }

        val bestDesc = if (bestMoveSan.isNotEmpty()) describeSan(bestMoveSan) else ""
        val playedDesc = describeSan(move.san)

        if (isMissedMate && (classification == MoveClassification.BLUNDER || classification == MoveClassification.MISTAKE)) {
            val bestText = if (bestDesc.isNotEmpty()) "$bestMoveSan ($bestDesc)" else bestMoveSan
            val playedText = if (playedDesc.isNotEmpty()) "${move.san} ($playedDesc)" else move.san
            return "Missed Checkmate! You had an immediate win with $bestText. Playing $playedText missed the checkmate victory, though you still hold a winning position."
        }

        return when (classification) {
            MoveClassification.BOOK -> {
                "Standard theoretical book move, developing pieces and fighting for central space."
            }
            MoveClassification.BRILLIANT -> {
                "A brilliant tactical strike! Seizes decisive initiative through an inspired positional breakthrough."
            }
            MoveClassification.BEST, MoveClassification.STRONG -> {
                when {
                    isCheck -> "Forces the opponent's king into defensive repositioning with check."
                    move.isCastling != null -> "Tucks the king into safety and connects the rooks along the back rank."
                    move.captured != null -> "Wins material cleanly while reinforcing active board control."
                    move.piece == PieceType.KNIGHT -> "Develops the knight to an active outpost controlling key central squares."
                    move.piece == PieceType.BISHOP -> "Places the bishop along an open diagonal exerting heavy diagonal pressure."
                    move.piece == PieceType.ROOK -> "Commands an open or semi-open file with heavy piece pressure."
                    else -> "The engine's top choice, maximizing piece harmony and positional pressure."
                }
            }
            MoveClassification.GOOD -> {
                "A solid and reliable move that preserves your strategic position."
            }
            MoveClassification.INACCURACY -> {
                if (bestMoveSan.isNotEmpty()) {
                    val bestText = if (bestDesc.isNotEmpty()) "$bestMoveSan ($bestDesc)" else bestMoveSan
                    "Inaccuracy. Better was $bestText to maintain sharper active pressure. Playing ${move.san} is slightly passive."
                } else {
                    "A slightly suboptimal choice that cedes minor positional momentum."
                }
            }
            MoveClassification.MISTAKE -> {
                if (bestMoveSan.isNotEmpty()) {
                    val bestText = if (bestDesc.isNotEmpty()) "$bestMoveSan ($bestDesc)" else bestMoveSan
                    val pawnLoss = if (centipawnLoss < 50000) " (drops ${"%.1f".format(centipawnLoss / 100.0)} pawns)" else ""
                    "Mistake. Better was $bestText which keeps your advantage. Playing ${move.san} weakens your position$pawnLoss."
                } else {
                    "A notable mistake that leaves pieces disconnected and allows opponent to seize tempo."
                }
            }
            MoveClassification.BLUNDER -> {
                if (bestMoveSan.isNotEmpty()) {
                    val bestText = if (bestDesc.isNotEmpty()) "$bestMoveSan ($bestDesc)" else bestMoveSan
                    val pawnLoss = if (centipawnLoss < 50000) "drops an advantage of ${"%.1f".format(centipawnLoss / 100.0)} pawns" else "misses a decisive winning attack"
                    "Blunder! You should have played $bestText. Playing ${move.san} $pawnLoss."
                } else {
                    "A critical blunder forfeiting valuable material or allowing a severe attack."
                }
            }
        }
    }

    private fun extractKeyMoments(analyzed: List<MoveAnalysis>): List<KeyMoment> {
        val moments = mutableListOf<KeyMoment>()

        // 1. First Inaccuracy
        val firstInaccuracy = analyzed.find { it.classification == MoveClassification.INACCURACY }
        if (firstInaccuracy != null) {
            moments.add(
                KeyMoment(
                    moveIndex = firstInaccuracy.moveIndex,
                    title = "First Inaccuracy",
                    description = "Move ${firstInaccuracy.ply}: ${firstInaccuracy.san} conceded a minor positional advantage",
                    classification = MoveClassification.INACCURACY
                )
            )
        }

        // 2. Brilliant Move
        val brilliant = analyzed.find { it.classification == MoveClassification.BRILLIANT }
        if (brilliant != null) {
            moments.add(
                KeyMoment(
                    moveIndex = brilliant.moveIndex,
                    title = "Brilliant Move",
                    description = "Move ${brilliant.ply}: ${brilliant.san} an inspired tactical breakthrough",
                    classification = MoveClassification.BRILLIANT
                )
            )
        }

        // 3. Biggest Mistake or Blunder
        val worstMove = analyzed.filter { it.classification == MoveClassification.BLUNDER || it.classification == MoveClassification.MISTAKE }
            .maxByOrNull { it.centipawnLoss }
        if (worstMove != null) {
            val title = if (worstMove.classification == MoveClassification.BLUNDER) "Biggest Blunder" else "Biggest Mistake"
            val lossDesc = if (worstMove.centipawnLoss >= 50000) "missed checkmate" else "lost ${(worstMove.centipawnLoss / 100.0).let { "%.1f".format(it) }} pawns"
            moments.add(
                KeyMoment(
                    moveIndex = worstMove.moveIndex,
                    title = title,
                    description = "Move ${worstMove.ply}: ${worstMove.san} (${describeSan(worstMove.san)}) allowed opponent to capitalize ($lossDesc)",
                    classification = worstMove.classification
                )
            )
        }

        // 4. Tactical Opportunity
        val tactical = analyzed.find { it.classification == MoveClassification.BEST && it.centipawnLoss == 0 && it.move.captured != null }
        if (tactical != null && tactical.moveIndex != worstMove?.moveIndex) {
            moments.add(
                KeyMoment(
                    moveIndex = tactical.moveIndex,
                    title = "Tactical Opportunity",
                    description = "Move ${tactical.ply}: ${tactical.san} capitalized on the opponent's exposed pieces",
                    classification = MoveClassification.BEST
                )
            )
        }

        // 5. Decisive Advantage
        val decisive = analyzed.find { kotlin.math.abs(it.evalAfterCp) >= 300 }
        if (decisive != null) {
            moments.add(
                KeyMoment(
                    moveIndex = decisive.moveIndex,
                    title = "Decisive Advantage",
                    description = "Move ${decisive.ply}: ${decisive.san} established a commanding lead (${formatEval(decisive.evalAfterCp)})",
                    classification = MoveClassification.BEST
                )
            )
        }

        // 6. Checkmate
        val finalMove = analyzed.lastOrNull()
        if (finalMove != null && (finalMove.san.contains("#") || finalMove.classification == MoveClassification.BEST)) {
            moments.add(
                KeyMoment(
                    moveIndex = finalMove.moveIndex,
                    title = "Checkmate",
                    description = "Move ${finalMove.ply}: ${finalMove.san} delivered the deciding final blow",
                    classification = MoveClassification.BEST
                )
            )
        }

        return moments.distinctBy { it.moveIndex }
    }

    /**
     * Classic Morphy Opera Game (1858) as a rich sample game for analysis.
     * Contains book moves, mistakes, brilliant sacrifices, and checkmate.
     */
    fun createSampleOperaGame(): List<Move> {
        val pairs = listOf(
            "e2" to "e4", "e7" to "e5",
            "g1" to "f3", "d7" to "d6",
            "d2" to "d4", "c8" to "g4",
            "d4" to "e5", "g4" to "f3",
            "d1" to "f3", "d6" to "e5",
            "f1" to "c4", "g8" to "f6",
            "f3" to "b3", "d8" to "e7",
            "b1" to "c3", "c7" to "c6",
            "c1" to "g5", "b7" to "b5",
            "c3" to "b5", "c6" to "b5",
            "c4" to "b5", "b8" to "d7",
            "e1" to "c1", "a8" to "d8",
            "d1" to "d7", "d8" to "d7",
            "h1" to "d1", "e7" to "e6",
            "b5" to "d7", "f6" to "d7",
            "b3" to "b8", "d7" to "b8",
            "d1" to "d8"
        )
        var state = createInitialGameState()
        val moves = mutableListOf<Move>()
        for ((fromStr, toStr) in pairs) {
            val from = algebraicToSquare(fromStr)
            val to = algebraicToSquare(toStr)
            val move = getLegalMoves(state).firstOrNull { it.from == from && it.to == to }
            if (move != null) {
                move.san = moveToSAN(state, move)
                moves.add(move)
                state = makeMove(state, move)
            }
        }
        return moves
    }
}
