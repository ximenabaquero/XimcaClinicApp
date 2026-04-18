package com.example.ximcaclinicapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ximcaclinicapp.data.Nota
import com.example.ximcaclinicapp.databinding.ItemNotaBinding

class NotaAdapter(
    private val onDelete: (Nota) -> Unit
) : ListAdapter<Nota, NotaAdapter.NotaViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Nota>() {
        override fun areItemsTheSame(oldItem: Nota, newItem: Nota) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Nota, newItem: Nota) = oldItem == newItem
    }

    inner class NotaViewHolder(private val binding: ItemNotaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nota: Nota) {
            binding.tvFechaNota.text   = nota.fechaCreacion
            binding.tvTextoNota.text   = nota.texto
            binding.btnEliminarNota.setOnClickListener { onDelete(nota) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val binding = ItemNotaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
