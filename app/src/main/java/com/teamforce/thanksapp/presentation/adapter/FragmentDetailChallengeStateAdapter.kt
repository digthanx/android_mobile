package com.teamforce.thanksapp.presentation.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.CommentsChallengeFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.ContendersChallengeFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.DetailsInnerChallengeFragment

class FragmentDetailChallengeStateAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3
    private var challengeId: Int = 0

    public fun setChallengeId(data: Int){
        challengeId = data
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.apply {
            putInt(ChallengeAdapter.CHALLENGER_ID, challengeId)
        }
        return when(position) {
            0 -> {
                val fragment = DetailsInnerChallengeFragment()
                fragment.arguments = bundle
                return fragment
            }
            1 -> CommentsChallengeFragment()
            2 -> {
               val fragment = ContendersChallengeFragment()
                fragment.arguments = bundle
                return fragment
            }
            else -> {
                DetailsInnerChallengeFragment()
            }
        }
    }

}