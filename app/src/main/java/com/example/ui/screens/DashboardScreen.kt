package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ai.AgentPersonaMode
import com.example.ai.AutonomousAgentEngine
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GeminiBlueAccent
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.GeminiTextTertiary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLockViewModel

/**
 * Arya Arya (आर्या आर्य) Minimal, Clean Assistant Dashboard:
 * 
 * - Architected by Mausam Kumar Raj (मौसम कुमार राज)
 * - Focused strictly on Core Conversation & Voice Output
 * - Top Bar: Arya Reactive Mood Avatar, Title, Architect Credit, Mode toggle, and Settings Button
 * - Center: Reactive Aura Voice Console, Listening Waveform & Clean Status
 * - Main Output Area: Live Response Card (with Markdown, Code Blocks, Copy & TTS Read-Aloud)
 * - Docked Bottom Chat Input Bar: Photo attach (+), Live Screen (GraphicEq), single-line field, Send/Mic
 * - Clutter-free: All extra hubs and shortcuts moved into Settings Icon Grid per user instructions
 */
@Composable
fun DashboardScreen(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    val allLogs by viewModel.allLogs.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val audioRmsDb by viewModel.voiceManager.audioRmsDb.collectAsState()
    val aiThinking by viewModel.aiThinking.collectAsState()
    val personaMode by AutonomousAgentEngine.personaMode.collectAsState()

    var manualTextInput by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }
    var attachedBase64Image by remember { mutableStateOf<String?>(null) }
    val extractedOcrText by viewModel.extractedOcrText.collectAsState()
    val isExtractingOcr by viewModel.isExtractingOcr.collectAsState()
    var dashboardFeedbackState by remember { mutableStateOf<String?>(null) }
    var showNegativeReasonPicker by remember { mutableStateOf(false) }
    var showDeveloperModal by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachedImageUri = it
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    attachedBase64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    Toast.makeText(context, "फोटो संलग्न की गई (Vision AI सक्रिय) 📸", Toast.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                Toast.makeText(context, "फोटो लोड करने में असमर्थ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Most recent AI response log for live presentation
    val recentAiLog = allLogs.firstOrNull { it.aiResponse != null && it.aiResponse.isNotBlank() }

    val isWitty = personaMode == AgentPersonaMode.WITTY_HUMOROUS


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .testTag("dashboard_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 12.dp, bottom = 95.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP BAR: Reactive Avatar & Settings Navigation
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Reactive Mood Avatar with Animated Glowing Aura
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    if (isListening) {
                                        listOf(Color(0xFFE53935), Color(0xFFFF5252), Color(0xFFE53935))
                                    } else if (aiThinking) {
                                        listOf(NeonPurple, NeonPink, NeonCyan, NeonPurple)
                                    } else if (isWitty) {
                                        listOf(NeonPink, NeonAmber, NeonPurple, NeonPink)
                                    } else {
                                        listOf(NeonCyan, GeminiBlueAccent, NeonGreen, NeonCyan)
                                    }
                                )
                            )
                            .padding(2.5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(R.drawable.arya_emblem_logo)
                                .crossfade(true)
                                .size(128, 128)
                                .build(),
                            contentDescription = "Arya Arya Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "आर्या आर्य",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary,
                                letterSpacing = 0.3.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        (if (isWitty) NeonPink else NeonCyan).copy(alpha = 0.15f)
                                    )
                                    .border(
                                        1.dp,
                                        (if (isWitty) NeonPink else NeonCyan).copy(alpha = 0.4f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        val newMode = if (isWitty) AgentPersonaMode.PROFESSIONAL else AgentPersonaMode.WITTY_HUMOROUS
                                        viewModel.setAgentPersonaMode(newMode)
                                        Toast.makeText(
                                            context,
                                            if (newMode == AgentPersonaMode.WITTY_HUMOROUS) "मस्ती मोड सक्रिय! 🔥" else "सीरियस मोड सक्रिय! 🧠",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isWitty) "मस्ती मोड 🔥" else "सीरियस मोड 🧠",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWitty) NeonPink else NeonCyan
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showDeveloperModal = true }
                                .padding(vertical = 2.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mausam_kumar_raj_portrait),
                                contentDescription = "मौसम कुमार राज",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, NeonCyan, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "आर्किटेक्ट: मौसम कुमार राज",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeonCyan.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // One-Click Screen Text Extraction & Copy Button
                    IconButton(
                        onClick = {
                            val responseText = recentAiLog?.aiResponse ?: "आर्या आर्य - मौसम कुमार राज द्वारा निर्मित एआई साथी।"
                            val spokenText = recentAiLog?.spokenText?.let { "प्रश्न: $it\n\n" } ?: ""
                            val fullScreenContent = "$spokenText$responseText"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Screen Text", fullScreenContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "स्क्रीन टेक्स्ट क्लिपबोर्ड पर कॉपी हो गया! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberSurface)
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            .testTag("screen_text_copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = "स्क्रीन टेक्स्ट कॉपी (Copy Screen Text)",
                            tint = NeonCyan,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { viewModel.setScreen(AppScreen.SETTINGS) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CyberSurface)
                            .border(1.dp, CyberBorder, CircleShape)
                            .testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = GeminiTextSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Real-time Voice Audio Visualizer Waveform when Listening
            AnimatedVisibility(visible = isListening) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VoiceWaveformVisualizer(
                        isListening = isListening,
                        audioRmsDb = audioRmsDb,
                        primaryColor = if (isWitty) NeonPink else NeonCyan,
                        secondaryColor = NeonPurple,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            if (aiThinking || isListening) {
                Text(
                    text = if (aiThinking) "आर्या आर्य सोच रही है..." else "सुन रही हूँ... भाई बोलो!",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = if (isListening) Color(0xFFF28B82) else NeonCyan,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ==========================================
            // 3. MAIN CONVERSATION OUTPUT AREA & OCR
            // ==========================================
            if (extractedOcrText != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1929)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ocr_extracted_text_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("छवि से निकाला गया टेक्स्ट (OCR)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            IconButton(
                                onClick = { viewModel.clearOcrText() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close OCR", tint = GeminiTextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        SelectionContainer {
                            Text(
                                text = extractedOcrText ?: "",
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("OCR Text", extractedOcrText ?: "")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "OCR टेक्स्ट क्लिपबोर्ड पर कॉपी हो गया! 📋", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("पूरा टेक्स्ट कॉपी करें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Button(
                                onClick = {
                                    manualTextInput = "इस निकाले गए टेक्स्ट का विश्लेषण करें:\n${extractedOcrText}"
                                    viewModel.clearOcrText()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("💬 सवाल पूछें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            if (recentAiLog != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isWitty) NeonPurple.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_response_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = if (isWitty) NeonPink else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "आर्या आर्य का उत्तर",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isWitty) NeonPink else NeonCyan
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Restart Button to re-generate the answer
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberCyan.copy(alpha = 0.15f))
                                        .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            val query = recentAiLog.spokenText
                                            if (query.isNotBlank()) {
                                                Toast.makeText(context, "उत्तर दोबारा तैयार किया जा रहा है...", Toast.LENGTH_SHORT).show()
                                                viewModel.sendChatMessage(query)
                                            }
                                        }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                        .testTag("dashboard_restart_response_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "रीस्टार्ट (Restart)",
                                            tint = CyberCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "रीस्टार्ट",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberCyan
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("AI Response", recentAiLog.aiResponse ?: "")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "उत्तर कॉपी हो गया!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("copy_response_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = GeminiTextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        recentAiLog.aiResponse?.let { viewModel.voiceManager.speak(it) }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("speak_response_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = if (isWitty) NeonAmber else GeminiBlueLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selectable Markdown & Code Blocks Live Parser
                        LiveResponseMarkdownView(
                            text = recentAiLog.aiResponse ?: "",
                            onRunCodeInLab = { extractedCode ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Extracted Code", extractedCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "कोड कॉपी किया गया! सेटिंग्स मेन्यू से कोड लैब खोलें।", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // ==========================================
                        // INTERACTIVE FEEDBACK LOOP & IMPROVEMENT MECHANISM
                        // ==========================================
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurfaceElevated)
                                .border(0.8.dp, CyberBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbs Up / Down with immediate action
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Thumbs Up (👍)
                                        IconButton(
                                            onClick = {
                                                dashboardFeedbackState = "POSITIVE"
                                                showNegativeReasonPicker = false
                                                viewModel.submitDetailedFeedback(
                                                    messageId = recentAiLog.id.toString(),
                                                    query = recentAiLog.spokenText,
                                                    response = recentAiLog.aiResponse ?: "",
                                                    isPositive = true,
                                                    reason = "सटीक व बेहतरीन उत्तर"
                                                )
                                            },
                                            modifier = Modifier.size(28.dp).testTag("feedback_thumbs_up_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ThumbUp,
                                                contentDescription = "लाइक (Thumbs Up)",
                                                tint = if (dashboardFeedbackState == "POSITIVE") NeonGreen else GeminiTextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        // Thumbs Down (👎)
                                        IconButton(
                                            onClick = {
                                                showNegativeReasonPicker = !showNegativeReasonPicker
                                            },
                                            modifier = Modifier.size(28.dp).testTag("feedback_thumbs_down_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ThumbDown,
                                                contentDescription = "डिसलाइक (Thumbs Down)",
                                                tint = if (dashboardFeedbackState != null && dashboardFeedbackState != "POSITIVE") ErrorRed else GeminiTextSecondary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = when {
                                                dashboardFeedbackState == "POSITIVE" -> "सहेज लिया गया 👍"
                                                dashboardFeedbackState != null -> "सुधार दर्ज: $dashboardFeedbackState"
                                                else -> "फीडबैक दें"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (dashboardFeedbackState == "POSITIVE") NeonGreen else if (dashboardFeedbackState != null) NeonAmber else GeminiTextSecondary
                                        )
                                    }

                                    // One-click Copy Shortcut right in the Feedback Bar
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonCyan.copy(alpha = 0.15f))
                                            .clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("AI Response", recentAiLog.aiResponse ?: "")
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "उत्तर कॉपी हो गया! 📋", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("कॉपी", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                        }
                                    }
                                }

                                // Interactive Reason Picker for Negative Feedback
                                if (showNegativeReasonPicker) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "सुधार का मुख्य कारण चुनें (आर्या तुरंत सुधार करेगी):",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeonAmber
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("अधूरा जवाब", "तथ्य अशुद्ध", "सुधार चाहिए", "लहजा सही नहीं").forEach { reason ->
                                            val isSelected = dashboardFeedbackState == reason
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) ErrorRed.copy(alpha = 0.2f) else CyberSurface)
                                                    .border(1.dp, if (isSelected) ErrorRed else CyberBorder, RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        dashboardFeedbackState = reason
                                                        showNegativeReasonPicker = false
                                                        viewModel.submitDetailedFeedback(
                                                            messageId = recentAiLog.id.toString(),
                                                            query = recentAiLog.spokenText,
                                                            response = recentAiLog.aiResponse ?: "",
                                                            isPositive = false,
                                                            reason = reason
                                                        )
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = reason,
                                                    fontSize = 9.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) ErrorRed else GeminiTextPrimary
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    // Instant Regenerate / Improve Button
                                    Button(
                                        onClick = {
                                            viewModel.regenerateWithFeedback(
                                                originalPrompt = recentAiLog.spokenText,
                                                negativeReason = dashboardFeedbackState ?: "सुधार चाहिए"
                                            )
                                            showNegativeReasonPicker = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("⚡ सुधार के साथ पुनः उत्तर उत्पन्न करें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Sleek Minimal Welcome Card when no recent response exists
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_welcome_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF000104))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.arya_emblem_logo),
                                contentDescription = "Arya Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "मौसम कुमार राज द्वारा निर्मित • आपकी सुपर-फास्ट AI साथी",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeonCyan,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "माइक दबाकर बोलें या नीचे टेक्स्ट बार में लिखकर कोई भी सवाल पूछें। सभी अतिरिक्त फीचर्स सेटिंग्स मेनू में सुव्यवस्थित हैं।",
                            fontSize = 12.sp,
                            color = GeminiTextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // ==========================================
        // 4. DOCKED CHAT INPUT BAR AT SCREEN BOTTOM
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            CyberDarkBg.copy(alpha = 0.90f),
                            CyberDarkBg,
                            CyberDarkBg
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Attached Photo Preview Chip
                if (attachedImageUri != null) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberSurfaceElevated)
                            .border(1.dp, NeonGreen.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = attachedImageUri,
                            contentDescription = "Attached Photo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "फोटो संलग्न है 📸",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                text = "आर्या विज़न विश्लेषण के लिए तैयार",
                                fontSize = 9.sp,
                                color = GeminiTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))

                        // Dedicated OCR Text Extraction Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(1.dp, NeonCyan, RoundedCornerShape(8.dp))
                                .clickable {
                                    attachedBase64Image?.let { viewModel.performOcrTextExtraction(it) }
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("dashboard_ocr_extract_button")
                        ) {
                            if (isExtractingOcr) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = NeonCyan, strokeWidth = 1.5.dp, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("रीड हो रहा...", fontSize = 9.sp, color = NeonCyan)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("OCR निकालें", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                attachedImageUri = null
                                attachedBase64Image = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Photo",
                                tint = ErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Plus Icon Button to Share / Attach Photo
                        IconButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (attachedImageUri != null) {
                                        Brush.linearGradient(listOf(NeonGreen, NeonCyan))
                                    } else {
                                        Brush.linearGradient(listOf(CyberSurfaceElevated, CyberSurface))
                                    }
                                )
                                .border(
                                    1.2.dp,
                                    if (attachedImageUri != null) NeonGreen else (if (isWitty) NeonPink else NeonCyan).copy(alpha = 0.6f),
                                    CircleShape
                                )
                                .testTag("dashboard_photo_plus_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "फोटो शेयर करें (Attach Photo)",
                                tint = if (attachedImageUri != null) Color.Black else (if (isWitty) NeonPink else NeonCyan),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Live Screen Button (लाइव स्क्रीन विकल्प)
                        IconButton(
                            onClick = { viewModel.setScreen(AppScreen.GEMINI_LIVE) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonPurple.copy(alpha = 0.3f), NeonCyan.copy(alpha = 0.25f))))
                                .border(1.2.dp, NeonPurple.copy(alpha = 0.8f), CircleShape)
                                .testTag("docked_live_screen_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "लाइव स्क्रीन (Live Screen)",
                                tint = NeonPurple,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        OutlinedTextField(
                            value = manualTextInput,
                            onValueChange = { manualTextInput = it },
                            placeholder = {
                                Text(
                                    text = if (attachedImageUri != null) "फोटो के बारे में पूछें..." else "आर्या से कुछ भी पूछें या + दबाएं...",
                                    fontSize = 13.sp,
                                    color = GeminiTextTertiary
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = GeminiTextPrimary,
                                unfocusedTextColor = GeminiTextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (manualTextInput.isNotBlank() || attachedBase64Image != null) {
                                        val query = manualTextInput.trim()
                                        val img = attachedBase64Image
                                        if (img != null) {
                                            viewModel.sendChatMessage(query.ifBlank { "इस फोटो को समझकर बताओ" }, img)
                                        } else {
                                            viewModel.sendTextQueryToGemini(query)
                                        }
                                        manualTextInput = ""
                                        attachedImageUri = null
                                        attachedBase64Image = null
                                    }
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("manual_text_input")
                        )

                        if (manualTextInput.isNotBlank() || attachedBase64Image != null) {
                            IconButton(
                                onClick = {
                                    val query = manualTextInput.trim()
                                    val img = attachedBase64Image
                                    if (img != null) {
                                        viewModel.sendChatMessage(query.ifBlank { "इस फोटो को समझकर बताओ" }, img)
                                    } else {
                                        viewModel.sendTextQueryToGemini(query)
                                    }
                                    manualTextInput = ""
                                    attachedImageUri = null
                                    attachedBase64Image = null
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isWitty) NeonPink else NeonCyan)
                                    .testTag("send_query_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    onRequireAudioPermission()
                                    if (isListening) viewModel.stopListening() else viewModel.startListening()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isListening) ErrorRed else (if (isWitty) NeonPink else NeonCyan))
                                    .testTag("docked_mic_send_btn")
                            ) {
                                Icon(
                                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Voice Input",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        if (showDeveloperModal) {
            AlertDialog(
                onDismissRequest = { showDeveloperModal = false },
                containerColor = CyberSurfaceElevated,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.mausam_kumar_raj_portrait),
                            contentDescription = "मौसम कुमार राज",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonCyan, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "मौसम कुमार राज",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "चीफ आर्किटेक्ट व डेवलपर",
                                fontSize = 11.sp,
                                color = NeonPink
                            )
                        }
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Arya AI Assistant के मुख्य रचयिता, निर्माता एवं विज़नरी डेवलपर मौसम कुमार राज (Mausam Kumar Raj) हैं।",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Instagram: STYLISH BOY MOUSAM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonAmber
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mausam_kumar_raj_developer),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Image(
                                painter = painterResource(id = R.drawable.mausam_kumar_raj_sunset),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Image(
                                painter = painterResource(id = R.drawable.mausam_kumar_raj_portrait),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDeveloperModal = false }) {
                        Text("सलाम भाई! 🙏", color = NeonCyan, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
}

/**
 * Live Markdown and Code Renderer for AI Responses
 * Parses code blocks ```language ... ``` cleanly without asterisks
 */
@Composable
fun LiveResponseMarkdownView(
    text: String,
    onRunCodeInLab: (String) -> Unit
) {
    val context = LocalContext.current
    val parts = remember(text) { splitMarkdownCodeBlocks(text) }

    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        parts.forEach { part ->
            when (part) {
                is MarkdownPart.CodeBlock -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF07090F)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = part.language.ifBlank { "Code" },
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                                Row {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Code", part.code)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "कोड कॉपी किया गया!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = GeminiTextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = part.code,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFFE2E8F0)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonCyan.copy(alpha = 0.15f))
                                    .clickable { onRunCodeInLab(part.code) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📋 कोड कॉपी करें",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
                }
                is MarkdownPart.Text -> {
                    Text(
                        text = part.content,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = GeminiTextPrimary
                    )
                }
            }
        }
    }
}
}

sealed class MarkdownPart {
    data class Text(val content: String) : MarkdownPart()
    data class CodeBlock(val language: String, val code: String) : MarkdownPart()
}

/**
 * Splits response text into code blocks and plain text parts.
 * Also ensures any stray asterisks are completely removed.
 */
fun splitMarkdownCodeBlocks(raw: String): List<MarkdownPart> {
    val clean = raw.replace("*", "")
    val parts = mutableListOf<MarkdownPart>()
    val codeBlockRegex = Regex("""```([a-zA-Z0-9_\-+]*)\n([\s\S]*?)```""")

    var lastIndex = 0
    val matches = codeBlockRegex.findAll(clean)

    for (match in matches) {
        val textBefore = clean.substring(lastIndex, match.range.first).trim()
        if (textBefore.isNotBlank()) {
            parts.add(MarkdownPart.Text(textBefore))
        }
        val language = match.groupValues[1].trim()
        val code = match.groupValues[2].trim()
        parts.add(MarkdownPart.CodeBlock(language, code))
        lastIndex = match.range.last + 1
    }

    if (lastIndex < clean.length) {
        val remainingText = clean.substring(lastIndex).trim()
        if (remainingText.isNotBlank()) {
            parts.add(MarkdownPart.Text(remainingText))
        }
    }

    if (parts.isEmpty()) {
        parts.add(MarkdownPart.Text(clean))
    }

    return parts
}
