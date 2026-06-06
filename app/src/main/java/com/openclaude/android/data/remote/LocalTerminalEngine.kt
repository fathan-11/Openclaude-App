package com.openclaude.android.data.remote

import com.openclaude.android.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

// ═══════════════════════════════════════════════════════════════
// LOCAL TERMINAL ENGINE
// Executes commands locally on the Android device using
// ProcessBuilder for real system commands + built-in commands.
// ═══════════════════════════════════════════════════════════════

@Singleton
class LocalTerminalEngine @Inject constructor() {

    private val _currentDir = MutableStateFlow(System.getProperty("user.dir") ?: "/")
    val currentDir: StateFlow<String> = _currentDir.asStateFlow()

    // ── Built-in Command Registry ──────────────────────────────
    private val builtInCommands = mapOf(
        "help" to BuiltInCommand(
            name = "help",
            description = "Show available commands",
            usage = "help [command]"
        ) { args, _ ->
            if (args.isNotEmpty()) {
                val cmd = builtInCommands[args[0]]
                if (cmd != null) {
                    CommandResult.success(listOf(
                        "Command: ${cmd.name}",
                        "Usage:   ${cmd.usage}",
                        "Info:    ${cmd.description}"
                    ))
                } else {
                    CommandResult.error("Unknown command: ${args[0]}. Type 'help' for a list of commands.")
                }
            } else {
                val lines = mutableListOf(
                    "Available commands:",
                    ""
                )
                builtInCommands.values.sortedBy { it.name }.forEach { cmd ->
                    lines.add("  ${cmd.name.padEnd(12)} ${cmd.description}")
                }
                lines.add("")
                lines.add("Type 'help <command>' for more info on a specific command.")
                lines.add("System commands (ls, cat, etc.) are also supported.")
                CommandResult.success(lines)
            }
        },

        "clear" to BuiltInCommand(
            name = "clear",
            description = "Clear terminal output",
            usage = "clear"
        ) { _, _ ->
            CommandResult(listOf("\u001B[2J\u001B[0H"), exitCode = 0, type = TerminalLineType.SYSTEM)
        },

        "echo" to BuiltInCommand(
            name = "echo",
            description = "Print text to terminal",
            usage = "echo <text>"
        ) { args, _ ->
            CommandResult.success(listOf(args.joinToString(" ")))
        },

        "pwd" to BuiltInCommand(
            name = "pwd",
            description = "Print working directory",
            usage = "pwd"
        ) { _, workingDir ->
            CommandResult.success(listOf(workingDir))
        },

        "cd" to BuiltInCommand(
            name = "cd",
            description = "Change directory",
            usage = "cd <path>"
        ) { args, workingDir ->
            val target = if (args.isEmpty() || args[0] == "~") {
                System.getProperty("user.home") ?: "/data/data"
            } else if (args[0] == "..") {
                File(workingDir).parent ?: "/"
            } else if (args[0].startsWith("/")) {
                args[0]
            } else {
                "$workingDir/${args[0]}"
            }

            val file = File(target)
            if (file.exists() && file.isDirectory) {
                CommandResult(
                    output = emptyList(),
                    exitCode = 0,
                    newWorkingDir = file.canonicalPath
                )
            } else {
                CommandResult.error("cd: no such file or directory: $target")
            }
        },

        "ls" to BuiltInCommand(
            name = "ls",
            description = "List directory contents",
            usage = "ls [-la] [path]"
        ) { args, workingDir ->
            val showHidden = args.contains("-a") || args.contains("-la") || args.contains("-al")
            val showLong = args.contains("-l") || args.contains("-la") || args.contains("-al")
            val path = args.firstOrNull { !it.startsWith("-") } ?: workingDir

            val file = File(path)
            if (!file.exists()) {
                CommandResult.error("ls: cannot access '$path': No such file or directory")
            } else if (!file.isDirectory) {
                CommandResult.success(listOf(file.name))
            } else {
                val files = file.listFiles()?.let { list ->
                    if (showHidden) list.sortedBy { it.name }
                    else list.filter { !it.name.startsWith(".") }.sortedBy { it.name }
                } ?: emptyList()

                if (showLong) {
                    val lines = mutableListOf("total ${files.size}")
                    files.forEach { f ->
                        val perms = if (f.isDirectory) "drwxr-xr-x" else "-rw-r--r--"
                        val size = if (f.isDirectory) "4096" else f.length().toString()
                        val date = java.text.SimpleDateFormat("MMM dd HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(f.lastModified()))
                        val name = if (f.isDirectory) "${f.name}/" else f.name
                        lines.add("$perms  1 user user  ${size.padStart(6)}  $date  $name")
                    }
                    CommandResult.success(lines)
                } else {
                    val names = files.map { if (it.isDirectory) "${it.name}/" else it.name }
                    CommandResult.success(listOf(names.joinToString("  ")))
                }
            }
        },

        "cat" to BuiltInCommand(
            name = "cat",
            description = "Display file contents",
            usage = "cat <file>"
        ) { args, _ ->
            if (args.isEmpty()) {
                CommandResult.error("cat: missing file operand")
            } else {
                val file = File(args[0])
                if (!file.exists()) {
                    CommandResult.error("cat: ${args[0]}: No such file or directory")
                } else if (file.isDirectory) {
                    CommandResult.error("cat: ${args[0]}: Is a directory")
                } else {
                    try {
                        val content = file.readText()
                        CommandResult.success(content.lines())
                    } catch (e: Exception) {
                        CommandResult.error("cat: ${args[0]}: Permission denied")
                    }
                }
            }
        },

        "mkdir" to BuiltInCommand(
            name = "mkdir",
            description = "Create a directory",
            usage = "mkdir <directory>"
        ) { args, workingDir ->
            if (args.isEmpty()) {
                CommandResult.error("mkdir: missing operand")
            } else {
                val dir = if (args[0].startsWith("/")) File(args[0]) else File(workingDir, args[0])
                if (dir.exists()) {
                    CommandResult.error("mkdir: cannot create directory '${args[0]}': File exists")
                } else {
                    if (dir.mkdirs()) {
                        CommandResult.success(emptyList())
                    } else {
                        CommandResult.error("mkdir: cannot create directory '${args[0]}'")
                    }
                }
            }
        },

        "touch" to BuiltInCommand(
            name = "touch",
            description = "Create an empty file",
            usage = "touch <file>"
        ) { args, workingDir ->
            if (args.isEmpty()) {
                CommandResult.error("touch: missing file operand")
            } else {
                val file = if (args[0].startsWith("/")) File(args[0]) else File(workingDir, args[0])
                try {
                    file.createNewFile()
                    CommandResult.success(emptyList())
                } catch (e: Exception) {
                    CommandResult.error("touch: cannot create file '${args[0]}': Permission denied")
                }
            }
        },

        "rm" to BuiltInCommand(
            name = "rm",
            description = "Remove files or directories",
            usage = "rm [-r] <path>"
        ) { args, workingDir ->
            val recursive = args.contains("-r") || args.contains("-rf") || args.contains("-fr")
            val path = args.firstOrNull { !it.startsWith("-") }
            if (path == null) {
                CommandResult.error("rm: missing operand")
            } else {
                val file = if (path.startsWith("/")) File(path) else File(workingDir, path)
                if (!file.exists()) {
                    CommandResult.error("rm: cannot remove '${path}': No such file or directory")
                } else if (file.isDirectory && !recursive) {
                    CommandResult.error("rm: cannot remove '${path}': Is a directory (use -r)")
                } else {
                    if (file.deleteRecursively()) {
                        CommandResult.success(emptyList())
                    } else {
                        CommandResult.error("rm: cannot remove '${path}': Permission denied")
                    }
                }
            }
        },

        "whoami" to BuiltInCommand(
            name = "whoami",
            description = "Print current user",
            usage = "whoami"
        ) { _, _ ->
            CommandResult.success(listOf(android.os.Build.USER ?: "user"))
        },

        "date" to BuiltInCommand(
            name = "date",
            description = "Print current date and time",
            usage = "date"
        ) { _, _ ->
            CommandResult.success(listOf(java.text.SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy",
                java.util.Locale.getDefault()
            ).format(java.util.Date())))
        },

        "uname" to BuiltInCommand(
            name = "uname",
            description = "Print system information",
            usage = "uname [-a]"
        ) { args, _ ->
            if (args.contains("-a")) {
                CommandResult.success(listOf(
                    "Linux ${android.os.Build.HOST ?: "localhost"} " +
                    "${android.os.Build.VERSION.RELEASE} " +
                    "#1 SMP ${android.os.Build.BOARD} " +
                    "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                ))
            } else {
                CommandResult.success(listOf("Linux"))
            }
        },

        "df" to BuiltInCommand(
            name = "df",
            description = "Show disk space usage",
            usage = "df [-h]"
        ) { args, _ ->
            val humanReadable = args.contains("-h")
            try {
                val stat = android.os.StatFs(System.getProperty("user.dir") ?: "/")
                val totalBytes = stat.totalBytes
                val freeBytes = stat.freeBytes
                val usedBytes = totalBytes - freeBytes

                fun formatSize(bytes: Long): String {
                    if (humanReadable) {
                        return when {
                            bytes >= 1_073_741_824 -> "${bytes / 1_073_741_824}G"
                            bytes >= 1_048_576 -> "${bytes / 1_048_576}M"
                            bytes >= 1024 -> "${bytes / 1024}K"
                            else -> "${bytes}B"
                        }
                    }
                    return bytes.toString()
                }

                CommandResult.success(listOf(
                    "Filesystem     ${if (humanReadable) "Size" else "1K-blocks"}  ${if (humanReadable) "Used" else "Used"}  ${if (humanReadable) "Avail" else "Available"}  Use%  Mounted on",
                    "/dev/block     ${formatSize(totalBytes).padStart(6)}  ${formatSize(usedBytes).padStart(6)}  ${formatSize(freeBytes).padStart(6)}  ${((usedBytes.toFloat() / totalBytes) * 100).toInt()}%  /"
                ))
            } catch (e: Exception) {
                CommandResult.error("df: ${e.message}")
            }
        },

        "env" to BuiltInCommand(
            name = "env",
            description = "Print environment variables",
            usage = "env"
        ) { _, _ ->
            val envVars = listOf(
                "HOME=${System.getProperty("user.home") ?: "/data/data"}",
                "USER=${android.os.Build.USER ?: "user"}",
                "SHELL=/system/bin/sh",
                "LANG=en_US.UTF-8",
                "PATH=/system/bin:/system/xbin",
                "ANDROID_SDK=${android.os.Build.VERSION.SDK_INT}",
                "ANDROID_VERSION=${android.os.Build.VERSION.RELEASE}",
                "DEVICE=${android.os.Build.MODEL}",
                "MANUFACTURER=${android.os.Build.MANUFACTURER}",
                "HOSTNAME=${android.os.Build.HOST ?: "localhost"}"
            )
            CommandResult.success(envVars)
        },

        "ps" to BuiltInCommand(
            name = "ps",
            description = "List running processes",
            usage = "ps"
        ) { _, _ ->
            try {
                val process = ProcessBuilder(arrayOf("ps"))
                    .redirectErrorStream(true)
                    .start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val lines = reader.readLines()
                process.waitFor()
                CommandResult.success(lines)
            } catch (e: Exception) {
                CommandResult.error("ps: ${e.message}")
            }
        },

        "top" to BuiltInCommand(
            name = "top",
            description = "Show top processes by CPU usage",
            usage = "top"
        ) { _, _ ->
            try {
                val process = ProcessBuilder(arrayOf("top", "-n", "1", "-b"))
                    .redirectErrorStream(true)
                    .start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val lines = reader.readLines().take(20) // Limit output
                process.waitFor()
                CommandResult.success(lines)
            } catch (e: Exception) {
                // Fallback: show basic process info
                CommandResult.success(listOf(
                    "PID    USER     CPU%   MEM%   COMMAND",
                    "------ -------- ------ ------ -------",
                    "${android.os.Process.myPid()}  ${android.os.Build.USER ?: "user"}  0.0    0.0    com.openclaude.android"
                ))
            }
        },

        "exit" to BuiltInCommand(
            name = "exit",
            description = "Exit terminal session",
            usage = "exit"
        ) { _, _ ->
            CommandResult.system("Goodbye.")
        }
    )

    // ── Command Execution ───────────────────────────────────────
    suspend fun execute(command: String, workingDir: String): CommandResult {
        return withContext(Dispatchers.IO) {
            val parts = parseCommand(command)
            if (parts.isEmpty()) {
                return@withContext CommandResult.success(emptyList())
            }

            val cmd = parts[0]
            val args = parts.drop(1)

            // Check built-in commands first
            val builtIn = builtInCommands[cmd]
            if (builtIn != null) {
                return@withContext builtIn.handler(args, workingDir)
            }

            // Execute as system command
            executeSystemCommand(parts, workingDir)
        }
    }

    // ── System Command Execution ────────────────────────────────
    private fun executeSystemCommand(parts: List<String>, workingDir: String): CommandResult {
        return try {
            val processBuilder = ProcessBuilder(parts)
                .directory(File(workingDir))
                .redirectErrorStream(true)

            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val output = mutableListOf<String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.add(line!!)
            }

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                CommandResult.success(output)
            } else {
                CommandResult(output, exitCode = exitCode, type = TerminalLineType.ERROR)
            }
        } catch (e: java.io.IOException) {
            CommandResult.error("${parts[0]}: command not found")
        } catch (e: Exception) {
            CommandResult.error("${parts[0]}: ${e.message}")
        }
    }

    // ── Command Parsing ─────────────────────────────────────────
    private fun parseCommand(command: String): List<String> {
        val parts = mutableListOf<String>()
        var current = StringBuilder()
        var inSingleQuote = false
        var inDoubleQuote = false
        var escapeNext = false

        for (char in command) {
            when {
                escapeNext -> {
                    current.append(char)
                    escapeNext = false
                }
                char == '\\' && !inSingleQuote -> escapeNext = true
                char == '\'' && !inDoubleQuote -> inSingleQuote = !inSingleQuote
                char == '"' && !inSingleQuote -> inDoubleQuote = !inDoubleQuote
                char.isWhitespace() && !inSingleQuote && !inDoubleQuote -> {
                    if (current.isNotEmpty()) {
                        parts.add(current.toString())
                        current = StringBuilder()
                    }
                }
                else -> current.append(char)
            }
        }
        if (current.isNotEmpty()) {
            parts.add(current.toString())
        }
        return parts
    }

    fun updateDir(newDir: String) {
        _currentDir.value = newDir
    }
}
