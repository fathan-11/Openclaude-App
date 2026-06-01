package com.openclaude.android.ui.screens.advanced

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: TasksViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Agent Tasks", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = { FloatingActionButton(onClick = { viewModel.toggleCreate() }) { Icon(Icons.Default.Add, "New Task") } }
    ) { padding ->
        if (uiState.tasks.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Assignment, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(16.dp))
                    Text("No tasks running")
                    Text("Create a task to get started", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.tasks) { task -> TaskCard(task, onCancel = { viewModel.cancelTask(task.id) }, onClick = { viewModel.selectTask(task) }) }
            }
        }

        if (uiState.showCreate) { CreateTaskDialog(onDismiss = { viewModel.toggleCreate() }, onCreate = { t, d -> viewModel.createTask(t, d) }) }
    }
}

@Composable
private fun TaskCard(task: AgentTask, onCancel: () -> Unit, onClick: () -> Unit) {
    val statusColor = when (task.status) { AgentTaskStatus.COMPLETED -> Color(0xFF238636); AgentTaskStatus.FAILED -> Color(0xFFDA3633); AgentTaskStatus.RUNNING -> MaterialTheme.colorScheme.primary; AgentTaskStatus.CANCELLED -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.onSurfaceVariant }
    val statusIcon = when (task.status) { AgentTaskStatus.COMPLETED -> Icons.Default.CheckCircle; AgentTaskStatus.FAILED -> Icons.Default.Error; AgentTaskStatus.RUNNING -> Icons.Default.PlayArrow; AgentTaskStatus.CANCELLED -> Icons.Default.Cancel; else -> Icons.Default.Schedule }

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(statusIcon, null, tint = statusColor)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, fontWeight = FontWeight.SemiBold)
                    Text("${task.status} • ${task.steps.count { it.status == AgentTaskStatus.COMPLETED }}/${task.steps.size} steps", style = MaterialTheme.typography.bodySmall)
                }
                if (task.status == AgentTaskStatus.RUNNING) { IconButton(onClick = onCancel) { Icon(Icons.Default.Stop, "Cancel", tint = MaterialTheme.colorScheme.error) } }
            }
            if (task.status == AgentTaskStatus.RUNNING) { Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { task.progress }, modifier = Modifier.fillMaxWidth()) }
        }
    }
}

@Composable
private fun CreateTaskDialog(onDismiss: () -> Unit, onCreate: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("New Task") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(100.dp))
        }},
        confirmButton = { TextButton(onClick = { onCreate(title, description) }, enabled = title.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
