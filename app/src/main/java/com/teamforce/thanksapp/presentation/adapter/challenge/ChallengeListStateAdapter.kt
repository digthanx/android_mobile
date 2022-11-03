package com.teamforce.thanksapp.presentation.adapter.challenge

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengeListFragment

class ChallengeListStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChallengeListFragment.newInstance(
                activeOnly = 0
            )
            1 -> ChallengeListFragment.newInstance(
                activeOnly = 1
            )
            else -> ChallengeListFragment.newInstance(
                activeOnly = 0
            )
        }
    }
}