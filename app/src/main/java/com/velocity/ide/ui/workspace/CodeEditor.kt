package com.velocity.ide.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.velocity.ide.ui.theme.AccentOrange
import com.velocity.ide.ui.theme.BgOverlay
import com.velocity.ide.ui.theme.TextMuted

@Composable
fun CodeEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSizeSp: Int = 14,
    showLineNumbers: Boolean = true
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val lineCount = value.lines().size.coerceAtLeast(1)
    val lineNumbers = (1..lineCount).joinToString("\n")

    val textStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = fontSizeSp.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = (fontSizeSp + 6).sp
    )

    Row(
        modifier = modifier
            .background(BgOverlay)
            .verticalScroll(verticalScroll)
            .horizontalScroll(horizontalScroll)
            .padding(vertical = 12.dp)
    ) {
        // Gutter
        if (showLineNumbers) {
            Text(
                text = lineNumbers,
                style = textStyle.copy(color = TextMuted),
                modifier = Modifier
                    .width(44.dp)
                    .padding(end = 8.dp)
                    .background(BgOverlay)
            )
        }

        // Editor
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            cursorBrush = SolidColor(AccentOrange),
            decorationBox = { innerTextField ->
                innerTextField()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        )
    }
}