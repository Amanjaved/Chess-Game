package com.example.chessgame.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chessgame.ai.MoveClassification
import com.example.chessgame.ai.MoveSuggestion
import com.example.chessgame.ai.hintBadgeColor
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.StudyAmberAccent
import com.example.chessgame.theme.StudyParchmentCream

/**
 * MoveSuggestionSection:
 * Coordinates the hints calculation state, candidate move cards, and tactical explanation card.
 */
@Composable
fun MoveSuggestionSection(
    suggestions: List<MoveSuggestion>,
    selectedSuggestion: MoveSuggestion?,
    isCalculating: Boolean,
    pieceTheme: PieceTheme,
    onSelectSuggestion: (MoveSuggestion?) -> Unit,
    onCloseHints: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF231A14),
                        Color(0xFF18120E)
                    )
                )
            )
            .border(1.2.dp, Color(0x35D4A373), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💡",
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RECOMMENDED MOVES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = StudyAmberAccent
                )
            }

            IconButton(
                onClick = {
                    SoundManager.playClick()
                    onCloseHints()
                },
                modifier = Modifier.size(28.dp)
            ) {
                Text(
                    text = "✕",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAFA293)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isCalculating) {
            HintsCalculatingView()
        } else if (suggestions.isNotEmpty()) {
            // Horizontally aligned / scrollable suggestion cards
            MoveSuggestionRow(
                suggestions = suggestions,
                selectedSuggestion = selectedSuggestion,
                pieceTheme = pieceTheme,
                onSelectSuggestion = onSelectSuggestion
            )

            // Explanation card for selected suggestion
            AnimatedVisibility(
                visible = selectedSuggestion != null,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)),
                exit = fadeOut(tween(150)) + slideOutVertically(tween(150))
            ) {
                if (selectedSuggestion != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MoveExplanationCard(
                        suggestion = selectedSuggestion,
                        onClearSelection = { onSelectSuggestion(null) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No legal suggestions available in this position.",
                    fontSize = 12.sp,
                    color = Color(0xFFAFA293)
                )
            }
        }
    }
}

/**
 * Loading state when engine calculates candidate move scores.
 */
@Composable
fun HintsCalculatingView(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x18000000))
            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.5.dp,
            color = StudyAmberAccent
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Finding the strongest moves...",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = StudyParchmentCream
        )
    }
}

/**
 * 3 Horizontally scrollable suggestion cards.
 */
@Composable
fun MoveSuggestionRow(
    suggestions: List<MoveSuggestion>,
    selectedSuggestion: MoveSuggestion?,
    pieceTheme: PieceTheme,
    onSelectSuggestion: (MoveSuggestion?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        suggestions.forEach { suggestion ->
            val isSelected = selectedSuggestion?.move == suggestion.move
            MoveSuggestionCard(
                suggestion = suggestion,
                isSelected = isSelected,
                pieceTheme = pieceTheme,
                onClick = {
                    SoundManager.playClick()
                    if (isSelected) {
                        onSelectSuggestion(null)
                    } else {
                        onSelectSuggestion(suggestion)
                    }
                }
            )
        }
    }
}

/**
 * Single candidate move card matching the app's tactile walnut & gold identity.
 */
@Composable
fun MoveSuggestionCard(
    suggestion: MoveSuggestion,
    isSelected: Boolean,
    pieceTheme: PieceTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeBg = suggestion.classification.hintBadgeColor
    val badgeTextColor = if (suggestion.classification == MoveClassification.BEST) {
        Color(0xFF1A120B)
    } else {
        Color(0xFF140E0A)
    }

    val a11yDescription = "Best move recommendation ${suggestion.rank}: ${suggestion.classification.label}, " +
            "${suggestion.piece.name.lowercase()} from ${suggestion.fromAlgebraic} to ${suggestion.toAlgebraic}."

    Box(
        modifier = modifier
            .width(112.dp)
            .height(86.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(12.dp), spotColor = Color(0x66DFB36E))
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF382718),
                            Color(0xFF271A10)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1F1712),
                            Color(0xFF150F0B)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x35D4A373),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .semantics { contentDescription = a11yDescription }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Classification Badge + Piece Thumbnail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Classification Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeBg)
                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                ) {
                    Text(
                        text = suggestion.classification.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeTextColor
                    )
                }

                // Small piece thumbnail
                Box(modifier = Modifier.size(20.dp)) {
                    PieceView(
                        type = suggestion.piece,
                        color = suggestion.color,
                        pieceTheme = pieceTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Middle: Move Notation (e.g. g1 → f3 or SAN)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = suggestion.fromAlgebraic,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyParchmentCream
                )
                Text(
                    text = " → ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyAmberAccent
                )
                Text(
                    text = suggestion.toAlgebraic,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyParchmentCream
                )
            }

            // Bottom: Centipawn Evaluation
            val evalText = if (suggestion.evaluation >= 900.0) {
                "+M1"
            } else if (suggestion.evaluation <= -900.0) {
                "-M1"
            } else {
                val prefix = if (suggestion.evaluation > 0) "+" else ""
                "$prefix${String.format(java.util.Locale.US, "%.2f", suggestion.evaluation)}"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${suggestion.rank}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8C7E70)
                )
                Text(
                    text = evalText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (suggestion.evaluation >= 0) Color(0xFFDFB36E) else Color(0xFFE57373)
                )
            }
        }
    }
}

/**
 * MoveExplanationCard:
 * Tactical breakdown shown when a player taps any candidate move.
 */
@Composable
fun MoveExplanationCard(
    suggestion: MoveSuggestion,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF140D09))
            .border(1.dp, Color(0x40DFB36E), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        fontSize = 12.sp,
                        color = StudyAmberAccent
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${suggestion.classification.label}: ${suggestion.fromAlgebraic} → ${suggestion.toAlgebraic}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = StudyParchmentCream
                    )
                    if (suggestion.san.isNotEmpty()) {
                        Text(
                            text = " (${suggestion.san})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = StudyAmberAccent
                        )
                    }
                }

                // Clear Preview text button
                Text(
                    text = "Clear Preview",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFAFA293),
                    modifier = Modifier
                        .clickable { onClearSelection() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Rationale explanation text
            Text(
                text = suggestion.explanation,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.sp,
                color = Color(0xFFEDE0D0),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preview only • Make your move on the board",
                    fontSize = 10.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color(0xFF8C7E70)
                )

                val evalText = if (suggestion.evaluation >= 900.0) "+Mate"
                else if (suggestion.evaluation <= -900.0) "-Mate"
                else "${if (suggestion.evaluation > 0) "+" else ""}${String.format(java.util.Locale.US, "%.2f", suggestion.evaluation)}"

                Text(
                    text = "Score: $evalText",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyAmberAccent
                )
            }
        }
    }
}
