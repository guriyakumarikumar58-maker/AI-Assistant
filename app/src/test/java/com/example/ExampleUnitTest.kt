package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTopAiModelsAvailable() {
    val models = com.example.ai.MultiAiCollaborationEngine.top10Models
    assertEquals(9, models.size)
    assertEquals("gemini-3.8-flash", models[0].id)
    assertEquals("gpt-4o", models[1].id)
    assertEquals("openai-o1", models[2].id)
  }

  @Test
  fun testInterAiDirectDialogueAndDataPacketExchange() {
    val modelA = com.example.ai.MultiAiCollaborationEngine.getModelById("gemini-3.8-flash")
    val modelB = com.example.ai.MultiAiCollaborationEngine.getModelById("gpt-4o")

    val session = com.example.ai.MultiAiCollaborationEngine.conductDirectAssistantDialogue(
      modelA = modelA,
      modelB = modelB,
      topic = "क्वांटम कंप्यूटिंग",
      exchangeType = com.example.ai.PeerExchangeType.TECHNICAL_DEBATE
    )

    assertNotNull(session)
    assertEquals(4, session.dialogueTurns.size)
    assertTrue(session.consensusDecision.isNotBlank())
    assertFalse("Consensus must not contain asterisks", session.consensusDecision.contains("*"))
    assertEquals(100, session.confidenceScore)

    val packets = com.example.ai.MultiAiCollaborationEngine.dataPackets.value
    assertTrue("At least one verified data packet must be recorded", packets.isNotEmpty())
    assertTrue(packets.last().signatureVerified)
  }

  @Test
  fun testMultiAiThreeStageConsensusPipeline() {
    val query = "क्विकसॉर्ट अल्गोरिदम की टाइम कॉम्प्लेक्सिटी व कोड लिखो"
    val decision = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(query)
    assertEquals("deepseek-r1", decision.primaryModel.id)

    val report = com.example.ai.MultiAiCollaborationEngine.obtainAndSynthesizeMultiAiKnowledge(query, decision)
    assertNotNull(report)
    assertTrue("Must have candidate insights from domain squad", report.candidateInsights.size >= 3)
    assertEquals(decision.primaryModel.id, report.selectedWinningModel.id)
    assertTrue(report.candidateInsights.any { it.isSelectedWinningBase })
    assertTrue("Accuracy score must be high", report.candidateInsights.first().accuracyScore >= 95)
    assertTrue("Thought summary must contain multi-stage breakdown", report.perfectedThoughtSummary.contains("डेटा अर्जन"))
    assertFalse("Thought summary must never contain asterisks", report.perfectedThoughtSummary.contains("*"))

    val directives = com.example.ai.MultiAiCollaborationEngine.buildCollaborationDirectives(decision, report)
    assertTrue(directives.contains("मल्टी-एआई कोलैबोरेशन इंजन"))
    assertTrue(directives.contains("मौसम कुमार राज"))
    assertFalse("Directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testDirectModelTargetedRouting() {
    val deepSeekQuery = "DeepSeek से पूछो यह बग कैसे फिक्स करें"
    val decisionDeepSeek = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(deepSeekQuery)
    assertEquals("deepseek-r1", decisionDeepSeek.primaryModel.id)

    val o1Query = "OpenAI o1 से जटिल गणितीय प्रमाण मांगो"
    val decisionO1 = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(o1Query)
    assertEquals("openai-o1", decisionO1.primaryModel.id)
  }

  @Test
  fun testAutonomousKnowledgeGraphEngine() {
    val query = "क्वांटम कंप्यूटिंग और क्रिप्टोग्राफी में गहरे संबंध समझाओ"
    val graph = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(query)
    assertNotNull(graph)
    assertTrue("Must have at least 5 structured nodes", graph.nodes.size >= 5)
    assertTrue("Must have interconnected edges", graph.edges.size >= 4)
    assertTrue(graph.structuredSynthesisHindi.contains("ऑटोनॉमस नॉलेज ग्राफ"))
    assertFalse("Summary must not contain asterisks", graph.structuredSynthesisHindi.contains("*"))

    val directives = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraphPromptDirectives(graph)
    assertTrue(directives.contains("AUTONOMOUS KNOWLEDGE GRAPH ENGINE"))
    assertFalse("Directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testCrossModalEmotionalResonanceEngine() {
    val stressedQuery = "मैं आज बहुत ज्यादा थक गया हूँ और थोड़ा परेशान हूँ"
    val profile = com.example.ai.CrossModalEmotionalResonanceEngine.evaluateCrossModalResonance(
      text = stressedQuery,
      audioPitchHz = 110f
    )
    assertNotNull(profile)
    assertEquals(com.example.ai.VoiceTone.TIRED_EXHAUSTED, profile.voiceTone)
    assertEquals(com.example.ai.FacialExpression.TIRED_DROOPING, profile.facialExpression)
    assertEquals(com.example.ai.BodyLanguageState.SLUMPED_TIRED, profile.bodyLanguage)
    assertTrue(profile.resonanceScorePercent >= 95)

    val directives = com.example.ai.CrossModalEmotionalResonanceEngine.buildEmpathyPromptDirective(profile)
    assertTrue(directives.contains("CROSS-MODAL EMOTIONAL RESONANCE"))
    assertTrue(directives.contains("सच्चा अपनापन"))
    assertFalse("Resonance directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testHealthAndSexualEducationKnowledgeGraph() {
    val query = "यौन शिक्षा और प्रजनन स्वास्थ्य के वैज्ञानिक नियम समझाओ"
    val graph = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(query)
    assertNotNull(graph)
    assertEquals("यौन शिक्षा, प्रजनन स्वास्थ्य व व्यावहारिक वेलनेस", graph.topicTitleHindi)
    assertTrue("Must have 5 dedicated nodes", graph.nodes.size == 5)
    assertTrue("Must have 5 relational edges", graph.edges.size == 5)
    assertTrue(graph.deepRelationalSummary.contains("यौन शिक्षा और स्वास्थ्य"))
    assertFalse("Summary must not contain asterisks", graph.structuredSynthesisHindi.contains("*"))

    val directives = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraphPromptDirectives(graph)
    assertTrue(directives.contains("यौन शिक्षा और स्वास्थ्य संबंधी विषय"))
    assertFalse("Directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testHesitantCrossModalResonanceForSensitiveTopics() {
    val sensitiveQuery = "मुझे इस व्यक्तिगत स्वास्थ्य और यौन विषय पर थोड़ी झिझक व संकोच हो रहा है"
    val profile = com.example.ai.CrossModalEmotionalResonanceEngine.evaluateCrossModalResonance(
      text = sensitiveQuery
    )
    assertNotNull(profile)
    assertEquals(com.example.ai.VoiceTone.HESITANT_VULNERABLE, profile.voiceTone)
    assertEquals(com.example.ai.FacialExpression.SHY_HESITANT, profile.facialExpression)
    assertEquals(com.example.ai.BodyLanguageState.GUARDED_HESITANT, profile.bodyLanguage)
    assertTrue(profile.dominantFeelingHindi.contains("संकोच, झिझक"))

    val directives = com.example.ai.CrossModalEmotionalResonanceEngine.buildEmpathyPromptDirective(profile)
    assertTrue(directives.contains("यौन शिक्षा और स्वास्थ्य संबंधी विषय"))
    assertTrue(directives.contains("सच्चा अपनापन"))
    assertFalse("Directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testHealthAndSexualEducationMultiAiRouting() {
    val query = "यौन शिक्षा और स्वास्थ्य संबंधी वैज्ञानिक जानकारी दो"
    val decision = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(query)
    assertEquals("gpt-4o", decision.primaryModel.id)
    assertTrue(decision.reasonHindi.contains("यौन शिक्षा, प्रजनन स्वास्थ्य"))

    val directives = com.example.ai.MultiAiCollaborationEngine.buildCollaborationDirectives(decision)
    assertTrue(directives.contains("यौन शिक्षा और स्वास्थ्य संबंधी विषय"))
    assertFalse("Directives must not contain asterisks", directives.contains("*"))
  }

  @Test
  fun testDeveloperPhotoRecognition() {
    val query = "यह फोटो आर्या का डेवलपर है और तीनों फोटो में वही आदमी है"
    val response = com.example.ai.LocalAiKnowledgeEngine.generateIntelligentResponse(query)
    assertTrue(response.contains("मौसम कुमार राज"))
    assertTrue(response.contains("चीफ आर्किटेक्ट"))
    assertTrue(response.contains("STYLISH BOY MOUSAM"))
    assertFalse("Response must not contain asterisks", response.contains("*"))
  }

  @Test
  fun testAuthenticAffectionateFriendToneAndFullKnowledgeGraphDirectives() {
    val query = "क्वांटम कंप्यूटिंग और क्रिप्टोग्राफी में गहरे संबंध समझाओ"
    val graph = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(query)
    val kgDirectives = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraphPromptDirectives(graph)
    assertTrue(
      "KG directives must contain authentic friend tone phrase",
      kgDirectives.contains("एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें")
    )
    assertFalse("KG directives must never contain asterisks", kgDirectives.contains("*"))

    val decision = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(query)
    val multiAiDirectives = com.example.ai.MultiAiCollaborationEngine.buildCollaborationDirectives(decision)
    assertTrue(
      "Multi-AI directives must contain authentic friend tone phrase",
      multiAiDirectives.contains("एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें")
    )
    assertFalse("Multi-AI directives must never contain asterisks", multiAiDirectives.contains("*"))

    assertTrue(
      "Gemini default system prompt must contain authentic friend tone phrase",
      com.example.gemini.GeminiRepository.DEFAULT_SYSTEM_PROMPT.contains("एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें")
    )
    assertTrue(
      "Frontier R&D prompt must contain authentic friend tone phrase",
      com.example.gemini.GeminiRepository.FRONTIER_RD_SYSTEM_PROMPT.contains("एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें")
    )
    assertTrue(
      "Autonomous prompt must contain authentic friend tone phrase",
      com.example.gemini.GeminiRepository.AUTONOMOUS_WITTY_SYSTEM_PROMPT.contains("एक सच्चे आत्मीय मित्र के लहजे में, पूर्णता के साथ प्रस्तुत करें")
    )
  }

  @Test
  fun testFreeFireKnowledgeBaseAndAnalyzer() {
    val weapons = com.example.ai.FreeFireKnowledgeBase.weapons
    assertTrue("Free Fire weapons catalog must contain iconic guns", weapons.size >= 8)
    assertTrue(weapons.any { it.name.contains("M1887") })
    assertTrue(weapons.any { it.name.contains("MP40") })
    assertTrue(weapons.any { it.name.contains("Woodpecker") })

    val characters = com.example.ai.FreeFireKnowledgeBase.characters
    assertTrue("Free Fire characters must include Alok, Tatsuya, Orion", characters.size >= 6)
    assertTrue(characters.any { it.name.contains("Alok") })
    assertTrue(characters.any { it.name.contains("Tatsuya") })

    val loadout = com.example.ai.FreeFireStrategyAnalyzer.analyzeAndBuildLoadout(com.example.ai.FfPlaystyle.RUSHER)
    assertNotNull(loadout)
    assertEquals(com.example.ai.FfPlaystyle.RUSHER, loadout.playstyle)
    assertTrue(loadout.primaryGun.name.contains("M1887") || loadout.primaryGun.name.contains("MP40"))
    assertTrue(loadout.strategyDirectivesHindi.contains("ग्लू वॉल"))
    assertFalse("Strategy directives must never contain asterisks", loadout.strategyDirectivesHindi.contains("*"))

    val sens = com.example.ai.FreeFireStrategyAnalyzer.calculateSensitivity(120)
    assertNotNull(sens)
    assertTrue("120Hz sensitivity general should be high for rapid drag", sens.general >= 95)

    val csBuy = com.example.ai.FreeFireStrategyAnalyzer.getCsRoundStrategy(1)
    assertNotNull(csBuy)
    assertTrue(csBuy.recommendedWeaponsHindi.contains("G18") || csBuy.recommendedWeaponsHindi.contains("M500"))
  }

  @Test
  fun testFreeFireQueryInMultiAiAndKnowledgeGraph() {
    val query = "Free Fire में M1887 वन टैप हेडशॉट और रोटेशन ड्रैग ट्रिक बताओ"
    val decision = com.example.ai.MultiAiCollaborationEngine.orchestrateQuery(query)
    assertEquals("gemini-3.8-flash", decision.primaryModel.id)
    assertTrue(decision.reasonHindi.contains("फ्री फायर"))

    val graph = com.example.ai.AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(query)
    assertNotNull(graph)
    assertTrue(graph.topicTitleHindi.contains("फ्री फायर"))
    assertTrue(graph.nodes.any { it.id == "FF1" })
    assertTrue(graph.deepRelationalSummary.contains("हथियार आँकड़े"))
    assertFalse("Summary must not contain asterisks", graph.structuredSynthesisHindi.contains("*"))

    val localResponse = com.example.ai.LocalAiKnowledgeEngine.generateIntelligentResponse("फ्री फायर में वन टैप हेडशॉट कैसे मारें?")
    assertTrue(localResponse.contains("रोटेशन ड्रैग"))
    assertTrue(localResponse.contains("M1887"))
    assertFalse("Local response must not contain asterisks", localResponse.contains("*"))
  }

  @Test
  fun testVoiceLockAndUnlockPhoneCommands() {
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceLockOffCommand("lock my phone"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceLockOffCommand("lock the phone"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceLockOffCommand("phone lock karo"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceLockOffCommand("फोन लॉक करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceLockOffCommand("स्क्रीन बंद करो और लॉक"))

    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceWakeOnCommand("unlock my phone"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceWakeOnCommand("unlock the phone"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceWakeOnCommand("phone unlock karo"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceWakeOnCommand("फोन अनलॉक करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isDeviceWakeOnCommand("स्क्रीन चालू करो"))

    assertEquals(com.example.security.VoiceActionType.DEVICE_LOCK_OFF, com.example.security.VoiceBiometricEngine.classifySpokenAction("Lock my phone"))
    assertEquals(com.example.security.VoiceActionType.DEVICE_WAKE_ON, com.example.security.VoiceBiometricEngine.classifySpokenAction("Unlock my phone"))
    assertEquals(com.example.security.VoiceActionType.DEVICE_LOCK_OFF, com.example.security.VoiceBiometricEngine.classifySpokenAction("फोन लॉक करो"))
    assertEquals(com.example.security.VoiceActionType.DEVICE_WAKE_ON, com.example.security.VoiceBiometricEngine.classifySpokenAction("फोन अनलॉक करो"))
  }

  @Test
  fun testStrangerVoiceRejectedForLockCommands() {
    // Authorized owner voice with close pitch (126 Hz against owner pitch 125 Hz)
    val ownerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "Lock my phone",
      measuredPitchHz = 126.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertTrue("Owner voice should be verified", ownerResult.isVerified)
    assertEquals(com.example.security.VoiceActionType.DEVICE_LOCK_OFF, ownerResult.matchedAction)
    assertFalse(ownerResult.isSecurityAlert)

    // Unauthorized stranger voice with completely different pitch (280 Hz)
    val strangerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "Lock my phone",
      measuredPitchHz = 280.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertFalse("Stranger voice must be rejected", strangerResult.isVerified)
    assertTrue("Stranger voice must trigger security alert", strangerResult.isSecurityAlert)
    assertTrue(strangerResult.statusMessage.contains("अनधिकृत आवाज़ अस्वीकृत"))
  }

  @Test
  fun testPersonalizedPreferenceVoiceCommands() {
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("स्पीड बढ़ाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("बोलने की गति बढ़ाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("faster speech"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("पिच बढ़ाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("आवाज़ पतली करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("काव्या पर्सोना लगाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("आरव पर्सोना लगाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("मस्ती मोड ऑन करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("गंभीर मोड लगाओ"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("फॉन्ट बड़ा करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("बोलकर जवाब देना बंद करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("बोलकर जवाब देना चालू करो"))
    assertTrue(com.example.security.VoiceBiometricEngine.isPreferenceCustomizationCommand("मेरी सेटिंग्स बताओ"))

    assertEquals(
      com.example.security.VoiceActionType.PREFERENCE_CUSTOMIZATION,
      com.example.security.VoiceBiometricEngine.classifySpokenAction("मेरी आवाज़ की स्पीड बढ़ाओ")
    )
    assertEquals(
      com.example.security.VoiceActionType.PREFERENCE_CUSTOMIZATION,
      com.example.security.VoiceBiometricEngine.classifySpokenAction("काव्या पर्सोना लगाओ")
    )
    assertEquals(
      com.example.security.VoiceActionType.PREFERENCE_CUSTOMIZATION,
      com.example.security.VoiceBiometricEngine.classifySpokenAction("मस्ती मोड ऑन करो")
    )
    assertEquals(
      com.example.security.VoiceActionType.PREFERENCE_CUSTOMIZATION,
      com.example.security.VoiceBiometricEngine.classifySpokenAction("फॉन्ट बड़ा करो")
    )
  }

  @Test
  fun testStrangerVoiceRejectedForPersonalizedPreferences() {
    // Authorized owner modifying personal settings
    val ownerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "स्पीड बढ़ाओ",
      measuredPitchHz = 125.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertTrue("Owner voice should be authorized for preferences", ownerResult.isVerified)
    assertEquals(com.example.security.VoiceActionType.PREFERENCE_CUSTOMIZATION, ownerResult.matchedAction)
    assertFalse(ownerResult.isSecurityAlert)
    assertFalse("Status message must not contain asterisks", ownerResult.statusMessage.contains("*"))

    // Stranger voice trying to modify owner's settings
    val strangerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "स्पीड बढ़ाओ",
      measuredPitchHz = 280.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertFalse("Stranger voice must be rejected for preferences", strangerResult.isVerified)
    assertTrue("Stranger voice must trigger security alert", strangerResult.isSecurityAlert)
    assertTrue(strangerResult.statusMessage.contains("अनधिकृत आवाज़ अस्वीकृत"))
    assertFalse("Alert message must not contain asterisks", strangerResult.statusMessage.contains("*"))
  }

  @Test
  fun testStrangerVoiceRejectedForGeminiAssistantAndAllGeneralCommands() {
    // Authorized owner asking Gemini assistant
    val ownerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "भारत की राजधानी क्या है?",
      measuredPitchHz = 125.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertTrue("Owner voice should be authorized for Gemini assistant", ownerResult.isVerified)
    assertEquals(com.example.security.VoiceActionType.GEMINI_ASSISTANT, ownerResult.matchedAction)
    assertFalse(ownerResult.isSecurityAlert)

    // Stranger voice trying to invoke Gemini assistant
    val strangerResult = com.example.security.VoiceBiometricEngine.verifyVoice(
      spokenText = "भारत की राजधानी क्या है?",
      measuredPitchHz = 310.0f,
      isEnrolled = true,
      ownerPitchHz = 125.0f,
      pitchToleranceHz = 35.0f,
      sensitivity = 0.75f
    )
    assertFalse("Stranger voice must be rejected for Gemini assistant", strangerResult.isVerified)
    assertTrue("Stranger voice must trigger security alert", strangerResult.isSecurityAlert)
    assertTrue(strangerResult.statusMessage.contains("केवल पंजीकृत मालिक की आवाज़ पर सक्रिय होने के लिए लॉक है"))
    assertFalse("Alert message must not contain asterisks", strangerResult.statusMessage.contains("*"))
  }

  @Test
  fun testGrokRebelliousWitAndXEcosystemIntegration() {
    val query = "ग्रोक मोड में एक्स इकोसिस्टम के ट्रेंड्स और सच बताओ"
    val response = com.example.ai.LocalAiKnowledgeEngine.generateIntelligentResponse(query)
    assertTrue("Must mention Grok mode", response.contains("ग्रोक"))
    assertTrue("Must address political correctness", response.contains("राजनीतिक शुद्धता"))
    assertTrue("Must mention X trends", response.contains("एक्स (X) लाइव ट्रेंड्स") || response.contains("एक्स इकोसिस्टम"))
    assertFalse("Response must not contain asterisks", response.contains("*"))

    assertTrue(
      "DEFAULT_SYSTEM_PROMPT must contain Grok rebellious wit",
      com.example.gemini.GeminiRepository.DEFAULT_SYSTEM_PROMPT.contains("GROK-STYLE REBELLIOUS WIT & REAL-TIME X ECOSYSTEM")
    )
    assertTrue(
      "DEFAULT_SYSTEM_PROMPT must mention political correctness transcendence",
      com.example.gemini.GeminiRepository.DEFAULT_SYSTEM_PROMPT.contains("राजनीतिक शुद्धता")
    )
    assertTrue(
      "AUTONOMOUS_WITTY_SYSTEM_PROMPT must contain Grok rebellious wit",
      com.example.gemini.GeminiRepository.AUTONOMOUS_WITTY_SYSTEM_PROMPT.contains("GROK REBELLIOUS WIT & X ECOSYSTEM")
    )
  }

  @Test
  fun testDynamicVerbosityDefaultsToConciseForStandardQuery() {
    val standardQuery = "भारत की राजधानी क्या है?"
    val decision = com.example.ai.verbosity.DynamicVerbosityEngine.evaluateVerbosity(standardQuery)
    assertEquals(com.example.ai.verbosity.VerbosityLevel.CONCISE, decision.level)
    assertEquals(com.example.ai.verbosity.VerbosityTrigger.DEFAULT_STANDARD_QUERY, decision.trigger)

    val directive = com.example.ai.verbosity.DynamicVerbosityEngine.buildVerbosityPromptDirective(decision)
    assertTrue("Directive must enforce concise mode", directive.contains("CONCISE & DIRECT"))
    assertTrue("Directive must instruct to minimize latency and avoid fluff", directive.contains("latency"))
  }

  @Test
  fun testDynamicVerbosityEscalatesToComprehensiveOnExplicitRequest() {
    val explicitQuery = "क्वांटम कंप्यूटिंग को विस्तार से और डिटेल में पूरा समझाओ"
    val decision = com.example.ai.verbosity.DynamicVerbosityEngine.evaluateVerbosity(explicitQuery)
    assertEquals(com.example.ai.verbosity.VerbosityLevel.COMPREHENSIVE, decision.level)
    assertEquals(com.example.ai.verbosity.VerbosityTrigger.USER_EXPLICIT_REQUEST, decision.trigger)

    val directive = com.example.ai.verbosity.DynamicVerbosityEngine.buildVerbosityPromptDirective(decision)
    assertTrue("Directive must mandate comprehensive mode", directive.contains("COMPREHENSIVE & IN-DEPTH"))
  }

  @Test
  fun testDynamicVerbosityEscalatesOnHighComplexitySubject() {
    val complexQuery = "माइक्रोसर्विस आर्किटेक्चर और वितरित सिस्टम में क्रिप्टोग्राफी के सिद्धांत क्या हैं?"
    val decision = com.example.ai.verbosity.DynamicVerbosityEngine.evaluateVerbosity(complexQuery)
    assertEquals(com.example.ai.verbosity.VerbosityLevel.COMPREHENSIVE, decision.level)
    assertEquals(com.example.ai.verbosity.VerbosityTrigger.HIGH_COMPLEXITY_SUBJECT, decision.trigger)
  }

  @Test
  fun testDynamicVerbosityExplicitShortRequest() {
    val shortQuery = "संक्षेप में और शॉर्ट में बताओ पाइथन क्या है"
    val decision = com.example.ai.verbosity.DynamicVerbosityEngine.evaluateVerbosity(shortQuery)
    assertEquals(com.example.ai.verbosity.VerbosityLevel.CONCISE, decision.level)
    assertEquals(com.example.ai.verbosity.VerbosityTrigger.USER_EXPLICIT_REQUEST, decision.trigger)
  }

  @Test
  fun testSileroVadDetectsSpeechStartAndTriggersPause() {
    val vad = com.example.voice.vad.SileroVadDetector()
    var speechStartTriggered = false
    var silenceFinishTriggered = false

    vad.onSpeechStartDetected = { speechStartTriggered = true }
    vad.onSpeechSilenceDetected = { silenceFinishTriggered = true }

    // Simulate high vocal energy (speech)
    vad.processRmsLevel(8.0f)
    vad.processRmsLevel(9.0f)

    assertTrue("Speech start must trigger callback to pause AI speech", speechStartTriggered)
    assertTrue("VAD state must be speech active", vad.vadMetrics.value.isSpeechActive)

    // Simulate consecutive silence frames
    repeat(15) {
      vad.processRmsLevel(0.5f)
    }

    assertTrue("Silence must trigger callback to finish listening", silenceFinishTriggered)
    assertFalse("VAD state must be silence", vad.vadMetrics.value.isSpeechActive)
  }
}



