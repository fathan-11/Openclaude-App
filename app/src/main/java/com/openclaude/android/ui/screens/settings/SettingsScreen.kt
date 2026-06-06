package com.openclaude.android.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclaude.android.core.ui.theme.*
import com.openclaude.android.data.model.Provider

// ═══════════════════════════════════════════════════════════════
// LINEAR-STYLE SETTINGS SCREEN
// Ultra-minimal dark settings, precision-engineered
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showApiKey by remember { mutableStateOf(false) }
    var showSaved by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            showSaved = true
            kotlinx.coroutines.delay(2000)
            showSaved = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // ── Provider Selection ─────────────────────────────
            SettingsSection(title = "AI Provider") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Provider.entries.forEach { provider ->
                        LinearProviderCard(
                            provider = provider,
                            selected = uiState.currentProvider == provider,
                            onClick = { viewModel.setProvider(provider) }
                        )
                    }
                }
            }

            // ── API Configuration ──────────────────────────────
            SettingsSection(title = "API Configuration") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LinearTextField(
                        value = uiState.apiKey,
                        onValueChange = { viewModel.setApiKey(it) },
                        label = "API Key",
                        placeholder = "sk-...",
                        isPassword = true,
                        showPassword = showApiKey,
                        onTogglePassword = { showApiKey = !showApiKey }
                    )

                    LinearTextField(
                        value = uiState.baseUrl,
                        onValueChange = { viewModel.setBaseUrl(it) },
                        label = "Base URL",
                        placeholder = "https://api.openai.com/v1"
                    )

                    LinearTextField(
                        value = uiState.selectedModel,
                        onValueChange = { viewModel.setModel(it) },
                        label = "Model",
                        placeholder = "gpt-4"
                    )
                }
            }

            // ── Appearance ─────────────────────────────────────
            SettingsSection(title = "Appearance") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Dark Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            "Use dark theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandIndigo,
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = SurfaceLevel3
                        )
                    )
                }
            }

            // ── Quick Models ───────────────────────────────────
            SettingsSection(title = "Quick Models") {
                val presets = listOf(
                    "gpt-4" to "GPT-4",
                    "gpt-4o-mini" to "GPT-4o Mini",
                    "gemini-2.0-flash" to "Gemini 2.0 Flash",
                    "deepseek-chat" to "DeepSeek Chat",
                    "qwen/qwen3-235b-a22b" to "Qwen 3 235B"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presets.take(3).forEach { (modelId, name) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setModel(modelId) },
                            color = SurfaceLevel3,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                fontSize = 12.sp,
                                color = if (uiState.selectedModel == modelId) AccentViolet else TextSecondary,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presets.drop(3).forEach { (modelId, name) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setModel(modelId) },
                            color = SurfaceLevel3,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                fontSize = 12.sp,
                                color = if (uiState.selectedModel == modelId) AccentViolet else TextSecondary,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // ── Save Button ────────────────────────────────────
            Button(
                onClick = { viewModel.saveSettings() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandIndigo,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save Settings", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            // ── Saved Confirmation ─────────────────────────────
            AnimatedVisibility(visible = showSaved) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = StatusGreen.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Settings saved!",
                            color = StatusGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Settings Section ──────────────────────────────────────────
@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

// ── Linear Text Field ─────────────────────────────────────────
@Composable
private fun LinearTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = TextQuaternary, fontSize = 14.sp)
            },
            trailingIcon = if (isPassword && onTogglePassword != null) {
                {
                    IconButton(onClick = onTogglePassword, modifier = Modifier.size(32.dp)) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle visibility",
                            tint = TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !showPassword) PasswordVisualTransformation() else VisualTransformation.None,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandIndigo,
                unfocusedBorderColor = BorderPrimary,
                focusedContainerColor = SurfaceLevel3,
                unfocusedContainerColor = SurfaceLevel3,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = BrandIndigo
            ),
            textStyle = LocalTextStyle.current.copy(
                color = TextPrimary,
                fontSize = 14.sp
            )
        )
    }
}

// ── Linear Provider Card ──────────────────────────────────────
@Composable
private fun LinearProviderCard(
    provider: Provider,
    selected: Boolean,
    onClick: () -> Unit
) {
    val (icon, description) = when (provider) {
        Provider.OPENAI -> Icons.Default.SmartToy to "OpenAI GPT models"
        Provider.GEMINI -> Icons.Default.AutoAwesome to "Google Gemini models"
        Provider.OLLAMA -> Icons.Default.Computer to "Local Ollama models"
        Provider.DEEPSEEK -> Icons.Default.Psychology to "DeepSeek models"
        Provider.OPENROUTER -> Icons.Default.Router to "OpenRouter (200+ models)"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = if (selected) BrandIndigo.copy(alpha = 0.08f) else SurfaceLevel3,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Icon ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) BrandIndigo.copy(alpha = 0.15f)
                        else SurfaceLevel2
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (selected) AccentViolet else TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // ── Text ───────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    provider.name,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary,
                    fontSize = 12.sp
                )
            }

            // ── Check ──────────────────────────────────────────
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(BrandIndigo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
