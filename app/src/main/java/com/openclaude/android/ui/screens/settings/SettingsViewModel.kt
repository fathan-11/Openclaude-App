package com.openclaude.android.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclaude.android.data.model.Provider
import com.openclaude.android.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val currentProvider: Provider = Provider.OPENAI,
    val apiKey: String = "",
    val baseUrl: String = "",
    val selectedModel: String = "",
    val isDarkMode: Boolean = true,
    val saved: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.selectedProvider,
                settingsRepository.selectedModel,
                settingsRepository.themeMode
            ) { provider, model, theme ->
                SettingsUiState(
                    currentProvider = provider,
                    selectedModel = model,
                    isDarkMode = theme == "dark"
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setProvider(provider: Provider) {
        viewModelScope.launch {
            settingsRepository.setSelectedProvider(provider)
            _uiState.update { it.copy(currentProvider = provider) }
        }
    }

    fun setApiKey(key: String) {
        _uiState.update { it.copy(apiKey = key) }
    }

    fun setBaseUrl(url: String) {
        _uiState.update { it.copy(baseUrl = url) }
    }

    fun setModel(model: String) {
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(if (enabled) "dark" else "light")
            _uiState.update { it.copy(isDarkMode = enabled) }
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            settingsRepository.setSelectedProvider(state.currentProvider)
            settingsRepository.setSelectedModel(state.selectedModel)
            settingsRepository.setThemeMode(if (state.isDarkMode) "dark" else "light")
            _uiState.update { it.copy(saved = true) }
        }
    }
}