package com.openclaude.android.ui.screens.github

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.GitHubIssue
import com.openclaude.android.data.model.IssueState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesScreen(owner: String, repo: String, viewModel: IssuesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, repo) { viewModel.loadIssues(owner, repo) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Issues", fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = { viewModel.toggleCreate() }) { Icon(Icons.Default.Add, "Create") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { viewModel.toggleCreate() }) { Icon(Icons.Default.Add, "New Issue") } }
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            else -> LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.issues) { issue -> IssueCard(issue) }
            }
        }

        if (uiState.showCreate) {
            CreateIssueDialog(onDismiss = { viewModel.toggleCreate() }, onCreate = { title, body -> viewModel.createIssue(owner, repo, title, body) })
        }
    }
}

@Composable
private fun IssueCard(issue: GitHubIssue) {
    val stateColor = if (issue.state == IssueState.OPEN) Color(0xFF238636) else Color(0xFF8957E5)
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BugReport, null, tint = stateColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("#${issue.number}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(issue.title, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            }
            if (issue.labels.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    issue.labels.take(3).forEach { label -> SuggestionChip(onClick = {}, label = { Text(label, style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.height(24.dp)) }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text("by ${issue.author} • ${issue.comments} comments", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CreateIssueDialog(onDismiss: () -> Unit, onCreate: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("New Issue") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp))
        }},
        confirmButton = { TextButton(onClick = { onCreate(title, body) }, enabled = title.isNotBlank()) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
