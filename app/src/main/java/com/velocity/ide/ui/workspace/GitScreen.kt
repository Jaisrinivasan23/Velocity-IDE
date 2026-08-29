package com.velocity.ide.ui.workspace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ai.settings.AiSettingsManager
import com.velocity.ide.data.vcs.GitManager
import com.velocity.ide.data.vcs.GitStatusResult
import com.velocity.ide.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun GitScreen(
    projectDir: File,
    gitManager: GitManager,
    settingsManager: AiSettingsManager
) {
    val coroutineScope = rememberCoroutineScope()
    var isRepo by remember { mutableStateOf(gitManager.isGitRepo(projectDir)) }
    var status by remember { mutableStateOf<GitStatusResult?>(null) }
    var commitMessage by remember { mutableStateOf("") }
    var actionFeedback by remember { mutableStateOf("") }

    fun refreshStatus() {
        coroutineScope.launch(Dispatchers.IO) {
            val newStatus = gitManager.getStatus(projectDir)
            withContext(Dispatchers.Main) {
                status = newStatus
                actionFeedback = ""
            }
        }
    }

    LaunchedEffect(isRepo) {
        if (isRepo) refreshStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Source,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "SOURCE CONTROL",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.4.sp
            )
        }
        Text(
            text = "Powered by JGit (Real-Time Production Ready)",
            fontSize = 12.sp,
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 44.dp, top = 4.dp, bottom = 24.dp)
        )

        if (!isRepo) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("This project is not a Git repository.", color = TextPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                val success = gitManager.initRepo(projectDir)
                                withContext(Dispatchers.Main) {
                                    if (success) isRepo = true
                                    else actionFeedback = "Failed to initialize repository."
                                }
                            }
                        },
                        shape = RoundedCornerShape(RadiusControl),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                    ) {
                        Text("Initialize Git Repository", color = Color.White)
                    }
                }
            }
        } else {
            val currentStatus = status
            if (currentStatus != null) {
                val allChanges = currentStatus.changedFiles + currentStatus.untrackedFiles + currentStatus.missingFiles

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "CHANGES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AccentOrange,
                        letterSpacing = 0.6.sp
                    )
                    IconButton(onClick = { refreshStatus() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (allChanges.isEmpty()) {
                    Text(
                        "Working tree is clean.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Card(
                        shape = RoundedCornerShape(RadiusCard),
                        colors = CardDefaults.cardColors(containerColor = BgSurface),
                        border = BorderStroke(1.dp, BorderDefault),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(allChanges) { file ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.InsertDriveFile,
                                        contentDescription = null,
                                        tint = AccentOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = file,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = commitMessage,
                    onValueChange = { commitMessage = it },
                    label = { Text("Commit Message") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(RadiusControl),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentOrange,
                        unfocusedBorderColor = BorderDefault,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentOrange
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (commitMessage.isBlank()) {
                                actionFeedback = "Please enter a commit message."
                                return@Button
                            }
                            if (settingsManager.gitAuthorName.isBlank() || settingsManager.gitAuthorEmail.isBlank()) {
                                actionFeedback = "Please configure Git Author Name and Email in Settings."
                                return@Button
                            }
                            coroutineScope.launch(Dispatchers.IO) {
                                val success = gitManager.commitAll(
                                    directory = projectDir,
                                    message = commitMessage,
                                    authorName = settingsManager.gitAuthorName,
                                    authorEmail = settingsManager.gitAuthorEmail
                                )
                                withContext(Dispatchers.Main) {
                                    if (success) {
                                        commitMessage = ""
                                        actionFeedback = "Committed successfully."
                                        refreshStatus()
                                    } else {
                                        actionFeedback = "Commit failed."
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(RadiusControl),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Commit All", color = Color.White)
                    }

                    Button(
                        onClick = {
                            if (settingsManager.githubPat.isBlank()) {
                                actionFeedback = "Please configure GitHub PAT in Settings."
                                return@Button
                            }
                            actionFeedback = "Pushing..."
                            coroutineScope.launch(Dispatchers.IO) {
                                val success = gitManager.push(projectDir, settingsManager.githubPat)
                                withContext(Dispatchers.Main) {
                                    actionFeedback = if (success) "Pushed successfully." else "Push failed."
                                }
                            }
                        },
                        shape = RoundedCornerShape(RadiusControl),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, BorderDefault),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Push")
                    }

                    Button(
                        onClick = {
                            if (settingsManager.githubPat.isBlank()) {
                                actionFeedback = "Please configure GitHub PAT in Settings."
                                return@Button
                            }
                            actionFeedback = "Pulling..."
                            coroutineScope.launch(Dispatchers.IO) {
                                val success = gitManager.pull(projectDir, settingsManager.githubPat)
                                withContext(Dispatchers.Main) {
                                    actionFeedback = if (success) {
                                        refreshStatus()
                                        "Pulled successfully."
                                    } else "Pull failed."
                                }
                            }
                        },
                        shape = RoundedCornerShape(RadiusControl),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, BorderDefault),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pull")
                    }
                }

                if (actionFeedback.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(actionFeedback, color = AccentOrange, fontSize = 14.sp)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentOrange)
                }
            }
        }
    }
}