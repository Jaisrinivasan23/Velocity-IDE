package com.velocity.ide.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.data.database.ChatEntity
import com.velocity.ide.data.database.MessageEntity
import com.velocity.ide.data.database.ProjectEntity
import com.velocity.ide.ui.theme.*
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AiScreen(
    project: ProjectEntity?,
    projects: List<ProjectEntity> = emptyList(),
    chats: List<ChatEntity> = emptyList(),
    activeChatId: String? = null,
    activeChatMessages: List<MessageEntity>,
    onBack: () -> Unit = {},
    onCreateSession: (ProjectEntity) -> Unit = {},
    onSelectSession: (ChatEntity) -> Unit = {},
    onRenameSession: (ChatEntity, String) -> Unit = { _, _ -> },
    onSendMessage: (String) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var renameTarget by remember { mutableStateOf<ChatEntity?>(null) }
    var renameText by remember { mutableStateOf("") }
    var localInput by remember { mutableStateOf("") }

    if (project == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.End).padding(end = 8.dp, top = 8.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(AccentSoft, RoundedCornerShape(RadiusCard)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = AccentOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No project open",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Please open a project to start chatting with AI",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BgElevated,
                drawerContentColor = TextSecondary,
                modifier = Modifier.width(300.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("SESSIONS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 0.4.sp)
                        Text(project.name, fontSize = 11.sp, color = TextMuted)
                    }
                }
                HorizontalDivider(color = BorderDefault)

                // New session
                TextButton(
                    onClick = { onCreateSession(project) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New session", color = AccentOrange)
                }

                // Current project sessions
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    items(chats.filter { it.projectId == project.id }) { chat ->
                        val isActive = chat.id == activeChatId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(RadiusControl))
                                .background(if (isActive) AccentSoft else Color.Transparent)
                                .combinedClickable(
                                    onClick = {
                                        onSelectSession(chat)
                                        scope.launch { drawerState.close() }
                                    },
                                    onLongClick = {
                                        renameTarget = chat
                                        renameText = chat.title
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isActive) AccentOrange else TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                chat.title,
                                fontSize = 13.sp,
                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isActive) TextPrimary else TextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Other projects
                val otherProjects = projects.filter { it.id != project.id }
                if (otherProjects.isNotEmpty()) {
                    HorizontalDivider(color = BorderDefault)
                    Text(
                        "OTHER PROJECTS",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                    otherProjects.take(4).forEach { p ->
                        val sessions = chats.filter { it.projectId == p.id }
                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                p.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            sessions.take(3).forEach { chat ->
                                Text(
                                    chat.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(RadiusControl))
                                        .clickable {
                                            onSelectSession(chat)
                                            scope.launch { drawerState.close() }
                                        }
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgElevated)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(Icons.Default.Menu, contentDescription = "Sessions", tint = TextSecondary)
                }
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = AccentOrange,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "Velocity AI",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        "Session: ${chats.find { it.id == activeChatId }?.title ?: "Default"} · ${project.name}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
            Divider(color = BorderDefault)

            // Chat History
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(activeChatMessages) { msg ->
                    val isUser = msg.role == "USER"
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        if (!isUser) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(RadiusControl))
                                    .background(AccentSoft)
                                    .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .background(
                                    if (isUser) AccentSoft.copy(alpha = 0.6f) else BgElevated,
                                    RoundedCornerShape(
                                        topStart = RadiusControl,
                                        topEnd = RadiusControl,
                                        bottomStart = if (isUser) RadiusControl else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else RadiusControl
                                    )
                                )
                                .border(
                                    1.dp,
                                    if (isUser) AccentOrange.copy(alpha = 0.35f) else BorderDefault,
                                    RoundedCornerShape(
                                        topStart = RadiusControl,
                                        topEnd = RadiusControl,
                                        bottomStart = if (isUser) RadiusControl else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else RadiusControl
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                if (isUser) "You" else "Velocity AI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) AccentOrange else TextMuted,
                                letterSpacing = 0.4.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.content,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }

                        if (isUser) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(RadiusControl))
                                    .background(BgSurface)
                                    .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Text(
                "SUGGESTIONS:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val suggestions = listOf("Explain this project", "Fix errors", "Suggest improvements")
                suggestions.forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .border(1.dp, BorderDefault, RoundedCornerShape(RadiusPill))
                            .background(BgElevated, RoundedCornerShape(RadiusPill))
                            .clickable { onSendMessage(suggestion) }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(suggestion, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .background(BgSurface, RoundedCornerShape(RadiusControl))
                    .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO Add */ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = TextSecondary)
                }
                IconButton(onClick = { /* TODO Mic */ }) {
                    Icon(Icons.Default.Mic, contentDescription = "Mic", tint = TextSecondary)
                }
                BasicTextField(
                    value = localInput,
                    onValueChange = { localInput = it },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    textStyle = LocalTextStyle.current.copy(color = TextPrimary, fontSize = 14.sp),
                    decorationBox = { innerTextField ->
                        if (localInput.isEmpty()) {
                            Text("Ask Velocity anything...", color = TextMuted, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                )
                IconButton(
                    onClick = {
                        if (localInput.isNotBlank()) {
                            onSendMessage(localInput)
                            localInput = ""
                        }
                    },
                    modifier = Modifier
                        .background(AccentOrange, RoundedCornerShape(RadiusControl))
                        .size(38.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    renameTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { renameTarget = null },
            containerColor = BgSurface,
            shape = RoundedCornerShape(RadiusCard),
            title = { Text("Rename session", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Session name") },
                    singleLine = true,
                    shape = RoundedCornerShape(RadiusControl),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            onRenameSession(target, renameText)
                            renameTarget = null
                        }
                    },
                    shape = RoundedCornerShape(RadiusControl)
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = { renameTarget = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}