package com.example.ximcaclinicapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotaDao {

    @Query("SELECT * FROM notas WHERE pacienteId = :pacienteId ORDER BY id DESC")
    fun getNotasByPaciente(pacienteId: Int): LiveData<List<Nota>>

    @Insert
    suspend fun insert(nota: Nota)

    @Delete
    suspend fun delete(nota: Nota)
}
