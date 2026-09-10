package com.example.chessgame.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.*

/**
 * Grandmaster Arena System Settings Dialog.
 * Categorized into COMBAT AUDIO, TACTICAL DISPLAY, and LOADOUT PREFERENCES.
 */
@Composable
fun SettingsDialog(
    soundEnabled: Boolean,
    onSoundChanged: (Boolean) -> Unit,
    voiceEnabled: Boolean,
    onVoiceChanged: (Boolean) -> Unit,
    showCoords: Boolean,
    onShowCoordsChanged: (Boolean) -> Unit,
    highlightMoves: Boolean,
    onHighlightMovesChanged: (Boolean) -> Unit,
    autoFlipLocal: Boolean,
    onAutoFlipChanged: (Boolean) -> Unit,
    showMoveHints: Boolean = true,
    onShowMoveHintsChanged: ((Boolean) -> Unit)? = null,
    currentBoardTheme: BoardTheme,
    currentPieceTheme: PieceTheme,
    onOpenThemes: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 580.dp)
                .shadow(32.dp, RoundedCornerShape(22.dp), spotColor = Color(0xDD000000))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF141A25),
                            Color(0xFF0C1018),
                            Color(0xFF070A0F)
                        )
                    )
                )
                .border(1.5.dp, ArenaColors.TitaniumBorder, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SYSTEM CONFIG",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Serif,
                            color = ArenaColors.CyberCyan
                        )
                        Text(
                            text = "Arena Telemetry & Audio Parameters",
                            fontSize = 11.sp,
                            color = ArenaColors.TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Text("✕", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ArenaColors.TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // ==========================================
                    // 1. AUDIO & TELEMETRY SECTION
                    // ==========================================
                    item {
                        SettingsSectionHeader("AUDIO & VOICE")
                    }

                    item {
                        TactileSwitchRow(
                            title = "Voice Announcer",
                            subtitle = "Spoken combat calls: check, checkmate, castling & promotion",
                            checked = voiceEnabled,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onVoiceChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Combat Sound Effects",
                            subtitle = "Tactile piece impact, captures & check warnings",
                            checked = soundEnabled,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onSoundChanged(it)
                            }
                        )
                    }

                    // ==========================================
                    // 2. TACTICAL DISPLAY
                    // ==========================================
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        SettingsSectionHeader("ARENA DISPLAY & HUD")
                    }

                    item {
                        TactileSwitchRow(
                            title = "Arena Coordinates",
                            subtitle = "FIDE grid labels (a-h, 1-8) along perimeter",
                            checked = showCoords,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onShowCoordsChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Legal Vector Highlights",
                            subtitle = "Dynamic cyan deployment dots & capture rings",
                            checked = highlightMoves,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onHighlightMovesChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Auto-Flip Orientation (Local 2P)",
                            subtitle = "Invert camera perspective on alternating commander turns",
                            checked = autoFlipLocal,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onAutoFlipChanged(it)
                            }
                        )
                    }

                    if (onShowMoveHintsChanged != null) {
                        item {
                            TactileSwitchRow(
                                title = "Move Hints & Engine Advice",
                                subtitle = "Top 3 candidate tactical moves & telemetry arrow preview",
                                checked = showMoveHints,
                                onCheckedChange = {
                                    SoundManager.playClick()
                                    onShowMoveHintsChanged(it)
                                }
                            )
                        }
                    }

                    // ==========================================
                    // 3. LOADOUT & ARMORY
                    // ==========================================
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        SettingsSectionHeader("ARMORY LOADOUT")
                    }

                    item {
                        ArenaCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Board: ${currentBoardTheme.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.TextPrimary
                                    )
                                    Text(
                                        text = "Pieces: ${currentPieceTheme.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ArenaColors.CyberCyan
                                    )
                                }

                                if (onOpenThemes != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    ArenaButton(
                                        text = "LAUNCH THE ARMORY",
                                        icon = "🛡️",
                                        isPrimary = true,
                                        onClick = {
                                            SoundManager.playClick()
                                            onClose()
                                            onOpenThemes()
                                        },
                                        modifier = Modifier.fillMaxWidth().height(42.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Grandmaster Arena • Combat Engine v2.0",
                                fontSize = 10.sp,
                                color = ArenaColors.TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.5.sp,
        color = ArenaColors.CyberCyan
    )
}

/**
 * Tactical cyber toggle switch.
 */
@Composable
private fun TactileSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 2.dp,
        animationSpec = tween(180),
        label = "thumb"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = ArenaColors.TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = ArenaColors.TextSecondary,
                lineHeight = 14.sp
            )
        }

        // Tactile Switch track and thumb
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    if (checked) {
                        Brush.horizontalGradient(
                            listOf(ArenaColors.CyberCyan, Color(0xFF0098A6))
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E2838), Color(0xFF151C27))
                        )
                    }
                )
                .border(1.dp, if (checked) ArenaColors.CyberCyan else ArenaColors.TitaniumBorder, RoundedCornerShape(13.dp))
                .padding(2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(20.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        if (checked) Color(0xFF05080E) else Color(0xFF90A4AE)
                    )
            )
        }
    }
}
