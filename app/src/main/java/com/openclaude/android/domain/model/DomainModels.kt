package com.openclaude.android.domain.model

import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider

data class ChatUiState(
    val messages: List<MessageUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isStreaming: Boolean = false,
    val error: String? = null,
    val canRetry: Boolean = false,
    val currentProvider: Provider = Provider.OPENAI,
    val currentModel: Model = Model.defaultModels(Provider.OPENAI).first(),
    val conversationId: String? = null,
)

data class MessageUiModel(
    val id: String,
    val role: String,
    val content: String,
    val timestamp: Long,
    val isStreaming: Boolean = false,
)

data class ConversationUiModel(
    val id: String,
    val title: String,
    val provider: Provider,
    val model: String,
    val lastMessage: String,
    val updatedAt: Long,
    val messageCount: Int,
)

data class SettingsUiState(
    val selectedProvider: Provider = Provider.OPENAI,
    val selectedModel: Model = Model.defaultModels(Provider.OPENAI).first(),
    val apiKeys: Map<Provider, String> = emptyMap(),
    val availableModels: List<Model> = Model.defaultModels(Provider.OPENAI),
    val customBaseUrls: Map<Provider, String> = emptyMap(),
    val isTestingConnection: Boolean = false,
    val connectionTestResult: ConnectionTestResult? = null,
)

sealed class ConnectionTestResult {
    data object Success : ConnectionTestResult()
    data class Failure(val message: String) : ConnectionTestResult()
}
