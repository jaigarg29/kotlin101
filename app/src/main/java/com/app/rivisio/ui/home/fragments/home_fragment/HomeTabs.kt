package com.app.rivisio.ui.home.fragments.home_fragment

import android.view.View
import com.app.rivisio.databinding.FragmentHomeBinding

class HomeTabs(private val binding: FragmentHomeBinding) {

    fun setTabSelected(position: Int) {

        binding.tabToday.isSelected = false
        binding.tabMissed.isSelected = false
        binding.tabUpcoming.isSelected = false

        when (position) {
            0 -> {
                binding.tabToday.isSelected = true
            }
            1 -> {
                binding.tabMissed.isSelected = true
            }
            2 -> {
                binding.tabUpcoming.isSelected = true
            }
        }
    }

    fun setTabClickListener(listener: View.OnClickListener) {
        binding.tabToday.setOnClickListener(listener)
        binding.tabMissed.setOnClickListener(listener)
        binding.tabUpcoming.setOnClickListener(listener)
    }
}