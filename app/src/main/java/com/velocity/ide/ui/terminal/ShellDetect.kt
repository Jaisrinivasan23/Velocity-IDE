package com.velocity.ide.ui.terminal

/**
 * Heuristic to decide whether terminal input is a shell command or a natural
 * language request that should be routed to the AI agent instead.
 */
object ShellDetect {

    private val shellPrefixes = listOf(
        "cd ", "npm ", "npx ", "git ", "ls ", "cat ", "echo ", "mkdir ", "rm ",
        "touch ", "curl ", "node ", "python", "gradle", "pnpm ", "yarn ", "cp ",
        "mv ", "chmod ", "export ", "clear", "pwd", "./", "sh ", "bash"
    )

    fun isLikelyCommand(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return true
        if (!trimmed.contains(' ')) return true // single token: treat as command
        return shellPrefixes.any { trimmed.startsWith(it) } ||
            trimmed.contains("&&") || trimmed.contains("|") ||
            trimmed.contains("$(") || trimmed.contains(" --")
    }
}