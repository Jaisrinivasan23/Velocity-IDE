package com.velocity.ide.ai.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

// --- OpenRouter / OpenAI Compatible DTOs ---

data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val stream: Boolean = true,
    val tools: List<OpenRouterTool>? = null,
    val tool_choice: String? = "auto"
)

data class OpenRouterMessage(
    val role: String, // system, user, assistant, tool
    val content: String?,
    val name: String? = null,
    val tool_calls: List<OpenRouterToolCall>? = null,
    val tool_call_id: String? = null
)

data class OpenRouterTool(
    val type: String = "function",
    val function: OpenRouterFunction
)

data class OpenRouterFunction(
    val name: String,
    val description: String,
    val parameters: Any // Map representing JSON Schema
)

data class OpenRouterToolCall(
    val id: String,
    val type: String = "function",
    val function: OpenRouterToolCallFunction
)

data class OpenRouterToolCallFunction(
    val name: String,
    val arguments: String // Stringified JSON
)

// --- Retrofit Interface ---

interface OpenRouterApiService {

    @Streaming
    @Headers(
        "Content-Type: application/json",
        "HTTP-Referer: https://github.com/velocity-ide/android", // Required by OpenRouter
        "X-Title: Velocity IDE" // Optional but good practice for OpenRouter
    )
    @POST("api/v1/chat/completions")
    suspend fun streamChatCompletions(
        @Header("Authorization") authorization: String, // "Bearer $API_KEY"
        @Body request: OpenRouterRequest
    ): ResponseBody
}
