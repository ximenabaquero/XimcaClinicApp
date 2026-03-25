package com.example.ximcaclinicapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.Usuario
import com.example.ximcaclinicapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(this)
        val usuarioDao = database.usuarioDao()

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val email = binding.etEmailReg.text.toString().trim()
            val password = binding.etPasswordReg.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val nuevoUsuario = Usuario(nombre = nombre, email = email, password = password)
                // Usamos el nombre correcto de la función definido en UsuarioDao
                usuarioDao.registrarUsuario(nuevoUsuario)
                Toast.makeText(this@RegisterActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                finish() // Regresa al Login
            }
        }

        binding.tvVolverLogin.setOnClickListener {
            finish()
        }
    }
}