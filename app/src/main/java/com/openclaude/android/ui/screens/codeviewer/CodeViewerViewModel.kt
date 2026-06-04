package com.openclaude.android.ui.screens.codeviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.text.AnnotatedString
import com.openclaude.android.core.util.SyntaxHighlighter
import com.openclaude.android.data.remote.FileContent
import com.openclaude.android.domain.usecase.ReadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val lineNumber: Int,
    val columnStart: Int,
    val columnEnd: Int,
    val lineContent: String
)

data class CodeViewerUiState(
    val fileContent: FileContent? = null,
    val highlightedCode: AnnotatedString? = null,
    val language: String = "text",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fontSize: Float = 14f,
    val showLineNumbers: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentMatchIndex: Int = -1,
    val isSearchActive: Boolean = false,
    val matchCount: Int = 0
)

@HiltViewModel
class CodeViewerViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val syntaxHighlighter: SyntaxHighlighter
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeViewerUiState())
    val uiState: StateFlow<CodeViewerUiState> = _uiState.asStateFlow()

    fun loadFile(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            readFileUseCase(path)
                .onSuccess { content ->
                    val language = syntaxHighlighter.detectLanguage(content.path.substringAfterLast("/"))
                    val highlighted = syntaxHighlighter.highlight(content.content, language)
                    _uiState.update {
                        it.copy(
                            fileContent = content,
                            highlightedCode = highlighted,
                            language = language,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchActive = !it.isSearchActive,
                searchQuery = if (it.isSearchActive) "" else it.searchQuery,
                searchResults = if (it.isSearchActive) emptyList() else it.searchResults,
                currentMatchIndex = -1,
                matchCount = 0
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch(query)
    }

    private fun performSearch(query: String) {
        if (query.isBlank() || _uiState.value.fileContent == null) {
            _uiState.update { it.copy(searchResults = emptyList(), currentMatchIndex = -1, matchCount = 0) }
            return
        }

        val content = _uiState.value.fileContent!!.content
        val lines = content.split("\n")
        val results = mutableListOf<SearchResult>()

        lines.forEachIndexed { lineIndex, line ->
            var startIndex = 0
            while (startIndex < line.length) {
                val foundIndex = line.indexOf(query, startIndex, ignoreCase = true)
                if (foundIndex == -1) break
                results.add(
                    SearchResult(
                        lineNumber = lineIndex + 1,
                        columnStart = foundIndex,
                        columnEnd = foundIndex + query.length,
                        lineContent = line
                    )
                )
                startIndex = foundIndex + 1
            }
        }

        _uiState.update {
            it.copy(
                searchResults = results,
                currentMatchIndex = if (results.isNotEmpty()) 0 else -1,
                matchCount = results.size
            )
        }
    }

    fun nextMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        val nextIndex = (_uiState.value.currentMatchIndex + 1) % results.size
        _uiState.update { it.copy(currentMatchIndex = nextIndex) }
    }

    fun previousMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        val prevIndex = if (_uiState.value.currentMatchIndex <= 0) results.size - 1
        else _uiState.value.currentMatchIndex - 1
        _uiState.update { it.copy(currentMatchIndex = prevIndex) }
    }

    fun increaseFontSize() {
        _uiState.update { it.copy(fontSize = (it.fontSize + 2f).coerceAtMost(28f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontSize = (it.fontSize - 2f).coerceAtMost(8f)) }
    }

    fun toggleLineNumbers() {
        _uiState.update { it.copy(showLineNumbers = !it.showLineNumbers) }
    }
}
