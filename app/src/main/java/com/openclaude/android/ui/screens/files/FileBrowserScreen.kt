package com.openclaude.android.ui.screens.files

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.FileNode
import com.openclaude.android.data.model.GitStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileBrowserScreen(
    onFileClick: (String) -> Unit,
    viewModel: FileBrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshGitStatus()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Files", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (uiState.currentPath != "/") {
                        IconButton(onClick = { viewModel.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (uiState.isGitRepo) {
                        // Git status badge
                        val modifiedCount = uiState.gitStatus.values.count {
                            it in listOf(GitStatus.MODIFIED, GitStatus.MODIFIED_STAGED)
                        }
                        val untrackedCount = uiState.gitStatus.values.count {
                            it == GitStatus.UNTRACKED
                        }
                        if (modifiedCount > 0 || untrackedCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text("${modifiedCount + untrackedCount}")
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Breadcrumb
            BreadcrumbNav(path = uiState.currentPath, onNavigate = { viewModel.loadFiles(it) })

            // Git status legend (when in git repo)
            if (uiState.isGitRepo && uiState.gitStatus.isNotEmpty()) {
                GitStatusLegend(
                    gitStatus = uiState.gitStatus,
                    onStatusClick = { status ->
                        viewModel.toggleGitStatusFilter(status)
                    }
                )
            }

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Warning, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.files, key = { it.path }) { file ->
                            FileTreeItem(
                                file = file,
                                onClick = {
                                    if (file.isDirectory) viewModel.toggleFolder(file)
                                    else onFileClick(file.path)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GitStatusLegend(
    gitStatus: Map<String, GitStatus>,
    onStatusClick: (GitStatus) -> Unit
) {
    val statusCounts = gitStatus.values.groupingBy { it }.eachCount()
    if (statusCounts.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Git:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        statusCounts.filter { it.key != GitStatus.CLEAN }.forEach { (status, count) ->
            Surface(
                onClick = { onStatusClick(status) },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GitStatusIndicator(status, size = 8)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${status.symbol}$count",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GitStatusIndicator(
    status: GitStatus?,
    size: Int = 12
) {
    val color = when (status) {
        GitStatus.MODIFIED, GitStatus.MODIFIED_STAGED -> Color(0xFFFFC107) // Amber
        GitStatus.ADDED, GitStatus.ADDED_STAGED -> Color(0xFF4CAF50) // Green
        GitStatus.DELETED, GitStatus.DELETED_STAGED -> Color(0xFFF44336) // Red
        GitStatus.UNTRACKED -> Color(0xFF9E9E9E) // Gray
        GitStatus.CONFLICT -> Color(0xFFE91E63) // Pink
        GitStatus.RENAMED, GitStatus.COPIED -> Color(0xFF2196F3) // Blue
        GitStatus.CLEAN -> Color(0xFF4CAF50) // Green
        GitStatus.NONE, null -> Color.Transparent
    }

    if (status != null && status != GitStatus.NONE) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
private fun BreadcrumbNav(path: String, onNavigate: (String) -> Unit) {
    val parts = path.split("/").filter { it.isNotEmpty() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = { onNavigate("/") }) {
            Text("root", fontSize = 12.sp)
        }
        parts.forEachIndexed { index, part ->
            Text("/", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            TextButton(onClick = {
                val subPath = "/" + parts.take(index + 1).joinToString("/")
                onNavigate(subPath)
            }) {
                Text(part, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun FileTreeItem(file: FileNode, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (file.isDirectory) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Git status indicator (left of icon)
            if (file.gitStatus != null && file.gitStatus != GitStatus.NONE) {
                GitStatusIndicator(file.gitStatus)
                Spacer(Modifier.width(8.dp))
            }

            Text(file.icon, fontSize = 20.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    file.name,
                    fontWeight = if (file.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!file.isDirectory) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${file.extension.uppercase()} • ${formatSize(file.size)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (file.gitStatus != null && file.gitStatus != GitStatus.NONE && file.gitStatus != GitStatus.CLEAN) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                file.gitStatus.symbol,
                                style = MaterialTheme.typography.labelSmall,
                                color = when (file.gitStatus) {
                                    GitStatus.MODIFIED, GitStatus.MODIFIED_STAGED -> Color(0xFFFFC107)
                                    GitStatus.ADDED, GitStatus.ADDED_STAGED -> Color(0xFF4CAF50)
                                    GitStatus.DELETED, GitStatus.DELETED_STAGED -> Color(0xFFF44336)
                                    GitStatus.UNTRACKED -> Color(0xFF9E9E9E)
                                    GitStatus.CONFLICT -> Color(0xFFE91E63)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            if (file.isDirectory) {
                Icon(
                    if (file.isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
