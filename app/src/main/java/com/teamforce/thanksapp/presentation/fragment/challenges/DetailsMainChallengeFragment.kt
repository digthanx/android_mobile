package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_CREATOR_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATE_ACTIVE
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATUS
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGE_BACKGROUND
import com.teamforce.thanksapp.presentation.viewmodel.challenge.DetailsMainChallengeViewModel
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_CREATOR_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_STATE_ACTIVE
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_STATUS
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGE_BACKGROUND
import com.teamforce.thanksapp.utils.Consts
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
        loadDataFromDb()
        setData()
        listenersForRequestedData()
        listenersBtn()


    }

    private fun initTabLayoutMediator(myResultWasReceivedSuccessfully: Boolean) {
        // Нужно добавить изменения в количестве вкладок в зависимости от статуса чалика
        val detailInnerAdapter = creatorId?.let { creatorId ->
            FragmentDetailChallengeStateAdapter(
                requireActivity(),
                creatorId = creatorId,
                profileId = viewModel.getProfileId(),
                myResultWasReceivedSuccessfully = myResultWasReceivedSuccessfully
            )
        }
        idChallenge?.let { detailInnerAdapter?.setChallengeId(it) }
        binding.pager.adapter = detailInnerAdapter
        if (creatorId == viewModel.getProfileId() && myResultWasReceivedSuccessfully) {
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.comments)
                    2 -> tab.text = context?.getString(R.string.winners)
                    3 -> tab.text = context?.getString(R.string.contenders)
                    4 -> tab.text = context?.getString(R.string.myResult)
                }
            }.attach()
        } else if (creatorId == viewModel.getProfileId()) {
            // Пока что если ты создать
            // я всегда буду показывать мой результат пока от бека нет поля
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.comments)
                    2 -> tab.text = context?.getString(R.string.winners)
                    3 -> tab.text = context?.getString(R.string.contenders)
                    4 -> tab.text = context?.getString(R.string.myResult)
                }
            }.attach()
        } else if (myResultWasReceivedSuccessfully) {
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.comments)
                    2 -> tab.text = context?.getString(R.string.winners)
                    3 -> tab.text = context?.getString(R.string.myResult)
                }
            }.attach()
        } else {
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.comments)
                    2 -> tab.text = context?.getString(R.string.winners)
                }
            }.attach()
        }
    }

    private fun loadDataFromDb() {
        idChallenge?.let {
            viewModel.loadChallengeResult(it)
        }
    }

    private fun listenersBtn() {
        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.closeCardSecondary.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun listenersForRequestedData() {
        viewModel.isSuccessOperationMyResult.observe(viewLifecycleOwner) {
            if (it) {
                initTabLayoutMediator(it)
            } else if (it == false) {
                initTabLayoutMediator(it)
            }
        }
    }

    private fun setData() {
        if (challengeActive == true) {
            binding.statusActiveText.text = requireContext().getString(R.string.active)
            binding.statusActiveTextSecondary.text = requireContext().getString(R.string.active)
            binding.statusActiveCard
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_info))
            binding.statusActiveCardSecondary
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_info))
        } else {
            binding.statusActiveText.text = requireContext().getString(R.string.completed)
            binding.statusActiveTextSecondary.text = requireContext().getString(R.string.completed)
            binding.statusActiveCard
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_success))
            binding.statusActiveCardSecondary
                .setCardBackgroundColor(requireContext().getColor(R.color.minor_success))
        }
        if (!challengeBackground.isNullOrEmpty()) {
            binding.standardCard.visibility = View.GONE
            binding.secondaryCard.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load("${Consts.BASE_URL}${challengeBackground}".toUri())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageBackground)
        }else{
            binding.standardCard.visibility = View.VISIBLE
            binding.secondaryCard.visibility = View.GONE
        }
    }


}