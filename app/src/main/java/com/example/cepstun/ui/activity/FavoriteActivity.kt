package com.example.cepstun.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityFavoriteBinding
//import com.example.cepstun.ui.adapter.FavoriteAdapter

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
//    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setupRecyclerView()
        loadFavorites()
    }

//    private fun setupRecyclerView() {
//        favoriteAdapter = FavoriteAdapter()
//        binding.rvFavoriteUsers.apply {
//            layoutManager = LinearLayoutManager(this@FavoriteActivity)
//            adapter = favoriteAdapter
//        }
//    }

    private fun loadFavorites() {
//        val favorites = FavoritesManager.getFavorites(this)
//        if (favorites.isNotEmpty()) {
//            binding.textEmpty.visibility = View.GONE
//            binding.rvFavoriteUsers.visibility = View.VISIBLE
//            favoriteAdapter.submitList(favorites)
//        } else {
//            binding.textEmpty.visibility = View.VISIBLE
//            binding.rvFavoriteUsers.visibility = View.GONE
//        }
    }
}
