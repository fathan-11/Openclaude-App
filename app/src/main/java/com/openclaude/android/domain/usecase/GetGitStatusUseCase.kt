package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.GitStatus
import com.openclaude.android.data.repository.GitRepository
import javax.inject.Inject

class GetGitStatusUseCase @Inject constructor(
    private val gitRepository: GitRepository
) {
    suspend operator fun invoke(path: String = "/"): Result<Map<String, GitStatus>> {
        return gitRepository.fetchStatus(path)
    }

    suspend fun getStatusForFile(path: String): GitStatus? {
        return gitRepository.getStatusForFile(path)
    }

    fun isGitRepo() = gitRepository.isGitRepo
}
