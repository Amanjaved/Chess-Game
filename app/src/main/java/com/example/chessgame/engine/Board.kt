package com.example.chessgame.engine

val FILES = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")
val RANKS = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")

const val STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

fun getFile(square: Int): Int = square % 8

fun getRank(square: Int): Int = square / 8

fun squareFromCoords(file: Int, rank: Int): Int = rank * 8 + file

fun squareToAlgebraic(square: Int): String {
    val f = getFile(square)
    val r = getRank(square)
    return "${FILES[f]}${RANKS[r]}"
}

fun algebraicToSquare(coord: String): Int {
    require(coord.length >= 2) { "Invalid algebraic coordinate: $coord" }
    val f = FILES.indexOf(coord[0].lowercase())
    val r = RANKS.indexOf(coord[1].toString())
    require(f != -1 && r != -1) { "Invalid algebraic coordinate: $coord" }
    return squareFromCoords(f, r)
}

fun isLightSquare(square: Int): Boolean {
    val f = getFile(square)
    val r = getRank(square)
    // a1 (0,0) is dark, b1 (1,0) is light
    return (f + r) % 2 != 0
}

fun createStartingBoard(): Array<Piece?> {
    val board = arrayOfNulls<Piece>(64)

    // White pieces (rank 0 and 1)
    board[0] = Piece(PieceType.ROOK, PieceColor.WHITE)
    board[1] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
    board[2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
    board[3] = Piece(PieceType.QUEEN, PieceColor.WHITE)
    board[4] = Piece(PieceType.KING, PieceColor.WHITE)
    board[5] = Piece(PieceType.BISHOP, PieceColor.WHITE)
    board[6] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
    board[7] = Piece(PieceType.ROOK, PieceColor.WHITE)

    for (f in 0..7) {
        board[squareFromCoords(f, 1)] = Piece(PieceType.PAWN, PieceColor.WHITE)
    }

    // Black pieces (rank 7 and 6)
    board[56] = Piece(PieceType.ROOK, PieceColor.BLACK)
    board[57] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
    board[58] = Piece(PieceType.BISHOP, PieceColor.BLACK)
    board[59] = Piece(PieceType.QUEEN, PieceColor.BLACK)
    board[60] = Piece(PieceType.KING, PieceColor.BLACK)
    board[61] = Piece(PieceType.BISHOP, PieceColor.BLACK)
    board[62] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
    board[63] = Piece(PieceType.ROOK, PieceColor.BLACK)

    for (f in 0..7) {
        board[squareFromCoords(f, 6)] = Piece(PieceType.PAWN, PieceColor.BLACK)
    }

    return board
}

fun createInitialGameState(): GameState {
    val state = GameState(
        board = createStartingBoard(),
        turn = PieceColor.WHITE,
        castling = CastlingRights(),
        enPassant = null,
        halfmoveClock = 0,
        fullmoveNumber = 1
    )
    state.positionHistory.add(getPositionSignature(state))
    return state
}

fun getPositionSignature(state: GameState): String {
    val sb = StringBuilder()
    for (i in 0 until 64) {
        val piece = state.board[i]
        if (piece == null) {
            sb.append('.')
        } else {
            val char = when (piece.type) {
                PieceType.PAWN -> 'p'
                PieceType.KNIGHT -> 'n'
                PieceType.BISHOP -> 'b'
                PieceType.ROOK -> 'r'
                PieceType.QUEEN -> 'q'
                PieceType.KING -> 'k'
            }
            sb.append(if (piece.color == PieceColor.WHITE) char.uppercaseChar() else char)
        }
    }

    sb.append(" ")
    sb.append(if (state.turn == PieceColor.WHITE) 'w' else 'b')
    sb.append(" ")

    val castling = StringBuilder()
    if (state.castling.whiteKingside) castling.append('K')
    if (state.castling.whiteQueenside) castling.append('Q')
    if (state.castling.blackKingside) castling.append('k')
    if (state.castling.blackQueenside) castling.append('q')
    sb.append(if (castling.isEmpty()) "-" else castling.toString())

    sb.append(" ")
    sb.append(state.enPassant?.let { squareToAlgebraic(it) } ?: "-")

    return sb.toString()
}

fun parseFEN(fen: String): GameState {
    val parts = fen.trim().split("\\s+".toRegex())
    val boardPart = parts[0]
    val turnPart = if (parts.size > 1 && parts[1] == "b") PieceColor.BLACK else PieceColor.WHITE
    val castlingPart = if (parts.size > 2) parts[2] else "-"
    val epPart = if (parts.size > 3) parts[3] else "-"
    val halfmove = if (parts.size > 4) parts[4].toIntOrNull() ?: 0 else 0
    val fullmove = if (parts.size > 5) parts[5].toIntOrNull() ?: 1 else 1

    val board = arrayOfNulls<Piece>(64)
    val rows = boardPart.split("/")

    for (r in 0 until 8) {
        val rank = 7 - r
        val rowStr = rows[r]
        var file = 0

        for (char in rowStr) {
            if (char in '1'..'8') {
                file += char.digitToInt()
            } else {
                val color = if (char.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
                val type = when (char.lowercaseChar()) {
                    'p' -> PieceType.PAWN
                    'n' -> PieceType.KNIGHT
                    'b' -> PieceType.BISHOP
                    'r' -> PieceType.ROOK
                    'q' -> PieceType.QUEEN
                    'k' -> PieceType.KING
                    else -> PieceType.PAWN
                }
                board[squareFromCoords(file, rank)] = Piece(type, color)
                file++
            }
        }
    }

    val castling = CastlingRights(
        whiteKingside = castlingPart.contains('K'),
        whiteQueenside = castlingPart.contains('Q'),
        blackKingside = castlingPart.contains('k'),
        blackQueenside = castlingPart.contains('q')
    )

    val enPassant = if (epPart != "-") algebraicToSquare(epPart) else null

    val state = GameState(
        board = board,
        turn = turnPart,
        castling = castling,
        enPassant = enPassant,
        halfmoveClock = halfmove,
        fullmoveNumber = fullmove
    )
    state.positionHistory.add(getPositionSignature(state))
    return state
}

fun toFEN(state: GameState): String {
    val sb = StringBuilder()
    for (r in 7 downTo 0) {
        var emptyCount = 0
        for (f in 0..7) {
            val piece = state.board[squareFromCoords(f, r)]
            if (piece == null) {
                emptyCount++
            } else {
                if (emptyCount > 0) {
                    sb.append(emptyCount)
                    emptyCount = 0
                }
                val char = when (piece.type) {
                    PieceType.PAWN -> 'p'
                    PieceType.KNIGHT -> 'n'
                    PieceType.BISHOP -> 'b'
                    PieceType.ROOK -> 'r'
                    PieceType.QUEEN -> 'q'
                    PieceType.KING -> 'k'
                }
                sb.append(if (piece.color == PieceColor.WHITE) char.uppercaseChar() else char)
            }
        }
        if (emptyCount > 0) {
            sb.append(emptyCount)
        }
        if (r > 0) {
            sb.append('/')
        }
    }

    sb.append(" ")
    sb.append(if (state.turn == PieceColor.WHITE) "w" else "b")
    sb.append(" ")

    val castling = StringBuilder()
    if (state.castling.whiteKingside) castling.append('K')
    if (state.castling.whiteQueenside) castling.append('Q')
    if (state.castling.blackKingside) castling.append('k')
    if (state.castling.blackQueenside) castling.append('q')
    sb.append(if (castling.isEmpty()) "-" else castling.toString())

    sb.append(" ")
    sb.append(state.enPassant?.let { squareToAlgebraic(it) } ?: "-")
    sb.append(" ")
    sb.append(state.halfmoveClock)
    sb.append(" ")
    sb.append(state.fullmoveNumber)

    return sb.toString()
}
