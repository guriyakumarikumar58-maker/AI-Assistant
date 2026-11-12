package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Chat Message Entity for 30-day persistent session memory.
 */
@Entity(
    tableName = "chat_messages",
    indices = [Index(value = ["sessionId"])]
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val sessionId: String,
    val sender: String, // "USER" or "MODEL"
    val text: String,
    val imageBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
