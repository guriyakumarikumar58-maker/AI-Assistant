package com.example.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
import com.example.data.VoiceLogEntity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseManager(private val context: Context) {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _cloudMessage = MutableStateFlow<String?>(null)
    val cloudMessage: StateFlow<String?> = _cloudMessage.asStateFlow()

    private var auth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            val app = FirebaseApp.initializeApp(context) ?: FirebaseApp.getInstance()
            auth = FirebaseAuth.getInstance(app)
            val databaseId = try {
                context.getString(R.string.firestore_database_id)
            } catch (_: Exception) {
                "ai-studio-android-aryaaias-4ff713d5-c9ea-41fb-8a8e-d0e3bbd570c3"
            }
            firestore = FirebaseFirestore.getInstance(app, databaseId)
            _currentUser.value = auth?.currentUser

            auth?.addAuthStateListener { firebaseAuth ->
                _currentUser.value = firebaseAuth.currentUser
                if (firebaseAuth.currentUser != null) {
                    _cloudMessage.value = "Google लॉगिन सक्रिय (${firebaseAuth.currentUser?.displayName ?: firebaseAuth.currentUser?.email ?: "मालिक"})"
                }
            }
        } catch (e: Exception) {
            Log.w("FirebaseManager", "Firebase initialization notice: ${e.message}")
        }
    }

    fun isFirebaseConfigured(): Boolean {
        return auth != null && firestore != null
    }

    /**
     * Signs in using Google via Credential Manager (Mandatory Invariant).
     */
    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser?> = withContext(Dispatchers.Main) {
        val authInstance = auth
        if (authInstance == null) {
            val err = "Firebase Auth इनिशियलाइज़ नहीं हुआ है"
            _cloudMessage.value = err
            return@withContext Result.failure(IllegalStateException(err))
        }

        try {
            val webClientId = getWebClientId()
            val credentialManager = CredentialManager.create(activity)
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = activity
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = authInstance.signInWithCredential(authCredential).await()
                val user = authResult.user
                _currentUser.value = user
                _cloudMessage.value = "Google खाता लिंक हुआ: ${user?.displayName ?: user?.email}"
                Result.success(user)
            } else {
                val msg = "अमान्य क्रेडेंशियल प्राप्त हुआ"
                _cloudMessage.value = msg
                Result.failure(Exception(msg))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.i("FirebaseManager", "Google Sign-In was cancelled by user: ${e.message}")
            _cloudMessage.value = "साइन-इन रद्द किया गया (Retry available)"
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.w("FirebaseManager", "Credential Manager error: ${e.message}")
            _cloudMessage.value = "Google साइन-इन त्रुटि: ${e.localizedMessage}"
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Firebase Google Sign-in error", e)
            _cloudMessage.value = "साइन-इन विफलता: ${e.localizedMessage}"
            Result.failure(e)
        }
    }

    private fun getWebClientId(): String {
        return try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) context.getString(resId) else "426554689974-2u1qmqh1vmadr9627oa5p4hk4ed6k0bn.apps.googleusercontent.com"
        } catch (_: Exception) {
            "426554689974-2u1qmqh1vmadr9627oa5p4hk4ed6k0bn.apps.googleusercontent.com"
        }
    }

    suspend fun saveVoiceProfileToFirestore(ownerName: String, wakePhrase: String, pitchHz: Float): Boolean = withContext(Dispatchers.IO) {
        val uid = _currentUser.value?.uid
        if (uid == null) {
            _cloudMessage.value = "Firestore में सेव करने के लिए कृपया पहले Google साइन-इन करें"
            return@withContext false
        }
        val fs = firestore ?: return@withContext false

        try {
            _isCloudSyncing.value = true
            val profileData = hashMapOf(
                "ownerName" to ownerName,
                "secretWakePhrase" to wakePhrase,
                "ownerPitchHz" to pitchHz,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            fs.collection("users").document(uid).set(profileData, SetOptions.merge()).await()
            _isCloudSyncing.value = false
            _cloudMessage.value = "Firestore क्लाउड पर वॉइस प्रोफ़ाइल सिंक हो गई!"
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Firestore sync error", e)
            _isCloudSyncing.value = false
            _cloudMessage.value = "क्लाउड सिंक त्रुटि: ${e.localizedMessage}"
            false
        }
    }

    suspend fun syncLogToFirestore(log: VoiceLogEntity) = withContext(Dispatchers.IO) {
        val uid = _currentUser.value?.uid ?: return@withContext
        val fs = firestore ?: return@withContext

        try {
            val logData = hashMapOf(
                "spokenText" to log.spokenText,
                "aiResponse" to (log.aiResponse ?: ""),
                "actionType" to log.actionType,
                "speakerStatus" to log.speakerStatus,
                "timestamp" to FieldValue.serverTimestamp(),
                "isSecurityAlert" to log.isSecurityAlert
            )
            fs.collection("users").document(uid).collection("voice_logs").add(logData).await()
        } catch (e: Exception) {
            Log.w("FirebaseManager", "Failed to sync log to Firestore: ${e.message}")
        }
    }

    fun signOut() {
        auth?.signOut()
        _currentUser.value = null
        _cloudMessage.value = "साइन आउट किया गया"
    }
}
