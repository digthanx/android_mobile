package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.presentation.adapter.challenge.ChallengeListStateAdapter
import com.teamforce.thanksapp.presentation.adapter.history.PagerAdapter
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.navigateSafely
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengesFragment : Fragment(R.layout.fragment_challenges) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentChallengesBinding by viewBinding()


    private var pagerAdapter: ChallengeListStateAdapter? = null
    private var mediator: TabLayoutMediator? = null



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initTabLayout()
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

    }

    private fun initView() {

    }

    private fun initTabLayout(){
        binding.apply {

            pagerAdapter = ChallengeListStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                when (pos) {
                    0 -> tab.text = getString(R.string.allChallenge)
                    else -> tab.text = getString(R.string.activeChallenge)
                }
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

}