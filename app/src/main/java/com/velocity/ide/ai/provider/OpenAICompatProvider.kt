package com.velocity.ide.ai.provider

import com.google.gson.Gson
import com.velocity.ide.ai.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

/**
 * OpenAI-compatible streaming provider. Serves OpenAI, OpenRouter, OpenCode Zen,
 * NVIDIA Build, custom OpenAI-compatible endpoints, and local LLM servers.
 */
class OpenAICompatProvider(
    override val id: String,
    private val apiKey: String,
    private val apiService: OpenAICompatApiService,
    private val selectedModel: String
) : LlmProvider {

    override val name: String = "$id ($selectedModel)"

    private val gson = Gson()

    override suspend fun complete(request: LlmRequest): LlmResponse {
        throw NotImplementedError("complete() is not implemented. Use stream().")
    }

    override suspend fun stream(request: LlmRequest): Flow<LlmEvent> = flow {
        val compatRequest = mapToCompatRequest(request)

        try {
            val responseBody = if (apiKey.isBlank()) {
                apiService.streamChatCompletions("", compatRequest)
            } else {
                apiService.streamChatCompletions("Bearer $apiKey", compatRequest)
            }

            responseBody.byteStream().bufferedReader().useLines { lines ->
                var currentToolCallId: String? = null
                var currentToolName: String? = null
                val currentToolArgs = StringBuilder()

                for (line in lines) {
                    if (line.startsWith("data: ")) {
                        val jsonStr = line.removePrefix("data: ").trim()
                        if (jsonStr == "[DONE]") {
                            if (currentToolCallId != null && currentToolName != null) {
                                val argsJson = try { JSONObject(currentToolArgs.toString()) } catch (e: Exception) { JSONObject() }
                                emit(LlmEvent.ToolCall(listOf(LlmToolCall(currentToolCallId, currentToolName, argsJson))))
                            }
                            break
                        }

                        try {
                            val root = JSONObject(jsonStr)
                            val choices = root.optJSONArray("choices")
                            if (choices != null && choices.length() > 0) {
                                val delta = choices.getJSONObject(0).optJSONObject("delta")
                                if (delta != null) {
                                    val text = delta.optString("content", "")
                                    if (text.isNotEmpty()) {
                                        emit(LlmEvent.TextDelta(text))
                                    }

                                    val toolCalls = delta.optJSONArray("tool_calls")
                                    if (toolCalls != null && toolCalls.length() > 0) {
                                        val tc = toolCalls.getJSONObject(0)
                                        val tcId = tc.optString("id", "")
                                        if (tcId.isNotEmpty()) {
                                            if (currentToolCallId != null && currentToolName != null) {
                                                val argsJson = try { JSONObject(currentToolArgs.toString()) } catch (e: Exception) { JSONObject() }
                                                emit(LlmEvent.ToolCall(listOf(LlmToolCall(currentToolCallId, currentToolName, argsJson))))
                                            }
                                            currentToolCallId = tcId
                                            currentToolName = tc.optJSONObject("function")?.optString("name", "")
                                            currentToolArgs.clear()
                                        }

                                        val argsChunk = tc.optJSONObject("function")?.optString("arguments", "")
                                        if (!argsChunk.isNullOrEmpty()) {
                                            currentToolArgs.append(argsChunk)
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // Ignore malformed chunks
                        }
                    }
                }
            }
            emit(LlmEvent.Completed)
        } catch (e: Exception) {
            emit(LlmEvent.Error(e.message ?: "Network error communicating with $id."))
        }
    }.flowOn(Dispatchers.IO)

    private fun mapToCompatRequest(request: LlmRequest): CompatChatRequest {
        val messages = mutableListOf<CompatMessage>()

        request.systemInstructions?.let {
            messages.add(CompatMessage(role = "system", content = it))
        }

        messages.addAll(request.messages.map { msg ->
            when (msg.role) {
                LlmMessage.Role.USER -> CompatMessage(role = "user", content = msg.content)
                LlmMessage.Role.MODEL -> CompatMessage(role = "assistant", content = msg.content)
                LlmMessage.Role.SYSTEM -> CompatMessage(role = "system", content = msg.content)
                LlmMessage.Role.TOOL -> CompatMessage(
                    role = "tool",
                    content = msg.content,
                    tool_call_id = msg.toolCallId ?: "unknown"
                )
            }
        })

        val tools = request.tools?.map { schema ->
            CompatTool(
                function = CompatFunction(
                    name = schema.name,
                    description = schema.description,
                    parameters = gson.fromJson(schema.parametersSchema.toString(), Map::class.java)
                )
            )
        }

        return CompatChatRequest(
            model = selectedModel,
            messages = messages,
            tools = tools,
            stream = true,
            temperature = request.temperature
        )
    }
}