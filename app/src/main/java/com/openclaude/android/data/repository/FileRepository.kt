package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.FileApiService
import com.openclaude.android.data.remote.FileContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val fileApiService: FileApiService
) {
    private val _fileTree = MutableStateFlow<List<FileNode>>(emptyList())
    val fileTree: Flow<List<FileNode>> = _fileTree.asStateFlow()
    
    private val _currentPath = MutableStateFlow("/")
    val currentPath: Flow<String> = _currentPath.asStateFlow()

    suspend fun browseFiles(path: String = "/"): Result<List<FileNode>> {
        return try {
            val files = fileApiService.listFiles(path)
            _fileTree.value = files
            _currentPath.value = path
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readFile(path: String): Result<FileContent> {
        return try {
            val content = fileApiService.readFile(path)
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchCode(query: SearchQuery): Result<List<SearchResult>> {
        return try {
            val results = fileApiService.searchCode(query.text, query.pathFilter)
            val filtered = if (query.fileFilter.isNotEmpty()) {
                results.filter { it.fileName.contains(query.fileFilter, !query.caseSensitive) }
            } else results
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDiff(file: String): Result<DiffResult> {
        return try {
            val diff = fileApiService.getDiff(file)
            Result.success(diff)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun expandFolder(node: FileNode): List<FileNode> {
        return _fileTree.value.map { n ->
            if (n.path == node.path) n.copy(isExpanded = !n.isExpanded) else n
        }.also { _fileTree.value = it }
    }
}
