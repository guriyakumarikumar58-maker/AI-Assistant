package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CodeBgDark
import com.example.ui.theme.CyberNeonCyan
import com.example.ui.theme.GeminiBlueAccent
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiBorder
import com.example.ui.theme.GeminiDarkBg
import com.example.ui.theme.GeminiSurface
import com.example.ui.theme.GeminiSurfaceElevated
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.GeminiTextTertiary
import com.example.ui.theme.GeminiSurfaceHover
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import kotlinx.coroutines.delay

/**
 * Parsed element types for rich live markdown and code rendering.
 * Strictly guarantees clean display without asterisks (* or **).
 */
sealed class ResponseBlock {
    data class Paragraph(val text: String) : ResponseBlock()
    data class Header(val level: Int, val text: String) : ResponseBlock()
    data class BulletItem(val text: String) : ResponseBlock()
    data class NumberedItem(val number: String, val text: String) : ResponseBlock()
    data class CodeSnippet(val language: String, val code: String) : ResponseBlock()
    data class Callout(val text: String, val isWarning: Boolean = false) : ResponseBlock()
}

/**
 * Parses raw AI response into structured presentation blocks with code detection and clean formatting.
 */
fun parseResponseToBlocks(rawText: String): List<ResponseBlock> {
    val sanitized = rawText
        .replace("**", "")
        .replace("*", "")
        .trim()

    if (sanitized.isBlank()) return emptyList()

    val blocks = mutableListOf<ResponseBlock>()
    val lines = sanitized.lines()
    var index = 0

    while (index < lines.size) {
        val line = lines[index]
        val trimmed = line.trim()

        if (trimmed.startsWith("```")) {
            // Code block parsing
            val lang = trimmed.removePrefix("```").trim().ifBlank { "code" }
            val codeLines = mutableListOf<String>()
            index++
            while (index < lines.size && !lines[index].trim().startsWith("```")) {
                codeLines.add(lines[index])
                index++
            }
            if (index < lines.size && lines[index].trim().startsWith("```")) {
                index++ // skip closing fence
            }
            blocks.add(ResponseBlock.CodeSnippet(language = lang, code = codeLines.joinToString("\n")))
            continue
        }

        when {
            trimmed.startsWith("### ") -> {
                blocks.add(ResponseBlock.Header(3, trimmed.removePrefix("### ").trim()))
            }
            trimmed.startsWith("## ") -> {
                blocks.add(ResponseBlock.Header(2, trimmed.removePrefix("## ").trim()))
            }
            trimmed.startsWith("# ") -> {
                blocks.add(ResponseBlock.Header(1, trimmed.removePrefix("# ").trim()))
            }
            trimmed.startsWith("• ") || trimmed.startsWith("- ") -> {
                val bulletContent = trimmed.substring(2).trim()
                blocks.add(ResponseBlock.BulletItem(bulletContent))
            }
            trimmed.matches(Regex("""^\d+[\.\)]\s+.*""")) -> {
                val numPart = trimmed.substringBefore(" ").replace(".", "").replace(")", "").trim()
                val textPart = trimmed.substringAfter(" ").trim()
                blocks.add(ResponseBlock.NumberedItem(numPart, textPart))
            }
            trimmed.startsWith("> ") -> {
                blocks.add(ResponseBlock.Callout(trimmed.removePrefix("> ").trim()))
            }
            trimmed.isNotBlank() -> {
                blocks.add(ResponseBlock.Paragraph(line))
            }
        }
        index++
    }

    return blocks
}

/**
 * Arya's Live Markdown & Code Renderer:
 * - Live dynamic block rendering
 * - Highlighted code snippets with copy button
 * - Styled lists, headers, and callouts
 * - Zero asterisks guarantee
 */
@Composable
fun AryaRichResponseView(
    response: String,
    modifier: Modifier = Modifier,
    onSpeak: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val blocks = remember(response) { parseResponseToBlocks(response) }

    // Live progressive rendering count
    var visibleBlockCount by remember(response) { mutableIntStateOf(0) }

    LaunchedEffect(response) {
        visibleBlockCount = 0
        if (blocks.isNotEmpty()) {
            for (i in 1..blocks.size) {
                visibleBlockCount = i
                delay(30L) // smooth progressive live rendering
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("arya_rich_response_view")
    ) {
        blocks.take(visibleBlockCount).forEachIndexed { i, block ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = 180))
            ) {
                when (block) {
                    is ResponseBlock.Header -> {
                        HeaderBlockView(block)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    is ResponseBlock.Paragraph -> {
                        ParagraphBlockView(block)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    is ResponseBlock.BulletItem -> {
                        BulletItemBlockView(block)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    is ResponseBlock.NumberedItem -> {
                        NumberedItemBlockView(block)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    is ResponseBlock.CodeSnippet -> {
                        CodeSnippetBlockView(block)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    is ResponseBlock.Callout -> {
                        CalloutBlockView(block)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderBlockView(block: ResponseBlock.Header) {
    val fontSize = when (block.level) {
        1 -> 18.sp
        2 -> 16.sp
        else -> 14.sp
    }
    val color = when (block.level) {
        1 -> CyberNeonCyan
        2 -> NeonViolet
        else -> GeminiBlueLight
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = (fontSize.value + 4).dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = block.text,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.2.sp
        )
    }
}

@Composable
private fun ParagraphBlockView(block: ResponseBlock.Paragraph) {
    Text(
        text = block.text,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = GeminiTextPrimary,
        fontWeight = FontWeight.Normal
    )
}

@Composable
private fun BulletItemBlockView(block: ResponseBlock.BulletItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(NeonEmerald)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = block.text,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            color = GeminiTextPrimary
        )
    }
}

@Composable
private fun NumberedItemBlockView(block: ResponseBlock.NumberedItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, top = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GeminiBlueLight.copy(alpha = 0.15f))
                .border(1.dp, GeminiBlueLight.copy(alpha = 0.4f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = block.number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GeminiBlueLight
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = block.text,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            color = GeminiTextPrimary
        )
    }
}

@Composable
private fun CodeSnippetBlockView(block: ResponseBlock.CodeSnippet) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CodeBgDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("code_snippet_card")
    ) {
        Column {
            // Header bar: Language pill & Copy Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeminiSurfaceElevated.copy(alpha = 0.7f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = CyberNeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = block.language.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberNeonCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isCopied) NeonEmerald.copy(alpha = 0.2f) else GeminiSurfaceHover)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Code", block.code)
                            clipboard.setPrimaryClip(clip)
                            isCopied = true
                            Toast.makeText(context, "कोड कॉपी किया गया! (Code Copied)", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = if (isCopied) NeonEmerald else GeminiTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = if (isCopied) "कॉपी हुआ" else "कॉपी करें",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isCopied) NeonEmerald else GeminiTextSecondary
                    )
                }
            }

            // Code Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = block.code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFE6EDF3)
                )
            }
        }
    }
}

@Composable
private fun CalloutBlockView(block: ResponseBlock.Callout) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeminiSurfaceElevated.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(width = 3.dp, height = 36.dp)
                    .background(NeonAmber, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = block.text,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = GeminiTextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
