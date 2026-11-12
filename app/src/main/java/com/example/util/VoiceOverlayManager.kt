package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.ui.overlay.VoiceWakeOverlayActivity

object VoiceOverlayManager {

    const val EXTRA_SPOKEN_TEXT = "extra_spoken_text"
    const val EXTRA_AI_RESPONSE = "extra_ai_response"
    const val EXTRA_MATCHED_ACTION = "extra_matched_action"

    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun createOverlaySettingsIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

    fun showVoiceWakePopup(
        context: Context,
        spokenText: String,
        aiResponse: String = "",
        matchedAction: String = ""
    ) {
        try {
            val intent = Intent(context, VoiceWakeOverlayActivity::class.java).apply {
                putExtra(EXTRA_SPOKEN_TEXT, spokenText)
                putExtra(EXTRA_AI_RESPONSE, aiResponse)
                putExtra(EXTRA_MATCHED_ACTION, matchedAction)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceOverlayManager", "Failed to launch VoiceWakeOverlayActivity: ${e.message}", e)
        }
    }
}
