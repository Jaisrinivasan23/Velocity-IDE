package com.velocity.ide.ai.agent

import com.velocity.ide.ai.settings.AiSettingsManager

/**
 * Decides whether a tool call must wait for explicit user approval.
 * Strict by default: destructive and execute actions always ask unless
 * the user has explicitly disabled the corresponding setting; "Always allow"
 * overrides are honored per tool for the duration of a run.
 */
class ApprovalPolicy(
    private val settingsManager: AiSettingsManager
) {
    private val alwaysAllowed = mutableSetOf<String>()

    fun alwaysAllow(toolName: String) {
        alwaysAllowed.add(toolName)
    }

    fun requiresApproval(toolName: String, permission: ToolPermission): Boolean {
        if (toolName in alwaysAllowed) return false
        return when (permission) {
            ToolPermission.DESTRUCTIVE -> settingsManager.askBeforeDeleting
            ToolPermission.EXECUTE -> settingsManager.askBeforeRunning
            ToolPermission.WRITE -> settingsManager.askBeforeEditing
            else -> false // READ / MEMORY never block
        }
    }
}