package com.example.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.preferences.VoiceProfilePreferences
import com.example.power.DevicePowerController
import com.example.telephony.PhoneCallController
import com.example.util.ServiceCacheFixController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AryaVoiceBackgroundService:
 * Background system service for call notifications and device utilities.
 *
 * Privacy & Resource Guarantee:
 * - NO microphone recording is performed in the background.
 * - Continuous wake-word listening ("Hey Arya" / "Ok Arya") is completely disabled/deleted.
 * - Microphone is strictly OFF; no wake-lock or audio recording runs here.
 */
class AryaVoiceBackgroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var prefs: VoiceProfilePreferences
    private lateinit var phoneCallController: PhoneCallController
    private lateinit var devicePowerController: DevicePowerController
    private lateinit var serviceCacheFixController: ServiceCacheFixController

    private var isCallCurrentlyRinging = false

    companion object {
        const val ACTION_START_SERVICE = "com.example.service.ACTION_START"
        const val ACTION_STOP_SERVICE = "com.example.service.ACTION_STOP"
        const val ACTION_REFRESH_SERVICE = "com.example.service.ACTION_REFRESH_SERVICE"
        const val ACTION_CALL_STATE_CHANGED = "com.example.service.ACTION_CALL_STATE"
        const val ACTION_TRIGGER_VOICE_LOCK = "com.example.service.ACTION_LOCK_DEVICE"
        const val ACTION_TRIGGER_VOICE_WAKE = "com.example.service.ACTION_WAKE_DEVICE"

        const val EXTRA_CALL_STATE = "extra_call_state"
        const val EXTRA_INCOMING_NUMBER = "extra_incoming_number"

        const val NOTIFICATION_CHANNEL_ID = "arya_voice_service_channel"
        const val NOTIFICATION_ID = 2001

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _lastVoiceEvent = MutableStateFlow<String?>("बैकग्राउंड सर्विस (माइक्रोफ़ोन ऑफ • सुरक्षित)")
        val lastVoiceEvent: StateFlow<String?> = _lastVoiceEvent.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        prefs = VoiceProfilePreferences(this)
        phoneCallController = PhoneCallController(this)
        devicePowerController = DevicePowerController(this)
        serviceCacheFixController = ServiceCacheFixController(this)

        createNotificationChannel()
        startOrUpdateForeground("Arya AI सुरक्षित शील्ड (माइक्रोफ़ोन पूर्णतः बंद)", isCallActive = false)
        _isServiceRunning.value = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: ACTION_START_SERVICE

        if (action != ACTION_STOP_SERVICE) {
            startOrUpdateForeground(
                if (isCallCurrentlyRinging) "इनकमिंग कॉल आ रही है..." else "Arya AI सुरक्षित शील्ड (माइक्रोफ़ोन पूर्णतः बंद)",
                isCallActive = isCallCurrentlyRinging
            )
        }

        when (action) {
            ACTION_STOP_SERVICE -> {
                try {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                } catch (_: Exception) {}
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_REFRESH_SERVICE -> {
                _lastVoiceEvent.value = "कैश व सर्विस सफलतापूर्वक रीफ्रेश किया गया"
                startOrUpdateForeground("सर्विस व कैश रीफ्रेश संपन्न (माइक्रोफ़ोन ऑफ)", isCallActive = false)
            }
            ACTION_CALL_STATE_CHANGED -> {
                val callState = intent?.getStringExtra(EXTRA_CALL_STATE)
                val incomingNumber = intent?.getStringExtra(EXTRA_INCOMING_NUMBER)
                handleCallStateChange(callState, incomingNumber)
            }
            ACTION_TRIGGER_VOICE_LOCK -> {
                devicePowerController.turnOffAndLockScreen()
            }
            ACTION_TRIGGER_VOICE_WAKE -> {
                devicePowerController.wakeUpScreen()
            }
            else -> {}
        }

        return START_STICKY
    }

    private fun handleCallStateChange(callState: String?, incomingNumber: String?) {
        when (callState) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                isCallCurrentlyRinging = true
                phoneCallController.updateCallState(true, incomingNumber)
                _lastVoiceEvent.value = "इनकमिंग कॉल: $incomingNumber"

                val callerInfo = if (!incomingNumber.isNullOrBlank()) "नंबर: $incomingNumber" else "अज्ञात नंबर"
                startOrUpdateForeground("इनकमिंग कॉल आ रही है ($callerInfo)", isCallActive = true)
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                isCallCurrentlyRinging = false
                phoneCallController.updateCallState(false, null)
                startOrUpdateForeground("कॉल चल रही है (In Call)", isCallActive = true)
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                isCallCurrentlyRinging = false
                phoneCallController.updateCallState(false, null)
                startOrUpdateForeground("Arya AI सुरक्षित शील्ड (माइक्रोफ़ोन पूर्णतः बंद)", isCallActive = false)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Arya AI सुरक्षा शील्ड सेवा",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "सिस्टम कॉल अलर्ट व सुरक्षा शील्ड सेवा"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startOrUpdateForeground(statusText: String, isCallActive: Boolean) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Arya AI सुरक्षा शील्ड")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceRunning.value = false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
