package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RealtimeAudioWaveformVisualizer
import com.example.ui.components.VoiceAuthWaveCanvas
import com.example.ui.components.VoiceEnrollmentWaveCanvas
import com.example.ui.components.VoiceWaveformVisualizer
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.viewmodel.VoiceLockViewModel

@Composable
fun VoiceTrainingScreen(
    viewModel: VoiceLockViewModel,
    onRequireAudioPermission: () -> Unit
) {
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val recognizedText by viewModel.voiceManager.recognizedText.collectAsState()
    val audioRmsDb by viewModel.voiceManager.audioRmsDb.collectAsState()
    val trainingStep by viewModel.trainingStep.collectAsState()
    val latestVerification by viewModel.latestVerification.collectAsState()

    var customPhraseInput by remember { mutableStateOf(viewModel.prefs.secretWakePhrase) }
    var ownerNameInput by remember { mutableStateOf(viewModel.prefs.ownerName) }
    var sensitivityValue by remember { mutableFloatStateOf(viewModel.prefs.sensitivity) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("voice_enrollment_screen")
    ) {
        // Title Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CyberCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "वॉइस बायोमेट्रिक सेटअप",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "केवल आपकी आवाज़ से सक्रिय होगा (Only Your Voice)",
                    fontSize = 12.sp,
                    color = CyberCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step-by-Step Enrollment Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "आवाज़ प्रोफाइल इनरोलमेंट",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )

                    val statusText = if (viewModel.prefs.isEnrolled) "रजिस्टर्ड" else "सेट नहीं"
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.prefs.isEnrolled) SuccessGreen else ErrorRed,
                        modifier = Modifier
                            .background(
                                (if (viewModel.prefs.isEnrolled) SuccessGreen else ErrorRed).copy(alpha = 0.15f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Step indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StepItem(step = 1, title = "पासवर्ड बोलें", currentStep = trainingStep)
                    StepItem(step = 2, title = "पिच कैलिब्रेशन", currentStep = trainingStep)
                    StepItem(step = 3, title = "प्रोफाइल तैयार", currentStep = trainingStep)
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (trainingStep) {
                    0 -> {
                        Text(
                            text = "जब आप अपनी आवाज़ इनरोल करेंगे, तो Arya AI आपकी आवाज़ की बायोमेट्रिक विशेषताओं (पिच व वेवफॉर्म) को कैलिब्रेट करेगा। इसके बाद सिर्फ आपकी आवाज़ से ही AI संचालित हो सकेगा।",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                viewModel.startVoiceTraining()
                                viewModel.voiceManager.startListening()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_enrollment_button")
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("वॉइस इनरोलमेंट शुरू करें", fontWeight = FontWeight.Bold)
                        }
                    }
                    1 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "स्टेप 1: अपना वेक-वाक्य बोलें",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Text(
                                text = "रिकॉर्डिंग प्रगति: ${if (isListening) "50%" else "25%"}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "(उदा: \"${viewModel.prefs.secretWakePhrase}\" या अपनी पसंद का वाक्य)",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { if (isListening) 0.50f else 0.25f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("recording_progress_indicator"),
                            color = CyberCyan,
                            trackColor = SurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Waveform Feedback Component
                        VoiceEnrollmentWaveCanvas(
                            isListening = isListening,
                            audioRmsDb = audioRmsDb,
                            recordingProgress = if (isListening) 0.50f else 0.25f,
                            statusText = if (isListening) "आवाज़ रिकॉर्ड हो रही है..." else "माइक तैयार",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("enrollment_waveform_canvas")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                viewModel.voiceManager.startListening()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) ErrorRed else CyberCyan,
                                contentColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isListening) "सुन रहा हूँ... बोलिए" else "माइक चालू करें")
                        }
                    }
                    2 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "स्टेप 2: पिच कैलिब्रेशन",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonBlue
                            )
                            Text(
                                text = "रिकॉर्डिंग प्रगति: ${if (isListening) "85%" else "65%"}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = "अपनी प्राकृतिक आवाज़ में वाक्य दोहराएं...",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { if (isListening) 0.85f else 0.65f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("recording_progress_indicator"),
                            color = NeonBlue,
                            trackColor = SurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Waveform Feedback Component
                        VoiceEnrollmentWaveCanvas(
                            isListening = isListening,
                            audioRmsDb = audioRmsDb,
                            recordingProgress = if (isListening) 0.85f else 0.65f,
                            detectedPitchHz = viewModel.prefs.ownerPitchHz,
                            statusText = if (isListening) "पिच रजिस्टर हो रहा है..." else "पुनः बोलिए",
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("enrollment_waveform_canvas")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onRequireAudioPermission()
                                viewModel.voiceManager.startListening()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListening) ErrorRed else CyberCyan,
                                contentColor = DeepNavy
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isListening) "पुनः बोलिए..." else "माइक चालू करें")
                        }
                    }
                    3 -> {
                        LinearProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("recording_progress_indicator"),
                            color = SuccessGreen,
                            trackColor = SurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "आपकी वॉइस प्रोफाइल सफलतापूर्वक इनरोल हो गई!",
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "बेस पिच: ${viewModel.prefs.ownerPitchHz.toInt()} Hz | सीक्रेट वाक्य: \"${viewModel.prefs.secretWakePhrase}\"",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.finishTraining() },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = DeepNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("पूर्ण करें (Finish Enrollment)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live Voice Match Tester
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "आवाज़ सत्यापन टेस्ट (Voice Match Tester)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )

                Text(
                    text = "अपनी आवाज़ या किसी दोस्त की आवाज़ से टेस्ट करें कि सिस्टम अजनबी को ब्लॉक करता है या नहीं:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Button(
                    onClick = {
                        onRequireAudioPermission()
                        viewModel.voiceManager.startListening()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isListening) ErrorRed else SurfaceElevated,
                        contentColor = CyberCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isListening) "सुन रहा हूँ... बोलिए" else "टेस्ट करने के लिए बोलें")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time Audio Visualization Waveform Component
                RealtimeAudioWaveformVisualizer(
                    isListening = isListening,
                    audioRmsDb = audioRmsDb,
                    detectedPitchHz = if (isListening) viewModel.prefs.ownerPitchHz else null,
                    targetPitchHz = viewModel.prefs.ownerPitchHz,
                    isVerified = latestVerification?.isVerified,
                    isSecurityAlert = latestVerification?.isSecurityAlert == true,
                    confidenceScore = latestVerification?.confidence,
                    statusText = when {
                        latestVerification?.isSecurityAlert == true -> "चेतावनी: अजनबी आवाज़ पहचानकर सिस्टम ने सुरक्षा लॉक सक्रिय किया है।"
                        latestVerification?.isVerified == true -> "सफल प्रमाणीकरण: आपकी आवाज़ के पिच और बायोमेट्रिक वेवफॉर्म का 100% मिलान हुआ।"
                        isListening -> "आवाज़ का वेवफॉर्म और पिच विश्लेषण जारी है... स्पष्ट रूप से बोलें।"
                        else -> "टेस्ट शुरू करने के लिए ऊपर दिए गए माइक बटन पर टैप करें।"
                    },
                    showControls = true,
                    showDecibelMeter = true,
                    onMicToggle = {
                        onRequireAudioPermission()
                        if (isListening) {
                            viewModel.voiceManager.stopListening()
                        } else {
                            viewModel.voiceManager.startListening()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                latestVerification?.let { result ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceElevated, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (result.isVerified) "मालिक की आवाज़!" else "अजनबी आवाज़ / ब्लॉक",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (result.isVerified) SuccessGreen else ErrorRed
                            )
                            Text(
                                text = "मैच: ${(result.confidence * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CyberCyan
                            )
                        }
                        LinearProgressIndicator(
                            progress = { result.confidence },
                            color = if (result.isVerified) SuccessGreen else ErrorRed,
                            trackColor = Color(0xFF334155),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        )
                        Text(
                            text = result.statusMessage,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sensitivity Tuning
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "बायोमेट्रिक संवेदनशीलता (Sensitivity)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                Text(
                    text = "जितना अधिक रखेंगे, उतनी ही सख्ती से केवल आपकी मूल आवाज़ ही स्वीकार होगी。",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Slider(
                    value = sensitivityValue,
                    onValueChange = {
                        sensitivityValue = it
                        viewModel.prefs.sensitivity = it
                    },
                    valueRange = 0.50f..0.95f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberCyan,
                        activeTrackColor = CyberCyan,
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("संतुलित (Balanced)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("स्तर: ${(sensitivityValue * 100).toInt()}%", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                    Text("अत्यधिक सख्त (Strict)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Owner Info & Secret Phrase Editor
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "मालिक का नाम व गुप्त वाक्य",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ownerNameInput,
                    onValueChange = {
                        ownerNameInput = it
                        viewModel.prefs.ownerName = it
                    },
                    label = { Text("मालिक का नाम") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customPhraseInput,
                    onValueChange = {
                        customPhraseInput = it
                        viewModel.prefs.secretWakePhrase = it
                    },
                    label = { Text("गुप्त अनलॉक वाक्य (Secret Phrase)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun StepItem(step: Int, title: String, currentStep: Int) {
    val isCompleted = currentStep > step
    val isCurrent = currentStep == step

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> SuccessGreen
                        isCurrent -> CyberCyan
                        else -> Color(0xFF334155)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isCompleted || isCurrent) DeepNavy else Color.White
            )
        }
        Text(
            text = title,
            fontSize = 11.sp,
            color = if (isCurrent) CyberCyan else Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
