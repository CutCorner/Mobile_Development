package com.example.cepstun.ui.activity.customer

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.databinding.ActivityNotificationBinding
import com.example.cepstun.ui.adapter.customer.NotificationAdapter
import com.example.cepstun.viewModel.NotificationViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    private val viewModel: NotificationViewModel by viewModels {
        ViewModelFactory.getInstance(this.application)
    }

    private lateinit var adapter: NotificationAdapter

    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.RVNotification
        val layoutManager: RecyclerView.LayoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, (layoutManager as LinearLayoutManager).orientation)
        recyclerView.addItemDecoration(itemDecoration)

        adapter = NotificationAdapter()
        recyclerView.adapter = adapter

        viewModel.getNotification().observe(this) {
            if (it.isEmpty()) {
                binding.apply {
                    TVEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } else {
                adapter.submitList(it)
                binding.apply {
                    recyclerView.visibility = View.VISIBLE
                    TVEmpty.visibility = View.GONE
                }

                adapter.setOnItemClickCallback(object :
                    NotificationAdapter.OnNotificationClickListener {

                    override fun onNotificationClick(barberId: String) {
                        viewModel.deleteNotification(barberId.toInt())
                    }
                })
            }
        }

        binding.IBBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}