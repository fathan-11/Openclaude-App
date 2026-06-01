package com.openclaude.android.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.ApiError
import com.openclaude.android.data.remote.StreamEvent
import com.openclaude.android.data.repository.ChatRepository
import com.openclaude.android.data.repository.SettingsRepository
import com.openclaude.android.domain.model.ChatUiState
import com.openclaude.android.domain.model.MessageUiModel
import com.openclaude.android.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository,
    private val networkMonitor: NetworkConnectivityMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    /** Whether the device is currently online. */
    val isOnline: StateFlow<Boolean> = networkMonitor.observe()
        .map { it is NetworkState.Connected }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), networkMonitor.isConnected())

    private var currentConversationId: String? = null
    private var lastMessageContent: String? = null
    private var retryCount = 0
    private val maxRetries = 3

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

        lastMessageContent = content
        retryCount = 0

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, canRetry = false) }

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
                            _uiState.update { it.copy(isLoading = false, isStreaming = false, canRetry = false) }
                            lastMessageContent = null
                            retryCount = 0
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
                            val apiError = mapErrorToApiError(event.message)
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isStreaming = false,
                                    error = event.message,
                                    errorType = apiError,
                                    canRetry = apiError?.isRetryable == true && retryCount < maxRetries,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                val apiError = chatRepository.mapExceptionToApiError(e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isStreaming = false,
                        error = apiError.userMessage,
                        errorType = apiError,
                        canRetry = apiError.isRetryable && retryCount < maxRetries,
                    )
                }
            }
        }
    }

    private fun mapErrorToApiError(errorMessage: String): ApiError? {
        return when {
            errorMessage.contains("timeout", ignoreCase = true) -> ApiError.TimeoutError(errorMessage)
            errorMessage.contains("network", ignoreCase = true) ||
            errorMessage.contains("connection", ignoreCase = true) -> ApiError.NetworkError(errorMessage)
            errorMessage.contains("401", ignoreCase = true) ||
            errorMessage.contains("403", ignoreCase = true) ||
            errorMessage.contains("invalid api key", ignoreCase = true) -> ApiError.AuthError(errorMessage)
            errorMessage.contains("429", ignoreCase = true) ||
            errorMessage.contains("rate limit", ignoreCase = true) -> ApiError.RateLimitError(errorMessage)
            errorMessage.contains("500", ignoreCase = true) ||
            errorMessage.contains("502", ignoreCase = true) ||
            errorMessage.contains("503", ignoreCase = true) ||
            errorMessage.contains("server error", ignoreCase = true) -> ApiError.ServerError(errorMessage)
            else -> ApiError.UnknownError(errorMessage)
        }
    }

    fun retryLastMessage() {
        val content = lastMessageContent ?: return
        if (retryCount >= maxRetries) {
            _uiState.update { it.copy(canRetry = false) }
            return
        }
        
        retryCount++
        sendMessage(content)
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
        _uiState.update { it.copy(error = null, errorType = null, canRetry = false) }
    }

    fun newChat() {
        createNewConversation()
    }
}
