package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.StopScreenShare
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gemini.GeminiChatModel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.VoiceLockViewModel
import com.example.ui.components.MultiAiCollaborationHubView
import com.example.ui.components.KnowledgeGraphVisualizerView
import com.example.ui.components.EmotionalResonanceMeterView

@Composable
fun GeminiChatScreen(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val chatMessages by viewModel.chatMessages.collectAsState()
    val selectedModel by viewModel.selectedChatModel.collectAsState()
    val useGoogleSearch by viewModel.useGoogleSearch.collectAsState()
    val useGoogleMaps by viewModel.useGoogleMaps.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val aiThinking by viewModel.aiThinking.collectAsState()
    val chatFontSize by viewModel.chatFontSize.collectAsState()
    val extractedOcrText by viewModel.extractedOcrText.collectAsState()
    val isExtractingOcr by viewModel.isExtractingOcr.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }
    var attachedBase64Image by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val isScreenSharingActive by viewModel.isScreenSharingActive.collectAsState()
    val capturedScreenBase64 by viewModel.capturedScreenBase64.collectAsState()
    val screenCaptureStatus by viewModel.screenCaptureStatus.collectAsState()

    var isAttachmentMenuExpanded by remember { mutableStateOf(false) }
    var showScreenShareConsentDialog by remember { mutableStateOf(false) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }


    // Automatically attach captured screen when MediaProjection finishes
    LaunchedEffect(capturedScreenBase64) {
        if (!capturedScreenBase64.isNullOrBlank()) {
            attachedBase64Image = capturedScreenBase64
            attachedImageUri = null
            Toast.makeText(context, "स्क्रीन कैप्चर संलग्न किया गया (Vision AI)", Toast.LENGTH_SHORT).show()
        }
    }

    // Photo picker launcher for Vision features (Gallery)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachedImageUri = it
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    attachedBase64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    Toast.makeText(context, "गैलरी से फोटो संलग्न की गई (Vision AI)", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "फोटो लोड करने में असमर्थ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Photo Capture Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraImageUri != null) {
            val uri = tempCameraImageUri!!
            attachedImageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    attachedBase64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    Toast.makeText(context, "कैमरा फोटो सफलतापूर्वक संलग्न की गई!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "कैमरा फोटो पढ़ने में त्रुटि", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera Permission Launcher with runtime verification
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                val imagesDir = java.io.File(context.cacheDir, "camera")
                if (!imagesDir.exists()) imagesDir.mkdirs()
                val imageFile = java.io.File(imagesDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )
                tempCameraImageUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "कैमरा फ़ाइल तैयार करने में असमर्थ: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "फोटो खींचने के लिए कैमरा अनुमति आवश्यक है", Toast.LENGTH_SHORT).show()
        }
    }

    // MediaProjection Screen Capture Intent Launcher
    val screenCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val serviceIntent = Intent(context, com.example.service.ScreenCaptureService::class.java).apply {
                putExtra(com.example.service.ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(com.example.service.ScreenCaptureService.EXTRA_RESULT_DATA, result.data)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Toast.makeText(context, "स्क्रीन कैप्चर शुरू हो रहा है...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "स्क्रीन शेयरिंग अनुमति अस्वीकृत", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("gemini_chat_screen")
    ) {
        // Header with Model & Grounding Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "Arya AI Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, CyberCyan.copy(alpha = 0.7f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Arya AI (Google + ChatGPT)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = "${selectedModel.displayName} • ${selectedModel.providerBadge}",
                        fontSize = 11.sp,
                        color = CyberCyan
                    )
                }
            }

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Chat",
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Model Selector Chips (Google, Gemini & ChatGPT modes)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(GeminiChatModel.values()) { model ->
                FilterChip(
                    selected = selectedModel == model,
                    onClick = { viewModel.setSelectedChatModel(model) },
                    label = { Text(model.displayName, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = DeepNavy
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Grounding Toggles (Google Search & Google Maps)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = useGoogleSearch || selectedModel == GeminiChatModel.GOOGLE_SEARCH,
                onClick = { viewModel.toggleGoogleSearch(!useGoogleSearch) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                label = { Text("Google Search", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonBlue,
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = useGoogleMaps,
                onClick = { viewModel.toggleGoogleMaps(!useGoogleMaps) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                },
                label = { Text("Google Maps", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF00C853),
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 5 Core Safety & Ethics Directives Compact Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A))
                .border(0.8.dp, SuccessGreen.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("chat_safety_bar")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "सुरक्षा, गोपनीयता व आचार संहिता",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                Text(
                    text = "5 नियम सक्रिय",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))

        // Dynamic Verbosity Control Bar (Concise default, comprehensive on complexity/request)
        com.example.ui.components.DynamicVerbosityControlView(
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Action Bar (ChatGPT & Google Tools)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            val quickTools = listOf(
                "✨ रचनात्मक कहानी" to "एक काल्पनिक और प्रेरणादायक विज्ञान-कथा (Science Fiction Story) लिखें जो ब्रह्मांडीय अन्वेषण और मानवीय संवेदनाओं पर आधारित हो",
                "🌌 दार्शनिक विचार" to "समय, ब्रह्मांड के नियम और मानवीय चेतना के परस्पर संबंध पर एक गहरा दार्शनिक विचार प्रस्तुत करें",
                "🎨 कला व साहित्य" to "प्रकृति, मानवीय भावनाओं और भविष्य की कल्पना को जोड़ते हुए एक सुंदर प्रेरणादायक कविता लिखें",
                "⚛️ क्वांटम चेतना" to "क्वांटम व बहुआयामी दृष्टिकोण से वर्तमान चुनौती का 4 आयामों में विश्लेषण करें",
                "💻 कोड लिखो" to "एक उपयोगी Kotlin / Python फंक्शन लिखो",
                "📧 ईमेल ड्राफ्ट" to "एक औपचारिक अवकाश / बिजनेस ईमेल का ड्राफ्ट लिखो",
                "🧮 गणित हल" to "चरणबद्ध तरीके से इस गणितीय समस्या को हल करें",
                "🔍 लाइव सर्च" to "आज की दुनिया की शीर्ष ताज़ा खबरें क्या हैं?",
                "📝 सारांश" to "आर्टिफिशियल इंटेलिजेंस के भविष्य का संक्षेप में विवरण दो",
                "💡 आइडिया" to "एक नए टेक स्टार्टअप के 3 इनोवेटिव आइडिया बताएं"
            )
            items(quickTools) { (label, prompt) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .clickable {
                            viewModel.sendChatMessage(prompt, attachedBase64Image)
                            attachedImageUri = null
                            attachedBase64Image = null
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(label, fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        var activeEngineTab by remember { mutableStateOf("MULTI_AI") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                Triple("MULTI_AI", "मल्टी‑एआई", "🌐"),
                Triple("KNOWLEDGE_GRAPH", "नॉलेज ग्राफ", "🕸️"),
                Triple("EMOTION_RESONANCE", "इमोशनल रेसोनेंस", "💖")
            ).forEach { (tabKey, title, emoji) ->
                val isSelected = activeEngineTab == tabKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceElevated)
                        .border(0.8.dp, if (isSelected) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { activeEngineTab = tabKey }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$emoji $title",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) CyberCyan else Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        when (activeEngineTab) {
            "KNOWLEDGE_GRAPH" -> KnowledgeGraphVisualizerView(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
            "EMOTION_RESONANCE" -> EmotionalResonanceMeterView(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
            else -> MultiAiCollaborationHubView(
                viewModel = viewModel,
                isCompact = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Message Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(chatMessages) { index, message ->
                val associatedQuestion = if (message.sender == "USER") {
                    message.text
                } else {
                    chatMessages.take(index).lastOrNull { it.sender == "USER" }?.text ?: message.text
                }
                val associatedImage = if (message.sender == "USER") {
                    message.imageBase64
                } else {
                    chatMessages.take(index).lastOrNull { it.sender == "USER" }?.imageBase64
                }

                ChatMessageItem(
                    message = message,
                    fontSize = chatFontSize,
                    associatedQuestion = associatedQuestion,
                    onSpeak = { text ->
                        viewModel.voiceManager.speak(text)
                    },
                    onFeedback = { isPos, tag ->
                        viewModel.submitAiFeedback(message.id, isPos, tag)
                    },
                    onRestart = { queryToRestart ->
                        if (queryToRestart.isNotBlank()) {
                            Toast.makeText(context, "पूर्ववर्ती प्रतिक्रिया क्लियर • नॉलेज ग्राफ व मल्टी-एआई द्वारा नया परिष्कृत उत्तर तैयार हो रहा है...", Toast.LENGTH_SHORT).show()
                            viewModel.restartChatMessage(message.id, queryToRestart, associatedImage)
                        }
                    }
                )
            }

            if (aiThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = CyberCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (selectedModel == GeminiChatModel.PRO_REASONING)
                                "o1 / Gemini Pro सोच रहा है (Deep Thinking)..."
                            else
                                "Arya AI उत्तर तैयार कर रही है...",
                            fontSize = 12.sp,
                            color = CyberCyan
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Attached Image / Screen Capture Preview Chip (Vision Input)
        if (attachedImageUri != null || attachedBase64Image != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceElevated, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (attachedImageUri == null && attachedBase64Image != null) Icons.Default.ScreenShare else Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (attachedImageUri == null && attachedBase64Image != null) "स्क्रीन कैप्चर संलग्न है (Vision AI)" else "फोटो संलग्न है (Vision AI)",
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Dedicated OCR Extract Action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberCyan, RoundedCornerShape(6.dp))
                        .clickable {
                            attachedBase64Image?.let { viewModel.performOcrTextExtraction(it) }
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    if (isExtractingOcr) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = CyberCyan, strokeWidth = 1.5.dp, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("रीड हो रहा...", fontSize = 9.sp, color = CyberCyan)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("OCR टेक्स्ट निकालें", fontSize = 9.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        attachedImageUri = null
                        attachedBase64Image = null
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Extracted OCR Text Card in Chat
        if (extractedOcrText != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E2E)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("छवि से निकला टेक्स्ट (OCR)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                        IconButton(onClick = { viewModel.clearOcrText() }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Clear, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    SelectionContainer {
                        Text(
                            text = extractedOcrText ?: "",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val clipboard = LocalClipboardManager.current
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .clickable {
                                    clipboard.setText(AnnotatedString(extractedOcrText ?: ""))
                                    Toast.makeText(context, "OCR टेक्स्ट कॉपी हो गया! 📋", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("📋 पूरा कॉपी करें", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberCyan.copy(alpha = 0.2f))
                                .clickable {
                                    textInput = "इस निकाले गए टेक्स्ट का विश्लेषण करें:\n${extractedOcrText}"
                                    viewModel.clearOcrText()
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("💬 इनपुट में जोड़ें", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
                        }
                    }
                }
            }
        }

        // Input Row with Photo Picker (+), Voice Mic and Send
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plus Icon Button with Attachment Menu (Camera, Gallery, Screen Share)
            Box {
                IconButton(
                    onClick = { isAttachmentMenuExpanded = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (attachedImageUri != null || attachedBase64Image != null) {
                                androidx.compose.ui.graphics.Brush.linearGradient(listOf(SuccessGreen, CyberCyan))
                            } else {
                                androidx.compose.ui.graphics.Brush.linearGradient(listOf(SurfaceElevated, SurfaceCard))
                            }
                        )
                        .border(
                            1.2.dp,
                            if (attachedImageUri != null || attachedBase64Image != null) SuccessGreen else CyberCyan.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .testTag("chat_attachment_plus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "संलग्नक मेनू (Attachment Menu)",
                        tint = if (attachedImageUri != null || attachedBase64Image != null) DeepNavy else CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Dropdown menu triggered by Plus (+) Button
                DropdownMenu(
                    expanded = isAttachmentMenuExpanded,
                    onDismissRequest = { isAttachmentMenuExpanded = false },
                    modifier = Modifier
                        .background(Color(0xFF0F172A))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    // Option 1: Camera (Take live photo with runtime CAMERA permission)
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("कैमरा (Camera)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("नया फोटो खींचें व चैट में भेजें", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        },
                        onClick = {
                            isAttachmentMenuExpanded = false
                            val hasCameraPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CAMERA
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                            if (hasCameraPermission) {
                                try {
                                    val imagesDir = java.io.File(context.cacheDir, "camera")
                                    if (!imagesDir.exists()) imagesDir.mkdirs()
                                    val imageFile = java.io.File(imagesDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                                    val uri = androidx.core.content.FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        imageFile
                                    )
                                    tempCameraImageUri = uri
                                    takePictureLauncher.launch(uri)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "कैमरा फ़ाइल त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier.testTag("menu_option_camera")
                    )

                    // Option 2: Gallery Photo Picker
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("गैलरी (Photo Gallery)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("डिवाइस से फ़ोटो चुनें", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                }
                            }
                        },
                        onClick = {
                            isAttachmentMenuExpanded = false
                            photoPickerLauncher.launch("image/*")
                        },
                        modifier = Modifier.testTag("menu_option_gallery")
                    )

                    // Option 3: Screen Share & Capture via MediaProjection
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isScreenSharingActive) Icons.Default.StopScreenShare else Icons.Default.ScreenShare,
                                    contentDescription = null,
                                    tint = NeonBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isScreenSharingActive) "स्क्रीन शेयरिंग सक्रिय" else "स्क्रीन शेयर व कैप्चर",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "MediaProjection द्वारा स्क्रीन समझ",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        },
                        onClick = {
                            isAttachmentMenuExpanded = false
                            showScreenShareConsentDialog = true
                        },
                        modifier = Modifier.testTag("menu_option_screen_share")
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = {
                    Text(
                        if (attachedImageUri != null) "छवि के बारे में पूछें..." else "बता भाई, आज क्या तूफानी करना है? ⚡",
                        fontSize = 13.sp
                    )
                },
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (textInput.isNotBlank() || attachedBase64Image != null) {
                        viewModel.sendChatMessage(textInput, attachedBase64Image)
                        textInput = ""
                        attachedImageUri = null
                        attachedBase64Image = null
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Mic Button (Voice Chat)
            IconButton(
                onClick = {
                    if (isListening) {
                        viewModel.stopListening()
                    } else {
                        onRequireAudioPermission()
                        viewModel.startListeningForAssistant()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isListening) Color(0xFFE53935) else SurfaceElevated)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) Color.White else CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (textInput.isNotBlank() || attachedBase64Image != null) {
                        viewModel.sendChatMessage(textInput, attachedBase64Image)
                        textInput = ""
                        attachedImageUri = null
                        attachedBase64Image = null
                    }
                },
                enabled = textInput.isNotBlank() || attachedBase64Image != null,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank() || attachedBase64Image != null) CyberCyan else SurfaceElevated)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (textInput.isNotBlank() || attachedBase64Image != null) DeepNavy else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Screen Sharing & Privacy Consent Dialog (MediaProjection API)
        if (showScreenShareConsentDialog) {
            AlertDialog(
                onDismissRequest = { showScreenShareConsentDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ScreenShare,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(32.dp)
                    )
                },
                title = {
                    Text(
                        text = "स्क्रीन शेयर व विश्लेषण सहमति",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "आर्या एआई आपकी वर्तमान स्क्रीन का एक सुरक्षित स्नैपशॉट कैप्चर कर सकती है ताकि वह आपके डिस्प्ले पर चल रही सामग्री, कोड, ऐप या दस्तावेज का विश्लेषण कर सके।",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "🔒 गोपनीयता सुरक्षा: कोई भी स्क्रीन डेटा बाहरी सर्वर पर अनधिकृत रूप से स्टोर नहीं किया जाएगा। केवल आपकी स्पष्ट सहमति पर एक बार स्नैपशॉट लिया जाएगा।",
                                fontSize = 10.5.sp,
                                color = SuccessGreen
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showScreenShareConsentDialog = false
                            val mpManager = context.getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as? android.media.projection.MediaProjectionManager
                            if (mpManager != null) {
                                val captureIntent = mpManager.createScreenCaptureIntent()
                                screenCaptureLauncher.launch(captureIntent)
                            } else {
                                Toast.makeText(context, "मीडिया प्रोजेक्शन अनुपलब्ध है", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("consent_confirm_screen_share")
                    ) {
                        Text("सहमति दें व कैप्चर करें", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showScreenShareConsentDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("रद्द करें", color = Color(0xFFCBD5E1))
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    fontSize: Float = 14f,
    associatedQuestion: String = message.text,
    onSpeak: (String) -> Unit,
    onFeedback: (Boolean, String) -> Unit = { _, _ -> },
    onRestart: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    val isUser = message.sender == "USER"
    var isThinkingExpanded by remember { mutableStateOf(false) }
    var feedbackGiven by remember { mutableStateOf<String?>(null) }
    var showFeedbackTags by remember { mutableStateOf(false) }
    var isPositiveFeedback by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser && message.modelName != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "Arya AI Logo",
                    modifier = Modifier
                        .size(15.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = message.modelName,
                    fontSize = 11.sp,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
                if (message.simulationNotice != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFB74D).copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = message.simulationNotice,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB74D)
                        )
                    }
                } else if (message.isLiveCloudApi) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF06D6A0).copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "🟢 लाइव क्लाउड",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06D6A0)
                        )
                    }
                }
            }
        }

        // Expandable Thought Chain (o1 / Reasoning)
        if (!isUser && message.thoughtProcess != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(bottom = 6.dp)
                    .clickable { isThinkingExpanded = !isThinkingExpanded }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("तार्किक सोच (Reasoning Chain)", fontSize = 11.sp, color = NeonBlue, fontWeight = FontWeight.Bold)
                        }
                        Icon(
                            imageVector = if (isThinkingExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (isThinkingExpanded) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.thoughtProcess,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) CyberCyan else SurfaceCard
            ),
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.95f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // If user attached an image, display badge
                if (message.imageBase64 != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(DeepNavy.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("[छवि संलग्न]", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Render content (wrapped in SelectionContainer for Selectable Text)
                SelectionContainer {
                    RenderFormattedMessage(text = message.text, isUser = isUser, fontSize = fontSize)
                }

                // Google Grounding Sources (if available)
                if (message.searchSources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "गूगल सर्च संदर्भ (Google Web Sources):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonBlue
                    )
                    message.searchSources.forEach { query ->
                        Text(
                            text = "• $query",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }

                // Message Actions for Model Bubble: Restart, Feedback, Speak and Copy
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Feedback Thumbs
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    isPositiveFeedback = true
                                    showFeedbackTags = !showFeedbackTags
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text(
                                    text = "👍",
                                    fontSize = 12.sp,
                                    color = if (feedbackGiven != null && isPositiveFeedback) SuccessGreen else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    isPositiveFeedback = false
                                    showFeedbackTags = !showFeedbackTags
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Text(
                                    text = "👎",
                                    fontSize = 12.sp,
                                    color = if (feedbackGiven != null && !isPositiveFeedback) Color(0xFFFF5252) else Color.White
                                )
                            }
                            if (feedbackGiven != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "✓ $feedbackGiven",
                                    fontSize = 10.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Right: Restart Button, Speak and Copy
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Dedicated Restart Button under AI Bubble
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { onRestart(associatedQuestion) }
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                                    .testTag("restart_ai_button")
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

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { onSpeak(message.text) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Read Aloud",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.text))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    // Adaptive Feedback Tags Row (Auto-Improvement & Learning)
                    if (showFeedbackTags) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val tags = if (isPositiveFeedback) {
                            listOf("सटीक उत्तर", "गहन विश्लेषण", "दोस्ताना लहजा", "उत्कृष्ट कोड")
                        } else {
                            listOf("अधूरा जवाब", "तथ्य अशुद्ध", "सुधार चाहिए", "लहजा सही नहीं")
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isPositiveFeedback) Color(0xFF0C2419) else Color(0xFF2B0F17))
                                        .border(0.8.dp, if (isPositiveFeedback) SuccessGreen.copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            feedbackGiven = tag
                                            showFeedbackTags = false
                                            onFeedback(isPositiveFeedback, tag)
                                        }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 9.sp,
                                        color = if (isPositiveFeedback) SuccessGreen else Color(0xFFFF8A80),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Actions directly under User Chat Bubble: Restart & Copy
        if (isUser) {
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                modifier = Modifier.padding(end = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceElevated)
                        .border(0.8.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable { onRestart(associatedQuestion) }
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("restart_user_button")
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

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(message.text))
                    },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Prompt",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Parses markdown code blocks and displays interactive code blocks with a Copy button (ChatGPT style).
 */
@Composable
fun RenderFormattedMessage(text: String, isUser: Boolean, fontSize: Float = 14f) {
    val clipboardManager = LocalClipboardManager.current
    val textColor = if (isUser) DeepNavy else Color.White
    val sanitized = text
        .replace(Regex("""\*{3,}"""), "")
        .replace(Regex("""(?m)^//+\s*"""), "")
        .replace(Regex("""\s*//+\s*"""), " ")

    if (sanitized.contains("```")) {
        val parts = sanitized.split("```")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code Block!
                val lines = part.trim().lines()
                val lang = lines.firstOrNull()?.trim() ?: "code"
                val codeContent = if (lines.size > 1) lines.drop(1).joinToString("\n") else part

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(lang.uppercase(), fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(codeContent))
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("कॉपी कोड", fontSize = 10.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = codeContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            } else {
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.45f).sp,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    } else {
        Text(
            text = text,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.45f).sp,
            color = textColor
        )
    }
}
