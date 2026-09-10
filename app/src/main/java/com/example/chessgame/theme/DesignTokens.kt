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
    // 1. MATERIAL PALETTE (WOOD, PARCHMENT, BRASS)
    // ==========================================
    val DeskEspressoDark = Color(0xFF0D0907)
    val DeskTimberDark = Color(0xFF19120D)
    val DeskMahoganyMid = Color(0xFF261B14)
    val DeskWalnutFrame = Color(0xFF38271D)

    val ParchmentCream = Color(0xFFF7F1E5)
    val ParchmentCard = Color(0xFFEFE6D5)
    val ParchmentFaded = Color(0xFFDACFBD)
    val InkFountainPen = Color(0xFF1E1712)
    val InkSecondary = Color(0xFF5A4D42)
    val InkFaded = Color(0xFF8C7D70)

    val BrassAccent = Color(0xFFE5A93C)
    val AmberGlow = Color(0xFFD49B55)
    val SoftGold = Color(0xFFEBD18C)

    val TableVignette = listOf(
        Color(0xFF2C1F17),
        Color(0xFF1B130E),
        Color(0xFF0F0B08),
        Color(0xFF080504)
    )

    // Move Quality Color Tokens
    val QualityBrilliant = Color(0xFF26C6DA) // Cyan / Diamond glow
    val QualityBest = Color(0xFF588157)      // Deep sage emerald
    val QualityGood = Color(0xFF8DA378)      // Subtle olive
    val QualityBook = Color(0xFF7E8B9B)      // Parchment slate blue
    val QualityInaccuracy = Color(0xFFE5A93C) // Warm amber
    val QualityMistake = Color(0xFFD06A3B)   // Terracotta orange
    val QualityBlunder = Color(0xFFC93B2B)   // Wax seal crimson

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
