package com.example.ximcaclinicapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Paciente::class, Usuario::class, Nota::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pacienteDao(): PacienteDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun notaDao(): NotaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración 1→2: crea la tabla de notas sin tocar datos existentes
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `notas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `pacienteId` INTEGER NOT NULL,
                        `texto` TEXT NOT NULL,
                        `fechaCreacion` TEXT NOT NULL
                    )"""
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ximca_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
