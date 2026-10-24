package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutonomousModuleEntity
import com.example.data.model.CodeArtifactEntity
import com.example.data.model.NeuralLogEntity
import com.example.data.model.SelfRepairEntity
import com.example.ui.JarvisUiState
import com.example.ui.components.HoloCard
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisBackground
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelemetryScreen(
    state: JarvisUiState,
    logs: List<NeuralLogEntity>,
    incidents: List<SelfRepairEntity>,
    modules: List<AutonomousModuleEntity>,
    artifacts: List<CodeArtifactEntity>,
    onClearLogs: () -> Unit
) {
    val context = LocalContext.current
    var selectedLevelFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }
    val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    val filteredLogs = logs.filter { log ->
        val matchesLevel = if (selectedLevelFilter == "ALL") true else log.level == selectedLevelFilter
        val matchesSearch = if (searchQuery.isBlank()) true else {
            log.message.contains(searchQuery, ignoreCase = true) ||
            log.subsystem.contains(searchQuery, ignoreCase = true) ||
            log.details.contains(searchQuery, ignoreCase = true)
        }
        matchesLevel && matchesSearch
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
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Neural Store",
                            tint = JarvisCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PERSISTENT NEURAL STATE & TELEMETRY",
                            color = JarvisCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "ROOM DATABASE TELEMETRY LOGS, REPAIR AUDIT & STATE EXPORT",
                        color = JarvisTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // 2. Database Summary Statistics
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${logs.size}", color = JarvisCyanPrimary, fontSize = 18.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(text = "LOGS", color = JarvisTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${incidents.size}", color = JarvisGreenSuccess, fontSize = 18.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(text = "REPAIRS", color = JarvisTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${modules.size}", color = JarvisPurpleEvolve, fontSize = 18.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(text = "MODULES", color = JarvisTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${artifacts.size}", color = JarvisAmberAccent, fontSize = 18.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(text = "ARTIFACTS", color = JarvisTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // 3. Action Toolbar (Export JSON / Reset)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val json = buildStructuredStateJson(state, logs, incidents, modules, artifacts)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("JARVIS State JSON", json))
                        Toast.makeText(context, "Neural state JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_export_state_json"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JarvisCyanPrimary.copy(alpha = 0.2f),
                        contentColor = JarvisCyanPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisCyanPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Export JSON", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onClearLogs,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_clear_neural_logs"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JarvisRedAlert.copy(alpha = 0.2f),
                        contentColor = JarvisRedAlert
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JarvisRedAlert),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Prune Logs", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. Search & Filter Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(text = "Search logs by tag, exception, or subsystem...", color = JarvisTextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = JarvisTextMuted, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_logs"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JarvisCyanPrimary,
                        unfocusedBorderColor = JarvisSurfaceBorder,
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val levels = listOf("ALL", "CRITICAL", "WARN", "PATCH", "EVOLVE", "INFO")
                    items(levels) { lvl ->
                        val isSelected = selectedLevelFilter == lvl
                        val chipColor = when (lvl) {
                            "CRITICAL" -> JarvisRedAlert
                            "WARN" -> JarvisAmberAccent
                            "PATCH" -> JarvisGreenSuccess
                            "EVOLVE" -> JarvisPurpleEvolve
                            "INFO" -> JarvisCyanPrimary
                            else -> JarvisCyanPrimary
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) chipColor.copy(alpha = 0.25f) else JarvisSurface)
                                .border(1.dp, if (isSelected) chipColor else JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                                .clickable { selectedLevelFilter = lvl }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = lvl,
                                color = if (isSelected) chipColor else JarvisTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. Filtered Telemetry Logs List
        item {
            Text(
                text = "LOG RECORDS (${filteredLogs.size})",
                color = JarvisCyanPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        if (filteredLogs.isEmpty()) {
            item {
                HoloCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No telemetry logs match the selected filter.",
                        color = JarvisTextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        } else {
            items(filteredLogs, key = { it.id }) { log ->
                val levelColor = when (log.level) {
                    "CRITICAL" -> JarvisRedAlert
                    "WARN" -> JarvisAmberAccent
                    "PATCH" -> JarvisGreenSuccess
                    "EVOLVE" -> JarvisPurpleEvolve
                    else -> JarvisCyanPrimary
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(JarvisCodeBackground)
                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(levelColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = log.level,
                                        color = levelColor,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.subsystem,
                                    color = JarvisTextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = timeFormat.format(Date(log.timestamp)),
                                color = JarvisTextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = log.message,
                            color = JarvisTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )

                        if (log.details.isNotEmpty()) {
                            Text(
                                text = log.details,
                                color = JarvisTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildStructuredStateJson(
    state: JarvisUiState,
    logs: List<NeuralLogEntity>,
    incidents: List<SelfRepairEntity>,
    modules: List<AutonomousModuleEntity>,
    artifacts: List<CodeArtifactEntity>
): String {
    return """
{
  "system": "JARVIS-Core",
  "version": "5.2.0",
  "timestamp": ${System.currentTimeMillis()},
  "vitals": {
    "coreTemperatureC": ${state.coreTemperature},
    "cpuQuantumCyclesGhz": ${state.cpuQuantumCycles},
    "memoryHeapMb": ${state.memoryUsageMb},
    "healingEfficiencyPct": ${state.healingEfficiency},
    "stabilityIndexPct": ${state.stabilityIndex},
    "evolutionGeneration": ${state.evolutionGeneration},
    "intelligenceScore": ${state.intelligenceScore}
  },
  "modulesCount": ${modules.size},
  "incidentsCount": ${incidents.size},
  "logsCount": ${logs.size},
  "artifactsCount": ${artifacts.size}
}
""".trimIndent()
}
