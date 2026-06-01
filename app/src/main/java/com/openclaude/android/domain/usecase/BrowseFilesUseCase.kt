package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.FileNode
import com.openclaude.android.data.repository.FileRepository
import javax.inject.Inject

class BrowseFilesUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(path: String = "/"): Result<List<FileNode>> {
        return fileRepository.browseFiles(path)
    }
    
    fun expandFolder(node: FileNode): List<FileNode> {
        return fileRepository.expandFolder(node)
    }
}
