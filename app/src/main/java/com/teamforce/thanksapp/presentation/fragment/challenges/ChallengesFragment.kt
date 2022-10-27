package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengeAdapter
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengePagerAdapter
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.challenge.ChallengesViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengesFragment : Fragment(R.layout.fragment_challenges) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentChallengesBinding by viewBinding()
    private val viewModel: ChallengesViewModel by viewModels()

    private val navController: NavController by lazy { findNavController() }

    private var listAdapter: ChallengePagerAdapter? = null

    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAdapter()
        loadChallenges()

        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_challengesFragment_to_profileGraph, null,
                OptionsTransaction().optionForProfileFragment
            )
        }

        binding.createBtn.setOnClickListener {
            findNavController().navigateSafely(
                R.id.action_challengesFragment_to_createChallengeFragment,
                null,
                OptionsTransaction().optionForProfileFromEditProfile
            )

        }

        sharedViewModel.state.observe(viewLifecycleOwner) { notificationsCount ->
            if (notificationsCount == 0) {
                binding.apply {
                    activeNotifyLayout.gone()
                    notify.visible()
                }
            } else {
                binding.apply {
                    activeNotifyLayout.visible()
                    notify.gone()
                    notifyBadge.text = notificationsCount.toString()
                }
            }
        }

    }

    private fun initView() {
        binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.balanceFragment,
                R.id.historyFragment,
                R.id.challenge_graph
            )
        )
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    private fun initAdapter(){
        listAdapter = ChallengePagerAdapter()

        binding.challengeRv.apply {
            this.adapter = listAdapter?.withLoadStateHeaderAndFooter(
                header = HistoryLoadStateAdapter(),
                footer = HistoryLoadStateAdapter()
            )
        }

        listAdapter?.addLoadStateListener { state ->
            binding.swipeRefreshLayout.isRefreshing = state.refresh == LoadState.Loading
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            listAdapter?.refresh()
            binding.swipeRefreshLayout.isRefreshing = true
        }

        listAdapter?.onChallengeClicked = { dataOfChallenge ->
            val bundle = Bundle()
            bundle.apply {
                putParcelable(ChallengesConsts.CHALLENGER_DATA, dataOfChallenge)
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

    private fun loadChallenges(){
        viewLifecycleOwner.lifecycleScope.launch {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.allChallenge.collectLatest { challenges ->
                listAdapter?.submitData(challenges)
            }
        }
    }

}