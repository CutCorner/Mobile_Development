package com.example.cepstun.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuHomeBarberBinding
import com.example.cepstun.ui.adapter.BarberAdapter


class MenuHomeBarberFragment : Fragment() {
    private var _binding: FragmentMenuHomeBarberBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

//    private lateinit var adapter: customerAdaper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHomeBarberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switcher.setOnCheckedChangeListener { checked ->
            if (checked){
                Toast.makeText(requireContext(), "Toko Buka", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(requireContext(), "Toko Tutup", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}