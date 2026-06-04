package com.openclaude.android.data.repository

import com.openclaude.android.data.model.GitStatus
import com.openclaude.android.data.remote.GitApiService
import com.openclaude.android.data.remote.GitFileStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitRepository @Inject constructor(
    private val gitApiService: GitApiService
) {
    private val _gitStatus = MutableStateFlow<Map<String, GitStatus>>(emptyMap())
    val gitStatus: Flow<Map<String, GitStatus>> = _gitStatus.asStateFlow()

    private val _isGitRepo = MutableStateFlow(false)
    val isGitRepo: Flow<Boolean> = _isGitRepo.asStateFlow()

    suspend fun fetchStatus(path: String = "/"): Result<Map<String, GitStatus>> {
        return try {
            val statuses = gitApiService.getStatus(path)
            val statusMap = statuses.associate { fileStatus ->
                fileStatus.path to parseGitStatus(fileStatus)
            }
            _gitStatus.value = statusMap
            _isGitRepo.value = true
            Result.success(statusMap)
        } catch (e: Exception) {
            _isGitRepo.value = false
            Result.failure(e)
        }
    }

    suspend fun getStatusForFile(path: String): GitStatus? {
        return _gitStatus.value[path]
    }

    suspend fun getDiff(file: String) = gitApiService.getDiff(file)

    suspend fun getLog(path: String = "/", limit: Int = 10) = gitApiService.getLog(path, limit)

    private fun parseGitStatus(fileStatus: GitFileStatus): GitStatus {
        return when {
            fileStatus.status == "?" -> GitStatus.UNTRACKED
            fileStatus.status == "!" -> GitStatus.CONFLICT
            fileStatus.status == "M" && !fileStatus.staged -> GitStatus.MODIFIED
            fileStatus.status == "M" && fileStatus.staged -> GitStatus.MODIFIED_STAGED
            fileStatus.status == "A" && !fileStatus.staged -> GitStatus.ADDED
            fileStatus.status == "A" && fileStatus.staged -> GitStatus.ADDED_STAGED
            fileStatus.status == "D" && !fileStatus.staged -> GitStatus.DELETED
            fileStatus.status == "D" && fileStatus.staged -> GitStatus.DELETED_STAGED
            fileStatus.status == "R" -> GitStatus.RENAMED
            fileStatus.status == "C" -> GitStatus.COPIED
            fileStatus.status.isBlank() -> GitStatus.CLEAN
            else -> GitStatus.NONE
        }
    }
}
