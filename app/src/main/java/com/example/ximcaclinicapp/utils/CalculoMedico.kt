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

    // Versión resumida usada en las tarjetas del listado y el formulario (texto corto).
    // Llama internamente a la clasificación completa para no duplicar la lógica de rangos.
    fun obtenerNivelPeso(imc: Double): String =
        obtenerClasificacionCirugiaPlastica(imc).categoria

    /**
     * Calcula la edad en años a partir de una fecha en formato "dd/MM/yyyy".
     * Devuelve "-- años" si la fecha no es válida.
     */
    fun calcularEdad(fechaNacimiento: String): String {
        return try {
            val partes = fechaNacimiento.split("/")
            if (partes.size != 3) return "-- años"
            val dia  = partes[0].trim().toInt()
            val mes  = partes[1].trim().toInt()
            val anio = partes[2].trim().toInt()

            val hoy        = Calendar.getInstance()
            val nacimiento = Calendar.getInstance().apply { set(anio, mes - 1, dia) }

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            // Ajuste si aún no ha llegado el cumpleaños este año
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--

            "$edad años"
        } catch (e: Exception) {
            "-- años"
        }
    }

    /**
     * Clasificación de IMC para Consultorio de Cirugía Plástica.
     * Basado en criterios OMS y clasificación quirúrgica estándar (ASA/Clasificación de Obesidad).
     */
    fun obtenerClasificacionCirugiaPlastica(imc: Double): ClasificacionIMC {
        return when {
            imc < 18.5 -> ClasificacionIMC(
                categoria = "Bajo Peso",
                grado = if (imc < 17.0) "Severo / Moderado" else "Leve",
                relevanciaQuirurgica = "Riesgo de mala cicatrización y déficit nutricional. Posponer procedimiento electivo hasta recuperación ponderal."
            )
            imc in 18.5..24.9 -> ClasificacionIMC(
                categoria = "Peso Normal",
                grado = "Peso Ideal Quirúrgico",
                relevanciaQuirurgica = "Candidato óptimo. Menor tasa de complicaciones. Tejidos con trofismo adecuado."
            )
            imc in 25.0..29.9 -> ClasificacionIMC(
                categoria = "Sobrepeso",
                grado = "Pre-obesidad",
                relevanciaQuirurgica = "Aceptable para procedimientos estéticos. Lipoescultura y abdominoplastia viables con buenos resultados."
            )
            imc in 30.0..34.9 -> ClasificacionIMC(
                categoria = "Obesidad",
                grado = "Grado I (Leve)",
                relevanciaQuirurgica = "Riesgo anestésico leve. Resultado estético limitado. Requiere consentimiento informado específico sobre limitaciones."
            )
            imc in 35.0..39.9 -> ClasificacionIMC(
                categoria = "Obesidad",
                grado = "Grado II (Moderada)",
                relevanciaQuirurgica = "Alto riesgo de necrosis grasa, dehiscencia de sutura y TVP. Considerar pérdida de peso preoperatoria obligatoria."
            )
            else -> ClasificacionIMC(
                categoria = "Obesidad Mórbida",
                grado = "Grado III (Severa)",
                relevanciaQuirurgica = "Contraindicación relativa para cirugía estética ambulatoria. Requiere manejo en ámbito hospitalario y valoración por cirugía bariátrica previa."
            )
        }
    }
}

/**
 * Modelo de datos específico para historia clínica de Cirugía Plástica.
 */
data class ClasificacionIMC(
    val categoria: String,             // Ej: "Normopeso"
    val grado: String,                 // Ej: "Grado I (Leve)"
    val relevanciaQuirurgica: String   // Nota clínica para el cirujano o consentimiento informado
)
