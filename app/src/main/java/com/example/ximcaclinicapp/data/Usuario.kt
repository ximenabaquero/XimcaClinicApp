package com.example.ximcaclinicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta es mi tabla de usuarios (los médicos que se registran en la app).
// Funciona igual que Paciente: @Entity = tabla en SQLite.
@Entity(tableName = "usuarios")
data class Usuario(

    // ID único autogenerado, igual que en Paciente
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,    // Nombre completo del médico
    val email: String,     // Correo que usa para iniciar sesión (debe ser único)
    val password: String,  // Contraseña (mínimo 6 caracteres, validado en el formulario)

    // El rol por defecto es MÉDICO. Si después quiero agregar ADMIN u otros, puedo.
    val rol: String = "MÉDICO"
)
