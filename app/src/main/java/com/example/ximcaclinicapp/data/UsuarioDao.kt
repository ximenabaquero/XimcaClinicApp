package com.example.ximcaclinicapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    // El corazón del Login: busca si existe el usuario con ese correo y clave
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?
}