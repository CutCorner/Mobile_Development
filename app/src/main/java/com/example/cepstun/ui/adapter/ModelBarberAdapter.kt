package com.example.cepstun.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cepstun.R
import com.example.cepstun.data.local.Model
import com.example.cepstun.databinding.ItemModelBarberBinding
import com.example.cepstun.ui.activity.CheckoutActivity

class ModelBarberAdapter(private var barberId: String, private val modelList: List<Model>): RecyclerView.Adapter<ModelBarberAdapter.ModelViewHolder>() {
    class ModelViewHolder(binding: ItemModelBarberBinding): RecyclerView.ViewHolder(binding.root) {
        val modelName = binding.TVModel
        val modelImage = binding.SIVModel
        val modelPrice = binding.TVPrice
    }

    private val filteredModelData = modelList.filter { it.id == barberId }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModelViewHolder {
        val binding = ItemModelBarberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        var totalNames = 0
        for (model in filteredModelData) {
            if (position < totalNames + model.name.size) {
                val nameIndex = position - totalNames
                holder.modelName.text = model.name[nameIndex]
                Glide.with(holder.itemView.context)
                    .load(model.image[nameIndex])
                    .centerCrop()
                    .placeholder(R.drawable.logo_placeholder)
                    .into(holder.modelImage)
                holder.modelPrice.text = model.price[nameIndex].toString()

                holder.itemView.setOnClickListener {
                    val clickedModel = Model(
                        id = model.id,
                        name = listOf(model.name[nameIndex]),
                        image = listOf(model.image[nameIndex]),
                        price = listOf(model.price[nameIndex])
                    )
                    onItemClickCallback.onItemClicked(clickedModel)
                }

                return
            }
            totalNames += model.name.size

//            holder.itemView.setOnClickListener {
//                Intent(holder.itemView.context, CheckoutActivity::class.java).also { intent ->
//                    intent.putExtra(CheckoutActivity.SELECTED_MODEL, filteredModelData[position])
//                    intent.putExtra(CheckoutActivity.SELECTED_BARBER, )
//                    holder.itemView.context.startActivity(intent)
//                }
//            }

        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(model: Model)
    }

    override fun getItemCount(): Int {
        return filteredModelData.sumOf { it.name.size }
    }
}