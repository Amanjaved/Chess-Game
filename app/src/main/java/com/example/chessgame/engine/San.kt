package com.example.chessgame.engine

fun moveToSAN(state: GameState, move: Move): String {
    if (move.isCastling == CastlingType.KINGSIDE) {
        return "O-O"
    }
    if (move.isCastling == CastlingType.QUEENSIDE) {
        return "O-O-O"
    }

    val from = move.from
    val to = move.to
    val piece = move.piece
    val toSqStr = squareToAlgebraic(to)
    val fromFile = FILES[getFile(from)]
    val fromRank = RANKS[getRank(from)]

    val sb = StringBuilder()

    if (piece == PieceType.PAWN) {
        if (move.captured != null || move.isEnPassant) {
            sb.append("${fromFile}x$toSqStr")
        } else {
            sb.append(toSqStr)
        }
        if (move.promotion != null) {
            val promoChar = when (move.promotion) {
                PieceType.QUEEN -> "Q"
                PieceType.ROOK -> "R"
                PieceType.BISHOP -> "B"
                PieceType.KNIGHT -> "N"
                else -> "Q"
            }
            sb.append("=$promoChar")
        }
    } else {
        val pieceChar = when (piece) {
            PieceType.KNIGHT -> "N"
            PieceType.BISHOP -> "B"
            PieceType.ROOK -> "R"
            PieceType.QUEEN -> "Q"
            PieceType.KING -> "K"
            else -> ""
        }
        sb.append(pieceChar)

        // Disambiguation
        val legalMoves = getLegalMoves(state)
        val candidates = legalMoves.filter { it.to == to && it.piece == piece && it.from != from }

        if (candidates.isNotEmpty()) {
            val sameFile = candidates.any { getFile(it.from) == getFile(from) }
            val sameRank = candidates.any { getRank(it.from) == getRank(from) }

            if (!sameFile) {
                sb.append(fromFile)
            } else if (!sameRank) {
                sb.append(fromRank)
            } else {
                sb.append(fromFile).append(fromRank)
            }
        }

        if (move.captured != null) {
            sb.append("x")
        }
        sb.append(toSqStr)
    }

    // Check check / checkmate suffix
    val nextState = makeMove(state, move)
    if (isCheckmate(nextState)) {
        sb.append("#")
    } else if (isInCheck(nextState.board, nextState.turn)) {
        sb.append("+")
    }

    return sb.toString()
}
