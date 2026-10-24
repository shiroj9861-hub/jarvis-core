package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.SelfHealingScenarios
import com.example.ui.JarvisScreen
import com.example.ui.JarvisViewModel
import com.example.ui.screens.CodeEngineScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.SelfHealingScreen
import com.example.ui.screens.TelemetryScreen
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyanPrimary
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: JarvisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val logs by viewModel.recentLogs.collectAsStateWithLifecycle()
                val incidents by viewModel.selfRepairIncidents.collectAsStateWithLifecycle()
                val modules by viewModel.autonomousModules.collectAsStateWithLifecycle()
                val artifacts by viewModel.codeArtifacts.collectAsStateWithLifecycle()

                // BackHandler returns to Dashboard if currently on another screen
                BackHandler(enabled = uiState.currentScreen != JarvisScreen.DASHBOARD) {
                    viewModel.selectScreen(JarvisScreen.DASHBOARD)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = JarvisBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("jarvis_bottom_nav"),
                            containerColor = JarvisSurface,
                            tonalElevation = 8.dp
                        ) {
                            val items = listOf(
                                Triple(JarvisScreen.DASHBOARD, Icons.Default.Terminal, "Console"),
                                Triple(JarvisScreen.SELF_HEAL, Icons.Default.Shield, "Auto-Heal"),
                                Triple(JarvisScreen.CODE_ENGINE, Icons.Default.Code, "Synthesizer"),
                                Triple(JarvisScreen.EVOLUTION, Icons.Default.AutoAwesome, "Evolution"),
                                Triple(JarvisScreen.TELEMETRY, Icons.Default.Storage, "Store")
                            )

                            items.forEach { (screen, icon, label) ->
                                val selected = uiState.currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { viewModel.selectScreen(screen) },
                                    modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}"),
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = JarvisBackground,
                                        selectedTextColor = JarvisCyanPrimary,
                                        indicatorColor = JarvisCyanPrimary,
                                        unselectedIconColor = JarvisTextMuted,
                                        unselectedTextColor = JarvisTextMuted
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentScreen) {
                            JarvisScreen.DASHBOARD -> DashboardScreen(
                                state = uiState,
                                logs = logs,
                                onNavigate = { viewModel.selectScreen(it) },
                                onInjectRandomFault = {
                                    val randomScenario = SelfHealingScenarios.SCENARIOS.random()
                                    viewModel.injectFault(randomScenario)
                                },
                                onTriggerEvolution = { viewModel.runAutonomousEvolutionCycle() },
                                onToggleAutoHeal = { viewModel.toggleAutoHeal(it) }
                            )

                            JarvisScreen.SELF_HEAL -> SelfHealingScreen(
                                state = uiState,
                                incidents = incidents,
                                onInjectFault = { viewModel.injectFault(it) },
                                onToggleAutoHeal = { viewModel.toggleAutoHeal(it) }
                            )

                            JarvisScreen.CODE_ENGINE -> CodeEngineScreen(
                                state = uiState,
                                artifacts = artifacts,
                                onSynthesizePreset = { viewModel.synthesizeCodePreset(it) },
                                onExecuteInSandbox = { viewModel.executeActiveArtifactInSandbox(it) },
                                onDeleteArtifact = { viewModel.deleteArtifact(it) }
                            )

                            JarvisScreen.EVOLUTION -> EvolutionScreen(
                                state = uiState,
                                modules = modules,
                                onTriggerEvolution = { viewModel.runAutonomousEvolutionCycle() },
                                onUpdateModuleWeight = { id, weight -> viewModel.updateModuleWeight(id, weight) },
                                onToggleModuleStatus = { id, status -> viewModel.toggleModuleStatus(id, status) },
                                onDeleteModule = { viewModel.deleteModule(it) }
                            )

                            JarvisScreen.TELEMETRY -> TelemetryScreen(
                                state = uiState,
                                logs = logs,
                                incidents = incidents,
                                modules = modules,
                                artifacts = artifacts,
                                onClearLogs = { viewModel.clearLogsAndIncidents() }
                            )
                        }
                    }
                }
            }
        }
    }
}
