package com.example.ximcaclinicapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ximcaclinicapp.data.Paciente
import com.example.ximcaclinicapp.databinding.ItemPacienteBinding
import com.example.ximcaclinicapp.utils.CalculosMedico
import com.example.ximcaclinicapp.R

// El Adapter es el "traductor" entre mi lista de datos (List<Paciente>)
// y las tarjetas visuales que aparecen en el RecyclerView.
// RecyclerView reutiliza las vistas (de ahí "Recycle") para no gastar memoria.
//
// ListAdapter es una versión inteligente del Adapter básico.
// Detecta qué elementos cambiaron y solo actualiza esos, no la lista entera.
// Para eso necesita el DIFF_CALLBACK (explicado abajo).
//
// Recibe dos lambdas (funciones) que le digo desde afuera:
// - onItemClick: qué hago cuando toco una tarjeta (ir al detalle)
// - onDeleteClick: qué hago cuando toco el botón eliminar
class PacienteAdapter(
    private val onItemClick: (Paciente) -> Unit,
    private val onDeleteClick: (Paciente) -> Unit
) : ListAdapter<Paciente, PacienteAdapter.PacienteViewHolder>(DIFF_CALLBACK) {

    companion object {
        // DiffUtil compara listas vieja vs nueva para saber qué cambió.
        // areItemsTheSame: ¿son el mismo elemento? (comparo por ID único)
        // areContentsTheSame: ¿tienen los mismos datos? (comparo todo el objeto)
        // Esto hace que la animación de actualización sea fluida y eficiente.
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Paciente>() {
            override fun areItemsTheSame(old: Paciente, new: Paciente) = old.id == new.id
            override fun areContentsTheSame(old: Paciente, new: Paciente) = old == new
        }
    }

    // ViewHolder "sostiene" (holds) las referencias a las vistas de UNA tarjeta.
    // Así no tengo que buscar cada TextView cada vez que la tarjeta aparece en pantalla.
    // Uso ViewBinding (ItemPacienteBinding) para acceder a las vistas sin findViewById.
    inner class PacienteViewHolder(private val binding: ItemPacienteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // bind() llena los datos de UN paciente en la tarjeta visual
        fun bind(paciente: Paciente) {
            binding.tvNombrePaciente.text = "${paciente.nombre} ${paciente.apellido}"
            binding.tvEstadoPaciente.text = paciente.estado

            // Asigno el badge de color según el estado del paciente
            val (bgRes, textColorRes) = when (paciente.estado) {
                "EN_CONSULTA" -> Pair(R.drawable.bg_badge_en_consulta, R.color.status_en_consulta_text)
                "ALTA"        -> Pair(R.drawable.bg_badge_alta, R.color.status_alta_text)
                else          -> Pair(R.drawable.bg_badge_en_espera, R.color.status_en_espera_text)
            }
            binding.tvEstadoPaciente.setBackgroundResource(bgRes)
            binding.tvEstadoPaciente.setTextColor(
                binding.root.context.getColor(textColorRes)
            )

            // Calculo el nivel de peso para mostrarlo junto al IMC
            val nivel = CalculosMedico.obtenerNivelPeso(paciente.imc)
            binding.tvImcPaciente.text = "IMC: ${paciente.imc} — $nivel"

            // Cuando toco la tarjeta entera, ejecuto onItemClick con este paciente
            binding.root.setOnClickListener { onItemClick(paciente) }

            // Cuando toco solo el botón X, ejecuto onDeleteClick
            binding.btnEliminar.setOnClickListener { onDeleteClick(paciente) }
        }
    }

    // RecyclerView llama a esta función cuando necesita crear una tarjeta nueva.
    // LayoutInflater convierte el XML (item_paciente.xml) en una vista real.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val binding = ItemPacienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PacienteViewHolder(binding)
    }

    // RecyclerView llama a esta función cuando tiene que llenar una tarjeta con datos.
    // getItem(position) me da el Paciente que corresponde a esa posición en la lista.
    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
