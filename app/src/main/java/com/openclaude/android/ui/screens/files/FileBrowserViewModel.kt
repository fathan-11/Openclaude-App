package com.openclaude.android.ui.screens.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.FileNode
import com.openclaude.android.data.model.GitStatus
import com.openclaude.android.domain.usecase.BrowseFilesUseCase
import com.openclaude.android.domain.usecase.FileOperationsUseCase
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
    val gitStatusLoading: Boolean = false,
    val showCreateDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showRenameDialog: Boolean = false,
    val selectedFile: FileNode? = null,
    val newName: String = "",
    val operationInProgress: Boolean = false,
    val operationMessage: String? = null
)

@HiltViewModel
class FileBrowserViewModel @Inject constructor(
    private val browseFilesUseCase: BrowseFilesUseCase,
    private val getGitStatusUseCase: GetGitStatusUseCase,
    private val fileOperationsUseCase: FileOperationsUseCase
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

    // File operations
    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true, newName = "") }
    }

    fun showDeleteDialog(file: FileNode) {
        _uiState.update { it.copy(showDeleteDialog = true, selectedFile = file) }
    }

    fun showRenameDialog(file: FileNode) {
        _uiState.update { it.copy(showRenameDialog = true, selectedFile = file, newName = file.name) }
    }

    fun dismissDialogs() {
        _uiState.update { it.copy(showCreateDialog = false, showDeleteDialog = false, showRenameDialog = false, selectedFile = null) }
    }

    fun updateNewName(name: String) {
        _uiState.update { it.copy(newName = name) }
    }

    fun createFile(name: String, isDirectory: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(operationInProgress = true) }
            val path = "${_uiState.value.currentPath}/$name"
            val result = if (isDirectory) {
                fileOperationsUseCase.createDirectory(path)
            } else {
                fileOperationsUseCase.createFile(path)
            }
            result.onSuccess {
                _uiState.update { it.copy(operationInProgress = false, operationMessage = "Created $name", showCreateDialog = false) }
                loadFiles(_uiState.value.currentPath)
            }.onFailure { e ->
                _uiState.update { it.copy(operationInProgress = false, operationMessage = "Error: ${e.message}") }
            }
        }
    }

    fun deleteSelectedFile() {
        val file = _uiState.value.selectedFile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(operationInProgress = true) }
            fileOperationsUseCase.deleteFile(file.path)
                .onSuccess {
                    _uiState.update { it.copy(operationInProgress = false, operationMessage = "Deleted ${file.name}", showDeleteDialog = false, selectedFile = null) }
                    loadFiles(_uiState.value.currentPath)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(operationInProgress = false, operationMessage = "Error: ${e.message}") }
                }
        }
    }

    fun renameSelectedFile() {
        val file = _uiState.value.selectedFile ?: return
        val newName = _uiState.value.newName.trim()
        if (newName.isBlank() || newName == file.name) {
            _uiState.update { it.copy(showRenameDialog = false) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(operationInProgress = true) }
            val newPath = "${_uiState.value.currentPath}/$newName"
            fileOperationsUseCase.renameFile(file.path, newPath)
                .onSuccess {
                    _uiState.update { it.copy(operationInProgress = false, operationMessage = "Renamed to $newName", showRenameDialog = false, selectedFile = null) }
                    loadFiles(_uiState.value.currentPath)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(operationInProgress = false, operationMessage = "Error: ${e.message}") }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(operationMessage = null) }
    }

    fun toggleGitStatusFilter(status: GitStatus) {
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
