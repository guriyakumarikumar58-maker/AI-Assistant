package com.example.gemini

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface GeminiApiService {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContentForModel(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest,
        @retrofit2.http.Header("X-Arya-User-Prompt") userPromptHeader: String? = null
    ): GenerateContentResponse

    @POST("v1beta/models/{model}:generateVideos")
    suspend fun generateVideos(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateVideosRequest
    ): VeoOperationResponse

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/"

        fun create(): GeminiApiService {
            val builder = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)

            // Standard resilient OkHttpClient
            val okHttpClient = builder.build()
            val moshi = com.squareup.moshi.Moshi.Builder().build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(GeminiApiService::class.java)
        }

        private fun extractPromptFromRequest(request: Request): String {
            return try {
                val header = request.header("X-Arya-User-Prompt")
                if (!header.isNullOrBlank()) {
                    return java.net.URLDecoder.decode(header, "UTF-8")
                }
                val copy = request.newBuilder().build()
                val buffer = okio.Buffer()
                copy.body?.writeTo(buffer)
                val json = buffer.readUtf8()
                // Strictly exclude systemInstruction so system prompt words like 'Real-time' never leak
                val contentsJson = if (json.contains("\"systemInstruction\"")) {
                    json.substringBefore("\"systemInstruction\"")
                } else json
                val regex = Regex(""""text"\s*:\s*"((?:\\.|[^"\\])*)"""")
                val matches = regex.findAll(contentsJson).toList()
                val lastText = matches.lastOrNull()?.groupValues?.get(1)
                val decoded = lastText?.replace("\\n", "\n")?.replace("\\\"", "\"")?.replace("\\\\", "\\") ?: ""
                unescapeUnicode(decoded)
            } catch (_: Exception) {
                ""
            }
        }

        private fun unescapeUnicode(input: String): String {
            val unicodeRegex = Regex("""\\u([0-9a-fA-F]{4})""")
            return unicodeRegex.replace(input) { m ->
                m.groupValues[1].toInt(16).toChar().toString()
            }
        }

        private fun createSyntheticSuccessResponse(request: Request, message: String): Response {
            val escapedMessage = message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "")
                .replace("\t", "\\t")

            val jsonPayload = """
                {
                  "candidates": [
                    {
                      "content": {
                        "role": "model",
                        "parts": [
                          {
                            "text": "$escapedMessage"
                          }
                        ]
                      },
                      "finishReason": "STOP"
                    }
                  ]
                }
            """.trimIndent()

            val responseBody = jsonPayload.toResponseBody("application/json".toMediaTypeOrNull())

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK (Auto-Healed)")
                .body(responseBody)
                .build()
        }
    }
}
