package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop
import com.example.cepstun.databinding.ItemHistoryBinding
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarbershopHistoryAdapter (): ListAdapter<HistoryBarbershop, BarbershopHistoryAdapter.BarbershopHistoryViewHolder>(
    DIFF_CALLBACK
){
    class BarbershopHistoryViewHolder(binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        val queueCustomer = binding.TVQueue
        val nameCustomer = binding.TVNameCustomer
        val model = binding.TVModel
        val addOn = binding.TVAddons
        val totalPrice = binding.TVPrice
        val status = binding.TVProses
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BarbershopHistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarbershopHistoryViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(
        holder: BarbershopHistoryViewHolder,
        position: Int
    ) {
        val data = getItem(position)

        holder.queueCustomer.text = data.positionCustomer.toString()
        holder.nameCustomer.text = data.nameCustomer
        holder.model.text = data.modelCustomer
        holder.addOn.text = data.addOnCustomer
        holder.status.text = data.status
        GlobalScope.launch {
            val formattedPrice = data.price.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.totalPrice.text = formattedPrice
            }
        }
    }

//    private lateinit var onItemClickCallback: OnOrderClickListener
//
//    fun setOnItemClickCallback(onItemClickCallback: OnOrderClickListener) {
//        this.onItemClickCallback = onItemClickCallback
//    }
//
//    interface OnOrderClickListener {
//        fun onOrderClick(barberId: String)
//    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryBarbershop>() {
            override fun areItemsTheSame(oldItem: HistoryBarbershop, newItem: HistoryBarbershop): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HistoryBarbershop, newItem: HistoryBarbershop): Boolean {
                return oldItem == newItem
            }
        }
    }

}