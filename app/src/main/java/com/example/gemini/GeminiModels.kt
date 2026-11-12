package com.example.gemini

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @param:Json(name = "contents") val contents: List<Content>,
    @param:Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @param:Json(name = "systemInstruction") val systemInstruction: Content? = null,
    @param:Json(name = "tools") val tools: List<Tool>? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @param:Json(name = "role") val role: String? = null,
    @param:Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @param:Json(name = "text") val text: String? = null,
    @param:Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @param:Json(name = "mimeType") val mimeType: String,
    @param:Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class Tool(
    @param:Json(name = "googleSearch") val googleSearch: Map<String, String>? = null
)

data class ChatTurn(
    val role: String = "user",
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @param:Json(name = "temperature") val temperature: Float? = null,
    @param:Json(name = "topP") val topP: Float? = null,
    @param:Json(name = "topK") val topK: Int? = null,
    @param:Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null,
    @param:Json(name = "responseModalities") val responseModalities: List<String>? = null,
    @param:Json(name = "imageConfig") val imageConfig: ImageConfig? = null,
    @param:Json(name = "speechConfig") val speechConfig: SpeechConfig? = null,
    @param:Json(name = "thinkingConfig") val thinkingConfig: ThinkingConfig? = null
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    @param:Json(name = "thinkingLevel") val thinkingLevel: String = "low"
)

@JsonClass(generateAdapter = true)
data class ImageConfig(
    @param:Json(name = "aspectRatio") val aspectRatio: String = "1:1",
    @param:Json(name = "imageSize") val imageSize: String = "1K"
)

@JsonClass(generateAdapter = true)
data class SpeechConfig(
    @param:Json(name = "voiceConfig") val voiceConfig: VoiceConfig? = null
)

@JsonClass(generateAdapter = true)
data class VoiceConfig(
    @param:Json(name = "prebuiltVoiceConfig") val prebuiltVoiceConfig: PrebuiltVoiceConfig? = null
)

@JsonClass(generateAdapter = true)
data class PrebuiltVoiceConfig(
    @param:Json(name = "voiceName") val voiceName: String = "Kore"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @param:Json(name = "candidates") val candidates: List<Candidate>? = null,
    @param:Json(name = "groundingMetadata") val groundingMetadata: GroundingMetadata? = null,
    @param:Json(name = "promptFeedback") val promptFeedback: PromptFeedback? = null
)

@JsonClass(generateAdapter = true)
data class PromptFeedback(
    @param:Json(name = "blockReason") val blockReason: String? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @param:Json(name = "content") val content: Content? = null,
    @param:Json(name = "finishReason") val finishReason: String? = null,
    @param:Json(name = "groundingMetadata") val groundingMetadata: GroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GroundingMetadata(
    @param:Json(name = "webSearchQueries") val webSearchQueries: List<String>? = null,
    @param:Json(name = "searchEntryPoint") val searchEntryPoint: SearchEntryPoint? = null
)

@JsonClass(generateAdapter = true)
data class SearchEntryPoint(
    @param:Json(name = "renderedContent") val renderedContent: String? = null
)

// Veo Video Models
@JsonClass(generateAdapter = true)
data class GenerateVideosRequest(
    @param:Json(name = "prompt") val prompt: String,
    @param:Json(name = "image") val image: VideoImageInput? = null,
    @param:Json(name = "config") val config: VeoConfig? = null
)

@JsonClass(generateAdapter = true)
data class VideoImageInput(
    @param:Json(name = "bytesBase64Encoded") val bytesBase64Encoded: String,
    @param:Json(name = "mimeType") val mimeType: String = "image/jpeg"
)

@JsonClass(generateAdapter = true)
data class VeoConfig(
    @param:Json(name = "numberOfVideos") val numberOfVideos: Int = 1,
    @param:Json(name = "resolution") val resolution: String = "720p",
    @param:Json(name = "aspectRatio") val aspectRatio: String = "16:9"
)

@JsonClass(generateAdapter = true)
data class VeoOperationResponse(
    @param:Json(name = "name") val name: String? = null,
    @param:Json(name = "done") val done: Boolean? = null
)
