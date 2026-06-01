package com.openclaude.android.data.model

enum class AgentTaskStatus { QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED }

data class AgentTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val status: AgentTaskStatus = AgentTaskStatus.QUEUED,
    val progress: Float = 0f,
    val steps: List<TaskStep> = emptyList(),
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val result: String? = null,
    val error: String? = null
)

data class TaskStep(
    val id: String = java.util.UUID.randomUUID().toString(),
    val description: String,
    val status: AgentTaskStatus = AgentTaskStatus.QUEUED,
    val output: String = ""
)
