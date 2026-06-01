package com.openclaude.android.ui.screens.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.Conversation
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.repository.ChatRepository
import com.openclaude.android.domain.model.ConversationUiModel
import com.openclaude.android.domain.usecase.GetConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState: StateFlow<ConversationListUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            getConversationsUseCase().collect { conversations ->
                val uiModels = conversations.map { conv ->
                    ConversationUiModel(
                        id = conv.id,
                        title = conv.title,
                        provider = Provider.fromName(conv.provider),
                        model = conv.model,
                        lastMessage = conv.title,
                        updatedAt = conv.updatedAt,
                        messageCount = conv.messageCount,
                    )
                }
                _uiState.update { it.copy(conversations = uiModels, isLoading = false) }
            }
        }
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepository.deleteConversation(id)
        }
    }

    fun deleteAllConversations() {
        viewModelScope.launch {
            chatRepository.deleteAllConversations()
        }
    }
}

data class ConversationListUiState(
    val conversations: List<ConversationUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
