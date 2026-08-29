package com.velocity.ide.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern

@Composable
fun CodeEditorView(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(scrollState)
    ) {
        // Line Numbers Column
        Column(
            modifier = Modifier
                .width(40.dp)
                .padding(vertical = 8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.End
        ) {
            val lines = value.split("\n").size
            for (i in 1..lines) {
                Text(
                    text = "$i",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // Editor input
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
                .fillMaxHeight(),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            visualTransformation = SyntaxHighlighterTransformation(),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    innerTextField()
                }
            }
        )
    }
}

class SyntaxHighlighterTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            highlightCode(text.text),
            OffsetMapping.Identity
        )
    }

    private fun highlightCode(code: String): AnnotatedString {
        // Simple highlighter matching keywords, types, strings, comments
        val keywords = Pattern.compile("\\b(class|interface|fun|function|val|var|const|let|import|from|export|return|if|else|for|while|do|switch|case|break|continue|new|this|package|public|private|protected|internal)\\b")
        val types = Pattern.compile("\\b(String|Int|Boolean|Float|Double|Long|List|Map|Set|Unit|void|number|any|unknown)\\b")
        val strings = Pattern.compile("\".*?\"|'.*?'|`.*?`")
        val comments = Pattern.compile("//.*?$|/\\*.*?\\*/", Pattern.MULTILINE)

        val annotatedString = buildAnnotatedString {
            append(code)

            // Highlight matches
            val matchers = listOf(
                Pair(keywords, SpanStyle(color = Color(0xFFEC4899), fontWeight = FontWeight.Bold)), // Pink accent
                Pair(types, SpanStyle(color = Color(0xFF3B82F6))),                             // Blue accent
                Pair(strings, SpanStyle(color = Color(0xFF10B981))),                           // Green accent
                Pair(comments, SpanStyle(color = Color(0xFF64748B)))                            // Slate gray
            )

            for ((pattern, style) in matchers) {
                val matcher = pattern.matcher(code)
                while (matcher.find()) {
                    addStyle(style, matcher.start(), matcher.end())
                }
            }
        }
        return annotatedString
    }
}
