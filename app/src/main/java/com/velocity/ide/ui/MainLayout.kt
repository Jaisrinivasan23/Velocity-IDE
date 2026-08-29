package com.velocity.ide.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.velocity.ide.ui.common.AppDrawer
import com.velocity.ide.ui.theme.AccentSoft
import com.velocity.ide.ui.theme.AccentOrange
import com.velocity.ide.ui.theme.BgElevated
import com.velocity.ide.ui.theme.BorderDefault
import com.velocity.ide.ui.theme.TextMuted
import com.velocity.ide.ui.theme.TextSecondary

enum class Screen {
    HOME, WORKSPACE, PREVIEW, TERMINAL, AI, DEPLOY, SETTINGS
}

/**
 * App shell: navigation drawer (Deploy + Settings) plus a project-mode bottom bar.
 * The bottom bar is only shown while a project is open and on primary project
 * screens (Workspace, Terminal, Preview, AI). HOME (no project yet) and the
 * contextual DEPLOY/SETTINGS screens render without it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    currentScreen: Screen,
    hasProject: Boolean,
    drawerState: DrawerState,
    onNavigate: (Screen) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val showBottomBar = hasProject &&
        currentScreen != Screen.HOME &&
        currentScreen != Screen.DEPLOY &&
        currentScreen != Screen.SETTINGS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BorderDefault)
                    )
                    NavigationBar(
                        containerColor = BgElevated,
                        contentColor = TextSecondary,
                        tonalElevation = 0.dp
                    ) {
                        NavBarItem(
                            selected = currentScreen == Screen.WORKSPACE,
                            onClick = { onNavigate(Screen.WORKSPACE) },
                            icon = Icons.Default.Folder,
                            label = "Workspace"
                        )
                        NavBarItem(
                            selected = currentScreen == Screen.TERMINAL,
                            onClick = { onNavigate(Screen.TERMINAL) },
                            icon = Icons.Default.Terminal,
                            label = "Terminal"
                        )
                        NavBarItem(
                            selected = currentScreen == Screen.PREVIEW,
                            onClick = { onNavigate(Screen.PREVIEW) },
                            icon = Icons.Default.Code,
                            label = "Preview"
                        )
                        NavBarItem(
                            selected = currentScreen == Screen.AI,
                            onClick = { onNavigate(Screen.AI) },
                            icon = Icons.Default.AutoAwesome,
                            label = "AI"
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        AppDrawer(
            drawerState = drawerState,
            currentScreen = currentScreen,
            onNavigate = onNavigate
        ) {
            Box(modifier = Modifier.padding(paddingValues)) {
                content(Modifier)
            }
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(imageVector = icon, contentDescription = label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AccentOrange,
            selectedTextColor = TextSecondary,
            indicatorColor = AccentSoft,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
        )
    )
}