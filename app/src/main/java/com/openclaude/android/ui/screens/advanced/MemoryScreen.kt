package com.openclaude.android.ui.screens.advanced

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
import com.openclaude.android.data.model.MemoryEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(viewModel: MemoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Memory", fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = { viewModel.toggleAdd() }) { Icon(Icons.Default.Add, "Add") } }) },
        floatingActionButton = { FloatingActionButton(onClick = { viewModel.toggleAdd() }) { Icon(Icons.Default.Add, "Add Memory") } }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Category filter
            ScrollableTabRow(selectedTabIndex = (listOf("all") + uiState.categories).indexOf(uiState.selectedCategory).coerceAtLeast(0)) {
                (listOf("all") + uiState.categories).forEach { cat -> Tab(selected = uiState.selectedCategory == cat, onClick = { viewModel.filterCategory(cat) }, text = { Text(cat.replaceFirstChar { it.uppercase() }) }) }
            }

            val filtered = if (uiState.selectedCategory == "all") uiState.entries else uiState.entries.filter { it.category == uiState.selectedCategory }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No memories stored") }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { entry -> MemoryCard(entry, onDelete = { viewModel.removeEntry(entry.id) }) }
                }
            }
        }

        if (uiState.showAdd) { AddMemoryDialog(onDismiss = { viewModel.toggleAdd() }, onAdd = { k, v, c -> viewModel.addEntry(k, v, c) }, categories = uiState.categories) }
    }
}

@Composable
private fun MemoryCard(entry: MemoryEntry, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.key, fontWeight = FontWeight.SemiBold)
                Text(entry.value, style = MaterialTheme.typography.bodyMedium)
                Text(entry.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun AddMemoryDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit, categories: List<String>) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("general") }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Memory") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("Key") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("Value") }, modifier = Modifier.fillMaxWidth())
            ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}) {
                OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth().menuAnchor())
            }
        }},
        confirmButton = { TextButton(onClick = { onAdd(key, value, category) }, enabled = key.isNotBlank() && value.isNotBlank()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
