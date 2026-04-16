package com.example.ximcaclinicapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.UsuarioDao
import com.example.ximcaclinicapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)

        if (prefs.contains("userId")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializamos el DAO desde la base de datos Singleton
        usuarioDao = AppDatabase.getDatabase(this).usuarioDao()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.tilEmail.error = "Ingresa tu correo"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (!email.contains("@")) {
                binding.tilEmail.error = "Ingresa un correo válido"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (password.isEmpty()) {
                binding.tilPassword.error = "Ingresa tu contraseña"
                return@setOnClickListener
            } else binding.tilPassword.error = null

            binding.btnLogin.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                val user = usuarioDao.login(email, password)

                runOnUiThread {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE

                    if (user != null) {
                        prefs.edit()
                            .putInt("userId", user.id)
                            .putString("userName", user.nombre)
                            .putString("userEmail", user.email)
                            .putString("userRol", user.rol)
                            .apply()

                        Toast.makeText(
                            this@LoginActivity,
                            "Bienvenido, ${user.nombre}",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}
