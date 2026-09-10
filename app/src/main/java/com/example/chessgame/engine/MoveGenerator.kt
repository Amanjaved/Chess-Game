package com.example.chessgame.engine

import kotlin.math.abs

fun findKing(board: Array<Piece?>, color: PieceColor): Int {
    for (i in 0 until 64) {
        val p = board[i]
        if (p != null && p.type == PieceType.KING && p.color == color) {
            return i
        }
    }
    return -1
}

fun isSquareAttacked(board: Array<Piece?>, targetSquare: Int, byColor: PieceColor): Boolean {
    val targetFile = getFile(targetSquare)
    val targetRank = getRank(targetSquare)

    // 1. Pawns
    val pawnAttackRank = if (byColor == PieceColor.WHITE) targetRank - 1 else targetRank + 1
    if (pawnAttackRank in 0..7) {
        for (df in intArrayOf(-1, 1)) {
            val f = targetFile + df
            if (f in 0..7) {
                val sq = squareFromCoords(f, pawnAttackRank)
                val p = board[sq]
                if (p != null && p.color == byColor && p.type == PieceType.PAWN) {
                    return true
                }
            }
        }
    }

    // 2. Knights
    val knightOffsets = arrayOf(
        intArrayOf(-2, -1), intArrayOf(-2, 1), intArrayOf(-1, -2), intArrayOf(-1, 2),
        intArrayOf(1, -2), intArrayOf(1, 2), intArrayOf(2, -1), intArrayOf(2, 1)
    )
    for (offset in knightOffsets) {
        val f = targetFile + offset[0]
        val r = targetRank + offset[1]
        if (f in 0..7 && r in 0..7) {
            val sq = squareFromCoords(f, r)
            val p = board[sq]
            if (p != null && p.color == byColor && p.type == PieceType.KNIGHT) {
                return true
            }
        }
    }

    // 3. Kings
    for (df in -1..1) {
        for (dr in -1..1) {
            if (df == 0 && dr == 0) continue
            val f = targetFile + df
            val r = targetRank + dr
            if (f in 0..7 && r in 0..7) {
                val sq = squareFromCoords(f, r)
                val p = board[sq]
                if (p != null && p.color == byColor && p.type == PieceType.KING) {
                    return true
                }
            }
        }
    }

    // 4. Straight rays (Rook, Queen)
    val straightDirs = arrayOf(
        intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1)
    )
    for (dir in straightDirs) {
        var f = targetFile + dir[0]
        var r = targetRank + dir[1]
        while (f in 0..7 && r in 0..7) {
            val sq = squareFromCoords(f, r)
            val p = board[sq]
            if (p != null) {
                if (p.color == byColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                    return true
                }
                break
            }
            f += dir[0]
            r += dir[1]
        }
    }

    // 5. Diagonal rays (Bishop, Queen)
    val diagDirs = arrayOf(
        intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1)
    )
    for (dir in diagDirs) {
        var f = targetFile + dir[0]
        var r = targetRank + dir[1]
        while (f in 0..7 && r in 0..7) {
            val sq = squareFromCoords(f, r)
            val p = board[sq]
            if (p != null) {
                if (p.color == byColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                    return true
                }
                break
            }
            f += dir[0]
            r += dir[1]
        }
    }

    return false
}

fun isInCheck(board: Array<Piece?>, color: PieceColor): Boolean {
    val kingSq = findKing(board, color)
    if (kingSq == -1) return false
    return isSquareAttacked(board, kingSq, color.opponent())
}

fun generatePseudoLegalMoves(state: GameState): List<Move> {
    val moves = mutableListOf<Move>()
    val board = state.board
    val turn = state.turn
    val opponent = turn.opponent()

    for (sq in 0 until 64) {
        val piece = board[sq] ?: continue
        if (piece.color != turn) continue

        val file = getFile(sq)
        val rank = getRank(sq)

        when (piece.type) {
            PieceType.PAWN -> {
                val forwardRank = if (turn == PieceColor.WHITE) rank + 1 else rank - 1
                val startRank = if (turn == PieceColor.WHITE) 1 else 6
                val promoRank = if (turn == PieceColor.WHITE) 7 else 0

                // Single advance
                if (forwardRank in 0..7) {
                    val forwardSq = squareFromCoords(file, forwardRank)
                    if (board[forwardSq] == null) {
                        if (forwardRank == promoRank) {
                            val promos = arrayOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)
                            for (promo in promos) {
                                moves.add(Move(sq, forwardSq, PieceType.PAWN, turn, promotion = promo))
                            }
                        } else {
                            moves.add(Move(sq, forwardSq, PieceType.PAWN, turn))
                        }

                        // Double advance
                        if (rank == startRank) {
                            val doubleRank = if (turn == PieceColor.WHITE) rank + 2 else rank - 2
                            val doubleSq = squareFromCoords(file, doubleRank)
                            if (board[doubleSq] == null) {
                                moves.add(Move(sq, doubleSq, PieceType.PAWN, turn))
                            }
                        }
                    }
                }

                // Diagonal captures
                for (df in intArrayOf(-1, 1)) {
                    val capFile = file + df
                    if (capFile in 0..7 && forwardRank in 0..7) {
                        val capSq = squareFromCoords(capFile, forwardRank)
                        val targetPiece = board[capSq]

                        if (targetPiece != null && targetPiece.color == opponent) {
                            if (forwardRank == promoRank) {
                                val promos = arrayOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)
                                for (promo in promos) {
                                    moves.add(Move(sq, capSq, PieceType.PAWN, turn, captured = targetPiece.type, promotion = promo))
                                }
                            } else {
                                moves.add(Move(sq, capSq, PieceType.PAWN, turn, captured = targetPiece.type))
                            }
                        } else if (state.enPassant != null && capSq == state.enPassant) {
                            moves.add(Move(sq, capSq, PieceType.PAWN, turn, captured = PieceType.PAWN, isEnPassant = true))
                        }
                    }
                }
            }

            PieceType.KNIGHT -> {
                val knightOffsets = arrayOf(
                    intArrayOf(-2, -1), intArrayOf(-2, 1), intArrayOf(-1, -2), intArrayOf(-1, 2),
                    intArrayOf(1, -2), intArrayOf(1, 2), intArrayOf(2, -1), intArrayOf(2, 1)
                )
                for (offset in knightOffsets) {
                    val f = file + offset[0]
                    val r = rank + offset[1]
                    if (f in 0..7 && r in 0..7) {
                        val destSq = squareFromCoords(f, r)
                        val targetPiece = board[destSq]
                        if (targetPiece == null) {
                            moves.add(Move(sq, destSq, PieceType.KNIGHT, turn))
                        } else if (targetPiece.color == opponent) {
                            moves.add(Move(sq, destSq, PieceType.KNIGHT, turn, captured = targetPiece.type))
                        }
                    }
                }
            }

            PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN -> {
                val directions = mutableListOf<IntArray>()
                if (piece.type == PieceType.BISHOP || piece.type == PieceType.QUEEN) {
                    directions.add(intArrayOf(1, 1))
                    directions.add(intArrayOf(1, -1))
                    directions.add(intArrayOf(-1, 1))
                    directions.add(intArrayOf(-1, -1))
                }
                if (piece.type == PieceType.ROOK || piece.type == PieceType.QUEEN) {
                    directions.add(intArrayOf(1, 0))
                    directions.add(intArrayOf(-1, 0))
                    directions.add(intArrayOf(0, 1))
                    directions.add(intArrayOf(0, -1))
                }

                for (dir in directions) {
                    var f = file + dir[0]
                    var r = rank + dir[1]
                    while (f in 0..7 && r in 0..7) {
                        val destSq = squareFromCoords(f, r)
                        val targetPiece = board[destSq]
                        if (targetPiece == null) {
                            moves.add(Move(sq, destSq, piece.type, turn))
                        } else {
                            if (targetPiece.color == opponent) {
                                moves.add(Move(sq, destSq, piece.type, turn, captured = targetPiece.type))
                            }
                            break
                        }
                        f += dir[0]
                        r += dir[1]
                    }
                }
            }

            PieceType.KING -> {
                // Normal 1-square moves
                for (df in -1..1) {
                    for (dr in -1..1) {
                        if (df == 0 && dr == 0) continue
                        val f = file + df
                        val r = rank + dr
                        if (f in 0..7 && r in 0..7) {
                            val destSq = squareFromCoords(f, r)
                            val targetPiece = board[destSq]
                            if (targetPiece == null) {
                                moves.add(Move(sq, destSq, PieceType.KING, turn))
                            } else if (targetPiece.color == opponent) {
                                moves.add(Move(sq, destSq, PieceType.KING, turn, captured = targetPiece.type))
                            }
                        }
                    }
                }

                // Castling
                val currentInCheck = isSquareAttacked(board, sq, opponent)
                if (!currentInCheck) {
                    if (turn == PieceColor.WHITE && sq == 4) {
                        // Kingside: e1 (4) -> g1 (6), rook on h1 (7)
                        if (state.castling.whiteKingside &&
                            board[5] == null && board[6] == null &&
                            board[7]?.type == PieceType.ROOK && board[7]?.color == PieceColor.WHITE &&
                            !isSquareAttacked(board, 5, opponent) &&
                            !isSquareAttacked(board, 6, opponent)
                        ) {
                            moves.add(Move(4, 6, PieceType.KING, PieceColor.WHITE, isCastling = CastlingType.KINGSIDE))
                        }

                        // Queenside: e1 (4) -> c1 (2), rook on a1 (0)
                        if (state.castling.whiteQueenside &&
                            board[1] == null && board[2] == null && board[3] == null &&
                            board[0]?.type == PieceType.ROOK && board[0]?.color == PieceColor.WHITE &&
                            !isSquareAttacked(board, 3, opponent) &&
                            !isSquareAttacked(board, 2, opponent)
                        ) {
                            moves.add(Move(4, 2, PieceType.KING, PieceColor.WHITE, isCastling = CastlingType.QUEENSIDE))
                        }
                    } else if (turn == PieceColor.BLACK && sq == 60) {
                        // Kingside: e8 (60) -> g8 (62), rook on h8 (63)
                        if (state.castling.blackKingside &&
                            board[61] == null && board[62] == null &&
                            board[63]?.type == PieceType.ROOK && board[63]?.color == PieceColor.BLACK &&
                            !isSquareAttacked(board, 61, opponent) &&
                            !isSquareAttacked(board, 62, opponent)
                        ) {
                            moves.add(Move(60, 62, PieceType.KING, PieceColor.BLACK, isCastling = CastlingType.KINGSIDE))
                        }

                        // Queenside: e8 (60) -> c8 (58), rook on a8 (56)
                        if (state.castling.blackQueenside &&
                            board[57] == null && board[58] == null && board[59] == null &&
                            board[56]?.type == PieceType.ROOK && board[56]?.color == PieceColor.BLACK &&
                            !isSquareAttacked(board, 59, opponent) &&
                            !isSquareAttacked(board, 58, opponent)
                        ) {
                            moves.add(Move(60, 58, PieceType.KING, PieceColor.BLACK, isCastling = CastlingType.QUEENSIDE))
                        }
                    }
                }
            }
        }
    }

    return moves
}

fun makeMove(state: GameState, move: Move): GameState {
    val next = state.copy()
    val from = move.from
    val to = move.to
    val piece = move.piece
    val color = move.color

    val finalType = move.promotion ?: piece
    next.board[to] = Piece(finalType, color)
    next.board[from] = null

    // Special castling rook move
    if (move.isCastling == CastlingType.KINGSIDE) {
        if (color == PieceColor.WHITE) {
            next.board[5] = Piece(PieceType.ROOK, PieceColor.WHITE)
            next.board[7] = null
        } else {
            next.board[61] = Piece(PieceType.ROOK, PieceColor.BLACK)
            next.board[63] = null
        }
    } else if (move.isCastling == CastlingType.QUEENSIDE) {
        if (color == PieceColor.WHITE) {
            next.board[3] = Piece(PieceType.ROOK, PieceColor.WHITE)
            next.board[0] = null
        } else {
            next.board[59] = Piece(PieceType.ROOK, PieceColor.BLACK)
            next.board[56] = null
        }
    } else if (move.isEnPassant) {
        val capRank = getRank(from)
        val capFile = getFile(to)
        next.board[squareFromCoords(capFile, capRank)] = null
    }

    // Castling rights update
    if (piece == PieceType.KING) {
        if (color == PieceColor.WHITE) {
            next.castling.whiteKingside = false
            next.castling.whiteQueenside = false
        } else {
            next.castling.blackKingside = false
            next.castling.blackQueenside = false
        }
    } else if (piece == PieceType.ROOK) {
        if (color == PieceColor.WHITE) {
            if (from == 0) next.castling.whiteQueenside = false
            if (from == 7) next.castling.whiteKingside = false
        } else {
            if (from == 56) next.castling.blackQueenside = false
            if (from == 63) next.castling.blackKingside = false
        }
    }

    if (to == 0) next.castling.whiteQueenside = false
    if (to == 7) next.castling.whiteKingside = false
    if (to == 56) next.castling.blackQueenside = false
    if (to == 63) next.castling.blackKingside = false

    // En passant target update
    if (piece == PieceType.PAWN && abs(to - from) == 16) {
        next.enPassant = (from + to) / 2
    } else {
        next.enPassant = null
    }

    // 50-move clock
    if (piece == PieceType.PAWN || move.captured != null) {
        next.halfmoveClock = 0
    } else {
        next.halfmoveClock += 1
    }

    // Fullmove counter
    if (color == PieceColor.BLACK) {
        next.fullmoveNumber += 1
    }

    next.turn = color.opponent()
    next.history.add(move)
    next.positionHistory.add(getPositionSignature(next))

    return next
}

fun getLegalMoves(state: GameState): List<Move> {
    val pseudoMoves = generatePseudoLegalMoves(state)
    val legalMoves = mutableListOf<Move>()

    for (move in pseudoMoves) {
        val nextState = makeMove(state, move)
        if (!isInCheck(nextState.board, state.turn)) {
            legalMoves.add(move)
        }
    }

    return legalMoves
}

fun getLegalMovesForSquare(state: GameState, square: Int): List<Move> {
    return getLegalMoves(state).filter { it.from == square }
}
