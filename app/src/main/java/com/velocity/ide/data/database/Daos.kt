package com.velocity.ide.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getChatsForProjectFlow(projectId: String): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats ORDER BY createdAt DESC")
    fun getAllChatsFlow(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatById(chatId: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE projectId = :projectId ORDER BY createdAt DESC")
    suspend fun getChatsForProject(projectId: String): List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("UPDATE chats SET title = :title WHERE id = :chatId")
    suspend fun renameChat(chatId: String, title: String)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChatFlow(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    suspend fun getMessagesForChat(chatId: String): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)
}

@Dao
interface ProjectMemoryDao {
    @Query("SELECT * FROM project_memories WHERE projectId = :projectId")
    fun getMemoriesForProjectFlow(projectId: String): Flow<List<ProjectMemoryEntity>>

    @Query("SELECT * FROM project_memories WHERE projectId = :projectId")
    suspend fun getMemoriesForProject(projectId: String): List<ProjectMemoryEntity>

    @Query("SELECT * FROM project_memories WHERE projectId = :projectId AND `key` = :key")
    suspend fun getMemoryByKey(projectId: String, key: String): ProjectMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: ProjectMemoryEntity)

    @Query("DELETE FROM project_memories WHERE projectId = :projectId AND `key` = :key")
    suspend fun deleteMemoryByKey(projectId: String, key: String)
}

@Dao
interface CheckpointDao {
    @Query("SELECT * FROM checkpoints WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getCheckpointsForProjectFlow(projectId: String): Flow<List<CheckpointEntity>>

    @Query("SELECT * FROM checkpoints WHERE id = :checkpointId")
    suspend fun getCheckpointById(checkpointId: String): CheckpointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckpoint(checkpoint: CheckpointEntity)

    @Query("DELETE FROM checkpoints WHERE id = :checkpointId")
    suspend fun deleteCheckpointById(checkpointId: String)
}

@Dao
interface AgentRunDao {
    @Query("SELECT * FROM agent_runs WHERE projectId = :projectId ORDER BY startedAt DESC")
    fun getRunsForProjectFlow(projectId: String): Flow<List<AgentRunEntity>>

    @Query("SELECT * FROM agent_runs WHERE projectId = :projectId ORDER BY startedAt DESC LIMIT :limit")
    suspend fun getRecentRuns(projectId: String, limit: Int): List<AgentRunEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: AgentRunEntity)

    @Query("DELETE FROM agent_runs WHERE projectId = :projectId")
    suspend fun deleteRunsForProject(projectId: String)
}
