package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.Conversation
import com.openclaude.android.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(): Flow<List<Conversation>> {
        return chatRepository.getConversations()
    }
}
