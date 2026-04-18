package com.example.ximcaclinicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta es mi "tabla" de pacientes en la base de datos local.
// @Entity le dice a Room: "oye, esto es una tabla, créala en SQLite".
// El tableName es el nombre real de la tabla adentro de la base de datos.
@Entity(tableName = "pacientes")
data class Paciente(

    // La @PrimaryKey es el ID único de cada paciente.
    // autoGenerate = true significa que Room le asigna el número solo (1, 2, 3...).
    // El val id = 0 es el valor por defecto para cuando creo un paciente nuevo
    // (Room lo ignora y pone el siguiente número disponible).
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,          // Nombre de pila del paciente
    val apellido: String,        // Apellido del paciente
    val fechaNacimiento: String, // Fecha en formato dd/mm/aaaa (lo guardo como texto)
    val peso: Double,            // Peso en kilogramos, ej: 65.5
    val estatura: Double,        // Estatura en metros, ej: 1.70
    val imc: Double,             // Índice de Masa Corporal, lo calculo con CalculosMedico

    // Estos dos tienen valor por defecto para que no sean obligatorios al crear
    val antecedentes: String = "",       // Enfermedades previas, alergias, etc.
    val estado: String = "EN_ESPERA",   // Estado actual: EN_ESPERA, EN_CONSULTA o ALTA
    val telefono: String = ""           // Teléfono de contacto (opcional)
)
