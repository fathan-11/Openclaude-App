package com.openclaude.android.data.model

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val category: String = "",
    val isEnabled: Boolean = true,
    val triggers: List<String> = emptyList(),
    val content: String = ""
)
