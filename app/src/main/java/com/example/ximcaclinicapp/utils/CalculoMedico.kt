package com.example.ximcaclinicapp.utils

import java.util.*
import kotlin.math.pow

object CalculosMedico {

    // La fórmula física: peso / estatura^2
    fun calcularIMC(peso: Double, estatura: Double): Double {
        if (estatura <= 0.0) return 0.0
        val imc = peso / estatura.pow(2)
        // Lo redondeamos a 2 decimales para que no parezca un número irracional infinito
        return String.format(Locale.US, "%.2f", imc).toDouble()
    }

    // Función extra: Clasificación biológica del IMC
    fun obtenerNivelPeso(imc: Double): String {
        return when {
            imc < 18.5 -> "Bajo peso"
            imc in 18.5..24.9 -> "Normal"
            imc in 25.0..29.9 -> "Sobrepeso"
            else -> "Obesidad"
        }
    }
}