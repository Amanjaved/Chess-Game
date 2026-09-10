package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.*

/**
 * Parchment Chess Notebook Pause Menu Overlay.
 */
@Composable
fun GameMenuOverlay(
    onResume: () -> Unit,
    onNewGame: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    onOpenHistory: (() -> Unit)? = null,
    onMainMenu: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(28.dp, RoundedCornerShape(20.dp), spotColor = Color(0xCC000000))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF131C2D), Color(0xFF0C1320))
                    )
                )
                .border(1.5.dp, Color(0xFF26354E), RoundedCornerShape(20.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header with Title and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PAUSED",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp,
                            fontFamily = GameFont,
                            color = Color(0xFFF8FAFC)
                        )
                        Text(
                            text = "Match options & tactical preferences",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    androidx.compose.material3.IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onResume()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0xFF1C273B))
                    ) {
                        Text("✕", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF8FAFC))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val items = mutableListOf(
                    Triple("RESUME GAME", "Return to the board") { onResume() },
                    Triple("RESTART MATCH", "Reset to opening position") { onNewGame() }
                )
                if (onOpenHistory != null) {
                    items.add(Triple("MOVE HISTORY", "Inspect recorded moves") { onOpenHistory() })
                }
                items.addAll(
                    listOf(
                        Triple("THEMES", "Change board & pieces") { onOpenThemes() },
                        Triple("HOW TO PLAY", "Chess rules & tactics") { onOpenHowToPlay() },
                        Triple("SETTINGS", "Audio & gameplay options") { onOpenSettings() },
                        Triple("MAIN MENU", "Leave current match") { onMainMenu() }
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items.forEach { (title, subtitle, action) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF162133))
                                .border(1.dp, Color(0xFF26354E), RoundedCornerShape(12.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    action()
                                }
                                .padding(horizontal = 14.dp, vertical = 11.dp)
                        ) {
                            Column {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = GameFont,
                                    color = Color(0xFFF8FAFC)
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Text(text = "→", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFB703))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom quote
                Text(
                    text = "“Every chess master was once a beginner.”",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFF64748B),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
