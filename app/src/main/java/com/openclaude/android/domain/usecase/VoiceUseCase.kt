package com.openclaude.android.domain.usecase

import javax.inject.Inject

class VoiceUseCase @Inject constructor() {
    fun getAvailableLanguages(): List<Pair<String, String>> = listOf("en-US" to "English", "id-ID" to "Indonesian", "ja-JP" to "Japanese", "zh-CN" to "Chinese", "ko-KR" to "Korean")
    fun getSpeedPresets(): List<Pair<String, Float>> = listOf("Slow" to 0.75f, "Normal" to 1.0f, "Fast" to 1.25f, "Very Fast" to 1.5f)
}
