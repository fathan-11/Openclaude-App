package com.openclaude.android.ui.screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.TerminalLine
import com.openclaude.android.data.model.TerminalLineType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.activeSession?.history?.size) {
        uiState.activeSession?.history?.size?.let {
            if (it > 0) listState.animateScrollToItem(it - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Terminal", fontWeight = FontWeight.Bold) },
                actions = {
                    if (uiState.isRunning) {
                        IconButton(onClick = { viewModel.killCurrentCommand() }) {
                            Icon(Icons.Default.Stop, "Kill", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = { viewModel.createNewTab() }) {
                        Icon(Icons.Default.Add, "New Tab")
                    }
                    IconButton(onClick = { viewModel.clearTerminal() }) {
                        Icon(Icons.Default.Delete, "Clear")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tab bar
            if (uiState.sessions.size > 1) {
                TabRow(
                    selectedTabIndex = uiState.activeSessionIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    uiState.sessions.forEachIndexed { index, session ->
                        Tab(
                            selected = index == uiState.activeSessionIndex,
                            onClick = { viewModel.switchTab(index) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(session.name, fontSize = 12.sp)
                                    if (uiState.sessions.size > 1) {
                                        Spacer(Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { viewModel.closeTab(index) },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Icon(Icons.Default.Close, "Close", modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Terminal output
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF0D1117))
                    .padding(8.dp)
            ) {
                items(uiState.activeSession?.history ?: emptyList()) { line ->
                    TerminalLineView(line)
                }
                if (uiState.isRunning) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Running...", color = Color(0xFF50FA7B), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161B22))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$", color = Color(0xFF50FA7B), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.updateInput(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter command...", color = Color(0xFF6272A4)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFFF8F8F2),
                        unfocusedTextColor = Color(0xFFF8F8F2),
                        focusedBorderColor = Color(0xFF50FA7B),
                        unfocusedBorderColor = Color(0xFF44475A),
                        cursorColor = Color(0xFF50FA7B)
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.executeCommand() },
                    enabled = uiState.inputText.isNotBlank() && !uiState.isRunning
                ) {
                    Icon(
                        Icons.Default.Send,
                        "Send",
                        tint = if (uiState.inputText.isNotBlank()) Color(0xFF50FA7B) else Color(0xFF6272A4)
                    )
                }
            }

            // Error display
            uiState.error?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        error,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalLineView(line: TerminalLine) {
    val color = when (line.type) {
        TerminalLineType.INPUT -> Color(0xFF50FA7B)
        TerminalLineType.OUTPUT -> Color(0xFFF8F8F2)
        TerminalLineType.ERROR -> Color(0xFFFF5555)
        TerminalLineType.SYSTEM -> Color(0xFF6272A4)
        TerminalLineType.SUCCESS -> Color(0xFF50FA7B)
    }

    Text(
        text = line.content,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 1.dp)
    )
}
