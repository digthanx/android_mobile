package com.teamforce.thanksapp.presentation.fragment.historyScreen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryListFragment.newInstance(
                sentOnly = -1,
                receivedOnly = -1
            )
            1 -> HistoryListFragment.newInstance(
                sentOnly = -1,
                receivedOnly = 1
            )

            else -> HistoryListFragment.newInstance(
                sentOnly = 1,
                receivedOnly = -1
            )
        }
    }
}