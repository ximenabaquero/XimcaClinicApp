package com.example.ximcaclinicapp.utils

import java.util.Calendar

class PacienteValidator {

    fun validateFields(
        nombre: String,
        apellido: String,
        fecha: String,
        pesoStr: String,
        estaturaStr: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Nombre y apellido: no vacíos, solo letras y espacios
        if (nombre.isBlank()) {
            errors["nombre"] = "El nombre es obligatorio"
        } else if (nombre.length < 2) {
            errors["nombre"] = "El nombre debe tener al menos 2 caracteres"
        }

        if (apellido.isBlank()) {
            errors["apellido"] = "El apellido es obligatorio"
        } else if (apellido.length < 2) {
            errors["apellido"] = "El apellido debe tener al menos 2 caracteres"
        }

        // Fecha: obligatoria y el paciente debe tener entre 0 y 120 años
        if (fecha.isBlank()) {
            errors["fechaNacimiento"] = "La fecha de nacimiento es obligatoria"
        } else {
            val edadError = validarEdad(fecha)
            if (edadError != null) errors["fechaNacimiento"] = edadError
        }

        // Peso: entre 1 kg y 500 kg
        val peso = pesoStr.toDoubleOrNull()
        when {
            peso == null || pesoStr.isBlank() -> errors["peso"] = "Ingresa un peso válido"
            peso < 1.0   -> errors["peso"] = "El peso mínimo es 1 kg"
            peso > 500.0 -> errors["peso"] = "El peso máximo es 500 kg"
        }

        // Estatura: entre 0.3 m y 2.5 m
        val estatura = estaturaStr.toDoubleOrNull()
        when {
            estatura == null || estaturaStr.isBlank() -> errors["estatura"] = "Ingresa una estatura válida"
            estatura < 0.3 -> errors["estatura"] = "La estatura mínima es 0.3 m"
            estatura > 2.5 -> errors["estatura"] = "La estatura máxima es 2.5 m"
        }

        return errors
    }

    // Verifica que la fecha corresponda a una persona de 0 a 120 años
    private fun validarEdad(fecha: String): String? {
        return try {
            val partes = fecha.split("/")
            if (partes.size != 3) return "Formato de fecha inválido"
            val dia  = partes[0].trim().toInt()
            val mes  = partes[1].trim().toInt()
            val anio = partes[2].trim().toInt()

            val hoy        = Calendar.getInstance()
            val nacimiento = Calendar.getInstance().apply { set(anio, mes - 1, dia) }

            // No puede ser una fecha futura
            if (nacimiento.after(hoy)) return "La fecha no puede ser futura"

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--

            if (edad > 120) return "Verifica la fecha de nacimiento (más de 120 años)"

            null // sin error
        } catch (e: Exception) {
            "Formato de fecha inválido"
        }
    }
}
