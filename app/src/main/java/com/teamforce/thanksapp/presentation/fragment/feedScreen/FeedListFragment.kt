package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentFeedListBinding
import com.teamforce.thanksapp.presentation.adapter.feed.NewFeedAdapter
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.feed.FeedListViewModel
import com.teamforce.thanksapp.utils.ViewLifecycleDelegate
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.feedNew.collectLatest {
                binding.refreshLayout.isRefreshing = false
                listAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        binding.list.adapter = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FeedListFragment()
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