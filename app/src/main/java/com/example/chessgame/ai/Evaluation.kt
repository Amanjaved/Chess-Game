package com.example.chessgame.ai

import com.example.chessgame.engine.*

val PIECE_VALUES = mapOf(
    PieceType.PAWN to 100,
    PieceType.KNIGHT to 320,
    PieceType.BISHOP to 330,
    PieceType.ROOK to 500,
    PieceType.QUEEN to 900,
    PieceType.KING to 20000
)

private val PAWN_TABLE = intArrayOf(
     0,   0,   0,   0,   0,   0,   0,   0,
     5,  10,  10, -20, -20,  10,  10,   5,
     5,  -5, -10,   0,   0, -10,  -5,   5,
     0,   0,   0,  25,  25,   0,   0,   0,
     5,   5,  10,  27,  27,  10,   5,   5,
    10,  10,  20,  30,  30,  20,  10,  10,
    50,  50,  50,  50,  50,  50,  50,  50,
     0,   0,   0,   0,   0,   0,   0,   0
)

private val KNIGHT_TABLE = intArrayOf(
   -50, -40, -30, -30, -30, -30, -40, -50,
   -40, -20,   0,   5,   5,   0, -20, -40,
   -30,   5,  10,  15,  15,  10,   5, -30,
   -30,   0,  15,  20,  20,  15,   0, -30,
   -30,   5,  15,  20,  20,  15,   5, -30,
   -30,   0,  10,  15,  15,  10,   0, -30,
   -40, -20,   0,   0,   0,   0, -20, -40,
   -50, -40, -30, -30, -30, -30, -40, -50
)

private val BISHOP_TABLE = intArrayOf(
   -20, -10, -10, -10, -10, -10, -10, -20,
   -10,   5,   0,   0,   0,   0,   5, -10,
   -10,  10,  10,  10,  10,  10,  10, -10,
   -10,   0,  10,  10,  10,  10,   0, -10,
   -10,   5,   5,  10,  10,   5,   5, -10,
   -10,  10,   5,  10,  10,   5,  10, -10,
   -10,   5,   0,   0,   0,   0,   5, -10,
   -20, -10, -10, -10, -10, -10, -10, -20
)

private val ROOK_TABLE = intArrayOf(
     0,   0,   0,   5,   5,   0,   0,   0,
    -5,   0,   0,   0,   0,   0,   0,  -5,
    -5,   0,   0,   0,   0,   0,   0,  -5,
    -5,   0,   0,   0,   0,   0,   0,  -5,
    -5,   0,   0,   0,   0,   0,   0,  -5,
    -5,   0,   0,   0,   0,   0,   0,  -5,
     5,  10,  10,  10,  10,  10,  10,   5,
     0,   0,   0,   0,   0,   0,   0,   0
)

private val QUEEN_TABLE = intArrayOf(
   -20, -10, -10,  -5,  -5, -10, -10, -20,
   -10,   0,   5,   0,   0,   0,   0, -10,
   -10,   5,   5,   5,   5,   5,   0, -10,
     0,   0,   5,   5,   5,   5,   0,  -5,
    -5,   0,   5,   5,   5,   5,   0,  -5,
   -10,   0,   5,   5,   5,   5,   0, -10,
   -10,   0,   0,   0,   0,   0,   0, -10,
   -20, -10, -10,  -5,  -5, -10, -10, -20
)

private val KING_TABLE_MIDDLE = intArrayOf(
    20,  30,  10,   0,   0,  10,  30,  20,
    20,  20,   0,   0,   0,   0,  20,  20,
   -10, -20, -20, -20, -20, -20, -20, -10,
   -20, -30, -30, -40, -40, -30, -30, -20,
   -30, -40, -40, -50, -50, -40, -40, -30,
   -30, -40, -40, -50, -50, -40, -40, -30,
   -30, -40, -40, -50, -50, -40, -40, -30,
   -30, -40, -40, -50, -50, -40, -40, -30
)

private val KING_TABLE_END = intArrayOf(
   -50, -30, -30, -30, -30, -30, -30, -50,
   -30, -10,   0,   0,   0,   0, -10, -30,
   -30,   0,  20,  25,  25,  20,   0, -30,
   -30,   0,  25,  35,  35,  25,   0, -30,
   -30,   0,  25,  35,  35,  25,   0, -30,
   -30,   0,  20,  25,  25,  20,   0, -30,
   -30, -10,   0,   0,   0,   0, -10, -30,
   -50, -30, -30, -30, -30, -30, -30, -50
)

private fun getPieceSquareValue(piece: PieceType, square: Int, color: PieceColor, isEndgame: Boolean): Int {
    val f = getFile(square)
    val r = getRank(square)
    val effectiveRank = if (color == PieceColor.WHITE) r else 7 - r
    val index = effectiveRank * 8 + f

    return when (piece) {
        PieceType.PAWN -> PAWN_TABLE[index]
        PieceType.KNIGHT -> KNIGHT_TABLE[index]
        PieceType.BISHOP -> BISHOP_TABLE[index]
        PieceType.ROOK -> ROOK_TABLE[index]
        PieceType.QUEEN -> QUEEN_TABLE[index]
        PieceType.KING -> if (isEndgame) KING_TABLE_END[index] else KING_TABLE_MIDDLE[index]
    }
}

fun evaluateBoard(state: GameState): Int {
    var materialWhite = 0
    var materialBlack = 0
    var positionalWhite = 0
    var positionalBlack = 0
    var totalNonPawnMaterial = 0

    for (i in 0 until 64) {
        val piece = state.board[i] ?: continue
        val valScore = PIECE_VALUES[piece.type] ?: 0
        if (piece.type != PieceType.PAWN && piece.type != PieceType.KING) {
            totalNonPawnMaterial += valScore
        }

        if (piece.color == PieceColor.WHITE) {
            materialWhite += valScore
        } else {
            materialBlack += valScore
        }
    }

    val isEndgame = totalNonPawnMaterial < 2600

    for (i in 0 until 64) {
        val piece = state.board[i] ?: continue
        val pst = getPieceSquareValue(piece.type, i, piece.color, isEndgame)
        if (piece.color == PieceColor.WHITE) {
            positionalWhite += pst
        } else {
            positionalBlack += pst
        }
    }

    val whiteScore = materialWhite + positionalWhite
    val blackScore = materialBlack + positionalBlack

    return whiteScore - blackScore
}
