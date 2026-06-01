package com.openclaude.android.data.model

data class FileNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val extension: String = "",
    val children: List<FileNode> = emptyList(),
    val isExpanded: Boolean = false,
    val lastModified: Long = 0
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
}
