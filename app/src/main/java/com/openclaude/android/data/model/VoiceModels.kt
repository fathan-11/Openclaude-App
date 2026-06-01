package com.openclaude.android.data.model

enum class VoiceState { IDLE, LISTENING, PROCESSING, SPEAKING }

data class VoiceConfig(
    val language: String = "en-US",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val autoSend: Boolean = true
)
