package com.example.ai

import androidx.compose.ui.graphics.Color

enum class UserMood(
    val titleHindi: String,
    val color: Color,
    val iconDescription: String
) {
    JOYFUL("प्रसन्न / उत्साहित", Color(0xFF00E676), "उच्च सकारात्मक ऊर्जा"),
    CALM("शांत / रिलैक्स", Color(0xFF00F0FF), "स्थिर और संतुलित"),
    STRESSED("तनाव / थका हुआ", Color(0xFFFFAB00), "सहानुभूति व आराम की आवश्यकता"),
    CURIOUS("जिज्ञासु / खोजी", Color(0xFF7C4DFF), "ज्ञान व सीखने की ललक"),
    SAD_EMPATHY("उदास / संवेदनशील", Color(0xFF2979FF), "कोमल और सहानुभूतिपूर्ण सहारा"),
    NEUTRAL("संतुलित", Color(0xFF90A4AE), "स्वाभाविक सामान्य स्थिति")
}

data class EmotionalState(
    val mood: UserMood,
    val valence: Float, // -1.0 (negative) to +1.0 (positive)
    val arousal: Float, // 0.0 (low energy) to 1.0 (high energy)
    val confidence: Float,
    val empathicGuidance: String,
    val recommendedTtsPitch: Float = 1.0f,
    val recommendedTtsRate: Float = 1.0f
)

/**
 * Advanced Emotional Intelligence Module:
 * Detects user moods via text semantics, Hindi/English emotional lexicon,
 * and microphone acoustic dynamics (pitch + RMS intensity).
 * Adapts Arya AI's empathy, tone, and pacing to match user feelings.
 */
object EmotionalIntelligenceEngine {

    private val joyKeywords = listOf(
        "खुश", "शानदार", "बढ़िया", "मज़ा", "उत्सुक", "खुशी", "जीत", "वाह", "अच्छा लगा",
        "happy", "great", "awesome", "excited", "fantastic", "yay", "love", "wonderful", "celebrate"
    )

    private val stressKeywords = listOf(
        "तनाव", "थक", "परेशान", "सिरदर्द", "थकावट", "मुश्किल", "चिंता", "दर्द", "दबाव", "भारी",
        "stress", "tired", "headache", "exhausted", "hard", "busy", "pressure", "overwhelmed", "anxious"
    )

    private val sadKeywords = listOf(
        "उदास", "बुरा", "रोया", "दुखी", "खो गया", "अकेला", "चोट", "मायूस", "निराश",
        "sad", "depressed", "lonely", "hurt", "crying", "lost", "bad", "gloomy", "heartbroken"
    )

    private val curiousKeywords = listOf(
        "क्यों", "कैसे", "क्या", "बताओ", "समझाइए", "जानना", "तर्क", "सीखना", "रिसर्च",
        "why", "how", "what", "explain", "learn", "curious", "research", "explore", "science"
    )

    private val calmKeywords = listOf(
        "शांति", "आराम", "सुकून", "हल्का", "ध्यान", "योग", "नींद", "धीमे",
        "calm", "relax", "peace", "rest", "sleep", "soothing", "meditate", "gentle"
    )

    fun analyzeMood(
        text: String,
        audioRmsDb: Float? = null,
        pitchHz: Float? = null
    ): EmotionalState {
        val lower = text.lowercase().trim()

        // 1. Text Keyword Scoring
        val joyScore = joyKeywords.count { lower.contains(it) }
        val stressScore = stressKeywords.count { lower.contains(it) }
        val sadScore = sadKeywords.count { lower.contains(it) }
        val curiousScore = curiousKeywords.count { lower.contains(it) }
        val calmScore = calmKeywords.count { lower.contains(it) }

        // 2. Acoustic adjustments
        val energyLevel = audioRmsDb?.let { (it / 15f).coerceIn(0f, 1f) } ?: 0.5f
        val pitchVariance = pitchHz?.let { (it - 160f).coerceIn(-60f, 60f) / 60f } ?: 0f

        return when {
            stressScore > 0 || (stressScore == 0 && sadScore == 0 && energyLevel > 0.85f && pitchVariance > 0.5f && lower.length > 50) -> {
                EmotionalState(
                    mood = UserMood.STRESSED,
                    valence = -0.6f,
                    arousal = 0.8f,
                    confidence = 0.90f,
                    empathicGuidance = "उपयोगकर्ता तनाव या दबाव में लग रहे हैं। शांत, आरामदायक और सुकून देने वाले स्वर में बात करें।",
                    recommendedTtsPitch = 0.95f,
                    recommendedTtsRate = 0.92f
                )
            }
            joyScore > 0 || (energyLevel > 0.7f && pitchVariance > 0.2f && (lower.contains("!") || lower.contains("वाह"))) -> {
                EmotionalState(
                    mood = UserMood.JOYFUL,
                    valence = 0.85f,
                    arousal = 0.85f,
                    confidence = 0.92f,
                    empathicGuidance = "उपयोगकर्ता खुश और सकारात्मक ऊर्जा में हैं। गर्मजोशी और उत्साह के साथ उत्तर दें।",
                    recommendedTtsPitch = 1.05f,
                    recommendedTtsRate = 1.04f
                )
            }
            sadScore > 0 -> {
                EmotionalState(
                    mood = UserMood.SAD_EMPATHY,
                    valence = -0.75f,
                    arousal = 0.3f,
                    confidence = 0.88f,
                    empathicGuidance = "उपयोगकर्ता उदास या भावुक हैं। अत्यधिक कोमलता, आत्मीयता और सहानुभूतिपूर्ण सहारा प्रदान करें।",
                    recommendedTtsPitch = 0.92f,
                    recommendedTtsRate = 0.88f
                )
            }
            curiousScore > 0 || lower.endsWith("?") || lower.contains("?") -> {
                EmotionalState(
                    mood = UserMood.CURIOUS,
                    valence = 0.4f,
                    arousal = 0.6f,
                    confidence = 0.85f,
                    empathicGuidance = "उपयोगकर्ता सीखने और जानने के लिए उत्सुक हैं। ज्ञानवर्धक, सटीक और स्पष्ट विवरण दें।",
                    recommendedTtsPitch = 1.0f,
                    recommendedTtsRate = 1.0f
                )
            }
            calmScore > 0 -> {
                EmotionalState(
                    mood = UserMood.CALM,
                    valence = 0.5f,
                    arousal = 0.25f,
                    confidence = 0.86f,
                    empathicGuidance = "उपयोगकर्ता शांत और स्थिर मनोदशा में हैं। सौम्य और संतुलित तरीके से बातचीत करें।",
                    recommendedTtsPitch = 0.98f,
                    recommendedTtsRate = 0.95f
                )
            }
            else -> {
                EmotionalState(
                    mood = UserMood.NEUTRAL,
                    valence = 0.1f,
                    arousal = 0.5f,
                    confidence = 0.75f,
                    empathicGuidance = "सामान्य एवं स्वाभाविक मित्रवत बातचीत करें।",
                    recommendedTtsPitch = 1.0f,
                    recommendedTtsRate = 1.0f
                )
            }
        }
    }
}
