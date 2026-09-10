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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.example.chessgame.audio.SoundManager
import com.example.chessgame.theme.PieceTheme
import com.example.chessgame.theme.StudyAmberAccent
import com.example.chessgame.theme.StudyParchmentCream

/**
 * MoveSuggestionSection:
 * Matches the reference design with header, 3 candidate move cards,
 * and a selected move tactical breakdown with engine evaluation score.
 */
@Composable
fun MoveSuggestionSection(
    suggestions: List<MoveSuggestion>,
    selectedSuggestion: MoveSuggestion?,
    isCalculating: Boolean,
    pieceTheme: PieceTheme,
    onSelectSuggestion: (MoveSuggestion?) -> Unit,
    onToggleHints: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xF018120E),
                        Color(0xF8120C08)
                    )
                )
            )
            .border(1.dp, Color(0x30DFB36E), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Section Header matching reference
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Lightbulb + Title + Subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💡",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Best Moves",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "Tap a move to see it on the board",
                        fontSize = 10.5.sp,
                        color = Color(0xFFAFA293)
                    )
                }
            }

            // Right: AI toggle switch with label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onToggleHints() }
            ) {
                Text(
                    text = "✦ Get hints powered by AI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudyAmberAccent
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = suggestions.isNotEmpty() || isCalculating,
                    onCheckedChange = { onToggleHints() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF140D09),
                        checkedTrackColor = StudyAmberAccent,
                        uncheckedThumbColor = Color(0xFF8C7E70),
                        uncheckedTrackColor = Color(0x35FFFFFF)
                    ),
                    modifier = Modifier.scale(0.72f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isCalculating) {
            HintsCalculatingView()
        } else if (suggestions.isNotEmpty()) {
            // Horizontally aligned 3 suggestion cards
            MoveSuggestionRow(
                suggestions = suggestions,
                selectedSuggestion = selectedSuggestion,
                pieceTheme = pieceTheme,
                onSelectSuggestion = onSelectSuggestion
            )

            // Selected Move Detail Panel (matches bottom card in reference)
            val activeDetail = selectedSuggestion ?: suggestions.firstOrNull()
            AnimatedVisibility(
                visible = activeDetail != null,
                enter = fadeIn(tween(200)) + slideInVertically(tween(200)),
                exit = fadeOut(tween(150)) + slideOutVertically(tween(150))
            ) {
                if (activeDetail != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MoveExplanationCard(
                        suggestion = activeDetail,
                        pieceTheme = pieceTheme,
                        onClearSelection = { onSelectSuggestion(null) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap Hint to generate the top 3 moves with AI analysis.",
                    fontSize = 11.sp,
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
            .height(68.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22000000))
            .border(1.dp, Color(0x25DFB36E), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = StudyAmberAccent
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Analyzing board position with AI...",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = StudyParchmentCream
        )
    }
}

/**
 * 3 Horizontally scrollable / aligned suggestion cards matching the reference image.
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
 * Single candidate move card matching reference image:
 * Top: Piece Avatar + Notation
 * Middle: Pill Badge (BEST MOVE / STRONG / GOOD)
 * Bottom: 2-line tactical summary
 */
@Composable
fun MoveSuggestionCard(
    suggestion: MoveSuggestion,
    isSelected: Boolean,
    pieceTheme: PieceTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val a11yDescription = "Best move recommendation ${suggestion.rank}: ${suggestion.classification.label}, " +
            "${suggestion.piece.name.lowercase()} from ${suggestion.fromAlgebraic} to ${suggestion.toAlgebraic}."

    val badgeColor = when (suggestion.classification) {
        MoveClassification.BEST -> StudyAmberAccent
        MoveClassification.STRONG -> Color(0xFFEDE0D0)
        else -> Color(0xFFAFA293)
    }

    Box(
        modifier = modifier
            .width(112.dp)
            .height(118.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(12.dp), spotColor = Color(0x66DFB36E))
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2E2015),
                            Color(0xFF1F140D)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1E1611),
                            Color(0xFF140D09)
                        )
                    )
                }
            )
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) StudyAmberAccent else Color(0x30DFB36E),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .semantics { contentDescription = a11yDescription }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Piece Icon + Notation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp)) {
                    PieceView(
                        type = suggestion.piece,
                        color = suggestion.color,
                        pieceTheme = pieceTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = suggestion.notation,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudyParchmentCream
                )
            }

            // Classification Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.18f))
                    .border(0.8.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = suggestion.classification.label,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = badgeColor
                )
            }

            // Short Description (2 lines)
            Text(
                text = suggestion.explanation,
                fontSize = 9.5.sp,
                lineHeight = 12.5.sp,
                color = Color(0xFFC7BBAE),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * MoveExplanationCard:
 * Matches the selected move detail card at the bottom of the reference image:
 * Left: Piece thumbnail/watermark
 * Middle: ★ BEST MOVE + Notation + full rationale
 * Right: Evaluation score pill (+0.42 / Evaluation)
 */
@Composable
fun MoveExplanationCard(
    suggestion: MoveSuggestion,
    pieceTheme: PieceTheme,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xF0120D09))
            .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Piece Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x33000000))
                    .border(0.8.dp, Color(0x25DFB36E), RoundedCornerShape(8.dp))
            ) {
                PieceView(
                    type = suggestion.piece,
                    color = suggestion.color,
                    pieceTheme = pieceTheme,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Middle: Classification, Notation & Tactical explanation
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${suggestion.classification.label}",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = StudyAmberAccent
                    )
                }

                Text(
                    text = suggestion.notation,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = StudyParchmentCream
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = suggestion.explanation,
                    fontSize = 10.5.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFC7BBAE),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Centipawn Evaluation Badge (+0.42 / Evaluation)
            val evalText = if (suggestion.evaluation >= 900.0) {
                "+Mate"
            } else if (suggestion.evaluation <= -900.0) {
                "-Mate"
            } else {
                val prefix = if (suggestion.evaluation > 0) "+" else ""
                "$prefix${String.format(java.util.Locale.US, "%.2f", suggestion.evaluation)}"
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x22DFB36E))
                    .border(1.dp, Color(0x35DFB36E), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = evalText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = StudyParchmentCream
                    )
                    Text(
                        text = "Evaluation",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFAFA293)
                    )
                }
            }
        }
    }
}
