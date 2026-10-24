package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SelfRepairEntity
import com.example.domain.SelfHealingScenarios
import com.example.ui.JarvisUiState
import com.example.ui.components.CodeDiffViewer
import com.example.ui.components.HoloCard
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisGreenSuccess
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
fun SelfHealingScreen(
    state: JarvisUiState,
    incidents: List<SelfRepairEntity>,
    onInjectFault: (SelfHealingScenarios.FaultScenario) -> Unit,
    onToggleAutoHeal: (Boolean) -> Unit
) {
    var selectedScenario by remember { mutableStateOf(SelfHealingScenarios.SCENARIOS.first()) }
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Banner with Loop Status
        item {
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlowColor = if (state.healingStep in 1..4) JarvisAmberAccent else JarvisGreenSuccess
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Self-Healing Engine",
                                tint = if (state.autoHealEnabled) JarvisGreenSuccess else JarvisTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SELF-HEALING & AST DEBUGGER",
                                color = JarvisCyanPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "AUTOMATED ANOMALY DETECTION & HOT-PATCHING",
                            color = JarvisTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Switch(
                        checked = state.autoHealEnabled,
                        onCheckedChange = onToggleAutoHeal,
                        modifier = Modifier.testTag("toggle_auto_heal_lab"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisGreenSuccess,
                            checkedTrackColor = JarvisSurfaceElevated
                        )
                    )
                }
            }
        }

        // 2. Active Healing Pipeline Stepper (Visual Progression)
        item {
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (state.healingStep in 1..4) JarvisAmberAccent else JarvisSurfaceBorder
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AUTOMATED REPAIR PIPELINE",
                            color = JarvisCyanPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (state.healingStep) {
                                1 -> "PHASE 1/5: DETECTED"
                                2 -> "PHASE 2/5: ISOLATING"
                                3 -> "PHASE 3/5: PATCHING"
                                4 -> "PHASE 4/5: VERIFYING"
                                5 -> "PHASE 5/5: RESOLVED"
                                else -> "MONITORING (IDLE)"
                            },
                            color = when (state.healingStep) {
                                1 -> JarvisRedAlert
                                in 2..4 -> JarvisAmberAccent
                                5 -> JarvisGreenSuccess
                                else -> JarvisTextMuted
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.healingStep in 1..4) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = JarvisAmberAccent,
                            trackColor = JarvisSurfaceElevated
                        )
                    }

                    // 5 Step Nodes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val steps = listOf("Detect", "Root Cause", "AST Diff", "Sandbox", "Hot-Patch")
                        steps.forEachIndexed { index, stepName ->
                            val stepNumber = index + 1
                            val isCompleted = state.healingStep >= stepNumber
                            val isCurrent = state.healingStep == stepNumber
                            val stepColor = when {
                                isCurrent -> JarvisAmberAccent
                                isCompleted -> JarvisGreenSuccess
                                else -> JarvisSurfaceBorder
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(stepColor.copy(alpha = 0.25f))
                                        .border(1.5.dp, stepColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted && !isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = JarvisGreenSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "$stepNumber",
                                            color = stepColor,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = stepName,
                                    color = if (isCompleted || isCurrent) JarvisTextPrimary else JarvisTextMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    if (state.healingStepDescription.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(JarvisSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "> ${state.healingStepDescription}",
                                color = JarvisTextSecondary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Fault Injection Simulation Lab
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "FAULT INJECTION SANDBOX",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select a synthetic anomaly to test JARVIS's recursive debugging loop:",
                        color = JarvisTextMuted,
                        fontSize = 12.sp
                    )

                    // Scenario selector chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SelfHealingScenarios.SCENARIOS.forEach { scenario ->
                            val isSelected = selectedScenario.incidentCode == scenario.incidentCode
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) JarvisSurfaceElevated else JarvisSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) JarvisCyanPrimary else JarvisSurfaceBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedScenario = scenario }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BugReport,
                                        contentDescription = null,
                                        tint = if (isSelected) JarvisRedAlert else JarvisTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = scenario.faultType,
                                            color = JarvisTextPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${scenario.affectedModule} • ${scenario.description}",
                                            color = JarvisTextMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { onInjectFault(selectedScenario) },
                        enabled = state.healingStep !in 1..4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_trigger_fault_injection"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JarvisRedAlert.copy(alpha = 0.25f),
                            contentColor = JarvisRedAlert
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JarvisRedAlert),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "INJECT FAULT & TRIGGER AUTO-HEAL",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 4. Latest Corrective Patch Diff Inspector
        val latestDiff = state.activeIncident?.patchDiff ?: selectedScenario.patchDiff
        item {
            CodeDiffViewer(
                diffText = latestDiff,
                title = "HOT-PATCH AST UNIFIED DIFF // ${state.activeIncident?.affectedModule ?: selectedScenario.affectedModule}"
            )
        }

        // 5. Incident History Log
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SELF-REPAIR INCIDENT AUDIT TRAIL (${incidents.size})",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (incidents.isEmpty()) {
                        Text(
                            text = "No incidents logged yet. Inject a synthetic anomaly above to test the autonomous repair loop.",
                            color = JarvisTextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            incidents.forEach { incident ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(JarvisSurfaceElevated)
                                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                    ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = incident.faultType,
                                                color = JarvisGreenSuccess,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${incident.recoveryTimeMs}ms",
                                                color = JarvisCyanPrimary,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 11.sp
                                            )
                                        }

                                        Text(
                                            text = "Module: ${incident.affectedModule} • ${incident.incidentCode}",
                                            color = JarvisTextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )

                                        Text(
                                            text = "Fix: ${incident.verificationSuite}",
                                            color = JarvisTextSecondary,
                                            fontSize = 11.sp
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
