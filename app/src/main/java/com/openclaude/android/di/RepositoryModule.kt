package com.openclaude.android.di

import com.openclaude.android.data.local.ConversationDao
import com.openclaude.android.data.local.MessageDao
import com.openclaude.android.data.remote.ApiService
import com.openclaude.android.data.remote.StreamingClient
import com.openclaude.android.data.repository.ChatRepository
import com.openclaude.android.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        conversationDao: ConversationDao,
        messageDao: MessageDao,
        apiService: ApiService,
        streamingClient: StreamingClient,
        settingsRepository: SettingsRepository,
    ): ChatRepository {
        return ChatRepository(
            conversationDao = conversationDao,
            messageDao = messageDao,
            apiService = apiService,
            streamingClient = streamingClient,
            settingsRepository = settingsRepository,
        )
    }
}
