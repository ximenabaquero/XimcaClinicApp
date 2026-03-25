package com.example.ximcaclinicapp.data

import kotlinx.coroutines.flow.Flow

// El Repository (repositorio) es el intermediario entre el ViewModel y el DAO.
// ¿Por qué existe si podría hablar directo con el DAO?
// Porque si en el futuro quiero agregar una API externa (datos de la nube),
// solo cambio el Repository sin tocar el ViewModel ni las pantallas.
// Separa responsabilidades: el ViewModel no sabe SI los datos vienen de
// la base de datos, de internet, o de donde sea. Solo pide datos al Repository.
class PacienteRepository(private val pacienteDao: PacienteDao) {

    // Expongo el Flow del DAO directamente. El ViewModel lo va a observar
    // y la pantalla se actualizará sola cuando cambien los datos.
    val allPacientes: Flow<List<Paciente>> = pacienteDao.getAllPacientes()

    // Estas funciones son simplemente "puentes" hacia el DAO.
    // El ViewModel las llama y el Repository las delega al DAO.
    suspend fun insert(paciente: Paciente) = pacienteDao.insertPaciente(paciente)
    suspend fun update(paciente: Paciente) = pacienteDao.updatePaciente(paciente)
    suspend fun delete(paciente: Paciente) = pacienteDao.deletePaciente(paciente)
    suspend fun getById(id: Int): Paciente? = pacienteDao.getPacienteById(id)
}
