package com.example.cepstun.ui.activity.customer

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.databinding.ActivityFavoriteBinding
import com.example.cepstun.ui.adapter.customer.FavoriteAdapter
import com.example.cepstun.viewModel.FavoriteViewModel
import com.example.cepstun.viewModel.ViewModelFactory


class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: FavoriteAdapter
    private lateinit var recyclerView: RecyclerView

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvFavoriteUsers

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FavoriteAdapter()
        recyclerView.adapter = adapter

        setRecyclerView()

        binding.IBBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun setRecyclerView(){
        viewModel.getFavBarber().observe(this) {
            if (it.isNotEmpty()) {
                binding.textEmpty.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(it)

                adapter.setOnItemClickCallback(object : FavoriteAdapter.OnFavoriteClickListener {
                    override fun onFavoriteClick(barberId: String) {
                        viewModel.deleteCusHistory(barberId.toInt())
                    }
                })

            } else{
                binding.textEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }


    }
}
