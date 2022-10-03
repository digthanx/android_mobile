package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.utils.OptionsTransaction


class DetailsMainChallengeFragment : Fragment(R.layout.fragment_details_main_challenge) {

    private val binding: FragmentDetailsMainChallengeBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabs = binding.tabLayout.getChildAt(0) as ViewGroup

        for (i in 0 until tabs.childCount ) {
            val tab = tabs.getChildAt(i)
            val layoutParams = tab.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 0f
            layoutParams.marginEnd = 12
            layoutParams.marginStart = 12
            layoutParams.width = 10
            tab.layoutParams = layoutParams
            binding.tabLayout.requestLayout()
        }
        binding.pager.adapter = FragmentDetailChallengeStateAdapter(requireActivity())
        TabLayoutMediator(binding.tabLayout, binding.pager){ tab, position ->
            when(position){
                0 -> tab.text = context?.getString(R.string.details)
                1 -> tab.text = context?.getString(R.string.comments)
                2 -> tab.text = context?.getString(R.string.participants)
            }
        }.attach()
        binding.closeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailsMainChallengeFragment_to_challengesFragment,
                null,
                OptionsTransaction().optionForProfileFromEditProfile
            )
        }
    }


}