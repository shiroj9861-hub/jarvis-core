package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AutonomousModuleEntity
import com.example.data.model.CodeArtifactEntity
import com.example.data.model.NeuralLogEntity
import com.example.data.model.NeuralTelemetryEntity
import com.example.data.model.SelfRepairEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NeuralLogDao {
    @Query("SELECT * FROM neural_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<NeuralLogEntity>>

    @Query("SELECT * FROM neural_logs WHERE level = :level ORDER BY timestamp DESC LIMIT 100")
    fun getLogsByLevel(level: String): Flow<List<NeuralLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: NeuralLogEntity): Long

    @Query("DELETE FROM neural_logs")
    suspend fun clearLogs()
}

@Dao
interface SelfRepairDao {
    @Query("SELECT * FROM self_repair_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<SelfRepairEntity>>

    @Query("SELECT * FROM self_repair_incidents WHERE id = :id")
    suspend fun getIncidentById(id: Long): SelfRepairEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: SelfRepairEntity): Long

    @Update
    suspend fun updateIncident(incident: SelfRepairEntity)

    @Query("DELETE FROM self_repair_incidents")
    suspend fun clearIncidents()
}

@Dao
interface AutonomousModuleDao {
    @Query("SELECT * FROM autonomous_modules ORDER BY neuralWeight DESC")
    fun getAllModules(): Flow<List<AutonomousModuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateModule(module: AutonomousModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modules: List<AutonomousModuleEntity>)

    @Update
    suspend fun updateModule(module: AutonomousModuleEntity)

    @Query("DELETE FROM autonomous_modules WHERE id = :moduleId")
    suspend fun deleteModule(moduleId: String)
}

@Dao
interface CodeArtifactDao {
    @Query("SELECT * FROM code_artifacts ORDER BY timestamp DESC")
    fun getAllArtifacts(): Flow<List<CodeArtifactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtifact(artifact: CodeArtifactEntity): Long

    @Query("DELETE FROM code_artifacts WHERE id = :id")
    suspend fun deleteArtifact(id: Long)

    @Query("DELETE FROM code_artifacts")
    suspend fun clearArtifacts()
}

@Dao
interface TelemetryDao {
    @Query("SELECT * FROM neural_telemetry ORDER BY timestamp DESC LIMIT 50")
    fun getRecentTelemetry(): Flow<List<NeuralTelemetryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(telemetry: NeuralTelemetryEntity): Long
}
