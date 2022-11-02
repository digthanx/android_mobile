package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.presentation.adapter.feed.PagerAdapter
import com.teamforce.thanksapp.presentation.viewmodel.feed.FeedViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.ViewLifecycleDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val binding: FragmentFeedBinding by viewBinding()
    private val pagerAdapter by ViewLifecycleDelegate {PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)}
    private var mediator: TabLayoutMediator? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                when (pos) {
                    0 -> tab.text = getString(R.string.allEvent)
                    1 -> tab.text = getString(R.string.thanks)
                    2 -> tab.text = getString(R.string.winners)
                    3 -> tab.text = getString(R.string.challenges)
                }
            }
            mediator?.attach()
        }

        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_profileGraph, null,
                OptionsTransaction().optionForProfileFragment
            )
        }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

}