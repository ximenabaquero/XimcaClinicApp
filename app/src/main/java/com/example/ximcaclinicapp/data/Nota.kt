package com.example.ximcaclinicapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pacienteId: Int,        // FK lógica al paciente
    val texto: String,
    val fechaCreacion: String   // "dd/MM/yyyy · HH:mm"
)
