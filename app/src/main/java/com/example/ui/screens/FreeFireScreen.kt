package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.CharacterSkillType
import com.example.ai.FfGameMode
import com.example.ai.FfPlaystyle
import com.example.ai.FreeFireKnowledgeBase
import com.example.ai.FreeFireStrategyAnalyzer
import com.example.ai.WeaponCategory
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.VoiceLockViewModel

private val FfDarkBg = Color(0xFF090D18)
private val FfSurface = Color(0xFF111728)
private val FfSurfaceElevated = Color(0xFF1B233C)
private val FfBorder = Color(0xFF283457)
private val FfFlame = Color(0xFFFF6D00)
private val FfGold = Color(0xFFFFD600)

@Composable
fun FreeFireScreen(
    viewModel: VoiceLockViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("रणनीति विश्लेषक", "हथियार व आँकड़े", "कैरेक्टर स्किल्स", "प्रो ट्रिक्स")

    var selectedPlaystyle by remember { mutableStateOf(FfPlaystyle.RUSHER) }
    var selectedMode by remember { mutableStateOf(FfGameMode.BR_RANKED) }
    var selectedRefreshRate by remember { mutableIntStateOf(90) }
    var selectedCsRound by remember { mutableIntStateOf(1) }

    var selectedWeaponCategory by remember { mutableStateOf<WeaponCategory?>(null) }
    var queryInput by remember { mutableStateOf("") }
    var aiAnswer by remember { mutableStateOf<String?>(null) }

    val currentLoadout = remember(selectedPlaystyle, selectedMode) {
        FreeFireStrategyAnalyzer.analyzeAndBuildLoadout(selectedPlaystyle, selectedMode)
    }

    val currentSensitivity = remember(selectedRefreshRate) {
        FreeFireStrategyAnalyzer.calculateSensitivity(selectedRefreshRate)
    }

    val currentCsStrategy = remember(selectedCsRound) {
        FreeFireStrategyAnalyzer.getCsRoundStrategy(selectedCsRound)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FfDarkBg)
            .testTag("free_fire_expert_screen")
    ) {
        // ==========================================
        // 1. TOP HEADER BAR
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FfSurfaceElevated)
                    .testTag("free_fire_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Free Fire अल्टीमेट प्रो गाइड",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Brush.horizontalGradient(listOf(FfFlame, FfGold)))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PRO META",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
                Text(
                    text = "गेम स्ट्रैटेजी एनालाइज़र • वेपन्स • कैरेक्टर्स • वन-टैप ट्रिक्स",
                    fontSize = 11.sp,
                    color = NeonCyan
                )
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(FfFlame.copy(alpha = 0.2f))
                    .border(1.dp, FfFlame.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = FfFlame,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ==========================================
        // 2. MODERN TAB BAR
        // ==========================================
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FfSurface,
            contentColor = FfFlame,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = FfFlame,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) FfFlame else Color.White.copy(alpha = 0.65f)
                        )
                    },
                    modifier = Modifier.testTag("tab_ff_${index}")
                )
            }
        }

        // ==========================================
        // 3. TAB CONTENT
        // ==========================================
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: STRATEGY ANALYZER
                    StrategyAnalyzerContent(
                        selectedPlaystyle = selectedPlaystyle,
                        onSelectPlaystyle = { selectedPlaystyle = it },
                        selectedMode = selectedMode,
                        onSelectMode = { selectedMode = it },
                        loadout = currentLoadout,
                        selectedRefreshRate = selectedRefreshRate,
                        onSelectRefreshRate = { selectedRefreshRate = it },
                        sensitivity = currentSensitivity,
                        selectedCsRound = selectedCsRound,
                        onSelectCsRound = { selectedCsRound = it },
                        csStrategy = currentCsStrategy
                    )
                }
                1 -> {
                    // TAB 2: WEAPONS & STATS
                    WeaponsContent(
                        selectedCategory = selectedWeaponCategory,
                        onSelectCategory = { selectedWeaponCategory = it }
                    )
                }
                2 -> {
                    // TAB 3: CHARACTERS & COMBOS
                    CharactersContent()
                }
                3 -> {
                    // TAB 4: PRO TRICKS & MECHANICS
                    ProTricksContent()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 4. ASK ARYA GAMING ADVICE CARD
            // ==========================================
            Card(
                colors = CardDefaults.cardColors(containerColor = FfSurfaceElevated),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.2.dp, FfFlame.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("ff_ask_arya_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FfFlame, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "आर्या से फ्री फायर सवाल पूछें",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Suggestion Chips
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val chips = listOf(
                            "वन-टैप हेडशॉट सेटिंग?",
                            "बेस्ट रशर कॉम्बो?",
                            "M1887 vs MP40?",
                            "360 सिट-अप ग्लू वॉल?",
                            "CS Ranked में जीत की ट्रिक?"
                        )
                        chips.forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FfSurface)
                                    .border(1.dp, FfBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        queryInput = chip
                                        aiAnswer = FreeFireStrategyAnalyzer.evaluateFreeFireQuery(chip)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(chip, fontSize = 10.sp, color = NeonAmber, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = queryInput,
                            onValueChange = { queryInput = it },
                            placeholder = { Text("सवाल पूछें (उदा: हेडशॉट ड्रैग ट्रिक)", fontSize = 12.sp, color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FfFlame,
                                unfocusedBorderColor = FfBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (queryInput.isNotBlank()) {
                                    aiAnswer = FreeFireStrategyAnalyzer.evaluateFreeFireQuery(queryInput)
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(FfFlame, FfGold)))
                                .testTag("ff_send_query_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }

                    AnimatedVisibility(visible = aiAnswer != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0D1222))
                                .border(1.dp, NeonGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("आर्या का प्रो गेमिंग जवाब ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonGreen)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NeonGreen.copy(alpha = 0.15f))
                                        .clickable {
                                            aiAnswer?.let { viewModel.voiceManager.speak(it) }
                                            Toast.makeText(context, "बोलकर सुनाया जा रहा है...", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("🔊 सुनें", fontSize = 10.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = aiAnswer ?: "",
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StrategyAnalyzerContent(
    selectedPlaystyle: FfPlaystyle,
    onSelectPlaystyle: (FfPlaystyle) -> Unit,
    selectedMode: FfGameMode,
    onSelectMode: (FfGameMode) -> Unit,
    loadout: com.example.ai.FfTacticalLoadout,
    selectedRefreshRate: Int,
    onSelectRefreshRate: (Int) -> Unit,
    sensitivity: com.example.ai.FfSensitivityProfile,
    selectedCsRound: Int,
    onSelectCsRound: (Int) -> Unit,
    csStrategy: com.example.ai.FfCsRoundStrategy
) {
    // 1. Playstyle Selector
    Text(
        text = "अपनी गेमिंग शैली चुनें (Select Playstyle):",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FfPlaystyle.entries.forEach { style ->
            val isSelected = selectedPlaystyle == style
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Brush.horizontalGradient(listOf(FfFlame, FfGold)) else Brush.linearGradient(listOf(FfSurfaceElevated, FfSurface)))
                    .border(1.dp, if (isSelected) FfFlame else FfBorder, RoundedCornerShape(12.dp))
                    .clickable { onSelectPlaystyle(style) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(style.iconEmoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = style.labelHindi,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. Mode Selector
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FfGameMode.entries.forEach { mode ->
            val isSelected = selectedMode == mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else FfSurface)
                    .border(1.dp, if (isSelected) CyberCyan else FfBorder, RoundedCornerShape(10.dp))
                    .clickable { onSelectMode(mode) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.labelHindi,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) CyberCyan else Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Recommended Tactical Loadout Card
    Card(
        colors = CardDefaults.cardColors(containerColor = FfSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, FfFlame.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth().testTag("ff_tactical_loadout_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = FfGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "मेटा लोडआउट (Tactical Loadout)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "जीत दर: ${loadout.winProbabilityPercent}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Ability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(FfFlame.copy(alpha = 0.12f))
                    .border(1.dp, FfFlame.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("सक्रिय (Active): ", fontSize = 11.sp, color = FfGold, fontWeight = FontWeight.Bold)
                        Text(loadout.activeCharacter.name, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "${loadout.activeCharacter.skillNameHindi} • ${loadout.activeCharacter.descriptionHindi}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Passives
            Text("सहायक पैसिव्स (Passives):", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            loadout.passiveCharacters.forEach { char ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${char.name} (${char.skillNameHindi}): ${char.descriptionHindi.take(55)}...",
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gun Combo & Pet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FfSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("प्राइमरी गन", fontSize = 10.sp, color = Color.Gray)
                        Text(loadout.primaryGun.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonAmber)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FfSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("सेकेंडरी गन", fontSize = 10.sp, color = Color.Gray)
                        Text(loadout.secondaryGun.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FfSurfaceElevated)
                        .padding(8.dp)
                ) {
                    Column {
                        Text("अनुशंसित पेट", fontSize = 10.sp, color = Color.Gray)
                        Text(loadout.recommendedPet.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FfGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tactical Advice
            Text(
                text = "रणनीतिक सलाह: ${loadout.strategyDirectivesHindi}",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 16.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1324))
                    .padding(8.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 4. Sensitivity & Device Optimizer Card
    Card(
        colors = CardDefaults.cardColors(containerColor = FfSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("ff_sensitivity_optimizer_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "डिवाइस सेंसिटिविटी कैलकुलेटर",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Refresh Rate Selector (60Hz / 90Hz / 120Hz)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(60, 90, 120).forEach { hz ->
                        val isSelected = selectedRefreshRate == hz
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyberCyan else FfSurfaceElevated)
                                .clickable { onSelectRefreshRate(hz) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${hz}Hz",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sensitivity Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("जनरल", sensitivity.general),
                    Pair("रेड डॉट", sensitivity.redDot),
                    Pair("2X स्कोप", sensitivity.scope2x),
                    Pair("4X स्कोप", sensitivity.scope4x),
                    Pair("स्नाइपर", sensitivity.sniperScope)
                ).forEach { (label, value) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FfSurfaceElevated)
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, fontSize = 9.sp, color = Color.Gray)
                            Text(value.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("फायर बटन साइज: ${sensitivity.fireButtonSizePercent}%", fontSize = 10.sp, color = FfGold, fontWeight = FontWeight.Bold)
                Text("अनुशंसित DPI: ${sensitivity.dpiRecommended}", fontSize = 10.sp, color = NeonGreen)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ड्रैग तकनीक: ${sensitivity.dragTechniqueHindi}",
                fontSize = 10.5.sp,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 15.sp
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // 5. Clash Squad (CS) Round-by-Round Economy Guide
    Card(
        colors = CardDefaults.cardColors(containerColor = FfSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("ff_cs_round_economy_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CS Ranked राउंड इकॉनमी गाइड",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Round Switcher
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(1, 2, 3, 4).forEach { r ->
                        val isSelected = selectedCsRound == r
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) NeonGreen else FfSurfaceElevated)
                                .clickable { onSelectCsRound(r) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "R$r",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "राउंड ${csStrategy.roundNumber} (बजट: ~${csStrategy.targetBudget} क्रेडिट्स):",
                fontSize = 11.sp,
                color = FfGold,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("हथियार: ${csStrategy.recommendedWeaponsHindi}", fontSize = 11.sp, color = Color.White)
            Text("यूटिलिटी: ${csStrategy.utilityItemsHindi}", fontSize = 11.sp, color = CyberCyan)
            Text("मूवमेंट व एंगल: ${csStrategy.tacticalMovementHindi}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), modifier = Modifier.padding(top = 2.dp))
            Text("बैकअप प्लान: ${csStrategy.backupPlanHindi}", fontSize = 10.sp, color = ErrorRed.copy(alpha = 0.9f), modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun WeaponsContent(
    selectedCategory: WeaponCategory?,
    onSelectCategory: (WeaponCategory?) -> Unit
) {
    // Category Chips
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val isAllSelected = selectedCategory == null
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAllSelected) FfFlame else FfSurfaceElevated)
                .clickable { onSelectCategory(null) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "सभी हथियार (All)",
                fontSize = 11.sp,
                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isAllSelected) Color.Black else Color.White
            )
        }

        WeaponCategory.entries.forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(cat.badgeColorHex) else FfSurfaceElevated)
                    .clickable { onSelectCategory(cat) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = cat.labelHindi,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.Black else Color.White
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    val filteredWeapons = FreeFireKnowledgeBase.weapons.filter {
        selectedCategory == null || it.category == selectedCategory
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filteredWeapons.forEach { weapon ->
            WeaponCard(weapon)
        }
    }
}

@Composable
private fun WeaponCard(weapon: com.example.ai.FfWeapon) {
    Card(
        colors = CardDefaults.cardColors(containerColor = FfSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, FfBorder),
        modifier = Modifier.fillMaxWidth().testTag("ff_weapon_${weapon.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weapon.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(weapon.category.badgeColorHex).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = weapon.category.labelHindi,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(weapon.category.badgeColorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stat bars
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    StatBarItem("डैमेज (Damage)", weapon.damage, ErrorRed)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatBarItem("फायर रेट (Fire Rate)", weapon.rateOfFire, FfFlame)
                }
                Column(modifier = Modifier.weight(1f)) {
                    StatBarItem("रेंज (Range)", weapon.range, CyberCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatBarItem("आर्मर पेनिट्रेशन", weapon.armorPenetration, NeonPurple)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "प्रो टिप: ${weapon.proTipHindi}",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 15.sp
            )

            Text(
                text = "अटैचमेंट्स: ${weapon.bestAttachmentsHindi}",
                fontSize = 10.sp,
                color = FfGold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun StatBarItem(label: String, value: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 9.sp, color = Color.Gray)
        Text(value.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
    }
    LinearProgressIndicator(
        progress = { (value / 100f).coerceIn(0f, 1f) },
        color = color,
        trackColor = FfSurfaceElevated,
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
    )
}

@Composable
private fun CharactersContent() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        FreeFireKnowledgeBase.characters.forEach { char ->
            Card(
                colors = CardDefaults.cardColors(containerColor = FfSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (char.skillType == CharacterSkillType.ACTIVE) FfFlame.copy(alpha = 0.5f) else CyberCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().testTag("ff_char_${char.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(char.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (char.skillType == CharacterSkillType.ACTIVE) FfFlame.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = char.skillType.labelHindi,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (char.skillType == CharacterSkillType.ACTIVE) FfFlame else CyberCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "क्षमता: ${char.skillNameHindi}${if (char.cooldownSec > 0) " (कूलडाउन: ${char.cooldownSec}s)" else ""}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FfGold
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(char.descriptionHindi, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("बेस्ट स्क्वाड कॉम्बो: ${char.bestCombosHindi}", fontSize = 10.sp, color = NeonGreen)
                }
            }
        }
    }
}

@Composable
private fun ProTricksContent() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FreeFireKnowledgeBase.mechanics.forEach { mechanic ->
            Card(
                colors = CardDefaults.cardColors(containerColor = FfSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, FfFlame.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("ff_mechanic_${mechanic.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(mechanic.titleHindi, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FfGold)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FfFlame.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(mechanic.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FfFlame)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    mechanic.stepsHindi.forEachIndexed { i, step ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("${i + 1}. ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                            Text(step, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f), lineHeight = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0A1020))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⚡ प्रो सीक्रेट: ${mechanic.proSecretHindi}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonAmber,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
