package com.velocity.ide.data.filesystem

import android.content.Context
import java.io.File
import java.io.IOException

class ProjectFileSystem(private val context: Context) {

    // Projects root directory inside the app's sandboxed internal files storage
    private val projectsRoot: File by lazy {
        File(context.filesDir, "projects").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Resolves a relative path to a File object, ensuring it is strictly sandboxed
     * inside the project's root directory. Throws SecurityException if path traversal is detected.
     */
    fun resolveSafePath(projectName: String, relativePath: String): File {
        // Prevent empty or malicious project names
        if (projectName.isBlank() || projectName.contains("/") || projectName.contains("\\") || projectName == "..") {
            throw SecurityException("Invalid project name: $projectName")
        }

        val projectDir = File(projectsRoot, projectName).canonicalFile
        val targetFile = File(projectDir, relativePath).canonicalFile

        // Security check: Verify target file is still inside the project root directory
        if (!targetFile.path.startsWith(projectDir.path)) {
            throw SecurityException("Access denied: Path traversal detected outside project sandbox: $relativePath")
        }

        return targetFile
    }

    fun createProject(projectName: String): File {
        val projectDir = resolveSafePath(projectName, "")
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
        return projectDir
    }

    fun deleteProject(projectName: String): Boolean {
        val projectDir = resolveSafePath(projectName, "")
        return projectDir.deleteRecursively()
    }

    fun listDirectory(projectName: String, relativePath: String): List<File> {
        val dir = resolveSafePath(projectName, relativePath)
        if (!dir.exists() || !dir.isDirectory) {
            return emptyList()
        }
        return dir.listFiles()?.toList() ?: emptyList()
    }

    fun createDirectory(projectName: String, relativePath: String): Boolean {
        val dir = resolveSafePath(projectName, relativePath)
        return dir.mkdirs()
    }

    fun createFile(projectName: String, relativePath: String): Boolean {
        val file = resolveSafePath(projectName, relativePath)
        // Ensure parent directories exist
        file.parentFile?.mkdirs()
        return try {
            file.createNewFile()
        } catch (e: IOException) {
            false
        }
    }

    fun readFile(projectName: String, relativePath: String): String {
        val file = resolveSafePath(projectName, relativePath)
        if (!file.exists() || !file.isFile) {
            throw IOException("File does not exist or is not a file: $relativePath")
        }
        return file.readText(Charsets.UTF_8)
    }

    fun writeFile(projectName: String, relativePath: String, content: String): Boolean {
        val file = resolveSafePath(projectName, relativePath)
        // Ensure parent directories exist
        file.parentFile?.mkdirs()
        return try {
            file.writeText(content, Charsets.UTF_8)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteFile(projectName: String, relativePath: String): Boolean {
        val file = resolveSafePath(projectName, relativePath)
        return if (file.exists()) {
            file.deleteRecursively()
        } else {
            false
        }
    }

    fun rename(projectName: String, relativePath: String, newName: String): Boolean {
        val file = resolveSafePath(projectName, relativePath)
        if (!file.exists()) return false

        val parent = file.parentFile ?: return false
        val target = File(parent, newName).canonicalFile

        // Security check for the new name
        val projectDir = resolveSafePath(projectName, "").canonicalFile
        if (!target.path.startsWith(projectDir.path)) {
            throw SecurityException("Rename target escapes the project sandbox.")
        }

        return file.renameTo(target)
    }

    fun exists(projectName: String, relativePath: String): Boolean {
        return try {
            resolveSafePath(projectName, relativePath).exists()
        } catch (e: SecurityException) {
            false
        }
    }

    fun getProjectsList(): List<String> {
        return projectsRoot.listFiles()?.filter { it.isDirectory }?.map { it.name } ?: emptyList()
    }
}
