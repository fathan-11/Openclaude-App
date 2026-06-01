package com.openclaude.android.ui.screens.mcp

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
import com.openclaude.android.data.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun McpScreen(
    viewModel: McpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MCP Servers", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.toggleAddDialog() }) {
                        Icon(Icons.Default.Add, "Add Server")
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
            uiState.servers.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Hub, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        Text("No MCP servers configured", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.toggleAddDialog() }) {
                            Text("Add Server")
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.servers) { server ->
                        McpServerCard(
                            server = server,
                            onConnect = { viewModel.connectServer(server.id) },
                            onDisconnect = { viewModel.disconnectServer(server.id) },
                            onRemove = { viewModel.removeServer(server.id) },
                            onSelect = { viewModel.selectServer(server) }
                        )
                    }
                }
            }
        }

        // Add server dialog
        if (uiState.showAddDialog) {
            AddServerDialog(
                onDismiss = { viewModel.toggleAddDialog() },
                onAdd = { name, url, desc -> viewModel.addServer(name, url, desc) }
            )
        }

        // Server detail
        uiState.selectedServer?.let { server ->
            ServerDetailSheet(
                server = server,
                onDismiss = { viewModel.selectServer(server.copy()) }
            )
        }
    }
}

@Composable
private fun McpServerCard(
    server: McpServer,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onRemove: () -> Unit,
    onSelect: () -> Unit
) {
    val statusColor = when (server.status) {
        McpServerStatus.CONNECTED -> MaterialTheme.colorScheme.primary
        McpServerStatus.CONNECTING -> MaterialTheme.colorScheme.tertiary
        McpServerStatus.ERROR -> MaterialTheme.colorScheme.error
        McpServerStatus.DISCONNECTED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onSelect
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Hub, null, tint = statusColor)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(server.name, fontWeight = FontWeight.SemiBold)
                    Text(server.url, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Badge(containerColor = statusColor)
            }

            if (server.tools.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text("${server.tools.size} tools available", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (server.status) {
                    McpServerStatus.CONNECTED -> {
                        OutlinedButton(onClick = onDisconnect, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.LinkOff, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Disconnect")
                        }
                    }
                    McpServerStatus.DISCONNECTED, McpServerStatus.ERROR -> {
                        Button(onClick = onConnect, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Link, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Connect")
                        }
                    }
                    McpServerStatus.CONNECTING -> {
                        Button(onClick = {}, enabled = false, modifier = Modifier.weight(1f)) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(4.dp))
                            Text("Connecting...")
                        }
                    }
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }

            server.errorMessage?.let { error ->
                Spacer(Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun AddServerDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add MCP Server") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onAdd(name, url, description) }, enabled = name.isNotBlank() && url.isNotBlank()) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ServerDetailSheet(server: McpServer, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(server.name) },
        text = {
            Column {
                Text("URL: ${server.url}")
                Text("Status: ${server.status}")
                if (server.description.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(server.description)
                }
                if (server.tools.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Tools:", fontWeight = FontWeight.SemiBold)
                    server.tools.forEach { tool ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.Build, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(tool.name, fontWeight = FontWeight.SemiBold)
                                Text(tool.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
