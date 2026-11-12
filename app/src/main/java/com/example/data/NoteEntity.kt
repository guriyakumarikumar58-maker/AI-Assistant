package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "सामान्य", // कार्य, निजी, विचार, महत्वपूर्ण
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
