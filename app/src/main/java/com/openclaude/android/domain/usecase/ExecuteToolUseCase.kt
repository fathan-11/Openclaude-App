package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.*
import com.openclaude.android.data.repository.ToolRepository
import javax.inject.Inject

class ExecuteToolUseCase @Inject constructor(
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(type: ToolType, name: String, input: Map<String, Any>): Result<ToolResult> {
        return toolRepository.executeTool(type, name, input)
    }

    suspend fun getHistory(limit: Int = 50): Result<List<ToolExecution>> {
        return toolRepository.getHistory(limit)
    }
}
