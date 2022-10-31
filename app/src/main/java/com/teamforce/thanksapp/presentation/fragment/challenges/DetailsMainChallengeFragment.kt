package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentDetailsMainChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import com.teamforce.thanksapp.presentation.adapter.challenge.FragmentDetailChallengeStateAdapter
import com.teamforce.thanksapp.presentation.viewmodel.challenge.DetailsMainChallengeViewModel
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_DATA
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.viewSinglePhoto
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailsMainChallengeFragment : Fragment(R.layout.fragment_details_main_challenge) {

    private val binding: FragmentDetailsMainChallengeBinding by viewBinding()

    private val viewModel: DetailsMainChallengeViewModel by viewModels()


    private var dataOfChallenge: ChallengeModelById? = null
    private var doIHaveResult: Boolean? = null
    private var challengeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            challengeId = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataFromDb()
        listenersForRequestedData()
        listenersBtn()


    }

    private fun initTabLayoutMediator(myResultWasReceivedSuccessfully: Boolean) {
        val detailInnerAdapter = dataOfChallenge?.creator_id?.let { creatorId ->
            FragmentDetailChallengeStateAdapter(
                requireActivity(),
                creatorId = creatorId,
                profileId = viewModel.getProfileId(),
                myResultWasReceivedSuccessfully = myResultWasReceivedSuccessfully
            )
        }
        dataOfChallenge?.id?.let { detailInnerAdapter?.setChallengeId(it) }
        binding.pager.adapter = detailInnerAdapter
        if (dataOfChallenge?.creator_id == viewModel.getProfileId() && myResultWasReceivedSuccessfully) {
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                when (position) {
                    0 -> tab.text = context?.getString(R.string.details)
                    1 -> tab.text = context?.getString(R.string.comments)
                    2 -> tab.text = context?.getString(R.string.winners)
                    3 -> tab.text = context?.getString(R.string.contenders)
                    4 -> tab.text = context?.getString(R.string.myResult)
                }
            }.attach()
        } else if (dataOfChallenge?.creator_id == viewModel.getProfileId()) {
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
        challengeId?.let { challengeId ->
            viewModel.loadChallenge(challengeId)
            viewModel.loadChallengeResult(challengeId)
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
            doIHaveResult = it
            if (doIHaveResult == true && dataOfChallenge != null) {
                initTabLayoutMediator(it)
            } else if (doIHaveResult == false && dataOfChallenge != null) {
                initTabLayoutMediator(it)
            }
        }
        viewModel.challenge.observe(viewLifecycleOwner){
            dataOfChallenge = it
            if (doIHaveResult == true && dataOfChallenge != null) {
                initTabLayoutMediator(doIHaveResult!!)
            } else if (doIHaveResult == false && dataOfChallenge != null) {
                initTabLayoutMediator(doIHaveResult!!)
            }
            setData()
        }
    }

    private fun setData() {
        if (dataOfChallenge?.active == true) {
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
        if (!dataOfChallenge?.photo.isNullOrEmpty()) {
            binding.standardCard.visibility = View.GONE
            binding.secondaryCard.visibility = View.VISIBLE
            val urlPhoto = dataOfChallenge?.photo?.replace("_thumb", "")
            Glide.with(requireContext())
                .load("${Consts.BASE_URL}${urlPhoto}".toUri())
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageBackground)

            binding.imageBackground.setOnClickListener { view ->
                urlPhoto?.let { photo ->
                    (view as ShapeableImageView).viewSinglePhoto(photo, requireContext())
                }
            }
        }else{
            binding.standardCard.visibility = View.VISIBLE
            binding.secondaryCard.visibility = View.GONE
        }
    }


}