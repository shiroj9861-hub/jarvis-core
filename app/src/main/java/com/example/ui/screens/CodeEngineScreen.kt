package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.CodeArtifactEntity
import com.example.domain.CodeTemplates
import com.example.ui.JarvisUiState
import com.example.ui.components.HoloCard
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBlueSecondary
import com.example.ui.theme.JarvisCodeBackground
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisPurpleEvolve
import com.example.ui.theme.JarvisRedAlert
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun CodeEngineScreen(
    state: JarvisUiState,
    artifacts: List<CodeArtifactEntity>,
    onSynthesizePreset: (CodeTemplates.CodePreset) -> Unit,
    onExecuteInSandbox: (CodeArtifactEntity) -> Unit,
    onDeleteArtifact: (Long) -> Unit
) {
    val context = LocalContext.current
    var customDirectiveText by remember { mutableStateOf("") }
    var selectedPreset by remember { mutableStateOf(CodeTemplates.PRESETS.first()) }
    var displayedArtifact by remember {
        mutableStateOf(artifacts.firstOrNull() ?: CodeArtifactEntity(
            title = selectedPreset.title,
            filename = selectedPreset.filename,
            language = selectedPreset.language,
            codeContent = selectedPreset.code,
            executionOutput = selectedPreset.executionLog,
            healthStatus = "OPTIMAL"
        ))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Code Engine",
                            tint = JarvisCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AUTONOMOUS CODE SYNTHESIZER",
                            color = JarvisCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "DYNAMIC FULL-STACK MODULE GENERATOR & VIRTUAL SANDBOX",
                        color = JarvisTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // 2. Preset Directives Selector
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECT ARCHITECTURAL PRESET",
                    color = JarvisCyanPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CodeTemplates.PRESETS) { preset ->
                        val isSelected = selectedPreset.title == preset.title
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) JarvisSurfaceElevated else JarvisSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) JarvisCyanPrimary else JarvisSurfaceBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedPreset = preset
                                    onSynthesizePreset(preset)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = preset.title,
                                    color = if (isSelected) JarvisCyanPrimary else JarvisTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${preset.language} • ${preset.filename}",
                                    color = JarvisTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Custom Directive Input
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AUTONOMOUS GOAL DIRECTIVE",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customDirectiveText,
                            onValueChange = { customDirectiveText = it },
                            placeholder = {
                                Text(
                                    text = "e.g. Synthesize high-throughput distributed lock...",
                                    color = JarvisTextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_custom_code_directive"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisCyanPrimary,
                                unfocusedBorderColor = JarvisSurfaceBorder,
                                focusedTextColor = JarvisTextPrimary,
                                unfocusedTextColor = JarvisTextPrimary
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                if (customDirectiveText.isNotBlank()) {
                                    val customPreset = CodeTemplates.CodePreset(
                                        title = customDirectiveText,
                                        filename = "DynamicModule_${System.currentTimeMillis() % 1000}.ts",
                                        language = "TypeScript",
                                        description = "Autonomous synthesis for: $customDirectiveText",
                                        code = """
// AUTONOMOUSLY SYNTHESIZED BY JARVIS-CORE
// DIRECTIVE: $customDirectiveText
export class SynthesizedModule {
  private activeState = true;

  public executeAutonomousWorkflow(): Promise<{ success: boolean; latencyMs: number }> {
    const start = performance.now();
    console.log('[JARVIS] Executing directive: $customDirectiveText');
    return Promise.resolve({
      success: true,
      latencyMs: Math.round(performance.now() - start)
    });
  }
}
""".trimIndent(),
                                        executionLog = """
[COMPILER] Target TS verified. AST validated.
[SANDBOX] SynthesizedModule initialized with 0 overhead.
[TEST] 4/4 synthetic verification criteria PASSED.
""".trimIndent(),
                                        complexity = "O(1) Autonomous",
                                        testCoverage = "100%"
                                    )
                                    onSynthesizePreset(customPreset)
                                    customDirectiveText = ""
                                }
                            },
                            modifier = Modifier.testTag("btn_synthesize_directive"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisCyanPrimary,
                                contentColor = JarvisBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Synthesize")
                        }
                    }

                    if (state.isSynthesizingCode) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Synthesizing AST & Memory Contracts...",
                                    color = JarvisAmberAccent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "${(state.codeSynthesisProgress * 100).toInt()}%",
                                    color = JarvisAmberAccent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                )
                            }
                            LinearProgressIndicator(
                                progress = { state.codeSynthesisProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = JarvisCyanPrimary,
                                trackColor = JarvisSurfaceElevated
                            )
                        }
                    }
                }
            }
        }

        // 4. Code Workspace & Syntax Viewer
        val currentCode = state.activeCodeArtifact?.codeContent ?: displayedArtifact.codeContent
        val currentFilename = state.activeCodeArtifact?.filename ?: displayedArtifact.filename
        val currentLang = state.activeCodeArtifact?.language ?: displayedArtifact.language

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(JarvisCodeBackground)
                    .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(8.dp))
            ) {
                // Code Tab Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(JarvisCyanPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentLang.uppercase(),
                            color = JarvisCyanPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentFilename,
                        color = JarvisTextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    // Copy Code Button
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("JARVIS Code", currentCode))
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = JarvisTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Code Lines with syntax styling
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    currentCode.lines().forEachIndexed { idx, line ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%2d ", idx + 1),
                                color = JarvisTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(28.dp)
                            )
                            Text(
                                text = line,
                                color = when {
                                    line.trim().startsWith("//") || line.trim().startsWith("#") -> JarvisTextMuted
                                    line.contains("class ") || line.contains("interface ") -> JarvisCyanPrimary
                                    line.contains("import ") || line.contains("package ") -> JarvisPurpleEvolve
                                    line.contains("fun ") || line.contains("function ") -> JarvisAmberAccent
                                    line.contains("return ") -> JarvisGreenSuccess
                                    else -> JarvisTextPrimary
                                },
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // 5. Virtual Sandbox Execution Runner
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VIRTUAL SANDBOX RUNNER",
                            color = JarvisCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                val target = state.activeCodeArtifact ?: displayedArtifact
                                onExecuteInSandbox(target)
                            },
                            enabled = !state.isExecutingSandbox,
                            modifier = Modifier.testTag("btn_run_sandbox_code"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisGreenSuccess.copy(alpha = 0.2f),
                                contentColor = JarvisGreenSuccess
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisGreenSuccess),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            if (state.isExecutingSandbox) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = JarvisGreenSuccess,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RUN & TEST",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val output = if (state.sandboxTerminalOutput.isNotEmpty()) {
                        state.sandboxTerminalOutput
                    } else {
                        state.activeCodeArtifact?.executionOutput ?: displayedArtifact.executionOutput
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(JarvisCodeBackground)
                            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = output,
                            color = JarvisGreenSuccess,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 6. Saved Artifacts Gallery
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SAVED CODE ARTIFACTS (${artifacts.size})",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (artifacts.isEmpty()) {
                        Text(
                            text = "No saved artifacts yet. Synthesize code using the presets above.",
                            color = JarvisTextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            artifacts.forEach { artifact ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(JarvisSurfaceElevated)
                                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            displayedArtifact = artifact
                                        }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = artifact.title,
                                                color = JarvisTextPrimary,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${artifact.language} • ${artifact.filename}",
                                                color = JarvisTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = { onDeleteArtifact(artifact.id) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Artifact",
                                                tint = JarvisRedAlert.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
