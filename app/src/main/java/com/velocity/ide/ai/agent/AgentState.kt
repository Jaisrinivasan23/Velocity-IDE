package com.velocity.ide.ai.agent

enum class AgentState {
    IDLE,
    PLANNING,
    INSPECTING,
    EXECUTING_TOOL,
    WAITING_FOR_TOOL,
    WAITING_FOR_APPROVAL,
    COMPLETED,
    FAILED,
    CANCELLED
}
