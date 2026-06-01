package com.openclaude.android.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.StreamEvent
import com.openclaude.android.data.repository.ChatRepository
import com.openclaude.android.data.repository.SettingsRepository
import com.openclaude.android.domain.model.ChatUiState
import com.openclaude.android.domain.model.MessageUiModel
import com.openclaude.android.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentConversationId: String? = null

    fun initialize(conversationId: String?) {
        val provider = settingsRepository.getSelectedProvider()
        val model = settingsRepository.getSelectedModel()

        _uiState.update {
            it.copy(
                currentProvider = provider,
                currentModel = model,
            )
        }

        if (conversationId != null) {
            loadConversation(conversationId)
        } else {
            createNewConversation()
        }
    }

    private fun createNewConversation() {
        viewModelScope.launch {
            val provider = _uiState.value.currentProvider
            val model = _uiState.value.currentModel
            val id = chatRepository.createConversation(
                title = "New Chat",
                provider = provider,
                model = model,
            )
            currentConversationId = id
            _uiState.update { it.copy(conversationId = id) }
            observeMessages(id)
        }
    }

    private fun loadConversation(conversationId: String) {
        currentConversationId = conversationId
        _uiState.update { it.copy(conversationId = conversationId) }
        observeMessages(conversationId)

        viewModelScope.launch {
            chatRepository.getConversation(conversationId).firstOrNull()?.let { conv ->
                val provider = Provider.fromName(conv.provider)
                val model = Model.defaultModels(provider).find { it.id == conv.model }
                    ?: Model.defaultModels(provider).first()
                _uiState.update {
                    it.copy(currentProvider = provider, currentModel = model)
                }
            }
        }
    }

    private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(conversationId).collect { messages ->
                val uiMessages = messages.map { msg ->
                    MessageUiModel(
                        id = msg.id,
                        role = msg.role,
                        content = msg.content,
                        timestamp = msg.timestamp,
                        isStreaming = msg.isStreaming,
                    )
                }
                _uiState.update { it.copy(messages = uiMessages) }
            }
        }
    }

    fun sendMessage(content: String) {
        val convId = currentConversationId ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                sendMessageUseCase(
                    conversationId = convId,
                    content = content,
                    provider = _uiState.value.currentProvider,
                    model = _uiState.value.currentModel,
                ).collect { event ->
                    when (event) {
                        is StreamEvent.Content -> {
                            _uiState.update { it.copy(isStreaming = true) }
                        }
                        is StreamEvent.Done -> {
                            _uiState.update { it.copy(isLoading = false, isStreaming = false) }
                            // Update title from first user message
                            val messages = _uiState.value.messages
                            if (messages.size <= 3) {
                                val firstUserMsg = messages.firstOrNull { it.role == "user" }
                                if (firstUserMsg != null) {
                                    val title = firstUserMsg.content.take(50)
                                    chatRepository.updateConversationTitle(convId, title)
                                }
                            }
                        }
                        is StreamEvent.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isStreaming = false,
                                    error = event.message,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isStreaming = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }

    fun setProvider(provider: Provider) {
        val models = Model.defaultModels(provider)
        val model = models.first()
        _uiState.update { it.copy(currentProvider = provider, currentModel = model) }
        settingsRepository.setSelectedProvider(provider)
        settingsRepository.setSelectedModel(model)
    }

    fun setModel(model: Model) {
        _uiState.update { it.copy(currentModel = model) }
        settingsRepository.setSelectedModel(model)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun newChat() {
        createNewConversation()
    }
}
