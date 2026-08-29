package com.velocity.ide.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.data.database.ProjectEntity
import com.velocity.ide.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    projects: List<ProjectEntity>,
    onCreateProject: (String) -> Unit,
    onOpenProject: (ProjectEntity) -> Unit,
    onBuildWithAI: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenMenu: () -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newProjectName by remember { mutableStateOf("") }
    var aiPromptText by remember { mutableStateOf("") }

    val activeProject = projects.maxByOrNull { it.createdAt } // For the "Hero" card

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgPrimary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onOpenMenu, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Velocity",
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "VELOCITY-IDE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 0.4.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextSecondary
                        )
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.clickable { onNavigateToSettings() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Good morning",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Ready to build?",
                    fontSize = 12.sp,
                    color = AccentOrange
                )
            }
        },
        containerColor = BgPrimary
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Home Hero Card
            if (activeProject != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenProject(activeProject) },
                        colors = CardDefaults.cardColors(containerColor = BgSurface),
                        shape = RoundedCornerShape(RadiusCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "CURRENT PROJECT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                activeProject.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "React · Vite",
                                fontSize = 13.sp,
                                color = TextSecondary
                            ) // Mock tech stack
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(StatusSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Build passed", fontSize = 13.sp, color = TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onOpenProject(activeProject) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(RadiusControl),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                            ) {
                                Text("Open Workspace")
                            }
                        }
                    }
                }
            }

            // Build With AI Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BgSurface),
                    shape = RoundedCornerShape(RadiusCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = AccentOrange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "BUILD WITH AI",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 0.4.sp
                            )
                        }

                        Text(
                            "Describe what you want. Velocity builds it.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )

                        OutlinedTextField(
                            value = aiPromptText,
                            onValueChange = { aiPromptText = it },
                            placeholder = {
                                Text("e.g. Build an Expense Tracker with dashboard...", color = TextMuted)
                            },
                            modifier = Modifier.fillMaxWidth().height(90.dp),
                            shape = RoundedCornerShape(RadiusControl),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentOrange,
                                unfocusedBorderColor = BorderDefault,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = AccentOrange
                            )
                        )

                        Button(
                            onClick = {
                                if (aiPromptText.isNotBlank()) {
                                    onBuildWithAI(aiPromptText)
                                    aiPromptText = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End),
                            shape = RoundedCornerShape(RadiusControl),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                        ) {
                            Text("Start building")
                        }
                    }
                }
            }

            // AI Shortcut Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShortcutCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Add,
                        title = "Build App",
                        onClick = { showCreateDialog = true }
                    )
                    ShortcutCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.BugReport,
                        title = "Fix bug",
                        onClick = { /* TODO */ },
                        soon = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShortcutCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Lightbulb,
                        title = "Explain Project",
                        onClick = { /* TODO */ },
                        soon = true
                    )
                    ShortcutCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Publish,
                        title = "Deploy",
                        onClick = { /* TODO */ },
                        soon = true
                    )
                }
            }

            // Recent Projects
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent Projects",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = { showCreateDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = AccentOrange
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New", color = AccentOrange)
                    }
                }
            }

            items(projects.sortedByDescending { it.createdAt }) { project ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusControl))
                        .clickable { onOpenProject(project) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                            .background(BgSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            project.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            "React · Vite • Edited ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.createdAt))}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // Spacer for BottomNav
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = BgSurface,
            shape = RoundedCornerShape(RadiusCard),
            title = { Text("Create Project", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newProjectName,
                    onValueChange = { newProjectName = it },
                    label = { Text("Project Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(RadiusControl),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(RadiusControl),
                    onClick = {
                        if (newProjectName.isNotBlank()) {
                            onCreateProject(newProjectName)
                            newProjectName = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ShortcutCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    soon: Boolean = false
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = BgElevated),
        shape = RoundedCornerShape(RadiusCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            if (soon) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "SOON",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}