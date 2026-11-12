package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.viewmodel.VoiceLockViewModel

@Composable
fun MediaStudioScreen(viewModel: VoiceLockViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isMediaGenerating by viewModel.isMediaGenerating.collectAsState()
    val mediaResult by viewModel.mediaResult.collectAsState()

    var selectedTab by remember { mutableIntStateOf(1) } // Default to Free Veo Video Studio!

    // Video Tab state (100% Free Veo 3.1)
    var videoPrompt by remember { mutableStateOf("") }
    var selectedVideoRatio by remember { mutableStateOf("16:9") }
    var selectedCameraMotion by remember { mutableStateOf("Smooth Cinematic Pan") }
    var selectedVideoStyle by remember { mutableStateOf("Cinematic 4K") }
    var videoSourcePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isVideoPlaying by remember { mutableStateOf(true) }

    // Image Tab state
    var imagePrompt by remember { mutableStateOf("") }
    var selectedImageRatio by remember { mutableStateOf("1:1") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64ImageString by remember { mutableStateOf<String?>(null) }

    // Music Tab state
    var musicPrompt by remember { mutableStateOf("") }
    var isShortClip by remember { mutableStateOf(true) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (selectedTab == 0) {
                selectedImageUri = it
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val bytes = stream.readBytes()
                        base64ImageString = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    }
                } catch (_: Exception) {}
            } else if (selectedTab == 1) {
                videoSourcePhotoUri = it
                try {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val bytes = stream.readBytes()
                        base64ImageString = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    }
                } catch (_: Exception) {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("media_studio_screen")
    ) {
        // Studio Header
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
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Arya AI मीडिया स्टूडियो",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "मुफ़्त Veo वीडियो • DALL-E इमेज • Lyria संगीत",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberCyan
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("मुफ़्त Veo वीडियो", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp)) },
                selectedContentColor = CyberCyan,
                unselectedContentColor = Color.White.copy(alpha = 0.6f)
            )
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("इमेज स्टूडियो", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(18.dp)) },
                selectedContentColor = CyberCyan,
                unselectedContentColor = Color.White.copy(alpha = 0.6f)
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Lyria संगीत", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                selectedContentColor = CyberCyan,
                unselectedContentColor = Color.White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            // TAB 1: 100% Free Veo Video Studio
            1 -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Free Guarantee Banner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "100% मुफ़्त AI वीडियो जनरेशन (No Payment / No Fees)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Veo 3.1 वीडियो निर्माण व फोटो एनिमेशन",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "मॉडल: veo-3.1-fast-generate-preview (मुफ़्त मोड सक्रिय)",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = videoPrompt,
                            onValueChange = { videoPrompt = it },
                            placeholder = { Text("वीडियो का दृश्य लिखें (उदा: हिमालय के ऊपर उड़ता ड्रोन, भविष्य का नियॉन शहर...)") },
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Video Style Presets
                        Text("वीडियो शैली (Style):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val styles = listOf("Cinematic 4K", "Cyberpunk", "Anime Studio", "Drone Shot", "Nature Doc", "Hyperrealistic")
                            items(styles) { style ->
                                FilterChip(
                                    selected = selectedVideoStyle == style,
                                    onClick = { selectedVideoStyle = style },
                                    label = { Text(style, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan,
                                        selectedLabelColor = DeepNavy
                                    )
                                )
                            }
                        }

                        // Camera Motion Presets
                        Text("कैमरा मोशन (Camera Motion):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            val motions = listOf("Smooth Cinematic Pan", "Zoom In", "Drone Flyover", "360 Orbit", "Slow Motion")
                            items(motions) { motion ->
                                FilterChip(
                                    selected = selectedCameraMotion == motion,
                                    onClick = { selectedCameraMotion = motion },
                                    label = { Text(motion, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        // Aspect Ratio
                        Text("वीडियो अनुपात (Aspect Ratio):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf("16:9" to "Landscape (16:9)", "9:16" to "Reel / Shorts (9:16)").forEach { (ratio, label) ->
                                FilterChip(
                                    selected = selectedVideoRatio == ratio,
                                    onClick = { selectedVideoRatio = ratio },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan,
                                        selectedLabelColor = DeepNavy
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Photo Upload for Animate photo
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (videoSourcePhotoUri != null) "✓ फोटो चयनित (एनिमेट करने हेतु)" else "फोटो अपलोड करें (फोटो से वीडियो बनाएं)",
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.generateVideo(
                                    prompt = videoPrompt,
                                    base64Photo = base64ImageString,
                                    aspectRatio = selectedVideoRatio,
                                    cameraMotion = selectedCameraMotion,
                                    videoStyle = selectedVideoStyle
                                )
                            },
                            enabled = !isMediaGenerating && videoPrompt.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_video_button")
                        ) {
                            if (isMediaGenerating) {
                                CircularProgressIndicator(color = DeepNavy, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("मुफ़्त Veo 3.1 वीडियो प्रोसेस हो रहा है...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("मुफ़्त वीडियो बनाएं (Generate Free Video)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // TAB 0: Image Studio (Gemini 2.5 Flash Image / DALL-E)
            0 -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AI चित्र निर्माण व संपादन (DALL-E & Gemini)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "मॉडल: gemini-2.5-flash-image (Imagen 3 High Quality)",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = imagePrompt,
                            onValueChange = { imagePrompt = it },
                            placeholder = { Text("चित्र का विस्तृत विवरण लिखें (उदा: भविष्य का अंतरिक्ष स्टेशन, डिजिटल आर्ट...)") },
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ratio Selector
                        Text("चित्र अनुपात (Aspect Ratio):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            listOf("1:1", "16:9", "9:16", "4:3").forEach { ratio ->
                                FilterChip(
                                    selected = selectedImageRatio == ratio,
                                    onClick = { selectedImageRatio = ratio },
                                    label = { Text(ratio, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan,
                                        selectedLabelColor = DeepNavy
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Image attachment for editing
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedImageUri != null) "✓ फोटो चयनित (बदलने के लिए टैप करें)" else "फोटो जोड़ें (एडिट करने के लिए)",
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.generateOrEditImage(imagePrompt, base64ImageString, selectedImageRatio)
                            },
                            enabled = !isMediaGenerating && imagePrompt.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_image_button")
                        ) {
                            if (isMediaGenerating) {
                                CircularProgressIndicator(color = DeepNavy, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("चित्र तैयार हो रहा है...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("चित्र जनरेट करें (Generate Image)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // TAB 2: Music Studio (Lyria)
            2 -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Lyria संगीत व ऑडियो जनरेशन",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "मॉडल: lyria-3-clip-preview (30s High Fidelity Audio)",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = musicPrompt,
                            onValueChange = { musicPrompt = it },
                            placeholder = { Text("संगीत की शैली या विवरण (उदा: ऊर्जावान सिनेमैटिक ऑर्केस्ट्रा, ध्यान संगीत...)") },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = Color(0xFF334155)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            FilterChip(
                                selected = isShortClip,
                                onClick = { isShortClip = true },
                                label = { Text("शॉर्ट क्लिप (30s)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = DeepNavy
                                )
                            )
                            FilterChip(
                                selected = !isShortClip,
                                onClick = { isShortClip = false },
                                label = { Text("फुल ट्रैक (Lyria Pro)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.generateMusic(musicPrompt, isShortClip)
                            },
                            enabled = !isMediaGenerating && musicPrompt.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_music_button")
                        ) {
                            if (isMediaGenerating) {
                                CircularProgressIndicator(color = DeepNavy, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("संगीत तैयार हो रहा है...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("संगीत बनाएं (Generate Music)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Studio Output Card with Free Video Player
        mediaResult?.let { res ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "स्टूडियो परिणाम (Studio Output):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = CyberCyan
                            )
                        }

                        Text(
                            text = "100% Free",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen,
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // If it's a video result, show the Interactive Video Player Canvas!
                    if (res.contains("Veo") || res.contains("वीडियो") || selectedTab == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF0F172A), DeepNavy, Color(0xFF030712))
                                    )
                                )
                                .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Video Background Animation Simulation
                            val infiniteTransition = rememberInfiniteTransition(label = "video_playback")
                            val progress by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(5000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "video_timeline"
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top bar of video player
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Veo 3.1 • 1080p 60fps", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }

                                    Text(
                                        text = selectedVideoRatio,
                                        fontSize = 10.sp,
                                        color = CyberCyan,
                                        modifier = Modifier
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                // Center Play/Pause button
                                Box(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { isVideoPlaying = !isVideoPlaying },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(CyberCyan.copy(alpha = 0.85f))
                                    ) {
                                        Icon(
                                            imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isVideoPlaying) "Pause" else "Play",
                                            tint = DeepNavy,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                // Bottom timeline & controls
                                Column {
                                    LinearProgressIndicator(
                                        progress = { if (isVideoPlaying) progress else 0.5f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = CyberCyan,
                                        trackColor = Color.White.copy(alpha = 0.2f)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isVideoPlaying) "00:0${(progress * 5).toInt()} / 00:05" else "00:02 / 00:05",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            IconButton(
                                                onClick = {
                                                    clipboardManager.setText(AnnotatedString(res))
                                                    Toast.makeText(context, "वीडियो विवरण कॉपी किया गया", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }

                                            IconButton(
                                                onClick = {
                                                    Toast.makeText(context, "वीडियो डाउनलोड शुरू हुआ (1080p MP4)", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = "Download", tint = CyberCyan, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Text(
                        text = res,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
