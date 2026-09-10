package com.example.chessgame.ai

import androidx.compose.ui.graphics.Color
import com.example.chessgame.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper to retrieve UI badge color for a move classification in hints.
 */
val MoveClassification.hintBadgeColor: Color
    get() = when (this) {
        MoveClassification.BEST -> Color(0xFFDFB36E)
        MoveClassification.STRONG -> Color(0xFFEDE0D0)
        MoveClassification.GOOD -> Color(0xFFAFA293)
        else -> Color(0xFF81D4FA)
    }

/**
 * Detailed candidate move suggestion model.
 */
data class MoveSuggestion(
    val move: Move,
    val from: Int,
    val to: Int,
    val notation: String,
    val evaluation: Double,
    val rank: Int,
    val classification: MoveClassification,
    val explanation: String
) {
    val fromAlgebraic: String get() = squareToAlgebraic(from)
    val toAlgebraic: String get() = squareToAlgebraic(to)
    val piece: PieceType get() = move.piece
    val color: PieceColor get() = move.color
    val san: String get() = move.san
}

/**
 * MoveSuggestionEngine:
 * Evaluates candidate legal moves for the current position using the existing
 * engine, ranking them to find the TOP 3 candidate moves without modifying game state.
 */
object MoveSuggestionEngine {

    /**
     * Generates top recommended legal moves for the given board position.
     * Guaranteed to only return strictly legal moves (no illegal or self-check moves).
     *
     * @param state Current game state
     * @param depth Search depth for minimax evaluation (default: 2 for instant responsive results)
     * @return Top 3 (or fewer) legal move suggestions ranked by engine evaluation
     */
    fun generateSuggestions(state: GameState, depth: Int = 2): List<MoveSuggestion> {
        val legalMoves = getLegalMoves(state)
        if (legalMoves.isEmpty()) return emptyList()

        // Evaluate candidate moves from the perspective of the moving player
        val scoredMoves = legalMoves.map { move ->
            val score = evaluateMove(state, move, depth)
            val san = moveToSAN(state, move)
            val moveWithSan = move.copy(san = san)
            Pair(moveWithSan, score)
        }

        // Sort descending: highest evaluation first
        val sorted = scoredMoves.sortedByDescending { it.second }
        val topCount = sorted.size.coerceAtMost(3)
        val topMoves = sorted.take(topCount)

        return topMoves.mapIndexed { index, (move, score) ->
            val rank = index + 1
            val classification = when (index) {
                0 -> MoveClassification.BEST
                1 -> MoveClassification.STRONG
                else -> MoveClassification.GOOD
            }

            // Convert centipawns to pawn units (e.g. +82 cp -> +0.82)
            val evalPawns = if (score >= 90000) {
                999.0
            } else if (score <= -90000) {
                -999.0
            } else {
                score / 100.0
            }

            val fromAlg = squareToAlgebraic(move.from)
            val toAlg = squareToAlgebraic(move.to)
            val notation = "$fromAlg → $toAlg"
            val explanation = generateExplanation(state, move, classification)

            MoveSuggestion(
                move = move,
                from = move.from,
                to = move.to,
                notation = notation,
                evaluation = evalPawns,
                rank = rank,
                classification = classification,
                explanation = explanation
            )
        }
    }

    /**
     * Asynchronously generates move suggestions in Dispatchers.Default
     * to ensure the UI stays completely fluid.
     */
    suspend fun generateSuggestionsAsync(state: GameState, depth: Int = 2): List<MoveSuggestion> {
        return withContext(Dispatchers.Default) {
            generateSuggestions(state, depth)
        }
    }

    /**
     * Contextual rule-based tactical explanations based on move type,
     * piece, board dynamics, and position consequences. Fully offline.
     */
    fun generateExplanation(state: GameState, move: Move, classification: MoveClassification): String {
        val nextState = makeMove(state, move)

        // 1. Immediate Checkmate
        if (isCheckmate(nextState)) {
            return "Delivers checkmate and wins the match immediately."
        }

        // 2. Check
        if (isInCheck(nextState.board, nextState.turn)) {
            return "Puts the opponent's king in check and creates immediate tactical pressure."
        }

        // 3. Pawn Promotion
        if (move.promotion != null) {
            val promoName = move.promotion.name.lowercase()
            return "Promotes the pawn into a powerful $promoName."
        }

        // 4. Castling
        if (move.isCastling != null) {
            return "Improves king safety and connects the rooks for active play."
        }

        // 5. Piece Capture
        if (move.captured != null || move.isEnPassant) {
            val victim = (move.captured ?: PieceType.PAWN).name.lowercase()
            return "Captures the opponent's $victim and improves material balance."
        }

        // 6. Knight Development / Center Control
        if (move.piece == PieceType.KNIGHT) {
            val toRank = getRank(move.to)
            val toFile = getFile(move.to)
            val isCenter = toRank in 2..5 && toFile in 2..5
            return if (isCenter) {
                "Develops the knight and increases control over the center."
            } else {
                "Develops the knight toward an active outpost."
            }
        }

        // 7. Bishop Development / Long Diagonal
        if (move.piece == PieceType.BISHOP) {
            val fromRank = getRank(move.from)
            val isDeveloping = (move.color == PieceColor.WHITE && fromRank == 0) ||
                               (move.color == PieceColor.BLACK && fromRank == 7)
            return if (isDeveloping) {
                "Develops the bishop along an active diagonal to contest key squares."
            } else {
                "Reposition bishop to maximize diagonal influence and piece coordination."
            }
        }

        // 8. Pawn Push / Central Space
        if (move.piece == PieceType.PAWN) {
            val toRank = getRank(move.to)
            val toFile = getFile(move.to)
            val isCentral = toFile in 3..4 && toRank in 3..4 // d4, e4, d5, e5
            return if (isCentral) {
                "Controls central squares and opens lines for your pieces."
            } else if ((move.color == PieceColor.WHITE && toRank >= 5) || (move.color == PieceColor.BLACK && toRank <= 2)) {
                "Advances pawn into enemy territory to gain space and restrict opponent mobility."
            } else {
                "Strengthens pawn structure and creates vital breathing room."
            }
        }

        // 9. Rook Activity
        if (move.piece == PieceType.ROOK) {
            return "Activates the rook along an open or semi-open file."
        }

        // 10. Queen Placement
        if (move.piece == PieceType.QUEEN) {
            return "Positions the queen to exert commanding pressure across multiple files."
        }

        // 11. King Step
        if (move.piece == PieceType.KING) {
            return "Safeguards the king and avoids dangerous tactical lines."
        }

        // 12. Fallback
        return when (classification) {
            MoveClassification.BEST -> "The strongest positional continuation in this position."
            MoveClassification.STRONG -> "A solid, principled move that maintains excellent control."
            MoveClassification.GOOD -> "A sound legal move that keeps your position safe."
            else -> "A solid, principled continuation in this position."
        }
    }
}
