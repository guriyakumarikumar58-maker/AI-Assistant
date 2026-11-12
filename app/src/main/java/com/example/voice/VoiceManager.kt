package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * VoiceManager:
 * Manages text-to-speech output and on-demand speech recognition.
 *
 * Privacy & Resource Guarantee:
 * - Microphone is strictly OFF by default.
 * - Recording only starts upon explicit user invocation (e.g. mic button click).
 * - Immediately stops recording once speech completes or on cancel/error.
 * - No background continuous microphone recording or looping.
 */
class VoiceManager(private val context: Context) : RecognitionListener, TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    private val _isTtsReadyFlow = MutableStateFlow(false)
    val isTtsReadyFlow: StateFlow<Boolean> = _isTtsReadyFlow.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _audioRmsDb = MutableStateFlow(0f)
    val audioRmsDb: StateFlow<Float> = _audioRmsDb.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var onSpeechComplete: ((String, Float) -> Unit)? = null
    var onUserStartedSpeaking: (() -> Unit)? = null
    var onUserFinishedSpeaking: (() -> Unit)? = null

    // Silero Voice Activity Detector (VAD) Engine
    val sileroVadDetector = com.example.voice.vad.SileroVadDetector(context).apply {
        onSpeechStartDetected = {
            // User began speaking: immediately pause AI speech (barge-in interruption)
            stopSpeaking()
            onUserStartedSpeaking?.invoke()
        }
        onSpeechSilenceDetected = {
            // Silence detected: user finished sentence, finish listening
            stopListening()
            onUserFinishedSpeaking?.invoke()
        }
    }

    private var estimatedPitchHz: Float = 160f
    private var currentRate: Float = 1.0f
    private var currentPitch: Float = 1.0f
    private var pendingPersona: com.example.voice.VoicePersona? = null
    private var pendingTextToSpeak: String? = null

    init {
        initSpeechRecognizer()
        initTextToSpeech()
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(this@VoiceManager)
            }
        }
    }

    private fun initTextToSpeech() {
        try {
            isTtsReady = false
            _isTtsReadyFlow.value = false
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            isTtsReady = false
            _isTtsReadyFlow.value = false
            _errorMessage.value = "TTS Initialization exception: ${e.message}"
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech
            if (tts != null) {
                try {
                    val hindiResult = tts.setLanguage(Locale.forLanguageTag("hi-IN"))
                    if (hindiResult == TextToSpeech.LANG_MISSING_DATA || hindiResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts.setLanguage(Locale.US)
                    }
                    tts.setSpeechRate(currentRate)
                    tts.setPitch(currentPitch)

                    pendingPersona?.let { applyPersonaToTts(it, tts) }

                    isTtsReady = true
                    _isTtsReadyFlow.value = true

                    pendingTextToSpeak?.let { pending ->
                        pendingTextToSpeak = null
                        speak(pending)
                    }
                } catch (e: Exception) {
                    isTtsReady = false
                    _isTtsReadyFlow.value = false
                    _errorMessage.value = "TTS Configuration error: ${e.message}"
                }
            } else {
                isTtsReady = false
                _isTtsReadyFlow.value = false
            }
        } else {
            isTtsReady = false
            _isTtsReadyFlow.value = false
            _errorMessage.value = "TTS Initialization failed with status: $status"
        }
    }

    fun setTtsSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.5f, 2.0f)
        if (!isTtsReady || textToSpeech == null) return
        try {
            textToSpeech?.setSpeechRate(currentRate)
        } catch (_: Exception) {}
    }

    fun setTtsPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
        if (!isTtsReady || textToSpeech == null) return
        try {
            textToSpeech?.setPitch(currentPitch)
        } catch (_: Exception) {}
    }

    fun setPersona(persona: com.example.voice.VoicePersona) {
        currentPitch = persona.basePitch
        currentRate = persona.baseRate
        pendingPersona = persona
        if (!isTtsReady || textToSpeech == null) return
        textToSpeech?.let { applyPersonaToTts(persona, it) }
    }

    private fun applyPersonaToTts(persona: com.example.voice.VoicePersona, tts: TextToSpeech) {
        try {
            tts.setPitch(persona.basePitch)
            tts.setSpeechRate(persona.baseRate)
            val voices = tts.voices
            if (!voices.isNullOrEmpty()) {
                // Priority order: Neural2 / Studio / Wavenet / Natural High-Quality voices for human-like warmth
                val neuralVoices = voices.filter { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    name.contains("neural") || name.contains("studio") || name.contains("wavenet") || name.contains("natural") || !voice.isNetworkConnectionRequired
                }

                val targetList = if (neuralVoices.isNotEmpty()) neuralVoices else voices

                val matchingVoice = when (persona) {
                    com.example.voice.VoicePersona.AARAV_MALE -> targetList.firstOrNull {
                        val n = it.name.lowercase(Locale.ROOT)
                        n.contains("male") && !n.contains("female")
                    } ?: voices.firstOrNull { it.name.contains("male", ignoreCase = true) }
                    com.example.voice.VoicePersona.VIKRANT_TECH -> targetList.firstOrNull {
                        it.name.contains("male", ignoreCase = true)
                    } ?: voices.firstOrNull { it.name.contains("male", ignoreCase = true) }
                    com.example.voice.VoicePersona.ALEX_ENGLISH -> targetList.firstOrNull {
                        it.locale.language == "en"
                    } ?: voices.firstOrNull { it.locale.language == "en" }
                    else -> targetList.firstOrNull {
                        val n = it.name.lowercase(Locale.ROOT)
                        n.contains("female") || n.contains("hi-in") || n.contains("hindi")
                    } ?: voices.firstOrNull { it.name.contains("female", ignoreCase = true) }
                }

                if (matchingVoice != null) {
                    tts.voice = matchingVoice
                }
            }
        } catch (_: Exception) {}
    }

    fun speak(text: String) {
        val sanitized = text.replace("*", "").trim()
        if (sanitized.isBlank()) return

        if (!isTtsReady || textToSpeech == null) {
            pendingTextToSpeak = sanitized
            return
        }

        try {
            val isHindi = sanitized.any { it in '\u0900'..'\u097F' }
            val tts = textToSpeech ?: return
            if (isHindi) {
                tts.setLanguage(Locale.forLanguageTag("hi-IN"))
            } else {
                tts.setLanguage(Locale.US)
            }
            tts.setSpeechRate(currentRate)
            tts.setPitch(currentPitch)
            tts.speak(sanitized, TextToSpeech.QUEUE_FLUSH, null, "VOICE_LOCK_UTTERANCE")
        } catch (e: Exception) {
            _errorMessage.value = "TTS speak error: ${e.message}"
        }
    }

    /**
     * Speaks text in a specified target language (e.g. en-US, es-ES, fr-FR, de-DE, ja-JP).
     */
    fun speakInLanguage(text: String, languageTag: String) {
        val sanitized = text.replace("*", "").trim()
        if (sanitized.isBlank()) return

        if (!isTtsReady || textToSpeech == null) {
            pendingTextToSpeak = sanitized
            return
        }

        try {
            val tts = textToSpeech ?: return
            val targetLocale = try {
                Locale.forLanguageTag(languageTag)
            } catch (_: Exception) {
                Locale.US
            }
            tts.setLanguage(targetLocale)
            tts.setSpeechRate(currentRate)
            tts.setPitch(currentPitch)
            tts.speak(sanitized, TextToSpeech.QUEUE_FLUSH, null, "TRANSLATION_UTTERANCE")
        } catch (e: Exception) {
            _errorMessage.value = "TTS translation speak error: ${e.message}"
        }
    }

    fun stopSpeaking() {
        if (!isTtsReady || textToSpeech == null) return
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    /**
     * Start speech recognition only on explicit user invocation in Hindi by default.
     */
    fun startListening() {
        startListeningWithLanguage("hi-IN")
    }

    /**
     * Start speech recognition for a specific spoken language.
     */
    fun startListeningWithLanguage(languageTag: String) {
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }
        if (speechRecognizer == null) {
            _errorMessage.value = "Speech recognition is not available on this device"
            return
        }

        stopSpeaking()
        _recognizedText.value = ""
        _errorMessage.value = null

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            _errorMessage.value = e.localizedMessage
            _isListening.value = false
        }
    }

    /**
     * Explicitly stop speech recognition and turn off microphone.
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isListening.value = false
        _audioRmsDb.value = 0f
    }

    fun destroy() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (_: Exception) {} finally {
            speechRecognizer = null
        }
        try {
            if (isTtsReady) {
                textToSpeech?.stop()
            }
            textToSpeech?.shutdown()
        } catch (_: Exception) {} finally {
            textToSpeech = null
            isTtsReady = false
            _isTtsReadyFlow.value = false
            pendingTextToSpeak = null
        }
        _isListening.value = false
        _audioRmsDb.value = 0f
    }

    override fun onReadyForSpeech(params: Bundle?) {
        _isListening.value = true
        _errorMessage.value = null
        sileroVadDetector.reset()
    }

    override fun onBeginningOfSpeech() {
        _isListening.value = true
        // Barge-in: user began vocalization
        stopSpeaking()
        onUserStartedSpeaking?.invoke()
    }

    override fun onRmsChanged(rmsdB: Float) {
        val safeRms = rmsdB.coerceAtLeast(0f)
        _audioRmsDb.value = safeRms
        sileroVadDetector.processRmsLevel(safeRms)
        // Security Fix: Do NOT map volume/loudness (RMS dB) to pitch.
        // True vocal pitch is strictly determined via FFT spectral analysis in onBufferReceived.
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        if (buffer != null && buffer.isNotEmpty()) {
            sileroVadDetector.processPcmFrame(buffer)
            val acoustic = com.example.security.FftPitchDetector.detectPitchFromPcm16(buffer)
            if (acoustic != null && acoustic.fundamentalPitchHz in 75f..360f) {
                estimatedPitchHz = acoustic.fundamentalPitchHz
            }
        }
    }

    override fun onEndOfSpeech() {
        _isListening.value = false
        _audioRmsDb.value = 0f
        sileroVadDetector.reset()
    }

    override fun onError(error: Int) {
        _isListening.value = false
        _audioRmsDb.value = 0f
        val msg = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Audio permission required"
            SpeechRecognizer.ERROR_NETWORK -> "Network connection error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech service is busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech input timeout"
            else -> "Speech recognition error ($error)"
        }
        _errorMessage.value = msg
        // Recording is fully stopped. No continuous restart loop.
    }

    override fun onResults(results: Bundle?) {
        _isListening.value = false
        _audioRmsDb.value = 0f
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        _recognizedText.value = text
        if (text.isNotBlank()) {
            onSpeechComplete?.invoke(text, estimatedPitchHz)
        }
        // Recording completed. No continuous restart loop.
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        if (text.isNotBlank()) {
            _recognizedText.value = text
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
