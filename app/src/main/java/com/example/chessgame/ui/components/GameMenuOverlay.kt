package com.example.chessgame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.*

/**
 * Tactical Combat Pause Menu Overlay.
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
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0xDD000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF141924),
                            Color(0xFF0C1018),
                            Color(0xFF080B11)
                        )
                    )
                )
                .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
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
                            text = "TACTICAL PAUSE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.5.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "Combat Operations & Parameters",
                            fontSize = 11.sp,
                            color = ArenaColors.TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onResume()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Text("✕", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val items = mutableListOf(
                    Triple("RESUME OPERATIONS", "Return immediately to the active board") { onResume() },
                    Triple("RESTART MATCH", "Reset forces to opening configuration") { onNewGame() }
                )
                if (onOpenHistory != null) {
                    items.add(Triple("COMBAT LOG (HISTORY)", "Inspect all recorded turn notations") { onOpenHistory() })
                }
                items.addAll(
                    listOf(
                        Triple("THE ARMORY (THEMES)", "Equip custom boards and piece sets") { onOpenThemes() },
                        Triple("TACTICAL MANUAL", "Chess doctrines, mechanics and rules") { onOpenHowToPlay() },
                        Triple("SYSTEM CONFIG", "Sound, voice announcer and visual insets") { onOpenSettings() },
                        Triple("COMMAND DOCK", "Conclude engagement and return to lobby") { onMainMenu() }
                    )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { (title, subtitle, action) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ArenaColors.TitaniumSurface)
                                .border(1.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    SoundManager.playClick()
                                    action()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = if (title.contains("RESUME")) ArenaColors.CyberCyan else ArenaColors.TextPrimary
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 10.sp,
                                        color = ArenaColors.TextMuted
                                    )
                                }
                                Text("›", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
