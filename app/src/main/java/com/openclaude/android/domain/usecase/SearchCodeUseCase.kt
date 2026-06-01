package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.SearchQuery
import com.openclaude.android.data.model.SearchResult
import com.openclaude.android.data.repository.FileRepository
import javax.inject.Inject

class SearchCodeUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(query: SearchQuery): Result<List<SearchResult>> {
        return fileRepository.searchCode(query)
    }
}
