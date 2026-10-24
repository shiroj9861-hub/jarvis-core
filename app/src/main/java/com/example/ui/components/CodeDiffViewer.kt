package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisCodeBackground
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisDiffGreenBg
import com.example.ui.theme.JarvisDiffRedBg
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisRedAlert
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary

@Composable
fun CodeDiffViewer(
    modifier: Modifier = Modifier,
    diffText: String,
    title: String = "PATCH AST DIFF"
) {
    val context = LocalContext.current
    val lines = diffText.lines()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(JarvisCodeBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = JarvisCyanPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("JARVIS Code Diff", diffText))
                    Toast.makeText(context, "Patch diff copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.height(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Patch Diff",
                    tint = JarvisTextMuted,
                    modifier = Modifier.height(16.dp)
                )
            }
        }

        // Diff lines
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            lines.forEachIndexed { index, line ->
                val (bgColor, textColor, prefixSymbol) = when {
                    line.startsWith("+") && !line.startsWith("+++") -> Triple(
                        JarvisDiffGreenBg,
                        JarvisGreenSuccess,
                        "+"
                    )
                    line.startsWith("-") && !line.startsWith("---") -> Triple(
                        JarvisDiffRedBg,
                        JarvisRedAlert,
                        "-"
                    )
                    line.startsWith("@@") -> Triple(
                        Color(0x2280D8FF),
                        JarvisCyanPrimary,
                        "@"
                    )
                    else -> Triple(Color.Transparent, JarvisTextPrimary, " ")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format("%2d", index + 1),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = JarvisTextMuted,
                        modifier = Modifier.width(28.dp)
                    )
                    Text(
                        text = line,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}
