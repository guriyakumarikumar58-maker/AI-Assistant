package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VoiceLogEntity::class,
        SynapticMemoryEntity::class,
        NoteEntity::class,
        ChatSessionEntity::class,
        ChatMessageEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class VoiceLockDatabase : RoomDatabase() {
    abstract fun voiceLogDao(): VoiceLogDao
    abstract fun synapticMemoryDao(): SynapticMemoryDao
    abstract fun noteDao(): NoteDao
    abstract fun chatMemoryDao(): ChatMemoryDao

    companion object {
        @Volatile
        private var INSTANCE: VoiceLockDatabase? = null

        fun getDatabase(context: Context): VoiceLockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoiceLockDatabase::class.java,
                    "voicelock_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
