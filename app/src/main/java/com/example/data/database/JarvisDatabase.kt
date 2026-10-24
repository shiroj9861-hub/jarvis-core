package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AutonomousModuleDao
import com.example.data.dao.CodeArtifactDao
import com.example.data.dao.NeuralLogDao
import com.example.data.dao.SelfRepairDao
import com.example.data.dao.TelemetryDao
import com.example.data.model.AutonomousModuleEntity
import com.example.data.model.CodeArtifactEntity
import com.example.data.model.NeuralLogEntity
import com.example.data.model.NeuralTelemetryEntity
import com.example.data.model.SelfRepairEntity

@Database(
    entities = [
        NeuralLogEntity::class,
        SelfRepairEntity::class,
        AutonomousModuleEntity::class,
        CodeArtifactEntity::class,
        NeuralTelemetryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun neuralLogDao(): NeuralLogDao
    abstract fun selfRepairDao(): SelfRepairDao
    abstract fun autonomousModuleDao(): AutonomousModuleDao
    abstract fun codeArtifactDao(): CodeArtifactDao
    abstract fun telemetryDao(): TelemetryDao

    companion object {
        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getInstance(context: Context): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    "jarvis_core_telemetry.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
