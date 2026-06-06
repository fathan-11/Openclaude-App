package com.openclaude.android.ui.screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.*
import com.openclaude.android.data.repository.TerminalRepository
import com.openclaude.android.domain.usecase.RunCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════
// TERMINAL VIEW MODEL
// Manages terminal UI state, command execution, history, tabs
// ═══════════════════════════════════════════════════════════════

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
    val currentDir: String get() = activeSession?.workingDirectory ?: "/"
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val runCommandUseCase: RunCommandUseCase,
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState: StateFlow<TerminalUiState> = _uiState.asStateFlow()

    init {
        // Sync sessions from repository
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
            _uiState.update {
                it.copy(sessions = listOf(session), activeSessionIndex = 0)
            }
        }
    }

    // ── Input ───────────────────────────────────────────────────
    fun updateInput(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    // ── Execute Command ─────────────────────────────────────────
    fun executeCommand() {
        val command = _uiState.value.inputText.trim()
        if (command.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, error = null, inputText = "") }

            // Add to command history
            val history = (_uiState.value.commandHistory + command).distinct().takeLast(100)
            _uiState.update { it.copy(commandHistory = history, historyIndex = -1) }

            // Handle clear command specially
            if (command == "clear") {
                terminalRepository.clearSession()
                _uiState.update { it.copy(isRunning = false) }
                return@launch
            }

            runCommandUseCase(command)
                .onSuccess { cmd ->
                    // Command already executed, just wait for completion
                    while (cmd.status == CommandStatus.RUNNING) {
                        kotlinx.coroutines.delay(50)
                    }
                    _uiState.update { it.copy(isRunning = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isRunning = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    // ── Kill Command ────────────────────────────────────────────
    fun killCurrentCommand() {
        val cmdId = _uiState.value.activeSession?.currentCommand?.id ?: return
        viewModelScope.launch {
            runCommandUseCase.kill(cmdId)
            _uiState.update { it.copy(isRunning = false) }
        }
    }

    // ── History Navigation ──────────────────────────────────────
    fun navigateHistoryUp() {
        val history = _uiState.value.commandHistory
        if (history.isEmpty()) return

        val newIndex = if (_uiState.value.historyIndex < history.size - 1) {
            _uiState.value.historyIndex + 1
        } else {
            _uiState.value.historyIndex
        }

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

    // ── Tab Management ──────────────────────────────────────────
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
        if (sessions.size <= 1) return

        viewModelScope.launch {
            terminalRepository.deleteSession(sessions[index].id)
            sessions.removeAt(index)
            val newIndex = _uiState.value.activeSessionIndex.coerceIn(0, sessions.size - 1)
            _uiState.update {
                it.copy(sessions = sessions, activeSessionIndex = newIndex)
            }
        }
    }

    // ── Clear ───────────────────────────────────────────────────
    fun clearTerminal() {
        val session = _uiState.value.activeSession ?: return
        viewModelScope.launch {
            terminalRepository.clearSession(session.id)
        }
    }

    // ── Dismiss Error ───────────────────────────────────────────
    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
