package com.example.cepstun.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberData
import com.example.cepstun.data.local.Rating
import com.example.cepstun.databinding.ItemReviewBinding

class ReviewAdapter(private var barberId: String, private var ratingData: List<Rating>): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    class ReviewViewHolder(binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root) {
        val reviewName = binding.TVName
        val modelName = binding.TVModel
        val addonsName = binding.TVAddons
        val reviewMessage = binding.TVReview
        val rate = binding.TVRate
        val image = binding.CIVReviewer
    }

    private val filteredRatingData = ratingData.filter { it.id == barberId }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder (binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {

        var totalNames = 0
        for (rating in filteredRatingData) {
            if (position < totalNames + rating.name.size) {
                val nameIndex = position - totalNames
                holder.reviewName.text = rating.name[nameIndex]
                holder.reviewMessage.text = if (rating.model[nameIndex] == "") "-" else rating.review[nameIndex]
                holder.modelName.text = if (rating.model[nameIndex] == "") "-" else rating.model[nameIndex]
                holder.addonsName.text = if (rating.addOns[nameIndex] == "") "-" else rating.addOns[nameIndex]

                holder.rate.text = rating.ratingScore[nameIndex].toString()

                Glide.with(holder.itemView.context)
                    .load(rating.image[nameIndex])
                    .placeholder(R.drawable.logo_placeholder)
                    .into(holder.image)
                return
            }
            totalNames += rating.name.size
        }

    }

    override fun getItemCount(): Int {

        var totalNames = 0
        for (rating in filteredRatingData) {
            totalNames += rating.name.size
        }
        return totalNames
    }

}