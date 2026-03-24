package com.example.ximcaclinicapp.utils

import java.security.MessageDigest

object PasswordUtils {

    // Función simple para hashear contraseñas (educativo, no para producción)
    // En producción usar bcrypt o similar
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Verificar contraseña comparando hash
    fun verifyPassword(password: String, hashed: String): Boolean {
        return hashPassword(password) == hashed
    }
}