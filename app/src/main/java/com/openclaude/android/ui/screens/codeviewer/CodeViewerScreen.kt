package com.openclaude.android.ui.screens.codeviewer

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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
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
                    IconButton(onClick = { /* Share intent */ }) {
                        Icon(Icons.Default.Share, "Share")
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
