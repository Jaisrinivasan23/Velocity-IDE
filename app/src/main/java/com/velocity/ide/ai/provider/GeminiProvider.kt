package com.velocity.ide.ai.provider

import com.google.gson.Gson
import com.velocity.ide.ai.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject

class GeminiProvider(
    private val apiKey: String,
    private val apiService: GeminiApiService,
    private val selectedModel: String = "gemini-1.5-pro",
    private val temperature: Double = 0.2
) : LlmProvider {

    override val id: String = "gemini"
    override val name: String = "Google Gemini ($selectedModel)"
    
    private val gson = Gson()

    override suspend fun complete(request: LlmRequest): LlmResponse {
        throw NotImplementedError("complete() is not implemented. Use stream().")
    }

    override suspend fun stream(request: LlmRequest): Flow<LlmEvent> = flow {
        val geminiRequest = mapToGeminiRequest(request)
        
        try {
            val responseBody = apiService.streamGenerateContent(
                model = selectedModel,
                apiKey = apiKey,
                request = geminiRequest
            )

            // Manual SSE parsing
            responseBody.byteStream().bufferedReader().useLines { lines ->
                var accumulatingFunctionCall: GeminiFunctionCall? = null
                var accumulatingFunctionArgs = StringBuilder()

                for (line in lines) {
                    if (line.startsWith("data: ")) {
                        val jsonStr = line.removePrefix("data: ").trim()
                        if (jsonStr == "[DONE]") break
                        
                        try {
                            // The actual structure depends on the specific Gemini SSE format.
                            // Assuming it returns standard Gemini chunk JSON.
                            val root = JSONObject(jsonStr)
                            val candidates = root.optJSONArray("candidates")
                            if (candidates != null && candidates.length() > 0) {
                                val content = candidates.getJSONObject(0).optJSONObject("content")
                                val parts = content?.optJSONArray("parts")
                                
                                if (parts != null && parts.length() > 0) {
                                    val part = parts.getJSONObject(0)
                                    
                                    val text = part.optString("text", "")
                                    if (text.isNotEmpty()) {
                                        emit(LlmEvent.TextDelta(text))
                                    }
                                    
                                    val functionCall = part.optJSONObject("functionCall")
                                    if (functionCall != null) {
                                        val name = functionCall.optString("name")
                                        val args = functionCall.optJSONObject("args")
                                        
                                        val toolCall = LlmToolCall(
                                            id = "call_${System.currentTimeMillis()}", // Gemini doesn't always provide tool IDs natively in the same way OpenAI does
                                            name = name,
                                            arguments = args ?: JSONObject()
                                        )
                                        emit(LlmEvent.ToolCall(listOf(toolCall)))
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
            emit(LlmEvent.Error(e.message ?: "Network error communicating with Gemini."))
        }
    }.flowOn(Dispatchers.IO)

    private fun mapToGeminiRequest(request: LlmRequest): GeminiRequest {
        val systemInstruction = request.systemInstructions?.let {
            GeminiContent("user", listOf(GeminiPart(text = it)))
        }

        val contents = request.messages.mapNotNull { msg ->
            when (msg.role) {
                LlmMessage.Role.USER -> GeminiContent("user", listOf(GeminiPart(text = msg.content)))
                LlmMessage.Role.MODEL -> GeminiContent("model", listOf(GeminiPart(text = msg.content)))
                LlmMessage.Role.SYSTEM -> null // Handled in systemInstruction
                LlmMessage.Role.TOOL -> {
                    // Convert TOOL response to GeminiFunctionResponse
                    val root = JSONObject(msg.content)
                    val map = mutableMapOf<String, Any>()
                    map["output"] = msg.content
                    GeminiContent("user", listOf(GeminiPart(functionResponse = GeminiFunctionResponse(
                        name = "unknown", // Should map proper name if possible
                        response = map
                    ))))
                }
            }
        }

        val tools = request.tools?.map { schema ->
            GeminiFunctionDeclaration(
                name = schema.name,
                description = schema.description,
                parameters = gson.fromJson(schema.parametersSchema.toString(), Map::class.java)
            )
        }?.let { listOf(GeminiTool(it)) }

        return GeminiRequest(
            systemInstruction = systemInstruction,
            contents = contents,
            tools = tools,
            generationConfig = GeminiGenerationConfig(
                temperature = request.temperature ?: temperature
            )
        )
    }
}
