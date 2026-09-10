package com.example.chessgame.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.engine.PieceColor
import com.example.chessgame.engine.PieceType
import com.example.chessgame.theme.*
import com.example.chessgame.ui.components.PieceView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalSetupScreen(
    pieceTheme: PieceTheme = ThemeRegistry.STORYBOOK_HANDCRAFTED,
    onBack: () -> Unit,
    onStartMatch: (player1Name: String, player2Name: String) -> Unit
) {
    BackHandler { onBack() }

    var p1Name by remember { mutableStateOf("Player 1") }
    var p2Name by remember { mutableStateOf("Player 2") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF231B15),
                        Color(0xFF150F0C),
                        Color(0xFF090705)
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(14.dp))
                    .padding(horizontal = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        SoundManager.playClick()
                        onBack()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Text("←", fontSize = 20.sp, fontWeight = FontWeight.Black, color = StudyParchmentCream)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "LOCAL 2 PLAYER",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "One board. Two minds.",
                        fontSize = 10.sp,
                        color = StudyAmberAccent
                    )
                }

                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pass & play notice badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x18FFFFFF))
                    .border(1.dp, Color(0x30FFFFFF), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Pass & Play on this device • Take turns on the table",
                    fontSize = 11.sp,
                    color = Color(0xFFC0B4A6)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Player 1 Card (White King)
            PlayerSetupCard(
                title = "WHITE PIECES",
                tag = "Moves First",
                pieceColor = PieceColor.WHITE,
                pieceTheme = pieceTheme,
                name = p1Name,
                onNameChange = { p1Name = it }
            )

            // VS divider badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(StudyAmberAccent)
                    .border(3.dp, Color(0xFF1B140D), CircleShape)
                    .shadow(8.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF140D05)
                )
            }

            // Player 2 Card (Black King)
            PlayerSetupCard(
                title = "BLACK PIECES",
                tag = "Moves Second",
                pieceColor = PieceColor.BLACK,
                pieceTheme = pieceTheme,
                name = p2Name,
                onNameChange = { p2Name = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom quote matching Screen 5
            Text(
                text = "“A duel of minds, face to face.”",
                fontSize = 12.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color(0xFFAFA293),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Start Match Button
            Button(
                onClick = {
                    SoundManager.playClick()
                    val p1 = if (p1Name.isBlank()) "Player 1" else p1Name.trim()
                    val p2 = if (p2Name.isBlank()) "Player 2" else p2Name.trim()
                    onStartMatch(p1, p2)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = StudyAmberAccent),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "START GAME",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color(0xFF19120A)
                )
            }
        }
    }
}

@Composable
private fun PlayerSetupCard(
    title: String,
    tag: String,
    pieceColor: PieceColor,
    pieceTheme: PieceTheme,
    name: String,
    onNameChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF281E17),
                        Color(0xFF1E1712)
                    )
                )
            )
            .border(1.dp, Color(0x38FFFFFF), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tabletop piece plinth
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF150F0B))
                    .border(1.dp, StudyAmberAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                PieceView(
                    type = PieceType.KING,
                    color = pieceColor,
                    modifier = Modifier.size(50.dp),
                    pieceTheme = pieceTheme
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyAmberAccent,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        color = Color(0xFF9E9283)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF140E0A),
                        unfocusedContainerColor = Color(0xFF140E0A),
                        focusedTextColor = StudyParchmentCream,
                        unfocusedTextColor = StudyParchmentCream,
                        cursorColor = StudyAmberAccent,
                        focusedIndicatorColor = StudyAmberAccent,
                        unfocusedIndicatorColor = Color(0x30FFFFFF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Enter player name...", fontSize = 13.sp, color = Color(0xFF6E6356))
                    }
                )
            }
        }
    }
}
