package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.ListOrder
import com.example.cepstun.databinding.ItemCustomerBinding
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerOrderAdapter : ListAdapter<ListOrder, CustomerOrderAdapter.CustomerOrderViewHolder>(
    DIFF_CALLBACK
) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListOrder>() {
            override fun areItemsTheSame(oldItem: ListOrder, newItem: ListOrder): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: ListOrder, newItem: ListOrder): Boolean {
                return oldItem == newItem && oldItem.position == newItem.position
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerOrderViewHolder {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerOrderViewHolder(binding)
    }

    class CustomerOrderViewHolder(val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameCustomer = binding.TVNameCustomer
        val queueCustomer = binding.TVQueue
        val totalPrice = binding.TVPrice
        val model = binding.TVModel
        val addon = binding.TVAddons
        val accept = binding.IBAccept
        val decline = binding.IBDecline

    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: CustomerOrderViewHolder, position: Int) {
        val order = getItem(position)

        if (position == 0) {
            holder.binding.IBAccept.visibility = View.VISIBLE
            holder.binding.IBDecline.visibility = View.VISIBLE
        } else {
            holder.binding.IBAccept.visibility = View.GONE
            holder.binding.IBDecline.visibility = View.GONE
        }

        holder.nameCustomer.text = order.name
        holder.queueCustomer.text = order.position.toString()
        holder.model.text = order.model
        holder.addon.text = order.addon
        GlobalScope.launch {
                val formattedPrice = order.price.toString().withCurrencyFormat(holder.itemView.context)
                withContext(Dispatchers.Main) {
                    holder.totalPrice.text = formattedPrice
                }
            }

        holder.accept.setOnClickListener {
            onItemClickListener.onAcceptClick(order)
        }

        holder.decline.setOnClickListener {
            onItemClickListener.onDeclineClick(order)
        }
    }

    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onAcceptClick(order: ListOrder)
        fun onDeclineClick(order: ListOrder)
    }
}