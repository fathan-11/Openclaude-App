package com.openclaude.android.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyntaxHighlighter @Inject constructor() {
    
    // Dracula-inspired color theme
    private val keywordColor = Color(0xFFBD93F9)   // Purple
    private val stringColor = Color(0xFFF1FA8C)    // Yellow
    private val commentColor = Color(0xFF6272A4)   // Gray
    private val numberColor = Color(0xFFBD93F9)    // Purple
    private val functionColor = Color(0xFF50FA7B)  // Green
    private val typeColor = Color(0xFF8BE9FD)      // Cyan
    private val annotationColor = Color(0xFFFFB86C) // Orange
    private val defaultColor = Color(0xFFF8F8F2)   // White
    
    private val keywords = setOf(
        "fun", "val", "var", "class", "object", "interface", "when", "if", "else",
        "for", "while", "return", "import", "package", "is", "as", "in", "out",
        "override", "abstract", "open", "sealed", "data", "enum", "companion",
        "suspend", "private", "public", "protected", "internal", "const", "lateinit",
        "by", "lazy", "this", "super", "try", "catch", "finally", "throw", "new",
        "def", "from", "async", "await", "export", "default", "let", "const", "function",
        "public", "static", "void", "int", "string", "bool", "true", "false", "null"
    )
    
    fun highlight(code: String, language: String = "kotlin"): AnnotatedString {
        return buildAnnotatedString {
            val lines = code.split("\n")
            lines.forEachIndexed { index, line ->
                highlightLine(line)
                if (index < lines.size - 1) append("\n")
            }
        }
    }
    
    private fun AnnotatedString.Builder.highlightLine(line: String) {
        var i = 0
        while (i < line.length) {
            when {
                // Comments
                line.startsWith("//", i) -> {
                    withStyle(SpanStyle(color = commentColor)) { append(line.substring(i)) }
                    return
                }
                // Strings
                line[i] == '"' -> {
                    val end = line.indexOf('"', i + 1)
                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i)) }
                        return
                    }
                }
                line[i] == '\'' -> {
                    val end = line.indexOf('\'', i + 1)
                    if (end != -1) {
                        withStyle(SpanStyle(color = stringColor)) { append(line.substring(i, end + 1)) }
                        i = end + 1
                    } else {
                        append(line[i])
                        i++
                    }
                }
                // Numbers
                line[i].isDigit() -> {
                    val start = i
                    while (i < line.length && (line[i].isDigit() || line[i] == '.')) i++
                    withStyle(SpanStyle(color = numberColor)) { append(line.substring(start, i)) }
                }
                // Words (keywords, identifiers)
                line[i].isLetter() || line[i] == '_' -> {
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    val word = line.substring(start, i)
                    val color = when {
                        keywords.contains(word) -> keywordColor
                        word.first().isUpperCase() -> typeColor
                        i < line.length && line[i] == '(' -> functionColor
                        else -> defaultColor
                    }
                    withStyle(SpanStyle(color = color)) { append(word) }
                }
                // Annotations
                line[i] == '@' -> {
                    val start = i
                    while (i < line.length && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    withStyle(SpanStyle(color = annotationColor)) { append(line.substring(start, i)) }
                }
                else -> {
                    append(line[i])
                    i++
                }
            }
        }
    }
    
    fun detectLanguage(filename: String): String {
        return when {
            filename.endsWith(".kt") -> "kotlin"
            filename.endsWith(".java") -> "java"
            filename.endsWith(".py") -> "python"
            filename.endsWith(".js") || filename.endsWith(".ts") -> "javascript"
            filename.endsWith(".xml") -> "xml"
            filename.endsWith(".json") -> "json"
            filename.endsWith(".md") -> "markdown"
            filename.endsWith(".html") -> "html"
            filename.endsWith(".css") -> "css"
            filename.endsWith(".sh") -> "bash"
            filename.endsWith(".gradle.kts") || filename.endsWith(".gradle") -> "kotlin"
            else -> "text"
        }
    }
}
