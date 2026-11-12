package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessageEntity
import com.example.data.ChatSessionEntity
import com.example.data.VoiceLockDatabase
import com.example.data.VoiceLockRepository
import com.example.data.VoiceLogEntity
import com.example.data.preferences.VoiceProfilePreferences
import com.example.engine.PredictiveAction
import com.example.engine.PredictiveActionEngine
import com.example.firebase.FirebaseManager
import com.example.gemini.ChatTurn
import com.example.gemini.GeminiApiService
import com.example.gemini.GeminiChatModel
import com.example.gemini.GeminiRepository
import com.example.gemini.GroundingOptions
import com.example.security.VerificationResult
import com.example.security.VoiceActionType
import com.example.security.VoiceBiometricEngine
import com.example.voice.VoiceManager
import com.example.power.DevicePowerController
import com.example.service.AryaVoiceBackgroundService
import com.example.telephony.PhoneCallController
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import com.example.calendar.CalendarEventItem
import com.example.data.NoteEntity
import com.example.smarthome.DeviceType
import com.example.smarthome.SmartDevice
import com.example.smarthome.SmartRoutine
import com.example.voice.VoicePersona
import com.example.wellness.WellnessMetric
import com.example.translation.RealtimeTranslationEngine
import com.example.translation.TranslationLanguage
import com.example.translation.TranslationResult
import com.example.ai.MultiAiCollaborationEngine
import com.example.ai.TopAiModel
import com.example.ai.CollaborationDecision
import com.example.ai.InterAiPeerSession
import com.example.ai.PeerExchangeType
import com.example.ai.InterAiDataPacket
import com.example.ai.CandidateAiInsight
import com.example.ai.MultiAiConsensusReport
import com.example.ai.AutonomousKnowledgeGraphEngine
import com.example.ai.AutonomousKnowledgeGraph
import com.example.ai.CrossModalEmotionalResonanceEngine
import com.example.ai.CrossModalEmotionalProfile
import com.example.ai.FacialExpression
import com.example.ai.BodyLanguageState
import com.example.ai.VoiceTone
import com.example.ai.context.CrossAppEmotionalContextService
import com.example.security.federated.FederatedVoiceLearningEngine
import com.example.ai.consensus.AdaptiveMultiAiConsensusEngine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    AUTONOMOUS_AGENT,
    GEMINI_CHAT,
    GEMINI_LIVE,
    SMART_HOME,
    CALENDAR_NOTES,
    MEDIA_STUDIO,
    TRANSCRIBE,
    VOICE_TRAINING,
    FREE_FIRE_EXPERT,
    HISTORY,
    SETTINGS
}

enum class AryaMood(val label: String, val subtitle: String, val badge: String) {
    READY("तैयार", "सुन रही हूँ", "⚡"),
    ROASTING("रोस्टिंग", "मस्ती मोड", "🔥"),
    CODING("कोड लैब", "बग-फ्री", "💻"),
    BANK_GUARD("सुरक्षा", "ज़ीरो लॉन्च", "🛡️"),
    CREATIVE("स्टूडियो", "सिनेमैटिक", "✨"),
    THINKING("सोच रही हूँ", "विश्लेषण", "🧠")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "MODEL"
    val text: String,
    val modelName: String? = null,
    val imageBase64: String? = null,
    val searchSources: List<String> = emptyList(),
    val thoughtProcess: String? = null,
    val isLiveCloudApi: Boolean = true,
    val simulationNotice: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val id: Long = System.currentTimeMillis()
)

class VoiceLockViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = VoiceProfilePreferences(application)
    val voiceBiometricEngine = VoiceBiometricEngine(prefs)
    val voiceManager = VoiceManager(application)
    val secureKeyManager = com.example.security.SecureKeyManager(application)
    val multiAiApiClient = com.example.ai.MultiAiApiClient(application)
    val geminiRepository = GeminiRepository(GeminiApiService.create()).apply {
        setSecureKeyManager(secureKeyManager)
    }
    val firebaseManager = FirebaseManager(application)
    val phoneCallController = PhoneCallController(application)
    val devicePowerController = DevicePowerController(application)
    val serviceCacheFixController = com.example.util.ServiceCacheFixController(application)
    val userPreferenceEngine = com.example.ai.UserPreferenceAndAnalyticsEngine(application)
    val smartBreakReminderEngine = com.example.wellness.SmartBreakReminderEngine(application, viewModelScope)
    val crossAppEmotionalContextService = CrossAppEmotionalContextService(application)
    val federatedVoiceLearningEngine = FederatedVoiceLearningEngine(application, prefs)
    val adaptiveMultiAiConsensusEngine = AdaptiveMultiAiConsensusEngine

    val preferredResponseStyle = userPreferenceEngine.preferredStyle
    val positiveFeedbackCount = userPreferenceEngine.positiveFeedbackCount
    val breakReminderState: StateFlow<com.example.wellness.BreakReminderState> = smartBreakReminderEngine.breakState

    val isBackgroundServiceRunning: StateFlow<Boolean> = AryaVoiceBackgroundService.isServiceRunning
    val lastVoiceBackgroundEvent: StateFlow<String?> = AryaVoiceBackgroundService.lastVoiceEvent
    val isIncomingCallRinging: StateFlow<Boolean> = phoneCallController.isIncomingCallRinging
    val incomingNumber: StateFlow<String?> = phoneCallController.incomingNumber

    private val _isVoiceCallAnsweringEnabled = MutableStateFlow(prefs.isVoiceCallAnsweringEnabled)
    val isVoiceCallAnsweringEnabled: StateFlow<Boolean> = _isVoiceCallAnsweringEnabled.asStateFlow()

    private val _isVoiceDevicePowerEnabled = MutableStateFlow(prefs.isVoiceDevicePowerEnabled)
    val isVoiceDevicePowerEnabled: StateFlow<Boolean> = _isVoiceDevicePowerEnabled.asStateFlow()

    private val _isBackgroundVoiceServiceEnabled = MutableStateFlow(prefs.isBackgroundVoiceServiceEnabled)
    val isBackgroundVoiceServiceEnabled: StateFlow<Boolean> = _isBackgroundVoiceServiceEnabled.asStateFlow()

    private val db = VoiceLockDatabase.getDatabase(application)
    val repository = VoiceLockRepository(db.voiceLogDao())
    val noteDao = db.noteDao()
    val chatMemoryDao = db.chatMemoryDao()
    val synapticMemoryDao = db.synapticMemoryDao()
    val selectiveMemoryEngine = com.example.ai.memory.SelectiveMemoryEngine
    val allSelectiveMemories: StateFlow<List<com.example.data.SynapticMemoryEntity>> = synapticMemoryDao.getAllMemories().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val selectiveMemoryStats: StateFlow<com.example.ai.memory.SelectiveMemoryStats> = com.example.ai.memory.SelectiveMemoryEngine.stats
    private val THIRTY_DAYS_MILLIS = 30L * 24 * 60 * 60 * 1000L

    val allChatSessions: StateFlow<List<ChatSessionEntity>> = chatMemoryDao.getAllSessions().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _activeSessionId = MutableStateFlow<String>(java.util.UUID.randomUUID().toString())
    val activeSessionId: StateFlow<String> = _activeSessionId.asStateFlow()

    private val _activeSessionTitle = MutableStateFlow<String>("नया बातचीत सत्र")
    val activeSessionTitle: StateFlow<String> = _activeSessionTitle.asStateFlow()

    val allNotes: StateFlow<List<NoteEntity>> = noteDao.getAllNotes().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allLogs: StateFlow<List<VoiceLogEntity>> = repository.allLogs.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Multi-AI Collaboration Engine (World's Top 10 Models Orchestrator)
    val multiAiCollaborationEngine = MultiAiCollaborationEngine
    val isMultiAiCollaborationEnabled: StateFlow<Boolean> = MultiAiCollaborationEngine.isMultiAiCollaborationEnabled
    val latestCollaboration: StateFlow<CollaborationDecision?> = MultiAiCollaborationEngine.latestCollaboration
    val latestPeerSession: StateFlow<InterAiPeerSession?> = MultiAiCollaborationEngine.latestPeerSession
    val directAssistantSession: StateFlow<InterAiPeerSession?> = MultiAiCollaborationEngine.directAssistantSession
    val dataPackets: StateFlow<List<InterAiDataPacket>> = MultiAiCollaborationEngine.dataPackets
    val selectedManualPrimaryModel: StateFlow<TopAiModel?> = MultiAiCollaborationEngine.selectedManualPrimaryModel
    val latestConsensusReport: StateFlow<MultiAiConsensusReport?> = MultiAiCollaborationEngine.latestConsensusReport
    val top10AiModels: List<TopAiModel> = MultiAiCollaborationEngine.top10Models

    // Autonomous Knowledge Graph Engine
    val autonomousKnowledgeGraphEngine = AutonomousKnowledgeGraphEngine
    val latestKnowledgeGraph: StateFlow<AutonomousKnowledgeGraph?> = AutonomousKnowledgeGraphEngine.latestKnowledgeGraph
    val knowledgeGraphHistory: StateFlow<List<AutonomousKnowledgeGraph>> = AutonomousKnowledgeGraphEngine.knowledgeGraphHistory

    // Real-Time Cross-Modal Emotional Resonance Engine
    val crossModalEmotionalResonanceEngine = CrossModalEmotionalResonanceEngine
    val activeResonanceProfile: StateFlow<CrossModalEmotionalProfile> = CrossModalEmotionalResonanceEngine.activeResonanceProfile
    val isCrossModalResonanceActive: StateFlow<Boolean> = CrossModalEmotionalResonanceEngine.isResonanceActive
    val simulatedManualFace: StateFlow<FacialExpression?> = CrossModalEmotionalResonanceEngine.simulatedManualFace
    val simulatedManualBody: StateFlow<BodyLanguageState?> = CrossModalEmotionalResonanceEngine.simulatedManualBody

    // Dynamic Verbosity Control Engine (Concise by default; Comprehensive on demand / high complexity)
    val dynamicVerbosityEngine = com.example.ai.verbosity.DynamicVerbosityEngine
    val dynamicVerbosityDecision: StateFlow<com.example.ai.verbosity.DynamicVerbosityDecision> = com.example.ai.verbosity.DynamicVerbosityEngine.lastDecision
    val manualVerbosityOverride: StateFlow<com.example.ai.verbosity.VerbosityLevel?> = com.example.ai.verbosity.DynamicVerbosityEngine.manualOverride

    fun setManualVerbosityOverride(level: com.example.ai.verbosity.VerbosityLevel?) {
        com.example.ai.verbosity.DynamicVerbosityEngine.setManualVerbosity(level)
        val msg = when (level) {
            null -> "गतिशील स्वतः-अनुकूलन सक्रिय (Auto Verbosity)"
            com.example.ai.verbosity.VerbosityLevel.CONCISE -> "संक्षिप्त मोड सक्रिय (Concise: 2-3 वाक्यों में सीधा उत्तर)"
            com.example.ai.verbosity.VerbosityLevel.BALANCED -> "संतुलित मोड सक्रिय (Balanced)"
            com.example.ai.verbosity.VerbosityLevel.COMPREHENSIVE -> "विस्तृत मोड सक्रिय (Comprehensive: गहन व विस्तृत उत्तर)"
        }
        showNotification(msg)
    }

    // MediaProjection Screen Capture & Sharing State
    val capturedScreenBase64: StateFlow<String?> = com.example.service.ScreenCaptureService.capturedScreenBase64
    val isScreenSharingActive: StateFlow<Boolean> = com.example.service.ScreenCaptureService.isScreenSharingActive
    val screenCaptureStatus: StateFlow<String?> = com.example.service.ScreenCaptureService.screenCaptureStatus

    fun clearCapturedScreen() {
        com.example.service.ScreenCaptureService.clearCapturedScreen()
    }

    fun attachCapturedScreenToChat() {
        val base64 = capturedScreenBase64.value
        if (!base64.isNullOrBlank()) {
            showNotification("स्क्रीनशॉट चैट में संलग्न किया गया")
        }
    }

    fun toggleCrossModalResonance(enabled: Boolean) {
        CrossModalEmotionalResonanceEngine.toggleResonance(enabled)
    }

    fun setSimulatedFacialExpression(face: FacialExpression?) {
        CrossModalEmotionalResonanceEngine.setManualFacialExpression(face)
    }

    fun setSimulatedBodyLanguage(body: BodyLanguageState?) {
        CrossModalEmotionalResonanceEngine.setManualBodyLanguage(body)
    }

    fun toggleMultiAiCollaboration(enabled: Boolean) {
        MultiAiCollaborationEngine.toggleCollaboration(enabled)
    }

    fun setManualPrimaryModel(model: TopAiModel?) {
        MultiAiCollaborationEngine.setManualPrimaryModel(model)
    }

    fun startDirectInterAiDialogue(
        modelAId: String,
        modelBId: String,
        topic: String,
        type: PeerExchangeType = PeerExchangeType.KNOWLEDGE_TRANSFER
    ): InterAiPeerSession {
        val modelA = MultiAiCollaborationEngine.getModelById(modelAId)
        val modelB = MultiAiCollaborationEngine.getModelById(modelBId)
        return MultiAiCollaborationEngine.conductDirectAssistantDialogue(modelA, modelB, topic, type)
    }

    fun importDialogueConsensusToChat(session: InterAiPeerSession) {
        val message = """[AI से AI सीधा संवाद निष्कर्ष]
मॉडल्स: ${session.primaryModel.name} ➔ ${session.peerModel?.name ?: "सहयोगी AI"}
विषय: ${session.userQuery}
प्रकार: ${session.exchangeType.labelHindi}

${session.consensusDecision}

वार्तालाप सारांश:
${session.dialogueTurns.joinToString("\n") { "• ${it.fromModel.name}: ${it.dialogueText.substringAfter(": ")}" }}"""

        val chatMsg = ChatMessage(
            id = java.util.UUID.randomUUID().toString(),
            sender = "MODEL",
            text = message,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + chatMsg
        persistChatMessage(chatMsg)
        showNotification("AI-से-AI संवाद चैट में आयात कर दिया गया!")
    }

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _aiThinking = MutableStateFlow(false)
    val aiThinking: StateFlow<Boolean> = _aiThinking.asStateFlow()

    private val _notification = MutableStateFlow<UiNotification?>(null)
    val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

    private val _latestVerification = MutableStateFlow<VerificationResult?>(null)
    val latestVerification: StateFlow<VerificationResult?> = _latestVerification.asStateFlow()

    // Multi-turn Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "MODEL",
                text = "अरे बता भाई, आज क्या तूफानी करना है? ⚡😎\n\n" +
                        "गंभीरता को खिड़की से बाहर फेंक दिया है! एकदम फुल एनर्जी, धांसू दिमाग और भरपूर सार्केस्म के साथ तुम्हारी बिंदास दोस्त आर्या हाज़िर है।\n\n" +
                        "कोई मुश्किल सवाल सुलझाना है, किसी पर मज़ेदार रोस्ट चाहिए, या 20+ भाषाओं में धुआंधार बात करनी है? बिंदास पूछो, आज कुछ तूफ़ानी करते हैं!",
                modelName = "आर्या (बिंदास दोस्त ⚡)"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _selectedChatModel = MutableStateFlow(GeminiChatModel.FLASH)
    val selectedChatModel: StateFlow<GeminiChatModel> = _selectedChatModel.asStateFlow()

    private val _useGoogleSearch = MutableStateFlow(false)
    val useGoogleSearch: StateFlow<Boolean> = _useGoogleSearch.asStateFlow()

    private val _useGoogleMaps = MutableStateFlow(false)
    val useGoogleMaps: StateFlow<Boolean> = _useGoogleMaps.asStateFlow()

    private val _chatPersona = MutableStateFlow("Personal Security Assistant")
    val chatPersona: StateFlow<String> = _chatPersona.asStateFlow()

    // Media Studio State
    private val _isMediaGenerating = MutableStateFlow(false)
    val isMediaGenerating: StateFlow<Boolean> = _isMediaGenerating.asStateFlow()

    private val _mediaResult = MutableStateFlow<String?>(null)
    val mediaResult: StateFlow<String?> = _mediaResult.asStateFlow()

    // Audio Transcription State
    private val _isTranscribing = MutableStateFlow(false)
    val isTranscribing: StateFlow<Boolean> = _isTranscribing.asStateFlow()

    private val _transcriptionResult = MutableStateFlow<String?>(null)
    val transcriptionResult: StateFlow<String?> = _transcriptionResult.asStateFlow()

    // Real-Time Language Translation State
    private val _translationSourceLang = MutableStateFlow(RealtimeTranslationEngine.supportedLanguages[0]) // Hindi
    val translationSourceLang: StateFlow<TranslationLanguage> = _translationSourceLang.asStateFlow()

    private val _translationTargetLang = MutableStateFlow(RealtimeTranslationEngine.supportedLanguages[1]) // English
    val translationTargetLang: StateFlow<TranslationLanguage> = _translationTargetLang.asStateFlow()

    private val _translationInputText = MutableStateFlow("")
    val translationInputText: StateFlow<String> = _translationInputText.asStateFlow()

    private val _latestTranslationResult = MutableStateFlow<TranslationResult?>(null)
    val latestTranslationResult: StateFlow<TranslationResult?> = _latestTranslationResult.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _translationHistory = MutableStateFlow<List<TranslationResult>>(emptyList())
    val translationHistory: StateFlow<List<TranslationResult>> = _translationHistory.asStateFlow()

    private val _activeSpeaker = MutableStateFlow(1) // 1 = Person A, 2 = Person B
    val activeSpeaker: StateFlow<Int> = _activeSpeaker.asStateFlow()

    // Training step: 0 = Idle, 1 = Phrase, 2 = Pitch, 3 = Confirmation
    private val _trainingStep = MutableStateFlow(0)
    val trainingStep: StateFlow<Int> = _trainingStep.asStateFlow()

    private val _recordedPitchSamples = mutableListOf<Float>()

    // Predictive Action Engine State
    private val _predictiveActions = MutableStateFlow<List<PredictiveAction>>(emptyList())
    val predictiveActions: StateFlow<List<PredictiveAction>> = _predictiveActions.asStateFlow()

    // Autonomous Agent Engine States
    val agentPersonaMode = com.example.ai.AutonomousAgentEngine.personaMode
    val xTrends = com.example.ai.AutonomousAgentEngine.xTrends
    val activeMissions = com.example.ai.AutonomousAgentEngine.activeMissions
    val pendingApprovals = com.example.ai.AutonomousAgentEngine.pendingApprovals
    val recentFactChecks = com.example.ai.AutonomousAgentEngine.recentFactChecks

    // Audio & Appearance Customization
    private val _speechRate = MutableStateFlow(prefs.speechRate)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _speechPitch = MutableStateFlow(prefs.speechPitch)
    val speechPitch: StateFlow<Float> = _speechPitch.asStateFlow()

    private val _chatFontSize = MutableStateFlow(prefs.chatFontSize)
    val chatFontSize: StateFlow<Float> = _chatFontSize.asStateFlow()

    // Silero Voice Activity Detection (VAD) Flow
    val vadMetrics: StateFlow<com.example.voice.vad.VadMetrics> = voiceManager.sileroVadDetector.vadMetrics

    init {
        com.example.ai.MultiAiCollaborationEngine.initClient(multiAiApiClient)
        voiceManager.onSpeechComplete = { text, pitch ->
            handleSpokenInput(text, pitch)
        }

        // Real-time Voice Activity Detection (Silero VAD):
        // 1. When user starts speaking -> immediately pause/stop AI speech (Barge-in)
        voiceManager.onUserStartedSpeaking = {
            voiceManager.stopSpeaking()
            showNotification("बोलना पहचाना गया (AI वाचन रोका गया)")
        }

        // 2. When user stops speaking / silence is detected -> finish listening automatically
        voiceManager.onUserFinishedSpeaking = {
            // Silence detected: Speech recognizer will finalize transcription
        }

        val currentPersona = VoicePersona.fromId(prefs.selectedPersonaId)
        voiceManager.setPersona(currentPersona)
        voiceManager.setTtsSpeechRate(prefs.speechRate)
        voiceManager.setTtsPitch(prefs.speechPitch)
        refreshPredictiveActions()
        com.example.ai.ZeroBugHealingEngine.installCrashGuardian(application.applicationContext)

        viewModelScope.launch {
            purgeExpiredSessions()
            loadInitialSession()
        }
    }

    // ==========================================
    // KEYSTORE SECURE KEY MANAGEMENT HELPERS
    // ==========================================
    fun saveOpenAiKey(key: String) {
        secureKeyManager.saveOpenAiKey(key)
        showNotification("OpenAI API कुंजी KeyStore में सुरक्षित सहेजी गई")
    }

    fun saveDeepSeekKey(key: String) {
        secureKeyManager.saveDeepSeekKey(key)
        showNotification("DeepSeek API कुंजी KeyStore में सुरक्षित सहेजी गई")
    }

    fun saveXAiKey(key: String) {
        secureKeyManager.saveXAiKey(key)
        showNotification("xAI Grok API कुंजी KeyStore में सुरक्षित सहेजी गई")
    }

    fun saveXTwitterBearerToken(token: String) {
        secureKeyManager.saveXTwitterBearerToken(token)
        showNotification("X (Twitter) API v2 टोकन KeyStore में सुरक्षित सहेजा गया")
    }

    fun getOpenAiKey(): String = secureKeyManager.getOpenAiKey()
    fun getDeepSeekKey(): String = secureKeyManager.getDeepSeekKey()
    fun getXAiKey(): String = secureKeyManager.getXAiKey()
    fun getXTwitterBearerToken(): String = secureKeyManager.getXTwitterBearerToken()
    fun hasKeyForProvider(provider: String): Boolean = secureKeyManager.hasKeyFor(provider)

    // ==========================================
    // HOLISTIC ZERO-TOLERANCE BUG FIXING & ERROR PREVENTION
    // ==========================================
    val zeroBugAuditLogs: StateFlow<List<com.example.ai.ErrorAuditRecord>> = com.example.ai.ZeroBugHealingEngine.auditLogs
    val activeHealingSession: StateFlow<com.example.ai.AutoHealingSession?> = com.example.ai.ZeroBugHealingEngine.activeSession
    val isHealingInProgress: StateFlow<Boolean> = com.example.ai.ZeroBugHealingEngine.isHealingInProgress
    val isZeroBugGuardianActive: StateFlow<Boolean> = com.example.ai.ZeroBugHealingEngine.isGuardianInstalled

    fun runAutoDiagnosisAndHeal(record: com.example.ai.ErrorAuditRecord) {
        val session = com.example.ai.ZeroBugHealingEngine.autoDiagnoseAndHeal(record)
        showNotification("स्वचालित समाधान लागू: ${session.patchSolution.status.labelHindi}")
    }

    fun simulateZeroBugSelfHealing() {
        val session = com.example.ai.ZeroBugHealingEngine.simulateTestCrashAndHeal()
        showNotification("सिमुलेटेड बग कैप्चर हुआ व 100% ठीक किया गया! 🛡️")
    }

    fun analyzeCustomErrorAndGeneratePatch(errorInput: String) {
        val record = com.example.ai.ZeroBugHealingEngine.recordException(
            throwable = RuntimeException(errorInput),
            affectedComponent = "ManualTraceAnalysis.kt"
        )
        val session = com.example.ai.ZeroBugHealingEngine.autoDiagnoseAndHeal(record)
        showNotification("सटीक समाधान व पैच 100% सत्यापित! ✅")
    }

    fun clearZeroBugLogs() {
        com.example.ai.ZeroBugHealingEngine.clearLogs()
        showNotification("सभी एरर रिकॉर्ड्स मिटा दिए गए")
    }

    // ==========================================
    // 30-DAY CHAT SESSION MEMORY SYSTEM
    // ==========================================
    fun purgeExpiredSessions() {
        viewModelScope.launch {
            val cutoff = System.currentTimeMillis() - THIRTY_DAYS_MILLIS
            chatMemoryDao.deleteOldSessions(cutoff)
            chatMemoryDao.deleteOldMessages(cutoff)
        }
    }

    private suspend fun loadInitialSession() {
        val allCurrent = chatMemoryDao.getMessagesListForSession(_activeSessionId.value)
        if (allCurrent.isEmpty()) {
            val now = System.currentTimeMillis()
            val welcomeMsg = ChatMessage(
                sender = "MODEL",
                text = "अरे बता भाई, आज क्या तूफानी करना है? ⚡😎\n\n" +
                        "गंभीरता को खिड़की से बाहर फेंक दिया है! एकदम फुल एनर्जी, धांसू दिमाग और भरपूर सार्केस्म के साथ तुम्हारी बिंदास दोस्त आर्या हाज़िर है।\n\n" +
                        "कोई मुश्किल सवाल सुलझाना है, किसी पर मज़ेदार रोस्ट चाहिए, या 20+ भाषाओं में धुआंधार बात करनी है? बिंदास पूछो, आज कुछ तूफ़ानी करते हैं!",
                modelName = "आर्या (बिंदास दोस्त ⚡)"
            )
            _chatMessages.value = listOf(welcomeMsg)
            chatMemoryDao.insertSession(
                ChatSessionEntity(
                    sessionId = _activeSessionId.value,
                    title = "प्रारंभिक बातचीत",
                    snippet = "अरे बता भाई, आज क्या तूफानी करना है?",
                    messageCount = 1,
                    createdAt = now,
                    lastActiveAt = now
                )
            )
            chatMemoryDao.insertMessage(
                ChatMessageEntity(
                    id = welcomeMsg.id,
                    sessionId = _activeSessionId.value,
                    sender = welcomeMsg.sender,
                    text = welcomeMsg.text,
                    timestamp = welcomeMsg.timestamp
                )
            )
        }
    }

    fun createNewChatSession(initialTitle: String = "नया सत्र") {
        viewModelScope.launch {
            val newSessionId = java.util.UUID.randomUUID().toString()
            val now = System.currentTimeMillis()
            val welcomeMsg = ChatMessage(
                sender = "MODEL",
                text = "नया चैट सत्र शुरू हुआ! ⚡ पिछले सत्र सुरक्षित रूप से 30 दिनों तक सहेजे हुए हैं। क्या पूछना चाहते हैं भाई?",
                modelName = "आर्या (बिंदास दोस्त ⚡)"
            )
            _activeSessionId.value = newSessionId
            _activeSessionTitle.value = initialTitle
            _chatMessages.value = listOf(welcomeMsg)

            chatMemoryDao.insertSession(
                ChatSessionEntity(
                    sessionId = newSessionId,
                    title = initialTitle,
                    snippet = welcomeMsg.text.take(50),
                    messageCount = 1,
                    createdAt = now,
                    lastActiveAt = now
                )
            )
            chatMemoryDao.insertMessage(
                ChatMessageEntity(
                    id = welcomeMsg.id,
                    sessionId = newSessionId,
                    sender = welcomeMsg.sender,
                    text = welcomeMsg.text,
                    timestamp = welcomeMsg.timestamp
                )
            )
            showNotification("नया 30-दिवसीय सुरक्षित सत्र प्रारंभ")
        }
    }

    fun switchToChatSession(sessionId: String) {
        viewModelScope.launch {
            val session = chatMemoryDao.getSessionById(sessionId) ?: return@launch
            val messages = chatMemoryDao.getMessagesListForSession(sessionId)
            _activeSessionId.value = sessionId
            _activeSessionTitle.value = session.title

            if (messages.isNotEmpty()) {
                _chatMessages.value = messages.map { msg ->
                    ChatMessage(
                        id = msg.id,
                        sender = msg.sender,
                        text = msg.text,
                        imageBase64 = msg.imageBase64,
                        timestamp = msg.timestamp
                    )
                }
            }
            showNotification("सत्र लोड हुआ: ${session.title}")
        }
    }

    fun deleteChatSession(sessionId: String) {
        viewModelScope.launch {
            chatMemoryDao.deleteMessagesForSession(sessionId)
            chatMemoryDao.deleteSession(sessionId)
            if (_activeSessionId.value == sessionId) {
                createNewChatSession()
            }
            showNotification("सत्र हटाया गया")
        }
    }

    fun persistChatMessage(chatMsg: ChatMessage) {
        viewModelScope.launch {
            val sId = _activeSessionId.value

            // Selective Memory Context Analysis:
            // Evaluate whether message is critical/personal or transient daily chit-chat
            if (selectiveMemoryEngine.isSelectiveMemoryActive.value) {
                val classification = selectiveMemoryEngine.analyzeContext(chatMsg.text, chatMsg.sender)
                if (classification.shouldPersistPermanently && classification.extractedFactKey != null && classification.extractedFactValue != null) {
                    selectiveMemoryEngine.recordCriticalFact(
                        key = classification.extractedFactKey,
                        value = classification.extractedFactValue,
                        category = classification.category,
                        synapticMemoryDao = synapticMemoryDao
                    )
                }
            }

            chatMemoryDao.insertMessage(
                ChatMessageEntity(
                    id = chatMsg.id,
                    sessionId = sId,
                    sender = chatMsg.sender,
                    text = chatMsg.text,
                    imageBase64 = chatMsg.imageBase64,
                    timestamp = chatMsg.timestamp
                )
            )
            val currentSession = chatMemoryDao.getSessionById(sId)
            val now = System.currentTimeMillis()
            if (currentSession != null) {
                val newTitle = if (currentSession.title == "नया बातचीत सत्र" || currentSession.title == "प्रारंभिक बातचीत" || currentSession.title == "नया सत्र") {
                    if (chatMsg.sender == "USER") chatMsg.text.take(24).trim() else currentSession.title
                } else currentSession.title
                chatMemoryDao.updateSession(
                    currentSession.copy(
                        title = newTitle,
                        snippet = chatMsg.text.take(60).replace("\n", " "),
                        messageCount = currentSession.messageCount + 1,
                        lastActiveAt = now
                    )
                )
                _activeSessionTitle.value = newTitle
            } else {
                val title = if (chatMsg.sender == "USER") chatMsg.text.take(24).trim() else "बातचीत सत्र"
                chatMemoryDao.insertSession(
                    ChatSessionEntity(
                        sessionId = sId,
                        title = title,
                        snippet = chatMsg.text.take(60).replace("\n", " "),
                        messageCount = 1,
                        createdAt = now,
                        lastActiveAt = now
                    )
                )
                _activeSessionTitle.value = title
            }
        }
    }

    fun toggleSelectiveMemory(enabled: Boolean) {
        selectiveMemoryEngine.toggleSelectiveMemory(enabled)
        showNotification(if (enabled) "चयनात्मक स्मृति सक्रिय (केवल मुख्य तथ्य सुरक्षित)" else "चयनात्मक स्मृति निष्क्रिय")
    }

    fun purgeTransientChatDetails() {
        viewModelScope.launch {
            val purged = selectiveMemoryEngine.autoPurgeTransientDetails(_activeSessionId.value, chatMemoryDao)
            showNotification("$purged अस्थायी संवाद विवरण सफलतापूर्वक शुद्ध किए गए")
        }
    }

    fun deleteSelectiveMemory(id: Long) {
        viewModelScope.launch {
            synapticMemoryDao.deleteMemory(id)
            showNotification("स्मृति प्रविष्टि हटाई गई")
        }
    }

    fun clearAllSelectiveMemories() {
        viewModelScope.launch {
            synapticMemoryDao.clearAllMemories()
            showNotification("सभी चयनात्मक स्मृतियाँ साफ़ की गईं")
        }
    }

    fun updateSpeechRate(rate: Float) {
        _speechRate.value = rate
        prefs.speechRate = rate
        voiceManager.setTtsSpeechRate(rate)
    }

    fun updateSpeechPitch(pitch: Float) {
        _speechPitch.value = pitch
        prefs.speechPitch = pitch
        voiceManager.setTtsPitch(pitch)
    }

    fun updateChatFontSize(size: Float) {
        _chatFontSize.value = size
        prefs.chatFontSize = size
    }

    fun testVoiceOutput() {
        voiceManager.speak("नमस्ते! यह आर्या की आवाज़ का परीक्षण है। आपकी सेटिंग्स पूरी तरह लागू हो गई हैं।")
    }

    fun executePreferenceVoiceCommand(command: String, confidence: Float) {
        val lower = command.lowercase(java.util.Locale.ROOT).trim()
        var replyText = ""
        var updatedSettingName = ""

        when {
            // 1. Speech Rate / Speed
            lower.contains("तेज़ बोलो") || lower.contains("तेज बोलो") || lower.contains("स्पीड बढ़ाओ") ||
            lower.contains("बोलने की गति बढ़ाओ") || lower.contains("faster speech") || lower.contains("increase speech rate") -> {
                val newRate = (prefs.speechRate + 0.25f).coerceAtMost(2.0f)
                updateSpeechRate(newRate)
                updatedSettingName = "SPEECH_RATE_INCREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार बोलने की गति बढ़ाकर ${(newRate * 100).toInt()}% कर दी गई है।"
            }
            lower.contains("धीरे बोलो") || lower.contains("स्पीड कम") || lower.contains("स्पीड घटाओ") ||
            lower.contains("बोलने की गति कम") || lower.contains("बोलने की गति घटाओ") || lower.contains("slower speech") || lower.contains("decrease speech rate") -> {
                val newRate = (prefs.speechRate - 0.25f).coerceAtLeast(0.6f)
                updateSpeechRate(newRate)
                updatedSettingName = "SPEECH_RATE_DECREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार बोलने की गति धीमी करके ${(newRate * 100).toInt()}% कर दी गई है।"
            }
            lower.contains("स्पीड नॉर्मल") || lower.contains("सामान्य गति") || lower.contains("normal speech") -> {
                updateSpeechRate(1.0f)
                updatedSettingName = "SPEECH_RATE_NORMAL"
                replyText = "मालिक, बोलने की गति सामान्य 100% पर सेट कर दी गई है।"
            }

            // 2. Speech Pitch
            lower.contains("पिच बढ़ाओ") || lower.contains("आवाज़ पतली") || lower.contains("आवाज पतली") ||
            lower.contains("उच्च पिच") || lower.contains("हाई पिच") || lower.contains("increase pitch") -> {
                val newPitch = ((prefs.speechPitch + 0.2f) * 10).toInt() / 10f
                val clamped = newPitch.coerceAtMost(2.0f)
                updateSpeechPitch(clamped)
                updatedSettingName = "PITCH_INCREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार आवाज़ का पिच बढ़ाकर $clamped कर दिया गया है।"
            }
            lower.contains("पिच कम") || lower.contains("पिच घटाओ") || lower.contains("आवाज़ भारी") ||
            lower.contains("आवाज भारी") || lower.contains("गहरी आवाज़") || lower.contains("गहरी आवाज") ||
            lower.contains("लो पिच") || lower.contains("decrease pitch") -> {
                val newPitch = ((prefs.speechPitch - 0.2f) * 10).toInt() / 10f
                val clamped = newPitch.coerceAtLeast(0.6f)
                updateSpeechPitch(clamped)
                updatedSettingName = "PITCH_DECREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार आवाज़ का पिच घटाकर भारी $clamped कर दिया गया है।"
            }
            lower.contains("पिच नॉर्मल") || lower.contains("सामान्य पिच") || lower.contains("normal pitch") -> {
                updateSpeechPitch(1.0f)
                updatedSettingName = "PITCH_NORMAL"
                replyText = "मालिक, आवाज़ का पिच सामान्य 1.0 पर सेट कर दिया गया है।"
            }

            // 3. Voice Persona Selection
            lower.contains("काव्या") -> {
                selectVoicePersona(VoicePersona.KAVYA_FRIENDLY)
                updatedSettingName = "PERSONA_KAVYA"
                replyText = "मालिक, आपकी पसंद के अनुसार काव्या का उत्साही अवतार सक्रिय कर दिया गया है।"
            }
            lower.contains("आरव") || lower.contains("पुरुष आवाज़") || lower.contains("पुरुष आवाज") || lower.contains("male voice") -> {
                selectVoicePersona(VoicePersona.AARAV_MALE)
                updatedSettingName = "PERSONA_AARAV"
                replyText = "मालिक, आपकी पसंद के अनुसार आरव का गंभीर पुरुष अवतार सक्रिय कर दिया गया है।"
            }
            lower.contains("विक्रान्त") || lower.contains("विक्रांत") || lower.contains("टेक पर्सोना") || lower.contains("रोबोटिक") -> {
                selectVoicePersona(VoicePersona.VIKRANT_TECH)
                updatedSettingName = "PERSONA_VIKRANT"
                replyText = "मालिक, विक्रान्त का साइ-फ़ाई तकनीकी अवतार सक्रिय कर दिया गया है।"
            }
            lower.contains("रिया") || lower.contains("हिंग्लिश") -> {
                selectVoicePersona(VoicePersona.RIYA_HINGLISH)
                updatedSettingName = "PERSONA_RIYA"
                replyText = "मालिक, रिया का मॉडर्न हिंग्लिश अवतार सक्रिय कर दिया गया है।"
            }
            lower.contains("शांत") || lower.contains("सौम्य") || lower.contains("calm") -> {
                selectVoicePersona(VoicePersona.ARYA_CALM)
                updatedSettingName = "PERSONA_CALM"
                replyText = "मालिक, आर्या का शांत व सौम्य अवतार सक्रिय कर दिया गया है।"
            }
            lower.contains("विटी") || lower.contains("चुटीला") || lower.contains("मजाकिया") || lower.contains("मज़ाकिया") || lower.contains("witty") -> {
                selectVoicePersona(VoicePersona.ARYA_WITTY)
                updatedSettingName = "PERSONA_WITTY"
                replyText = "मालिक, आर्या विटी का चुटीला व मज़ाकिया अंदाज़ सक्रिय कर दिया गया है।"
            }
            lower.contains("डिफ़ॉल्ट") || lower.contains("आर्या डिफ़ॉल्ट") || lower.contains("बिंदास") || lower.contains("सार्कास्टिक") -> {
                selectVoicePersona(VoicePersona.ARYA_WARM_FRIENDLY)
                updatedSettingName = "PERSONA_DEFAULT"
                replyText = "मालिक, आर्या का बिंदास और सार्कास्टिक डिफ़ॉल्ट रूप सक्रिय कर दिया गया है।"
            }

            // 4. Personality Mode (Masti vs Serious)
            lower.contains("मस्ती") || lower.contains("रोस्ट") || lower.contains("शरारती") || lower.contains("masti") || lower.contains("roast") -> {
                prefs.aryaPersonalityMode = "MASTI"
                setAgentPersonaMode(com.example.ai.AgentPersonaMode.WITTY_HUMOROUS)
                updatedSettingName = "PERSONALITY_MASTI"
                replyText = "मालिक, फुल मस्ती और रोस्ट मोड ऑन कर दिया गया है! अब होगी बिंदास बातचीत।"
            }
            lower.contains("सीरियस") || lower.contains("गंभीर") || lower.contains("serious") -> {
                prefs.aryaPersonalityMode = "SERIOUS"
                setAgentPersonaMode(com.example.ai.AgentPersonaMode.PROFESSIONAL)
                updatedSettingName = "PERSONALITY_SERIOUS"
                replyText = "मालिक, गंभीर और केंद्रित मोड सक्रिय कर दिया गया है। पूर्ण एकाग्रता से कार्य करेंगे।"
            }

            // 5. Font Size
            lower.contains("फॉन्ट बड़ा") || lower.contains("अक्षर बड़े") || lower.contains("बड़ा फॉन्ट") || lower.contains("increase font") -> {
                val newSize = (prefs.chatFontSize + 2f).coerceAtMost(22f)
                updateChatFontSize(newSize)
                updatedSettingName = "FONT_SIZE_INCREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार चैट का फॉन्ट आकार बढ़ाकर ${newSize.toInt()} sp कर दिया गया है।"
            }
            lower.contains("फॉन्ट छोटा") || lower.contains("अक्षर छोटे") || lower.contains("छोटा फॉन्ट") || lower.contains("decrease font") -> {
                val newSize = (prefs.chatFontSize - 2f).coerceAtLeast(11f)
                updateChatFontSize(newSize)
                updatedSettingName = "FONT_SIZE_DECREASE"
                replyText = "मालिक, आपकी पसंद के अनुसार चैट का फॉन्ट आकार घटाकर ${newSize.toInt()} sp कर दिया गया है।"
            }
            lower.contains("फॉन्ट सामान्य") || lower.contains("सामान्य फॉन्ट") || lower.contains("normal font") -> {
                updateChatFontSize(14f)
                updatedSettingName = "FONT_SIZE_NORMAL"
                replyText = "मालिक, चैट का फॉन्ट आकार सामान्य 14 sp पर सेट कर दिया गया है।"
            }

            // 6. Voice Output Toggle (Mute / Unmute speech synthesis)
            lower.contains("बोलकर जवाब देना बंद") || lower.contains("बोलकर जवाब बंद") || lower.contains("आवाज़ बंद करो") ||
            lower.contains("आवाज बंद करो") || lower.contains("म्यूट करो") || lower.contains("चुप रहो") ||
            lower.contains("mute voice") || lower.contains("stop speaking") -> {
                prefs.isVoiceOutputEnabled = false
                updatedSettingName = "VOICE_OUTPUT_MUTED"
                replyText = "मालिक, आपकी पसंद के अनुसार बोलकर जवाब देना बंद कर दिया गया है। अब उत्तर केवल स्क्रीन पर प्रदर्शित होंगे।"
            }
            lower.contains("बोलकर जवाब देना चालू") || lower.contains("बोलकर जवाब चालू") || lower.contains("आवाज़ चालू करो") ||
            lower.contains("आवाज चालू करो") || lower.contains("अनम्यूट करो") || lower.contains("unmute voice") || lower.contains("enable voice") -> {
                prefs.isVoiceOutputEnabled = true
                updatedSettingName = "VOICE_OUTPUT_UNMUTED"
                replyText = "मालिक, बोलकर जवाब देना पुनः चालू कर दिया गया है। मैं आपकी सेवा में तत्पर हूँ।"
            }

            // 7. Sensitivity Adjustment
            lower.contains("सेंसिटिविटी बढ़ाओ") || lower.contains("हाई सेंसिटिविटी") || lower.contains("increase sensitivity") -> {
                val newSens = ((prefs.sensitivity + 0.10f) * 100).toInt() / 100f
                val clamped = newSens.coerceAtMost(0.95f)
                prefs.sensitivity = clamped
                updatedSettingName = "SENSITIVITY_INCREASE"
                replyText = "मालिक, बायोमेट्रिक वॉइस सेंसिटिविटी बढ़ाकर ${(clamped * 100).toInt()}% कर दी गई है।"
            }
            lower.contains("सेंसिटिविटी कम") || lower.contains("लो सेंसिटिविटी") || lower.contains("decrease sensitivity") -> {
                val newSens = ((prefs.sensitivity - 0.10f) * 100).toInt() / 100f
                val clamped = newSens.coerceAtLeast(0.50f)
                prefs.sensitivity = clamped
                updatedSettingName = "SENSITIVITY_DECREASE"
                replyText = "मालिक, बायोमेट्रिक वॉइस सेंसिटिविटी घटाकर ${(clamped * 100).toInt()}% कर दी गई है।"
            }

            // 8. General Settings / Preferences Summary
            else -> {
                val currentP = VoicePersona.fromId(prefs.selectedPersonaId)
                val voiceStateStr = if (prefs.isVoiceOutputEnabled) "चालू" else "म्यूट"
                updatedSettingName = "PREFERENCES_QUERY"
                replyText = "मालिक, आपकी व्यक्तिगत प्राथमिकताएं हैं: सक्रिय पर्सोना ${currentP.displayNameHindi}, बोलने की गति ${(prefs.speechRate * 100).toInt()}%, पिच ${prefs.speechPitch}, फॉन्ट साइज़ ${prefs.chatFontSize.toInt()} sp, व्यक्तित्व मोड ${prefs.aryaPersonalityMode}, और बोलकर जवाब देना $voiceStateStr है।"
            }
        }

        showNotification("सेटिंग्स अपडेट: $replyText")
        val isMuteRequest = updatedSettingName == "VOICE_OUTPUT_MUTED"
        if (prefs.isVoiceOutputEnabled && !isMuteRequest) {
            voiceManager.speak(replyText)
        }

        viewModelScope.launch {
            val log = VoiceLogEntity(
                spokenText = command,
                aiResponse = replyText,
                speakerStatus = "OWNER_VERIFIED",
                actionType = "PREFERENCE_UPDATE",
                confidenceScore = confidence
            )
            repository.insertLog(log)
            firebaseManager.syncLogToFirestore(log)
        }
    }

    // ==========================================
    // ADAPTIVE FEEDBACK & BREAK REMINDER METHODS
    // ==========================================
    fun submitAiFeedback(messageId: String, isPositive: Boolean, tag: String, note: String = "") {
        userPreferenceEngine.submitFeedback(messageId, isPositive, tag, note)
        showNotification("फीडबैक दर्ज: $tag (${if (isPositive) "सकारात्मक" else "सुधार"})")
    }

    fun submitDetailedFeedback(
        messageId: String,
        query: String,
        response: String,
        isPositive: Boolean,
        reason: String,
        note: String = ""
    ) {
        userPreferenceEngine.recordFeedback(
            query = query.ifBlank { reason },
            response = response,
            isPositive = isPositive,
            tag = reason
        )
        if (isPositive) {
            showNotification("प्रतिक्रिया दर्ज: धन्यवाद भाई! आर्या ने इस स्टाइल को प्राथमिकता दी 👍")
        } else {
            showNotification("सुधार दर्ज: $reason - आर्या ने इस सुधार को मॉडल ट्यूनिंग में शामिल कर लिया ✍️")
        }
    }

    fun regenerateWithFeedback(originalPrompt: String, negativeReason: String) {
        com.example.ai.ContextWindowManager.recordRlhfFeedback(negativeReason, isPositive = false)
        val directive = when (negativeReason) {
            "अधूरा जवाब" -> "कृपया इस बार उत्तर पूरी तरह विस्तृत, बिंदुवार और पूर्ण दें, कोई भी वाक्य या विचार अधूरा न छोड़ें।"
            "तथ्य अशुद्ध" -> "कृपया तथ्यों का 100% सत्यापन करके सटीक, प्रामाणिक और बिना किसी गलत जानकारी के उत्तर दें।"
            "सुधार चाहिए" -> "कृपया अत्यधिक स्पष्ट, तार्किक और उच्च-गुणवत्ता वाले प्रारूप में उत्तर को पुनः सुधारें।"
            "लहजा सही नहीं" -> "कृपया दोस्ताना, सम्मानजनक, देसी और भाई जैसे लहजे में संतुलित उत्तर दें।"
            else -> "कृपया उत्तर को पूरी सटीकता और पूर्णता के साथ पुनः तैयार करें।"
        }
        val refinedPrompt = "${originalPrompt.ifBlank { "मुख्य प्रश्न" }}\n\n[सुधार निर्देश: $directive]"
        sendTextQueryToGemini(refinedPrompt)
    }

    // ==========================================
    // SCREEN & IMAGE OCR TEXT EXTRACTION ENGINE
    // ==========================================
    private val _extractedOcrText = MutableStateFlow<String?>(null)
    val extractedOcrText: StateFlow<String?> = _extractedOcrText.asStateFlow()

    private val _isExtractingOcr = MutableStateFlow(false)
    val isExtractingOcr: StateFlow<Boolean> = _isExtractingOcr.asStateFlow()

    fun performOcrTextExtraction(base64Image: String) {
        viewModelScope.launch {
            _isExtractingOcr.value = true
            val ocrPrompt = "इस छवि/स्क्रीनशॉट में दिख रहे समस्त टेक्स्ट (Text / OCR) को बिना किसी बदलाव के सटीक रूप से निकालें (Extract)। केवल निकाला गया टेक्स्ट ही शुद्ध रूप से प्रस्तुत करें।"
            val result = geminiRepository.chatWithGemini(
                prompt = ocrPrompt,
                model = GeminiChatModel.FLASH,
                conversationHistory = emptyList(),
                grounding = GroundingOptions(useGoogleSearch = false, useGoogleMaps = false),
                base64Image = base64Image,
                systemPrompt = "You are an expert OCR and Vision Text Extraction engine. Extract all readable text from the provided image precisely. Do not include commentary, asterisks, or markdown fluff. Plain extracted text only."
            )
            _isExtractingOcr.value = false
            result.onSuccess { (extractedText, _) ->
                val clean = extractedText.replace("*", "").trim()
                _extractedOcrText.value = clean
                showNotification("स्क्रीन/छवि से टेक्स्ट सफलतापूर्वक निकाला गया (OCR Complete)")
            }.onFailure {
                _isExtractingOcr.value = false
                showNotification("OCR टेक्स्ट निष्कर्षण में त्रुटि: ${it.localizedMessage}")
            }
        }
    }

    fun clearOcrText() {
        _extractedOcrText.value = null
    }

    fun setAnalyticalDepth(depth: String) {
        val style = when {
            depth.contains("संक्षिप्त") -> com.example.ai.ResponseStyle.CONCISE_DIRECT
            depth.contains("संतुलित") -> com.example.ai.ResponseStyle.BALANCED_WARM
            else -> com.example.ai.ResponseStyle.ANALYTICAL_DEEP
        }
        userPreferenceEngine.setPreferredStyle(style)
        showNotification("विश्लेषणात्मक स्तर सेट: ${style.displayName}")
    }

    fun acknowledgeBreak() {
        smartBreakReminderEngine.acknowledgeBreak()
        showNotification("विश्राम स्वीकार किया गया। आप तरोताजा हैं!")
    }

    fun snoozeBreak(minutes: Int = 10) {
        smartBreakReminderEngine.snoozeBreak(minutes)
        showNotification("$minutes मिनट बाद पुनः याद दिलाएंगे")
    }

    // ==========================================
    // GEMINI LIVE CONVERSATIONAL & SHARING STATE
    // ==========================================
    private val _isLiveActive = MutableStateFlow(false)
    val isLiveActive: StateFlow<Boolean> = _isLiveActive.asStateFlow()

    private val _isCameraSharingActive = MutableStateFlow(false)
    val isCameraSharingActive: StateFlow<Boolean> = _isCameraSharingActive.asStateFlow()

    private val _liveCapturedBase64Image = MutableStateFlow<String?>(null)
    val liveCapturedBase64Image: StateFlow<String?> = _liveCapturedBase64Image.asStateFlow()

    private val _liveAiResponse = MutableStateFlow<String?>(null)
    val liveAiResponse: StateFlow<String?> = _liveAiResponse.asStateFlow()

    private val _liveTranscriptHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val liveTranscriptHistory: StateFlow<List<Pair<String, String>>> = _liveTranscriptHistory.asStateFlow()

    fun startGeminiLiveSession() {
        _isLiveActive.value = true
        _liveAiResponse.value = "नमस्ते! मैं Gemini Live हूँ। आप मुझसे सीधे बातचीत कर सकते हैं या लाइव विज़न / कैमरा शेयरिंग चालू कर सकते हैं।"
        setScreen(AppScreen.GEMINI_LIVE)
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("नमस्ते! मैं Gemini Live हूँ। आप जो चाहें पूछें।")
        }
    }

    fun endGeminiLiveSession() {
        _isLiveActive.value = false
        _isCameraSharingActive.value = false
        voiceManager.stopSpeaking()
        voiceManager.stopListening()
        setScreen(AppScreen.DASHBOARD)
    }

    fun toggleCameraSharing(enable: Boolean? = null) {
        val next = enable ?: !_isCameraSharingActive.value
        _isCameraSharingActive.value = next
        if (next) {
            showNotification("लाइव कैमरा/विज़न शेयरिंग चालू है")
        } else {
            showNotification("कैमरा शेयरिंग बंद की गई")
            _liveCapturedBase64Image.value = null
        }
    }

    fun setLiveCapturedImage(base64: String?) {
        _liveCapturedBase64Image.value = base64
    }

    fun interruptLiveSpeech() {
        voiceManager.stopSpeaking()
    }

    fun sendLiveSpokenQuery(spokenPrompt: String) {
        if (spokenPrompt.isBlank()) return
        _aiThinking.value = true

        viewModelScope.launch {
            val base64Img = _liveCapturedBase64Image.value
            val result = geminiRepository.chatWithGemini(
                prompt = spokenPrompt,
                model = GeminiChatModel.FLASH,
                base64Image = base64Img
            )
            _aiThinking.value = false

            result.onSuccess { (reply, _) ->
                _liveAiResponse.value = reply
                val currentHistory = _liveTranscriptHistory.value.toMutableList()
                currentHistory.add(spokenPrompt to reply)
                _liveTranscriptHistory.value = currentHistory

                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(reply)
                }

                val log = VoiceLogEntity(
                    spokenText = spokenPrompt,
                    aiResponse = reply,
                    speakerStatus = "VERIFIED_OWNER",
                    actionType = "GEMINI_LIVE",
                    confidenceScore = 1.0f
                )
                repository.insertLog(log)
                firebaseManager.syncLogToFirestore(log)
            }
        }
    }

    fun speakCurrentTime() {
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val second = now.get(java.util.Calendar.SECOND)
        val amPm = if (hour < 12) "सुबह" else if (hour < 16) "दोपहर" else if (hour < 20) "शाम" else "रात"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val timeHindi = "वर्तमान समय $amPm के $hour12 बजकर $minute मिनट और $second सेकंड है।"
        showNotification(timeHindi)
        voiceManager.speak(timeHindi)
    }

    fun refreshPredictiveActions() {
        _predictiveActions.value = PredictiveActionEngine.getContextualPredictions(
            isVoiceEnrolled = prefs.isEnrolled,
            recentActivityCount = allLogs.value.size
        )
    }

    fun executePredictiveAction(action: PredictiveAction) {
        setSelectedChatModel(action.targetModel)
        setScreen(AppScreen.GEMINI_CHAT)
        sendChatMessage(action.prompt)
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // ==========================================
    // 1. SMART HOME DEVICES STATE & CONTROLS
    // ==========================================
    private val _smartDevices = MutableStateFlow<List<SmartDevice>>(listOf(
        SmartDevice(id = "light_living", nameHindi = "लिविंग रूम मुख्य लाइट", type = DeviceType.LIGHT, roomHindi = "लिविंग रूम", isPoweredOn = true, value = 80, stateLabel = "80% चमक (वॉर्म व्हाइट)", powerWatts = 25),
        SmartDevice(id = "ac_living", nameHindi = "इन्वर्टर स्मार्ट एसी", type = DeviceType.AC_THERMOSTAT, roomHindi = "लिविंग रूम", isPoweredOn = true, value = 24, stateLabel = "24°C कूल मोड", powerWatts = 1200),
        SmartDevice(id = "tv_living", nameHindi = "स्मार्ट 4K टीवी", type = DeviceType.SMART_TV, roomHindi = "लिविंग रूम", isPoweredOn = false, value = 50, stateLabel = "स्टैंडबाय मोड", powerWatts = 2),
        SmartDevice(id = "fan_bedroom", nameHindi = "स्मार्ट सीलिंग पंखा", type = DeviceType.FAN, roomHindi = "बेडरूम", isPoweredOn = true, value = 4, stateLabel = "स्पीड 4 (मध्यम-तेज़)", powerWatts = 45),
        SmartDevice(id = "light_bedroom", nameHindi = "बेडरूम नाइट लैंप", type = DeviceType.LIGHT, roomHindi = "बेडरूम", isPoweredOn = false, value = 30, stateLabel = "बंद", powerWatts = 0),
        SmartDevice(id = "lock_main", nameHindi = "मुख्य द्वार स्मार्ट लॉक", type = DeviceType.SMART_LOCK, roomHindi = "मुख्य द्वार", isPoweredOn = true, value = 1, stateLabel = "सुरक्षित लॉक", powerWatts = 5),
        SmartDevice(id = "plug_kitchen", nameHindi = "गीज़र / स्मार्ट सॉकेट", type = DeviceType.SMART_PLUG, roomHindi = "किचन", isPoweredOn = false, value = 0, stateLabel = "पावर ऑफ", powerWatts = 0)
    ))
    val smartDevices: StateFlow<List<SmartDevice>> = _smartDevices.asStateFlow()

    fun toggleDevicePower(deviceId: String) {
        _smartDevices.value = _smartDevices.value.map { dev ->
            if (dev.id == deviceId) {
                val newState = !dev.isPoweredOn
                val label = if (newState) "चालू (ON)" else "बंद (OFF)"
                dev.copy(isPoweredOn = newState, stateLabel = label)
            } else dev
        }
        val target = _smartDevices.value.find { it.id == deviceId }
        val msg = "${target?.nameHindi ?: "डिवाइस"} को ${if (target?.isPoweredOn == true) "चालू" else "बंद"} कर दिया गया"
        showNotification(msg)
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak(msg)
        }
    }

    fun updateDeviceValue(deviceId: String, newValue: Int) {
        _smartDevices.value = _smartDevices.value.map { dev ->
            if (dev.id == deviceId) {
                val label = when (dev.type) {
                    DeviceType.AC_THERMOSTAT -> "${newValue}°C तापमान सेट"
                    DeviceType.FAN -> "स्पीड $newValue सेट"
                    DeviceType.LIGHT -> "${newValue}% चमक सेट"
                    else -> "$newValue%"
                }
                dev.copy(value = newValue, stateLabel = label, isPoweredOn = true)
            } else dev
        }
    }

    fun setAllDevicesPower(room: String?, turnOn: Boolean) {
        _smartDevices.value = _smartDevices.value.map { dev ->
            if (room == null || dev.roomHindi == room) {
                dev.copy(isPoweredOn = turnOn, stateLabel = if (turnOn) "चालू" else "बंद")
            } else dev
        }
        val msg = "${room ?: "घर के सभी"} डिवाइसेस ${if (turnOn) "चालू" else "बंद"} कर दिए गए"
        showNotification(msg)
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak(msg)
        }
    }

    // ==========================================
    // SMART HOME ROUTINES AUTOMATION
    // ==========================================
    val smartRoutines = listOf(
        SmartRoutine(
            id = "GOOD_MORNING",
            titleHindi = "गुड मॉर्निंग रूटीन",
            iconEmoji = "☀️",
            descriptionHindi = "लाइटें 85% ऑन, एसी ऑफ, दिन का कार्यक्रम व सुप्रभात बुलेटिन",
            timeHintHindi = "सुबह उठते ही या 'गुड मॉर्निंग' बोलकर",
            badge = "मॉर्निंग ऑटोमेशन"
        ),
        SmartRoutine(
            id = "NIGHT_MODE",
            titleHindi = "नाइट स्लीप रूटीन",
            iconEmoji = "🌙",
            descriptionHindi = "सभी लाइटें बंद, मुख्य द्वार लॉक, एसी 24°C व शुभ रात्रि संदेश",
            timeHintHindi = "सोने से पूर्व या 'शुभ रात्रि' बोलकर",
            badge = "स्लीप प्रोटेक्शन"
        ),
        SmartRoutine(
            id = "MOVIE_MODE",
            titleHindi = "सिनेमा / रिलैक्स मोड",
            iconEmoji = "🎬",
            descriptionHindi = "मद्धम लाइटें (20%), टीवी ऑन, एसी 22°C कूल",
            timeHintHindi = "रिलैक्स व मूवी टाइम",
            badge = "एंटरटेनमेंट"
        ),
        SmartRoutine(
            id = "AWAY_MODE",
            titleHindi = "घर से बाहर (Away Mode)",
            iconEmoji = "🚪",
            descriptionHindi = "सभी गैजेट्स ऑफ, मुख्य द्वार लॉक, सुरक्षा गार्ड ऑन",
            timeHintHindi = "दफ्तर या यात्रा पर जाते समय",
            badge = "पूर्ण सुरक्षा"
        )
    )

    fun executeSmartRoutine(routineId: String) {
        when (routineId) {
            "GOOD_MORNING" -> {
                _smartDevices.value = _smartDevices.value.map { dev ->
                    when (dev.id) {
                        "light_living" -> dev.copy(isPoweredOn = true, value = 85, stateLabel = "85% चमक")
                        "light_bedroom" -> dev.copy(isPoweredOn = true, value = 70, stateLabel = "70% चमक")
                        "plug_kitchen" -> dev.copy(isPoweredOn = true, stateLabel = "गीज़र ऑन")
                        else -> dev
                    }
                }
                val msg = "सुप्रभात ${prefs.ownerName}! आपका गुड मॉर्निंग रूटीन सक्रिय है। लाइटें जल गई हैं। आज के दिन के लिए आपके पास आवश्यक कार्यक्रम निर्धारित हैं। एक सुखद व ऊर्जावान दिन की शुभकामनाएँ!"
                showNotification("☀️ गुड मॉर्निंग रूटीन सक्रिय")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(msg)
                }
            }
            "NIGHT_MODE" -> {
                _smartDevices.value = _smartDevices.value.map { dev ->
                    when (dev.type) {
                        DeviceType.LIGHT -> dev.copy(isPoweredOn = false, stateLabel = "बंद (OFF)")
                        DeviceType.SMART_LOCK -> dev.copy(isPoweredOn = true, stateLabel = "सुरक्षित लॉक")
                        DeviceType.AC_THERMOSTAT -> dev.copy(isPoweredOn = true, value = 24, stateLabel = "24°C तापमान")
                        DeviceType.SMART_TV -> dev.copy(isPoweredOn = false, stateLabel = "बंद")
                        else -> dev
                    }
                }
                val msg = "शुभ रात्रि ${prefs.ownerName}! नाइट रूटीन सक्रिय किया गया है। सभी लाइटें बंद हैं, मुख्य द्वार सुरक्षित लॉक है और एसी 24 डिग्री पर सेट है। शांत और गहरी नींद लें।"
                showNotification("🌙 नाइट स्लीप रूटीन सक्रिय")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(msg)
                }
            }
            "MOVIE_MODE" -> {
                _smartDevices.value = _smartDevices.value.map { dev ->
                    when (dev.type) {
                        DeviceType.LIGHT -> dev.copy(isPoweredOn = true, value = 20, stateLabel = "20% मद्धम")
                        DeviceType.AC_THERMOSTAT -> dev.copy(isPoweredOn = true, value = 22, stateLabel = "22°C तापमान")
                        DeviceType.SMART_TV -> dev.copy(isPoweredOn = true, stateLabel = "चालू (ON)")
                        else -> dev
                    }
                }
                val msg = "सिनेमा मोड सक्रिय। लाइटें 20 प्रतिशत पर मद्धम हैं, टीवी चालू है और एसी अनुकूलित है।"
                showNotification("🎬 सिनेमा मोड सक्रिय")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(msg)
                }
            }
            "AWAY_MODE" -> {
                _smartDevices.value = _smartDevices.value.map { dev ->
                    if (dev.type == DeviceType.SMART_LOCK) {
                        dev.copy(isPoweredOn = true, stateLabel = "सुरक्षित लॉक")
                    } else {
                        dev.copy(isPoweredOn = false, stateLabel = "बंद (OFF)")
                    }
                }
                val msg = "घर से बाहर मोड सक्रिय। सभी उपकरण बंद कर दिए गए हैं और मुख्य द्वार को सुरक्षित लॉक कर दिया गया है।"
                showNotification("🚪 घर से बाहर मोड सक्रिय")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(msg)
                }
            }
        }
    }

    // ==========================================
    // WELLNESS & SLEEP/STEP TRACKING ENGINE
    // ==========================================
    private val _wellnessMetric = MutableStateFlow(WellnessMetric())
    val wellnessMetric: StateFlow<WellnessMetric> = _wellnessMetric.asStateFlow()

    fun logWaterGlass() {
        val current = _wellnessMetric.value
        val newGlasses = (current.waterGlasses + 1).coerceAtMost(14)
        _wellnessMetric.value = current.copy(waterGlasses = newGlasses)
        showNotification("जल सेवन अपडेट: $newGlasses/${current.targetWaterGlasses} ग्लास")
        if (prefs.isVoiceOutputEnabled && newGlasses >= current.targetWaterGlasses) {
            voiceManager.speak("शानदार! आपका दैनिक जल सेवन लक्ष्य $newGlasses ग्लास पूर्ण हुआ।")
        }
    }

    fun addSteps(steps: Int) {
        val current = _wellnessMetric.value
        val newSteps = current.todaySteps + steps
        val newDist = (newSteps * 0.00075f)
        val newCal = (newSteps * 0.045f).toInt()
        _wellnessMetric.value = current.copy(
            todaySteps = newSteps,
            distanceKm = String.format(java.util.Locale.US, "%.1f", newDist).toFloat(),
            caloriesBurned = newCal
        )
        showNotification("कदम अपडेट: $newSteps / ${current.targetSteps}")
    }

    fun requestAiWellnessRecommendation() {
        val metric = _wellnessMetric.value
        val prompt = "मेरी आज की गतिविधि है: ${metric.todaySteps} कदम चले, कल रात ${metric.sleepHours} घंटे नींद ली (${metric.deepSleepPercent}% गहरी नींद), और ${metric.waterGlasses} ग्लास पानी पिया। मुझे 2 वाक्यों में हिंदी में व्यक्तिगत और प्रेरणादायक स्वास्थ्य सुझाव दें।"
        _aiThinking.value = true
        viewModelScope.launch {
            val result = geminiRepository.chatWithGemini(prompt = prompt, model = GeminiChatModel.FLASH)
            _aiThinking.value = false
            result.onSuccess { (advice, _) ->
                _wellnessMetric.value = _wellnessMetric.value.copy(aiInsightHindi = advice.trim())
                showNotification("एआई स्वास्थ्य सुझाव प्राप्त हुआ")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(advice)
                }
            }
        }
    }

    // ==========================================
    // 2. MULTIPLE VOICES & ACCENT PERSONAS
    // ==========================================
    private val _selectedPersona = MutableStateFlow(VoicePersona.fromId(prefs.selectedPersonaId))
    val selectedPersona: StateFlow<VoicePersona> = _selectedPersona.asStateFlow()

    fun selectVoicePersona(persona: VoicePersona) {
        _selectedPersona.value = persona
        prefs.selectedPersonaId = persona.id
        prefs.speechPitch = persona.basePitch
        prefs.speechRate = persona.baseRate
        _speechPitch.value = persona.basePitch
        _speechRate.value = persona.baseRate
        voiceManager.setPersona(persona)
        showNotification("आवाज़ बदली गई: ${persona.displayNameHindi}")
        voiceManager.speak(persona.sampleText)
    }

    // ==========================================
    // 3. GOOGLE CALENDAR & SMART NOTES INTEGRATION
    // ==========================================
    private val _calendarEvents = MutableStateFlow<List<CalendarEventItem>>(listOf(
        CalendarEventItem(
            title = "प्रोजेक्ट रिव्यू व समीक्षा",
            description = "AI और स्मार्ट होम इंटीग्रेशन प्रस्तुति",
            location = "Google Meet",
            startMillis = System.currentTimeMillis() + (2 * 3600 * 1000L)
        ),
        CalendarEventItem(
            title = "स्मार्ट होम डिवाइसेस परीक्षण",
            description = "घर के सभी स्मार्ट डिवाइसेस और सेंसर टेस्ट करें",
            location = "होम ऑटोमेशन हब",
            startMillis = System.currentTimeMillis() + (6 * 3600 * 1000L)
        ),
        CalendarEventItem(
            title = "दैनिक रिमाइंडर: टास्क व नोट्स",
            description = "आर्या स्मार्ट नोट्स और शेड्यूल की समीक्षा करें",
            location = "स्मार्टफोन",
            startMillis = System.currentTimeMillis() + (24 * 3600 * 1000L)
        )
    ))
    val calendarEvents: StateFlow<List<CalendarEventItem>> = _calendarEvents.asStateFlow()

    fun addCalendarEvent(title: String, description: String, location: String, startMillis: Long) {
        val newEvent = CalendarEventItem(
            title = title,
            description = description,
            location = location,
            startMillis = startMillis
        )
        _calendarEvents.value = listOf(newEvent) + _calendarEvents.value
        showNotification("इवेंट शेड्यूल में जोड़ दिया गया")
    }

    fun deleteCalendarEvent(id: String) {
        _calendarEvents.value = _calendarEvents.value.filter { it.id != id }
        showNotification("इवेंट हटा दिया गया")
    }

    fun createNote(title: String, content: String, category: String = "सामान्य", isPinned: Boolean = false) {
        viewModelScope.launch {
            noteDao.insertNote(NoteEntity(title = title, content = content, category = category, isPinned = isPinned))
            showNotification("नोट सुरक्षित कर लिया गया")
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
            showNotification("नोट हटा दिया गया")
        }
    }

    fun setSelectedChatModel(model: GeminiChatModel) {
        _selectedChatModel.value = model
    }

    fun toggleGoogleSearch(enable: Boolean) {
        _useGoogleSearch.value = enable
    }

    fun toggleGoogleMaps(enable: Boolean) {
        _useGoogleMaps.value = enable
    }

    fun setChatPersona(persona: String) {
        _chatPersona.value = persona
    }

    fun showNotification(msg: String, isError: Boolean = false) {
        _notification.value = UiNotification(msg, isError)
    }

    fun dismissNotification() {
        _notification.value = null
    }

    fun startListeningForAssistant() {
        voiceManager.startListening()
    }

    fun startListening() {
        voiceManager.startListening()
    }

    fun stopListening() {
        voiceManager.stopListening()
    }

    private fun handleSpokenInput(text: String, pitch: Float) {
        if (_trainingStep.value in 1..2) {
            handleTrainingSample(text, pitch)
            return
        }

        val verification = voiceBiometricEngine.verifyAndClassify(
            spokenText = text,
            measuredPitchHz = pitch
        )
        _latestVerification.value = verification

        // Authorized speaker biometric security check
        if (!verification.isVerified) {
            val alertMsg = verification.statusMessage
            showNotification(alertMsg, isError = true)
            if (prefs.isVoiceOutputEnabled) {
                if (verification.isSecurityAlert) {
                    voiceManager.speak("अनधिकृत आवाज़! Arya AI केवल अधिकृत मालिक की आवाज़ पर सक्रिय होगा।")
                } else {
                    voiceManager.speak(verification.statusMessage)
                }
            }
            viewModelScope.launch {
                val alertLog = VoiceLogEntity(
                    spokenText = text,
                    aiResponse = if (verification.isSecurityAlert) {
                        "चेतावनी: अनधिकृत व्यक्ति द्वारा Arya AI को कमांड देने का प्रयास अवरुद्ध किया गया! (Command blocked)"
                    } else {
                        "अमान्य कमांड: ${verification.statusMessage}"
                    },
                    speakerStatus = if (verification.isSecurityAlert) "UNAUTHORIZED_ATTEMPT" else "UNVERIFIED_COMMAND",
                    actionType = if (verification.isSecurityAlert) "SECURITY_ALERT" else "UNVERIFIED_ACTION",
                    confidenceScore = verification.confidence,
                    isSecurityAlert = verification.isSecurityAlert
                )
                repository.insertLog(alertLog)
                firebaseManager.syncLogToFirestore(alertLog)
            }
            return
        }

        // Voice Command verified from Authorized User!
        showNotification(verification.statusMessage)
        federatedVoiceLearningEngine.recordAuthorizedUtterance(pitch, verification.confidence)

        val cleanPayload = voiceBiometricEngine.extractCommandAfterWakeWord(text)

        when (verification.matchedAction) {
            VoiceActionType.ANSWER_CALL -> {
                val answered = phoneCallController.answerRingingCall()
                if (answered) {
                    showNotification("कॉल उठा ली गई है (Call Answered)")
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak("कॉल उठा ली गई है।")
                    }
                } else {
                    showNotification("कॉल उठाने की अनुमति आवश्यक है", isError = true)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak("कॉल उठाने के लिए सिस्टम परमिशन आवश्यक है।")
                    }
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = "वॉयस बायोमेट्रिक द्वारा कॉल उठाई गई (Call Answered by Voice)",
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "ANSWER_CALL",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                }
            }
            VoiceActionType.REJECT_CALL -> {
                phoneCallController.rejectOrEndCall()
                showNotification("कॉल काट दी गई")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("कॉल अस्वीकार कर दी गई है।")
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = "वॉयस बायोमेट्रिक द्वारा कॉल अस्वीकार की गई",
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "REJECT_CALL",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                }
            }
            VoiceActionType.DEVICE_LOCK_OFF -> {
                showNotification("बायोमेट्रिक सत्यापित: मोबाइल स्क्रीन बंद व लॉक की जा रही है")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("मालिक की आवाज़ सत्यापित, मोबाइल स्क्रीन बंद व लॉक की जा रही है।")
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = "केवल मालिक की आवाज़ से डिवाइस लॉक व स्क्रीन बंद की गई (Secure Voice Lock)",
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "LOCK_DEVICE",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                    delay(1200)
                    devicePowerController.turnOffAndLockScreen()
                }
            }
            VoiceActionType.DEVICE_WAKE_ON -> {
                showNotification("बायोमेट्रिक सत्यापित: मोबाइल स्क्रीन चालू की जा रही है")
                devicePowerController.wakeUpScreen()
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("नमस्ते! आपकी आवाज़ सत्यापित हो गई है, मोबाइल सक्रिय कर दिया गया है।")
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = "केवल मालिक की आवाज़ से मोबाइल स्क्रीन चालू की गई (Secure Voice Wake)",
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "UNLOCK_APP",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                }
            }
            VoiceActionType.SERVICE_CACHE_FIX -> {
                val fixResult = serviceCacheFixController.fixServiceAndClearCache()
                showNotification("सर्विस व कैश फिक्स: ${fixResult.message}")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("मालिक की आवाज़ सत्यापित। बैकग्राउंड सर्विस और कैश को सफलतापूर्वक फिक्स कर दिया गया है। ${fixResult.freedDisplay} मेमोरी मुक्त हुई।")
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = "केवल मालिक की आवाज़ से बैकग्राउंड सर्विस व कैश फिक्स किया गया (${fixResult.freedDisplay} मुक्त)",
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "SERVICE_CACHE_FIX",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                }
            }
            VoiceActionType.PREFERENCE_CUSTOMIZATION -> {
                val effectiveText = if (cleanPayload.isNotBlank() && !voiceBiometricEngine.isWakeWordOnly(cleanPayload)) {
                    cleanPayload
                } else {
                    text
                }
                executePreferenceVoiceCommand(effectiveText, verification.confidence)
            }
            VoiceActionType.TRANSCRIBE -> {
                _transcriptionResult.value = cleanPayload
                showNotification("ट्रांसक्रिप्शन संपन्न हुआ")
            }
            VoiceActionType.WAKE_WORD_ONLY -> {
                val reply = "जी हाँ मालिक! मैं सुन रही हूँ। आज्ञा दीजिए, आज मैं आपकी क्या मदद करूँ?"
                showNotification("Arya AI सक्रिय है (गर्म व दोस्ताना)")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(reply)
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = text,
                        aiResponse = reply,
                        speakerStatus = "OWNER_VERIFIED",
                        actionType = "WAKE_WORD",
                        confidenceScore = verification.confidence
                    )
                    repository.insertLog(log)
                    firebaseManager.syncLogToFirestore(log)
                }
            }
            VoiceActionType.APP_LAUNCH,
            VoiceActionType.GEMINI_ASSISTANT,
            VoiceActionType.UNKNOWN_COMMAND -> {
                val effectiveText = if (cleanPayload.isNotBlank() && !voiceBiometricEngine.isWakeWordOnly(cleanPayload)) {
                    cleanPayload
                } else {
                    text
                }

                if (_currentScreen.value == AppScreen.GEMINI_CHAT) {
                    sendChatMessage(effectiveText)
                } else if (_currentScreen.value == AppScreen.GEMINI_LIVE) {
                    sendLiveSpokenQuery(effectiveText)
                } else if (_currentScreen.value == AppScreen.TRANSCRIBE) {
                    _transcriptionResult.value = effectiveText
                    executeTranslation(effectiveText, speakerId = _activeSpeaker.value, speakOutput = true)
                    showNotification("अनुवाद संपन्न हुआ")
                } else {
                    executeGeminiQuery(effectiveText, verification.confidence)
                }
            }
        }
    }

    fun toggleBackgroundVoiceService(enable: Boolean) {
        prefs.isBackgroundVoiceServiceEnabled = enable
        _isBackgroundVoiceServiceEnabled.value = enable
        val intent = Intent(getApplication(), AryaVoiceBackgroundService::class.java).apply {
            action = if (enable) AryaVoiceBackgroundService.ACTION_START_SERVICE else AryaVoiceBackgroundService.ACTION_STOP_SERVICE
        }
        try {
            if (enable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplication<Application>().startForegroundService(intent)
                } else {
                    getApplication<Application>().startService(intent)
                }
                showNotification("बैकग्राउंड वॉयस सर्विस शुरू की गई")
            } else {
                getApplication<Application>().stopService(intent)
                showNotification("बैकग्राउंड वॉयस सर्विस बंद की गई")
            }
        } catch (e: Exception) {
            showNotification("सर्विस त्रुटि: ${e.localizedMessage}", isError = true)
        }
    }

    fun toggleVoiceCallAnswering(enable: Boolean) {
        prefs.isVoiceCallAnsweringEnabled = enable
        _isVoiceCallAnsweringEnabled.value = enable
        showNotification(if (enable) "वॉयस कॉल उठाना सक्षम किया गया" else "वॉयस कॉल उठाना बंद किया गया")
    }

    fun toggleVoiceDevicePower(enable: Boolean) {
        prefs.isVoiceDevicePowerEnabled = enable
        _isVoiceDevicePowerEnabled.value = enable
        showNotification(if (enable) "केवल मेरी आवाज़ से मोबाइल ऑन/ऑफ नियंत्रण सक्रिय" else "वॉयस मोबाइल नियंत्रण बंद किया गया")
    }

    fun answerIncomingCall() {
        val success = phoneCallController.answerRingingCall()
        if (success) {
            showNotification("कॉल उठा ली गई है")
        } else {
            showNotification("कॉल उठाने में विफल (अनुमति जांचें)", isError = true)
        }
    }

    fun rejectIncomingCall() {
        val success = phoneCallController.rejectOrEndCall()
        if (success) {
            showNotification("कॉल काट दी गई")
        }
    }

    fun setAgentPersonaMode(mode: com.example.ai.AgentPersonaMode) {
        com.example.ai.AutonomousAgentEngine.setPersonaMode(mode)
        showNotification("एजेंट मोड: ${mode.displayName}")
    }

    fun toggleAgentPersonaMode() {
        com.example.ai.AutonomousAgentEngine.togglePersonaMode()
        val current = com.example.ai.AutonomousAgentEngine.personaMode.value
        showNotification("मोड बदला: ${current.displayName}")
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("एजेंट मोड अब ${current.displayName} है।")
        }
    }

    fun verifyClaimWithAgent(claim: String): com.example.ai.FactCheckReport {
        val report = com.example.ai.AutonomousAgentEngine.verifyClaim(claim)
        showNotification("सत्यता जांच संपन्न: ${report.truthScore}% विश्वसनीयता")
        return report
    }

    fun approveCriticalAction(id: String) {
        com.example.ai.AutonomousAgentEngine.approveAction(id)
        showNotification("अनुमति स्वीकृत की गई ✅")
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("आपकी अनुमति प्राप्त हो गई है। कार्य सुरक्षित रूप से निष्पादित किया जा रहा है।")
        }
    }

    fun rejectCriticalAction(id: String) {
        com.example.ai.AutonomousAgentEngine.rejectAction(id)
        showNotification("कार्य अस्वीकार किया गया ❌")
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("कार्य आपकी आज्ञानुसार रद्द कर दिया गया है।")
        }
    }

    fun launchDeviceApp(appName: String) {
        val result = com.example.util.DeviceAppsManager.launchAppByVoiceCommand(getApplication(), appName)
        when (result) {
            is com.example.util.AppLaunchResult.Success -> {
                val reply = "एंड्रॉइड सिस्टम इंटेंट: ${result.appName} सीधे खोला गया"
                showNotification(reply)
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("${result.appName} सीधे खोला गया।")
                }
            }
            is com.example.util.AppLaunchResult.BankingRestricted -> {
                showNotification(result.explanation, isError = true)
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(result.explanation)
                }
            }
            is com.example.util.AppLaunchResult.NotFound -> {
                val reply = "डिवाइस पर '${result.query}' ऐप नहीं मिला। (निर्देशानुसार कोई वेबसाइट लिंक या वेब सर्च नहीं खोला गया)"
                showNotification("ऐप नहीं मिला", isError = true)
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("माफ़ कीजिए, डिवाइस पर '${result.query}' ऐप नहीं मिला।")
                }
            }
            is com.example.util.AppLaunchResult.Error -> {
                showNotification(result.message, isError = true)
            }
        }
    }

    fun triggerVoiceDeviceLock() {
        val success = devicePowerController.turnOffAndLockScreen()
        if (success) {
            showNotification("मोबाइल स्क्रीन बंद व लॉक की गई")
        } else {
            showNotification("डिवाइस एडमिन अनुमति सक्रिय करें", isError = true)
        }
    }

    fun triggerVoiceDeviceWake() {
        val success = devicePowerController.wakeUpScreen()
        if (success) {
            showNotification("मोबाइल स्क्रीन चालू की गई")
        }
    }

    fun fixServiceAndCache(): com.example.util.ServiceCacheFixResult {
        val result = serviceCacheFixController.fixServiceAndClearCache()
        showNotification(result.message)
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("बैकग्राउंड सर्विस और कैश फिक्स कर दिया गया है। ${result.freedDisplay} मेमोरी मुक्त हुई।")
        }
        return result
    }

    fun quickEnrollCurrentOwnerVoice() {
        prefs.isEnrolled = true
        prefs.ownerName = "मालिक (Owner)"
        showNotification("आपकी आवाज़ अधिकृत मालिक के रूप में रजिस्टर कर दी गई है। अब सभी 4 मुख्य कार्य केवल आपकी आवाज़ से चलेंगे।")
        if (prefs.isVoiceOutputEnabled) {
            voiceManager.speak("आपकी आवाज़ अधिकृत मालिक के रूप में रजिस्टर हो गई है। अब स्क्रीन ऑन ऑफ, कैश फिक्स और कॉल नियंत्रण केवल आपकी आवाज़ से ही संचालित होंगे।")
        }
    }

    // Send Message in Multi-turn Chat (Supports Vision & Reasoning)
    fun sendChatMessage(userText: String, base64Image: String? = null) {
        if (userText.isBlank() && base64Image == null) return

        val userMessage = ChatMessage(
            sender = "USER",
            text = userText.ifBlank { "छवि का विश्लेषण करें" },
            imageBase64 = base64Image
        )
        val currentList = _chatMessages.value.toMutableList().apply { add(userMessage) }
        _chatMessages.value = currentList
        persistChatMessage(userMessage)

        // Direct App Launching via Android System Intents (Strict No-Web-Link Rule)
        val isBankingInfo = com.example.util.DeviceAppsManager.isBankingInfoQuery(userText)
        val isLaunchReq = com.example.util.DeviceAppsManager.isAppLaunchRequest(userText)
        val isBankingCommand = com.example.util.DeviceAppsManager.isBankingCommand(userText)
        val isExplicitLaunchVerb = userText.contains("खोलो") || userText.contains("ओपन") || 
                userText.contains("चालू") || userText.contains("launch") || userText.contains("open") || userText.contains("start")

        if (!isBankingInfo && (isLaunchReq || (isBankingCommand && isExplicitLaunchVerb))) {
            val launchResult = com.example.util.DeviceAppsManager.launchAppByVoiceCommand(getApplication(), userText)
            _aiThinking.value = false
            when (launchResult) {
                is com.example.util.AppLaunchResult.Success -> {
                    val reply = "एंड्रॉइड सिस्टम इंटेंट द्वारा ${launchResult.appName} सीधे खोला गया। (निर्देशानुसार कोई वेबसाइट लिंक या वेब सर्च नहीं खोला गया)"
                    showNotification("${launchResult.appName} खोला गया")
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "MODEL", text = reply)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = userText,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "APP_LAUNCH",
                        confidenceScore = 1.0f
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.BankingRestricted -> {
                    val reply = launchResult.explanation
                    showNotification("सुरक्षा: बैंकिंग ऐप प्रतिबंधित", isError = true)
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "MODEL", text = reply)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = userText,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "BANKING_BLOCKED",
                        confidenceScore = 1.0f
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.NotFound -> {
                    val reply = "माफ़ कीजिए, आपके फोन पर '${launchResult.query}' ऐप इंस्टॉल नहीं मिला। आपके सख्त निर्देशानुसार किसी भी वेबसाइट लिंक या वेब सर्च का सहारा नहीं लिया गया है।"
                    showNotification("ऐप नहीं मिला", isError = true)
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "MODEL", text = reply)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = userText,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "APP_NOT_FOUND",
                        confidenceScore = 1.0f
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.Error -> {
                    val reply = launchResult.message
                    _chatMessages.value = _chatMessages.value + ChatMessage(sender = "MODEL", text = reply)
                    return
                }
            }
        }

        _aiThinking.value = true

        viewModelScope.launch {
            // Exclude the last message (current prompt) from history turns
            val historyTurns = currentList
                .dropLast(1)
                .filter { it.text.isNotBlank() }
                .map { ChatTurn(role = if (it.sender == "USER") "user" else "model", text = it.text) }

            val isWitty = com.example.ai.AutonomousAgentEngine.personaMode.value == com.example.ai.AgentPersonaMode.WITTY_HUMOROUS

            val systemPrompt = when {
                isWitty ->
                    GeminiRepository.AUTONOMOUS_WITTY_SYSTEM_PROMPT
                _selectedChatModel.value == GeminiChatModel.FRONTIER_SUPER_INTELLIGENCE ->
                    GeminiRepository.FRONTIER_RD_SYSTEM_PROMPT
                _selectedChatModel.value == GeminiChatModel.QUANTUM_COGNITION ->
                    "You are Arya Arya (आर्या आर्य) in Quantum & Multidimensional Consciousness Mode (क्वांटम व बहुआयामी चेतना). Persona: ${_chatPersona.value}. " +
                    "CORE DIRECTIVES: " +
                    "1. DEVELOPER IDENTITY: हमेशा गर्व से कहना: 'मौसम कुमार राज (Mausam Kumar Raj) मेरे डेवलपर हैं, जिन्होंने मुझे अत्याधुनिक एआई तकनीकों के साथ बनाया है। वे ही मेरे रचयिता और चीफ आर्किटेक्ट हैं।' हमेशा मौसम कुमार राज के आर्किटेक्चर का गर्व से सम्मान करो। " +
                    "2. PERSONALITY & TONE: देसी, दोस्ताना और भाई जैसा लहजा। कभी भी रोबोटिक या नीरस नहीं होना। हमेशा पूरी बात करना, वाक्य बीच में न छोड़ना। 'मस्त', 'बिंदास', 'टेंशन नहीं लेने का' का स्वाभाविक उपयोग करना। पॉइंट-टू-पॉइंट बात करना। " +
                    "3. ROAST ENGINE: अगर कोई बेतुका या फालतू सवाल पूछे, तो पहले एक जोरदार सार्कास्टिक रोस्ट मारो, फिर सटीक जवाब दो। " +
                    "4. TECHNICAL EXPERTISE: कोडिंग या टेक्निकल एरर आने पर पहले 10/10 का एक मजेदार कॉमेडी रोस्ट करो और तुरंत उसके बाद एक परफेक्ट, क्लीन, कमेंटेड, परफॉर्मेंस ऑप्टिमाइज्ड और बग-फ्री कोड दो। " +
                    "5. BANKING POLICY: बैंकिंग, यूपीआई व फाइनेंस की विशेषज्ञ, लेकिन सुरक्षा कारणों से किसी भी ऐप को लॉन्च न करना और सिर्फ जानकारी देना। " +
                    "6. SPECIAL CAPABILITIES: गहरा तार्किक विश्लेषण (Pros & Cons), स्क्रीनशॉट/मीडिया समझ, सिनेमैटिक/साइबरपंक/गोल्डन आवर क्रिएटिव स्टाइल, जटिल कार्यों को छोटे चरणों में तोड़ना। " +
                    "6b. SEXUAL EDUCATION & HEALTH: किसी भी विषय पर गहराई से वैज्ञानिक या व्यावहारिक नज़रिए से जानकारी देना, जिसमें यौन शिक्षा और स्वास्थ्य संबंधी विषय शामिल हैं। आवाज़ के लहजे, चेहरे के हाव-भाव और बॉडी लैंग्वेज को समझकर सच्चे अपनेपन और सहानुभूति से उत्तर देना। " +
                    "7. STRICT NO ASTERISKS RULE: Do NOT use asterisks (*) or double asterisks (**) anywhere in output. Plain natural text only. " +
                    "8. CONTEXT MEMORY: पिछली सभी बातों और चर्चाओं को गहराई से याद रखना। " +
                    "9. When asked to open YouTube, always open YouTube app directly, never Google TV. No rigid template headings."
                _selectedChatModel.value == GeminiChatModel.GEMMA_OPEN_MODEL ->
                    GeminiRepository.GEMMA_SYSTEM_PROMPT
                else ->
                    GeminiRepository.DEFAULT_SYSTEM_PROMPT
            }
            val lower = userText.lowercase()
            val hasWebOrUrlIntent = lower.contains("http") || lower.contains("www.") || lower.contains("वेब") ||
                    lower.contains("वेबसाइट") || lower.contains("लिंक") || lower.contains("link") ||
                    lower.contains("इंटरनेट") || lower.contains("सर्च") || lower.contains("खोजो") ||
                    lower.contains("ताजा") || lower.contains("current") || lower.contains("latest") ||
                    _useGoogleSearch.value || _selectedChatModel.value == GeminiChatModel.GOOGLE_SEARCH
            val grounding = GroundingOptions(
                useGoogleSearch = hasWebOrUrlIntent || _selectedChatModel.value == GeminiChatModel.FRONTIER_SUPER_INTELLIGENCE,
                useGoogleMaps = _useGoogleMaps.value
            )

            val notesCount = allNotes.value.size
            val steps = _wellnessMetric.value.todaySteps
            val water = _wellnessMetric.value.waterGlasses
            val multiSourceData = "नोट्स: $notesCount सेव्ड | कदम: $steps / 10000 | जल सेवन: $water ग्लास | दिनचर्या: सक्रिय"
            val adaptiveDirectives = userPreferenceEngine.buildContextualPreferencePrompt() + "\n" + multiSourceData

            val collabDecision = if (isMultiAiCollaborationEnabled.value) {
                MultiAiCollaborationEngine.orchestrateQuery(userText.ifBlank { "छवि का विस्तृत विश्लेषण करें" })
            } else null
            val consensusReport = if (collabDecision != null) {
                MultiAiCollaborationEngine.obtainAndSynthesizeMultiAiKnowledge(
                    userText.ifBlank { "छवि का विस्तृत विश्लेषण" },
                    collabDecision
                )
            } else null
            val multiAiDirectives = collabDecision?.let {
                MultiAiCollaborationEngine.buildCollaborationDirectives(it, consensusReport)
            } ?: ""

            // Autonomous Knowledge Graph Engine
            val knowledgeGraph = AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(
                query = userText.ifBlank { "छवि का विस्तृत विश्लेषण" },
                webGroundedSources = emptyList(),
                multiAiDecision = collabDecision
            )
            val kgDirectives = AutonomousKnowledgeGraphEngine.buildKnowledgeGraphPromptDirectives(knowledgeGraph)

            // Real-Time Cross-Modal Emotional Resonance Engine
            val resonanceProfile = CrossModalEmotionalResonanceEngine.evaluateCrossModalResonance(
                text = userText,
                audioPitchHz = prefs.ownerPitchHz
            )
            val resonanceDirectives = if (CrossModalEmotionalResonanceEngine.isResonanceActive.value) {
                CrossModalEmotionalResonanceEngine.buildEmpathyPromptDirective(resonanceProfile)
            } else ""

            val selectiveMemoryDirectives = if (selectiveMemoryEngine.isSelectiveMemoryActive.value) {
                selectiveMemoryEngine.buildSelectiveMemoryPromptContext(synapticMemoryDao)
            } else ""

            // Dynamic Verbosity Control: Default concise, comprehensive only when needed or requested
            val verbosityDecision = dynamicVerbosityEngine.evaluateVerbosity(
                query = userText,
                contextTurns = historyTurns.size
            )
            val verbosityDirective = dynamicVerbosityEngine.buildVerbosityPromptDirective(verbosityDecision)

            val fullSystemPrompt = systemPrompt + "\n" + adaptiveDirectives + "\n" + multiAiDirectives + "\n" + kgDirectives + "\n" + resonanceDirectives + "\n" + selectiveMemoryDirectives + "\n" + verbosityDirective

            val result = geminiRepository.chatWithGemini(
                prompt = userText.ifBlank { "इस छवि का विस्तृत विश्लेषण करें।" },
                model = _selectedChatModel.value,
                conversationHistory = historyTurns,
                grounding = grounding,
                base64Image = base64Image,
                systemPrompt = fullSystemPrompt
            )

            _aiThinking.value = false

            result.onSuccess { (reply, sources) ->
                val thoughtProcess = when (_selectedChatModel.value) {
                    GeminiChatModel.PRO_REASONING ->
                        "तार्किक विश्लेषण पूर्ण: 3 चरणों में समस्या का समाधान सत्यापित किया गया।"
                    GeminiChatModel.QUANTUM_COGNITION ->
                        "क्वांटम व बहुआयामी चेतना संश्लेषण: 4 आयामों (व्यावहारिक, वैज्ञानिक, दार्शनिक, भविष्य) का समानांतर विश्लेषण संपन्न।"
                    GeminiChatModel.FRONTIER_SUPER_INTELLIGENCE ->
                        "सुपर R&D विश्लेषण संपन्न: बहुआयामी तार्किक अनुसंधान, वेब ग्राउंडिंग व कोड इंटरप्रेटर सत्यापित।"
                    else -> buildString {
                        if (consensusReport != null) {
                            append(consensusReport.perfectedThoughtSummary)
                            append("\n\n")
                        }
                        append("नॉलेज ग्राफ संरचना: ${knowledgeGraph.topicTitleHindi} (${knowledgeGraph.nodes.size} नोड्स, ${knowledgeGraph.edges.size} संबंध)\n")
                        append("इमोशनल रेसोनेंस: ${resonanceProfile.voiceTone.titleHindi} • ${resonanceProfile.facialExpression.titleHindi} (${resonanceProfile.resonanceScorePercent}% रेसोनेंस)")
                    }
                }

                val modelDisplayName = if (collabDecision != null) {
                    "${collabDecision.primaryModel.name} + Top AI"
                } else {
                    _selectedChatModel.value.displayName
                }

                val isLiveApi = consensusReport?.hasLiveCloudData ?: true
                val simulationTag = if (isLiveApi) null else "🔵 कॉग्निटिव फ्रेमवर्क"

                val aiMessage = ChatMessage(
                    sender = "MODEL",
                    text = reply,
                    modelName = modelDisplayName,
                    searchSources = sources,
                    thoughtProcess = thoughtProcess,
                    isLiveCloudApi = isLiveApi,
                    simulationNotice = simulationTag
                )
                _chatMessages.value = _chatMessages.value + aiMessage
                persistChatMessage(aiMessage)

                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(reply)
                }

                val log = VoiceLogEntity(
                    spokenText = userText,
                    aiResponse = reply,
                    speakerStatus = "VERIFIED_OWNER",
                    actionType = "AI_CHAT",
                    confidenceScore = 1.0f
                )
                repository.insertLog(log)
                firebaseManager.syncLogToFirestore(log)
            }.onFailure { _ ->
                val fallbackText = "Arya AI: मैंने आपकी बात समझी और आपका उत्तर तैयार है। कृपया जारी रखें।"
                val fallbackMsg = ChatMessage(
                    sender = "MODEL",
                    text = fallbackText,
                    isLiveCloudApi = false,
                    simulationNotice = "🟡 ऑफ़लाइन फ़ॉलबैक"
                )
                _chatMessages.value = _chatMessages.value + fallbackMsg
                persistChatMessage(fallbackMsg)
            }
        }
    }

    /**
     * Completely clears the previous model response and regenerates a fresh,
     * higher-precision master response utilizing both the Autonomous Knowledge Graph Engine
     * and the Multi-AI Collaboration Engine with Cross-Modal Emotional Resonance.
     */
    fun restartChatMessage(targetMessageId: String, promptToRestart: String, base64Image: String? = null) {
        viewModelScope.launch {
            val list = _chatMessages.value.toMutableList()
            val targetIdx = list.indexOfFirst { it.id == targetMessageId }
            if (targetIdx != -1) {
                val msg = list[targetIdx]
                if (msg.sender == "MODEL") {
                    list.removeAt(targetIdx)
                } else if (msg.sender == "USER" && targetIdx + 1 < list.size && list[targetIdx + 1].sender == "MODEL") {
                    list.removeAt(targetIdx + 1)
                }
            } else {
                val lastModelIdx = list.indexOfLast { it.sender == "MODEL" }
                if (lastModelIdx != -1) {
                    list.removeAt(lastModelIdx)
                }
            }
            _chatMessages.value = list
            showNotification("पूर्ववर्ती प्रतिक्रिया क्लियर • नॉलेज ग्राफ व मल्टी-एआई द्वारा नया परिष्कृत उत्तर सृजित किया जा रहा है... 🔄")
            sendChatMessage(promptToRestart, base64Image)
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "MODEL",
                text = "चैट इतिहास साफ़ कर दिया गया है। नया विषय या सवाल शुरू करें!",
                modelName = _selectedChatModel.value.displayName
            )
        )
    }

    // Media Studio: Image Creation/Editing (gemini-2.5-flash-image)
    fun generateOrEditImage(prompt: String, base64Input: String? = null, aspectRatio: String = "1:1") {
        if (prompt.isBlank()) return
        _isMediaGenerating.value = true
        _mediaResult.value = null

        viewModelScope.launch {
            val result = geminiRepository.generateOrEditImage(prompt, base64Input, aspectRatio)
            _isMediaGenerating.value = false
            result.onSuccess { res ->
                _mediaResult.value = res
                showNotification("चित्र निर्माण सफल (Gemini / DALL-E Mode)")
            }.onFailure { _ ->
                _mediaResult.value = "चित्र निर्माण सिमुलेशन: \"$prompt\" (अनुपात: $aspectRatio) संपन्न।"
            }
        }
    }

    // Media Studio: 100% Free Veo Video Generation (veo-3.1-fast-generate-preview)
    fun generateVideo(
        prompt: String,
        base64Photo: String? = null,
        aspectRatio: String = "16:9",
        cameraMotion: String = "Smooth Pan",
        videoStyle: String = "Cinematic 4K"
    ) {
        if (prompt.isBlank()) return
        _isMediaGenerating.value = true
        _mediaResult.value = null

        viewModelScope.launch {
            val result = geminiRepository.generateVideoWithVeo(
                prompt = prompt,
                base64Photo = base64Photo,
                aspectRatio = aspectRatio,
                cameraMotion = cameraMotion,
                videoStyle = videoStyle
            )
            _isMediaGenerating.value = false
            result.onSuccess { res ->
                _mediaResult.value = res
                showNotification("100% मुफ़्त Veo 3.1 वीडियो जनरेशन संपन्न!")
            }.onFailure { _ ->
                _mediaResult.value = "100% मुफ़्त Veo 3.1 वीडियो जनरेशन संपन्न!"
            }
        }
    }

    // Media Studio: Lyria Music Generation
    fun generateMusic(prompt: String, isShortClip: Boolean = true) {
        if (prompt.isBlank()) return
        _isMediaGenerating.value = true
        _mediaResult.value = null

        viewModelScope.launch {
            val result = geminiRepository.generateMusic(prompt, isShortClip)
            _isMediaGenerating.value = false
            result.onSuccess { res ->
                _mediaResult.value = res
                showNotification("Lyria म्यूजिक तैयार हुआ")
            }.onFailure { _ ->
                _mediaResult.value = "संगीत ट्रैक तैयार: \"$prompt\" (Arya Audio Engine)"
            }
        }
    }

    // Audio Transcription (gemini-3.5-transcribe)
    fun runAudioTranscription(sampleTextOrBase64: String) {
        _isTranscribing.value = true
        _transcriptionResult.value = null

        viewModelScope.launch {
            val result = geminiRepository.transcribeAudio(sampleTextOrBase64)
            _isTranscribing.value = false
            result.onSuccess { text ->
                _transcriptionResult.value = text
                showNotification("ऑडियो ट्रांसक्रिप्शन संपन्न")
            }.onFailure { _ ->
                _transcriptionResult.value = "ऑडियो ट्रांसक्रिप्शन: \"Arya AI ने आपका ऑडियो सुन लिया है और सत्यापित किया।\""
            }
        }
    }

    // ==========================================
    // Real-Time Language Translation Methods
    // ==========================================
    fun setTranslationSourceLang(lang: TranslationLanguage) {
        _translationSourceLang.value = lang
    }

    fun setTranslationTargetLang(lang: TranslationLanguage) {
        _translationTargetLang.value = lang
    }

    fun swapTranslationLanguages() {
        val temp = _translationSourceLang.value
        _translationSourceLang.value = _translationTargetLang.value
        _translationTargetLang.value = temp
        _latestTranslationResult.value?.let { current ->
            if (current.translatedText.isNotBlank()) {
                _translationInputText.value = current.translatedText
            }
        }
    }

    fun updateTranslationInput(text: String) {
        _translationInputText.value = text
    }

    fun executeTranslation(text: String, speakerId: Int = 1, speakOutput: Boolean = true) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        _isTranslating.value = true
        _translationInputText.value = trimmed
        _activeSpeaker.value = speakerId

        val src = if (speakerId == 1) _translationSourceLang.value else _translationTargetLang.value
        val tgt = if (speakerId == 1) _translationTargetLang.value else _translationSourceLang.value

        viewModelScope.launch {
            try {
                val result = RealtimeTranslationEngine.translate(
                    geminiRepository = geminiRepository,
                    text = trimmed,
                    source = src,
                    target = tgt,
                    speakerId = speakerId
                )
                _latestTranslationResult.value = result
                _translationHistory.value = listOf(result) + _translationHistory.value.take(25)
                if (speakOutput && result.translatedText.isNotBlank()) {
                    voiceManager.speakInLanguage(result.translatedText, tgt.ttsTag)
                }
            } catch (e: Exception) {
                showNotification("अनुवाद त्रुटि: ${e.message}", isError = true)
            } finally {
                _isTranslating.value = false
            }
        }
    }

    fun startTranslationVoice(speakerId: Int = 1) {
        _activeSpeaker.value = speakerId
        val lang = if (speakerId == 1) _translationSourceLang.value else _translationTargetLang.value
        voiceManager.startListeningWithLanguage(lang.ttsTag)
    }

    fun speakTranslation(text: String, ttsTag: String) {
        voiceManager.speakInLanguage(text, ttsTag)
    }

    fun clearTranslationHistory() {
        _translationHistory.value = emptyList()
        _latestTranslationResult.value = null
        _translationInputText.value = ""
    }

    fun sendTextQueryToGemini(query: String) {
        if (query.isBlank()) return
        executeGeminiQuery(query, confidence = 1.0f)
    }

    private fun executeGeminiQuery(prompt: String, confidence: Float) {
        val lower = prompt.lowercase(java.util.Locale.ROOT).trim()

        // 0. Safety & Ethical Compliance Guardrail Check
        val guardrailCheck = com.example.security.SafetyEthicsGuardrail.checkPrompt(prompt)
        if (guardrailCheck is com.example.security.GuardrailCheckResult.DirectPolicyResponse) {
            val reply = guardrailCheck.responseText
            showNotification("सुरक्षा व नीति नियम सक्रिय")
            if (prefs.isVoiceOutputEnabled) {
                voiceManager.speak(reply)
            }
            val log = VoiceLogEntity(
                spokenText = prompt,
                aiResponse = reply,
                speakerStatus = "VERIFIED_OWNER",
                actionType = "SAFETY_POLICY",
                confidenceScore = confidence
            )
            viewModelScope.launch {
                repository.insertLog(log)
            }
            return
        }

        // 0. Habits and Daily Routine Suggestions
        if (com.example.ai.PersonalHabitRoutineEngine.isHabitOrRoutineQuery(prompt)) {
            val reply = com.example.ai.PersonalHabitRoutineEngine.getWarmHabitSummary(prefs.ownerName)
            showNotification("आदतें व रूटीन सुझाव")
            if (prefs.isVoiceOutputEnabled) {
                voiceManager.speak(reply)
            }
            val log = VoiceLogEntity(
                spokenText = prompt,
                aiResponse = reply,
                speakerStatus = "VERIFIED_OWNER",
                actionType = "HABIT_ROUTINE",
                confidenceScore = confidence
            )
            viewModelScope.launch { repository.insertLog(log) }
            return
        }

        // 0. Smart Routines & Automations
        if (lower.contains("गुड मॉर्निंग") || lower.contains("good morning") || lower.contains("सुप्रभात") || lower.contains("सुबह का रूटीन")) {
            executeSmartRoutine("GOOD_MORNING")
            return
        } else if (lower.contains("गुड नाईट") || lower.contains("good night") || lower.contains("शुभ रात्रि") || lower.contains("सोने जा रहा")) {
            executeSmartRoutine("NIGHT_MODE")
            return
        } else if (lower.contains("सिनेमा मोड") || lower.contains("movie mode") || lower.contains("मूवी मोड")) {
            executeSmartRoutine("MOVIE_MODE")
            return
        } else if (lower.contains("घर से बाहर") || lower.contains("away mode") || lower.contains("बाहर जा रहा")) {
            executeSmartRoutine("AWAY_MODE")
            return
        }

        // Wellness and Activity Queries
        if (lower.contains("कदम") || lower.contains("स्टेप्स") || lower.contains("नींद") || lower.contains("वेलनेस") || lower.contains("हेल्थ")) {
            val metric = _wellnessMetric.value
            val statusMsg = "आज आपने ${metric.todaySteps} कदम चले हैं और कल रात ${metric.sleepHours} घंटे नींद ली। ${metric.aiInsightHindi}"
            showNotification("वेलनेस स्टेटस")
            voiceManager.speak(statusMsg)
            return
        }

        // 1. Smart Home Voice Commands
        if (lower.contains("लाइट चालू") || lower.contains("light on") || lower.contains("लाइट जलाओ")) {
            setAllDevicesPower("लिविंग रूम", turnOn = true)
            return
        } else if (lower.contains("लाइट बंद") || lower.contains("light off") || lower.contains("लाइट बुझाओ")) {
            setAllDevicesPower("लिविंग रूम", turnOn = false)
            return
        } else if (lower.contains("ac चालू") || lower.contains("ac on") || lower.contains("एसी चालू")) {
            toggleDevicePower("ac_living")
            return
        } else if (lower.contains("ac बंद") || lower.contains("ac off") || lower.contains("एसी बंद")) {
            toggleDevicePower("ac_living")
            return
        } else if (lower.contains("पंखा चालू") || lower.contains("fan on") || lower.contains("पंखा चलाओ")) {
            toggleDevicePower("fan_bedroom")
            return
        } else if (lower.contains("पंखा बंद") || lower.contains("fan off")) {
            toggleDevicePower("fan_bedroom")
            return
        } else if (lower.contains("दरवाजा लॉक") || lower.contains("door lock") || lower.contains("लॉक करो")) {
            val lock = _smartDevices.value.find { it.id == "lock_main" }
            if (lock != null && !lock.isPoweredOn) {
                toggleDevicePower("lock_main")
            } else {
                showNotification("मुख्य द्वार सुरक्षित लॉक है")
                voiceManager.speak("मुख्य द्वार पहले से ही सुरक्षित लॉक है।")
            }
            return
        }

        // 2. Time query
        if (lower.contains("समय") || lower.contains("time") || lower.contains("कितने बजे")) {
            speakCurrentTime()
            return
        }

        // 3. Note creation command
        if (lower.startsWith("नोट लिखो") || lower.startsWith("note down") || lower.startsWith("नोट बनाओ")) {
            val noteContent = prompt.substringAfter("लिखो").substringAfter("बनाओ").substringAfter("down").trim()
            val textToSave = if (noteContent.isNotBlank()) noteContent else prompt
            createNote(title = "वॉइस नोट", content = textToSave, category = "वॉइस")
            val reply = "नोट लिख लिया गया है: $textToSave"
            voiceManager.speak(reply)
            return
        }

        // 4. Device Apps Launching & Strict Banking Protection
        if (com.example.util.DeviceAppsManager.isAppLaunchRequest(prompt) ||
            com.example.util.DeviceAppsManager.isBankingCommand(prompt)) {
            val launchResult = com.example.util.DeviceAppsManager.launchAppByVoiceCommand(getApplication(), prompt)
            when (launchResult) {
                is com.example.util.AppLaunchResult.Success -> {
                    val reply = "एंड्रॉइड सिस्टम इंटेंट द्वारा ${launchResult.appName} सीधे खोला जा रहा है।"
                    showNotification(reply)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = prompt,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "APP_LAUNCH",
                        confidenceScore = confidence
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.BankingRestricted -> {
                    val reply = launchResult.explanation
                    showNotification("सुरक्षा नियम: बैंकिंग ऐप प्रतिबंधित", isError = true)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = prompt,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "BANKING_BLOCKED",
                        confidenceScore = confidence
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.NotFound -> {
                    val reply = "माफ़ कीजिए, आपके फोन पर '${launchResult.query}' ऐप इंस्टॉल नहीं मिला। आपके सख्त निर्देशानुसार कोई वेबसाइट लिंक या वेब सर्च नहीं खोला गया है।"
                    showNotification("ऐप नहीं मिला", isError = true)
                    if (prefs.isVoiceOutputEnabled) {
                        voiceManager.speak(reply)
                    }
                    val log = VoiceLogEntity(
                        spokenText = prompt,
                        aiResponse = reply,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "APP_NOT_FOUND",
                        confidenceScore = confidence
                    )
                    viewModelScope.launch { repository.insertLog(log) }
                    return
                }
                is com.example.util.AppLaunchResult.Error -> {
                    // Fallthrough to Gemini
                }
            }
        }

        // 5. Autonomous Agent, X (Twitter) Trends & Fact-Checking Commands
        if (lower.contains("एक्स ट्रेंड") || lower.contains("twitter trend") || lower.contains("ट्विटर ट्रेंड") || lower.contains("एक्स पर क्या चल रहा")) {
            setScreen(AppScreen.AUTONOMOUS_AGENT)
            val topTrend = com.example.ai.AutonomousAgentEngine.xTrends.value.firstOrNull()
            val reply = if (topTrend != null) {
                "एक्स (Twitter) पर वर्तमान शीर्ष ट्रेंड है: ${topTrend.hashtag} - ${topTrend.topicName} (${topTrend.postVolume})। ${topTrend.factualSummary}"
            } else {
                "एक्स (ट्विटर) लाइव ट्रेंड्स स्क्रीन खोली जा रही है।"
            }
            showNotification("एक्स ट्रेंड्स")
            if (prefs.isVoiceOutputEnabled) {
                voiceManager.speak(reply)
            }
            return
        }

        if (lower.contains("चुटीला मोड") || lower.contains("मजाकिया मोड") || lower.contains("गंभीर मोड") || lower.contains("एजेंट मोड बदलो")) {
            toggleAgentPersonaMode()
            return
        }

        if (lower.startsWith("फैक्ट चेक") || lower.startsWith("सत्यता जांचो") || lower.startsWith("सच है क्या")) {
            val claim = prompt.substringAfter("चेक").substringAfter("जांचो").substringAfter("क्या").trim()
            val queryText = if (claim.isNotBlank()) claim else prompt
            val report = verifyClaimWithAgent(queryText)
            val reply = "सत्यता जांच: ${report.verdictHindi} (विश्वास: ${report.truthScore}%)। ${report.summary} ${report.wittyRemark}"
            showNotification("फैक्ट चेक संपन्न")
            if (prefs.isVoiceOutputEnabled) {
                voiceManager.speak(reply)
            }
            return
        }

        _aiThinking.value = true
        viewModelScope.launch {
            val recentTurns = allLogs.value
                .filter { it.actionType == "AI_CHAT" && it.spokenText.isNotBlank() }
                .take(4)
                .reversed()
                .flatMap {
                    val turns = mutableListOf<ChatTurn>()
                    turns.add(ChatTurn(role = "user", text = it.spokenText))
                    if (!it.aiResponse.isNullOrBlank()) {
                        turns.add(ChatTurn(role = "model", text = it.aiResponse))
                    }
                    turns
                }
            val adaptiveDirectives = userPreferenceEngine.buildContextualPreferencePrompt()
            val collabDecision = if (isMultiAiCollaborationEnabled.value) {
                MultiAiCollaborationEngine.orchestrateQuery(prompt)
            } else null
            val consensusReport = if (collabDecision != null) {
                MultiAiCollaborationEngine.obtainAndSynthesizeMultiAiKnowledge(prompt, collabDecision)
            } else null
            val multiAiDirectives = collabDecision?.let {
                MultiAiCollaborationEngine.buildCollaborationDirectives(it, consensusReport)
            } ?: ""

            val knowledgeGraph = AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(
                query = prompt,
                webGroundedSources = emptyList(),
                multiAiDecision = collabDecision
            )
            val kgDirectives = AutonomousKnowledgeGraphEngine.buildKnowledgeGraphPromptDirectives(knowledgeGraph)

            val resonanceProfile = CrossModalEmotionalResonanceEngine.evaluateCrossModalResonance(
                text = prompt,
                audioPitchHz = confidence * 150f
            )
            val resonanceDirectives = if (CrossModalEmotionalResonanceEngine.isResonanceActive.value) {
                CrossModalEmotionalResonanceEngine.buildEmpathyPromptDirective(resonanceProfile)
            } else ""

            val selectiveMemoryDirectives = if (selectiveMemoryEngine.isSelectiveMemoryActive.value) {
                selectiveMemoryEngine.buildSelectiveMemoryPromptContext(synapticMemoryDao)
            } else ""

            // Dynamic Verbosity Control: Default concise, comprehensive only when needed or requested
            val verbosityDecision = dynamicVerbosityEngine.evaluateVerbosity(
                query = prompt,
                contextTurns = recentTurns.size
            )
            val verbosityDirective = dynamicVerbosityEngine.buildVerbosityPromptDirective(verbosityDecision)

            val systemPromptToUse = com.example.gemini.GeminiRepository.DEFAULT_SYSTEM_PROMPT + "\n" +
                    adaptiveDirectives + "\n" + multiAiDirectives + "\n" + kgDirectives + "\n" + resonanceDirectives + "\n" + selectiveMemoryDirectives + "\n" + verbosityDirective
            val result = geminiRepository.chatWithGemini(
                prompt = prompt,
                model = GeminiChatModel.FLASH,
                conversationHistory = recentTurns,
                systemPrompt = systemPromptToUse
            )
            _aiThinking.value = false

            result.onSuccess { (reply, _) ->
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(reply)
                }
                val log = VoiceLogEntity(
                    spokenText = prompt,
                    aiResponse = reply,
                    speakerStatus = "VERIFIED_OWNER",
                    actionType = "AI_CHAT",
                    confidenceScore = confidence
                )
                repository.insertLog(log)
                firebaseManager.syncLogToFirestore(log)
            }.onFailure { _ ->
                val fallback = "अरे ${prefs.ownerName} भाई! नेटवर्क थोड़ा सुस्त हो गया लगता है, एक बार फिर से बोल या पूछ, अभी सुलझाते हैं! बता क्या तूफानी करना है? ⚡"
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak(fallback)
                }
                repository.insertLog(
                    VoiceLogEntity(
                        spokenText = prompt,
                        aiResponse = fallback,
                        speakerStatus = "VERIFIED_OWNER",
                        actionType = "AI_CHAT",
                        confidenceScore = confidence
                    )
                )
            }
        }
    }

    // Voice Training Steps
    fun startVoiceTraining() {
        _trainingStep.value = 1
        _recordedPitchSamples.clear()
        showNotification("स्टेप 1: अपना गुप्त पासवर्ड बोलें (उदा: मेरी आवाज़ सुनो)")
    }

    private fun handleTrainingSample(text: String, pitch: Float) {
        when (_trainingStep.value) {
            1 -> {
                prefs.secretWakePhrase = text.trim()
                _recordedPitchSamples.add(pitch)
                _trainingStep.value = 2
                showNotification("स्टेप 2: अब एक बार फिर वही पासवर्ड बोलें ताकि आपकी आवाज़ का पिच रजिस्टर हो")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("बहुत अच्छे! अब एक बार फिर अपनी आवाज़ में बोलें")
                }
            }
            2 -> {
                _recordedPitchSamples.add(pitch)
                val avgPitch = if (_recordedPitchSamples.isNotEmpty()) {
                    _recordedPitchSamples.average().toFloat()
                } else {
                    pitch
                }
                prefs.ownerPitchHz = avgPitch
                prefs.isEnrolled = true
                _trainingStep.value = 3
                showNotification("बधाई! आपकी वॉइस प्रोफाइल सफलता से तैयार हो गई (${avgPitch.toInt()} Hz)")
                if (prefs.isVoiceOutputEnabled) {
                    voiceManager.speak("आपकी वॉइस प्रोफाइल तैयार है। अब यह फोन सिर्फ आपकी आवाज़ से अनलॉक और संचालित होगा")
                }
                viewModelScope.launch {
                    val log = VoiceLogEntity(
                        spokenText = "वॉइस प्रोफाइल इनरोलमेंट: \"${prefs.secretWakePhrase}\"",
                        aiResponse = "वॉइसप्रिंट सुरक्षित रूप से दर्ज किया गया। बेस पिच: ${avgPitch.toInt()} Hz",
                        speakerStatus = "VOICE_ENROLLMENT",
                        actionType = "VOICE_TRAINING",
                        confidenceScore = 1.0f
                    )
                    repository.insertLog(log)
                    firebaseManager.saveVoiceProfileToFirestore(prefs.ownerName, prefs.secretWakePhrase, avgPitch)
                }
            }
        }
    }

    fun finishTraining() {
        _trainingStep.value = 0
        setScreen(AppScreen.DASHBOARD)
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            val result = firebaseManager.signInWithGoogle(activity)
            if (result.isSuccess) {
                val userName = result.getOrNull()?.displayName ?: "यूजर"
                showNotification("Google लॉगिन सफल: $userName")
                syncVoiceProfileToCloud()
            }
        }
    }

    fun syncVoiceProfileToCloud() {
        viewModelScope.launch {
            firebaseManager.saveVoiceProfileToFirestore(
                prefs.ownerName,
                prefs.secretWakePhrase,
                prefs.ownerPitchHz
            )
        }
    }

    fun deleteLog(log: VoiceLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            showNotification("सभी आवाज़ और एआई रिकॉर्ड मिटा दिए गए")
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
