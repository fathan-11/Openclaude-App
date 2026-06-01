package com.openclaude.android.ui.screens.advanced

import androidx.lifecycle.ViewModel
import com.openclaude.android.data.model.VoiceConfig
import com.openclaude.android.data.model.VoiceState
import com.openclaude.android.domain.usecase.VoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class VoiceUiState(val state: VoiceState = VoiceState.IDLE, val config: VoiceConfig = VoiceConfig(), val recognizedText: String = "", val languages: List<Pair<String, String>> = emptyList())

@HiltViewModel
class VoiceViewModel @Inject constructor(private val voiceUseCase: VoiceUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    init { _uiState.update { it.copy(languages = voiceUseCase.getAvailableLanguages()) } }

    fun startListening() { _uiState.update { it.copy(state = VoiceState.LISTENING) } }
    fun stopListening() { _uiState.update { it.copy(state = VoiceState.PROCESSING) } }
    fun onResult(text: String) { _uiState.update { it.copy(state = VoiceState.IDLE, recognizedText = text) } }
    fun setLanguage(lang: String) { _uiState.update { it.copy(config = it.config.copy(language = lang)) } }
    fun setSpeed(speed: Float) { _uiState.update { it.copy(config = it.config.copy(speechRate = speed)) } }
    fun clearText() { _uiState.update { it.copy(recognizedText = "") } }
}
