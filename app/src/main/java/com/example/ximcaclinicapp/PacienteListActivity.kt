package com.example.ximcaclinicapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.databinding.ActivityPacienteListBinding

class PacienteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteListBinding
    private val viewModel: PacienteViewModel by viewModels()
    private lateinit var adapter: PacienteAdapter

    // Guardamos la lista completa para poder filtrar sin perder datos
    private var listaCompleta: List<Paciente> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PacienteAdapter(
            onItemClick = { paciente ->
                val intent = Intent(this, PacienteDetailActivity::class.java).apply {
                    putExtra("id",              paciente.id)
                    putExtra("nombre",          paciente.nombre)
                    putExtra("apellido",        paciente.apellido)
                    putExtra("fechaNacimiento", paciente.fechaNacimiento)
                    putExtra("telefono",        paciente.telefono)
                    putExtra("peso",            paciente.peso)
                    putExtra("estatura",        paciente.estatura)
                    putExtra("imc",             paciente.imc)
                    putExtra("antecedentes",    paciente.antecedentes)
                    putExtra("estado",          paciente.estado)
                }
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            },
            onDeleteClick = { paciente ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar paciente")
                    .setMessage("¿Seguro que deseas eliminar a ${paciente.nombre} ${paciente.apellido}?")
                    .setPositiveButton("Eliminar") { _, _ -> viewModel.delete(paciente) }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        setSupportActionBar(binding.toolbar)
        binding.recyclerViewPacientes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPacientes.adapter = adapter

        // Observa la lista completa y actualiza el buscador en tiempo real
        viewModel.allPacientes.observe(this) { lista ->
            listaCompleta = lista
            filtrar(binding.etBuscar.text.toString())
        }

        // Filtrado en tiempo real mientras el usuario escribe
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filtrar(s.toString()) }
        })

        binding.fabAgregarPaciente.setOnClickListener {
            startActivity(Intent(this, PacienteFormActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun filtrar(query: String) {
        val resultado = if (query.isBlank()) {
            listaCompleta
        } else {
            val q = query.trim().lowercase()
            listaCompleta.filter {
                it.nombre.lowercase().contains(q) ||
                it.apellido.lowercase().contains(q)
            }
        }
        adapter.submitList(resultado)
        binding.tvListaVacia.visibility = if (resultado.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        return true
    }
}
