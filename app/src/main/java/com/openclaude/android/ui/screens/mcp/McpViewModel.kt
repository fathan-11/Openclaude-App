package com.openclaude.android.ui.screens.mcp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.*
import com.openclaude.android.domain.usecase.ManageMcpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class McpUiState(
    val servers: List<McpServer> = emptyList(),
    val selectedServer: McpServer? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false
)

@HiltViewModel
class McpViewModel @Inject constructor(
    private val manageMcpUseCase: ManageMcpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(McpUiState())
    val uiState: StateFlow<McpUiState> = _uiState.asStateFlow()

    init {
        loadServers()
    }

    fun loadServers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            manageMcpUseCase.loadServers()
                .onSuccess { servers ->
                    _uiState.update { it.copy(servers = servers, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun addServer(name: String, url: String, description: String) {
        viewModelScope.launch {
            manageMcpUseCase.addServer(name, url, description)
                .onSuccess {
                    _uiState.update { it.copy(showAddDialog = false) }
                    loadServers()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }

    fun removeServer(serverId: String) {
        viewModelScope.launch {
            manageMcpUseCase.removeServer(serverId)
            loadServers()
        }
    }

    fun connectServer(serverId: String) {
        viewModelScope.launch {
            manageMcpUseCase.connect(serverId)
            loadServers()
        }
    }

    fun disconnectServer(serverId: String) {
        viewModelScope.launch {
            manageMcpUseCase.disconnect(serverId)
            loadServers()
        }
    }

    fun selectServer(server: McpServer) {
        _uiState.update { it.copy(selectedServer = server) }
    }

    fun toggleAddDialog() {
        _uiState.update { it.copy(showAddDialog = !it.showAddDialog) }
    }
}
