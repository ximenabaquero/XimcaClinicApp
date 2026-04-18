package com.example.ximcaclinicapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.databinding.ActivityPacienteFormBinding
import com.example.ximcaclinicapp.utils.CalculosMedico
import com.example.ximcaclinicapp.utils.PacienteValidator
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.TimeZone

// Este formulario sirve tanto para crear un paciente nuevo como para editar uno existente.
// La diferencia es si recibe un "id" por intent o no.
class PacienteFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteFormBinding
    private val viewModel: PacienteViewModel by viewModels()
    private val validator = PacienteValidator()

    private var pacienteId: Int = -1
    private val modoEdicion get() = pacienteId != -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        pacienteId = intent.getIntExtra("id", -1)

        if (modoEdicion) {
            binding.tvTituloForm.text = "Editar Paciente"
            supportActionBar?.title   = "Editar Paciente"
            cargarDatosParaEdicion()
        } else {
            binding.tvTituloForm.text = "Nuevo Paciente"
            supportActionBar?.title   = "Nuevo Paciente"
            binding.toggleEstado.check(R.id.btnEstadoEspera)
        }

        configurarFechaPicker()
        configurarCalculoImc()
        binding.btnGuardar.setOnClickListener { guardarPaciente() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Si estoy editando, relleno los campos con los datos que ya tenía el paciente
    private fun cargarDatosParaEdicion() {
        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etApellido.setText(intent.getStringExtra("apellido"))
        binding.etFechaNacimiento.setText(intent.getStringExtra("fechaNacimiento"))
        binding.etTelefono.setText(intent.getStringExtra("telefono"))
        binding.etPeso.setText(intent.getDoubleExtra("peso", 0.0).toString())
        binding.etEstatura.setText(intent.getDoubleExtra("estatura", 0.0).toString())
        binding.etImc.setText(intent.getDoubleExtra("imc", 0.0).toString())
        binding.etAntecedentes.setText(intent.getStringExtra("antecedentes"))
        when (intent.getStringExtra("estado")) {
            "EN_CONSULTA" -> binding.toggleEstado.check(R.id.btnEstadoConsulta)
            "ALTA"        -> binding.toggleEstado.check(R.id.btnEstadoAlta)
            else          -> binding.toggleEstado.check(R.id.btnEstadoEspera)
        }
    }

    // El campo de fecha no es editable a mano. Al tocarlo se abre el calendario de Material.
    private fun configurarFechaPicker() {
        val abrirPicker = View.OnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Fecha de nacimiento")
                .build()
            picker.addOnPositiveButtonClickListener { millis ->
                val cal  = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.timeInMillis = millis
                val dd   = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
                val mm   = String.format("%02d", cal.get(Calendar.MONTH) + 1)
                val yyyy = cal.get(Calendar.YEAR)
                binding.etFechaNacimiento.setText("$dd/$mm/$yyyy")
            }
            picker.show(supportFragmentManager, "fecha_picker")
        }
        binding.etFechaNacimiento.setOnClickListener(abrirPicker)
        binding.tilFechaNacimiento.setEndIconOnClickListener(abrirPicker)
    }

    // Cada vez que el usuario escribe peso o estatura, recalculo el IMC en tiempo real
    private fun configurarCalculoImc() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val peso     = binding.etPeso.text.toString().toDoubleOrNull()
                val estatura = binding.etEstatura.text.toString().toDoubleOrNull()
                if (peso != null && estatura != null && estatura > 0) {
                    val imc   = CalculosMedico.calcularIMC(peso, estatura)
                    val nivel = CalculosMedico.obtenerNivelPeso(imc)
                    binding.etImc.setText("$imc — $nivel")
                } else {
                    binding.etImc.setText("")
                }
            }
        }
        binding.etPeso.addTextChangedListener(watcher)
        binding.etEstatura.addTextChangedListener(watcher)
    }

    private fun guardarPaciente() {
        if (!validateFields()) return

        binding.btnGuardar.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        val paciente = createPacienteFromForm()

        if (modoEdicion) {
            viewModel.update(paciente)
            Toast.makeText(this, "Paciente actualizado", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(paciente)
            Toast.makeText(this, "Paciente registrado", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun validateFields(): Boolean {
        val nombre    = binding.etNombre.text.toString()
        val apellido  = binding.etApellido.text.toString()
        val fecha     = binding.etFechaNacimiento.text.toString()
        val pesoStr   = binding.etPeso.text.toString()
        val estaturaStr = binding.etEstatura.text.toString()

        val errors = validator.validateFields(nombre, apellido, fecha, pesoStr, estaturaStr)

        binding.tilNombre.error          = errors["nombre"]
        binding.tilApellido.error        = errors["apellido"]
        binding.tilFechaNacimiento.error = errors["fechaNacimiento"]
        binding.tilPeso.error            = errors["peso"]
        binding.tilEstatura.error        = errors["estatura"]

        return errors.isEmpty()
    }

    private fun createPacienteFromForm(): Paciente {
        val peso     = binding.etPeso.text.toString().toDoubleOrNull() ?: 0.0
        val estatura = binding.etEstatura.text.toString().toDoubleOrNull() ?: 0.0

        return Paciente(
            id              = if (modoEdicion) pacienteId else 0,
            nombre          = binding.etNombre.text.toString().trim(),
            apellido        = binding.etApellido.text.toString().trim(),
            fechaNacimiento = binding.etFechaNacimiento.text.toString().trim(),
            telefono        = binding.etTelefono.text.toString().trim(),
            peso            = peso,
            estatura        = estatura,
            imc             = CalculosMedico.calcularIMC(peso, estatura),
            antecedentes    = binding.etAntecedentes.text.toString().trim(),
            estado          = when (binding.toggleEstado.checkedButtonId) {
                R.id.btnEstadoConsulta -> "EN_CONSULTA"
                R.id.btnEstadoAlta     -> "ALTA"
                else                   -> "EN_ESPERA"
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
