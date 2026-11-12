package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SynapticMemoryDao {

    @Query("SELECT * FROM synaptic_memories ORDER BY synapticWeight DESC, lastUpdated DESC")
    fun getAllMemories(): Flow<List<SynapticMemoryEntity>>

    @Query("SELECT * FROM synaptic_memories WHERE category = :category ORDER BY synapticWeight DESC")
    fun getMemoriesByCategory(category: String): Flow<List<SynapticMemoryEntity>>

    @Query("SELECT * FROM synaptic_memories WHERE memoryKey = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): SynapticMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: SynapticMemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: SynapticMemoryEntity)

    @Query("DELETE FROM synaptic_memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM synaptic_memories")
    suspend fun clearAllMemories()

    @Query("SELECT COUNT(*) FROM synaptic_memories")
    fun getMemoryCount(): Flow<Int>
}
