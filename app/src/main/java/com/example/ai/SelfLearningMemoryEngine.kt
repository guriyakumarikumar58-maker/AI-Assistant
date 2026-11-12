package com.example.ai

import com.example.data.SynapticMemoryDao
import com.example.data.SynapticMemoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Continuous Self-Learning System (Adaptive Synaptic Memory):
 * Analyzes conversation patterns, user habits, and emotional footprints
 * to continuously refine Arya AI's personalization.
 */
class SelfLearningMemoryEngine(private val memoryDao: SynapticMemoryDao) {

    suspend fun recordInteraction(
        userText: String,
        detectedMood: UserMood,
        isVoiceInput: Boolean
    ) = withContext(Dispatchers.IO) {
        val lower = userText.lowercase().trim()

        // 1. Learn Mood Trend
        val existingMoodMemory = memoryDao.getMemoryByKey("dominant_mood_${detectedMood.name}")
        if (existingMoodMemory != null) {
            memoryDao.updateMemory(
                existingMoodMemory.copy(
                    accessCount = existingMoodMemory.accessCount + 1,
                    synapticWeight = existingMoodMemory.synapticWeight + 0.15f,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        } else {
            memoryDao.insertMemory(
                SynapticMemoryEntity(
                    memoryKey = "dominant_mood_${detectedMood.name}",
                    memoryValue = detectedMood.titleHindi,
                    category = "EMOTION",
                    confidence = 0.88f,
                    synapticWeight = 1.0f
                )
            )
        }

        // 2. Learn User Interests & Topics
        val topicKeywords = mapOf(
            "coding" to ("प्रोग्रामिंग व कोडिंग" to "KNOWLEDGE"),
            "music" to ("संगीत व गीत" to "PREFERENCE"),
            "video" to ("वीडियो निर्माण" to "PREFERENCE"),
            "news" to ("ताजा समाचार व करंट अफेयर्स" to "HABIT"),
            "study" to ("शिक्षा व अध्ययन" to "HABIT"),
            "science" to ("विज्ञान व तकनीक" to "KNOWLEDGE"),
            "weather" to ("मौसम व दिनचर्या" to "HABIT")
        )

        for ((key, pair) in topicKeywords) {
            val (desc, category) = pair
            if (lower.contains(key) || lower.contains(desc.take(4))) {
                val existing = memoryDao.getMemoryByKey("interest_$key")
                if (existing != null) {
                    memoryDao.updateMemory(
                        existing.copy(
                            accessCount = existing.accessCount + 1,
                            synapticWeight = existing.synapticWeight + 0.25f,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                } else {
                    memoryDao.insertMemory(
                        SynapticMemoryEntity(
                            memoryKey = "interest_$key",
                            memoryValue = desc,
                            category = category,
                            confidence = 0.90f,
                            synapticWeight = 1.2f
                        )
                    )
                }
            }
        }

        // 3. Increment Overall Synaptic Experience
        val totalExp = memoryDao.getMemoryByKey("total_interactions")
        if (totalExp != null) {
            val newCount = totalExp.accessCount + 1
            memoryDao.updateMemory(
                totalExp.copy(
                    memoryValue = "$newCount बातचीत से सीखा",
                    accessCount = newCount,
                    synapticWeight = totalExp.synapticWeight + 0.05f,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        } else {
            memoryDao.insertMemory(
                SynapticMemoryEntity(
                    memoryKey = "total_interactions",
                    memoryValue = "1 बातचीत से सीखा",
                    category = "HABIT",
                    confidence = 1.0f,
                    synapticWeight = 1.0f
                )
            )
        }
    }

    suspend fun getLearnedProfileSummary(): String = withContext(Dispatchers.IO) {
        val total = memoryDao.getMemoryByKey("total_interactions")?.accessCount ?: 0
        val topHabit = memoryDao.getMemoryByKey("dominant_mood_JOYFUL")?.memoryValue ?: "शांत व सकारात्मक"
        "आर्या की सक्रिय मेमोरी: $total न्यूरल अनुभव संचित। प्रमुख स्वभाव: $topHabit"
    }
}
