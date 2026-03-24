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
import dagger.hilt.android.AndroidEntryPoint

// Esta es la pantalla principal del CRUD: muestra la lista de todos los pacientes
// en un RecyclerView (lista scrolleable) y tiene el botón + para agregar uno nuevo.
@AndroidEntryPoint
class PacienteListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteListBinding

    // viewModels() crea o recupera el ViewModel asociado a esta Activity.
    // Si giro el teléfono, el ViewModel sobrevive y no pierdo la lista.
    // "by" es delegación de Kotlin: le delego la creación del ViewModel al framework.
    private val viewModel: PacienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Creo el Adapter pasándole las dos acciones que quiero que haga:
        // 1. Al tocar una tarjeta → abro el detalle del paciente
        // 2. Al tocar el botón X → pregunto si realmente quiere eliminar
        val adapter = PacienteAdapter(
            onItemClick = { paciente ->
                // Paso los datos del paciente a PacienteDetailActivity usando extras de Intent.
                // Es como llenar una mochila (Intent) con los datos y dársela a la siguiente pantalla.
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
                // Antes de eliminar, pido confirmación con un diálogo para evitar borrados accidentales
                AlertDialog.Builder(this)
                    .setTitle("Eliminar paciente")
                    .setMessage("¿Seguro que deseas eliminar a ${paciente.nombre} ${paciente.apellido}?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        viewModel.delete(paciente) // Le digo al ViewModel que elimine
                        // La lista se actualiza sola gracias al Flow+LiveData, no necesito hacer nada más
                    }
                    .setNegativeButton("Cancelar", null) // null = no hace nada al cancelar
                    .show()
            }
        )

        // Configuro el RecyclerView: necesita un LayoutManager (cómo acomoda los items)
        // LinearLayoutManager = lista vertical, uno debajo del otro
        binding.recyclerViewPacientes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPacientes.adapter = adapter

        // Observo el LiveData del ViewModel. Cada vez que cambia la lista de pacientes
        // (porque alguien agregó, editó o eliminó uno), este bloque se ejecuta automáticamente.
        viewModel.allPacientes.observe(this) { lista ->
            adapter.submitList(lista) // Le doy la nueva lista al Adapter para que actualice las vistas

            // Si la lista está vacía, muestro el mensaje "no hay pacientes". Si no, lo escondo.
            binding.tvListaVacia.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        // El FAB (Floating Action Button) con el ícono "+" abre el formulario de creación
        binding.fabAgregarPaciente.setOnClickListener {
            startActivity(Intent(this, PacienteFormActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            // No paso ningún extra → PacienteFormActivity sabrá que es modo CREAR
        }

        // Muestro la flecha de "volver" en la barra de título
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Cuando toco la flecha de volver en la barra superior, cierro esta pantalla
    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        return true
    }
}
