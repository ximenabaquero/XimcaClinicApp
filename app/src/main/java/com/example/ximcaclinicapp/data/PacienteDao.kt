package com.example.ximcaclinicapp.data

import kotlinx.coroutines.flow.Flow
import androidx.room.*

// DAO = Data Access Object. Es básicamente el "menú" de operaciones que puedo
// hacerle a la tabla de pacientes. Aquí defino TODO lo que quiero hacer con esa tabla.
// Room genera el código SQL por mí gracias a las anotaciones (@Query, @Insert, etc.).
@Dao
interface PacienteDao {

    // Trae TODOS los pacientes ordenados por nombre de A a Z.
    // Retorna un Flow<List<Paciente>>: esto es magia de Kotlin.
    // Flow significa que la lista se actualiza SOLA en la pantalla cada vez que
    // algo cambia en la base de datos. Sin eso, tendría que recargar manualmente.
    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    fun getAllPacientes(): Flow<List<Paciente>>

    // Busca UN paciente por su ID. Lo uso cuando abro el detalle o edito.
    // Es suspend porque la consulta a la base de datos no puede bloquear la pantalla
    // (tiene que correr en segundo plano).
    @Query("SELECT * FROM pacientes WHERE id = :id LIMIT 1")
    suspend fun getPacienteById(id: Int): Paciente?

    // Inserta un paciente nuevo. Si por alguna razón ya existe uno con el mismo ID,
    // lo reemplaza (REPLACE). En la práctica eso no pasa porque el ID es autoGenerado.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaciente(paciente: Paciente)

    // Actualiza un paciente existente. Room sabe cuál es porque compara el ID.
    @Update
    suspend fun updatePaciente(paciente: Paciente)

    // Elimina un paciente. Room lo busca por ID y lo borra.
    @Delete
    suspend fun deletePaciente(paciente: Paciente)
}
