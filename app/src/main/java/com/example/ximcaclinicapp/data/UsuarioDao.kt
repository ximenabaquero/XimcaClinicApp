package com.example.ximcaclinicapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO para la tabla de usuarios. Solo necesito dos operaciones:
// registrar un usuario nuevo y verificar credenciales al iniciar sesión.
@Dao
interface UsuarioDao {

    // Guarda un usuario nuevo en la base de datos.
    // Se usa cuando el médico llena el formulario de registro.
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    // Esta es la función del login. Le paso el email y la contraseña,
    // y Room busca si existe algún usuario que tenga EXACTAMENTE ese email Y esa contraseña.
    // Si los encuentra, devuelve ese usuario. Si no, devuelve null.
    // En el LoginActivity uso eso: si user != null → acceso concedido, si es null → error.
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?
}
