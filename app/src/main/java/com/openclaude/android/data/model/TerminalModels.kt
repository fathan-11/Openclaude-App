package com.openclaude.android.data.model

enum class CommandStatus { RUNNING, COMPLETED, FAILED, KILLED }

data class TerminalCommand(
    val id: String = java.util.UUID.randomUUID().toString(),
    val command: String,
    val workingDirectory: String = "/",
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val exitCode: Int? = null,
    val status: CommandStatus = CommandStatus.RUNNING
)

data class TerminalLine(
    val content: String,
    val type: TerminalLineType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TerminalLineType { INPUT, OUTPUT, ERROR, SYSTEM, SUCCESS }

data class TerminalSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Terminal",
    val workingDirectory: String = "/",
    val history: List<TerminalLine> = emptyList(),
    val currentCommand: TerminalCommand? = null,
    val isActive: Boolean = true
)
