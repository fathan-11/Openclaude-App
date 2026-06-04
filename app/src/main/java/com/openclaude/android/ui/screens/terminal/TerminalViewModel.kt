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
    val sessions: List<TerminalSession> = emptyList(),
    val activeSessionIndex: Int = 0,
    val inputText: String = "",
    val commandHistory: List<String> = emptyList(),
    val historyIndex: Int = -1,
    val isRunning: Boolean = false,
    val error: String? = null
) {
    val activeSession: TerminalSession? get() = sessions.getOrNull(activeSessionIndex)
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val runCommandUseCase: RunCommandUseCase,
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            terminalRepository.sessions.collect { sessions ->
                if (sessions.isNotEmpty()) {
                    _uiState.update { it.copy(sessions = sessions) }
                }
            }
        }
        // Create initial session
        viewModelScope.launch {
            val session = terminalRepository.createSession("Terminal 1")
            _uiState.update { it.copy(sessions = listOf(session), activeSessionIndex = 0) }
        }
    }

    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun executeCommand() {
        val session = _uiState.value.activeSession ?: return
        val command = _uiState.value.inputText.trim()
        if (command.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, error = null, inputText = "") }
            val history = (_uiState.value.commandHistory + command).distinct().takeLast(100)
            _uiState.update { it.copy(commandHistory = history, historyIndex = -1) }

            runCommandUseCase(command)
                .onSuccess { cmd ->
                    while (true) {
                        delay(100)
                        runCommandUseCase.getOutput(cmd.id)
                        val currentSession = terminalRepository.activeSession.value
                        if (currentSession?.currentCommand?.status != CommandStatus.RUNNING) break
                    }
                    _uiState.update { it.copy(isRunning = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isRunning = false, error = e.message) }
                }
        }
    }

    fun killCurrentCommand() {
        val cmdId = _uiState.value.activeSession?.currentCommand?.id ?: return
        viewModelScope.launch {
            runCommandUseCase.kill(cmdId)
        }
    }

    fun navigateHistoryUp() {
        val history = _uiState.value.commandHistory
        if (history.isEmpty()) return
        val newIndex = if (_uiState.value.historyIndex < history.size - 1) {
            _uiState.value.historyIndex + 1
        } else _uiState.value.historyIndex
        _uiState.update {
            it.copy(
                historyIndex = newIndex,
                inputText = history[history.size - 1 - newIndex]
            )
        }
    }

    fun navigateHistoryDown() {
        val history = _uiState.value.commandHistory
        if (_uiState.value.historyIndex <= 0) {
            _uiState.update { it.copy(historyIndex = -1, inputText = "") }
            return
        }
        val newIndex = _uiState.value.historyIndex - 1
        _uiState.update {
            it.copy(
                historyIndex = newIndex,
                inputText = history[history.size - 1 - newIndex]
            )
        }
    }

    // Tab management
    fun createNewTab() {
        viewModelScope.launch {
            val tabNumber = _uiState.value.sessions.size + 1
            val session = terminalRepository.createSession("Terminal $tabNumber")
            _uiState.update {
                it.copy(
                    sessions = it.sessions + session,
                    activeSessionIndex = it.sessions.size
                )
            }
        }
    }

    fun switchTab(index: Int) {
        if (index in _uiState.value.sessions.indices) {
            _uiState.update { it.copy(activeSessionIndex = index) }
            terminalRepository.setActiveSession(_uiState.value.sessions[index].id)
        }
    }

    fun closeTab(index: Int) {
        val sessions = _uiState.value.sessions.toMutableList()
        if (sessions.size <= 1) return // Keep at least one tab

        viewModelScope.launch {
            terminalRepository.deleteSession(sessions[index].id)
            sessions.removeAt(index)
            val newIndex = _uiState.value.activeSessionIndex.coerceIn(0, sessions.size - 1)
            _uiState.update {
                it.copy(sessions = sessions, activeSessionIndex = newIndex)
            }
        }
    }

    fun clearTerminal() {
        val session = _uiState.value.activeSession ?: return
        viewModelScope.launch {
            terminalRepository.clearSession(session.id)
        }
    }
}
