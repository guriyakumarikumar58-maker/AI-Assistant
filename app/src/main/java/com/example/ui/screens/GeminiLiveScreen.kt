package com.example.ui.screens

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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLockViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gemini Live Fullscreen Mode with Live Camera / Screen Vision Sharing.
 * Delivers Google Gemini's signature full-duplex conversational AI experience.
 */
@Composable
fun GeminiLiveScreen(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val context = LocalContext.current
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val recognizedText by viewModel.voiceManager.recognizedText.collectAsState()
    val audioRmsDb by viewModel.voiceManager.audioRmsDb.collectAsState()
    val aiThinking by viewModel.aiThinking.collectAsState()
    val liveAiResponse by viewModel.liveAiResponse.collectAsState()
    val isCameraSharing by viewModel.isCameraSharingActive.collectAsState()
    val liveTranscriptHistory by viewModel.liveTranscriptHistory.collectAsState()

    var showTranscript by remember { mutableStateOf(true) }
    var attachedCameraUri by remember { mutableStateOf<Uri?>(null) }
    val transcriptListState = rememberLazyListState()

    // Camera Image Picker for Live Vision Sharing
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachedCameraUri = it
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    viewModel.setLiveCapturedImage(base64)
                    Toast.makeText(context, "कैमरा विज़न फ्रेम साझा किया गया", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "फ्रेम लोड करने में विफल", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        onRequireAudioPermission()
        viewModel.voiceManager.startListening()
    }

    LaunchedEffect(liveTranscriptHistory.size) {
        if (liveTranscriptHistory.isNotEmpty()) {
            transcriptListState.animateScrollToItem(liveTranscriptHistory.size - 1)
        }
    }

    // Google Gemini Midnight Theme
    val geminiObsidianBg = Color(0xFF0F1014)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(geminiObsidianBg)
            .testTag("gemini_live_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // TOP BAR: Gemini Live Brand & End Session
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini Spark",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gemini Live",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Live indicator dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isListening) SuccessGreen else Color(0xFFFF5252))
                            )
                        }
                        Text(
                            text = if (isCameraSharing) "लाइव कैमरा/विज़न शेयरिंग सक्रिय" else "निरंतर वॉयस मोड",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Transcript toggle button
                    IconButton(
                        onClick = { showTranscript = !showTranscript },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1F24))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Toggle Subtitles",
                            tint = if (showTranscript) CyberCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // End Session (X button)
                    IconButton(
                        onClick = { viewModel.endGeminiLiveSession() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF282A30))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End Live Session",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // CAMERA / VISION SHARING VIEWFINDER (IF ACTIVE)
            // ==========================================
            AnimatedVisibility(visible = isCameraSharing) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D24)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 12.dp)
                        .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "लाइव कैमरा व विज़न शेयरिंग मोड",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Gemini आपके सामने की चीज़ों को देखकर उत्तर दे सकता है",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { cameraLauncher.launch("image/*") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberCyan,
                                    contentColor = DeepNavy
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("फ़ोटो / फ़्रेम शेयर करें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // GEMINI LIVE ANIMATED GLOWING ORB CENTERPIECE
            // ==========================================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GeminiLiveOrb(
                    audioRmsDb = audioRmsDb,
                    isListening = isListening,
                    isThinking = aiThinking,
                    onClick = {
                        if (isListening) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening()
                        }
                    }
                )
            }

            // ==========================================
            // REAL-TIME SPOKEN / AI SUBTITLES
            // ==========================================
            AnimatedVisibility(visible = showTranscript) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF181A20).copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(bottom = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (aiThinking) "Gemini विचार कर रहा है..." else if (isListening) "आपकी आवाज़ सुन रहा हूँ..." else "लाइव ट्रांसक्रिप्ट",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (aiThinking) PurpleAccent else CyberCyan
                            )
                            if (aiThinking) {
                                CircularProgressIndicator(
                                    color = PurpleAccent,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Spoken input transcript or latest AI reply
                        if (recognizedText.isNotBlank()) {
                            Text(
                                text = "आप: \"$recognizedText\"",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        } else if (!liveAiResponse.isNullOrBlank()) {
                            Text(
                                text = liveAiResponse ?: "",
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFFE2E8F0),
                                maxLines = 3
                            )
                        } else {
                            Text(
                                text = "कुछ भी पूछिए, Gemini Live सीधे आवाज़ में जवाब देगा...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BOTTOM GEMINI LIVE FLOATING CONTROLS
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Camera / Screen Vision Sharing Button
                IconButton(
                    onClick = { viewModel.toggleCameraSharing() },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isCameraSharing) NeonBlue else Color(0xFF1E2129))
                ) {
                    Icon(
                        imageVector = if (isCameraSharing) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Toggle Camera Sharing",
                        tint = if (isCameraSharing) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 2. Center Mic Mute / Unmute
                IconButton(
                    onClick = {
                        if (isListening) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            onRequireAudioPermission()
                            viewModel.voiceManager.startListening()
                        }
                    },
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isListening) listOf(
                                    Color(0xFF4285F4),
                                    Color(0xFF29B6F6)
                                ) else listOf(
                                    Color(0xFFFF5252),
                                    Color(0xFFB71C1C)
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mic Toggle",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 3. Pause / Interrupt AI Speech
                IconButton(
                    onClick = { viewModel.interruptLiveSpeech() },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2129))
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Interrupt",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 4. End Live Session (Red button)
                IconButton(
                    onClick = { viewModel.endGeminiLiveSession() },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Animated Gemini Live Orb: Organic fluid glowing rings that pulse and ripple dynamically with speech.
 */
@Composable
fun GeminiLiveOrb(
    audioRmsDb: Float,
    isListening: Boolean,
    isThinking: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gemini_orb_transition")

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val normalizedRms = (audioRmsDb.coerceIn(-2f, 15f) + 2f) / 17f
    val dynamicRadiusBoost = if (isListening) normalizedRms * 35f else 0f

    Box(
        modifier = Modifier
            .size(240.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 4f) * breathingScale + dynamicRadiusBoost

            // Outer ethereal glow ring 3
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4285F4).copy(alpha = if (isListening) 0.25f else 0.1f),
                        Color(0xFF9B72CF).copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.8f
                ),
                radius = baseRadius * 1.8f,
                center = center
            )

            // Dynamic morphing petal waves (Gemini Live signature)
            val waveCount = 5
            for (i in 0 until waveCount) {
                val angle = pulsePhase + (i * (6.283f / waveCount))
                val waveOffset = Offset(
                    x = center.x + cos(angle) * (baseRadius * 0.35f),
                    y = center.y + sin(angle) * (baseRadius * 0.35f)
                )

                val waveColor = if (isThinking) {
                    PurpleAccent.copy(alpha = 0.4f)
                } else if (i % 2 == 0) {
                    Color(0xFF29B6F6).copy(alpha = 0.35f)
                } else {
                    Color(0xFF9B72CF).copy(alpha = 0.35f)
                }

                drawCircle(
                    color = waveColor,
                    radius = baseRadius * 0.65f,
                    center = waveOffset
                )
            }

            // Core Radiant Centerpiece
            drawCircle(
                brush = Brush.radialGradient(
                    colors = if (isThinking) listOf(
                        Color(0xFFD8B4FE),
                        PurpleAccent,
                        Color(0xFF3B0764)
                    ) else listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFF38BDF8),
                        Color(0xFF4285F4),
                        Color(0xFF0F172A)
                    ),
                    center = center,
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = center
            )
        }

        // Center Spark Icon
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Gemini",
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(38.dp)
        )
    }
}
