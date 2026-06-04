package com.openclaude.android.ui.screens.codeviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.graphics.Color
import com.openclaude.android.core.util.SyntaxHighlighter
import com.openclaude.android.data.remote.FileContent
import com.openclaude.android.domain.usecase.ReadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchResult(
    val startIndex: Int,
    val endIndex: Int,
    val lineNumber: Int,
)

data class CodeViewerUiState(
    val fileContent: FileContent? = null,
    val highlightedCode: AnnotatedString? = null,
    val language: String = "text",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fontSize: Float = 14f,
    val showLineNumbers: Boolean = true,
    // Search state
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentMatchIndex: Int = -1,
    val matchCount: Int = 0,
    // Replace state
    val isReplaceVisible: Boolean = false,
    val replaceQuery: String = "",
)

@HiltViewModel
class CodeViewerViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val syntaxHighlighter: SyntaxHighlighter
) : ViewModel() {

    private val _uiState = MutableStateFlow(CodeViewerUiState())
    val uiState: StateFlow<CodeViewerUiState> = _uiState.asStateFlow()

    private var rawContent: String = ""

    fun loadFile(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            readFileUseCase(path)
                .onSuccess { content ->
                    rawContent = content.content
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

    fun increaseFontSize() {
        _uiState.update { it.copy(fontSize = (it.fontSize + 2f).coerceAtMost(28f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontSize = (it.fontSize - 2f).coerceAtLeast(8f)) }
    }

    fun toggleLineNumbers() {
        _uiState.update { it.copy(showLineNumbers = !it.showLineNumbers) }
    }

    // Search functionality
    fun toggleSearch() {
        val isVisible = !_uiState.value.isSearchVisible
        if (!isVisible) {
            clearSearch()
        }
        _uiState.update { it.copy(isSearchVisible = isVisible) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch(query)
    }

    private fun performSearch(query: String) {
        if (query.isEmpty() || rawContent.isEmpty()) {
            _uiState.update { it.copy(searchResults = emptyList(), currentMatchIndex = -1, matchCount = 0) }
            updateHighlightedCode()
            return
        }

        val results = mutableListOf<SearchResult>()
        val lowerContent = rawContent.lowercase()
        val lowerQuery = query.lowercase()
        var index = 0
        var lineNumber = 1

        while (index < rawContent.length) {
            val foundIndex = lowerContent.indexOf(lowerQuery, index)
            if (foundIndex == -1) break

            // Count line number
            while (index <= foundIndex && index < rawContent.length) {
                if (rawContent[index] == '\n') lineNumber++
                index++
            }

            results.add(SearchResult(
                startIndex = foundIndex,
                endIndex = foundIndex + query.length,
                lineNumber = lineNumber
            ))
            index = foundIndex + 1
        }

        val currentMatch = if (results.isNotEmpty()) 0 else -1
        _uiState.update { 
            it.copy(
                searchResults = results, 
                currentMatchIndex = currentMatch,
                matchCount = results.size
            )
        }
        updateHighlightedCode()
    }

    fun nextMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        val nextIndex = (_uiState.value.currentMatchIndex + 1) % results.size
        _uiState.update { it.copy(currentMatchIndex = nextIndex) }
        updateHighlightedCode()
    }

    fun previousMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        val prevIndex = if (_uiState.value.currentMatchIndex <= 0) results.size - 1 else _uiState.value.currentMatchIndex - 1
        _uiState.update { it.copy(currentMatchIndex = prevIndex) }
        updateHighlightedCode()
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), currentMatchIndex = -1, matchCount = 0) }
        updateHighlightedCode()
    }

    fun toggleReplace() {
        _uiState.update { it.copy(isReplaceVisible = !it.isReplaceVisible) }
    }

    fun updateReplaceQuery(query: String) {
        _uiState.update { it.copy(replaceQuery = query) }
    }

    fun replaceCurrent() {
        val state = _uiState.value
        if (state.searchResults.isEmpty() || state.currentMatchIndex < 0) return
        val result = state.searchResults[state.currentMatchIndex]
        val replaceWith = state.replaceQuery
        rawContent = rawContent.substring(0, result.startIndex) + replaceWith + rawContent.substring(result.endIndex)
        performSearch(state.searchQuery)
    }

    fun replaceAll() {
        val state = _uiState.value
        if (state.searchResults.isEmpty()) return
        val query = state.searchQuery
        val replaceWith = state.replaceQuery
        rawContent = rawContent.replace(query, replaceWith, ignoreCase = true)
        performSearch(state.searchQuery)
    }

    private fun updateHighlightedCode() {
        val content = rawContent
        if (content.isEmpty()) return

        val query = _uiState.value.searchQuery
        val results = _uiState.value.searchResults
        val currentIdx = _uiState.value.currentMatchIndex

        val language = _uiState.value.language
        val baseHighlighted = syntaxHighlighter.highlight(content, language)

        if (query.isEmpty() || results.isEmpty()) {
            _uiState.update { it.copy(highlightedCode = baseHighlighted) }
            return
        }

        // Build annotated string with search highlights
        val highlighted = buildAnnotatedString {
            append(baseHighlighted)
            
            // Apply search result highlights
            results.forEachIndexed { index, result ->
                val style = if (index == currentIdx) {
                    SpanStyle(background = Color(0xFFFFB86C).copy(alpha = 0.5f)) // Current match - orange
                } else {
                    SpanStyle(background = Color(0xFFF1FA8C).copy(alpha = 0.3f)) // Other matches - yellow
                }
                try {
                    addStyle(style, result.startIndex, result.endIndex)
                } catch (_: Exception) {
                    // Ignore if indices are out of bounds
                }
            }
        }

        _uiState.update { it.copy(highlightedCode = highlighted) }
    }

    fun getCurrentMatchLine(): Int? {
        val results = _uiState.value.searchResults
        val index = _uiState.value.currentMatchIndex
        return if (index in results.indices) results[index].lineNumber else null
    }
}
