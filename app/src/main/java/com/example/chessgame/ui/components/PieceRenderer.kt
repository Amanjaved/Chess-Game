package com.example.chessgame.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.ThemeRegistry

/**
 * Universal Chess Piece Renderer.
 * Supports both high-resolution hand-crafted raster piece sets (like Storybook Handcrafted)
 * and scalable vector piece sets (Tournament Staunton, Carved Wood, Minimal Slate).
 */
@Composable
fun PieceView(
    type: PieceType,
    color: PieceColor,
    modifier: Modifier = Modifier,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        val rasterRes = if (pieceTheme.isRaster) pieceTheme.getPieceDrawable(type, color) else null

        if (rasterRes != null) {
            // Hand-drawn / storybook piece with subtle soft contact shadow
            Image(
                painter = painterResource(id = rasterRes),
                contentDescription = "${color.name} ${type.name}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Soft natural contact shadow on the ground
                        drawOval(
                            color = Color(0x33100B06),
                            topLeft = Offset(size.width * 0.18f, size.height * 0.82f),
                            size = androidx.compose.ui.geometry.Size(size.width * 0.64f, size.height * 0.16f)
                        )
                    }
            )
        } else {
            // Vector pieces (Staunton / Carved Wood / Minimal / Brass / Marble / Glass / etc.)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val isWhite = color == PieceColor.WHITE

                when (pieceTheme.id) {
                    "carved_wood" -> drawCarvedWoodPiece(type, isWhite)
                    "minimal_slate" -> drawMinimalSlatePiece(type, isWhite)
                    else -> drawMaterialPiece(pieceTheme.id, type, isWhite)
                }
            }
        }
    }
}

// ==========================================
// 1. MATERIAL & STAUNTON VECTOR PIECES
// ==========================================
private fun DrawScope.drawMaterialPiece(themeId: String, type: PieceType, isWhite: Boolean) {
    val strokeColor = when (themeId) {
        "brass" -> if (isWhite) Color(0xFF6D4C00) else Color(0xFF14191C)
        "chrome" -> if (isWhite) Color(0xFF37474F) else Color(0xFF1A2327)
        "glass" -> if (isWhite) Color(0xFF00695C) else Color(0xFF102027)
        "cyber" -> if (isWhite) Color(0xFF004D40) else Color(0xFF3E2723)
        "marble" -> if (isWhite) Color(0xFF455A64) else Color(0xFF10171B)
        "stone" -> if (isWhite) Color(0xFF424242) else Color(0xFF121212)
        "royal" -> if (isWhite) Color(0xFF5D4037) else Color(0xFF1B0007)
        "paper" -> if (isWhite) Color(0xFF4E342E) else Color(0xFF1C130E)
        else -> if (isWhite) Color(0xFF2C2219) else Color(0xFF0F0B08)
    }

    val strokeWidth = size.width * 0.032f

    val rimColor = when (themeId) {
        "brass" -> if (isWhite) Color(0xFFFFF9C4) else Color(0xFFFFD54F)
        "chrome" -> if (isWhite) Color(0xFFFFFFFF) else Color(0xFFCFD8DC)
        "glass" -> if (isWhite) Color(0xFFE0F7FA) else Color(0xFF80DEEA)
        "cyber" -> if (isWhite) Color(0xFFE0F7FA) else Color(0xFFFFE0B2)
        "marble" -> if (isWhite) Color(0xFFFFFFFF) else Color(0xFFB0BEC5)
        "stone" -> if (isWhite) Color(0xFFF5F5F5) else Color(0xFF9E9E9E)
        "royal" -> if (isWhite) Color(0xFFFFF8E1) else Color(0xFFFFD54F)
        "paper" -> if (isWhite) Color(0xFFFAF6EE) else Color(0xFFD7CCC8)
        else -> if (isWhite) Color(0xFFFFFFFF) else Color(0xFFC4AB94)
    }

    val accentColor = when (themeId) {
        "brass" -> if (isWhite) Color(0xFFFFB300) else Color(0xFFFFA000)
        "cyber" -> if (isWhite) Color(0xFF00E5FF) else Color(0xFFFF6D00)
        "royal" -> if (isWhite) Color(0xFFD4AF37) else Color(0xFF880E4F)
        else -> if (isWhite) Color(0xFFD4A373) else Color(0xFFEADBCE)
    }

    val bodyBrush = when (themeId) {
        "brass" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFFFE082), Color(0xFFFFCA28), Color(0xFFFFA000)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF546E7A), Color(0xFF37474F), Color(0xFF263238), Color(0xFF14191C)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "marble" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFFB0BEC5)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF455A64), Color(0xFF263238), Color(0xFF1A2126), Color(0xFF0D1114)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "stone" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFEEEEEE), Color(0xFFE0E0E0), Color(0xFFBDBDBD), Color(0xFF9E9E9E)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF424242), Color(0xFF303030), Color(0xFF212121), Color(0xFF121212)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "chrome" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFECEFF1), Color(0xFF90A4AE), Color(0xFFFFFFFF), Color(0xFF78909C)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF607D8B), Color(0xFF37474F), Color(0xFF212121), Color(0xFF455A64)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "glass" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xEEFFFFFF), Color(0xCCE0F7FA), Color(0x99B2EBF2), Color(0x664DD0E1)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xEE455A64), Color(0xCC263238), Color(0x99102027), Color(0x77004D40)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "cyber" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF84FFFF), Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFF01579B)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD180), Color(0xFFFF9100), Color(0xFFFF3D00), Color(0xFFBF360C)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        "royal" -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFF8E1), Color(0xFFF7EBD2), Color(0xFFD4AF37)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF5B1228), Color(0xFF3F0B1A), Color(0xFF280610), Color(0xFF160309)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
        else -> {
            if (isWhite) {
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF9F5EC), Color(0xFFEADBCE), Color(0xFFD4C2B0)),
                    start = Offset(size.width * 0.2f, size.height * 0.1f),
                    end = Offset(size.width * 0.9f, size.height * 0.95f)
                )
            } else {
                Brush.linearGradient(
                    colors = listOf(Color(0xFF5A4436), Color(0xFF3D2C22), Color(0xFF271B14), Color(0xFF160E0A)),
                    start = Offset(size.width * 0.15f, size.height * 0.1f),
                    end = Offset(size.width * 0.85f, size.height * 0.95f)
                )
            }
        }
    }

    when (type) {
        PieceType.PAWN -> drawLuxuryPawn(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.KNIGHT -> drawLuxuryKnight(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.BISHOP -> drawLuxuryBishop(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.ROOK -> drawLuxuryRook(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.QUEEN -> drawLuxuryQueen(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.KING -> drawLuxuryKing(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
    }
}

// ==========================================
// 2. CARVED WOOD VECTOR PIECES
// ==========================================
private fun DrawScope.drawCarvedWoodPiece(type: PieceType, isWhite: Boolean) {
    val strokeColor = if (isWhite) Color(0xFF4A3425) else Color(0xFF1E140C)
    val strokeWidth = size.width * 0.034f
    val rimColor = if (isWhite) Color(0xFFFAF0E6) else Color(0xFFB58A63)
    val accentColor = if (isWhite) Color(0xFFC69C6D) else Color(0xFFD4A373)

    val bodyBrush = if (isWhite) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFF7EFE4), Color(0xFFEBDCC8), Color(0xFFD9C2A7), Color(0xFFC4A887)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF4A3223), Color(0xFF332014), Color(0xFF22140C), Color(0xFF140A05)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    }

    when (type) {
        PieceType.PAWN -> drawLuxuryPawn(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.KNIGHT -> drawLuxuryKnight(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.BISHOP -> drawLuxuryBishop(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.ROOK -> drawLuxuryRook(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.QUEEN -> drawLuxuryQueen(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.KING -> drawLuxuryKing(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
    }
}

// ==========================================
// 3. MINIMAL SLATE PIECES
// ==========================================
private fun DrawScope.drawMinimalSlatePiece(type: PieceType, isWhite: Boolean) {
    val strokeColor = if (isWhite) Color(0xFF1E2630) else Color(0xFF0A0E13)
    val strokeWidth = size.width * 0.036f
    val rimColor = if (isWhite) Color(0xFFFFFFFF) else Color(0xFF90A4AE)
    val accentColor = if (isWhite) Color(0xFFB0BEC5) else Color(0xFFCFD8DC)

    val bodyBrush = if (isWhite) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFFB0BEC5)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF455A64), Color(0xFF37474F), Color(0xFF263238), Color(0xFF1A2328)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    }

    when (type) {
        PieceType.PAWN -> drawLuxuryPawn(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.KNIGHT -> drawLuxuryKnight(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.BISHOP -> drawLuxuryBishop(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.ROOK -> drawLuxuryRook(bodyBrush, strokeColor, rimColor, strokeWidth, isWhite)
        PieceType.QUEEN -> drawLuxuryQueen(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
        PieceType.KING -> drawLuxuryKing(bodyBrush, strokeColor, rimColor, accentColor, strokeWidth, isWhite)
    }
}

// ==========================================
// VECTOR GEOMETRY DETAILS
// ==========================================
private fun DrawScope.drawSculptedBase(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    val plinth = Path().apply {
        moveTo(w * 0.20f, h * 0.88f)
        cubicTo(w * 0.20f, h * 0.84f, w * 0.32f, h * 0.82f, w * 0.50f, h * 0.82f)
        cubicTo(w * 0.68f, h * 0.82f, w * 0.80f, h * 0.84f, w * 0.80f, h * 0.88f)
        lineTo(w * 0.80f, h * 0.92f)
        cubicTo(w * 0.80f, h * 0.94f, w * 0.20f, h * 0.94f, w * 0.20f, h * 0.92f)
        close()
    }
    drawPath(plinth, brush)
    drawPath(plinth, strokeColor, style = Stroke(strokeWidth))

    val molding = Path().apply {
        moveTo(w * 0.26f, h * 0.82f)
        cubicTo(w * 0.26f, h * 0.78f, w * 0.36f, h * 0.77f, w * 0.50f, h * 0.77f)
        cubicTo(w * 0.64f, h * 0.77f, w * 0.74f, h * 0.78f, w * 0.74f, h * 0.82f)
        close()
    }
    drawPath(molding, brush)
    drawPath(molding, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val rimHighlight = Path().apply {
        moveTo(w * 0.24f, h * 0.87f)
        cubicTo(w * 0.32f, h * 0.84f, w * 0.44f, h * 0.83f, w * 0.54f, h * 0.83f)
    }
    drawPath(rimHighlight, rimColor.copy(alpha = if (isWhite) 0.8f else 0.55f), style = Stroke(strokeWidth * 0.7f))
}

private fun DrawScope.drawLuxuryPawn(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val body = Path().apply {
        moveTo(w * 0.32f, h * 0.77f)
        cubicTo(w * 0.35f, h * 0.62f, w * 0.42f, h * 0.48f, w * 0.42f, h * 0.44f)
        lineTo(w * 0.58f, h * 0.44f)
        cubicTo(w * 0.58f, h * 0.48f, w * 0.65f, h * 0.62f, w * 0.68f, h * 0.77f)
        close()
    }
    drawPath(body, brush)
    drawPath(body, strokeColor, style = Stroke(strokeWidth))

    val collar = Path().apply {
        moveTo(w * 0.38f, h * 0.44f)
        cubicTo(w * 0.38f, h * 0.40f, w * 0.62f, h * 0.40f, w * 0.62f, h * 0.44f)
        cubicTo(w * 0.62f, h * 0.47f, w * 0.38f, h * 0.47f, w * 0.38f, h * 0.44f)
        close()
    }
    drawPath(collar, brush)
    drawPath(collar, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val headRadius = w * 0.17f
    val headCenter = Offset(w * 0.50f, h * 0.28f)
    drawCircle(brush, headRadius, headCenter)
    drawCircle(strokeColor, headRadius, headCenter, style = Stroke(strokeWidth))

    val gleamRadius = headRadius * 0.40f
    val gleamCenter = Offset(w * 0.44f, h * 0.23f)
    drawCircle(rimColor.copy(alpha = if (isWhite) 0.85f else 0.6f), gleamRadius, gleamCenter)
}

private fun DrawScope.drawLuxuryKnight(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val horse = Path().apply {
        moveTo(w * 0.30f, h * 0.77f)
        cubicTo(w * 0.24f, h * 0.65f, w * 0.22f, h * 0.52f, w * 0.25f, h * 0.42f)
        cubicTo(w * 0.26f, h * 0.35f, w * 0.29f, h * 0.32f, w * 0.34f, h * 0.33f)
        cubicTo(w * 0.36f, h * 0.35f, w * 0.37f, h * 0.39f, w * 0.40f, h * 0.39f)
        cubicTo(w * 0.42f, h * 0.34f, w * 0.44f, h * 0.26f, w * 0.46f, h * 0.20f)
        lineTo(w * 0.50f, h * 0.14f)
        lineTo(w * 0.53f, h * 0.21f)
        lineTo(w * 0.56f, h * 0.16f)
        lineTo(w * 0.58f, h * 0.23f)
        cubicTo(w * 0.64f, h * 0.28f, w * 0.74f, h * 0.35f, w * 0.72f, h * 0.46f)
        lineTo(w * 0.67f, h * 0.48f)
        cubicTo(w * 0.74f, h * 0.54f, w * 0.73f, h * 0.64f, w * 0.70f, h * 0.77f)
        close()
    }
    drawPath(horse, brush)
    drawPath(horse, strokeColor, style = Stroke(strokeWidth))

    val mane1 = Path().apply {
        moveTo(w * 0.60f, h * 0.32f)
        cubicTo(w * 0.64f, h * 0.36f, w * 0.68f, h * 0.39f, w * 0.70f, h * 0.44f)
    }
    drawPath(mane1, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val mane2 = Path().apply {
        moveTo(w * 0.58f, h * 0.45f)
        cubicTo(w * 0.62f, h * 0.51f, w * 0.67f, h * 0.55f, w * 0.68f, h * 0.61f)
    }
    drawPath(mane2, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val jaw = Path().apply {
        moveTo(w * 0.33f, h * 0.45f)
        cubicTo(w * 0.38f, h * 0.47f, w * 0.45f, h * 0.45f, w * 0.48f, h * 0.40f)
    }
    drawPath(jaw, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val eyeCenter = Offset(w * 0.41f, h * 0.28f)
    drawCircle(strokeColor, w * 0.032f, eyeCenter)
    drawCircle(rimColor, w * 0.016f, Offset(w * 0.405f, h * 0.275f))
    drawCircle(strokeColor, w * 0.022f, Offset(w * 0.31f, h * 0.36f))

    val neckRim = Path().apply {
        moveTo(w * 0.26f, h * 0.44f)
        cubicTo(w * 0.24f, h * 0.54f, w * 0.26f, h * 0.64f, w * 0.31f, h * 0.74f)
    }
    drawPath(neckRim, rimColor.copy(alpha = if (isWhite) 0.7f else 0.5f), style = Stroke(strokeWidth * 0.7f))
}

private fun DrawScope.drawLuxuryBishop(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    accentColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val body = Path().apply {
        moveTo(w * 0.32f, h * 0.77f)
        cubicTo(w * 0.35f, h * 0.64f, w * 0.42f, h * 0.55f, w * 0.42f, h * 0.50f)
        lineTo(w * 0.58f, h * 0.50f)
        cubicTo(w * 0.58f, h * 0.55f, w * 0.65f, h * 0.64f, w * 0.68f, h * 0.77f)
        close()
    }
    drawPath(body, brush)
    drawPath(body, strokeColor, style = Stroke(strokeWidth))

    val collar = Path().apply {
        moveTo(w * 0.36f, h * 0.50f)
        cubicTo(w * 0.36f, h * 0.46f, w * 0.64f, h * 0.46f, w * 0.64f, h * 0.50f)
        cubicTo(w * 0.64f, h * 0.53f, w * 0.36f, h * 0.53f, w * 0.36f, h * 0.50f)
        close()
    }
    drawPath(collar, brush)
    drawPath(collar, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val mitre = Path().apply {
        moveTo(w * 0.36f, h * 0.46f)
        cubicTo(w * 0.30f, h * 0.36f, w * 0.38f, h * 0.22f, w * 0.50f, h * 0.16f)
        cubicTo(w * 0.62f, h * 0.22f, w * 0.70f, h * 0.36f, w * 0.64f, h * 0.46f)
        close()
    }
    drawPath(mitre, brush)
    drawPath(mitre, strokeColor, style = Stroke(strokeWidth))

    val slit = Path().apply {
        moveTo(w * 0.56f, h * 0.24f)
        lineTo(w * 0.44f, h * 0.38f)
    }
    drawPath(slit, strokeColor, style = Stroke(strokeWidth * 1.2f, cap = StrokeCap.Round))

    val finialRadius = w * 0.055f
    val finialCenter = Offset(w * 0.50f, h * 0.14f)
    drawCircle(brush, finialRadius, finialCenter)
    drawCircle(strokeColor, finialRadius, finialCenter, style = Stroke(strokeWidth * 0.8f))
    drawCircle(accentColor, finialRadius * 0.4f, finialCenter)
}

private fun DrawScope.drawLuxuryRook(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val tower = Path().apply {
        moveTo(w * 0.31f, h * 0.77f)
        lineTo(w * 0.35f, h * 0.38f)
        lineTo(w * 0.65f, h * 0.38f)
        lineTo(w * 0.69f, h * 0.77f)
        close()
    }
    drawPath(tower, brush)
    drawPath(tower, strokeColor, style = Stroke(strokeWidth))

    val cornice = Path().apply {
        moveTo(w * 0.28f, h * 0.38f)
        lineTo(w * 0.28f, h * 0.34f)
        lineTo(w * 0.72f, h * 0.34f)
        lineTo(w * 0.72f, h * 0.38f)
        close()
    }
    drawPath(cornice, brush)
    drawPath(cornice, strokeColor, style = Stroke(strokeWidth))

    val battlements = Path().apply {
        moveTo(w * 0.28f, h * 0.34f)
        lineTo(w * 0.28f, h * 0.22f)
        lineTo(w * 0.36f, h * 0.22f)
        lineTo(w * 0.36f, h * 0.27f)
        lineTo(w * 0.44f, h * 0.27f)
        lineTo(w * 0.44f, h * 0.22f)
        lineTo(w * 0.56f, h * 0.22f)
        lineTo(w * 0.56f, h * 0.27f)
        lineTo(w * 0.64f, h * 0.27f)
        lineTo(w * 0.64f, h * 0.22f)
        lineTo(w * 0.72f, h * 0.22f)
        lineTo(w * 0.72f, h * 0.34f)
        close()
    }
    drawPath(battlements, brush)
    drawPath(battlements, strokeColor, style = Stroke(strokeWidth))
}

private fun DrawScope.drawLuxuryQueen(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    accentColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val body = Path().apply {
        moveTo(w * 0.30f, h * 0.77f)
        cubicTo(w * 0.36f, h * 0.65f, w * 0.42f, h * 0.55f, w * 0.40f, h * 0.48f)
        lineTo(w * 0.60f, h * 0.48f)
        cubicTo(w * 0.58f, h * 0.55f, w * 0.64f, h * 0.65f, w * 0.70f, h * 0.77f)
        close()
    }
    drawPath(body, brush)
    drawPath(body, strokeColor, style = Stroke(strokeWidth))

    val collar = Path().apply {
        moveTo(w * 0.35f, h * 0.48f)
        cubicTo(w * 0.35f, h * 0.44f, w * 0.65f, h * 0.44f, w * 0.65f, h * 0.48f)
        cubicTo(w * 0.65f, h * 0.51f, w * 0.35f, h * 0.51f, w * 0.35f, h * 0.48f)
        close()
    }
    drawPath(collar, brush)
    drawPath(collar, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val coronet = Path().apply {
        moveTo(w * 0.35f, h * 0.44f)
        lineTo(w * 0.22f, h * 0.24f)
        lineTo(w * 0.35f, h * 0.33f)
        lineTo(w * 0.36f, h * 0.18f)
        lineTo(w * 0.44f, h * 0.31f)
        lineTo(w * 0.50f, h * 0.14f)
        lineTo(w * 0.56f, h * 0.31f)
        lineTo(w * 0.64f, h * 0.18f)
        lineTo(w * 0.65f, h * 0.33f)
        lineTo(w * 0.78f, h * 0.24f)
        lineTo(w * 0.65f, h * 0.44f)
        close()
    }
    drawPath(coronet, brush)
    drawPath(coronet, strokeColor, style = Stroke(strokeWidth))

    val pearlTips = listOf(
        Offset(w * 0.22f, h * 0.24f),
        Offset(w * 0.36f, h * 0.18f),
        Offset(w * 0.50f, h * 0.14f),
        Offset(w * 0.64f, h * 0.18f),
        Offset(w * 0.78f, h * 0.24f)
    )
    val pearlRadius = w * 0.032f
    for (pt in pearlTips) {
        drawCircle(brush, pearlRadius, pt)
        drawCircle(strokeColor, pearlRadius, pt, style = Stroke(strokeWidth * 0.6f))
        drawCircle(accentColor, pearlRadius * 0.45f, pt)
    }
}

private fun DrawScope.drawLuxuryKing(
    brush: Brush,
    strokeColor: Color,
    rimColor: Color,
    accentColor: Color,
    strokeWidth: Float,
    isWhite: Boolean
) {
    val w = size.width
    val h = size.height

    drawSculptedBase(brush, strokeColor, rimColor, strokeWidth, isWhite)

    val body = Path().apply {
        moveTo(w * 0.29f, h * 0.77f)
        cubicTo(w * 0.34f, h * 0.63f, w * 0.41f, h * 0.54f, w * 0.40f, h * 0.47f)
        lineTo(w * 0.60f, h * 0.47f)
        cubicTo(w * 0.59f, h * 0.54f, w * 0.66f, h * 0.63f, w * 0.71f, h * 0.77f)
        close()
    }
    drawPath(body, brush)
    drawPath(body, strokeColor, style = Stroke(strokeWidth))

    val crownBand = Path().apply {
        moveTo(w * 0.32f, h * 0.47f)
        cubicTo(w * 0.32f, h * 0.42f, w * 0.68f, h * 0.42f, w * 0.68f, h * 0.47f)
        cubicTo(w * 0.68f, h * 0.51f, w * 0.32f, h * 0.51f, w * 0.32f, h * 0.47f)
        close()
    }
    drawPath(crownBand, brush)
    drawPath(crownBand, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val dome = Path().apply {
        moveTo(w * 0.31f, h * 0.42f)
        cubicTo(w * 0.25f, h * 0.32f, w * 0.33f, h * 0.24f, w * 0.50f, h * 0.24f)
        cubicTo(w * 0.67f, h * 0.24f, w * 0.75f, h * 0.32f, w * 0.69f, h * 0.42f)
        close()
    }
    drawPath(dome, brush)
    drawPath(dome, strokeColor, style = Stroke(strokeWidth))

    val arch = Path().apply {
        moveTo(w * 0.42f, h * 0.42f)
        cubicTo(w * 0.42f, h * 0.30f, w * 0.47f, h * 0.24f, w * 0.50f, h * 0.24f)
        cubicTo(w * 0.53f, h * 0.24f, w * 0.58f, h * 0.30f, w * 0.58f, h * 0.42f)
    }
    drawPath(arch, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val crossVertical = Path().apply {
        addRect(Rect(w * 0.470f, h * 0.08f, w * 0.530f, h * 0.24f))
    }
    drawPath(crossVertical, brush)
    drawPath(crossVertical, strokeColor, style = Stroke(strokeWidth * 0.8f))

    val crossHorizontal = Path().apply {
        addRect(Rect(w * 0.415f, h * 0.12f, w * 0.585f, h * 0.175f))
    }
    drawPath(crossHorizontal, brush)
    drawPath(crossHorizontal, strokeColor, style = Stroke(strokeWidth * 0.8f))

    drawCircle(accentColor, w * 0.024f, Offset(w * 0.50f, h * 0.147f))
}
