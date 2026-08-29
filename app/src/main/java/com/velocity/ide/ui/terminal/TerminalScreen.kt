package com.velocity.ide.ui.terminal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ui.theme.*

/**
 * Standalone terminal route. Output is the real process output streamed from
 * [com.velocity.ide.runtime.ProcessManager] via MainActivity; command execution
 * also runs through ProcessManager. Rendering only.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(
    terminalOutput: String,
    onExecuteCommand: (String) -> Boolean,
    hasProject: Boolean = true,
    onAiRequest: (String) -> Unit = {},
    onBack: () -> Unit = {},
    fontSizeSp: Int = 12,
    keepScreenOn: Boolean = false
) {
    var cmdInput by remember { mutableStateOf("") }
    val isNl = cmdInput.isNotBlank() && !ShellDetect.isLikelyCommand(cmdInput)

    val view = androidx.compose.ui.platform.LocalView.current
    androidx.compose.runtime.DisposableEffect(fontSizeSp, keepScreenOn) {
        view.keepScreenOn = keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

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
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Terminal,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    "Terminal",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Text(
                    "System shell",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
        Divider(color = BorderDefault)

        if (!hasProject) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                com.velocity.ide.ui.common.UiEmptyState(
                    icon = Icons.Default.Terminal,
                    title = "No project open",
                    caption = "Open a project from Home to use the terminal."
                )
            }
            return
        }

        // Output
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(BgTerminal)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            Text(
                text = terminalOutput.ifEmpty { "Terminal session ready.\n$" },
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFB8F2C2),
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp + 6).sp
            )
        }

        // Natural-language chip
        if (isNl) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    onClick = { onAiRequest(cmdInput) },
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
                            "Run as AI request",
                            color = AccentOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Command Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurface)
                .border(1.dp, BorderDefault)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$ ", color = StatusSuccess, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
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
                            fontSize = fontSizeSp.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    innerTextField()
                }
            )
            IconButton(
                onClick = {
                    if (cmdInput.isNotBlank()) {
                        if (onExecuteCommand(cmdInput)) {
                            cmdInput = ""
                        }
                    }
                }
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = StatusSuccess)
            }
        }
    }
}