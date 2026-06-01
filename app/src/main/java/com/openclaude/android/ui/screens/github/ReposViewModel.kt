package com.openclaude.android.ui.screens.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.GitHubRepo
import com.openclaude.android.domain.usecase.LoadReposUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReposUiState(val repos: List<GitHubRepo> = emptyList(), val isLoading: Boolean = false, val error: String? = null)

@HiltViewModel
class ReposViewModel @Inject constructor(private val loadReposUseCase: LoadReposUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ReposUiState())
    val uiState: StateFlow<ReposUiState> = _uiState.asStateFlow()

    init { loadRepos() }

    fun loadRepos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            loadReposUseCase()
                .onSuccess { repos -> _uiState.update { it.copy(repos = repos, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message, isLoading = false) } }
        }
    }
}
