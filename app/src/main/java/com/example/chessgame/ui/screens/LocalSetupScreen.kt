package com.example.chessgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.PieceView

/**
 * Screen 5: Local 2 Player (The Duel)
 * "Grandmaster Arena" Faceoff Setup:
 * - COMMANDER 1 (White Army - Cyan Corner)
 * - VS Divider (Crossed Swords with Energy Nexus)
 * - COMMANDER 2 (Black Army - Crimson Corner)
 * - Pass & Play single-device instructions
 * - "START DUEL" primary CTA
 */
@Composable
fun LocalSetupScreen(
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onBack: () -> Unit,
    onStartMatch: (player1Name: String, player2Name: String) -> Unit
) {
    var p1Name by remember { mutableStateOf("Player 1") }
    var p2Name by remember { mutableStateOf("Player 2") }

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
                    title = "PASS & PLAY",
                    subtitle = "TWO PLAYERS • ONE DEVICE",
                    onBack = onBack
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Duelists Area
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // COMMANDER 1: WHITE FACTION
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        isHighlighted = true,
                        highlightColor = ArenaColors.CyberCyan
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF192535))
                                            .border(1.dp, ArenaColors.CyberCyan, RoundedCornerShape(10.dp))
                                    ) {
                                        PieceView(
                                            type = PieceType.KING,
                                            color = PieceColor.WHITE,
                                            pieceTheme = pieceTheme,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "COMMANDER 1",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp,
                                            color = ArenaColors.TextPrimary
                                        )
                                        Text(
                                            text = "White Army • Moves First",
                                            fontSize = 10.sp,
                                            color = ArenaColors.CyberCyan
                                        )
                                    }
                                }

                                ArenaBadge(text = "INITIATIVE", color = ArenaColors.CyberCyan, fontSize = 8)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = p1Name,
                                onValueChange = { if (it.length <= 16) p1Name = it },
                                singleLine = true,
                                label = { Text("Codename / Name", fontSize = 11.sp, color = ArenaColors.TextSecondary) },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ArenaColors.TextPrimary,
                                    unfocusedTextColor = ArenaColors.TextPrimary,
                                    focusedBorderColor = ArenaColors.CyberCyan,
                                    unfocusedBorderColor = ArenaColors.TitaniumBorder,
                                    focusedContainerColor = Color(0xFF101722),
                                    unfocusedContainerColor = Color(0xFF0F151E)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // VS NEXUS DIVIDER
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Brush.horizontalGradient(listOf(Color.Transparent, ArenaColors.TitaniumBorder)))
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A2330))
                                .border(1.dp, ArenaColors.SolarAmber, CircleShape)
                        ) {
                            Text(
                                text = "⚔️",
                                fontSize = 16.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Brush.horizontalGradient(listOf(ArenaColors.TitaniumBorder, Color.Transparent)))
                        )
                    }

                    // COMMANDER 2: BLACK FACTION
                    ArenaCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        isHighlighted = true,
                        highlightColor = ArenaColors.CrimsonAlert
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF281820))
                                            .border(1.dp, ArenaColors.CrimsonAlert, RoundedCornerShape(10.dp))
                                    ) {
                                        PieceView(
                                            type = PieceType.KING,
                                            color = PieceColor.BLACK,
                                            pieceTheme = pieceTheme,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "COMMANDER 2",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp,
                                            color = ArenaColors.TextPrimary
                                        )
                                        Text(
                                            text = "Black Army • Second to Move",
                                            fontSize = 10.sp,
                                            color = ArenaColors.CrimsonAlert
                                        )
                                    }
                                }

                                ArenaBadge(text = "DEFENDER", color = ArenaColors.CrimsonAlert, fontSize = 8)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = p2Name,
                                onValueChange = { if (it.length <= 16) p2Name = it },
                                singleLine = true,
                                label = { Text("Codename / Name", fontSize = 11.sp, color = ArenaColors.TextSecondary) },
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = ArenaColors.TextPrimary,
                                    unfocusedTextColor = ArenaColors.TextPrimary,
                                    focusedBorderColor = ArenaColors.CrimsonAlert,
                                    unfocusedBorderColor = ArenaColors.TitaniumBorder,
                                    focusedContainerColor = Color(0xFF1C1318),
                                    unfocusedContainerColor = Color(0xFF170F14)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Tactical Notice
                    Text(
                        text = "Take turns on the same device. The board can auto-flip in Settings for face-to-face combat.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = ArenaColors.TextSecondary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Start Match CTA
                ArenaButton(
                    text = "START DUEL",
                    icon = "⚔️",
                    onClick = {
                        val n1 = p1Name.trim().ifEmpty { "Player 1" }
                        val n2 = p2Name.trim().ifEmpty { "Player 2" }
                        onStartMatch(n1, n2)
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
