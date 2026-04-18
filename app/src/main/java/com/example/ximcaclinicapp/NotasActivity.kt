package com.example.ximcaclinicapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximcaclinicapp.data.Nota
import com.example.ximcaclinicapp.databinding.ActivityNotasBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotasBinding
    private val viewModel: NotaViewModel by viewModels()
    private lateinit var adapter: NotaAdapter

    private var pacienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pacienteId = intent.getIntExtra("pacienteId", -1)
        val nombrePaciente = intent.getStringExtra("pacienteNombre") ?: ""
        binding.tvSubtituloPaciente.text = nombrePaciente

        // Adapter con confirmación antes de eliminar
        adapter = NotaAdapter { nota ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar nota")
                .setMessage("¿Deseas eliminar esta nota?")
                .setPositiveButton("Eliminar") { _, _ -> viewModel.eliminar(nota) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.rvNotas.layoutManager = LinearLayoutManager(this)
        binding.rvNotas.adapter = adapter

        // Observa las notas del paciente en tiempo real
        viewModel.getNotas(pacienteId).observe(this) { notas ->
            adapter.submitList(notas)
            binding.tvVacio.visibility = if (notas.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAgregarNota.setOnClickListener { mostrarDialogoNuevaNota() }
    }

    private fun mostrarDialogoNuevaNota() {
        // Forzamos tema claro para que el diálogo no herede el modo oscuro de Samsung
        val lightContext = ContextThemeWrapper(this, R.style.Theme_XimcaClinicApp)
        val dialogView = LayoutInflater.from(lightContext).inflate(R.layout.dialog_nueva_nota, null)
        val et = dialogView.findViewById<TextInputEditText>(R.id.etNotaDialog)

        MaterialAlertDialogBuilder(lightContext)
            .setTitle("Nueva nota")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val texto = et.text.toString().trim()
                if (texto.isNotEmpty()) {
                    val fecha = SimpleDateFormat(
                        "dd/MM/yyyy · HH:mm", Locale.getDefault()
                    ).format(Date())
                    viewModel.insertar(
                        Nota(pacienteId = pacienteId, texto = texto, fechaCreacion = fecha)
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
