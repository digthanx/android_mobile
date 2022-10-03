package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.utils.OptionsTransaction


class DetailsMainChallengeFragment : Fragment(R.layout.fragment_details_main_challenge) {

    private val binding: FragmentDetailsMainChallengeBinding by viewBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        TabLayoutMediator(binding.tabLayout, binding.pager){ tab, position ->
//            tab.text = "OBJECT ${(position + 1)}"
//        }.attach()
        binding.closeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailsMainChallengeFragment_to_challengesFragment,
                null,
                OptionsTransaction().optionForProfileFromEditProfile
            )
        }
    }


}