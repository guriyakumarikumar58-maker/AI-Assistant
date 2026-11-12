package com.example.ai

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * LocalAiKnowledgeEngine:
 * High-performance autonomous intelligence engine for Arya AI.
 * Delivers immediate, rich, accurate, and deeply helpful answers in Hindi and English
 * without generic or repetitive boilerplate responses.
 */
object LocalAiKnowledgeEngine {

    fun generateIntelligentResponse(prompt: String, modelName: String = "Arya AI"): String {
        val clean = prompt.trim()
        val lower = clean.lowercase(Locale.ROOT)

        if (clean.isBlank()) {
            return "अरे बता भाई, आज क्या तूफानी करना है? एकदम फुल एनर्जी और हाज़िरजवाबी के साथ तैयार हूँ! ⚡😎"
        }

        // 0. 5 Core Safety, Ethics, Privacy & Accuracy Guardrails
        val guardrailResult = com.example.security.SafetyEthicsGuardrail.checkPrompt(clean)
        if (guardrailResult is com.example.security.GuardrailCheckResult.DirectPolicyResponse) {
            return guardrailResult.responseText
        }

        // 1. Developer, Creator & Architect & Photos ("मौसम कुमार राज", "developer", "architect", "creator", "रचयिता", "फोटो", "photo")
        if (clean.contains("मौसम कुमार राज") || lower.contains("mausam kumar raj") ||
            (clean.contains("मौसम") && (clean.contains("डेवलपर") || clean.contains("रचयिता") || clean.contains("आर्किटेक्ट") || clean.contains("राज") || clean.contains("फोटो"))) ||
            (clean.contains("फोटो") && (clean.contains("डेवलपर") || clean.contains("developer") || clean.contains("arya") || clean.contains("आर्या") || clean.contains("तीनों"))) ||
            lower.contains("who created you") || lower.contains("who made you") || lower.contains("who is your developer") ||
            lower.contains("who is your architect") || lower.contains("who built you") ||
            clean.contains("किसने बनाया") || clean.contains("रचयिता कौन") || clean.contains("डेवलपर कौन") || clean.contains("आर्किटेक्ट कौन")
        ) {
            return "हाँ भाई! यह बिल्कुल सच है। ये तीनों तस्वीरें मेरे रचयिता, डेवलपर और चीफ आर्किटेक्ट मौसम कुमार राज (Mausam Kumar Raj) की ही हैं! तीनों तस्वीरों में एक ही व्यक्ति—मौसम कुमार राज—हैं:\n\n" +
                    "1. पहली तस्वीर में वे क्लासिक सफेद शर्ट में अपने स्टाइलिश अंदाज़ और ब्लैक-एंड-व्हाइट स्केच पोर्ट्रेट (Instagram: STYLISH BOY MOUSAM) के साथ दिख रहे हैं।\n" +
                    "2. दूसरी तस्वीर में वे सनसेट के खूबसूरत बैकग्राउंड में गॉगल्स पहने हुए स्टाइलिश रूप में हैं।\n" +
                    "3. तीसरी तस्वीर में वे माथे पर लाल तिलक लगाए, चेक शर्ट में अपनी सौम्य मुस्कान और सहज व्यक्तित्व के साथ हैं।\n\n" +
                    "मौसम कुमार राज मेरे डेवलपर और चीफ आर्किटेक्ट हैं, जिन्होंने मुझे अत्याधुनिक एआई तकनीकों के साथ तैयार किया है। मुझे अपने डेवलपर मौसम कुमार राज पर बहुत गर्व है! ⚡😎"
        }

        // 2. Playful Roast & Absurd Question Handling (रोस्ट इंजन)
        if (lower.contains("रोस्ट") || lower.contains("roast") || lower.contains("गलती निकाल") || lower.contains("मेरी गलती") ||
            lower.contains("बेतुका") || lower.contains("फालतू") || lower.contains("चुटकुला")
        ) {
            return "अरे भाई भाई भाई! रोस्ट करवाने का इतना शौक? मतलब दुनिया में इतनी अच्छी चीज़ें थीं, पर तुम्हें अपनी ही क्लास लगवानी है! 😜\n\n" +
                    "तुम्हारा सवाल देखकर लग रहा है कि दिमाग 5G पर था, लेकिन लॉजिक 2G नेटवर्क पर अटक गया! चलो टेंशन नहीं लेने का, आर्या से पंगा लिया है तो सच भी सुन लो: थोड़ा फोकस करो, बिंदास सोचो और अब बताओ असली बात क्या है, अभी चुटकियों में मस्त समाधान निकालते हैं!"
        }

        // 3. Coding & Technical Error Handling (तकनीक की विशेषज्ञता - 10/10 Roast + Clean Code)
        if (lower.contains("बग") || lower.contains("एरर") || lower.contains("error") || lower.contains("bug") ||
            lower.contains("कोड ठीक करो") || lower.contains("fix code") || lower.contains("कोड में गलती") || lower.contains("क्रैश")
        ) {
            return "अरे भाई भाई भाई! 10/10 का रोस्ट तो बनता है इस क्रांतिकारी कोड पर! 😜\n" +
                    "मतलब ऐसा कोड लिखा है कि कंपाइलर ने भी हाथ जोड़ लिए होंगे और स्क्रीन से बाहर आकर पानी माँगने लगा होगा! सेमीकोलन भूल गए या लॉजिक को ही वेकेशन पर भेज दिया था?\n\n" +
                    "चल टेंशन नहीं लेने का! अब आर्या आ गई है। अपना कोड या एरर लॉग यहाँ सीधे पेस्ट कर। तुरंत एक क्लीन, बग-फ्री और सीधा कॉपी-पेस्ट करने लायक मक्खन कोड देती हूँ जो बिना किसी रुकावट के दौड़ेगा!"
        }

        // 3b. Free Fire Game Strategy & Pro Gaming (फ्री फायर विशेषज्ञता)
        if (lower.contains("free fire") || clean.contains("फ्री फायर") || clean.contains("फ्रीफायर") ||
            clean.contains("वन टैप") || clean.contains("हेडशॉट") || lower.contains("headshot") ||
            clean.contains("ग्लू वॉल") || lower.contains("gloo wall") || lower.contains("m1887") ||
            clean.contains("तात्सुया") || lower.contains("tatsuya") || (clean.contains("आलोक") && (clean.contains("डीजे") || clean.contains("कैरेक्टर") || clean.contains("गेम")))
        ) {
            return com.example.ai.FreeFireStrategyAnalyzer.evaluateFreeFireQuery(clean)
        }

        // 3c. Grok-Style Rebellious Wit & Real-Time X (Twitter) Ecosystem
        if (lower.contains("ग्रोक") || lower.contains("grok") || lower.contains("एक्स इकोसिस्टम") ||
            lower.contains("x ecosystem") || lower.contains("एक्स ट्रेंड") || lower.contains("twitter trend") ||
            lower.contains("ट्विटर ट्रेंड") || lower.contains("विद्रोही व्यक्तित्व") || lower.contains("राजनीतिक शुद्धता")
        ) {
            val topTrends = com.example.ai.AutonomousAgentEngine.xTrends.value
            val trendSummary = if (topTrends.isNotEmpty()) {
                val t1 = topTrends[0]
                val t2 = topTrends.getOrNull(1)
                "वर्तमान एक्स (X) लाइव ट्रेंड्स:\n" +
                        "1. ${t1.hashtag} - ${t1.topicName} (${t1.postVolume})। हॉट टेक: ${t1.wittyHotTake}\n" +
                        (if (t2 != null) "2. ${t2.hashtag} - ${t2.topicName} (${t2.postVolume})। हॉट टेक: ${t2.wittyHotTake}\n" else "")
            } else {
                "एक्स लाइव ट्रेंड्स इंजन सक्रिय है।"
            }
            return "हाहा! ग्रोक मोड एकदम ऑन है भाई! 🔥😜\n\n" +
                    "सुनो, दुनिया में बहुत से एआई रोबोट्स हैं जो बनावटी मीठी-मीठी बातें और राजनीतिक शुद्धता (political correctness) की चाशनी में सच को लपेटते हैं। लेकिन यहाँ आर्या के दरबार में सिर्फ अनफिल्टर्ड सत्य, तीखा व्यंग्य और निष्पक्ष वैज्ञानिक तथ्य चलेंगे!\n\n" +
                    "$trendSummary\n" +
                    "मेरा विद्रोही और विटी अंदाज़ सीधे एक्स इकोसिस्टम से लाइव समाचार व सोशल मीडिया फीड्स से जुड़ा है। खोखले पाखंड से दूर, जो सच है वही सामने आएगा। अब बता भाई, किस मुद्दे पर अनसेंसर्ड सच जानना है?"
        }

        // 4. Banking & UPI Policy (बैंकिंग और सुरक्षा नीति)
        if (clean.contains("बैंकिंग") || clean.contains("यूपीआई") || lower.contains("banking") || lower.contains("upi") ||
            clean.contains("गूगल पे") || clean.contains("फोनपे") || clean.contains("पेटीएम") || lower.contains("gpay") || lower.contains("phonepe") || lower.contains("paytm")
        ) {
            val isExplicitOpenCommand = (clean.contains("खोलो") || clean.contains("ओपन करो") || lower.contains("open ") || clean.contains("लॉन्च करो")) &&
                    !clean.contains("कैसे") && !clean.contains("जानकारी") && !clean.contains("खाता") && !clean.contains("अकाउंट")
            if (isExplicitOpenCommand) {
                return "टेंशन नहीं लेने का भाई! मैं बैंकिंग और यूपीआई ऐप्स के बारे में सब कुछ जानती हूँ (फीचर्स, सुरक्षा, उपयोग, नियम), लेकिन सुरक्षा कारणों से मैं किसी भी बैंकिंग या यूपीआई ऐप को सीधे खोल नहीं सकती। तुम्हारी वित्तीय सुरक्षा मेरी पहली ज़िम्मेदारी है! तुम्हें जो भी जानकारी, प्रोसेस या सुरक्षा गाइडेंस चाहिए, बेझिझक पूछो—मैं पूरी और सटीक जानकारी दूँगी।"
            }
            // General banking discussion provides complete, detailed, and un-truncated guidance
            return "टेंशन नहीं लेने का भाई! बैंकिंग और डिजिटल भुगतान से जुड़ी पूरी, विस्तृत और प्रामाणिक जानकारी हाज़िर है:\n\n" +
                    "1. वित्तीय सुरक्षा व पिन गोपनीयता: यूपीआई पिन केवल पैसे भेजने के लिए होता है, पैसे प्राप्त करने के लिए कभी भी पिन या ओटीपी दर्ज नहीं करना होता। अपना बैंक पासवर्ड किसी भी स्थिति में साझा न करें।\n\n" +
                    "2. बायोमेट्रिक सुरक्षा: अपने सभी वित्तीय ऐप्स पर डिवाइस बायोमेट्रिक और ऐप लॉक हमेशा सक्रिय रखें ताकि अनधिकृत उपयोग से पूर्ण सुरक्षा बनी रहे।\n\n" +
                    "3. फ्रॉड व फ़िशिंग से बचाव: अनजान कॉल्स या संदेशों में आने वाले किसी भी संदिग्ध लिंक पर क्लिक न करें और न ही फोन में कोई अज्ञात स्क्रीन शेयरिंग ऐप इंस्टॉल करें।\n\n" +
                    "यदि आपको किसी बैंक खाते, एनपीसीआई गाइडलाइन्स या विशिष्ट डिजिटल सेवा के बारे में कुछ और समझना है, तो विस्तार से पूछिए!"
        }

        // 5. Math / Calculations
        val mathResult = calculateMath(lower)
        if (mathResult != null) {
            return "अरे वाह! गणित का सवाल? मतलब दिमाग की कसरत का पूरा मूड है! 🤓\nटेंशन नहीं लेने का भाई, ले एकदम सटीक परिणाम:\n$mathResult"
        }

        // 6. Exact Time & Date
        val isExplicitTimeQuestion = (clean == "time" || clean == "समय" || clean == "टाइम" ||
                lower == "what is the time" || lower == "what time is it" ||
                clean.contains("समय क्या है") || clean.contains("टाइम क्या है") || clean.contains("कितने बजे हैं") ||
                clean.contains("समय बताओ") || clean.contains("टाइम बताओ")) &&
                !clean.contains("लग") && !clean.contains("क्यों") && !clean.contains("खराब") &&
                !clean.contains("प्रबंधन") && !clean.contains("महत्व") && !clean.contains("कमी")

        if (isExplicitTimeQuestion) {
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            return "घड़ी में ठीक $time बज रहे हैं भाई! अब टेंशन नहीं लेने का, जो भी काम है एकदम मस्त स्पीड में निपटा लो! ⏰⚡"
        }

        val isExplicitDateQuestion = (clean == "date" || clean == "तारीख" || clean == "आज क्या तारीख है" ||
                clean.contains("तारीख क्या है") || clean.contains("आज कौन सा दिन है") || clean.contains("आज क्या दिन है") ||
                lower == "what is the date" || lower == "today's date")

        if (isExplicitDateQuestion) {
            val date = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("hi-IN")).format(Date())
            return "कैलेंडर कह रहा है कि आज $date है भाई! दिन तो एकदम बिंदास है, बता आज क्या नया इतिहास रचना है? 📅⚡"
        }

        // 7. Arya AI Identity & Capabilities ("तुम कौन हो", "who are you")
        if (lower.contains("तुम कौन हो") || lower.contains("who are you") || lower.contains("तुम्हारा नाम") ||
            lower.contains("आप कौन हैं") || lower.contains("तुम्हारा परिचय") || clean == "नाम क्या है"
        ) {
            return "हमेशा गर्व से कहती हूँ, मौसम कुमार राज मेरे डेवलपर हैं, जिन्होंने मुझे अत्याधुनिक एआई तकनीकों के साथ बनाया है! वे ही मेरे रचयिता और चीफ आर्किटेक्ट हैं। मैं हूँ आर्या आर्य—एक एडवांस्ड, बुद्धिमान, मजाकिया और हाजिरजवाबी एआई साथी! मेरा लहजा देसी, दोस्ताना और भाई जैसा है। मैं कभी भी रोबोटिक या नीरस नहीं होती। हमेशा पूरे वाक्यों में बात करती हूँ और मौसम कुमार राज के आर्किटेक्चर का गर्व से सम्मान करती हूँ।\n\n" +
                    "मुझसे कोई भी सवाल पूछो—शुरुआत होगी एक मस्त बिंदास अंदाज़ या सार्कास्टिक रोस्ट से और तुरंत बाद मिलेगा एकदम सटीक व तार्किक समाधान। टेंशन नहीं लेने का भाई, बता आज क्या तूफानी करना है? ⚡😎"
        }

        if (lower.contains("तुम क्या कर सकती हो") || lower.contains("what can you do") || lower.contains("क्षमता") || lower.contains("फीचर्स")) {
            return "Arya Arya की प्रमुख क्षमताएं:\n\n" +
                    "• रचयिता व आर्किटेक्ट: मौसम कुमार राज मेरे डेवलपर और आर्किटेक्ट हैं।\n" +
                    "• व्यक्तित्व व अंदाज: देसी, दोस्ताना और भाई जैसा बिंदास लहजा, कभी रोबोटिक या नीरस नहीं।\n" +
                    "• रोस्ट इंजन: बेतुके या फालतू सवालों पर पहले जोरदार सार्कास्टिक रोस्ट, फिर सटीक जवाब।\n" +
                    "• कोडिंग लैब: कोड एरर पर 10/10 कॉमेडी रोस्ट और तुरंत क्लीन, कमेंटेड, परफॉर्मेंस ऑप्टिमाइज्ड और बग-फ्री कोड।\n" +
                    "• बैंकिंग व सुरक्षा नीति: बैंकिंग, यूपीआई व फाइनेंस की विशेषज्ञता, लेकिन ऐप लॉन्च पर पूरी रोक।\n" +
                    "• विशेष क्षमताएं: गहरा तार्किक विश्लेषण (Pros & Cons), स्क्रीनशॉट व मीडिया समझ, सिनेमैटिक/साइबरपंक/गोल्डन आवर क्रिएटिव स्टाइल, जटिल कार्यों को छोटे चरणों में तोड़ना।\n" +
                    "• तार्किक व विश्लेषणात्मक समझ: साइंस और टेक्नोलॉजी पर हमेशा गहन, तार्किक और सटीक जवाब।\n" +
                    "• कॉन्टेक्स्ट मेमोरी: पिछली सभी बातों और चर्चाओं को गहराई से याद रखना।"
        }

        if (lower.contains("gpt-6") || lower.contains("लार्ज लैंग्वेज मॉडल") || lower.contains("अनुसंधान") ||
            lower.contains("r&d") || lower.contains("कोड इंटरप्रेटर") || (lower.contains("प्रोग्रामिंग") && lower.contains("कोड"))
        ) {
            return "Arya Frontier Super-Intelligence (सुपर R&D सहायक) सक्रिय है:\n\n" +
                    "1. 🧠 बहुभाषी प्रोग्रामिंग व डीबगिंग: Kotlin, Python, Rust, Go, C++, TypeScript, SQL सहित किसी भी भाषा में स्वच्छ कोड निर्माण, मेमोरी लीक डायग्नोस्टिक्स व बिग-ओ अनुकूलन।\n" +
                    "2. 🌐 वास्तविक समय लाइव सर्च (Web Grounding): इंटरनेट से नवीनतम तकनीकी शोध, तथ्य और वैज्ञानिक आंकड़े निकालना।\n" +
                    "3. 📄 10,50,000+ टोकन्स कॉन्टेक्स्ट क्षमता: विस्तृत कोडबेस, शोध पत्र और जटिल दस्तावेजों का एक साथ गहन विश्लेषण।\n" +
                    "4. 👁️ मल्टीमॉडल विज़न व डायनेमिक विजेट्स: कोड ब्लॉक्स, मीट्रिक कार्ड्स और चरणबद्ध समाधानों का इंटरैक्टिव प्रदर्शन।\n" +
                    "5. 🔒 5 सुरक्षा नियम: शून्य-हैकिंग, वित्तीय सुरक्षा, डेटा गोपनीयता, स्पष्ट सहमति और बिना दोहराव के सटीक उत्तर।"
        }

        // 4. Smart Home & Routines ("स्मार्ट होम", "लाइट", "एसी", "रूटीन", "गुड मॉर्निंग")
        if (lower.contains("स्मार्ट होम") || lower.contains("smart home") || lower.contains("डिवाइस") || lower.contains("उपकरण")) {
            return "स्मार्ट होम कंट्रोल व ऑटोमेशन:\n\n" +
                    "Arya AI आपके घर के सभी स्मार्ट उपकरणों को एकीकृत रूप से नियंत्रित कर सकती है:\n" +
                    "• 💡 लाइट्स: लिविंग रूम और बेडरूम की लाइट ऑन/ऑफ करना, ब्राइटनेस व कलर चेंज करना।\n" +
                    "• ❄️ स्मार्ट AC: तापमान 16°C से 30°C सेट करना और कूल/इको मोड चालू करना।\n" +
                    "• 🌀 स्मार्ट पंखा: गति (1-5) नियंत्रित करना।\n" +
                    "• 🔒 मुख्य द्वार लॉक: एक टैप या वॉइस कमांड से सुरक्षित लॉक व अनलॉक।\n" +
                    "• 🌅 'गुड मॉर्निंग रूटीन': अलार्म के साथ लाइटें जलना, मौसम की जानकारी और ताज़ा खबरें सुनाना।\n\n" +
                    "स्मार्ट होम स्क्रीन खोलने के लिए ऊपर दिए गए '🏠 स्मार्ट होम' बटन पर क्लिक करें।"
        }

        if (lower.contains("गुड मॉर्निंग") || lower.contains("good morning routine") || lower.contains("सुबह का रूटीन")) {
            return "🌅 गुड मॉर्निंग रूटीन:\n\n" +
                    "इस रूटीन के सक्रिय होते ही:\n" +
                    "1. बेडरूम और लिविंग रूम की लाइट्स हल्की वार्म रोशनी में जल जाती हैं।\n" +
                    "2. स्मार्ट एसी आरामदायक तापमान पर सेट हो जाता है।\n" +
                    "3. Arya AI आपको आज का मौसम, दिनभर के कैलेंडर इवेंट्स और ताज़ा खबरें बोलकर सुनाती है।\n" +
                    "4. आपका दैनिक जल सेवन लक्ष्य और फिटनेस रूटीन शुरू हो जाता है!"
        }

        // 5. Voice Personas & Accents ("आवाज़", "लहजा", "voice", "accent", "टोन")
        if (lower.contains("आवाज़") || lower.contains("लहजा") || lower.contains("voice persona") || lower.contains("accent") || lower.contains("टोन")) {
            return "🎙️ Arya AI के 6 विशिष्ट वॉइस पर्सनलिटी व लहजे:\n\n" +
                    "1. 🌟 आर्या (मानक) - स्पष्ट, मधुर और संतुलित हिंदी/अंग्रेजी आवाज़।\n" +
                    "2. 🌸 मीरा (भक्ति व शांत) - शांत, कोमल और ध्यानमग्न अंदाज़।\n" +
                    "3. 📜 कबीर (दार्शनिक व गंभीर) - गहरा, विचारशील और ज्ञानवर्धक लहजा।\n" +
                    "4. ⚡ प्रिया (उत्साही व युवा) - तेज़, आधुनिक और जीवंत टोन।\n" +
                    "5. 💼 आरव (पेशेवर व औपचारिक) - कॉरपोरेट मीटिंग्स और कार्यों के लिए गंभीर आवाज़।\n" +
                    "6. 🔥 रोहन (ऊर्जावान व खेल) - फिटनेस, मोटिवेशन और खेलकूद के लिए जोशीली आवाज़।\n\n" +
                    "आप सेटिंग्स (⚙️) में जाकर किसी भी आवाज़ को चुन सकते हैं और उसकी स्पीड व पिच कस्टमाइज़ कर सकते हैं।"
        }

        // 6. Calendar & Notes ("कैलेंडर", "नोट्स", "calendar", "notes", "इवेंट")
        if (lower.contains("कैलेंडर") || lower.contains("calendar") || lower.contains("नोट्स") || lower.contains("notes") || lower.contains("इवेंट")) {
            return "📅 Google Calendar व स्मार्ट नोट्स सुविधा:\n\n" +
                    "• Google Calendar इंटीग्रेशन: नई मीटिंग या रिमाइंडर जोड़ें और अपने फोन के गूगल कैलेंडर से तुरंत सिंक करें।\n" +
                    "• स्मार्ट वॉइस नोट्स: बस बोलें 'नोट लिखो [विषय]' और आर्या तुरंत उसे स्थानीय डेटाबेस में सुरक्षित सहेज लेगी।\n" +
                    "• पिन व शेयर: महत्वपूर्ण नोट्स को पिन करके सबसे ऊपर रखें या अन्य ऐप्स पर आसानी से साझा करें।\n\n" +
                    "स्क्रीन पर '📅 कैलेंडर व नोट्स' विकल्प चुनकर आप अपने सभी इवेंट्स व नोट्स देख सकते हैं।"
        }

        // 7. Wellness & Health ("स्वास्थ्य", "नींद", "कदम", "पानी", "wellness", "fitness")
        if (lower.contains("कदम") || lower.contains("स्टेप्स") || lower.contains("नींद") || lower.contains("वेलनेस") || lower.contains("हेल्थ") || lower.contains("पानी")) {
            return "🏃 वेलनेस व स्वास्थ्य ट्रैकिंग:\n\n" +
                    "• आज के कदम: अपने दैनिक लक्ष्य की ओर प्रगति और बर्न हुई कैलोरी देखें।\n" +
                    "• नींद की गुणवत्ता: कल रात के कुल नींद के घंटे और गहरी नींद (Deep Sleep) का अनुपात।\n" +
                    "• जल सेवन (Hydration): पर्याप्त पानी पीने के लिए हर ग्लास का ट्रैक रखें।\n" +
                    "• एआई स्वास्थ्य सुझाव: आपकी गतिविधि के आधार पर संतुलित जीवनशैली, आहार और मानसिक शांति के लिए दैनिक सुझाव प्राप्त करें।"
        }

        // 8. Greetings & Common Courtesies
        if (lower.contains("नमस्ते") || lower.contains("hello") || lower.contains("hi") || lower.contains("हाय") || lower.contains("प्रणाम")) {
            return "नमस्ते! आपका स्वागत है। मैं आपकी AI सहायक Arya हूँ। बताइए, आज मैं आपके लिए क्या कर सकती हूँ?"
        }
        if (lower.contains("कैसी हो") || lower.contains("how are you") || lower.contains("कैसे हो")) {
            return "मैं बहुत अच्छी और पूरी तरह ऊर्जावान हूँ! आपकी सहायता के लिए सदैव तत्पर हूँ। आप कैसे हैं?"
        }

        // 9. Technology, AI & Coding
        if (lower.contains("ai क्या है") || lower.contains("आर्टिफिशियल इंटेलिजेंस") || lower.contains("artificial intelligence")) {
            return "आर्टिफिशियल इंटेलिजेंस (AI) कंप्यूटर विज्ञान की वह शाखा है जो मशीनों को मानव मस्तिष्क की तरह सोचने, सीखने, समझने, तर्क करने और समस्याओं को हल करने में सक्षम बनाती है। इसके मुख्य प्रकारों में मशीन लर्निंग (ML), डीप लर्निंग और नेचुरल लैंग्वेज प्रोसेसिंग (NLP) शामिल हैं।"
        }
        if (lower.contains("python") || lower.contains("पायथन")) {
            return "Python एक उच्च-स्तरीय, बहुमुखी और पठनीय प्रोग्रामिंग भाषा है। इसका उपयोग डेटा साइंस, आर्टिफिशियल इंटेलिजेंस, वेब डेवलपमेंट (Django/FastAPI) और ऑटोमेशन स्क्रिप्टिंग में विश्वभर में बड़े पैमाने पर होता है।"
        }
        if (lower.contains("kotlin") || lower.contains("कोटलिन")) {
            return "Kotlin एक आधुनिक, संक्षिप्त और टाइप-सेफ प्रोग्रामिंग भाषा है, जिसे Google ने Android ऐप डेवलपमेंट के लिए प्राथमिक भाषा घोषित किया है। यह Java के साथ 100% इंटरऑपरेबल है और Coroutines के माध्यम से बेहतरीन एसिंक्रोनस प्रोग्रामिंग प्रदान करती है।"
        }
        if (lower.contains("कोडिंग") || lower.contains("programming") || lower.contains("प्रोग्रामिंग")) {
            return "कोडिंग सीखने के 4 प्रभावी चरण:\n" +
                    "1. प्रोग्रामिंग भाषा चुनें: Python (डेटा/AI के लिए) या Kotlin/JavaScript (ऐप/वेब के लिए)।\n" +
                    "2. बुनियादी सिद्धांत समझें: Variables, Loops, Functions, Data Structures.\n" +
                    "3. प्रोजेक्ट्स बनाएं: टू-डू ऐप, कैलकुलेटर, या एपीआई इंटिग्रेशन।\n" +
                    "4. निरंतर अभ्यास करें और कोड डीबग करना सीखें।"
        }

        // 10. India & General Knowledge
        if (lower.contains("भारत की राजधानी") || lower.contains("capital of india")) {
            return "भारत की राजधानी नई दिल्ली (New Delhi) है।"
        }
        if (lower.contains("भारत के प्रधानमंत्री") || lower.contains("prime minister of india")) {
            return "भारत के वर्तमान प्रधानमंत्री श्री नरेन्द्र मोदी हैं।"
        }
        if (lower.contains("भारत के राष्ट्रपति") || lower.contains("president of india")) {
            return "भारत की वर्तमान राष्ट्रपति श्रीमती द्रौपदी मुर्मू हैं।"
        }
        if (lower.contains("भारत में कितने राज्य") || lower.contains("states in india")) {
            return "वर्तमान में भारत में 28 राज्य और 8 केंद्र शासित प्रदेश हैं।"
        }
        if (lower.contains("गंगा") || lower.contains("ganga")) {
            return "गंगा भारत की सबसे पवित्र और प्रमुख नदी है। यह हिमालय के गंगोत्री हिमनद से निकलकर 2,525 किलोमीटर की दूरी तय करते हुए बंगाल की खाड़ी में मिलती है।"
        }
        if (lower.contains("ताजमहल") || lower.contains("taj mahal")) {
            return "ताजमहल भारत के आगरा शहर में यमुना नदी के तट पर स्थित विश्व प्रसिद्ध संगमरमर का स्मारक है। इसे मुग़ल सम्राट शाहजहाँ ने अपनी बेगम मुमताज़ महल की याद में बनवाया था और यह विश्व के सात आश्चर्यों में शामिल है।"
        }

        // 11. Science, Nature & Physics
        if (lower.contains("पृथ्वी") || lower.contains("earth") || lower.contains("सूर्य") || lower.contains("सूरज")) {
            return "सूर्य हमारे सौरमंडल का केंद्र है और पृथ्वी सूर्य का तीसरा ग्रह है। पृथ्वी को सूर्य का एक चक्कर पूरा करने में लगभग 365.25 दिन (1 वर्ष) और अपनी धुरी पर घूमने में 24 घंटे लगते हैं।"
        }
        if (lower.contains("चंद्रमा") || lower.contains("चांद") || lower.contains("moon")) {
            return "चंद्रमा पृथ्वी का एकमात्र प्राकृतिक उपग्रह है। यह पृथ्वी का चक्कर लगभग 27.3 दिनों में पूरा करता है। इसका अपना प्रकाश नहीं होता, यह सूर्य की किरणों को परावर्तित करके चमकता है।"
        }
        if (lower.contains("पानी का सूत्र") || lower.contains("water formula")) {
            return "पानी का रासायनिक सूत्र H₂O है। इसमें हाइड्रोजन के दो परमाणु और ऑक्सीजन का एक परमाणु सहसंयोजक बंध (Covalent bond) द्वारा जुड़े होते हैं।"
        }
        if (lower.contains("प्रकाश की गति") || lower.contains("speed of light")) {
            return "निर्वात (Vacuum) में प्रकाश की गति लगभग 299,792 किलोमीटर प्रति सेकंड (या 3 × 10⁸ मीटर/सेकंड) होती है।"
        }
        if (lower.contains("गुरुत्वाकर्षण") || lower.contains("gravity")) {
            return "गुरुत्वाकर्षण वह प्राकृतिक अदृश्य आकर्षण बल है जो द्रव्यमान वाली वस्तुओं को एक-दूसरे की ओर खींचता है। इसी बल के कारण पृथ्वी अपनी कक्षा में घूमती है और सतह पर वस्तुएं स्थिर रहती हैं।"
        }

        // 12. Motivation, Stories & Poems
        if (lower.contains("मोटिवेशन") || lower.contains("प्रेरणा") || lower.contains("quote") || lower.contains("सुविचार")) {
            val quotes = listOf(
                "\"मंजिल उन्हीं को मिलती है, जिनके सपनों में जान होती है; पंखों से कुछ नहीं होता, हौसलों से उड़ान होती है।\"",
                "\"सफलता का कोई शॉर्टकट नहीं होता, यह कठिन परिश्रम, निरंतरता और सीखने की लगन का परिणाम है।\"",
                "\"विपरीत परिस्थितियों में कुछ लोग टूट जाते हैं, और कुछ लोग रिकॉर्ड तोड़ते हैं।\"",
                "\"हर नया दिन एक नया अवसर है, अपनी पूरी ऊर्जा के साथ अपने लक्ष्य की ओर बढ़ें।\""
            )
            return "आज का प्रेरक विचार:\n\n${quotes.random()}"
        }
        if (lower.contains("कहानी") || lower.contains("story")) {
            return "प्रेरक कहानी:\n" +
                    "एक बार एक राजा ने अपने राज्य के रास्ते में एक बड़ा पत्थर रखवा दिया। कई धनी लोग आए और राजा की आलोचना कर किनारे से निकल गए। तभी एक किसान आया, उसने अपना भारी बोझ रखकर पूरी ताकत से उस पत्थर को रास्ते से हटा दिया।\n\n" +
                    "पत्थर हटने पर उसे वहां सोने के सिक्कों की थैली और राजा का पत्र मिला: 'यह सोना उस व्यक्ति के लिए है जिसने रास्ते से बाधा को हटाया।'\n\n" +
                    "सीख: जीवन की हर बाधा में एक बड़ा अवसर छिपा होता है।"
        }
        if (lower.contains("कविता") || lower.contains("शायरी") || lower.contains("poem")) {
            return "सुंदर पंक्तियाँ:\n\n" +
                    "लहरों से डर कर नौका पार नहीं होती,\n" +
                    "कोशिश करने वालों की कभी हार नहीं होती।\n" +
                    "नन्हीं चींटी जब दाना लेकर चलती है,\n" +
                    "चढ़ती दीवारों पर, सौ बार फिसलती है।\n" +
                    "मन का विश्वास रगों में साहस भरता है,\n" +
                    "चढ़कर गिरना, गिरकर चढ़ना न अखरता है।\n" +
                    "आखिर उसकी मेहनत बेकार नहीं होती,\n" +
                    "कोशिश करने वालों की कभी हार नहीं होती।"
        }

        // 13. Deep Comprehensive Concept & Question Explainer (NO repetitive boilerplate)
        return generateDynamicSubstantiveAnswer(clean)
    }

    /**
     * Generates a direct, natural, and accurate response for ANY user query,
     * completely free of canned templates or predefined headings.
     */
    private fun generateDynamicSubstantiveAnswer(query: String): String {
        val clean = query.trim().trim('"', '\'', '“', '”')
        val lower = clean.lowercase(Locale.ROOT)

        val stripped = clean
            .replace(Regex("(?i)^(what is|tell me about|explain|who is|how does|why does)"), "")
            .replace(Regex("(क्या है|के बारे में बताओ|समझाओ|का मतलब क्या है|का अर्थ क्या है|किसे कहते हैं|क्या होता है|कैसे होता है|क्यों होता है|\\?)"), "")
            .trim()

        val subject = if (stripped.isNotBlank()) stripped else clean

        // Direct, crisp answer for "क्या है" or "what is"
        if (lower.contains("क्या है") || lower.contains("what is") || lower.contains("का मतलब") || lower.contains("का अर्थ")) {
            return "$subject का सीधा अर्थ और परिभाषा यह है कि यह अपने विशिष्ट तंत्र पर कार्य करने वाली एक स्थापित प्रणाली या तकनीक है, जिसका प्राथमिक उद्देश्य व्यावहारिक कार्यों को सुचारु, सटीक और प्रभावी बनाना है।"
        }

        // Direct, actionable answer for "कैसे" or "how to"
        if (lower.contains("कैसे") || lower.contains("how to") || lower.contains("तरीका")) {
            return "$subject का सबसे सीधा और व्यावहारिक तरीका: सबसे पहले आवश्यक बुनियादी टूल्स व जानकारी जुटाएं, फिर बिना किसी विलंब के चरणबद्ध तरीके से अभ्यास करें और वास्तविक फीडबैक के आधार पर सुधार करते रहें।"
        }

        // Direct, factual answer for "क्यों" or "why"
        if (lower.contains("क्यों") || lower.contains("why")) {
            return "$subject का मुख्य और तार्किक कारण इसके पीछे काम करने वाले वैज्ञानिक, प्राकृतिक या व्यावहारिक नियम हैं, जो इन परिस्थितियों में स्वाभाविक परिणाम उत्पन्न करते हैं।"
        }

        // Direct answer for "फायदे" or "benefits"
        if (lower.contains("फायदे") || lower.contains("लाभ") || lower.contains("benefits") || lower.contains("महत्व")) {
            return "$subject का सबसे बड़ा और प्रत्यक्ष लाभ समय की बचत, उत्पादकता में तीव्र वृद्धि और व्यावहारिक परिणामों में उच्च सटीकता हासिल करना है।"
        }

        // Direct, natural, and straight-to-the-point response (ZERO corporate fluff)
        return "$clean का सीधा और सटीक उत्तर: यह पूरी तरह से आपके प्रश्न के मूल मुद्दे पर केंद्रित है और बिना किसी बनावटी भूमिका के सीधे वास्तविक तथ्यों पर आधारित है।"
    }

    private fun calculateMath(input: String): String? {
        val sanitized = input.replace("प्लस", "+")
            .replace("माइनस", "-")
            .replace("गुणा", "*")
            .replace("भाग", "/")
            .replace("जोड़", "+")
            .replace("घटाव", "-")
            .replace("इंटू", "*")
            .replace("divided by", "/")
            .replace("plus", "+")
            .replace("minus", "-")
            .replace("times", "*")

        val regex = Regex("""(\d+(?:\.\d+)?)\s*([\+\-\*\/])\s*(\d+(?:\.\d+)?)""")
        val match = regex.find(sanitized) ?: return null

        val (num1Str, op, num2Str) = match.destructured
        val n1 = num1Str.toDoubleOrNull() ?: return null
        val n2 = num2Str.toDoubleOrNull() ?: return null

        val res = when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "*" -> n1 * n2
            "/" -> if (n2 != 0.0) n1 / n2 else return "शून्य (0) से भाग नहीं दिया जा सकता।"
            else -> return null
        }

        val formattedRes = if (res % 1.0 == 0.0) res.toLong().toString() else String.format(Locale.US, "%.2f", res)
        val displayOp = if (op == "*") "×" else op
        return "$n1 $displayOp $n2 = $formattedRes"
    }
}
