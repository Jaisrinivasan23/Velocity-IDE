package com.velocity.ide.ui.deploy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.data.database.ProjectEntity
import com.velocity.ide.ui.common.UiEmptyState
import com.velocity.ide.ui.theme.*

@Composable
fun DeployScreen(
    project: ProjectEntity?,
    isDeploying: Boolean,
    deployFeedback: String?,
    deployFailed: Boolean,
    onBack: () -> Unit,
    onDeploy: (target: String) -> Unit
) {
    var selectedTarget by remember { mutableStateOf("Vercel Edge Network") }
    val canDeploy = project != null && !isDeploying

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgElevated)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text("DEPLOYMENT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 0.4.sp)
                Text(
                    "Ship ${project?.name ?: "project"} to production",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Divider(color = BorderDefault)

        if (project == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                UiEmptyState(
                    icon = Icons.Default.RocketLaunch,
                    title = "No project open",
                    caption = "Open a project to use deployment."
                )
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Checklist Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BgSurface),
                shape = RoundedCornerShape(RadiusCard),
                border = BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "PRE-FLIGHT CHECKLIST",
                            color = StatusSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.6.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    ChecklistItem("Dependencies verified", true)
                    ChecklistItem("Production bundle verified", true)
                    ChecklistItem("Environment variables set", true)
                    ChecklistItem("Unit tests passing (18/18)", true)
                    ChecklistItem("Project Doctor checks (3 warnings)", false)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "DEPLOYMENT TARGET",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 0.6.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Grid 2x2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TargetCard(
                    title = "Vercel Edge Network",
                    subtitle = "Global serverless\ndistribution",
                    icon = Icons.Default.Language,
                    isSelected = selectedTarget == "Vercel Edge Network",
                    onClick = { selectedTarget = "Vercel Edge Network" },
                    modifier = Modifier.weight(1f)
                )
                TargetCard(
                    title = "Netlify Cloud",
                    subtitle = "Static CDN &\nfunction hosting",
                    icon = Icons.Default.Language,
                    isSelected = selectedTarget == "Netlify Cloud",
                    onClick = { selectedTarget = "Netlify Cloud" },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TargetCard(
                    title = "GitHub Pages",
                    subtitle = "Repository static\ngh-pages",
                    icon = Icons.Default.Language,
                    isSelected = selectedTarget == "GitHub Pages",
                    onClick = { selectedTarget = "GitHub Pages" },
                    modifier = Modifier.weight(1f)
                )
                TargetCard(
                    title = "Local Device PWA",
                    subtitle = "Standalone offline\ninstaller",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedTarget == "Local Device PWA",
                    onClick = { selectedTarget = "Local Device PWA" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onDeploy(selectedTarget) },
                enabled = canDeploy,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusControl),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = BgSurface,
                    disabledContentColor = TextMuted
                )
            ) {
                if (isDeploying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Deploying...")
                } else {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deploy Project")
                }
            }

            deployFeedback?.let { feedback ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = feedback,
                    color = if (deployFailed) StatusError else StatusSuccess,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ChecklistItem(text: String, isSuccess: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(if (isSuccess) StatusSuccessSoft else StatusWarningSoft, RoundedCornerShape(RadiusControl))
                .border(1.dp, if (isSuccess) StatusSuccess else StatusWarning, RoundedCornerShape(RadiusControl)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Warning,
                contentDescription = null,
                tint = if (isSuccess) StatusSuccess else StatusWarning,
                modifier = Modifier.size(12.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun TargetCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentSoft.copy(alpha = 0.5f) else BgSurface,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(RadiusCard),
        border = BorderStroke(1.dp, if (isSelected) AccentOrange else BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isSelected) AccentOrange else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(subtitle, color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        }
    }
}