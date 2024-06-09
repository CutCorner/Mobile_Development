package com.example.cepstun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.data.local.BarberData
//import com.example.cepstun.databinding.ItemFavoriteBinding

//class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

//    private val favorites = mutableListOf<BarberData>()
//
//    fun submitList(newFavorites: List<BarberData>) {
//        favorites.clear()
//        favorites.addAll(newFavorites)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
////        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return FavoriteViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
//        holder.bind(favorites[position])
//    }
//
//    override fun getItemCount(): Int = favorites.size
//
//    class FavoriteViewHolder(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(barberData: BarberData) {
//            binding.apply {
//                TVTittleBarber.text = barberData.name
//                TVLocationBarber.text = barberData.location
//                RBRate.rating = barberData.rate.toFloat()
//                TVRate.text = barberData.rate.toString()
//                Glide.with(itemView.context)
//                    .load(barberData.image)
//                    .centerCrop()
//                    .into(IVImage)
//            }
//        }
//    }
//}
