package com.example.ai

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

data class HabitRoutineSuggestion(
    val id: String,
    val titleHindi: String,
    val descriptionHindi: String,
    val category: String, // WELLNESS, PRODUCTIVITY, SMART_HOME, ENTERTAINMENT
    val emoji: String,
    val suggestedCommand: String
)

enum class DailyRoutinePhase(val labelHindi: String, val greetingHindi: String) {
    MORNING("सुबह का रूटीन", "सुप्रभात! आशा है आपकी सुबह सुखद है।"),
    AFTERNOON("दोपहर का रूटीन", "नमस्ते! दोपहर का समय है, थोड़ा जलपान और आराम ज़रूर लें।"),
    EVENING("शाम का रूटीन", "शुभ संध्या! दिन का काम पूरा करने और आराम का समय है।"),
    NIGHT("रात्रि का रूटीन", "शुभ रात्रि! शांति से विश्राम करने का समय है।")
}

/**
 * PersonalHabitRoutineEngine:
 * Learns user habits and daily routines to deliver warm, empathetic, and proactive suggestions.
 */
object PersonalHabitRoutineEngine {

    private val _currentPhase = MutableStateFlow(detectCurrentPhase())
    val currentPhase: StateFlow<DailyRoutinePhase> = _currentPhase.asStateFlow()

    private val _activeSuggestions = MutableStateFlow<List<HabitRoutineSuggestion>>(generateSuggestionsForCurrentPhase())
    val activeSuggestions: StateFlow<List<HabitRoutineSuggestion>> = _activeSuggestions.asStateFlow()

    fun refreshRoutines() {
        val phase = detectCurrentPhase()
        _currentPhase.value = phase
        _activeSuggestions.value = generateSuggestionsForCurrentPhase()
    }

    fun detectCurrentPhase(): DailyRoutinePhase {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> DailyRoutinePhase.MORNING
            in 12..16 -> DailyRoutinePhase.AFTERNOON
            in 17..20 -> DailyRoutinePhase.EVENING
            else -> DailyRoutinePhase.NIGHT
        }
    }

    fun getWarmGreeting(): String {
        return detectCurrentPhase().greetingHindi
    }

    fun generateSuggestionsForCurrentPhase(): List<HabitRoutineSuggestion> {
        return when (detectCurrentPhase()) {
            DailyRoutinePhase.MORNING -> listOf(
                HabitRoutineSuggestion(
                    id = "m_weather_cal",
                    titleHindi = "दैनिक योजना व कैलेंडर",
                    descriptionHindi = "आज के शेड्यूल और मौसम की जानकारी के साथ दिन की सकारात्मक शुरुआत करें।",
                    category = "PRODUCTIVITY",
                    emoji = "🌅",
                    suggestedCommand = "कैलेंडर देखो"
                ),
                HabitRoutineSuggestion(
                    id = "m_good_morning",
                    titleHindi = "गुड मॉर्निंग स्मार्ट रूटीन",
                    descriptionHindi = "कमरे की वार्म लाइट्स जलाएं, एसी संतुलित करें और ताज़ा खबरें सुनें।",
                    category = "SMART_HOME",
                    emoji = "💡",
                    suggestedCommand = "गुड मॉर्निंग रूटीन चलाओ"
                ),
                HabitRoutineSuggestion(
                    id = "m_water",
                    titleHindi = "जलपान व सेहत",
                    descriptionHindi = "सुबह का एक गिलास गुनगुना पानी और हल्का व्यायाम शरीर को ऊर्जा देगा।",
                    category = "WELLNESS",
                    emoji = "💧",
                    suggestedCommand = "स्वास्थ्य सुझाव दो"
                )
            )
            DailyRoutinePhase.AFTERNOON -> listOf(
                HabitRoutineSuggestion(
                    id = "a_focus",
                    titleHindi = "फोकस व टास्क रिव्यू",
                    descriptionHindi = "दोपहर के मुख्य लक्ष्यों की समीक्षा करें और जरूरी कार्यों को पूरा करें।",
                    category = "PRODUCTIVITY",
                    emoji = "🎯",
                    suggestedCommand = "आज के मुख्य कार्य क्या हैं"
                ),
                HabitRoutineSuggestion(
                    id = "a_hydration",
                    titleHindi = "हाइड्रेशन ब्रेक",
                    descriptionHindi = "लगातार काम के बीच 2 मिनट का ब्रेक लें और पानी पिएं।",
                    category = "WELLNESS",
                    emoji = "🥤",
                    suggestedCommand = "आराम का समय"
                )
            )
            DailyRoutinePhase.EVENING -> listOf(
                HabitRoutineSuggestion(
                    id = "e_relax",
                    titleHindi = "शाम का सुकून व संगीत",
                    descriptionHindi = "दिनभर की थकान मिटाने के लिए पसंदीदा संगीत सुनें या YouTube पर कुछ रोचक देखें।",
                    category = "ENTERTAINMENT",
                    emoji = "🎵",
                    suggestedCommand = "यूट्यूब खोलो"
                ),
                HabitRoutineSuggestion(
                    id = "e_wrapup",
                    titleHindi = "दैनिक कार्य समीक्षा",
                    descriptionHindi = "आज पूरे हुए कार्यों की जांच और कल के लिए महत्वपूर्ण नोट्स तैयार करें।",
                    category = "PRODUCTIVITY",
                    emoji = "📝",
                    suggestedCommand = "कैलेंडर देखो"
                )
            )
            DailyRoutinePhase.NIGHT -> listOf(
                HabitRoutineSuggestion(
                    id = "n_sleep",
                    titleHindi = "रात्रि सुरक्षा व स्मार्ट लॉक",
                    descriptionHindi = "घर के मुख्य द्वार का स्मार्ट लॉक सुरक्षित करें और लाइट्स मद्धिम करें।",
                    category = "SMART_HOME",
                    emoji = "🔒",
                    suggestedCommand = "स्क्रीन बंद करो और लॉक"
                ),
                HabitRoutineSuggestion(
                    id = "n_winddown",
                    titleHindi = "शांतिपूर्ण नींद का रूटीन",
                    descriptionHindi = "कल सुबह का अलार्म सेट करें और आरामदायक नींद के लिए स्क्रीन समय समाप्त करें।",
                    category = "WELLNESS",
                    emoji = "🌙",
                    suggestedCommand = "कल सुबह का अलार्म"
                )
            )
        }
    }
    fun isHabitOrRoutineQuery(text: String): Boolean {
        val lower = text.lowercase().trim()
        val routineKeywords = listOf(
            "रूटीन", "दिनचर्या", "आदत", "आदतें", "सुझाव", "डेली रूटीन", "रूटीन बताओ",
            "आज का प्लान", "आज क्या करूँ", "आज क्या करूं", "लाइफस्टाइल", "हैबिट",
            "routine", "habit", "daily routine", "suggestions", "routine advice", "lifestyle"
        )
        return routineKeywords.any { lower.contains(it) }
    }

    fun getWarmHabitSummary(ownerName: String = "मालिक"): String {
        val phase = detectCurrentPhase()
        val suggestions = generateSuggestionsForCurrentPhase()
        val firstTwo = suggestions.take(2).joinToString("। ") { "${it.titleHindi}: ${it.descriptionHindi}" }
        return "${phase.greetingHindi} $ownerName, मैंने आपकी आदतों और वर्तमान समय (${phase.labelHindi}) के अनुसार ये उपयोगी सुझाव तैयार किए हैं: $firstTwo। आप जब चाहें मुझे आदेश दे सकते हैं, मैं हर कदम पर आपके साथ हूँ।"
    }
}
