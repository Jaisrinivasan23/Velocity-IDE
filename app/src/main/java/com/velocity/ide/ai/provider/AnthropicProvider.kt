package com.velocity.ide.ai.provider

import com.google.gson.Gson
import com.velocity.ide.ai.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

/**
 * Native Anthropic (Claude) provider using the Messages API with SSE and
 * tool_use / tool_result content blocks.
 */
class AnthropicProvider(
    private val apiKey: String,
    private val apiService: AnthropicApiService,
    private val selectedModel: String = "claude-3-5-sonnet-latest"
) : LlmProvider {

    override val id: String = "anthropic"
    override val name: String = "Anthropic Claude ($selectedModel)"

    private val gson = Gson()

    override suspend fun complete(request: LlmRequest): LlmResponse {
        throw NotImplementedError("complete() is not implemented. Use stream().")
    }

    override suspend fun stream(request: LlmRequest): Flow<LlmEvent> = flow {
        val anthropicRequest = mapToAnthropicRequest(request)

        try {
            val responseBody = apiService.streamMessages(
                apiKey = apiKey,
                version = "2023-06-01",
                contentType = "application/json",
                request = anthropicRequest
            )

            responseBody.byteStream().bufferedReader().useLines { lines ->
                var toolUseId: String? = null
                var toolName: String? = null
                val toolArgs = StringBuilder()

                for (line in lines) {
                    if (line.startsWith("event:")) continue
                    if (!line.startsWith("data: ")) continue
                    val jsonStr = line.removePrefix("data: ").trim()
                    if (jsonStr.isEmpty()) continue

                    try {
                        val root = JSONObject(jsonStr)
                        when (root.optString("type")) {
                            "content_block_start" -> {
                                val block = root.optJSONObject("content_block")
                                if (block != null && block.optString("type") == "tool_use") {
                                    toolUseId = block.optString("id")
                                    toolName = block.optString("name")
                                    toolArgs.clear()
                                }
                            }
                            "content_block_delta" -> {
                                val delta = root.optJSONObject("delta")
                                if (delta != null) {
                                    when (delta.optString("type")) {
                                        "text_delta" -> {
                                            val text = delta.optString("text")
                                            if (text.isNotEmpty()) emit(LlmEvent.TextDelta(text))
                                        }
                                        "input_json_delta" -> {
                                            toolArgs.append(delta.optString("partial_json"))
                                        }
                                    }
                                }
                            }
                            "content_block_stop" -> {
                                if (toolUseId != null && toolName != null) {
                                    val argsJson = try {
                                        JSONObject(toolArgs.toString())
                                    } catch (e: Exception) {
                                        if (toolArgs.isEmpty()) JSONObject() else JSONObject().put("_raw", toolArgs.toString())
                                    }
                                    emit(LlmEvent.ToolCall(listOf(LlmToolCall(toolUseId, toolName, argsJson))))
                                    toolUseId = null
                                    toolName = null
                                    toolArgs.clear()
                                }
                            }
                            "message_stop" -> { /* done */ }
                            "error" -> {
                                emit(LlmEvent.Error(root.optJSONObject("error")?.optString("message") ?: "Anthropic error"))
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore malformed chunks
                    }
                }
            }
            emit(LlmEvent.Completed)
        } catch (e: Exception) {
            emit(LlmEvent.Error(e.message ?: "Network error communicating with Anthropic."))
        }
    }.flowOn(Dispatchers.IO)

    private fun mapToAnthropicRequest(request: LlmRequest): AnthropicRequest {
        val system = request.systemInstructions

        // Build alternating role runs from the message list (Anthropic requires
        // contiguous user/assistant turns and a tool_result inside a user turn).
        val reconstructed = mutableListOf<AnthropicMessage>()
        var prevRole: String? = null
        var buffer = mutableListOf<AnthropicContentBlock>()
        fun flushBuf(role: String) {
            if (buffer.isNotEmpty()) {
                reconstructed.add(AnthropicMessage(role, buffer))
                buffer = mutableListOf()
            }
        }

        request.messages.forEach { msg ->
            when (msg.role) {
                LlmMessage.Role.USER -> {
                    flushBuf(prevRole ?: "user")
                    prevRole = "user"
                    buffer.add(AnthropicContentBlock.Text(msg.content))
                }
                LlmMessage.Role.MODEL -> {
                    flushBuf(prevRole ?: "assistant")
                    prevRole = "assistant"
                    buffer.add(AnthropicContentBlock.Text(msg.content))
                }
                LlmMessage.Role.TOOL -> {
                    flushBuf(prevRole ?: "user")
                    prevRole = "user"
                    buffer.add(AnthropicContentBlock.ToolResult("unknown", msg.content))
                }
                LlmMessage.Role.SYSTEM -> { /* handled in system */ }
            }
        }
        flushBuf(prevRole ?: "user")

        val tools = request.tools?.map { schema ->
            AnthropicTool(
                name = schema.name,
                description = schema.description,
                input_schema = gson.fromJson(schema.parametersSchema.toString(), Map::class.java)
            )
        }

        return AnthropicRequest(
            model = selectedModel,
            max_tokens = 4096,
            system = system,
            messages = reconstructed,
            tools = tools,
            stream = true,
            temperature = request.temperature
        )
    }
}