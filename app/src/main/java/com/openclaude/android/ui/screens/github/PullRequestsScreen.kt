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
import com.openclaude.android.data.model.GitHubPR
import com.openclaude.android.data.model.PrState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRequestsScreen(owner: String, repo: String, onPRClick: (Int) -> Unit, viewModel: PullRequestsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(owner, repo) { viewModel.loadPRs(owner, repo) }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Pull Requests", fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; viewModel.loadPRs(owner, repo, "open") }, text = { Text("Open") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; viewModel.loadPRs(owner, repo, "closed") }, text = { Text("Closed") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2; viewModel.loadPRs(owner, repo, "all") }, text = { Text("All") })
            }
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.prs) { pr -> PRCard(pr) { onPRClick(pr.number) } }
                }
            }
        }
    }
}

@Composable
private fun PRCard(pr: GitHubPR, onClick: () -> Unit) {
    val stateColor = when (pr.state) { PrState.OPEN -> Color(0xFF238636); PrState.MERGED -> Color(0xFF8957E5); PrState.CLOSED -> Color(0xFFDA3633) }
    val stateIcon = when (pr.state) { PrState.OPEN -> Icons.Default.CheckCircle; PrState.MERGED -> Icons.Default.Merge; PrState.CLOSED -> Icons.Default.Cancel }

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(stateIcon, null, tint = stateColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("#${pr.number}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(pr.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (pr.isDraft) { Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) { Text("Draft", style = MaterialTheme.typography.labelSmall) } }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("by ${pr.author}", style = MaterialTheme.typography.bodySmall)
                Text("${pr.comments} comments", style = MaterialTheme.typography.bodySmall)
                Text("+${pr.additions}/-${pr.deletions}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF238636))
            }
        }
    }
}
