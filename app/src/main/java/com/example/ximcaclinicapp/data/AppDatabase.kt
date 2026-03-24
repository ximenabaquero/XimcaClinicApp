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
}
