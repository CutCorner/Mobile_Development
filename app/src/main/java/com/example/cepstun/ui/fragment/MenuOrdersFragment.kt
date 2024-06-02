package com.example.cepstun.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cepstun.R
import com.example.cepstun.databinding.FragmentMenuOrdersBinding
import com.example.cepstun.ui.adapter.SectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class MenuOrdersFragment : Fragment() {

    private var _binding: FragmentMenuOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // connect viewpager2 with adapter
        val sectionPagerAdapter = SectionsPagerAdapter(requireActivity() as AppCompatActivity)
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionPagerAdapter

        // connect viewpager2 with TabLayoutMediator
        val tabs = binding.TLMenuOrders
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