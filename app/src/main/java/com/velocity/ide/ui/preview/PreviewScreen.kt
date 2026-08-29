package com.velocity.ide.ui.preview

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.velocity.ide.ui.common.UiEmptyState
import com.velocity.ide.ui.theme.*

/**
 * Live preview route. When a dev-server URL is available (parsed from the real
 * terminal output in MainActivity), the running application is rendered in a
 * WebView. Otherwise an honest empty state is shown — no simulated preview.
 */
@Composable
fun PreviewScreen(
    devServerUrl: String?,
    onBack: () -> Unit
) {
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
                Text("LIVE PREVIEW", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, letterSpacing = 0.4.sp)
                Text(
                    if (devServerUrl != null) "Connected to $devServerUrl" else "No dev server running",
                    color = if (devServerUrl != null) StatusSuccess else TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
        Divider(color = BorderDefault)

        if (devServerUrl != null) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl(devServerUrl)
                    }
                },
                update = { it.loadUrl(devServerUrl) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    UiEmptyState(
                        icon = Icons.Default.PhoneAndroid,
                        title = "No dev server running",
                        caption = "Start a dev server in Terminal (e.g. npm run dev) and the running app will appear here."
                    )
                }
            }
        }
    }
}