package com.example.ximcaclinicapp.utils

class PacienteValidator {
    fun validateFields(
        nombre: String,
        apellido: String,
        fecha: String,
        pesoStr: String,
        estaturaStr: String
    ): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (nombre.isBlank()) errors["nombre"] = "El nombre es obligatorio"
        if (apellido.isBlank()) errors["apellido"] = "El apellido es obligatorio"
        if (fecha.isBlank()) errors["fechaNacimiento"] = "La fecha es obligatoria"
        
        val peso = pesoStr.toDoubleOrNull()
        if (peso == null || peso <= 0) errors["peso"] = "Peso inválido"
        
        val estatura = estaturaStr.toDoubleOrNull()
        if (estatura == null || estatura <= 0) errors["estatura"] = "Estatura inválida"

        return errors
    }
}
