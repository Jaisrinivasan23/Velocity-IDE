package com.velocity.ide.ai.agent

import com.velocity.ide.data.filesystem.ProjectFileSystem
import com.velocity.ide.runtime.ProcessManager
import org.json.JSONArray
import org.json.JSONObject

data class ToolResult(
    val isSuccess: Boolean,
    val output: String
)

/** Risk classification used by the approval policy. */
enum class ToolPermission {
    READ, WRITE, EXECUTE, DESTRUCTIVE, MEMORY
}

interface AgentTool {
    val name: String
    val description: String
    val inputSchema: JSONObject
    val permission: ToolPermission

    suspend fun execute(projectName: String, arguments: JSONObject): ToolResult
}

/**
 * Marker for tools that operate on the persisted project identity (project id in the local
 * database) rather than the on-disk sandbox folder (project name). File tools use the
 * filesystem name; memory tools use the database id.
 */
interface ProjectMemoryTool

// 1. Read File Tool
class ReadFileTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "read_file"
    override val permission = ToolPermission.READ
    override val description = "Reads the content of a specific file inside the project directory."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "path": { "type": "string", "description": "Relative path to the file (e.g. src/App.tsx)" }
            },
            "required": ["path"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val path = arguments.optString("path")
        if (path.isNullOrBlank()) return ToolResult(false, "Missing 'path' argument.")
        return try {
            val content = fs.readFile(projectName, path)
            ToolResult(true, content)
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to read file.")
        }
    }
}

// 2. Write File Tool
class WriteFileTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "write_file"
    override val permission = ToolPermission.WRITE
    override val description = "Overwrites or creates a specific file inside the project directory."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "path": { "type": "string", "description": "Relative path to the file" },
                "content": { "type": "string", "description": "The exact content to write" }
            },
            "required": ["path", "content"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val path = arguments.optString("path")
        val content = arguments.optString("content")
        if (path.isNullOrBlank()) return ToolResult(false, "Missing 'path' argument.")
        return try {
            val success = fs.writeFile(projectName, path, content)
            if (success) ToolResult(true, "File written successfully.")
            else ToolResult(false, "Failed to write file.")
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to write file.")
        }
    }
}

// 3. List Files Tool
class ListFilesTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "list_files"
    override val permission = ToolPermission.READ
    override val description = "Lists files and subdirectories inside a specific path."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "path": { "type": "string", "description": "Relative path to directory to list. Use empty string for root." }
            },
            "required": ["path"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val path = arguments.optString("path") ?: ""
        return try {
            val files = fs.listDirectory(projectName, path)
            val output = files.joinToString("\n") { file ->
                val type = if (file.isDirectory) "[DIR]" else "[FILE]"
                "$type ${file.name}"
            }
            ToolResult(true, if (output.isEmpty()) "Directory is empty." else output)
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to list files.")
        }
    }
}

// 4. Run Command Tool
class RunCommandTool(
    private val fs: ProjectFileSystem,
    private val processManager: ProcessManager
) : AgentTool {
    override val name = "run_command"
    override val permission = ToolPermission.EXECUTE
    override val description = "Executes a shell command inside the project directory."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "command": { "type": "string", "description": "The command to run (e.g. npm install)" }
            },
            "required": ["command"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val command = arguments.optString("command")
        if (command.isNullOrBlank()) return ToolResult(false, "Missing 'command' argument.")
        return try {
            val projectDir = fs.resolveSafePath(projectName, "")
            val result = processManager.executeSynchronous(
                command = command.split(" "),
                workingDir = projectDir
            )
            ToolResult(result.exitCode == 0, "Exit code: ${result.exitCode}\nStdout: ${result.stdout}\nStderr: ${result.stderr}")
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to run command.")
        }
    }
}

// 5. Create Directory Tool
class CreateDirectoryTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "create_directory"
    override val permission = ToolPermission.WRITE
    override val description = "Creates a directory inside the project."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "path": { "type": "string", "description": "Relative path to the new directory" }
            },
            "required": ["path"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val path = arguments.optString("path")
        if (path.isNullOrBlank()) return ToolResult(false, "Missing 'path' argument.")
        return try {
            val success = fs.createDirectory(projectName, path)
            if (success) ToolResult(true, "Directory created successfully.")
            else ToolResult(false, "Failed to create directory.")
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to create directory.")
        }
    }
}

// 6. Delete File Tool
class DeleteFileTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "delete_file"
    override val permission = ToolPermission.DESTRUCTIVE
    override val description = "Deletes a file or directory inside the project."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "path": { "type": "string", "description": "Relative path to the file or directory to delete" }
            },
            "required": ["path"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val path = arguments.optString("path")
        if (path.isNullOrBlank()) return ToolResult(false, "Missing 'path' argument.")
        return try {
            val success = fs.deleteFile(projectName, path)
            if (success) ToolResult(true, "Deleted successfully.")
            else ToolResult(false, "Failed to delete.")
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to delete.")
        }
    }
}

// 7. Search Files Tool
class SearchFilesTool(private val fs: ProjectFileSystem) : AgentTool {
    override val name = "search_files"
    override val permission = ToolPermission.READ
    override val description = "Searches for a string inside files in a specific directory."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "query": { "type": "string", "description": "The string to search for" },
                "path": { "type": "string", "description": "Relative path to search in (use empty string for root)" }
            },
            "required": ["query", "path"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val query = arguments.optString("query")
        val searchPath = arguments.optString("path") ?: ""
        if (query.isNullOrBlank()) return ToolResult(false, "Missing 'query' argument.")
        
        return try {
            val root = fs.resolveSafePath(projectName, searchPath)
            if (!root.exists() || !root.isDirectory) {
                return ToolResult(false, "Search path is invalid or not a directory.")
            }
            
            val results = mutableListOf<String>()
            root.walkTopDown().forEach { file ->
                if (file.isFile) {
                    try {
                        val content = file.readText()
                        if (content.contains(query)) {
                            results.add("Found in: ${file.absolutePath.removePrefix(fs.resolveSafePath(projectName, "").absolutePath)}")
                        }
                    } catch (e: Exception) {
                        // Skip unreadable files
                    }
                }
            }
            ToolResult(true, if (results.isEmpty()) "No matches found." else results.take(50).joinToString("\n"))
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to search.")
        }
    }
}

// 8. Update Project Memory Tool
class UpdateProjectMemoryTool(
    private val memoryDao: com.velocity.ide.data.database.ProjectMemoryDao
) : AgentTool, ProjectMemoryTool {
    override val name = "update_project_memory"
    override val permission = ToolPermission.MEMORY
    override val description = "Updates the architectural or contextual memory for the project. Use keys like 'Architecture Overview', 'Tech Stack & Dependencies', 'Design System & Conventions', or 'AI Decision Memory'."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {
                "key": { "type": "string", "description": "The category key of the memory (e.g. 'Tech Stack')" },
                "value": { "type": "string", "description": "The detailed markdown content for this memory category" }
            },
            "required": ["key", "value"]
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        val key = arguments.optString("key")
        val value = arguments.optString("value")
        if (key.isNullOrBlank() || value.isNullOrBlank()) return ToolResult(false, "Missing 'key' or 'value' argument.")
        
        return try {
            val id = "${projectName}_$key"
            val entity = com.velocity.ide.data.database.ProjectMemoryEntity(
                id = id,
                projectId = projectName,
                key = key,
                value = value,
                updatedAt = System.currentTimeMillis()
            )
            memoryDao.insertMemory(entity)
            ToolResult(true, "Project memory '$key' updated successfully.")
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to update project memory.")
        }
    }
}

// 9. Get Project Memory Tool
class GetProjectMemoryTool(
    private val memoryDao: com.velocity.ide.data.database.ProjectMemoryDao
) : AgentTool, ProjectMemoryTool {
    override val name = "get_project_memory"
    override val permission = ToolPermission.MEMORY
    override val description = "Retrieves all currently stored architectural and contextual memories for the project."
    override val inputSchema = JSONObject("""
        {
            "type": "object",
            "properties": {},
            "required": []
        }
    """.trimIndent())

    override suspend fun execute(projectName: String, arguments: JSONObject): ToolResult {
        return try {
            val memories = memoryDao.getMemoriesForProject(projectName)
            if (memories.isEmpty()) {
                ToolResult(true, "No project memories stored yet.")
            } else {
                val output = memories.joinToString("\n\n") { "### ${it.key}\n${it.value}" }
                ToolResult(true, output)
            }
        } catch (e: Exception) {
            ToolResult(false, e.message ?: "Failed to get project memory.")
        }
    }
}
