package com.example.ximcaclinicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta clase representa la tabla "pacientes" en la base de datos.
// Cada propiedad es una columna de esa tabla.
@Entity(tableName = "pacientes")
data class Paciente(

    // El id lo genera Room automáticamente, no lo ingresa el usuario.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String, // formato dd/MM/yyyy
    val peso: Double,            // en kg
    val estatura: Double,        // en metros
    val imc: Double,             // se calcula automáticamente en el formulario

    // Estos dos tienen valor por defecto para que no sean obligatorios
    val antecedentes: String = "",
    val estado: String = "EN_ESPERA",   // EN_ESPERA | EN_CONSULTA | ALTA
    val telefono: String = ""
)
