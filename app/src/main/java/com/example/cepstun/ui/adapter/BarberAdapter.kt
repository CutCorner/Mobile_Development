package com.example.cepstun.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.databinding.ItemBarberBinding
import com.example.cepstun.ui.activity.BarbershopActivity
import com.example.cepstun.ui.activity.BarbershopActivity.Companion.DATA_BARBER
import com.example.cepstun.ui.activity.LoginActivity

class BarberAdapter(private val barberList: List<BarberData>): RecyclerView.Adapter<BarberAdapter.BarberViewHolder>() {
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
        val data = barberList[position]
        holder.barberName.text = data.name
        holder.barberRate.text = holder.itemView.context.getString(R.string.rate, data.rate.toString())
//        holder.modelImage.setImageResource(data.image)
        holder.barberStarRate.rating = data.rate.toFloat()

        Glide.with(holder.itemView.context)
            .load(data.logo)
            .centerCrop()
            .into(holder.barberLogo)

        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, BarbershopActivity::class.java).also { intent ->
                intent.putExtra(DATA_BARBER, data)
                holder.itemView.context.startActivity(intent)
            }
        }

    }

    override fun getItemCount() = barberList.size

}