package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentDetailsInnerChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesStatus
import com.teamforce.thanksapp.presentation.viewmodel.challenge.DetailsInnerChallengerViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class DetailsInnerChallengeFragment : Fragment(R.layout.fragment_details_inner_challenge) {

    private val binding: FragmentDetailsInnerChallengeBinding by viewBinding()
    private val viewModel: DetailsInnerChallengerViewModel by viewModels()

    private var idChallenge: Int? = null
    private var likesCountInner: Int = 0
    private var isLikedInner: Boolean? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onStart() {
        super.onStart()
        checkReportSharedPref()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scrollView.setFooterView(R.id.user_item)
        loadChallengeData(idChallenge)
        setDataAboutChallengeInListener()
        checkReportSharedPref()
        handleLike()
        binding.sendReportBtn.setOnClickListener {
            val bundle = Bundle()
            idChallenge?.let { it1 -> bundle.putInt(CHALLENGER_ID, it1) }
            it.findNavController().navigate(
                R.id.action_global_createReportFragment,
                bundle,
                OptionsTransaction().optionForEditProfile
            )
        }
    }

    private fun transactionToProfileOfCreator(creatorId: Int, view: View){
        val bundle = Bundle()
        bundle.putInt(Consts.USER_ID, creatorId)
        view.findNavController().navigate(
            R.id.action_global_someonesProfileFragment,
            bundle,
            OptionsTransaction().optionForTransactionWithSaveBackStack)
    }


    private fun loadChallengeData(challengeId: Int?) {
        challengeId?.let {
            viewModel.loadChallenge(it)
        }
    }


    private fun checkReportSharedPref() {
        val sharedPref = requireContext().getSharedPreferences("report", 0)
        if (!sharedPref.getString("commentReport", "").isNullOrEmpty() ||
            !sharedPref.getString("imageReport", "").isNullOrEmpty()
        ) {
            binding.sendReportBtn.text = requireContext().getString(R.string.draft)
        } else {
            binding.sendReportBtn.text = requireContext().getString(R.string.sendReport)
        }
    }

    private fun setDataAboutChallengeInListener() {

        viewModel.challenge.observe(viewLifecycleOwner) { challenge ->
            if(challenge != null){
                setLikes(challenge.likes_amount, challenge.user_liked)
                binding.likeBtn.text = challenge.likes_amount.toString()
                if (challenge.active) {
                    binding.nameChallenge.text = challenge.name
                    binding.descriptionChallenge.text = challenge.description
                    binding.stateAboutReports.text = challenge.status
                    if (challenge?.active == true) {
                        binding.stateAboutAddParticipants.text =
                            requireContext().getString(R.string.gettingReportsActive)
                    } else {
                        binding.stateAboutAddParticipants.text =
                            requireContext().getString(R.string.gettingReportsFinished)
                    }
                    binding.prizeFundValue.text =
                        String.format(requireContext().getString(R.string.fund), challenge.fund.toString())
                    binding.dateEndValue.text = challenge.end_at?.let { endAt ->
                        convertDateToNecessaryFormat(endAt)
                    }
                    binding.prizePoolValue.text =
                        String.format(
                            requireContext()
                                .getString(R.string.occupiedPrizePool), challenge.winners_count, challenge.awardees
                        )
                    binding.userTgName.setText(
                        String.format(requireContext().getString(R.string.tgName), challenge.creator_tg_name)
                    )
                    if (!challenge.creator_photo.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load("${Consts.BASE_URL}${challenge.creator_photo}".toUri())
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .into(binding.userAvatar)
                    }

                    if (challenge.status.isNullOrEmpty()) {
                        binding.stateAboutReports.visibility = View.GONE
                    } else {
                        binding.stateAboutReports.visibility = View.VISIBLE
                        challenge.status.let { it1 -> enableOrDisableSentReportButton(it1) }
                        binding.stateAboutReports.text = challenge.status
                    }

                    binding.userItem.setOnClickListener { view ->
                        challenge.creator_id?.let { id ->
                            transactionToProfileOfCreator(id, view)
                        }
                    }

                }
            }

        }
    }

    private fun handleLike(){
        binding.likeBtn.setOnClickListener { view ->
            isLikedInner?.let { likeBtnClicked(it) }
            idChallenge?.let { challengeId -> viewModel.pressLike(challengeId) }
        }
        viewModel.likeResult.observe(viewLifecycleOwner){ view ->
            // Некст две строки нужны для динамического обновления кол ва лайков
            // Если кто то кликнул в то время, пока ты не обновлял данные
            // TODO Мне не очень нравится реализация, можно как то лучше
            loadChallengeData(idChallenge)
            setDataAboutChallengeInListener()
            isLikedInner?.let { setLikes(likesCountInner, it) }
        }
    }


    private fun likeBtnClicked(isLiked: Boolean){
        if (isLikedInner != null) {
            isLikedInner = !isLiked
            if (isLikedInner == true) {
                likesCountInner += 1
                binding.likeBtn.text = likesCountInner.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
            } else {
                likesCountInner -= 1
                binding.likeBtn.text = likesCountInner.toString()
                binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))

            }
        }
    }

    private fun setLikes(likesAmount: Int, isLiked: Boolean) {
        likesCountInner = likesAmount
        isLikedInner = isLiked
        binding.likeBtn.text =  likesCountInner.toString()
        if (isLiked) {
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_success_secondary))
        } else {
            binding.likeBtn.setBackgroundColor(requireContext().getColor(R.color.minor_info_secondary))
        }
    }

    private fun enableOrDisableSentReportButton(statusChallenge: String) {
        // Для прода
        binding.sendReportBtn.isEnabled = (statusChallenge.trim().contains(ChallengesStatus.YOU_CAN_SENT_REPORT.value, true) ||
                statusChallenge.trim().contains(ChallengesStatus.REPORT_REFUSED.value, true)) &&
                !statusChallenge.trim().contains(ChallengesStatus.YOU_ARE_CREATER.value, ignoreCase = true)
        // Для разработки
//        binding.sendReportBtn.isEnabled =
//            statusChallenge.trim().contains(ChallengesStatus.YOU_CAN_SENT_REPORT.value, true) ||
//                statusChallenge.trim().contains(ChallengesStatus.REPORT_REFUSED.value, true)
    }

    private fun convertDateToNecessaryFormat(dateChallenge: String): String {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(dateChallenge, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            val date = dateTime?.subSequence(0, 10)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString =
                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString =
                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            if (date == todayString) {
                return "Сегодня"
            } else if (date == yesterdayString) {
                return "Вчера"
            } else {
                return date.toString()
            }
        } catch (e: Exception) {
            Log.e("ChallengeAdapter", e.message, e.fillInStackTrace())
            return "Не определено"
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            DetailsInnerChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }

}