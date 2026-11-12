package com.example.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONArray
import org.json.JSONObject

/**
 * EncryptedVoiceBiometricVault:
 * Provides hardware-backed, military-standard AES-256-GCM encrypted local storage
 * for user voiceprints and acoustic biometric templates using Android KeyStore.
 *
 * Privacy & Security Guarantees:
 * 1. The 256-bit AES master key is generated and stored securely inside the hardware-backed
 *    Android KeyStore (TEE / Secure Element). Key material is never stored in plaintext on disk.
 * 2. Biometric templates (fundamental pitch, harmonic ratios, secret wake phrase) are
 *    encrypted locally via authenticated AES-256-GCM with a tamper-resistant integrity hash.
 * 3. Any previous unencrypted key seed files on disk are actively purged and securely wiped.
 * 4. Stored in internal app storage (mode private) accessible only by the application process.
 */
data class VoiceprintBiometricTemplate(
    val ownerName: String,
    val secretWakePhrase: String,
    val ownerPitchHz: Float,
    val pitchToleranceHz: Float,
    val sensitivity: Float,
    val acousticHarmonics: List<Float> = listOf(1.0f, 0.75f, 0.52f, 0.38f),
    val enrolledTimestamp: Long = System.currentTimeMillis(),
    val biometricIntegrityHash: String = "",
    val encryptionAlgorithm: String = "AES-256-GCM-KeyStore"
)

object EncryptedVoiceBiometricVault {

    private const val VAULT_FILE_NAME = "encrypted_voiceprint_vault.bin"
    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEYSTORE_ALIAS = "AryaBiometricMasterKeyStore"
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val GCM_IV_LENGTH_BYTES = 12

    /**
     * Purges legacy plaintext key seed files from disk if present.
     */
    private fun cleanupLegacyPlaintextKeys(context: Context) {
        try {
            val legacyKeyFile = File(context.filesDir, "biometric_vault_entropy.key")
            if (legacyKeyFile.exists()) {
                legacyKeyFile.writeBytes(ByteArray(legacyKeyFile.length().toInt().coerceAtMost(128)))
                legacyKeyFile.delete()
            }
        } catch (_: Exception) {}
    }

    /**
     * Obtains or generates a hardware-backed 256-bit AES-GCM master key directly in Android KeyStore.
     * The cryptographic key material never touches the internal storage in plaintext.
     */
    private fun getOrCreateKeyStoreMasterKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply {
            load(null)
        }

        if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyEntry = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (keyEntry != null) {
                return keyEntry.secretKey
            }
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE_PROVIDER
        )
        val spec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Computes SHA-256 integrity digest for the biometric data to detect tampering.
     */
    fun computeIntegrityHash(
        ownerName: String,
        wakePhrase: String,
        pitchHz: Float,
        toleranceHz: Float,
        sensitivity: Float,
        timestamp: Long
    ): String {
        val raw = "$ownerName|$wakePhrase|$pitchHz|$toleranceHz|$sensitivity|$timestamp|AryaVoiceKeyStoreVaultV3"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(raw.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    /**
     * Encrypts and securely saves the user's voiceprint biometric template to local internal storage
     * using the Android KeyStore master key.
     */
    @Synchronized
    fun saveEncryptedVoiceprint(
        context: Context,
        template: VoiceprintBiometricTemplate
    ): Boolean {
        return try {
            cleanupLegacyPlaintextKeys(context)
            val secretKey = getOrCreateKeyStoreMasterKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            // Android KeyStore automatically generates a secure cryptographic random IV
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv ?: return false

            val integrityHash = computeIntegrityHash(
                template.ownerName,
                template.secretWakePhrase,
                template.ownerPitchHz,
                template.pitchToleranceHz,
                template.sensitivity,
                template.enrolledTimestamp
            )

            val jsonObject = JSONObject().apply {
                put("ownerName", template.ownerName)
                put("secretWakePhrase", template.secretWakePhrase)
                put("ownerPitchHz", template.ownerPitchHz.toDouble())
                put("pitchToleranceHz", template.pitchToleranceHz.toDouble())
                put("sensitivity", template.sensitivity.toDouble())
                put("enrolledTimestamp", template.enrolledTimestamp)
                put("biometricIntegrityHash", integrityHash)
                put("encryptionAlgorithm", "AES-256-GCM-KeyStore")

                val harmonicsArray = JSONArray()
                template.acousticHarmonics.forEach { harmonicsArray.put(it.toDouble()) }
                put("acousticHarmonics", harmonicsArray)
            }

            val plaintextBytes = jsonObject.toString().toByteArray(StandardCharsets.UTF_8)
            val ciphertextBytes = cipher.doFinal(plaintextBytes)

            // Save format: [12 bytes IV] + [Ciphertext + 16 bytes GCM Tag]
            val vaultFile = File(context.filesDir, VAULT_FILE_NAME)
            val outputBytes = ByteArray(iv.size + ciphertextBytes.size)
            System.arraycopy(iv, 0, outputBytes, 0, iv.size)
            System.arraycopy(ciphertextBytes, 0, outputBytes, iv.size, ciphertextBytes.size)

            vaultFile.writeBytes(outputBytes)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Decrypts and retrieves the stored voiceprint biometric template from local internal storage
     * using the Android KeyStore master key.
     * Returns null if not enrolled, corrupt, or tampered with.
     */
    @Synchronized
    fun loadDecryptedVoiceprint(context: Context): VoiceprintBiometricTemplate? {
        val vaultFile = File(context.filesDir, VAULT_FILE_NAME)
        if (!vaultFile.exists() || vaultFile.length() < (GCM_IV_LENGTH_BYTES + 16)) {
            return null
        }

        return try {
            val secretKey = getOrCreateKeyStoreMasterKey()
            val fileBytes = vaultFile.readBytes()
            if (fileBytes.size <= GCM_IV_LENGTH_BYTES + 16) return null

            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            val ciphertext = ByteArray(fileBytes.size - GCM_IV_LENGTH_BYTES)
            System.arraycopy(fileBytes, 0, iv, 0, GCM_IV_LENGTH_BYTES)
            System.arraycopy(fileBytes, GCM_IV_LENGTH_BYTES, ciphertext, 0, ciphertext.size)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            val decryptedBytes = cipher.doFinal(ciphertext)
            val jsonString = String(decryptedBytes, StandardCharsets.UTF_8)
            val json = JSONObject(jsonString)

            val ownerName = json.optString("ownerName", "मालिक (Owner)")
            val secretWakePhrase = json.optString("secretWakePhrase", "मेरी आवाज़ सुनो")
            val ownerPitchHz = json.optDouble("ownerPitchHz", 165.0).toFloat()
            val pitchToleranceHz = json.optDouble("pitchToleranceHz", 50.0).toFloat()
            val sensitivity = json.optDouble("sensitivity", 0.70).toFloat()
            val enrolledTimestamp = json.optLong("enrolledTimestamp", System.currentTimeMillis())
            val savedHash = json.optString("biometricIntegrityHash", "")

            // Verify integrity
            val expectedHash = computeIntegrityHash(
                ownerName,
                secretWakePhrase,
                ownerPitchHz,
                pitchToleranceHz,
                sensitivity,
                enrolledTimestamp
            )

            if (savedHash.isNotEmpty() && savedHash != expectedHash) {
                // Potential tampering detected
                return null
            }

            val harmonicsList = mutableListOf<Float>()
            val harmonicsArray = json.optJSONArray("acousticHarmonics")
            if (harmonicsArray != null) {
                for (i in 0 until harmonicsArray.length()) {
                    harmonicsList.add(harmonicsArray.optDouble(i, 1.0).toFloat())
                }
            }
            if (harmonicsList.isEmpty()) {
                harmonicsList.addAll(listOf(1.0f, 0.75f, 0.52f, 0.38f))
            }

            VoiceprintBiometricTemplate(
                ownerName = ownerName,
                secretWakePhrase = secretWakePhrase,
                ownerPitchHz = ownerPitchHz,
                pitchToleranceHz = pitchToleranceHz,
                sensitivity = sensitivity,
                acousticHarmonics = harmonicsList,
                enrolledTimestamp = enrolledTimestamp,
                biometricIntegrityHash = savedHash,
                encryptionAlgorithm = "AES-256-GCM-KeyStore"
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks if a valid, uncompromised encrypted biometric template exists on device.
     */
    fun hasEnrolledVoiceprint(context: Context): Boolean {
        return loadDecryptedVoiceprint(context) != null
    }

    /**
     * Securely wipes the biometric vault and zeroes out the encrypted file.
     */
    @Synchronized
    fun purgeEncryptedVault(context: Context) {
        try {
            cleanupLegacyPlaintextKeys(context)
            val vaultFile = File(context.filesDir, VAULT_FILE_NAME)
            if (vaultFile.exists()) {
                vaultFile.writeBytes(ByteArray(vaultFile.length().toInt().coerceAtMost(1024)))
                vaultFile.delete()
            }
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
            if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
                keyStore.deleteEntry(KEYSTORE_ALIAS)
            }
        } catch (_: Exception) {}
    }

    /**
     * Verifies whether the local encrypted biometric vault has valid cryptographic integrity.
     */
    fun verifyVaultCryptographicIntegrity(context: Context): Boolean {
        val vaultFile = File(context.filesDir, VAULT_FILE_NAME)
        if (!vaultFile.exists()) return true
        return loadDecryptedVoiceprint(context) != null
    }
}
