package com.example.ximcaclinicapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.databinding.ActivityPacienteFormBinding
import com.example.ximcaclinicapp.utils.CalculosMedico

// Este formulario sirve para DOS cosas: crear un paciente nuevo Y editar uno existente.
// Sé en qué modo estoy según si me llegó un "id" en el Intent:
//   - id = -1 (o no llegó nada) → modo CREAR
//   - id >= 0                    → modo EDITAR (ese es el ID del paciente a editar)
class PacienteFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPacienteFormBinding
    private val viewModel: PacienteViewModel by viewModels()

    // Guardo el ID del paciente. Si es -1, es porque estoy creando uno nuevo.
    private var pacienteId: Int = -1

    // Esta propiedad calculada me dice si estoy en modo edición de forma legible.
    // En vez de escribir "pacienteId != -1" en todo el código, escribo "modoEdicion".
    private val modoEdicion get() = pacienteId != -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPacienteFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intento leer el ID del paciente del Intent.
        // Si no existe la clave "id", el valor por defecto es -1 (modo crear).
        pacienteId = intent.getIntExtra("id", -1)

        if (modoEdicion) {
            // Modo EDITAR: cambio el título y lleno los campos con los datos actuales
            binding.tvTituloForm.text = "Editar Paciente"
            supportActionBar?.title = "Editar Paciente"
            cargarDatosParaEdicion()
        } else {
            // Modo CREAR: pongo "EN_ESPERA" como estado por defecto en el campo
            binding.tvTituloForm.text = "Nuevo Paciente"
            supportActionBar?.title = "Nuevo Paciente"
            binding.etEstado.setText("EN_ESPERA")
        }

        // Activo el cálculo automático del IMC cuando cambien peso o estatura
        configurarCalculoImc()

        binding.btnGuardar.setOnClickListener { guardarPaciente() }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Si vengo del detalle a editar, cargo los datos actuales del paciente en los campos
    private fun cargarDatosParaEdicion() {
        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etApellido.setText(intent.getStringExtra("apellido"))
        binding.etFechaNacimiento.setText(intent.getStringExtra("fechaNacimiento"))
        binding.etPeso.setText(intent.getDoubleExtra("peso", 0.0).toString())
        binding.etEstatura.setText(intent.getDoubleExtra("estatura", 0.0).toString())
        binding.etImc.setText(intent.getDoubleExtra("imc", 0.0).toString())
        binding.etAntecedentes.setText(intent.getStringExtra("antecedentes"))
        binding.etEstado.setText(intent.getStringExtra("estado"))
    }

    // TextWatcher "escucha" cambios en un campo de texto en tiempo real.
    // Lo agrego a los campos de peso y estatura para calcular el IMC automáticamente
    // cada vez que el usuario termina de escribir un número.
    private fun configurarCalculoImc() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // afterTextChanged se llama cada vez que el texto cambia.
            // Intento convertir los campos a Double. Si alguno no es un número válido,
            // toDoubleOrNull() devuelve null y limpio el campo de IMC.
            override fun afterTextChanged(s: Editable?) {
                val peso = binding.etPeso.text.toString().toDoubleOrNull()
                val estatura = binding.etEstatura.text.toString().toDoubleOrNull()

                if (peso != null && estatura != null && estatura > 0) {
                    val imc = CalculosMedico.calcularIMC(peso, estatura)
                    val nivel = CalculosMedico.obtenerNivelPeso(imc)
                    binding.etImc.setText("$imc — $nivel") // Muestro IMC y su clasificación juntos
                } else {
                    binding.etImc.setText("") // Si los números no son válidos, dejo el campo vacío
                }
            }
        }

        // Le digo a los dos campos que avisen cuando cambien
        binding.etPeso.addTextChangedListener(watcher)
        binding.etEstatura.addTextChangedListener(watcher)
    }

    // Función que valida todos los campos y guarda el paciente si todo está bien
    private fun guardarPaciente() {
        val nombre = binding.etNombre.text.toString().trim()
        val apellido = binding.etApellido.text.toString().trim()
        val fecha = binding.etFechaNacimiento.text.toString().trim()
        val pesoStr = binding.etPeso.text.toString().trim()
        val estaturaStr = binding.etEstatura.text.toString().trim()
        val antecedentes = binding.etAntecedentes.text.toString().trim()
        val estado = binding.etEstado.text.toString().trim().ifEmpty { "EN_ESPERA" }

        // --- VALIDACIONES ---
        // Valido campo por campo y paro en el primero que falle.
        // El error aparece visualmente debajo del campo con texto rojo.

        if (nombre.isEmpty()) {
            binding.tilNombre.error = "El nombre es obligatorio"
            return
        } else binding.tilNombre.error = null

        if (apellido.isEmpty()) {
            binding.tilApellido.error = "El apellido es obligatorio"
            return
        } else binding.tilApellido.error = null

        if (fecha.isEmpty()) {
            binding.tilFechaNacimiento.error = "La fecha es obligatoria"
            return
        } else binding.tilFechaNacimiento.error = null

        // toDoubleOrNull() es seguro: si el texto no es un número válido, devuelve null
        // en vez de lanzar una excepción que crashearía la app.
        val peso = pesoStr.toDoubleOrNull()
        if (peso == null || peso <= 0) {
            binding.tilPeso.error = "Ingresa un peso válido en kg"
            return
        } else binding.tilPeso.error = null

        val estatura = estaturaStr.toDoubleOrNull()
        if (estatura == null || estatura <= 0) {
            binding.tilEstatura.error = "Ingresa una estatura válida en metros (ej: 1.70)"
            return
        } else binding.tilEstatura.error = null

        // Calculo el IMC final con los valores validados
        val imc = CalculosMedico.calcularIMC(peso, estatura)

        // Construyo el objeto Paciente con todos los datos del formulario.
        // Si estoy editando, uso el ID original (pacienteId) para que Room sepa
        // qué registro actualizar. Si estoy creando, id = 0 y Room asigna uno nuevo.
        val paciente = Paciente(
            id = if (modoEdicion) pacienteId else 0,
            nombre = nombre,
            apellido = apellido,
            fechaNacimiento = fecha,
            peso = peso,
            estatura = estatura,
            imc = imc,
            antecedentes = antecedentes,
            estado = estado
        )

        // Le digo al ViewModel qué operación hacer (insert o update)
        if (modoEdicion) {
            viewModel.update(paciente)
            Toast.makeText(this, "Paciente actualizado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(paciente)
            Toast.makeText(this, "Paciente registrado correctamente", Toast.LENGTH_SHORT).show()
        }

        // Cierro el formulario. La lista se actualizará sola gracias al Flow+LiveData.
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
