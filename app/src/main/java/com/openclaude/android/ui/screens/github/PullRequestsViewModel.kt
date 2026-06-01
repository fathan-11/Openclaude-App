package com.openclaude.android.ui.screens.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.GitHubPR
import com.openclaude.android.domain.usecase.GetPullRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PRsUiState(val prs: List<GitHubPR> = emptyList(), val isLoading: Boolean = false, val error: String? = null, val filter: String = "open")

@HiltViewModel
class PullRequestsViewModel @Inject constructor(private val getPRsUseCase: GetPullRequestsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(PRsUiState())
    val uiState: StateFlow<PRsUiState> = _uiState.asStateFlow()

    fun loadPRs(owner: String, repo: String, state: String = "open") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, filter = state) }
            getPRsUseCase(owner, repo, state)
                .onSuccess { prs -> _uiState.update { it.copy(prs = prs, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }
}
