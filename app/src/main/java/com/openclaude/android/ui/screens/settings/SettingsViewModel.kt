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
                settingsRepository.currentProvider,
                settingsRepository.getApiKey(),
                settingsRepository.getBaseUrl(),
                settingsRepository.selectedModel,
                settingsRepository.isDarkMode
            ) { provider, apiKey, baseUrl, model, dark ->
                SettingsUiState(
                    currentProvider = provider,
                    apiKey = apiKey,
                    baseUrl = baseUrl,
                    selectedModel = model,
                    isDarkMode = dark
                )
            }.collect { _uiState.value = it }
        }
    }

    fun setProvider(provider: Provider) {
        viewModelScope.launch {
            settingsRepository.setProvider(provider)
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
            settingsRepository.setDarkMode(enabled)
            _uiState.update { it.copy(isDarkMode = enabled) }
        }
    }

    fun saveSettings() {
        viewModelScope.launch {
            val state = _uiState.value
            settingsRepository.setApiKey(state.apiKey)
            settingsRepository.setBaseUrl(state.baseUrl)
            settingsRepository.setModel(state.selectedModel)
            _uiState.update { it.copy(saved = true) }
        }
    }
}
