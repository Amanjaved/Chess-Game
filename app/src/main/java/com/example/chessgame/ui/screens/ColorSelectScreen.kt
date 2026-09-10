package com.example.chessgame.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.PieceView

/**
 * Screen 4: Faction Deployment (Side Selection)
 * "Grandmaster Arena" Tactical Faction Choice:
 * - WHITE LEGION (First Strike Initiative)
 * - BLACK DYNASTY (Counter-Attack Strategy)
 * - RANDOM FATE (Balanced + Bonus XP)
 * - Floating piece presentation with active energy glow
 * - Deploy to Battle primary action
 */
@Composable
fun ColorSelectScreen(
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onBack: () -> Unit,
    onStartGame: (PlayerColorChoice) -> Unit
) {
    var selectedChoice by remember { mutableStateOf(PlayerColorChoice.WHITE) }

    ArenaBackgroundScaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                ArenaHeader(
                    title = "CHOOSE YOUR COLOR",
                    subtitle = "PLAY AS WHITE OR BLACK",
                    onBack = onBack
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Factions List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Choice 1: WHITE PIECES
                    val isWhiteSelected = selectedChoice == PlayerColorChoice.WHITE
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        isHighlighted = isWhiteSelected,
                        highlightColor = ArenaColors.RoyalGold,
                        onClick = {
                            selectedChoice = PlayerColorChoice.WHITE
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Floating White King Piece Icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(
                                        elevation = if (isWhiteSelected) 10.dp else 2.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        spotColor = ArenaColors.RoyalGold
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1B2838))
                                    .border(
                                        width = if (isWhiteSelected) 1.8.dp else 1.dp,
                                        color = if (isWhiteSelected) ArenaColors.RoyalGold else ArenaColors.TitaniumBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                PieceView(
                                    type = PieceType.KING,
                                    color = PieceColor.WHITE,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "WHITE PIECES",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = ArenaColors.TextPrimary
                                    )
                                    ArenaBadge(
                                        text = "MOVES FIRST",
                                        color = ArenaColors.RoyalGold,
                                        fontSize = 8
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Command the white pieces. Seize the opening tempo and lead the initial attack.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = ArenaColors.TextSecondary
                                )
                            }
                        }
                    }

                    // Choice 2: BLACK PIECES
                    val isBlackSelected = selectedChoice == PlayerColorChoice.BLACK
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        isHighlighted = isBlackSelected,
                        highlightColor = ArenaColors.SolarAmber,
                        onClick = {
                            selectedChoice = PlayerColorChoice.BLACK
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Floating Black King Piece Icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(
                                        elevation = if (isBlackSelected) 10.dp else 2.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        spotColor = ArenaColors.SolarAmber
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF1B1D28))
                                    .border(
                                        width = if (isBlackSelected) 1.8.dp else 1.dp,
                                        color = if (isBlackSelected) ArenaColors.SolarAmber else ArenaColors.TitaniumBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                PieceView(
                                    type = PieceType.KING,
                                    color = PieceColor.BLACK,
                                    pieceTheme = pieceTheme,
                                    modifier = Modifier.size(52.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "BLACK PIECES",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = ArenaColors.TextPrimary
                                    )
                                    ArenaBadge(
                                        text = "MOVES SECOND",
                                        color = ArenaColors.SolarAmber,
                                        fontSize = 8
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Command the black pieces. Play solid defenses and launch sharp counterattacks.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = ArenaColors.TextSecondary
                                )
                            }
                        }
                    }

                    // Choice 3: RANDOM
                    val isRandomSelected = selectedChoice == PlayerColorChoice.RANDOM
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        isHighlighted = isRandomSelected,
                        highlightColor = ArenaColors.EmeraldVictory,
                        onClick = {
                            selectedChoice = PlayerColorChoice.RANDOM
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Random Dice / Scales Icon
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .shadow(
                                        elevation = if (isRandomSelected) 10.dp else 2.dp,
                                        shape = RoundedCornerShape(14.dp),
                                        spotColor = ArenaColors.EmeraldVictory
                                    )
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF14241F))
                                    .border(
                                        width = if (isRandomSelected) 1.8.dp else 1.dp,
                                        color = if (isRandomSelected) ArenaColors.EmeraldVictory else ArenaColors.TitaniumBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                Text(
                                    text = "🎲",
                                    fontSize = 32.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "RANDOM COLOR",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = ArenaColors.TextPrimary
                                    )
                                    ArenaBadge(
                                        text = "+50 BONUS XP",
                                        color = ArenaColors.EmeraldVictory,
                                        fontSize = 8
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Color is determined randomly at match start. Sharpen your skills from either perspective.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = ArenaColors.TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Start Game Button
                ArenaButton(
                    text = "START CHESS MATCH",
                    icon = "▶",
                    onClick = {
                        onStartGame(selectedChoice)
                    },
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

            }
        }
    }
}
