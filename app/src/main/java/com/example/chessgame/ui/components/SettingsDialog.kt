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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.*

/**
 * Physical Chess Notebook Settings Dialog.
 * Categorized into GAMEPLAY, DISPLAY, and GAME sections with tactile switches.
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
                .shadow(28.dp, RoundedCornerShape(20.dp), spotColor = Color(0xCC000000))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF231A14),
                            Color(0xFF16100C),
                            Color(0xFF0F0B08)
                        )
                    )
                )
                .border(1.5.dp, Color(0x38D4A373), RoundedCornerShape(20.dp))
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
                            text = "CHESS SETTINGS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = StudyParchmentCream
                        )
                        Text(
                            text = "Tactile Studio Preferences",
                            fontSize = 11.sp,
                            color = StudyAmberAccent
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playClick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF))
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = StudyParchmentCream)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // ==========================================
                    // 1. GAMEPLAY SECTION
                    // ==========================================
                    item {
                        SettingsSectionHeader("GAMEPLAY")
                    }

                    item {
                        TactileSwitchRow(
                            title = "Voice Announcer",
                            subtitle = "Real-time spoken check, checkmate & castle",
                            checked = voiceEnabled,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onVoiceChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Sound Effects",
                            subtitle = "Wood piece strikes, captures & brass bells",
                            checked = soundEnabled,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onSoundChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Board Coordinates",
                            subtitle = "FIDE notation labels (a-h, 1-8) along edges",
                            checked = showCoords,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onShowCoordsChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Legal Move Highlights",
                            subtitle = "Sage destination dots & terracotta capture rings",
                            checked = highlightMoves,
                            onCheckedChange = {
                                SoundManager.playClick()
                                onHighlightMovesChanged(it)
                            }
                        )
                    }

                    item {
                        TactileSwitchRow(
                            title = "Auto-Flip Board (Local 2P)",
                            subtitle = "Rotate board orientation on alternating turns",
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
                                title = "Move Suggestions & Hints",
                                subtitle = "Top 3 candidate moves & board arrows",
                                checked = showMoveHints,
                                onCheckedChange = {
                                    SoundManager.playClick()
                                    onShowMoveHintsChanged(it)
                                }
                            )
                        }
                    }

                    // ==========================================
                    // 2. DISPLAY SECTION
                    // ==========================================
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        SettingsSectionHeader("DISPLAY & THEMES")
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x18FFFFFF))
                                .border(1.dp, Color(0x28FFFFFF), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current Board: ${currentBoardTheme.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudyParchmentCream
                                    )
                                    Text(
                                        text = "Pieces: ${currentPieceTheme.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StudyAmberAccent
                                    )
                                }

                                if (onOpenThemes != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            SoundManager.playClick()
                                            onClose()
                                            onOpenThemes()
                                        },
                                        modifier = Modifier.fillMaxWidth().height(40.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "OPEN THEME STUDIO",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp,
                                            color = Color(0xFF161008)
                                        )
                                    }
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
                                text = "“A calm mind makes the best moves.”",
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color(0xFFAFA293),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Chess Master Studio • v1.0.0",
                                fontSize = 10.sp,
                                color = Color(0x60FFFFFF)
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
        color = StudyAmberAccent
    )
}

/**
 * Tactile brass/wood toggle switch.
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = StudyParchmentCream
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFFAAA095),
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
                            listOf(Color(0xFFD4A373), Color(0xFFB07D48))
                        )
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2A2019), Color(0xFF1E1712))
                        )
                    }
                )
                .border(1.dp, if (checked) Color(0xFFF1C79B) else Color(0x30FFFFFF), RoundedCornerShape(13.dp))
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
                        if (checked) Color(0xFF1E140B) else Color(0xFFDCD4CB)
                    )
            )
        }
    }
}
