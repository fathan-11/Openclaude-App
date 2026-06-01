package com.openclaude.android.data.model

data class Model(
    val id: String,
    val name: String,
    val provider: Provider,
    val maxTokens: Int = 4096
) {
    companion object {
        fun defaultModels(provider: Provider): List<Model> {
            return when (provider) {
                Provider.OPENAI -> listOf(
                    Model("gpt-4o", "GPT-4o", provider, 128000),
                    Model("gpt-4o-mini", "GPT-4o Mini", provider, 128000),
                    Model("gpt-4-turbo", "GPT-4 Turbo", provider, 128000),
                    Model("gpt-3.5-turbo", "GPT-3.5 Turbo", provider, 16385),
                    Model("o1-preview", "o1 Preview", provider, 128000),
                    Model("o1-mini", "o1 Mini", provider, 128000),
                )
                Provider.GEMINI -> listOf(
                    Model("gemini-2.0-flash", "Gemini 2.0 Flash", provider, 1048576),
                    Model("gemini-1.5-pro", "Gemini 1.5 Pro", provider, 2097152),
                    Model("gemini-1.5-flash", "Gemini 1.5 Flash", provider, 1048576),
                    Model("gemini-pro", "Gemini Pro", provider, 32768),
                )
                Provider.OLLAMA -> listOf(
                    Model("llama3.1", "Llama 3.1", provider, 131072),
                    Model("llama3.1:70b", "Llama 3.1 70B", provider, 131072),
                    Model("codellama", "Code Llama", provider, 16384),
                    Model("mistral", "Mistral", provider, 32768),
                    Model("deepseek-coder-v2", "DeepSeek Coder V2", provider, 131072),
                    Model("qwen2.5-coder", "Qwen 2.5 Coder", provider, 131072),
                )
                Provider.DEEPSEEK -> listOf(
                    Model("deepseek-chat", "DeepSeek Chat", provider, 65536),
                    Model("deepseek-coder", "DeepSeek Coder", provider, 65536),
                    Model("deepseek-reasoner", "DeepSeek Reasoner", provider, 65536),
                )
                Provider.OPENROUTER -> listOf(
                    Model("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", provider, 200000),
                    Model("anthropic/claude-3-opus", "Claude 3 Opus", provider, 200000),
                    Model("openai/gpt-4o", "GPT-4o", provider, 128000),
                    Model("google/gemini-2.0-flash-exp", "Gemini 2.0 Flash", provider, 1048576),
                    Model("meta-llama/llama-3.1-405b-instruct", "Llama 3.1 405B", provider, 131072),
                    Model("deepseek/deepseek-chat", "DeepSeek Chat", provider, 65536),
                )
            }
        }
    }
}
