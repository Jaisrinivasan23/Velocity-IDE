package com.velocity.ide.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String, // UUID or Project Name
    val name: String,
    val path: String,
    val createdAt: Long
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String, // UUID
    val projectId: String,      // foreign key equivalent
    val title: String,
    val createdAt: Long
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String, // UUID
    val chatId: String,         // foreign key equivalent
    val role: String,           // "user", "assistant", "system", "tool"
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "project_memories")
data class ProjectMemoryEntity(
    @PrimaryKey val id: String, // projectId + "_" + key
    val projectId: String,
    val key: String,
    val value: String,
    val updatedAt: Long
)

@Entity(tableName = "checkpoints")
data class CheckpointEntity(
    @PrimaryKey val id: String, // UUID
    val projectId: String,
    val title: String,
    val timestamp: Long,
    val backupPath: String
)

@Entity(tableName = "agent_runs")
data class AgentRunEntity(
    @PrimaryKey val id: String, // UUID
    val projectId: String,
    val chatId: String,
    val request: String,
    val startedAt: Long,
    val durationMs: Long,
    val result: String,          // COMPLETED / FAILED / CANCELLED
    val filesChanged: Int,
    val commands: String         // newline-joined executed commands
)
