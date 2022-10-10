package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.ChallengeAdapter
import com.teamforce.thanksapp.presentation.adapter.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_CREATOR_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATE_ACTIVE
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATUS
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGE_BACKGROUND
import com.teamforce.thanksapp.presentation.viewmodel.DetailsMainChallengeViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class DetailsMainChallengeFragment : Fragment(R.layout.fragment_details_main_challenge) {

    private val binding: FragmentDetailsMainChallengeBinding by viewBinding()

    private val viewModel: DetailsMainChallengeViewModel by viewModels()

    private var statusChallenge: String? = null
    private var idChallenge: Int? = null
    private var challengeActive: Boolean? = null
    private var challengeBackground: String? = null
    private var creatorId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusChallenge = it.getString(CHALLENGER_STATUS)
            idChallenge = it.getInt(CHALLENGER_ID)
            challengeActive = it.getBoolean(CHALLENGER_STATE_ACTIVE)
            challengeBackground = it.getString(CHALLENGE_BACKGROUND)
            creatorId = it.getInt(CHALLENGER_CREATOR_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        initTabLayoutMediator()
        listenersBtn()


    }

    private fun initTabLayoutMediator(){
        // Нужно добавить изменения в количестве вкладок в зависимости от статуса чалика
        val detailInnerFragment = FragmentDetailChallengeStateAdapter(requireActivity())
        idChallenge?.let { detailInnerFragment.setChallengeId(it) }
        binding.pager.adapter = detailInnerFragment
        if(creatorId == viewModel.getProfileId()){
            TabLayoutMediator(binding.tabLayout, binding.pager){ tab, position ->
                when(position){
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.winners)
                    2 -> tab.text = context?.getString(R.string.contenders)
                }
            }.attach()
        }else{
            TabLayoutMediator(binding.tabLayout, binding.pager){ tab, position ->
                when(position){
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.winners)
                }
            }.attach()
        }
    }

    private fun listenersBtn(){
        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.closeBtnSecondary.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setData(){
        //if(challengeBackground.isNullOrEmpty())
        if(challengeActive == true){
            binding.statusActiveText.text = requireContext().getString(R.string.active)
            binding.statusActiveTextSecondary.text = requireContext().getString(R.string.active)
            binding.statusActiveCard
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_info))
        }else{
            binding.statusActiveText.text = requireContext().getString(R.string.completed)
            binding.statusActiveTextSecondary.text = requireContext().getString(R.string.completed)
            binding.statusActiveCard
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_success))
        }
        if(!challengeBackground.isNullOrEmpty()){
            binding.standardCard.visibility = View.GONE
            binding.secondaryCard.visibility = View.VISIBLE
            binding.closeBtnSecondary.setTextColor(requireContext().getColor(R.color.general_background))
            Glide.with(requireContext())
                .load("${Consts.BASE_URL}${challengeBackground}".toUri())
                .fitCenter()
                .centerCrop()
                .into(binding.imageBackground)
        }
    }


}