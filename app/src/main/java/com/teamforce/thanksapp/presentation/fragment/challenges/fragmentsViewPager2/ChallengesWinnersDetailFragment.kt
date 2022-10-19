package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesWinnersDetailBinding
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import com.teamforce.thanksapp.presentation.viewmodel.WinnersDetailChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengesWinnersDetailFragment : Fragment(R.layout.fragment_challenges_winners_detail) {

    private val binding: FragmentChallengesWinnersDetailBinding by viewBinding()
    private val viewModel: WinnersDetailChallengeViewModel by viewModels()

    private var challengeId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    companion object {

        @JvmStatic
        fun newInstance(idChallenge: String) =
            ChallengesWinnersDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(CHALLENGER_ID, idChallenge)
                }
            }
    }
}