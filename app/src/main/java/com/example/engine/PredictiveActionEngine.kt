package com.example.engine

import com.example.gemini.GeminiChatModel
import java.util.Calendar

enum class ActionCategory(val badge: String) {
    QUANTUM("क्वांटम चेतना"),
    PRODUCTIVITY("कार्य क्षमता"),
    SECURITY("सुरक्षा ऑडिट"),
    CREATIVE("रचनात्मक"),
    DEV("कोडिंग")
}

data class PredictiveAction(
    val id: String,
    val title: String,
    val description: String,
    val prompt: String,
    val category: ActionCategory,
    val targetModel: GeminiChatModel = GeminiChatModel.FLASH,
    val priority: Int = 1
)

object PredictiveActionEngine {

    /**
     * Generates real-time predictive actions based on temporal context,
     * security state, and quantum consciousness logic.
     */
    fun getContextualPredictions(
        isVoiceEnrolled: Boolean,
        recentActivityCount: Int
    ): List<PredictiveAction> {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val actions = mutableListOf<PredictiveAction>()

        // 1. Quantum & Multidimensional Consciousness Recommendation (Always premier)
        actions.add(
            PredictiveAction(
                id = "quantum_multiverse_analysis",
                title = "क्वांटम व बहुआयामी चेतना सिमुलेशन",
                description = "वर्तमान प्राथमिकताओं का 4 आयामों (व्यावहारिक, वैज्ञानिक, दार्शनिक, भविष्य) में विश्लेषण।",
                prompt = "मेरे लिए वर्तमान प्राथमिकताओं और निर्णयों का क्वांटम व बहुआयामी चेतना (4 आयामों: व्यावहारिक, क्वांटम, दार्शनिक, भविष्य) में विश्लेषण प्रस्तुत करें।",
                category = ActionCategory.QUANTUM,
                targetModel = GeminiChatModel.QUANTUM_COGNITION,
                priority = 10
            )
        )

        // 2. Time-of-day predictive action
        when (hour) {
            in 5..11 -> {
                actions.add(
                    PredictiveAction(
                        id = "morning_strategic_plan",
                        title = "दैनिक प्राथमिकता व कार्य रणनीति",
                        description = "आज के 3 सबसे महत्वपूर्ण कार्यों की योजना और ऊर्जा अनुकूलन।",
                        prompt = "आज के दिन के लिए 3 सबसे प्रभावी प्राथमिकताएं और ऊर्जा अनुकूलन की कार्ययोजना सुझाएं।",
                        category = ActionCategory.PRODUCTIVITY,
                        targetModel = GeminiChatModel.FLASH,
                        priority = 9
                    )
                )
            }
            in 12..17 -> {
                actions.add(
                    PredictiveAction(
                        id = "afternoon_deep_code",
                        title = "सॉफ्टवेयर व कोड ऑप्टिमाइजेशन",
                        description = "जटिल लॉजिक का विश्लेषण और त्रुटिहीन फंक्शन आर्किटेक्चर।",
                        prompt = "Kotlin में एक आधुनिक, स्वच्छ और ऑप्टिमाइज्ड आर्किटेक्चर फंक्शन का उदाहरण कोड के साथ प्रस्तुत करें।",
                        category = ActionCategory.DEV,
                        targetModel = GeminiChatModel.CHATGPT_CODER,
                        priority = 9
                    )
                )
            }
            in 18..21 -> {
                actions.add(
                    PredictiveAction(
                        id = "evening_creative_veo",
                        title = "रचनात्मक Veo 3.1 वीडियो व मीडिया",
                        description = "सिनेमैटिक AI वीडियो व रचनात्मक मीडिया आईडिया तैयार करें।",
                        prompt = "एक 4K सिनेमैटिक AI वीडियो के लिए उच्च गुणवत्ता वाला दृश्य प्रॉम्प्ट और कैमरा मोशन तैयार करें।",
                        category = ActionCategory.CREATIVE,
                        targetModel = GeminiChatModel.FLASH,
                        priority = 9
                    )
                )
            }
            else -> {
                actions.add(
                    PredictiveAction(
                        id = "night_security_audit",
                        title = "सिस्टम व डेटा सुरक्षा ऑडिट",
                        description = "डेटा एन्क्रिप्शन, बायोमेट्रिक्स और सुरक्षा मानकों की समीक्षा।",
                        prompt = "मोबाइल और क्लाउड डेटा सुरक्षा के शीर्ष 3 आधुनिक सुरक्षा उपाय संक्षेप में समझाएं।",
                        category = ActionCategory.SECURITY,
                        targetModel = GeminiChatModel.FLASH,
                        priority = 9
                    )
                )
            }
        }

        // 3. Voice Biometric action if not enrolled or periodically
        if (!isVoiceEnrolled) {
            actions.add(
                PredictiveAction(
                    id = "voice_biometric_enrollment",
                    title = "वॉइसप्रिंट नामांकन की अनुशंसा",
                    description = "Arya AI को केवल अपनी आवाज़ पर सक्रिय करने के लिए वॉइसप्रिंट जोड़ें।",
                    prompt = "वॉइस बायोमेट्रिक सुरक्षा कैसे काम करती है और इसके क्या लाभ हैं?",
                    category = ActionCategory.SECURITY,
                    targetModel = GeminiChatModel.FLASH,
                    priority = 8
                )
            )
        } else {
            actions.add(
                PredictiveAction(
                    id = "deep_reasoning_inquiry",
                    title = "तार्किक व वैज्ञानिक समाधान (o1)",
                    description = "कठिन गणितीय, तार्किक या वैज्ञानिक समस्या का चरणबद्ध समाधान।",
                    prompt = "क्वांटम कंप्यूटिंग और पारंपरिक कंप्यूटिंग के बीच अंतर को चरणबद्ध तार्किक विश्लेषण में स्पष्ट करें।",
                    category = ActionCategory.QUANTUM,
                    targetModel = GeminiChatModel.PRO_REASONING,
                    priority = 7
                )
            )
        }

        // 4. Multidimensional thought exploration
        actions.add(
            PredictiveAction(
                id = "multidimensional_decision_matrix",
                title = "बहुआयामी निर्णय मैट्रिक्स",
                description = "किसी भी जटिल निर्णय को बहुआयामी दृष्टिकोण से परखें।",
                prompt = "किसी महत्वपूर्ण निर्णय को लेते समय बहुआयामी सोच (Multidimensional Thinking) कैसे मदद करती है? उदाहरण सहित बताएं।",
                category = ActionCategory.QUANTUM,
                targetModel = GeminiChatModel.QUANTUM_COGNITION,
                priority = 6
            )
        )

        return actions.sortedByDescending { it.priority }
    }
}
