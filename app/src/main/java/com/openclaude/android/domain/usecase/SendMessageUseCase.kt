package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.remote.StreamEvent
import com.openclaude.android.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        provider: Provider,
        model: Model,
    ): Flow<StreamEvent> {
        chatRepository.saveUserMessage(conversationId, content)
        return chatRepository.streamAssistantResponse(conversationId, provider, model)
    }
}
