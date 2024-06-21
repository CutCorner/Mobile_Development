package com.example.cepstun.ui.adapter.barbershop

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.databinding.ItemImageBinding

class AddImageAdapter(private val onImageRemove: (Uri) -> Unit) :
    ListAdapter<Uri, AddImageAdapter.ImageViewHolder>(UriDiffCallback) {

    object UriDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
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
        val uri = getItem(position)
        holder.imageView.setImageURI(uri)
        holder.removeButton.setOnClickListener {
            onImageRemove(uri)
        }
    }
}