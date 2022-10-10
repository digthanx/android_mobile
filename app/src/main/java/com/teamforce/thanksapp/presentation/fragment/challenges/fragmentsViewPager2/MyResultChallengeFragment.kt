package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.databinding.FragmentMyResultChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment


class MyResultChallengeFragment : Fragment(R.layout.fragment_my_result_challenge) {

    private val binding: FragmentMyResultChallengeBinding by viewBinding()

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

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            MyResultChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ChallengesFragment.CHALLENGER_ID, challengeId)
                }
            }
    }
}