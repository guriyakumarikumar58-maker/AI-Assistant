package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Continuous Self-Learning System Entity:
 * Stores learned habits, emotional memory footprints, preferred topics, and synaptic weights
 * allowing Arya AI to continuously evolve and adapt to the individual user.
 */
@Entity(tableName = "synaptic_memories")
data class SynapticMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memoryKey: String,
    val memoryValue: String,
    val category: String, // "EMOTION", "HABIT", "KNOWLEDGE", "PREFERENCE"
    val confidence: Float = 0.85f,
    val synapticWeight: Float = 1.0f,
    val accessCount: Int = 1,
    val lastUpdated: Long = System.currentTimeMillis()
)
