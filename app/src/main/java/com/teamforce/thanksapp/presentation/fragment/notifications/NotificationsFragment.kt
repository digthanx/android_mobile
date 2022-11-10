package com.teamforce.thanksapp.presentation.fragment.notifications

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentNotificationsBinding
import com.teamforce.thanksapp.presentation.adapter.history.HistoryLoadStateAdapter
import com.teamforce.thanksapp.presentation.adapter.notifications.NotificationPageAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts
import com.teamforce.thanksapp.presentation.viewmodel.NotificationsViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.navigateSafely
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
        findNavController().navigate(
            R.id.action_global_someonesProfileFragment,
            bundleOf(Consts.USER_ID to userId),
            OptionsTransaction().optionForAdditionalInfoFeedFragment
        )
    }

    private fun onTransactionClicked(transactionId: Int, isReaction: Boolean?) {
        var page = 0
        if (isReaction != null) {
            page = if (isReaction) 2
            else 1
        }
        findNavController().navigate(
            R.id.action_notificationsFragment_to_additionalInfoFeedItemFragment2,
            bundleOf(
                Consts.TRANSACTION_ID to transactionId,
                Consts.NEEDED_PAGE_POSITION to page
            ),
            OptionsTransaction().optionForEditProfile

        )
    }

    private fun onChallengeClicked(challengeId: Int, isTabChangeRequired: Boolean) {
        findNavController().navigateSafely(
            R.id.action_global_detailsMainChallengeFragment,
            bundleOf(
                ChallengesConsts.CHALLENGER_ID to challengeId,
                if (isTabChangeRequired) Consts.NEEDED_PAGE_POSITION to 3 else Consts.NEEDED_PAGE_POSITION to 0
            ),
            OptionsTransaction().optionForAdditionalInfoFeedFragment
        )
    }
}