package com.velocity.ide.ai.provider

import org.json.JSONObject

/**
 * Standardized request sent to the LLM Provider.
 */
data class LlmRequest(
    val systemInstructions: String?,
    val messages: List<LlmMessage>,
    val tools: List<LlmToolSchema>? = null,
    val model: String? = null,
    val temperature: Double? = null
)

/**
 * A message within the conversation history.
 */
data class LlmMessage(
    val role: Role,
    val content: String,
    val toolCalls: List<LlmToolCall>? = null,
    val toolCallId: String? = null // Used if this message is a response to a tool call
) {
    enum class Role {
        USER, MODEL, SYSTEM, TOOL
    }
}

/**
 * A tool call requested by the LLM.
 */
data class LlmToolCall(
    val id: String,
    val name: String,
    val arguments: JSONObject
)

/**
 * A machine-readable JSON schema defining an available tool.
 * We use JSONObject to hold the schema definition to remain flexible.
 */
data class LlmToolSchema(
    val name: String,
    val description: String,
    val parametersSchema: JSONObject
)

/**
 * Standardized complete response from the LLM Provider.
 */
data class LlmResponse(
    val content: String?,
    val toolCalls: List<LlmToolCall>? = null,
    val error: String? = null
)

/**
 * Standardized streaming events from the LLM Provider.
 */
sealed class LlmEvent {
    data class TextDelta(val text: String) : LlmEvent()
    data class ToolCall(val toolCalls: List<LlmToolCall>) : LlmEvent()
    data class Error(val message: String) : LlmEvent()
    object Completed : LlmEvent()
}
