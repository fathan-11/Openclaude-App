package com.openclaude.android.ui.screens.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.*
import com.openclaude.android.domain.usecase.ExecuteToolUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolOutputUiState(
    val executions: List<ToolExecution> = emptyList(),
    val selectedExecution: ToolExecution? = null,
    val selectedResult: ToolResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ToolOutputViewModel @Inject constructor(
    private val executeToolUseCase: ExecuteToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolOutputUiState())
    val uiState: StateFlow<ToolOutputUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            executeToolUseCase.getHistory()
                .onSuccess { history ->
                    _uiState.update { it.copy(executions = history, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun selectExecution(execution: ToolExecution) {
        _uiState.update { it.copy(selectedExecution = execution) }
    }

    fun executeTool(type: ToolType, name: String, input: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            executeToolUseCase(type, name, input)
                .onSuccess { result ->
                    _uiState.update { it.copy(selectedResult = result, isLoading = false) }
                    loadHistory()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }
}
