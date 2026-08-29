package com.velocity.ide.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProjectEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        ProjectMemoryEntity::class,
        CheckpointEntity::class,
        AgentRunEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun projectDao(): ProjectDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun projectMemoryDao(): ProjectMemoryDao
    abstract fun checkpointDao(): CheckpointDao
    abstract fun agentRunDao(): AgentRunDao

    /** Destructive full reset for the "Data" settings section. */
    suspend fun clearAllData() = withContext(kotlinx.coroutines.Dispatchers.IO) {
        clearAllTables()
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "velocity_ide_database"
                )
                .fallbackToDestructiveMigration() // Simple for development/prototype builds
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
