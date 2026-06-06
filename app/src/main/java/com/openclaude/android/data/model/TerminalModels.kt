package com.openclaude.android.data.model

// ═══════════════════════════════════════════════════════════════
// TERMINAL DATA MODELS
// Clean data classes for terminal emulator
// ═══════════════════════════════════════════════════════════════

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

enum class TerminalLineType {
    INPUT,      // User command input (green prompt)
    OUTPUT,     // Command output (white)
    ERROR,      // Error output (red)
    SYSTEM,     // System messages (gray)
    SUCCESS,    // Success messages (green)
    INFO,       // Informational (blue/cyan)
    WARNING     // Warning messages (amber)
}

data class TerminalSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "Terminal",
    val workingDirectory: String = "/",
    val history: List<TerminalLine> = emptyList(),
    val currentCommand: TerminalCommand? = null,
    val isActive: Boolean = true
)

// ═══════════════════════════════════════════════════════════════
// BUILT-IN COMMAND DEFINITIONS
// ═══════════════════════════════════════════════════════════════

data class BuiltInCommand(
    val name: String,
    val description: String,
    val usage: String,
    val handler: (args: List<String>, workingDir: String) -> CommandResult
)

data class CommandResult(
    val output: List<String>,
    val exitCode: Int = 0,
    val newWorkingDir: String? = null,
    val type: TerminalLineType = TerminalLineType.OUTPUT
) {
    companion object {
        fun success(output: List<String>) = CommandResult(output, exitCode = 0, type = TerminalLineType.OUTPUT)
        fun error(message: String) = CommandResult(listOf(message), exitCode = 1, type = TerminalLineType.ERROR)
        fun info(message: String) = CommandResult(listOf(message), exitCode = 0, type = TerminalLineType.INFO)
        fun system(message: String) = CommandResult(listOf(message), exitCode = 0, type = TerminalLineType.SYSTEM)
    }
}
