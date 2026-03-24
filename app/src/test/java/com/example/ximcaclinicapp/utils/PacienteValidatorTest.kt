package com.example.ximcaclinicapp.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PacienteValidatorTest {

    private val validator = PacienteValidator()

    @Test
    fun `validateFields returns empty map for valid inputs`() {
        val errors = validator.validateFields(
            nombre = "Juan",
            apellido = "Pérez",
            fechaNacimiento = "1990-01-01",
            pesoStr = "70.5",
            estaturaStr = "1.75"
        )
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `validateFields detects empty nombre`() {
        val errors = validator.validateFields(
            nombre = "",
            apellido = "Pérez",
            fechaNacimiento = "1990-01-01",
            pesoStr = "70.5",
            estaturaStr = "1.75"
        )
        assertEquals(1, errors.size)
        assertEquals("El nombre es obligatorio", errors["nombre"])
    }

    @Test
    fun `validateFields detects invalid peso`() {
        val errors = validator.validateFields(
            nombre = "Juan",
            apellido = "Pérez",
            fechaNacimiento = "1990-01-01",
            pesoStr = "abc",
            estaturaStr = "1.75"
        )
        assertEquals(1, errors.size)
        assertEquals("Ingresa un peso válido en kg", errors["peso"])
    }

    @Test
    fun `validateFields detects multiple errors`() {
        val errors = validator.validateFields(
            nombre = "",
            apellido = "",
            fechaNacimiento = "",
            pesoStr = "-5",
            estaturaStr = "0"
        )
        assertEquals(5, errors.size)
    }
}