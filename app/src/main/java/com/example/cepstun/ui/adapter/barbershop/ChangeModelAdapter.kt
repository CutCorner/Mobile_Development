package com.example.cepstun.ui.adapter.barbershop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.remote.response.barber.ModelAccount
import com.example.cepstun.databinding.ItemModelBarberBinding
import com.example.cepstun.utils.getFullImageUrl
import com.example.cepstun.utils.withCurrencyFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangeModelAdapter : ListAdapter<ModelAccount, ChangeModelAdapter.ModelViewHolder>(ModelDiffCallback) {

    class ModelViewHolder(binding: ItemModelBarberBinding) : RecyclerView.ViewHolder(binding.root) {
        val nameModel = binding.TVModel
        val priceModel = binding.TVPrice
        val imageModel = binding.SIVModel
        val deleteModel = binding.IBDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = getItem(position)

        holder.nameModel.text = model.name
        GlobalScope.launch {
            val formattedPrice = model.price.toString().withCurrencyFormat(holder.itemView.context)
            withContext(Dispatchers.Main) {
                holder.priceModel.text = formattedPrice
            }
        }
        Glide.with(holder.itemView.context)
            .load(model.imgSrc?.getFullImageUrl())
            .centerCrop()
            .placeholder(R.drawable.logo_placeholder)
            .into(holder.imageModel)

        holder.deleteModel.setOnClickListener {
            onItemClickCallback.onDeleteClick(model.name)
        }
    }

    private lateinit var onItemClickCallback: OnChangeClickListener

    fun setOnItemClickCallback(onItemClickCallback: OnChangeClickListener) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnChangeClickListener {
        fun onDeleteClick(name: String)
    }

    companion object ModelDiffCallback : DiffUtil.ItemCallback<ModelAccount>() {
        override fun areItemsTheSame(oldItem: ModelAccount, newItem: ModelAccount) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ModelAccount, newItem: ModelAccount) =
            oldItem == newItem
    }
}