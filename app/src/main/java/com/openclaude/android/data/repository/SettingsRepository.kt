package com.openclaude.android.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.openclaude.android.data.model.Provider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val SELECTED_PROVIDER = stringPreferencesKey("selected_provider")
        private val SELECTED_MODEL = stringPreferencesKey("selected_model")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val FONT_SIZE = intPreferencesKey("font_size")

        // API Keys
        private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        private val OLLAMA_BASE_URL = stringPreferencesKey("ollama_base_url")
        private val DEEPSEEK_API_KEY = stringPreferencesKey("deepseek_api_key")
        private val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    }

    val selectedProvider: Flow<Provider> = dataStore.data.map { prefs ->
        Provider.fromName(prefs[SELECTED_PROVIDER] ?: "OPENAI")
    }

    val selectedModel: Flow<String> = dataStore.data.map { prefs ->
        prefs[SELECTED_MODEL] ?: "gpt-4o"
    }

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "dark"
    }

    val fontSize: Flow<Int> = dataStore.data.map { prefs ->
        prefs[FONT_SIZE] ?: 14
    }

    suspend fun setSelectedProvider(provider: Provider) {
        dataStore.edit { prefs ->
            prefs[SELECTED_PROVIDER] = provider.name
        }
    }

    suspend fun setSelectedModel(model: String) {
        dataStore.edit { prefs ->
            prefs[SELECTED_MODEL] = model
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    suspend fun setFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[FONT_SIZE] = size
        }
    }

    suspend fun getApiKey(provider: Provider): String? {
        val prefs = dataStore.data.first()
        return when (provider) {
            Provider.OPENAI -> prefs[OPENAI_API_KEY]
            Provider.GEMINI -> prefs[GEMINI_API_KEY]
            Provider.OLLAMA -> "" // Ollama doesn't need API key
            Provider.DEEPSEEK -> prefs[DEEPSEEK_API_KEY]
            Provider.OPENROUTER -> prefs[OPENROUTER_API_KEY]
        }?.takeIf { it.isNotBlank() }
    }

    suspend fun setApiKey(provider: Provider, key: String) {
        dataStore.edit { prefs ->
            when (provider) {
                Provider.OPENAI -> prefs[OPENAI_API_KEY] = key
                Provider.GEMINI -> prefs[GEMINI_API_KEY] = key
                Provider.OLLAMA -> prefs[OLLAMA_BASE_URL] = key
                Provider.DEEPSEEK -> prefs[DEEPSEEK_API_KEY] = key
                Provider.OPENROUTER -> prefs[OPENROUTER_API_KEY] = key
            }
        }
    }

    fun getBaseUrl(provider: Provider): String {
        return provider.baseUrl
    }

    fun getApiKeyFlow(provider: Provider): Flow<String> {
        return dataStore.data.map { prefs ->
            when (provider) {
                Provider.OPENAI -> prefs[OPENAI_API_KEY] ?: ""
                Provider.GEMINI -> prefs[GEMINI_API_KEY] ?: ""
                Provider.OLLAMA -> prefs[OLLAMA_BASE_URL] ?: provider.baseUrl
                Provider.DEEPSEEK -> prefs[DEEPSEEK_API_KEY] ?: ""
                Provider.OPENROUTER -> prefs[OPENROUTER_API_KEY] ?: ""
            }
        }
    }
}
