package com.teamforce.thanksapp.presentation.fragment.historyScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentHistoryBinding
import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {
    private val binding: FragmentHistoryBinding by viewBinding()
    private val viewModel: HistoryViewModel by viewModels()
    private var pagerAdapter: PagerAdapter? = null
    private var mediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            pagerAdapter = PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                if (pos == 0) tab.text = getString(R.string.allHistory)
                if (pos == 1) tab.text = getString(R.string.received)
                else tab.text = getString(R.string.sended)
            }

            mediator?.attach()
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
