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

data class CodeViewerUiState(
    val fileContent: FileContent? = null,
    val highlightedCode: AnnotatedString? = null,
    val language: String = "text",
    val isLoading: Boolean = false,
    val error: String? = null,
    val fontSize: Float = 14f,
    val showLineNumbers: Boolean = true
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
