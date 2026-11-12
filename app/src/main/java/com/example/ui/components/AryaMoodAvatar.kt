package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.GeminiBlueAccent
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiDarkBg
import com.example.ui.theme.GeminiSurfaceElevated
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonViolet
import com.example.ui.viewmodel.AryaMood

@Composable
fun AryaMoodAvatar(
    mood: AryaMood,
    isThinking: Boolean = false,
    size: Dp = 90.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val activeMood = if (isThinking) AryaMood.THINKING else mood

    val moodPrimaryColor by animateColorAsState(
        targetValue = when (activeMood) {
            AryaMood.READY -> CyberNeonCyan
            AryaMood.ROASTING -> NeonMagenta
            AryaMood.CODING -> NeonEmerald
            AryaMood.BANK_GUARD -> NeonAmber
            AryaMood.CREATIVE -> NeonViolet
            AryaMood.THINKING -> GeminiBlueAccent
        },
        animationSpec = tween(400),
        label = "mood_primary"
    )

    val moodSecondaryColor by animateColorAsState(
        targetValue = when (activeMood) {
            AryaMood.READY -> GeminiBlueLight
            AryaMood.ROASTING -> NeonOrange
            AryaMood.CODING -> CyberNeonCyan
            AryaMood.BANK_GUARD -> GeminiBlueLight
            AryaMood.CREATIVE -> NeonMagenta
            AryaMood.THINKING -> NeonViolet
        },
        animationSpec = tween(400),
        label = "mood_secondary"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "avatar_animation")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isThinking) 1.15f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isThinking) 750 else 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "avatar_spin"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("arya_mood_avatar")
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size + 24.dp)
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        ) {
            // Outer Pulsing Neon Aura
            Box(
                modifier = Modifier
                    .size(size + 18.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                moodPrimaryColor.copy(alpha = 0.45f),
                                moodSecondaryColor.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Orbiting Neon Ring
            Box(
                modifier = Modifier
                    .size(size + 8.dp)
                    .rotate(rotationAngle)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                moodPrimaryColor,
                                moodSecondaryColor,
                                Color.Transparent,
                                moodPrimaryColor
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Inner Core Avatar Frame
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(GeminiDarkBg)
                    .border(2.dp, moodPrimaryColor.copy(alpha = 0.8f), CircleShape)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.ic_app_logo)
                        .crossfade(true)
                        .size(160, 160)
                        .build(),
                    contentDescription = "Arya AI Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // Dynamic Emoji Mood Floating Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 4.dp, bottom = 4.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(GeminiSurfaceElevated)
                    .border(1.5.dp, moodPrimaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeMood.badge,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Mood Title Chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(moodPrimaryColor.copy(alpha = 0.12f))
                .border(1.dp, moodPrimaryColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(moodPrimaryColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${activeMood.label} • ${activeMood.subtitle}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = moodPrimaryColor
            )
        }
    }
}
