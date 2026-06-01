package com.openclaude.android.di

import com.openclaude.android.data.local.ConversationDao
import com.openclaude.android.data.local.MessageDao
import com.openclaude.android.data.remote.ApiService
import com.openclaude.android.data.remote.FileApiService
import com.openclaude.android.data.remote.StreamingClient
import com.openclaude.android.data.repository.ChatRepository
import com.openclaude.android.data.repository.FileRepository
import com.openclaude.android.data.repository.SettingsRepository
import com.openclaude.android.domain.usecase.BrowseFilesUseCase
import com.openclaude.android.domain.usecase.ReadFileUseCase
import com.openclaude.android.domain.usecase.SearchCodeUseCase
import com.openclaude.android.data.remote.McpApiService
import com.openclaude.android.data.remote.TerminalApiService
import com.openclaude.android.data.remote.ToolApiService
import com.openclaude.android.data.repository.McpRepository
import com.openclaude.android.data.repository.TerminalRepository
import com.openclaude.android.data.repository.ToolRepository
import com.openclaude.android.domain.usecase.GetDiffUseCase
import com.openclaude.android.domain.usecase.ExecuteToolUseCase
import com.openclaude.android.domain.usecase.ManageMcpUseCase
import com.openclaude.android.domain.usecase.RunCommandUseCase
import com.openclaude.android.data.remote.GitHubApiService
import com.openclaude.android.data.repository.GitHubRepository
import com.openclaude.android.domain.usecase.LoadReposUseCase
import com.openclaude.android.domain.usecase.GetRepoDetailsUseCase
import com.openclaude.android.domain.usecase.GetPullRequestsUseCase
import com.openclaude.android.domain.usecase.GetIssuesUseCase
import com.openclaude.android.domain.usecase.GetCommitsUseCase
import com.openclaude.android.domain.usecase.CreateIssueUseCase
import com.openclaude.android.domain.usecase.CreatePullRequestUseCase
import com.openclaude.android.domain.usecase.VoiceUseCase
import com.openclaude.android.domain.usecase.SkillsUseCase
import com.openclaude.android.domain.usecase.MemoryUseCase
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

    @Provides
    @Singleton
    fun provideFileRepository(
        fileApiService: FileApiService
    ): FileRepository {
        return FileRepository(fileApiService)
    }

    @Provides
    @Singleton
    fun provideBrowseFilesUseCase(
        fileRepository: FileRepository
    ): BrowseFilesUseCase {
        return BrowseFilesUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideReadFileUseCase(
        fileRepository: FileRepository
    ): ReadFileUseCase {
        return ReadFileUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideSearchCodeUseCase(
        fileRepository: FileRepository
    ): SearchCodeUseCase {
        return SearchCodeUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideGetDiffUseCase(
        fileRepository: FileRepository
    ): GetDiffUseCase {
        return GetDiffUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideTerminalRepository(
        terminalApiService: TerminalApiService
    ): TerminalRepository {
        return TerminalRepository(terminalApiService)
    }

    @Provides
    @Singleton
    fun provideToolRepository(
        toolApiService: ToolApiService
    ): ToolRepository {
        return ToolRepository(toolApiService)
    }

    @Provides
    @Singleton
    fun provideMcpRepository(
        mcpApiService: McpApiService
    ): McpRepository {
        return McpRepository(mcpApiService)
    }

    @Provides
    @Singleton
    fun provideRunCommandUseCase(
        terminalRepository: TerminalRepository
    ): RunCommandUseCase {
        return RunCommandUseCase(terminalRepository)
    }

    @Provides
    @Singleton
    fun provideExecuteToolUseCase(
        toolRepository: ToolRepository
    ): ExecuteToolUseCase {
        return ExecuteToolUseCase(toolRepository)
    }

    @Provides
    @Singleton
    fun provideManageMcpUseCase(
        mcpRepository: McpRepository
    ): ManageMcpUseCase {
        return ManageMcpUseCase(mcpRepository)
    }

    @Provides
    @Singleton
    fun provideGitHubRepository(
        gitHubApiService: GitHubApiService
    ): GitHubRepository {
        return GitHubRepository(gitHubApiService)
    }

    @Provides
    @Singleton
    fun provideLoadReposUseCase(
        gitHubRepository: GitHubRepository
    ): LoadReposUseCase {
        return LoadReposUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideGetRepoDetailsUseCase(
        gitHubRepository: GitHubRepository
    ): GetRepoDetailsUseCase {
        return GetRepoDetailsUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideGetPullRequestsUseCase(
        gitHubRepository: GitHubRepository
    ): GetPullRequestsUseCase {
        return GetPullRequestsUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideGetIssuesUseCase(
        gitHubRepository: GitHubRepository
    ): GetIssuesUseCase {
        return GetIssuesUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideGetCommitsUseCase(
        gitHubRepository: GitHubRepository
    ): GetCommitsUseCase {
        return GetCommitsUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideCreateIssueUseCase(
        gitHubRepository: GitHubRepository
    ): CreateIssueUseCase {
        return CreateIssueUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideCreatePullRequestUseCase(
        gitHubRepository: GitHubRepository
    ): CreatePullRequestUseCase {
        return CreatePullRequestUseCase(gitHubRepository)
    }

    @Provides
    @Singleton
    fun provideVoiceUseCase(): VoiceUseCase {
        return VoiceUseCase()
    }

    @Provides
    @Singleton
    fun provideSkillsUseCase(): SkillsUseCase {
        return SkillsUseCase()
    }

    @Provides
    @Singleton
    fun provideMemoryUseCase(): MemoryUseCase {
        return MemoryUseCase()
    }
}
