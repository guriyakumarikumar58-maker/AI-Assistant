package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VoiceLogEntity
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.WarningGold
import com.example.ui.viewmodel.VoiceLockViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: VoiceLockViewModel) {
    val context = LocalContext.current
    val allLogs by viewModel.allLogs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearDialog by remember { mutableStateOf(false) }

    val filteredLogs = allLogs.filter { log ->
        val matchesSearch = searchQuery.isBlank() ||
                log.spokenText.contains(searchQuery, ignoreCase = true) ||
                (log.aiResponse?.contains(searchQuery, ignoreCase = true) == true)

        val matchesCategory = when (selectedFilter) {
            "AI" -> log.actionType == "AI_CHAT"
            "COMMANDS" -> log.actionType in listOf("LOCK_DEVICE", "UNLOCK_APP", "ANSWER_CALL", "REJECT_CALL")
            "ALERTS" -> log.isSecurityAlert || log.actionType == "SECURITY_ALERT"
            else -> true
        }

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("history_screen")
    ) {
        // Header
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
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "आवाज़ व सुरक्षा इतिहास",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${allLogs.size} रिकॉर्ड सुरक्षित",
                        fontSize = 12.sp,
                        color = CyberCyan
                    )
                }
            }

            if (allLogs.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("clear_all_logs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear All",
                        tint = ErrorRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("बोले गए शब्दों में खोजें...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("सभी (${allLogs.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = DeepNavy
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "AI",
                    onClick = { selectedFilter = "AI" },
                    label = { Text("जेमिनी एआई") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = DeepNavy
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "COMMANDS",
                    onClick = { selectedFilter = "COMMANDS" },
                    label = { Text("कमांड्स / लॉक") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberCyan,
                        selectedLabelColor = DeepNavy
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "ALERTS",
                    onClick = { selectedFilter = "ALERTS" },
                    label = { Text("सुरक्षा अलर्ट") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ErrorRed,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log List
        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "कोई रिकॉर्ड नहीं मिला\n(जब आप बोलेंगे, यहाँ सब सुरक्षित रहेगा)",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLogs, key = { it.id }) { log ->
                    VoiceLogCard(
                        log = log,
                        onSpeak = { text -> viewModel.voiceManager.speak(text) },
                        onDelete = { viewModel.deleteLog(log) }
                    )
                }
            }
        }

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("इतिहास साफ़ करें?") },
                text = { Text("क्या आप सभी बोले गए वॉइस लॉग और एआई बातचीत मिटाना चाहते हैं?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAllLogs()
                        showClearDialog = false
                    }) {
                        Text("हाँ, मिटाएं", color = ErrorRed)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("रद्द करें")
                    }
                }
            )
        }
    }
}

@Composable
private fun VoiceLogCard(
    log: VoiceLogEntity,
    onSpeak: (String) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val formattedTime = remember(log.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(log.timestamp))
    }

    val isAlert = log.isSecurityAlert || log.actionType == "SECURITY_ALERT"
    val isLockAction = log.actionType == "LOCK_DEVICE" || log.actionType == "UNLOCK_APP"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) Color(0xFF261014) else SurfaceCard
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Badge & Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isCallAction = log.actionType == "ANSWER_CALL" || log.actionType == "REJECT_CALL"
                    val badgeColor = when {
                        isAlert -> ErrorRed
                        isCallAction -> SuccessGreen
                        isLockAction -> NeonBlue
                        else -> CyberCyan
                    }
                    val badgeIcon = when {
                        isAlert -> Icons.Default.Warning
                        isCallAction -> Icons.Default.PhoneInTalk
                        isLockAction -> Icons.Default.Lock
                        else -> Icons.Default.AutoAwesome
                    }
                    val badgeText = when {
                        isAlert -> "सुरक्षा चेतावनी"
                        log.actionType == "ANSWER_CALL" -> "कॉल उठाई गई"
                        log.actionType == "REJECT_CALL" -> "कॉल अस्वीकृत"
                        log.actionType == "LOCK_DEVICE" -> "फ़ोन बंद व लॉक"
                        log.actionType == "UNLOCK_APP" -> "फ़ोन चालू व ऑन"
                        else -> "आर्या एआई"
                    }

                    Box(
                        modifier = Modifier
                            .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (log.speakerStatus == "VERIFIED_OWNER") "✓ मालिक" else "अज्ञात",
                        fontSize = 11.sp,
                        color = if (log.speakerStatus == "VERIFIED_OWNER") SuccessGreen else WarningGold
                    )
                }

                Text(
                    text = formattedTime,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User's Spoken Text
            Text(
                text = "बोला गया: \"${log.spokenText}\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            // AI / System Response
            log.aiResponse?.let { reply ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = reply,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = if (isAlert) ErrorRed else Color.White.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Speak, Copy, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                log.aiResponse?.let { reply ->
                    IconButton(
                        onClick = { onSpeak(reply) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("VoiceLog", reply))
                            Toast.makeText(context, "टेक्स्ट कॉपी किया गया", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
