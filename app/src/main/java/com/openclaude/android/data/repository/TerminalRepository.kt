package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.TerminalApiService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalRepository @Inject constructor(
    private val terminalApiService: TerminalApiService
) {
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions: StateFlow<List<TerminalSession>> = _sessions.asStateFlow()

    private val _activeSession = MutableStateFlow<TerminalSession?>(null)
    val activeSession: StateFlow<TerminalSession?> = _activeSession.asStateFlow()

    suspend fun runCommand(command: String, workingDir: String = "/"): Result<TerminalCommand> {
        return try {
            val cmd = TerminalCommand(command = command, workingDirectory = workingDir)
            val result = terminalApiService.runCommand(cmd)

            // Add to active session
            val session = _activeSession.value ?: createSession()
            val lines = session.history + TerminalLine("$ $command", TerminalLineType.INPUT)
            _activeSession.value = session.copy(history = lines, currentCommand = result)

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOutput(commandId: String): Result<List<TerminalLine>> {
        return try {
            val output = terminalApiService.getOutput(commandId)

            // Append to active session
            val session = _activeSession.value
            if (session != null) {
                _activeSession.value = session.copy(history = session.history + output)
            }

            Result.success(output)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun killCommand(commandId: String): Result<Unit> {
        return try {
            terminalApiService.killCommand(commandId)

            val session = _activeSession.value
            if (session?.currentCommand?.id == commandId) {
                _activeSession.value = session.copy(
                    currentCommand = session.currentCommand.copy(status = CommandStatus.KILLED),
                    history = session.history + TerminalLine("Process killed", TerminalLineType.SYSTEM)
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createSession(): TerminalSession {
        val session = TerminalSession()
        _sessions.value = _sessions.value + session
        _activeSession.value = session
        return session
    }

    fun clearSession() {
        _activeSession.value = _activeSession.value?.copy(history = emptyList())
    }
}
