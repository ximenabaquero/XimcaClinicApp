package com.example.ximcaclinicapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Le digo a Room cuáles son mis tablas y en qué versión está la base de datos.
// Cada vez que agrego o cambio algo en las tablas, subo el número de versión
// y creo una migración para no perder los datos del usuario.
@Database(
    entities = [Paciente::class, Usuario::class, Nota::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pacienteDao(): PacienteDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun notaDao(): NotaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Versión 2: agregué la tabla de notas para poder guardar notas por paciente
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `notas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `pacienteId` INTEGER NOT NULL,
                        `texto`  TEXT NOT NULL,
                        `fechaCreacion` TEXT NOT NULL
                    )"""
                )
            }
        }

        // Versión 3: agregué el campo teléfono a la tabla de pacientes.
        // Uso ALTER TABLE para no borrar los pacientes que ya estaban guardados.
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `pacientes` ADD COLUMN `telefono` TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        // Esta función devuelve siempre la misma instancia de la base de datos.
        // Si no existe todavía, la crea. Así evito abrir múltiples conexiones.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ximca_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
