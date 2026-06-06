package com.openclaude.android.ui.screens.terminal

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.core.ui.theme.*
import com.openclaude.android.data.model.TerminalLine
import com.openclaude.android.data.model.TerminalLineType

// ═══════════════════════════════════════════════════════════════
// LINEAR-STYLE TERMINAL SCREEN
// Ultra-minimal dark terminal with real command execution
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    // Auto-scroll to bottom
    LaunchedEffect(uiState.activeSession?.history?.size) {
        uiState.activeSession?.history?.size?.let {
            if (it > 0) listState.animateScrollToItem(it - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Terminal",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                },
                actions = {
                    // Kill button
                    AnimatedVisibility(visible = uiState.isRunning) {
                        IconButton(onClick = { viewModel.killCurrentCommand() }) {
                            Icon(
                                Icons.Default.Stop,
                                "Kill",
                                tint = StatusRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    // New tab
                    IconButton(onClick = { viewModel.createNewTab() }) {
                        Icon(
                            Icons.Default.Add,
                            "New Tab",
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    // Clear
                    IconButton(onClick = { viewModel.clearTerminal() }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            "Clear",
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CanvasBlack
                )
            )
        },
        containerColor = CanvasBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Tab Bar (if multiple tabs) ─────────────────────
            if (uiState.sessions.size > 1) {
                TerminalTabBar(
                    sessions = uiState.sessions.map { it.name },
                    activeIndex = uiState.activeSessionIndex,
                    onTabClick = { viewModel.switchTab(it) },
                    onCloseTab = { viewModel.closeTab(it) }
                )
            }

            // ── Terminal Output ────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(CanvasBlack)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Welcome message if empty
                val history = uiState.activeSession?.history ?: emptyList()
                if (history.isEmpty() && !uiState.isRunning) {
                    item(key = "welcome") {
                        WelcomeMessage()
                    }
                }

                items(
                    items = history,
                    key = { "${it.timestamp}_${it.content.hashCode()}" }
                ) { line ->
                    TerminalLineView(line)
                }

                // Running indicator
                if (uiState.isRunning) {
                    item(key = "running") {
                        RunningIndicator()
                    }
                }
            }

            // ── Error Bar ──────────────────────────────────────
            AnimatedVisibility(visible = uiState.error != null) {
                uiState.error?.let { error ->
                    ErrorBar(
                        error = error,
                        onDismiss = { viewModel.dismissError() }
                    )
                }
            }

            // ── Input Area ─────────────────────────────────────
            TerminalInput(
                inputText = uiState.inputText,
                onInputChange = { viewModel.updateInput(it) },
                onExecute = {
                    viewModel.executeCommand()
                    focusManager.clearFocus()
                },
                onHistoryUp = { viewModel.navigateHistoryUp() },
                onHistoryDown = { viewModel.navigateHistoryDown() },
                isRunning = uiState.isRunning,
                currentDir = uiState.activeSession?.workingDirectory ?: "/"
            )
        }
    }
}

// ── Welcome Message ────────────────────────────────────────────
@Composable
private fun WelcomeMessage() {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = "Welcome to OpenClaude Terminal",
            color = AccentViolet,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Type 'help' for a list of commands.",
            color = TextTertiary,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Supports built-in commands and system commands.",
            color = TextQuaternary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

// ── Terminal Line View ─────────────────────────────────────────
@Composable
private fun TerminalLineView(line: TerminalLine) {
    val color = when (line.type) {
        TerminalLineType.INPUT -> AccentViolet
        TerminalLineType.OUTPUT -> TextSecondary
        TerminalLineType.ERROR -> StatusRed
        TerminalLineType.SYSTEM -> TextQuaternary
        TerminalLineType.SUCCESS -> StatusGreen
        TerminalLineType.INFO -> BrandIndigoMuted
        TerminalLineType.WARNING -> StatusAmber
    }

    // Skip clear escape sequences
    if (line.content.contains("\u001B[2J")) return

    Text(
        text = line.content,
        color = color,
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        modifier = Modifier.padding(vertical = 0.5.dp)
    )
}

// ── Running Indicator ──────────────────────────────────────────
@Composable
private fun RunningIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            color = AccentViolet,
            strokeWidth = 1.5.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Running...",
            color = TextTertiary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

// ── Error Bar ──────────────────────────────────────────────────
@Composable
private fun ErrorBar(
    error: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = StatusRed.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = StatusRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = error,
                color = StatusRed,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = StatusRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ── Terminal Input ─────────────────────────────────────────────
@Composable
private fun TerminalInput(
    inputText: String,
    onInputChange: (String) -> Unit,
    onExecute: () -> Unit,
    onHistoryUp: () -> Unit,
    onHistoryDown: () -> Unit,
    isRunning: Boolean,
    currentDir: String
) {
    Surface(
        color = PanelDark,
        tonalElevation = 0.dp
    ) {
        Column {
            // ── Prompt Line ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Directory indicator
                Text(
                    text = "~${currentDir.removePrefix("/data/data").removePrefix("/home")}",
                    color = TextQuaternary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            // ── Input Field ────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prompt symbol
                Text(
                    text = "$ ",
                    color = AccentViolet,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 40.dp),
                    placeholder = {
                        Text(
                            "Enter command...",
                            color = TextQuaternary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentViolet.copy(alpha = 0.3f),
                        unfocusedBorderColor = BorderPrimary,
                        focusedContainerColor = SurfaceLevel3,
                        unfocusedContainerColor = SurfaceLevel3,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentViolet
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = TextPrimary
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { onExecute() }
                    ),
                    singleLine = true,
                    enabled = !isRunning
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send button
                IconButton(
                    onClick = onExecute,
                    enabled = inputText.isNotBlank() && !isRunning,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isRunning) AccentViolet
                            else SurfaceLevel3
                        )
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TextPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else TextQuaternary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Terminal Tab Bar ───────────────────────────────────────────
@Composable
private fun TerminalTabBar(
    sessions: List<String>,
    activeIndex: Int,
    onTabClick: (Int) -> Unit,
    onCloseTab: (Int) -> Unit
) {
    Surface(
        color = PanelDark,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            sessions.forEachIndexed { index, name ->
                val isActive = index == activeIndex

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onTabClick(index) },
                    color = if (isActive) SurfaceLevel3 else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                            color = if (isActive) TextPrimary else TextTertiary,
                            maxLines = 1
                        )
                        if (sessions.size > 1) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onCloseTab(index) },
                                tint = TextQuaternary
                            )
                        }
                    }
                }
            }
        }
    }
}
