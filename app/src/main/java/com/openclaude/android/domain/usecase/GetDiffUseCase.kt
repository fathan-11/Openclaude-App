package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.DiffResult
import com.openclaude.android.data.repository.FileRepository
import javax.inject.Inject

class GetDiffUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(file: String): Result<DiffResult> {
        return fileRepository.getDiff(file)
    }
}
