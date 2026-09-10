package com.example.chessgame.progression

import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.algebraicToSquare

data class ChessPuzzle(
    val id: String,
    val title: String,
    val theme: String,
    val description: String,
    val fen: String,
    val playerColor: PieceColor,
    val fromSquareAlg: String,
    val toSquareAlg: String,
    val hint: String,
    val explanation: String
) {
    val fromSquare: Int get() = algebraicToSquare(fromSquareAlg)
    val toSquare: Int get() = algebraicToSquare(toSquareAlg)
}

object DailyPuzzleRepository {
    val PUZZLES = listOf(
        ChessPuzzle(
            id = "puzzle_back_rank",
            title = "Back Rank Execution",
            theme = "Checkmate in 1",
            description = "Black's King is trapped behind a wall of pawns. Deliver the decisive blow.",
            fen = "6k1/5ppp/8/8/8/8/8/4R1K1 w - - 0 1",
            playerColor = PieceColor.WHITE,
            fromSquareAlg = "e1",
            toSquareAlg = "e8",
            hint = "Invade the 8th rank with your rook.",
            explanation = "Re8# is checkmate! Black's king cannot escape because his own pawns block f7, g7, and h7."
        ),
        ChessPuzzle(
            id = "puzzle_scholars_strike",
            title = "F7 Weakness",
            theme = "Checkmate in 1",
            description = "Black's f7 square is defended only by the King. Strike with maximum force.",
            fen = "r1bqkb1r/pppp1ppp/2n5/4p3/2B1n3/5Q2/PPPP1PPP/RNB1K1NR w KQkq - 0 1",
            playerColor = PieceColor.WHITE,
            fromSquareAlg = "f3",
            toSquareAlg = "f7",
            hint = "Coordinate your Queen and Bishop against f7.",
            explanation = "Qxf7# delivers checkmate! Supported by the Bishop on c4, the Queen cannot be captured by the King."
        ),
        ChessPuzzle(
            id = "puzzle_knight_fork",
            title = "Royal Fork",
            theme = "Tactical Fork",
            description = "White has an opportunity to fork the Black King and Queen.",
            fen = "r3k2r/ppp2ppp/8/8/3n4/5N2/PPP2PPP/R1B1K2R b KQkq - 0 1",
            playerColor = PieceColor.BLACK,
            fromSquareAlg = "d4",
            toSquareAlg = "c2",
            hint = "Jump to c2 to attack both king and rook.",
            explanation = "Nxc2+ forks White's king and rook, winning decisive material."
        ),
        ChessPuzzle(
            id = "puzzle_smothered_door",
            title = "Corridor Checkmate",
            theme = "Heavy Piece Mate",
            description = "White has double rooks lined up against the black king.",
            fen = "3r2k1/5ppp/8/8/8/8/1R6/1R4K1 w - - 0 1",
            playerColor = PieceColor.WHITE,
            fromSquareAlg = "b2",
            toSquareAlg = "b8",
            hint = "Force the exchange on the back rank.",
            explanation = "Rb8! Black is forced into 1... Rxb8 2. Rxb8# with unstoppable back-rank mate."
        )
    )

    fun getTodayPuzzle(): ChessPuzzle {
        // Deterministic daily rotation
        val dayIndex = (System.currentTimeMillis() / (1000 * 60 * 60 * 24)).toInt()
        return PUZZLES[Math.floorMod(dayIndex, PUZZLES.size)]
    }
}
