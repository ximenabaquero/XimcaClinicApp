package com.example.ximcaclinicapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.data.PacienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// El ViewModel es el "cerebro" de la pantalla. Guarda y gestiona los datos
// aunque la pantalla se gire, se pause o se recree.
// Sin ViewModel, cada vez que giro el teléfono perdería la lista de pacientes.
//
// Usamos ViewModel (no AndroidViewModel) porque Hilt inyecta el Repository
// directamente: no necesitamos el contexto de Application aquí.
@HiltViewModel
class PacienteViewModel @Inject constructor(
    private val repository: PacienteRepository
) : ViewModel() {

    // LiveData es una lista "observable": cuando cambia, la pantalla se actualiza sola.
    // asLiveData() convierte el Flow del Repository en LiveData para que el Activity
    // pueda observarlo con el método .observe().
    val allPacientes = repository.allPacientes.asLiveData()

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
