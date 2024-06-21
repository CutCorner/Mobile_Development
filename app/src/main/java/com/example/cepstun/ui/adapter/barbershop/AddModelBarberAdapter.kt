package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.AddModel
import com.example.cepstun.databinding.ItemModelBarberBinding

class AddModelBarberAdapter(private val onItemDelete: (AddModel) -> Unit
) : ListAdapter<AddModel, AddModelBarberAdapter.ModelViewHolder>(ModelDiffCallback) {

    object ModelDiffCallback : DiffUtil.ItemCallback<AddModel>() {

        override fun areItemsTheSame(oldItem: AddModel, newItem: AddModel) =  oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AddModel, newItem: AddModel) = oldItem == newItem

    }

    class ModelViewHolder(binding: ItemModelBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.SIVModel
        val nameTextView = binding.TVModel
        val priceTextView = binding.TVPrice
        val deleteButton = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = getItem(position)
        holder.imageView.setImageURI(model.uri)
        holder.nameTextView.text = model.name
        holder.priceTextView.text = model.price.toString()
        holder.deleteButton.visibility = View.VISIBLE
        holder.deleteButton.setOnClickListener {
            onItemDelete(model)
        }
    }
}