package com.example.data

import kotlinx.coroutines.flow.Flow

class VoiceLockRepository(
    private val dao: VoiceLogDao,
    private val synapticDao: SynapticMemoryDao? = null
) {
    val allLogs: Flow<List<VoiceLogEntity>> = dao.getAllLogs()
    val securityAlerts: Flow<List<VoiceLogEntity>> = dao.getSecurityAlerts()
    val allSynapticMemories: Flow<List<SynapticMemoryEntity>> =
        synapticDao?.getAllMemories() ?: kotlinx.coroutines.flow.flowOf(emptyList())

    fun getLogsByAction(actionType: String): Flow<List<VoiceLogEntity>> {
        return dao.getLogsByAction(actionType)
    }

    suspend fun insertLog(log: VoiceLogEntity): Long {
        return dao.insertLog(log)
    }

    suspend fun deleteLog(log: VoiceLogEntity) {
        dao.deleteLog(log)
    }

    suspend fun clearAllLogs() {
        dao.clearAllLogs()
    }

    suspend fun insertSynapticMemory(memory: SynapticMemoryEntity) {
        synapticDao?.insertMemory(memory)
    }

    suspend fun clearAllSynapticMemories() {
        synapticDao?.clearAllMemories()
    }
}
