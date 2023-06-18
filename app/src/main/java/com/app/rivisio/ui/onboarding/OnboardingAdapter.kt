package com.app.rivisio.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.rivisio.R

class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        var fragment: Fragment? = null

        fragment = when (position) {
            0 -> {
                OnboardingFragment.newInstance(
                    "Magical \nMemory ?",
                    "What if you had the power to remember anything you learn for however long you want to?",
                    R.drawable.onboard_1,
                    R.color.onboard_1
                )
            }
            1 -> {
                OnboardingFragment.newInstance(
                    "Reality \nCheck ?",
                    "Definitely Yes Does it require a super memory? Strictly No",
                    R.drawable.onboard_2,
                    R.color.onboard_2
                )
            }
            else -> {
                OnboardingFragment.newInstance(
                    "Spaced \nRepetition !",
                    "Through our intelligent algorithm which paces your revisions at frequencies proven for maximum retention.",
                    R.drawable.onboard_3,
                    R.color.onboard_3
                )
            }
        }

        return fragment
    }
}