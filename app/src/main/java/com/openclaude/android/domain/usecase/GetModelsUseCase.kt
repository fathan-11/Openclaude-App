package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.Model
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.repository.ChatRepository
import javax.inject.Inject

class GetModelsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    operator fun invoke(provider: Provider): List<Model> {
        return Model.defaultModels(provider)
    }

    suspend fun fetchRemoteModels(provider: Provider): List<String> {
        return chatRepository.getAvailableModels(provider)
    }
}
