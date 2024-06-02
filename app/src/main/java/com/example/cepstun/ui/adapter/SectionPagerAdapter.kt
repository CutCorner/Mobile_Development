package com.example.cepstun.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cepstun.ui.fragment.OrderPagerFragment

class SectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {

        val fragment = OrderPagerFragment()

        fragment.arguments = Bundle().apply {
            putInt(OrderPagerFragment.ARG_SECTION_NUMBER, position + 1)
        }

        return fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}
