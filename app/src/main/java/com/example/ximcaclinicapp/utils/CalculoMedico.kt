package com.example.ximcaclinicapp.utils

import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

// Clase de utilidades médicas. La declaro como object para poder usarla
// sin crear instancias: CalculosMedico.calcularIMC(...)
object CalculosMedico {

    // Fórmula del IMC: peso dividido entre estatura al cuadrado
    // Uso Locale.US para que el decimal use punto y no coma
    fun calcularIMC(peso: Double, estatura: Double): Double {
        if (estatura <= 0.0) return 0.0
        val imc = peso / estatura.pow(2)
        return String.format(Locale.US, "%.2f", imc).toDouble()
    }

    // Devuelve solo la categoría (ej: "Sobrepeso"), usada en las tarjetas de la lista
    fun obtenerNivelPeso(imc: Double): String =
        obtenerClasificacionCirugiaPlastica(imc).categoria

    // Calcula la edad a partir de la fecha en formato dd/MM/yyyy
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
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--

            "$edad años"
        } catch (e: Exception) {
            "-- años"
        }
    }

    // Clasificación detallada del IMC orientada a cirugía plástica.
    // Incluye categoría, grado y una nota clínica relevante para el médico.
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

// Modelo que agrupa los tres datos de la clasificación
data class ClasificacionIMC(
    val categoria: String,
    val grado: String,
    val relevanciaQuirurgica: String
)
