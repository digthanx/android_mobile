package com.teamforce.thanksapp.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.ContendersChallengeFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.DetailsInnerChallengeFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.MyResultChallengeFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.WinnersChallengeFragment

class FragmentDetailChallengeStateAdapter(
    fragment: FragmentActivity,
    val creatorId: Int,
    val profileId: Int,
    val myResultWasReceivedSuccessfully: Boolean
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return if (profileId == creatorId && myResultWasReceivedSuccessfully) {
            4
        } else if (profileId == creatorId) {
            4
        } else if (myResultWasReceivedSuccessfully) {
            3
        } else {
            2
        }
    }

    private var challengeId: Int = 0

    public fun setChallengeId(data: Int) {
        challengeId = data
    }

    override fun createFragment(position: Int): Fragment {
        if (creatorId == profileId && myResultWasReceivedSuccessfully) {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)

                1 -> WinnersChallengeFragment.newInstance(challengeId)
                2 -> ContendersChallengeFragment.newInstance(challengeId)
                3 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    DetailsInnerChallengeFragment.newInstance(challengeId)
                }
            }
        } else if (creatorId == profileId) {
            // Пока что если ты создать
            // я всегда буду показывать мой результат пока от бека нет поля
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> WinnersChallengeFragment.newInstance(challengeId)
                2 -> ContendersChallengeFragment.newInstance(challengeId)
                3 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    DetailsInnerChallengeFragment.newInstance(challengeId)
                }
            }
        } else if (myResultWasReceivedSuccessfully) {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> WinnersChallengeFragment.newInstance(challengeId)
                2 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    DetailsInnerChallengeFragment.newInstance(challengeId)
                }
            }
        } else {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> WinnersChallengeFragment.newInstance(challengeId)
                else -> {
                    DetailsInnerChallengeFragment.newInstance(challengeId)
                }
            }
        }
    }

}