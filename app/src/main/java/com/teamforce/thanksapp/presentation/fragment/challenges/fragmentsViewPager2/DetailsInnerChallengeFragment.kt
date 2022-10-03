package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentDetailsInnerChallengeBinding
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.ChallengeAdapter
import com.teamforce.thanksapp.presentation.viewmodel.DetailsInnerChallengerViewModel


class DetailsInnerChallengeFragment : Fragment(R.layout.fragment_details_inner_challenge) {

    private val binding: FragmentDetailsInnerChallengeBinding by viewBinding()
    private val viewModel = DetailsInnerChallengerViewModel()

    private var idChallenge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengeAdapter.CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initViewModel()
        loadChallengeData(idChallenge)
    }

    private fun loadChallengeData(challengeId: Int?){
        challengeId?.let {
            viewModel.loadChallenge(it)
        }
    }

    private fun setDataAboutChallenge(){

    }
}