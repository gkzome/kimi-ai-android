package com.kimi.ai.android.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Chat",
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// Kimi API Request/Response models
data class ChatCompletionRequest(
    val model: String = "moonshot-v1-8k",
    val messages: List<MessageDto>,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

data class MessageDto(
    val role: String,
    val content: String
)

data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val index: Int,
    val message: MessageDto,
    val finish_reason: String?
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

// Models available
sealed class KimiModel(val id: String, val displayName: String, val description: String) {
    object Moonshot8K : KimiModel("moonshot-v1-8k", "Moonshot v1 (8K)", "Fast responses, 8K context")
    object Moonshot32K : KimiModel("moonshot-v1-32k", "Moonshot v1 (32K)", "Long context, 32K tokens")
    object Moonshot128K : KimiModel("moonshot-v1-128k", "Moonshot v1 (128K)", "Extended context, 128K tokens")

    companion object {
        val allModels = listOf(Moonshot8K, Moonshot32K, Moonshot128K)
        fun fromId(id: String): KimiModel = allModels.find { it.id == id } ?: Moonshot8K
    }
}
