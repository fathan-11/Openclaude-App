package com.openclaude.android.ui.screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.*
import com.openclaude.android.data.repository.TerminalRepository
import com.openclaude.android.domain.usecase.RunCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TerminalUiState(
    val session: TerminalSession? = null,
    val inputText: String = "",
    val commandHistory: List<String> = emptyList(),
    val historyIndex: Int = -1,
    val isRunning: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val runCommandUseCase: RunCommandUseCase,
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            terminalRepository.activeSession.collect { session ->
                _uiState.update { it.copy(session = session) }
            }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun executeCommand() {
        val command = _uiState.value.inputText.trim()
        if (command.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, error = null, inputText = "") }

            // Add to history
            val history = (_uiState.value.commandHistory + command).distinct().takeLast(100)
            _uiState.update { it.copy(commandHistory = history, historyIndex = -1) }

            runCommandUseCase(command)
                .onSuccess { cmd ->
                    // Poll for output
                    while (true) {
                        delay(100)
                        runCommandUseCase.getOutput(cmd.id)
                        val session = terminalRepository.activeSession.value
                        if (session?.currentCommand?.status != CommandStatus.RUNNING) break
                    }
                    _uiState.update { it.copy(isRunning = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isRunning = false, error = e.message) }
                }
        }
    }

    fun killCurrentCommand() {
        val cmdId = _uiState.value.session?.currentCommand?.id ?: return
        viewModelScope.launch {
            runCommandUseCase.kill(cmdId)
        }
    }

    fun navigateHistoryUp() {
        val history = _uiState.value.commandHistory
        if (history.isEmpty()) return
        val newIndex = (_uiState.value.historyIndex + 1).coerceAtMost(history.size - 1)
        _uiState.update { it.copy(historyIndex = newIndex, inputText = history[history.size - 1 - newIndex]) }
    }

    fun navigateHistoryDown() {
        val history = _uiState.value.commandHistory
        val newIndex = (_uiState.value.historyIndex - 1).coerceAtLeast(-1)
        _uiState.update {
            it.copy(
                historyIndex = newIndex,
                inputText = if (newIndex == -1) "" else history[history.size - 1 - newIndex]
            )
        }
    }

    fun clearTerminal() {
        terminalRepository.clearSession()
    }
}
