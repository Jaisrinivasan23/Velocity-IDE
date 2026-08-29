package com.velocity.ide.ai.provider

import kotlinx.coroutines.flow.Flow

interface LlmProvider {
    val id: String
    val name: String

    /**
     * Complete a request in a single blocking call.
     */
    suspend fun complete(request: LlmRequest): LlmResponse

    /**
     * Stream a request, emitting events as they arrive.
     */
    suspend fun stream(request: LlmRequest): Flow<LlmEvent>
}
