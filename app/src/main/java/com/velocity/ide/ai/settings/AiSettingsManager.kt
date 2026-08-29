package com.velocity.ide.ai.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AiSettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "velocity_secure_ai_settings",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    var selectedProvider: String
        get() = sharedPreferences.getString("selected_provider", "gemini") ?: "gemini"
        set(value) = sharedPreferences.edit().putString("selected_provider", value).apply()

    var geminiApiKey: String
        get() = sharedPreferences.getString("api_key_gemini", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_gemini", value).apply()

    var openRouterApiKey: String
        get() = sharedPreferences.getString("api_key_openrouter", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_openrouter", value).apply()

    var openRouterModel: String
        get() = sharedPreferences.getString("model_openrouter", "anthropic/claude-3-haiku") ?: "anthropic/claude-3-haiku"
        set(value) = sharedPreferences.edit().putString("model_openrouter", value).apply()

    // OpenAI
    var openaiApiKey: String
        get() = sharedPreferences.getString("api_key_openai", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_openai", value).apply()

    var openaiModel: String
        get() = sharedPreferences.getString("model_openai", "gpt-4o-mini") ?: "gpt-4o-mini"
        set(value) = sharedPreferences.edit().putString("model_openai", value).apply()

    // Anthropic
    var anthropicApiKey: String
        get() = sharedPreferences.getString("api_key_anthropic", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_anthropic", value).apply()

    var anthropicModel: String
        get() = sharedPreferences.getString("model_anthropic", "claude-3-5-sonnet-latest") ?: "claude-3-5-sonnet-latest"
        set(value) = sharedPreferences.edit().putString("model_anthropic", value).apply()

    // OpenCode Zen
    var zenApiKey: String
        get() = sharedPreferences.getString("api_key_zen", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_zen", value).apply()

    var zenModel: String
        get() = sharedPreferences.getString("model_zen", "opencodezen/sonnet") ?: "opencodezen/sonnet"
        set(value) = sharedPreferences.edit().putString("model_zen", value).apply()

    // NVIDIA Build / NIM
    var nvidiaApiKey: String
        get() = sharedPreferences.getString("api_key_nvidia", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_nvidia", value).apply()

    var nvidiaModel: String
        get() = sharedPreferences.getString("model_nvidia", "nvidia/nemotron-nano-8b-v1") ?: "nvidia/nemotron-nano-8b-v1"
        set(value) = sharedPreferences.edit().putString("model_nvidia", value).apply()

    // Custom provider
    var customProviderName: String
        get() = sharedPreferences.getString("custom_name", "Custom") ?: "Custom"
        set(value) = sharedPreferences.edit().putString("custom_name", value).apply()

    var customBaseUrl: String
        get() = sharedPreferences.getString("custom_base_url", "") ?: ""
        set(value) = sharedPreferences.edit().putString("custom_base_url", value).apply()

    var customApiKey: String
        get() = sharedPreferences.getString("api_key_custom", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_custom", value).apply()

    var customModel: String
        get() = sharedPreferences.getString("model_custom", "") ?: ""
        set(value) = sharedPreferences.edit().putString("model_custom", value).apply()

    // Local LLM
    var localBaseUrl: String
        get() = sharedPreferences.getString("local_base_url", "http://10.0.2.2:11434/v1") ?: "http://10.0.2.2:11434/v1"
        set(value) = sharedPreferences.edit().putString("local_base_url", value).apply()

    var localApiKey: String
        get() = sharedPreferences.getString("api_key_local", "") ?: ""
        set(value) = sharedPreferences.edit().putString("api_key_local", value).apply()

    var localModel: String
        get() = sharedPreferences.getString("model_local", "qwen2.5-coder:14b") ?: "qwen2.5-coder:14b"
        set(value) = sharedPreferences.edit().putString("model_local", value).apply()

    // Agent behavior
    var agentMode: String
        get() = sharedPreferences.getString("agent_mode", "AGENT") ?: "AGENT"
        set(value) = sharedPreferences.edit().putString("agent_mode", value).apply()

    var temperature: Double
        get() = sharedPreferences.getFloat("temperature", 0.2f).toDouble()
        set(value) = sharedPreferences.edit().putFloat("temperature", value.toFloat()).apply()

    var maxSteps: Int
        get() = sharedPreferences.getInt("max_steps", 30)
        set(value) = sharedPreferences.edit().putInt("max_steps", value).apply()

    // Appearance
    var accentColorName: String
        get() = sharedPreferences.getString("accent_color", "orange") ?: "orange"
        set(value) = sharedPreferences.edit().putString("accent_color", value).apply()

    // Editor & Terminal
    var editorFontSize: Int
        get() = sharedPreferences.getInt("editor_font_size", 14)
        set(value) = sharedPreferences.edit().putInt("editor_font_size", value).apply()

    var editorLineNumbers: Boolean
        get() = sharedPreferences.getBoolean("editor_line_numbers", true)
        set(value) = sharedPreferences.edit().putBoolean("editor_line_numbers", value).apply()

    var terminalFontSize: Int
        get() = sharedPreferences.getInt("terminal_font_size", 12)
        set(value) = sharedPreferences.edit().putInt("terminal_font_size", value).apply()

    var keepScreenOn: Boolean
        get() = sharedPreferences.getBoolean("keep_screen_on", false)
        set(value) = sharedPreferences.edit().putBoolean("keep_screen_on", value).apply()

    fun getApiKey(provider: String): String? {
        return sharedPreferences.getString("api_key_$provider", null)
    }

    fun setApiKey(provider: String, key: String) {
        sharedPreferences.edit().putString("api_key_$provider", key).apply()
    }

    // Agent Permissions
    var askBeforeEditing: Boolean
        get() = sharedPreferences.getBoolean("perm_ask_edit", true)
        set(value) = sharedPreferences.edit().putBoolean("perm_ask_edit", value).apply()

    var askBeforeRunning: Boolean
        get() = sharedPreferences.getBoolean("perm_ask_run", true)
        set(value) = sharedPreferences.edit().putBoolean("perm_ask_run", value).apply()

    var askBeforeDeleting: Boolean
        get() = sharedPreferences.getBoolean("perm_ask_delete", true)
        set(value) = sharedPreferences.edit().putBoolean("perm_ask_delete", value).apply()

    // Git Settings
    var gitAuthorName: String
        get() = sharedPreferences.getString("git_name", "") ?: ""
        set(value) = sharedPreferences.edit().putString("git_name", value).apply()

    var gitAuthorEmail: String
        get() = sharedPreferences.getString("git_email", "") ?: ""
        set(value) = sharedPreferences.edit().putString("git_email", value).apply()

    var githubPat: String
        get() = sharedPreferences.getString("github_pat", "") ?: ""
        set(value) = sharedPreferences.edit().putString("github_pat", value).apply()

    // Deployment Platform Tokens
    var vercelToken: String
        get() = sharedPreferences.getString("vercel_token", "") ?: ""
        set(value) = sharedPreferences.edit().putString("vercel_token", value).apply()

    var netlifyToken: String
        get() = sharedPreferences.getString("netlify_token", "") ?: ""
        set(value) = sharedPreferences.edit().putString("netlify_token", value).apply()
}
