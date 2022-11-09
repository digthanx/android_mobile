package com.teamforce.thanksapp.presentation.adapter.challenge

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2.*

class FragmentDetailChallengeStateAdapter(
    fragment: FragmentActivity,
    val creatorId: Int,
    val profileId: Int,
    val myResultWasReceivedSuccessfully: Boolean
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return if (profileId == creatorId && myResultWasReceivedSuccessfully) {
            5
        } else if (profileId == creatorId) {
            5
        } else if (myResultWasReceivedSuccessfully) {
            4
        } else {
            3
        }
    }

    private var challengeId: Int = 0

    fun setChallengeId(data: Int) {
        challengeId = data
    }

    override fun createFragment(position: Int): Fragment {
        if (creatorId == profileId && myResultWasReceivedSuccessfully) {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> CommentsChallengeFragment.newInstance(challengeId)
                2 -> WinnersChallengeFragment.newInstance(challengeId)
                3 -> ContendersChallengeFragment.newInstance(challengeId)
                4 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    MyResultChallengeFragment.newInstance(challengeId)
                }
            }
        } else if (creatorId == profileId) {
            // Пока что если ты создать
            // я всегда буду показывать мой результат пока от бека нет поля
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> CommentsChallengeFragment.newInstance(challengeId)
                2 -> WinnersChallengeFragment.newInstance(challengeId)
                3 -> ContendersChallengeFragment.newInstance(challengeId)
                4 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    MyResultChallengeFragment.newInstance(challengeId)
                }
            }
        } else if (myResultWasReceivedSuccessfully) {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> CommentsChallengeFragment.newInstance(challengeId)
                2 -> WinnersChallengeFragment.newInstance(challengeId)
                3 -> MyResultChallengeFragment.newInstance(challengeId)
                else -> {
                    MyResultChallengeFragment.newInstance(challengeId)
                }
            }
        } else {
            return when (position) {
                0 -> DetailsInnerChallengeFragment.newInstance(challengeId)
                1 -> CommentsChallengeFragment.newInstance(challengeId)
                2 -> WinnersChallengeFragment.newInstance(challengeId)
                else -> {
                    MyResultChallengeFragment.newInstance(challengeId)
                }
            }
        }
    }

}