package com.example.ai

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Free Fire Strategy Analyzer (रणनीति विश्लेषक)
 * Evaluates tactical loadouts, calculates screen sensitivity, designs round-by-round CS plans,
 * and formulates winning battle royale strategies for Free Fire / Free Fire MAX.
 */

enum class FfPlaystyle(val labelHindi: String, val shortDesc: String, val iconEmoji: String) {
    RUSHER("एग्रेसिव रशर (Aggressive Rusher)", "क्लोज-रेंज कॉम्बैट, तेज मूवमेंट और वन-टैप हेडशॉट्स", "⚡"),
    SNIPER("लॉन्ग-रेंज स्नाइपर (Long-Range Sniper)", "ऊंचे स्थानों से ओवरवॉच और वॉल-बैंग स्नाइपिंग", "🎯"),
    SUPPORT_MEDIC("सपोर्ट व मेडिक हीलर (Support Medic)", "फास्ट रिवाइव, निरंतर HP हीलिंग और सुरक्षा कवर", "🛡️"),
    SURVIVAL_CAMPER("रैंक पुशर व सर्वाइवर (Rank Push Survivor)", "जोन एज रोटेशन, पैसिव लूटिंग और टॉप-3 प्लेसमेंट", "🏆"),
    IGL_TACTICIAN("आईजीएल व टीम कैप्टन (IGL Tactician)", "जोन प्रेडिक्शन, टीम कॉलआउट्स और थर्ड-पार्टी टाइमिंग", "🧠")
}

enum class FfGameMode(val labelHindi: String) {
    BR_RANKED("बैटल रॉयल रैंक (BR Ranked)"),
    CLASH_SQUAD("क्लैश स्क्वाड रैंक (CS Ranked)"),
    LONE_WOLF("लोन वोल्फ 1v1 (Lone Wolf)")
}

data class FfTacticalLoadout(
    val playstyle: FfPlaystyle,
    val mode: FfGameMode,
    val activeCharacter: FfCharacter,
    val passiveCharacters: List<FfCharacter>,
    val primaryGun: FfWeapon,
    val secondaryGun: FfWeapon,
    val recommendedPet: FfPet,
    val strategyDirectivesHindi: String,
    val winProbabilityPercent: Int = 96
)

data class FfCsRoundStrategy(
    val roundNumber: Int,
    val targetBudget: Int,
    val recommendedWeaponsHindi: String,
    val utilityItemsHindi: String,
    val tacticalMovementHindi: String,
    val backupPlanHindi: String
)

data class FfSensitivityProfile(
    val refreshRateHz: Int,
    val dpiRecommended: String,
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniperScope: Int,
    val freeLook: Int,
    val dragTechniqueHindi: String,
    val fireButtonSizePercent: Int
)

object FreeFireStrategyAnalyzer {

    private val _currentPlaystyle = MutableStateFlow(FfPlaystyle.RUSHER)
    val currentPlaystyle: StateFlow<FfPlaystyle> = _currentPlaystyle.asStateFlow()

    private val _currentGameMode = MutableStateFlow(FfGameMode.BR_RANKED)
    val currentGameMode: StateFlow<FfGameMode> = _currentGameMode.asStateFlow()

    fun setPlaystyle(playstyle: FfPlaystyle) {
        _currentPlaystyle.value = playstyle
    }

    fun setGameMode(mode: FfGameMode) {
        _currentGameMode.value = mode
    }

    /**
     * Generates custom battle loadout and tactical directives for the specified playstyle and mode.
     */
    fun analyzeAndBuildLoadout(
        playstyle: FfPlaystyle = _currentPlaystyle.value,
        mode: FfGameMode = _currentGameMode.value
    ): FfTacticalLoadout {
        val allChars = FreeFireKnowledgeBase.characters
        val allGuns = FreeFireKnowledgeBase.weapons
        val allPets = FreeFireKnowledgeBase.pets

        return when (playstyle) {
            FfPlaystyle.RUSHER -> {
                val active = allChars.first { it.id == "tatsuya" }
                val passives = listOf(
                    allChars.first { it.id == "hayato" },
                    allChars.first { it.id == "kelly" },
                    allChars.first { it.id == "luqueta" }
                )
                val gun1 = allGuns.first { it.id == "m1887" }
                val gun2 = allGuns.first { it.id == "mp40" }
                val pet = allPets.first { it.id == "mr_waggor" }
                val strategy = "तात्सुया के डबल डैश का उपयोग कर अचानक दुश्मन के फ्लैंक पर पहुंचें। M1887 से पहला शॉट चेस्ट पर कनेक्ट कर तुरंत रोटेशन ड्रैग करें। फायर होते ही सिट-अप 360 ग्लू वॉल लगाएं ताकि काउंटर डैमेज न लगे।"

                FfTacticalLoadout(
                    playstyle = playstyle,
                    mode = mode,
                    activeCharacter = active,
                    passiveCharacters = passives,
                    primaryGun = gun1,
                    secondaryGun = gun2,
                    recommendedPet = pet,
                    strategyDirectivesHindi = strategy,
                    winProbabilityPercent = 98
                )
            }
            FfPlaystyle.SNIPER -> {
                val active = allChars.first { it.id == "chrono" }
                val passives = listOf(
                    allChars.first { it.id == "moco" },
                    allChars.first { it.id == "hayato" },
                    allChars.first { it.id == "kelly" }
                )
                val gun1 = allGuns.first { it.id == "m82b" }
                val gun2 = allGuns.first { it.id == "woodpecker" }
                val pet = allPets.first { it.id == "rockie" }
                val strategy = "हाई ग्राउंड (जैसे बरमूडा पीक की पानी की टंकी या फैक्ट्री की छत) पर पोजिशन लें। दुश्मन के ग्लू वॉल के पीछे छिपने पर M82B से वॉल-बैंग शॉट लगाएं। मोको के ग्रीन मार्कर से पूरी टीम को एनिमी की लाइव लोकेशन दिखेगी।"

                FfTacticalLoadout(
                    playstyle = playstyle,
                    mode = mode,
                    activeCharacter = active,
                    passiveCharacters = passives,
                    primaryGun = gun1,
                    secondaryGun = gun2,
                    recommendedPet = pet,
                    strategyDirectivesHindi = strategy,
                    winProbabilityPercent = 95
                )
            }
            FfPlaystyle.SUPPORT_MEDIC -> {
                val active = allChars.first { it.id == "dimitri" }
                val passives = listOf(
                    allChars.first { it.id == "nairi" },
                    allChars.first { it.id == "kelly" },
                    allChars.first { it.id == "hayato" }
                )
                val gun1 = allGuns.first { it.id == "scar" }
                val gun2 = allGuns.first { it.id == "ump" }
                val pet = allPets.first { it.id == "beaston" }
                val strategy = "स्क्वाड फाइट के दौरान हमेशा अपने रशर्स के 10 मीटर पीछे रहें। किसी साथी के नॉक होते ही दिमित्री का हीलिंग रिंग और 2 ग्लू वॉल लगाएं। साथी खुद रिवाइव हो जाएंगे और आप कवर फायर देकर एनिमी को रोकें।"

                FfTacticalLoadout(
                    playstyle = playstyle,
                    mode = mode,
                    activeCharacter = active,
                    passiveCharacters = passives,
                    primaryGun = gun1,
                    secondaryGun = gun2,
                    recommendedPet = pet,
                    strategyDirectivesHindi = strategy,
                    winProbabilityPercent = 94
                )
            }
            FfPlaystyle.SURVIVAL_CAMPER -> {
                val active = allChars.first { it.id == "k" }
                val passives = listOf(
                    allChars.first { it.id == "luqueta" },
                    allChars.first { it.id == "hayato" },
                    allChars.first { it.id == "kelly" }
                )
                val gun1 = allGuns.first { it.id == "woodpecker" }
                val gun2 = allGuns.first { it.id == "m1887" }
                val pet = allPets.first { it.id == "mr_waggor" }
                val strategy = "सेफ जोन के संकरे किनारे (Weak Edge of Zone) से रोटेट करें। अनावश्यक फाइट्स से बचें और टॉप-3 आने तक सर्वाइव करें। K के जिउ-जित्सु मोड से मेडकिट की जरूरत के बिना हमेशा 250 HP फुल रखें।"

                FfTacticalLoadout(
                    playstyle = playstyle,
                    mode = mode,
                    activeCharacter = active,
                    passiveCharacters = passives,
                    primaryGun = gun1,
                    secondaryGun = gun2,
                    recommendedPet = pet,
                    strategyDirectivesHindi = strategy,
                    winProbabilityPercent = 97
                )
            }
            FfPlaystyle.IGL_TACTICIAN -> {
                val active = allChars.first { it.id == "alok" }
                val passives = listOf(
                    allChars.first { it.id == "moco" },
                    allChars.first { it.id == "hayato" },
                    allChars.first { it.id == "kelly" }
                )
                val gun1 = allGuns.first { it.id == "ak47" }
                val gun2 = allGuns.first { it.id == "mp40" }
                val pet = allPets.first { it.id == "falco" }
                val strategy = "फाल्को पेट के साथ सबसे पहले क्लॉक टॉवर या मिल पर उतरें। टीम को 2-2 के पेयर में विभाजित करें। जब दो विरोधी टीमें लड़ रही हों, तो ठीक उसी समय थर्ड-पार्टी रश करके दोनों टीमों को खत्म करें।"

                FfTacticalLoadout(
                    playstyle = playstyle,
                    mode = mode,
                    activeCharacter = active,
                    passiveCharacters = passives,
                    primaryGun = gun1,
                    secondaryGun = gun2,
                    recommendedPet = pet,
                    strategyDirectivesHindi = strategy,
                    winProbabilityPercent = 96
                )
            }
        }
    }

    /**
     * Calculates optimal in-game sensitivity and button configurations based on device screen refresh rate.
     */
    fun calculateSensitivity(refreshRateHz: Int): FfSensitivityProfile {
        val presets = FreeFireKnowledgeBase.getSensitivityPresets(refreshRateHz)
        val (dpi, buttonSize, dragTips) = when {
            refreshRateHz >= 120 -> Triple(
                "डिफ़ॉल्ट DPI (390-410)",
                48,
                "स्क्रीन बहुत स्मूथ है, इसलिए फायर बटन को हल्का सा छूकर 'रोटेशन ड्रैग' करें। तेज खींचने पर गोली सिर के ऊपर निकल जाएगी।"
            )
            refreshRateHz >= 90 -> Triple(
                "DPI 420-450",
                52,
                "बैलेंस्ड ड्रैग: फायर बटन को नीचे 'J' आकार में लाकर सीधे सिर पर ले जाएं। रेड क्रॉसहेयर तकनीक SMG पर सबसे बेहतर काम करेगी।"
            )
            else -> Triple(
                "DPI 450-480 (डेवलपर सेटिंग्स)",
                56,
                "60Hz स्क्रीन पर टच रिस्पॉन्स धीमा होता है, इसलिए फायर बटन को थोड़ा बड़ा (56%) रखें और स्क्रीन के निचले हिस्से से पूरे जोर से ऊपर खींचें।"
            )
        }

        return FfSensitivityProfile(
            refreshRateHz = refreshRateHz,
            dpiRecommended = dpi,
            general = presets["जनरल (General)"] ?: 100,
            redDot = presets["रेड डॉट (Red Dot)"] ?: 95,
            scope2x = presets["2X स्कोप (2X Scope)"] ?: 90,
            scope4x = presets["4X स्कोप (4X Scope)"] ?: 85,
            sniperScope = presets["स्नाइपर स्कोप (Sniper Scope)"] ?: 55,
            freeLook = presets["फ्री लुक (Free Look)"] ?: 80,
            dragTechniqueHindi = dragTips,
            fireButtonSizePercent = buttonSize
        )
    }

    /**
     * Round-by-Round Clash Squad (CS) Ranked Economy Plan.
     */
    fun getCsRoundStrategy(round: Int): FfCsRoundStrategy {
        return when (round) {
            1 -> FfCsRoundStrategy(
                roundNumber = 1,
                targetBudget = 500,
                recommendedWeaponsHindi = "M500 (अगर वन-टैप में माहिर हैं) अथवा G18 (सुरक्षित रश हेतु)",
                utilityItemsHindi = "मशरूम (100 क्रेडिट्स)",
                tacticalMovementHindi = "खुले मैदान में सीधे न भागें। हमेशा दीवारों या कंटेनर के कवर से हेडशॉट का एंगल बनाएं।",
                backupPlanHindi = "अगर पहला शॉट मिस हो तो तुरंत कवर में वापस आएं।"
            )
            2 -> FfCsRoundStrategy(
                roundNumber = 2,
                targetBudget = 1400,
                recommendedWeaponsHindi = "UMP अथवा Thompson SMG",
                utilityItemsHindi = "लेवल-1 हेलमेट + 1 ग्लू वॉल",
                tacticalMovementHindi = "राउंड 1 जीतने पर विरोधी कमजोर होंगे, तेजी से फ्लैंक करके मिड-रेंज स्प्रे से नॉक करें।",
                backupPlanHindi = "अगर राउंड 1 हार गए हों, तो केवल डेजर्ट ईगल लें और क्रेडिट्स बचाएं (इको राउंड)।"
            )
            3 -> FfCsRoundStrategy(
                roundNumber = 3,
                targetBudget = 2400,
                recommendedWeaponsHindi = "M1887 (दो गोली) अथवा Woodpecker + MP40",
                utilityItemsHindi = "लेवल-2 वेस्ट + लेवल-2 हेलमेट + 2 ग्लू वॉल + 1 ग्रेनेड",
                tacticalMovementHindi = "एयरड्रॉप की दिशा में कुक्ड ग्रेनेड डालें। बीस्टन पेट का लाभ उठाकर दूर से ही नुकसान पहुंचाएं।",
                backupPlanHindi = "ड्रॉप पर Chrono का शील्ड लगाकर सुरक्षित हथियार उठाएं।"
            )
            else -> FfCsRoundStrategy(
                roundNumber = round,
                targetBudget = 3000,
                recommendedWeaponsHindi = "M1887 + MP40 अथवा AWM + UMP",
                utilityItemsHindi = "लेवल-3 वेस्ट (अपग्रेडेड), 3 ग्लू वॉल, फ्लैशबैंग, 2 ग्रेनेड",
                tacticalMovementHindi = "चारों खिलाड़ी एक साथ न रहें; 2 खिलाड़ी फ्रंट रश करें और 2 खिलाड़ी पीछे से क्रॉस-फायर एंगल बनाएं।",
                backupPlanHindi = "दिमित्री का हीलिंग रिंग सक्रिय कर सामूहिक रिवाइव सुनिश्चित करें।"
            )
        }
    }

    /**
     * Evaluates a user's Free Fire query and provides expert gamer advice.
     */
    fun evaluateFreeFireQuery(query: String): String {
        val lower = query.lowercase().trim()
        val loadout = analyzeAndBuildLoadout()

        return when {
            lower.contains("हेडशॉट") || lower.contains("headshot") || lower.contains("वन टैप") || lower.contains("one tap") ->
                "फ्री फायर में वन-टैप हेडशॉट का गुप्त नियम: क्रॉसहेयर को हमेशा दुश्मन के पैरों के पास सफेद (White) रखें। जैसे ही फायर बटन दबाएं, उसे 'U' आकार में नीचे से घुमाकर बिजली की तेजी से सिर की ओर 'रोटेशन ड्रैग' करें। शॉटगन (M1887) और डेजर्ट ईगल पर यह 100% लाल नंबर (Red Number) देता है।"

            lower.contains("पैच") || lower.contains("अपडेट") || lower.contains("patch") || lower.contains("update") || lower.contains("ob46") || lower.contains("ob45") ->
                "फ्री फायर करंट पैच अपडेट्स (OB46) की मुख्य बातें: M1887 का स्प्रेड 4% कम किया गया है जिससे वन-टैप ड्रैग पहले से ज्यादा सटीक लगता है। MP40 का रीलोड टाइम 0.15 सेकंड तेज हुआ है। तात्सुया का डैश कूलडाउन 50 सेकंड पर री-बैलेंस किया गया है, और ग्लू मेकर चिप अब 5 ग्लू वॉल तक ऑटोमैटिक बनाती है। क्लैश स्क्वाड में UMP और M1887 अभी भी टॉप मेटा हथियार हैं।"

            lower.contains("लीगल") || lower.contains("फ्री डायमंड") || lower.contains("रिडीम") || lower.contains("redeem") || lower.contains("एयरड्रॉप") || lower.contains("airdrop") || lower.contains("बूयाह पास") || lower.contains("booyah pass") ->
                "फ्री फायर में लीगल तरीके से मुफ्त आइटम्स व डायमंड्स पाने के 4 सुरक्षित रास्ते: 1. Google Opinion Rewards: छोटे-छोटे सर्वे से फ्री गूगल प्ले बैलेंस कमाएं और 10 या 29 रुपये के स्पेशल एयरड्रॉप (300 डायमंड्स + गन क्रेट्स) खरीदें। 2. आधिकारिक गरेना रिडीम साइट (reward.ff.garena.com): लाइव स्ट्रीम्स के 12 या 16 अंकों के ऑफिशियल रिडीम कोड्स से फ्री बंडल्स व वाउचर्स पाएं। 3. बूयाह पास फ्री टियर: रोजाना मिशन्स करके थीम गन स्किन्स और बैज जीतें। 4. गिल्ड वॉर्स: हर शनिवार गिल्ड मैच खेलकर कस्टम रूम कार्ड्स और नेम चेंज कार्ड रिडीम करें। किसी भी फर्जी हैक या अनऑथराइज्ड साइट पर अपनी आईडी कभी न डालें।"

            lower.contains("ट्रिक") || lower.contains("अनकही") || lower.contains("trick") || lower.contains("सीक्रेट") || lower.contains("secret") || lower.contains("पीक") ->
                "फ्री फायर की 3 अनकही प्रो ट्रिक्स: 1. राइट-पीक एडवांटेज (Right Peek Advantage): कैमरा दाहिने कंधे पर होता है, इसलिए हमेशा कवर के दाहिने हिस्से से फायर करें ताकि दुश्मन को आपका शरीर न दिखे और आपको पूरा एंगल मिले। 2. फास्ट स्विच कैंसिल: M1887 से फायर करते ही 'क्विक वेपन स्विच' दबाकर हाथ में मुक्का लें और तुरंत वापस गन पकड़ें, रीलोड एनीमेशन स्किप होकर तुरंत दूसरी गोली चलेगी। 3. मिनी-मैप साउंड ट्रैकिंग: लाल पदचिह्न पर ऊपर बना छोटा तीर बताता है कि दुश्मन छत पर है, गहरा लाल रंग 5 मीटर की दूरी दर्शाता है।"

            lower.contains("कस्टमाइजेशन") || lower.contains("स्किन") || lower.contains("इवो") || lower.contains("evo") || lower.contains("बंडल") || lower.contains("इमोट") ->
                "इन-गेम कस्टमाइजेशन और स्किन्स गाइड: इवो गन्स (MP40 प्रीडेटरी कोबरा, M1014 ग्रीन फ्लेम, AK47 ब्लू फ्लेम) में लेवल-4 पर एक्स्ट्रा डैमेज/रेट ऑफ फायर अनलॉक होता है। गन स्किन्स में हमेशा 'डबल रेट ऑफ फायर' या 'डबल डैमेज + आर्मर पेनिट्रेशन' को प्राथमिकता दें। मैजिक क्यूब फ्रैगमेंट्स (100 फ्रैगमेंट्स = 1 क्यूब) से स्टोर में क्लासिक बंडल्स बिल्कुल फ्री में रिडीम किए जा सकते हैं।"

            lower.contains("कैरेक्टर") || lower.contains("character") || lower.contains("कॉम्बो") || lower.contains("combo") ->
                "फ्री फायर का नंबर 1 प्रो रशर कैरेक्टर कॉम्बो है: एक्टिव में तात्सुया (Tatsuya) अथवा आलोक (Alok) रखें। पैसिव में हयातो (Hayato - आर्मर पेनिट्रेशन), केली (Kelly - तेज मूवमेंट) और लुकेता (Luqueta - 250 मैक्स HP) जोड़ें। यह कॉम्बो क्लैश स्क्वाड और बीआर रैंक दोनों में अपराजेय है।"

            lower.contains("सेंसिटिविटी") || lower.contains("sensitivity") || lower.contains("सेटिंग") || lower.contains("setting") ->
                "प्रो प्लेयर्स की अनुशंसित सेंसिटिविटी: जनरल (General): 100, रेड डॉट (Red Dot): 95, 2X स्कोप: 90, 4X स्कोप: 85, स्नाइपर स्कोप: 55, फ्री लुक: 80। फायर बटन का आकार 50% से 52% के बीच रखें और स्क्रीन पर थोड़ा नीचे सेट करें ताकि ऊपर ड्रैग करने के लिए भरपूर जगह मिले।"

            lower.contains("ग्लू वॉल") || lower.contains("gloo wall") || lower.contains("360") ->
                "फास्ट 360 सिट-अप ग्लू वॉल ट्रिक: गोली मारते ही फायर बटन दबाए रखकर स्क्रीन को नीचे जमीन की ओर स्वाइप करें, तुरंत बैठने (Crouch) का बटन दबाएं, ग्लू वॉल सिलेक्ट करें और स्क्रीन को 360 डिग्री घुमाते हुए लेफ्ट फायर बटन दबाएं। 0.2 सेकंड में आपके चारों तरफ अभेद्य सुरक्षा कवच बन जाएगा।"

            lower.contains("गन") || lower.contains("हथियार") || lower.contains("weapon") || lower.contains("best gun") ->
                "फ्री फायर की टॉप गन कॉम्बो: क्लोज रेंज के लिए M1887 (दो गोली) या MP40, और मिड से लॉन्ग रेंज के लिए वुडपेकर (Woodpecker) या M82B एंटी-ग्लू वॉल स्नाइपर। यह कॉम्बो हर रेंज में दुश्मन को नॉक करने में सक्षम है।"

            lower.contains("रैंक") || lower.contains("ग्रैंडमास्टर") || lower.contains("grandmaster") || lower.contains("heroic") ->
                "ग्रैंडमास्टर रैंक पुश गाइड: शुरुआत में क्लॉक टॉवर या पीक जैसे हॉट-ड्रॉप पर सीधा न उतरें। फाल्को पेट लेकर रिम नाम विलेज या वाटरफ्रंट जैसी सुरक्षित जगह उतरें। पहले लेवल-3 वेस्ट और फेवरेट गन्स लूटें। हमेशा सेफ जोन के पतले किनारे (Weak Zone Edge) से रोटेट करें और टॉप-3 आने के बाद ही आक्रामक रश करें।"

            else ->
                "भाई, फ्री फायर में जीत के 3 सुनहरे नियम हैं: 1. हमेशा कवर-टू-कवर मूवमेंट करें और ओपन में दौड़ने से बचें। 2. रोटेशन ड्रैग हेडशॉट और सिट-अप 360 ग्लू वॉल का अभ्यास करें। 3. तात्सुया + हयातो + केली + लुकेता का मेटा कॉम्बो इस्तेमाल करें। Booyah पक्का है!"
        }
    }
}
