package com.openclaude.android.data.model

enum class Provider(val displayName: String, val baseUrl: String) {
    OPENAI("OpenAI", "https://api.openai.com/v1/"),
    GEMINI("Gemini", "https://generativelanguage.googleapis.com/v1beta/"),
    OLLAMA("Ollama", "http://localhost:11434/v1/"),
    DEEPSEEK("DeepSeek", "https://api.deepseek.com/v1/"),
    OPENROUTER("OpenRouter", "https://openrouter.ai/api/v1/");

    companion object {
        fun fromName(name: String): Provider {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: OPENAI
        }
    }
}
