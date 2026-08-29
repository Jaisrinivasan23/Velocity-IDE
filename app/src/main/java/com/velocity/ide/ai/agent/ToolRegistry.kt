package com.velocity.ide.ai.agent

import com.velocity.ide.ai.provider.LlmToolSchema

class ToolRegistry {
    private val tools = mutableMapOf<String, AgentTool>()

    fun register(tool: AgentTool) {
        tools[tool.name] = tool
    }

    fun unregister(toolName: String) {
        tools.remove(toolName)
    }

    fun getTool(name: String): AgentTool? {
        return tools[name]
    }

    fun getAvailableTools(): List<AgentTool> {
        return tools.values.toList()
    }

    /**
     * Extracts the machine-readable schemas for all registered tools
     * to inject into the LLM context.
     */
    fun getToolSchemas(): List<LlmToolSchema> {
        return tools.values.map { tool ->
            LlmToolSchema(
                name = tool.name,
                description = tool.description,
                parametersSchema = tool.inputSchema
            )
        }
    }
}
