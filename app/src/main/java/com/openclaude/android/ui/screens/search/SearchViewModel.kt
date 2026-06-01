package com.openclaude.android.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.SearchQuery
import com.openclaude.android.data.model.SearchResult
import com.openclaude.android.domain.usecase.SearchCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
    val isRegex: Boolean = false,
    val caseSensitive: Boolean = false,
    val fileFilter: String = "",
    val recentSearches: List<String> = emptyList()
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCodeUseCase: SearchCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private var searchJob: Job? = null

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.length >= 2) {
            debounceSearch()
        } else {
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    fun toggleRegex() {
        _uiState.update { it.copy(isRegex = !it.isRegex) }
        if (_uiState.value.query.length >= 2) search()
    }

    fun toggleCaseSensitive() {
        _uiState.update { it.copy(caseSensitive = !it.caseSensitive) }
        if (_uiState.value.query.length >= 2) search()
    }

    fun setFileFilter(filter: String) {
        _uiState.update { it.copy(fileFilter = filter) }
        if (_uiState.value.query.length >= 2) search()
    }

    private fun debounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            search()
        }
    }

    private fun search() {
        val state = _uiState.value
        if (state.query.length < 2) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, error = null) }
            val query = SearchQuery(
                text = state.query,
                isRegex = state.isRegex,
                caseSensitive = state.caseSensitive,
                fileFilter = state.fileFilter
            )
            searchCodeUseCase(query)
                .onSuccess { results ->
                    val recent = (listOf(state.query) + state.recentSearches).distinct().take(10)
                    _uiState.update { it.copy(results = results, isSearching = false, recentSearches = recent) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isSearching = false) }
                }
        }
    }

    fun clearResults() {
        _uiState.update { it.copy(query = "", results = emptyList()) }
    }
}
