package com.example.ximcaclinicapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.databinding.ActivityPacienteDetailBinding
import com.example.ximcaclinicapp.utils.CalculosMedico
import com.example.ximcaclinicapp.viewmodel.PacienteViewModel
import com.example.ximcaclinicapp.R

// Esta pantalla muestra TODOS los datos de un paciente específico.
// Llego aquí desde PacienteListActivity cuando toco una tarjeta.
// Desde aquí puedo editar o eliminar al paciente.
class PacienteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteDetailBinding
    private val viewModel: PacienteViewModel by viewModels()

    // Guardo el paciente que estoy viendo para usarlo en editar/eliminar
    private lateinit var paciente: Paciente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reconstruyo el objeto Paciente con los datos que me mandó PacienteListActivity.
        // intent.getStringExtra("nombre") recupera el valor que se guardó con putExtra("nombre", ...)
        // El ?: "" es el valor por defecto si el extra viene null (por seguridad).
        paciente = Paciente(
            id = intent.getIntExtra("id", 0),
            nombre = intent.getStringExtra("nombre") ?: "",
            apellido = intent.getStringExtra("apellido") ?: "",
            fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: "",
            telefono = intent.getStringExtra("telefono") ?: "",
            peso = intent.getDoubleExtra("peso", 0.0),
            estatura = intent.getDoubleExtra("estatura", 0.0),
            imc = intent.getDoubleExtra("imc", 0.0),
            antecedentes = intent.getStringExtra("antecedentes") ?: "",
            estado = intent.getStringExtra("estado") ?: "EN_ESPERA"
        )

        // Lleno todas las vistas con los datos del paciente
        mostrarDatos()

        // Botón NOTAS: abre la pantalla de notas del paciente
        binding.btnNotas.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java).apply {
                putExtra("pacienteId", paciente.id)
                putExtra("pacienteNombre", "${paciente.nombre} ${paciente.apellido}")
            }
            startActivity(intent)
        }

        // Botón EDITAR: abro el formulario en modo edición, pasándole todos los datos actuales
        binding.btnEditar.setOnClickListener {
            val intent = Intent(this, PacienteFormActivity::class.java).apply {
                putExtra("id", paciente.id)       // El ID le dice al formulario que es modo EDITAR
                putExtra("nombre", paciente.nombre)
                putExtra("apellido", paciente.apellido)
                putExtra("fechaNacimiento", paciente.fechaNacimiento)
                putExtra("peso", paciente.peso)
                putExtra("estatura", paciente.estatura)
                putExtra("imc", paciente.imc)
                putExtra("telefono", paciente.telefono)
                putExtra("antecedentes", paciente.antecedentes)
                putExtra("estado", paciente.estado)
            }
            startActivity(intent)
            finish() // Cierro el detalle para que al volver quede en la lista actualizada
        }

        // Botón ELIMINAR: confirmo antes de borrar
        binding.btnEliminarDetalle.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar paciente")
                .setMessage("¿Seguro que deseas eliminar a ${paciente.nombre} ${paciente.apellido}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.delete(paciente) // Elimino a través del ViewModel
                    finish() // Regreso a la lista (que ya se habrá actualizado sola)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del paciente"
    }

    // Función auxiliar que llena cada TextView con el dato correspondiente del paciente
    private fun mostrarDatos() {
        binding.tvNombreCompleto.text = "${paciente.nombre} ${paciente.apellido}"
        binding.tvEstado.text = paciente.estado

        // Badge de color según el estado
        val (bgRes, textColorRes) = when (paciente.estado) {
            "EN_CONSULTA" -> Pair(R.drawable.bg_badge_en_consulta, R.color.status_en_consulta_text)
            "ALTA"        -> Pair(R.drawable.bg_badge_alta, R.color.status_alta_text)
            else          -> Pair(R.drawable.bg_badge_en_espera, R.color.status_en_espera_text)
        }
        binding.tvEstado.setBackgroundResource(bgRes)
        binding.tvEstado.setTextColor(getColor(textColorRes))

        binding.tvTelefono.text = paciente.telefono.ifEmpty { "No registrado" }
        binding.tvFechaNacimiento.text = paciente.fechaNacimiento
        binding.tvEdad.text = CalculosMedico.calcularEdad(paciente.fechaNacimiento)
        binding.tvPeso.text = "${paciente.peso} kg"
        binding.tvEstatura.text = "${paciente.estatura} m"

        // Clasificación quirúrgica completa del IMC
        val clasificacion = CalculosMedico.obtenerClasificacionCirugiaPlastica(paciente.imc)
        binding.tvImc.text = "${paciente.imc}  —  ${clasificacion.categoria} · ${clasificacion.grado}"
        binding.tvNotaQuirurgica.text = clasificacion.relevanciaQuirurgica

        // Si no hay antecedentes, muestro "Ninguno" en vez de dejarlo en blanco
        binding.tvAntecedentes.text = paciente.antecedentes.ifEmpty { "Ninguno" }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
