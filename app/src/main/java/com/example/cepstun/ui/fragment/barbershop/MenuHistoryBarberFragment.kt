package com.example.cepstun.ui.fragment.barbershop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuHistoryBarberBinding
import com.example.cepstun.ui.adapter.barbershop.SectionsPagerAdapter2
import com.google.android.material.tabs.TabLayoutMediator

class MenuHistoryBarberFragment : Fragment() {

    private var _binding: FragmentMenuHistoryBarberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuHistoryBarberBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // connect viewpager2 with adapter
        val sectionPagerAdapter2 = SectionsPagerAdapter2(requireActivity() as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionPagerAdapter2

        // connect viewpager2 with TabLayoutMediator
        val tabs = binding.TLMenuHistory
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    companion object{
        private val TAB_TITLES = intArrayOf(
            R.string.on_going, R.string.history
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}