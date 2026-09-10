package com.example.chessgame.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design Tokens for "The Chess Desk" Aesthetic System.
 * Encapsulates unified spacing, corner radii, elevation, animation timing,
 * and harmonious tactile material palettes.
 */
object ChessDeskTokens {

    // ==========================================
    // 1. MATERIAL PALETTE (OBSIDIAN, GOLD, NEON)
    // ==========================================
    val DeskEspressoDark = Color(0xFF070A10)
    val DeskTimberDark = Color(0xFF0F1726)
    val DeskMahoganyMid = Color(0xFF1B273C)
    val DeskWalnutFrame = Color(0xFF2B3A54)

    val ParchmentCream = Color(0xFFF8FAFC)
    val ParchmentCard = Color(0xFF162032)
    val ParchmentFaded = Color(0xFF94A3B8)
    val InkFountainPen = Color(0xFFFFFFFF)
    val InkSecondary = Color(0xFF94A3B8)
    val InkFaded = Color(0xFF64748B)

    val BrassAccent = Color(0xFFFFB703)
    val AmberGlow = Color(0xFFFFC53D)
    val SoftGold = Color(0xFFFFE082)

    val TableVignette = listOf(
        Color(0xFF1B273C),
        Color(0xFF111827),
        Color(0xFF0B0F18),
        Color(0xFF06090F)
    )

    // Move Quality Color Tokens
    val QualityBrilliant = Color(0xFF00E5FF) // Neon Cyan glow
    val QualityBest = Color(0xFF10B981)      // Vivid Emerald
    val QualityGood = Color(0xFF34D399)      // Bright mint
    val QualityBook = Color(0xFF38BDF8)      // Electric blue
    val QualityInaccuracy = Color(0xFFFFB703) // Warm amber
    val QualityMistake = Color(0xFFFB923C)   // Bright orange
    val QualityBlunder = Color(0xFFEF4444)   // Crimson red

    // ==========================================
    // 2. SPACING SYSTEM
    // ==========================================
    val spaceXs = 4.dp
    val spaceSm = 8.dp
    val spaceMd = 12.dp
    val spaceLg = 16.dp
    val spaceXl = 20.dp
    val spaceXxl = 24.dp
    val spaceHero = 32.dp

    // ==========================================
    // 3. CORNER RADII
    // ==========================================
    val radiusSm = 6.dp
    val radiusMd = 12.dp
    val radiusLg = 16.dp
    val radiusXl = 22.dp
    val radiusRound = 999.dp

    // ==========================================
    // 4. ELEVATION SHADOWS
    // ==========================================
    val elevationFlat = 0.dp
    val elevationSubtle = 2.dp
    val elevationCard = 6.dp
    val elevationLifted = 10.dp
    val elevationBoard = 20.dp
    val elevationModal = 28.dp

    // ==========================================
    // 5. ANIMATION DURATIONS (MILLISECONDS)
    // ==========================================
    const val animInstantMs = 120
    const val animSnappyMs = 200
    const val animPieceGlideMs = 280
    const val animSceneRevealMs = 360

    // Common Brushes
    val StudyCardBackground = Brush.verticalGradient(
        listOf(DeskMahoganyMid, DeskTimberDark)
    )
    val ScorecardBackground = Brush.verticalGradient(
        listOf(ParchmentCream, ParchmentCard)
    )
    val BrassBorderGradient = Brush.linearGradient(
        listOf(BrassAccent, Color(0x60E5A93C), BrassAccent)
    )
}
