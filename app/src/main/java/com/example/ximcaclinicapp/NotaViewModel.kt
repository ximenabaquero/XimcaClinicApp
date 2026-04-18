package com.example.ximcaclinicapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Nota
import kotlinx.coroutines.launch

class NotaViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).notaDao()

    fun getNotas(pacienteId: Int): LiveData<List<Nota>> =
        dao.getNotasByPaciente(pacienteId)

    fun insertar(nota: Nota) {
        viewModelScope.launch { dao.insert(nota) }
    }

    fun eliminar(nota: Nota) {
        viewModelScope.launch { dao.delete(nota) }
    }
}
