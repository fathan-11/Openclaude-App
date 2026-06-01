package com.openclaude.android.data.model

data class SearchResult(
    val filePath: String,
    val fileName: String,
    val lineNumber: Int,
    val lineContent: String,
    val matchStart: Int,
    val matchEnd: Int,
    val context: String = ""
)

data class SearchQuery(
    val text: String,
    val isRegex: Boolean = false,
    val caseSensitive: Boolean = false,
    val fileFilter: String = "",
    val pathFilter: String = ""
)
