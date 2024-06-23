package com.example.cepstun.ui.adapter.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.Model
import com.example.cepstun.databinding.ItemModelBarberBinding
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelBarberAdapter : ListAdapter<Model, ModelBarberAdapter.ModelViewHolder>(ModelDiffCallback()) {

    class ModelViewHolder(binding: ItemModelBarberBinding): RecyclerView.ViewHolder(binding.root) {
        val modelName = binding.TVModel
        val modelImage = binding.SIVModel
        val modelPrice = binding.TVPrice
        val delete = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = getItem(position)
        holder.modelName.text = model.name
        Glide.with(holder.itemView.context)
            .load(model.image.getFullImageUrl())
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.modelImage)
        GlobalScope.launch {
            val formattedPrice = model.price.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.modelPrice.text = formattedPrice
            }
        }
        holder.delete.visibility = View.GONE

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(model)
        }
    }

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(model: Model)
    }
}

class ModelDiffCallback : DiffUtil.ItemCallback<Model>() {
    override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
        return oldItem == newItem
    }
}