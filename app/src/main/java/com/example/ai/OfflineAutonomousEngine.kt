package com.example.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.astrology.AstrologicalIntelligenceEngine
import com.example.astrology.VedicRashi

data class OfflineProcessingResult(
    val handled: Boolean,
    val response: String,
    val intentCategory: String = "OFFLINE_CORE"
)

/**
 * 100% Offline Autonomous AI Engine:
 * Enables Arya AI to function completely without an internet connection.
 * Handles device commands, mathematics, time/date, knowledge queries,
 * and offline natural conversation with zero latency.
 */
object OfflineAutonomousEngine {

    fun isDeviceOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun processQueryOffline(query: String, context: Context): OfflineProcessingResult {
        val clean = query.trim().lowercase()

        // 1. Time & Date Query (Strict explicit match only)
        val isExplicitTime = (clean == "time" || clean == "समय" || clean == "टाइम" ||
                clean.contains("समय क्या है") || clean.contains("टाइम क्या है") || clean.contains("कितने बजे हैं") ||
                clean.contains("समय बताओ") || clean.contains("टाइम बताओ") ||
                clean == "what is the time" || clean == "what time is it") &&
                !clean.contains("लग") && !clean.contains("क्यों") && !clean.contains("रियल") && !clean.contains("real")

        if (isExplicitTime) {
            val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            return OfflineProcessingResult(
                handled = true,
                response = "वर्तमान समय है: $timeStr",
                intentCategory = "TIME_DATE"
            )
        }

        val isExplicitDate = clean == "date" || clean == "तारीख" || clean == "आज क्या तारीख है" ||
                clean.contains("तारीख क्या है") || clean.contains("आज कौन सा दिन है") || clean.contains("आज क्या दिन है")

        if (isExplicitDate) {
            val dateStr = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.forLanguageTag("hi-IN")).format(Date())
            return OfflineProcessingResult(
                handled = true,
                response = "आज की तारीख है: $dateStr",
                intentCategory = "TIME_DATE"
            )
        }

        // 2. Math Calculations
        val mathMatch = solveSimpleMath(clean)
        if (mathMatch != null) {
            return OfflineProcessingResult(
                handled = true,
                response = "ऑफलाइन गणितीय गणना:\n$mathMatch",
                intentCategory = "MATH_SOLVER"
            )
        }

        // 3. Arya Offline Identity & Core
        if (clean.contains("तुम कौन हो") || clean.contains("who are you") || clean.contains("नाम क्या है")) {
            return OfflineProcessingResult(
                handled = true,
                response = "मैं Arya (आर्या) हूँ — 100% ऑफलाइन ऑटोनॉमस क्षमता से लैस आपकी निजी AI सहायक। बिना इंटरनेट के भी मैं आपके ऐप्स खोल सकती हूँ, गणित हल कर सकती हूँ, नोट्स लिख सकती हूँ और आपकी आवाज़ पहचान सकती हूँ।",
                intentCategory = "IDENTITY"
            )
        }

        if (clean.contains("नमस्ते") || clean.contains("हेलो") || clean.contains("हाय") || clean.contains("hello") || clean.contains("hi")) {
            return OfflineProcessingResult(
                handled = true,
                response = "नमस्ते! मैं आपकी ऑफलाइन AI सहायक Arya हूँ। बिना इंटरनेट के भी आपकी सेवा में हाज़िर हूँ। बताइए क्या सहायता करूँ?",
                intentCategory = "GREETING"
            )
        }

        if (clean.contains("कैसी हो") || clean.contains("how are you")) {
            return OfflineProcessingResult(
                handled = true,
                response = "मैं बहुत अच्छी हूँ और पूर्णतः सक्रिय हूँ! ऑफलाइन मोड में मेरा न्यूरल इंजन पूरी गति से काम कर रहा है।",
                intentCategory = "STATUS"
            )
        }

        if (clean.contains("मोटिवेशन") || clean.contains("विचार") || clean.contains("quote") || clean.contains("सुविचार")) {
            val quotes = listOf(
                "\"सफलता का रहस्य है लगातार आगे बढ़ते रहना, चाहे गति कितनी भी धीमी क्यों न हो।\"",
                "\"मंजिल उन्हीं को मिलती है, जिनके सपनों में जान होती है; पंखों से कुछ नहीं होता, हौसलों से उड़ान होती है।\"",
                "\"हर नया दिन एक नया अवसर लेकर आता है, अपनी पूरी क्षमता के साथ शुरुआत करें।\""
            )
            return OfflineProcessingResult(
                handled = true,
                response = "आज का प्रेरक विचार:\n${quotes.random()}",
                intentCategory = "MOTIVATION"
            )
        }

        if (clean.contains("ऑफलाइन क्या") || clean.contains("what can you do offline")) {
            return OfflineProcessingResult(
                handled = true,
                response = "ऑफलाइन मोड में मेरी प्रमुख क्षमताएं:\n• कोई भी फ़ोन ऐप खोलना (यूट्यूब, व्हाट्सएप, कैमरा, सेटिंग्स)\n• गणित व हिसाब-किताब हल करना\n• समय, तारीख व दैनिक रिमाइंडर\n• वैदिक व पाश्चात्य राशिफल व बायो-रिदम\n• वॉइस बायोमेट्रिक पहचान व विश्लेषण\n• प्रेरक विचार और ऑफलाइन सहायता",
                intentCategory = "CAPABILITIES"
            )
        }

        // 4. Astrological & Horoscope Queries
        if (clean.contains("राशिफल") || clean.contains("ज्योतिष") || clean.contains("horoscope") || clean.contains("राशि") || clean.contains("मुहूर्त")) {
            val matchedRashi = VedicRashi.values().find { clean.contains(it.hindiName.lowercase()) || clean.contains(it.englishName.lowercase()) }
                ?: VedicRashi.MESHA
            val pred = AstrologicalIntelligenceEngine.getDailyPrediction(matchedRashi)
            return OfflineProcessingResult(
                handled = true,
                response = "ऑफलाइन ज्योतिष गणना (${matchedRashi.hindiName} राशि):\n${pred.generalHoroscope}\n\n• शुभ अंक: ${pred.luckyNumber} | शुभ रंग: ${pred.luckyColorName}\n• ${pred.auspiciousTimeMuhurat}\n• उपाय: ${pred.planetaryRemedy}",
                intentCategory = "ASTROLOGY"
            )
        }

        // General Offline Fallback: Answer user's question intelligently
        return OfflineProcessingResult(
            handled = true,
            response = LocalAiKnowledgeEngine.generateIntelligentResponse(query),
            intentCategory = "OFFLINE_RESPONSE"
        )
    }

    private fun solveSimpleMath(input: String): String? {
        val sanitized = input.replace("प्लस", "+")
            .replace("माइनस", "-")
            .replace("गुणा", "*")
            .replace("भाग", "/")
            .replace("जोड़", "+")
            .replace("घटाव", "-")

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

        val formattedResult = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
        val displayOp = if (op == "*") "×" else op
        return "$num1Str $displayOp $num2Str = $formattedResult"
    }
}
