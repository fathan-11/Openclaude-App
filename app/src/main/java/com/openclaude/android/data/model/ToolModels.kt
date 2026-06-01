package com.openclaude.android.data.model

enum class ToolType { BASH, FILE_READ, FILE_WRITE, FILE_EDIT, SEARCH, WEB_SEARCH, WEB_FETCH, MCP, AGENT, CUSTOM }

enum class ToolStatus { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED }

data class ToolExecution(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: ToolType,
    val name: String,
    val input: Map<String, Any> = emptyMap(),
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val status: ToolStatus = ToolStatus.PENDING
)

data class ToolResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val executionId: String,
    val output: String,
    val isError: Boolean = false,
    val metadata: Map<String, Any> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
