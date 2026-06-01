package com.openclaude.android.ui.screens.advanced

import androidx.lifecycle.ViewModel
import com.openclaude.android.data.model.MemoryEntry
import com.openclaude.android.domain.usecase.MemoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MemoryUiState(val entries: List<MemoryEntry> = emptyList(), val categories: List<String> = emptyList(), val selectedCategory: String = "all", val showAdd: Boolean = false)

@HiltViewModel
class MemoryViewModel @Inject constructor(private val memoryUseCase: MemoryUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(MemoryUiState())
    val uiState: StateFlow<MemoryUiState> = _uiState.asStateFlow()

    init { _uiState.update { it.copy(categories = memoryUseCase.getCategories()) } }

    fun addEntry(key: String, value: String, category: String) {
        val entry = MemoryEntry(key = key, value = value, category = category)
        _uiState.update { it.copy(entries = it.entries + entry, showAdd = false) }
    }

    fun removeEntry(id: String) { _uiState.update { it.copy(entries = it.entries.filter { e -> e.id != id }) } }
    fun filterCategory(cat: String) { _uiState.update { it.copy(selectedCategory = cat) } }
    fun toggleAdd() { _uiState.update { it.copy(showAdd = !it.showAdd) } }
}
