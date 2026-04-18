package com.example.ximcaclinicapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Usuario
import com.example.ximcaclinicapp.data.UsuarioDao
import com.example.ximcaclinicapp.databinding.ActivityRegisterBinding
import com.example.ximcaclinicapp.utils.PasswordUtils
import kotlinx.coroutines.launch

// Pantalla de registro. Solo se llega aquí desde el link en LoginActivity.
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = AppDatabase.getDatabase(this).usuarioDao()

        binding.btnRegistrar.setOnClickListener {
            val nombre   = binding.etNombre.text.toString().trim()
            val email    = binding.etEmailReg.text.toString().trim()
            val password = binding.etPasswordReg.text.toString().trim()

            // Validaciones: los tres campos son obligatorios
            if (nombre.isEmpty()) {
                binding.tilNombre.error = "El nombre es obligatorio"
                return@setOnClickListener
            } else binding.tilNombre.error = null

            if (email.isEmpty() || !email.contains("@")) {
                binding.tilEmailReg.error = "Ingresa un correo electrónico válido"
                return@setOnClickListener
            } else binding.tilEmailReg.error = null

            // La contraseña debe tener mínimo 6 caracteres
            if (password.length < 6) {
                binding.tilPasswordReg.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            } else binding.tilPasswordReg.error = null

            binding.btnRegistrar.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                // Guardo la contraseña hasheada con SHA-256, no en texto plano
                val hashedPassword = PasswordUtils.hashPassword(password)
                val nuevoUsuario = Usuario(
                    nombre   = nombre,
                    email    = email,
                    password = hashedPassword
                    // rol queda como "MÉDICO" por defecto
                )
                usuarioDao.registrarUsuario(nuevoUsuario)

                runOnUiThread {
                    binding.btnRegistrar.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "Cuenta creada. Inicia sesión.", Toast.LENGTH_SHORT).show()
                    finish() // Regresa automáticamente al Login
                }
            }
        }

        binding.tvVolverLogin.setOnClickListener { finish() }
    }
}
