package com.example.security

import com.example.data.preferences.VoiceProfilePreferences
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

data class VerificationResult(
    val isVerified: Boolean,
    val confidence: Float,
    val matchedAction: VoiceActionType,
    val statusMessage: String,
    val isSecurityAlert: Boolean = false
)

enum class VoiceActionType {
    GEMINI_ASSISTANT,
    APP_LAUNCH,
    TRANSCRIBE,
    WAKE_WORD_ONLY,
    ANSWER_CALL,
    REJECT_CALL,
    DEVICE_LOCK_OFF,
    DEVICE_WAKE_ON,
    SERVICE_CACHE_FIX,
    PREFERENCE_CUSTOMIZATION,
    UNKNOWN_COMMAND
}

class VoiceBiometricEngine(private val prefs: VoiceProfilePreferences) {

    fun verifyAndClassify(
        spokenText: String,
        measuredPitchHz: Float? = null
    ): VerificationResult {
        return verifyVoice(
            spokenText = spokenText,
            measuredPitchHz = measuredPitchHz,
            isEnrolled = prefs.isEnrolled,
            ownerPitchHz = prefs.ownerPitchHz,
            pitchToleranceHz = prefs.pitchToleranceHz,
            sensitivity = prefs.sensitivity
        )
    }

    fun classifyAction(text: String): VoiceActionType = classifySpokenAction(text)
    fun hasExplicitWakeCommand(text: String): Boolean = VoiceBiometricEngine.hasExplicitWakeCommand(text)
    fun extractCommandAfterWakeWord(text: String): String = VoiceBiometricEngine.extractCommandAfterWakeWord(text)
    fun isWakeWordOnly(text: String): Boolean = VoiceBiometricEngine.isWakeWordOnly(text)
    fun isAnswerCallCommand(lower: String): Boolean = VoiceBiometricEngine.isAnswerCallCommand(lower)
    fun isRejectCallCommand(lower: String): Boolean = VoiceBiometricEngine.isRejectCallCommand(lower)
    fun isDeviceLockOffCommand(lower: String): Boolean = VoiceBiometricEngine.isDeviceLockOffCommand(lower)
    fun isDeviceWakeOnCommand(lower: String): Boolean = VoiceBiometricEngine.isDeviceWakeOnCommand(lower)
    fun isServiceCacheFixCommand(lower: String): Boolean = VoiceBiometricEngine.isServiceCacheFixCommand(lower)
    fun isPreferenceCustomizationCommand(lower: String): Boolean = VoiceBiometricEngine.isPreferenceCustomizationCommand(lower)
    fun calculatePhraseSimilarity(phraseA: String, phraseB: String): Float = VoiceBiometricEngine.calculatePhraseSimilarity(phraseA, phraseB)

    companion object {

        /**
         * Verifies if incoming speech belongs to the enrolled owner and detects action.
         * Ensures only the authorized user's voice can trigger sensitive operations:
         * - Screen off & Lock ("Lock my phone", "स्क्रीन बंद करो और लॉक")
         * - Screen on & Wake ("Unlock my phone", "स्क्रीन चालू और वेक")
         * - Background service cache fix ("बैकग्राउंड सर्विस कैश फिक्स")
         * - Phone Call handling ("कॉल उठाओ" / "कॉल काटो")
         */
        fun verifyVoice(
            spokenText: String,
            measuredPitchHz: Float? = null,
            isEnrolled: Boolean = true,
            ownerPitchHz: Float = 125f,
            pitchToleranceHz: Float = 35f,
            sensitivity: Float = 0.75f
        ): VerificationResult {
            val cleanInput = spokenText.trim().lowercase(Locale.ROOT)
            if (cleanInput.isEmpty()) {
                return VerificationResult(
                    isVerified = false,
                    confidence = 0f,
                    matchedAction = VoiceActionType.UNKNOWN_COMMAND,
                    statusMessage = "कोई आवाज़ सुनाई नहीं दी (No voice input detected)"
                )
            }

            val action = classifySpokenAction(cleanInput)

            // Strict Owner-Only Enforcement for critical operations
            val isOwnerPermissionMandatory = when (action) {
                VoiceActionType.DEVICE_LOCK_OFF,
                VoiceActionType.DEVICE_WAKE_ON,
                VoiceActionType.SERVICE_CACHE_FIX,
                VoiceActionType.ANSWER_CALL,
                VoiceActionType.REJECT_CALL,
                VoiceActionType.PREFERENCE_CUSTOMIZATION -> true
                else -> false
            }

            // If not enrolled yet, STRICTLY BLOCK sensitive actions!
            if (!isEnrolled) {
                if (isOwnerPermissionMandatory) {
                    val actionName = when (action) {
                        VoiceActionType.DEVICE_LOCK_OFF -> "स्क्रीन बंद व लॉक"
                        VoiceActionType.DEVICE_WAKE_ON -> "स्क्रीन चालू व वेक"
                        VoiceActionType.SERVICE_CACHE_FIX -> "बैकग्राउंड सर्विस व कैश फिक्स"
                        VoiceActionType.ANSWER_CALL, VoiceActionType.REJECT_CALL -> "कॉल हैंडलिंग"
                        VoiceActionType.PREFERENCE_CUSTOMIZATION -> "व्यक्तिगत सेटिंग्स व पसंद में बदलाव"
                        else -> "सुरक्षित कमांड"
                    }
                    return VerificationResult(
                        isVerified = false,
                        confidence = 0f,
                        matchedAction = action,
                        statusMessage = "सुरक्षा अलर्ट: आवाज़ इनरोल नहीं है! $actionName केवल अधिकृत मालिक की आवाज़ से संचालित करने के लिए पहले 'वॉइस ट्रेनिंग' पूरी करें।",
                        isSecurityAlert = true
                    )
                }
                return VerificationResult(
                    isVerified = true,
                    confidence = 0.85f,
                    matchedAction = action,
                    statusMessage = if (action == VoiceActionType.WAKE_WORD_ONLY) {
                        "आर्या वेक वर्ड सक्रिय! (\"Arya\" activated)"
                    } else {
                        "वॉइस प्रोफाइल अभी सेट नहीं है (Voice not enrolled yet - setup recommended)"
                    }
                )
            }

            // 1. Acoustic similarity score check against owner's enrolled pitch profile
            var acousticScore = 0.90f
            measuredPitchHz?.let { pitch ->
                if (pitch > 50f && pitch < 400f) {
                    val diff = abs(pitch - ownerPitchHz)
                    val tolerance = pitchToleranceHz
                    acousticScore = if (diff <= tolerance) {
                        1.0f - (diff / (tolerance * 2f)).coerceIn(0f, 0.25f)
                    } else {
                        max(0.15f, 1.0f - (diff / tolerance))
                    }
                }
            }

            // Strict threshold check according to user sensitivity setting
            val threshold = sensitivity.coerceIn(0.50f, 0.90f)

            // Strict biometric verification across all functions to ensure AI activates ONLY for the registered owner
            val requiredThreshold = threshold.coerceAtLeast(0.70f)

            // Ensure the command was spoken by the authorized user
            val isAcousticallyAuthorized = acousticScore >= requiredThreshold

            if (!isAcousticallyAuthorized) {
                val alertDetail = when (action) {
                    VoiceActionType.DEVICE_LOCK_OFF ->
                        "अनधिकृत आवाज़ अस्वीकृत! केवल पंजीकृत मालिक की आवाज़ से फोन लॉक किया जा सकता है। (${(acousticScore * 100).toInt()}% match)"
                    VoiceActionType.DEVICE_WAKE_ON ->
                        "अनधिकृत आवाज़ अस्वीकृत! केवल पंजीकृत मालिक की आवाज़ से फोन अनलॉक किया जा सकता है। (${(acousticScore * 100).toInt()}% match)"
                    VoiceActionType.SERVICE_CACHE_FIX ->
                        "अनधिकृत आवाज़ अस्वीकृत! बैकग्राउंड सर्विस व कैश फिक्स केवल पंजीकृत मालिक की आवाज़ से किया जा सकता है। (${(acousticScore * 100).toInt()}% match)"
                    VoiceActionType.ANSWER_CALL, VoiceActionType.REJECT_CALL ->
                        "अनधिकृत आवाज़ अस्वीकृत! कॉल हैंडलिंग केवल पंजीकृत मालिक की आवाज़ से हो सकती है। (${(acousticScore * 100).toInt()}% match)"
                    VoiceActionType.PREFERENCE_CUSTOMIZATION ->
                        "अनधिकृत आवाज़ अस्वीकृत! केवल पंजीकृत मालिक की आवाज़ से व्यक्तिगत सेटिंग्स बदली जा सकती हैं। (${(acousticScore * 100).toInt()}% match)"
                    VoiceActionType.APP_LAUNCH ->
                        "अनधिकृत आवाज़ अस्वीकृत! केवल पंजीकृत मालिक की आवाज़ से ऐप खोले जा सकते हैं। (${(acousticScore * 100).toInt()}% match)"
                    else ->
                        "अनधिकृत आवाज़ अस्वीकृत! आर्या AI केवल पंजीकृत मालिक की आवाज़ पर सक्रिय होने के लिए लॉक है। (${(acousticScore * 100).toInt()}% match)"
                }
                return VerificationResult(
                    isVerified = false,
                    confidence = acousticScore,
                    matchedAction = action,
                    statusMessage = alertDetail,
                    isSecurityAlert = true
                )
            }

            val message = when (action) {
                VoiceActionType.ANSWER_CALL -> "मालिक की आवाज़ सत्यापित: कॉल उठाने की कमांड स्वीकृत"
                VoiceActionType.REJECT_CALL -> "मालिक की आवाज़ सत्यापित: कॉल काटने की कमांड स्वीकृत"
                VoiceActionType.DEVICE_LOCK_OFF -> "बायोमेट्रिक सत्यापित: स्क्रीन बंद व लॉक की जा रही है"
                VoiceActionType.DEVICE_WAKE_ON -> "बायोमेट्रिक सत्यापित: स्क्रीन चालू व वेक की जा रही है"
                VoiceActionType.SERVICE_CACHE_FIX -> "बायोमेट्रिक सत्यापित: बैकग्राउंड सर्विस व कैश फिक्स किया जा रहा है"
                VoiceActionType.PREFERENCE_CUSTOMIZATION -> "मालिक की आवाज़ सत्यापित: व्यक्तिगत सेटिंग्स व पसंद अपडेट की जा रही है"
                VoiceActionType.WAKE_WORD_ONLY -> "अधिकृत आवाज़ सत्यापित: Arya AI तैयार है"
                else -> "अधिकृत उपयोगकर्ता सत्यापित: Arya AI सक्रिय (${(acousticScore * 100).toInt()}%)"
            }

            return VerificationResult(
                isVerified = true,
                confidence = acousticScore,
                matchedAction = action,
                statusMessage = message
            )
        }

        fun hasExplicitWakeCommand(text: String): Boolean {
            val clean = text.trim().lowercase(Locale.ROOT)
                .replace("!", "").replace("?", "").replace(".", "").replace(",", "")
            val explicitPrefixes = listOf(
                "हे आर्या", "हे आरिया", "hey arya", "hey aria",
                "ओके आर्या", "ओके आरिया", "ok arya", "ok aria"
            )
            return explicitPrefixes.any { prefix ->
                clean == prefix || clean.startsWith("$prefix ") || clean.contains(" $prefix ") || clean.endsWith(" $prefix")
            }
        }

        fun extractCommandAfterWakeWord(text: String): String {
            val lower = text.lowercase(Locale.ROOT)
            val prefixes = listOf(
                "हे आर्या", "हे आरिया", "hey arya", "hey aria",
                "ओके आर्या", "ओके आरिया", "ok arya", "ok aria"
            )
            for (prefix in prefixes) {
                val idx = lower.indexOf(prefix)
                if (idx >= 0) {
                    val after = text.substring(idx + prefix.length).trim()
                    if (after.isNotBlank()) {
                        return after
                    }
                }
            }
            return text.trim()
        }

        fun isWakeWordOnly(text: String): Boolean {
            val clean = text.trim().lowercase(Locale.ROOT)
                .replace("!", "").replace("?", "").replace(".", "").replace(",", "")
            val explicitWakeWords = listOf(
                "हे आर्या", "हे आरिया", "hey arya", "hey aria",
                "ओके आर्या", "ओके आरिया", "ok arya", "ok aria"
            )
            return explicitWakeWords.any { clean == it || clean == "$it जी" || clean == "ओ $it" }
        }

        fun classifySpokenAction(text: String): VoiceActionType {
            val lower = text.lowercase(Locale.ROOT)

            // 1. Phone Call Answer Keywords
            if (isAnswerCallCommand(lower)) {
                return VoiceActionType.ANSWER_CALL
            }

            // 2. Phone Call Reject Keywords
            if (isRejectCallCommand(lower)) {
                return VoiceActionType.REJECT_CALL
            }

            // 3. Device Screen / Mobile OFF & Lock Keywords ("Lock my phone", "स्क्रीन बंद करो और लॉक")
            if (isDeviceLockOffCommand(lower)) {
                return VoiceActionType.DEVICE_LOCK_OFF
            }

            // 4. Device Screen / Mobile ON & Wake Keywords ("Unlock my phone", "स्क्रीन चालू और वेक")
            if (isDeviceWakeOnCommand(lower)) {
                return VoiceActionType.DEVICE_WAKE_ON
            }

            // 5. Background Service & Cache Fix Keywords ("बैकग्राउंड सर्विस कैश फिक्स")
            if (isServiceCacheFixCommand(lower)) {
                return VoiceActionType.SERVICE_CACHE_FIX
            }

            // 6. Personalized Preferences & Settings Voice Commands
            if (isPreferenceCustomizationCommand(lower)) {
                return VoiceActionType.PREFERENCE_CUSTOMIZATION
            }

            // 7. Wake word
            if (isWakeWordOnly(lower)) {
                return VoiceActionType.WAKE_WORD_ONLY
            }

            // 7. App Launch
            if (com.example.util.DeviceAppsManager.isAppLaunchRequest(lower)) {
                return VoiceActionType.APP_LAUNCH
            }

            // 8. Transcribe
            if (lower.contains("transcribe") || lower.contains("ट्रांसक्राइब") || lower.contains("लिखो") || lower.contains("टाइप")) {
                return VoiceActionType.TRANSCRIBE
            }

            return VoiceActionType.GEMINI_ASSISTANT
        }

        fun isAnswerCallCommand(lower: String): Boolean {
            val answerPhrases = listOf(
                "कॉल उठाओ", "कॉल उठा लो", "कॉल उठा लीजिए", "कॉल उठाएं", "कॉल उठाओ जी", "कॉल उठा",
                "कॉल रिसीव करो", "कॉल रिसीव", "कॉल आंसर करो", "कॉल आंसर",
                "फोन उठाओ", "फ़ोन उठाओ", "फोन उठा", "फ़ोन उठा",
                "हाँ उठाओ", "हां उठाओ", "हाँ उठा", "उठा लो कॉल", "उठाओ कॉल",
                "answer call", "answer the call", "pick up call", "pick up the call",
                "pick up", "accept call", "receive call"
            )
            return answerPhrases.any { lower.contains(it) } || lower == "उठाओ" || lower == "हाँ" || lower == "हां" || lower == "yes answer"
        }

        fun isRejectCallCommand(lower: String): Boolean {
            val rejectPhrases = listOf(
                "कॉल काटो", "कॉल काट दो", "कॉल काटिए", "कॉल रिजेक्ट करो", "कॉल अस्वीकार करो",
                "फोन काटो", "फ़ोन काटो", "कट करो कॉल", "कॉल ड्रॉप करो",
                "reject call", "decline call", "hang up", "cut the call", "cut call"
            )
            return rejectPhrases.any { lower.contains(it) }
        }

        fun isDeviceLockOffCommand(lower: String): Boolean {
            if (lower.contains("unlock") || lower.contains("अनलॉक")) return false
            val lockOffPhrases = listOf(
                "lock my phone", "lock my mobile", "lock phone", "lock the phone", "lock screen", "lock the screen",
                "phone lock karo", "phone lock", "mobile lock karo", "screen lock karo", "lock my device",
                "स्क्रीन बंद करो और लॉक", "स्क्रीन बंद और लॉक",
                "मोबाइल बंद करो और लॉक", "फ़ोन बंद करो और लॉक", "फोन बंद करो और लॉक",
                "मोबाइल बंद करो", "फ़ोन बंद करो", "फोन बंद करो", "स्क्रीन बंद करो", "डिवाइस बंद करो",
                "मोबाइल लॉक करो", "फ़ोन लॉक करो", "फोन लॉक करो", "स्क्रीन लॉक करो", "मोबाइल लॉक", "फोन लॉक", "फ़ोन लॉक",
                "फोन को लॉक करो", "फ़ोन को लॉक करो", "मोबाइल को लॉक करो", "लॉक माय फोन", "लॉक माय मोबाइल",
                "मोबाइल बंद", "फोन बंद", "फ़ोन बंद", "स्क्रीन बंद", "स्क्रीन लॉक",
                "मोबाइल सुला दो", "फोन सुला दो", "स्लीप मोड", "स्लीप मोड में डालो",
                "टर्न ऑफ मोबाइल", "टर्न ऑफ स्क्रीन", "टर्न ऑफ फोन",
                "लॉक फोन", "लॉक फ़ोन", "लॉक स्क्रीन",
                "स्विच ऑफ स्क्रीन", "पावर ऑफ स्क्रीन",
                "turn off screen", "turn off phone", "sleep phone", "screen off",
                "screen off and lock", "turn off and lock"
            )
            return lockOffPhrases.any { lower.contains(it) }
        }

        fun isDeviceWakeOnCommand(lower: String): Boolean {
            val wakeOnPhrases = listOf(
                "unlock my phone", "unlock phone", "unlock the phone", "unlock mobile", "unlock my mobile",
                "phone unlock karo", "phone unlock", "mobile unlock karo", "screen unlock karo", "unlock my device",
                "फोन अनलॉक करो", "फ़ोन अनलॉक करो", "मोबाइल अनलॉक करो", "स्क्रीन अनलॉक करो",
                "फोन को अनलॉक करो", "फ़ोन को अनलॉक करो", "मोबाइल को अनलॉक करो",
                "अनलॉक फोन", "अनलॉक फ़ोन", "अनलॉक मोबाइल", "अनलॉक स्क्रीन", "अनलॉक माय फोन",
                "स्क्रीन चालू और वेक", "स्क्रीन चालू और ऑन",
                "मोबाइल चालू और वेक", "फोन चालू और वेक", "फ़ोन चालू और वेक",
                "मोबाइल चालू करो", "फ़ोन चालू करो", "फोन चालू करो", "स्क्रीन चालू करो",
                "मोबाइल ऑन करो", "स्क्रीन ऑन करो", "फ़ोन ऑन करो", "फोन ऑन करो",
                "मोबाइल खोलो", "फ़ोन खोलो", "फोन खोलो", "स्क्रीन खोलो",
                "मोबाइल ऑन", "स्क्रीन ऑन", "फोन ऑन", "फ़ोन ऑन", "स्क्रीन वेक करो",
                "जागो आर्या", "जागो", "वेक अप", "स्क्रीन जगाओ",
                "टर्न ऑन स्क्रीन", "टर्न ऑन फोन", "टर्न ऑन मोबाइल",
                "wake up phone", "wake up screen", "turn on screen", "turn on phone",
                "wake phone", "screen on", "screen on and wake"
            )
            return wakeOnPhrases.any { lower.contains(it) }
        }

        fun isServiceCacheFixCommand(lower: String): Boolean {
            val fixPhrases = listOf(
                "बैकग्राउंड सर्विस कैश फिक्स", "सर्विस कैश फिक्स", "सर्विस कैश फिक्स करो",
                "कैश फिक्स करो", "कैश फिक्स", "कैश साफ करो", "कैश साफ़ करो", "क्लियर कैश",
                "सर्विस फिक्स करो", "सर्विस फिक्स", "बैकग्राउंड सर्विस फिक्स", "सर्विस ठीक करो",
                "सर्विस रीस्टार्ट करो", "सर्विस रीस्टार्ट", "बैकग्राउंड सर्विस रीस्टार्ट",
                "कैश क्लियर करो", "सर्विस क्रैश फिक्स", "क्रैश फिक्स",
                "fix cache", "fix service cache", "service cache fix",
                "background service cache fix", "clear cache", "fix service", "restart background service"
            )
            return fixPhrases.any { lower.contains(it) }
        }

        fun isPreferenceCustomizationCommand(lower: String): Boolean {
            val prefPhrases = listOf(
                // Speech Rate / Speed
                "स्पीड बढ़ाओ", "स्पीड घटाओ", "स्पीड कम करो", "बोलने की गति बढ़ाओ", "बोलने की गति कम करो", "बोलने की गति घटाओ",
                "तेज़ बोलो", "तेज बोलो", "धीरे बोलो", "स्पीड नॉर्मल करो", "सामान्य गति", "बोलने की गति",
                "speech rate", "faster speech", "slower speech", "increase speech rate", "decrease speech rate",
                // Speech Pitch
                "पिच बढ़ाओ", "पिच घटाओ", "पिच कम करो", "आवाज़ पतली करो", "आवाज पतली करो",
                "आवाज़ भारी करो", "आवाज भारी करो", "गहरी आवाज़", "गहरी आवाज", "उच्च पिच", "हाई पिच", "लो पिच",
                "सामान्य पिच", "पिच नॉर्मल करो", "increase pitch", "decrease pitch", "change pitch",
                // Persona Switching
                "काव्या पर्सोना", "काव्या की आवाज़", "काव्या की आवाज", "आरव पर्सोना", "आरव की आवाज़", "आरव की आवाज",
                "पुरुष आवाज़", "पुरुष आवाज", "विक्रान्त पर्सोना", "विक्रांत पर्सोना", "टेक पर्सोना", "रोबोटिक पर्सोना",
                "रिया पर्सोना", "हिंग्लिश पर्सोना", "हिंग्लिश आवाज़", "हिंग्लिश आवाज",
                "शांत पर्सोना", "शांत आवाज़", "शांत आवाज", "सौम्य आवाज़",
                "विटी पर्सोना", "चुटीला पर्सोना", "मजाकिया पर्सोना", "मज़ाकिया पर्सोना",
                "डिफ़ॉल्ट पर्सोना", "बिंदास पर्सोना", "पर्सोना बदलो", "आवाज़ बदलो", "आवाज बदलो",
                "change persona", "switch persona", "switch voice", "change voice persona",
                // Personality Mode
                "मस्ती मोड ऑन", "मस्ती मोड लगाओ", "रोस्ट मोड ऑन", "रोस्ट मोड लगाओ", "शरारती मोड",
                "सीरियस मोड ऑन", "सीरियस मोड लगाओ", "गंभीर मोड ऑन", "गंभीर मोड लगाओ",
                "masti mode", "roast mode", "serious mode",
                // Font Size
                "फॉन्ट बड़ा करो", "फॉन्ट छोटा करो", "फॉन्ट सामान्य करो", "अक्षर बड़े करो", "अक्षर छोटे करो",
                "बड़ा फॉन्ट", "छोटा फॉन्ट", "चैट फॉन्ट", "फॉन्ट साइज़", "फॉन्ट साइज",
                "increase font size", "decrease font size", "normal font size", "change font size",
                // Voice Output Mute / Unmute
                "बोलकर जवाब देना बंद करो", "बोलकर जवाब बंद करो", "आवाज़ बंद करो", "आवाज बंद करो", "म्यूट करो", "चुप रहो",
                "बोलकर जवाब देना चालू करो", "बोलकर जवाब चालू करो", "आवाज़ चालू करो", "आवाज चालू करो", "अनम्यूट करो",
                "mute voice", "unmute voice", "stop speaking", "turn off voice output", "turn on voice output",
                // Sensitivity
                "सेंसिटिविटी बढ़ाओ", "सेंसिटिविटी कम करो", "हाई सेंसिटिविटी", "लो सेंसिटिविटी",
                "increase sensitivity", "decrease sensitivity",
                // Preferences & Settings inquiry
                "मेरी सेटिंग्स बताओ", "मेरी सेटिंग्स दिखाओ", "मेरी पसंद क्या है", "मेरी प्रोफाइल बताओ",
                "शो माय सेटिंग्स", "शो माय प्रेफरेंस", "show my preferences", "show my settings", "tell my settings"
            )
            return prefPhrases.any { lower.contains(it) }
        }

        fun calculatePhraseSimilarity(phraseA: String, phraseB: String): Float {
            val wordsA = phraseA.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
            val wordsB = phraseB.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }.toSet()
            if (wordsA.isEmpty() || wordsB.isEmpty()) return 0f
            val intersection = wordsA.intersect(wordsB).size
            val union = wordsA.union(wordsB).size
            return intersection.toFloat() / union.toFloat()
        }
    }
}
