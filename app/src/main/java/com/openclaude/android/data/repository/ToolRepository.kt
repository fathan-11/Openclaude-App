package com.openclaude.android.data.repository

import com.openclaude.android.data.model.*
import com.openclaude.android.data.remote.ToolApiService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolRepository @Inject constructor(
    private val toolApiService: ToolApiService
) {
    private val _executions = MutableStateFlow<List<ToolExecution>>(emptyList())
    val executions: StateFlow<List<ToolExecution>> = _executions.asStateFlow()

    suspend fun executeTool(type: ToolType, name: String, input: Map<String, Any>): Result<ToolResult> {
        return try {
            val execution = ToolExecution(type = type, name = name, input = input)
            _executions.value = _executions.value + execution

            val result = toolApiService.executeTool(execution)

            // Update status
            _executions.value = _executions.value.map {
                if (it.id == execution.id) it.copy(status = ToolStatus.COMPLETED, completedAt = System.currentTimeMillis())
                else it
            }

            Result.success(result)
        } catch (e: Exception) {
            _executions.value = _executions.value.map {
                if (it.id == it.id) it.copy(status = ToolStatus.FAILED)
                else it
            }
            Result.failure(e)
        }
    }

    suspend fun getHistory(limit: Int = 50): Result<List<ToolExecution>> {
        return try {
            val history = toolApiService.getHistory(limit)
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
