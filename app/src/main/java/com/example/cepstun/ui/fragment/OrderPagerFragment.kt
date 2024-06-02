package com.example.cepstun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.databinding.FragmentOrderPagerBinding
import com.example.cepstun.viewModel.OrderPagerViewModel
import com.example.cepstun.viewModel.ViewModelFactory

class OrderPagerFragment : Fragment() {

    private var _binding: FragmentOrderPagerBinding? = null
    private val binding get() = _binding!!


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

//    private lateinit var adapter: orderAdapter
    private lateinit var recyclerView: RecyclerView

    private var position: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            position = it.getInt(ARG_SECTION_NUMBER)
        }

        recyclerView = binding.RVOrder

        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        recyclerView.addItemDecoration(itemDecoration)


//        //declaration of adapter
//        adapter = UserAdapter()
//
//
        // for tab with different data
        if (position == 1) {
//            viewModel.getCusOnGoing().observe(viewLifecycleOwner) {
//                setDataOrder(it)
//            }
            binding.TVEmpty.text = "OnGoing Kosong"
        } else {
            viewModel.getCusHistory().observe(viewLifecycleOwner) {
//                setDataOrder(it)
            }
            binding.TVEmpty.text = "History Kosong"
        }
    }

    // set data from viewModel to adapter
//    private fun setDataOrder(data: List<OnGoCustomer>) {
//        adapter.setUserList(data)
//    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}