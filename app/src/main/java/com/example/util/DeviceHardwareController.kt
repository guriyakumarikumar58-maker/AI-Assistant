package com.example.util

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build

sealed class HardwareActionResult {
    data class Success(val message: String) : HardwareActionResult()
    data class Failure(val reason: String) : HardwareActionResult()
}

/**
 * DeviceHardwareController:
 * Enables voice-commanded hardware execution:
 * - Flashlight / Torch toggle ("टॉर्च चालू करो", "टॉर्च बंद करो")
 * - Audio Volume control ("आवाज़ बढ़ाओ", "वॉल्यूम कम करो", "म्यूट करो")
 * - Device status checks
 */
class DeviceHardwareController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var isTorchOn = false

    fun toggleTorch(enable: Boolean): HardwareActionResult {
        if (cameraManager == null) {
            return HardwareActionResult.Failure("डिवाइस में कैमरा फ्लैशलाइट उपलब्ध नहीं है।")
        }

        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }

            if (cameraId == null) {
                return HardwareActionResult.Failure("फ्लैशलाइट कैमरा सेंसर नहीं मिला।")
            }

            cameraManager.setTorchMode(cameraId, enable)
            isTorchOn = enable
            val msg = if (enable) "फ्लैशलाइट चालू कर दी गई है।" else "फ्लैशलाइट बंद कर दी गई है।"
            HardwareActionResult.Success(msg)
        } catch (e: Exception) {
            HardwareActionResult.Failure("फ्लैशलाइट नियंत्रण में त्रुटि: ${e.message}।")
        }
    }

    fun isTorchActive(): Boolean = isTorchOn

    fun adjustVolume(increase: Boolean): HardwareActionResult {
        val am = audioManager ?: return HardwareActionResult.Failure("ऑडियो मैनेजर उपलब्ध नहीं है।")
        return try {
            val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            am.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
            val currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val percent = (currentVol * 100) / maxVol.coerceAtLeast(1)
            val msg = if (increase) "वॉल्यूम बढ़ा दिया गया है ($percent%)।" else "वॉल्यूम कम कर दिया गया है ($percent%)।"
            HardwareActionResult.Success(msg)
        } catch (e: Exception) {
            HardwareActionResult.Failure("वॉल्यूम नियंत्रण त्रुटि: ${e.message}।")
        }
    }

    fun muteVolume(): HardwareActionResult {
        val am = audioManager ?: return HardwareActionResult.Failure("ऑडियो मैनेजर उपलब्ध नहीं है।")
        return try {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI)
            HardwareActionResult.Success("मीडिया वॉल्यूम म्यूट कर दिया गया है।")
        } catch (e: Exception) {
            HardwareActionResult.Failure("म्यूट करने में त्रुटि: ${e.message}।")
        }
    }

    companion object {
        fun isFlashlightOnCommand(lower: String): Boolean {
            val phrases = listOf(
                "टॉर्च चालू", "टॉर्च जलाओ", "टॉर्च ऑन", "फ्लैशलाइट चालू", "फ़्लैशलाइट चालू",
                "फ्लैशलाइट जलाओ", "flashlight on", "torch on", "turn on flashlight", "turn on torch"
            )
            return phrases.any { lower.contains(it) }
        }

        fun isFlashlightOffCommand(lower: String): Boolean {
            val phrases = listOf(
                "टॉर्च बंद", "टॉर्च बुझाओ", "फ्लैशलाइट बंद", "फ़्लैशलाइट बंद",
                "flashlight off", "torch off", "turn off flashlight", "turn off torch"
            )
            return phrases.any { lower.contains(it) }
        }

        fun isVolumeUpCommand(lower: String): Boolean {
            val phrases = listOf(
                "वॉल्यूम बढ़ाओ", "आवाज़ बढ़ाओ", "आवाज बढ़ाओ", "वॉल्यूम ज्यादा", "साउंड बढ़ाओ",
                "volume up", "increase volume", "raise volume"
            )
            return phrases.any { lower.contains(it) }
        }

        fun isVolumeDownCommand(lower: String): Boolean {
            val phrases = listOf(
                "वॉल्यूम कम", "आवाज़ कम", "आवाज कम", "वॉल्यूम घटाओ", "साउंड कम",
                "volume down", "decrease volume", "lower volume"
            )
            return phrases.any { lower.contains(it) }
        }

        fun isMuteCommand(lower: String): Boolean {
            val phrases = listOf(
                "म्यूट करो", "आवाज़ बंद", "आवाज बंद", "साइलेंट करो", "mute volume", "mute"
            )
            return phrases.any { lower.contains(it) }
        }
    }
}
