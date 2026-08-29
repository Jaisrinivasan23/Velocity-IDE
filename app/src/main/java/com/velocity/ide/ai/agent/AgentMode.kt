package com.velocity.ide.ai.agent

/** Interaction mode for the agent. */
enum class AgentMode {
    /** Explain only: READ and MEMORY tools. */
    ASK,

    /** Inspect and modify files; commands and deletions still require approval. */
    EDIT,

    /** Full autonomous workflow with all tools and the approval policy. */
    AGENT;

    fun allows(permission: ToolPermission): Boolean = when (this) {
        ASK -> permission == ToolPermission.READ || permission == ToolPermission.MEMORY
        EDIT -> permission == ToolPermission.READ || permission == ToolPermission.WRITE || permission == ToolPermission.MEMORY
        AGENT -> true
    }
}