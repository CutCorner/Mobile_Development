package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.AddAddOn
import com.example.cepstun.databinding.ItemAddonBarberBinding

class AddAddOnBarberAdapter (private val onItemDelete: (AddAddOn) -> Unit
) : ListAdapter<AddAddOn, AddAddOnBarberAdapter.AddOnViewHolder>(AddOnDiffCallback) {

    object AddOnDiffCallback : DiffUtil.ItemCallback<AddAddOn>() {
        override fun areItemsTheSame(oldItem: AddAddOn, newItem: AddAddOn) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AddAddOn, newItem: AddAddOn) = oldItem == newItem
    }

    class AddOnViewHolder(binding: ItemAddonBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameTextView = binding.TVAddon
        val priceTextView = binding.TVPrice
        val deleteButton = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddOnViewHolder {
        val binding = ItemAddonBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddOnViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddOnViewHolder, position: Int) {
        val model = getItem(position)
        holder.nameTextView.text = model.name
        holder.priceTextView.text = model.price.toString()
        holder.deleteButton.visibility = View.VISIBLE
        holder.deleteButton.setOnClickListener {
            onItemDelete(model)
        }
    }
}