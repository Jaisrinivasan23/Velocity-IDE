package com.velocity.ide.ai.agent

import com.velocity.ide.data.filesystem.ProjectFileSystem
import com.velocity.ide.runtime.ProcessManager
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

class ProjectContextEngine(
    private val fs: ProjectFileSystem,
    private val processManager: ProcessManager,
    private val memoryDao: com.velocity.ide.data.database.ProjectMemoryDao
) {
    /**
     * Builds the system prompt / context to inject into the LLM.
     * This ensures the LLM knows what project it is looking at before it starts planning.
     */
    suspend fun buildProjectContext(projectName: String): String {
        val root = try {
            fs.resolveSafePath(projectName, "")
        } catch (e: Exception) {
            return "Error resolving project path: ${e.message}"
        }

        val tree = generateFileTree(root, maxDepth = 3)
        
        val memories = memoryDao.getMemoriesForProject(projectName)
        val memoryBlock = if (memories.isEmpty()) {
            "No project memory has been established yet. You should use update_project_memory to document the Architecture, Tech Stack, and Design System as you build."
        } else {
            memories.joinToString("\n\n") { "### ${it.key}\n${it.value}" }.take(8192) // Context budget: cap memory block
        }
        
        return """
            You are Velocity AI, an autonomous software engineering agent operating inside an Android IDE.
            You have full access to tools to read files, create files, edit files, and run terminal commands.
            
            Current Project: $projectName
            Root Path: ${root.absolutePath}
            
            --- PROJECT MEMORY ---
            $memoryBlock
            ----------------------
            
            Project Structure (Max Depth 3):
            $tree
            
            Guidelines:
            1. Think step-by-step.
            2. If you need to understand a file, use read_file.
            3. If you need to create a component, use create_file or write_file.
            4. If you need to install dependencies, use run_command.
            5. Do NOT guess file contents. Read them if you need to modify them.
            6. Keep the Project Memory updated using the update_project_memory tool whenever you establish a new pattern or dependency.
        """.trimIndent()
    }

    private fun generateFileTree(dir: File, maxDepth: Int, currentDepth: Int = 0): String {
        if (currentDepth > maxDepth) return "  ".repeat(currentDepth) + "...\n"
        
        val builder = StringBuilder()
        val files = dir.listFiles()?.sortedBy { !it.isDirectory } ?: emptyList()
        
        for (file in files) {
            // Ignore common massive dirs
            if (file.name == "node_modules" || file.name == ".git" || file.name == "build") {
                builder.append("  ".repeat(currentDepth)).append("[DIR] ${file.name} (skipped)\n")
                continue
            }
            
            val type = if (file.isDirectory) "[DIR]" else "[FILE]"
            builder.append("  ".repeat(currentDepth)).append("$type ${file.name}\n")
            
            if (file.isDirectory) {
                builder.append(generateFileTree(file, maxDepth, currentDepth + 1))
            }
        }
        return builder.toString()
    }
}
