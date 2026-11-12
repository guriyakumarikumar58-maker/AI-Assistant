package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_logs")
data class VoiceLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val spokenText: String,
    val aiResponse: String? = null,
    val speakerStatus: String, // "VERIFIED_OWNER", "UNAUTHORIZED_ATTEMPT", "PIN_FALLBACK", "VOICE_ENROLLMENT"
    val actionType: String,    // "LOCK_DEVICE", "UNLOCK_APP", "AI_CHAT", "VOICE_TRAINING", "SECURITY_ALERT"
    val confidenceScore: Float = 1.0f,
    val isSecurityAlert: Boolean = false
)
