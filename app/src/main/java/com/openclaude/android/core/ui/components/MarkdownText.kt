package com.openclaude.android.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(
    markdown: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val blocks = parseMarkdownBlocks(markdown)
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> {
                    val headingStyle = when (block.level) {
                        1 -> MaterialTheme.typography.headlineLarge
                        2 -> MaterialTheme.typography.headlineMedium
                        3 -> MaterialTheme.typography.headlineSmall
                        else -> MaterialTheme.typography.titleLarge
                    }
                    Text(
                        text = block.text,
                        color = color,
                        style = headingStyle.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                is MarkdownBlock.Code -> {
                    CodeBlock(
                        code = block.code,
                        language = block.language,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                is MarkdownBlock.BulletList -> {
                    block.items.forEach { item ->
                        Text(
                            text = "•  $item",
                            color = color,
                            style = style,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
                is MarkdownBlock.NumberedList -> {
                    block.items.forEachIndexed { index, item ->
                        Text(
                            text = "${index + 1}.  $item",
                            color = color,
                            style = style,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = block.text,
                        color = color,
                        style = style,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                is MarkdownBlock.InlineCode -> {
                    Text(
                        text = block.text,
                        color = color,
                        style = style,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private sealed class MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class Code(val code: String, val language: String) : MarkdownBlock()
    data class BulletList(val items: List<String>) : MarkdownBlock()
    data class NumberedList(val items: List<String>) : MarkdownBlock()
    data class Paragraph(val text: String) : MarkdownBlock()
    data class InlineCode(val text: String) : MarkdownBlock()
}

private fun parseMarkdownBlocks(markdown: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = markdown.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        when {
            line.startsWith("```") -> {
                val language = line.removePrefix("```").trim()
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                blocks.add(MarkdownBlock.Code(codeLines.joinToString("\n"), language))
                i++
            }
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length.coerceIn(1, 4)
                val text = line.dropWhile { it == '#' }.trim()
                blocks.add(MarkdownBlock.Heading(level, text))
                i++
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                val items = mutableListOf<String>()
                while (i < lines.size && (lines[i].startsWith("- ") || lines[i].startsWith("* "))) {
                    items.add(lines[i].drop(2).trim())
                    i++
                }
                blocks.add(MarkdownBlock.BulletList(items))
            }
            line.matches(Regex("^\\d+\\.\\s.*")) -> {
                val items = mutableListOf<String>()
                while (i < lines.size && lines[i].matches(Regex("^\\d+\\.\\s.*"))) {
                    items.add(lines[i].replaceFirst(Regex("^\\d+\\.\\s"), ""))
                    i++
                }
                blocks.add(MarkdownBlock.NumberedList(items))
            }
            line.isBlank() -> {
                i++
            }
            else -> {
                blocks.add(MarkdownBlock.Paragraph(line))
                i++
            }
        }
    }
    return blocks
}
