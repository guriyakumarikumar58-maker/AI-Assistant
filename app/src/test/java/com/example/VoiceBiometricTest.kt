package com.example

import com.example.security.VoiceActionType
import com.example.security.VoiceBiometricEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceBiometricTest {

    @Test
    fun testGeminiQueryClassificationForOwner() {
        val result = VoiceBiometricEngine.verifyVoice(
            spokenText = "आज का मौसम कैसा है?",
            measuredPitchHz = 160f,
            isEnrolled = true,
            ownerPitchHz = 160f,
            pitchToleranceHz = 45f,
            sensitivity = 0.70f
        )
        assertTrue(result.isVerified)
        assertEquals(VoiceActionType.GEMINI_ASSISTANT, result.matchedAction)
    }

    @Test
    fun testTranscribeActionDetected() {
        val result = VoiceBiometricEngine.verifyVoice(
            spokenText = "मेरी बात ट्रांसक्राइब करो",
            measuredPitchHz = 160f,
            isEnrolled = true,
            ownerPitchHz = 160f,
            pitchToleranceHz = 45f,
            sensitivity = 0.70f
        )
        assertTrue(result.isVerified)
        assertEquals(VoiceActionType.TRANSCRIBE, result.matchedAction)
    }

    @Test
    fun testUnauthorizedStrangerVoiceBlockedFromAryaAI() {
        // High pitch stranger voice (320Hz vs enrolled 160Hz)
        val result = VoiceBiometricEngine.verifyVoice(
            spokenText = "आज का मौसम कैसा है?",
            measuredPitchHz = 320f,
            isEnrolled = true,
            ownerPitchHz = 160f,
            pitchToleranceHz = 45f,
            sensitivity = 0.70f
        )
        assertTrue(!result.isVerified)
        assertTrue(result.isSecurityAlert)
    }

    @Test
    fun testBankingAppsStrictlyExcludedFromAccess() {
        // Verify all major banking, UPI, wallet and finance apps are strictly blocked
        assertTrue(com.example.util.DeviceAppsManager.isBankingApp("SBI YONO", "com.sbi.lotusintouch"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingApp("HDFC Bank", "com.hdfcbank.android"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingApp("Google Pay", "com.google.android.apps.nbu.paisa.user"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingApp("Paytm", "net.one97.paytm"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingApp("PhonePe", "com.phonepe.app"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingCommand("एसबीआई बैंक खोलो"))
        assertTrue(com.example.util.DeviceAppsManager.isBankingCommand("पेटीएम ओपन करो"))
    }

    @Test
    fun testNonBankingAppsAllowedForAryaAccess() {
        // Verify non-banking apps are allowed
        assertTrue(!com.example.util.DeviceAppsManager.isBankingApp("YouTube", "com.google.android.youtube"))
        assertTrue(!com.example.util.DeviceAppsManager.isBankingApp("WhatsApp", "com.whatsapp"))
        assertTrue(!com.example.util.DeviceAppsManager.isBankingApp("Camera", "com.android.camera"))
        assertTrue(!com.example.util.DeviceAppsManager.isBankingApp("Google Chrome", "com.android.chrome"))
    }

    @Test
    fun testAutonomousAgentFactVerification() {
        val report = com.example.ai.AutonomousAgentEngine.verifyClaim("पृथ्वी चपटी है")
        assertTrue(report.truthScore < 10)
        assertTrue(report.sourcesChecked.isNotEmpty())
        assertTrue(report.wittyRemark.isNotBlank())
    }
}
