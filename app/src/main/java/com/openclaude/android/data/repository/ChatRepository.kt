package com.openclaude.android.data.repository

import com.openclaude.android.data.local.ConversationDao
import com.openclaude.android.data.local.MessageDao
import com.openclaude.android.data.model.ChatMessage
import com.openclaude.android.data.model.Conversation
import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.ApiError
import com.openclaude.android.data.remote.ApiService
import com.openclaude.android.data.remote.StreamEvent
import com.openclaude.android.data.remote.StreamingClient
import com.openclaude.android.data.remote.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val apiService: ApiService,
    private val streamingClient: StreamingClient,
    private val settingsRepository: SettingsRepository,
    private val networkMonitor: NetworkConnectivityMonitor,
) {
    fun getConversations(): Flow<List<Conversation>> = conversationDao.getAllConversations()

    fun getConversation(id: String): Flow<Conversation?> = conversationDao.observeConversation(id)

    fun getMessages(conversationId: String): Flow<List<ChatMessage>> =
        messageDao.getMessagesByConversation(conversationId)

    suspend fun createConversation(
        title: String = "New Chat",
        provider: Provider,
        model: Model,
    ): String {
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            provider = provider.name,
            model = model.id,
        )
        conversationDao.insertConversation(conversation)
        return conversation.id
    }

    suspend fun deleteConversation(id: String) {
        messageDao.deleteMessagesByConversation(id)
        conversationDao.deleteConversationById(id)
    }

    suspend fun deleteAllConversations() {
        messageDao.deleteAllMessages()
        conversationDao.deleteAllConversations()
    }

    suspend fun saveUserMessage(conversationId: String, content: String): ChatMessage {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "user",
            content = content,
        )
        messageDao.insertMessage(message)
        conversationDao.updateConversationTimestamp(conversationId)
        return message
    }

    suspend fun saveAssistantMessage(conversationId: String, content: String): ChatMessage {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = "assistant",
            content = content,
        )
        messageDao.insertMessage(message)
        conversationDao.updateConversationTimestamp(conversationId)
        return message
    }

    fun streamAssistantResponse(
        conversationId: String,
        provider: Provider,
        model: Model,
    ): Flow<StreamEvent> = flow {
        try {
            val apiKey = settingsRepository.getApiKey(provider)
                ?: throw ApiError.AuthError("API key not set for ${provider.displayName}")

            val messages = messageDao.getMessagesByConversationSync(conversationId)
            val messageDtos = messages.map { msg ->
                MessageDto(role = msg.role, content = msg.content)
            }

            val tempMessageId = UUID.randomUUID().toString()
            var fullContent = ""

            // Emit initial streaming message
            val tempMessage = ChatMessage(
                id = tempMessageId,
                conversationId = conversationId,
                role = "assistant",
                content = "",
                isStreaming = true,
            )
            messageDao.insertMessage(tempMessage)

            streamingClient.streamChatCompletion(
                baseUrl = settingsRepository.getBaseUrl(provider),
                apiKey = apiKey,
                model = model.id,
                messages = messageDtos,
                temperature = 0.7,
                maxTokens = model.maxTokens,
            ).collect { event ->
                when (event) {
                    is StreamEvent.Content -> {
                        if (event.text.isEmpty()) return@collect
                        
                        fullContent += event.text
                        messageDao.updateMessage(tempMessage.copy(content = fullContent))
                        emit(StreamEvent.Content(event.text))
                    }
                    is StreamEvent.Done -> {
                        messageDao.updateMessage(
                            tempMessage.copy(
                                content = fullContent,
                                isStreaming = false
                            )
                        )
                        conversationDao.updateConversationTimestamp(conversationId)
                        emit(StreamEvent.Done)
                    }
                    is StreamEvent.Error -> {
                        messageDao.updateMessage(
                            tempMessage.copy(
                                content = if (fullContent.isEmpty()) "Error: ${event.message}" else fullContent,
                                isStreaming = false
                            )
                        )
                        emit(event)
                    }
                }
            }
        } catch (e: ApiError) {
            emit(StreamEvent.Error(e.userMessage))
        } catch (e: IllegalStateException) {
            val apiError = ApiError.UnknownError(e.message ?: "Unknown error")
            emit(StreamEvent.Error(apiError.userMessage))
        } catch (e: Exception) {
            val apiError = ApiError.fromException(e)
            emit(StreamEvent.Error(apiError.userMessage))
        }
    }

    suspend fun updateConversationTitle(conversationId: String, title: String) {
        conversationDao.getConversationById(conversationId)?.let { conv ->
            conversationDao.updateConversation(conv.copy(title = title))
        }
    }

    suspend fun getAvailableModels(provider: Provider): List<String> {
        return try {
            if (!networkMonitor.isConnected()) {
                return Model.defaultModels(provider).map { it.id }
            }
            val apiKey = settingsRepository.getApiKey(provider) ?: return emptyList()
            streamingClient.getModels(
                baseUrl = settingsRepository.getBaseUrl(provider),
                apiKey = apiKey
            )
        } catch (e: ApiError) {
            Model.defaultModels(provider).map { it.id }
        } catch (e: Exception) {
            Model.defaultModels(provider).map { it.id }
        }
    }

    /**
     * Maps an exception to an ApiError for consistent error handling.
     */
    fun mapExceptionToApiError(e: Exception): ApiError {
        return when (e) {
            is ApiError -> e
            else -> ApiError.fromException(e)
        }
    }
}
