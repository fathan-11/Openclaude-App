package com.openclaude.android.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.openclaude.android.data.model.Provider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderChip(
    selectedProvider: Provider,
    onProviderSelected: (Provider) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        AssistChip(
            onClick = { expanded = true },
            label = { Text(selectedProvider.displayName) },
            leadingIcon = {
                Icon(
                    imageVector = providerIcon(selectedProvider),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Provider.entries.forEach { provider ->
                DropdownMenuItem(
                    text = { Text(provider.displayName) },
                    onClick = {
                        onProviderSelected(provider)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = providerIcon(provider),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}

private fun providerIcon(provider: Provider): ImageVector {
    return when (provider) {
        Provider.OPENAI -> Icons.Default.SmartToy
        Provider.GEMINI -> Icons.Default.AutoAwesome
        Provider.OLLAMA -> Icons.Default.Computer
        Provider.DEEPSEEK -> Icons.Default.Psychology
        Provider.OPENROUTER -> Icons.Default.Router
    }
}
