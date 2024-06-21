package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.data.local.entity.customer.NotificationCustomer
import com.example.cepstun.databinding.ItemNotificationBinding
class NotificationAdapter: ListAdapter<NotificationCustomer, NotificationAdapter.NotificationViewHolder>(
    DIFF_CALLBACK
){
    class NotificationViewHolder(binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        val nameBarber = binding.TVBarberName
        val model = binding.TVModel
        val addOn = binding.TVAddon
        val status = binding.TVSatatus
        val reason = binding.TVReason
        val date = binding.TVDate
        val delete = binding.IBDelete
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {
        val data = getItem(position)

        holder.nameBarber.text = data.nameBarber
        holder.model.text = data.modelBarber
        holder.addOn.text = data.addOnBarber
        holder.status.text = data.status
        holder.reason.text = data.message
        holder.date.text = data.date

        holder.delete.setOnClickListener {
            onItemClickCallback.onNotificationClick(data.id.toString())
        }
    }

    private lateinit var onItemClickCallback: OnNotificationClickListener

    fun setOnItemClickCallback(onItemClickCallback: OnNotificationClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnNotificationClickListener {
        fun onNotificationClick(barberId: String)
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NotificationCustomer>() {
            override fun areItemsTheSame(oldItem: NotificationCustomer, newItem: NotificationCustomer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NotificationCustomer, newItem: NotificationCustomer): Boolean {
                return oldItem == newItem
            }
        }
    }

}