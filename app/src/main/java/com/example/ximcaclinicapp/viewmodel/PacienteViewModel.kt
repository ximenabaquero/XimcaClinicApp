package com.example.ximcaclinicapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.data.PacienteRepository
import kotlinx.coroutines.launch

class PacienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PacienteRepository

    init {
        val dao = AppDatabase.getDatabase(application).pacienteDao()
        repository = PacienteRepository(dao)
    }

    val allPacientes = repository.allPacientes.asLiveData()

    fun insert(paciente: Paciente) = viewModelScope.launch { repository.insert(paciente) }
    fun update(paciente: Paciente) = viewModelScope.launch { repository.update(paciente) }
    fun delete(paciente: Paciente) = viewModelScope.launch { repository.delete(paciente) }

    suspend fun getById(id: Int): Paciente? = repository.getById(id)
}
