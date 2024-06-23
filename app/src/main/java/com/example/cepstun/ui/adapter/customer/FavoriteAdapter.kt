package com.example.cepstun.ui.adapter.customer

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.entity.customer.FavoriteCustomer
import com.example.cepstun.databinding.ItemFavoriteBarberBinding
import com.example.cepstun.ui.activity.customer.BarbershopActivity
import com.example.cepstun.utils.getFullImageUrl
import kotlinx.coroutines.DelicateCoroutinesApi

class FavoriteAdapter : ListAdapter<FavoriteCustomer, FavoriteAdapter.FavoriteViewHolder>(
    DIFF_CALLBACK
){
    class FavoriteViewHolder(binding: ItemFavoriteBarberBinding): RecyclerView.ViewHolder(binding.root) {
        val logoBarber = binding.IVLogoBarber
        val nameBarber = binding.TVTittleBarber
        val favorite = binding.IBFavorite
        val rateStar = binding.RBRate
        val rateNum = binding.TVRate
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val data = getItem(position)

        Glide.with(holder.itemView.context)
            .load(data.logoBarber.getFullImageUrl())
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.logoBarber)
        holder.nameBarber.text = data.nameBarber
        holder.rateStar.rating = data.ratingBarber.toFloat()
        holder.rateNum.text = "(${data.ratingBarber}/5)"

        holder.favorite.setOnClickListener {
            onItemClickCallback.onFavoriteClick(data.id.toString())
        }
        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, BarbershopActivity::class.java).also { intent->
                intent.putExtra(BarbershopActivity.ID_BARBER, data.idBarber)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    private lateinit var onItemClickCallback: OnFavoriteClickListener

    fun setOnItemClickCallback(onItemClickCallback: OnFavoriteClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnFavoriteClickListener {
        fun onFavoriteClick(barberId: String)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteCustomer>() {
            override fun areItemsTheSame(oldItem: FavoriteCustomer, newItem: FavoriteCustomer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteCustomer, newItem: FavoriteCustomer): Boolean {
                return oldItem == newItem
            }
        }
    }
}
