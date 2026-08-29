package com.velocity.ide

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.velocity.ide.ai.agent.*
import com.velocity.ide.data.database.AppDatabase
import com.velocity.ide.data.database.ChatEntity
import com.velocity.ide.data.database.MessageEntity
import com.velocity.ide.data.database.ProjectEntity
import com.velocity.ide.data.filesystem.ProjectFileSystem
import com.velocity.ide.runtime.ProcessManager
import com.velocity.ide.ui.MainLayout
import com.velocity.ide.ui.Screen
import com.velocity.ide.ui.agent.AgentScreen
import com.velocity.ide.ui.common.UiEmptyState
import com.velocity.ide.ui.home.HomeScreen
import com.velocity.ide.ui.theme.AccentOrange
import com.velocity.ide.ui.theme.BgPrimary
import com.velocity.ide.ui.theme.RadiusControl
import com.velocity.ide.ui.theme.VelocityTheme
import com.velocity.ide.ui.workspace.WorkspaceScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var fileSystem: ProjectFileSystem
    private lateinit var processManager: ProcessManager
    private lateinit var toolRegistry: ToolRegistry
    private lateinit var contextEngine: ProjectContextEngine
    private lateinit var providerRegistry: com.velocity.ide.ai.provider.ProviderRegistry
    private lateinit var checkpointManager: com.velocity.ide.ai.agent.CheckpointManager
    private lateinit var gitManager: com.velocity.ide.data.vcs.GitManager
    private lateinit var settingsManager: com.velocity.ide.ai.settings.AiSettingsManager
    private lateinit var deployClient: com.velocity.ide.data.deploy.DeployClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Services & Database
        database = AppDatabase.getDatabase(this)
        fileSystem = ProjectFileSystem(this)
        processManager = ProcessManager()
        gitManager = com.velocity.ide.data.vcs.GitManager()
        settingsManager = com.velocity.ide.ai.settings.AiSettingsManager(this)
        deployClient = com.velocity.ide.data.deploy.DeployClient()

        // Setup AI Tools and Agent Loop
        toolRegistry = ToolRegistry().apply {
            register(ReadFileTool(fileSystem))
            register(WriteFileTool(fileSystem))
            register(ListFilesTool(fileSystem))
            register(RunCommandTool(fileSystem, processManager))
            register(CreateDirectoryTool(fileSystem))
            register(DeleteFileTool(fileSystem))
            register(SearchFilesTool(fileSystem))
            register(UpdateProjectMemoryTool(database.projectMemoryDao()))
            register(GetProjectMemoryTool(database.projectMemoryDao()))
        }

        contextEngine = ProjectContextEngine(fileSystem, processManager, database.projectMemoryDao())
        providerRegistry = com.velocity.ide.ai.provider.ProviderRegistry(settingsManager)
        checkpointManager = com.velocity.ide.ai.agent.CheckpointManager(this, fileSystem, database.checkpointDao())

setContent {
            var accentName by remember { mutableStateOf(settingsManager.accentColorName) }
            VelocityTheme(accentName = accentName) {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val projectsState = database.projectDao().getAllProjectsFlow()
                    .collectAsState(initial = emptyList())
                val allChats by database.chatDao().getAllChatsFlow().collectAsState(initial = emptyList())
                var currentProject by remember { mutableStateOf<ProjectEntity?>(null) }
                var activeChat by remember { mutableStateOf<ChatEntity?>(null) }
                var fileTree by remember { mutableStateOf<List<File>>(emptyList()) }
                val openTabs = remember { mutableStateListOf<File>() }
                var activeTab by remember { mutableStateOf<File?>(null) }
                var activeFileContent by remember { mutableStateOf("") }

                var chatsList by remember { mutableStateOf<List<ChatEntity>>(emptyList()) }
                var chatMessages by remember { mutableStateOf<List<MessageEntity>>(emptyList()) }
                var terminalOutput by remember { mutableStateOf("") }
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var showAgentSheet by remember { mutableStateOf(false) }
                var agentLiveState by remember { mutableStateOf(com.velocity.ide.ui.agent.AgentLiveState()) }
                var agentJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
                var activeAgentEngine by remember { mutableStateOf<com.velocity.ide.ai.agent.AgentEngine?>(null) }
                var lastAgentRequest by remember { mutableStateOf<String?>(null) }
                var latestCheckpointId by remember { mutableStateOf<String?>(null) }
                var isDeploying by remember { mutableStateOf(false) }
                var deployFeedback by remember { mutableStateOf<String?>(null) }
                var deployFailed by remember { mutableStateOf(false) }

                // Refresh file tree on file operations
                fun refreshFileTree(projectName: String) {
                    fileTree = fileSystem.listDirectory(projectName, "")
                }

                // ── Sessions ───────────────────────────────────────────────────
                fun createSession(project: ProjectEntity) {
                    val count = allChats.count { it.projectId == project.id } + 1
                    val chat = ChatEntity(
                        id = "chat_${project.id}_$count",
                        projectId = project.id,
                        title = "Session $count",
                        createdAt = System.currentTimeMillis()
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.chatDao().insertChat(chat)
                        withContext(Dispatchers.Main) { activeChat = chat }
                    }
                }

                fun selectChat(chat: ChatEntity?) {
                    activeChat = chat
                    chatMessages = emptyList()
                }

                fun renameChat(chatId: String, title: String) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.chatDao().renameChat(chatId, title)
                    }
                }

                // Runs the agent loop against the provider configured in Settings. Engines are
                // created per run so configuration changes take effect immediately.
                fun runAgent(project: ProjectEntity, chatId: String, text: String, afterRun: () -> Unit = {}) {
                    val key = settingsManager.getApiKey(settingsManager.selectedProvider)
                    if (key.isNullOrBlank()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Configure your API key in Settings before using the agent",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    lastAgentRequest = text
                    agentLiveState = agentLiveState.copy(
                        error = null,
                        pendingApproval = null,
                        streamingText = "",
                        recentChanges = emptyList()
                    )
                    val chatId = activeChat?.id ?: "chat_${project.id}"
                    agentJob = lifecycleScope.launch(Dispatchers.IO) {
                        val engine = newAgentEngine()
                        activeAgentEngine = engine
                        engine.processRequest(
                            project.id,
                            chatId,
                            text,
                            fsProjectName = project.name
                        ).collect { event ->
                            when (event) {
                                is AgentEvent.TextDelta -> agentLiveState = agentLiveState.copy(
                                    streamingText = (agentLiveState.streamingText + event.text).take(12000)
                                )
                                is AgentEvent.StateChanged -> agentLiveState = agentLiveState.copy(state = event.newState)
                                is AgentEvent.ToolStarted -> agentLiveState = agentLiveState.copy(
                                    activeTools = agentLiveState.activeTools + event.toolName
                                )
                                is AgentEvent.ToolCompleted -> agentLiveState = agentLiveState.copy(
                                    activeTools = agentLiveState.activeTools - event.toolName
                                )
                                is AgentEvent.ApprovalPending -> agentLiveState = agentLiveState.copy(
                                    pendingApproval = com.velocity.ide.ui.agent.ApprovalRequest(event.toolName, event.arguments)
                                )
                                is AgentEvent.CheckpointCreated -> latestCheckpointId = event.checkpointId
                                is AgentEvent.DiffAvailable -> agentLiveState = agentLiveState.copy(
                                    recentChanges = event.changedFiles
                                )
                                is AgentEvent.RunRecorded -> {}
                                is AgentEvent.Error -> agentLiveState = agentLiveState.copy(error = event.message)
                                is AgentEvent.Completed -> agentLiveState = agentLiveState.copy(
                                    streamingText = "",
                                    pendingApproval = null
                                )
                            }
                        }
                        withContext(Dispatchers.Main) { afterRun() }
                    }
                }

                fun stopAgent() {
                    activeAgentEngine?.cancel()
                    agentJob?.cancel()
                    processManager.stopProcess("term")
                    agentLiveState = agentLiveState.copy(pendingApproval = null)
                }

                fun approveAgent(alwaysAllow: Boolean) {
                    val engine = activeAgentEngine
                    val pending = agentLiveState.pendingApproval
                    if (engine != null && pending != null) {
                        if (alwaysAllow) engine.allowAlways(pending.toolName)
                        lifecycleScope.launch { engine.submitApproval(true) }
                        agentLiveState = agentLiveState.copy(pendingApproval = null)
                    }
                }

                fun denyAgent() {
                    val engine = activeAgentEngine
                    if (engine != null) lifecycleScope.launch { engine.submitApproval(false) }
                    agentLiveState = agentLiveState.copy(pendingApproval = null)
                }

                fun rollbackToCheckpoint() {
                    val project = currentProject ?: return
                    val checkpointId = latestCheckpointId ?: return
                    lifecycleScope.launch {
                        val ok = checkpointManager.rollback(checkpointId, project.name)
                        withContext(Dispatchers.Main) {
                            if (ok) {
                                refreshFileTree(project.name)
                                Toast.makeText(this@MainActivity, "Rolled back to pre-run state", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Rollback failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                // Real deployment pipeline: build via ProcessManager, then publish through the
                // selected target. All results come from actual execution; failures are surfaced.
                fun runDeploy(project: ProjectEntity, target: String) {
                    if (isDeploying) return
                    isDeploying = true
                    deployFeedback = null
                    deployFailed = false
                    lifecycleScope.launch(Dispatchers.IO) {
                        val dir = File(project.path)
                        var message: String? = null
                        var failed = false

                        if (!gitManager.isGitRepo(dir)) {
                            message = "No Git repository. Initialize it in Workspace > Git before deploying."
                            failed = true
                        } else {
                            val build = processManager.executeSynchronous(listOf("npm", "run", "build"), dir, 180)
                            if (build.exitCode != 0) {
                                message = "Build failed:\n${build.stderr.trim().take(400)}"
                                failed = true
                            } else {
                                val result = when {
                                    target.contains("Vercel") -> {
                                        val token = settingsManager.vercelToken
                                        if (token.isBlank()) {
                                            com.velocity.ide.data.deploy.DeployResult(false, "Set a Vercel token in Settings first.")
                                        } else {
                                            deployClient.vercelDeploy(dir, token)
                                        }
                                    }
                                    target.contains("Netlify") -> {
                                        val token = settingsManager.netlifyToken
                                        if (token.isBlank()) {
                                            com.velocity.ide.data.deploy.DeployResult(false, "Set a Netlify token in Settings first.")
                                        } else {
                                            deployClient.netlifyDeploy(dir, token)
                                        }
                                    }
                                    else -> {
                                        val pat = settingsManager.githubPat
                                        if (pat.isBlank()) {
                                            com.velocity.ide.data.deploy.DeployResult(false, "Set a GitHub PAT in Settings first.")
                                        } else {
                                            val pushed = gitManager.push(dir, pat)
                                            if (pushed) {
                                                com.velocity.ide.data.deploy.DeployResult(true, "Pushed to Git remote successfully.")
                                            } else {
                                                com.velocity.ide.data.deploy.DeployResult(false, "Git push failed. Check the remote origin and PAT.")
                                            }
                                        }
                                    }
                                }
                                message = result.message + (result.url?.let { "\nURL: $it" } ?: "")
                                failed = !result.success
                            }
                        }

                        withContext(Dispatchers.Main) {
                            deployFeedback = message
                            deployFailed = failed
                            isDeploying = false
                        }
                    }
                }

                // Listen to terminal output
                LaunchedEffect(Unit) {
                    processManager.terminalOutputFlow.collectLatest { output ->
                        terminalOutput += output
                    }
                }

                // Derive a live dev-server URL from the real terminal output, if a server started.
                fun devServerUrl(): String? =
                    Regex("Local:\\s+http://localhost:(\\d+)")
                        .find(terminalOutput)
                        ?.groupValues
                        ?.getOrNull(1)
                        ?.let { "http://localhost:$it" }

                // Exit project mode: deselect the project and return to Home.
                fun exitToHome() {
                    currentProject = null
                    openTabs.clear()
                    activeTab = null
                    activeFileContent = ""
                    chatMessages = emptyList()
                    agentLiveState = com.velocity.ide.ui.agent.AgentLiveState()
                    showAgentSheet = false
                    currentScreen = Screen.HOME
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                MainLayout(
                    currentScreen = currentScreen,
                    hasProject = currentProject != null,
                    drawerState = drawerState,
                    onNavigate = { screen ->
                        currentScreen = screen
                        navController.navigate(screen.name.lowercase()) {
                            launchSingleTop = true
                        }
                    }
                ) { modifier ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = modifier
                    ) {
                        composable("home") {
                            HomeScreen(
                                projects = projectsState.value,
                                onCreateProject = { name ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val projectDir = fileSystem.createProject(name)
                                        val project = ProjectEntity(
                                            id = UUID.randomUUID().toString(),
                                            name = name,
                                            path = projectDir.absolutePath,
                                            createdAt = System.currentTimeMillis()
                                        )
                                        database.projectDao().insertProject(project)

                                        // Auto-create standard subfolders for template
                                        fileSystem.createDirectory(name, "src")
                                        fileSystem.createDirectory(name, "src/components")
                                        fileSystem.createFile(name, "package.json")
                                    }
                                    Toast.makeText(this@MainActivity, "Project created", Toast.LENGTH_SHORT).show()
                                },
                                onOpenProject = { project ->
                                    currentProject = project
                                    refreshFileTree(project.name)

                                    // Create/Get initial Chat session
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val existing = database.chatDao().getChatsForProject(project.id)
                                        val chat = existing.maxByOrNull { it.createdAt } ?: run {
                                            val c = ChatEntity(
                                                id = "chat_${project.id}",
                                                projectId = project.id,
                                                title = "Session 1",
                                                createdAt = System.currentTimeMillis()
                                            )
                                            database.chatDao().insertChat(c)
                                            c
                                        }
                                        withContext(Dispatchers.Main) {
                                            activeChat = chat
                                            currentScreen = Screen.WORKSPACE
                                            navController.navigate("workspace")
                                        }
                                    }
                                },
                                onBuildWithAI = { prompt ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val name = "AI_Project_${System.currentTimeMillis().toString().takeLast(4)}"
                                        val projectDir = fileSystem.createProject(name)
                                        val project = ProjectEntity(
                                            id = UUID.randomUUID().toString(),
                                            name = name,
                                            path = projectDir.absolutePath,
                                            createdAt = System.currentTimeMillis()
                                        )
                                        database.projectDao().insertProject(project)

                                        fileSystem.createDirectory(name, "src")
                                        fileSystem.createDirectory(name, "src/components")

                                        val initialChatId = "chat_${project.id}"
                                        database.chatDao().insertChat(
                                            ChatEntity(
                                                id = initialChatId,
                                                projectId = project.id,
                                                title = "Session 1",
                                                createdAt = System.currentTimeMillis()
                                            )
                                        )

                                        withContext(Dispatchers.Main) { activeChat = ChatEntity(initialChatId, project.id, "Session 1", System.currentTimeMillis()) }

                                        // Let Agent loop handle the project generation prompt
                                        runAgent(project, initialChatId, prompt) {
                                            currentProject = project
                                            refreshFileTree(project.name)
                                            currentScreen = Screen.WORKSPACE
                                            navController.navigate("workspace")
                                        }
                                    }
                                },
                                onNavigateToSettings = {
                                    navController.navigate("settings")
                                },
                                onOpenMenu = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        }

                        composable("workspace") {
                            val project = currentProject
                            val chat = activeChat
                            BackHandler(onBack = { exitToHome() })
                            if (project != null) {
                                // Query chat messages in background
                                LaunchedEffect(chat?.id) {
                                    database.messageDao().getMessagesForChatFlow(chat?.id ?: "chat_${project.id}").collectLatest { msgs ->
                                        chatMessages = msgs
                                    }
                                }

                                WorkspaceScreen(
                                    project = project,
                                    fileTree = fileTree,
                                    openTabs = openTabs,
                                    activeTab = activeTab,
                                    fileContent = activeFileContent,
                                    onFileContentChange = {
                                        activeFileContent = it
                                    },
                                    onTabSelected = { tab ->
                                        activeTab = tab
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val text = fileSystem.readFile(project.name, tab.name)
                                            withContext(Dispatchers.Main) {
                                                activeFileContent = text
                                            }
                                        }
                                    },
                                    onTabClosed = { tab ->
                                        openTabs.remove(tab)
                                        if (activeTab?.absolutePath == tab.absolutePath) {
                                            activeTab = openTabs.lastOrNull()
                                            activeFileContent = if (activeTab != null) {
                                                fileSystem.readFile(project.name, activeTab!!.name)
                                            } else ""
                                        }
                                    },
                                    onFileSelected = { file ->
                                        if (file.isFile) {
                                            if (!openTabs.contains(file)) {
                                                openTabs.add(file)
                                            }
                                            activeTab = file
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                val text = fileSystem.readFile(project.name, file.name)
                                                withContext(Dispatchers.Main) {
                                                    activeFileContent = text
                                                }
                                            }
                                        }
                                    },
                                    onSaveFile = {
                                        val file = activeTab
                                        if (file != null) {
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                fileSystem.writeFile(project.name, file.name, activeFileContent)
                                            }
                                            Toast.makeText(this@MainActivity, "File Saved", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onCreateFile = { name ->
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            fileSystem.createFile(project.name, name)
                                            refreshFileTree(project.name)
                                        }
                                    },
                                    onCreateFolder = { name ->
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            fileSystem.createDirectory(project.name, name)
                                            refreshFileTree(project.name)
                                        }
                                    },
                                    terminalOutput = terminalOutput,
                                    onExecuteCommand = { cmd ->
                                        val projectDir = File(project.path)
                                        processManager.startProcess("term", cmd, projectDir)
                                    },
                                    onBackToHome = {
                                        exitToHome()
                                    },
                                    onOpenAgent = {
                                        showAgentSheet = true
                                    },
                                    onAiRequest = { text ->
                                        runAgent(project, activeChat?.id ?: "chat_${project.id}", "Terminal request: $text")
                                    },
                                    onNavigateDeploy = {
                                        navController.navigate("deploy") { launchSingleTop = true }
                                    },
                                    onNavigateSettings = {
                                        navController.navigate("settings") { launchSingleTop = true }
                                    },
                                    memoriesFlow = database.projectMemoryDao().getMemoriesForProjectFlow(project.id),
                                    gitManager = gitManager,
                                    settingsManager = settingsManager,
                                    editorFontSize = settingsManager.editorFontSize,
                                    editorLineNumbers = settingsManager.editorLineNumbers
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(BgPrimary)
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    UiEmptyState(
                                        icon = Icons.Default.FolderOpen,
                                        title = "No project open",
                                        caption = "Create or open a project from Home to start building."
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Button(
                                        onClick = { exitToHome() },
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                                    ) {
                                        Text("Go to Home")
                                    }
                                }
                            }
                        }
                        
                        composable("preview") {
                            com.velocity.ide.ui.preview.PreviewScreen(
                                devServerUrl = devServerUrl(),
                                onBack = { navController.navigate("workspace") { launchSingleTop = true } }
                            )
                        }

                        composable("terminal") {
                            com.velocity.ide.ui.terminal.TerminalScreen(
                                terminalOutput = terminalOutput,
                                hasProject = currentProject != null,
                                fontSizeSp = settingsManager.terminalFontSize,
                                keepScreenOn = settingsManager.keepScreenOn,
                                onBack = { navController.navigate("workspace") { launchSingleTop = true } },
                                onAiRequest = { text ->
                                    val project = currentProject
                                    if (project != null) {
                                        runAgent(project, activeChat?.id ?: "chat_${project.id}", "Terminal request: $text")
                                    }
                                },
                                onExecuteCommand = { cmd ->
                                    if (currentProject != null) {
                                        val projectDir = File(currentProject!!.path)
                                        processManager.startProcess("term", cmd, projectDir)
                                        true
                                    } else {
                                        Toast.makeText(this@MainActivity, "Open project first", Toast.LENGTH_SHORT).show()
                                        false
                                    }
                                }
                            )
                        }
                        
                        composable("ai") {
                            val project = currentProject
                            val chat = activeChat
                            LaunchedEffect(chat?.id) {
                                database.messageDao().getMessagesForChatFlow(chat?.id ?: "chat_${project?.id}").collectLatest { msgs ->
                                    chatMessages = msgs
                                }
                            }
                            com.velocity.ide.ui.ai.AiScreen(
                                project = project,
                                projects = projectsState.value,
                                chats = allChats,
                                activeChatId = chat?.id,
                                activeChatMessages = chatMessages,
                                onBack = { navController.navigate("workspace") { launchSingleTop = true } },
                                onCreateSession = { p ->
                                    createSession(p)
                                },
                                onSelectSession = { c -> selectChat(c) },
                                onRenameSession = { c, name -> renameChat(c.id, name) },
                                onSendMessage = { messageText ->
                                    val p = project
                                    if (p != null) {
                                        runAgent(p, chat?.id ?: "chat_${p.id}", messageText)
                                    }
                                }
                            )
                        }
                        
                        composable("deploy") {
                            com.velocity.ide.ui.deploy.DeployScreen(
                                project = currentProject,
                                isDeploying = isDeploying,
                                deployFeedback = deployFeedback,
                                deployFailed = deployFailed,
                                onBack = { navController.navigate("workspace") { launchSingleTop = true } },
                                onDeploy = { target ->
                                    val project = currentProject
                                    if (project != null) runDeploy(project, target)
                                }
                            )
                        }
                        
                        composable("settings") {
                            com.velocity.ide.ui.settings.SettingsScreen(
                                settingsManager = settingsManager,
                                onBack = { navController.navigate("home") { launchSingleTop = true } },
                                onAccentChange = { name ->
                                    accentName = name
                                    settingsManager.accentColorName = name
                                },
                                onResetData = {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        database.projectDao().getAllProjectsFlow().let { }
                                        // Delete sandbox folders + DB rows
                                        fileSystem.getProjectsList().forEach { fileSystem.deleteProject(it) }
                                        val all = database.chatDao().getAllChatsFlow()
                                        // Room flows can't be iterated synchronously; clear via DAO helpers:
                                        database.clearAllData()
                                        withContext(Dispatchers.Main) {
                                            exitToHome()
                                            Toast.makeText(this@MainActivity, "All data cleared", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                
                if (showAgentSheet) {
                    val project = currentProject
                    val runsFlow = remember(project?.id) {
                        database.agentRunDao().getRunsForProjectFlow(project?.id ?: "")
                    }
                    val agentRuns by runsFlow.collectAsState(initial = emptyList())
                    AgentScreen(
                        messages = chatMessages,
                        liveState = agentLiveState,
                        lastRequest = lastAgentRequest,
                        canRollback = latestCheckpointId != null && agentLiveState.recentChanges.isNotEmpty(),
                        previousRuns = agentRuns.take(5),
                        mode = settingsManager.agentMode,
                        onModeChange = { m ->
                            settingsManager.agentMode = m
                        },
                        onDismiss = { showAgentSheet = false },
                        onApprove = { always -> approveAgent(always) },
                        onDeny = { denyAgent() },
                        onStop = { stopAgent() },
                        onRollback = { rollbackToCheckpoint() },
                        onRetry = { text ->
                            val p = project
                            if (p != null) runAgent(p, activeChat?.id ?: "chat_${p.id}", text)
                        },
                        onSendMessage = { text ->
                            if (project != null) {
                                runAgent(project, activeChat?.id ?: "chat_${project.id}", text) {
                                    refreshFileTree(project.name)
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    /**
     * Builds a fresh agent engine using the provider selected in Settings.
     * Called per agent run so Settings changes apply immediately.
     */
    private fun newAgentEngine(): AgentEngine {
        val registry = ToolRegistry().apply {
            register(ReadFileTool(fileSystem))
            register(WriteFileTool(fileSystem))
            register(ListFilesTool(fileSystem))
            register(RunCommandTool(fileSystem, processManager))
            register(CreateDirectoryTool(fileSystem))
            register(DeleteFileTool(fileSystem))
            register(SearchFilesTool(fileSystem))
            register(UpdateProjectMemoryTool(database.projectMemoryDao()))
            register(GetProjectMemoryTool(database.projectMemoryDao()))
        }
        return AgentEngine(
            llmProvider = providerRegistry.getActiveProvider(),
            messageDao = database.messageDao(),
            toolRegistry = registry,
            contextEngine = contextEngine,
            approvalPolicy = com.velocity.ide.ai.agent.ApprovalPolicy(settingsManager),
            checkpointManager = checkpointManager,
            runDao = database.agentRunDao(),
            mode = com.velocity.ide.ai.agent.AgentMode.valueOf(settingsManager.agentMode),
            temperature = settingsManager.temperature,
            maxSteps = settingsManager.maxSteps
        )
    }
}
