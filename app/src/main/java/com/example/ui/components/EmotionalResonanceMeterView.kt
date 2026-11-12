package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.BodyLanguageState
import com.example.ai.FacialExpression
import com.example.ai.VoiceTone
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.GeminiTextTertiary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.VoiceLockViewModel

/**
 * EmotionalResonanceMeterView:
 * Displays real-time cross-modal emotional resonance (Voice Tone + Face + Body Language)
 * to ensure genuine empathy, heartfelt warmth, and brotherly attunement.
 */
@Composable
fun EmotionalResonanceMeterView(
    viewModel: VoiceLockViewModel,
    modifier: Modifier = Modifier
) {
    val isResonanceActive by viewModel.isCrossModalResonanceActive.collectAsState()
    val profile by viewModel.activeResonanceProfile.collectAsState()
    val manualFace by viewModel.simulatedManualFace.collectAsState()
    val manualBody by viewModel.simulatedManualBody.collectAsState()

    var isDetailsExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(NeonPink, NeonPurple, NeonCyan))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("emotional_resonance_meter_view")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Icon, Title, and Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPink, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "क्रॉस‑मोडल इमोशनल रेसोनेंस",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonPink.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${profile.resonanceScorePercent}% रेसोनेंस",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPink
                                )
                            }
                        }
                        Text(
                            text = "आवाज़, चेहरे के हाव-भाव व बॉडी लैंग्वेज की गहरी समझ",
                            fontSize = 10.sp,
                            color = GeminiTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isResonanceActive,
                    onCheckedChange = { viewModel.toggleCrossModalResonance(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonPink,
                        uncheckedTrackColor = CyberSurfaceElevated
                    ),
                    modifier = Modifier.testTag("toggle_resonance_switch")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Cross-Modal Modalities Indicator Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Modality 1: Voice Tone
                ModalityCard(
                    icon = profile.voiceTone.indicatorEmoji,
                    labelHindi = "आवाज़ का लहजा",
                    valueHindi = profile.voiceTone.titleHindi,
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                )

                // Modality 2: Facial Expression
                ModalityCard(
                    icon = profile.facialExpression.indicatorEmoji,
                    labelHindi = "चेहरे के हाव-भाव",
                    valueHindi = profile.facialExpression.titleHindi,
                    color = NeonPink,
                    modifier = Modifier.weight(1f)
                )

                // Modality 3: Body Language
                ModalityCard(
                    icon = profile.bodyLanguage.indicatorEmoji,
                    labelHindi = "बॉडी लैंग्वेज",
                    valueHindi = profile.bodyLanguage.titleHindi,
                    color = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dominant Feeling & Heartfelt Warmth Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CyberSurfaceElevated)
                    .border(0.8.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { isDetailsExpanded = !isDetailsExpanded }
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💖", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "अनुभूत भावना: ${profile.dominantFeelingHindi}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = if (isDetailsExpanded) "संक्षेप ▲" else "सहानुभूति विवरण ▼",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }

                    AnimatedVisibility(visible = isDetailsExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "सहानुभूति व आत्मीय मार्गदर्शन:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiBlueLight
                            )
                            Text(
                                text = profile.empathyGuidanceHindi,
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "चेहरे के भाव अनुकरण (परीक्षण हेतु टैप करें):",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeminiTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(FacialExpression.values()) { face ->
                                    val isSelected = (manualFace == face) || (manualFace == null && profile.facialExpression == face)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) NeonPink.copy(alpha = 0.2f) else CyberSurface)
                                            .border(0.8.dp, if (isSelected) NeonPink else CyberBorder, RoundedCornerShape(6.dp))
                                            .clickable {
                                                if (manualFace == face) viewModel.setSimulatedFacialExpression(null)
                                                else viewModel.setSimulatedFacialExpression(face)
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${face.indicatorEmoji} ${face.titleHindi.take(8)}",
                                            fontSize = 8.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonPink else Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "बॉडी लैंग्वेज अनुकरण (परीक्षण हेतु टैप करें):",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GeminiTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(BodyLanguageState.values()) { body ->
                                    val isSelected = (manualBody == body) || (manualBody == null && profile.bodyLanguage == body)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else CyberSurface)
                                            .border(0.8.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(6.dp))
                                            .clickable {
                                                if (manualBody == body) viewModel.setSimulatedBodyLanguage(null)
                                                else viewModel.setSimulatedBodyLanguage(body)
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${body.indicatorEmoji} ${body.titleHindi.take(8)}",
                                            fontSize = 8.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) NeonGreen else Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModalityCard(
    icon: String,
    labelHindi: String,
    valueHindi: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceElevated)
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = labelHindi,
                    fontSize = 8.sp,
                    color = GeminiTextTertiary
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueHindi,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}
