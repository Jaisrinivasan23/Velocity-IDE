package com.velocity.ide.ai.agent

import com.velocity.ide.ai.provider.*
import com.velocity.ide.data.database.AgentRunDao
import com.velocity.ide.data.database.AgentRunEntity
import com.velocity.ide.data.database.MessageDao
import com.velocity.ide.data.database.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.util.UUID

class AgentEngine(
    private val llmProvider: LlmProvider,
    private val messageDao: MessageDao,
    private val toolRegistry: ToolRegistry,
    private val contextEngine: ProjectContextEngine,
    private val approvalPolicy: ApprovalPolicy? = null,
    private val checkpointManager: CheckpointManager? = null,
    private val runDao: AgentRunDao? = null,
    private val mode: AgentMode = AgentMode.AGENT,
    private val temperature: Double? = null,
    private val maxSteps: Int = 30,
    private val maxToolCalls: Int = 100,
    private val maxRuntimeMs: Long = 5 * 60 * 1000L,
    private val maxToolOutputChars: Int = 40_000,
    private val approvalTimeoutMs: Long = 300_000L
) {
    private var pendingApproval: MutableSharedFlow<Boolean>? = null
    @Volatile private var cancelled = false

    fun cancel() {
        cancelled = true
        pendingApproval?.tryEmit(false)
    }

    fun allowAlways(toolName: String) {
        approvalPolicy?.alwaysAllow(toolName)
    }

    suspend fun submitApproval(approved: Boolean) {
        pendingApproval?.emit(approved)
    }

    /**
     * Executes the main autonomous agent loop, streaming events back to the UI.
     *
     * @param projectId persistent project identity used for chat memory / project memory (database id)
     * @param fsProjectName on-disk sandbox folder name for file and command tools (project name)
     */
    suspend fun processRequest(
        projectId: String,
        chatId: String,
        userRequest: String,
        fsProjectName: String = projectId,
        maxIterations: Int = maxSteps
    ): Flow<AgentEvent> = flow {
        val runId = UUID.randomUUID().toString()
        val startedAt = System.currentTimeMillis()
        val commandsRun = mutableListOf<String>()
        var filesChanged = 0

        emit(AgentEvent.StateChanged(AgentState.PLANNING))

        // Save user message
        messageDao.insertMessage(
            MessageEntity(UUID.randomUUID().toString(), chatId, LlmMessage.Role.USER.name, userRequest, System.currentTimeMillis())
        )

        // Pre-run checkpoint for rollback
        var checkpointId: String? = null
        if (checkpointManager != null) {
            checkpointManager.createCheckpoint(fsProjectName, projectId)?.let {
                checkpointId = it.id
                emit(AgentEvent.CheckpointCreated(it.id))
            }
        }

        var iteration = 0
        var toolCallCount = 0
        var loopActive = true

        while (loopActive && iteration < maxIterations) {
            iteration++

            if (cancelled) {
                emit(AgentEvent.StateChanged(AgentState.CANCELLED))
                recordRun(runId, projectId, chatId, userRequest, startedAt, "CANCELLED", filesChanged, commandsRun)
                emit(AgentEvent.Completed)
                return@flow
            }
            if (System.currentTimeMillis() - startedAt > maxRuntimeMs) {
                emit(AgentEvent.Error("Max runtime exceeded. Agent stopped."))
                loopActive = false
                break
            }

            // Build Context (System instructions)
            emit(AgentEvent.TextDelta("\n[System: Inspecting project state...]"))
            val systemContext = contextEngine.buildProjectContext(fsProjectName)

            // Get History
            val historyEntities = messageDao.getMessagesForChat(chatId)
            val llmMessages = historyEntities.map { entity ->
                LlmMessage(
                    role = LlmMessage.Role.valueOf(entity.role),
                    content = entity.content
                )
            }.toMutableList()

            // Prepare Request (filter tools by the active mode)
            val request = LlmRequest(
                systemInstructions = systemContext,
                messages = llmMessages,
                tools = toolRegistry.getToolSchemas().filter { schema ->
                    mode.allows(toolRegistry.getTool(schema.name)?.permission ?: ToolPermission.READ)
                },
                temperature = temperature
            )

            // Call LLM and Stream response
            var fullAssistantResponse = StringBuilder()
            var toolCallsRequested: List<LlmToolCall>? = null

            try {
                llmProvider.stream(request).collect { event ->
                    when (event) {
                        is LlmEvent.TextDelta -> {
                            fullAssistantResponse.append(event.text)
                            emit(AgentEvent.TextDelta(event.text))
                        }
                        is LlmEvent.ToolCall -> {
                            toolCallsRequested = event.toolCalls
                        }
                        is LlmEvent.Error -> {
                            emit(AgentEvent.Error(event.message))
                            loopActive = false
                        }
                        is LlmEvent.Completed -> {
                            // Stream finished for this round
                        }
                    }
                }

                // Save assistant message if it said anything
                if (fullAssistantResponse.isNotEmpty()) {
                    messageDao.insertMessage(
                        MessageEntity(
                            UUID.randomUUID().toString(), chatId, LlmMessage.Role.MODEL.name,
                            fullAssistantResponse.toString(), System.currentTimeMillis()
                        )
                    )
                }

                // If LLM requested tools, execute them and continue the loop
                if (!toolCallsRequested.isNullOrEmpty()) {
                    emit(AgentEvent.StateChanged(AgentState.EXECUTING_TOOL))

                    val toolResultsContent = StringBuilder()

                    for (call in toolCallsRequested!!) {
                        if (cancelled) break
                        if (toolCallCount >= maxToolCalls) {
                            emit(AgentEvent.Error("Max tool calls ($maxToolCalls) reached. Agent stopped."))
                            loopActive = false
                            break
                        }
                        toolCallCount++

                        emit(AgentEvent.ToolStarted(call.name, call.arguments.toString()))

                        val tool = toolRegistry.getTool(call.name)
                        if (tool != null) {
                            val executionKey = if (tool is ProjectMemoryTool) projectId else fsProjectName

                            // Mode gate: disallow tools outside the active mode
                            if (!mode.allows(tool.permission)) {
                                val deniedOut = "Tool ${call.name} is not allowed in $mode mode."
                                emit(AgentEvent.ToolCompleted(call.name, deniedOut))
                                toolResultsContent.append(deniedOut).append("\n---\n")
                                continue
                            }

                            // Approval gate
                            val policy = approvalPolicy
                            if (policy != null && policy.requiresApproval(call.name, tool.permission)) {
                                emit(AgentEvent.StateChanged(AgentState.WAITING_FOR_APPROVAL))
                                emit(AgentEvent.ApprovalPending(call.name, call.arguments.toString()))
                                val flow = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
                                pendingApproval = flow
                                val approved = withTimeoutOrNull(approvalTimeoutMs) { flow.first() } ?: false
                                pendingApproval = null
                                if (!approved) {
                                    val deniedOut = "Tool ${call.name} was denied by the user."
                                    emit(AgentEvent.ToolCompleted(call.name, deniedOut))
                                    toolResultsContent.append(deniedOut).append("\n---\n")
                                    continue
                                }
                            }

                            val result = tool.execute(executionKey, call.arguments)
                            if (tool.permission == ToolPermission.WRITE || tool.permission == ToolPermission.DESTRUCTIVE) {
                                filesChanged++
                            }
                            if (tool.permission == ToolPermission.EXECUTE) {
                                commandsRun.add(call.arguments.optString("command"))
                            }
                            val truncated = result.output.take(maxToolOutputChars) +
                                if (result.output.length > maxToolOutputChars) "\n[Output truncated]" else ""
                            val status = if (result.isSuccess) "SUCCESS" else "FAILED"
                            val out = "Tool ${call.name} result: $status\n$truncated"
                            emit(AgentEvent.ToolCompleted(call.name, out))
                            toolResultsContent.append(out).append("\n---\n")
                        } else {
                            val out = "Tool ${call.name} not found."
                            emit(AgentEvent.ToolCompleted(call.name, out))
                            toolResultsContent.append(out).append("\n---\n")
                        }
                    }

                    if (cancelled) {
                        emit(AgentEvent.StateChanged(AgentState.CANCELLED))
                        loopActive = false
                    } else {
                        // Save Tool results to history as a SYSTEM/TOOL message so the LLM sees it on the next loop
                        messageDao.insertMessage(
                            MessageEntity(
                                UUID.randomUUID().toString(), chatId, LlmMessage.Role.TOOL.name,
                                toolResultsContent.toString(), System.currentTimeMillis()
                            )
                        )
                    }
                    // Loop continues so LLM can react to tool output
                } else {
                    // No tools requested, the LLM is done answering
                    loopActive = false
                }

            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(AgentEvent.Error("LLM Exception: ${e.message}"))
                loopActive = false
            }
        }

        if (!cancelled) {
            if (iteration >= maxIterations) {
                emit(AgentEvent.Error("Max steps ($maxIterations) reached. Agent paused to prevent an infinite loop."))
            }
            if (checkpointManager != null) {
                val diff = checkpointManager.diffSummary(fsProjectName)
                emit(AgentEvent.DiffAvailable(diff))
            }
            emit(AgentEvent.StateChanged(AgentState.COMPLETED))
            recordRun(runId, projectId, chatId, userRequest, startedAt, "COMPLETED", filesChanged, commandsRun)
        }
        emit(AgentEvent.Completed)
    }

    private suspend fun recordRun(
        runId: String,
        projectId: String,
        chatId: String,
        request: String,
        startedAt: Long,
        result: String,
        filesChanged: Int,
        commands: List<String>
    ) {
        runDao?.insertRun(
            AgentRunEntity(
                id = runId,
                projectId = projectId,
                chatId = chatId,
                request = request,
                startedAt = startedAt,
                durationMs = System.currentTimeMillis() - startedAt,
                result = result,
                filesChanged = filesChanged,
                commands = commands.joinToString("\n")
            )
        )
    }
}