package com.example.ximcaclinicapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.data.PacienteRepository
import kotlinx.coroutines.launch

// Uso AndroidViewModel en vez de ViewModel normal porque necesito
// el contexto de la app para abrir la base de datos.
// La ventaja del ViewModel es que los datos no se pierden si la pantalla se rota.
class PacienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PacienteRepository

    // allPacientes es la lista de pacientes que se actualiza automáticamente
    // cada vez que hay un cambio en la base de datos.
    val allPacientes: androidx.lifecycle.LiveData<List<Paciente>>

    init {
        val dao = AppDatabase.getDatabase(application).pacienteDao()
        repository = PacienteRepository(dao)
        allPacientes = repository.allPacientes.asLiveData()
    }

    // Estas tres funciones son las operaciones básicas de la base de datos.
    // Las pongo en viewModelScope para que se ejecuten en segundo plano
    // y no congelen la pantalla.
    fun insert(paciente: Paciente) = viewModelScope.launch { repository.insert(paciente) }
    fun update(paciente: Paciente) = viewModelScope.launch { repository.update(paciente) }
    fun delete(paciente: Paciente) = viewModelScope.launch { repository.delete(paciente) }

    suspend fun getById(id: Int): Paciente? = repository.getById(id)
}
