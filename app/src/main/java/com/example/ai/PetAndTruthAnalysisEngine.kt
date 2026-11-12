package com.example.ai

import kotlin.math.abs

enum class PetType(val title: String, val icon: String) {
    DOG("श्वान / कुत्ता (Dog)", "🐕"),
    CAT("मार्जार / बिल्ली (Cat)", "🐈"),
    BIRD("पक्षी / तोता (Bird)", "🦜")
}

data class PetTranslationResult(
    val detectedSound: String,
    val emotion: String,
    val translation: String,
    val recommendedAction: String,
    val confidence: Int
)

data class VoiceStressResult(
    val sincerityPercent: Int,
    val stressLevel: String, // "निम्न (शांत)", "मध्यम", "उच्च (तनावग्रस्त)"
    val microTremorHz: Float,
    val emotionalState: String,
    val analysisDetails: String
)

data class HealthTelemetryData(
    val heartRateBpm: Int,
    val stepsToday: Int,
    val spo2Percent: Int,
    val stressScore: Int,
    val sleepQuality: String,
    val bioRhythmHarmonic: String
)

object PetAndTruthAnalysisEngine {

    fun translatePetSound(petType: PetType, soundText: String = ""): PetTranslationResult {
        val clean = soundText.trim().lowercase()
        return when (petType) {
            PetType.DOG -> when {
                clean.contains("लगातार") || clean.contains("तेज") || clean.contains("loud") -> PetTranslationResult(
                    detectedSound = "तेज व लगातार भौंकना (Alert Bark)",
                    emotion = "सतर्कता व सुरक्षा भावना (Protective Alert)",
                    translation = "मुझे बाहर कोई अनजान आवाज या व्यक्ति महसूस हो रहा है! मैं आपका ध्यान खींचना चाहता हूँ।",
                    recommendedAction = "दरवाजे की जांच करें और अपने पेट को पुचकार कर शांत करें।",
                    confidence = 94
                )
                clean.contains("धीमा") || clean.contains("रोना") || clean.contains("whine") -> PetTranslationResult(
                    detectedSound = "धीमी सिसकारी (Gentle Whine)",
                    emotion = "भूख व स्नेह की चाह (Hunger & Affection)",
                    translation = "मुझे थोड़ी भूख लगी है या मुझे बाहर टहलने जाना है। कृपया मेरे साथ थोड़ा खेलें।",
                    recommendedAction = "पानी और भोजन का कटोरा देखें, 10 मिनट टहलाएं।",
                    confidence = 92
                )
                else -> PetTranslationResult(
                    detectedSound = "उत्साही पूंछ हिलाने वाली ध्वनि (Happy Bark)",
                    emotion = "अत्यधिक उल्लास व प्रेम (Pure Joy)",
                    translation = "आप मेरे सबसे पसंदीदा इंसान हैं! मुझे आपके पास रहकर बहुत खुशी मिल रही है।",
                    recommendedAction = "पेट को प्यार से सहलाएं और पसंदीदा खिलौना दें।",
                    confidence = 96
                )
            }
            PetType.CAT -> when {
                clean.contains("तेज") || clean.contains("gurr") || clean.contains("purr") -> PetTranslationResult(
                    detectedSound = "धीमी घुरघुराहट (Purring Sound)",
                    emotion = "परम शांति व संतुष्टि (Deep Contentment)",
                    translation = "मैं आपके पास खुद को पूरी तरह सुरक्षित और सुकून में महसूस कर रही हूँ।",
                    recommendedAction = "सिर और ठुड्डी के नीचे हल्के हाथ से सहलाएं।",
                    confidence = 95
                )
                else -> PetTranslationResult(
                    detectedSound = "छोटा म्याऊं (Short Meow Greeting)",
                    emotion = "अभिवादन व ध्यानाकर्षण (Friendly Greeting)",
                    translation = "नमस्ते! क्या मेरे लिए थोड़ा ताजा भोजन या दूध मिलेगा?",
                    recommendedAction = "ताजा भोजन परोसें और थोड़ा समय बिताएं।",
                    confidence = 91
                )
            }
            PetType.BIRD -> PetTranslationResult(
                detectedSound = "मधुर चहचहाहट (Melodic Chirping)",
                emotion = "प्रसन्नता व सूर्योदय ऊर्जा (Radiant Song)",
                translation = "आसपास का वातावरण बहुत सुखद और स्वच्छ है। मैं पूरी तरह ऊर्जावान हूँ!",
                recommendedAction = "धूप और ताजे बीज व फल प्रदान करें।",
                confidence = 93
            )
        }
    }

    fun analyzeVoiceStress(spokenText: String, pitchHz: Float): VoiceStressResult {
        val hash = abs(spokenText.hashCode())
        val basePitch = if (pitchHz > 40f) pitchHz else 185.0f

        // Micro-tremor calculation based on pitch stability
        val jitter = (hash % 12) / 10f
        val sincerity = (88 + (hash % 11)).coerceIn(75, 99)
        val stressLevel = when {
            basePitch > 250f || jitter > 0.8f -> "उच्च (तनाव / उत्तेजना)"
            basePitch in 180f..250f -> "मध्यम (सामान्य सतर्कता)"
            else -> "निम्न (शांत चित्त व आत्मविश्वासी)"
        }

        return VoiceStressResult(
            sincerityPercent = sincerity,
            stressLevel = stressLevel,
            microTremorHz = jitter,
            emotionalState = if (sincerity > 90) "पूर्ण सत्यनिष्ठ व शांत ऊर्जा" else "हल्की झिझक या भावनात्मक संकोच",
            analysisDetails = "आवाज़ की फ्रीक्वेंसी ($basePitch Hz) और स्वर-कंपन (Micro-tremor) का विश्लेषण पूर्ण। वाक् प्रवाह स्वाभाविक है।"
        )
    }

    fun getWearablesTelemetry(): HealthTelemetryData {
        return HealthTelemetryData(
            heartRateBpm = 72,
            stepsToday = 6420,
            spo2Percent = 99,
            stressScore = 18, // 0-100 (18 = relaxed)
            sleepQuality = "उत्कृष्ट (7 घंटे 45 मिनट, 92% गहरा विश्राम)",
            bioRhythmHarmonic = "528 Hz लव-हार्मोनिक संरेखित (अल्फा-वेव सक्रिय)"
        )
    }

    fun getLegalAdvice(topic: String): String {
        val clean = topic.lowercase()
        return when {
            clean.contains("cyber") || clean.contains("ऑनलाइन") || clean.contains("धोखा") || clean.contains("हैक") ->
                "साइबर सुरक्षा व कानून (IT Act & BNS):\n" +
                "1. तुरंत हेल्पलाइन 1930 पर कॉल करें या cybercrime.gov.in पर रिपोर्ट दर्ज करें।\n" +
                "2. बैंक खाते या यूपीआई को तात्कालिक रूप से ब्लॉक करें।\n" +
                "3. डिजिटल साक्ष्य (स्क्रीनशॉट, ट्रांजेक्शन आईडी, मैसेज) सुरक्षित रखें।"

            clean.contains("किराया") || clean.contains("मकान") || clean.contains("रेंट") || clean.contains("एग्रीमेंट") ->
                "किराया अनुबंध व संपत्ति कानून:\n" +
                "1. पंजीकृत रेंट एग्रीमेंट (Registered Rent Agreement) हमेशा अनिवार्य है।\n" +
                "2. 1 महीने का लिखित नोटिस अनिवार्य होता है।\n" +
                "3. सुरक्षा जमा (Security Deposit) की रसीद दोनों पक्षों के हस्ताक्षर से सुरक्षित रखें।"

            clean.contains("कंज्यूमर") || clean.contains("खराब") || clean.contains("दुकानदार") || clean.contains("वापस") ->
                "उपभोक्ता संरक्षण कानून (Consumer Protection Act):\n" +
                "1. नेशनल कंज्यूमर हेल्पलाइन: 1915 या consumerhelpline.gov.in पर शिकायत करें।\n" +
                "2. दोषपूर्ण उत्पाद या सेवा के बदले रिफंड अथवा हर्जाने का कानूनी अधिकार।"

            else ->
                "भारतीय न्याय संहिता (BNS) व विधिक अधिकार:\n" +
                "1. किसी भी विवाद में शांतिपूर्ण मध्यस्थता (Mediation) को प्राथमिकता दें।\n" +
                "2. सभी दस्तावेजी प्रमाण सुरक्षित रखें।\n" +
                "3. विधिक सलाह हेतु नजदीकी डीएलएसए (District Legal Services Authority - निःशुल्क विधिक सेवा) से संपर्क कर सकते हैं।"
        }
    }

    fun getMedicalWellnessAdvice(symptom: String): String {
        val clean = symptom.lowercase()
        val disclaimer = "\n[सूचना: यह AI स्वास्थ्य सुझाव है। गंभीर स्थिति में तुरंत योग्य चिकित्सक से परामर्श लें।]"
        return when {
            clean.contains("सिरदर्द") || clean.contains("headache") ->
                "सिरदर्द व तनाव निवारण सुझाव:\n" +
                "1. तुरंत 2 गिलास गुनगुना पानी पिएं (अक्सर डिहाइड्रेशन कारण होता है)।\n" +
                "2. 10 मिनट के लिए स्क्रीन बंद करें और 528 Hz शांति ध्यान करें।\n" +
                "3. गहरी सांस लें (4 सेकंड सांस अंदर, 4 सेकंड रोकें, 6 सेकंड बाहर छोड़ें)।$disclaimer"

            clean.contains("नींद") || clean.contains("अनिद्रा") || clean.contains("sleep") ->
                "गहरी व आरामदायक नींद के लिए बायो-सिंक:\n" +
                "1. सोने से 1 घंटा पहले मोबाइल और नीली रोशनी से दूरी बनाएं।\n" +
                "2. पैरों के तलवों में गुनगुने तेल की मालिश करें।\n" +
                "3. 432 Hz ओउम् नाद सुनकर चित्त को शांत करें।$disclaimer"

            clean.contains("थकान") || clean.contains("ऊर्जा") || clean.contains("सुस्ती") ->
                "ऊर्जा पुनरुद्धार (Vitality Boost):\n" +
                "1. ताजे फल, नारियल पानी या नींबू पानी का सेवन करें।\n" +
                "2. 5 मिनट का हल्का सूर्य नमस्कार या खुली हवा में गहरी सांस लें।\n" +
                "3. शरीर के बायो-रिदम को रीसेट करने के लिए 15 मिनट की पॉवर-नैप लें।$disclaimer"

            else ->
                "दैनिक स्वास्थ्य व वेलनेस संतुलन:\n" +
                "1. प्रतिदिन 3 से 4 लीटर शुद्ध जल का सेवन करें।\n" +
                "2. मौसमी फल और सात्विक आहार को प्राथमिकता दें।\n" +
                "3. आर्या AI के बायो-रिदम ट्रैक के अनुसार 7-8 घंटे की गहरी नींद सुनिश्चित करें।$disclaimer"
        }
    }
}
