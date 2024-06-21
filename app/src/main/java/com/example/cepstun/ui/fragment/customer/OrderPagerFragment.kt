package com.example.cepstun.ui.fragment.customer

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.BarberDataList
import com.example.cepstun.data.local.entity.customer.HistoryCustomer
import com.example.cepstun.databinding.FragmentOrderPagerBinding
import com.example.cepstun.ui.activity.customer.BarberLocationActivity
import com.example.cepstun.ui.adapter.customer.CustomerHistoryAdapter
import com.example.cepstun.viewModel.OrderPagerViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class OrderPagerFragment : Fragment() {

    private var _binding: FragmentOrderPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CustomerHistoryAdapter

    private lateinit var recyclerView: RecyclerView

    private var position: Int? = null


    private val viewModel: OrderPagerViewModel by viewModels {
        ViewModelFactory.getInstance(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentOrderPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_SECTION_NUMBER)
        }

        recyclerView = binding.RVOrder

        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager


        // for tab with different data
        if (position == 1) {
            adapter = CustomerHistoryAdapter(position!!)
            recyclerView.adapter = adapter

            val queue = viewModel.getQueue()
            if (queue.barberID != "" || queue.yourQueue != "" ) {
                binding.TVEmpty.visibility = View.GONE

                val barberData = BarberDataList.barberDataValue.find { it.id == queue.barberID }!!
                viewModel.observeQueue(queue.barberID, queue.yourQueue.toInt())
                viewModel.order.observe(viewLifecycleOwner) {
                    if (it != null) {
                        val data = HistoryCustomer (
                            id = 0,
                            idBarber = queue.barberID,
                            logoBarber = barberData.logo,
                            nameBarber = barberData.name,
                            modelBarber = it.model,
                            addOnBarber = it.addon,
                            status = it.proses,
                            priceBarber = it.price.toInt()
                        )
                        adapter.submitList(listOf(data))
                    } else {
                        adapter.submitList(emptyList())
                    }
                }

                adapter.setOnItemClickCallback(object : CustomerHistoryAdapter.OnOrderClickListener {

                    override fun onOrderClick(barberId: String) {
                        Intent(requireContext(), BarberLocationActivity::class.java).also {
                            it.putExtra(BarberLocationActivity.ID_BARBER, queue.barberID)
                            it.putExtra(BarberLocationActivity.YOUR_QUEUE, queue.yourQueue)
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }
                    }
                })
            } else {
                binding.TVEmpty.text = getString(R.string.ongoing_empty)
                binding.TVEmpty.visibility = View.VISIBLE
            }
        } else {
            adapter = CustomerHistoryAdapter(position!!)
            recyclerView.adapter = adapter

            viewModel.getCusHistory().observe(viewLifecycleOwner) {
                if (it.isEmpty()){
                    binding.apply {
                        TVEmpty.text = getString(R.string.history_empty)
                        TVEmpty.visibility = View.VISIBLE
                        RVOrder.visibility = View.GONE
                    }
                } else {
                    adapter.submitList(it)
                    binding.apply {
                        RVOrder.visibility = View.VISIBLE
                        TVEmpty.visibility = View.GONE
                    }

                    adapter.setOnItemClickCallback(object : CustomerHistoryAdapter.OnOrderClickListener {

                        override fun onOrderClick(barberId: String) {
                            viewModel.deleteCusHistory(barberId.toInt())
                        }
                    })
                }
            }
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}