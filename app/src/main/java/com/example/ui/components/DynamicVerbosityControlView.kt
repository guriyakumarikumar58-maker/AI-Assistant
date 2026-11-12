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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ai.verbosity.DynamicVerbosityEngine
import com.example.ai.verbosity.VerbosityLevel
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.VoiceLockViewModel

/**
 * DynamicVerbosityControlView:
 * Displays real-time response verbosity mode and provides intuitive toggle controls.
 * Highlights whether the AI is currently in:
 * - Concise & Direct (Default for minimal latency & zero fluff)
 * - Balanced (Standard depth)
 * - Comprehensive (In-depth for complex topics or explicit user requests)
 */
@Composable
fun DynamicVerbosityControlView(
    viewModel: VoiceLockViewModel,
    modifier: Modifier = Modifier
) {
    val decision by viewModel.dynamicVerbosityDecision.collectAsState()
    val manualOverride by viewModel.manualVerbosityOverride.collectAsState()

    var isSelectorExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.4f), NeonPurple.copy(alpha = 0.3f)))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dynamic_verbosity_card")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Icon, Title, and Active Mode Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (decision.level) {
                                VerbosityLevel.CONCISE -> Icons.Default.Bolt
                                VerbosityLevel.BALANCED -> Icons.Default.Speed
                                VerbosityLevel.COMPREHENSIVE -> Icons.Default.Description
                            },
                            contentDescription = null,
                            tint = when (decision.level) {
                                VerbosityLevel.CONCISE -> NeonCyan
                                VerbosityLevel.BALANCED -> NeonGreen
                                VerbosityLevel.COMPREHENSIVE -> NeonAmber
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "प्रत्युत्तर विस्तार नियंत्रण (Verbosity)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeminiTextPrimary
                        )
                        Text(
                            text = if (manualOverride != null) "मैन्युअल मोड सक्रिय" else "गतिशील स्वतः-अनुकूलन सक्रिय",
                            fontSize = 10.5.sp,
                            color = NeonCyan
                        )
                    }
                }

                // Active Mode Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (decision.level) {
                                VerbosityLevel.CONCISE -> NeonCyan.copy(alpha = 0.2f)
                                VerbosityLevel.BALANCED -> NeonGreen.copy(alpha = 0.2f)
                                VerbosityLevel.COMPREHENSIVE -> NeonAmber.copy(alpha = 0.2f)
                            }
                        )
                        .clickable { isSelectorExpanded = !isSelectorExpanded }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("verbosity_active_pill")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (decision.level) {
                                VerbosityLevel.CONCISE -> "⚡ संक्षिप्त (Concise)"
                                VerbosityLevel.BALANCED -> "⚖️ संतुलित"
                                VerbosityLevel.COMPREHENSIVE -> "📖 विस्तृत (Deep)"
                            },
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (decision.level) {
                                VerbosityLevel.CONCISE -> NeonCyan
                                VerbosityLevel.BALANCED -> NeonGreen
                                VerbosityLevel.COMPREHENSIVE -> NeonAmber
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reason / Trigger Explanation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurface)
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = decision.trigger.reasonHindi,
                        fontSize = 11.sp,
                        color = GeminiTextSecondary,
                        maxLines = 2
                    )
                }
            }

            // Expandable Mode Selector (Auto vs Manual Choices)
            AnimatedVisibility(visible = isSelectorExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "मोड चुनें (Select Preferred Response Depth):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Auto (Dynamic) Option
                        VerbosityOptionButton(
                            title = "गतिशील (Auto)",
                            isSelected = manualOverride == null,
                            activeColor = NeonCyan,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setManualVerbosityOverride(null)
                                isSelectorExpanded = false
                            }
                        )

                        // Concise (Default Direct)
                        VerbosityOptionButton(
                            title = "संक्षिप्त (Concise)",
                            isSelected = manualOverride == VerbosityLevel.CONCISE,
                            activeColor = NeonCyan,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setManualVerbosityOverride(VerbosityLevel.CONCISE)
                                isSelectorExpanded = false
                            }
                        )

                        // Comprehensive (In-Depth)
                        VerbosityOptionButton(
                            title = "विस्तृत (In-Depth)",
                            isSelected = manualOverride == VerbosityLevel.COMPREHENSIVE,
                            activeColor = NeonAmber,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.setManualVerbosityOverride(VerbosityLevel.COMPREHENSIVE)
                                isSelectorExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerbosityOptionButton(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.22f) else CyberSurface)
            .border(
                1.dp,
                if (isSelected) activeColor else CyberBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else Color(0xFF94A3B8)
        )
    }
}
