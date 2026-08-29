package com.velocity.ide.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ai.settings.AiSettingsManager
import com.velocity.ide.ui.theme.*

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    placeholder: String = "",
    textSize: Int = 14
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurface, RoundedCornerShape(RadiusControl))
                .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(color = TextPrimary, fontSize = textSize.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentOrange),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(placeholder, color = TextMuted, fontSize = textSize.sp)
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(if (checked) AccentOrange else BgSurface, RoundedCornerShape(RadiusControl))
                .border(1.dp, if (checked) AccentOrange else BorderDefault, RoundedCornerShape(RadiusControl)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun CustomRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(if (selected) AccentSoft else BgSurface, RoundedCornerShape(RadiusPill))
                .border(1.dp, if (selected) AccentOrange else BorderDefault, RoundedCornerShape(RadiusPill)),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(modifier = Modifier.size(8.dp).background(AccentOrange, RoundedCornerShape(RadiusPill)))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun SettingsScreen(
    settingsManager: AiSettingsManager,
    onBack: () -> Unit,
    onAccentChange: (String) -> Unit = {},
    onResetData: () -> Unit = {}
) {
    // Provider selections
    var selectedProvider by remember { mutableStateOf(settingsManager.selectedProvider) }
    // Keys / models
    var geminiKey by remember { mutableStateOf(settingsManager.geminiApiKey) }
    var openRouterKey by remember { mutableStateOf(settingsManager.openRouterApiKey) }
    var openRouterModel by remember { mutableStateOf(settingsManager.openRouterModel) }
    var openaiKey by remember { mutableStateOf(settingsManager.openaiApiKey) }
    var openaiModel by remember { mutableStateOf(settingsManager.openaiModel) }
    var anthropicKey by remember { mutableStateOf(settingsManager.anthropicApiKey) }
    var anthropicModel by remember { mutableStateOf(settingsManager.anthropicModel) }
    var zenKey by remember { mutableStateOf(settingsManager.zenApiKey) }
    var zenModel by remember { mutableStateOf(settingsManager.zenModel) }
    var nvidiaKey by remember { mutableStateOf(settingsManager.nvidiaApiKey) }
    var nvidiaModel by remember { mutableStateOf(settingsManager.nvidiaModel) }
    var customName by remember { mutableStateOf(settingsManager.customProviderName) }
    var customBase by remember { mutableStateOf(settingsManager.customBaseUrl) }
    var customKey by remember { mutableStateOf(settingsManager.customApiKey) }
    var customModel by remember { mutableStateOf(settingsManager.customModel) }
    var localBase by remember { mutableStateOf(settingsManager.localBaseUrl) }
    var localKey by remember { mutableStateOf(settingsManager.localApiKey) }
    var localModel by remember { mutableStateOf(settingsManager.localModel) }

    // Agent
    var agentMode by remember { mutableStateOf(settingsManager.agentMode) }
    var temperature by remember { mutableStateOf(settingsManager.temperature) }
    var maxSteps by remember { mutableStateOf(settingsManager.maxSteps) }
    var askEdit by remember { mutableStateOf(settingsManager.askBeforeEditing) }
    var askRun by remember { mutableStateOf(settingsManager.askBeforeRunning) }
    var askDelete by remember { mutableStateOf(settingsManager.askBeforeDeleting) }

    // Appearance
    var accentName by remember { mutableStateOf(settingsManager.accentColorName) }

    // Editor / Terminal
    var editorFont by remember { mutableStateOf(settingsManager.editorFontSize) }
    var editorLines by remember { mutableStateOf(settingsManager.editorLineNumbers) }
    var termFont by remember { mutableStateOf(settingsManager.terminalFontSize) }
    var keepOn by remember { mutableStateOf(settingsManager.keepScreenOn) }

    // Git / Deploy
    var gitName by remember { mutableStateOf(settingsManager.gitAuthorName) }
    var gitEmail by remember { mutableStateOf(settingsManager.gitAuthorEmail) }
    var githubPat by remember { mutableStateOf(settingsManager.githubPat) }
    var vercelToken by remember { mutableStateOf(settingsManager.vercelToken) }
    var netlifyToken by remember { mutableStateOf(settingsManager.netlifyToken) }

    var confirmReset by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(BgPrimary)) {
        // TopBar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgElevated)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Divider(color = BorderDefault)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // ── Appearance ─────────────────────────────────────────────
            SettingsSection("Appearance", "Accent color applied live across the app") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AccentPresets.values.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(RadiusControl))
                                .background(preset.primary)
                                .border(
                                    2.dp,
                                    if (accentName == preset.name) Color.White else Color.Transparent,
                                    RoundedCornerShape(RadiusControl)
                                )
                                .clickable {
                                    accentName = preset.name
                                    settingsManager.accentColorName = preset.name
                                    onAccentChange(preset.name)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (accentName == preset.name) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // ── AI Providers ───────────────────────────────────────────
            SettingsSection("AI Providers", "The selected provider powers the agent") {
                val providers = listOf(
                    "gemini" to "Google Gemini",
                    "anthropic" to "Anthropic Claude",
                    "openai" to "OpenAI",
                    "openrouter" to "OpenRouter",
                    "opencodezen" to "OpenCode Zen",
                    "nvidia" to "NVIDIA Build",
                    "custom" to "Custom Provider",
                    "local" to "Local Model"
                )
                providers.forEach { (id, label) ->
                    CustomRadioButton(
                        selected = selectedProvider == id,
                        onClick = {
                            selectedProvider = id
                            settingsManager.selectedProvider = id
                        },
                        label = label
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedProvider) {
                    "gemini" -> CustomTextField(geminiKey, { geminiKey = it; settingsManager.geminiApiKey = it }, "Gemini API Key", isPassword = true, placeholder = "AIza...")
                    "anthropic" -> {
                        CustomTextField(anthropicKey, { anthropicKey = it; settingsManager.anthropicApiKey = it }, "Anthropic API Key", isPassword = true, placeholder = "sk-ant-...")
                        CustomTextField(anthropicModel, { anthropicModel = it; settingsManager.anthropicModel = it }, "Model", placeholder = "claude-3-5-sonnet-latest")
                    }
                    "openai" -> {
                        CustomTextField(openaiKey, { openaiKey = it; settingsManager.openaiApiKey = it }, "OpenAI API Key", isPassword = true, placeholder = "sk-...")
                        CustomTextField(openaiModel, { openaiModel = it; settingsManager.openaiModel = it }, "Model", placeholder = "gpt-4o-mini")
                    }
                    "openrouter" -> {
                        CustomTextField(openRouterKey, { openRouterKey = it; settingsManager.openRouterApiKey = it }, "OpenRouter API Key", isPassword = true, placeholder = "sk-or-...")
                        CustomTextField(openRouterModel, { openRouterModel = it; settingsManager.openRouterModel = it }, "Model", placeholder = "anthropic/claude-3-haiku")
                    }
                    "opencodezen" -> {
                        CustomTextField(zenKey, { zenKey = it; settingsManager.zenApiKey = it }, "OpenCode Zen API Key", isPassword = true)
                        CustomTextField(zenModel, { zenModel = it; settingsManager.zenModel = it }, "Model", placeholder = "opencodezen/sonnet")
                    }
                    "nvidia" -> {
                        CustomTextField(nvidiaKey, { nvidiaKey = it; settingsManager.nvidiaApiKey = it }, "NVIDIA Build API Key", isPassword = true)
                        CustomTextField(nvidiaModel, { nvidiaModel = it; settingsManager.nvidiaModel = it }, "Model", placeholder = "nvidia/nemotron-nano-8b-v1")
                    }
                    "custom" -> {
                        CustomTextField(customName, { customName = it; settingsManager.customProviderName = it }, "Provider Name", placeholder = "My endpoint")
                        CustomTextField(customBase, { customBase = it; settingsManager.customBaseUrl = it }, "Base URL", placeholder = "https://host/v1")
                        CustomTextField(customKey, { customKey = it; settingsManager.customApiKey = it }, "API Key (optional)", isPassword = true)
                        CustomTextField(customModel, { customModel = it; settingsManager.customModel = it }, "Model", placeholder = "model-name")
                    }
                    "local" -> {
                        CustomTextField(localBase, { localBase = it; settingsManager.localBaseUrl = it }, "Endpoint URL", placeholder = "http://10.0.2.2:11434/v1")
                        CustomTextField(localKey, { localKey = it; settingsManager.localApiKey = it }, "API Key (optional)", isPassword = true)
                        CustomTextField(localModel, { localModel = it; settingsManager.localModel = it }, "Model", placeholder = "qwen2.5-coder:14b")
                        Text(
                            "On-device inference runtime is not bundled. This connects to LAN OpenAI-compatible servers such as Ollama or llama.cpp.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // ── Agent ──────────────────────────────────────────────────
            SettingsSection("Agent", "Mode, permissions and run limits") {
                Text("Mode", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("ASK", "EDIT", "AGENT").forEach { m ->
                        CustomRadioButton(
                            selected = agentMode == m,
                            onClick = {
                                agentMode = m
                                settingsManager.agentMode = m
                            },
                            label = m
                        )
                    }
                }
                Text(
                    "Ask: read-only · Edit: files, commands and deletions still ask · Agent: full autonomy with approvals.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomCheckbox(
                    checked = askEdit,
                    onCheckedChange = { askEdit = it; settingsManager.askBeforeEditing = it },
                    label = "Ask before editing files"
                )
                CustomCheckbox(
                    checked = askRun,
                    onCheckedChange = { askRun = it; settingsManager.askBeforeRunning = it },
                    label = "Ask before running commands"
                )
                CustomCheckbox(
                    checked = askDelete,
                    onCheckedChange = { askDelete = it; settingsManager.askBeforeDeleting = it },
                    label = "Ask before deleting files"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Temperature: %.2f".format(temperature),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = temperature.toFloat(),
                    onValueChange = { temperature = it.toDouble(); settingsManager.temperature = temperature },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Max steps: $maxSteps", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Slider(
                        value = maxSteps.toFloat(),
                        onValueChange = { maxSteps = it.toInt().coerceAtLeast(5); settingsManager.maxSteps = maxSteps },
                        valueRange = 5f..100f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                    )
                }
            }

            // ── Editor ─────────────────────────────────────────────────
            SettingsSection("Editor", "Code editing preferences") {
                Text(
                    "Font size: $editorFont",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = editorFont.toFloat(),
                    onValueChange = { editorFont = it.toInt().coerceIn(12, 18); settingsManager.editorFontSize = editorFont },
                    valueRange = 12f..18f,
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
                CustomCheckbox(
                    checked = editorLines,
                    onCheckedChange = { editorLines = it; settingsManager.editorLineNumbers = it },
                    label = "Show line numbers"
                )
            }

            // ── Terminal ───────────────────────────────────────────────
            SettingsSection("Terminal", "Shell preferences") {
                Text(
                    "Font size: $termFont",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = termFont.toFloat(),
                    onValueChange = { termFont = it.toInt().coerceIn(10, 18); settingsManager.terminalFontSize = termFont },
                    valueRange = 10f..18f,
                    colors = SliderDefaults.colors(thumbColor = AccentOrange, activeTrackColor = AccentOrange)
                )
                CustomCheckbox(
                    checked = keepOn,
                    onCheckedChange = { keepOn = it; settingsManager.keepScreenOn = it },
                    label = "Keep screen on while using terminal"
                )
            }

            // ── MCP Servers ────────────────────────────────────────────
            SettingsSection("MCP Servers", "HTTP/SSE JSON-RPC tool servers. stdio servers are not supported on Android.") {
                McpServerEditor(mcpJson) { newJson ->
                    mcpJson = newJson
                    settingsManager.mcpServersJson = newJson
                }
            }

            // ── Source Control ─────────────────────────────────────────
            SettingsSection("Source Control (Git)", "Author identity and repository credentials") {
                CustomTextField(gitName, { gitName = it; settingsManager.gitAuthorName = it }, "Author Name")
                CustomTextField(gitEmail, { gitEmail = it; settingsManager.gitAuthorEmail = it }, "Author Email")
                CustomTextField(githubPat, { githubPat = it; settingsManager.githubPat = it }, "GitHub Personal Access Token", isPassword = true)
            }

            // ── Deployment ─────────────────────────────────────────────
            SettingsSection("Deployment Platforms", "Tokens used by the Deploy flow") {
                CustomTextField(vercelToken, { vercelToken = it; settingsManager.vercelToken = it }, "Vercel Access Token", isPassword = true)
                CustomTextField(netlifyToken, { netlifyToken = it; settingsManager.netlifyToken = it }, "Netlify Personal Access Token", isPassword = true)
            }

            // ── Data ───────────────────────────────────────────────────
            SettingsSection("Data", "Destructive actions") {
                Button(
                    onClick = { confirmReset = true },
                    shape = RoundedCornerShape(RadiusControl),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusErrorSoft, contentColor = StatusError),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StatusError),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete all projects, checkpoints and run history")
                }
            }

            // ── About ──────────────────────────────────────────────────
            SettingsSection("About", "Velocity-IDE · Build software. Anywhere.") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Version 1.0 · Android (Jetpack Compose)", color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            containerColor = BgSurface,
            shape = RoundedCornerShape(RadiusCard),
            title = { Text("Delete all data?", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("This permanently removes all projects, chats, checkpoints and run history from this device.", color = TextSecondary, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        confirmReset = false
                        onResetData()
                    },
                    shape = RoundedCornerShape(RadiusControl),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError)
                ) {
                    Text("Delete everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = BgSurface),
        shape = RoundedCornerShape(RadiusCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = AccentOrange, fontSize = 15.sp)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(subtitle, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun McpServerEditor(json: String, onChanged: (String) -> Unit) {
    val initialServers = try {
        org.json.JSONArray(json).let { arr ->
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Triple(o.optString("name", "MCP"), o.optString("url", ""), o.optString("token", ""))
            }
        }
    } catch (e: Exception) {
        emptyList<Triple<String, String, String>>()
    }
    var servers by remember { mutableStateOf(initialServers) }

    fun persist(newList: List<Triple<String, String, String>>) {
        servers = newList
        val arr = org.json.JSONArray()
        newList.forEach { (n, u, t) ->
            arr.put(org.json.JSONObject().put("name", n).put("url", u).put("token", t))
        }
        onChanged(arr.toString())
    }

    servers.forEachIndexed { index, server ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(server.first, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(server.second, color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
            IconButton(onClick = {
                persist(servers.filterIndexed { i, _ -> i != index })
            }) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = StatusError, modifier = Modifier.size(16.dp))
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    if (showAdd) {
        CustomTextField(name, { name = it }, "Server name", placeholder = "filesystem")
        CustomTextField(url, { url = it }, "URL", placeholder = "http://10.0.2.2:8000/mcp")
        CustomTextField(token, { token = it }, "Auth token (optional)", isPassword = true)
        Button(
            onClick = {
                if (name.isNotBlank() && url.isNotBlank()) {
                    persist(servers + Triple(name, url, token))
                    name = ""; url = ""; token = ""
                    showAdd = false
                }
            },
            shape = RoundedCornerShape(RadiusControl),
            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add server")
        }
    } else {
        TextButton(onClick = { showAdd = true }) {
            Icon(Icons.Default.Add, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add MCP server", color = AccentOrange)
        }
    }
}