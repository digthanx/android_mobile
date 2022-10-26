package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.GetChallengeWinnersResponse
import com.teamforce.thanksapp.databinding.FragmentChallengesWinnersDetailBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_WINNER
import com.teamforce.thanksapp.presentation.viewmodel.WinnersDetailChallengeViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.viewSinglePhoto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengesWinnersDetailFragment : Fragment(R.layout.fragment_challenges_winners_detail) {

    private val binding: FragmentChallengesWinnersDetailBinding by viewBinding()
    private val viewModel: WinnersDetailChallengeViewModel by viewModels()

    private var challengeId: Int? = null
    private var dataOfWinner: GetChallengeWinnersResponse.Winner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dataOfWinner = it.getParcelable(CHALLENGER_WINNER)
            challengeId = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataOfWinner?.reportId?.let { viewModel.loadChallengeWinnerReportDetail(it) }
        viewModel.winnerReport.observe(viewLifecycleOwner){
            binding.nameChallenge.text = it?.challenge?.name
            binding.descriptionChallenge.text = it?.challengeText
            Glide.with(requireContext())
                .load("${Consts.BASE_URL}${it?.challengePhoto}".toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .fitCenter()
                .error(R.drawable.ic_anon_avatar)
                .into(binding.imageBackground)

            binding.imageBackground.setOnClickListener { view ->
                it?.challengePhoto?.let { photo ->
                    (view as ShapeableImageView).viewSinglePhoto(photo, requireContext())
                }
            }
        }
        dataOfWinner?.let {
            binding.userNameLabelTv.text =
                String.format(
                    requireContext().getString(R.string.userSurnameAndName), it.participant_surname,
                it.participant_name)
            // Возможно лучше другое подставить, тк тут никнейм, а не телеграмм именно
            binding.userTgName.text = String.format(
                requireContext().getString(R.string.tgName), it.nickname)
            // Поставить плейсхолдер и фото если ничего нет стандартную
            Glide.with(requireContext())
                .load("${Consts.BASE_URL}${it.participant_photo}".toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_anon_avatar)
                .error(R.drawable.ic_anon_avatar)
                .into(binding.userAvatar)
        }

        binding.closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.userItem.setOnClickListener {
            val bundle: Bundle = Bundle()
            dataOfWinner?.participant_id?.let { id ->
                bundle.putInt(Consts.USER_ID, id)
            }
            it.findNavController().navigate(R.id.action_global_someonesProfileFragment, bundle)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(idChallenge: String, dataOfWinner: GetChallengeWinnersResponse.Winner) =
            ChallengesWinnersDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(CHALLENGER_ID, idChallenge)
                    putParcelable(CHALLENGER_WINNER, dataOfWinner)
                }
            }
    }
}