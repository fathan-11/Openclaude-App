package com.openclaude.android.ui.screens.codeviewer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
    viewModel: CodeViewerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(filePath) {
        viewModel.loadFile(filePath)
    }

    Scaffold(
        topBar = {
            Column {
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
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        // Search
                        IconButton(onClick = { viewModel.toggleSearch() }) {
                            Icon(
                                Icons.Default.Search, 
                                "Find in file",
                                tint = if (uiState.isSearchVisible) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Font size controls
                        IconButton(onClick = { viewModel.decreaseFontSize() }) {
                            Icon(Icons.Default.TextDecrease, "Decrease font")
                        }
                        IconButton(onClick = { viewModel.increaseFontSize() }) {
                            Icon(Icons.Default.TextIncrease, "Increase font")
                        }
                        // Copy all
                        IconButton(onClick = {
                            uiState.fileContent?.let {
                                clipboardManager.setText(AnnotatedString(it.content))
                            }
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copy")
                        }
                        // Share
                        IconButton(onClick = {
                            uiState.fileContent?.let { file ->
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, file.content)
                                    type = "text/plain"
                                }
                                val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            }
                        }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                )
                
                // Search bar
                AnimatedVisibility(
                    visible = uiState.isSearchVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SearchBar(
                        query = uiState.searchQuery,
                        matchCount = uiState.matchCount,
                        currentMatchIndex = uiState.currentMatchIndex,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onNext = { viewModel.nextMatch() },
                        onPrevious = { viewModel.previousMatch() },
                        onClose = { viewModel.toggleSearch() }
                    )
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
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
                
                Row(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                            for (i in 1..lineCount) {
                                Text(
                                    "$i",
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                    textAlign = TextAlign.End,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = uiState.fontSize.sp * 0.85f,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    matchCount: Int,
    currentMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search input
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
            
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                "Find in file...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
            
            // Match count
            if (query.isNotEmpty()) {
                Text(
                    text = if (matchCount > 0) "${currentMatchIndex + 1}/$matchCount" else "No results",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            
            // Navigation buttons
            IconButton(
                onClick = onPrevious,
                enabled = matchCount > 0,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "Previous", modifier = Modifier.size(18.dp))
            }
            IconButton(
                onClick = onNext,
                enabled = matchCount > 0,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "Next", modifier = Modifier.size(18.dp))
            }
            
            // Close
            IconButton(onClick = onClose, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, "Close search", modifier = Modifier.size(18.dp))
            }
        }
    }
}
