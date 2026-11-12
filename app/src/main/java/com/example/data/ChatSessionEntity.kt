package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 30-Day Chat Session Memory Entity:
 * Stores chat conversation sessions with title, message count, and last active timestamp.
 * Auto-retention policy keeps sessions for 30 days so users can resume past discussions.
 */
@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val sessionId: String,
    val title: String,
    val snippet: String = "",
    val messageCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis()
)
