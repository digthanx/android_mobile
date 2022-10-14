package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.FragmentFeedListBinding
import com.teamforce.thanksapp.presentation.adapter.feed.FeedPageAdapter
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.feed.FeedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedListFragment() : Fragment(R.layout.fragment_feed_list) {

    private val binding: FragmentFeedListBinding by viewBinding()
    private val viewModel: FeedListViewModel by viewModels()
    private var listAdapter: FeedPageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mineOnly = arguments?.getInt(MINE_ONLY_KEY)
        val publicOnly = arguments?.getInt(PUBLIC_ONLY_KEY)

        listAdapter = FeedPageAdapter(viewModel.getUsername(), ::onLikeClicked, ::onDislikeClicked)

        binding.list.apply {
            this.adapter = listAdapter?.withLoadStateHeaderAndFooter(
                header = HistoryLoadStateAdapter(),
                footer = HistoryLoadStateAdapter()
            )
            layoutManager = LinearLayoutManager(requireContext())
        }

        listAdapter?.addLoadStateListener { state ->
            binding.refreshLayout.isRefreshing = state.refresh == LoadState.Loading
        }

        binding.refreshLayout.setOnRefreshListener {
            listAdapter?.refresh()
            binding.refreshLayout.isRefreshing = true
        }

        lifecycleScope.launch {
            viewModel.getFeed(
                mineOnly = mineOnly!!,
                publicOnly = publicOnly!!
            ).collect() {
                binding.refreshLayout.isRefreshing = false
                listAdapter?.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        binding.list.adapter = null
        listAdapter = null
        super.onDestroyView()
    }

    companion object {
        private const val MINE_ONLY_KEY = "mine_only"
        private const val PUBLIC_ONLY_KEY = "mine_only"

        @JvmStatic
        fun newInstance(
            mine: Int,
            public: Int
        ) = FeedListFragment().apply {
            arguments = bundleOf(
                MINE_ONLY_KEY to mine,
                PUBLIC_ONLY_KEY to PUBLIC_ONLY_KEY
            )
        }
    }

    private fun onLikeClicked(item: FeedResponse, position: Int) {

    }

    private fun onDislikeClicked(item: FeedResponse, position: Int) {

    }
}