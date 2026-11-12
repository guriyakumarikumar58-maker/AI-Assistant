package com.example.translation

import com.example.gemini.GeminiRepository

data class TranslationLanguage(
    val code: String,
    val name: String,
    val englishName: String,
    val nativeName: String,
    val flag: String,
    val ttsTag: String
)

data class TranslationResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val originalText: String,
    val translatedText: String,
    val sourceLang: TranslationLanguage,
    val targetLang: TranslationLanguage,
    val phonetic: String? = null,
    val speakerId: Int = 1, // 1 for Person A, 2 for Person B
    val timestamp: Long = System.currentTimeMillis()
)

data class QuickPhrase(
    val category: String,
    val iconEmoji: String,
    val textHindi: String,
    val textEnglish: String
)

object RealtimeTranslationEngine {

    val supportedLanguages = listOf(
        TranslationLanguage("hi", "हिंदी", "Hindi", "हिन्दी", "🇮🇳", "hi-IN"),
        TranslationLanguage("en", "अंग्रेज़ी", "English", "English", "🇬🇧", "en-US"),
        TranslationLanguage("es", "स्पैनिश", "Spanish", "Español", "🇪🇸", "es-ES"),
        TranslationLanguage("fr", "फ्रेंच", "French", "Français", "🇫🇷", "fr-FR"),
        TranslationLanguage("de", "जर्मन", "German", "Deutsch", "🇩🇪", "de-DE"),
        TranslationLanguage("ja", "जापानी", "Japanese", "日本語", "🇯🇵", "ja-JP"),
        TranslationLanguage("zh", "चीनी", "Chinese", "中文", "🇨🇳", "zh-CN"),
        TranslationLanguage("ru", "रूसी", "Russian", "Русский", "🇷🇺", "ru-RU"),
        TranslationLanguage("ar", "अरबी", "Arabic", "العربية", "🇸🇦", "ar-SA"),
        TranslationLanguage("bn", "बंगाली", "Bengali", "বাংলা", "🇮🇳", "bn-IN"),
        TranslationLanguage("mr", "मराठी", "Marathi", "मराठी", "🇮🇳", "mr-IN"),
        TranslationLanguage("ta", "तमिल", "Tamil", "தமிழ்", "🇮🇳", "ta-IN"),
        TranslationLanguage("te", "तेलुगु", "Telugu", "తెలుగు", "🇮🇳", "te-IN"),
        TranslationLanguage("gu", "गुजराती", "Gujarati", "ગુજરાતી", "🇮🇳", "gu-IN"),
        TranslationLanguage("pa", "पंजाबी", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", "pa-IN"),
        TranslationLanguage("ur", "उर्दू", "Urdu", "اردو", "🇵🇰", "ur-PK"),
        TranslationLanguage("kn", "कन्नड़", "Kannada", "ಕನ್ನಡ", "🇮🇳", "kn-IN"),
        TranslationLanguage("ml", "मलयालम", "Malayalam", "മലയാളം", "🇮🇳", "ml-IN"),
        TranslationLanguage("or", "ओडिया", "Odia", "ଓଡ଼ିଆ", "🇮🇳", "or-IN"),
        TranslationLanguage("as", "असमिया", "Assamese", "অসমীয়া", "🇮🇳", "as-IN"),
        TranslationLanguage("sa", "संस्कृत", "Sanskrit", "संस्कृतम्", "🇮🇳", "sa-IN"),
        TranslationLanguage("it", "इतालवी", "Italian", "Italiano", "🇮🇹", "it-IT"),
        TranslationLanguage("ko", "कोरियाई", "Korean", "한국어", "🇰🇷", "ko-KR"),
        TranslationLanguage("pt", "पुर्तगाली", "Portuguese", "Português", "🇧🇷", "pt-BR"),
        TranslationLanguage("nl", "डच", "Dutch", "Nederlands", "🇳🇱", "nl-NL"),
        TranslationLanguage("tr", "तुर्की", "Turkish", "Türkçe", "🇹🇷", "tr-TR")
    )

    fun getLanguageByCode(code: String): TranslationLanguage {
        return supportedLanguages.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: supportedLanguages[0]
    }

    val quickPhrases = listOf(
        QuickPhrase("अभिवादन (Greetings)", "👋", "नमस्ते! आप कैसे हैं?", "Hello! How are you?"),
        QuickPhrase("अभिवादन (Greetings)", "🙏", "आपसे मिलकर बहुत खुशी हुई।", "Nice to meet you."),
        QuickPhrase("अभिवादन (Greetings)", "✨", "आपका दिन शुभ हो!", "Have a wonderful day!"),
        QuickPhrase("यात्रा (Travel)", "✈️", "हवाई अड्डा या रेलवे स्टेशन कहाँ है?", "Where is the airport or train station?"),
        QuickPhrase("यात्रा (Travel)", "🚕", "मुझे निकटतम होटल तक जाना है।", "I need to go to the nearest hotel."),
        QuickPhrase("यात्रा (Travel)", "🗺️", "क्या आप मुझे रास्ता बता सकते हैं?", "Can you please show me the way?"),
        QuickPhrase("आपातकाल (Emergency)", "🚨", "कृपया मेरी मदद कीजिए!", "Please help me!"),
        QuickPhrase("आपातकाल (Emergency)", "🏥", "मुझे तुरंत डॉक्टर या अस्पताल की जरूरत है।", "I need a doctor or hospital immediately."),
        QuickPhrase("आपातकाल (Emergency)", "👮", "निकटतम पुलिस स्टेशन कहाँ है?", "Where is the nearest police station?"),
        QuickPhrase("भोजन (Dining)", "🍽️", "कृपया मेनू कार्ड दिखाइए।", "Please show me the menu."),
        QuickPhrase("भोजन (Dining)", "💧", "क्या मुझे पीने का पानी मिल सकता है?", "Can I have some drinking water?"),
        QuickPhrase("भोजन (Dining)", "😋", "यह खाना बहुत स्वादिष्ट था, धन्यवाद!", "This food was delicious, thank you!"),
        QuickPhrase("खरीदारी (Shopping)", "🛍️", "इसकी कीमत क्या है?", "How much does this cost?"),
        QuickPhrase("खरीदारी (Shopping)", "💳", "क्या आप कार्ड या ऑनलाइन भुगतान लेते हैं?", "Do you accept card or online payments?"),
        QuickPhrase("बातचीत (Casual)", "🤝", "आपका बहुत-बहुत धन्यवाद!", "Thank you very much!"),
        QuickPhrase("बातचीत (Casual)", "🙂", "माफ़ कीजिए, क्या आप अंग्रेज़ी बोलते हैं?", "Excuse me, do you speak English?")
    )

    // Offline local phrase translations for instant zero-latency responses
    private val offlineDictionary = mapOf(
        "hello" to mapOf("hi" to "नमस्ते", "es" to "Hola", "fr" to "Bonjour", "de" to "Hallo", "ja" to "こんにちは", "ar" to "مرحبا"),
        "how are you" to mapOf("hi" to "आप कैसे हैं?", "es" to "¿Cómo estás?", "fr" to "Comment allez-vous?", "de" to "Wie geht es Ihnen?", "ja" to "お元気ですか？"),
        "thank you" to mapOf("hi" to "धन्यवाद", "es" to "Gracias", "fr" to "Merci", "de" to "Danke", "ja" to "ありがとうございます", "ar" to "شكرا لك"),
        "where is the bathroom" to mapOf("hi" to "वॉशरूम कहाँ है?", "es" to "¿Dónde está el baño?", "fr" to "Où sont les toilettes?", "de" to "Wo ist die Toilette?"),
        "please help me" to mapOf("hi" to "कृपया मेरी मदद करें", "es" to "Por favor ayúdame", "fr" to "Aidez-moi s'il vous plaît", "de" to "Bitte helfen Sie mir"),
        "नमस्ते" to mapOf("en" to "Hello", "es" to "Hola", "fr" to "Bonjour", "de" to "Hallo", "ja" to "こんにちは"),
        "आप कैसे हैं" to mapOf("en" to "How are you?", "es" to "¿Cómo estás?", "fr" to "Comment allez-vous?"),
        "धन्यवाद" to mapOf("en" to "Thank you", "es" to "Gracias", "fr" to "Merci", "de" to "Danke", "ja" to "ありがとう"),
        "मुझे मदद चाहिए" to mapOf("en" to "I need help", "es" to "Necesito ayuda", "fr" to "J'ai besoin d'aide")
    )

    fun findOfflineTranslation(text: String, targetLangCode: String): String? {
        val normalized = text.trim().lowercase().removeSuffix("?").removeSuffix("।").removeSuffix(".")
        for ((key, translations) in offlineDictionary) {
            if (normalized == key || normalized.contains(key)) {
                translations[targetLangCode]?.let { return it }
            }
        }
        return null
    }

    /**
     * Translates text using Gemini repository with fallback to local translation.
     */
    suspend fun translate(
        geminiRepository: GeminiRepository,
        text: String,
        source: TranslationLanguage,
        target: TranslationLanguage,
        speakerId: Int = 1
    ): TranslationResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return TranslationResult(
                originalText = text,
                translatedText = "",
                sourceLang = source,
                targetLang = target,
                speakerId = speakerId
            )
        }

        // If source and target are the same language
        if (source.code == target.code) {
            return TranslationResult(
                originalText = trimmed,
                translatedText = trimmed,
                sourceLang = source,
                targetLang = target,
                speakerId = speakerId
            )
        }

        // Check offline dictionary first for common quick phrases
        val offlineMatch = findOfflineTranslation(trimmed, target.code)

        val prompt = "Translate the following text accurately and idiomatically from ${source.englishName} to ${target.englishName}.\n" +
                "Source Text: \"$trimmed\"\n" +
                "Directives:\n" +
                "1. Provide ONLY the natural translated text on the first line.\n" +
                "2. If the target language script is non-Latin (like Hindi, Japanese, Russian, Arabic, Chinese), provide the phonetic Romanized pronunciation on the second line starting with 'Pronunciation: '.\n" +
                "3. Never add explanations, asterisks, or commentary."

        val aiResult = geminiRepository.chatWithGemini(
            prompt = prompt,
            model = com.example.gemini.GeminiChatModel.FLASH
        )

        return if (aiResult.isSuccess) {
            val fullResponse = aiResult.getOrNull()?.first?.trim() ?: (offlineMatch ?: trimmed)
            val lines = fullResponse.lines().map { it.trim() }.filter { it.isNotBlank() }
            val mainTranslation = lines.firstOrNull()?.replace("\"", "") ?: (offlineMatch ?: trimmed)
            val pronunciationLine = lines.firstOrNull { it.startsWith("Pronunciation:", ignoreCase = true) }
                ?.substringAfter(":")?.trim()

            TranslationResult(
                originalText = trimmed,
                translatedText = mainTranslation,
                sourceLang = source,
                targetLang = target,
                phonetic = pronunciationLine,
                speakerId = speakerId
            )
        } else {
            TranslationResult(
                originalText = trimmed,
                translatedText = offlineMatch ?: "[$trimmed - अनुवाद: ${target.name}]",
                sourceLang = source,
                targetLang = target,
                speakerId = speakerId
            )
        }
    }
}
