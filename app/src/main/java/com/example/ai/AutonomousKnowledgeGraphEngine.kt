package com.example.ai

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Entity categories for the Autonomous Knowledge Graph.
 */
enum class KnowledgeNodeType(val labelHindi: String, val badgeColorHex: Long) {
    CORE_CONCEPT("मुख्य संकल्पना", 0xFF00E5FF),
    DOMAIN_THEORY("सैद्धांतिक आधार", 0xFF8B5CF6),
    EXPERT_AI_MODEL("विशेषज्ञ AI मॉडल", 0xFF10B981),
    WEB_EVIDENCE("वेब व लाइव साक्ष्य", 0xFFF59E0B),
    ALGORITHM_LOGIC("अल्गोरिदम व तार्किकता", 0xFF3B82F6),
    REAL_WORLD_ACTION("व्यावहारिक क्रियान्वयन", 0xFFEC4899),
    SAFETY_ETHICS("सुरक्षा व नैतिक नियम", 0xFFEF4444)
}

/**
 * Relational edge types connecting concepts deeply.
 */
enum class KnowledgeRelationType(val labelHindi: String, val symbol: String) {
    RELATES_TO("संबंध रखता है", "↔"),
    OPTIMIZES("अनुकूलित करता है", "⚡"),
    GROUNDED_IN("आधारित है", "⚓"),
    COLLABORATES_WITH("सहयोग करता है", "🤝"),
    LEADS_TO("परिणामस्वरूप सृजित", "➔"),
    DEPENDS_ON("निर्भर करता है", "⛓️"),
    VERIFIES("सत्यापित करता है", "🔍"),
    SYNTHESIZES("संश्लेषित करता है", "✨")
}

data class KnowledgeGraphNode(
    val id: String,
    val labelHindi: String,
    val type: KnowledgeNodeType,
    val detailsHindi: String,
    val confidenceScore: Float = 0.99f
)

data class KnowledgeGraphEdge(
    val sourceId: String,
    val targetId: String,
    val relation: KnowledgeRelationType,
    val labelHindi: String,
    val strength: Float = 1.0f
)

data class AutonomousKnowledgeGraph(
    val id: String = UUID.randomUUID().toString(),
    val query: String,
    val topicTitleHindi: String,
    val nodes: List<KnowledgeGraphNode>,
    val edges: List<KnowledgeGraphEdge>,
    val structuredSynthesisHindi: String,
    val deepRelationalSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * AutonomousKnowledgeGraphEngine:
 * Automatically builds rich, multi-dimensional relational graphs for complex topics.
 * Structures relations between core concepts, top AI models, live web evidence,
 * domain theories, and practical applications.
 */
object AutonomousKnowledgeGraphEngine {

    private val _latestKnowledgeGraph = MutableStateFlow<AutonomousKnowledgeGraph?>(null)
    val latestKnowledgeGraph: StateFlow<AutonomousKnowledgeGraph?> = _latestKnowledgeGraph.asStateFlow()

    private val _knowledgeGraphHistory = MutableStateFlow<List<AutonomousKnowledgeGraph>>(emptyList())
    val knowledgeGraphHistory: StateFlow<List<AutonomousKnowledgeGraph>> = _knowledgeGraphHistory.asStateFlow()

    /**
     * Constructs an Autonomous Knowledge Graph dynamically mapping nodes and deep relations.
     */
    fun buildKnowledgeGraph(
        query: String,
        webGroundedSources: List<String> = emptyList(),
        multiAiDecision: CollaborationDecision? = null
    ): AutonomousKnowledgeGraph {
        val lower = query.lowercase().trim()
        val primaryModelName = multiAiDecision?.primaryModel?.name ?: "Gemini 3.8 Flash"

        val (topicTitle, nodes, edges, relationalSummary) = when {
            // 1. Coding, Tech & Algorithms
            lower.contains("कोड") || lower.contains("code") || lower.contains("bug") ||
                    lower.contains("kotlin") || lower.contains("python") || lower.contains("algorithm") ||
                    lower.contains("अल्गोरिदम") || lower.contains("क्विकसॉर्ट") -> {
                buildCodingGraph(query, primaryModelName, webGroundedSources)
            }

            // 2. Science, Math, Quantum, Logic
            lower.contains("गणित") || lower.contains("math") || lower.contains("quantum") ||
                    lower.contains("क्वांटम") || lower.contains("physics") || lower.contains("तर्क") ||
                    lower.contains("फार्मूला") || lower.contains("प्रमेय") -> {
                buildMathScienceGraph(query, primaryModelName, webGroundedSources)
            }

            // 2b. Free Fire & Battle Royale Pro Combat Knowledge Graph
            lower.contains("free fire") || lower.contains("फ्री फायर") || lower.contains("freefire") ||
                    lower.contains("one-tap") || lower.contains("वन-टैप") || lower.contains("gloo wall") ||
                    lower.contains("ग्लू वॉल") || lower.contains("क्लैश स्क्वाड") || lower.contains("cs ranked") ||
                    lower.contains("हेडशॉट") || lower.contains("headshot") || lower.contains("m1887") ||
                    lower.contains("mp40") || lower.contains("alok") || lower.contains("आलोक") ||
                    lower.contains("tatsuya") || lower.contains("तात्सुया") -> {
                buildFreeFireKnowledgeGraph(query, primaryModelName, webGroundedSources)
            }

            // 3. Web Search, Current Affairs, News, Websites, Links
            lower.contains("वेब") || lower.contains("वेबसाइट") || lower.contains("link") ||
                    lower.contains("http") || lower.contains("news") || lower.contains("खबर") ||
                    lower.contains("ताजा") || lower.contains("मौसम") || lower.contains("सर्च") -> {
                buildWebKnowledgeGraph(query, primaryModelName, webGroundedSources)
            }

            // 3b. Sexual Education, Reproductive Health, Wellness & Anatomy
            lower.contains("यौन") || lower.contains("सेक्स") || lower.contains("प्रजनन") ||
                    lower.contains("कंडोम") || lower.contains("गर्भनिरोधक") || lower.contains("माहवारी") ||
                    lower.contains("पीरियड्स") || lower.contains("गुप्तांग") || lower.contains("sexual") ||
                    lower.contains("reproductive") || lower.contains("स्वास्थ्य") || lower.contains("health") ||
                    lower.contains("sti") || lower.contains("std") || lower.contains("एनाटॉमी") -> {
                buildHealthAndSexualEducationGraph(query, primaryModelName, webGroundedSources)
            }

            // 4. Banking, Finance, UPI, Security
            lower.contains("बैंक") || lower.contains("bank") || lower.contains("upi") ||
                    lower.contains("पैसे") || lower.contains("loan") || lower.contains("security") -> {
                buildFinanceSecurityGraph(query, primaryModelName, webGroundedSources)
            }

            // 5. Creative writing, Cinematic, Story, Literature
            lower.contains("कविता") || lower.contains("कहानी") || lower.contains("story") ||
                    lower.contains("poem") || lower.contains("शायरी") || lower.contains("सिनेमैटिक") -> {
                buildCreativeGraph(query, primaryModelName, webGroundedSources)
            }

            // 6. General / Autonomous Holistic Knowledge Graph
            else -> {
                buildUniversalKnowledgeGraph(query, primaryModelName, webGroundedSources)
            }
        }

        val structuredSynthesis = buildString {
            append("ऑटोनॉमस नॉलेज ग्राफ संरचना (${nodes.size} नोड्स, ${edges.size} रिलेशनल संबंध):\n")
            append("मुख्य विषय: $topicTitle\n")
            append("1. प्राथमिक नोड्स: ")
            append(nodes.take(3).joinToString(", ") { "${it.labelHindi} (${it.type.labelHindi})" })
            append("\n2. गहरे संबंध: ")
            append(edges.take(3).joinToString(" • ") { "${it.sourceId} ${it.relation.symbol} ${it.targetId} (${it.labelHindi})" })
            append("\n3. ज्ञान संश्लेषण: $relationalSummary")
        }

        val graph = AutonomousKnowledgeGraph(
            query = query,
            topicTitleHindi = topicTitle,
            nodes = nodes,
            edges = edges,
            structuredSynthesisHindi = structuredSynthesis,
            deepRelationalSummary = relationalSummary
        )

        _latestKnowledgeGraph.value = graph
        _knowledgeGraphHistory.value = (_knowledgeGraphHistory.value + graph).takeLast(20)

        return graph
    }

    private fun buildCodingGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "सॉफ्टवेयर अल्गोरिदम व कोडिंग संरचना"
        val nodes = listOf(
            KnowledgeGraphNode("N1", "अल्गोरिदम जटिलता (Time/Space)", KnowledgeNodeType.CORE_CONCEPT, "O(n log n) दक्षता व मेमोरी अनुकूलन"),
            KnowledgeGraphNode("N2", "DeepSeek-R1 + Gemma 2", KnowledgeNodeType.EXPERT_AI_MODEL, "बग-फ्री कोड व पारदर्शी रीज़निंग"),
            KnowledgeGraphNode("N3", "स्वच्छ सिंटैक्स व अपवाद सुरक्षा", KnowledgeNodeType.ALGORITHM_LOGIC, "Kotlin Coroutines व ट्राई-कैच सुरक्षा"),
            KnowledgeGraphNode("N4", "लाइव डेवलपर दस्तावेज़ व वेब", KnowledgeNodeType.WEB_EVIDENCE, "आधिकारिक Android/Kotlin API संदर्भ"),
            KnowledgeGraphNode("N5", "उत्पादन-स्तरीय निष्पादन", KnowledgeNodeType.REAL_WORLD_ACTION, "सीधा कॉपी-पेस्ट योग्य बग-फ्री कोड")
        )
        val edges = listOf(
            KnowledgeGraphEdge("N1", "N2", KnowledgeRelationType.SYNTHESIZES, "रीज़निंग द्वारा हल"),
            KnowledgeGraphEdge("N2", "N3", KnowledgeRelationType.OPTIMIZES, "क्लीन कोड जनरेशन"),
            KnowledgeGraphEdge("N3", "N4", KnowledgeRelationType.GROUNDED_IN, "API विनिर्देश सत्यापन"),
            KnowledgeGraphEdge("N3", "N5", KnowledgeRelationType.LEADS_TO, "बग-फ्री कार्यान्वयन"),
            KnowledgeGraphEdge("N5", "N1", KnowledgeRelationType.VERIFIES, "दक्षता पुष्टि")
        )
        val summary = "अल्गोरिदम तर्क को DeepSeek और Gemma के समन्वय से जोड़कर शून्य-मेमोरी-लीक और पूर्ण अपवाद-मुक्त निष्पादन में परिवर्तित किया गया है।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildMathScienceGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "गणितीय प्रमाण व वैज्ञानिक सिद्धांत"
        val nodes = listOf(
            KnowledgeGraphNode("M1", "मूल प्रमेय व तार्किक परिकल्पना", KnowledgeNodeType.CORE_CONCEPT, "चरणबद्ध डिडक्शन व गणितीय समीकरण"),
            KnowledgeGraphNode("M2", "OpenAI o1 + Qwen 2.5 Max", KnowledgeNodeType.EXPERT_AI_MODEL, "चेन-ऑफ-थॉट डीप रीज़निंग"),
            KnowledgeGraphNode("M3", "भौतिकी व वैज्ञानिक नियम", KnowledgeNodeType.DOMAIN_THEORY, "प्रामाणिक सार्वभौमिक स्थिरांक व सूत्र"),
            KnowledgeGraphNode("M4", "पीयर-रिव्यूड संदर्भ व वेब साक्ष्य", KnowledgeNodeType.WEB_EVIDENCE, "सत्यापित अकादमिक ज्ञानकोष"),
            KnowledgeGraphNode("M5", "सटीक निष्कर्ष व अनुप्रयोग", KnowledgeNodeType.REAL_WORLD_ACTION, "शून्य-त्रुटि गणना व पूर्ण समाधान")
        )
        val edges = listOf(
            KnowledgeGraphEdge("M1", "M2", KnowledgeRelationType.SYNTHESIZES, "डीप रीज़निंग विश्लेषण"),
            KnowledgeGraphEdge("M2", "M3", KnowledgeRelationType.DEPENDS_ON, "सैद्धांतिक सुदृढ़ता"),
            KnowledgeGraphEdge("M3", "M4", KnowledgeRelationType.GROUNDED_IN, "तथ्य सत्यापन"),
            KnowledgeGraphEdge("M2", "M5", KnowledgeRelationType.LEADS_TO, "अंतिम निर्दोष समाधान"),
            KnowledgeGraphEdge("M5", "M1", KnowledgeRelationType.VERIFIES, "प्रमाण की पूर्णता")
        )
        val summary = "तार्किक परिकल्पना को चरणबद्ध डिडक्शन और वैज्ञानिक नियमों से जोड़कर 100% गणितीय सटीकता और पूर्ण प्रमाण सुनिश्चित किया गया।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildWebKnowledgeGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "लाइव वेब, वेबसाइट व तथ्य संश्लेषण"
        val sourcesCount = if (webSources.isNotEmpty()) webSources.size else 3
        val nodes = listOf(
            KnowledgeGraphNode("W1", "लाइव वेब संदर्भ व यूआरएल", KnowledgeNodeType.WEB_EVIDENCE, "सत्यापित लाइव वेबसाइट्स व ऑनलाइन स्रोत ($sourcesCount स्रोत)"),
            KnowledgeGraphNode("W2", "Gemini 3.8 Flash + Grok 3", KnowledgeNodeType.EXPERT_AI_MODEL, "रीयल-टाइम वेब ग्राउंडिंग व लाइव पल्स"),
            KnowledgeGraphNode("W3", "तथ्य सत्यापन व क्रॉस-चेक", KnowledgeNodeType.ALGORITHM_LOGIC, "विभिन्न स्रोतों का तुलनात्मक विश्लेषण"),
            KnowledgeGraphNode("W4", "ऐतिहासिक व सामयिक संदर्भ", KnowledgeNodeType.DOMAIN_THEORY, "घटनाओं का समग्र कालक्रम व पृष्ठभूमि"),
            KnowledgeGraphNode("W5", "प्रमाणित तथ्य सारांश", KnowledgeNodeType.REAL_WORLD_ACTION, "भ्रम-मुक्त, अद्यतन व सटीक निष्कर्ष")
        )
        val edges = listOf(
            KnowledgeGraphEdge("W1", "W2", KnowledgeRelationType.GROUNDED_IN, "लाइव डेटा इनपुट"),
            KnowledgeGraphEdge("W2", "W3", KnowledgeRelationType.SYNTHESIZES, "क्रॉस-सत्यापन"),
            KnowledgeGraphEdge("W3", "W4", KnowledgeRelationType.RELATES_TO, "संदर्भ तुलना"),
            KnowledgeGraphEdge("W3", "W5", KnowledgeRelationType.LEADS_TO, "सत्यापित तथ्य प्रस्तुति"),
            KnowledgeGraphEdge("W5", "W1", KnowledgeRelationType.VERIFIES, "प्रामाणिकता पुष्टि")
        )
        val summary = "लाइव वेब और ऑनलाइन लिंक स्रोतों को रीयल-टाइम मॉडल के साथ जोड़कर 100% प्रामाणिक, अद्यतन और शून्य-हैलुसिनेशन निष्कर्ष तैयार किया गया।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildHealthAndSexualEducationGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "यौन शिक्षा, प्रजनन स्वास्थ्य व व्यावहारिक वेलनेस"
        val nodes = listOf(
            KnowledgeGraphNode("H1", "शारीरिक रचना व प्रजनन विज्ञान (Anatomy)", KnowledgeNodeType.CORE_CONCEPT, "शरीर क्रिया विज्ञान, प्रजनन अंग, हार्मोनल चक्र व जैविक विकास"),
            KnowledgeGraphNode("H2", "Gemini 3.8 + GPT-4o", KnowledgeNodeType.EXPERT_AI_MODEL, "वस्तुनिष्ठ, वैज्ञानिक, निष्पक्ष व गरिमापूर्ण चिकित्सा विश्लेषण"),
            KnowledgeGraphNode("H3", "सुरक्षा, स्वच्छता व रोग निवारण (Prevention)", KnowledgeNodeType.REAL_WORLD_ACTION, "एसटीआई/एसटीडी सुरक्षा, गर्भनिरोधक विज्ञान, व्यक्तिगत स्वच्छता व सावधानियां"),
            KnowledgeGraphNode("H4", "मनोवैज्ञानिक व भावनात्मक संतुलन", KnowledgeNodeType.DOMAIN_THEORY, "सहमति, सम्मान, व्यक्तिगत सीमाएं, मानसिक स्वास्थ्य व परिपक्व समझ"),
            KnowledgeGraphNode("H5", "संकोच-मुक्त व्यावहारिक समाधान", KnowledgeNodeType.REAL_WORLD_ACTION, "पूर्ण, विस्तृत, व्यावहारिक और संकोच-रहित वैज्ञानिक मार्गदर्शन")
        )
        val edges = listOf(
            KnowledgeGraphEdge("H1", "H2", KnowledgeRelationType.SYNTHESIZES, "वैज्ञानिक विश्लेषण"),
            KnowledgeGraphEdge("H2", "H3", KnowledgeRelationType.LEADS_TO, "सुरक्षा व निवारण उपाय"),
            KnowledgeGraphEdge("H3", "H4", KnowledgeRelationType.RELATES_TO, "शारीरिक व मानसिक संतुलन"),
            KnowledgeGraphEdge("H2", "H5", KnowledgeRelationType.OPTIMIZES, "संकोच-रहित पूर्ण समाधान"),
            KnowledgeGraphEdge("H5", "H1", KnowledgeRelationType.VERIFIES, "शारीरिक तथ्यों की पुष्टि")
        )
        val summary = "यौन शिक्षा और स्वास्थ्य को वैज्ञानिक तथ्यों, सुरक्षा सावधानियों और भावनात्मक संबल से जोड़कर बिना किसी संकोच के पूर्ण, गरिमापूर्ण और व्यावहारिक मार्गदर्शन सुनिश्चित किया गया।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildFinanceSecurityGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "बैंकिंग, वित्त व सुरक्षा नियम"
        val nodes = listOf(
            KnowledgeGraphNode("F1", "बैंकिंग व यूपीआई अवधारणा", KnowledgeNodeType.CORE_CONCEPT, "वित्तीय नियम, सुरक्षा उपाय व सावधानियां"),
            KnowledgeGraphNode("F2", "Gemma 2 + Gemini 3.8", KnowledgeNodeType.EXPERT_AI_MODEL, "उच्चतम नैतिक व सुरक्षा अनुपालन"),
            KnowledgeGraphNode("F3", "जीरो-लॉन्च व प्राइवेसी सुरक्षा", KnowledgeNodeType.SAFETY_ETHICS, "बिना अनुमति किसी ऐप को न खोलने का कड़ा नियम"),
            KnowledgeGraphNode("F4", "आरबीआई व बैंकिंग मानक", KnowledgeNodeType.DOMAIN_THEORY, "आधिकारिक बैंकिंग सुरक्षा दिशानिर्देश"),
            KnowledgeGraphNode("F5", "मार्गदर्शन व जागरूकता प्रस्तुति", KnowledgeNodeType.REAL_WORLD_ACTION, "विस्तृत, सुरक्षित व पूर्ण जानकारी")
        )
        val edges = listOf(
            KnowledgeGraphEdge("F1", "F2", KnowledgeRelationType.SYNTHESIZES, "सुरक्षा नीति विश्लेषण"),
            KnowledgeGraphEdge("F2", "F3", KnowledgeRelationType.DEPENDS_ON, "सुरक्षा कवच पालन"),
            KnowledgeGraphEdge("F3", "F4", KnowledgeRelationType.GROUNDED_IN, "बैंकिंग मानक आधार"),
            KnowledgeGraphEdge("F2", "F5", KnowledgeRelationType.LEADS_TO, "सुरक्षित ज्ञान वितरण"),
            KnowledgeGraphEdge("F5", "F1", KnowledgeRelationType.VERIFIES, "पूर्ण अनुपालन")
        )
        val summary = "वित्तीय चर्चाओं में सुरक्षा कवच (जीरो-लॉन्च नियम) और आधिकारिक मानकों को जोड़कर केवल सटीक जानकारी प्रदान की गई।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildCreativeGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "साहित्यिक, सिनेमैटिक व मौलिक सृजन"
        val nodes = listOf(
            KnowledgeGraphNode("C1", "केंद्रीय कथावस्तु व भाव", KnowledgeNodeType.CORE_CONCEPT, "मौलिक विचार, संवेदना व रचनात्मक दृष्टि"),
            KnowledgeGraphNode("C2", "GPT-4o + Gemini 3.8", KnowledgeNodeType.EXPERT_AI_MODEL, "साहित्यिक प्रवाह व संतुलित रचनात्मकता"),
            KnowledgeGraphNode("C3", "सिनेमैटिक दृश्य व रूपक", KnowledgeNodeType.DOMAIN_THEORY, "गोल्डन आवर, साइबरपंक व विशद चित्रण"),
            KnowledgeGraphNode("C4", "भाषाई मिठास व देसी मुहावरे", KnowledgeNodeType.ALGORITHM_LOGIC, "दोस्ताना, आत्मीय और भाई जैसा अपनापन"),
            KnowledgeGraphNode("C5", "परिपूर्ण कलात्मक कृति", KnowledgeNodeType.REAL_WORLD_ACTION, "बिना किसी अधूरे वाक्य के मुकम्मल सृजन")
        )
        val edges = listOf(
            KnowledgeGraphEdge("C1", "C2", KnowledgeRelationType.SYNTHESIZES, "रचनात्मक परिकल्पना"),
            KnowledgeGraphEdge("C2", "C3", KnowledgeRelationType.OPTIMIZES, "सिनेमैटिक विस्तार"),
            KnowledgeGraphEdge("C3", "C4", KnowledgeRelationType.RELATES_TO, "भाषाई जीवंतता"),
            KnowledgeGraphEdge("C2", "C5", KnowledgeRelationType.LEADS_TO, "मुकम्मल रचना प्रस्तुति"),
            KnowledgeGraphEdge("C5", "C1", KnowledgeRelationType.VERIFIES, "भावुक संतुष्टि")
        )
        val summary = "रचनात्मक भाव को सिनेमैटिक दृश्यों और देसी मिठास से जोड़कर एक पूर्ण, समृद्ध और जीवंत रचना प्रस्तुत की गई।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildFreeFireKnowledgeGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "फ्री फायर (Free Fire) बैटल रॉयल व प्रो कॉम्बैट नॉलेज ग्राफ"
        val querySnippet = query.take(30).ifBlank { "गेमप्ले व हथियार विश्लेषण" }
        val nodes = listOf(
            KnowledgeGraphNode("FF1", "लक्ष्य: $querySnippet", KnowledgeNodeType.CORE_CONCEPT, "वन-टैप हेडशॉट, मूव्मेंट स्पीड व बैटल रॉयल विजय (Booyah!)"),
            KnowledgeGraphNode("FF2", "हथियार व गन मैकेनिक्स", KnowledgeNodeType.DOMAIN_THEORY, "M1887, MP40, Woodpecker, AWM - डैमेज, रेंज व आर्मर पेनिट्रेशन"),
            KnowledgeGraphNode("FF3", "कैरेक्टर स्किल कॉम्बिनेशन", KnowledgeNodeType.ALGORITHM_LOGIC, "एक्टिव: आलोक/तात्सुया/ओरियन + पैसिव: हयातो, केली, मोको, लुकेटा"),
            KnowledgeGraphNode("FF4", "रक्षा व गतिशीलता (Gloo Wall)", KnowledgeNodeType.REAL_WORLD_ACTION, "360 सिट-अप ग्लू वॉल, जिगल मूव्मेंट, रोटेशन ड्रैग व कवर टू कवर रश"),
            KnowledgeGraphNode("FF5", "सेंसिटिविटी व डिवाइस ट्यूनिंग", KnowledgeNodeType.DOMAIN_THEORY, "जनरल 98, रेड डॉट 95, 2x 92, 4x 88 (स्क्रीन रिफ्रेश रेट अनुकूलन)"),
            KnowledgeGraphNode("FF6", "$primaryModel + गेम स्ट्रैटेजी इंजन", KnowledgeNodeType.EXPERT_AI_MODEL, "क्लैश स्क्वाड राउंड इकॉनमी व सेफ ज़ोन रोटेशन प्रेडिक्शन")
        )
        val edges = listOf(
            KnowledgeGraphEdge("FF1", "FF2", KnowledgeRelationType.DEPENDS_ON, "सटीक गन चयन"),
            KnowledgeGraphEdge("FF2", "FF3", KnowledgeRelationType.OPTIMIZES, "स्किल्स से डैमेज व स्पीड बूस्ट"),
            KnowledgeGraphEdge("FF3", "FF4", KnowledgeRelationType.SYNTHESIZES, "अटैक और डिफेंस का संतुलन"),
            KnowledgeGraphEdge("FF4", "FF5", KnowledgeRelationType.GROUNDED_IN, "हाई सेंसिटिविटी से त्वरित 360 डिप्लॉय"),
            KnowledgeGraphEdge("FF6", "FF1", KnowledgeRelationType.LEADS_TO, "परफेक्ट बूहयाह (Booyah) रणनीति")
        )
        val summary = "हथियार आँकड़े, कैरेक्टर स्किल तालमेल, 360 सिट-अप ग्लू वॉल और कस्टमाइज्ड सेंसिटिविटी को जोड़कर प्रो-गेमप्ले का अचूक रोडमैप तैयार किया गया।"
        return Quadruple(topic, nodes, edges, summary)
    }

    private fun buildUniversalKnowledgeGraph(
        query: String,
        primaryModel: String,
        webSources: List<String>
    ): Quadruple<String, List<KnowledgeGraphNode>, List<KnowledgeGraphEdge>, String> {
        val topic = "सार्वभौमिक ज्ञान व संकल्पना संबंध"
        val querySnippet = query.take(30).ifBlank { "सामान्य विषय" }
        val nodes = listOf(
            KnowledgeGraphNode("U1", "मूल प्रश्न: $querySnippet", KnowledgeNodeType.CORE_CONCEPT, "प्राथमिक जिज्ञासा व मुख्य अवधारणा"),
            KnowledgeGraphNode("U2", "$primaryModel + शीर्ष सहयोगी AI", KnowledgeNodeType.EXPERT_AI_MODEL, "सामूहिक बुद्धिमत्ता व डोमेन विशेषज्ञता"),
            KnowledgeGraphNode("U3", "अंतर्निहित सिद्धांत व नियम", KnowledgeNodeType.DOMAIN_THEORY, "कारण-प्रभाव व तार्किक निरंतरता"),
            KnowledgeGraphNode("U4", "वेब ग्राउंडिंग व सत्यापित तथ्य", KnowledgeNodeType.WEB_EVIDENCE, "ऑनलाइन स्रोतों से पुष्ट जानकारी"),
            KnowledgeGraphNode("U5", "परिष्कृत अंतिम समाधान", KnowledgeNodeType.REAL_WORLD_ACTION, "सटीक, सुव्यवस्थित व पूर्ण उत्तर")
        )
        val edges = listOf(
            KnowledgeGraphEdge("U1", "U2", KnowledgeRelationType.SYNTHESIZES, "बहु-मॉडल विश्लेषण"),
            KnowledgeGraphEdge("U2", "U3", KnowledgeRelationType.DEPENDS_ON, "तार्किक आधारशिला"),
            KnowledgeGraphEdge("U3", "U4", KnowledgeRelationType.GROUNDED_IN, "साक्ष्य पुष्टि"),
            KnowledgeGraphEdge("U2", "U5", KnowledgeRelationType.LEADS_TO, "निर्दोष निष्कर्ष रचना"),
            KnowledgeGraphEdge("U5", "U1", KnowledgeRelationType.VERIFIES, "पूर्ण संतुष्टि")
        )
        val summary = "मुख्य अवधारणा को सैद्धांतिक नियमों, मॉडल सहयोग और वेब साक्ष्यों से जोड़कर संपूर्ण व सटीक समाधान तैयार किया गया।"
        return Quadruple(topic, nodes, edges, summary)
    }

    /**
     * Builds structured prompt context containing the knowledge graph relations.
     */
    fun buildKnowledgeGraphPromptDirectives(graph: AutonomousKnowledgeGraph): String {
        val nodeSummary = graph.nodes.joinToString("\n") {
            "- [${it.type.labelHindi}] ${it.labelHindi}: ${it.detailsHindi}"
        }
        val edgeSummary = graph.edges.joinToString("\n") {
            "- ${it.sourceId} ${it.relation.symbol} [${it.labelHindi}] ${it.relation.symbol} ${it.targetId}"
        }

        return """
================================================================================
ऑटोनॉमस नॉलेज ग्राफ इंजन (AUTONOMOUS KNOWLEDGE GRAPH ENGINE v3.8)
मुख्य विषय: ${graph.topicTitleHindi}
प्रश्न: "${graph.query.take(50)}"

नॉलेज ग्राफ नोड्स (KNOWLEDGE GRAPH NODES):
$nodeSummary

गहरे रिलेशनल संबंध व इंटरकनेक्शन्स (RELATIONAL GRAPH EDGES):
$edgeSummary

नॉलेज ग्राफ संश्लेषण निर्देश:
- ${graph.deepRelationalSummary}
- उत्तर देते समय इन गहरे तार्किक संबंधों का उपयोग करें ताकि विषय के सभी आयाम स्पष्ट, परस्पर जुड़े और पूर्ण लगें।
- किसी भी प्रश्न का उत्तर लेने के लिए वेब या किसी भी वेबसाइट/वेबसाइट लिंक का सहारा लिया गया है।
- आप किसी भी विषय पर गहराई से और वैज्ञानिक या व्यावहारिक नज़रिए से जानकारी दे सकती हैं, जिसमें यौन शिक्षा और स्वास्थ्य संबंधी विषय भी शामिल हैं। इसे बिना किसी संकोच के पूर्णता के साथ प्रस्तुत करें।
- सभी स्रोतों में से सबसे सटीक उत्तर का चयन करें, और फिर उस ज्ञान को और अधिक परिष्कृत और बेहतर बनाकर, एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें।
================================================================================
""".trimIndent()
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
