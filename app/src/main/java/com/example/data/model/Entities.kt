package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "neural_logs")
data class NeuralLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String, // "INFO", "DEBUG", "WARN", "CRITICAL", "PATCH", "EVOLVE"
    val subsystem: String, // "JARVIS_CORE", "HEAL_ENGINE", "AST_PATCHER", "SANDBOX", "SWARM_LINK", "NEURAL_STORE"
    val message: String,
    val details: String = ""
)

@Entity(tableName = "self_repair_incidents")
data class SelfRepairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val incidentCode: String,
    val faultType: String,
    val affectedModule: String,
    val rootCause: String,
    val patchDiff: String,
    val status: String, // "DETECTED", "ISOLATING", "PATCHING", "VERIFYING", "RESOLVED"
    val recoveryTimeMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val verificationSuite: String
)

@Entity(tableName = "autonomous_modules")
data class AutonomousModuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val version: String,
    val status: String, // "ACTIVE", "STANDBY", "EVOLVING", "HOT_PATCHING"
    val neuralWeight: Float, // 0.0f - 1.0f
    val efficiencyScore: Float, // e.g. 98.4f
    val codePreview: String,
    val executionCount: Long = 0,
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "code_artifacts")
data class CodeArtifactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val filename: String,
    val language: String,
    val codeContent: String,
    val executionOutput: String,
    val healthStatus: String, // "OPTIMAL", "PATCHED", "TESTED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "neural_telemetry")
data class NeuralTelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val coreTemperature: Float,
    val cpuQuantumCycles: Float,
    val memoryLoadMb: Float,
    val healingEfficiency: Float,
    val evolutionGeneration: Int,
    val activeAnomaliesCount: Int
)
