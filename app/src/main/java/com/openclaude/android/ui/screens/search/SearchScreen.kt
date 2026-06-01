package com.openclaude.android.ui.screens.search

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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.SearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onResultClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Search", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search code...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { viewModel.toggleCaseSensitive() }) {
                            Icon(
                                Icons.Default.TextFields,
                                "Case sensitive",
                                tint = if (uiState.caseSensitive) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.toggleRegex() }) {
                            Text(
                                ".*",
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isRegex) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearResults() }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            
            // File filter
            if (uiState.query.isNotEmpty()) {
                OutlinedTextField(
                    value = uiState.fileFilter,
                    onValueChange = { viewModel.setFileFilter(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("Filter by filename...") },
                    leadingIcon = { Icon(Icons.Default.FilterList, null) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }

            when {
                uiState.isSearching -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                uiState.results.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.results) { result ->
                            SearchResultItem(
                                result = result,
                                query = uiState.query,
                                onClick = { onResultClick(result.filePath) }
                            )
                        }
                    }
                }
                uiState.query.length >= 2 && !uiState.isSearching -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Text("No results found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                uiState.recentSearches.isNotEmpty() -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recent Searches", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        uiState.recentSearches.forEach { search ->
                            TextButton(onClick = { viewModel.updateQuery(search) }) {
                                Icon(Icons.Default.History, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(search)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(result: SearchResult, query: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // File path
            Text(
                result.filePath,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            // Line number + content
            Row {
                Text(
                    "${result.lineNumber}:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    result.lineContent.trim(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
