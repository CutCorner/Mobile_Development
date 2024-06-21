package com.example.cepstun.ui.adapter.barbershop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cepstun.ui.fragment.barbershop.HistoryPagerFragment

class SectionsPagerAdapter2(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {

        val fragment = HistoryPagerFragment()

        fragment.arguments = Bundle().apply {
            putInt(HistoryPagerFragment.ARG_SECTION_NUMBER, position + 1)
        }

        return fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}
