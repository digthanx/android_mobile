package com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentDetailsInnerFeedBinding
import com.teamforce.thanksapp.databinding.FragmentReactionsFeedBinding
import com.teamforce.thanksapp.presentation.adapter.feed.FeedReactionsPageAdapter
import com.teamforce.thanksapp.presentation.viewmodel.feed.DetailInnerFeedViewModel
import com.teamforce.thanksapp.presentation.viewmodel.feed.FragmentReactionsFeedViewModel
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReactionsFeedFragment : Fragment(R.layout.fragment_reactions_feed) {

    private val binding: FragmentReactionsFeedBinding by viewBinding()
    private val viewModel: FragmentReactionsFeedViewModel by viewModels()

    private var listAdapter: FeedReactionsPageAdapter? = null
    private var transactionId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getInt(TRANSACTION_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = FeedReactionsPageAdapter()
        binding.reactionsRv.adapter = listAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            transactionId?.let { transactionId ->
                viewModel.loadReactions(transactionId).collectLatest { reactions ->
                    listAdapter?.submitData(reactions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.reactionsRv.adapter = null
        listAdapter = null
    }

    companion object {

        @JvmStatic
        fun newInstance(transactionId: Int) =
            ReactionsFeedFragment().apply {
                arguments = Bundle().apply {
                    putInt(TRANSACTION_ID, transactionId)
                }
            }
    }
}