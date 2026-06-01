package com.openclaude.android.ui.screens.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.GitHubIssue
import com.openclaude.android.domain.usecase.GetIssuesUseCase
import com.openclaude.android.domain.usecase.CreateIssueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IssuesUiState(val issues: List<GitHubIssue> = emptyList(), val isLoading: Boolean = false, val error: String? = null, val showCreate: Boolean = false)

@HiltViewModel
class IssuesViewModel @Inject constructor(
    private val getIssuesUseCase: GetIssuesUseCase,
    private val createIssueUseCase: CreateIssueUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(IssuesUiState())
    val uiState: StateFlow<IssuesUiState> = _uiState.asStateFlow()

    fun loadIssues(owner: String, repo: String, state: String = "open") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getIssuesUseCase(owner, repo, state)
                .onSuccess { issues -> _uiState.update { it.copy(issues = issues, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }

    fun createIssue(owner: String, repo: String, title: String, body: String) {
        viewModelScope.launch {
            createIssueUseCase(owner, repo, title, body)
                .onSuccess { _uiState.update { it.copy(showCreate = false) }; loadIssues(owner, repo) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun toggleCreate() { _uiState.update { it.copy(showCreate = !it.showCreate) } }
}
