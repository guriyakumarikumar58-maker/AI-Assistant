package com.example.ai.context

import android.content.Context
import com.example.ai.CrossModalEmotionalResonanceEngine
import com.example.ai.CrossModalEmotionalProfile
import com.example.ai.VoiceTone
import com.example.ai.FacialExpression
import com.example.ai.BodyLanguageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

/**
 * On-device Anonymized Contextual State
 */
enum class AppInteractionCategory(val labelHindi: String, val typicalEmotion: VoiceTone) {
    PRODUCTIVITY_OFFICE("दस्तावेज़ व कार्य", VoiceTone.FOCUSED_INQUISITIVE),
    COMMUNICATION_MESSAGING("संवाद व चैटिंग", VoiceTone.WARM_AFFECTIONATE),
    GAMING_ENTERTAINMENT("गेमिंग व मनोरंजन", VoiceTone.EXCITED_JOYFUL),
    FINANCE_BANKING("सुरक्षित वित्तीय कार्य", VoiceTone.CALM_PEACEFUL),
    HEALTH_WELLNESS("स्वास्थ्य व विश्राम", VoiceTone.CALM_PEACEFUL),
    LATE_NIGHT_EXHAUSTION("देर रात का उपयोग", VoiceTone.TIRED_EXHAUSTED)
}

data class ContextualEmotionalInsight(
    val id: String = UUID.randomUUID().toString(),
    val inferredCategory: AppInteractionCategory,
    val inferredStateHindi: String,
    val proactiveSuggestionHindi: String,
    val confidencePercent: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isPrivacyPreservedLocally: Boolean = true
)

/**
 * CrossAppEmotionalContextService:
 * Securely infers user contextual and emotional cues across permitted interactions
 * using 100% on-device local processing and cryptographically salted data anonymization.
 *
 * Privacy Guarantees:
 * 1. Zero Keystroke or Raw Screen Logging: Strictly observes high-level app category metadata only.
 * 2. On-Device Anonymization: App IDs and package signatures are transformed using salted SHA-256 hashes.
 * 3. Proactive Assistance: Automatically suggests breaks, empathetic check-ins, or focus modes.
 */
class CrossAppEmotionalContextService(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _currentInsight = MutableStateFlow(
        ContextualEmotionalInsight(
            inferredCategory = AppInteractionCategory.COMMUNICATION_MESSAGING,
            inferredStateHindi = "सक्रिय संवाद व संतुलन",
            proactiveSuggestionHindi = "आपकी उत्पादकता और ऊर्जा उत्कृष्ट है। जरूरत पड़ने पर आर्या से कभी भी सलाह लें।",
            confidencePercent = 96
        )
    )
    val currentInsight: StateFlow<ContextualEmotionalInsight> = _currentInsight.asStateFlow()

    private val _isServiceActive = MutableStateFlow(true)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    private val anonymizationSalt = "AryaLocalPrivacySalt_2026_${UUID.randomUUID()}"

    /**
     * Anonymizes application identifiers into cryptographic hashes to protect user privacy.
     */
    fun anonymizeIdentifier(identifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("$identifier$anonymizationSalt".toByteArray(Charsets.UTF_8))
        return hash.take(8).joinToString("") { "%02x".format(it) }
    }

    /**
     * Ingests high-level permitted interaction signals and infers user emotional state.
     */
    fun analyzeContextualCue(
        appCategoryName: String,
        sessionDurationMinutes: Int,
        hourOfDay: Int
    ) {
        if (!_isServiceActive.value) return

        scope.launch {
            val lower = appCategoryName.lowercase()
            val category = when {
                hourOfDay in 0..5 -> AppInteractionCategory.LATE_NIGHT_EXHAUSTION
                sessionDurationMinutes > 90 -> AppInteractionCategory.LATE_NIGHT_EXHAUSTION
                lower.contains("game") || lower.contains("fire") || lower.contains("play") -> AppInteractionCategory.GAMING_ENTERTAINMENT
                lower.contains("doc") || lower.contains("code") || lower.contains("work") || lower.contains("office") -> AppInteractionCategory.PRODUCTIVITY_OFFICE
                lower.contains("bank") || lower.contains("upi") || lower.contains("pay") -> AppInteractionCategory.FINANCE_BANKING
                lower.contains("fit") || lower.contains("health") || lower.contains("meditation") -> AppInteractionCategory.HEALTH_WELLNESS
                else -> AppInteractionCategory.COMMUNICATION_MESSAGING
            }

            val (stateHindi, suggestionHindi, confidence) = when (category) {
                AppInteractionCategory.LATE_NIGHT_EXHAUSTION -> Triple(
                    "थकान व तनाव की संभावना",
                    "काफी देर से स्क्रीन पर हैं भाई! 10 मिनट का छोटा सा ब्रेक लें या पानी पीकर थोड़ा टहलें।",
                    94
                )
                AppInteractionCategory.PRODUCTIVITY_OFFICE -> Triple(
                    "गहरा ध्यान व बौद्धिक एकाग्रता",
                    "आप काम में पूरी तरह डूबे हैं। अगर किसी कोडिंग या रिसर्च में मदद चाहिए तो बस एक आवाज़ दें।",
                    92
                )
                AppInteractionCategory.GAMING_ENTERTAINMENT -> Triple(
                    "उत्साह, रोमांच व प्रतिस्पर्धा",
                    "फुल एनर्जी मोड ऑन है! गेमिंग ट्रिक्स या रणनीति की जानकारी चाहिए तो आर्या हाज़िर है।",
                    95
                )
                AppInteractionCategory.FINANCE_BANKING -> Triple(
                    "सावधानी व सतर्कता",
                    "वित्तीय सुरक्षा का पूरा ध्यान रखें। किसी भी अनजान व्यक्ति के साथ OTP या पिन साझा न करें।",
                    98
                )
                AppInteractionCategory.HEALTH_WELLNESS -> Triple(
                    "शांति, विश्राम व संतुलन",
                    "उत्कृष्ट दिनचर्या! अपनी सेहत और मानसिक शांति को प्राथमिकता देना बहुत अच्छी आदत है।",
                    93
                )
                AppInteractionCategory.COMMUNICATION_MESSAGING -> Triple(
                    "दोस्ताना व सामाजिक संवाद",
                    "मस्त माहौल है! बातचीत जारी रखें और दिन को खुशनुमा बनाएं।",
                    90
                )
            }

            _currentInsight.value = ContextualEmotionalInsight(
                inferredCategory = category,
                inferredStateHindi = stateHindi,
                proactiveSuggestionHindi = suggestionHindi,
                confidencePercent = confidence
            )

            // Update emotional resonance engine on-device
            if (category == AppInteractionCategory.LATE_NIGHT_EXHAUSTION) {
                CrossModalEmotionalResonanceEngine.setManualFacialExpression(FacialExpression.TIRED_DROOPING)
                CrossModalEmotionalResonanceEngine.setManualBodyLanguage(BodyLanguageState.SLUMPED_TIRED)
            } else if (category == AppInteractionCategory.GAMING_ENTERTAINMENT) {
                CrossModalEmotionalResonanceEngine.setManualFacialExpression(FacialExpression.WARM_SMILE)
                CrossModalEmotionalResonanceEngine.setManualBodyLanguage(BodyLanguageState.OPEN_ENGAGED)
            }
        }
    }

    fun toggleService(enabled: Boolean) {
        _isServiceActive.value = enabled
    }
}
