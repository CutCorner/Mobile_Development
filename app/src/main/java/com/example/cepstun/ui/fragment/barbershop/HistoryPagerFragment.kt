package com.example.cepstun.ui.fragment.barbershop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.data.local.entity.barbershop.HistoryBarbershop
import com.example.cepstun.databinding.FragmentHistoryPagerBinding
import com.example.cepstun.ui.adapter.barbershop.BarbershopHistoryAdapter
import com.example.cepstun.viewModel.OrderPagerViewModel
import com.example.cepstun.viewModel.ViewModelFactory

//import com.example.cepstun.ui.adapter.customer.CustomerHistoryAdapter

class HistoryPagerFragment : Fragment() {

    private var _binding: FragmentHistoryPagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BarbershopHistoryAdapter

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
        _binding = FragmentHistoryPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_SECTION_NUMBER)
        }

        recyclerView = binding.RVHistory

        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager


        // for tab with different data
        if (position == 1) {
            adapter = BarbershopHistoryAdapter()
            recyclerView.adapter = adapter

            viewModel.getFirstBooked()
            viewModel.listOrder.observe(viewLifecycleOwner){
                if (it != null){
                    binding.TVEmpty.visibility = View.GONE

                    val data = HistoryBarbershop (
                        id = 0,
                        userId = it.userId,
                        nameCustomer = it.name,
                        positionCustomer = it.position,
                        modelCustomer = it.model,
                        addOnCustomer = it.addon,
                        price = it.price.toInt(),
                        status = "Berjalan Saat Ini"
                    )
                    adapter.submitList(listOf(data))


//                    adapter.setOnItemClickCallback(object : CustomerHistoryAdapter.OnOrderClickListener {
//
//                        override fun onOrderClick(barberId: String) {
//                            Intent(requireContext(), BarberLocationActivity::class.java).also {
//                                it.putExtra(BarberLocationActivity.ID_BARBER, queue.barberID)
//                                it.putExtra(BarberLocationActivity.YOUR_QUEUE, queue.yourQueue)
//                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                startActivity(it)
//                            }
//                        }
//                    })
                } else {
                    binding.apply {
                        TVEmpty.text = getString(R.string.ongoing_empty)
                        TVEmpty.visibility = View.VISIBLE
                        RVHistory.visibility = View.GONE
                    }
                }
            }
        } else {
            adapter = BarbershopHistoryAdapter()
            recyclerView.adapter = adapter

            viewModel.getBarHistory().observe(viewLifecycleOwner) {
                if (it.isEmpty()){
                    binding.apply {
                        TVEmpty.text = getString(R.string.history_empty)
                        TVEmpty.visibility = View.VISIBLE
                        RVHistory.visibility = View.GONE
                    }
                } else {
                    adapter.submitList(it)
                    binding.apply {
                        RVHistory.visibility = View.VISIBLE
                        TVEmpty.visibility = View.GONE
                    }
//
//                    adapter.setOnItemClickCallback(object : CustomerHistoryAdapter.OnOrderClickListener {
//
//                        override fun onOrderClick(barberId: String) {
//                            viewModel.deleteCusHistory(barberId.toInt())
//                        }
//                    })
                }
            }
        }
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number2"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}