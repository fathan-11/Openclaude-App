package com.openclaude.android.ui.screens.codeviewer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeViewerScreen(
    filePath: String,
    onBack: () -> Unit,
    onShare: ((String, String) -> Unit)? = null,
    viewModel: CodeViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(filePath) {
        viewModel.loadFile(filePath)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        filePath.substringAfterLast("/"),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.isSearchActive) viewModel.toggleSearch()
                        else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(
                            Icons.Default.Search,
                            "Find",
                            tint = if (uiState.isSearchActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.decreaseFontSize() }) {
                        Icon(Icons.Default.TextDecrease, "Decrease font")
                    }
                    IconButton(onClick = { viewModel.increaseFontSize() }) {
                        Icon(Icons.Default.TextIncrease, "Increase font")
                    }
                    IconButton(onClick = {
                        uiState.fileContent?.let {
                            clipboardManager.setText(AnnotatedString(it.content))
                        }
                    }) {
                        Icon(Icons.Default.ContentCopy, "Copy")
                    }
                    IconButton(onClick = {
                        uiState.fileContent?.let { content ->
                            onShare?.invoke(filePath, content.content)
                        }
                    }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            AnimatedVisibility(visible = uiState.isSearchActive) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    matchCount = uiState.matchCount,
                    currentMatch = uiState.currentMatchIndex,
                    onNext = { viewModel.nextMatch() },
                    onPrevious = { viewModel.previousMatch() },
                    onClose = { viewModel.toggleSearch() }
                )
            }

            // File content
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
                        }
                    }
                }
                uiState.fileContent != null -> {
                    val hScrollState = rememberScrollState()
                    val vScrollState = rememberScrollState()

                    Row(modifier = Modifier.fillMaxSize()) {
                        // Line numbers
                        if (uiState.showLineNumbers) {
                            Column(
                                modifier = Modifier
                                    .width(48.dp)
                                    .verticalScroll(vScrollState)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(vertical = 8.dp)
                            ) {
                                val lineCount = uiState.fileContent!!.lineCount
                                val currentMatchLine = if (uiState.currentMatchIndex >= 0 && uiState.searchResults.isNotEmpty())
                                    uiState.searchResults[uiState.currentMatchIndex].lineNumber else -1

                                for (i in 1..lineCount) {
                                    val isHighlighted = i == currentMatchLine
                                    Text(
                                        "$i",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.surface
                                            )
                                            .padding(horizontal = 4.dp),
                                        textAlign = TextAlign.End,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = uiState.fontSize.sp * 0.85f,
                                        color = if (isHighlighted) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Code content
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(hScrollState)
                                .verticalScroll(vScrollState)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            if (uiState.highlightedCode != null) {
                                Text(
                                    text = uiState.highlightedCode!!,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = uiState.fontSize.sp,
                                    lineHeight = (uiState.fontSize * 1.5f).sp
                                )
                            }
                        }
                    }

                    // Current match indicator
                    if (uiState.searchResults.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                "Match ${uiState.currentMatchIndex + 1} of ${uiState.matchCount}",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    matchCount: Int,
    currentMatch: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Find in file...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, "Clear")
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (matchCount > 0) {
                Text(
                    "${currentMatch + 1}/$matchCount",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onPrevious, enabled = matchCount > 0) {
                Icon(Icons.Default.KeyboardArrowUp, "Previous")
            }
            IconButton(onClick = onNext, enabled = matchCount > 0) {
                Icon(Icons.Default.KeyboardArrowDown, "Next")
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, "Close search")
            }
        }
    }
}
