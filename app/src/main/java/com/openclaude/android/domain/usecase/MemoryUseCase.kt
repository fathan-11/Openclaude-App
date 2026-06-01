package com.openclaude.android.domain.usecase

import com.openclaude.android.data.model.MemoryEntry
import javax.inject.Inject

class MemoryUseCase @Inject constructor() {
    fun getCategories(): List<String> = listOf("general", "user", "project", "preferences", "facts")
    fun formatMemory(entry: MemoryEntry): String = "[${entry.category}] ${entry.key}: ${entry.value}"
}
