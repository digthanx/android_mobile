package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.ContendersAdapter
import com.teamforce.thanksapp.presentation.adapter.decorators.VerticalDividerItemDecorator
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContendersChallengeFragment : Fragment(R.layout.fragment_contenders_challenge) {

    private val binding: FragmentContendersChallengeBinding by viewBinding()

    private val viewModel: ContendersChallengeViewModel by viewModels()

    private var idChallenge: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ContendersAdapter()
        binding.contendersRv.adapter = adapter
        binding.contendersRv.addItemDecoration(VerticalDividerItemDecorator(16, adapter.itemCount))
        loadParticipants()
        setData()
        adapter.applyClickListener = {reportId: Int, state: Char ->
            viewModel.checkReport(reportId, state)
        }
        adapter.refuseClickListener = {reportId: Int, state: Char ->
            viewModel.checkReport(reportId, state)
        }
        listeningResponse()

    }

    private fun listeningResponse(){
        viewModel.contendersError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        viewModel.isSuccessOperation.observe(viewLifecycleOwner){
            if(it.successResult)
                if(it.state == 'W'){ Toast.makeText(requireContext(),
                    requireContext().getString(R.string.applyCheckReport),
                    Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.deniedCheckReport),
                        Toast.LENGTH_LONG).show()
                }
            loadParticipants()
            setData()
        }
    }

    private fun loadParticipants(){
        idChallenge?.let { viewModel.loadContenders(it) }
    }

    private fun setData(){
        viewModel.contenders.observe(viewLifecycleOwner) {
            (binding.contendersRv.adapter as ContendersAdapter).submitList(it)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            ContendersChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ChallengesFragment.CHALLENGER_ID, challengeId)
                }
            }
    }


}