package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
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
import com.example.ai.MultiAiCollaborationEngine
import com.example.ai.TopAiModel
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
 * MultiAiCollaborationHubView:
 * Interactive UI displaying the world's Top 10 AI models collaboration engine.
 * Allows toggling automatic routing, selecting primary model, and viewing
 * the synthesis reasoning across all models.
 */
@Composable
fun MultiAiCollaborationHubView(
    viewModel: VoiceLockViewModel,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val isEnabled by viewModel.isMultiAiCollaborationEnabled.collectAsState()
    val latestDecision by viewModel.latestCollaboration.collectAsState()
    val selectedManualModel by viewModel.selectedManualPrimaryModel.collectAsState()
    val top10Models = viewModel.top10AiModels

    var showDetailsDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(NeonCyan, NeonPurple, NeonGreen))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_ai_collaboration_hub")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Title, Tag, and Switch Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "मल्टी‑एआई कोलैबोरेशन इंजन",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TOP 10 AI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                        }
                        Text(
                            text = "दुनिया के शीर्ष 10 मॉडल्स का सामूहिक ज्ञान व सत्यापन",
                            fontSize = 11.sp,
                            color = GeminiTextSecondary
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { viewModel.toggleMultiAiCollaboration(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonGreen,
                        uncheckedTrackColor = CyberSurfaceElevated
                    ),
                    modifier = Modifier.testTag("toggle_multi_ai_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Model Collaboration Banner
            val currentPrimary = selectedManualModel ?: latestDecision?.primaryModel ?: top10Models.first()
            val currentCollabs = latestDecision?.collaboratorModels ?: top10Models.filter { it.id != currentPrimary.id }.take(3)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceElevated)
                    .border(1.dp, Color(currentPrimary.colorHex).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentPrimary.iconEmoji,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentPrimary.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(currentPrimary.colorHex)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[मुख्य मॉडल • ${currentPrimary.provider}]",
                                        fontSize = 10.sp,
                                        color = GeminiTextTertiary
                                    )
                                }
                                Text(
                                    text = currentPrimary.specialtyHindi,
                                    fontSize = 10.sp,
                                    color = GeminiTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedManualModel != null) "कस्टम चयन" else "ऑटो रूटिंग ⚡",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Supporting Collaborator Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "सहयोगी टीम: ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeminiTextTertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(currentCollabs) { collab ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(collab.colorHex).copy(alpha = 0.12f))
                                        .border(0.8.dp, Color(collab.colorHex).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${collab.iconEmoji} ${collab.name}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(collab.colorHex)
                                    )
                                }
                            }
                        }
                    }

                    if (!isCompact && latestDecision != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🎯 चयन तर्क: ${latestDecision?.reasonHindi}",
                            fontSize = 10.sp,
                            color = GeminiBlueLight,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Carousel of All Top 10 Models
            Text(
                text = "विश्व के टॉप 10 AI मॉडल्स (चुनने हेतु टैप करें):",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = GeminiTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(top10Models) { model ->
                    val isSelected = (selectedManualModel?.id == model.id) ||
                            (selectedManualModel == null && currentPrimary.id == model.id)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(model.colorHex).copy(alpha = 0.2f) else CyberSurfaceElevated)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(model.colorHex) else CyberBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (selectedManualModel?.id == model.id) {
                                    viewModel.setManualPrimaryModel(null) // Reset to Auto
                                } else {
                                    viewModel.setManualPrimaryModel(model)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.iconEmoji,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${model.rank} ${model.name}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(model.colorHex) else Color.White
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(model.colorHex),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = model.provider,
                                    fontSize = 9.sp,
                                    color = GeminiTextTertiary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Core Directives Guarantee Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DirectivePill(
                    icon = "⚡",
                    label = "त्वरित व सटीक",
                    color = NeonCyan
                )
                DirectivePill(
                    icon = "✍️",
                    label = "पूर्ण व विस्तृत वाक्य",
                    color = NeonPurple
                )
                DirectivePill(
                    icon = "🛡️",
                    label = "100% सत्यता गारंटी",
                    color = NeonGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Inter-AI Direct Communication & Peer Exchange Panel
            var isPeerDialogueExpanded by remember { mutableStateOf(false) }
            val latestPeerSession by viewModel.latestPeerSession.collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceElevated)
                    .border(1.dp, Brush.horizontalGradient(listOf(NeonPurple.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))), RoundedCornerShape(12.dp))
                    .clickable { isPeerDialogueExpanded = !isPeerDialogueExpanded }
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🤝", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "AI से AI सीधा संवाद व सूचना आदान‑प्रदान",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (latestPeerSession != null)
                                        "सक्रिय सह-संवाद: ${latestPeerSession?.dialogueTurns?.size ?: 0} राउंड्स संपन्न"
                                    else
                                        "मॉडल्स के आपसी संचार प्रोटोकॉल का लाइव विवरण",
                                    fontSize = 9.sp,
                                    color = GeminiTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isPeerDialogueExpanded) "संक्षेप ▲" else "विस्तार ▼",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }

                    AnimatedVisibility(visible = isPeerDialogueExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            val session = latestPeerSession ?: MultiAiCollaborationEngine.executeInterAiPeerDialogue(
                                "सामान्य प्रश्न विश्लेषण",
                                latestDecision ?: MultiAiCollaborationEngine.orchestrateQuery("सामान्य प्रश्न")
                            )

                            session.dialogueTurns.forEach { turn ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberSurface)
                                        .border(0.8.dp, Color(turn.fromModel.colorHex).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${turn.fromModel.iconEmoji} ${turn.fromModel.name}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(turn.fromModel.colorHex)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "➔",
                                                fontSize = 9.sp,
                                                color = GeminiTextTertiary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${turn.toModel.iconEmoji} ${turn.toModel.name}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(turn.toModel.colorHex)
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(
                                                text = when (turn.stepType) {
                                                    "PROPOSAL" -> "प्रस्ताव 💡"
                                                    "CROSS_CHECK" -> "सत्यापन 🔍"
                                                    "FACT_VERIFICATION" -> "तथ्य पुष्टि 🛡️"
                                                    else -> "सर्वसहमति 🤝"
                                                },
                                                fontSize = 9.sp,
                                                color = NeonGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = turn.dialogueText,
                                            fontSize = 10.sp,
                                            lineHeight = 14.sp,
                                            color = Color(0xFFE2E8F0)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "✅ सर्वसम्मत निष्कर्ष: ${session.consensusDecision}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Stage Consensus & Knowledge Synthesis Card
            val latestConsensusReport by viewModel.latestConsensusReport.collectAsState()
            var isSynthesisDetailsExpanded by remember { mutableStateOf(false) }

            val consensusToDisplay = latestConsensusReport ?: remember {
                val dec = latestDecision ?: MultiAiCollaborationEngine.orchestrateQuery("सामान्य ज्ञान व तकनीकी प्रश्न")
                MultiAiCollaborationEngine.obtainAndSynthesizeMultiAiKnowledge("सामान्य ज्ञान व तकनीकी प्रश्न", dec)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceElevated)
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.5f), NeonGreen.copy(alpha = 0.5f))),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { isSynthesisDetailsExpanded = !isSynthesisDetailsExpanded }
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "3-चरणीय सामूहिक बुद्धिमत्ता व परिष्करण",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "1. डेटा अर्जन ➔ 2. सटीक चयन (${consensusToDisplay.selectedWinningModel.name}) ➔ 3. पूर्ण परिष्करण",
                                    fontSize = 9.sp,
                                    color = NeonCyan
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isSynthesisDetailsExpanded) "संक्षेप ▲" else "सत्यापन विवरण ▼",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }

                    AnimatedVisibility(visible = isSynthesisDetailsExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "चरण 1: संबंधित क्षेत्र के सर्वश्रेष्ठ मॉडल्स से डेटा अर्जन:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiBlueLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            consensusToDisplay.candidateInsights.forEach { candidate ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberSurface)
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = candidate.model.iconEmoji, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = candidate.model.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(candidate.model.colorHex)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "• ${candidate.roleHindi}",
                                                fontSize = 9.sp,
                                                color = GeminiTextTertiary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (candidate.isLiveCloudApi) NeonGreen.copy(alpha = 0.2f)
                                                        else Color(0xFFFFB74D).copy(alpha = 0.2f)
                                                    )
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(
                                                    text = candidate.statusBadgeHindi,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (candidate.isLiveCloudApi) NeonGreen else Color(0xFFFFB74D)
                                                )
                                            }
                                        }
                                        Text(
                                            text = candidate.dataSnippetHindi,
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (candidate.isSelectedWinningBase) NeonGreen.copy(alpha = 0.2f)
                                                else CyberSurfaceElevated
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${candidate.accuracyScore}%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (candidate.isSelectedWinningBase) NeonGreen else GeminiTextSecondary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "चरण 2: सर्वाधिक सटीक उत्तर का चयन:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "चयनित मॉडल: ${consensusToDisplay.selectedWinningModel.name} | ${consensusToDisplay.selectionRationaleHindi}",
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "चरण 3: ज्ञान का परिष्करण व पूर्णता:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPurple
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = consensusToDisplay.refinementDirectivesHindi,
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Secure Key Management & X (Twitter) API v2 Integration Card
            var isKeysSectionExpanded by remember { mutableStateOf(false) }
            var openAiInput by remember { mutableStateOf(viewModel.getOpenAiKey()) }
            var deepSeekInput by remember { mutableStateOf(viewModel.getDeepSeekKey()) }
            var xAiInput by remember { mutableStateOf(viewModel.getXAiKey()) }
            var xTwitterInput by remember { mutableStateOf(viewModel.getXTwitterBearerToken()) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceElevated)
                    .border(
                        0.8.dp,
                        Brush.horizontalGradient(listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.5f))),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { isKeysSectionExpanded = !isKeysSectionExpanded }
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔐", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "मल्टी-एआई व X (Twitter) API कुंजी प्रबंधन",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Android KeyStore द्वारा 100% हार्डवेयर एन्क्रिप्टेड",
                                    fontSize = 9.sp,
                                    color = CyberCyan
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurface)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isKeysSectionExpanded) "छुपाएं ▲" else "कीज़ सेटिंग्स ▼",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC084FC)
                            )
                        }
                    }

                    AnimatedVisibility(visible = isKeysSectionExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text(
                                text = "रीयल-टाइम क्लाउड विनिमय हेतु आधिकारिक API कुंजियाँ दर्ज करें। अनुपलब्ध होने पर स्वायत्त कॉग्निटिव फ्रेमवर्क (Cognitive Framework) स्वतः सक्रिय रहता है।",
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                color = GeminiTextSecondary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // OpenAI Key
                            androidx.compose.material3.OutlinedTextField(
                                value = openAiInput,
                                onValueChange = { openAiInput = it },
                                label = { Text("OpenAI API Key (sk-...)", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = Color.White)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // DeepSeek Key
                            androidx.compose.material3.OutlinedTextField(
                                value = deepSeekInput,
                                onValueChange = { deepSeekInput = it },
                                label = { Text("DeepSeek API Key (sk-...)", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = Color.White)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // xAI Grok Key
                            androidx.compose.material3.OutlinedTextField(
                                value = xAiInput,
                                onValueChange = { xAiInput = it },
                                label = { Text("xAI (Grok) API Key (xai-...)", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = Color.White)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // X (Twitter) Bearer Token
                            androidx.compose.material3.OutlinedTextField(
                                value = xTwitterInput,
                                onValueChange = { xTwitterInput = it },
                                label = { Text("X (Twitter) API v2 Bearer Token", fontSize = 10.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp, color = Color.White)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            androidx.compose.material3.Button(
                                onClick = {
                                    viewModel.saveOpenAiKey(openAiInput)
                                    viewModel.saveDeepSeekKey(deepSeekInput)
                                    viewModel.saveXAiKey(xAiInput)
                                    viewModel.saveXTwitterBearerToken(xTwitterInput)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B5CF6)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("KeyStore में सुरक्षित सहेजें (Save to KeyStore)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectivePill(
    icon: String,
    label: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}
