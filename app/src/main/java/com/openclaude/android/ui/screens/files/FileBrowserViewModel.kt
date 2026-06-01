package com.openclaude.android.ui.screens.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.FileNode
import com.openclaude.android.domain.usecase.BrowseFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FileBrowserUiState(
    val files: List<FileNode> = emptyList(),
    val currentPath: String = "/",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val browseFilesUseCase: BrowseFilesUseCase
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
                    _uiState.update { it.copy(files = files, currentPath = path, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun toggleFolder(node: FileNode) {
        if (node.isDirectory) {
            browseFilesUseCase.expandFolder(node)
            viewModelScope.launch {
                // Reload to get children
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
    }
}
