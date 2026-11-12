package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.service.AryaVoiceBackgroundService

class PhoneCallReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            val serviceIntent = Intent(context, AryaVoiceBackgroundService::class.java).apply {
                action = AryaVoiceBackgroundService.ACTION_CALL_STATE_CHANGED
                putExtra(AryaVoiceBackgroundService.EXTRA_CALL_STATE, stateStr)
                putExtra(AryaVoiceBackgroundService.EXTRA_INCOMING_NUMBER, incomingNumber)
            }

            try {
                context.startService(serviceIntent)
            } catch (_: Exception) {
                // If in background and restricted, startForegroundService if appropriate
                try {
                    context.startForegroundService(serviceIntent)
                } catch (_: Exception) {}
            }
        }
    }
}
