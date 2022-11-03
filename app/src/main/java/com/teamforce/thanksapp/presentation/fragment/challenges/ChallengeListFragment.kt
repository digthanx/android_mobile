package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengeListBinding
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengePagerAdapter
import com.teamforce.thanksapp.presentation.adapter.decorators.VerticalDividerDecoratorForListWithBottomBar
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.challenge.ChallengesViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ChallengeListFragment : Fragment(R.layout.fragment_challenge_list) {

    private val binding: FragmentChallengeListBinding by viewBinding()
    private val viewModel: ChallengesViewModel by viewModels()

    private var activeOnly: Int? = null
    private var listAdapter: ChallengePagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            activeOnly = it.getInt(ACTIVE_ONLY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAdapter()
        activeOnly?.let { loadChallenges(it) }

    }

    private fun initView() {
        binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
    }

    private fun initAdapter(){
        listAdapter = ChallengePagerAdapter()

        binding.challengeRv.apply {
            this.adapter = listAdapter?.withLoadStateHeaderAndFooter(
                header = HistoryLoadStateAdapter(),
                footer = HistoryLoadStateAdapter()
            )
            this.addItemDecoration(VerticalDividerDecoratorForListWithBottomBar(8, listAdapter!!.itemCount))
        }

        listAdapter?.addLoadStateListener { state ->
            binding.swipeRefreshLayout.isRefreshing = state.refresh == LoadState.Loading
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            listAdapter?.refresh()
            binding.swipeRefreshLayout.isRefreshing = true
        }

        listAdapter?.onChallengeClicked = { challengeId: Int ->
            val bundle = Bundle()
            bundle.apply {
                putInt(ChallengesConsts.CHALLENGER_ID, challengeId)
            }
            findNavController().navigate(
                R.id.action_challengesFragment_to_detailsMainChallengeFragment,
                bundle,
                OptionsTransaction().optionForEditProfile
            )
        }

        listAdapter?.onCreatorOfChallengeClicked = {creatorId ->
            val bundle = Bundle()
            bundle.putInt(Consts.USER_ID, creatorId)
            view?.findNavController()?.navigate(
                R.id.action_global_someonesProfileFragment,
                bundle,
                OptionsTransaction().optionForTransactionWithSaveBackStack
            )
        }

    }

    private fun loadChallenges(activeOnly: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            if(activeOnly == 1){
                binding.swipeRefreshLayout.isRefreshing = false
                viewModel.acitveChallenge.collectLatest { challenges ->
                    listAdapter?.submitData(challenges)
                }
            }else{
                binding.swipeRefreshLayout.isRefreshing = false
                viewModel.allChallenge.collectLatest { challenges ->
                    listAdapter?.submitData(challenges)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.challengeRv.adapter = null
    }

    companion object {
        private const val ACTIVE_ONLY = "active_only"

        @JvmStatic
        fun newInstance(activeOnly: Int) =
            ChallengeListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACTIVE_ONLY, activeOnly)
                }
            }
    }
}