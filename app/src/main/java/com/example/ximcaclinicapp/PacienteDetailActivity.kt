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

// Pantalla que muestra todos los datos de un paciente.
// Llego aquí desde la lista cuando toco una tarjeta.
class PacienteDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteDetailBinding
    private val viewModel: PacienteViewModel by viewModels()

    private lateinit var paciente: Paciente

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Reconstruyo el objeto Paciente con los datos que me pasó PacienteListActivity
        paciente = Paciente(
            id              = intent.getIntExtra("id", 0),
            nombre          = intent.getStringExtra("nombre")          ?: "",
            apellido        = intent.getStringExtra("apellido")        ?: "",
            fechaNacimiento = intent.getStringExtra("fechaNacimiento") ?: "",
            telefono        = intent.getStringExtra("telefono")        ?: "",
            peso            = intent.getDoubleExtra("peso", 0.0),
            estatura        = intent.getDoubleExtra("estatura", 0.0),
            imc             = intent.getDoubleExtra("imc", 0.0),
            antecedentes    = intent.getStringExtra("antecedentes")    ?: "",
            estado          = intent.getStringExtra("estado")          ?: "EN_ESPERA"
        )

        mostrarDatos()

        // Botón VER NOTAS
        binding.btnNotas.setOnClickListener {
            val intent = Intent(this, NotasActivity::class.java).apply {
                putExtra("pacienteId",     paciente.id)
                putExtra("pacienteNombre", "${paciente.nombre} ${paciente.apellido}")
            }
            startActivity(intent)
        }

        // Botón EDITAR: abro el formulario pasándole los datos actuales del paciente
        binding.btnEditar.setOnClickListener {
            val intent = Intent(this, PacienteFormActivity::class.java).apply {
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
            finish()
        }

        // Botón ELIMINAR: pido confirmación antes de borrar
        binding.btnEliminarDetalle.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar paciente")
                .setMessage("¿Seguro que deseas eliminar a ${paciente.nombre} ${paciente.apellido}?")
                .setPositiveButton("Eliminar") { _, _ ->
                    viewModel.delete(paciente)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalle del paciente"
    }

    private fun mostrarDatos() {
        binding.tvNombreCompleto.text = "${paciente.nombre} ${paciente.apellido}"
        binding.tvEstado.text         = paciente.estado

        // Cambio el color del badge según el estado del paciente
        val (bgRes, textColorRes) = when (paciente.estado) {
            "EN_CONSULTA" -> Pair(R.drawable.bg_badge_en_consulta, R.color.status_en_consulta_text)
            "ALTA"        -> Pair(R.drawable.bg_badge_alta,        R.color.status_alta_text)
            else          -> Pair(R.drawable.bg_badge_en_espera,   R.color.status_en_espera_text)
        }
        binding.tvEstado.setBackgroundResource(bgRes)
        binding.tvEstado.setTextColor(getColor(textColorRes))

        binding.tvTelefono.text       = paciente.telefono.ifEmpty { "No registrado" }
        binding.tvFechaNacimiento.text = paciente.fechaNacimiento
        binding.tvEdad.text           = CalculosMedico.calcularEdad(paciente.fechaNacimiento)
        binding.tvPeso.text           = "${paciente.peso} kg"
        binding.tvEstatura.text       = "${paciente.estatura} m"

        // Muestro la clasificación completa del IMC (categoría + grado quirúrgico)
        val clasificacion = CalculosMedico.obtenerClasificacionCirugiaPlastica(paciente.imc)
        binding.tvImc.text           = "${paciente.imc}  —  ${clasificacion.categoria} · ${clasificacion.grado}"
        binding.tvNotaQuirurgica.text = clasificacion.relevanciaQuirurgica

        binding.tvAntecedentes.text = paciente.antecedentes.ifEmpty { "Ninguno" }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
