package com.velocity.ide.ai.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

// --- Gemini DTOs ---

data class GeminiRequest(
    val systemInstruction: GeminiContent? = null,
    val contents: List<GeminiContent>,
    val tools: List<GeminiTool>? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    val role: String, // "user" or "model"
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String? = null,
    val functionCall: GeminiFunctionCall? = null,
    val functionResponse: GeminiFunctionResponse? = null
)

data class GeminiFunctionCall(
    val name: String,
    val args: Map<String, Any>?
)

data class GeminiFunctionResponse(
    val name: String,
    val response: Map<String, Any>
)

data class GeminiTool(
    val functionDeclarations: List<GeminiFunctionDeclaration>
)

data class GeminiFunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: Any // Expected to be a Map representing the JSON schema
)

data class GeminiGenerationConfig(
    val temperature: Double? = null,
    val topP: Double? = null,
    val maxOutputTokens: Int? = null
)

// --- Retrofit Interface ---

interface GeminiApiService {
    
    @Streaming
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/{model}:streamGenerateContent")
    suspend fun streamGenerateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Query("alt") alt: String = "sse",
        @Body request: GeminiRequest
    ): ResponseBody
}
