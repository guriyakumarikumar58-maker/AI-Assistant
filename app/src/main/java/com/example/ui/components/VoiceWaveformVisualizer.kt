package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SuccessGreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-performance Canvas visual audio wave animation that responds in real-time
 * to the user's voice input (RMS dB level) to deliver immediate interaction feedback.
 */
@Composable
fun VoiceWaveformVisualizer(
    isListening: Boolean,
    audioRmsDb: Float,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    primaryColor: Color = CyberCyan,
    secondaryColor: Color = NeonBlue,
    isSecurityAlert: Boolean = false,
    isSuccess: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave_transition")
    val phase by infiniteTransition.animateFloat(
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

    // Smoothly interpolate RMS changes to prevent sudden jitter
    val animatedRms by animateFloatAsState(
        targetValue = if (isListening) (audioRmsDb.coerceIn(0f, 25f) / 12f).coerceIn(0.15f, 2.0f) else 0.08f,
        animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
        label = "animated_rms"
    )

    val activePrimaryColor = when {
        isSecurityAlert -> ErrorRed
        isSuccess -> SuccessGreen
        else -> primaryColor
    }

    val activeSecondaryColor = when {
        isSecurityAlert -> Color(0xFFFF8A80)
        isSuccess -> Color(0xFFB9F6CA)
        else -> secondaryColor
    }

    Box(
        modifier = modifier
            .height(64.dp)
            .padding(horizontal = 4.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f

            // 1. Draw Background Flowing Sine Wave (Underlay with Gradient)
            val fillWavePath = Path()
            fillWavePath.moveTo(0f, height)
            fillWavePath.lineTo(0f, centerY)

            val step = 4f
            var curX = 0f
            while (curX <= width) {
                val progress = curX / width
                val envelope = sin(progress * PI.toFloat()) // Natural taper at edges
                val wave1 = sin(phase + progress * 8f) * (height * 0.28f) * animatedRms * envelope
                val wave2 = cos(secondaryPhase + progress * 5f) * (height * 0.12f) * animatedRms * envelope
                val y = centerY + wave1 + wave2
                fillWavePath.lineTo(curX, y)
                curX += step
            }
            fillWavePath.lineTo(width, height)
            fillWavePath.close()

            drawPath(
                path = fillWavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        activePrimaryColor.copy(alpha = if (isListening) 0.35f else 0.10f),
                        activeSecondaryColor.copy(alpha = if (isListening) 0.15f else 0.04f),
                        Color.Transparent
                    )
                )
            )

            // 2. Draw Sharp Glowing Wave Crest Lines
            val crestPath1 = Path()
            val crestPath2 = Path()
            crestPath1.moveTo(0f, centerY)
            crestPath2.moveTo(0f, centerY)

            curX = 0f
            while (curX <= width) {
                val progress = curX / width
                val envelope = sin(progress * PI.toFloat())
                val y1 = centerY + sin(phase + progress * 7f) * (height * 0.32f) * animatedRms * envelope
                val y2 = centerY + sin(secondaryPhase + progress * 6f + 1f) * (height * 0.22f) * animatedRms * envelope
                crestPath1.lineTo(curX, y1)
                crestPath2.lineTo(curX, y2)
                curX += step
            }

            drawPath(
                path = crestPath1,
                brush = Brush.horizontalGradient(
                    colors = listOf(activePrimaryColor.copy(alpha = 0.4f), activePrimaryColor, activeSecondaryColor)
                ),
                style = Stroke(
                    width = if (isListening) 2.5.dp.toPx() else 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawPath(
                path = crestPath2,
                brush = Brush.horizontalGradient(
                    colors = listOf(activeSecondaryColor, activePrimaryColor.copy(alpha = 0.8f), PurpleAccent.copy(alpha = 0.5f))
                ),
                style = Stroke(
                    width = if (isListening) 1.8.dp.toPx() else 1.0.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // 3. Central Equalizer Bar Array (Direct Voice Level Response)
            val barWidth = (width / barCount) * 0.48f
            val spacing = (width - (barWidth * barCount)) / (barCount + 1)

            for (i in 0 until barCount) {
                val x = spacing + i * (barWidth + spacing)
                val barProgress = i.toFloat() / barCount
                val envelope = sin(barProgress * PI.toFloat())

                // Dynamic height reacting to speech level + harmonic phase
                val dynamicHeight = if (isListening) {
                    val harmonic = sin(phase * 1.5f + i * 0.4f)
                    val base = height * 0.16f
                    val speechPeak = (height * 0.75f) * ((harmonic + 1.2f) / 2.2f) * envelope * animatedRms
                    (base + speechPeak).coerceIn(6f, height * 0.95f)
                } else {
                    (height * 0.10f) + (sin(i * 0.5f) * 3f)
                }

                val top = (height - dynamicHeight) / 2f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(activePrimaryColor, activeSecondaryColor, PurpleAccent)
                    ),
                    topLeft = Offset(x, top),
                    size = Size(barWidth, dynamicHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}

/**
 * Concentric circular & biometric audio wave animation specifically designed for the
 * voice authentication, verification, and speaker enrollment process.
 * Provides immediate feedback with audio ripples, pitch rings, and security match state.
 */
@Composable
fun VoiceAuthWaveCanvas(
    isListening: Boolean,
    audioRmsDb: Float,
    modifier: Modifier = Modifier,
    isVerified: Boolean? = null,
    isSecurityAlert: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "auth_wave_transition")

    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_phase"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_angle"
    )

    val animatedRms by animateFloatAsState(
        targetValue = if (isListening) (audioRmsDb.coerceIn(0f, 25f) / 10f).coerceIn(0.2f, 2.2f) else 0.1f,
        animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
        label = "auth_rms"
    )

    val primaryColor = when {
        isSecurityAlert -> ErrorRed
        isVerified == true -> SuccessGreen
        else -> CyberCyan
    }

    val secondaryColor = when {
        isSecurityAlert -> Color(0xFFFF5252)
        isVerified == true -> Color(0xFF00E676)
        else -> NeonBlue
    }

    Box(
        modifier = modifier
            .height(130.dp)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.height / 2f) * 0.95f
            val baseRadius = maxRadius * 0.35f

            // 1. Draw Concentric Expanding Audio Wave Rings (Acoustic Biometric Sonar)
            val ringCount = 3
            for (r in 0 until ringCount) {
                val ringOffset = (pulsePhase + (r.toFloat() / ringCount)) % 1f
                val currentRadius = baseRadius + (maxRadius - baseRadius) * ringOffset
                val alpha = (1f - ringOffset) * (if (isListening) 0.65f else 0.20f)

                drawCircle(
                    color = primaryColor.copy(alpha = alpha),
                    radius = currentRadius,
                    center = center,
                    style = Stroke(
                        width = (2.5f * (1f - ringOffset * 0.5f)).dp.toPx()
                    )
                )
            }

            // 2. Undulating Radial Audio Waves that deform with user voice amplitude
            val pointCount = 48
            val wavePath = Path()
            val angleStep = (2 * PI) / pointCount

            for (i in 0..pointCount) {
                val angle = i * angleStep
                val waveOffset = if (isListening) {
                    val harmonic = sin((angle * 5f) + (rotationAngle * PI / 180f).toFloat())
                    (harmonic * (16f * animatedRms))
                } else {
                    sin(angle * 4f) * 3f
                }

                val radius = baseRadius + waveOffset
                val x = center.x + (cos(angle) * radius).toFloat()
                val y = center.y + (sin(angle) * radius).toFloat()

                if (i == 0) {
                    wavePath.moveTo(x, y)
                } else {
                    wavePath.lineTo(x, y)
                }
            }
            wavePath.close()

            // Draw audio aura glow
            drawPath(
                path = wavePath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = if (isListening) 0.40f else 0.15f),
                        secondaryColor.copy(alpha = if (isListening) 0.20f else 0.05f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius
                )
            )

            // Draw vibrant border for the wave
            drawPath(
                path = wavePath,
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, secondaryColor, PurpleAccent, primaryColor),
                    center = center
                ),
                style = Stroke(
                    width = if (isListening) 2.5.dp.toPx() else 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // 3. Central Biometric Core Hub
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.8f),
                        DeepNavy
                    ),
                    center = center,
                    radius = baseRadius * 0.7f
                ),
                radius = baseRadius * 0.7f,
                center = center
            )

            drawCircle(
                color = primaryColor,
                radius = (baseRadius * 0.25f) * (if (isListening) animatedRms.coerceIn(0.8f, 1.4f) else 1f),
                center = center
            )
        }
    }
}

/**
 * Visual waveform feedback component for voice profile enrollment.
 * Includes a real-time responsive audio wave canvas with dynamic recording progress bar,
 * live amplitude frequency bars, and pitch meter feedback.
 */
@Composable
fun VoiceEnrollmentWaveCanvas(
    isListening: Boolean,
    audioRmsDb: Float,
    recordingProgress: Float, // 0.0f to 1.0f
    detectedPitchHz: Float? = null,
    modifier: Modifier = Modifier,
    statusText: String = "आवाज़ रिकॉर्ड हो रही है..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "enrollment_wave_transition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = recordingProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "animated_progress"
    )

    val animatedRms by animateFloatAsState(
        targetValue = if (isListening) (audioRmsDb.coerceIn(0f, 25f) / 10f).coerceIn(0.15f, 2.2f) else 0.08f,
        animationSpec = tween(durationMillis = 80, easing = FastOutSlowInEasing),
        label = "rms"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 6.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height * 0.50f

            // 1. Background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyberCyan.copy(alpha = if (isListening) 0.18f else 0.06f),
                        NeonBlue.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(width / 2f, midY),
                    radius = width * 0.45f
                ),
                center = Offset(width / 2f, midY),
                radius = width * 0.45f
            )

            // 2. Frequency Spectrum Bars (28 bars)
            val barCount = 28
            val totalBarWidth = width * 0.85f
            val startX = (width - totalBarWidth) / 2f
            val barSpacing = totalBarWidth / barCount
            val barWidth = barSpacing * 0.55f

            for (i in 0 until barCount) {
                val normalizedIndex = i.toFloat() / (barCount - 1)
                val centerDistance = kotlin.math.abs(normalizedIndex - 0.5f) * 2f
                val envelope = 1f - (centerDistance * 0.65f)

                val harmonic1 = sin(phase + i * 0.42f)
                val harmonic2 = cos(phase * 1.5f + i * 0.28f)
                val variation = (harmonic1 * 0.6f + harmonic2 * 0.4f).coerceIn(-1f, 1f)

                val baseHeight = 6.dp.toPx()
                val dynamicHeight = if (isListening) {
                    baseHeight + (envelope * animatedRms * 32.dp.toPx() * (0.45f + 0.55f * kotlin.math.abs(variation)))
                } else {
                    baseHeight + (envelope * 4.dp.toPx() * (0.7f + 0.3f * variation))
                }.coerceIn(4.dp.toPx(), height * 0.42f)

                val barX = startX + i * barSpacing
                val barTop = midY - dynamicHeight
                val barTotalHeight = dynamicHeight * 2f

                val barBrush = Brush.verticalGradient(
                    colors = listOf(
                        CyberCyan,
                        NeonBlue,
                        PurpleAccent
                    ),
                    startY = barTop,
                    endY = barTop + barTotalHeight
                )

                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(barX, barTop),
                    size = Size(barWidth, barTotalHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }

            // 3. Smooth Harmonic Crest Line
            val crestPath = Path()
            val stepX = width / 60f
            for (j in 0..60) {
                val curX = j * stepX
                val normX = j / 60f
                val bell = 1f - kotlin.math.abs(normX - 0.5f) * 1.4f
                val waveVal = sin(phase * 2f + normX * 8f) * (if (isListening) animatedRms * 18.dp.toPx() else 4.dp.toPx()) * bell.coerceAtLeast(0.1f)
                val curY = midY - waveVal

                if (j == 0) crestPath.moveTo(curX, curY) else crestPath.lineTo(curX, curY)
            }

            drawPath(
                path = crestPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(CyberCyan.copy(alpha = 0.2f), CyberCyan, NeonBlue, CyberCyan.copy(alpha = 0.2f))
                ),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 4. Recording Progress Bar Track & Indicator (Bottom of visualizer)
            val progressY = height - 12.dp.toPx()
            val progressHeight = 6.dp.toPx()
            val progressRadius = CornerRadius(progressHeight / 2f, progressHeight / 2f)

            // Inactive track
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(startX, progressY),
                size = Size(totalBarWidth, progressHeight),
                cornerRadius = progressRadius
            )

            // Active progress fill
            if (animatedProgress > 0f) {
                val filledWidth = (totalBarWidth * animatedProgress).coerceAtLeast(progressHeight)
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(CyberCyan, SuccessGreen, NeonBlue),
                        startX = startX,
                        endX = startX + filledWidth
                    ),
                    topLeft = Offset(startX, progressY),
                    size = Size(filledWidth, progressHeight),
                    cornerRadius = progressRadius
                )

                // Glowing cursor point
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(startX + filledWidth, progressY + progressHeight / 2f)
                )
            }
        }
    }
}

