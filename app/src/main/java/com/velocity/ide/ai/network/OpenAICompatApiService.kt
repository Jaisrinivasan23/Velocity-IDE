package com.velocity.ide.ai.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

data class CompatChatRequest(
    val model: String,
    val messages: List<CompatMessage>,
    val stream: Boolean = true,
    val tools: List<CompatTool>? = null,
    val tool_choice: String? = "auto",
    val temperature: Double? = null
)

data class CompatMessage(
    val role: String,
    val content: String?,
    val name: String? = null,
    val tool_calls: List<CompatToolCall>? = null,
    val tool_call_id: String? = null
)

data class CompatTool(
    val type: String = "function",
    val function: CompatFunction
)

data class CompatFunction(
    val name: String,
    val description: String,
    val parameters: Any
)

data class CompatToolCall(
    val id: String,
    val type: String = "function",
    val function: CompatToolCallFunction
)

data class CompatToolCallFunction(
    val name: String,
    val arguments: String
)

/**
 * OpenAI-compatible streaming chat completions, used by OpenAI, OpenRouter,
 * OpenCode Zen, NVIDIA Build, custom endpoints, and local OpenAI-compatible
 * servers (Ollama/llama.cpp). Base URL is supplied by the caller.
 */
interface OpenAICompatApiService {

    @Streaming
    @POST("chat/completions")
    suspend fun streamChatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: CompatChatRequest
    ): ResponseBody
}