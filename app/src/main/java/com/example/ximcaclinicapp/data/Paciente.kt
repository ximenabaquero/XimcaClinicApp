<<<<<<< HEAD
@Entity(tableName = "pacientes")
data class Paciente(
    primaryKey(autoGenerate = true) val id: Int = 0,
=======
package com.example.ximcaclinicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacientes")
data class Paciente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
>>>>>>> 288dd03 (DFW)
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: String,
    val peso: Double, //kg
    val estatura: Double,
    val imc: Double,
    val antecedentes: String = "",
    val estado: String = "EN_ESPERA"
)
