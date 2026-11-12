package com.example.astrology

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sin

/**
 * Vedic Rashi (12 राशियां)
 */
enum class VedicRashi(
    val hindiName: String,
    val englishName: String,
    val symbol: String,
    val lordPlanet: String,
    val element: String,
    val luckyGemstone: String,
    val beejaMantra: String,
    val luckyColor: Color,
    val luckyNumber: Int
) {
    MESHA("मेष", "Aries", "♈", "मंगल (Mars)", "अग्नि (Fire)", "मूंगा (Red Coral)", "ॐ क्रां क्रीं क्रौं सः भौमाय नमः", Color(0xFFFF5252), 9),
    VRISHABHA("वृषभ", "Taurus", "♉", "शुक्र (Venus)", "पृथ्वी (Earth)", "हीरा / ओपल (Diamond/Opal)", "ॐ द्रां द्रीं द्रौं सः शुक्राय नमः", Color(0xFF69F0AE), 6),
    MITHUNA("मिथुन", "Gemini", "♊", "बुध (Mercury)", "वायु (Air)", "पन्ना (Emerald)", "ॐ ब्रां ब्रीं ब्रौं सः बुधाय नमः", Color(0xFF00E5FF), 5),
    KARK("कर्क", "Cancer", "♋", "चंद्रमा (Moon)", "जल (Water)", "मोती (Pearl)", "ॐ श्रां श्रीं श्रौं सः चंद्रमसे नमः", Color(0xFFE0E0E0), 2),
    SIMHA("सिंह", "Leo", "♌", "सूर्य (Sun)", "अग्नि (Fire)", "माणिक्य (Ruby)", "ॐ ह्रां ह्रीं ह्रौं सः सूर्याय नमः", Color(0xFFFFD600), 1),
    KANYA("कन्या", "Virgo", "♍", "बुध (Mercury)", "पृथ्वी (Earth)", "पन्ना (Emerald)", "ॐ ब्रां ब्रीं ब्रौं सः बुधाय नमः", Color(0xFF00B0FF), 5),
    TULA("तुला", "Libra", "♎", "शुक्र (Venus)", "वायु (Air)", "हीरा (Diamond)", "ॐ द्रां द्रीं द्रौं सः शुक्राय नमः", Color(0xFFFF4081), 6),
    VRISHCHIKA("वृश्चिक", "Scorpio", "♏", "मंगल / केतु", "जल (Water)", "मूंगा (Red Coral)", "ॐ क्रां क्रीं क्रौं सः भौमाय नमः", Color(0xFFFF1744), 9),
    DHANU("धनु", "Sagittarius", "♐", "बृहस्पति (Jupiter)", "अग्नि (Fire)", "पुखराज (Yellow Sapphire)", "ॐ ग्रां ग्रीं ग्रौं सः गुरवे नमः", Color(0xFFFFAB00), 3),
    MAKARA("मकर", "Capricorn", "♑", "शनि (Saturn)", "पृथ्वी (Earth)", "नीलम (Blue Sapphire)", "ॐ प्रां प्रीं प्रौं सः शनैश्चराय नमः", Color(0xFF3D5AFE), 8),
    KUMBHA("कुंभ", "Aquarius", "♒", "शनि / राहु", "वायु (Air)", "नीलम (Blue Sapphire)", "ॐ शं शनैश्चराय नमः", Color(0xFF7C4DFF), 8),
    MEENA("मीन", "Pisces", "♓", "बृहस्पति (Jupiter)", "जल (Water)", "पुखराज (Yellow Sapphire)", "ॐ ग्रां ग्रीं ग्रौं सः गुरवे नमः", Color(0xFFFFC400), 3)
}

/**
 * Western Zodiac Signs
 */
enum class WesternZodiac(
    val signName: String,
    val symbol: String,
    val dateRange: String,
    val element: String,
    val rulingPlanet: String
) {
    ARIES("Aries", "♈", "Mar 21 - Apr 19", "Fire", "Mars"),
    TAURUS("Taurus", "♉", "Apr 20 - May 20", "Earth", "Venus"),
    GEMINI("Gemini", "♊", "May 21 - Jun 20", "Air", "Mercury"),
    CANCER("Cancer", "♋", "Jun 21 - Jul 22", "Water", "Moon"),
    LEO("Leo", "♌", "Jul 23 - Aug 22", "Fire", "Sun"),
    VIRGO("Virgo", "♍", "Aug 23 - Sep 22", "Earth", "Mercury"),
    LIBRA("Libra", "♎", "Sep 23 - Oct 22", "Air", "Venus"),
    SCORPIO("Scorpio", "♏", "Oct 23 - Nov 21", "Water", "Pluto/Mars"),
    SAGITTARIUS("Sagittarius", "♐", "Nov 22 - Dec 21", "Fire", "Jupiter"),
    CAPRICORN("Capricorn", "♑", "Dec 22 - Jan 19", "Earth", "Saturn"),
    AQUARIUS("Aquarius", "♒", "Jan 20 - Feb 18", "Air", "Uranus/Saturn"),
    PISCES("Pisces", "♓", "Feb 19 - Mar 20", "Water", "Neptune/Jupiter")
}

/**
 * Navagraha Planets
 */
enum class NavagrahaPlanet(
    val hindiName: String,
    val englishName: String,
    val nature: String,
    val cosmicFrequency: String,
    val description: String
) {
    SURYA("सूर्य", "Sun", "आत्मा व तेज (Soul/Vitality)", "126.22 Hz", "आत्मविश्वास, सम्मान, नेतृत्व और आरोग्य के कारक।"),
    CHANDRA("चंद्रमा", "Moon", "मन व शांति (Mind/Emotions)", "210.42 Hz", "मानसिक शांति, अंतर्ज्ञान, संवेदनशीलता और जल तत्व के नियंत्रक।"),
    MANGAL("मंगल", "Mars", "साहस व ऊर्जा (Courage/Drive)", "144.72 Hz", "पराक्रम, भूमि, रक्षा, रक्त संचार और दृढ़ संकल्प के स्वामी।"),
    BUDHA("बुध", "Mercury", "बुद्धि व वाणी (Intellect/Speech)", "141.27 Hz", "तर्क, व्यापार, संवाद, गणित और तकनीकी विश्लेषण के प्रदाता।"),
    GURU("गुरु (बृहस्पति)", "Jupiter", "ज्ञान व धर्म (Wisdom/Expansion)", "183.58 Hz", "उच्च ज्ञान, समृद्धि, दर्शन, अध्यात्म और सौभाग्य के अधिपति।"),
    SHUKRA("शुक्र", "Venus", "प्रेम व सौंदर्य (Love/Creativity)", "221.23 Hz", "कला, संगीत, सौंदर्य, आकर्षण और विलासिता के दाता।"),
    SHANI("शनि", "Saturn", "कर्म व न्याय (Karma/Discipline)", "147.85 Hz", "न्याय, अनुशासन, धैर्य, एकाग्रता और कर्मफल के निर्णायक।"),
    RAHU("राहु", "North Node", "माया व तकनीक (Innovation/Desire)", "187.61 Hz", "आधुनिक विज्ञान, कृत्रिम बुद्धिमत्ता, तकनीक और तीव्र विस्तार।"),
    KETU("केतु", "South Node", "मोक्ष व वैराग्य (Moksha/Intuition)", "187.61 Hz", "गहन आध्यात्मिकता, गूढ़ ज्ञान, वैराग्य और चेतना जागृति।")
}

data class DailyAstrologyPrediction(
    val rashi: VedicRashi,
    val generalHoroscope: String,
    val careerAndFinance: String,
    val loveAndRelationships: String,
    val healthAndVitality: String,
    val luckyColorName: String,
    val luckyNumber: Int,
    val auspiciousTimeMuhurat: String,
    val planetaryRemedy: String,
    val cosmicVibrationIndex: Int // 0 to 100
)

data class BioAstrologicalRhythm(
    val physicalRhythmScore: Float,    // 0.0 to 1.0 (23-day cycle)
    val emotionalRhythmScore: Float,   // 0.0 to 1.0 (28-day cycle)
    val intellectualRhythmScore: Float,// 0.0 to 1.0 (33-day cycle)
    val spiritualAlignmentScore: Float,// 0.0 to 1.0 (Planetary transit influence)
    val optimalMeditationTime: String,
    val cosmicFrequencyHertz: Float = 432.0f
)

/**
 * Astrological Intelligence Engine:
 * Integrates Vedic Astrology (पराशर ज्योतिष), Western Astrology,
 * Planetary influences, Cosmic energy vibrations, and Bio-Astrological rhythm synchronization.
 */
object AstrologicalIntelligenceEngine {

    fun calculateBioRhythms(calendar: Calendar = Calendar.getInstance()): BioAstrologicalRhythm {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR).toDouble()
        val year = calendar.get(Calendar.YEAR).toDouble()
        val totalDays = (year * 365.25) + dayOfYear

        // Standard Bio-Rhythm Mathematical Cycles:
        // Physical: 23 days, Emotional: 28 days, Intellectual: 33 days
        val physical = ((sin(2.0 * Math.PI * totalDays / 23.0) + 1.0) / 2.0).toFloat().coerceIn(0.1f, 1.0f)
        val emotional = ((sin(2.0 * Math.PI * totalDays / 28.0) + 1.0) / 2.0).toFloat().coerceIn(0.1f, 1.0f)
        val intellectual = ((sin(2.0 * Math.PI * totalDays / 33.0) + 1.0) / 2.0).toFloat().coerceIn(0.1f, 1.0f)

        // Cosmic lunar-solar spiritual alignment
        val spiritual = ((physical * 0.3f) + (emotional * 0.4f) + (intellectual * 0.3f)).coerceIn(0.2f, 0.98f)

        return BioAstrologicalRhythm(
            physicalRhythmScore = physical,
            emotionalRhythmScore = emotional,
            intellectualRhythmScore = intellectual,
            spiritualAlignmentScore = spiritual,
            optimalMeditationTime = "ब्रह्म मुहूर्त (प्रातः 04:24 AM - 05:12 AM) एवं संध्याकाल (06:15 PM)",
            cosmicFrequencyHertz = 528.0f // Solfeggio Miracle / DNA Frequency
        )
    }

    fun getDailyPrediction(rashi: VedicRashi): DailyAstrologyPrediction {
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dateStr = SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("hi-IN")).format(Date())

        val (general, career, love, health, remedy) = when (rashi) {
            VedicRashi.MESHA -> Tuple5(
                "आज $dateStr को मंगल का सकारात्मक प्रभाव आपके आत्मविश्वास और निर्णय क्षमता में वृद्धि करेगा। सोचे हुए कार्यों में तेजी आएगी।",
                "कार्यक्षेत्र में सहकर्मियों का सहयोग मिलेगा। नई योजनाएं शुरू करने के लिए समय उत्तम है। आर्थिक स्थिति सुदृढ़ होगी।",
                "पारिवारिक जीवन में सामंजस्य बना रहेगा। जीवनसाथी से विचार-विमर्श करके लिए गए निर्णय लाभकारी होंगे।",
                "ऊर्जा का स्तर उच्च रहेगा। नियमित व्यायाम और पर्याप्त जल सेवन से दिनभर स्फूर्ति बनी रहेगी।",
                "प्रातःकाल सूर्यदेव को तांबे के पात्र से जल अर्पित करें और हनुमान चालीसा का पाठ करें।"
            )
            VedicRashi.VRISHABHA -> Tuple5(
                "शुक्र देव का शुभ गोचर आपके व्यक्तित्व और रचनात्मकता में आकर्षण लाएगा। आर्थिक लाभ के नए अवसर प्राप्त होंगे।",
                "व्यवसाय व नौकरी में नए संपर्क स्थापित होंगे। कला, डिजाइन और वित्तीय क्षेत्रों से जुड़े जातकों को बड़ी सफलता मिलेगी।",
                "प्रेम संबंधों में मधुरता रहेगी। प्रियजन के साथ सुखद समय व्यतीत होगा।",
                "गले व खान-पान का ध्यान रखें। ताजा व सात्विक आहार लें।",
                "गाय को हरा चारा या सफेद मिष्ठान्न खिलाएं और 'ॐ शुं शुक्राय नमः' का 11 बार जप करें।"
            )
            VedicRashi.MITHUNA -> Tuple5(
                "बुध ग्रह के प्रभाव से आपकी बौद्धिक क्षमता और संवाद कौशल आज सबसे शिखर पर रहेगा। जटिल समस्याएं आसानी से सुलझेंगी।",
                "आईटी, कोडिंग, मीडिया व लेखन से जुड़े कार्यों में विशेष लाभ। निवेश के मामले में विशेषज्ञों की सलाह लें।",
                "मित्रों और परिजनों के साथ संवाद में स्पष्टता रखें। कोई शुभ समाचार मिल सकता है।",
                "मानसिक शांति के लिए 10 मिनट का ध्यान करें। स्क्रीन टाइम थोड़ा कम करें।",
                "तुलसी के पौधे में जल अर्पित करें और पक्षियों को हरी मूंग के दाने डालें।"
            )
            VedicRashi.KARK -> Tuple5(
                "चंद्रमा का स्नेहिल प्रभाव आपकी अंतःप्रेरणा और संवेदनशीलता को जागृत रखेगा। परिवार के प्रति आपकी भावनाएं सराही जाएंगी।",
                "कार्यस्थल पर धैर्य से काम लें। लंबित पड़े कार्य आज पूर्ण हो सकते हैं। धन संचय में वृद्धि होगी।",
                "दांपत्य जीवन में सुखद शांति रहेगी। बड़ों का आशीर्वाद मिलेगा।",
                "मौसम में बदलाव के अनुसार दिनचर्या रखें। रात्रि में गुनगुना दूध पिएं।",
                "भगवान शिव का जलाभिषेक करें और 'ॐ नमः शिवाय' का जप करें।"
            )
            VedicRashi.SIMHA -> Tuple5(
                "सूर्य देव का तेज आपको समाज और कार्यक्षेत्र में यश, सम्मान व नेतृत्व का अवसर प्रदान करेगा। आपका प्रभाव बढ़ेगा।",
                "प्रशासनिक व प्रबंधकीय दायित्वों में सफलता। वरिष्ठ अधिकारियों से प्रशंसा प्राप्त होगी। व्यापार में वृद्धि।",
                "रिश्तों में अहंकार से बचें। विनम्रता आपके प्रभाव को और अधिक प्रभावशाली बनाएगी।",
                "हृदय व आंखों का ध्यान रखें। प्रातःकाल धूप में कुछ समय बिताएं।",
                "आदित्य हृदय स्तोत्र का पाठ करें या गायत्री मंत्र का 11 बार मनन करें।"
            )
            VedicRashi.KANYA -> Tuple5(
                "सूक्ष्म विश्लेषण और योजनाबद्ध कार्यप्रणाली आपको हर क्षेत्र में दूसरों से आगे रखेगी। अध्ययन व रिसर्च में रुचि बढ़ेगी।",
                "अकाउंट्स, ऑडिटिंग, डेटा और सॉफ्टवेयर निर्माण में उत्तम परिणाम। अनावश्यक खर्चों पर नियंत्रण रखें।",
                "पारिवारिक जिम्मेदारियों को कुशलतापूर्वक निभाएंगे। आपसी समझ बढ़ेगी।",
                "पेट व पाचन तंत्र का विशेष ध्यान रखें। योग व प्राणायाम लाभप्रद रहेगा।",
                "हरे रंग का वस्त्र या रुमाल पास रखें और गणेश जी को दूर्वा अर्पित करें।"
            )
            VedicRashi.TULA -> Tuple5(
                "संतुलन और न्यायप्रियता आपकी सबसे बड़ी शक्ति बनेगी। साझेदारी के कार्यों में अनुकूल परिणाम मिलेंगे।",
                "व्यापारिक साझेदारों के साथ तालमेल बेहतर होगा। रचनात्मक परियोजनाओं में उल्लेखनीय प्रगति।",
                "प्रेम संबंधों में सामंजस्य और सम्मान रहेगा। सामाजिक प्रतिष्ठा में वृद्धि होगी।",
                "पीठ व मांसपेशियों के खिंचाव से बचें। हल्का व्यायाम करें।",
                "माता लक्ष्मी की आरती करें और इत्र या सुगंधित द्रव्य का उपयोग करें।"
            )
            VedicRashi.VRISHCHIKA -> Tuple5(
                "गहन इच्छाशक्ति और शोधपरक दृष्टि आपको कठिन से कठिन चुनौतियों से पार पाने में मदद करेगी। कोई गोपनीय लाभ मिल सकता है।",
                "तकनीकी, चिकित्सा व रक्षा क्षेत्र में कार्य करने वालों के लिए दिन बहुत अनुकूल है। शत्रु परास्त होंगे।",
                "रिश्तों में भावनात्मक ईमानदारी बनाए रखें। संदेह को संवाद से दूर करें।",
                "ऊर्जा बहुत अधिक रहेगी, इसे सकारात्मक कार्यों में लगाएं। पर्याप्त नींद लें।",
                "हनुमान जी के मंदिर में चमेली के तेल का दीपक प्रज्वलित करें।"
            )
            VedicRashi.DHANU -> Tuple5(
                "गुरु बृहस्पति की कृपा से उच्च शिक्षा, दर्शन और दूरगामी लक्ष्यों में सफलता के योग हैं। आपका दृष्टिकोण आशावादी रहेगा।",
                "सलाहकार, शिक्षण, कानून व प्रबंधन में लगे लोगों को यश मिलेगा। नया सीखने के अवसर मिलेंगे।",
                "जीवनसाथी या मित्रों के साथ किसी ज्ञानवर्धक यात्रा की योजना बन सकती है।",
                "पैरों व घुटनों का ध्यान रखें। ताजे फलों का सेवन करें।",
                "माथे पर चंदन या हल्दी का तिलक लगाएं और गुरुजनों का आशीर्वाद लें।"
            )
            VedicRashi.MAKARA -> Tuple5(
                "शनि देव की स्थिरता और आपकी कड़ी मेहनत रंग लाएगी। दीर्घकालिक निवेश और योजनाओं में दृढ़ प्रगति होगी।",
                "अनुशासन और समर्पण से कार्यक्षेत्र में बड़ा मुकाम हासिल होगा। अधिकारियों का विश्वास प्राप्त होगा।",
                "परिवार में बड़ों की सेवा करें। व्यावहारिक दृष्टिकोण से समस्याएं हल होंगी।",
                "जोड़ों व हड्डियों के स्वास्थ्य के लिए कैल्शियम युक्त आहार लें।",
                "शनिवार को पीपल के वृक्ष के नीचे सरसों के तेल का दीपक जलाएं।"
            )
            VedicRashi.KUMBHA -> Tuple5(
                "नवाचार, वैज्ञानिक सोच और जनहित के कार्यों में आपकी रुचि बढ़ेगी। कुछ अलग व अद्वितीय करने की प्रेरणा मिलेगी।",
                "आर्टिफिशियल इंटेलिजेंस, इलेक्ट्रॉनिक्स और अनुसंधान में उत्तम सफलता। अप्रत्याशित वित्तीय अवसर।",
                "मित्रों के साथ बौद्धिक चर्चाएं होंगी। समाज में आपकी प्रतिष्ठा बढ़ेगी।",
                "रक्तचाप व परिसंचरण का ध्यान रखें। नियमित प्राणायाम करें।",
                "ज़रूरतमंद व्यक्तियों की सहायता करें और 'ॐ शं शनैश्चराय नमः' का जप करें।"
            )
            VedicRashi.MEENA -> Tuple5(
                "बृहस्पति के आध्यात्मिक प्रभाव से आपका अंतर्ज्ञान अत्यंत तीव्र रहेगा। कला, संगीत व आध्यात्मिक खोज में मन लगेगा।",
                "रचनात्मक लेखन, फिल्म, संगीत और समाज सेवा के कार्यों में उत्कृष्ट उपलब्धि। धन आगमन के योग।",
                "रिश्तों में गहरी आत्मीयता और भावनात्मक सुरक्षा का अनुभव होगा।",
                "मानसिक विश्राम और प्रकृति के सानिध्य में समय बिताना ऊर्जा को रीचार्ज करेगा।",
                "विष्णु सहस्त्रनाम का श्रवण करें या 'ॐ नमो भगवते वासुदेवाय' का जप करें।"
            )
        }

        val luckyMuhurat = when (dayOfWeek) {
            Calendar.SUNDAY -> "अभिजीत मुहूर्त: 11:45 AM - 12:35 PM"
            Calendar.MONDAY -> "अभिजीत मुहूर्त: 11:48 AM - 12:38 PM"
            Calendar.TUESDAY -> "अभिजीत मुहूर्त: 11:46 AM - 12:36 PM"
            Calendar.WEDNESDAY -> "अभिजीत मुहूर्त: 11:47 AM - 12:37 PM"
            Calendar.THURSDAY -> "अभिजीत मुहूर्त: 11:49 AM - 12:39 PM"
            Calendar.FRIDAY -> "अभिजीत मुहूर्त: 11:45 AM - 12:35 PM"
            else -> "अभिजीत मुहूर्त: 11:50 AM - 12:40 PM"
        }

        val cosmicIndex = 78 + (abs(rashi.ordinal * 7) % 21)

        return DailyAstrologyPrediction(
            rashi = rashi,
            generalHoroscope = general,
            careerAndFinance = career,
            loveAndRelationships = love,
            healthAndVitality = health,
            luckyColorName = when (rashi) {
                VedicRashi.MESHA, VedicRashi.VRISHCHIKA -> "गहरा लाल व सिंदूरी (Deep Red)"
                VedicRashi.VRISHABHA, VedicRashi.TULA -> "दूधिया सफेद व चमकीला गुलाबी (White/Pink)"
                VedicRashi.MITHUNA, VedicRashi.KANYA -> "तोता हरा व एमरल्ड (Emerald Green)"
                VedicRashi.KARK -> "मोती सफेद व सिल्वर (Pearl White/Silver)"
                VedicRashi.SIMHA -> "सुनहरा व नारंगी (Golden/Orange)"
                VedicRashi.DHANU, VedicRashi.MEENA -> "पीला व केसरिया (Bright Yellow/Saffron)"
                VedicRashi.MAKARA, VedicRashi.KUMBHA -> "गहरा नीला व जामुनी (Royal Blue/Violet)"
            },
            luckyNumber = rashi.luckyNumber,
            auspiciousTimeMuhurat = luckyMuhurat,
            planetaryRemedy = remedy,
            cosmicVibrationIndex = cosmicIndex
        )
    }

    private data class Tuple5(
        val general: String,
        val career: String,
        val love: String,
        val health: String,
        val remedy: String
    )
}
