package com.example.chessgame.engine

enum class PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING
}

enum class PieceColor {
    WHITE,
    BLACK;

    fun opponent(): PieceColor = if (this == WHITE) BLACK else WHITE
}

data class Piece(
    val type: PieceType,
    val color: PieceColor
)

enum class CastlingType {
    KINGSIDE,
    QUEENSIDE
}

data class Move(
    val from: Int,
    val to: Int,
    val piece: PieceType,
    val color: PieceColor,
    val captured: PieceType? = null,
    val promotion: PieceType? = null,
    val isCastling: CastlingType? = null,
    val isEnPassant: Boolean = false,
    var san: String = ""
)

data class CastlingRights(
    var whiteKingside: Boolean = true,
    var whiteQueenside: Boolean = true,
    var blackKingside: Boolean = true,
    var blackQueenside: Boolean = true
) {
    fun copy(): CastlingRights = CastlingRights(
        whiteKingside = whiteKingside,
        whiteQueenside = whiteQueenside,
        blackKingside = blackKingside,
        blackQueenside = blackQueenside
    )
}

data class GameState(
    val board: Array<Piece?>,
    var turn: PieceColor = PieceColor.WHITE,
    val castling: CastlingRights = CastlingRights(),
    var enPassant: Int? = null,
    var halfmoveClock: Int = 0,
    var fullmoveNumber: Int = 1,
    val history: MutableList<Move> = mutableListOf(),
    val positionHistory: MutableList<String> = mutableListOf()
) {
    fun copy(): GameState {
        val newBoard = Array(64) { i -> board[i] }
        val newState = GameState(
            board = newBoard,
            turn = turn,
            castling = castling.copy(),
            enPassant = enPassant,
            halfmoveClock = halfmoveClock,
            fullmoveNumber = fullmoveNumber,
            history = history.toMutableList(),
            positionHistory = positionHistory.toMutableList()
        )
        return newState
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState
        if (!board.contentEquals(other.board)) return false
        if (turn != other.turn) return false
        if (castling != other.castling) return false
        if (enPassant != other.enPassant) return false
        if (halfmoveClock != other.halfmoveClock) return false
        if (fullmoveNumber != other.fullmoveNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = board.contentHashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + castling.hashCode()
        result = 31 * result + (enPassant ?: 0)
        result = 31 * result + halfmoveClock
        result = 31 * result + fullmoveNumber
        return result
    }
}

enum class OutcomeReason {
    CHECKMATE,
    STALEMATE,
    INSUFFICIENT_MATERIAL,
    FIFTY_MOVE_RULE,
    THREEFOLD_REPETITION,
    RESIGNATION,
    MUTUAL_AGREEMENT
}

data class GameOutcome(
    val isOver: Boolean,
    val winner: PieceColor? = null,
    val isDraw: Boolean = false,
    val reason: OutcomeReason? = null
)
