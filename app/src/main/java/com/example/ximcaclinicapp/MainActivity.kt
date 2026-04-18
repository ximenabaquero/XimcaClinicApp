package com.example.ximcaclinicapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ximcaclinicapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PacienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)

        if (!prefs.contains("userId")) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val userName  = prefs.getString("userName",  "Doctor") ?: "Doctor"
        val userEmail = prefs.getString("userEmail", "")       ?: ""
        val userRol   = prefs.getString("userRol",   "MÉDICO") ?: "MÉDICO"

        binding.tvNombreUsuario.text = "Dr. $userName"
        binding.tvEmailUsuario.text  = userEmail
        binding.tvRolUsuario.text    = userRol

        // Actualiza las estadísticas en tiempo real con todos los pacientes
        viewModel.allPacientes.observe(this) { lista ->
            binding.tvStatTotal.text    = lista.size.toString()
            binding.tvStatEspera.text   = lista.count { it.estado == "EN_ESPERA"   }.toString()
            binding.tvStatConsulta.text = lista.count { it.estado == "EN_CONSULTA" }.toString()
            binding.tvStatAlta.text     = lista.count { it.estado == "ALTA"        }.toString()
        }

        binding.btnVerPacientes.setOnClickListener {
            startActivity(Intent(this, PacienteListActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
