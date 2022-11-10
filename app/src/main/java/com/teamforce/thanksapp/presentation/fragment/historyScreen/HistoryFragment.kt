package com.teamforce.thanksapp.presentation.fragment.historyScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentHistoryBinding
import com.teamforce.thanksapp.presentation.adapter.history.PagerAdapter
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.presentation.viewmodel.history.HistoryViewModel
import com.teamforce.thanksapp.utils.invisible
import com.teamforce.thanksapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val binding: FragmentHistoryBinding by viewBinding()
    private val viewModel: HistoryViewModel by viewModels()
    private var pagerAdapter: PagerAdapter? = null
    private var mediator: TabLayoutMediator? = null
    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_historyFragment_to_profileGraph, null,
                OptionsTransaction().optionForProfileFragment
            )
        }

        binding.apply {

            pagerAdapter = PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                when (pos) {
                    0 -> tab.text = getString(R.string.allHistory)
                    1 -> tab.text = getString(R.string.received)
                    else -> tab.text = getString(R.string.sended)
                }
            }

            mediator?.attach()
        }

        binding.notifyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_notificationFragment)
        }

        sharedViewModel.state.observe(viewLifecycleOwner) { notificationsCount ->
            if (notificationsCount == 0) {
                binding.apply {
                    activeNotifyLayout.invisible()
                    notify.visible()
                }
            } else {
                binding.apply {
                    activeNotifyLayout.visible()
                    notify.invisible()
                    notifyBadge.text = notificationsCount.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        pagerAdapter = null
        super.onDestroyView()

    }

    companion object {
        const val TAG = "HistoryFragment"
    }
}
