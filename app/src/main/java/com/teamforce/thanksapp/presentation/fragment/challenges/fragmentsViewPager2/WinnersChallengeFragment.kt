package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.databinding.FragmentWinnersChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.ContendersAdapter
import com.teamforce.thanksapp.presentation.adapter.WinnersAdapter
import com.teamforce.thanksapp.presentation.adapter.decorators.HorizontalDividerItemDecoration
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import com.teamforce.thanksapp.presentation.viewmodel.WinnersChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WinnersChallengeFragment : Fragment(R.layout.fragment_winners_challenge) {

    private val binding: FragmentWinnersChallengeBinding by viewBinding()

    private val viewModel: WinnersChallengeViewModel by viewModels()
    private var idChallenge: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengesFragment.CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = WinnersAdapter()
        binding.winnersRv.adapter = adapter
        binding.winnersRv.addItemDecoration(
            HorizontalDividerItemDecoration(16, adapter.itemCount)
        )
        loadWinners()
        setData()
        listeningResponse()

    }

    private fun loadWinners(){
        idChallenge?.let { viewModel.loadWinners(it) }
    }

    private fun setData(){
        viewModel.winners.observe(viewLifecycleOwner) {
            (binding.winnersRv.adapter as WinnersAdapter).submitList(it)
        }
    }

    private fun listeningResponse(){
        viewModel.winnersError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }
}