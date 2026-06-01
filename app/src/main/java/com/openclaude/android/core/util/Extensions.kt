package com.openclaude.android.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.success(it) }
        .catch { emit(Result.failure(it)) }
}

fun Long.toFormattedDate(pattern: String = "MMM dd, yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        diff < 604_800_000 -> "${diff / 86_400_000}d ago"
        else -> toFormattedDate("MMM dd")
    }
}

fun String.truncate(maxLength: Int): String {
    return if (length > maxLength) {
        substring(0, maxLength) + "..."
    } else {
        this
    }
}

fun String.extractCodeBlocks(): List<Pair<String, String>> {
    val regex = Regex("```(\\w*)\\n([\\s\\S]*?)```")
    return regex.findAll(this).map { match ->
        val language = match.groupValues[1]
        val code = match.groupValues[2].trim()
        language to code
    }.toList()
}

fun String.isCodeBlock(): Boolean {
    return trim().startsWith("```") && trim().endsWith("```")
}
