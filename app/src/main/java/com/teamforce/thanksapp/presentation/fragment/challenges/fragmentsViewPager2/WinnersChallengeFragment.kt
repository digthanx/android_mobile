package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WinnersChallengeFragment : Fragment(R.layout.fragment_winners_challenge) {

    private val binding: FragmentContendersChallengeBinding by viewBinding()

    //private val viewModel: WinnersChallengeViewModel by viewModels()
    private var idChallenge: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengesFragment.CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}