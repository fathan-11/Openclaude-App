package com.openclaude.android.data.model

enum class DiffLineType { CONTEXT, ADDITION, DELETION }

data class DiffLine(
    val type: DiffLineType,
    val oldLineNum: Int?,
    val newLineNum: Int?,
    val content: String
)

data class DiffHunk(
    val oldStart: Int,
    val oldCount: Int,
    val newStart: Int,
    val newCount: Int,
    val header: String,
    val lines: List<DiffLine>
)

data class DiffResult(
    val oldFile: String,
    val newFile: String,
    val hunks: List<DiffHunk>,
    val additions: Int,
    val deletions: Int
)
