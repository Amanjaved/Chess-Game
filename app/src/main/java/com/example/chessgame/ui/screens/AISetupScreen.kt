package com.example.chessgame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.R
import com.example.chessgame.ai.AIDifficulty
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Faction / side selection choice for game setup.
 */
enum class PlayerColorChoice {
    WHITE,
    RANDOM,
    BLACK
}

/**
 * Opponent Dossier definition for AI difficulty setup.
 */
data class OpponentDossier(
    val difficulty: AIDifficulty,
    val name: String,
    val title: String,
    val style: String,
    val rating: String,
    val stars: String,
    val description: String,
    val imageRes: Int,
    val themeColor: Color
)

/**
 * Screen 3: AI Opponent Dossier Selection
 * "Grandmaster Arena" Opponent Showcase:
 * - 4 Rich illustrated opponent character dossiers:
 *   - Cadet Valen (Recruit / Easy)
 *   - Strategist Lyra (Tactician / Medium)
 *   - Commander Voron (Warmaster / Hard)
 *   - Oracle Kairos (Grandmaster / Expert)
 * - Tactical battle attributes, playstyles, and ELO rating estimates
 * - "ENGAGE OPPONENT" primary CTA
 * - Safe area handling (WindowInsets.systemBars)
 */
@Composable
fun AISetupScreen(
    onBack: () -> Unit,
    onSelectDifficulty: (AIDifficulty) -> Unit,
    onStartGame: ((AIDifficulty, PlayerColorChoice) -> Unit)? = null
) {
    val opponents = remember {
        listOf(
            OpponentDossier(
                difficulty = AIDifficulty.EASY,
                name = "Novice Alex",
                title = "BEGINNER",
                style = "Casual • Developing • Direct",
                rating = "800 ELO",
                stars = "★☆☆☆",
                description = "Practicing opening principles and basic tactics. Great for warm-ups and casual games.",
                imageRes = R.drawable.avatar_opponent_recruit,
                themeColor = ArenaColors.EmeraldVictory
            ),
            OpponentDossier(
                difficulty = AIDifficulty.MEDIUM,
                name = "Club Player Maya",
                title = "INTERMEDIATE",
                style = "Tactical • Alert • Active",
                rating = "1350 ELO",
                stars = "★★☆☆",
                description = "Sharp club competitor. Controls open files, creates tactical pins, and punishes mistakes.",
                imageRes = R.drawable.avatar_opponent_tactician,
                themeColor = ArenaColors.RoyalGold
            ),
            OpponentDossier(
                difficulty = AIDifficulty.HARD,
                name = "Master Viktor",
                title = "ADVANCED",
                style = "Strategic • Positional • Precise",
                rating = "1850 ELO",
                stars = "★★★☆",
                description = "Tournament veteran. Calculates deep piece trades, king safety, and sharp pawn structures.",
                imageRes = R.drawable.avatar_opponent_warmaster,
                themeColor = ArenaColors.SolarAmber
            ),
            OpponentDossier(
                difficulty = AIDifficulty.EXPERT,
                name = "Grandmaster Anton",
                title = "GRANDMASTER",
                style = "Flawless • Deep Calculation",
                rating = "2400 ELO",
                stars = "★★★★",
                description = "World championship caliber AI searching multiple plies with supreme tactical accuracy.",
                imageRes = R.drawable.avatar_opponent_grandmaster,
                themeColor = ArenaColors.CrimsonAlert
            )
        )
    }

    var selectedDifficulty by remember { mutableStateOf(AIDifficulty.MEDIUM) }

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
                    title = "CHOOSE OPPONENT",
                    subtitle = "COMPUTER DIFFICULTY",
                    onBack = onBack
                )


                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Opponent Dossiers List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    opponents.forEach { dossier ->
                        val isSelected = dossier.difficulty == selectedDifficulty

                        ArenaCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            isHighlighted = isSelected,
                            highlightColor = dossier.themeColor,
                            onClick = {
                                selectedDifficulty = dossier.difficulty
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Character Portrait with Glowing Tier Border
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(76.dp)
                                        .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(14.dp), spotColor = dossier.themeColor)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF0F1621))
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) dossier.themeColor else ArenaColors.TitaniumBorder,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = dossier.imageRes),
                                        contentDescription = dossier.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Opponent Dossier Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = dossier.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = ArenaColors.TextPrimary
                                        )

                                        ArenaBadge(
                                            text = dossier.stars,
                                            color = dossier.themeColor,
                                            fontSize = 9
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = dossier.title,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.8.sp,
                                            color = dossier.themeColor
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${dossier.rating}",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ArenaColors.TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = dossier.description,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.5.sp,
                                        color = Color(0xFFC0CAD6),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Deploy CTA
                val selectedDossier = opponents.first { it.difficulty == selectedDifficulty }
                ArenaButton(
                    text = "CHALLENGE ${selectedDossier.name.uppercase()}",
                    icon = "⚔️",
                    onClick = {
                        onSelectDifficulty(selectedDifficulty)
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
