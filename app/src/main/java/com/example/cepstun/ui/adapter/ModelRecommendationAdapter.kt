package com.example.cepstun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.ModelData
import com.example.cepstun.databinding.ItemModelRecommendationBinding

class ModelRecommendationAdapter(private val modelList: List<ModelData>): RecyclerView.Adapter<ModelRecommendationAdapter.ModelViewHolder>() {
    class ModelViewHolder(binding: ItemModelRecommendationBinding): RecyclerView.ViewHolder(binding.root) {
        val modelName = binding.TVModel
        val modelImage = binding.SIVModel
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModelViewHolder {
        val binding = ItemModelRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val data = modelList[position]

        holder.modelName.text = data.name
//        holder.modelImage.setImageResource(data.image)

        Glide.with(holder.itemView.context)
            .load(data.image)
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.modelImage)

    }

    override fun getItemCount() = modelList.size

}