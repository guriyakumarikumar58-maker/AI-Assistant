package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AgentPersonaMode
import com.example.ai.AutonomousAgentEngine
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GeminiBlueAccent
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLockViewModel
import com.example.voice.VoicePersona

/**
 * Clean, organized, clutter-free Settings Screen for Arya AI Assistant.
 * Streamlined to retain only genuine, actionable preferences with zero clutter:
 * 1. आवाज़ एवं वाचन (Voice & Audio)
 * 2. बातचीत का लहजा (Conversation Tone & Witty Mode)
 * 3. बायोमेट्रिक सुरक्षा (Voice Biometrics & Match Sensitivity)
 * 4. चैट प्रदर्शन (Chat Display & Font Size)
 * 5. आर्किटेक्ट एवं रीसेट (Developer Credit & Profile Reset)
 */
@Composable
fun SettingsScreen(
    viewModel: VoiceLockViewModel
) {
    val context = LocalContext.current

    // Handle Android system back press to return cleanly to Dashboard
    BackHandler {
        viewModel.setScreen(AppScreen.DASHBOARD)
    }

    var isVoiceOutput by remember { mutableStateOf(viewModel.prefs.isVoiceOutputEnabled) }
    var showResetDialog by remember { mutableStateOf(false) }

    val speechRate by viewModel.speechRate.collectAsState()
    val speechPitch by viewModel.speechPitch.collectAsState()
    val chatFontSize by viewModel.chatFontSize.collectAsState()
    val selectedPersona by viewModel.selectedPersona.collectAsState()
    val personaMode by AutonomousAgentEngine.personaMode.collectAsState()

    var localSensitivity by remember { mutableFloatStateOf(viewModel.prefs.sensitivity) }
    val isEnrolled = viewModel.prefs.isEnrolled
    val isWitty = personaMode == AgentPersonaMode.WITTY_HUMOROUS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("settings_screen")
    ) {
        // ==========================================
        // TOP APP BAR: Back Navigation & Clean Header
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.setScreen(AppScreen.DASHBOARD) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberSurface)
                    .border(1.dp, CyberBorder, CircleShape)
                    .testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "वापस जाएं",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "सेटिंग्स (Settings)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeminiTextPrimary
                )
                Text(
                    text = "आर्या AI प्राथमिकताओं का प्रबंधन।",
                    fontSize = 11.5.sp,
                    color = GeminiTextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonCyan.copy(alpha = 0.12f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "v2.5 ⚡",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 1. आवाज़ एवं वाचन सेटिंग्स (VOICE & TTS)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "वॉयस आउटपुट (TTS)",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Text(
                                text = if (isVoiceOutput) "आर्या बोलकर उत्तर देगी।" else "म्यूट (केवल टेक्स्ट उत्तर)।",
                                fontSize = 11.sp,
                                color = GeminiTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isVoiceOutput,
                        onCheckedChange = { checked ->
                            isVoiceOutput = checked
                            viewModel.prefs.isVoiceOutputEnabled = checked
                            Toast.makeText(
                                context,
                                if (checked) "वॉयस आउटपुट चालू किया गया।" else "वॉयस आउटपुट म्यूट किया गया।",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("voice_output_switch")
                    )
                }

                if (isVoiceOutput) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "वॉयस पर्सोना चुनें:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Clean 2x2 Grid for compact and elegant Voice Persona selection
                    val personas = VoicePersona.entries
                    val rows = personas.chunked(2)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        rows.forEach { rowPersonas ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowPersonas.forEach { persona ->
                                    val isSelected = persona == selectedPersona
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberSurface)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else CyberBorder,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                viewModel.selectVoicePersona(persona)
                                                Toast.makeText(
                                                    context,
                                                    "${persona.displayNameHindi} आवाज़ चुनी गई।",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = persona.displayNameHindi,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) NeonCyan else GeminiTextPrimary
                                                )
                                                Text(
                                                    text = persona.badge,
                                                    fontSize = 9.sp,
                                                    color = if (isSelected) NeonCyan.copy(alpha = 0.9f) else GeminiTextSecondary
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = NeonCyan,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Speech Rate Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "बोलने की गति (Speech Rate)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = GeminiTextPrimary
                        )
                        Text(
                            text = "${"%.2f".format(speechRate)}x",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = { viewModel.updateSpeechRate(it) },
                        valueRange = 0.5f..2.0f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("speech_rate_slider")
                    )

                    // Speech Pitch Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "आवाज़ की पिच (Voice Pitch)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = GeminiTextPrimary
                        )
                        Text(
                            text = "${"%.2f".format(speechPitch)}x",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple
                        )
                    }
                    Slider(
                        value = speechPitch,
                        onValueChange = { viewModel.updateSpeechPitch(it) },
                        valueRange = 0.5f..1.5f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonPurple,
                            activeTrackColor = NeonPurple,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("speech_pitch_slider")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = { viewModel.testVoiceOutput() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "आवाज़ का परीक्षण करें (Test Voice)",
                            fontSize = 11.5.sp,
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 2. बातचीत का लहजा (CONVERSATION TONE)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background((if (isWitty) NeonPink else NeonGreen).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isWitty) NeonPink else NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "मस्ती व बेबाक मोड (Witty Mode)",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Text(
                                text = if (isWitty) "सक्रिय: बेबाक सच, सार्केस्म और मस्ती।" else "शांत: सटीक, तथ्यात्मक और पेशेवर उत्तर।",
                                fontSize = 11.sp,
                                color = if (isWitty) NeonPink else GeminiTextSecondary
                            )
                        }
                    }

                    Switch(
                        checked = isWitty,
                        onCheckedChange = { checked ->
                            val newMode = if (checked) AgentPersonaMode.WITTY_HUMOROUS else AgentPersonaMode.PROFESSIONAL
                            viewModel.setAgentPersonaMode(newMode)
                            Toast.makeText(
                                context,
                                if (checked) "मस्ती व बेबाक मोड सक्रिय किया गया।" else "सीरियस मोड सक्रिय किया गया।",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonPink,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("witty_mode_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 3. बायोमेट्रिक सुरक्षा (VOICE BIOMETRICS)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "वॉयस बायोमेट्रिक्स सुरक्षा",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Text(
                                text = if (isEnrolled) "आवाज़ इनरोल है • मालिक की पहचान सक्रिय।" else "आवाज़ इनरोल नहीं है।",
                                fontSize = 11.sp,
                                color = if (isEnrolled) SuccessGreen else NeonAmber
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background((if (isEnrolled) SuccessGreen else NeonAmber).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEnrolled) "सुरक्षित ✓" else "सेटअप करें",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnrolled) SuccessGreen else NeonAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Voice Sensitivity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "बायोमेट्रिक संवेदनशीलता",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = GeminiTextPrimary
                    )
                    Text(
                        text = "${(localSensitivity * 100).toInt()}%",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }

                Slider(
                    value = localSensitivity,
                    onValueChange = {
                        localSensitivity = it
                        viewModel.prefs.sensitivity = it
                    },
                    valueRange = 0.50f..0.95f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("voice_sensitivity_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("संतुलित (50%)", fontSize = 9.5.sp, color = GeminiTextSecondary)
                    Text(
                        text = if (localSensitivity >= 0.80f) "सख्त पहरा (80%+)" else "सामान्य सुरक्षा",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )
                    Text("कड़ा (95%)", fontSize = 9.5.sp, color = GeminiTextSecondary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.setScreen(AppScreen.VOICE_TRAINING) },
                    colors = ButtonDefaults.buttonColors(containerColor = GeminiBlueAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("train_voice_nav_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEnrolled) "आवाज़ फिर से ट्रेन करें (Re-train Voice)" else "आवाज़ ट्रेन करें (Train Voice)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 4. चैट प्रदर्शन (CHAT DISPLAY)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "चैट टेक्स्ट का आकार",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Text(
                                text = "पढ़ने में सुगमता के लिए फ़ॉन्ट साइज़।",
                                fontSize = 11.sp,
                                color = GeminiTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "${chatFontSize.toInt()} sp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Slider(
                    value = chatFontSize,
                    onValueChange = { viewModel.updateChatFontSize(it) },
                    valueRange = 12f..22f,
                    steps = 4,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonAmber,
                        activeTrackColor = NeonAmber,
                        inactiveTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("chat_font_size_slider")
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "पूर्वावलोकन: आर्या - नमस्ते भाई! यह टेक्स्ट का आकार है।",
                        fontSize = chatFontSize.sp,
                        color = GeminiTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 4.5 चयनात्मक स्मृति तंत्र (SELECTIVE MEMORY VAULT)
        // ==========================================
        com.example.ui.components.SelectiveMemoryVaultView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 4.6 प्रत्युत्तर विस्तार नियंत्रण (DYNAMIC VERBOSITY CONTROL)
        // ==========================================
        com.example.ui.components.DynamicVerbosityControlView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ==========================================
        // 5. आर्किटेक्ट एवं प्रोफाइल रीसेट (ABOUT & RESET)
        // ==========================================
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Arya AI Assistant",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeminiTextPrimary
                        )
                        Text(
                            text = "चीफ आर्किटेक्ट: मौसम कुमार राज • Gemini 3.5 Flash",
                            fontSize = 11.sp,
                            color = GeminiTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed.copy(alpha = 0.15f),
                        contentColor = ErrorRed
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "वॉइस प्रोफाइल रीसेट करें (Reset Voice Profile)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = CyberSurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(
                        text = "वॉइस प्रोफाइल रीसेट करें?",
                        color = GeminiTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "क्या आप अपनी आवाज़ का सहेजा हुआ बायोमेट्रिक डेटा मिटाकर नए सिरे से सेटअप करना चाहते हैं?",
                        color = GeminiTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.prefs.resetProfile()
                            showResetDialog = false
                            viewModel.showNotification("प्रोफाइल सफलतापूर्वक रीसेट हो गई।")
                            viewModel.setScreen(AppScreen.VOICE_TRAINING)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                    ) {
                        Text("हाँ, रीसेट करें", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("रद्द करें", color = GeminiTextSecondary)
                    }
                }
            )
        }
    }
}
