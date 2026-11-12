package com.example.voice

enum class VoicePersona(
    val id: String,
    val displayNameHindi: String,
    val descriptionHindi: String,
    val basePitch: Float,
    val baseRate: Float,
    val sampleText: String,
    val badge: String
) {
    ARYA_WARM_FRIENDLY(
        id = "ARYA_WARM_FRIENDLY",
        displayNameHindi = "आर्या (मजाकिया, शरारती व सार्कास्टिक - डिफ़ॉल्ट)",
        descriptionHindi = "बिंदास दोस्त — मज़ाक, सार्केस्म, वन-लाइनर्स और तूफानी दिमाग",
        basePitch = 1.18f,
        baseRate = 1.05f,
        sampleText = "अरे बता भाई, आज क्या तूफानी करना है? एकदम फुल एनर्जी और सार्केस्म के साथ हाज़िर हूँ!",
        badge = "सार्कास्टिक व मज़ाकिया 🔥"
    ),
    ARYA_CALM(
        id = "ARYA_CALM",
        displayNameHindi = "आर्या (शांत व सौम्य)",
        descriptionHindi = "शांत, मधुर व सौम्य महिला आवाज़",
        basePitch = 1.15f,
        baseRate = 1.0f,
        sampleText = "नमस्ते! मैं आर्या हूँ। मैं शांत और सहज आवाज़ में आपकी मदद करने के लिए तत्पर हूँ।",
        badge = "शांत महिला"
    ),
    AARAV_MALE(
        id = "AARAV_MALE",
        displayNameHindi = "आरव (गंभीर पुरुष)",
        descriptionHindi = "सशक्त, गंभीर और आधिकारिक पुरुष आवाज़",
        basePitch = 0.78f,
        baseRate = 0.95f,
        sampleText = "नमस्ते! मैं आरव हूँ। आपके सभी कार्यों, सुरक्षा और स्मार्ट होम आदेशों के लिए तैयार हूँ।",
        badge = "गंभीर पुरुष"
    ),
    KAVYA_FRIENDLY(
        id = "KAVYA_FRIENDLY",
        displayNameHindi = "काव्या (ऊर्जावान)",
        descriptionHindi = "दोस्ताना, उत्साही और चुलबुली आवाज़",
        basePitch = 1.35f,
        baseRate = 1.10f,
        sampleText = "अरे वाह! नमस्ते! मैं काव्या हूँ। बताइए आज हम क्या नया और मज़ेदार करने वाले हैं?",
        badge = "उत्साही महिला"
    ),
    VIKRANT_TECH(
        id = "VIKRANT_TECH",
        displayNameHindi = "विक्रान्त (साइ-फ़ाई टेक)",
        descriptionHindi = "रोबोटिक, तकनीकी और भविष्यवादी आवाज़",
        basePitch = 0.60f,
        baseRate = 1.05f,
        sampleText = "प्रणाली ऑनलाइन है। मैं विक्रान्त हूँ—आपका तकनीकी अवतार। आदेश दें।",
        badge = "रोबोटिक टेक"
    ),
    RIYA_HINGLISH(
        id = "RIYA_HINGLISH",
        displayNameHindi = "रिया (मॉडर्न हिंग्लिश)",
        descriptionHindi = "युवा, आधुनिक हिंग्लिश लहजा व अंदाज़",
        basePitch = 1.22f,
        baseRate = 1.15f,
        sampleText = "हेलो! मैं रिया हूँ। लेट्स गेट थिंग्स डन, बताइए आज क्या शेड्यूल और टास्क हैं?",
        badge = "हिंग्लिश लहजा"
    ),
    ALEX_ENGLISH(
        id = "ALEX_ENGLISH",
        displayNameHindi = "एलेक्स (ग्लोबल एक्सेंट)",
        descriptionHindi = "अंतर्राष्ट्रीय अंग्रेज़ी और भारतीय संगम",
        basePitch = 0.98f,
        baseRate = 1.05f,
        sampleText = "Hello! I am Alex. Ready to assist you with smart home, calendar, and notes.",
        badge = "इंटरनेशनल"
    ),
    ARYA_WITTY(
        id = "ARYA_WITTY",
        displayNameHindi = "आर्या विटी (चुटीला व मज़ाकिया)",
        descriptionHindi = "हास्य, व्यंग्य, चुटीला व बेबाक अंदाज़ (Grok-style Witty & Humorous)",
        basePitch = 1.25f,
        baseRate = 1.12f,
        sampleText = "अरे जनाब! आर्या हाज़िर है। चुटकुले, एक्स ट्रेंड्स या गंभीर काम — जो भी बोलें, काम भी होगा और मज़ा भी आएगा!",
        badge = "चुटीला व मज़ाकिया"
    );

    companion object {
        fun fromId(id: String): VoicePersona {
            return entries.firstOrNull { it.id == id } ?: ARYA_WARM_FRIENDLY
        }
    }
}
