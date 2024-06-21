package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.Rating
import com.example.cepstun.databinding.ItemReviewBinding

class ReviewAdapter : ListAdapter<Rating, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    class ReviewViewHolder(binding: ItemReviewBinding): RecyclerView.ViewHolder(binding.root) {
        val reviewName = binding.TVName
        val modelName = binding.TVModel
        val addonsName = binding.TVAddons
        val reviewMessage = binding.TVReview
        val rate = binding.TVRate
        val image = binding.CIVReviewer
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val rating = getItem(position)
        holder.reviewName.text = rating.name
        holder.reviewMessage.text = if (rating.model == "") "-" else rating.review
        holder.modelName.text = if (rating.model == "") "-" else rating.model
        holder.addonsName.text = if (rating.addOns == "") "-" else rating.addOns
        holder.rate.text = rating.ratingScore.toString()

        Glide.with(holder.itemView.context)
            .load(rating.image)
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.image)
    }
}

class ReviewDiffCallback : DiffUtil.ItemCallback<Rating>() {
    override fun areItemsTheSame(oldItem: Rating, newItem: Rating): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Rating, newItem: Rating): Boolean {
        return oldItem == newItem
    }
}