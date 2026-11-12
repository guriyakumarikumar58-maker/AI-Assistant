package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.preferences.VoiceProfilePreferences
import com.example.service.AryaVoiceBackgroundService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = VoiceProfilePreferences(context)
            if (prefs.isBackgroundVoiceServiceEnabled) {
                val serviceIntent = Intent(context, AryaVoiceBackgroundService::class.java).apply {
                    action = AryaVoiceBackgroundService.ACTION_START_SERVICE
                }
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
