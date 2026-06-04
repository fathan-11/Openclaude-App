package com.openclaude.android.data.model

data class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val extension: String = "",
    val children: List<FileNode> = emptyList(),
    val isExpanded: Boolean = false,
    val lastModified: Long = 0,
    val gitStatus: GitStatus? = null
) {
    val icon: String get() = when {
        isDirectory && isExpanded -> "📂"
        isDirectory -> "📁"
        extension == "kt" -> "🟣"
        extension == "java" -> "🟠"
        extension == "xml" -> "🔵"
        extension == "json" -> "🟡"
        extension == "md" -> "📝"
        extension == "py" -> "🐍"
        extension == "js" || extension == "ts" -> "🟨"
        extension == "html" -> "🌐"
        extension == "css" -> "🎨"
        else -> "📄"
    }

    val gitStatusColor: Long get() = when (gitStatus) {
        GitStatus.MODIFIED, GitStatus.MODIFIED_STAGED -> 0xFFFFC107 // Amber
        GitStatus.ADDED, GitStatus.ADDED_STAGED -> 0xFF4CAF50 // Green
        GitStatus.DELETED, GitStatus.DELETED_STAGED -> 0xFFF44336 // Red
        GitStatus.UNTRACKED -> 0xFF9E9E9E // Gray
        GitStatus.CONFLICT -> 0xFFE91E63 // Pink
        GitStatus.RENAMED, GitStatus.COPIED -> 0xFF2196F3 // Blue
        GitStatus.CLEAN -> 0xFF4CAF50 // Green
        GitStatus.NONE, null -> 0x00000000 // Transparent
    }
}
