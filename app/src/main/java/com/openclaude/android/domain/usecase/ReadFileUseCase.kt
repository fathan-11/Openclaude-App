package com.openclaude.android.domain.usecase

import com.openclaude.android.data.remote.FileContent
import com.openclaude.android.data.repository.FileRepository
import javax.inject.Inject

class ReadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(path: String): Result<FileContent> {
        return fileRepository.readFile(path)
    }
}
