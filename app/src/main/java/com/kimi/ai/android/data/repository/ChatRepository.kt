package com.kimi.ai.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kimi.ai.android.data.api.KimiApiService
import com.kimi.ai.android.data.model.ChatCompletionRequest
import com.kimi.ai.android.data.model.ChatMessage
import com.kimi.ai.android.data.model.MessageDto
import com.kimi.ai.android.data.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kimi_settings")

class ChatRepository(context: Context) {

    private val apiService = KimiApiService.create()
    private val dataStore = context.dataStore

    private val _conversations = mutableListOf<com.kimi.ai.android.data.model.Conversation>()
    private val conversationsMutex = Mutex()

    companion object {
        val API_KEY = stringPreferencesKey("api_key")
        val SELECTED_MODEL = stringPreferencesKey("selected_model")
        private const val DEFAULT_MODEL = "moonshot-v1-8k"
    }

    // Settings
    val apiKey: Flow<String> = dataStore.data.map { preferences ->
        preferences[API_KEY] ?: ""
    }

    val selectedModel: Flow<String> = dataStore.data.map { preferences ->
        preferences[SELECTED_MODEL] ?: DEFAULT_MODEL
    }

    suspend fun saveApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[API_KEY] = key
        }
    }

    suspend fun saveModel(model: String) {
        dataStore.edit { preferences ->
            preferences[SELECTED_MODEL] = model
        }
    }

    // Conversations
    suspend fun getConversations(): List<com.kimi.ai.android.data.model.Conversation> {
        return conversationsMutex.withLock {
            _conversations.sortedByDescending { it.updatedAt }
        }
    }

    suspend fun getConversation(id: String): com.kimi.ai.android.data.model.Conversation? {
        return conversationsMutex.withLock {
            _conversations.find { it.id == id }
        }
    }

    suspend fun createConversation(): com.kimi.ai.android.data.model.Conversation {
        val conversation = com.kimi.ai.android.data.model.Conversation()
        conversationsMutex.withLock {
            _conversations.add(conversation)
        }
        return conversation
    }

    suspend fun addMessage(conversationId: String, message: ChatMessage) {
        conversationsMutex.withLock {
            val index = _conversations.indexOfFirst { it.id == conversationId }
            if (index != -1) {
                val conv = _conversations[index]
                val updatedMessages = conv.messages + message
                val title = if (conv.title == "New Chat" && message.role == MessageRole.USER) {
                    message.content.take(30) + if (message.content.length > 30) "..." else ""
                } else conv.title
                _conversations[index] = conv.copy(
                    messages = updatedMessages,
                    title = title,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    suspend fun clearConversation(conversationId: String) {
        conversationsMutex.withLock {
            val index = _conversations.indexOfFirst { it.id == conversationId }
            if (index != -1) {
                val conv = _conversations[index]
                _conversations[index] = conv.copy(messages = emptyList(), title = "New Chat")
            }
        }
    }

    suspend fun deleteConversation(conversationId: String) {
        conversationsMutex.withLock {
            _conversations.removeAll { it.id == conversationId }
        }
    }

    // API
    suspend fun sendMessageToKimi(
        apiKey: String,
        model: String,
        messages: List<ChatMessage>
    ): String {
        val messageDtos = messages.map {
            MessageDto(
                role = when (it.role) {
                    MessageRole.USER -> "user"
                    MessageRole.ASSISTANT -> "assistant"
                    MessageRole.SYSTEM -> "system"
                },
                content = it.content
            )
        }

        val request = ChatCompletionRequest(
            model = model,
            messages = messageDtos
        )

        val response = apiService.sendMessage("Bearer $apiKey", request)
        return response.choices.firstOrNull()?.message?.content
            ?: throw Exception("Empty response from API")
    }
}
