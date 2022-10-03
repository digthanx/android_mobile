package com.teamforce.thanksapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.DetailsInnerChallengeFragment

class FragmentDetailChallengeStateAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> DetailsInnerChallengeFragment()
            else -> {
                DetailsInnerChallengeFragment()
            }
        }
    }

}