package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log

data class InstalledApp(
    val name: String,
    val packageName: String,
    val icon: Drawable? = null
)

sealed class AppLaunchResult {
    data class Success(val appName: String, val packageName: String) : AppLaunchResult()
    data class BankingRestricted(val appName: String, val packageName: String, val explanation: String) : AppLaunchResult()
    data class NotFound(val query: String, val suggestedPlayStoreUrl: String) : AppLaunchResult()
    data class Error(val message: String) : AppLaunchResult()
}

/**
 * DeviceAppsManager:
 * Manages device application query, access control, and launching.
 *
 * STRICT USER PRIVACY & FINANCIAL SECURITY RULE:
 * - Access is granted to all non-banking device apps (social, media, tools, productivity, system, etc.).
 * - ALL banking, financial, payment, UPI, loan, card, wallet, and investment apps are STRICTLY EXCLUDED
 *   and RESTRICTED from Arya AI access ("सिर्फ बैंकिंग ऐप्स को छोड़कर / जो ऐप बैंक से जुड़े हुए हैं, उन सब ऐप को छोड़कर").
 */
object DeviceAppsManager {

    private const val TAG = "DeviceAppsManager"

    /**
     * Keywords and identifiers associated with banking, finance, wallets, UPI, credit, cards & payments.
     * Matches app names, queries, and package names.
     */
    private val bankingKeywords = listOf(
        // Core English banking & finance keywords
        "bank", "banking", "finance", "financial", "payment", "upi", "wallet",
        "money", "credit", "debit", "loan", "invest", "stock", "mutual fund",
        "card", "atm", "insurance", "paisa", "rupee",

        // Hindi keywords
        "बैंक", "बैंकिंग", "पैसा", "पैसे", "रुपया", "रुपये", "खाता", "पासबुक",
        "पेमेंट", "यूपीआई", "वॉलेट", "लोन", "कर्ज", "निवेश", "क्रेडिट", "डेबिट", "कार्ड",
        "पेटीएम", "फोनपे", "गूगल पे", "जीपे", "भीम", "क्रेड", "योनो", "बजाज फिनसर्व", "ग्रो", "ज़ेरोधा",

        // Major Indian & Global Banks & Netbanking apps
        "sbi", "state bank", "yono", "hdfc", "icici", "imobile", "axis", "kotak",
        "pnb", "punjab national", "bob", "bank of baroda", "canara", "union bank",
        "idbi", "indusind", "yes bank", "federal bank", "rbl", "bandhan", "uco",
        "indian bank", "central bank", "bank of india", "citibank", "standard chartered",
        "hsbc", "dbs", "barclays", "chase", "wells fargo",

        // Major UPI, Wallets & Financial Payment gateways
        "gpay", "google pay", "phonepe", "paytm", "bhim", "cred", "amazon pay",
        "mobikwik", "freecharge", "airtel thanks", "jio finance", "navi", "fi money",
        "jupiter", "slice", "famup", "onecard", "bajaj finserv", "tata pay",

        // Investment, Stocks & Trading apps
        "zerodha", "kite", "groww", "upstox", "angel one", "5paisa", "dhan", "indmoney",
        "coinbase", "binance", "wazirx", "coinmarketcap"
    )

    /**
     * Specific package names known to belong to banking, UPI, and financial apps
     */
    private val bankingPackageSignatures = setOf(
        // UPI & Major Wallets
        "com.google.android.apps.nbu.paisa.user", // Google Pay
        "net.one97.paytm",                       // Paytm
        "com.phonepe.app",                       // PhonePe
        "in.org.npci.upiapp",                    // BHIM
        "com.dreamplug.androidapp",              // CRED
        "com.mobikwik_new",                      // MobiKwik
        "com.freecharge.android",                // Freecharge
        "com.niyo.bharat",                       // Niyo Bharat

        // Major Indian Banks
        "com.sbi.lotusintouch",                  // YONO SBI
        "com.sbi.SBIFreedomPlus",                // YONO Lite SBI
        "com.snapwork.sbi",                      // SBI Quick
        "com.hdfcbank.android",                  // HDFC Bank MobileBanking
        "com.csam.icici.bank.imobile",           // ICICI iMobile Pay
        "com.axis.mobile",                       // Axis Mobile
        "com.msf.kbank.mobile",                  // Kotak 811 Mobile Banking
        "com.pnb.pnbone",                        // PNB ONE
        "com.bankofbaroda.mconnect",             // Baroda M-Connect Plus / bob World
        "com.canarabank.mobility",               // Canara ai1
        "com.infrasofttech.uboi",                // Union Bank of India (Vyom)
        "com.idbi.mpassbook",                    // IDBI Bank
        "com.indusind.mobile",                   // IndusInd Bank
        "com.yesbank",                           // YES Bank MobileBanking
        "com.fedmobile",                         // FedMobile (Federal Bank)
        "com.rblbank.mobank",                    // RBL MoBank
        "com.bandhan.mbandhan",                  // mBandhan
        "com.infrasoft.indianbank",              // Indian Bank IndOASIS
        "com.boi.mpassbook",                     // Bank of India

        // Investments & Neobanks
        "com.zerodha.kite3",                     // Kite by Zerodha
        "com.nextbillion.groww",                 // Groww
        "in.upstox.app",                         // Upstox
        "com.msf.angelmobile",                   // Angel One
        "com.epaisa",                            // 5paisa
        "money.jupiter",                         // Jupiter Neobank
        "com.bankopen.fi",                       // Fi Money
        "com.slicepay"                           // Slice
    )

    // Hindi & English synonyms and aliases for common Android apps (Non-banking apps)
    private val commonAppAliases = mapOf(
        "youtube" to listOf("यूट्यूब", "यू ट्यूब", "youtube", "yt", "यू-ट्यूब", "युटुब", "यूटुब", "youtube app"),
        "whatsapp" to listOf("व्हाट्सएप", "व्हाट्सऐप", "वाटसप", "व्हाट्स अप", "whatsapp", "wa", "chat"),
        "camera" to listOf("कैमरा", "कैमरे", "photo", "cam", "selfie", "camera"),
        "settings" to listOf("सेटिंग्स", "सेटिंग", "system settings", "settings", "setting", "सिस्टम सेटिंग"),
        "chrome" to listOf("क्रोम", "गूगल क्रोम", "browser", "इंटरनेट", "chrome", "google chrome"),
        "maps" to listOf("मैप्स", "गूगल मैप्स", "navigation", "नक्शा", "maps", "google maps", "दिशा"),
        "gmail" to listOf("जीमेल", "ईमेल", "mail", "email", "gmail", "गूगल मेल"),
        "gallery" to listOf("गैलरी", "photos", "फ़ोटो", "तस्वीरें", "gallery", "गूगल फोटोज", "google photos"),
        "calculator" to listOf("कैलकुलेटर", "कैलकुलेशन", "calc", "गणक", "calculator", "हिसाब"),
        "clock" to listOf("घड़ी", "अलार्म", "alarm", "timer", "clock", "टाइम"),
        "calendar" to listOf("कैलेंडर", "तारीख", "पंचांग", "calendar", "डेट"),
        "contacts" to listOf("कॉन्टैक्ट्स", "संपर्क", "फोन बुक", "contacts", "नंबर"),
        "phone" to listOf("फोन", "कॉल", "डायलर", "dialer", "call", "phone", "कॉलिंग"),
        "messages" to listOf("मैसेज", "संदेश", "sms", "messaging", "messages", "एसएमएस"),
        "play store" to listOf("प्ले स्टोर", "प्लेस्टोर", "store", "apps", "play store", "google play"),
        "instagram" to listOf("इंस्टाग्राम", "इंस्टा", "instagram", "insta", "ig", "रील"),
        "facebook" to listOf("फेसबुक", "एफबी", "facebook", "fb"),
        "telegram" to listOf("टेलीग्राम", "telegram", "tg"),
        "snapchat" to listOf("स्नैपचैट", "स्नैप", "snapchat", "snap"),
        "twitter" to listOf("ट्विटर", "एक्स", "twitter", "x"),
        "spotify" to listOf("स्पॉटिफाई", "म्यूजिक", "गाना", "music", "spotify", "songs"),
        "netflix" to listOf("नेटफ्लिक्स", "netflix", "movie"),
        "hotstar" to listOf("हॉटस्टार", "डिज्नी", "hotstar", "disney"),
        "amazon" to listOf("अमेज़न", "अमेजन", "amazon", "shopping"),
        "flipkart" to listOf("फ्लिपकार्ट", "flipkart"),
        "drive" to listOf("ड्राइव", "गूगल ड्राइव", "drive", "google drive"),
        "docs" to listOf("डॉक्स", "docs", "google docs"),
        "sheets" to listOf("शीट्स", "sheets", "google sheets", "एक्सेल", "excel"),
        "files" to listOf("फाइलें", "फ़ाइलें", "फाइल मैनेजर", "files", "my files", "file manager")
    )

    /**
     * Evaluates if a given application name, package name, or user query refers to a banking/financial app.
     * Returns true if it is a banking app, false otherwise.
     */
    fun isBankingApp(appName: String, packageName: String = ""): Boolean {
        val lowerPkg = packageName.lowercase().trim()
        val lowerName = appName.lowercase().trim()

        // 1. Direct check against verified banking package signatures
        if (lowerPkg.isNotBlank() && bankingPackageSignatures.any { lowerPkg.contains(it) || it.contains(lowerPkg) }) {
            return true
        }

        // 2. Keyword substring check on package name
        if (lowerPkg.isNotBlank()) {
            val pkgTokens = lowerPkg.split(".", "_", "-")
            if (pkgTokens.any { token ->
                    token in listOf("bank", "banking", "upi", "paytm", "phonepe", "gpay", "wallet", "paisa", "rupay", "money", "loan", "card", "invest", "zerodha", "groww")
                }) {
                return true
            }
        }

        // 3. Keyword check on app label / target query
        for (kw in bankingKeywords) {
            if (lowerName == kw) return true
            if (lowerName.contains(" $kw ") || lowerName.startsWith("$kw ") || lowerName.endsWith(" $kw")) {
                return true
            }
            if (lowerName.contains(kw) && (kw.length >= 4 || kw in listOf("sbi", "pnb", "bob", "rbl", "uco", "boi", "fi", "atm"))) {
                return true
            }
        }

        return false
    }

    /**
     * Checks if a voice/text command target is asking for a banking/finance app
     */
    fun isBankingCommand(command: String): Boolean {
        val clean = command.lowercase().trim()
        return bankingKeywords.any { kw ->
            clean == kw || clean.contains(" $kw ") || clean.startsWith("$kw ") || clean.endsWith(" $kw") ||
                    clean.contains(kw)
        }
    }

    /**
     * Checks if the user is discussing apps in general or asking for information
     * rather than requesting to open or unlock them.
     */
    fun isGeneralAppDiscussion(text: String): Boolean {
        val lower = text.lowercase().trim()
        val discussionKeywords = listOf(
            "जानकारी", "के बारे में", "क्या है", "कैसे", "बताओ", "समझाओ", "सिखाओ", "विवरण",
            "फीचर्स", "फायदे", "सुरक्षा", "टिप्स", "मार्गदर्शन", "रिव्यू", "तुलना", "अंतर",
            "कैसा है", "सुरक्षित है", "काम करता है", "नियम", "उपयोग",
            "info", "information", "about", "what is", "how to", "details", "explain", "features", "security", "compare", "difference"
        )
        return discussionKeywords.any { lower.contains(it) }
    }

    /**
     * Checks if a command is asking for INFORMATION about a banking/finance app
     * rather than requesting to open or launch it.
     */
    fun isBankingInfoQuery(command: String): Boolean {
        val clean = command.lowercase().trim()
        val isBanking = isBankingCommand(clean) || isBankingApp(clean)
        if (!isBanking) return false
        val explicitOpen = clean.contains("खोलो") || clean.contains("ओपन") || clean.contains("open") || clean.contains("launch") || clean.contains("अनलॉक") || clean.contains("unlock")
        return !explicitOpen || isGeneralAppDiscussion(clean)
    }

    /**
     * Checks if the user command or app target specifically asks for YouTube
     */
    fun isYouTubeRequest(target: String): Boolean {
        val clean = target.lowercase().trim()
        val tokens = listOf("youtube", "यूट्यूब", "यू ट्यूब", "यू-ट्यूब", "yt", "युटुब", "यूटुब")
        return tokens.any { clean == it || clean.contains(it) }
    }

    /**
     * Standard explanation message when banking apps are restricted from opening.
     * Clarifies that information is provided, but apps are never launched directly.
     */
    fun getBankingRestrictionMessage(appName: String): String {
        val cleanName = if (appName.isNotBlank()) " ('$appName')" else ""
        return "टेंशन नहीं लेने का भाई! मैं बैंकिंग और यूपीआई ऐप्स के बारे में सब कुछ जानती हूँ (फीचर्स, सुरक्षा, उपयोग, नियम), लेकिन सुरक्षा कारणों से मैं किसी भी बैंकिंग या यूपीआई ऐप$cleanName को सीधे खोल नहीं सकती। तुम्हारी वित्तीय सुरक्षा मेरी पहली ज़िम्मेदारी है! तुम्हें जो भी जानकारी या गाइडेंस चाहिए, बेझिझक पूछो, मैं पूरी और सटीक जानकारी दूँगी।"
    }

    /**
     * Query all installed launchable applications on the device,
     * FILTERING OUT and excluding all banking and financial apps as instructed by the user.
     */
    fun getInstalledApps(context: Context, excludeBanking: Boolean = true): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val appsMap = mutableMapOf<String, InstalledApp>()

        // 1. Resolve Activities with LAUNCHER category
        try {
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            for (resolveInfo in resolveInfos) {
                try {
                    val appName = resolveInfo.loadLabel(pm).toString()
                    val packageName = resolveInfo.activityInfo.packageName
                    val icon = resolveInfo.loadIcon(pm)

                    // Banking filter enforcement
                    if (!excludeBanking || !isBankingApp(appName, packageName)) {
                        appsMap[packageName] = InstalledApp(name = appName, packageName = packageName, icon = icon)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading resolve info", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying launcher intents", e)
        }

        // 2. Also check installed applications that have a launch intent
        try {
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (appInfo in installedApps) {
                if (!appsMap.containsKey(appInfo.packageName)) {
                    val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                    if (launchIntent != null) {
                        try {
                            val name = pm.getApplicationLabel(appInfo).toString()
                            if (!excludeBanking || !isBankingApp(name, appInfo.packageName)) {
                                val icon = pm.getApplicationIcon(appInfo)
                                appsMap[appInfo.packageName] = InstalledApp(name = name, packageName = appInfo.packageName, icon = icon)
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking installed applications", e)
        }

        return appsMap.values.sortedBy { it.name.lowercase() }
    }

    /**
     * Check if speech or text is requesting to open an app.
     * General discussion about banking or other apps is strictly informational and NEVER interpreted as a command to open or unlock.
     */
    fun isAppLaunchRequest(text: String): Boolean {
        val lower = text.lowercase().trim()
        if (isBankingInfoQuery(lower) || isGeneralAppDiscussion(lower)) return false

        val explicitActionVerbs = listOf(
            "खोलो", "खोलिए", "खोल दो", "खोलना", "ओपन करो", "ओपन कीजिए", "ओपन कर दो",
            "चालू करो", "चालू कर दो", "स्टार्ट करो", "चलाओ", "प्ले करो", "लॉन्च करो",
            "open ", "launch ", "start ", "run "
        )
        val hasExplicitVerb = explicitActionVerbs.any { lower.contains(it) } || lower.endsWith("open") || lower.endsWith("खोलो") || lower.endsWith("ओपन")
        if (!hasExplicitVerb) {
            return false
        }

        return matchesKnownApp(lower)
    }

    /**
     * Checks if the text matches any known common app alias or canonical name
     */
    fun matchesKnownApp(query: String): Boolean {
        val clean = extractTargetAppName(query)
        if (clean.isBlank()) return false
        val lower = clean.lowercase()

        for ((canonicalKey, aliases) in commonAppAliases) {
            if (lower == canonicalKey || lower.contains(canonicalKey) || canonicalKey.contains(lower)) {
                return true
            }
            if (aliases.any { lower == it || lower.contains(it) || it.contains(lower) }) {
                return true
            }
        }
        return false
    }

    /**
     * Extracts the target app name from user command
     */
    fun extractTargetAppName(command: String): String {
        var clean = command.lowercase().trim()
            .replace("!", "").replace("?", "").replace(".", "").replace(",", "")

        val removePhrases = listOf(
            // Arya wake words & greetings
            "आरिया", "आर्या", "आर्य", "aria", "arya",
            "hey arya", "hey aria", "ok arya", "ok aria",
            "हे आर्या", "हे आरिया", "ओके आर्या", "ओके आरिया",
            "सुनो आर्या", "सुनो आरिया", "नमस्ते आर्या", "नमस्ते आरिया",
            "हाय आर्या", "हाय आरिया", "हेलो आर्या", "hello arya",

            // Directives & filler requests
            "सीधे एंड्रॉइड सिस्टम इंटेंट्स का उपयोग करके", "एंड्रॉइड सिस्टम इंटेंट्स",
            "सिस्टम इंटेंट", "वेबसाइट लिंक न लें", "वेब सर्च न लें",
            "प्लीज", "please", "जरा", "कृपया", "जल्दी", "तुरंत", "can you",

            // App nouns
            "ऐप को", "ऐप", "एप को", "एप", "एप्प को", "एप्प", "app", "application", "एप्लिकेशन", "सॉफ्टवेयर",

            // Action verbs
            "खोलो", "खोलिए", "खोल दो", "खोल", "खोलना", "ओपन करो", "ओपन कीजिए", "ओपन कर दो", "ओपन",
            "चालू करो", "चालू कर दो", "चालू", "स्टार्ट करो", "स्टार्ट", "चलाओ", "प्ले करो", "दिखाओ", "लॉन्च करो", "लॉन्च",
            "open", "launch", "start", "run", "play", "to", "the"
        )

        for (phrase in removePhrases) {
            clean = clean.replace(phrase, " ")
        }
        return clean.trim().replace("\\s+".toRegex(), " ")
    }

    // Direct package name mappings for popular Android applications
    private val knownAppPackages = mapOf(
        "youtube" to listOf("com.google.android.youtube"),
        "whatsapp" to listOf("com.whatsapp", "com.whatsapp.w4b"),
        "chrome" to listOf("com.android.chrome"),
        "instagram" to listOf("com.instagram.android"),
        "facebook" to listOf("com.facebook.katana"),
        "twitter" to listOf("com.twitter.android"),
        "telegram" to listOf("org.telegram.messenger"),
        "snapchat" to listOf("com.snapchat.android"),
        "spotify" to listOf("com.spotify.music"),
        "netflix" to listOf("com.netflix.mediaclient"),
        "hotstar" to listOf("in.startv.hotstar"),
        "gmail" to listOf("com.google.android.gm"),
        "maps" to listOf("com.google.android.apps.maps"),
        "photos" to listOf("com.google.android.apps.photos"),
        "drive" to listOf("com.google.android.apps.docs"),
        "keep" to listOf("com.google.android.keep"),
        "notes" to listOf("com.google.android.keep", "com.samsung.android.app.notes"),
        "sheets" to listOf("com.google.android.apps.docs.editors.sheets"),
        "docs" to listOf("com.google.android.apps.docs.editors.docs"),
        "files" to listOf("com.google.android.apps.nbu.files", "com.google.android.documentsui", "com.sec.android.app.myfiles"),
        "play store" to listOf("com.android.vending"),
        "calculator" to listOf("com.google.android.calculator", "com.android.calculator2"),
        "clock" to listOf("com.google.android.deskclock", "com.android.deskclock"),
        "calendar" to listOf("com.google.android.calendar"),
        "amazon" to listOf("in.amazon.mShop.android.shopping"),
        "flipkart" to listOf("com.flipkart.android"),
        "swiggy" to listOf("in.swiggy.android"),
        "zomato" to listOf("com.application.zomato"),
        "uber" to listOf("com.ubercab"),
        "ola" to listOf("com.olacabs.customer"),
        "truecaller" to listOf("com.truecaller")
    )

    /**
     * Finds and launches the requested application directly using Android System Intents.
     * STRICT USER RULES ENFORCED:
     * 1. Direct Android System Intents exclusively (Launch Intent, Category Intent, Media/Settings Intent).
     * 2. NEVER resort to website links, web URLs, or web search queries under any circumstances.
     * 3. Banking & financial apps remain 100% strictly excluded and restricted.
     */
    fun launchAppByVoiceCommand(context: Context, command: String): AppLaunchResult {
        val targetName = extractTargetAppName(command)
        if (targetName.isBlank()) {
            return AppLaunchResult.Error("कृपया ऐप का नाम बताएं जिसे आप सीधे सिस्टम इंटेंट से खोलना चाहते हैं (जैसे: 'यूट्यूब खोलो' या 'कैमरा खोलो')।")
        }

        // STRICT BANKING EXCLUSION CHECK
        if (isBankingCommand(targetName) || isBankingApp(targetName)) {
            val explanation = getBankingRestrictionMessage(targetName)
            return AppLaunchResult.BankingRestricted(appName = targetName, packageName = "", explanation = explanation)
        }

        val pm = context.packageManager
        // Query installed apps (banking excluded from access list)
        val installedApps = getInstalledApps(context, excludeBanking = true)

        // STRICT DIRECT RESOLUTION FOR YOUTUBE (ALWAYS LAUNCH YOUTUBE APP, NEVER GOOGLE TV)
        if (isYouTubeRequest(targetName) || isYouTubeRequest(command)) {
            val ytPackages = listOf(
                "com.google.android.youtube",
                "com.google.android.apps.youtube.mango",
                "com.google.android.youtube.tv"
            )
            for (pkg in ytPackages) {
                try {
                    val launchIntent = pm.getLaunchIntentForPackage(pkg)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        return AppLaunchResult.Success("YouTube", pkg)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching YouTube package $pkg", e)
                }
            }

            // Search installed apps specifically for YouTube, strictly excluding Google TV / videos
            val ytInstalled = installedApps.firstOrNull { app ->
                val p = app.packageName.lowercase()
                val n = app.name.lowercase()
                !p.contains("videos") && !n.contains("google tv") && !n.contains("tv") &&
                        (p.contains("youtube") || n.contains("youtube") || n.contains("यूट्यूब"))
            }
            if (ytInstalled != null) {
                try {
                    val launchIntent = pm.getLaunchIntentForPackage(ytInstalled.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        return AppLaunchResult.Success(ytInstalled.name, ytInstalled.packageName)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching YouTube installed app", e)
                }
            }

            // Attempt direct launch via YouTube intent without ever falling back to Google TV
            try {
                val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:")).apply {
                    setPackage("com.google.android.youtube")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(ytIntent)
                return AppLaunchResult.Success("YouTube", "com.google.android.youtube")
            } catch (_: Exception) {}

            return AppLaunchResult.NotFound(
                query = "YouTube",
                suggestedPlayStoreUrl = ""
            )
        }

        // 1. Exact match on installed app name
        var matchedApp = installedApps.firstOrNull { app ->
            app.name.equals(targetName, ignoreCase = true)
        }

        // 2. Contains match on installed app name
        if (matchedApp == null) {
            matchedApp = installedApps.firstOrNull { app ->
                val appLabel = app.name.lowercase()
                appLabel.contains(targetName) || targetName.contains(appLabel)
            }
        }

        // 3. Match via common aliases (e.g. "यूट्यूब" -> YouTube, "व्हाट्सएप" -> WhatsApp)
        if (matchedApp == null) {
            matchedApp = findByAliases(installedApps, targetName)
        }

        // 4. Match against package name containing targetName
        if (matchedApp == null) {
            matchedApp = installedApps.firstOrNull { app ->
                app.packageName.lowercase().contains(targetName)
            }
        }

        // 5. Check direct known package signatures
        if (matchedApp == null) {
            for ((key, packages) in knownAppPackages) {
                if (targetName.contains(key) || key.contains(targetName) || isAliasMatchForKey(key, targetName)) {
                    for (pkg in packages) {
                        try {
                            val launchIntent = pm.getLaunchIntentForPackage(pkg)
                            if (launchIntent != null) {
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launchIntent)
                                val appLabel = try {
                                    val appInfo = pm.getApplicationInfo(pkg, 0)
                                    pm.getApplicationLabel(appInfo).toString()
                                } catch (_: Exception) {
                                    key.replaceFirstChar { it.uppercase() }
                                }
                                return AppLaunchResult.Success(appLabel, pkg)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error launching known package $pkg", e)
                        }
                    }
                }
            }
        }

        if (matchedApp != null) {
            // Verify matched app is not banking
            if (isBankingApp(matchedApp.name, matchedApp.packageName)) {
                return AppLaunchResult.BankingRestricted(
                    appName = matchedApp.name,
                    packageName = matchedApp.packageName,
                    explanation = getBankingRestrictionMessage(matchedApp.name)
                )
            }

            try {
                val launchIntent = pm.getLaunchIntentForPackage(matchedApp.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    return AppLaunchResult.Success(matchedApp.name, matchedApp.packageName)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting launch intent for ${matchedApp.packageName}", e)
            }
        }

        // 6. Direct Android System Intents Fallback (Camera, Settings, Dialer, Alarms, Maps, Browser, Calculator, Calendar, Contacts, Gallery, Email, Messaging, Music, Files)
        val systemIntentHandled = handleSystemIntentFallback(context, targetName)
        if (systemIntentHandled != null) {
            return AppLaunchResult.Success(systemIntentHandled, "android.system.intent")
        }

        // 7. App not found on device:
        // STRICT USER DIRECTIVE: Under NO circumstances resort to website links, web URLs, or web search.
        // Report directly that the app is not installed, with zero web redirection.
        return AppLaunchResult.NotFound(
            query = targetName,
            suggestedPlayStoreUrl = ""
        )
    }

    private fun isAliasMatchForKey(canonicalKey: String, query: String): Boolean {
        val lowerQuery = query.lowercase().trim()
        val aliases = commonAppAliases[canonicalKey] ?: return false
        return lowerQuery == canonicalKey || aliases.any { alias ->
            lowerQuery.contains(alias) || alias.contains(lowerQuery)
        }
    }

    private fun findByAliases(installedApps: List<InstalledApp>, query: String): InstalledApp? {
        val lowerQuery = query.lowercase().trim()

        for ((canonicalKey, aliases) in commonAppAliases) {
            val isMatch = lowerQuery == canonicalKey || aliases.any { alias ->
                lowerQuery.contains(alias) || alias.contains(lowerQuery)
            }

            if (isMatch) {
                val isYoutubeTarget = canonicalKey == "youtube"
                // Find installed app matching canonicalKey or any alias
                val found = installedApps.firstOrNull { app ->
                    val nameLower = app.name.lowercase()
                    val pkgLower = app.packageName.lowercase()
                    if (isYoutubeTarget && (pkgLower.contains("videos") || nameLower.contains("google tv") || nameLower.contains("tv"))) {
                        false
                    } else {
                        nameLower.contains(canonicalKey) || pkgLower.contains(canonicalKey) ||
                                aliases.any { alias -> nameLower.contains(alias) || pkgLower.contains(alias) }
                    }
                }
                if (found != null) return found
            }
        }
        return null
    }

    /**
     * Launches Android system components exclusively using Android System Intents.
     * Strictly avoids opening web links or web search engines.
     */
    private fun handleSystemIntentFallback(context: Context, query: String): String? {
        val lower = query.lowercase().trim()
        return try {
            when {
                // 1. Camera Intent
                lower.contains("camera") || lower.contains("कैमरा") || lower.contains("फोटो") || lower.contains("कैमरे") -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "कैमरा (Camera System Intent)"
                }

                // 2. Settings Intent
                lower.contains("setting") || lower.contains("सेटिंग") || lower.contains("सेटिंग्स") -> {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "सेटिंग्स (Settings System Intent)"
                }

                // 3. Dialer / Phone Call Intent
                lower.contains("call") || lower.contains("कॉल") || lower.contains("dial") || lower.contains("फोन") || lower.contains("डायलर") -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "फोन डायलर (Dialer System Intent)"
                }

                // 4. Clock & Alarms Intent
                lower.contains("alarm") || lower.contains("अलार्म") || lower.contains("घड़ी") || lower.contains("clock") || lower.contains("timer") -> {
                    val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "अलार्म व घड़ी (Alarm System Intent)"
                }

                // 5. Maps / Navigation Intent (Opens native maps app, not a browser)
                lower.contains("map") || lower.contains("मैप्स") || lower.contains("नक्शा") || lower.contains("नेविगेशन") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MAPS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(intent)
                        "मैप्स (Maps System Intent)"
                    } catch (_: Exception) {
                        val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(geoIntent)
                        "मैप्स (Maps Intent)"
                    }
                }

                // 6. Web Browser App (Opens default browser app itself, NOT a search webpage)
                lower.contains("browser") || lower.contains("ब्राउज़र") || lower.contains("chrome") || lower.contains("क्रोम") || lower.contains("इंटरनेट") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "ब्राउज़र ऐप (Browser System Intent)"
                }

                // 7. Calculator Intent
                lower.contains("calculator") || lower.contains("कैलकुलेटर") || lower.contains("calc") || lower.contains("हिसाब") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALCULATOR).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "कैलकुलेटर (Calculator System Intent)"
                }

                // 8. Calendar Intent
                lower.contains("calendar") || lower.contains("कैलेंडर") || lower.contains("पंचांग") || lower.contains("तारीख") -> {
                    try {
                        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CALENDAR).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        "कैलेंडर (Calendar System Intent)"
                    } catch (_: Exception) {
                        val calIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("content://com.android.calendar/time")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(calIntent)
                        "कैलेंडर (Calendar Intent)"
                    }
                }

                // 9. Contacts Intent
                lower.contains("contacts") || lower.contains("कॉन्टैक्ट्स") || lower.contains("संपर्क") || lower.contains("फोन बुक") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_CONTACTS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "कॉन्टैक्ट्स (Contacts System Intent)"
                }

                // 10. Gallery / Photos Intent
                lower.contains("gallery") || lower.contains("गैलरी") || lower.contains("photos") || lower.contains("फ़ोटो") || lower.contains("तस्वीरें") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "गैलरी व फ़ोटो (Gallery System Intent)"
                }

                // 11. Email / Gmail Intent
                lower.contains("email") || lower.contains("ईमेल") || lower.contains("gmail") || lower.contains("जीमेल") || lower.contains("mail") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "ईमेल (Email System Intent)"
                }

                // 12. Messages / SMS Intent
                lower.contains("message") || lower.contains("मैसेज") || lower.contains("संदेश") || lower.contains("sms") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MESSAGING).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "मैसेजिंग (Messaging System Intent)"
                }

                // 13. Music / Audio Player Intent
                lower.contains("music") || lower.contains("म्यूजिक") || lower.contains("गाने") || lower.contains("गीत") || lower.contains("ऑडियो") -> {
                    try {
                        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        "म्यूजिक प्लेयर (Music System Intent)"
                    } catch (_: Exception) {
                        val musicIntent = Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(musicIntent)
                        "म्यूजिक प्लेयर (Media Intent)"
                    }
                }

                // 14. Files / File Manager Intent
                lower.contains("files") || lower.contains("फ़ाइलें") || lower.contains("फाइलें") || lower.contains("file manager") || lower.contains("फाइल मैनेजर") -> {
                    val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_FILES).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "फाइल मैनेजर (Files System Intent)"
                }

                // 15. Audio Recorder Intent
                lower.contains("recorder") || lower.contains("रिकॉर्डर") || lower.contains("रिकॉर्डिंग") || lower.contains("वॉइस रिकॉर्ड") -> {
                    val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "ऑडियो रिकॉर्डर (Sound Recorder Intent)"
                }

                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing system intent for $query", e)
            null
        }
    }
}
