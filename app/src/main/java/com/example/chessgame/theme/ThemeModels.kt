package com.example.chessgame.theme

import androidx.compose.ui.graphics.Color
import com.example.chessgame.R
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType

/**
 * Modular Board Theme Configuration.
 * Fully decoupled from chess logic to allow unlimited board skins.
 */
data class BoardTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val boardDrawableRes: Int? = null,
    val lightSquare: Color,
    val darkSquare: Color,
    val tableTopBackground: List<Color>,
    val frameBorder: Color,
    val frameInnerBorder: Color,
    val coordinateColorLight: Color,
    val coordinateColorDark: Color,
    val selectedSquareColor: Color = Color(0x66D4A373),
    val legalMoveColor: Color = Color(0x99588157),
    val captureColor: Color = Color(0xAA9E2A2B),
    val lastMoveColor: Color = Color(0x40E0A96D),
    val scorepadPaper: Color = Color(0xFFF4EAD4),
    val scorepadInk: Color = Color(0xFF2B2118),
    val scorepadBorder: Color = Color(0xFFD6C7A1)
)

/**
 * Modular Piece Theme Configuration.
 * Fully decoupled from board themes so users can mix & match any piece set with any board.
 */
data class PieceTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val description: String,
    val isRaster: Boolean = false,
    // 12 Piece Resource IDs (Used when isRaster = true)
    val whiteKingRes: Int? = null,
    val whiteQueenRes: Int? = null,
    val whiteRookRes: Int? = null,
    val whiteBishopRes: Int? = null,
    val whiteKnightRes: Int? = null,
    val whitePawnRes: Int? = null,
    val blackKingRes: Int? = null,
    val blackQueenRes: Int? = null,
    val blackRookRes: Int? = null,
    val blackBishopRes: Int? = null,
    val blackKnightRes: Int? = null,
    val blackPawnRes: Int? = null
) {
    fun getPieceDrawable(type: PieceType, color: PieceColor): Int? {
        return if (color == PieceColor.WHITE) {
            when (type) {
                PieceType.KING -> whiteKingRes
                PieceType.QUEEN -> whiteQueenRes
                PieceType.ROOK -> whiteRookRes
                PieceType.BISHOP -> whiteBishopRes
                PieceType.KNIGHT -> whiteKnightRes
                PieceType.PAWN -> whitePawnRes
            }
        } else {
            when (type) {
                PieceType.KING -> blackKingRes
                PieceType.QUEEN -> blackQueenRes
                PieceType.ROOK -> blackRookRes
                PieceType.BISHOP -> blackBishopRes
                PieceType.KNIGHT -> blackKnightRes
                PieceType.PAWN -> blackPawnRes
            }
        }
    }
}

/**
 * Registry of all available Board and Piece themes.
 */
object ThemeRegistry {
    // ==========================================
    // 1. BOARD THEMES
    // ==========================================
    val ARTISAN_WOOD = BoardTheme(
        id = "artisan_wood",
        name = "Artisan Wood",
        subtitle = "Hand-drawn rustic oak & parchment",
        description = "Charming organic lines on warm study wood with hand-inked square borders.",
        boardDrawableRes = R.drawable.board_artisan,
        lightSquare = Color(0xFFF2E9D8),
        darkSquare = Color(0xFFA6774E),
        tableTopBackground = listOf(Color(0xFF2C1F16), Color(0xFF1E150F), Color(0xFF130D09)),
        frameBorder = Color(0xFF5C3D28),
        frameInnerBorder = Color(0xFF3B2516),
        coordinateColorLight = Color(0xFF7A583A),
        coordinateColorDark = Color(0xFFF7EFE4),
        selectedSquareColor = Color(0x66C98A4C),
        legalMoveColor = Color(0x995F7D4E),
        captureColor = Color(0xAA943D32)
    )

    val DARK_WALNUT = BoardTheme(
        id = "dark_walnut",
        name = "Dark Walnut",
        subtitle = "Rich carved walnut & maple inlay",
        description = "Deep roasted grain timber with delicate ivory inlays and brass corner accents.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFEADBCE),
        darkSquare = Color(0xFF744A32),
        tableTopBackground = listOf(Color(0xFF1D1612), Color(0xFF140E0A), Color(0xFF0C0806)),
        frameBorder = Color(0xFF4A2E1F),
        frameInnerBorder = Color(0xFF2E1C12),
        coordinateColorLight = Color(0xFF5E3C29),
        coordinateColorDark = Color(0xFFF0E4D8)
    )

    val FOREST_GROVE = BoardTheme(
        id = "forest_grove",
        name = "Forest Grove",
        subtitle = "Moss green cloth & aged parchment",
        description = "Classic European chess salon board with muted velvet green and warm parchment squares.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFEDE8D0),
        darkSquare = Color(0xFF5B7052),
        tableTopBackground = listOf(Color(0xFF172018), Color(0xFF101711), Color(0xFF0B0F0B)),
        frameBorder = Color(0xFF3B4835),
        frameInnerBorder = Color(0xFF273023),
        coordinateColorLight = Color(0xFF4E6046),
        coordinateColorDark = Color(0xFFF5F2E3),
        legalMoveColor = Color(0x994E8752)
    )

    val VINTAGE_PARCHMENT = BoardTheme(
        id = "vintage_parchment",
        name = "Vintage Parchment",
        subtitle = "Sepia manuscript & aged tea stain",
        description = "Inspired by 19th-century tournament manuscripts and hand-annotated opening manuals.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFF5EFE0),
        darkSquare = Color(0xFFC4A882),
        tableTopBackground = listOf(Color(0xFF261E16), Color(0xFF1A140E), Color(0xFF100C09)),
        frameBorder = Color(0xFF7A6145),
        frameInnerBorder = Color(0xFF52402D),
        coordinateColorLight = Color(0xFF8C6D4C),
        coordinateColorDark = Color(0xFFFAF6EE)
    )

    val MIDNIGHT_STUDY = BoardTheme(
        id = "midnight_study",
        name = "Midnight Study",
        subtitle = "Deep indigo study & ash birch",
        description = "A late night analysis session under a single warm reading lamp.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFD6DBE0),
        darkSquare = Color(0xFF475B6E),
        tableTopBackground = listOf(Color(0xFF131821), Color(0xFF0E1219), Color(0xFF080B0F)),
        frameBorder = Color(0xFF2B3A4A),
        frameInnerBorder = Color(0xFF1D2833),
        coordinateColorLight = Color(0xFF3B4E60),
        coordinateColorDark = Color(0xFFE8ECEF)
    )

    val ROYAL_MARBLE = BoardTheme(
        id = "royal_marble",
        name = "Royal Marble",
        subtitle = "Carrara stone & graphite marble",
        description = "Smooth polished Italian stone with subtle veining and brushed bronze frame.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFECEFF1),
        darkSquare = Color(0xFF546E7A),
        tableTopBackground = listOf(Color(0xFF1B1F23), Color(0xFF121518), Color(0xFF0B0D0F)),
        frameBorder = Color(0xFF37474F),
        frameInnerBorder = Color(0xFF263238),
        coordinateColorLight = Color(0xFF455A64),
        coordinateColorDark = Color(0xFFF3F5F6)
    )

    val OLD_LIBRARY = BoardTheme(
        id = "old_library",
        name = "Old Library",
        subtitle = "Rich leather, mahogany & gold leaf",
        description = "Surrounded by bookshelves, warm desk lighting, and historic chess treatises.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFF4EBD9),
        darkSquare = Color(0xFF8B5A2B),
        tableTopBackground = listOf(Color(0xFF23180F), Color(0xFF18100A), Color(0xFF0F0A06)),
        frameBorder = Color(0xFF5A391B),
        frameInnerBorder = Color(0xFF3B2512),
        coordinateColorLight = Color(0xFF6B4522),
        coordinateColorDark = Color(0xFFF9F3E8)
    )

    val OCEAN_BREEZE = BoardTheme(
        id = "ocean_breeze",
        name = "Ocean",
        subtitle = "Aquamarine glass & deep sea navy",
        description = "Refreshing nautical study theme inspired by coastal winds and sea glass.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFE0F2F1),
        darkSquare = Color(0xFF2E6F7E),
        tableTopBackground = listOf(Color(0xFF102027), Color(0xFF0B171C), Color(0xFF060D10)),
        frameBorder = Color(0xFF1D4E5B),
        frameInnerBorder = Color(0xFF13353E),
        coordinateColorLight = Color(0xFF265B68),
        coordinateColorDark = Color(0xFFF0FDFD),
        selectedSquareColor = Color(0x664DB6AC),
        legalMoveColor = Color(0x9926A69A)
    )

    val DESERT_DUNE = BoardTheme(
        id = "desert_dune",
        name = "Desert",
        subtitle = "Warm sandstone & sunlit terracotta",
        description = "Sun-drenched study overlooking desert sand dunes and clay pottery.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFFCEFD2),
        darkSquare = Color(0xFFB57048),
        tableTopBackground = listOf(Color(0xFF2C1C13), Color(0xFF1E130D), Color(0xFF130B07)),
        frameBorder = Color(0xFF7A482A),
        frameInnerBorder = Color(0xFF52301B),
        coordinateColorLight = Color(0xFF8C5332),
        coordinateColorDark = Color(0xFFFFF7E6)
    )

    val ROYAL_COURT = BoardTheme(
        id = "royal_court",
        name = "Royal",
        subtitle = "Imperial velvet burgundy & ivory",
        description = "Grandmaster court board fitted with rich burgundy enamel and gilded trims.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFFFF8E7),
        darkSquare = Color(0xFF7B1F34),
        tableTopBackground = listOf(Color(0xFF220C12), Color(0xFF17080C), Color(0xFF0E0407)),
        frameBorder = Color(0xFF571424),
        frameInnerBorder = Color(0xFF3B0C18),
        coordinateColorLight = Color(0xFF6E182D),
        coordinateColorDark = Color(0xFFFFFBF2),
        selectedSquareColor = Color(0x66E5A93C)
    )

    val JAPANESE_HINOKI = BoardTheme(
        id = "japanese_hinoki",
        name = "Japanese",
        subtitle = "Pale Hinoki cypress & sumi ink",
        description = "Zen minimalist aesthetic featuring pale cypress wood and dark charcoal calligraphy ink.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFEFE8D8),
        darkSquare = Color(0xFF383838),
        tableTopBackground = listOf(Color(0xFF1E1D1B), Color(0xFF141312), Color(0xFF0C0B0A)),
        frameBorder = Color(0xFF42403C),
        frameInnerBorder = Color(0xFF262523),
        coordinateColorLight = Color(0xFF4A4742),
        coordinateColorDark = Color(0xFFF7F2E6)
    )

    val CYBER_HORIZON = BoardTheme(
        id = "cyber_horizon",
        name = "Cyber",
        subtitle = "Matte carbon fiber & neon grid",
        description = "High-tech midnight tournament board with subtle cybernetic edge chamfers.",
        boardDrawableRes = null,
        lightSquare = Color(0xFF333E48),
        darkSquare = Color(0xFF181F26),
        tableTopBackground = listOf(Color(0xFF0D1117), Color(0xFF080B0F), Color(0xFF040608)),
        frameBorder = Color(0xFF242F3A),
        frameInnerBorder = Color(0xFF151C22),
        coordinateColorLight = Color(0xFF4A5C6D),
        coordinateColorDark = Color(0xFF70879D),
        selectedSquareColor = Color(0x6600E5FF),
        legalMoveColor = Color(0x9900E5FF)
    )

    val TOURNAMENT_CLASSIC = BoardTheme(
        id = "tournament_classic",
        name = "Tournament",
        subtitle = "FIDE championship vinyl & buff",
        description = "The gold standard international championship board trusted by grandmasters worldwide.",
        boardDrawableRes = null,
        lightSquare = Color(0xFFECE6D2),
        darkSquare = Color(0xFF4E7852),
        tableTopBackground = listOf(Color(0xFF1A1A18), Color(0xFF121210), Color(0xFF0A0A08)),
        frameBorder = Color(0xFF365339),
        frameInnerBorder = Color(0xFF253927),
        coordinateColorLight = Color(0xFF416545),
        coordinateColorDark = Color(0xFFF5F0E1)
    )

    val ALL_BOARD_THEMES = listOf(
        ARTISAN_WOOD,
        DARK_WALNUT,
        FOREST_GROVE,
        MIDNIGHT_STUDY,
        OLD_LIBRARY,
        ROYAL_MARBLE,
        OCEAN_BREEZE,
        DESERT_DUNE,
        ROYAL_COURT,
        JAPANESE_HINOKI,
        VINTAGE_PARCHMENT,
        CYBER_HORIZON,
        TOURNAMENT_CLASSIC
    )

    // ==========================================
    // 2. PIECE THEMES
    // ==========================================
    val STORYBOOK_HANDCRAFTED = PieceTheme(
        id = "storybook_handcrafted",
        name = "Classic",
        subtitle = "Hand-drawn character figurines",
        description = "Warm hand-drawn characters from the master chess pack with illustrated attire, flags, and scarves.",
        isRaster = true,
        whiteKingRes = R.drawable.piece_king_white,
        whiteQueenRes = R.drawable.piece_queen_white,
        whiteRookRes = R.drawable.piece_rook_white,
        whiteBishopRes = R.drawable.piece_bishop_white,
        whiteKnightRes = R.drawable.piece_knight_white,
        whitePawnRes = R.drawable.piece_pawn_white,
        blackKingRes = R.drawable.piece_king_black,
        blackQueenRes = R.drawable.piece_queen_black,
        blackRookRes = R.drawable.piece_rook_black,
        blackBishopRes = R.drawable.piece_bishop_black,
        blackKnightRes = R.drawable.piece_knight_black,
        blackPawnRes = R.drawable.piece_pawn_black
    )

    val STAUNTON_TOURNAMENT = PieceTheme(
        id = "staunton_tournament",
        name = "Tournament",
        subtitle = "Crisp weighted championship Staunton",
        description = "Classic championship Staunton silhouette with specular highlights and rim lighting.",
        isRaster = false
    )

    val CARVED_WOOD = PieceTheme(
        id = "carved_wood",
        name = "Wood",
        subtitle = "Lathe-turned boxwood & ebony",
        description = "Warm organic woodgrain styling with chiselled bevels and satin finish.",
        isRaster = false
    )

    val MARBLE_STONE = PieceTheme(
        id = "marble",
        name = "Marble",
        subtitle = "Carrara stone & polished obsidian",
        description = "Heavy sculpted marble with light stone veins and glossy lacquer sheen.",
        isRaster = false
    )

    val CARVED_STONE = PieceTheme(
        id = "stone",
        name = "Stone",
        subtitle = "Ancient chiselled granite & basalt",
        description = "Rough-hewn monolithic pieces chiselled from ancient volcanic stone.",
        isRaster = false
    )

    val BRUSHED_BRASS = PieceTheme(
        id = "brass",
        name = "Brass",
        subtitle = "Polished brass & blackened cast iron",
        description = "Solid metallic pieces with warm golden lathe highlights and industrial weight.",
        isRaster = false
    )

    val POLISHED_CHROME = PieceTheme(
        id = "chrome",
        name = "Chrome",
        subtitle = "Reflective mirrored steel & nickel",
        description = "Precision-machined mirrored chrome reflecting studio lighting.",
        isRaster = false
    )

    val FROSTED_GLASS = PieceTheme(
        id = "glass",
        name = "Glass",
        subtitle = "Frosted sea crystal & smoky quartz",
        description = "Translucent refractive glass with luminous edge refractions.",
        isRaster = false
    )

    val PARCHMENT_INK = PieceTheme(
        id = "paper",
        name = "Paper",
        subtitle = "Vintage calligraphy & ink hatching",
        description = "Classical hand-etched ink drawings with cross-hatched shading.",
        isRaster = false
    )

    val ROYAL_HEIRLOOM = PieceTheme(
        id = "royal",
        name = "Royal",
        subtitle = "Ornate crowns & gilded heraldry",
        description = "Heirloom royal court figures adorned with jewels and golden crests.",
        isRaster = false
    )

    val FANTASY_REALM = PieceTheme(
        id = "fantasy",
        name = "Fantasy",
        subtitle = "Elven silver & runic dwarf stone",
        description = "Mythical chess set with flowing elven blades and runic carved bases.",
        isRaster = false
    )

    val MINIMAL_SLATE = PieceTheme(
        id = "minimal_slate",
        name = "Minimal",
        subtitle = "Bauhaus geometric architecture",
        description = "Sleek contemporary design with refined minimalist geometric balance.",
        isRaster = false
    )

    val CYBER_NEON = PieceTheme(
        id = "cyber",
        name = "Cyber",
        subtitle = "Holographic angular neon vectors",
        description = "Sci-fi sharp vectors with glowing cyan and amber edge circuits.",
        isRaster = false
    )

    val ALL_PIECE_THEMES = listOf(
        STORYBOOK_HANDCRAFTED,
        STAUNTON_TOURNAMENT,
        CARVED_WOOD,
        MARBLE_STONE,
        CARVED_STONE,
        BRUSHED_BRASS,
        POLISHED_CHROME,
        FROSTED_GLASS,
        PARCHMENT_INK,
        ROYAL_HEIRLOOM,
        FANTASY_REALM,
        MINIMAL_SLATE,
        CYBER_NEON
    )

    fun getBoardTheme(id: String): BoardTheme {
        return ALL_BOARD_THEMES.find { it.id == id } ?: ARTISAN_WOOD
    }

    fun findBoardTheme(id: String): BoardTheme? {
        return ALL_BOARD_THEMES.find { it.id == id }
    }

    fun getPieceTheme(id: String): PieceTheme {
        return ALL_PIECE_THEMES.find { it.id == id } ?: STORYBOOK_HANDCRAFTED
    }

    fun findPieceTheme(id: String): PieceTheme? {
        return ALL_PIECE_THEMES.find { it.id == id }
    }
}
