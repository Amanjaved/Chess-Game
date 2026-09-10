package com.example.chessgame

import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.ai.GameAnalyzer
import com.example.chessgame.ai.MoveClassification
import com.example.chessgame.ai.calculateAIMoveDirect
import com.example.chessgame.engine.*
import org.junit.Assert.*
import org.junit.Test

class EngineTest {

    @Test
    fun testInitialSetup() {
        val state = createInitialGameState()
        assertEquals(PieceColor.WHITE, state.turn)
        assertEquals(32, state.board.count { it != null })
        assertTrue(state.castling.whiteKingside)
        assertTrue(state.castling.whiteQueenside)
        assertTrue(state.castling.blackKingside)
        assertTrue(state.castling.blackQueenside)
    }

    @Test
    fun testCoordinates() {
        assertEquals(0, algebraicToSquare("a1"))
        assertEquals(7, algebraicToSquare("h1"))
        assertEquals(28, algebraicToSquare("e4"))
        assertEquals(60, algebraicToSquare("e8"))
        assertEquals(63, algebraicToSquare("h8"))

        assertEquals("a1", squareToAlgebraic(0))
        assertEquals("e4", squareToAlgebraic(28))
        assertEquals("h8", squareToAlgebraic(63))
    }

    @Test
    fun testFENParserAndSerializer() {
        val state = createInitialGameState()
        val fen = toFEN(state)
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fen)

        val parsed = parseFEN(fen)
        assertEquals(PieceColor.WHITE, parsed.turn)
        assertTrue(parsed.castling.whiteKingside)
        assertTrue(parsed.castling.blackQueenside)
    }

    @Test
    fun testOpeningMoves() {
        val state = createInitialGameState()
        val moves = getLegalMoves(state)
        // 16 pawn moves + 4 knight moves = 20
        assertEquals(20, moves.size)
    }

    @Test
    fun testEnPassant() {
        val fen = "rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 3"
        val state = parseFEN(fen)
        val e5 = algebraicToSquare("e5")
        val d6 = algebraicToSquare("d6")

        val epMove = getLegalMoves(state).find { it.from == e5 && it.to == d6 && it.isEnPassant }
        assertNotNull(epMove)

        val after = makeMove(state, epMove!!)
        assertNull(after.board[algebraicToSquare("d5")]) // Captured black pawn removed
        assertEquals(PieceType.PAWN, after.board[d6]?.type)
        assertEquals(PieceColor.WHITE, after.board[d6]?.color)
    }

    @Test
    fun testPromotion() {
        val fen = "8/P7/8/8/8/8/8/4K2k w - - 0 1"
        val state = parseFEN(fen)
        val a7 = algebraicToSquare("a7")
        val a8 = algebraicToSquare("a8")

        val promos = getLegalMoves(state).filter { it.from == a7 && it.to == a8 }
        assertEquals(4, promos.size)

        val queenPromo = promos.find { it.promotion == PieceType.QUEEN }!!
        val after = makeMove(state, queenPromo)
        assertEquals(PieceType.QUEEN, after.board[a8]?.type)
        assertEquals(PieceColor.WHITE, after.board[a8]?.color)
    }

    @Test
    fun testPinnedPieceCannotMove() {
        // King on e1, Bishop on e2, enemy Rook on e8
        val fen = "4r3/8/8/8/8/8/4B3/4K3 w - - 0 1"
        val state = parseFEN(fen)
        val e2 = algebraicToSquare("e2")

        val bishopMoves = getLegalMoves(state).filter { it.from == e2 }
        assertTrue(bishopMoves.isEmpty())
    }

    @Test
    fun testCastling() {
        val fen = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1"
        val state = parseFEN(fen)
        val e1 = algebraicToSquare("e1")

        val castleMoves = getLegalMoves(state).filter { it.from == e1 && it.isCastling != null }
        assertEquals(2, castleMoves.size)

        val ks = castleMoves.find { it.isCastling == CastlingType.KINGSIDE }!!
        val after = makeMove(state, ks)
        assertEquals(PieceType.KING, after.board[algebraicToSquare("g1")]?.type)
        assertEquals(PieceType.ROOK, after.board[algebraicToSquare("f1")]?.type)
        assertNull(after.board[algebraicToSquare("h1")])
    }

    @Test
    fun testCastlingThroughCheckBlocked() {
        // Black rook on f8 attacks f1
        val fen = "4rk1r/8/8/8/8/8/8/R3K2R w KQ - 0 1"
        val state = parseFEN(fen)
        val e1 = algebraicToSquare("e1")

        val ks = getLegalMoves(state).find { it.from == e1 && it.isCastling == CastlingType.KINGSIDE }
        assertNull(ks)
    }

    @Test
    fun testFoolsMate() {
        var state = createInitialGameState()
        // 1. f3 e5 2. g4 Qh4#
        val f3 = getLegalMoves(state).find { it.from == algebraicToSquare("f2") && it.to == algebraicToSquare("f3") }!!
        state = makeMove(state, f3)
        val e5 = getLegalMoves(state).find { it.from == algebraicToSquare("e7") && it.to == algebraicToSquare("e5") }!!
        state = makeMove(state, e5)
        val g4 = getLegalMoves(state).find { it.from == algebraicToSquare("g2") && it.to == algebraicToSquare("g4") }!!
        state = makeMove(state, g4)
        val qh4 = getLegalMoves(state).find { it.from == algebraicToSquare("d8") && it.to == algebraicToSquare("h4") }!!
        state = makeMove(state, qh4)

        assertTrue(isInCheck(state.board, PieceColor.WHITE))
        assertTrue(isCheckmate(state))
        val outcome = getGameOutcome(state)
        assertTrue(outcome.isOver)
        assertEquals(PieceColor.BLACK, outcome.winner)
        assertEquals(OutcomeReason.CHECKMATE, outcome.reason)
    }

    @Test
    fun testStalemate() {
        val fen = "k7/2K5/1Q6/8/8/8/8/8 b - - 0 1"
        val state = parseFEN(fen)
        assertFalse(isInCheck(state.board, PieceColor.BLACK))
        assertTrue(isStalemate(state))
        val outcome = getGameOutcome(state)
        assertTrue(outcome.isOver)
        assertTrue(outcome.isDraw)
        assertEquals(OutcomeReason.STALEMATE, outcome.reason)
    }

    @Test
    fun testInsufficientMaterial() {
        val kvk = parseFEN("8/8/8/4k3/8/8/4K3/8 w - - 0 1")
        assertTrue(isInsufficientMaterial(kvk))

        val kbvk = parseFEN("8/8/8/4k3/8/8/4KB2/8 w - - 0 1")
        assertTrue(isInsufficientMaterial(kbvk))

        val kpvk = parseFEN("8/8/8/4k3/8/8/4KP2/8 w - - 0 1")
        assertFalse(isInsufficientMaterial(kpvk))
    }

    @Test
    fun testAISelection() {
        val state = createInitialGameState()
        for (diff in AIDifficulty.values()) {
            val move = calculateAIMoveDirect(state, diff)
            assertNotNull(move)
            val legal = getLegalMoves(state)
            assertTrue(legal.any { it.from == move!!.from && it.to == move.to })
        }
    }

    @Test
    fun testAICapturesHangingQueen() {
        val fen = "rnb1kbnr/ppp1pppp/3p4/4Q3/8/8/PPPPPPPP/RNB1KBNR b KQkq - 0 1"
        val state = parseFEN(fen)
        val move = calculateAIMoveDirect(state, AIDifficulty.HARD)
        assertNotNull(move)
        assertEquals(PieceType.QUEEN, move!!.captured)
        assertEquals(algebraicToSquare("e5"), move.to)
    }

    @Test
    fun testAIFindsMateIn1() {
        val fen = "r1bqkb1r/pppp1ppp/2n5/4p2Q/2B1n3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4"
        val state = parseFEN(fen)
        val move = calculateAIMoveDirect(state, AIDifficulty.HARD)
        assertNotNull(move)
        assertEquals(algebraicToSquare("f7"), move!!.to) // Qxf7# is Scholar's Mate
    }

    @Test
    fun testGameAnalysisDetectsMistakeAndSuggestsBestMove() = kotlinx.coroutines.runBlocking {
        // e4, e5, Ke2 (Bongcloud - classic king walk mistake!)
        val e4 = Move(algebraicToSquare("e2"), algebraicToSquare("e4"), PieceType.PAWN, PieceColor.WHITE, san = "e4")
        val e5 = Move(algebraicToSquare("e7"), algebraicToSquare("e5"), PieceType.PAWN, PieceColor.BLACK, san = "e5")
        val ke2 = Move(algebraicToSquare("e1"), algebraicToSquare("e2"), PieceType.KING, PieceColor.WHITE, san = "Ke2")

        val report = GameAnalyzer.analyzeGameAsync(listOf(e4, e5, ke2)) { }
        assertEquals(3, report.moves.size)

        val analyzedKe2 = report.moves[2]
        // Ke2 must NOT be classified as BEST MOVE
        assertNotEquals(MoveClassification.BEST, analyzedKe2.classification)
        assertTrue(analyzedKe2.classification == MoveClassification.MISTAKE || 
                   analyzedKe2.classification == MoveClassification.INACCURACY ||
                   analyzedKe2.classification == MoveClassification.BLUNDER)

        // Best engine move must be provided
        assertNotNull(analyzedKe2.bestEngineMove)
        assertTrue(analyzedKe2.bestEngineSan.isNotEmpty())

        // Explanation must mention the mistake/inaccuracy and name the best move
        assertTrue(analyzedKe2.explanation.contains(analyzedKe2.bestEngineSan))
    }
}
