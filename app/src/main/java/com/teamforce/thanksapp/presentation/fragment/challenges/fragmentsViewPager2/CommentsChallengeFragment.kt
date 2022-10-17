package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCommentsChallengeBinding
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import com.teamforce.thanksapp.utils.Consts


class CommentsChallengeFragment : Fragment(R.layout.fragment_comments_challenge) {

    private val binding: FragmentCommentsChallengeBinding by viewBinding()
  //  private val viewModel: CommentsChallengeViewModel by viewModels()

    private var idChallenge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengesFragment.CHALLENGER_ID)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            CommentsChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ChallengesFragment.CHALLENGER_ID, challengeId)
                }
            }
    }
}