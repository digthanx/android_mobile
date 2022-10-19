package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment


class ChallengesWinnersDetailFragment : Fragment(R.layout.fragment_challenges_winners_detail) {

    private var challengeId: Int? = null


    companion object {

        @JvmStatic
        fun newInstance(idChallenge: String) =
            ChallengesWinnersDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ChallengesFragment.CHALLENGER_ID, idChallenge)
                }
            }
    }
}