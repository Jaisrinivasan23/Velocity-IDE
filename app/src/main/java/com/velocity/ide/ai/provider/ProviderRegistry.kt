package com.velocity.ide.ai.provider

import com.velocity.ide.ai.network.AnthropicApiService
import com.velocity.ide.ai.network.GeminiApiService
import com.velocity.ide.ai.network.OpenAICompatApiService
import com.velocity.ide.ai.settings.AiSettingsManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builds the LLM provider selected in Settings. Every provider maps to a real
 * API adapter; custom and local entries use an OpenAI-compatible endpoint.
 */
class ProviderRegistry(
    private val settingsManager: AiSettingsManager
) {
    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getActiveProvider(): LlmProvider {
        return when (settingsManager.selectedProvider) {
            "gemini" -> GeminiProvider(
                apiKey = settingsManager.geminiApiKey,
                apiService = retrofit("https://generativelanguage.googleapis.com/").create(GeminiApiService::class.java),
                selectedModel = "gemini-1.5-pro",
                temperature = settingsManager.temperature
            )
            "anthropic" -> AnthropicProvider(
                apiKey = settingsManager.anthropicApiKey,
                apiService = retrofit("https://api.anthropic.com/").create(AnthropicApiService::class.java),
                selectedModel = settingsManager.anthropicModel
            )
            "openai" -> compat("openai", settingsManager.openaiApiKey, "https://api.openai.com/v1/", settingsManager.openaiModel)
            "openrouter" -> compat("openrouter", settingsManager.openRouterApiKey, "https://openrouter.ai/api/v1/", settingsManager.openRouterModel)
            "opencodezen" -> compat("opencodezen", settingsManager.zenApiKey, "https://zen.opencode.ai/v1/", settingsManager.zenModel)
            "nvidia" -> compat("nvidia", settingsManager.nvidiaApiKey, "https://integrate.api.nvidia.com/v1/", settingsManager.nvidiaModel)
            "custom" -> compat(
                settingsManager.customProviderName.ifBlank { "custom" },
                settingsManager.customApiKey,
                settingsManager.customBaseUrl,
                settingsManager.customModel
            )
            "local" -> compat(
                "local",
                settingsManager.localApiKey,
                settingsManager.localBaseUrl,
                settingsManager.localModel
            )
            else -> GeminiProvider(
                apiKey = settingsManager.geminiApiKey,
                apiService = retrofit("https://generativelanguage.googleapis.com/").create(GeminiApiService::class.java),
                selectedModel = "gemini-1.5-pro",
                temperature = settingsManager.temperature
            )
        }
    }

    private fun compat(id: String, apiKey: String, baseUrl: String, model: String): LlmProvider {
        // Retrofit requires a trailing slash on the base URL.
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return OpenAICompatProvider(
            id = id,
            apiKey = apiKey,
            apiService = retrofit(normalized).create(OpenAICompatApiService::class.java),
            selectedModel = model.ifBlank { "gpt-4o-mini" }
        )
    }
}