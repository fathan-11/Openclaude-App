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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsScreen(viewModel: SkillsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val categories = listOf("all" to "All", "development" to "Dev", "learning" to "Learn", "testing" to "Test", "docs" to "Docs", "git" to "Git")

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Skills", fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = categories.indexOfFirst { it.first == uiState.selectedCategory }.coerceAtLeast(0)) {
                categories.forEach { (key, label) -> Tab(selected = uiState.selectedCategory == key, onClick = { viewModel.filterByCategory(key) }, text = { Text(label) }) }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.skills) { skill -> SkillCard(skill, onClick = { viewModel.selectSkill(skill) }, onToggle = { viewModel.toggleSkill(skill) }) }
            }
        }
        uiState.selectedSkill?.let { skill -> SkillDetailSheet(skill, onDismiss = { viewModel.selectSkill(skill.copy()) }) }
    }
}

@Composable
private fun SkillCard(skill: Skill, onClick: () -> Unit, onToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(skill.name, fontWeight = FontWeight.SemiBold)
                Text(skill.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(skill.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Switch(checked = skill.isEnabled, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun SkillDetailSheet(skill: Skill, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(skill.name) },
        text = { Column { Text(skill.description); Spacer(Modifier.height(8.dp)); Text("Category: ${skill.category}"); if (skill.triggers.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Text("Triggers:"); skill.triggers.forEach { Text("• $it") } } } },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}
