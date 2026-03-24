package com.example.ximcaclinicapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ximcaclinicapp.data.UsuarioDao
import com.example.ximcaclinicapp.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// LoginActivity es la primera pantalla que ve el usuario.
// Aquí verifico si ya hay sesión guardada, y si la hay, salto directo al dashboard.
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    // ViewBinding: en lugar de buscar vistas con findViewById (que puede crashear
    // si me equivoco el ID), binding me da acceso directo y seguro a todas las vistas del XML.
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SharedPreferences es como un pequeño archivo de configuración donde guardo
        // datos simples (nombre, email, ID del usuario logueado).
        // "session" es el nombre del archivo. MODE_PRIVATE = solo mi app puede leerlo.
        val prefs = getSharedPreferences("session", MODE_PRIVATE)

        // Si ya hay un userId guardado, significa que el usuario ya inició sesión antes
        // y no cerró sesión. Lo mando directo al dashboard sin que tenga que loguearse de nuevo.
        if (prefs.contains("userId")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // finish() cierra esta pantalla para que no vuelva atrás con el botón back
            return   // Salgo del onCreate, no ejecuto nada más
        }

        // Si llegué aquí es porque NO hay sesión activa. Muestro el formulario de login.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()    // .trim() quita espacios al inicio y al final
            val password = binding.etPassword.text.toString().trim()

            // --- VALIDACIONES ---
            // Valido antes de consultar la base de datos para dar feedback inmediato al usuario.
            if (email.isEmpty()) {
                binding.tilEmail.error = "Ingresa tu correo" // Muestra el error debajo del campo
                return@setOnClickListener // Detengo la ejecución aquí
            } else binding.tilEmail.error = null // Limpio el error si ya es válido

            if (!email.contains("@")) {
                binding.tilEmail.error = "Ingresa un correo válido"
                return@setOnClickListener
            } else binding.tilEmail.error = null

            if (password.isEmpty()) {
                binding.tilPassword.error = "Ingresa tu contraseña"
                return@setOnClickListener
            } else binding.tilPassword.error = null

            // Mostrar indicador de carga
            binding.btnLogin.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            // lifecycleScope.launch crea una "corrutina": código que corre en segundo plano
            // sin bloquear la pantalla. Room NO permite consultas en el hilo principal (UI thread).
            lifecycleScope.launch {
                // Le pregunto a la base de datos si existe ese email+contraseña.
                // Devuelve el Usuario si existe, o null si no.
                val user = usuarioDao.login(email, password)

                // Ocultar indicador de carga
                runOnUiThread {
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = View.GONE
                }

                if (user != null) {
                    // ¡Login exitoso! Guardo los datos del usuario en SharedPreferences.
                    // Así la próxima vez que abra la app, no tiene que volver a loguearse.
                    prefs.edit()
                        .putInt("userId", user.id)
                        .putString("userName", user.nombre)
                        .putString("userEmail", user.email)
                        .putString("userRol", user.rol)
                        .apply() // .apply() guarda de forma asíncrona (no bloquea)

                    Toast.makeText(
                        this@LoginActivity,
                        "Bienvenido, ${user.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Voy al dashboard principal
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                } else {
                    // Credenciales incorrectas. No digo si falló el email o la contraseña
                    // por seguridad (no quiero darle pistas a alguien malintencionado).
                    Toast.makeText(
                        this@LoginActivity,
                        "Correo o contraseña incorrectos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Si no tiene cuenta, la mando a registrarse
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}
