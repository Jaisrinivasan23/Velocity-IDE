package com.velocity.ide.ai.provider

import com.google.gson.Gson
import com.velocity.ide.ai.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

class OpenRouterProvider(
    private val apiKey: String,
    private val apiService: OpenRouterApiService,
    private val selectedModel: String = "anthropic/claude-3-haiku"
) : LlmProvider {

    override val id: String = "openrouter"
    override val name: String = "OpenRouter ($selectedModel)"
    
    private val gson = Gson()

    override suspend fun complete(request: LlmRequest): LlmResponse {
        throw NotImplementedError("complete() is not implemented. Use stream().")
    }

    override suspend fun stream(request: LlmRequest): Flow<LlmEvent> = flow {
        val openRouterRequest = mapToOpenRouterRequest(request)
        
        try {
            val responseBody = apiService.streamChatCompletions(
                authorization = "Bearer $apiKey",
                request = openRouterRequest
            )

            responseBody.byteStream().bufferedReader().useLines { lines ->
                var currentToolCallId: String? = null
                var currentToolName: String? = null
                val currentToolArgs = StringBuilder()

                for (line in lines) {
                    if (line.startsWith("data: ")) {
                        val jsonStr = line.removePrefix("data: ").trim()
                        if (jsonStr == "[DONE]") {
                            // If we were accumulating tool arguments, emit them before finishing
                            if (currentToolCallId != null && currentToolName != null) {
                                val argsJson = try { JSONObject(currentToolArgs.toString()) } catch (e: Exception) { JSONObject() }
                                val toolCall = LlmToolCall(currentToolCallId, currentToolName, argsJson)
                                emit(LlmEvent.ToolCall(listOf(toolCall)))
                            }
                            break
                        }
                        
                        try {
                            val root = JSONObject(jsonStr)
                            val choices = root.optJSONArray("choices")
                            if (choices != null && choices.length() > 0) {
                                val delta = choices.getJSONObject(0).optJSONObject("delta")
                                if (delta != null) {
                                    // Text delta
                                    val text = delta.optString("content", "")
                                    if (text.isNotEmpty()) {
                                        emit(LlmEvent.TextDelta(text))
                                    }
                                    
                                    // Tool call delta (OpenAI streams tool calls in chunks)
                                    val toolCalls = delta.optJSONArray("tool_calls")
                                    if (toolCalls != null && toolCalls.length() > 0) {
                                        val tc = toolCalls.getJSONObject(0)
                                        val tcId = tc.optString("id", "")
                                        if (tcId.isNotEmpty()) {
                                            // New tool call starting
                                            if (currentToolCallId != null && currentToolName != null) {
                                                // Emit previous tool call if one was accumulating
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
                            // Ignore parsing errors for individual chunks
                        }
                    }
                }
            }
            emit(LlmEvent.Completed)
        } catch (e: Exception) {
            emit(LlmEvent.Error(e.message ?: "Network error communicating with OpenRouter."))
        }
    }.flowOn(Dispatchers.IO)

    private fun mapToOpenRouterRequest(request: LlmRequest): OpenRouterRequest {
        val messages = mutableListOf<OpenRouterMessage>()
        
        request.systemInstructions?.let {
            messages.add(OpenRouterMessage(role = "system", content = it))
        }

        messages.addAll(request.messages.map { msg ->
            when (msg.role) {
                LlmMessage.Role.USER -> OpenRouterMessage(role = "user", content = msg.content)
                LlmMessage.Role.MODEL -> OpenRouterMessage(role = "assistant", content = msg.content)
                LlmMessage.Role.SYSTEM -> OpenRouterMessage(role = "system", content = msg.content)
                LlmMessage.Role.TOOL -> OpenRouterMessage(
                    role = "tool", 
                    content = msg.content, 
                    tool_call_id = msg.toolCallId ?: "unknown"
                )
            }
        })

        val tools = request.tools?.map { schema ->
            OpenRouterTool(
                function = OpenRouterFunction(
                    name = schema.name,
                    description = schema.description,
                    parameters = gson.fromJson(schema.parametersSchema.toString(), Map::class.java)
                )
            )
        }

        return OpenRouterRequest(
            model = selectedModel,
            messages = messages,
            tools = tools,
            stream = true
        )
    }
}
