package com.example.ai

/**
 * Free Fire Knowledge Base (फ्री फायर अल्टीमेट नॉलेज बेस)
 * Contains comprehensive, authentic, and deep tactical data for Garena Free Fire / Free Fire MAX:
 * - Characters & Abilities (Active & Passive)
 * - Weapons & In-Depth Gun Statistics (Damage, Fire Rate, Range, Armor Penetration)
 * - Game Mechanics: One-Tap Headshot, Rotation Drag, Fast 360 Sit-up Gloo Wall
 * - Attachments, Pets, Maps (Bermuda, Purgatory, Kalahari, Alpine, NeXtara)
 * - Clash Squad (CS) Round Economy & Ranked Battle Royale (BR) Strategies
 */

enum class WeaponCategory(val labelHindi: String, val badgeColorHex: Long) {
    AR("असॉल्ट राइफल (AR)", 0xFFFF9800),
    SMG("सब-मशीन गन (SMG)", 0xFF00E5FF),
    SHOTGUN("शॉटगन (Shotgun)", 0xFFE91E63),
    SNIPER("स्नाइपर (Sniper)", 0xFF9C27B0),
    MARKSMAN("मार्क्समैन राइफल (Marksman)", 0xFF4CAF50),
    PISTOL("पिस्तौल व सेकेंडरी", 0xFFFFC107)
}

data class FfWeapon(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val damage: Int,            // Scale 0-100
    val rateOfFire: Int,        // Scale 0-100
    val range: Int,             // Scale 0-100
    val reloadSpeed: Int,       // Scale 0-100
    val magazine: Int,          // Bullet capacity
    val accuracy: Int,          // Scale 0-100
    val movementSpeed: Int,     // Scale 0-100
    val armorPenetration: Int,  // Scale 0-100
    val proTipHindi: String,
    val bestAttachmentsHindi: String
)

enum class CharacterSkillType(val labelHindi: String) {
    ACTIVE("सक्रिय क्षमता (Active)"),
    PASSIVE("निष्क्रिय क्षमता (Passive)")
}

data class FfCharacter(
    val id: String,
    val name: String,
    val skillNameHindi: String,
    val skillType: CharacterSkillType,
    val cooldownSec: Int,
    val descriptionHindi: String,
    val tacticalRoleHindi: String,
    val bestCombosHindi: String
)

data class FfPet(
    val id: String,
    val name: String,
    val skillNameHindi: String,
    val descriptionHindi: String,
    val bestPairingHindi: String
)

data class FfGameMechanic(
    val id: String,
    val titleHindi: String,
    val category: String,
    val stepsHindi: List<String>,
    val proSecretHindi: String
)

object FreeFireKnowledgeBase {

    val characters: List<FfCharacter> = listOf(
        FfCharacter(
            id = "alok",
            name = "डीजे आलोक (DJ Alok)",
            skillNameHindi = "ड्रॉप द बीट (Drop the Beat)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 45,
            descriptionHindi = "5 मीटर का आभा मंडल बनाता है जो गति को 15% बढ़ाता है और 10 सेकंड तक 3 HP/sec बहाल करता है। रशिंग और स्क्वाड हीलिंग का सदाबहार किंग।",
            tacticalRoleHindi = "रशर व स्क्वाड सपोर्टर (All-Rounder Rusher)",
            bestCombosHindi = "Alok + Hayato + Kelly + Luqueta (अल्टीमेट रशर कॉम्बो)"
        ),
        FfCharacter(
            id = "tatsuya",
            name = "तात्सुया (Tatsuya)",
            skillNameHindi = "रेबेल रश (Rebel Rush)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 45,
            descriptionHindi = "बिजली की गति से 0.3 सेकंड में आगे डैश करता है। लगातार 2 बार डैश किया जा सकता है। क्लोज-रेंज कॉम्बैट और सिट-अप ग्लू वॉल के साथ फ्लैंकिंग में बेजोड़।",
            tacticalRoleHindi = "अल्ट्रा-फास्ट रशर व क्लोज-कॉम्बैट स्पेशलिस्ट",
            bestCombosHindi = "Tatsuya + Hayato + Kelly + J.Biebs (CS Ranked मेटा)"
        ),
        FfCharacter(
            id = "chrono",
            name = "क्रोनो (Chrono)",
            skillNameHindi = "टाइम टर्नर (Time Turner)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 75,
            descriptionHindi = "अभंग फोर्स फील्ड बनाता है जो 800 नुकसान ब्लॉक करता है। ओपन एरिया में रिवाइव देने, ड्रॉप लूटने और थर्ड-पार्टी से बचने का सबसे मजबूत सुरक्षा कवच।",
            tacticalRoleHindi = "डिफेंसिव रशर व ओपन-फील्ड सर्वाइवर",
            bestCombosHindi = "Chrono + Moco + Nairi + Luqueta"
        ),
        FfCharacter(
            id = "k",
            name = "के (K - Captain Booyah)",
            skillNameHindi = "मास्टर ऑफ ऑल (Master of All)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 6,
            descriptionHindi = "अधिकतम EP 250 तक बढ़ाता है। जिउ-जित्सु मोड में EP से HP रूपांतरण दर 500% तेज होती है। साइकोलॉजी मोड में प्रति 2 सेकंड 3 EP स्वतः उत्पन्न करता है। मेडकिट की जरूरत न के बराबर।",
            tacticalRoleHindi = "जोन सर्वाइवर व अनस्टॉपेबल टैंक",
            bestCombosHindi = "K + Ford + J.Biebs + Luqueta (जोन के बाहर भी सुरक्षित)"
        ),
        FfCharacter(
            id = "dimitri",
            name = "दिमित्री (Dimitri)",
            skillNameHindi = "हीलिंग हार्टबीट (Healing Heartbeat)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 60,
            descriptionHindi = "3.5 मीटर हीलिंग ज़ोन बनाता है जिसमें नॉकडाउन साथी और खुद खिलाड़ी स्वयं को बिना किसी की मदद के रिवाइव (सेल्फ-रिवाइव) कर सकते हैं और 10 HP/sec रिकवर करते हैं।",
            tacticalRoleHindi = "सेल्फ-रिवाइव व क्लैश स्क्वाड लाइफसेवर",
            bestCombosHindi = "Dimitri + Thiva + Olivia + Kapella (अमर स्क्वाड हीलिंग)"
        ),
        FfCharacter(
            id = "orion",
            name = "ओरियन (Orion)",
            skillNameHindi = "क्रिमसन क्रश (Crimson Crush)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 3,
            descriptionHindi = "300 EP की खपत कर 3 सेकंड के लिए अजेय (Invulnerable) क्रिमसन रूप धारण करता है। कोई बुलेट या ग्रेनेड डैमेज नहीं लगता और 5 मीटर के दायरे में दुश्मनों से 15 HP चूस लेता है।",
            tacticalRoleHindi = "अजेय क्लोज-रेंज डिस्ट्रॉयर",
            bestCombosHindi = "Orion + Miguel + Hayato + Kelly (हर किल पर 200 EP + अनंत ओरियन)"
        ),
        FfCharacter(
            id = "homer",
            name = "होमर (Homer)",
            skillNameHindi = "सेंस शॉकवेव (Sense Shockwave)",
            skillType = CharacterSkillType.ACTIVE,
            cooldownSec = 60,
            descriptionHindi = "100 मीटर के भीतर निकटतम दुश्मन पर ड्रोन छोड़ता है, जो 25 डैमेज देता है और 5 सेकंड के लिए उनकी मूवमेंट स्पीड 60% व फायरिंग स्पीड 35% घटा देता है।",
            tacticalRoleHindi = "एंटी-रशर व ड्रोन कंट्रोलर",
            bestCombosHindi = "Homer + Shirou + Hayato + Kelly"
        ),
        FfCharacter(
            id = "hayato",
            name = "हयातो (Awakened Hayato)",
            skillNameHindi = "बुशिडो व आर्ट ऑफ ब्लेड्स (Bushido)",
            skillType = CharacterSkillType.PASSIVE,
            cooldownSec = 0,
            descriptionHindi = "मैक्स HP में 10% की कमी पर आर्मर पेनिट्रेशन 5% बढ़ता है। अवेकन्ड रूप में सामने से आने वाले डैमेज को भी 3% कम करता है। शॉटगन और SMG फाइट्स का अनिवार्य कैरेक्टर।",
            tacticalRoleHindi = "आर्मर ब्रेकर व क्लोज-रेंज मास्टर",
            bestCombosHindi = "हर रशर और स्नाइपर का अनिवार्य पैसिव"
        ),
        FfCharacter(
            id = "kelly",
            name = "केली (Awakened Kelly)",
            skillNameHindi = "डैश व डेडली वेलोसिटी (Dash)",
            skillType = CharacterSkillType.PASSIVE,
            cooldownSec = 0,
            descriptionHindi = "स्प्रिंटिंग गति 6% स्थायी रूप से बढ़ती है। 4 सेकंड दौड़ने के बाद पहला शॉट 106% अतिरिक्त नुकसान पहुंचाता है। तेज रोटेशन और मूवमेंट की रीढ़।",
            tacticalRoleHindi = "स्पीड बूस्टर व फर्स्ट-स्ट्राइक डैमेज",
            bestCombosHindi = "सभी प्रो प्लेयर्स की पहली पसंद"
        ),
        FfCharacter(
            id = "moco",
            name = "मोको (Awakened Moco)",
            skillNameHindi = "हैकर्स आई व एनिग्मा (Hacker's Eye)",
            skillType = CharacterSkillType.PASSIVE,
            cooldownSec = 0,
            descriptionHindi = "शत्रु को गोली लगते ही उस पर 5 सेकंड के लिए हरा मार्कर लग जाता है जो पूरी टीम को दिखाई देता है। अगर शत्रु भागता है तो मार्किंग अवधि 6.5 सेकंड तक बढ़ जाती है।",
            tacticalRoleHindi = "टैक्टिकल ट्रैकर व वॉल-बैंग गाइड",
            bestCombosHindi = "M82B स्नाइपर और ग्लू वॉल आर-पार स्प्रे हेतु अनिवार्य"
        ),
        FfCharacter(
            id = "luqueta",
            name = "लुकेता (Luqueta)",
            skillNameHindi = "हैट ट्रिक (Hat Trick)",
            skillType = CharacterSkillType.PASSIVE,
            cooldownSec = 0,
            descriptionHindi = "प्रत्येक किल के साथ अधिकतम HP 25 बढ़ती है (अधिकतम 50 HP तक)। यानी आपका टोटल HP 250 हो जाता है, जिससे आप 2-3 अतिरिक्त गोलियां झेल सकते हैं।",
            tacticalRoleHindi = "मैक्सिमम HP टैंक",
            bestCombosHindi = "Alok / K / Tatsuya + Luqueta"
        ),
        FfCharacter(
            id = "nairi",
            name = "नैरी (Nairi)",
            skillNameHindi = "आइस आयरन (Ice Iron)",
            skillType = CharacterSkillType.PASSIVE,
            cooldownSec = 0,
            descriptionHindi = "लगाई गई ग्लू वॉल प्रति सेकंड 150 ड्यूरेबिलिटी पुनः प्राप्त करती है। साथ ही ग्लू वॉल के भीतर रहने पर टीम मेट्स को HP हीलिंग मिलती है। एनिमी ग्रेनेड्स से पूर्ण सुरक्षा।",
            tacticalRoleHindi = "अभेद्य ग्लू वॉल डिफेंस",
            bestCombosHindi = "Skyler या Dimitri के साथ संयुक्त डिफेंस"
        )
    )

    val weapons: List<FfWeapon> = listOf(
        FfWeapon(
            id = "m1887",
            name = "M1887 (दो गोली / Double Barrel)",
            category = WeaponCategory.SHOTGUN,
            damage = 100,
            rateOfFire = 42,
            range = 21,
            reloadSpeed = 55,
            magazine = 2,
            accuracy = 10,
            movementSpeed = 78,
            armorPenetration = 54,
            proTipHindi = "वन-टैप और जंप-शॉट का बेताज बादशाह। पहली गोली चेस्ट पर कनेक्ट कर तुरंत फायर बटन को ऊपर 'रोटेशन ड्रैग' करें और तुरंत ग्लू वॉल लगाएं।",
            bestAttachmentsHindi = "कोई अटैचमेंट नहीं लगता, केवल रॉ स्किल"
        ),
        FfWeapon(
            id = "mp40",
            name = "MP40 (स्पीड किंग SMG)",
            category = WeaponCategory.SMG,
            damage = 48,
            rateOfFire = 83,
            range = 22,
            reloadSpeed = 48,
            magazine = 20,
            accuracy = 17,
            movementSpeed = 88,
            armorPenetration = 0,
            proTipHindi = "क्लोज-रेंज में सबसे तेज फायरिंग रेट। स्ट्रेट ड्रैग हेडशॉट के लिए फायर बटन को हल्का सा नीचे दबाकर सीधे ऊपर खींचें। 4-5 बुलेट में एनिमी खत्म।",
            bestAttachmentsHindi = "लेवल-3 मैगज़ीन व स्टॉक"
        ),
        FfWeapon(
            id = "woodpecker",
            name = "वुडपेकर (Woodpecker)",
            category = WeaponCategory.MARKSMAN,
            damage = 85,
            rateOfFire = 38,
            range = 63,
            reloadSpeed = 48,
            magazine = 12,
            accuracy = 69,
            movementSpeed = 62,
            armorPenetration = 77,
            proTipHindi = "उच्चतम आर्मर पेनिट्रेशन (77)। लेवल 3 का हेलमेट भी एक हेडशॉट में चकनाचूर। मिड से लॉन्ग रेंज में सिंगल-टैप ड्रैग से 300+ का लाल नंबर हेडशॉट पड़ता है।",
            bestAttachmentsHindi = "साइलेंसर, मजल लेवल-3, फोरग्रिप"
        ),
        FfWeapon(
            id = "m82b",
            name = "M82B (एंटी-ग्लू वॉल स्नाइपर)",
            category = WeaponCategory.SNIPER,
            damage = 90,
            rateOfFire = 27,
            range = 85,
            reloadSpeed = 41,
            magazine = 8,
            accuracy = 90,
            movementSpeed = 55,
            armorPenetration = 67,
            proTipHindi = "फ्री फायर की एकमात्र स्नाइपर जो ग्लू वॉल के आर-पार गोली मारकर पीछे छिपे दुश्मन को डैमेज देती है। मोको के साथ पेयर करने पर वॉल-हैंग किल्स की गारंटी।",
            bestAttachmentsHindi = "साइलेंसर"
        ),
        FfWeapon(
            id = "awm",
            name = "AWM (अल्टीमेट वन-शॉट स्नाइपर)",
            category = WeaponCategory.SNIPER,
            damage = 90,
            rateOfFire = 27,
            range = 91,
            reloadSpeed = 34,
            magazine = 5,
            accuracy = 90,
            movementSpeed = 65,
            armorPenetration = 0,
            proTipHindi = "बॉडी पर 150 और हेड पर 1100+ डैमेज। स्विच-शॉट ट्रिक: फायर करने के तुरंत बाद क्विक-वेपन-स्विच दबाएं ताकि रीलोड एनीमेशन स्किप हो जाए और लगातार गोलियां चलें।",
            bestAttachmentsHindi = "आर्मर पियर्सर चिप, मजल लेवल-3"
        ),
        FfWeapon(
            id = "ak47",
            name = "AK47 (हाय डैमेज क्लासिक AR)",
            category = WeaponCategory.AR,
            damage = 61,
            rateOfFire = 56,
            range = 72,
            reloadSpeed = 41,
            magazine = 30,
            accuracy = 41,
            movementSpeed = 62,
            armorPenetration = 0,
            proTipHindi = "डैमेज बहुत ज्यादा है लेकिन रिकॉइल तेज होता है। लगातार स्प्रे करने के बजाय 3-4 गोलियों के बर्स्ट फायर में ड्रैग करें ताकि गोलियां फैले नहीं और सीधे सिर पर लगें।",
            bestAttachmentsHindi = "मजल लेवल-3, फोरग्रिप लेवल-3, स्टॉक"
        ),
        FfWeapon(
            id = "ump",
            name = "UMP (मेटा SMG)",
            category = WeaponCategory.SMG,
            damage = 50,
            rateOfFire = 75,
            range = 25,
            reloadSpeed = 59,
            magazine = 30,
            accuracy = 42,
            movementSpeed = 79,
            armorPenetration = 63,
            proTipHindi = "क्लैश स्क्वाड का सबसे लोकप्रिय हथियार। 63 आर्मर पेनिट्रेशन के कारण वेस्ट और हेलमेट होने पर भी भारी नुकसान देता है। मिड-रेंज ड्रैग बहुत स्मूथ है।",
            bestAttachmentsHindi = "मजल लेवल-3 व मैगज़ीन"
        ),
        FfWeapon(
            id = "m1014",
            name = "M1014 (स्प्रे शॉटगन / 6 गोलियां)",
            category = WeaponCategory.SHOTGUN,
            damage = 94,
            rateOfFire = 38,
            range = 10,
            reloadSpeed = 20,
            magazine = 6,
            accuracy = 10,
            movementSpeed = 60,
            armorPenetration = 0,
            proTipHindi = "अपग्रेड चिप लगाकर M1014-III बनाएं। क्लोज रेंज में जंप करके क्राउच-ड्रैग से पूरी 6 गोलियों में से 2-3 हेडशॉट कनेक्ट हो जाते हैं।",
            bestAttachmentsHindi = "अपग्रेड चिप्स (I, II, III), शॉटगन चोक"
        ),
        FfWeapon(
            id = "scar",
            name = "SCAR (स्थिर व नियंत्रित AR)",
            category = WeaponCategory.AR,
            damage = 53,
            rateOfFire = 61,
            range = 60,
            reloadSpeed = 41,
            magazine = 30,
            accuracy = 42,
            movementSpeed = 62,
            armorPenetration = 0,
            proTipHindi = "शुरुआती और प्रो दोनों के लिए संतुलित। अपग्रेड चिप-III के साथ इसका रेट ऑफ फायर और डैमेज दोनों जबरदस्त हो जाते हैं। रिकॉइल बहुत कम है।",
            bestAttachmentsHindi = "SCAR-III चिप, फोरग्रिप, मजल"
        ),
        FfWeapon(
            id = "desert_eagle",
            name = "डेजर्ट ईगल (Desert Eagle)",
            category = WeaponCategory.PISTOL,
            damage = 90,
            rateOfFire = 33,
            range = 38,
            reloadSpeed = 69,
            magazine = 7,
            accuracy = 45,
            movementSpeed = 76,
            armorPenetration = 0,
            proTipHindi = "क्लैश स्क्वाड राउंड 1 और लोन वोल्फ का सबसे घातक हथियार। एक गोली में 495 का रेड नंबर वन-टैप हेडशॉट देता है। सिट-अप और स्टैंड-स्टिल ड्रैग बेस्ट काम करता है।",
            bestAttachmentsHindi = "मजल"
        )
    )

    val pets: List<FfPet> = listOf(
        FfPet(
            id = "mr_waggor",
            name = "मिस्टर वैगर (Mr. Waggor)",
            skillNameHindi = "स्मूथ ग्लू (Smooth Gloo)",
            descriptionHindi = "जब आपके पास 2 से कम ग्लू वॉल होती हैं, तो यह हर 100 सेकंड में एक मुफ्त ग्लू वॉल तैयार करता है। लास्ट ज़ोन सर्वाइवल के लिए अनिवार्य।",
            bestPairingHindi = "रशर, सर्वाइवर और सोलो रैंक पुशर्स"
        ),
        FfPet(
            id = "rockie",
            name = "रॉकी (Rockie)",
            skillNameHindi = "स्टे चिल (Stay Chill)",
            descriptionHindi = "एक्टिव कैरेक्टर (आलोक, क्रोनो, तात्सुया, होमर आदि) के कूलडाउन समय को 15% घटा देता है। तेजी से स्किल्स का दोबारा इस्तेमाल करने हेतु सर्वोत्तम।",
            bestPairingHindi = "Chrono, Tatsuya और Alok यूज़र्स"
        ),
        FfPet(
            id = "falco",
            name = "फाल्को (Falco)",
            skillNameHindi = "स्काईलाइन स्प्री (Skyline Spree)",
            descriptionHindi = "पैराशूट से कूदने की गति 50% और डाइविंग गति 45% बढ़ाता है। पूरी स्क्वाड को जमीन पर सबसे पहले उतारता है ताकि पहले गन मिल सके।",
            bestPairingHindi = "बीआर रैंक स्क्वाड में कम से कम 1 प्लेयर के पास होना जरूरी"
        ),
        FfPet(
            id = "beaston",
            name = "बीस्टन (Beaston)",
            skillNameHindi = "हेल्पिंग हैंड (Helping Hand)",
            descriptionHindi = "ग्रेनेड, ग्लू वॉल, फ्लैशबैंग और स्मोक फेंकने की दूरी 30% बढ़ा देता है। दूर बैठे एनिमी पर परफेक्ट कुक्ड ग्रेनेड गिराने का अचूक साथी।",
            bestPairingHindi = "ग्रेनेडियर और सपोर्ट प्लेयर्स"
        )
    )

    val mechanics: List<FfGameMechanic> = listOf(
        FfGameMechanic(
            id = "rotation_drag",
            titleHindi = "वन-टैप रोटेशन ड्रैग (One-Tap Rotation Drag)",
            category = "हेडशॉट तकनीक",
            stepsHindi = listOf(
                "क्रॉसहेयर (सफेद गोला) को सीधे दुश्मन के शरीर पर न रखें, बल्कि दुश्मन के पैरों के पास या बाएं/दाएं साइड में रखें।",
                "जैसे ही फायर बटन दबाएं, उसे 'U' या 'J' आकार में नीचे हल्का सा झुकाकर बिजली की तेजी से दुश्मन के सिर की दिशा में ऊपर खींचें।",
                "फायर होते ही तुरंत क्विक-वेपन-स्विच या ग्लू वॉल बटन दबाएं ताकि एनीमेशन कैंसिल हो और रीलोड न फंसे।"
            ),
            proSecretHindi = "अगर दुश्मन दाईं ओर भाग रहा है, तो फायर बटन को दाईं ओर तिरछा ऊपर खींचें। अगर बाईं ओर भाग रहा है, तो बाईं ओर खींचें।"
        ),
        FfGameMechanic(
            id = "situp_360_gloowall",
            titleHindi = "फास्ट 360 सिट-अप ग्लू वॉल (360 Sit-Up Gloo Wall)",
            category = "रक्षात्मक मैकेनिक",
            stepsHindi = listOf(
                "शॉट फायर करते ही फायर बटन को पकड़े हुए स्क्रीन को तेजी से नीचे जमीन की ओर स्वाइप करें।",
                "उसी समय 'सिट (बैठने)' का बटन दबाएं और ग्लू वॉल पर टैप करें।",
                "बाएं हाथ के फायर बटन से ग्लू वॉल ड्रॉप करें और स्क्रीन को 360 डिग्री घुमाकर अपने चारों तरफ तीन दीवारें 0.3 सेकंड में खड़ी करें।"
            ),
            proSecretHindi = "बैठने (Crouch) से ग्लू वॉल आपके बिल्कुल पैरों के पास लगती है, जिससे गोली लगने का गैप 0% हो जाता है।"
        ),
        FfGameMechanic(
            id = "crosshair_placement",
            titleHindi = "रेड बनाम व्हाइट क्रॉसहेयर प्लेसमेंट (Crosshair Secrets)",
            category = "निशानेबाजी",
            stepsHindi = listOf(
                "व्हाइट क्रॉसहेयर तकनीक (Shotguns & Desert Eagle): क्रॉसहेयर को सफेद रखते हुए ड्रैग करने से एम-लॉक नहीं होता और सीधे हेडशॉट कनेक्ट होता है।",
                "रेड क्रॉसहेयर तकनीक (SMG & AR): क्लोज रेंज में पहले क्रॉसहेयर लाल होने दें, फिर स्ट्रेट ड्रैग करें ताकि ऑटो-एम सिर की ओर लॉक हो जाए।"
            ),
            proSecretHindi = "लॉन्ग रेंज में पहले स्कोप इन करें, रेड डॉट बनते ही स्कोप आउट करके फायर ड्रैग करें।"
        )
    )

    fun getSensitivityPresets(refreshRateHz: Int): Map<String, Int> {
        return when {
            refreshRateHz >= 120 -> mapOf(
                "जनरल (General)" to 98,
                "रेड डॉट (Red Dot)" to 94,
                "2X स्कोप (2X Scope)" to 88,
                "4X स्कोप (4X Scope)" to 82,
                "स्नाइपर स्कोप (Sniper Scope)" to 52,
                "फ्री लुक (Free Look)" to 75
            )
            refreshRateHz >= 90 -> mapOf(
                "जनरल (General)" to 100,
                "रेड डॉट (Red Dot)" to 96,
                "2X स्कोप (2X Scope)" to 90,
                "4X स्कोप (4X Scope)" to 85,
                "स्नाइपर स्कोप (Sniper Scope)" to 58,
                "फ्री लुक (Free Look)" to 80
            )
            else -> mapOf(
                "जनरल (General)" to 100,
                "रेड डॉट (Red Dot)" to 100,
                "2X स्कोप (2X Scope)" to 95,
                "4X स्कोप (4X Scope)" to 90,
                "स्नाइपर स्कोप (Sniper Scope)" to 65,
                "फ्री लुक (Free Look)" to 85
            )
        }
    }

    val patchUpdates: List<FfPatchUpdate> = listOf(
        FfPatchUpdate(
            patchVersion = "OB46 / 2026 करंट पैच",
            titleHindi = "शॉटगन स्प्रेड री-बैलेंस व फास्ट स्विच ऑप्टिमाइजेशन",
            keyChangesHindi = listOf(
                "M1887 की पहली गोली का स्प्रेड 4% कम किया गया, जिससे रोटेशन ड्रैग पर हेडशॉट ज्यादा आसानी से कनेक्ट होता है।",
                "MP40 का रीलोड टाइम 0.15 सेकंड तेज किया गया और मिड-रेंज डैमेज ड्रॉप को स्थिर किया गया।",
                "तात्सुया (Tatsuya) के डैश का कूलडाउन 45 सेकंड से बढ़ाकर 50 सेकंड किया गया ताकि अत्यधिक रश एडवांटेज संतुलित रहे।",
                "ग्लू मेकर (Gloo Maker) चिप अब बैकपैक में 5 ग्लू वॉल तक स्वचालित रूप से जनरेट करती है।"
            ),
            metaImpactHindi = "क्लैश स्क्वाड में UMP और M1887 का दबदबा बरकरार है, जबकि सोलो BR में K और दिमित्री का सर्वाइवल रेट सबसे ऊंचा है।"
        ),
        FfPatchUpdate(
            patchVersion = "OB45 समर पैच",
            titleHindi = "कैरेक्टर स्किल रिवर्क व वेपन मास्टर क्लास",
            keyChangesHindi = listOf(
                "नैरी (Nairi) की क्षमता अब ग्लू वॉल पर गोली लगने पर अतिरिक्त हीलिंग और ड्यूरेबिलिटी प्रदान करती है।",
                "वुडपेकर (Woodpecker) का आर्मर पेनिट्रेशन 75 से बढ़ाकर 77 किया गया, जिससे यह लेवल-4 वेस्ट पर भी 85+ का डैमेज देता है।"
            ),
            metaImpactHindi = "स्नाइपर्स और मार्क्समैन राइफल्स लॉन्ग रेंज में पहले से ज्यादा घातक हो गए हैं।"
        )
    )

    val legalRewardMethods: List<FfLegalRewardMethod> = listOf(
        FfLegalRewardMethod(
            id = "google_opinion_rewards",
            titleHindi = "Google Opinion Rewards से 100% फ्री प्ले क्रेडिट्स",
            category = "ऑफिशियल प्ले स्टोर बैलेंस",
            howToEarnHindi = "गूगल का आधिकारिक ऐप इंस्टॉल करें। 10-15 सेकंड के छोटे-छोटे सर्वे का उत्तर देकर प्ले स्टोर बैलेंस प्राप्त करें।",
            rewardItemsHindi = "फ्री डायमंड्स, 10 रुपये और 29 रुपये के स्पेशल एयरड्रॉप (300 डायमंड्स + गन क्रेट्स)",
            officialLinkOrNote = "Google Play Store आधिकारिक ऐप — किसी भी थर्ड पार्टी ऐप या हैक की जरूरत नहीं।"
        ),
        FfLegalRewardMethod(
            id = "official_redeem_website",
            titleHindi = "गरेना ऑफिशियल रिडीम साइट (reward.ff.garena.com)",
            category = "ऑफिशियल गरेना रिडेम्पशन",
            howToEarnHindi = "आधिकारिक गरेना रिडेम्पशन पोर्टल (reward.ff.garena.com) पर अपने फेसबुक, गूगल या एक्स अकाउंट से लॉगिन करें और 12 या 16 अक्षरों का रिडीम कोड दर्ज करें।",
            rewardItemsHindi = "मुफ्त कैरेक्टर बंडल्स, इमोट्स, रूम कार्ड्स, वेपन रॉयल वाउचर्स और डायमंड रॉयल टिकट्स",
            officialLinkOrNote = "https://reward.ff.garena.com — किसी भी फर्जी वेबसाइट पर पासवर्ड कभी न डालें।"
        ),
        FfLegalRewardMethod(
            id = "booyah_pass_free_tier",
            titleHindi = "बूयाह पास (Booyah Pass) फ्री टियर व डेली बैज मिशन्स",
            category = "इन-गेम मिशन रिवॉर्ड्स",
            howToEarnHindi = "रोजाना के बैटल रॉयल और क्लैश स्क्वाड मिशन्स पूरे करके फ्री बैज कलेक्ट करें।",
            rewardItemsHindi = "फ्री गन स्किन्स (थीम बेस्ड), अवतार, बैनर, पेट फूड और गोल्ड कॉइन्स",
            officialLinkOrNote = "प्रत्येक महीने की 1 तारीख को नया बूयाह पास सीजन शुरू होता है।"
        ),
        FfLegalRewardMethod(
            id = "guild_wars_store",
            titleHindi = "गिल्ड वॉर्स टोकन्स व गिल्ड स्टोर",
            category = "गिल्ड समुदाय रिवॉर्ड्स",
            howToEarnHindi = "सक्रिय गिल्ड में शामिल होकर प्रत्येक शनिवार को गिल्ड वॉर्स खेलें और दैनिक गिल्ड क्वेस्ट पूरे करें।",
            rewardItemsHindi = "कस्टम रूम कार्ड्स, नेम चेंज कार्ड (39 डायमंड्स + टोकन्स), वेपन लूट क्रेट्स",
            officialLinkOrNote = "इन-गेम स्टोर -> रिडीम -> गिल्ड टोकन सेक्शन।"
        ),
        FfLegalRewardMethod(
            id = "magic_cube_fragments",
            titleHindi = "मैजिक क्यूब फ्रैगमेंट्स (Magic Cube Events)",
            category = "लेजेंडरी बंडल्स",
            howToEarnHindi = "दिवाली, एनिवर्सरी और होली जैसे बड़े फेस्टिव इवेंट्स में 100 क्यूब फ्रैगमेंट्स फ्री में मिलते हैं।",
            rewardItemsHindi = "स्टोर के सभी क्लासिक और लेजेंडरी कॉस्ट्यूम बंडल्स बिल्कुल मुफ्त",
            officialLinkOrNote = "100 फ्रैगमेंट्स = 1 पूरा मैजिक क्यूब (स्टोर में सीधे रिडीम)।"
        )
    )

    val secretTricks: List<FfSecretTrick> = listOf(
        FfSecretTrick(
            id = "sound_radar_tracking",
            titleHindi = "मिनी-मैप साउंड रडार ट्रैकिंग",
            descriptionHindi = "हेडफोन के बिना भी दुश्मन की सटीक दूरी और ऊंचाई का पता लगाना।",
            proExecutionSteps = listOf(
                "मिनी-मैप पर जब लाल पदचिह्न (Red Footstep) आते हैं, तो उनकी चमक देखें। गहरा लाल रंग यानी दुश्मन 5 मीटर के अंदर है।",
                "अगर पदचिह्न के ऊपर छोटा तीर का निशान (Arrow) बना है, तो इसका अर्थ है दुश्मन आपसे ऊपरी मंजिल या छत पर है।"
            ),
            advantageHindi = "कैंपर दुश्मन को बिना चेहरा देखे प्री-एम लगाकर नॉक करने की 100% गारंटी।"
        ),
        FfSecretTrick(
            id = "fast_switch_animation_cancel",
            titleHindi = "फास्ट वेपन स्विच एनीमेशन कैंसिल (Double Shot Trick)",
            descriptionHindi = "M1887 और स्नाइपर (AWM / Kar98k) के रीलोड एनीमेशन को 0 सेकंड करना।",
            proExecutionSteps = listOf(
                "कस्टम HUD में 'क्विक वेपन स्विच (Quick Weapon Switch)' बटन को 80% साइज पर बाएं अंगूठे के पास रखें।",
                "गोली मारते ही तुरंत स्विच बटन दबाकर दूसरी गन या हाथ में मुक्का लें, और तुरंत वापस M1887 पर आ जाएं।",
                "इससे गन का कॉकिंग और चैम्बरिंग एनीमेशन स्किप हो जाता है और दूसरी गोली तुरंत फायर होती है।"
            ),
            advantageHindi = "0.5 सेकंड में 2 गोलियों का लगातार घातक बर्स्ट, जिससे विरोधी को ग्लू वॉल लगाने का मौका भी नहीं मिलता।"
        ),
        FfSecretTrick(
            id = "right_peek_camera_advantage",
            titleHindi = "राइट-पीक कैमरा एडवांटेज (Right-Side Angle Rule)",
            descriptionHindi = "फ्री फायर का थर्ड-पर्सन कैमरा हमेशा कैरेक्टर के दाहिने कंधे के ऊपर होता है।",
            proExecutionSteps = listOf(
                "हमेशा दीवार, पेड़ या ग्लू वॉल के दाहिनी तरफ (Right Side) से बाहर निकलकर फायर करें।",
                "बाईं तरफ से बाहर निकलने पर आपका पूरा शरीर पहले दिखता है और विरोधी को आसान हेडशॉट मिल जाता है।",
                "दाहिनी तरफ से केवल आपका सिर और गन की नली दिखती है, जिससे विरोधी को आप नजर नहीं आते।"
            ),
            advantageHindi = "कवर में रहते हुए दुश्मन को देखना और 0 डैमेज खाए बिना वन-टैप मारना।"
        ),
        FfSecretTrick(
            id = "smoke_moco_wallbang",
            titleHindi = "स्मोक + मोको वॉल-बैंग मार्कर ट्रिक",
            descriptionHindi = "धुएं में छिपे दुश्मन को आर-पार हेडशॉट मारना।",
            proExecutionSteps = listOf(
                "विरोधी की ग्लू वॉल के आगे स्मोक ग्रेनेड फेंकें।",
                "मोको की 'हैकर आई' स्किल से जब एक भी स्प्रे गोली दुश्मन को छूती है, तो 5 सेकंड के लिए उसके सिर के ऊपर हरा तीर चमकता है।",
                "धुएं के अंदर कुछ न दिखने पर भी उस हरे तीर के ठीक नीचे रोटेशन ड्रैग करें।"
            ),
            advantageHindi = "अदृश्य दुश्मन पर 100% अंधाधुंध की जगह पिन-पॉइंट हेडशॉट।"
        )
    )
}

data class FfPatchUpdate(
    val patchVersion: String,
    val titleHindi: String,
    val keyChangesHindi: List<String>,
    val metaImpactHindi: String
)

data class FfLegalRewardMethod(
    val id: String,
    val titleHindi: String,
    val category: String,
    val howToEarnHindi: String,
    val rewardItemsHindi: String,
    val officialLinkOrNote: String
)

data class FfSecretTrick(
    val id: String,
    val titleHindi: String,
    val descriptionHindi: String,
    val proExecutionSteps: List<String>,
    val advantageHindi: String
)
