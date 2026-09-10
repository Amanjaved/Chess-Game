package com.example.chessgame.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.audio.SoundManager

/**
 * ============================================================
 * "TACTICAL MONOLITH / GRANDMASTER ARENA" DESIGN SYSTEM
 * Centralized design tokens, colors, surfaces, buttons, and HUD
 * components for a world-class competitive mobile game experience.
 * ============================================================
 */

object ArenaColors {
    // Backgrounds & Void
    val VoidAbyss = Color(0xFF090D14)
    val VoidDeep = Color(0xFF0B111A)
    val VoidScrim = Color(0xF2070B10)

    // Titanium Surfaces
    val TitaniumSurface = Color(0xFF131A24)
    val TitaniumSurfaceRaised = Color(0xFF1A2433)
    val TitaniumSurfaceHover = Color(0xFF233044)
    val TitaniumBorder = Color(0xFF2B3A4F)
    val TitaniumBorderSubtle = Color(0x354E6788)

    // High-Energy Game Accents
    val CyberCyan = Color(0xFF00E5FF)
    val CyberCyanDim = Color(0xFF00B0FF)
    val CyberCyanGlow = Color(0x3300E5FF)

    val SolarAmber = Color(0xFFFFAB00)
    val SolarAmberBright = Color(0xFFFFD700)
    val SolarAmberGlow = Color(0x33FFAB00)

    val CrimsonAlert = Color(0xFFFF3366)
    val CrimsonGlow = Color(0x33FF3366)

    val EmeraldVictory = Color(0xFF00E676)
    val EmeraldGlow = Color(0x3300E676)

    val RoyalPurple = Color(0xFF9D4EDD)

    // Typography & Ink
    val TextPrimary = Color(0xFFF2F6FA)
    val TextSecondary = Color(0xFF8C9BAE)
    val TextMuted = Color(0xFF506175)
    val TextDisabled = Color(0xFF384556)
}

/**
 * Game Atmospheric Scaffold:
 * Sets the "Grandmaster Arena" background with an intelligent radial vignette
 * so gameplay elements remain crisp and readable while the arena edges pop.
 */
@Composable
fun ArenaBackgroundScaffold(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ArenaColors.VoidAbyss)
    ) {
        // LAYER 1: Futuristic Arena Monolith Background
        Image(
            painter = painterResource(id = R.drawable.bg_arena_monolith),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // LAYER 1.5: Deep space darkening vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xE6080C13), // 90% dark center
                            Color(0xCC090E16),
                            Color(0x66080D14)  // 40% ambient edges
                        ),
                        radius = 1350f
                    )
                )
        )

        // LAYER 2: Foreground Game Content
        content()
    }
}

/**
 * Standardized High-Impact Game Header with Safe-Area insets.
 */
@Composable
fun ArenaHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    rightContent: (@Composable RowScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left: Back Button
        if (onBack != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x331C2636))
                    .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(10.dp))
                    .clickable {
                        SoundManager.playClick()
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ArenaColors.TextPrimary
                )
            }
        }

        // Center: Title + Subtitle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.5.sp,
                fontFamily = FontFamily.SansSerif,
                color = ArenaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ArenaColors.CyberCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Right Actions
        if (rightContent != null) {
            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                content = rightContent
            )
        }
    }
}

/**
 * Primary Tactical Game Button:
 * Features tactile physical press animation (scale down on touch),
 * glowing energy border, gradient fill, and crisp sound feedback.
 */
@Composable
fun ArenaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    isDanger: Boolean = false,
    height: Dp = 48.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "btn_press"
    )

    val baseColor = when {
        isDanger -> ArenaColors.CrimsonAlert
        isPrimary -> ArenaColors.CyberCyan
        else -> ArenaColors.SolarAmber
    }

    val bgBrush = if (isPrimary) {
        Brush.verticalGradient(
            listOf(
                baseColor.copy(alpha = 0.95f),
                baseColor.copy(alpha = 0.75f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFF1E293B),
                Color(0xFF141D2A)
            )
        )
    }

    val borderColor = if (isPrimary) {
        baseColor
    } else {
        ArenaColors.TitaniumBorder
    }

    val contentColor = if (isPrimary) {
        Color(0xFF070B10)
    } else {
        ArenaColors.TextPrimary
    }

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .height(height)
            .shadow(
                elevation = if (isPrimary && enabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isPrimary) baseColor.copy(alpha = 0.5f) else Color.Black
            )
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) bgBrush else Brush.verticalGradient(listOf(Color(0xFF151D28), Color(0xFF101720))))
            .border(
                width = if (isPrimary) 1.5.dp else 1.dp,
                color = if (enabled) borderColor else ArenaColors.TextDisabled,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                SoundManager.playClick()
                onClick()
            }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 15.sp,
                    color = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = contentColor
            )
        }
    }
}

/**
 * Compact Tactical Pill Button (for in-game controls and HUD actions).
 */
@Composable
fun ArenaPill(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    enabled: Boolean = true,
    activeColor: Color = ArenaColors.CyberCyan
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "pill_press"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .height(38.dp)
            .shadow(
                elevation = if (isActive) 6.dp else 1.dp,
                shape = RoundedCornerShape(19.dp),
                spotColor = if (isActive) activeColor else Color.Black
            )
            .clip(RoundedCornerShape(19.dp))
            .background(
                if (isActive) {
                    Brush.verticalGradient(listOf(activeColor, activeColor.copy(alpha = 0.8f)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF192230), Color(0xFF101722)))
                }
            )
            .border(
                width = 1.2.dp,
                color = if (isActive) activeColor else ArenaColors.TitaniumBorder,
                shape = RoundedCornerShape(19.dp)
            )
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null
            ) {
                SoundManager.playClick()
                onClick()
            }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 12.sp,
                color = if (isActive) Color(0xFF070B10) else ArenaColors.TextPrimary
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color(0xFF070B10) else ArenaColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Tactical Combat Card Container with elevated titanium surface and bevel borders.
 */
@Composable
fun ArenaCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isHighlighted: Boolean = false,
    highlightColor: Color = ArenaColors.CyberCyan,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1.0f,
        animationSpec = tween(100),
        label = "card_press"
    )

    Box(
        modifier = modifier
            .scale(scaleAnim)
            .shadow(
                elevation = if (isHighlighted) 10.dp else 4.dp,
                shape = shape,
                spotColor = if (isHighlighted) highlightColor.copy(alpha = 0.4f) else Color.Black
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A2433),
                        Color(0xFF121924)
                    )
                )
            )
            .border(
                width = if (isHighlighted) 1.8.dp else 1.dp,
                color = if (isHighlighted) highlightColor else ArenaColors.TitaniumBorder,
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        SoundManager.playClick()
                        onClick()
                    }
                } else Modifier
            ),
        content = content
    )
}

/**
 * Tactical Status & Stat Badge (XP, Rank, Accuracy, Best Move).
 */
@Composable
fun ArenaBadge(
    text: String,
    modifier: Modifier = Modifier,
    icon: String? = null,
    color: Color = ArenaColors.CyberCyan,
    fontSize: Int = 10
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = (fontSize + 1).sp
                )
                Spacer(modifier = Modifier.width(3.5.dp))
            }
            Text(
                text = text,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = color
            )
        }
    }
}
