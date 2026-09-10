package com.example.chessgame.engine

fun isCheckmate(state: GameState): Boolean {
    return isInCheck(state.board, state.turn) && getLegalMoves(state).isEmpty()
}

fun isStalemate(state: GameState): Boolean {
    return !isInCheck(state.board, state.turn) && getLegalMoves(state).isEmpty()
}

fun isFiftyMoveRule(state: GameState): Boolean {
    return state.halfmoveClock >= 100
}

fun isThreefoldRepetition(state: GameState): Boolean {
    if (state.positionHistory.isEmpty()) return false
    val currentSig = state.positionHistory.last()
    var count = 0
    for (sig in state.positionHistory) {
        if (sig == currentSig) {
            count++
            if (count >= 3) return true
        }
    }
    return false
}

fun isInsufficientMaterial(state: GameState): Boolean {
    val pieces = mutableListOf<Pair<Piece, Int>>()
    for (i in 0 until 64) {
        val p = state.board[i]
        if (p != null) {
            pieces.add(Pair(p, i))
        }
    }

    // If there is any pawn, rook, or queen, it is sufficient
    if (pieces.any { it.first.type == PieceType.PAWN || it.first.type == PieceType.ROOK || it.first.type == PieceType.QUEEN }) {
        return false
    }

    // King vs King
    if (pieces.size == 2) {
        return true
    }

    // King + Bishop vs King or King + Knight vs King
    if (pieces.size == 3) {
        return pieces.any { it.first.type == PieceType.BISHOP || it.first.type == PieceType.KNIGHT }
    }

    // King + Bishop vs King + Bishop (with bishops on same color)
    if (pieces.size == 4) {
        val whiteBishops = pieces.filter { it.first.color == PieceColor.WHITE && it.first.type == PieceType.BISHOP }
        val blackBishops = pieces.filter { it.first.color == PieceColor.BLACK && it.first.type == PieceType.BISHOP }
        if (whiteBishops.size == 1 && blackBishops.size == 1) {
            val wbLight = isLightSquare(whiteBishops[0].second)
            val bbLight = isLightSquare(blackBishops[0].second)
            return wbLight == bbLight
        }
    }

    return false
}

fun getGameOutcome(state: GameState): GameOutcome {
    if (isCheckmate(state)) {
        val winner = state.turn.opponent()
        return GameOutcome(isOver = true, winner = winner, isDraw = false, reason = OutcomeReason.CHECKMATE)
    }

    if (isStalemate(state)) {
        return GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.STALEMATE)
    }

    if (isInsufficientMaterial(state)) {
        return GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.INSUFFICIENT_MATERIAL)
    }

    if (isFiftyMoveRule(state)) {
        return GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.FIFTY_MOVE_RULE)
    }

    if (isThreefoldRepetition(state)) {
        return GameOutcome(isOver = true, winner = null, isDraw = true, reason = OutcomeReason.THREEFOLD_REPETITION)
    }

    return GameOutcome(isOver = false, winner = null, isDraw = false, reason = null)
}
