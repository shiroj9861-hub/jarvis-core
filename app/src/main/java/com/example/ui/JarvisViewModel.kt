package com.example.ui

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.JarvisDatabase
import com.example.data.model.AutonomousModuleEntity
import com.example.data.model.CodeArtifactEntity
import com.example.data.model.NeuralLogEntity
import com.example.data.model.NeuralTelemetryEntity
import com.example.data.model.SelfRepairEntity
import com.example.data.repository.JarvisRepository
import com.example.domain.CodeTemplates
import com.example.domain.SelfHealingScenarios
import com.example.ui.components.CoreMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

enum class JarvisScreen(val title: String) {
    DASHBOARD("Console"),
    SELF_HEAL("Auto-Heal"),
    CODE_ENGINE("Synthesizer"),
    EVOLUTION("Evolution"),
    TELEMETRY("Neural Store")
}

data class JarvisUiState(
    val currentScreen: JarvisScreen = JarvisScreen.DASHBOARD,
    val coreMode: CoreMode = CoreMode.NOMINAL,
    val coreTemperature: Float = 38.6f,
    val cpuQuantumCycles: Float = 4.92f,
    val memoryUsageMb: Float = 24.3f,
    val healingEfficiency: Float = 99.8f,
    val stabilityIndex: Float = 99.4f,
    val evolutionGeneration: Int = 42,
    val intelligenceScore: Float = 148.6f,
    val autoHealEnabled: Boolean = true,
    val autoEvolveEnabled: Boolean = true,
    val activeIncident: SelfRepairEntity? = null,
    val healingStep: Int = 0, // 0 = Idle, 1 = Detected, 2 = Root Cause Isolated, 3 = Synthesizing Patch, 4 = Regression Testing, 5 = Resolved
    val healingStepDescription: String = "",
    val isSynthesizingCode: Boolean = false,
    val codeSynthesisProgress: Float = 0f,
    val activeCodeArtifact: CodeArtifactEntity? = null,
    val isExecutingSandbox: Boolean = false,
    val sandboxTerminalOutput: String = ""
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JarvisRepository(JarvisDatabase.getInstance(application))
    private val vibrator = application.getSystemService(Vibrator::class.java)

    private val _uiState = MutableStateFlow(JarvisUiState())
    val uiState: StateFlow<JarvisUiState> = _uiState.asStateFlow()

    val recentLogs: StateFlow<List<NeuralLogEntity>> = repository.recentLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selfRepairIncidents: StateFlow<List<SelfRepairEntity>> = repository.selfRepairIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autonomousModules: StateFlow<List<AutonomousModuleEntity>> = repository.autonomousModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val codeArtifacts: StateFlow<List<CodeArtifactEntity>> = repository.codeArtifacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val telemetryHistory: StateFlow<List<NeuralTelemetryEntity>> = repository.telemetryHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var heartbeatJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureDefaultModulesSeeded()
            seedDefaultArtifactsIfEmpty()
            repository.log("INFO", "JARVIS_CORE", "JARVIS-Core v5.2 online. Autonomous self-healing subroutines initialized.")
            startHeartbeatTelemetry()
        }
    }

    private suspend fun seedDefaultArtifactsIfEmpty() {
        val preset = CodeTemplates.PRESETS.first()
        repository.saveCodeArtifact(
            CodeArtifactEntity(
                title = preset.title,
                filename = preset.filename,
                language = preset.language,
                codeContent = preset.code,
                executionOutput = preset.executionLog,
                healthStatus = "OPTIMAL"
            )
        )
    }

    private fun triggerHaptic(durationMs: Long = 40) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun startHeartbeatTelemetry() {
        heartbeatJob?.cancel()
        heartbeatJob = viewModelScope.launch(Dispatchers.Default) {
            var cycle = 0
            while (true) {
                delay(3500)
                cycle++
                if (_uiState.value.coreMode == CoreMode.NOMINAL) {
                    val jitterTemp = 38.0f + (Math.random().toFloat() * 1.5f)
                    val jitterGhz = 4.85f + (Math.random().toFloat() * 0.15f)
                    val jitterMem = 22.0f + (Math.random().toFloat() * 3.0f)

                    _uiState.update {
                        it.copy(
                            coreTemperature = (jitterTemp * 10).toInt() / 10f,
                            cpuQuantumCycles = (jitterGhz * 100).toInt() / 100f,
                            memoryUsageMb = (jitterMem * 10).toInt() / 10f
                        )
                    }

                    if (cycle % 4 == 0) {
                        val messages = listOf(
                            "Neural AST tree integrity verified 100%.",
                            "Heap boundary allocations checked: zero leaks.",
                            "Consensus telemetry stream synced across virtual nodes.",
                            "Autonomous self-monitoring heartbeat optimal. RTT: 0.8ms."
                        )
                        repository.log("INFO", "SYNAPSE_CORE", messages.random())
                    }
                }
            }
        }
    }

    fun selectScreen(screen: JarvisScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun toggleAutoHeal(enabled: Boolean) {
        _uiState.update { it.copy(autoHealEnabled = enabled) }
        viewModelScope.launch {
            repository.log(
                "INFO",
                "HEAL_ENGINE",
                if (enabled) "Self-healing & automated debugging loop ENGAGED." else "Self-healing loop suspended."
            )
        }
    }

    // ==========================================
    // 2. SELF-HEALING & AUTOMATED DEBUGGING LOOP
    // ==========================================
    fun injectFault(scenario: SelfHealingScenarios.FaultScenario) {
        viewModelScope.launch {
            triggerHaptic(90)
            _uiState.update {
                it.copy(
                    coreMode = CoreMode.FAULT_DETECTED,
                    coreTemperature = 68.4f,
                    memoryUsageMb = 89.2f,
                    stabilityIndex = 84.1f,
                    healingStep = 1,
                    healingStepDescription = "CRITICAL: ${scenario.incidentCode} detected in ${scenario.affectedModule}!"
                )
            }

            repository.log("CRITICAL", "ANOMALY_DETECTOR", "${scenario.faultType} detected in ${scenario.affectedModule}", scenario.description)

            val incident = SelfRepairEntity(
                incidentCode = scenario.incidentCode,
                faultType = scenario.faultType,
                affectedModule = scenario.affectedModule,
                rootCause = scenario.rootCause,
                patchDiff = scenario.patchDiff,
                status = "DETECTED",
                recoveryTimeMs = scenario.recoveryTimeMs,
                verificationSuite = scenario.verificationSuite
            )
            val incidentId = repository.recordIncident(incident)
            val recordedIncident = incident.copy(id = incidentId)
            _uiState.update { it.copy(activeIncident = recordedIncident) }

            // Phase 2: Root cause isolation
            delay(1200)
            _uiState.update {
                it.copy(
                    coreMode = CoreMode.SELF_HEALING,
                    healingStep = 2,
                    healingStepDescription = "AST Patcher isolating stack trace and memory boundaries..."
                )
            }
            repository.log("WARN", "AST_PATCHER", "Root cause identified: ${scenario.rootCause}")
            repository.updateIncident(recordedIncident.copy(status = "ISOLATING"))

            // Phase 3: Synthesizing Hot-Patch
            delay(1400)
            _uiState.update {
                it.copy(
                    healingStep = 3,
                    healingStepDescription = "Formulating unified patch AST diff & null-safety contracts..."
                )
            }
            repository.log("PATCH", "AST_PATCHER", "Generated corrective patch AST for ${scenario.affectedModule}")
            repository.updateIncident(recordedIncident.copy(status = "PATCHING"))

            // Phase 4: Sandboxed Regression Testing
            delay(1300)
            _uiState.update {
                it.copy(
                    healingStep = 4,
                    healingStepDescription = "Running sandbox regression suite: ${scenario.verificationSuite}"
                )
            }
            repository.log("INFO", "SANDBOX", "Regression verification underway: 100% test assertions valid.")
            repository.updateIncident(recordedIncident.copy(status = "VERIFYING"))

            // Phase 5: Zero-Downtime Deployment & State Restoration
            delay(1500)
            triggerHaptic(120)
            val updatedIncident = recordedIncident.copy(status = "RESOLVED")
            repository.updateIncident(updatedIncident)

            _uiState.update {
                it.copy(
                    coreMode = CoreMode.NOMINAL,
                    coreTemperature = 39.1f,
                    memoryUsageMb = 23.4f,
                    stabilityIndex = 99.8f,
                    healingEfficiency = 99.9f,
                    healingStep = 5,
                    healingStepDescription = "Self-Repair Successful! Hot-patch deployed with 0ms downtime.",
                    activeIncident = updatedIncident
                )
            }
            repository.log("PATCH", "HEAL_ENGINE", "Incident ${scenario.incidentCode} resolved in ${scenario.recoveryTimeMs}ms. Vitals nominal.")
        }
    }

    // ==========================================
    // 3. RECURSIVE SELF-IMPROVEMENT & FEATURE EXTENSION
    // ==========================================
    fun runAutonomousEvolutionCycle() {
        viewModelScope.launch {
            triggerHaptic(60)
            _uiState.update {
                it.copy(
                    coreMode = CoreMode.EVOLVING,
                    healingStepDescription = "Evaluating architectural bottlenecks and neural matrix topology..."
                )
            }

            repository.log("EVOLVE", "JARVIS_CORE", "Recursive evolution cycle initiated. Evaluating capability matrix.")
            delay(1200)

            val modulePool = listOf(
                AutonomousModuleEntity(
                    id = "mod_predictive_scaler_" + System.currentTimeMillis() % 1000,
                    name = "Dynamic Subsystem Auto-Scaler",
                    category = "Architecture",
                    description = "Autonomously allocates coroutine thread pools and dynamically adjusts GC thresholds during micro-bursts.",
                    version = "v1.0.0",
                    status = "ACTIVE",
                    neuralWeight = 0.89f,
                    efficiencyScore = 99.2f,
                    codePreview = "class AutoScaler {\n  fun balancePools(metric: Telemetry): PoolWeights {\n    return DynamicKernel.reweight(metric)\n  }\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_zero_knowledge_audit_" + System.currentTimeMillis() % 1000,
                    name = "Zero-Knowledge Query Gateway",
                    category = "Security",
                    description = "Verifies cryptographic telemetry validity without exposing confidential neural weight values.",
                    version = "v2.0.1",
                    status = "ACTIVE",
                    neuralWeight = 0.94f,
                    efficiencyScore = 99.7f,
                    codePreview = "class ZkQueryGateway {\n  fun verifyProof(proof: ByteArray): Boolean {\n    return SnarkVerifier.verify(proof)\n  }\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_voice_synthesizer_" + System.currentTimeMillis() % 1000,
                    name = "Holographic Neural Voice Synthesizer",
                    category = "Interface",
                    description = "Synthesizes low-latency British-inflected JARVIS neural voice waveforms directly from cognition streams.",
                    version = "v3.1.0",
                    status = "ACTIVE",
                    neuralWeight = 0.91f,
                    efficiencyScore = 98.9f,
                    codePreview = "class NeuralVoiceSynthesizer {\n  suspend fun synthesize(stream: CognitionFeed) {\n    WaveformDSP.renderPcm(stream.tokenize())\n  }\n}"
                )
            )

            val newModule = modulePool.random()
            repository.saveModule(newModule)

            _uiState.update {
                it.copy(
                    coreMode = CoreMode.NOMINAL,
                    evolutionGeneration = it.evolutionGeneration + 1,
                    intelligenceScore = it.intelligenceScore + 2.8f,
                    stabilityIndex = 99.9f,
                    healingStepDescription = "Dynamic module '${newModule.name}' synthesized and hot-attached."
                )
            }

            repository.log("EVOLVE", "JARVIS_CORE", "Synthesized and dynamically attached [${newModule.name}] (${newModule.version}). Evolution Gen: ${_uiState.value.evolutionGeneration}")
        }
    }

    fun updateModuleWeight(moduleId: String, newWeight: Float) {
        viewModelScope.launch {
            val module = autonomousModules.value.find { it.id == moduleId }
            if (module != null) {
                repository.saveModule(module.copy(neuralWeight = newWeight))
                repository.log("INFO", "NEURAL_STORE", "Adjusted weight for ${module.name} to ${(newWeight * 100).toInt()}%")
            }
        }
    }

    fun toggleModuleStatus(moduleId: String, newStatus: String) {
        viewModelScope.launch {
            val module = autonomousModules.value.find { it.id == moduleId }
            if (module != null) {
                repository.saveModule(module.copy(status = newStatus))
                repository.log("INFO", "NEURAL_STORE", "Module ${module.name} status updated to $newStatus")
            }
        }
    }

    fun deleteModule(moduleId: String) {
        viewModelScope.launch {
            repository.deleteModule(moduleId)
            repository.log("WARN", "NEURAL_STORE", "Detached and pruned autonomous module ID $moduleId")
        }
    }

    // ==========================================
    // 1. AUTONOMOUS CODE GENERATION & EXECUTION
    // ==========================================
    fun synthesizeCodePreset(preset: CodeTemplates.CodePreset) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSynthesizingCode = true, codeSynthesisProgress = 0.1f) }
            triggerHaptic(40)

            repository.log("INFO", "SYNTHESIZER", "Synthesizing code module: ${preset.title} (${preset.language})")

            delay(400)
            _uiState.update { it.copy(codeSynthesisProgress = 0.45f) }
            delay(500)
            _uiState.update { it.copy(codeSynthesisProgress = 0.85f) }
            delay(350)

            val artifact = CodeArtifactEntity(
                title = preset.title,
                filename = preset.filename,
                language = preset.language,
                codeContent = preset.code,
                executionOutput = preset.executionLog,
                healthStatus = "OPTIMAL"
            )
            val id = repository.saveCodeArtifact(artifact)
            val savedArtifact = artifact.copy(id = id)

            _uiState.update {
                it.copy(
                    isSynthesizingCode = false,
                    codeSynthesisProgress = 1.0f,
                    activeCodeArtifact = savedArtifact,
                    sandboxTerminalOutput = preset.executionLog
                )
            }

            repository.log("INFO", "SANDBOX", "Verified artifact ${preset.filename}. ${preset.testCoverage}")
        }
    }

    fun executeActiveArtifactInSandbox(artifact: CodeArtifactEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExecutingSandbox = true) }
            triggerHaptic(30)
            delay(800)
            _uiState.update {
                it.copy(
                    isExecutingSandbox = false,
                    sandboxTerminalOutput = artifact.executionOutput + "\n[RUNTIME_AUDIT] Re-verified at ${System.currentTimeMillis()}. Memory footprint: 1.2MB. Status: OPTIMAL."
                )
            }
            repository.log("INFO", "SANDBOX", "Virtual execution pass complete for ${artifact.filename}.")
        }
    }

    fun deleteArtifact(id: Long) {
        viewModelScope.launch {
            repository.deleteCodeArtifact(id)
        }
    }

    fun clearLogsAndIncidents() {
        viewModelScope.launch {
            repository.clearLogs()
            repository.clearIncidents()
            repository.log("INFO", "JARVIS_CORE", "Neural state store pruned. Fresh telemetry baseline established.")
        }
    }
}
