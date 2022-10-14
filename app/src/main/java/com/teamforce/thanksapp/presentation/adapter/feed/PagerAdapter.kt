package com.teamforce.thanksapp.presentation.adapter.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.feedScreen.FeedListFragment

class PagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FeedListFragment.newInstance(mine = -1, public = -1)
            1 -> FeedListFragment.newInstance(mine = -1, public = -1)
            2 -> FeedListFragment.newInstance(mine = -1, public = -1)
            else -> Fragment()
        }
    }
}