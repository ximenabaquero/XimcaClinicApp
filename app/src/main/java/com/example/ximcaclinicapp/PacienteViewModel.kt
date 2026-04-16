package com.example.ximcaclinicapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.data.PacienteRepository
import kotlinx.coroutines.launch

// El ViewModel es el "cerebro" de la pantalla. Guarda y gestiona los datos
// aunque la pantalla se gire, se pause o se recree.
// Sin ViewModel, cada vez que giro el teléfono perdería la lista de pacientes.
//
// Heredo de AndroidViewModel (en vez de ViewModel normal) porque necesito
// el "application" para poder crear la base de datos.
class PacienteViewModel(application: Application) : AndroidViewModel(application) {

    // El Repository es quien habla con la base de datos.
    // El ViewModel no debería saber los detalles de dónde vienen los datos.
    private val repository: PacienteRepository

    // LiveData es una lista "observable": cuando cambia, la pantalla se actualiza sola.
    // asLiveData() convierte el Flow del Repository en LiveData para que el Activity
    // pueda observarlo con el método .observe().
    val allPacientes: androidx.lifecycle.LiveData<List<Paciente>>

    // El bloque init se ejecuta cuando se crea el ViewModel (una sola vez).
    // Aquí inicializo el Repository conectándolo a la base de datos.
    init {
        val dao = AppDatabase.getDatabase(application).pacienteDao()
        repository = PacienteRepository(dao)
        allPacientes = repository.allPacientes.asLiveData()
    }

    // Estas funciones las llaman las Activities para modificar datos.
    // viewModelScope.launch lanza la operación en segundo plano (coroutine)
    // para no bloquear la pantalla mientras Room escribe en la base de datos.
    fun insert(paciente: Paciente) = viewModelScope.launch { repository.insert(paciente) }
    fun update(paciente: Paciente) = viewModelScope.launch { repository.update(paciente) }
    fun delete(paciente: Paciente) = viewModelScope.launch { repository.delete(paciente) }

    // Esta la uso si necesito buscar un paciente específico por ID.
    // Es suspend porque también accede a la base de datos en segundo plano.
    suspend fun getById(id: Int): Paciente? = repository.getById(id)
}
