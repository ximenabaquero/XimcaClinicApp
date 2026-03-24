package com.example.ximcaclinicapp.utils

/**
 * Clase pura para validar datos de pacientes.
 * No depende de Android, por lo que es fácil de testear con unit tests.
 */
class PacienteValidator {

    /**
     * Valida los campos de un paciente.
     * @return Mapa de errores: clave = campo, valor = mensaje de error. Vacío si válido.
     */
    fun validateFields(
        nombre: String,
        apellido: String,
        fechaNacimiento: String,
        pesoStr: String,
        estaturaStr: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (nombre.trim().isEmpty()) {
            errors["nombre"] = "El nombre es obligatorio"
        }

        if (apellido.trim().isEmpty()) {
            errors["apellido"] = "El apellido es obligatorio"
        }

        if (fechaNacimiento.trim().isEmpty()) {
            errors["fechaNacimiento"] = "La fecha es obligatoria"
        }

        val peso = pesoStr.trim().toDoubleOrNull()
        if (peso == null || peso <= 0) {
            errors["peso"] = "Ingresa un peso válido en kg"
        }

        val estatura = estaturaStr.trim().toDoubleOrNull()
        if (estatura == null || estatura <= 0) {
            errors["estatura"] = "Ingresa una estatura válida en metros (ej: 1.70)"
        }

        return errors
    }
}