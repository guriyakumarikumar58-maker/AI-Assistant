package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceLogDao {
    @Query("SELECT * FROM voice_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<VoiceLogEntity>>

    @Query("SELECT * FROM voice_logs WHERE isSecurityAlert = 1 ORDER BY timestamp DESC")
    fun getSecurityAlerts(): Flow<List<VoiceLogEntity>>

    @Query("SELECT * FROM voice_logs WHERE actionType = :actionType ORDER BY timestamp DESC")
    fun getLogsByAction(actionType: String): Flow<List<VoiceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VoiceLogEntity): Long

    @Delete
    suspend fun deleteLog(log: VoiceLogEntity)

    @Query("DELETE FROM voice_logs")
    suspend fun clearAllLogs()
}
