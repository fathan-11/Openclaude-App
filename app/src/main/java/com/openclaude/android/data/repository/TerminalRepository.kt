package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.LocalTerminalEngine
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════
// TERMINAL REPOSITORY
// Local-only terminal session management. No remote API.
// ═══════════════════════════════════════════════════════════════

@Singleton
class TerminalRepository @Inject constructor(
    private val engine: LocalTerminalEngine
) {
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions: StateFlow<List<TerminalSession>> = _sessions.asStateFlow()

    private val _activeSession = MutableStateFlow<TerminalSession?>(null)
    val activeSession: StateFlow<TerminalSession?> = _activeSession.asStateFlow()

    val currentDir: StateFlow<String> = engine.currentDir

    // ── Run Command ─────────────────────────────────────────────
    suspend fun runCommand(command: String, workingDir: String = engine.currentDir.value): Result<TerminalCommand> {
        return try {
            val cmd = TerminalCommand(command = command, workingDirectory = workingDir)

            // Add input line to session
            val session = _activeSession.value ?: createSession()
            val inputLine = TerminalLine("$ $command", TerminalLineType.INPUT)
            _activeSession.value = session.copy(
                history = session.history + inputLine,
                currentCommand = cmd
            )

            // Execute command locally
            val result = engine.execute(command, workingDir)

            // Handle working directory change
            if (result.newWorkingDir != null) {
                engine.updateDir(result.newWorkingDir)
            }

            // Determine line type based on exit code and content
            val lineType = when {
                result.exitCode != 0 -> TerminalLineType.ERROR
                result.type == TerminalLineType.SYSTEM -> TerminalLineType.SYSTEM
                result.type == TerminalLineType.INFO -> TerminalLineType.INFO
                result.type == TerminalLineType.WARNING -> TerminalLineType.WARNING
                result.output.any { it.contains("\u001B[2J") } -> TerminalLineType.SYSTEM // clear command
                else -> TerminalLineType.OUTPUT
            }

            // Add output lines to session
            val outputLines = result.output.map { TerminalLine(it, lineType) }
            val completedCmd = cmd.copy(
                completedAt = System.currentTimeMillis(),
                exitCode = result.exitCode,
                status = if (result.exitCode == 0) CommandStatus.COMPLETED else CommandStatus.FAILED
            )

            val currentSession = _activeSession.value ?: session
            _activeSession.value = currentSession.copy(
                history = currentSession.history + outputLines,
                currentCommand = completedCmd
            )

            Result.success(completedCmd)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Get Output (for polling compatibility) ──────────────────
    suspend fun getOutput(commandId: String): Result<List<TerminalLine>> {
        val session = _activeSession.value ?: return Result.success(emptyList())
        val cmd = session.currentCommand ?: return Result.success(emptyList())

        if (cmd.id == commandId && cmd.status != CommandStatus.RUNNING) {
            return Result.success(session.history.filter { it.type != TerminalLineType.INPUT })
        }

        return Result.success(emptyList())
    }

    // ── Kill Command ────────────────────────────────────────────
    suspend fun killCommand(commandId: String): Result<Unit> {
        val session = _activeSession.value
        if (session?.currentCommand?.id == commandId) {
            _activeSession.value = session.copy(
                currentCommand = session.currentCommand.copy(status = CommandStatus.KILLED),
                history = session.history + TerminalLine("Process killed", TerminalLineType.SYSTEM)
            )
        }
        return Result.success(Unit)
    }

    // ── Session Management ──────────────────────────────────────
    fun createSession(name: String = "Terminal"): TerminalSession {
        val session = TerminalSession(
            name = name,
            workingDirectory = engine.currentDir.value,
            history = listOf(
                TerminalLine("Welcome to the Terminal. Type 'help' for a list of commands.", TerminalLineType.INFO)
            )
        )
        _sessions.value = _sessions.value + session
        _activeSession.value = session
        return session
    }

    fun setActiveSession(sessionId: String) {
        val session = _sessions.value.find { it.id == sessionId } ?: return
        _activeSession.value = session
        engine.updateDir(session.workingDirectory)
    }

    fun deleteSession(sessionId: String) {
        _sessions.value = _sessions.value.filter { it.id != sessionId }
        if (_activeSession.value?.id == sessionId) {
            _activeSession.value = _sessions.value.firstOrNull()
        }
    }

    fun clearSession(sessionId: String? = null) {
        val targetId = sessionId ?: _activeSession.value?.id ?: return
        val session = _sessions.value.find { it.id == targetId }
        if (session != null) {
            val updated = session.copy(history = emptyList())
            val idx = _sessions.value.indexOf(session)
            val sessions = _sessions.value.toMutableList()
            sessions[idx] = updated
            _sessions.value = sessions
            if (_activeSession.value?.id == targetId) {
                _activeSession.value = updated
            }
        }
    }
}
