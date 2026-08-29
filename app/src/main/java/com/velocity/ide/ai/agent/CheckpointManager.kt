package com.velocity.ide.ai.agent

import android.content.Context
import com.velocity.ide.data.database.CheckpointDao
import com.velocity.ide.data.database.CheckpointEntity
import com.velocity.ide.data.filesystem.ProjectFileSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Full-snapshot checkpoints of a project folder, stored as zip files under
 * filesDir/checkpoints with a row in the existing checkpoints table.
 * Used to restore the project state before an agent run.
 */
class CheckpointManager(
    private val context: Context,
    private val fs: ProjectFileSystem,
    private val checkpointDao: CheckpointDao
) {
    private val checkpointsRoot: File by lazy {
        File(context.filesDir, "checkpoints").apply { mkdirs() }
    }

    suspend fun createCheckpoint(
        projectName: String,
        projectId: String,
        title: String = "Agent run"
    ): CheckpointEntity? = withContext(Dispatchers.IO) {
        try {
            val projectDir = fs.resolveSafePath(projectName, "")
            val checkpointId = UUID.randomUUID().toString()
            val zipFile = File(checkpointsRoot, "$checkpointId.zip")

            ZipOutputStream(FileOutputStream(zipFile)).use { zip ->
                projectDir.walkTopDown().filter { it.isFile }.forEach { f ->
                    val rel = f.relativeTo(projectDir).path.replace('\\', '/')
                    if (rel.startsWith(".git/") || rel == "node_modules") return@forEach
                    zip.putNextEntry(ZipEntry(rel))
                    zip.write(f.readBytes())
                    zip.closeEntry()
                }
            }

            val entity = CheckpointEntity(
                id = checkpointId,
                projectId = projectId,
                title = title,
                timestamp = System.currentTimeMillis(),
                backupPath = zipFile.absolutePath
            )
            checkpointDao.insertCheckpoint(entity)
            entity
        } catch (e: Exception) {
            null
        }
    }

    /** Restores the project folder to the snapshot stored in [checkpointId]. */
    suspend fun rollback(checkpointId: String, projectName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val entity = checkpointDao.getCheckpointById(checkpointId) ?: return@withContext false
            val zipFile = File(entity.backupPath)
            if (!zipFile.exists()) return@withContext false

            val projectDir = fs.resolveSafePath(projectName, "")
            projectDir.listFiles()?.forEach { it.deleteRecursively() }

            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val target = File(projectDir, entry.name)
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        FileOutputStream(target).use { out -> zis.copyTo(out) }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Lists relative paths differing between the most recent snapshot and the live project. */
    suspend fun diffSummary(projectName: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val projectDir = fs.resolveSafePath(projectName, "")
            val latest = checkpointsRoot.listFiles()
                ?.filter { it.extension == "zip" }
                ?.maxByOrNull { it.lastModified() }
                ?: return@withContext emptyList()
            val zipFile = latest

            val oldPaths = mutableSetOf<String>()
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) oldPaths.add(entry.name)
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            val newPaths = projectDir.walkTopDown()
                .filter { it.isFile }
                .map { it.relativeTo(projectDir).path.replace('\\', '/') }
                .toSet()

            (oldPaths union newPaths).filter { rel ->
                val live = File(projectDir, rel)
                rel !in oldPaths || rel !in newPaths || live.length() != 0L && !live.readBytes().contentEquals(zipBytes(zipFile, rel))
            }.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun zipBytes(zipFile: File, rel: String): ByteArray? {
        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name == rel) {
                    return zis.readBytes()
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        return null
    }
}