package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.translation.QuickPhrase
import com.example.translation.RealtimeTranslationEngine
import com.example.translation.TranslationLanguage
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.viewmodel.VoiceLockViewModel

@Composable
fun TranscribeScreen(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = रियल-टाइम अनुवाद, 1 = ऑडियो ट्रांसक्रिप्शन

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy)
            .testTag("transcribe_screen")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedMainTab == 0) Icons.Default.RecordVoiceOver else Icons.Default.Subtitles,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (selectedMainTab == 0) "रियल-टाइम भाषा अनुवाद" else "ऑडियो ट्रांसक्रिप्शन",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (selectedMainTab == 0) "Gemini 3.8 Flash • 20+ विश्व भाषाएँ" else "gemini-3.8-transcribe",
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Dual Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1829))
                    .border(1.dp, Color(0xFF1E2D4A), RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedMainTab == 0) CyberCyan else Color.Transparent)
                        .clickable { selectedMainTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌐 लाइव अनुवाद (Translator)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMainTab == 0) DeepNavy else Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedMainTab == 1) CyberCyan else Color.Transparent)
                        .clickable { selectedMainTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎙️ ट्रांसक्रिप्शन (Speech-to-Text)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMainTab == 1) DeepNavy else Color(0xFF94A3B8)
                    )
                }
            }
        }

        if (selectedMainTab == 0) {
            RealtimeTranslatorSection(viewModel, onRequireAudioPermission)
        } else {
            AudioTranscriptionSection(viewModel, onRequireAudioPermission)
        }
    }
}

@Composable
private fun RealtimeTranslatorSection(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    val sourceLang by viewModel.translationSourceLang.collectAsState()
    val targetLang by viewModel.translationTargetLang.collectAsState()
    val inputText by viewModel.translationInputText.collectAsState()
    val latestResult by viewModel.latestTranslationResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val history by viewModel.translationHistory.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()

    var showSourceLangDialog by remember { mutableStateOf(false) }
    var showTargetLangDialog by remember { mutableStateOf(false) }
    var translatorSubMode by remember { mutableIntStateOf(0) } // 0 = त्वरित अनुवाद, 1 = दोतरफ़ा बातचीत

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Language Selector Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF131F33))
                        .clickable { showSourceLangDialog = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = sourceLang.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = sourceLang.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = sourceLang.nativeName,
                                fontSize = 10.sp,
                                color = CyberCyan
                            )
                        }
                    }
                }

                // Swap Button
                IconButton(
                    onClick = { viewModel.swapTranslationLanguages() },
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "⇄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }

                // Target Language
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF131F33))
                        .clickable { showTargetLangDialog = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = targetLang.flag, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = targetLang.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = targetLang.nativeName,
                                fontSize = 10.sp,
                                color = Color(0xFFA5B4FC)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-mode tabs (Quick Translate vs Conversation Mode)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF111A29))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (translatorSubMode == 0) Color(0xFF1E2D4A) else Color.Transparent)
                    .clickable { translatorSubMode = 0 }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "त्वरित अनुवाद (Quick)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (translatorSubMode == 0) CyberCyan else Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (translatorSubMode == 1) Color(0xFF1E2D4A) else Color.Transparent)
                    .clickable { translatorSubMode = 1 }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "दोतरफ़ा बातचीत (Conversation)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (translatorSubMode == 1) CyberCyan else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (translatorSubMode == 0) {
            // ==========================================
            // QUICK TRANSLATE SUB-MODE
            // ==========================================
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${sourceLang.flag} ${sourceLang.name} इनपुट:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                        if (inputText.isNotBlank()) {
                            TextButton(onClick = { viewModel.updateTranslationInput("") }) {
                                Text("साफ़ करें", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateTranslationInput(it) },
                        placeholder = { Text("यहाँ लिखें या माइक से बोलें...", fontSize = 13.sp, color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .testTag("translate_input_field"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                if (isListening) {
                                    viewModel.stopListening()
                                } else {
                                    viewModel.startTranslationVoice(speakerId = 1)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) Color(0xFFFF5252) else Color(0xFF1E2D4A),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isListening) Color.White else CyberCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListening) "सुन रही हूँ..." else "बोलें (${sourceLang.code})",
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.executeTranslation(inputText, speakerId = 1, speakOutput = true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                contentColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            if (isTranslating) {
                                CircularProgressIndicator(color = DeepNavy, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("अनुवाद हो रहा है...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text("अनुवाद करें (${targetLang.name})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Translated Output Card
            latestResult?.let { result ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1B2E)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().testTag("translation_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = result.targetLang.flag, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${result.targetLang.name} अनुवाद:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { viewModel.speakTranslation(result.translatedText, result.targetLang.ttsTag) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Translation", result.translatedText))
                                        Toast.makeText(context, "अनुवाद कॉपी किया गया", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result.translatedText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )

                        result.phonetic?.let { phon ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "उच्चारण: $phon",
                                fontSize = 12.sp,
                                color = CyberCyan.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Quick Travel & Conversation Phrases
            Text(
                text = "⚡ त्वरित दैनिक व यात्रा वाक्य (Quick Phrases):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            RealtimeTranslationEngine.quickPhrases.take(8).forEach { phrase ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E30)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val textToTranslate = if (sourceLang.code == "hi") phrase.textHindi else phrase.textEnglish
                            viewModel.executeTranslation(textToTranslate, speakerId = 1, speakOutput = true)
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(text = phrase.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = phrase.textHindi, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                                Text(text = phrase.textEnglish, fontSize = 10.sp, color = Color(0xFF94A3B8))
                            }
                        }
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
        } else {
            // ==========================================
            // TWO-WAY CONVERSATION SUB-MODE
            // ==========================================
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1829)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "दो-तरफ़ा बातचीत का इतिहास (${history.size}):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearTranslationHistory() }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (history.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "नीचे दिए गए दोनों बटनों में से किसी एक को दबाकर अपनी भाषा में बात शुरू करें।",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            history.forEach { item ->
                                val isSpeaker1 = item.speakerId == 1
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSpeaker1) Color(0xFF16253D) else Color(0xFF221C38)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSpeaker1) CyberCyan.copy(alpha = 0.3f) else Color(0xFF818CF8).copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isSpeaker1) "${item.sourceLang.flag} ${item.sourceLang.name}" else "${item.sourceLang.flag} ${item.sourceLang.name}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSpeaker1) CyberCyan else Color(0xFFA5B4FC)
                                            )
                                            IconButton(
                                                onClick = { viewModel.speakTranslation(item.translatedText, item.targetLang.ttsTag) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Speak",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Text(text = item.originalText, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "➔ ${item.targetLang.flag} ${item.translatedText}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dual Live Voice Input Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                if (isListening) viewModel.stopListening() else viewModel.startTranslationVoice(speakerId = 1)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) Color(0xFFFF5252) else CyberCyan,
                                contentColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "${sourceLang.flag} ${sourceLang.name} बोलें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                if (isListening) viewModel.stopListening() else viewModel.startTranslationVoice(speakerId = 2)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) Color(0xFFFF5252) else Color(0xFF818CF8),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "${targetLang.flag} ${targetLang.name} बोलें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Source Language Picker Modal
    if (showSourceLangDialog) {
        LanguagePickerDialog(
            title = "स्रोत भाषा चुनें (Source Language)",
            languages = RealtimeTranslationEngine.supportedLanguages,
            currentSelected = sourceLang,
            onSelect = {
                viewModel.setTranslationSourceLang(it)
                showSourceLangDialog = false
            },
            onDismiss = { showSourceLangDialog = false }
        )
    }

    // Target Language Picker Modal
    if (showTargetLangDialog) {
        LanguagePickerDialog(
            title = "लक्ष्य भाषा चुनें (Target Language)",
            languages = RealtimeTranslationEngine.supportedLanguages,
            currentSelected = targetLang,
            onSelect = {
                viewModel.setTranslationTargetLang(it)
                showTargetLangDialog = false
            },
            onDismiss = { showTargetLangDialog = false }
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    title: String,
    languages: List<TranslationLanguage>,
    currentSelected: TranslationLanguage,
    onSelect: (TranslationLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                languages.forEach { lang ->
                    val isSelected = lang.code == currentSelected.code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { onSelect(lang) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = lang.flag, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = lang.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberCyan else Color.White
                                )
                                Text(
                                    text = "${lang.nativeName} (${lang.englishName})",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        if (isSelected) {
                            Text("✓", color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें", color = CyberCyan)
            }
        },
        containerColor = Color(0xFF131D2E),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AudioTranscriptionSection(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val audioRmsDb by viewModel.voiceManager.audioRmsDb.collectAsState()
    val recognizedText by viewModel.voiceManager.recognizedText.collectAsState()
    val isTranscribing by viewModel.isTranscribing.collectAsState()
    val transcriptionResult by viewModel.transcriptionResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Transcription Mic Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "माइक्रोफोन से ऑडियो ट्रांसक्रिप्शन",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "बोलिए, gemini-3.5-transcribe इसे सटीक टेक्स्ट में बदल देगा",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                VoiceWaveformVisualizer(
                    isListening = isListening,
                    audioRmsDb = audioRmsDb,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isListening) {
                            viewModel.stopListening()
                        } else {
                            onRequireAudioPermission()
                            viewModel.startListening()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) Color(0xFFFF5252) else CyberCyan,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("transcribe_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isListening) "रिकॉर्डिंग रोकें (Stop)" else "ऑडियो रिकॉर्ड करना शुरू करें",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isTranscribing) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = CyberCyan, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("gemini-3.5-transcribe ट्रांसक्राइब कर रहा है...", fontSize = 12.sp, color = CyberCyan)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Output Card
        val displayOutput = transcriptionResult ?: recognizedText.ifBlank { null }
        displayOutput?.let { text ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ट्रांसक्रिप्ट टेक्स्ट (Transcribed Text):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CyberCyan
                        )

                        Row {
                            IconButton(
                                onClick = { viewModel.voiceManager.speak(text) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speak",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Transcript", text))
                                    Toast.makeText(context, "ट्रांसक्रिप्ट कॉपी की गई", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = text,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
