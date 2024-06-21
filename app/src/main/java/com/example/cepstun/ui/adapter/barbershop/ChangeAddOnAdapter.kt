package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.remote.response.barber.AddonsAccount
import com.example.cepstun.databinding.ItemAddonBarberBinding
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeAddOnAdapter : ListAdapter<AddonsAccount, ChangeAddOnAdapter.AddOnsViewHolder>(AddOnsDiffCallback) {

    class AddOnsViewHolder(binding: ItemAddonBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameAddOn = binding.TVAddon
        val priceAddOn = binding.TVPrice
        val deleteAddOn = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddOnsViewHolder {
        val binding = ItemAddonBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddOnsViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: AddOnsViewHolder, position: Int) {
        val model = getItem(position)

        holder.deleteAddOn.setImageResource(R.drawable.ic_delete)

        holder.nameAddOn.text = model.name
        GlobalScope.launch {
            val formattedPrice = model.price.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.priceAddOn.text = formattedPrice
            }
        }

        holder.deleteAddOn.setOnClickListener {
            onItemClickCallback.onDeleteClick(model.name)
        }
    }

    private lateinit var onItemClickCallback: OnChangeClickListener

    fun setOnItemClickCallback(onItemClickCallback: OnChangeClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnChangeClickListener {
        fun onDeleteClick(name: String)
    }

    companion object AddOnsDiffCallback : DiffUtil.ItemCallback<AddonsAccount>() {
        override fun areItemsTheSame(oldItem: AddonsAccount, newItem: AddonsAccount) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AddonsAccount, newItem: AddonsAccount) =
            oldItem == newItem
    }
}