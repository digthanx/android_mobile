package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentFeedListBinding
import com.teamforce.thanksapp.presentation.adapter.feed.NewFeedAdapter
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts
import com.teamforce.thanksapp.presentation.viewmodel.feed.FeedListViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.ViewLifecycleDelegate
import com.teamforce.thanksapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedListFragment : Fragment(R.layout.fragment_feed_list) {

    private val binding: FragmentFeedListBinding by viewBinding()
    private val viewModel: FeedListViewModel by viewModels()
    private val listAdapter by ViewLifecycleDelegate {
        NewFeedAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = FeedCategory.valueOf(arguments?.getString("category")!!)

        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            this.adapter = listAdapter.withLoadStateHeaderAndFooter(
                header = HistoryLoadStateAdapter(),
                footer = HistoryLoadStateAdapter()
            )
        }

        listAdapter.addLoadStateListener { state ->
            binding.refreshLayout.isRefreshing = state.refresh == LoadState.Loading
        }

        binding.refreshLayout.setOnRefreshListener {
            listAdapter.refresh()
            binding.refreshLayout.isRefreshing = true
        }

        when (category) {
            FeedCategory.all -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.all.collectLatest {
                        binding.refreshLayout.isRefreshing = false
                        listAdapter.submitData(it)
                    }
                }
            }
            FeedCategory.challenges -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.challenges.collectLatest {
                        binding.refreshLayout.isRefreshing = false
                        listAdapter.submitData(it)
                    }
                }
            }
            FeedCategory.transactions -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.transactions.collectLatest {
                        binding.refreshLayout.isRefreshing = false
                        listAdapter.submitData(it)
                    }
                }
            }
            FeedCategory.winners -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.winners.collectLatest {
                        binding.refreshLayout.isRefreshing = false
                        listAdapter.submitData(it)
                    }
                }
            }
        }
        listAdapter.onTransactionClicked = { transactionId ->
            val bundle = Bundle()
            bundle.putInt(Consts.TRANSACTION_ID, transactionId)
            //TODO Добавить запрос в бд и установку данных переделать под данные с бека
            view.findNavController()
                .navigate(
                    R.id.action_global_additionalInfoFeedItemFragment,
                    bundle,
                    OptionsTransaction().optionForAdditionalInfoFeedFragment
                )
        }

        listAdapter.onChallengeClicked = { challengeId: Int ->
            val bundle = Bundle()
            bundle.putInt(ChallengesConsts.CHALLENGER_ID, challengeId)
            //TODO В детали челленджа добавить новый запрос и установку данных с другой модели
            view.findNavController().navigateSafely(
                R.id.action_global_detailsMainChallengeFragment,
                bundle,
                OptionsTransaction().optionForAdditionalInfoFeedFragment
            )
            // Добавить в модель победителя challengeId когда бек добавит
        }

        listAdapter.onSomeonesClicked = { userId: Int ->
            val bundle = Bundle()
            bundle.putInt(Consts.USER_ID, userId)
            view.findNavController().navigate(
                R.id.action_global_someonesProfileFragment,
                bundle,
                OptionsTransaction().optionForAdditionalInfoFeedFragment
            )
        }

    }

    override fun onDestroyView() {
        binding.list.adapter = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(
            feedCategory: FeedCategory
        ) = FeedListFragment().apply {
            arguments = Bundle().apply {
                putString("category", feedCategory.name)
            }
        }
    }

//    private fun onLikeClicked(item: FeedResponse, position: Int) {
//        listAdapter.like(position)
//    }
//
//    private fun onDislikeClicked(item: FeedResponse, position: Int) {
//        listAdapter.dislike(position)
//    }
}

enum class FeedCategory {
    all,
    transactions,
    winners,
    challenges
}