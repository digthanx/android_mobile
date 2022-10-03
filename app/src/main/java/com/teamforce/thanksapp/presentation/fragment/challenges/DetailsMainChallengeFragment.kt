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
import com.teamforce.thanksapp.presentation.adapter.ChallengeAdapter
import com.teamforce.thanksapp.presentation.adapter.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.utils.OptionsTransaction

private const val CHALLENGER_STATUS = ChallengeAdapter.CHALLENGER_STATUS
private const val CHALLENGER_ID = ChallengeAdapter.CHALLENGER_ID

class DetailsMainChallengeFragment : Fragment(R.layout.fragment_details_main_challenge) {

    private val binding: FragmentDetailsMainChallengeBinding by viewBinding()

    private var statusChallenge: String? = null
    private var idChallenge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusChallenge = it.getString(CHALLENGER_STATUS)
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayoutMediator()
        listenersBtn()


    }

    private fun initTabLayoutMediator(){
        // Нужно добавить изменения в количестве вкладок в зависимости от статуса чалика
        val detailInnerFragment = FragmentDetailChallengeStateAdapter(requireActivity())
        idChallenge?.let { detailInnerFragment.setChallengeId(it) }
        binding.pager.adapter = detailInnerFragment
        TabLayoutMediator(binding.tabLayout, binding.pager){ tab, position ->
            when(position){
                0 -> tab.text = context?.getString(R.string.details)
                1 -> tab.text = context?.getString(R.string.comments)
                2 -> tab.text = context?.getString(R.string.participants)
            }
        }.attach()
    }

    private fun listenersBtn(){
        binding.closeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_detailsMainChallengeFragment_to_challengesFragment,
                null,
                OptionsTransaction().optionForProfileFromEditProfile
            )
        }
    }


}