package com.example.ximcaclinicapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Aquí le digo a Room qué tablas existen en mi base de datos.
// entities = las clases que son tablas (Paciente y Usuario).
// version = si cambio la estructura de las tablas (agrego columnas, etc.),
//           tengo que subir este número y escribir una "migración".
// exportSchema = false significa que no genero un archivo JSON con el esquema
//                (no lo necesito para este proyecto académico).
@Database(entities = [Paciente::class, Usuario::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Room genera automáticamente la implementación de estos DAOs.
    // Solo tengo que declararlos aquí como funciones abstractas.
    abstract fun pacienteDao(): PacienteDao
    abstract fun usuarioDao(): UsuarioDao

    // companion object es como el "static" de Java.
    // Aquí implemento el patrón Singleton: que solo exista UNA instancia
    // de la base de datos en toda la app. Crear múltiples instancias
    // puede causar errores y desperdicia memoria.
    companion object {

        // @Volatile asegura que todos los hilos (threads) vean el mismo valor de INSTANCE.
        // Sin esto, dos hilos podrían crear dos bases de datos al mismo tiempo.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si ya existe una instancia, la devuelvo directamente (sin crear otra).
            // Si no existe, entro al bloque synchronized para crearla.
            // synchronized(this) evita que dos hilos creen la instancia al mismo tiempo.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Uso applicationContext para evitar memory leaks
                    AppDatabase::class.java,
                    "ximca_database"            // Este es el nombre del archivo .db en el dispositivo
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
