package com.openclaude.android.ui.screens.codeviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.text.AnnotatedString
import com.openclaude.android.core.util.SyntaxHighlighter
import com.openclaude.android.data.remote.FileContent
import com.openclaude.android.domain.usecase.ReadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val lines: List<String> = emptyList(),
    val highlightedLines: List<AnnotatedString> = emptyList(),
    val language: String = "text",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fontSize: Float = 14f,
    val showLineNumbers: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentMatchIndex: Int = -1,
    val isSearchActive: Boolean = false,
    val matchCount: Int = 0,
    val isLargeFile: Boolean = false,
    val visibleLineRange: IntRange = 0..100,
    val totalLines: Int = 0,
    val wordWrap: Boolean = false
)

@HiltViewModel
class CodeViewerViewModel @Inject constructor(
    private val readFileUseCase: ReadFileUseCase,
    private val syntaxHighlighter: SyntaxHighlighter
) : ViewModel() {

    companion object {
        const val LARGE_FILE_THRESHOLD = 5000
        const val CHUNK_SIZE = 200
    }

    private val _uiState = MutableStateFlow(CodeViewerUiState())
    val uiState: StateFlow<CodeViewerUiState> = _uiState.asStateFlow()

    fun loadFile(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            readFileUseCase(path)
                .onSuccess { content ->
                    val language = syntaxHighlighter.detectLanguage(content.path.substringAfterLast("/"))
                    val lines = content.content.split("\n")
                    val isLarge = lines.size > LARGE_FILE_THRESHOLD

                    if (isLarge) {
                        // Lazy load: only highlight visible chunk initially
                        val initialChunk = lines.take(CHUNK_SIZE)
                        val highlightedChunk = initialChunk.map { line ->
                            syntaxHighlighter.highlightLine(line, language)
                        }
                        _uiState.update {
                            it.copy(
                                fileContent = content,
                                lines = lines,
                                highlightedLines = highlightedChunk,
                                language = language,
                                isLoading = false,
                                isLargeFile = true,
                                totalLines = lines.size,
                                visibleLineRange = 0 until CHUNK_SIZE
                            )
                        }
                        // Highlight rest in background
                        launch(Dispatchers.Default) {
                            val allHighlighted = lines.map { line ->
                                syntaxHighlighter.highlightLine(line, language)
                            }
                            _uiState.update { it.copy(highlightedLines = allHighlighted) }
                        }
                    } else {
                        val highlighted = syntaxHighlighter.highlight(content.content, language)
                        _uiState.update {
                            it.copy(
                                fileContent = content,
                                highlightedCode = highlighted,
                                lines = lines,
                                highlightedLines = lines.map { line -> syntaxHighlighter.highlightLine(line, language) },
                                language = language,
                                isLoading = false,
                                totalLines = lines.size
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun updateVisibleRange(startLine: Int, endLine: Int) {
        val state = _uiState.value
        if (!state.isLargeFile) return

        val safeStart = startLine.coerceAtLeast(0)
        val safeEnd = endLine.coerceAtMost(state.totalLines)

        // If we need more highlighted lines, generate them
        if (safeEnd > state.highlightedLines.size) {
            viewModelScope.launch(Dispatchers.Default) {
                val language = state.language
                val newLines = state.lines.subList(state.highlightedLines.size, safeEnd).map { line ->
                    syntaxHighlighter.highlightLine(line, language)
                }
                _uiState.update {
                    it.copy(
                        highlightedLines = it.highlightedLines + newLines,
                        visibleLineRange = safeStart until safeEnd
                    )
                }
            }
        } else {
            _uiState.update { it.copy(visibleLineRange = safeStart until safeEnd) }
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
        val lines = _uiState.value.lines
        val results = mutableListOf<SearchResult>()
        lines.forEachIndexed { lineIndex, line ->
            var startIndex = 0
            while (startIndex < line.length) {
                val foundIndex = line.indexOf(query, startIndex, ignoreCase = true)
                if (foundIndex == -1) break
                results.add(SearchResult(lineIndex + 1, foundIndex, foundIndex + query.length, line))
                startIndex = foundIndex + 1
            }
        }
        _uiState.update { it.copy(searchResults = results, currentMatchIndex = if (results.isNotEmpty()) 0 else -1, matchCount = results.size) }
    }

    fun nextMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        _uiState.update { it.copy(currentMatchIndex = (it.currentMatchIndex + 1) % results.size) }
    }

    fun previousMatch() {
        val results = _uiState.value.searchResults
        if (results.isEmpty()) return
        val prevIndex = if (_uiState.value.currentMatchIndex <= 0) results.size - 1 else _uiState.value.currentMatchIndex - 1
        _uiState.update { it.copy(currentMatchIndex = prevIndex) }
    }

    fun increaseFontSize() { _uiState.update { it.copy(fontSize = (it.fontSize + 2f).coerceAtMost(28f)) } }
    fun decreaseFontSize() { _uiState.update { it.copy(fontSize = (it.fontSize - 2f).coerceAtMost(8f)) } }
    fun toggleLineNumbers() { _uiState.update { it.copy(showLineNumbers = !it.showLineNumbers) } }
    fun toggleWordWrap() { _uiState.update { it.copy(wordWrap = !it.wordWrap) } }
}
