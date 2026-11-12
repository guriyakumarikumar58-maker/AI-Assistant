package com.example.power

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.receiver.AryaDeviceAdminReceiver

class DevicePowerController(private val context: Context) {

    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val adminComponent = ComponentName(context, AryaDeviceAdminReceiver::class.java)

    /**
     * Checks if Arya AI is authorized as a Device Administrator to lock the screen.
     */
    fun isDeviceAdminActive(): Boolean {
        return try {
            devicePolicyManager.isAdminActive(adminComponent)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Creates an Intent to prompt the user to grant Device Administrator permission.
     */
    fun createAdminActivationIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Arya AI को आपकी आवाज़ से फ़ोन स्क्रीन बंद और लॉक करने के लिए डिवाइस एडमिन अनुमति की आवश्यकता है।"
            )
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Turns the mobile display off and locks the device securely via DevicePolicyManager.
     * Returns true if locked, false if device admin is required.
     * Note: On Android 10+ (API 29/30+), lockNow() securely locks the device.
     */
    fun turnOffAndLockScreen(): Boolean {
        return if (isDeviceAdminActive()) {
            try {
                devicePolicyManager.lockNow()
                true
            } catch (e: Exception) {
                false
            }
        } else {
            // Prompt activation intent if not enabled yet
            try {
                val intent = createAdminActivationIntent()
                context.startActivity(intent)
            } catch (_: Exception) {}
            false
        }
    }

    /**
     * Wakes the display from sleeping state.
     *
     * Modern Android (API 30+) Architecture Notice:
     * Third-party Android apps cannot bypass or dismiss a secure Keyguard/PIN/Biometric lock
     * programmatically without Samsung Knox or Device Owner provisioning.
     * Instead, Arya AI follows standard Android security architecture:
     * 1. Turns on the display via standard wake-up mechanism.
     * 2. Launches VoiceWakeOverlayActivity with setShowWhenLocked(true) and setTurnScreenOn(true),
     *    allowing full voice interaction over the lock screen without compromising the user's PIN/Keyguard security.
     */
    @Suppress("DEPRECATION")
    fun wakeUpScreen(): Boolean {
        return try {
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "AryaAI:VoiceScreenWakeLock"
            )
            wakeLock.acquire(4000L) // Keep awake for 4 seconds so display lights up
            wakeLock.release()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks whether the screen is currently interactive/on.
     */
    fun isScreenOn(): Boolean {
        return try {
            powerManager.isInteractive
        } catch (_: Exception) {
            true
        }
    }
}
