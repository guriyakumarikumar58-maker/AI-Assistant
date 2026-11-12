package com.example.telephony

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PhoneCallController:
 * Google Play Compliant telephony and calling controller.
 *
 * Uses standard Android Intents (Intent.ACTION_DIAL) requiring ZERO dangerous or
 * restricted permissions (no CALL_PHONE, no READ_CALL_LOG, no ANSWER_PHONE_CALLS).
 */
class PhoneCallController(private val context: Context) {

    private val _isIncomingCallRinging = MutableStateFlow(false)
    val isIncomingCallRinging: StateFlow<Boolean> = _isIncomingCallRinging.asStateFlow()

    private val _incomingNumber = MutableStateFlow<String?>(null)
    val incomingNumber: StateFlow<String?> = _incomingNumber.asStateFlow()

    private val _lastCallActionStatus = MutableStateFlow<String?>(null)
    val lastCallActionStatus: StateFlow<String?> = _lastCallActionStatus.asStateFlow()

    /**
     * Google Play Compliant: Opens the system dialer with standard Intent.ACTION_DIAL.
     * Zero dangerous permissions required.
     */
    fun openDialer(phoneNumber: String = ""): Boolean {
        return try {
            val uri = if (phoneNumber.isNotBlank()) {
                Uri.parse("tel:${Uri.encode(phoneNumber)}")
            } else {
                Uri.parse("tel:")
            }
            val intent = Intent(Intent.ACTION_DIAL, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            _lastCallActionStatus.value = if (phoneNumber.isNotBlank()) {
                "डायलर खुला: $phoneNumber"
            } else {
                "फोन डायलर खुला (मानक इंटेंट)"
            }
            true
        } catch (e: Exception) {
            _lastCallActionStatus.value = "डायलर खोलने में विफल: ${e.localizedMessage}"
            false
        }
    }

    /**
     * Updates call state status.
     */
    fun updateCallState(isRinging: Boolean, number: String? = null) {
        _isIncomingCallRinging.value = isRinging
        if (isRinging) {
            _incomingNumber.value = number
            _lastCallActionStatus.value = "कॉल अलर्ट" + if (!number.isNullOrBlank()) ": $number" else ""
        } else {
            _incomingNumber.value = null
        }
    }

    /**
     * Uses standard system dialer intent to handle incoming / outgoing call interaction safely.
     */
    fun answerRingingCall(): Boolean {
        return openDialer()
    }

    /**
     * Clears ringing call alert.
     */
    fun rejectOrEndCall(): Boolean {
        _isIncomingCallRinging.value = false
        _lastCallActionStatus.value = "कॉल अलर्ट बंद किया गया"
        return true
    }
}
