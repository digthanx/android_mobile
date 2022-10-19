package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentWinnersChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.WinnersAdapter
import com.teamforce.thanksapp.presentation.adapter.decorators.VerticalDividerItemDecorator
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_WINNER
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.WinnersChallengeViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WinnersChallengeFragment : Fragment(R.layout.fragment_winners_challenge) {

    private val binding: FragmentWinnersChallengeBinding by viewBinding()

    private val viewModel: WinnersChallengeViewModel by viewModels()
    private var idChallenge: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = WinnersAdapter()
        binding.winnersRv.adapter = adapter
        binding.winnersRv.addItemDecoration(
            VerticalDividerItemDecorator(16, adapter.itemCount)
        )
        if (idChallenge != null){
            adapter.onWinnerClicked = { dataOfWinner ->
                val bundle = Bundle()
                with(bundle){
                    putInt(CHALLENGER_ID, idChallenge!!)
                    putParcelable(CHALLENGER_WINNER, dataOfWinner)
                }
                view.findNavController().navigate(
                    R.id.action_global_challengesWinnersDetailFragment,
                    bundle,
                    OptionsTransaction().optionForAdditionalInfoFeedFragment)
            }
        }

        loadWinners()
        setData()
        listeningResponse()

    }

    private fun loadWinners(){
        idChallenge?.let { viewModel.loadWinners(it) }
    }

    private fun setData(){
        viewModel.winners.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                binding.noWinners.visibility = View.GONE
                (binding.winnersRv.adapter as WinnersAdapter).submitList(it)
            }else{
                binding.noWinners.visibility = View.VISIBLE
            }
        }
    }

    private fun listeningResponse(){
        viewModel.winnersError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadWinners()
        setData()
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            WinnersChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }
}