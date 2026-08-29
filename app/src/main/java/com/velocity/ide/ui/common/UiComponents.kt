package com.velocity.ide.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ui.theme.*

/**
 * Presentational-only building blocks. No business logic or state writes.
 */

@Composable
fun UiCard(
    modifier: Modifier = Modifier,
    contentColor: androidx.compose.ui.graphics.Color = TextPrimary,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(RadiusCard),
    containerColor: androidx.compose.ui.graphics.Color = BgSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        shape = shape,
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun UiSectionHeader(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    title: String,
    subtitle: String? = null
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(AccentSoft, RoundedCornerShape(RadiusControl)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 0.6.sp
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun UiEmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    title: String,
    caption: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(BgElevated, RoundedCornerShape(RadiusControl)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        if (caption != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                caption,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}