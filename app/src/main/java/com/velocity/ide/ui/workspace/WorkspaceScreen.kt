package com.velocity.ide.ui.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
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
import com.velocity.ide.data.database.ChatEntity
import com.velocity.ide.data.database.MessageEntity
import com.velocity.ide.data.database.ProjectEntity
import com.velocity.ide.data.database.ProjectMemoryEntity
import com.velocity.ide.ui.theme.*
import java.io.File

enum class WorkspaceTab {
    FILES, CODE, AI, TERMINAL, MEMORY, GIT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(
    project: ProjectEntity,
    fileTree: List<File>,
    openTabs: List<File>,
    activeTab: File?,
    fileContent: String,
    onFileContentChange: (String) -> Unit,
    onTabSelected: (File) -> Unit,
    onTabClosed: (File) -> Unit,
    onFileSelected: (File) -> Unit,
    onSaveFile: () -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
    terminalOutput: String,
    onExecuteCommand: (String) -> Unit,
    onBackToHome: () -> Unit,
    onOpenAgent: () -> Unit,
    onAiRequest: (String) -> Unit = {},
    onNavigateDeploy: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    memoriesFlow: kotlinx.coroutines.flow.Flow<List<com.velocity.ide.data.database.ProjectMemoryEntity>>,
    gitManager: com.velocity.ide.data.vcs.GitManager,
    settingsManager: com.velocity.ide.ai.settings.AiSettingsManager,
    editorFontSize: Int = 14,
    editorLineNumbers: Boolean = true
) {
    var selectedWorkspaceTab by remember { mutableStateOf(WorkspaceTab.CODE) }

    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var showContextualAiSheet by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgElevated)
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Home", tint = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(StatusSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Running", fontSize = 12.sp, color = StatusSuccess)
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        IconButton(
                            onClick = { selectedWorkspaceTab = WorkspaceTab.MEMORY },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Storage,
                                contentDescription = "Memory",
                                tint = if (selectedWorkspaceTab == WorkspaceTab.MEMORY) AccentOrange else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .border(1.dp, StatusSuccess, RoundedCornerShape(RadiusPill))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(StatusSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Passed", fontSize = 10.sp, color = StatusSuccess)
                        }

                        Box {
                            IconButton(onClick = { showOverflowMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary)
                            }
                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Deploy", color = TextPrimary) },
                                    leadingIcon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = AccentOrange) },
                                    onClick = {
                                        showOverflowMenu = false
                                        onNavigateDeploy()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings", color = TextPrimary) },
                                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = AccentOrange) },
                                    onClick = {
                                        showOverflowMenu = false
                                        onNavigateSettings()
                                    }
                                )
                            }
                        }
                    }
                }

                // Tab Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgPrimary)
                        .border(1.dp, BorderDefault),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedWorkspaceTab = WorkspaceTab.FILES }) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = "Files",
                            tint = if (selectedWorkspaceTab == WorkspaceTab.FILES) AccentOrange else TextSecondary
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderDefault))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedWorkspaceTab = WorkspaceTab.CODE }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val isCode = selectedWorkspaceTab == WorkspaceTab.CODE
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                tint = if (isCode) AccentOrange else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Code",
                                color = if (isCode) TextPrimary else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = if (isCode) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        if (isCode) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(AccentOrange)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedWorkspaceTab = WorkspaceTab.TERMINAL }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val isTerminal = selectedWorkspaceTab == WorkspaceTab.TERMINAL
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Terminal,
                                contentDescription = null,
                                tint = if (isTerminal) AccentOrange else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Terminal",
                                color = if (isTerminal) TextPrimary else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = if (isTerminal) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                        if (isTerminal) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(AccentOrange)
                            )
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(BorderDefault))
                    IconButton(onClick = { selectedWorkspaceTab = WorkspaceTab.GIT }) {
                        Icon(
                            Icons.Default.AccountTree,
                            contentDescription = "Git",
                            tint = if (selectedWorkspaceTab == WorkspaceTab.GIT) AccentOrange else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        containerColor = BgPrimary
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedWorkspaceTab) {
                WorkspaceTab.AI -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = onOpenAgent,
                            shape = RoundedCornerShape(RadiusControl),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Velocity AI Assistant")
                        }
                    }
                }
                WorkspaceTab.FILES -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                project.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Row {
                                IconButton(
                                    onClick = { showCreateFileDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NoteAdd,
                                        contentDescription = "New File",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { showCreateFolderDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CreateNewFolder,
                                        contentDescription = "New Folder",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(fileTree) { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(RadiusControl))
                                        .clickable {
                                            onFileSelected(file)
                                            if (file.isFile) selectedWorkspaceTab = WorkspaceTab.CODE
                                        }
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                                        contentDescription = null,
                                        tint = if (file.isDirectory) StatusWarning else AccentOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = file.name,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                WorkspaceTab.CODE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Multi-file tabs
                        if (openTabs.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(BgSurface)
                                    .horizontalScroll(rememberScrollState())
                                    .border(1.dp, BorderDefault),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                openTabs.forEach { tab ->
                                    val isActive = activeTab?.absolutePath == tab.absolutePath
                                    Row(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .background(if (isActive) BgElevated else Color.Transparent)
                                            .border(1.dp, if (isActive) AccentOrange.copy(alpha = 0.6f) else BorderDefault)
                                            .clickable { onTabSelected(tab) }
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = tab.name,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (isActive) TextPrimary else TextSecondary
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = TextMuted,
                                            modifier = Modifier.size(14.dp).clickable { onTabClosed(tab) }
                                        )
                                    }
                                }
                            }
                        }

                        // Editor Area
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            if (activeTab != null) {
                                CodeEditor(
                                    value = fileContent,
                                    onValueChange = onFileContentChange,
                                    modifier = Modifier.fillMaxSize(),
                                    fontSizeSp = editorFontSize,
                                    showLineNumbers = editorLineNumbers
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Code,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "Select a file from the Files tab",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // Editor Bottom Toolbar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(BgElevated)
                                .border(1.dp, BorderDefault)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                IconButton(onClick = { /* TODO */ }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                                }
                                IconButton(onClick = onSaveFile) {
                                    Icon(Icons.Default.Save, contentDescription = "Save", tint = TextSecondary)
                                }
                                IconButton(onClick = { /* TODO Run */ }) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = StatusSuccess)
                                }
                            }
                            IconButton(onClick = { showContextualAiSheet = true }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Context", tint = AccentOrange)
                            }
                        }
                    }
                }

                WorkspaceTab.TERMINAL -> {
                    Column(
                        modifier = Modifier.fillMaxSize().background(BgPrimary).padding(16.dp)
                    ) {
                        Text(
                            "Terminal",
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(BgTerminal, RoundedCornerShape(RadiusControl))
                                .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = if (terminalOutput.isEmpty()) "> _" else terminalOutput,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        var cmdInput by remember { mutableStateOf("") }
                        val isNl = cmdInput.isNotBlank() && !com.velocity.ide.ui.terminal.ShellDetect.isLikelyCommand(cmdInput)
                        if (isNl) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    onClick = {
                                        onAiRequest(cmdInput)
                                        cmdInput = ""
                                    },
                                    color = AccentSoft,
                                    shape = RoundedCornerShape(RadiusPill),
                                    border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = AccentOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "Run with AI",
                                            color = AccentOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgSurface, RoundedCornerShape(RadiusControl))
                                .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                ">",
                                color = AccentOrange,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = cmdInput,
                                onValueChange = { cmdInput = it },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                decorationBox = { innerTextField ->
                                    if (cmdInput.isEmpty()) {
                                        Text(
                                            "Enter command...",
                                            color = TextMuted,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            IconButton(
                                onClick = {
                                    if (cmdInput.isNotBlank()) {
                                        onExecuteCommand(cmdInput)
                                        cmdInput = ""
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Execute", tint = StatusSuccess)
                            }
                        }
                    }
                }

                WorkspaceTab.MEMORY -> {
                    MemoryScreen(memoriesFlow = memoriesFlow)
                }

                WorkspaceTab.GIT -> {
                    GitScreen(
                        projectDir = File(project.path),
                        gitManager = gitManager,
                        settingsManager = settingsManager
                    )
                }
            }
        }
    }

    if (showCreateFileDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFileDialog = false },
            containerColor = BgSurface,
            shape = RoundedCornerShape(RadiusCard),
            title = { Text("Create File", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("File Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(RadiusControl),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            onCreateFile(newItemName); newItemName = ""; showCreateFileDialog = false
                        }
                    },
                    shape = RoundedCornerShape(RadiusControl)
                ) {
                    Text("Create")
                }
            }
        )
    }

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            containerColor = BgSurface,
            shape = RoundedCornerShape(RadiusCard),
            title = { Text("Create Folder", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    label = { Text("Folder Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(RadiusControl),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newItemName.isNotBlank()) {
                            onCreateFolder(newItemName); newItemName = ""; showCreateFolderDialog = false
                        }
                    },
                    shape = RoundedCornerShape(RadiusControl)
                ) {
                    Text("Create")
                }
            }
        )
    }

    if (showContextualAiSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContextualAiSheet = false },
            containerColor = BgElevated,
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text(
                    "AI for ${activeTab?.name ?: "Project"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                val options = listOf("Explain this file", "Fix errors", "Refactor this component", "Write tests")
                options.forEach { opt ->
                    Text(
                        text = opt,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(RadiusControl))
                            .clickable {
                                onOpenAgent()
                                showContextualAiSheet = false
                            }
                            .padding(vertical = 14.dp)
                    )
                    Divider(color = BorderDefault)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}