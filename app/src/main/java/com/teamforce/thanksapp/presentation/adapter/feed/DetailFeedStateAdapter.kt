package com.teamforce.thanksapp.presentation.adapter.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.*
import com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2.CommentsFeedFragment
import com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2.DetailsInnerFeedFragment
import com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2.ReactionsFeedFragment

class DetailFeedStateAdapter(
    fragment: FragmentActivity
): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    private var transactionId: Int = 0


   fun setTransactionId(outsideTransactionId: Int?) {
        if (outsideTransactionId != null) {
            transactionId = outsideTransactionId
        }
    }

    override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DetailsInnerFeedFragment.newInstance(transactionId)
                1 -> CommentsFeedFragment.newInstance(transactionId)
                2 -> ReactionsFeedFragment.newInstance(transactionId)
                else -> {
                    DetailsInnerChallengeFragment.newInstance(transactionId)
                }
            }

    }
}