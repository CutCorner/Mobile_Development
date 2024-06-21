package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.example.cepstun.databinding.ItemOrderBinding
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.*

class CustomerHistoryAdapter (private val pagePosition: Int): ListAdapter<HistoryCustomer, CustomerHistoryAdapter.CustomerHistoryViewHolder>(
    DIFF_CALLBACK
){
    class CustomerHistoryViewHolder(binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root) {
        val logoBarber = binding.IVLogoBarber
        val nameBarber = binding.TVName
        val model = binding.TVModel
        val addOn = binding.TVAddons
        val totalPrice = binding.TVPrice
        val status = binding.TVStatus
        val delete = binding.IBDelete
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerHistoryViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerHistoryViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(
        holder: CustomerHistoryViewHolder,
        position: Int
    ) {
        val data = getItem(position)

        if (pagePosition == 1) {
            holder.delete.setImageResource(R.drawable.ic_location)
            holder.delete.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray5), android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            holder.delete.setImageResource(R.drawable.ic_delete)
            holder.delete.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN)
        }

        Glide.with(holder.itemView.context)
            .load(data.logoBarber)
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.logoBarber)
        holder.nameBarber.text = data.nameBarber
        holder.model.text = data.modelBarber
        holder.addOn.text = data.addOnBarber
        holder.status.text = data.status
        GlobalScope.launch {
            val formattedPrice = data.priceBarber.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.totalPrice.text = formattedPrice
            }
        }

        holder.delete.setOnClickListener {
            onItemClickCallback.onOrderClick(data.id.toString())
        }
    }

    private lateinit var onItemClickCallback: OnOrderClickListener

    fun setOnItemClickCallback(onItemClickCallback: OnOrderClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnOrderClickListener {
        fun onOrderClick(barberId: String)
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryCustomer>() {
            override fun areItemsTheSame(oldItem: HistoryCustomer, newItem: HistoryCustomer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HistoryCustomer, newItem: HistoryCustomer): Boolean {
                return oldItem == newItem
            }
        }
    }

}