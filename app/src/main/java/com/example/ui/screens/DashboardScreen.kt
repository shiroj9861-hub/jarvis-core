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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NeuralLogEntity
import com.example.domain.SelfHealingScenarios
import com.example.ui.JarvisScreen
import com.example.ui.JarvisUiState
import com.example.ui.components.ArcReactorVisualizer
import com.example.ui.components.CoreMode
import com.example.ui.components.HoloCard
import com.example.ui.components.MetricGauge
import com.example.ui.components.TerminalConsole
import com.example.ui.theme.JarvisAmberAccent
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBlueSecondary
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
fun DashboardScreen(
    state: JarvisUiState,
    logs: List<NeuralLogEntity>,
    onNavigate: (JarvisScreen) -> Unit,
    onInjectRandomFault: () -> Unit,
    onTriggerEvolution: () -> Unit,
    onToggleAutoHeal: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Hologram Title & System Status
        item {
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlowColor = when (state.coreMode) {
                    CoreMode.FAULT_DETECTED -> JarvisRedAlert
                    CoreMode.SELF_HEALING -> JarvisAmberAccent
                    CoreMode.EVOLVING -> JarvisPurpleEvolve
                    else -> JarvisCyanPrimary
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (state.coreMode) {
                                            CoreMode.FAULT_DETECTED -> JarvisRedAlert
                                            CoreMode.SELF_HEALING -> JarvisAmberAccent
                                            CoreMode.EVOLVING -> JarvisPurpleEvolve
                                            else -> JarvisGreenSuccess
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "JARVIS CORE v5.2",
                                color = JarvisCyanPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "AUTONOMOUS SELF-HEALING AI AGENT",
                            color = JarvisTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Auto-heal loop quick toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (state.autoHealEnabled) "HEAL: ON" else "HEAL: OFF",
                            color = if (state.autoHealEnabled) JarvisGreenSuccess else JarvisTextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = state.autoHealEnabled,
                            onCheckedChange = onToggleAutoHeal,
                            modifier = Modifier.testTag("toggle_auto_heal_dashboard"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JarvisGreenSuccess,
                                checkedTrackColor = JarvisSurfaceElevated,
                                uncheckedThumbColor = JarvisTextMuted,
                                uncheckedTrackColor = JarvisSurface
                            )
                        )
                    }
                }
            }
        }

        // 2. Center Arc Reactor Visualizer & Core State Badge
        item {
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlowColor = when (state.coreMode) {
                    CoreMode.FAULT_DETECTED -> JarvisRedAlert
                    CoreMode.SELF_HEALING -> JarvisAmberAccent
                    CoreMode.EVOLVING -> JarvisPurpleEvolve
                    else -> JarvisCyanPrimary
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ArcReactorVisualizer(
                        size = 200.dp,
                        mode = state.coreMode,
                        energyLevel = state.stabilityIndex / 100f
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val (statusLabel, statusColor) = when (state.coreMode) {
                        CoreMode.NOMINAL -> Pair("SYSTEM NOMINAL // SELF-DIAGNOSTICS ACTIVE", JarvisGreenSuccess)
                        CoreMode.FAULT_DETECTED -> Pair("ANOMALY DETECTED // ISOLATING AST STACK", JarvisRedAlert)
                        CoreMode.SELF_HEALING -> Pair("SELF-HEALING // COMPILING AST HOT-PATCH", JarvisAmberAccent)
                        CoreMode.EVOLVING -> Pair("RECURSIVE EVOLUTION // ATTACHING MODULE", JarvisPurpleEvolve)
                        CoreMode.OVERDRIVE -> Pair("OVERDRIVE STABILITY // 100% CONSENSUS", JarvisCyanPrimary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (state.healingStepDescription.isNotEmpty()) {
                        Text(
                            text = state.healingStepDescription,
                            color = JarvisTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // 3. Telemetry Matrix (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Core Temp",
                        value = "${state.coreTemperature}",
                        unit = "°C",
                        fraction = (state.coreTemperature - 30f) / 50f,
                        color = if (state.coreTemperature > 60f) JarvisRedAlert else JarvisCyanPrimary,
                        icon = Icons.Default.Thermostat
                    )
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Quantum Clock",
                        value = "${state.cpuQuantumCycles}",
                        unit = "GHz",
                        fraction = state.cpuQuantumCycles / 5.5f,
                        color = JarvisBlueSecondary,
                        icon = Icons.Default.Speed
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Heap Load",
                        value = "${state.memoryUsageMb}",
                        unit = "MB",
                        fraction = state.memoryUsageMb / 120f,
                        color = if (state.memoryUsageMb > 80f) JarvisAmberAccent else JarvisCyanPrimary,
                        icon = Icons.Default.Memory
                    )
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Self-Heal Ratio",
                        value = "${state.healingEfficiency}",
                        unit = "%",
                        fraction = state.healingEfficiency / 100f,
                        color = JarvisGreenSuccess,
                        icon = Icons.Default.TrendingUp
                    )
                }
            }
        }

        // 4. Quick Directive Actions
        item {
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = JarvisSurfaceBorder
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AUTONOMOUS DIRECTIVE DISPATCH",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onInjectRandomFault,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_inject_fault_dashboard"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisRedAlert.copy(alpha = 0.2f),
                                contentColor = JarvisRedAlert
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisRedAlert),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Inject Fault",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Inject Bug",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onTriggerEvolution,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_trigger_evolution_dashboard"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisPurpleEvolve.copy(alpha = 0.2f),
                                contentColor = JarvisPurpleEvolve
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisPurpleEvolve),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Evolve",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Evolve Gen",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(JarvisScreen.CODE_ENGINE) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_code_synthesizer"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisCyanPrimary.copy(alpha = 0.15f),
                                contentColor = JarvisCyanPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisCyanPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Code Synthesizer",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Synthesizer",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onNavigate(JarvisScreen.SELF_HEAL) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_self_heal_lab"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JarvisAmberAccent.copy(alpha = 0.15f),
                                contentColor = JarvisAmberAccent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, JarvisAmberAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Self-Heal Lab",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Heal Lab",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 5. Live Autonomous Thought Stream
        item {
            TerminalConsole(
                logs = logs,
                maxHeight = 220.dp,
                title = "JARVIS COGNITION & TELEMETRY STREAM"
            )
        }
    }
}
