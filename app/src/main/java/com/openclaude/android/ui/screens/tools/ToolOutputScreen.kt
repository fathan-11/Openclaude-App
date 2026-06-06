package com.openclaude.android.ui.screens.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.ToolExecution
import com.openclaude.android.data.model.ToolStatus as DataToolStatus
import com.openclaude.android.data.model.ToolType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolOutputScreen(
    viewModel: ToolOutputViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tools", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadHistory() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.executions.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Build, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Text("No tool executions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Tools will appear here when used in chat", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.executions) { execution ->
                        ToolCard(
                            execution = execution,
                            onClick = { viewModel.selectExecution(execution) }
                        )
                    }
                }
            }
        }

        // Selected execution detail
        uiState.selectedExecution?.let { execution ->
            AlertDialog(
                onDismissRequest = { viewModel.selectExecution(execution) },
                title = { Text(execution.name) },
                text = {
                    Column {
                        Text("Type: ${execution.type}", fontWeight = FontWeight.SemiBold)
                        Text("Status: ${execution.status}")
                        Spacer(Modifier.height(8.dp))
                        Text("Input:", fontWeight = FontWeight.SemiBold)
                        execution.input.forEach { (key, value) ->
                            Text("  $key: $value", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.selectExecution(execution) }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun ToolCard(execution: ToolExecution, onClick: () -> Unit) {
    val (icon, color) = when (execution.type) {
        ToolType.BASH -> Icons.Default.Terminal to MaterialTheme.colorScheme.primary
        ToolType.FILE_READ, ToolType.FILE_WRITE, ToolType.FILE_EDIT -> Icons.Default.Description to MaterialTheme.colorScheme.tertiary
        ToolType.SEARCH -> Icons.Default.Search to MaterialTheme.colorScheme.secondary
        ToolType.WEB_SEARCH, ToolType.WEB_FETCH -> Icons.Default.Public to MaterialTheme.colorScheme.primary
        ToolType.MCP -> Icons.Default.Hub to MaterialTheme.colorScheme.tertiary
        ToolType.AGENT -> Icons.Default.SmartToy to MaterialTheme.colorScheme.secondary
        ToolType.CUSTOM -> Icons.Default.Extension to MaterialTheme.colorScheme.onSurface
    }

    val statusColor = when (execution.status) {
        DataToolStatus.COMPLETED -> MaterialTheme.colorScheme.primary
        DataToolStatus.FAILED -> MaterialTheme.colorScheme.error
        DataToolStatus.RUNNING -> MaterialTheme.colorScheme.tertiary
        DataToolStatus.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant
        DataToolStatus.CANCELLED -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(execution.name, fontWeight = FontWeight.SemiBold)
                Text("${execution.type} • ${execution.status}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Badge(containerColor = statusColor)
        }
    }
}
