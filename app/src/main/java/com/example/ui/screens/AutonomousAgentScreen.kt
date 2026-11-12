package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AgentPersonaMode
import com.example.ai.AutonomousAgentEngine
import com.example.ai.AutonomousMission
import com.example.ai.CriticalActionApproval
import com.example.ai.FactCheckReport
import com.example.ai.WorkflowStatus
import com.example.ai.XTrendItem
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLockViewModel
import com.example.util.rememberHaptic

/**
 * AutonomousAgentScreen:
 * Autonomous Personal AI Agent Workspace:
 * - Real-time X (Twitter) trends & live topic intelligence
 * - Witty, less-censored & humorous persona toggle (Grok-style mode)
 * - Multi-source web fact verification with truth confidence scoring
 * - Human-in-the-Loop strict permission approval protocol for critical actions
 * - Autonomous multi-step background workflow execution & cloud sandbox
 * - Full device apps integration while strictly protecting banking apps
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AutonomousAgentScreen(
    viewModel: VoiceLockViewModel,
    onBack: () -> Unit
) {
    val haptic = rememberHaptic()
    val personaMode by viewModel.agentPersonaMode.collectAsState()
    val xTrends by viewModel.xTrends.collectAsState()
    val activeMissions by viewModel.activeMissions.collectAsState()
    val pendingApprovals by viewModel.pendingApprovals.collectAsState()
    val recentFactChecks by viewModel.recentFactChecks.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var xSearchQuery by remember { mutableStateOf("") }
    var factCheckInput by remember { mutableStateOf("") }

    val tabs = listOf(
        "X (ट्विटर) ट्रेंड्स",
        "सत्यता जांच (Fact Check)",
        "स्वायत्त वर्कफ़्लो",
        "अनुमति व ऐप एक्सेस"
    )

    BackHandler {
        onBack()
    }

    val cardBackground = Color(0xFF111927)
    val cardBorder = Color(0xFF223249)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF070B14),
                        DeepNavy,
                        Color(0xFF050810)
                    )
                )
            )
            .testTag("autonomous_agent_screen")
    ) {
        // ==========================================
        // 1. TOP HEADER
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptic.tap()
                            onBack()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF162235))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "स्वायत्त एआई एजेंट",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AUTONOMOUS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                        Text(
                            text = "एक्स ट्रेंड्स • बहु-स्रोतीय फैक्ट चेकिंग • स्वायत्त क्लाउड",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Cloud Sandbox Live Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SuccessGreen.copy(alpha = 0.15f))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "क्लाउड लाइव",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Persona Toggle Card (Witty & Humorous Mode)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162338)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) PurpleAccent else cardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.tap()
                        viewModel.toggleAgentPersonaMode()
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (personaMode == AgentPersonaMode.WITTY_HUMOROUS)
                                        Brush.linearGradient(listOf(PurpleAccent, CyberCyan))
                                    else
                                        Brush.linearGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = personaMode.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = personaMode.badge,
                                    fontSize = 10.sp,
                                    color = if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) CyberCyan else Color(0xFF94A3B8),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = personaMode.tag,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Toggle Switch Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) PurpleAccent.copy(alpha = 0.25f)
                                else Color(0xFF1E293B)
                            )
                            .border(
                                1.dp,
                                if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) PurpleAccent else Color(0xFF475569),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) "चुटीला ऑन 🔥" else "गंभीर मोड",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (personaMode == AgentPersonaMode.WITTY_HUMOROUS) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. SCROLLABLE TAB BAR
        // ==========================================
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color(0xFF0D1424),
            contentColor = CyberCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CyberCyan,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val selected = selectedTabIndex == index
                Tab(
                    selected = selected,
                    onClick = {
                        haptic.tap()
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) CyberCyan else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        // ==========================================
        // 3. TAB CONTENT
        // ==========================================
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> XTrendsTabContent(
                    xTrends = xTrends,
                    searchQuery = xSearchQuery,
                    onSearchQueryChange = { xSearchQuery = it },
                    onAnalyzeQuery = { query ->
                        if (query.isNotBlank()) {
                            viewModel.sendTextQueryToGemini("एक्स (Twitter) और वेब पर '${query}' के बारे में रियल-टाइम ट्रेंड्स, जनभावना और मुख्य बातें बताएं।")
                            viewModel.setScreen(AppScreen.GEMINI_CHAT)
                        }
                    },
                    cardBackground = cardBackground,
                    cardBorder = cardBorder
                )
                1 -> FactCheckTabContent(
                    factChecks = recentFactChecks,
                    input = factCheckInput,
                    onInputChange = { factCheckInput = it },
                    onVerify = {
                        if (factCheckInput.isNotBlank()) {
                            viewModel.verifyClaimWithAgent(factCheckInput)
                            factCheckInput = ""
                        }
                    },
                    cardBackground = cardBackground,
                    cardBorder = cardBorder
                )
                2 -> WorkflowsTabContent(
                    missions = activeMissions,
                    cardBackground = cardBackground,
                    cardBorder = cardBorder
                )
                3 -> PermissionsAndAppsTabContent(
                    pendingApprovals = pendingApprovals,
                    onApprove = { viewModel.approveCriticalAction(it) },
                    onReject = { viewModel.rejectCriticalAction(it) },
                    onLaunchApp = { viewModel.launchDeviceApp(it) },
                    cardBackground = cardBackground,
                    cardBorder = cardBorder
                )
            }
        }
    }
}

/**
 * Tab 0: X (Twitter) Live Trends Content
 */
@Composable
fun XTrendsTabContent(
    xTrends: List<XTrendItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAnalyzeQuery: (String) -> Unit,
    cardBackground: Color,
    cardBorder: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("एक्स या वेब पर कोई भी विषय खोजें...", color = Color(0xFF64748B), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    IconButton(
                        onClick = { onAnalyzeQuery(searchQuery) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberCyan)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "एक्स (Twitter) लाइव ट्रेंड्स व इनसाइट्स",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "लाइव डेटा",
                    fontSize = 11.sp,
                    color = CyberCyan,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Trends List
        items(xTrends) { trend ->
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header: Hashtag, Volume, Sentiment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = trend.hashtag,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberCyan
                            )
                            Text(
                                text = trend.topicName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = trend.postVolume,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = trend.sentiment,
                                fontSize = 10.sp,
                                color = PurpleAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Factual Summary
                    Text(
                        text = trend.factualSummary,
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Witty Grok-style Hot Take
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E34))
                            .border(1.dp, PurpleAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "🔥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "चुटीला हॉट-टेक (Witty Take):",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleAccent
                                )
                                Text(
                                    text = trend.wittyHotTake,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sources & Verification badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "विश्वसनीयता: ${trend.truthScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        Text(
                            text = "स्रोतः ${trend.verifiedSources.joinToString(", ")}",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: Fact Check Tab Content
 */
@Composable
fun FactCheckTabContent(
    factChecks: List<FactCheckReport>,
    input: String,
    onInputChange: (String) -> Unit,
    onVerify: () -> Unit,
    cardBackground: Color,
    cardBorder: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Query Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "बहु-स्रोतीय फैक्ट चेकर व सत्यता सत्यापन",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "किसी भी सोशल मीडिया दावे या खबर का 5 स्वतंत्र वेब स्रोतों से मिलान करें।",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = input,
                        onValueChange = onInputChange,
                        placeholder = { Text("दावा यहाँ लिखें (जैसे: 'क्या पृथ्वी चपटी है?', 'एआई सुरक्षा नियम')", color = Color(0xFF64748B), fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = cardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onVerify,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("सत्यता जांचें (Verify Now)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "सत्यापित रिपोर्ट्स (${factChecks.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Fact Check Items
        items(factChecks) { report ->
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (report.truthScore > 80) SuccessGreen.copy(alpha = 0.5f) else Color(0xFFFFB300).copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = report.verdictHindi,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (report.truthScore > 80) SuccessGreen else Color(0xFFFFB300)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "स्कोर: ${report.truthScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (report.truthScore > 80) SuccessGreen else Color(0xFFFFB300)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "दावा: \"${report.claim}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = report.summary,
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Witty remark
                    if (report.wittyRemark.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1B2335))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "💬 आर्या का विचार: ${report.wittyRemark}",
                                fontSize = 11.sp,
                                color = CyberCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = "सत्यापित स्रोत: ${report.sourcesChecked.joinToString(", ")}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

/**
 * Tab 2: Workflows & Cloud Sandbox Content
 */
@Composable
fun WorkflowsTabContent(
    missions: List<AutonomousMission>,
    cardBackground: Color,
    cardBorder: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cloud Sandbox Monitor
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "क्वांटम क्लाउड सैंडबॉक्स एनवायरनमेंट",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "ऑनलाइन", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "वर्चुअल vCPU", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "4.8 GHz AI Core", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text(text = "सैंडबॉक्स रैम", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "32 GB Isolated", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text(text = "क्लाउड स्टोरेज", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(text = "1 TB Private", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ब्राउज़र इंजन: हेडलेस क्रोमियम ऑटोनॉमस वेब ब्राउज़र (सक्रिय)",
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "स्वायत्त मिशन्स व बैकग्राउंड वर्कफ़्लो (${missions.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Missions List
        items(missions) { mission ->
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mission.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (mission.status) {
                                        WorkflowStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.2f)
                                        WorkflowStatus.RUNNING -> CyberCyan.copy(alpha = 0.2f)
                                        WorkflowStatus.WAITING_APPROVAL -> Color(0xFFFFB300).copy(alpha = 0.2f)
                                        else -> Color(0xFF1E293B)
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = mission.status.labelHindi,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (mission.status) {
                                    WorkflowStatus.COMPLETED -> SuccessGreen
                                    WorkflowStatus.RUNNING -> CyberCyan
                                    WorkflowStatus.WAITING_APPROVAL -> Color(0xFFFFB300)
                                    else -> Color.White
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = mission.description,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { mission.currentStep.toFloat() / mission.totalSteps.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyberCyan,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step Logs
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D1424))
                            .padding(10.dp)
                    ) {
                        mission.stepsLog.forEach { log ->
                            Text(
                                text = log,
                                fontSize = 10.sp,
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    if (mission.finalResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = mission.finalResult!!,
                            fontSize = 11.sp,
                            color = SuccessGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: Permissions and Device Apps Access Hub
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PermissionsAndAppsTabContent(
    pendingApprovals: List<CriticalActionApproval>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    cardBackground: Color,
    cardBorder: Color
) {
    val nonBankingApps = listOf(
        "यूट्यूब (YouTube)" to "youtube",
        "व्हाट्सएप (WhatsApp)" to "whatsapp",
        "कैमरा (Camera)" to "camera",
        "क्रोम ब्राउज़र (Chrome)" to "chrome",
        "गूगल मैप्स (Google Maps)" to "maps",
        "गैलरी / फ़ोटो (Photos)" to "gallery",
        "कैलेंडर (Calendar)" to "calendar",
        "घड़ी व अलार्म (Clock)" to "clock",
        "कॉन्टैक्ट्स (Contacts)" to "contacts",
        "फोन डायलर (Dialer)" to "phone",
        "मैसेजेस (SMS)" to "messages",
        "कैलकुलेटर (Calculator)" to "calculator",
        "फ़ाइल मैनेजर (Files)" to "files",
        "सिस्टम सेटिंग्स (Settings)" to "settings"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Human-in-the-Loop Safeguard Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2838)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "मानवीय अनुमति प्रोटोकॉल (Human-in-the-Loop)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "निर्देशानुसार: कोई भी खरीददारी, टिकट बुकिंग, या संवेदनशील कार्य बिना आपकी स्पष्ट अनुमति के कभी निष्पादित नहीं होगा। आपकी सुरक्षा 100% सुनिश्चित है।",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Pending Critical Approvals
        item {
            Text(
                text = "प्रतीक्षित अनुमतियां (${pendingApprovals.count { !it.isApproved && !it.isRejected }})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(pendingApprovals) { approval ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        approval.isApproved -> Color(0xFF0F2618)
                        approval.isRejected -> Color(0xFF261215)
                        else -> cardBackground
                    }
                ),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when {
                        approval.isApproved -> SuccessGreen
                        approval.isRejected -> ErrorRed
                        else -> Color(0xFFFFB300)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = approval.actionTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = when {
                                    approval.isApproved -> "स्वीकृत ✅"
                                    approval.isRejected -> "अस्वीकृत ❌"
                                    else -> "अनुमति आवश्यक ⚠️"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    approval.isApproved -> SuccessGreen
                                    approval.isRejected -> ErrorRed
                                    else -> Color(0xFFFFB300)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "लक्षित ऐप: ${approval.targetApp}",
                        fontSize = 11.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = approval.actionDetails,
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "जोखिम समीक्षा: ${approval.riskAssessment}",
                        fontSize = 10.sp,
                        color = Color(0xFFFFB300)
                    )

                    if (!approval.isApproved && !approval.isRejected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onApprove(approval.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("अनुमति दें", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onReject(approval.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("अस्वीकार करें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // STRICT BANKING EXCLUSION SHIELD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1417)),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "बैंकिंग ऐप्स: 100% सुरक्षित व प्रतिबंधित",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "सख्त नीति लागू (सिर्फ बैंकिंग ऐप्स को छोड़कर)",
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "आपके सख्त आदेशानुसार, Arya AI को किसी भी बैंक ऐप, यूपीआई, वॉलेट, लोन, क्रेडिट कार्ड या निवेश ऐप (SBI, HDFC, ICICI, PNB, Paytm, PhonePe, GPay, Cred, Zerodha आदि) का कोई एक्सेस नहीं है। आपकी वित्तीय सुरक्षा 100% अभेद्य है।",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // NON-BANKING APPS ACCESS (ALL OTHER APPS)
        item {
            Text(
                text = "मोबाइल के अन्य सभी ऐप्स (पूर्ण एक्सेस सक्रिय)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "एआई एक्सेस सक्रिय: आप वॉयस या टच से इन्हें खोल सकते हैं",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        nonBankingApps.forEach { (label, command) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF162338))
                                    .border(1.dp, cardBorder, RoundedCornerShape(10.dp))
                                    .clickable { onLaunchApp(command) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.AutoMirrored.Filled.Launch,
                                        contentDescription = "Open",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(12.dp)
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
