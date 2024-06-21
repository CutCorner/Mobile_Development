package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.databinding.ItemBarberBinding

class BarberSearchAdapter : ListAdapter<BarberData, BarberSearchAdapter.BarberViewHolder>(BarberSearchDiffCallback) {

    class BarberViewHolder(binding: ItemBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val barberName = binding.TVTittleBarber
        val barberLogo = binding.IVLogoBarber
        val barberRate = binding.TVRate
        val barberStarRate = binding.RBRate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberViewHolder {
        val binding = ItemBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        val data = getItem(position)
        holder.barberName.text = data.name

        if (data.rate != null) {
            holder.barberRate.text = holder.itemView.context.getString(R.string.rate, data.rate.toString())
            holder.barberStarRate.rating = data.rate.toFloat()
        } else {
            val averageRating = data.rating.map { it.ratingScore }.average()
            val roundedRating = Math.round(averageRating * 10.0) / 10.0
            holder.barberRate.text = holder.itemView.context.getString(R.string.rate, roundedRating.toString())
            holder.barberStarRate.rating = roundedRating.toFloat()
        }


        Glide.with(holder.itemView.context)
            .load(data.logo)
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.barberLogo)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onBarberClick(data.id)
        }
    }

    private var onItemClickCallback: OnBarberClickListener? = null

    fun setOnItemClickCallback(onItemClickCallback: OnBarberClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnBarberClickListener {
        fun onBarberClick(barberId: String)
    }

    companion object {
        val BarberSearchDiffCallback = object : DiffUtil.ItemCallback<BarberData>() {
            override fun areItemsTheSame(oldItem: BarberData, newItem: BarberData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BarberData, newItem: BarberData): Boolean {
                return oldItem == newItem
            }
        }
    }
}