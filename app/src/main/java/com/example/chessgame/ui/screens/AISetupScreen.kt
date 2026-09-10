package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.GameFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PlayerColorChoice {
    WHITE,
    RANDOM,
    BLACK
}

/**
 * Screen 3: Choose Your Opponent (Boss Select Arena)
 * Redesigned as a true AAA Game Character/Boss Select screen:
 * - 4 distinct AI Boss Engines with custom combat rating, badge, quote, and XP bonus
 * - Faction Selection: Dawn (White), Fate (Random), Dusk (Black)
 * - Tactile sound effects and glowing neon borders for the selected challenge
 */
@Composable
fun AISetupScreen(
    onBack: () -> Unit,
    onSelectDifficulty: (AIDifficulty) -> Unit,
    onStartGame: ((AIDifficulty, PlayerColorChoice) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedDifficulty by remember { mutableStateOf(AIDifficulty.MEDIUM) }
    var selectedColor by remember { mutableStateOf(PlayerColorChoice.WHITE) }
    var isNavigating by remember { mutableStateOf(false) }

    fun launchBattle(difficulty: AIDifficulty) {
        if (!isNavigating) {
            isNavigating = true
            SoundManager.playCastle()
            coroutineScope.launch {
                delay(120)
                if (onStartGame != null) {
                    onStartGame(difficulty, selectedColor)
                } else {
                    onSelectDifficulty(difficulty)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A10))
    ) {
        // LAYER 1: ATMOSPHERIC ARENA BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.bg_main_menu),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.28f }
        )

        // Radial obsidian darkening vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x990A0F1D),
                            Color(0xEE070A10),
                            Color(0xFF070A10)
                        ),
                        radius = 1200f
                    )
                )
        )

        // LAYER 2: NATIVE COMPOSE UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ----------------------------------------------------
                // TOP BAR: Back Button & Arena Title
                // ----------------------------------------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF141C2B))
                            .border(1.dp, Color(0xFF26354E), CircleShape)
                            .clickable {
                                SoundManager.playClick()
                                onBack()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_chevron),
                            contentDescription = "Back",
                            tint = Color(0xFFF1F5F9),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CHOOSE OPPONENT",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                        Text(
                            text = "4 AI ENGINES • RATED COMBAT",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFFFB703)
                        )
                    }

                    // Balance spacer
                    Spacer(modifier = Modifier.size(42.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ----------------------------------------------------
                // FACTION / SIDE SELECTOR
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHOOSE YOUR SIDE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        fontFamily = GameFont,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FactionSelectionPill(
                            title = "WHITE",
                            subtitle = "FIRST MOVE",
                            icon = "♔",
                            isSelected = selectedColor == PlayerColorChoice.WHITE,
                            activeColor = Color(0xFFFFD54F),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                selectedColor = PlayerColorChoice.WHITE
                            }
                        )

                        FactionSelectionPill(
                            title = "RANDOM",
                            subtitle = "FATE",
                            icon = "🎲",
                            isSelected = selectedColor == PlayerColorChoice.RANDOM,
                            activeColor = Color(0xFF38BDF8),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                selectedColor = PlayerColorChoice.RANDOM
                            }
                        )

                        FactionSelectionPill(
                            title = "BLACK",
                            subtitle = "COUNTER",
                            icon = "♚",
                            isSelected = selectedColor == PlayerColorChoice.BLACK,
                            activeColor = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                SoundManager.playClick()
                                selectedColor = PlayerColorChoice.BLACK
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ----------------------------------------------------
                // 4 AI OPPONENT BOSS CARDS
                // ----------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. NOVICE (The Apprentice)
                    AIOpponentCard(
                        name = "THE APPRENTICE",
                        title = "Novice • 600 Elo",
                        description = "Learn fundamentals and tactics without heavy pressure.",
                        xpReward = "+25 XP",
                        pieceRes = R.drawable.ic_diff_pawn,
                        accentColor = Color(0xFF10B981),
                        isSelected = selectedDifficulty == AIDifficulty.EASY,
                        onClick = {
                            selectedDifficulty = AIDifficulty.EASY
                            launchBattle(AIDifficulty.EASY)
                        }
                    )

                    // 2. WARRIOR (The Tactician)
                    AIOpponentCard(
                        name = "THE TACTICIAN",
                        title = "Intermediate • 1200 Elo",
                        description = "Active piece play, tactical traps, and sharp counter-attacks.",
                        xpReward = "+50 XP",
                        pieceRes = R.drawable.ic_diff_knight,
                        accentColor = Color(0xFF00E5FF),
                        isSelected = selectedDifficulty == AIDifficulty.MEDIUM,
                        onClick = {
                            selectedDifficulty = AIDifficulty.MEDIUM
                            launchBattle(AIDifficulty.MEDIUM)
                        }
                    )

                    // 3. MASTER (The Royal Marshal)
                    AIOpponentCard(
                        name = "THE ROYAL MARSHAL",
                        title = "Master • 1800 Elo",
                        description = "Deep calculation, relentless pressure, and endgame mastery.",
                        xpReward = "+100 XP",
                        pieceRes = R.drawable.ic_diff_rook,
                        accentColor = Color(0xFFFFB703),
                        isSelected = selectedDifficulty == AIDifficulty.HARD,
                        onClick = {
                            selectedDifficulty = AIDifficulty.HARD
                            launchBattle(AIDifficulty.HARD)
                        }
                    )

                    // 4. GRANDMASTER (The Shadow Overlord)
                    AIOpponentCard(
                        name = "SHADOW OVERLORD",
                        title = "Grandmaster • 2500 Elo",
                        description = "Final Boss. Punishes every mistake with surgical precision.",
                        xpReward = "+200 XP",
                        pieceRes = R.drawable.ic_diff_king,
                        accentColor = Color(0xFFFF3366),
                        isSelected = selectedDifficulty == AIDifficulty.EXPERT,
                        onClick = {
                            selectedDifficulty = AIDifficulty.EXPERT
                            launchBattle(AIDifficulty.EXPERT)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Launch CTA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                        .height(52.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFFB703))
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFB703), Color(0xFFFF9800), Color(0xFFFF5722))
                            )
                        )
                        .clickable {
                            launchBattle(selectedDifficulty)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "COMMENCE BATTLE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = GameFont,
                            color = Color(0xFF140D04)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "▶",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF140D04)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Faction Selection Pill (White, Random, Black).
 */
@Composable
private fun FactionSelectionPill(
    title: String,
    subtitle: String,
    icon: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.15f) else Color(0xFF101726))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) activeColor else Color(0xFF222E42),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = GameFont,
                    color = if (isSelected) activeColor else Color(0xFFF1F5F9)
                )
            }
            Text(
                text = subtitle,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                fontFamily = GameFont,
                color = if (isSelected) activeColor else Color(0xFF64748B)
            )
        }
    }
}

/**
 * AI Opponent Character Card (Boss Select Style).
 */
@Composable
private fun AIOpponentCard(
    name: String,
    title: String,
    description: String,
    xpReward: String,
    pieceRes: Int,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "card_press")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .scale(scale)
            .shadow(if (isSelected) 10.dp else 4.dp, RoundedCornerShape(16.dp), spotColor = accentColor)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        if (isSelected) accentColor.copy(alpha = 0.18f) else Color(0xFF131C2D),
                        Color(0xFF0F1726)
                    )
                )
            )
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) accentColor else Color(0xFF24344C),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Piece Avatar Frame with Accent Glow
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = pieceRes),
                        contentDescription = name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(accentColor.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = xpReward,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = GameFont,
                                color = accentColor
                            )
                        }
                    }

                    Text(
                        text = title.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        fontFamily = GameFont,
                        color = accentColor
                    )

                    Text(
                        text = description,
                        fontSize = 9.5.sp,
                        fontFamily = GameFont,
                        color = Color(0xFF94A3B8),
                        maxLines = 1
                    )
                }
            }

            // Right Select Indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor else Color(0xFF182234))
                    .border(1.dp, if (isSelected) accentColor else Color(0xFF2C3C56), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSelected) "✓" else "▶",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color(0xFF0F1726) else Color(0xFF64748B)
                )
            }
        }
    }
}
