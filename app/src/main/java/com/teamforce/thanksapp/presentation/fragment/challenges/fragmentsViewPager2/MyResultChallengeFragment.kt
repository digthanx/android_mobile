package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMyResultChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.challenge.ResultChallengeAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.viewmodel.challenge.MyResultChallengeViewModel
import com.teamforce.thanksapp.utils.showDialogAboutDownloadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyResultChallengeFragment : Fragment(R.layout.fragment_my_result_challenge) {

    private val binding: FragmentMyResultChallengeBinding by viewBinding()

    private val viewModel: MyResultChallengeViewModel by viewModels()

    private var idChallenge: Int? = null
    private var listAdapter: ResultChallengeAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ResultChallengeAdapter()
        binding.listOfResults.adapter = listAdapter
        loadMyResult()
        listeners()
        listAdapter?.onImageLongClicked = { clickedView, photo ->
            showDialogAboutDownloadImage(photo, clickedView, requireContext(), lifecycleScope)
        }
    }

    private fun loadMyResult(){
        idChallenge?.let {
            viewModel.loadChallengeResult(it)
        }
    }

    private fun listeners(){
        viewModel.myResultError.observe(viewLifecycleOwner){
            binding.noData.visibility = View.VISIBLE
        }

        viewModel.myResult.observe(viewLifecycleOwner){
            (binding.listOfResults.adapter as ResultChallengeAdapter).submitList(it?.reversed())
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            MyResultChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }
}