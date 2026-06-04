package com.openclaude.android.ui.screens.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.FileNode
import com.openclaude.android.data.model.GitStatus
import com.openclaude.android.domain.usecase.BrowseFilesUseCase
import com.openclaude.android.domain.usecase.GetGitStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FileBrowserUiState(
    val files: List<FileNode> = emptyList(),
    val currentPath: String = "/",
    val isLoading: Boolean = false,
    val error: String? = null,
    val gitStatus: Map<String, GitStatus> = emptyMap(),
    val isGitRepo: Boolean = false,
    val gitStatusLoading: Boolean = false
)

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val browseFilesUseCase: BrowseFilesUseCase,
    private val getGitStatusUseCase: GetGitStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileBrowserUiState())
    val uiState: StateFlow<FileBrowserUiState> = _uiState.asStateFlow()

    init {
        loadFiles("/")
    }

    fun loadFiles(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            browseFilesUseCase(path)
                .onSuccess { files ->
                    val enrichedFiles = files.map { file ->
                        val gitStatus = getGitStatusUseCase.getStatusForFile(file.path)
                        file.copy(gitStatus = gitStatus)
                    }
                    _uiState.update { it.copy(files = enrichedFiles, currentPath = path, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun refreshGitStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(gitStatusLoading = true) }
            getGitStatusUseCase(_uiState.value.currentPath)
                .onSuccess { statusMap ->
                    _uiState.update { state ->
                        val enrichedFiles = state.files.map { file ->
                            val gitStatus = statusMap[file.path]
                            file.copy(gitStatus = gitStatus)
                        }
                        state.copy(
                            gitStatus = statusMap,
                            isGitRepo = true,
                            files = enrichedFiles,
                            gitStatusLoading = false
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isGitRepo = false, gitStatusLoading = false) }
                }
        }
    }

    fun toggleFolder(node: FileNode) {
        if (node.isDirectory) {
            browseFilesUseCase.expandFolder(node)
            viewModelScope.launch {
                loadFiles(node.path)
            }
        }
    }

    fun navigateUp() {
        val parent = _uiState.value.currentPath.substringBeforeLast("/", "/")
        loadFiles(parent)
    }

    fun refresh() {
        loadFiles(_uiState.value.currentPath)
        refreshGitStatus()
    }

    fun toggleGitStatusFilter(status: GitStatus) {
        // Toggle filtering by git status
        viewModelScope.launch {
            val currentFiles = _uiState.value.files
            val gitStatusMap = _uiState.value.gitStatus

            val enrichedFiles = currentFiles.map { file ->
                val gitStatus = gitStatusMap[file.path]
                file.copy(gitStatus = gitStatus)
            }
            _uiState.update { it.copy(files = enrichedFiles) }
        }
    }
}
