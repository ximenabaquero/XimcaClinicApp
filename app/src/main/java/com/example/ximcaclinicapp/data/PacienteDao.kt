package com.example.ximcaclinicapp.data

import kotlinx.coroutines.flow.Flow
import androidx.room.*
@Dao
interface PacienteDao {
    // Traer todos los pacientes ordenados alfabéticamente
    // Usamos Flow para que la UI se actualice sola (reactividad)
    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun getAllPacientes(): Flow<List<Paciente>>

    // Insertar un nuevo paciente (Create)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: Paciente)

    // Actualizar datos de un paciente existente (Update)
    @Update
    suspend fun updatePaciente(paciente: Paciente)

    // Eliminar un paciente (Delete)
    @Delete
    suspend fun deletePaciente(paciente: Paciente)
}
