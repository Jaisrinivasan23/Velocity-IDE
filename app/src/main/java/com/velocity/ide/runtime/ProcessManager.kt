package com.velocity.ide.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

data class ProcessResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
)

class ProcessManager {

    private val activeProcesses = ConcurrentHashMap<String, Process>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _terminalOutputFlow = MutableSharedFlow<String>(replay = 50)
    val terminalOutputFlow: SharedFlow<String> = _terminalOutputFlow

    /**
     * Spawns a background shell command process inside a sandboxed working directory.
     */
    fun startProcess(
        processId: String,
        commandLine: String,
        workingDir: File,
        environment: Map<String, String> = emptyMap()
    ) {
        // Kill existing process with the same ID if running
        stopProcess(processId)

        coroutineScope.launch {
            try {
                // On Android, commands must be run via the shell
                val processBuilder = ProcessBuilder("/system/bin/sh", "-c", commandLine)
                    .directory(workingDir)
                    .redirectErrorStream(true)

                // Inject environment variables
                processBuilder.environment().putAll(environment)

                val process = processBuilder.start()
                activeProcesses[processId] = process

                emitOutput("[Process Started: $commandLine]\n")

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    emitOutput("$line\n")
                }

                // Wait for exit
                val finished = process.waitFor(10, TimeUnit.MINUTES)
                val exitCode = if (finished) process.exitValue() else -1
                emitOutput("[Process Exited with code $exitCode]\n")

            } catch (e: Exception) {
                emitOutput("[Process Error: ${e.message}]\n")
            } finally {
                activeProcesses.remove(processId)
            }
        }
    }

    /**
     * Executes a process synchronously and returns the output (useful for tests or short calls).
     */
    fun executeSynchronous(
        command: List<String>,
        workingDir: File,
        timeoutSeconds: Long = 10
    ): ProcessResult {
        return try {
            val process = ProcessBuilder(command)
                .directory(workingDir)
                .start()

            val stdoutBuilder = StringBuilder()
            val stderrBuilder = StringBuilder()

            val stdoutJob = coroutineScope.launch {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stdoutBuilder.append(line).append("\n")
                    }
                }
            }

            val stderrJob = coroutineScope.launch {
                BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stderrBuilder.append(line).append("\n")
                    }
                }
            }

            val exited = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            if (!exited) {
                process.destroyForcibly()
                return ProcessResult(-1, stdoutBuilder.toString(), "Process timed out after $timeoutSeconds seconds")
            }

            kotlinx.coroutines.runBlocking {
                stdoutJob.join()
                stderrJob.join()
            }

            ProcessResult(process.exitValue(), stdoutBuilder.toString(), stderrBuilder.toString())
        } catch (e: Exception) {
            ProcessResult(-1, "", e.message ?: "Unknown Process Error")
        }
    }

    fun writeToStdin(processId: String, data: String) {
        val process = activeProcesses[processId] ?: return
        coroutineScope.launch {
            try {
                OutputStreamWriter(process.outputStream).use { writer ->
                    writer.write(data)
                    writer.flush()
                }
            } catch (e: Exception) {
                emitOutput("[Failed to write to stdin: ${e.message}]\n")
            }
        }
    }

    fun stopProcess(processId: String) {
        val process = activeProcesses.remove(processId)
        if (process != null && process.isAlive) {
            process.destroy()
            coroutineScope.launch {
                emitOutput("[Process Force Stopped]\n")
            }
        }
    }

    private suspend fun emitOutput(text: String) {
        _terminalOutputFlow.emit(text)
    }
}
