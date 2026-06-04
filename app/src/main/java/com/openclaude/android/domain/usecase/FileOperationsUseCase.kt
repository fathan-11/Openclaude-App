package com.openclaude.android.domain.usecase

import com.openclaude.android.data.remote.FileOperationResult
import com.openclaude.android.data.repository.FileRepository
import javax.inject.Inject

class FileOperationsUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend fun createFile(path: String, content: String = ""): Result<FileOperationResult> {
        return fileRepository.createFile(path, content)
    }

    suspend fun writeFile(path: String, content: String): Result<FileOperationResult> {
        return fileRepository.writeFile(path, content)
    }

    suspend fun deleteFile(path: String): Result<FileOperationResult> {
        return fileRepository.deleteFile(path)
    }

    suspend fun createDirectory(path: String): Result<FileOperationResult> {
        return fileRepository.createDirectory(path)
    }

    suspend fun renameFile(oldPath: String, newPath: String): Result<FileOperationResult> {
        return fileRepository.renameFile(oldPath, newPath)
    }
}
