package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AutonomousModuleEntity
import com.example.ui.JarvisUiState
import com.example.ui.components.HoloCard
import com.example.ui.components.MetricGauge
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
fun EvolutionScreen(
    state: JarvisUiState,
    modules: List<AutonomousModuleEntity>,
    onTriggerEvolution: () -> Unit,
    onUpdateModuleWeight: (String, Float) -> Unit,
    onToggleModuleStatus: (String, String) -> Unit,
    onDeleteModule: (String) -> Unit
) {
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
            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlowColor = JarvisPurpleEvolve
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Neural Evolution",
                            tint = JarvisPurpleEvolve,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RECURSIVE SELF-EVOLUTION ENGINE",
                            color = JarvisPurpleEvolve,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "CONTINUOUS TOPOLOGY OPTIMIZATION & DYNAMIC MODULE EXTENSION",
                        color = JarvisTextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // 2. Evolution Telemetry Metrics (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Evolution Gen",
                        value = "${state.evolutionGeneration}",
                        unit = "GEN",
                        fraction = (state.evolutionGeneration % 100) / 100f,
                        color = JarvisPurpleEvolve,
                        icon = Icons.Default.AutoAwesome
                    )
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Cognitive Index",
                        value = "${state.intelligenceScore}",
                        unit = "IQx",
                        fraction = state.intelligenceScore / 200f,
                        color = JarvisCyanPrimary,
                        icon = Icons.Default.Psychology
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Active Modules",
                        value = "${modules.count { it.status == "ACTIVE" }}",
                        unit = "/ ${modules.size}",
                        fraction = modules.count { it.status == "ACTIVE" }.toFloat() / (modules.size.coerceAtLeast(1)),
                        color = JarvisGreenSuccess,
                        icon = Icons.Default.Hub
                    )
                    MetricGauge(
                        modifier = Modifier.weight(1f),
                        title = "Matrix Consensus",
                        value = "${state.stabilityIndex}",
                        unit = "%",
                        fraction = state.stabilityIndex / 100f,
                        color = JarvisBlueSecondary,
                        icon = Icons.Default.Hub
                    )
                }
            }
        }

        // 3. Trigger Recursive Evolution Action Card
        item {
            HoloCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "RECURSIVE SYNTHESIS DISPATCH",
                        color = JarvisCyanPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Instruct JARVIS to evaluate architectural gaps, formulate a new autonomous subsystem, and hot-attach it into runtime memory.",
                        color = JarvisTextMuted,
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = onTriggerEvolution,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_trigger_evolution_cycle"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JarvisPurpleEvolve.copy(alpha = 0.25f),
                            contentColor = JarvisPurpleEvolve
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JarvisPurpleEvolve),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TRIGGER RECURSIVE EVOLUTION CYCLE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 4. Dynamic Module Matrix Registry
        item {
            Text(
                text = "ACTIVE AUTONOMOUS MODULE REGISTRY (${modules.size})",
                color = JarvisCyanPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        items(modules, key = { it.id }) { module ->
            var expanded by remember { mutableStateOf(false) }

            HoloCard(
                modifier = Modifier.fillMaxWidth(),
                accentGlowColor = if (module.status == "ACTIVE") JarvisCyanPrimary.copy(alpha = 0.3f) else null
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (module.status == "ACTIVE") JarvisGreenSuccess else JarvisTextMuted)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = module.name,
                                    color = JarvisTextPrimary,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${module.category} • ${module.version}",
                                    color = JarvisTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = module.status == "ACTIVE",
                                onCheckedChange = { active ->
                                    onToggleModuleStatus(module.id, if (active) "ACTIVE" else "STANDBY")
                                },
                                modifier = Modifier.testTag("switch_module_${module.id}"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = JarvisGreenSuccess,
                                    checkedTrackColor = JarvisSurfaceElevated,
                                    uncheckedThumbColor = JarvisTextMuted,
                                    uncheckedTrackColor = JarvisSurface
                                )
                            )

                            IconButton(
                                onClick = { onDeleteModule(module.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Detach Module",
                                    tint = JarvisRedAlert.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = module.description,
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )

                    // Weight slider
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Neural Weight Bias",
                                color = JarvisTextMuted,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${(module.neuralWeight * 100).toInt()}%",
                                color = JarvisCyanPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = module.neuralWeight,
                            onValueChange = { newWeight ->
                                onUpdateModuleWeight(module.id, newWeight)
                            },
                            valueRange = 0.1f..1.0f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("slider_weight_${module.id}"),
                            colors = SliderDefaults.colors(
                                thumbColor = JarvisCyanPrimary,
                                activeTrackColor = JarvisCyanPrimary,
                                inactiveTrackColor = JarvisSurfaceElevated
                            )
                        )
                    }

                    // Code snippet accordion toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(JarvisSurfaceElevated)
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = JarvisCyanPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (expanded) "Hide AST Implementation" else "Inspect AST Implementation",
                                color = JarvisTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = JarvisTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(visible = expanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(JarvisCodeBackground)
                                .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = module.codePreview,
                                color = JarvisCyanPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
