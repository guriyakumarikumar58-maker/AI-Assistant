package com.example.ai.verbosity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Verbosity Levels for AI Response Generation.
 * - CONCISE: Default mode. Direct, laser-focused, minimal latency, zero filler.
 * - BALANCED: Standard conversational depth with essential context.
 * - COMPREHENSIVE: In-depth, exhaustive explanation, step-by-step breakdown, historical/technical nuances.
 */
enum class VerbosityLevel(
    val id: String,
    val labelHindi: String,
    val descriptionHindi: String,
    val maxSentencesGuideline: Int
) {
    CONCISE(
        id = "concise",
        labelHindi = "संक्षिप्त व सटीक (Concise)",
        descriptionHindi = "सीधा, बिंदुवार और तत्काल उत्तर। बिना किसी अनावश्यक भूमिका या विस्तार के।",
        maxSentencesGuideline = 3
    ),
    BALANCED(
        id = "balanced",
        labelHindi = "संतुलित (Balanced)",
        descriptionHindi = "उपयुक्त विवरण के साथ स्पष्ट और सहज प्रत्युत्तर।",
        maxSentencesGuideline = 6
    ),
    COMPREHENSIVE(
        id = "comprehensive",
        labelHindi = "विस्तृत व गहन (Comprehensive)",
        descriptionHindi = "पूर्ण गहराई, चरण-दर-चरण विश्लेषण, कोड/उदाहरण व संपूर्ण पृष्ठभूमि सहित।",
        maxSentencesGuideline = 20
    )
}

/**
 * Reason for the chosen Verbosity Level:
 * Tells the UI and prompt system why this level was selected.
 */
enum class VerbosityTrigger(val reasonHindi: String) {
    DEFAULT_STANDARD_QUERY("डिफ़ॉल्ट नियम: सामान्य प्रश्नों के लिए संक्षिप्त व सीधा उत्तर"),
    USER_EXPLICIT_REQUEST("उपयोगकर्ता का स्पष्ट निर्देश: 'विस्तार से', 'डिटेल में', या 'पूरा समझाओ'"),
    HIGH_COMPLEXITY_SUBJECT("जटिल विषय: आर्किटेक्चर, गहन विज्ञान, दर्शन, शोध या बहु-चरणीय समस्या"),
    CODE_ANALYSIS_DEBUG("कोड डिबगिंग व तकनीकी कार्यान्वयन: संपूर्ण कोड व व्याख्या आवश्यक"),
    MANUAL_USER_OVERRIDE("उपयोगकर्ता द्वारा मैन्युअल मोड चयन")
}

data class DynamicVerbosityDecision(
    val level: VerbosityLevel,
    val trigger: VerbosityTrigger,
    val confidence: Float,
    val queryComplexityScore: Int // 1 to 10
)

/**
 * DynamicVerbosityEngine:
 * Evaluates incoming user queries and conversational context to determine the appropriate verbosity level.
 * Defaults to CONCISE for standard queries to minimize latency and eliminate fluff.
 * Escalates to COMPREHENSIVE or BALANCED only when explicitly requested, or when context/complexity demands it.
 */
object DynamicVerbosityEngine {

    private val _manualOverride = MutableStateFlow<VerbosityLevel?>(null)
    val manualOverride: StateFlow<VerbosityLevel?> = _manualOverride.asStateFlow()

    private val _lastDecision = MutableStateFlow(
        DynamicVerbosityDecision(
            level = VerbosityLevel.CONCISE,
            trigger = VerbosityTrigger.DEFAULT_STANDARD_QUERY,
            confidence = 0.95f,
            queryComplexityScore = 2
        )
    )
    val lastDecision: StateFlow<DynamicVerbosityDecision> = _lastDecision.asStateFlow()

    fun setManualVerbosity(level: VerbosityLevel?) {
        _manualOverride.value = level
        if (level != null) {
            _lastDecision.value = DynamicVerbosityDecision(
                level = level,
                trigger = VerbosityTrigger.MANUAL_USER_OVERRIDE,
                confidence = 1.0f,
                queryComplexityScore = when (level) {
                    VerbosityLevel.CONCISE -> 2
                    VerbosityLevel.BALANCED -> 5
                    VerbosityLevel.COMPREHENSIVE -> 9
                }
            )
        }
    }

    /**
     * Evaluates the context, user instructions, and technical complexity of the query.
     */
    fun evaluateVerbosity(query: String, contextTurns: Int = 0): DynamicVerbosityDecision {
        val override = _manualOverride.value
        if (override != null) {
            val decision = DynamicVerbosityDecision(
                level = override,
                trigger = VerbosityTrigger.MANUAL_USER_OVERRIDE,
                confidence = 1.0f,
                queryComplexityScore = when (override) {
                    VerbosityLevel.CONCISE -> 2
                    VerbosityLevel.BALANCED -> 5
                    VerbosityLevel.COMPREHENSIVE -> 9
                }
            )
            _lastDecision.value = decision
            return decision
        }

        val clean = query.trim()
        val lower = clean.lowercase(Locale.ROOT)

        // 1. Explicit User Request for Brevity
        if (lower.contains("शॉर्ट में") || lower.contains("short में") || lower.contains("संक्षेप में") ||
            lower.contains("in short") || lower.contains("briefly") || lower.contains("concise") ||
            lower.contains("एक लाइन में") || lower.contains("कम शब्दों में") || lower.contains("quick") ||
            lower.contains("सिर्फ नाम") || lower.contains("सीधा बताओ")
        ) {
            val decision = DynamicVerbosityDecision(
                level = VerbosityLevel.CONCISE,
                trigger = VerbosityTrigger.USER_EXPLICIT_REQUEST,
                confidence = 0.99f,
                queryComplexityScore = 1
            )
            _lastDecision.value = decision
            return decision
        }

        // 2. Explicit User Request for In-Depth Detail
        if (lower.contains("विस्तार से") || lower.contains("विस्तृत") || lower.contains("डिटेल में") ||
            lower.contains("in detail") || lower.contains("comprehensively") || lower.contains("पूरा समझाओ") ||
            lower.contains("गहराई से") || lower.contains("deep dive") || lower.contains("step by step") ||
            lower.contains("चरण-दर-चरण") || lower.contains("संपूर्ण जानकारी") || lower.contains("explain everything") ||
            lower.contains("डिटेल बताओ") || lower.contains("विस्तारपूर्वक")
        ) {
            val decision = DynamicVerbosityDecision(
                level = VerbosityLevel.COMPREHENSIVE,
                trigger = VerbosityTrigger.USER_EXPLICIT_REQUEST,
                confidence = 0.98f,
                queryComplexityScore = 9
            )
            _lastDecision.value = decision
            return decision
        }

        // 3. High Complexity Subject Matter (Architecture, Multi-step coding, Quantum physics, Deep logic)
        val highComplexityKeywords = listOf(
            "आर्किटेक्चर", "architecture", "microservices", "क्वांटम", "quantum",
            "एल्गोरिदम", "algorithm", "डेटा स्ट्रक्चर", "data structure",
            "मशीन लर्निंग", "machine learning", "न्यूरल नेटवर्क", "neural network",
            "संविधान", "constitution", "जटिल", "complex analysis", "तुलना करो", "compare and contrast",
            "रूट कॉज", "root cause", "डेवऑप्स", "ci/cd", "कर्नेल", "kernel", "क्रिप्टोग्राफी", "cryptography"
        )
        val matchesComplexity = highComplexityKeywords.any { lower.contains(it) }

        if (matchesComplexity || clean.length > 200) {
            val decision = DynamicVerbosityDecision(
                level = VerbosityLevel.COMPREHENSIVE,
                trigger = VerbosityTrigger.HIGH_COMPLEXITY_SUBJECT,
                confidence = 0.90f,
                queryComplexityScore = 8
            )
            _lastDecision.value = decision
            return decision
        }

        // 4. Code Generation or Debugging requiring complete solution
        if ((lower.contains("कोड") || lower.contains("code") || lower.contains("फंक्शन") || lower.contains("class") || lower.contains("error")) &&
            (lower.contains("बनाओ") || lower.contains("लिखो") || lower.contains("fix") || lower.contains("debug") || lower.contains("सॉल्व"))
        ) {
            val decision = DynamicVerbosityDecision(
                level = VerbosityLevel.COMPREHENSIVE,
                trigger = VerbosityTrigger.CODE_ANALYSIS_DEBUG,
                confidence = 0.92f,
                queryComplexityScore = 7
            )
            _lastDecision.value = decision
            return decision
        }

        // 5. Default Standard Queries: Concise & Direct (Minimizing latency and conversational fluff)
        val decision = DynamicVerbosityDecision(
            level = VerbosityLevel.CONCISE,
            trigger = VerbosityTrigger.DEFAULT_STANDARD_QUERY,
            confidence = 0.95f,
            queryComplexityScore = 3
        )
        _lastDecision.value = decision
        return decision
    }

    /**
     * Builds a strong system prompt directive to enforce the calculated verbosity level.
     */
    fun buildVerbosityPromptDirective(decision: DynamicVerbosityDecision): String {
        return buildString {
            append("\n[DYNAMIC VERBOSITY CONTROL SYSTEM]:\n")
            when (decision.level) {
                VerbosityLevel.CONCISE -> {
                    append("MODE: CONCISE & DIRECT (डिफ़ॉल्ट संक्षिप्त मोड)\n")
                    append("RULE: उपयोगकर्ता ने संक्षिप्त उत्तर चाहा है या यह सामान्य प्रश्न है। उत्तर बिल्कुल सीधा, बिंदुवार और 2-3 वाक्यों में दें।\n")
                    append("STRICT INSTRUCTION: किसी भी प्रकार की अनावश्यक भूमिका, 'नमस्ते भाई' की लंबी दोहराव, पृष्ठभूमि या बिना मांगे विस्तार पर पूर्ण प्रतिबंध है। सीधे मुद्दे का उत्तर दें ताकि प्रत्युत्तर समय (latency) न्यूनतम रहे।\n")
                }
                VerbosityLevel.BALANCED -> {
                    append("MODE: BALANCED (संतुलित संवाद मोड)\n")
                    append("RULE: उत्तर को सटीक, स्पष्ट और आवश्यक मुख्य बिंदुओं के साथ प्रस्तुत करें। अनावश्यक फैलाव से बचें।\n")
                }
                VerbosityLevel.COMPREHENSIVE -> {
                    append("MODE: COMPREHENSIVE & IN-DEPTH (विस्तृत व गहन मोड)\n")
                    append("REASON: ${decision.trigger.reasonHindi}\n")
                    append("RULE: प्रश्न की जटिलता या उपयोगकर्ता के स्पष्ट अनुरोध के कारण, विषय का पूर्ण, चरण-दर-चरण, गहन और सर्वांगीण विश्लेषण प्रस्तुत करें।\n")
                    append("INSTRUCTION: सभी आवश्यक पहलुओं, उदाहरणों, कोड और तकनीकी बारीकियों को शामिल करें। कोई भी आवश्यक जानकारी अधूरी न छोड़ें।\n")
                }
            }
        }
    }
}
