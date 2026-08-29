package com.velocity.ide.ui.agent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
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
import com.velocity.ide.ai.agent.AgentState
import com.velocity.ide.data.database.MessageEntity
import com.velocity.ide.ui.theme.*

data class ApprovalRequest(
    val toolName: String,
    val arguments: String
)

data class AgentLiveState(
    val state: AgentState = AgentState.IDLE,
    val streamingText: String = "",
    val activeTools: List<String> = emptyList(),
    val error: String? = null,
    val pendingApproval: ApprovalRequest? = null,
    val recentChanges: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentScreen(
    messages: List<MessageEntity>,
    liveState: AgentLiveState,
    lastRequest: String? = null,
    canRollback: Boolean = false,
    previousRuns: List<com.velocity.ide.data.database.AgentRunEntity> = emptyList(),
    mode: String = "AGENT",
    onModeChange: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit,
    onApprove: (alwaysAllow: Boolean) -> Unit = {},
    onDeny: () -> Unit = {},
    onStop: () -> Unit = {},
    onRollback: () -> Unit = {},
    onRetry: (text: String) -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = BgElevated,
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Velocity AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = liveState.state.name,
                            fontSize = 12.sp,
                            color = if (liveState.state == AgentState.IDLE) TextSecondary else StatusSuccess
                        )
                        if (liveState.activeTools.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${liveState.activeTools.size} tool(s) active",
                                fontSize = 11.sp,
                                color = AccentOrange,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Row {
                    if (liveState.state != AgentState.IDLE && liveState.state != AgentState.COMPLETED &&
                        liveState.state != AgentState.FAILED && liveState.state != AgentState.CANCELLED
                    ) {
                        IconButton(onClick = onStop, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = StatusError)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Historical messages
                items(messages) { msg ->
                    val isUser = msg.role == "USER"
                    val isTool = msg.role == "TOOL"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) AccentSoft.copy(alpha = 0.6f) else BgSurface
                            ),
                            modifier = Modifier.widthIn(max = 310.dp),
                            shape = RoundedCornerShape(
                                topStart = RadiusControl,
                                topEnd = RadiusControl,
                                bottomStart = if (isUser) RadiusControl else 2.dp,
                                bottomEnd = if (isUser) 2.dp else RadiusControl
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isUser) AccentOrange.copy(alpha = 0.35f)
                                else if (isTool) StatusInfo.copy(alpha = 0.35f)
                                else BorderDefault
                            )
                        ) {
                            Text(
                                text = msg.content,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                fontFamily = if (isTool) FontFamily.Monospace else FontFamily.Default,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Live streaming + active tools card
                if (liveState.state != AgentState.IDLE && liveState.state != AgentState.COMPLETED) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgSurface),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusCard),
                            border = BorderStroke(1.dp, AccentOrange.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (liveState.streamingText.isNotBlank()) {
                                    Text(
                                        text = liveState.streamingText,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        lineHeight = 18.sp
                                    )
                                } else {
                                    Text(
                                        text = "Agent is thinking...",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                }

                                if (liveState.activeTools.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = BorderDefault)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "EXECUTING TOOLS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        letterSpacing = 0.5.sp
                                    )
                                    liveState.activeTools.forEach { tool ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 6.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(12.dp),
                                                strokeWidth = 2.dp,
                                                color = AccentOrange
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                tool,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = AccentOrange
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Approval request card
                val pending = liveState.pendingApproval
                if (pending != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusWarningSoft),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusCard),
                            border = BorderStroke(1.dp, StatusWarning)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "APPROVAL REQUIRED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusWarning,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    pending.toolName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    pending.arguments,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onApprove(false) },
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Allow", color = Color.White)
                                    }
                                    Button(
                                        onClick = { onApprove(true) },
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Always allow", color = Color.White)
                                    }
                                    Button(
                                        onClick = onDeny,
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
                                        border = BorderStroke(1.dp, StatusError),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Deny")
                                    }
                                }
                            }
                        }
                    }
                }

                // Changes + rollback after a completed run
                if (liveState.state == AgentState.COMPLETED && liveState.recentChanges.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgSurface),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusCard),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "CHANGED FILES (${liveState.recentChanges.size})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusSuccess,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                liveState.recentChanges.take(20).forEach { file ->
                                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = StatusSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            file,
                                            fontSize = 12.sp,
                                            color = TextPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                if (canRollback) {
Spacer(modifier = Modifier.height(12.dp))

                // Mode selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSurface, RoundedCornerShape(RadiusControl))
                        .border(1.dp, BorderDefault, RoundedCornerShape(RadiusControl))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modes = listOf("ASK", "EDIT", "AGENT")
                    modes.forEach { m ->
                        val selected = mode == m
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(RadiusControl))
                                .background(if (selected) AccentOrange else Color.Transparent)
                                .clickable { onModeChange(m) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                m,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = onRollback,
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusWarning),
                                        border = BorderStroke(1.dp, StatusWarning)
                                    ) {
                                        Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Rollback to pre-run state")
                                    }
                                }
                            }
                        }
                    }
                }

                if (liveState.error != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = StatusErrorSoft),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Error: ${liveState.error}",
                                    color = StatusError,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                if (lastRequest != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onRetry(lastRequest) },
                                        shape = RoundedCornerShape(RadiusControl),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Retry request")
                                    }
                                }
                            }
                        }
                    }
                }

                // Previous runs (collapsible)
                if (previousRuns.isNotEmpty()) {
                    item {
                        var expanded by remember { mutableStateOf(false) }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = BgSurface),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusCard),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded = !expanded },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "PREVIOUS RUNS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted,
                                        letterSpacing = 0.6.sp
                                    )
                                    Text(
                                        if (expanded) "Hide" else "Show",
                                        fontSize = 12.sp,
                                        color = AccentOrange
                                    )
                                }
                                if (expanded) {
                                    previousRuns.forEach { run ->
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            run.request,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        Text(
                                            "${run.result} · ${run.durationMs / 1000}s · ${run.filesChanged} file(s) changed",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            var input by remember { mutableStateOf("") }
            val isBusy = liveState.state != AgentState.IDLE && liveState.state != AgentState.COMPLETED && liveState.state != AgentState.FAILED && liveState.state != AgentState.CANCELLED

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text(if (isBusy) "Agent is working..." else "Ask AI to build or edit...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(RadiusControl),
                    enabled = !isBusy,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentOrange,
                        unfocusedBorderColor = BorderDefault,
                        disabledBorderColor = BorderDefault.copy(alpha = 0.3f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = AccentOrange
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    enabled = !isBusy && input.isNotBlank(),
                    onClick = {
                        onSendMessage(input)
                        input = ""
                    },
                    modifier = Modifier
                        .background(
                            if (isBusy || input.isBlank()) BgSurface else AccentOrange,
                            RoundedCornerShape(RadiusControl)
                        )
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (isBusy || input.isBlank()) TextMuted else Color.White
                    )
                }
            }
        }
    }
}