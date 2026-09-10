package com.example.chessgame.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.engine.*
import com.example.chessgame.theme.BoardTheme
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.ThemeRegistry

/**
 * Physical Tabletop Chessboard.
 * Designed like a real wooden chessboard sitting on a study table.
 * Supports both hand-drawn artisan board textures and rich procedural timber/marble tiles,
 * complete with smooth gliding piece animations and realistic contact shadows.
 */
@Composable
fun ChessBoardView(
    state: GameState,
    flipped: Boolean,
    isInteractable: Boolean,
    showCoordinates: Boolean,
    highlightMoves: Boolean,
    boardTheme: BoardTheme = ThemeRegistry.ARTISAN_WOOD,
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    lastMove: Move?,
    onMove: (Move) -> Unit,
    previewFrom: Int? = null,
    previewTo: Int? = null,
    modifier: Modifier = Modifier
) {
    var selectedSquare by remember { mutableStateOf<Int?>(null) }
    var pendingPromotion by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val board = state.board
    val turn = state.turn
    val kingInCheckSq = if (isInCheck(board, turn)) findKing(board, turn) else -1

    val legalMovesForSelected = remember(state, selectedSquare) {
        if (selectedSquare != null) getLegalMovesForSquare(state, selectedSquare!!) else emptyList()
    }

    // Warm elegant check pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "check_pulse")
    val checkPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Destination target pulsing animation for preview hints
    val destinationPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "destination_alpha"
    )
    val destinationPulseScale by infiniteTransition.animateFloat(
        initialValue = 0.80f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "destination_scale"
    )

    // Smooth gliding piece animation
    val moveAnimProgress = remember { Animatable(1f) }
    var animatedMove by remember { mutableStateOf<Move?>(null) }

    LaunchedEffect(lastMove) {
        if (lastMove != null) {
            animatedMove = lastMove
            moveAnimProgress.snapTo(0f)
            moveAnimProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
        }
    }

    val isAnimating = moveAnimProgress.value < 1f && animatedMove != null

    fun handleSquareClick(sq: Int) {
        if (!isInteractable || isAnimating) return

        if (selectedSquare != null) {
            val from = selectedSquare!!
            if (from == sq) {
                selectedSquare = null
                return
            }

            val matching = legalMovesForSelected.filter { it.to == sq }
            if (matching.isNotEmpty()) {
                val piece = board[from]
                if (piece?.type == PieceType.PAWN && (getRank(sq) == 7 || getRank(sq) == 0)) {
                    pendingPromotion = Pair(from, sq)
                    selectedSquare = null
                    return
                }

                onMove(matching[0])
                selectedSquare = null
                return
            }
        }

        val clicked = board[sq]
        if (clicked != null && clicked.color == turn) {
            selectedSquare = sq
        } else {
            selectedSquare = null
        }
    }

    // Physical Tabletop Board Container with rich wooden beveling and contact shadow
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(20.dp, RoundedCornerShape(14.dp), spotColor = Color(0x99000000))
            .clip(RoundedCornerShape(14.dp))
            .background(boardTheme.frameBorder)
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    listOf(
                        boardTheme.frameBorder.copy(alpha = 0.9f),
                        boardTheme.frameInnerBorder,
                        boardTheme.frameBorder
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(6.dp)
    ) {
        // Inner board wrapper
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, boardTheme.frameInnerBorder, RoundedCornerShape(8.dp))
        ) {
            // Optional hand-drawn artisan board background image
            if (boardTheme.boardDrawableRes != null) {
                Image(
                    painter = painterResource(id = boardTheme.boardDrawableRes),
                    contentDescription = "Chessboard",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val squareWidth = maxWidth / 8
                val squareHeight = maxHeight / 8
                val density = androidx.compose.ui.platform.LocalDensity.current
                val squareWidthPx = with(density) { squareWidth.toPx() }
                val squareHeightPx = with(density) { squareHeight.toPx() }

                // ==========================================
                // LAYER 1: 8x8 Board Squares, Highlights & Coordinates
                // ==========================================
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0 until 8) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until 8) {
                                val file = if (flipped) 7 - col else col
                                val rank = if (flipped) row else 7 - row
                                val sq = squareFromCoords(file, rank)

                                val piece = board[sq]
                                val isLight = isLightSquare(sq)
                                val isSelected = selectedSquare == sq
                                val isLastMoveFrom = lastMove?.from == sq
                                val isLastMoveTo = lastMove?.to == sq
                                val isKingChecked = kingInCheckSq == sq

                                val isPreviewFrom = previewFrom == sq
                                val isPreviewTo = previewTo == sq

                                val targetMove = legalMovesForSelected.find { it.to == sq }
                                val isLegalTarget = highlightMoves && targetMove != null
                                val isCaptureTarget = isLegalTarget && (piece != null || targetMove.isEnPassant)

                                val showRank = showCoordinates && col == 0
                                val showFile = showCoordinates && row == 7

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(
                                            when {
                                                isKingChecked -> Color(0xFFE11D48).copy(alpha = checkPulseAlpha * 0.7f)
                                                isSelected -> boardTheme.selectedSquareColor
                                                isPreviewTo -> Color(0xFFDFB36E).copy(alpha = 0.55f)
                                                isPreviewFrom -> Color(0xFFDFB36E).copy(alpha = 0.30f)
                                                isLastMoveTo -> boardTheme.lastMoveColor.copy(alpha = 0.55f)
                                                isLastMoveFrom -> boardTheme.lastMoveColor.copy(alpha = 0.35f)
                                                boardTheme.boardDrawableRes != null -> Color.Transparent
                                                isLight -> boardTheme.lightSquare
                                                else -> boardTheme.darkSquare
                                            }
                                        )
                                        .then(
                                            when {
                                                isSelected -> Modifier.border(2.5.dp, Color(0xFFFFCC00))
                                                isKingChecked -> Modifier.border(2.dp, Color(0xFFFF2A1A))
                                                isPreviewTo -> Modifier.border(2.dp, Color(0xFFDFB36E), RoundedCornerShape(2.dp))
                                                isPreviewFrom -> Modifier.border(1.5.dp, Color(0x99DFB36E), RoundedCornerShape(2.dp))
                                                else -> Modifier
                                            }
                                        )
                                ) {
                                    // King In Check dramatic flame aura
                                    if (isKingChecked) {
                                        KingCheckEffect()
                                    }

                                    // Coordinates
                                    if (showRank) {
                                        Text(
                                            text = RANKS[rank],
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isLight) boardTheme.coordinateColorLight else boardTheme.coordinateColorDark,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(start = 2.dp, top = 1.dp)
                                        )
                                    }
                                    if (showFile) {
                                        Text(
                                            text = FILES[file],
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isLight) boardTheme.coordinateColorLight else boardTheme.coordinateColorDark,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(end = 2.dp, bottom = 1.dp)
                                        )
                                    }

                                    // Preview destination pulsing reticle marker
                                    if (isPreviewTo) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .graphicsLayer {
                                                    scaleX = destinationPulseScale
                                                    scaleY = destinationPulseScale
                                                    alpha = destinationPulseAlpha
                                                }
                                                .border(2.5.dp, Color(0xFFFFCC00), CircleShape)
                                        )
                                    }

                                    // Comic game move indicators
                                    if (isLegalTarget) {
                                        if (isCaptureTarget) {
                                            // Capture target: comic red/orange combat ring
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(0.86f)
                                                    .border(2.5.dp, Color(0xFFFF3B30), CircleShape)
                                                    .padding(2.dp)
                                                    .border(1.dp, Color(0x88FFD700), CircleShape)
                                            )
                                        } else {
                                            // Quiet move: bright comic emerald dot with dark stroke
                                            Box(
                                                modifier = Modifier
                                                    .size(11.dp)
                                                    .shadow(2.dp, CircleShape)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF22C55E))
                                                    .border(1.5.dp, Color(0xFF0F172A), CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // LAYER 2: Move Preview Directional Arrow Canvas
                // Sits above the board squares and below the chess pieces
                // ==========================================
                if (previewFrom != null && previewTo != null) {
                    PreviewArrowCanvas(
                        fromSq = previewFrom,
                        toSq = previewTo,
                        flipped = flipped,
                        squareWidthPx = squareWidthPx,
                        squareHeightPx = squareHeightPx,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // ==========================================
                // LAYER 3: Interactive Pieces Grid
                // ==========================================
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0 until 8) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0 until 8) {
                                val file = if (flipped) 7 - col else col
                                val rank = if (flipped) row else 7 - row
                                val sq = squareFromCoords(file, rank)
                                val piece = board[sq]
                                val isSelected = selectedSquare == sq
                                val hideStaticPiece = isAnimating && animatedMove?.to == sq

                                // Selected piece physical hover lift
                                val pieceElevation by animateDpAsState(
                                    targetValue = if (isSelected) (-6).dp else 0.dp,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                    label = "piece_hover"
                                )

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { handleSquareClick(sq) }
                                ) {
                                    if (piece != null && !hideStaticPiece) {
                                        PieceView(
                                            type = piece.type,
                                            color = piece.color,
                                            pieceTheme = pieceTheme,
                                            modifier = Modifier
                                                .fillMaxSize(0.90f)
                                                .offset(y = pieceElevation)
                                                .graphicsLayer {
                                                    if (isSelected) {
                                                        scaleX = 1.10f
                                                        scaleY = 1.10f
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Smooth Gliding Piece Animation Overlay
                if (isAnimating && animatedMove != null) {
                    val move = animatedMove!!
                    val fromFile = getFile(move.from)
                    val fromRank = getRank(move.from)
                    val toFile = getFile(move.to)
                    val toRank = getRank(move.to)

                    val fromDisplayCol = if (flipped) 7 - fromFile else fromFile
                    val fromDisplayRow = if (flipped) fromRank else 7 - fromRank
                    val toDisplayCol = if (flipped) 7 - toFile else toFile
                    val toDisplayRow = if (flipped) toRank else 7 - toRank

                    val progress = moveAnimProgress.value
                    val curCol = fromDisplayCol + (toDisplayCol - fromDisplayCol) * progress
                    val curRow = fromDisplayRow + (toDisplayRow - fromDisplayRow) * progress

                    val posX = squareWidth * curCol
                    val posY = squareHeight * curRow

                    // Subtle parabolic physical flight arc
                    val flightLift = (-10 * kotlin.math.sin(progress * Math.PI)).dp

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(squareWidth, squareHeight)
                            .offset(x = posX, y = posY + flightLift)
                            .graphicsLayer {
                                scaleX = 1.12f
                                scaleY = 1.12f
                                shadowElevation = 14.dp.toPx()
                            }
                    ) {
                        PieceView(
                            type = move.promotion ?: move.piece,
                            color = move.color,
                            pieceTheme = pieceTheme,
                            modifier = Modifier.fillMaxSize(0.92f)
                        )
                    }
                }
            }
        }
    }

    // Promotion Tray Dialog
    pendingPromotion?.let { (from, to) ->
        PromotionTray(
            color = turn,
            pieceTheme = pieceTheme,
            onSelect = { promoType ->
                val promoMove = Move(
                    from = from,
                    to = to,
                    piece = PieceType.PAWN,
                    color = turn,
                    promotion = promoType,
                    captured = board[to]?.type
                )
                onMove(promoMove)
                pendingPromotion = null
            }
        )
    }
}

/**
 * Directional Arrow Canvas for Move Preview.
 * Draws an elegant, tactile directional arrow from source square center to destination square center.
 * Features a tapered shaft, graceful arrowhead wings, drop shadow, and warm gold gradient.
 * Sits above the board squares and below the chess pieces.
 */
@Composable
private fun PreviewArrowCanvas(
    fromSq: Int,
    toSq: Int,
    flipped: Boolean,
    squareWidthPx: Float,
    squareHeightPx: Float,
    modifier: Modifier = Modifier
) {
    val fromFile = getFile(fromSq)
    val fromRank = getRank(fromSq)
    val toFile = getFile(toSq)
    val toRank = getRank(toSq)

    val fromCol = if (flipped) 7 - fromFile else fromFile
    val fromRow = if (flipped) fromRank else 7 - fromRank
    val toCol = if (flipped) 7 - toFile else toFile
    val toRow = if (flipped) toRank else 7 - toRank

    val startX = (fromCol + 0.5f) * squareWidthPx
    val startY = (fromRow + 0.5f) * squareHeightPx
    val endX = (toCol + 0.5f) * squareWidthPx
    val endY = (toRow + 0.5f) * squareHeightPx

    // Arrow breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "arrow_anim")
    val arrowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowAlpha"
    )

    Canvas(modifier = modifier) {
        val dx = endX - startX
        val dy = endY - startY
        val length = kotlin.math.sqrt(dx * dx + dy * dy)
        if (length < 1f) return@Canvas

        val ux = dx / length
        val uy = dy / length
        val nx = -uy
        val ny = ux

        // Dimensions proportional to board square scale
        val shaftWidth = squareWidthPx * 0.15f
        val headLength = (squareWidthPx * 0.40f).coerceAtMost(length * 0.45f)
        val headWidth = squareWidthPx * 0.42f

        // The arrow shaft emerges slightly outside the source piece center
        val startOffset = squareWidthPx * 0.20f
        val actualStartX = startX + ux * startOffset
        val actualStartY = startY + uy * startOffset

        // Arrow tip points right into the center of destination square
        val tipX = endX
        val tipY = endY

        // Base center of the arrowhead
        val baseCenterX = tipX - ux * headLength
        val baseCenterY = tipY - uy * headLength

        // Arrowhead wing points
        val leftWingX = baseCenterX + nx * (headWidth / 2f)
        val leftWingY = baseCenterY + ny * (headWidth / 2f)
        val rightWingX = baseCenterX - nx * (headWidth / 2f)
        val rightWingY = baseCenterY - ny * (headWidth / 2f)

        // Shaft corners at the base of the arrowhead
        val shaftLeftEndX = baseCenterX + nx * (shaftWidth / 2f)
        val shaftLeftEndY = baseCenterY + ny * (shaftWidth / 2f)
        val shaftRightEndX = baseCenterX - nx * (shaftWidth / 2f)
        val shaftRightEndY = baseCenterY - ny * (shaftWidth / 2f)

        // Shaft corners at the source
        val shaftLeftStartX = actualStartX + nx * (shaftWidth / 2f)
        val shaftLeftStartY = actualStartY + ny * (shaftWidth / 2f)
        val shaftRightStartX = actualStartX - nx * (shaftWidth / 2f)
        val shaftRightStartY = actualStartY - ny * (shaftWidth / 2f)

        val arrowPath = Path().apply {
            moveTo(shaftLeftStartX, shaftLeftStartY)
            lineTo(shaftLeftEndX, shaftLeftEndY)
            lineTo(leftWingX, leftWingY)
            lineTo(tipX, tipY)
            lineTo(rightWingX, rightWingY)
            lineTo(shaftRightEndX, shaftRightEndY)
            lineTo(shaftRightStartX, shaftRightStartY)
            close()
        }

        // 1. Tactile contact drop shadow
        drawPath(
            path = arrowPath,
            color = Color(0x66000000),
            style = Fill
        )

        // 2. Rich warm gold gradient fill matching app visual identity
        drawPath(
            path = arrowPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE5A93C).copy(alpha = arrowAlpha),
                    Color(0xFFDFB36E).copy(alpha = arrowAlpha)
                ),
                start = Offset(actualStartX, actualStartY),
                end = Offset(tipX, tipY)
            ),
            style = Fill
        )

        // 3. Crisp brass contour edge
        drawPath(
            path = arrowPath,
            color = Color(0xFF6B451B).copy(alpha = 0.85f),
            style = Stroke(width = 1.75.dp.toPx())
        )
    }
}

/**
 * Comic fire flame and alert beacon effect for King in Check.
 * Matches the dramatic comic action game visual identity.
 */
@Composable
private fun KingCheckEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "king_check_flame")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )
    val flameAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_alpha"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_rot"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing fiery radial aura
        Canvas(
            modifier = Modifier
                .fillMaxSize(1.25f)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = flameAlpha
                    rotationZ = rotation
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF2A1A).copy(alpha = 0.95f),
                        Color(0xFFFF8C00).copy(alpha = 0.70f),
                        Color(0xFFFFD700).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        // Inner comic hazard warning border
        Box(
            modifier = Modifier
                .fillMaxSize(0.92f)
                .border(2.5.dp, Color(0xFFFF2A1A), CircleShape)
        )
    }
}
