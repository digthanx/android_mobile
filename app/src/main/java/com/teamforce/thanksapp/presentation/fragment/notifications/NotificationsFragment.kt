package com.teamforce.thanksapp.presentation.fragment.notifications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentNotificationsBinding
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.adapter.notifications.NotificationPageAdapter
import com.teamforce.thanksapp.presentation.viewmodel.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private val binding: FragmentNotificationsBinding by viewBinding()

    private val viewModel: NotificationsViewModel by viewModels()
    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()
    private var listAdapter: NotificationPageAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.dropNotificationCounter()
        listAdapter = NotificationPageAdapter(
            onChallengeClicked = ::onChallengeClicked,
            onUserClicked = ::onUserClicked,
            onTransactionClicked = ::onTransactionClicked
        )

        binding.notificationsList.apply {
            adapter = listAdapter?.withLoadStateHeaderAndFooter(
                header = HistoryLoadStateAdapter(),
                footer = HistoryLoadStateAdapter(),

                )
            layoutManager = LinearLayoutManager(requireContext())
        }

        listAdapter?.addLoadStateListener { state ->
            binding.swipeRefreshLayout.isRefreshing = state.refresh == LoadState.Loading
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            listAdapter?.refresh()
            binding.swipeRefreshLayout.isRefreshing = true
        }

        binding.closeButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.notifications.collectLatest {
                listAdapter?.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        binding.notificationsList.adapter = null
        listAdapter = null
        super.onDestroyView()

    }

    private fun onUserClicked(userId: Int) {

    }

    private fun onTransactionClicked() {

    }

    private fun onChallengeClicked(challengeId: Int) {

    }
}