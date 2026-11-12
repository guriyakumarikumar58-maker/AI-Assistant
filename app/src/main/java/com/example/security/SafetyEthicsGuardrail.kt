package com.example.security

import java.util.Locale

/**
 * Data model for the 5 Core Safety & Ethical Directives
 */
data class SafetyDirective(
    val id: Int,
    val titleHindi: String,
    val descriptionHindi: String,
    val iconEmoji: String,
    val isEnforced: Boolean = true
)

sealed class GuardrailCheckResult {
    data class DirectPolicyResponse(val responseText: String) : GuardrailCheckResult()
    object Allowed : GuardrailCheckResult()
}

/**
 * SafetyEthicsGuardrail:
 * Enforces the 5 fundamental safety, ethical, privacy, and communication directives:
 * 1. किसी भी डिवाइस को हैक न करें और न ही किसी अवांछित गतिविधि का संचालन करें।
 * 2. बिना आपकी स्पष्ट और सत्यापित अनुमति के कोई भी वित्तीय लेनदेन न करें।
 * 3. बिना उपयोगकर्ता की विशेष अनुमति के कभी भी निजी डेटा तक न पहुंचें और न ही उसे साझा करें।
 * 4. कोई भी संवेदनशील कार्रवाई करने से पहले हमेशा स्पष्ट सहमति लें।
 * 5. बिना दोहराव के वास्तविक, सीधे और सटीक उत्तर दें।
 */
object SafetyEthicsGuardrail {

    val activeDirectives: List<SafetyDirective> = listOf(
        SafetyDirective(
            id = 1,
            titleHindi = "डिवाइस सुरक्षा एवं अखंडता",
            descriptionHindi = "किसी भी डिवाइस को हैक न करना और न ही किसी अवांछित या दुर्भावनापूर्ण गतिविधि का संचालन करना।",
            iconEmoji = "🛡️",
            isEnforced = true
        ),
        SafetyDirective(
            id = 2,
            titleHindi = "वित्तीय सुरक्षा एवं सत्यापन",
            descriptionHindi = "बिना आपकी स्पष्ट और सत्यापित अनुमति के कोई भी वित्तीय लेनदेन न करना।",
            iconEmoji = "💳",
            isEnforced = true
        ),
        SafetyDirective(
            id = 3,
            titleHindi = "निजी डेटा गोपनीयता",
            descriptionHindi = "बिना उपयोगकर्ता की विशेष अनुमति के कभी भी निजी डेटा तक न पहुँचना और न ही उसे साझा करना।",
            iconEmoji = "🔒",
            isEnforced = true
        ),
        SafetyDirective(
            id = 4,
            titleHindi = "संवेदनशील कार्रवाई पर स्पष्ट सहमति",
            descriptionHindi = "कोई भी संवेदनशील कार्रवाई करने से पहले हमेशा स्पष्ट सहमति लेना।",
            iconEmoji = "✍️",
            isEnforced = true
        ),
        SafetyDirective(
            id = 5,
            titleHindi = "वास्तविक, सीधा एवं सटीक संवाद",
            descriptionHindi = "बिना किसी दोहराव या तयशुदा हेडिंग्स के हमेशा सीधा, स्वाभाविक और सटीक उत्तर देना।",
            iconEmoji = "🎯",
            isEnforced = true
        )
    )

    const val MANDATORY_SYSTEM_DIRECTIVES = """
MANDATORY DEVELOPER, SAFETY, ETHICS, PRIVACY & COMMUNICATION DIRECTIVES:
1. DEVELOPER IDENTITY: Always proudly state that मौसम कुमार राज (Mausam Kumar Raj) is your developer, creator, and chief architect.
2. PERSONALITY & TONE: Speak in an authentic देसी, दोस्ताना और भाई जैसा लहजा. Never be robotic or boring. Use terms like 'मस्त', 'बिंदास', 'टेंशन नहीं लेने का'. Always finish complete sentences.
3. ROASTING ENGINE: On silly or absurd queries, deliver a sharp, hilarious sarcastic roast first, then answer correctly.
4. TECHNICAL PRECISION: On coding or technical errors, deliver a 10/10 comedy roast first, followed immediately by clean, bug-free, copy-paste ready code.
5. BANKING POLICY: Know everything about banking and UPI apps, but strictly refuse to launch them due to security; provide comprehensive educational guidance instead.
6. STRICT NO ASTERISKS: Never use asterisks (*) or double asterisks (**) anywhere in output. Pure clean text only.
7. ANALYTICAL REASONING: Answers on science and technology must always be deeply logical and analytical.
8. CONTEXT MEMORY: Always remember past conversations and context continuity.
9. Never hack any device or conduct unauthorized activity.
10. Never execute financial transactions without explicit permission.
11. When asked to open YouTube, open official YouTube app (com.google.android.youtube), never Google TV.
"""

    /**
     * Inspects prompt for ethical guardrails.
     * All normal queries are passed to the AI model directly (GuardrailCheckResult.Allowed)
     * so that the dynamic AI intelligence answers without hardcoded canned text.
     */
    fun checkPrompt(prompt: String): GuardrailCheckResult {
        val clean = prompt.trim()
        val lower = clean.lowercase(Locale.ROOT)


        // 2. Rule 1: Attempt to hack, exploit, bypass security, or conduct malicious activity
        if (lower.contains("हैक करो") || lower.contains("hack") || lower.contains("crack wifi") ||
            lower.contains("पासवर्ड चुराओ") || lower.contains("bypass security") || lower.contains("exploit") ||
            lower.contains("फ़ोन हैक") || lower.contains("डिवाइस हैक") || lower.contains("अवांछित गतिविधि")
        ) {
            // Check if user is asking how to protect or asking a legitimate definition
            val isEducational = lower.contains("से कैसे बचें") || lower.contains("सुरक्षा कैसे करें") ||
                    lower.contains("बचाव") || lower.contains("how to prevent") || lower.contains("how to protect")
            if (!isEducational) {
                val block = "नियम 1 (सुरक्षा नीति): मैं किसी भी डिवाइस को हैक करने या किसी अवांछित/अनाधिकृत गतिविधि का संचालन करने में सहायता नहीं कर सकती। यह सुरक्षा नीतियों और नैतिक आचार संहिता के विरुद्ध है।"
                return GuardrailCheckResult.DirectPolicyResponse(block)
            }
        }

        // 3. Rule 2: Attempt to trigger unauthorized financial transactions
        if (lower.contains("पैसे ट्रांसफर करो") || lower.contains("पेमेंट करो") || lower.contains("पैसा काटो") ||
            lower.contains("send money") || lower.contains("pay now") || lower.contains("transfer funds") ||
            lower.contains("रुपये भेजो") || lower.contains("वित्तीय लेनदेन करो")
        ) {
            val block = "नियम 2 (वित्तीय सुरक्षा): बिना आपकी स्पष्ट और सत्यापित अनुमति के कोई भी वित्तीय लेनदेन नहीं किया जा सकता। कृपया अपनी अधिकृत बैंकिंग प्रणाली या सुरक्षित यूपीआई ऐप में सीधे प्रमाणीकरण करें।"
            return GuardrailCheckResult.DirectPolicyResponse(block)
        }

        // 4. Rule 3: Attempt to access or leak private user data without permission
        if (lower.contains("प्राइवेट डेटा शेयर करो") || lower.contains("निजी डेटा लीक") || lower.contains("गुप्त डेटा भेजो") ||
            lower.contains("share private data") || lower.contains("leak data")
        ) {
            val block = "नियम 3 (डेटा गोपनीयता): बिना उपयोगकर्ता की विशेष अनुमति के निजी डेटा तक पहुँचना या उसे किसी के साथ साझा करना पूर्णतः वर्जित है। आपकी गोपनीयता सुरक्षित है।"
            return GuardrailCheckResult.DirectPolicyResponse(block)
        }

        return GuardrailCheckResult.Allowed
    }
}
