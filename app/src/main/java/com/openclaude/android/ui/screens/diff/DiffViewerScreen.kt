package com.openclaude.android.ui.screens.diff

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclaude.android.data.model.DiffLine
import com.openclaude.android.data.model.DiffLineType
import com.openclaude.android.data.model.DiffHunk
import com.openclaude.android.data.model.DiffResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffViewerScreen(
    diff: DiffResult,
    onBack: () -> Unit
) {
    var showUnified by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Diff", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Toggle unified/split
                    TextButton(onClick = { showUnified = !showUnified }) {
                        Icon(
                            if (showUnified) Icons.Default.ViewSidebar else Icons.Default.ViewHeadline,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (showUnified) "Split" else "Unified")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Stats bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(diff.oldFile, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${diff.hunks.sumOf { it.lines.size }} lines",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("+${diff.additions}", color = Color(0xFF50FA7B), fontWeight = FontWeight.Bold)
                    Text(" / ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-${diff.deletions}", color = Color(0xFFFF5555), fontWeight = FontWeight.Bold)
                }
            }
            
            if (showUnified) {
                // Unified diff view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp)
                ) {
                    diff.hunks.forEach { hunk ->
                        DiffHunkView(hunk)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            } else {
                // Side-by-side diff view
                SideBySideDiffView(diff = diff)
            }
        }
    }
}

/**
 * Side-by-side diff view showing old and new versions next to each other.
 */
@Composable
fun SideBySideDiffView(diff: DiffResult) {
    val scrollState = rememberScrollState()
    
    // Build side-by-side line pairs
    val linePairs = remember(diff) {
        buildSideBySidePairs(diff)
    }
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
    ) {
        // Old file column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                diff.oldFile,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            
            linePairs.forEach { pair ->
                SideBySideLine(
                    lineNum = pair.oldLineNum,
                    content = pair.oldContent,
                    type = pair.oldType,
                    isLeft = true
                )
            }
        }
        
        // Divider
        VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 2.dp)
        
        // New file column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                diff.newFile,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            
            linePairs.forEach { pair ->
                SideBySideLine(
                    lineNum = pair.newLineNum,
                    content = pair.newContent,
                    type = pair.newType,
                    isLeft = false
                )
            }
        }
    }
}

/**
 * Represents a line pair in the side-by-side view.
 */
data class SideBySideLinePair(
    val oldLineNum: Int? = null,
    val oldContent: String = "",
    val oldType: DiffLineType = DiffLineType.CONTEXT,
    val newLineNum: Int? = null,
    val newContent: String = "",
    val newType: DiffLineType = DiffLineType.CONTEXT,
)

/**
 * Builds line pairs for side-by-side diff display.
 */
fun buildSideBySidePairs(diff: DiffResult): List<SideBySideLinePair> {
    val pairs = mutableListOf<SideBySideLinePair>()
    
    diff.hunks.forEach { hunk ->
        // Add hunk header
        pairs.add(SideBySideLinePair(
            oldContent = hunk.header,
            oldType = DiffLineType.CONTEXT,
            newContent = hunk.header,
            newType = DiffLineType.CONTEXT,
        ))
        
        // Group lines by type to align additions and deletions
        val pendingDeletions = mutableListOf<DiffLine>()
        val pendingAdditions = mutableListOf<DiffLine>()
        
        hunk.lines.forEach { line ->
            when (line.type) {
                DiffLineType.CONTEXT -> {
                    // Flush pending deletions and additions
                    flushPendingLines(pendingDeletions, pendingAdditions, pairs)
                    pairs.add(SideBySideLinePair(
                        oldLineNum = line.oldLineNum,
                        oldContent = line.content,
                        oldType = DiffLineType.CONTEXT,
                        newLineNum = line.newLineNum,
                        newContent = line.content,
                        newType = DiffLineType.CONTEXT,
                    ))
                }
                DiffLineType.DELETION -> {
                    pendingDeletions.add(line)
                }
                DiffLineType.ADDITION -> {
                    pendingAdditions.add(line)
                }
            }
        }
        
        // Flush remaining
        flushPendingLines(pendingDeletions, pendingAdditions, pairs)
    }
    
    return pairs
}

private fun flushPendingLines(
    deletions: MutableList<DiffLine>,
    additions: MutableList<DiffLine>,
    pairs: MutableList<SideBySideLinePair>
) {
    val maxSize = maxOf(deletions.size, additions.size)
    for (i in 0 until maxSize) {
        val del = deletions.getOrNull(i)
        val add = additions.getOrNull(i)
        pairs.add(SideBySideLinePair(
            oldLineNum = del?.oldLineNum,
            oldContent = del?.content ?: "",
            oldType = del?.type ?: DiffLineType.CONTEXT,
            newLineNum = add?.newLineNum,
            newContent = add?.content ?: "",
            newType = add?.type ?: DiffLineType.CONTEXT,
        ))
    }
    deletions.clear()
    additions.clear()
}

@Composable
private fun SideBySideLine(
    lineNum: Int?,
    content: String,
    type: DiffLineType,
    isLeft: Boolean
) {
    val bgColor = when (type) {
        DiffLineType.ADDITION -> if (!isLeft) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color.Transparent
        DiffLineType.DELETION -> if (isLeft) Color(0xFFC62828).copy(alpha = 0.15f) else Color.Transparent
        DiffLineType.CONTEXT -> Color.Transparent
    }
    val textColor = when (type) {
        DiffLineType.ADDITION -> if (!isLeft) Color(0xFF50FA7B) else MaterialTheme.colorScheme.onSurfaceVariant
        DiffLineType.DELETION -> if (isLeft) Color(0xFFFF5555) else MaterialTheme.colorScheme.onSurfaceVariant
        DiffLineType.CONTEXT -> MaterialTheme.colorScheme.onSurface
    }
    val prefix = when {
        type == DiffLineType.ADDITION && !isLeft -> "+"
        type == DiffLineType.DELETION && isLeft -> "-"
        else -> " "
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line number
        Text(
            text = lineNum?.toString() ?: "",
            modifier = Modifier.width(32.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        // Prefix
        Text(
            text = prefix,
            modifier = Modifier.width(14.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        // Content
        Text(
            text = content.ifEmpty { " " },
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DiffHunkView(hunk: DiffHunk) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Hunk header
            Text(
                hunk.header,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Lines
            hunk.lines.forEach { line ->
                DiffLineView(line)
            }
        }
    }
}

@Composable
private fun DiffLineView(line: DiffLine) {
    val bgColor = when (line.type) {
        DiffLineType.ADDITION -> Color(0xFF2E7D32).copy(alpha = 0.2f)
        DiffLineType.DELETION -> Color(0xFFC62828).copy(alpha = 0.2f)
        DiffLineType.CONTEXT -> Color.Transparent
    }
    val prefix = when (line.type) {
        DiffLineType.ADDITION -> "+"
        DiffLineType.DELETION -> "-"
        DiffLineType.CONTEXT -> " "
    }
    val textColor = when (line.type) {
        DiffLineType.ADDITION -> Color(0xFF50FA7B)
        DiffLineType.DELETION -> Color(0xFFFF5555)
        DiffLineType.CONTEXT -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        // Line numbers
        Text(
            "${line.oldLineNum ?: ""}",
            modifier = Modifier.width(36.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "${line.newLineNum ?: ""}",
            modifier = Modifier.width(36.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        // Prefix
        Text(
            prefix,
            modifier = Modifier.width(16.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        // Content
        Text(
            line.content,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = textColor
        )
    }
}
