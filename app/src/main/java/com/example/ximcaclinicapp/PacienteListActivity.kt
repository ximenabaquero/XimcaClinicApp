package com.example.ximcaclinicapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximcaclinicapp.databinding.ActivityPacienteListBinding
import com.example.ximcaclinicapp.ui.PacienteAdapter
import com.example.ximcaclinicapp.viewmodel.PacienteViewModel

class PacienteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteListBinding
    private val viewModel: PacienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PacienteAdapter(
            onItemClick = { paciente ->
                val intent = Intent(this, PacienteDetailActivity::class.java).apply {
                    putExtra("id", paciente.id)
                    putExtra("nombre", paciente.nombre)
                    putExtra("apellido", paciente.apellido)
                    putExtra("fechaNacimiento", paciente.fechaNacimiento)
                    putExtra("peso", paciente.peso)
                    putExtra("estatura", paciente.estatura)
                    putExtra("imc", paciente.imc)
                    putExtra("antecedentes", paciente.antecedentes)
                    putExtra("estado", paciente.estado)
                }
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            },
            onDeleteClick = { paciente ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar paciente")
                    .setMessage("¿Seguro que deseas eliminar a ${paciente.nombre} ${paciente.apellido}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.delete(paciente)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        binding.recyclerViewPacientes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPacientes.adapter = adapter

        viewModel.allPacientes.observe(this) { lista ->
            adapter.submitList(lista)
            binding.tvListaVacia.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAgregarPaciente.setOnClickListener {
            startActivity(Intent(this, PacienteFormActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        return true
    }
}
