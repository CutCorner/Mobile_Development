package com.example.cepstun.ui.adapter.barbershop

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.remote.dataClass.ImageItem
import com.example.cepstun.databinding.ItemImageBinding
import com.example.cepstun.utils.getFullImageUrl

class ChangeImageAdapter(private val onImageRemove: (ImageItem) -> Unit) :
    ListAdapter<ImageItem, ChangeImageAdapter.ImageViewHolder>(ImageItemDiffCallback) {

    object ImageItemDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
            return oldItem == newItem
        }
    }

    class ImageViewHolder(binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.IVBarber
        val removeButton = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        val uri = when (item) {
            is ImageItem.UriImage -> item.uri
            is ImageItem.UrlImage -> Uri.parse(item.url.getFullImageUrl())
        }
        Glide.with(holder.imageView.context)
            .load(uri)
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.imageView)
        holder.removeButton.setOnClickListener {
            onImageRemove(item)
        }
    }
}