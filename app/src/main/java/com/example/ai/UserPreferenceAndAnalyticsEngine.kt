package com.example.ai

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ResponseStyle(val displayName: String, val promptInstruction: String) {
    ANALYTICAL_DEEP("गहन विश्लेषणात्मक (Analytical)", "Provide deep analytical reasoning, evaluate pros & cons, structure arguments logically, and cite evidence-based rationale."),
    BALANCED_WARM("संतुलित व दोस्ताना (Balanced & Warm)", "Balance clarity, empathy, warmth, and practical logic."),
    CONCISE_DIRECT("संक्षिप्त व सटीक (Concise)", "Be exceptionally brief, direct, bullet-pointed, and avoid unnecessary filler."),
    SOCRATIC_PARTNER("विचारशील साथी (Thought Partner)", "Act as an intellectual collaborator: ask sharp probing questions and explore multi-dimensional angles.")
}

data class UserFeedbackItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val query: String,
    val response: String,
    val isPositive: Boolean,
    val feedbackTag: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class UserPreferenceAndAnalyticsEngine(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("arya_user_preferences_ai", Context.MODE_PRIVATE)

    private val _preferredStyle = MutableStateFlow(
        ResponseStyle.valueOf(prefs.getString("preferred_style", ResponseStyle.ANALYTICAL_DEEP.name) ?: ResponseStyle.ANALYTICAL_DEEP.name)
    )
    val preferredStyle: StateFlow<ResponseStyle> = _preferredStyle.asStateFlow()

    private val _positiveFeedbackCount = MutableStateFlow(prefs.getInt("positive_feedback_count", 0))
    val positiveFeedbackCount: StateFlow<Int> = _positiveFeedbackCount.asStateFlow()

    private val _negativeFeedbackCount = MutableStateFlow(prefs.getInt("negative_feedback_count", 0))
    val negativeFeedbackCount: StateFlow<Int> = _negativeFeedbackCount.asStateFlow()

    private val _learnedTopics = MutableStateFlow(
        prefs.getStringSet("learned_topics", setOf("प्रौद्योगिकी", "भाषा अनुवाद", "स्वास्थ्य", "उत्पादकता")) ?: emptySet()
    )
    val learnedTopics: StateFlow<Set<String>> = _learnedTopics.asStateFlow()

    fun setPreferredStyle(style: ResponseStyle) {
        _preferredStyle.value = style
        prefs.edit().putString("preferred_style", style.name).apply()
    }

    fun recordFeedback(query: String, response: String, isPositive: Boolean, tag: String? = null) {
        if (isPositive) {
            val count = _positiveFeedbackCount.value + 1
            _positiveFeedbackCount.value = count
            prefs.edit().putInt("positive_feedback_count", count).apply()
        } else {
            val count = _negativeFeedbackCount.value + 1
            _negativeFeedbackCount.value = count
            prefs.edit().putInt("negative_feedback_count", count).apply()
        }

        // Dynamically tune RLHF weights in ContextWindowManager
        ContextWindowManager.recordRlhfFeedback(tag ?: query, isPositive)

        // Infer and learn topics from query
        val newTopics = _learnedTopics.value.toMutableSet()
        val lower = query.lowercase()
        when {
            lower.contains("अनुवाद") || lower.contains("translate") || lower.contains("language") -> newTopics.add("बहुभाषी अनुवाद")
            lower.contains("कोड") || lower.contains("code") || lower.contains("programming") -> newTopics.add("सॉफ्टवेयर डेवलपमेंट")
            lower.contains("स्वास्थ्य") || lower.contains("नींद") || lower.contains("पानी") || lower.contains("break") -> newTopics.add("वेलनेस व लाइफस्टाइल")
            lower.contains("व्यापार") || lower.contains("मार्केट") || lower.contains("business") -> newTopics.add("बिजनेस व मार्केट रणनीति")
            lower.contains("विज्ञान") || lower.contains("ai") || lower.contains("रिसर्च") -> newTopics.add("Frontier AI व अनुसंधान")
        }
        _learnedTopics.value = newTopics
        prefs.edit().putStringSet("learned_topics", newTopics).apply()
    }

    fun submitFeedback(messageId: String, isPositive: Boolean, tag: String, note: String = "") {
        recordFeedback(query = tag, response = note, isPositive = isPositive, tag = tag)
    }

    /**
     * Synthesizes learned user preferences into a contextual instruction
     * for Gemini 3.8 Flash.
     */
    fun buildContextualPreferencePrompt(): String {
        val style = _preferredStyle.value
        val topics = _learnedTopics.value.joinToString(", ")
        return """
            ADAPTIVE USER PREFERENCE PROFILE:
            - Preferred Thinking & Communication Style: ${style.displayName}
            - Analytical Style Directives: ${style.promptInstruction}
            - Core User Interests (Learned across interactions): $topics
            - Total Positive Feedback Validations: ${_positiveFeedbackCount.value}
            Always adapt your reasoning depth, logical structure, and empathy to match this profile.
        """.trimIndent()
    }
}
