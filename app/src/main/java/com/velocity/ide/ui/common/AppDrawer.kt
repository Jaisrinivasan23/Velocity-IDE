package com.velocity.ide.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ui.Screen
import com.velocity.ide.ui.theme.*

/**
 * App navigation drawer (hamburger) exposing non-primary destinations:
 * Deploy and Settings. Presentational only; navigation flows through onNavigate.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BgElevated,
                drawerContentColor = TextSecondary,
                modifier = Modifier.width(300.dp)
            ) {
                // Brand header
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("VELOCITY-IDE", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Project hub", fontSize = 11.sp, color = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderDefault)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "MORE",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                DrawerItem(
                    label = "Deploy",
                    icon = Icons.Default.RocketLaunch,
                    selected = currentScreen == Screen.DEPLOY,
                    onClick = { onNavigate(Screen.DEPLOY) }
                )
                DrawerItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { onNavigate(Screen.SETTINGS) }
                )
            }
        }
    ) {
        content()
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) AccentOrange else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) TextPrimary else TextSecondary
        )
    }
}