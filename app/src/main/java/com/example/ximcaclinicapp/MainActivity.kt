package com.example.ximcaclinicapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ximcaclinicapp.databinding.ActivityMainBinding

// MainActivity es el "dashboard" o pantalla principal que aparece después del login.
// Muestra el nombre del médico, su email y su rol, y tiene el botón para ir a los pacientes.
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Leo la sesión guardada cuando el usuario inició sesión en LoginActivity
        val prefs = getSharedPreferences("session", MODE_PRIVATE)

        // Verificación de seguridad: si alguien llega aquí sin sesión activa,
        // lo mando de vuelta al Login. Esto protege el dashboard de accesos no autorizados.
        if (!prefs.contains("userId")) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Recupero los datos del usuario que guardé en SharedPreferences al momento del login.
        // El segundo parámetro de getString es el valor por defecto si la clave no existe.
        val userName = prefs.getString("userName", "Doctor") ?: "Doctor"
        val userEmail = prefs.getString("userEmail", "") ?: ""
        val userRol = prefs.getString("userRol", "MÉDICO") ?: "MÉDICO"

        // Muestro los datos en las vistas del XML
        binding.tvNombreUsuario.text = "Dr. $userName"
        binding.tvEmailUsuario.text = userEmail
        binding.tvRolUsuario.text = userRol

        // Este botón lleva a la lista de pacientes (la pantalla con el RecyclerView)
        binding.btnVerPacientes.setOnClickListener {
            startActivity(Intent(this, PacienteListActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Cerrar sesión: limpio SharedPreferences y vuelvo al Login.
        // finish() cierra el MainActivity para que el botón "atrás" no regrese aquí.
        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply() // Borro TODOS los datos de sesión
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
