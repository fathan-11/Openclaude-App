package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.TerminalCommand
import com.openclaude.android.data.model.TerminalLine
import com.openclaude.android.data.repository.TerminalRepository
import javax.inject.Inject

class RunCommandUseCase @Inject constructor(
    private val terminalRepository: TerminalRepository
) {
    suspend operator fun invoke(command: String, workingDir: String = terminalRepository.currentDir.value): Result<TerminalCommand> {
        return terminalRepository.runCommand(command, workingDir)
    }

    suspend fun getOutput(commandId: String): Result<List<TerminalLine>> {
        return terminalRepository.getOutput(commandId)
    }

    suspend fun kill(commandId: String): Result<Unit> {
        return terminalRepository.killCommand(commandId)
    }
}
