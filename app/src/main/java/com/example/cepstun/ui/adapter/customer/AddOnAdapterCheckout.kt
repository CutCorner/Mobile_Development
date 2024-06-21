package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.AddOn
import com.example.cepstun.databinding.ItemAddonBinding
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddOnAdapterCheckout(private val onAddOnClick: (AddOn) -> Unit) :
    ListAdapter<AddOn, AddOnAdapterCheckout.AddOnViewHolder>(AddOnDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddOnViewHolder {
        val binding = ItemAddonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddOnViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: AddOnViewHolder, position: Int) {
        val addOn = getItem(position)
        holder.binding.TVAddon.text = addOn.name

        GlobalScope.launch {
            val formattedPrice = addOn.price.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.binding.TVPriceAddOn.text = formattedPrice
            }
        }

        holder.binding.IBAdd.setImageResource(if (addOn.isSelected) R.drawable.ic_remove else R.drawable.ic_add)
        holder.binding.IBAdd.setOnClickListener {
            addOn.isSelected = !addOn.isSelected
            notifyItemChanged(position)
            onAddOnClick(addOn)
        }
    }

    inner class AddOnViewHolder(val binding: ItemAddonBinding) : RecyclerView.ViewHolder(binding.root)

    class AddOnDiffCallback : DiffUtil.ItemCallback<AddOn>() {
        override fun areItemsTheSame(oldItem: AddOn, newItem: AddOn): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: AddOn, newItem: AddOn): Boolean {
            return oldItem == newItem
        }
    }
}