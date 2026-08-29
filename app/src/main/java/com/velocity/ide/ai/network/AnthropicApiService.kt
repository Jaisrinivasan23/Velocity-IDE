package com.velocity.ide.ai.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

data class AnthropicRequest(
    val model: String,
    val max_tokens: Int,
    val system: String? = null,
    val messages: List<AnthropicMessage>,
    val tools: List<AnthropicTool>? = null,
    val stream: Boolean = true,
    val temperature: Double? = null
)

data class AnthropicMessage(
    val role: String, // user / assistant
    val content: List<AnthropicContentBlock>
)

sealed class AnthropicContentBlock {
    data class Text(val text: String) : AnthropicContentBlock()
    data class ToolUse(val id: String, val name: String, val input: Map<String, Any>) : AnthropicContentBlock()
    data class ToolResult(val toolUseId: String, val content: String) : AnthropicContentBlock()
}

data class AnthropicTool(
    val name: String,
    val description: String,
    val input_schema: Any
)

/**
 * Native Anthropic Messages API (SSE). Uses the tool_use / tool_result block
 * protocol and maps onto the shared LlmEvent stream.
 */
interface AnthropicApiService {

    @Streaming
    @POST("v1/messages")
    suspend fun streamMessages(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String,
        @Header("Content-Type") contentType: String,
        @Body request: AnthropicRequest
    ): ResponseBody
}