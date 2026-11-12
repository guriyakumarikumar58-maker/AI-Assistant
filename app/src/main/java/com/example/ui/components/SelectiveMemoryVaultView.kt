package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SynapticMemoryEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.PurpleAccent
import com.example.ui.viewmodel.VoiceLockViewModel

/**
 * SelectiveMemoryVaultView:
 * Displays the selective memory mechanism that analyzes context to retain
 * only critical or highly personal information while automatically purging
 * transient daily chit-chat and ephemeral small talk to maximize privacy and storage.
 */
@Composable
fun SelectiveMemoryVaultView(
    viewModel: VoiceLockViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.selectiveMemoryStats.collectAsState()
    val isSelectiveActive by viewModel.selectiveMemoryEngine.isSelectiveMemoryActive.collectAsState()
    val memories by viewModel.allSelectiveMemories.collectAsState()

    var isMemoriesExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.6f), NeonPurple.copy(alpha = 0.4f)))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("selective_memory_vault_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Title, and Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "चयनात्मक स्मृति तंत्र (Selective Memory)",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeminiTextPrimary
                        )
                        Text(
                            text = "अस्थायी बातें स्वतः शुद्ध • केवल महत्वपूर्ण डेटा सुरक्षित",
                            fontSize = 10.5.sp,
                            color = NeonCyan
                        )
                    }
                }

                Switch(
                    checked = isSelectiveActive,
                    onCheckedChange = { viewModel.toggleSelectiveMemory(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("selective_memory_toggle_switch")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "आर्या का संदर्भ विश्लेषक केवल अत्यधिक व्यक्तिगत, महत्वपूर्ण पहचान व प्राथमिकताओं को याद रखता है। दैनिक कैज़ुअल बातचीत स्वतः शुद्ध कर दी जाती है ताकि आपकी प्राइवेसी और डिवाइस स्टोरेज 100% सुरक्षित रहे।",
                fontSize = 11.sp,
                color = GeminiTextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid: Storage Saved & Privacy Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Storage Optimization
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoDelete, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("स्टोरेज बचत", fontSize = 10.sp, color = GeminiTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.storageOptimizationPercent}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                        Text(
                            text = "${stats.transientPurgedCount} क्षणिक बातें शुद्ध",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Metric 2: Privacy Score
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CyberSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("प्राइवेसी शील्ड", fontSize = 10.sp, color = GeminiTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stats.privacyScorePercent}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonGreen
                        )
                        Text(
                            text = "${stats.criticalPreservedCount} महत्वपूर्ण स्मृतियाँ",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: Purge Transient Data Now Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.purgeTransientChatDetails() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("purge_transient_memory_button")
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("क्षणिक डेटा शुद्ध करें (Purge Transient)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { isMemoriesExpanded = !isMemoriesExpanded },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMemoriesExpanded) NeonCyan.copy(alpha = 0.2f) else CyberSurface,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = if (isMemoriesExpanded) "छिपाएं" else "स्मृतियाँ (${memories.size})",
                        fontSize = 11.sp
                    )
                }
            }

            // Expanded Stored Critical Memories List
            AnimatedVisibility(visible = isMemoriesExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "संरक्षित महत्वपूर्ण स्मृतियाँ (AI द्वारा याद रखी गई बातें):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (memories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurface, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "अभी कोई स्थायी व्यक्तिगत स्मृति संग्रहित नहीं है। बातचीत के दौरान महत्वपूर्ण बातें (नाम, पहचान, प्राथमिकता) स्वतः दर्ज होंगी।",
                                fontSize = 11.sp,
                                color = GeminiTextSecondary
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            memories.take(8).forEach { mem ->
                                CriticalMemoryItemRow(
                                    memory = mem,
                                    onDelete = { viewModel.deleteSelectiveMemory(mem.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CriticalMemoryItemRow(
    memory: SynapticMemoryEntity,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (memory.category) {
                                    "CRITICAL_PERSONAL" -> NeonPink.copy(alpha = 0.2f)
                                    "USER_PREFERENCE" -> NeonCyan.copy(alpha = 0.2f)
                                    "HEALTH_WELLNESS" -> NeonGreen.copy(alpha = 0.2f)
                                    else -> PurpleAccent.copy(alpha = 0.2f)
                                },
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (memory.category) {
                                "CRITICAL_PERSONAL" -> "व्यक्तिगत"
                                "USER_PREFERENCE" -> "प्राथमिकता"
                                "HEALTH_WELLNESS" -> "स्वास्थ्य"
                                else -> "महत्वपूर्ण"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (memory.category) {
                                "CRITICAL_PERSONAL" -> NeonPink
                                "USER_PREFERENCE" -> NeonCyan
                                "HEALTH_WELLNESS" -> NeonGreen
                                else -> PurpleAccent
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = memory.memoryKey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = memory.memoryValue,
                    fontSize = 11.sp,
                    color = GeminiTextSecondary,
                    maxLines = 2
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "हटाएं",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
