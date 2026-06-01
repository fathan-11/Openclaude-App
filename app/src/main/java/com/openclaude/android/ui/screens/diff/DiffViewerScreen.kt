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
                Text(diff.oldFile, fontWeight = FontWeight.SemiBold)
                Row {
                    Text("+${diff.additions}", color = Color(0xFF50FA7B), fontWeight = FontWeight.Bold)
                    Text(" / ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("-${diff.deletions}", color = Color(0xFFFF5555), fontWeight = FontWeight.Bold)
                }
            }
            
            // Diff hunks
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
        }
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
