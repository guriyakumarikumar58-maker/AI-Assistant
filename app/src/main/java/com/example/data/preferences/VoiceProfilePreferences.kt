package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.security.EncryptedVoiceBiometricVault
import com.example.security.VoiceprintBiometricTemplate

class VoiceProfilePreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("voicelock_prefs", Context.MODE_PRIVATE)

    init {
        // Sync encrypted vault with preferences on startup if enrolled
        if (EncryptedVoiceBiometricVault.hasEnrolledVoiceprint(context)) {
            EncryptedVoiceBiometricVault.loadDecryptedVoiceprint(context)?.let { template ->
                prefs.edit()
                    .putBoolean("is_enrolled", true)
                    .putString("owner_name", template.ownerName)
                    .putString("secret_wake_phrase", template.secretWakePhrase)
                    .putFloat("owner_pitch_hz", template.ownerPitchHz)
                    .putFloat("pitch_tolerance_hz", template.pitchToleranceHz)
                    .putFloat("sensitivity", template.sensitivity)
                    .apply()
            }
        }
    }

    private fun syncToEncryptedVault() {
        if (isEnrolled) {
            val template = VoiceprintBiometricTemplate(
                ownerName = ownerName,
                secretWakePhrase = secretWakePhrase,
                ownerPitchHz = ownerPitchHz,
                pitchToleranceHz = pitchToleranceHz,
                sensitivity = sensitivity
            )
            EncryptedVoiceBiometricVault.saveEncryptedVoiceprint(context, template)
        }
    }

    var isEnrolled: Boolean
        get() = prefs.getBoolean("is_enrolled", false)
        set(value) {
            prefs.edit().putBoolean("is_enrolled", value).apply()
            if (value) {
                syncToEncryptedVault()
            } else {
                EncryptedVoiceBiometricVault.purgeEncryptedVault(context)
            }
        }

    var ownerName: String
        get() = prefs.getString("owner_name", "मालिक (Owner)") ?: "मालिक (Owner)"
        set(value) {
            prefs.edit().putString("owner_name", value).apply()
            syncToEncryptedVault()
        }

    var secretWakePhrase: String
        get() = prefs.getString("secret_wake_phrase", "मेरी आवाज़ सुनो") ?: "मेरी आवाज़ सुनो"
        set(value) {
            prefs.edit().putString("secret_wake_phrase", value).apply()
            syncToEncryptedVault()
        }

    var ownerPitchHz: Float
        get() = prefs.getFloat("owner_pitch_hz", 165f)
        set(value) {
            prefs.edit().putFloat("owner_pitch_hz", value).apply()
            syncToEncryptedVault()
        }

    var pitchToleranceHz: Float
        get() = prefs.getFloat("pitch_tolerance_hz", 50f)
        set(value) {
            prefs.edit().putFloat("pitch_tolerance_hz", value).apply()
            syncToEncryptedVault()
        }

    var sensitivity: Float
        get() = prefs.getFloat("sensitivity", 0.70f)
        set(value) {
            prefs.edit().putFloat("sensitivity", value).apply()
            syncToEncryptedVault()
        }

    var isVoiceOutputEnabled: Boolean
        get() = prefs.getBoolean("is_voice_output_enabled", true)
        set(value) = prefs.edit().putBoolean("is_voice_output_enabled", value).apply()

    var speechRate: Float
        get() = prefs.getFloat("speech_rate", 1.0f)
        set(value) = prefs.edit().putFloat("speech_rate", value).apply()

    var speechPitch: Float
        get() = prefs.getFloat("speech_pitch", 1.0f)
        set(value) = prefs.edit().putFloat("speech_pitch", value).apply()

    var chatFontSize: Float
        get() = prefs.getFloat("chat_font_size", 14f)
        set(value) = prefs.edit().putFloat("chat_font_size", value).apply()

    var selectedPersonaId: String
        get() = prefs.getString("selected_persona_id", "ARYA_WARM_FRIENDLY") ?: "ARYA_WARM_FRIENDLY"
        set(value) = prefs.edit().putString("selected_persona_id", value).apply()

    var isBackgroundVoiceServiceEnabled: Boolean
        get() = prefs.getBoolean("is_background_voice_service_enabled", false)
        set(value) = prefs.edit().putBoolean("is_background_voice_service_enabled", value).apply()

    var isVoiceCallAnsweringEnabled: Boolean
        get() = prefs.getBoolean("is_voice_call_answering_enabled", true)
        set(value) = prefs.edit().putBoolean("is_voice_call_answering_enabled", value).apply()

    var isVoiceDevicePowerEnabled: Boolean
        get() = prefs.getBoolean("is_voice_device_power_enabled", true)
        set(value) = prefs.edit().putBoolean("is_voice_device_power_enabled", value).apply()

    var isScreenOffVoiceWakeEnabled: Boolean
        get() = prefs.getBoolean("is_screen_off_voice_wake_enabled", false)
        set(value) = prefs.edit().putBoolean("is_screen_off_voice_wake_enabled", value).apply()

    var isScreenOverlayEnabled: Boolean
        get() = prefs.getBoolean("is_screen_overlay_enabled", true)
        set(value) = prefs.edit().putBoolean("is_screen_overlay_enabled", value).apply()

    var aryaPersonalityMode: String
        get() = prefs.getString("arya_personality_mode", "MASTI") ?: "MASTI"
        set(value) = prefs.edit().putString("arya_personality_mode", value).apply()

    fun resetProfile() {
        prefs.edit().clear().apply()
        EncryptedVoiceBiometricVault.purgeEncryptedVault(context)
    }

    /**
     * Checks if local biometric vault is encrypted and uncompromised.
     */
    fun isVaultCryptographicallySecured(): Boolean {
        return EncryptedVoiceBiometricVault.verifyVaultCryptographicIntegrity(context)
    }
}
