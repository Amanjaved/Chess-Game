package com.example.chessgame.ai

import com.example.chessgame.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

enum class AIDifficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT
}

private const val CHECKMATE_SCORE = 100000

private fun scoreMove(move: Move): Int {
    var score = 0
    // MVV-LVA
    if (move.captured != null) {
        val victimVal = PIECE_VALUES[move.captured] ?: 0
        val attackerVal = PIECE_VALUES[move.piece] ?: 0
        score += victimVal * 10 - attackerVal
    }
    if (move.promotion != null) {
        score += PIECE_VALUES[move.promotion] ?: 0
    }
    return score
}

private fun orderMoves(moves: List<Move>): List<Move> {
    return moves.sortedByDescending { scoreMove(it) }
}

private fun quiescence(
    state: GameState,
    alphaParam: Int,
    betaParam: Int,
    maxQDepth: Int,
    color: PieceColor
): Int {
    var alpha = alphaParam
    val standPat = (if (color == PieceColor.WHITE) 1 else -1) * evaluateBoard(state)

    if (standPat >= betaParam) {
        return betaParam
    }
    if (alpha < standPat) {
        alpha = standPat
    }
    if (maxQDepth <= 0) {
        return standPat
    }

    val legalMoves = getLegalMoves(state)
    val captureMoves = orderMoves(legalMoves.filter { it.captured != null || it.promotion != null })

    for (move in captureMoves) {
        val nextState = makeMove(state, move)
        val score = -quiescence(nextState, -betaParam, -alpha, maxQDepth - 1, color.opponent())

        if (score >= betaParam) {
            return betaParam
        }
        if (score > alpha) {
            alpha = score
        }
    }

    return alpha
}

private data class MinimaxResult(val score: Int, val bestMove: Move? = null)

private fun minimax(
    state: GameState,
    depth: Int,
    alphaParam: Int,
    betaParam: Int,
    maximizingColor: PieceColor
): MinimaxResult {
    var alpha = alphaParam

    if (isCheckmate(state)) {
        return MinimaxResult(-CHECKMATE_SCORE - depth)
    }

    if (isStalemate(state) || isInsufficientMaterial(state)) {
        return MinimaxResult(0)
    }

    if (depth == 0) {
        val qScore = quiescence(state, alpha, betaParam, 2, state.turn)
        return MinimaxResult(qScore)
    }

    val legalMoves = orderMoves(getLegalMoves(state))
    if (legalMoves.isEmpty()) {
        return MinimaxResult(0)
    }

    var bestMove: Move = legalMoves[0]
    var maxScore = -Int.MAX_VALUE

    for (move in legalMoves) {
        val nextState = makeMove(state, move)
        val result = minimax(nextState, depth - 1, -betaParam, -alpha, maximizingColor)
        val score = -result.score

        if (score > maxScore) {
            maxScore = score
            bestMove = move
        }

        if (score > alpha) {
            alpha = score
        }

        if (alpha >= betaParam) {
            break // Alpha-beta cutoff
        }
    }

    return MinimaxResult(maxScore, bestMove)
}

data class EngineSearchResult(val score: Int, val bestMove: Move?)

fun searchPosition(state: GameState, depth: Int = 2): EngineSearchResult {
    val res = minimax(state, depth, -Int.MAX_VALUE, Int.MAX_VALUE, state.turn)
    return EngineSearchResult(res.score, res.bestMove)
}

fun evaluateMove(state: GameState, move: Move, depth: Int = 2): Int {
    val nextState = makeMove(state, move)
    if (isCheckmate(nextState)) {
        return CHECKMATE_SCORE
    }
    if (isStalemate(nextState) || isInsufficientMaterial(nextState)) {
        return 0
    }
    val res = minimax(nextState, (depth - 1).coerceAtLeast(1), -Int.MAX_VALUE, Int.MAX_VALUE, state.turn.opponent())
    return -res.score
}

fun calculateAIMoveDirect(state: GameState, difficulty: AIDifficulty): Move? {
    val legalMoves = getLegalMoves(state)
    if (legalMoves.isEmpty()) return null

    return when (difficulty) {
        AIDifficulty.EASY -> {
            val captures = legalMoves.filter { it.captured != null }
            if (captures.isNotEmpty() && Random.nextDouble() < 0.35) {
                captures[Random.nextInt(captures.size)]
            } else {
                legalMoves[Random.nextInt(legalMoves.size)]
            }
        }
        AIDifficulty.MEDIUM -> {
            val result = minimax(state, 2, -Int.MAX_VALUE, Int.MAX_VALUE, state.turn)
            if (Random.nextDouble() < 0.20 && legalMoves.size > 1) {
                val ordered = orderMoves(legalMoves)
                val topSlice = ordered.take(3.coerceAtMost(ordered.size))
                topSlice[Random.nextInt(topSlice.size)]
            } else {
                result.bestMove ?: legalMoves[0]
            }
        }
        AIDifficulty.HARD -> {
            val result = minimax(state, 3, -Int.MAX_VALUE, Int.MAX_VALUE, state.turn)
            result.bestMove ?: legalMoves[0]
        }
        AIDifficulty.EXPERT -> {
            val result = minimax(state, 4, -Int.MAX_VALUE, Int.MAX_VALUE, state.turn)
            result.bestMove ?: legalMoves[0]
        }
    }
}

suspend fun calculateAIMoveAsync(state: GameState, difficulty: AIDifficulty): Move? {
    val minDelay = when (difficulty) {
        AIDifficulty.EASY -> 850L
        AIDifficulty.MEDIUM -> 1100L
        AIDifficulty.HARD -> 1300L
        AIDifficulty.EXPERT -> 1500L
    }

    val startTime = System.currentTimeMillis()

    val move = withContext(Dispatchers.Default) {
        calculateAIMoveDirect(state, difficulty)
    }

    val elapsed = System.currentTimeMillis() - startTime
    val remaining = (minDelay - elapsed).coerceAtLeast(0L)
    if (remaining > 0) {
        delay(remaining)
    }

    return move
}
