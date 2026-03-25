package com.example.ximcaclinicapp.utils

import java.util.*
import kotlin.math.pow

// object significa que esta clase es un Singleton: solo existe una instancia.
// La uso para agrupar funciones de cálculo médico que no necesitan guardar estado.
// Para usarla escribo: CalculosMedico.calcularIMC(...)
object CalculosMedico {

    // Fórmula del IMC: peso (kg) dividido entre estatura (m) al cuadrado.
    // Ejemplo: 65 kg / (1.70)² = 65 / 2.89 = 22.49
    // Si la estatura es 0 o negativa, devuelvo 0 para evitar dividir entre cero.
    // String.format con Locale.US asegura que el decimal use punto (22.49) y no coma (22,49),
    // porque si uso coma, toDouble() falla al convertir de vuelta.
    fun calcularIMC(peso: Double, estatura: Double): Double {
        if (estatura <= 0.0) return 0.0
        val imc = peso / estatura.pow(2)
        return String.format(Locale.US, "%.2f", imc).toDouble()
    }

    // Con el IMC ya calculado, digo en qué rango está el paciente.
    // Estos rangos son los estándar de la Organización Mundial de la Salud (OMS).
    fun obtenerNivelPeso(imc: Double): String {
        return when {
            imc < 18.5            -> "Bajo peso"
            imc in 18.5..24.9     -> "Normal"
            imc in 25.0..29.9     -> "Sobrepeso"
            else                  -> "Obesidad"
        }
    }
}
