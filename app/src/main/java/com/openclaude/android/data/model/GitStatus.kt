package com.openclaude.android.data.model

enum class GitStatus(val symbol: String, val label: String) {
    MODIFIED("M", "Modified"),
    ADDED("A", "Added"),
    DELETED("D", "Deleted"),
    RENAMED("R", "Renamed"),
    COPIED("C", "Copied"),
    UNTRACKED("?", "Untracked"),
    MODIFIED_STAGED("M*", "Staged Modified"),
    ADDED_STAGED("A*", "Staged Added"),
    DELETED_STAGED("D*", "Staged Deleted"),
    CONFLICT("!", "Conflict"),
    CLEAN("~", "Clean"),
    NONE("", "No Git");

    companion object {
        fun fromGitCode(code: String): GitStatus {
            return when {
                code.contains("M") && code.contains(" ") -> MODIFIED
                code.contains("M") -> MODIFIED_STAGED
                code.contains("A") && code.contains(" ") -> ADDED
                code.contains("A") -> ADDED_STAGED
                code.contains("D") && code.contains(" ") -> DELETED
                code.contains("D") -> DELETED_STAGED
                code.contains("R") -> RENAMED
                code.contains("C") -> COPIED
                code.contains("?") -> UNTRACKED
                code.contains("!") -> CONFLICT
                code.isBlank() -> CLEAN
                else -> NONE
            }
        }
    }
}
