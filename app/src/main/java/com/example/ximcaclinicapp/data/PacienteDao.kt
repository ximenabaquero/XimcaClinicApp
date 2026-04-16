package com.example.ximcaclinicapp.data

import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface PacienteDao {

    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun getAllPacientes(): Flow<List<Paciente>>

    @Query("SELECT * FROM pacientes WHERE id = :id LIMIT 1")
    suspend fun getPacienteById(id: Int): Paciente?

    // Cambiamos a Long para que devuelva el ID y evite el error 'jvm signature V'
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: Paciente): Long

    // Cambiamos a Int para que devuelva filas afectadas
    @Update
    suspend fun updatePaciente(paciente: Paciente): Int

    // Cambiamos a Int para que devuelva filas afectadas
    @Delete
    suspend fun deletePaciente(paciente: Paciente): Int
}
