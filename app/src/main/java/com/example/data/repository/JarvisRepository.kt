package com.example.data.repository

import com.example.data.database.JarvisDatabase
import com.example.data.model.AutonomousModuleEntity
import com.example.data.model.CodeArtifactEntity
import com.example.data.model.NeuralLogEntity
import com.example.data.model.NeuralTelemetryEntity
import com.example.data.model.SelfRepairEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class JarvisRepository(private val database: JarvisDatabase) {

    val recentLogs: Flow<List<NeuralLogEntity>> = database.neuralLogDao().getRecentLogs()
    val selfRepairIncidents: Flow<List<SelfRepairEntity>> = database.selfRepairDao().getAllIncidents()
    val autonomousModules: Flow<List<AutonomousModuleEntity>> = database.autonomousModuleDao().getAllModules()
    val codeArtifacts: Flow<List<CodeArtifactEntity>> = database.codeArtifactDao().getAllArtifacts()
    val telemetryHistory: Flow<List<NeuralTelemetryEntity>> = database.telemetryDao().getRecentTelemetry()

    suspend fun log(
        level: String,
        subsystem: String,
        message: String,
        details: String = ""
    ) {
        database.neuralLogDao().insertLog(
            NeuralLogEntity(
                level = level,
                subsystem = subsystem,
                message = message,
                details = details
            )
        )
    }

    suspend fun recordIncident(incident: SelfRepairEntity): Long {
        return database.selfRepairDao().insertIncident(incident)
    }

    suspend fun updateIncident(incident: SelfRepairEntity) {
        database.selfRepairDao().updateIncident(incident)
    }

    suspend fun saveModule(module: AutonomousModuleEntity) {
        database.autonomousModuleDao().insertOrUpdateModule(module)
    }

    suspend fun deleteModule(moduleId: String) {
        database.autonomousModuleDao().deleteModule(moduleId)
    }

    suspend fun saveCodeArtifact(artifact: CodeArtifactEntity): Long {
        return database.codeArtifactDao().insertArtifact(artifact)
    }

    suspend fun deleteCodeArtifact(id: Long) {
        database.codeArtifactDao().deleteArtifact(id)
    }

    suspend fun recordTelemetry(telemetry: NeuralTelemetryEntity) {
        database.telemetryDao().insertTelemetry(telemetry)
    }

    suspend fun clearLogs() {
        database.neuralLogDao().clearLogs()
    }

    suspend fun clearIncidents() {
        database.selfRepairDao().clearIncidents()
    }

    suspend fun ensureDefaultModulesSeeded() {
        val existing = database.autonomousModuleDao().getAllModules().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                AutonomousModuleEntity(
                    id = "mod_ast_healer",
                    name = "AST Auto-Healer & Patch Engine",
                    category = "Self-Repair",
                    description = "Monitors runtime exceptions, performs root cause AST analysis, and deploys verified hot-patches without downtime.",
                    version = "v3.2.0",
                    status = "ACTIVE",
                    neuralWeight = 0.96f,
                    efficiencyScore = 99.8f,
                    codePreview = "fun onAnomalyDetected(ex: Throwable) {\n  val patch = ASTSynthesizer.generateFix(ex)\n  RegressionRunner.verify(patch)\n  HotPatcher.inject(patch)\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_code_synthesizer",
                    name = "Autonomous Code Generation Engine",
                    category = "Synthesis",
                    description = "Generates high-performance multi-language code modules, full-stack components, and state engines on demand.",
                    version = "v4.1.2",
                    status = "ACTIVE",
                    neuralWeight = 0.92f,
                    efficiencyScore = 98.6f,
                    codePreview = "suspend fun synthesizeModule(prompt: Directive): CodeArtifact {\n  val ast = NeuralAST.formulate(prompt)\n  return VirtualSandbox.compileAndTest(ast)\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_quantum_shield",
                    name = "Quantum Threat Neutralizer",
                    category = "Security",
                    description = "Defends neural weights, scans memory buffers for race conditions, and isolates malicious or erratic execution threads.",
                    version = "v2.8.5",
                    status = "ACTIVE",
                    neuralWeight = 0.88f,
                    efficiencyScore = 99.4f,
                    codePreview = "class QuantumShield {\n  fun verifyBufferIntegrity(ptr: Long): Boolean {\n    return SHA3_512.verify(ptr) && !HeapLeakDetector.isTainted(ptr)\n  }\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_memory_consolidator",
                    name = "Deep Memory Vector Consolidator",
                    category = "State",
                    description = "Periodically compresses telemetry traces, prunes dead locks, and reorganizes vector embeddings for instant lookup.",
                    version = "v1.9.4",
                    status = "ACTIVE",
                    neuralWeight = 0.84f,
                    efficiencyScore = 97.9f,
                    codePreview = "fun consolidateTelemetry(stream: Flow<Event>) {\n  val vectors = stream.toCompressedEmbeddings()\n  NeuralStore.commitAtomic(vectors)\n}"
                ),
                AutonomousModuleEntity(
                    id = "mod_swarm_orchestrator",
                    name = "Multi-Agent Swarm Orchestrator",
                    category = "Swarm",
                    description = "Coordinates distributed sub-agents, delegates micro-tasks, and synthesizes consensus across autonomous threads.",
                    version = "v2.1.0",
                    status = "STANDBY",
                    neuralWeight = 0.76f,
                    efficiencyScore = 96.2f,
                    codePreview = "suspend fun coordinateSwarm(objective: String): ConsensusResult {\n  val votes = agents.map { it.evaluateAsync(objective) }.awaitAll()\n  return ConsensusEngine.resolve(votes)\n}"
                )
            )
            database.autonomousModuleDao().insertAll(defaults)
            log("INFO", "JARVIS_CORE", "Initialized neural module matrix with 5 primary autonomous subsystems.")
        }
    }
}
