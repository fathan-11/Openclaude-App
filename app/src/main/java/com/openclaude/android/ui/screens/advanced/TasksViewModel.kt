package com.openclaude.android.ui.screens.advanced

import androidx.lifecycle.ViewModel
import com.openclaude.android.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TasksUiState(val tasks: List<AgentTask> = emptyList(), val selectedTask: AgentTask? = null, val showCreate: Boolean = false)

@HiltViewModel
class TasksViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun createTask(title: String, description: String) {
        val task = AgentTask(title = title, description = description)
        _uiState.update { it.copy(tasks = it.tasks + task, showCreate = false) }
    }

    fun selectTask(task: AgentTask) { _uiState.update { it.copy(selectedTask = task) } }
    fun cancelTask(id: String) { _uiState.update { it.copy(tasks = it.tasks.map { t -> if (t.id == id) t.copy(status = AgentTaskStatus.CANCELLED) else t }) } }
    fun toggleCreate() { _uiState.update { it.copy(showCreate = !it.showCreate) } }
}
