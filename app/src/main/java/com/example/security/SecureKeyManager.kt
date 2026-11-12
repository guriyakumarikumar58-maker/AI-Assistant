package com.example.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.BuildConfig
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * SecureKeyManager:
 * Hardware-backed secure key and token storage using Android KeyStore (AES-256-GCM).
 *
 * Ensures all third-party AI keys (OpenAI, DeepSeek, xAI Grok) and X (Twitter) tokens
 * are NEVER stored in plaintext on disk. They are encrypted using an AndroidKeyStore
 * master key before being written to private SharedPreferences.
 */
class SecureKeyManager(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "arya_secure_api_vault"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "AryaApiVaultMasterKey"
        private const val GCM_TAG_LENGTH_BITS = 128
        private const val GCM_IV_LENGTH_BYTES = 12

        // Preference Keys
        const val KEY_OPENAI_API_KEY = "enc_openai_api_key"
        const val KEY_DEEPSEEK_API_KEY = "enc_deepseek_api_key"
        const val KEY_XAI_API_KEY = "enc_xai_api_key"
        const val KEY_X_TWITTER_BEARER = "enc_x_twitter_bearer_token"
        const val KEY_GEMINI_API_KEY = "enc_gemini_api_key"
    }

    private fun getOrCreateMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        if (keyStore.containsAlias(MASTER_KEY_ALIAS)) {
            val entry = keyStore.getEntry(MASTER_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )
        val spec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(plaintext: String): String? {
        if (plaintext.isBlank()) return null
        return try {
            val secretKey = getOrCreateMasterKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv ?: return null

            val cipherBytes = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))
            val combined = ByteArray(iv.size + cipherBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherBytes, 0, combined, iv.size, cipherBytes.size)

            Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
    }

    private fun decrypt(encryptedBase64: String?): String? {
        if (encryptedBase64.isNullOrBlank()) return null
        return try {
            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            if (combined.size <= GCM_IV_LENGTH_BYTES + 16) return null

            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            val cipherBytes = ByteArray(combined.size - GCM_IV_LENGTH_BYTES)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH_BYTES)
            System.arraycopy(combined, GCM_IV_LENGTH_BYTES, cipherBytes, 0, cipherBytes.size)

            val secretKey = getOrCreateMasterKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(cipherBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    // ==========================================
    // GETTERS & SETTERS (KeyStore Protected)
    // ==========================================

    fun saveOpenAiKey(key: String) {
        val encrypted = encrypt(key.trim())
        prefs.edit().putString(KEY_OPENAI_API_KEY, encrypted).apply()
    }

    fun getOpenAiKey(): String {
        val decrypted = decrypt(prefs.getString(KEY_OPENAI_API_KEY, null))
        if (!decrypted.isNullOrBlank()) return decrypted

        // Fallback to BuildConfig if provided at compile time
        return try {
            val buildConfigClass = BuildConfig::class.java
            val field = buildConfigClass.getField("OPENAI_API_KEY")
            val raw = (field.get(null) as? String)?.trim() ?: ""
            if (raw.startsWith("MY_")) "" else raw
        } catch (_: Exception) {
            ""
        }
    }

    fun saveDeepSeekKey(key: String) {
        val encrypted = encrypt(key.trim())
        prefs.edit().putString(KEY_DEEPSEEK_API_KEY, encrypted).apply()
    }

    fun getDeepSeekKey(): String {
        val decrypted = decrypt(prefs.getString(KEY_DEEPSEEK_API_KEY, null))
        if (!decrypted.isNullOrBlank()) return decrypted

        return try {
            val buildConfigClass = BuildConfig::class.java
            val field = buildConfigClass.getField("DEEPSEEK_API_KEY")
            val raw = (field.get(null) as? String)?.trim() ?: ""
            if (raw.startsWith("MY_")) "" else raw
        } catch (_: Exception) {
            ""
        }
    }

    fun saveXAiKey(key: String) {
        val encrypted = encrypt(key.trim())
        prefs.edit().putString(KEY_XAI_API_KEY, encrypted).apply()
    }

    fun getXAiKey(): String {
        val decrypted = decrypt(prefs.getString(KEY_XAI_API_KEY, null))
        if (!decrypted.isNullOrBlank()) return decrypted

        return try {
            val buildConfigClass = BuildConfig::class.java
            val field = buildConfigClass.getField("XAI_API_KEY")
            val raw = (field.get(null) as? String)?.trim() ?: ""
            if (raw.startsWith("MY_")) "" else raw
        } catch (_: Exception) {
            ""
        }
    }

    fun saveXTwitterBearerToken(token: String) {
        val encrypted = encrypt(token.trim())
        prefs.edit().putString(KEY_X_TWITTER_BEARER, encrypted).apply()
    }

    fun getXTwitterBearerToken(): String {
        val decrypted = decrypt(prefs.getString(KEY_X_TWITTER_BEARER, null))
        if (!decrypted.isNullOrBlank()) return decrypted

        return try {
            val buildConfigClass = BuildConfig::class.java
            val field = buildConfigClass.getField("X_TWITTER_BEARER_TOKEN")
            val raw = (field.get(null) as? String)?.trim() ?: ""
            if (raw.startsWith("MY_")) "" else raw
        } catch (_: Exception) {
            ""
        }
    }

    fun saveGeminiKey(key: String) {
        val encrypted = encrypt(key.trim())
        prefs.edit().putString(KEY_GEMINI_API_KEY, encrypted).apply()
    }

    fun getGeminiKey(): String {
        val decrypted = decrypt(prefs.getString(KEY_GEMINI_API_KEY, null))
        if (!decrypted.isNullOrBlank()) return decrypted

        return try {
            val buildConfigClass = BuildConfig::class.java
            val field = buildConfigClass.getField("GEMINI_API_KEY")
            (field.get(null) as? String)?.trim() ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    fun hasKeyFor(provider: String): Boolean {
        return when (provider.lowercase()) {
            "openai" -> getOpenAiKey().isNotBlank()
            "deepseek" -> getDeepSeekKey().isNotBlank()
            "xai", "grok" -> getXAiKey().isNotBlank()
            "x", "twitter" -> getXTwitterBearerToken().isNotBlank()
            "google", "gemini" -> getGeminiKey().isNotBlank()
            else -> false
        }
    }
}
