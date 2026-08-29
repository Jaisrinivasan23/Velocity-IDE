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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.data.database.ProjectMemoryEntity
import com.velocity.ide.ui.theme.*
import kotlinx.coroutines.flow.Flow

@Composable
fun MemoryScreen(
    memoriesFlow: Flow<List<ProjectMemoryEntity>>
) {
    val memories by memoriesFlow.collectAsState(initial = emptyList())

    // Known keys to ensure they always show up in the UI, even if empty
    val knownCategories = listOf(
        Pair("Architecture Overview", Icons.Default.Layers),
        Pair("Tech Stack & Dependencies", Icons.Default.Memory),
        Pair("Design System & Conventions", Icons.Default.Brush),
        Pair("AI Decision Memory", Icons.Default.History)
    )

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
                    imageVector = Icons.Default.AccountTree,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "PROJECT MEMORY",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.4.sp
            )
        }
        Text(
            text = "Architectural knowledge & Context Graph",
            fontSize = 13.sp,
            color = TextSecondary,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            modifier = Modifier.padding(start = 44.dp, top = 4.dp, bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(knownCategories) { category ->
                val (key, icon) = category
                val memory = memories.find { it.key == key }

                MemoryCard(
                    title = key,
                    icon = icon,
                    content = memory?.value ?: "No memory recorded yet. The AI will populate this as you build."
                )
            }

            // Render any unexpected dynamic memories the AI might have created
            val dynamicMemories = memories.filter { m -> knownCategories.none { it.first == m.key } }
            items(dynamicMemories) { memory ->
                MemoryCard(
                    title = memory.key,
                    icon = Icons.Default.Info,
                    content = memory.value
                )
            }
        }
    }
}

@Composable
fun MemoryCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        shape = RoundedCornerShape(RadiusCard),
        colors = CardDefaults.cardColors(containerColor = BgSurface, contentColor = TextPrimary),
        border = BorderStroke(1.dp, BorderDefault),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}