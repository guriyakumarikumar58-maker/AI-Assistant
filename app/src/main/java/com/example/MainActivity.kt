package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.service.AryaVoiceBackgroundService
import com.example.ui.screens.CalendarNotesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GeminiChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MediaStudioScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHomeScreen
import com.example.ui.screens.TranscribeScreen
import com.example.ui.screens.VoiceTrainingScreen
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiBluePill
import com.example.ui.theme.GeminiBorder
import com.example.ui.theme.GeminiDarkBg
import com.example.ui.theme.GeminiSurface
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.VoiceLockTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLockViewModel
import com.example.util.rememberHaptic

class MainActivity : ComponentActivity() {

    private val viewModel: VoiceLockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VoiceLockTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val notification by viewModel.notification.collectAsState()
                val isIncomingCallRinging by viewModel.isIncomingCallRinging.collectAsState()
                val incomingNumber by viewModel.incomingNumber.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val haptic = rememberHaptic()

                // Multi-permission launcher for Audio, Phone State, Answer Calls, and Notifications
                val multiplePermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
                    if (audioGranted) {
                        viewModel.showNotification("ऑडियो अनुमति सक्रिय है (Audio Permission Granted)")
                    } else {
                        Toast.makeText(
                            this,
                            "Arya AI वॉयस पहचान के लिए माइक्रोफ़ोन अनुमति आवश्यक है",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    // Auto-start background service if enabled in preferences
                    if (viewModel.prefs.isBackgroundVoiceServiceEnabled) {
                        startBackgroundService()
                    }
                }

                fun requestRequiredPermissions() {
                    val permissionsNeeded = mutableListOf<String>()
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    if (permissionsNeeded.isNotEmpty()) {
                        multiplePermissionLauncher.launch(permissionsNeeded.toTypedArray())
                    } else {
                        if (viewModel.prefs.isBackgroundVoiceServiceEnabled) {
                            startBackgroundService()
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    requestRequiredPermissions()
                }

                LaunchedEffect(notification) {
                    notification?.let {
                        snackbarHostState.showSnackbar(it.message)
                        viewModel.dismissNotification()
                    }
                }

                // Handle back press - return to Dashboard if on another screen
                BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
                    haptic.tap()
                    if (currentScreen == AppScreen.GEMINI_LIVE) {
                        viewModel.endGeminiLiveSession()
                    } else {
                        viewModel.setScreen(AppScreen.DASHBOARD)
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = GeminiDarkBg,
                    bottomBar = {
                        // Per user request: Dashboard, Chat and Live are moved to the Settings menu with compact icons.
                        // On Dashboard itself, the bottom area is dedicated exclusively to the docked chat input bar.
                        if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.GEMINI_LIVE) {
                            NavigationBar(
                                containerColor = GeminiSurface,
                                contentColor = GeminiBlueLight,
                                tonalElevation = 3.dp,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = GeminiBorder.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                    )
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            ) {
                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        haptic.tap()
                                        viewModel.setScreen(AppScreen.DASHBOARD)
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard", modifier = Modifier.size(20.dp)) },
                                    label = { Text("डैशबोर्ड", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GeminiBlueLight,
                                        selectedTextColor = GeminiBlueLight,
                                        unselectedIconColor = GeminiTextSecondary,
                                        unselectedTextColor = GeminiTextSecondary,
                                        indicatorColor = GeminiBluePill
                                    ),
                                    modifier = Modifier.testTag("nav_return_dashboard")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.CALENDAR_NOTES,
                                    onClick = {
                                        haptic.tap()
                                        viewModel.setScreen(AppScreen.CALENDAR_NOTES)
                                    },
                                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar & Notes", modifier = Modifier.size(20.dp)) },
                                    label = { Text("कैलेंडर", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GeminiBlueLight,
                                        selectedTextColor = GeminiBlueLight,
                                        unselectedIconColor = GeminiTextSecondary,
                                        unselectedTextColor = GeminiTextSecondary,
                                        indicatorColor = GeminiBluePill
                                    ),
                                    modifier = Modifier.testTag("nav_calendar_notes")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.HISTORY,
                                    onClick = {
                                        haptic.tap()
                                        viewModel.setScreen(AppScreen.HISTORY)
                                    },
                                    icon = { Icon(Icons.Default.History, contentDescription = "History", modifier = Modifier.size(20.dp)) },
                                    label = { Text("इतिहास", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GeminiBlueLight,
                                        selectedTextColor = GeminiBlueLight,
                                        unselectedIconColor = GeminiTextSecondary,
                                        unselectedTextColor = GeminiTextSecondary,
                                        indicatorColor = GeminiBluePill
                                    ),
                                    modifier = Modifier.testTag("nav_history")
                                )

                                NavigationBarItem(
                                    selected = currentScreen == AppScreen.SETTINGS,
                                    onClick = {
                                        haptic.tap()
                                        viewModel.setScreen(AppScreen.SETTINGS)
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(20.dp)) },
                                    label = { Text("सेटिंग्स मेन्यू", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = GeminiBlueLight,
                                        selectedTextColor = GeminiBlueLight,
                                        unselectedIconColor = GeminiTextSecondary,
                                        unselectedTextColor = GeminiTextSecondary,
                                        indicatorColor = GeminiBluePill
                                    ),
                                    modifier = Modifier.testTag("nav_settings")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(
                            targetState = currentScreen,
                            label = "ScreenTransition"
                        ) { screen ->
                            when (screen) {
                                AppScreen.DASHBOARD -> DashboardScreen(
                                    viewModel = viewModel,
                                    onRequireAudioPermission = { requestRequiredPermissions() }
                                )
                                AppScreen.AUTONOMOUS_AGENT -> com.example.ui.screens.AutonomousAgentScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.setScreen(AppScreen.DASHBOARD) }
                                )
                                AppScreen.GEMINI_CHAT -> GeminiChatScreen(
                                    viewModel = viewModel,
                                    onRequireAudioPermission = { requestRequiredPermissions() }
                                )
                                AppScreen.GEMINI_LIVE -> com.example.ui.screens.GeminiLiveScreen(
                                    viewModel = viewModel,
                                    onRequireAudioPermission = { requestRequiredPermissions() }
                                )
                                AppScreen.MEDIA_STUDIO -> MediaStudioScreen(
                                    viewModel = viewModel
                                )
                                AppScreen.TRANSCRIBE -> TranscribeScreen(
                                    viewModel = viewModel,
                                    onRequireAudioPermission = { requestRequiredPermissions() }
                                )
                                AppScreen.VOICE_TRAINING -> VoiceTrainingScreen(
                                    viewModel = viewModel,
                                    onRequireAudioPermission = { requestRequiredPermissions() }
                                )
                                AppScreen.FREE_FIRE_EXPERT -> com.example.ui.screens.FreeFireScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.setScreen(AppScreen.DASHBOARD) }
                                )
                                AppScreen.HISTORY -> HistoryScreen(
                                    viewModel = viewModel
                                )
                                AppScreen.SMART_HOME -> SmartHomeScreen(
                                    viewModel = viewModel
                                )
                                AppScreen.CALENDAR_NOTES -> CalendarNotesScreen(
                                    viewModel = viewModel
                                )
                                AppScreen.SETTINGS -> SettingsScreen(
                                    viewModel = viewModel
                                )
                            }
                        }

                        // ==========================================
                        // INCOMING PHONE CALL FLOATING ALERT OVERLAY
                        // ==========================================
                        AnimatedVisibility(
                            visible = isIncomingCallRinging,
                            enter = slideInVertically(initialOffsetY = { -it }),
                            exit = slideOutVertically(targetOffsetY = { -it }),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2433)),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, CyberCyan),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(46.dp)
                                                .clip(CircleShape)
                                                .background(SuccessGreen.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PhoneInTalk,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "इनकमिंग फ़ोन कॉल (Incoming Call)",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = if (!incomingNumber.isNullOrBlank()) "नंबर: $incomingNumber" else "कॉल आ रही है...",
                                                color = CyberCyan,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "वॉयस कमांड: 'कॉल उठाओ' या 'कॉल काटो' बोलें",
                                                color = Color.LightGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.answerIncomingCall() },
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("कॉल उठाएं", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.rejectIncomingCall() },
                                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("काटें", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startBackgroundService() {
        val intent = Intent(this, AryaVoiceBackgroundService::class.java).apply {
            action = AryaVoiceBackgroundService.ACTION_START_SERVICE
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (_: Exception) {}
    }

    override fun onPause() {
        super.onPause()
        // Strict Privacy: Immediately stop microphone listening when user leaves the screen
        viewModel.stopListening()
    }

    override fun onStop() {
        super.onStop()
        // Ensure recording is stopped immediately when app is closed or minimized
        viewModel.stopListening()
    }
}
