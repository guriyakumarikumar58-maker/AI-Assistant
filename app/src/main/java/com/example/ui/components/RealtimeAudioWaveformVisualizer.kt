package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Visualizer presentation styles for voice waveform rendering.
 */
enum class WaveformVisualizerStyle(val labelHindi: String, val icon: @Composable () -> Unit) {
    SPECTRUM_BARS(
        labelHindi = "स्पेक्ट्रम बार्स",
        icon = { Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp)) }
    ),
    OSCILLOSCOPE(
        labelHindi = "ऑसिलोस्कोप",
        icon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp)) }
    ),
    BIOMETRIC_SONAR(
        labelHindi = "बायोमेट्रिक सोनार",
        icon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp)) }
    )
}

/**
 * RealtimeAudioWaveformVisualizer:
 * A high-performance 60fps Canvas audio waveform component that responds in real-time
 * to microphone input levels captured under the RECORD_AUDIO permission.
 *
 * Key Capabilities:
 * 1. Fluid, low-latency audio wave animation driven by live RMS dB input.
 * 2. Visual states tailored for voice authentication (Listening, Verifying Pitch, Verified Owner, Security Alert).
 * 3. 3 selectable high-tech rendering styles (Dual-Sided Equalizer, Continuous Oscilloscope, Biometric Radial Sonar).
 * 4. Real-time decibel energy readout and Voice Activity Detection (VAD) indicator.
 * 5. Integrated RECORD_AUDIO permission detection and seamless grant request flow.
 */
@Composable
fun RealtimeAudioWaveformVisualizer(
    isListening: Boolean,
    audioRmsDb: Float,
    modifier: Modifier = Modifier,
    detectedPitchHz: Float? = null,
    targetPitchHz: Float? = null,
    isVerified: Boolean? = null,
    isSecurityAlert: Boolean = false,
    confidenceScore: Float? = null,
    statusText: String? = null,
    showControls: Boolean = true,
    showDecibelMeter: Boolean = true,
    initialStyle: WaveformVisualizerStyle = WaveformVisualizerStyle.SPECTRUM_BARS,
    onMicToggle: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
    }

    var selectedStyle by remember { mutableStateOf(initialStyle) }
    var sensitivityMultiplier by remember { mutableFloatStateOf(1.2f) }

    // Smooth continuous phase progression for flowing waves
    val infiniteTransition = rememberInfiniteTransition(label = "realtime_audio_wave_transition")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val secondaryPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "secondary_wave_phase"
    )

    val sonarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar_pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    // Dynamic smoothing of incoming RMS dB to prevent abrupt flickering while preserving punchiness
    val normalizedRms = if (isListening && hasAudioPermission) {
        val clampedDb = audioRmsDb.coerceIn(0f, 25f)
        ((clampedDb / 10f) * sensitivityMultiplier).coerceIn(0.15f, 2.5f)
    } else {
        0.08f
    }

    val animatedRms by animateFloatAsState(
        targetValue = normalizedRms,
        animationSpec = tween(durationMillis = 75, easing = FastOutSlowInEasing),
        label = "animated_rms"
    )

    // Semantic palette according to biometric verification state
    val targetPrimaryColor = when {
        isSecurityAlert -> ErrorRed
        isVerified == true -> NeonGreen
        isListening -> CyberCyan
        else -> Color(0xFF64748B)
    }

    val targetSecondaryColor = when {
        isSecurityAlert -> Color(0xFFFF5252)
        isVerified == true -> SuccessGreen
        isListening -> NeonBlue
        else -> Color(0xFF475569)
    }

    val primaryColor by animateColorAsState(
        targetValue = targetPrimaryColor,
        animationSpec = tween(300),
        label = "primary_color"
    )

    val secondaryColor by animateColorAsState(
        targetValue = targetSecondaryColor,
        animationSpec = tween(300),
        label = "secondary_color"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D121F)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    primaryColor.copy(alpha = 0.5f),
                    secondaryColor.copy(alpha = 0.3f),
                    PurpleAccent.copy(alpha = 0.2f)
                )
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("realtime_audio_waveform_visualizer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Bar: Live Status & Decibel Readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Status Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening && hasAudioPermission) primaryColor else Color.Gray.copy(alpha = 0.6f)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            !hasAudioPermission -> "माइक अनुमति प्रतीक्षित"
                            isSecurityAlert -> "सुरक्षा चेतावनी • अजनबी आवाज़"
                            isVerified == true -> "बायोमेट्रिक मिलान • अधिकृत मालिक"
                            isListening -> if (audioRmsDb > 1f) "आवाज़ सक्रिय (Voice Active)" else "सुन रहा हूँ... बोलिए"
                            else -> "माइक स्टैंडबाय"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                if (showDecibelMeter && hasAudioPermission) {
                    Surface(
                        color = Color(0xFF1E2638),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", audioRmsDb)} dB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                            if (detectedPitchHz != null && detectedPitchHz > 0f) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${detectedPitchHz.toInt()} Hz",
                                    fontSize = 10.sp,
                                    color = NeonPink
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Visualizer Canvas or Permission Request View
            if (!hasAudioPermission) {
                // Permission Request Callout Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color(0xFF131A2B), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "माइक्रोफ़ोन अनुमति आवश्यक है",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CyberCyan
                        )
                        Text(
                            text = "रीयल-टाइम वॉयस ऑथेंटिकेशन और वेवफॉर्म विज़ुअलाइज़ेशन के लिए RECORD_AUDIO अनुमति आवश्यक है।",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = DeepNavy),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("grant_audio_permission_button")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("माइक अनुमति दें (Grant Permission)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Main Waveform Canvas Render Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(135.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF0A0F1D)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .semantics { contentDescription = "Real-time Voice Waveform Visualizer Canvas" }
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("waveform_canvas")
                    ) {
                        when (selectedStyle) {
                            WaveformVisualizerStyle.SPECTRUM_BARS -> {
                                drawSpectrumBars(
                                    isListening = isListening,
                                    animatedRms = animatedRms,
                                    phase = wavePhase,
                                    primaryColor = primaryColor,
                                    secondaryColor = secondaryColor
                                )
                            }
                            WaveformVisualizerStyle.OSCILLOSCOPE -> {
                                drawOscilloscope(
                                    isListening = isListening,
                                    animatedRms = animatedRms,
                                    phase = wavePhase,
                                    secondaryPhase = secondaryPhase,
                                    primaryColor = primaryColor,
                                    secondaryColor = secondaryColor
                                )
                            }
                            WaveformVisualizerStyle.BIOMETRIC_SONAR -> {
                                drawBiometricSonar(
                                    isListening = isListening,
                                    animatedRms = animatedRms,
                                    pulse = sonarPulse,
                                    rotation = rotationAngle,
                                    primaryColor = primaryColor,
                                    secondaryColor = secondaryColor,
                                    isVerified = isVerified,
                                    isSecurityAlert = isSecurityAlert
                                )
                            }
                        }
                    }

                    // Biometric Verification Badge Overlay (When verified or alert)
                    if (isVerified != null || isSecurityAlert) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    if (isSecurityAlert) ErrorRed.copy(alpha = 0.25f) else SuccessGreen.copy(alpha = 0.25f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSecurityAlert) ErrorRed else SuccessGreen,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSecurityAlert) Icons.Default.Warning else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isSecurityAlert) ErrorRed else SuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSecurityAlert) "अवरुद्ध" else "सत्यापित",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSecurityAlert) ErrorRed else SuccessGreen
                                )
                                if (confidenceScore != null) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${(confidenceScore * 100).toInt()}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-status text or target pitch guidance
            if (!statusText.isNullOrBlank()) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Controls & Style Switcher Row
            if (showControls) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Style Selector Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        WaveformVisualizerStyle.values().forEach { style ->
                            val isSelected = selectedStyle == style
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) primaryColor.copy(alpha = 0.22f) else Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) primaryColor else Color.Transparent
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedStyle = style }
                                    .testTag("style_chip_${style.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    style.icon()
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = style.labelHindi,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) primaryColor else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    // Mic Action Toggle Button (if provided)
                    if (onMicToggle != null) {
                        IconButton(
                            onClick = onMicToggle,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isListening) ErrorRed.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f))
                                .border(1.dp, if (isListening) ErrorRed else CyberCyan, CircleShape)
                                .testTag("waveform_mic_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = if (isListening) "माइक बंद करें" else "माइक चालू करें",
                                tint = if (isListening) ErrorRed else CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 1. Dual-sided mirrored spectrum frequency bars rendering.
 */
private fun DrawScope.drawSpectrumBars(
    isListening: Boolean,
    animatedRms: Float,
    phase: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val width = size.width
    val height = size.height
    val centerY = height / 2f
    val barCount = 36
    val barTotalWidth = width * 0.90f
    val startX = (width - barTotalWidth) / 2f
    val spacing = barTotalWidth / barCount
    val barWidth = spacing * 0.58f

    // Ambient background wave glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = if (isListening) 0.16f else 0.05f),
                secondaryColor.copy(alpha = 0.04f),
                Color.Transparent
            ),
            center = Offset(width / 2f, centerY),
            radius = width * 0.45f
        ),
        center = Offset(width / 2f, centerY),
        radius = width * 0.45f
    )

    for (i in 0 until barCount) {
        val normalizedIndex = i.toFloat() / (barCount - 1)
        val centerDist = abs(normalizedIndex - 0.5f) * 2f
        val bellEnvelope = 1f - (centerDist * 0.65f)

        // Frequency harmonic movement
        val harmonic1 = sin(phase + i * 0.38f)
        val harmonic2 = cos(phase * 1.4f + i * 0.24f)
        val variation = (harmonic1 * 0.65f + harmonic2 * 0.35f).coerceIn(-1f, 1f)

        val minHeight = 4.dp.toPx()
        val amplitudeHeight = if (isListening) {
            minHeight + (bellEnvelope * animatedRms * (height * 0.38f) * (0.4f + 0.6f * abs(variation)))
        } else {
            minHeight + (bellEnvelope * (height * 0.06f) * (0.8f + 0.2f * variation))
        }.coerceIn(minHeight, height * 0.45f)

        val barX = startX + i * spacing
        val barTop = centerY - amplitudeHeight
        val totalBarHeight = amplitudeHeight * 2f

        val barBrush = Brush.verticalGradient(
            colors = listOf(
                primaryColor,
                secondaryColor,
                PurpleAccent
            ),
            startY = barTop,
            endY = barTop + totalBarHeight
        )

        drawRoundRect(
            brush = barBrush,
            topLeft = Offset(barX, barTop),
            size = Size(barWidth, totalBarHeight),
            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
        )

        // Glowing cap dots at peaks when speech is active
        if (isListening && animatedRms > 0.4f) {
            drawCircle(
                color = Color.White,
                radius = (barWidth * 0.4f).coerceIn(1.5f, 3f),
                center = Offset(barX + barWidth / 2f, barTop - 2.dp.toPx())
            )
            drawCircle(
                color = Color.White,
                radius = (barWidth * 0.4f).coerceIn(1.5f, 3f),
                center = Offset(barX + barWidth / 2f, barTop + totalBarHeight + 2.dp.toPx())
            )
        }
    }

    // Center baseline laser guide
    drawLine(
        color = primaryColor.copy(alpha = if (isListening) 0.35f else 0.15f),
        start = Offset(startX, centerY),
        end = Offset(startX + barTotalWidth, centerY),
        strokeWidth = 1.dp.toPx()
    )
}

/**
 * 2. Continuous flowing oscilloscope waveform trace.
 */
private fun DrawScope.drawOscilloscope(
    isListening: Boolean,
    animatedRms: Float,
    phase: Float,
    secondaryPhase: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val width = size.width
    val height = size.height
    val centerY = height / 2f

    // Background horizontal baseline grids
    val gridLines = 3
    for (g in 1..gridLines) {
        val yOffset = (height / (gridLines + 1)) * g
        drawLine(
            color = Color(0xFF1E293B).copy(alpha = 0.6f),
            start = Offset(0f, yOffset),
            end = Offset(width, yOffset),
            strokeWidth = 1.dp.toPx()
        )
    }

    val primaryPath = Path()
    val fillPath = Path()
    val secondaryPath = Path()

    primaryPath.moveTo(0f, centerY)
    fillPath.moveTo(0f, height)
    fillPath.lineTo(0f, centerY)
    secondaryPath.moveTo(0f, centerY)

    val stepX = 4f
    var curX = 0f
    while (curX <= width) {
        val normX = curX / width
        val envelope = sin(normX * PI.toFloat()) // Smooth taper at edges

        val wave1 = sin(phase + normX * 8f) * (height * 0.32f) * animatedRms * envelope
        val wave2 = cos(secondaryPhase + normX * 6f) * (height * 0.14f) * animatedRms * envelope
        val y1 = centerY + wave1 + wave2

        val y2 = centerY + sin(secondaryPhase + normX * 7f + 1.2f) * (height * 0.20f) * animatedRms * envelope

        primaryPath.lineTo(curX, y1)
        fillPath.lineTo(curX, y1)
        secondaryPath.lineTo(curX, y2)

        curX += stepX
    }

    fillPath.lineTo(width, height)
    fillPath.close()

    // 1. Underlay gradient fill
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = if (isListening) 0.30f else 0.08f),
                secondaryColor.copy(alpha = if (isListening) 0.12f else 0.02f),
                Color.Transparent
            ),
            startY = centerY - (height * 0.35f),
            endY = height
        )
    )

    // 2. Secondary subtle harmonic echo line
    drawPath(
        path = secondaryPath,
        brush = Brush.horizontalGradient(
            listOf(secondaryColor.copy(alpha = 0.2f), secondaryColor.copy(alpha = 0.6f), PurpleAccent.copy(alpha = 0.2f))
        ),
        style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
    )

    // 3. Primary sharp vibrant beam line
    drawPath(
        path = primaryPath,
        brush = Brush.horizontalGradient(
            listOf(primaryColor.copy(alpha = 0.3f), primaryColor, secondaryColor, primaryColor.copy(alpha = 0.3f))
        ),
        style = Stroke(
            width = if (isListening) 2.5.dp.toPx() else 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )
}

/**
 * 3. Radial biometric acoustic sonar with concentric soundwave rings and rotating reticle.
 */
private fun DrawScope.drawBiometricSonar(
    isListening: Boolean,
    animatedRms: Float,
    pulse: Float,
    rotation: Float,
    primaryColor: Color,
    secondaryColor: Color,
    isVerified: Boolean?,
    isSecurityAlert: Boolean
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = (size.height / 2f) * 0.92f
    val baseRadius = maxRadius * 0.32f

    // 1. Concentric acoustic sonar rings expanding outward
    val ringCount = 3
    for (r in 0 until ringCount) {
        val ringProgress = (pulse + (r.toFloat() / ringCount)) % 1f
        val ringRadius = baseRadius + (maxRadius - baseRadius) * ringProgress
        val ringAlpha = (1f - ringProgress) * (if (isListening) 0.65f else 0.25f)

        drawCircle(
            color = primaryColor.copy(alpha = ringAlpha),
            radius = ringRadius,
            center = center,
            style = Stroke(
                width = (2.2f * (1f - ringProgress * 0.5f)).dp.toPx()
            )
        )
    }

    // 2. Undulating polygon perimeter reacting to live voice input levels
    val points = 36
    val polygonPath = Path()
    val angleStep = (2f * PI.toFloat()) / points.toFloat()

    for (p in 0..points) {
        val angle = p * angleStep
        val waveMod = if (isListening) {
            val harmonic = sin(angle * 4f + (rotation * PI.toFloat() / 180f))
            (harmonic * (14f * animatedRms))
        } else {
            sin(angle * 3f) * 2.5f
        }

        val r = (baseRadius + waveMod).coerceAtLeast(10f)
        val x = center.x + cos(angle) * r
        val y = center.y + sin(angle) * r

        if (p == 0) polygonPath.moveTo(x, y) else polygonPath.lineTo(x, y)
    }
    polygonPath.close()

    drawPath(
        path = polygonPath,
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = if (isListening) 0.45f else 0.15f),
                secondaryColor.copy(alpha = if (isListening) 0.18f else 0.05f),
                Color.Transparent
            ),
            center = center,
            radius = maxRadius
        )
    )

    drawPath(
        path = polygonPath,
        brush = Brush.sweepGradient(
            colors = listOf(primaryColor, secondaryColor, PurpleAccent, primaryColor),
            center = center
        ),
        style = Stroke(
            width = if (isListening) 2.5.dp.toPx() else 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    )

    // 3. Central biometric core icon & reticle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.85f),
                DeepNavy
            ),
            center = center,
            radius = baseRadius * 0.65f
        ),
        radius = baseRadius * 0.65f,
        center = center
    )

    drawCircle(
        color = Color.White,
        radius = (baseRadius * 0.22f) * (if (isListening) animatedRms.coerceIn(0.85f, 1.5f) else 1f),
        center = center
    )

    // Crosshair tick marks
    val tickLength = 8.dp.toPx()
    val tickDist = maxRadius * 0.82f
    listOf(0f, 90f, 180f, 270f).forEach { deg ->
        val rad = deg * PI.toFloat() / 180f
        val startPoint = Offset(
            center.x + cos(rad) * (tickDist - tickLength),
            center.y + sin(rad) * (tickDist - tickLength)
        )
        val endPoint = Offset(
            center.x + cos(rad) * tickDist,
            center.y + sin(rad) * tickDist
        )
        drawLine(
            color = primaryColor.copy(alpha = 0.6f),
            start = startPoint,
            end = endPoint,
            strokeWidth = 1.5.dp.toPx()
        )
    }
}
