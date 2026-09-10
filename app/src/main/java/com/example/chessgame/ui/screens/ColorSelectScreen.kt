package com.example.chessgame.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.PieceView

@Composable
fun ColorSelectScreen(
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onBack: () -> Unit,
    onStartGame: (PlayerColorChoice) -> Unit
) {
    var selectedChoice by remember { mutableStateOf(PlayerColorChoice.WHITE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF241B15),
                        Color(0xFF140F0C),
                        Color(0xFF080605)
                    ),
                    radius = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header (Visually Centered)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CHOOSE YOUR SIDE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "White moves first, Black responds",
                        fontSize = 11.sp,
                        color = StudyAmberAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Selection Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Side-by-Side White and Black King Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // White Selection Card
                    Box(modifier = Modifier.weight(1f)) {
                        SideSquareCard(
                            title = "White",
                            subtitle = "Play as White",
                            choice = PlayerColorChoice.WHITE,
                            isSelected = selectedChoice == PlayerColorChoice.WHITE,
                            pieceColor = PieceColor.WHITE,
                            pieceTheme = pieceTheme,
                            onClick = {
                                selectedChoice = PlayerColorChoice.WHITE
                                SoundManager.playClick()
                            }
                        )
                    }

                    // Black Selection Card
                    Box(modifier = Modifier.weight(1f)) {
                        SideSquareCard(
                            title = "Black",
                            subtitle = "Play as Black",
                            choice = PlayerColorChoice.BLACK,
                            isSelected = selectedChoice == PlayerColorChoice.BLACK,
                            pieceColor = PieceColor.BLACK,
                            pieceTheme = pieceTheme,
                            onClick = {
                                selectedChoice = PlayerColorChoice.BLACK
                                SoundManager.playClick()
                            }
                        )
                    }
                }

                // Wide Random Card
                WideRandomCard(
                    title = "Random",
                    subtitle = "A surprise each game",
                    isSelected = selectedChoice == PlayerColorChoice.RANDOM,
                    pieceTheme = pieceTheme,
                    onClick = {
                        selectedChoice = PlayerColorChoice.RANDOM
                        SoundManager.playClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom quote matching Screen 4
            Text(
                text = "“The board is neutral until the first move.”",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFFAFA293),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Start Game CTA
            Button(
                onClick = {
                    SoundManager.playClick()
                    onStartGame(selectedChoice)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "CONTINUE →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF19120A)
                )
            }
        }
    }
}

@Composable
private fun SideSquareCard(
    title: String,
    subtitle: String,
    choice: PlayerColorChoice,
    isSelected: Boolean,
    pieceColor: PieceColor,
    pieceTheme: PieceTheme,
    onClick: () -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 14.dp else 2.dp,
        animationSpec = tween(220),
        label = "elev"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = tween(220),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .shadow(animatedElevation, RoundedCornerShape(16.dp), spotColor = if (isSelected) StudyAmberAccent else Color.Black)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(listOf(Color(0xFF3B291D), Color(0xFF241810)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF221710), Color(0xFF160E0A)))
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x25FFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 22.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (pieceColor == PieceColor.WHITE) Color(0xFFF7F3EB) else Color(0xFF281C14)
                    )
                    .border(
                        1.dp,
                        if (isSelected) StudyAmberAccent else Color(0x30FFFFFF),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                PieceView(
                    type = PieceType.KING,
                    color = pieceColor,
                    modifier = Modifier.size(56.dp),
                    pieceTheme = pieceTheme
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) StudyAmberAccent else StudyParchmentCream
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFFAFA293)
            )
        }
    }
}

@Composable
private fun WideRandomCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    pieceTheme: PieceTheme,
    onClick: () -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 14.dp else 2.dp,
        animationSpec = tween(220),
        label = "elev_r"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(animatedElevation, RoundedCornerShape(16.dp), spotColor = if (isSelected) StudyAmberAccent else Color.Black)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(listOf(Color(0xFF3B291D), Color(0xFF241810)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xFF221710), Color(0xFF160E0A)))
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x25FFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E2016))
                        .border(1.dp, if (isSelected) StudyAmberAccent else Color(0x25FFFFFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎲", fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) StudyAmberAccent else StudyParchmentCream
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = Color(0xFFAFA293)
                    )
                }
            }

            // Radio indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isSelected) StudyAmberAccent else Color(0x40FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(StudyAmberAccent)
                    )
                }
            }
        }
    }
}


