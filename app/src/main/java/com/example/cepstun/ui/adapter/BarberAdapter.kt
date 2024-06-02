package com.example.cepstun.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.Rating
import com.example.cepstun.databinding.ItemBarberBinding
import com.example.cepstun.ui.activity.BarbershopActivity
import com.example.cepstun.ui.activity.BarbershopActivity.Companion.ID_BARBER

class BarberAdapter(private var barberData: List<BarberData>, private var ratingData: List<Rating>): RecyclerView.Adapter<BarberAdapter.BarberViewHolder>() {
    class BarberViewHolder(binding: ItemBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val barberName = binding.TVTittleBarber
        val barberLogo = binding.IVLogoBarber
        val barberRate = binding.TVRate
        val barberStarRate = binding.RBRate
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BarberViewHolder {
        val binding = ItemBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarberViewHolder, position: Int) {
        val data = barberData[position]
        holder.barberName.text = data.name

        val barberId = data.id
        val barberRating = ratingData.find { it.id == barberId }
        if (barberRating != null) {
            val averageRating = barberRating.ratingScore.average()
            val roundedRating = Math.round(averageRating * 10.0) / 10.0
            holder.barberRate.text = holder.itemView.context.getString(R.string.rate, roundedRating.toString())
            holder.barberStarRate.rating = roundedRating.toFloat()
        }

//        holder.barberRate.text = holder.itemView.context.getString(R.string.rate, data.rate.toString())
//        holder.barberStarRate.rating = data.rate.toFloat()

        Glide.with(holder.itemView.context)
            .load(data.logo)
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.barberLogo)

        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, BarbershopActivity::class.java).also { intent ->
                intent.putExtra(ID_BARBER, data.id)
                holder.itemView.context.startActivity(intent)
            }
        }

    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun setData(newBarberData: List<BarberData>, newRatingData: List<Rating>) {
//        barberData = newBarberData
//        ratingData = newRatingData
//        notifyDataSetChanged()
//    }

    override fun getItemCount() = barberData.size

}