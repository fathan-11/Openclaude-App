package com.openclaude.android.ui.screens.advanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.data.model.VoiceState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(viewModel: VoiceViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Voice Mode", fontWeight = FontWeight.Bold) }) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            // Mic button
            FilledIconButton(
                onClick = { if (uiState.state == VoiceState.LISTENING) viewModel.stopListening() else viewModel.startListening() },
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = when (uiState.state) { VoiceState.LISTENING -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.primary }
                )
            ) {
                Icon(
                    when (uiState.state) { VoiceState.LISTENING -> Icons.Default.Mic; VoiceState.PROCESSING -> Icons.Default.HourglassTop; else -> Icons.Default.MicNone },
                    contentDescription = "Mic",
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                when (uiState.state) { VoiceState.LISTENING -> "Listening..."; VoiceState.PROCESSING -> "Processing..."; VoiceState.SPEAKING -> "Speaking..."; else -> "Tap to speak" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            if (uiState.recognizedText.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text(uiState.recognizedText, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(32.dp))

            // Settings
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.languages.take(3).forEach { (code, name) ->
                    FilterChip(selected = uiState.config.language == code, onClick = { viewModel.setLanguage(code) }, label = { Text(name) })
                }
            }
        }
    }
}
