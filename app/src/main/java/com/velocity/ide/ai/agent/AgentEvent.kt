package com.velocity.ide.ai.agent

sealed class AgentEvent {
    data class TextDelta(val text: String) : AgentEvent()
    data class StateChanged(val newState: AgentState) : AgentEvent()
    data class ToolStarted(val toolName: String, val arguments: String) : AgentEvent()
    data class ToolCompleted(val toolName: String, val result: String) : AgentEvent()
    data class ApprovalPending(val toolName: String, val arguments: String) : AgentEvent()
    data class CheckpointCreated(val checkpointId: String) : AgentEvent()
    data class DiffAvailable(val changedFiles: List<String>) : AgentEvent()
    data class RunRecorded(val filesChanged: Int, val appsRan: List<String>) : AgentEvent()
    data class Error(val message: String) : AgentEvent()
    object Completed : AgentEvent()
}