package com.example.chessgame.ai

import com.example.chessgame.engine.*
import org.junit.Assert.*
import org.junit.Test

class MoveSuggestionEngineTest {

    @Test
    fun testOpeningPositionSuggestions() {
        val state = createInitialGameState()
        val suggestions = MoveSuggestionEngine.generateSuggestions(state, depth = 2)

        // Must return exactly 3 top suggestions
        assertEquals(3, suggestions.size)

        // Ranks must be 1, 2, 3
        assertEquals(1, suggestions[0].rank)
        assertEquals(2, suggestions[1].rank)
        assertEquals(3, suggestions[2].rank)

        // Classifications must be BEST, STRONG, GOOD
        assertEquals(MoveClassification.BEST, suggestions[0].classification)
        assertEquals(MoveClassification.STRONG, suggestions[1].classification)
        assertEquals(MoveClassification.GOOD, suggestions[2].classification)

        // All suggestions must be strictly legal
        val legalMoves = getLegalMoves(state)
        for (s in suggestions) {
            assertTrue("Suggested move must be legal: ${s.notation}", legalMoves.any { it.from == s.from && it.to == s.to })
            assertNotNull(s.explanation)
            assertTrue("Explanation should not be empty", s.explanation.isNotEmpty())
            assertTrue("Notation should contain arrow", s.notation.contains("→"))
        }

        // Suggestions should be ordered descending by evaluation
        assertTrue("Top move score >= second move score", suggestions[0].evaluation >= suggestions[1].evaluation)
        assertTrue("Second move score >= third move score", suggestions[1].evaluation >= suggestions[2].evaluation)
    }

    @Test
    fun testCheckPositionSuggestions() {
        // Black queen checks White king: White king must move or block or capture
        // White king on e1 (sq 4), Black Queen on e4 (sq 28)
        val fen = "rnb1kbnr/pppp1ppp/8/8/4q3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1"
        val state = parseFEN(fen)

        val legalMoves = getLegalMoves(state)
        assertTrue("White must have legal responses to check", legalMoves.isNotEmpty())

        val suggestions = MoveSuggestionEngine.generateSuggestions(state, depth = 2)
        assertTrue("Should return suggestions in check position", suggestions.isNotEmpty())
        assertTrue("Max 3 suggestions", suggestions.size <= 3)

        for (s in suggestions) {
            // Must be one of the legal check-resolving moves
            assertTrue(legalMoves.any { it.from == s.from && it.to == s.to })
            val nextState = makeMove(state, s.move)
            assertFalse("King must no longer be in check after suggestion", isInCheck(nextState.board, state.turn))
        }
    }

    @Test
    fun testMateInOnePrioritizedAsBest() {
        // Scholar's mate position: White Qh5, Bc4, Black king on e8, f7 weak
        // White to move: Qxf7# delivers checkmate
        val fen = "r1bqkb1r/pppp1ppp/2n5/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4"
        val state = parseFEN(fen)

        val suggestions = MoveSuggestionEngine.generateSuggestions(state, depth = 2)
        assertFalse("Suggestions should not be empty", suggestions.isEmpty())

        val top = suggestions[0]
        assertEquals(MoveClassification.BEST, top.classification)
        // Qxf7# is from h5 (sq 39) to f7 (sq 53)
        assertEquals(39, top.from)
        assertEquals(53, top.to)
        assertTrue("Explanation should mention checkmate", top.explanation.contains("checkmate", ignoreCase = true))
    }

    @Test
    fun testFewLegalMovesReturnsAvailableCount() {
        // Position where moving player only has 1 or 2 legal moves
        val fen = "k7/8/1K6/8/8/8/8/7R b - - 0 1" // Black king on a8, White Ka6, Rh1. Black can only play Kb8
        val state = parseFEN(fen)
        val legalMoves = getLegalMoves(state)
        assertEquals(1, legalMoves.size)

        val suggestions = MoveSuggestionEngine.generateSuggestions(state, depth = 2)
        assertEquals(1, suggestions.size)
        assertEquals(MoveClassification.BEST, suggestions[0].classification)
        assertEquals(1, suggestions[0].rank)
    }

    @Test
    fun testCaptureExplanation() {
        // White knight on f3 can capture black pawn on e5
        val fen = "rnbqkbnr/pppp1ppp/8/4p3/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 0 2"
        val state = parseFEN(fen)

        val captureMove = Move(
            from = algebraicToSquare("f3"),
            to = algebraicToSquare("e5"),
            piece = PieceType.KNIGHT,
            color = PieceColor.WHITE,
            captured = PieceType.PAWN
        )

        val explanation = MoveSuggestionEngine.generateExplanation(state, captureMove, MoveClassification.BEST)
        assertTrue("Explanation should mention capture", explanation.contains("capture", ignoreCase = true))
    }

    @Test
    fun testCastlingExplanation() {
        // White ready to castle kingside
        val fen = "r1bqk2r/pppp1ppp/2n2n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4"
        val state = parseFEN(fen)

        val castleMove = Move(
            from = algebraicToSquare("e1"),
            to = algebraicToSquare("g1"),
            piece = PieceType.KING,
            color = PieceColor.WHITE,
            isCastling = CastlingType.KINGSIDE
        )

        val explanation = MoveSuggestionEngine.generateExplanation(state, castleMove, MoveClassification.BEST)
        assertTrue("Explanation should mention king safety or rooks", explanation.contains("king safety", ignoreCase = true) || explanation.contains("rooks", ignoreCase = true))
    }
}
