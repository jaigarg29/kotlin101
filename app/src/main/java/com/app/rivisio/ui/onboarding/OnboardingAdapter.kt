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
                    "Memorize Anything Permanently",
                    "That is, you learn it once and remember forever.",
                    R.drawable.onboard_1,
                    R.color.onboard_1
                )
            }
            1 -> {
                OnboardingFragment.newInstance(
                    "Is it really possible?",
                    "It is possible by using Spaced Repetition method, empirically established by German scientist Hermann Ebbinghaus.",
                    R.drawable.onboard_2,
                    R.color.onboard_2
                )
            }
            else -> {
                OnboardingFragment.newInstance(
                    "How we help you?",
                    "Our algorithm plans your revisions spaced at intervals proven for maximum retention.",
                    R.drawable.onboard_3,
                    R.color.onboard_3
                )
            }
        }

        return fragment
    }
}