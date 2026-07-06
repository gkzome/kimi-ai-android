package com.kimi.ai.android.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kimi.ai.android.data.model.ChatMessage
import com.kimi.ai.android.data.model.Conversation
import com.kimi.ai.android.data.model.KimiModel
import com.kimi.ai.android.data.model.MessageRole
import com.kimi.ai.android.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository(application)

    private val _currentConversation = mutableStateOf<Conversation?>(null)
    val currentConversation: State<Conversation?> = _currentConversation

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: SnapshotStateList<ChatMessage> = _messages

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    private val _conversations = mutableStateListOf<Conversation>()
    val conversations: SnapshotStateList<Conversation> = _conversations

    private val _currentModel = mutableStateOf(KimiModel.Moonshot8K)
    val currentModel: State<KimiModel> = _currentModel

    init {
        viewModelScope.launch {
            val modelId = repository.selectedModel.first()
            _currentModel.value = KimiModel.fromId(modelId)
            createNewConversation()
            loadConversations()
        }
    }

    fun createNewConversation() {
        viewModelScope.launch {
            val conversation = repository.createConversation()
            _currentConversation.value = conversation
            _messages.clear()
            _messages.addAll(conversation.messages)
            loadConversations()
        }
    }

    fun selectConversation(conversationId: String) {
        viewModelScope.launch {
            val conversation = repository.getConversation(conversationId)
            conversation?.let {
                _currentConversation.value = it
                _messages.clear()
                _messages.addAll(it.messages)
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val apiKey = repository.apiKey.first()
            if (apiKey.isBlank()) {
                _errorMessage.emit("Please set your API key in settings")
                return@launch
            }

            val conversationId = _currentConversation.value?.id ?: return@launch

            // Add user message
            val userMessage = ChatMessage(role = MessageRole.USER, content = content.trim())
            repository.addMessage(conversationId, userMessage)
            _messages.add(userMessage)

            // Add loading message
            val loadingMessage = ChatMessage(role = MessageRole.ASSISTANT, content = "", isLoading = true)
            _messages.add(loadingMessage)
            _isLoading.value = true

            try {
                val model = _currentModel.value.id
                val currentMessages = _messages.filter { !it.isLoading }.toList()

                val response = repository.sendMessageToKimi(apiKey, model, currentMessages)

                // Remove loading message
                _messages.remove(loadingMessage)

                // Add AI response
                val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, content = response)
                repository.addMessage(conversationId, assistantMessage)
                _messages.add(assistantMessage)

                // Update current conversation
                _currentConversation.value = repository.getConversation(conversationId)
                loadConversations()

            } catch (e: Exception) {
                _messages.remove(loadingMessage)
                val errorMsg = when {
                    e.message?.contains("401") == true -> "Invalid API key. Please check your settings."
                    e.message?.contains("429") == true -> "Rate limit exceeded. Please try again later."
                    e.message?.contains("timeout", ignoreCase = true) == true -> "Request timed out. Please try again."
                    else -> "Error: ${e.localizedMessage ?: "Something went wrong"}"
                }
                _errorMessage.emit(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearCurrentChat() {
        viewModelScope.launch {
            _currentConversation.value?.id?.let { id ->
                repository.clearConversation(id)
                _messages.clear()
                _currentConversation.value = repository.getConversation(id)
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            repository.deleteConversation(conversationId)
            if (_currentConversation.value?.id == conversationId) {
                createNewConversation()
            } else {
                loadConversations()
            }
        }
    }

    fun saveApiKey(apiKey: String) {
        viewModelScope.launch {
            repository.saveApiKey(apiKey.trim())
        }
    }

    fun saveModel(model: KimiModel) {
        viewModelScope.launch {
            _currentModel.value = model
            repository.saveModel(model.id)
        }
    }

    private fun loadConversations() {
        viewModelScope.launch {
            val convs = repository.getConversations()
            _conversations.clear()
            _conversations.addAll(convs)
        }
    }
}
