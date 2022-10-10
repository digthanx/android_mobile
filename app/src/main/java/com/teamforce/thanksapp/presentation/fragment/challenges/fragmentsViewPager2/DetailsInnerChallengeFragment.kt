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
import com.teamforce.thanksapp.presentation.adapter.ChallengeAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.viewmodel.DetailsInnerChallengerViewModel
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
        loadChallengeData(idChallenge)
        setDataAboutChallenge()
        checkReportSharedPref()
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


    private fun loadChallengeData(challengeId: Int?) {
        challengeId?.let {
            viewModel.loadChallenge(it)
        }
    }


    private fun checkReportSharedPref(){
        val sharedPref = requireContext().getSharedPreferences("report", 0)
        if (!sharedPref.getString("commentReport", "").isNullOrEmpty()||
            !sharedPref.getString("imageReport", "").isNullOrEmpty()){
            binding.sendReportBtn.text = requireContext().getString(R.string.draft)
        }else{
            binding.sendReportBtn.text = requireContext().getString(R.string.sendReport)
        }
    }

    private fun setDataAboutChallenge() {

        viewModel.challenge.observe(viewLifecycleOwner) {
            binding.nameChallenge.text = it.name
            binding.descriptionChallenge.text = it.description
            binding.stateAboutReports.text = it.status
            if(it.active){
                binding.stateAboutAddParticipants.text =
                    requireContext().getString(R.string.gettingReportsActive)
            }else{
                binding.stateAboutAddParticipants.text =
                    requireContext().getString(R.string.gettingReportsFinished)
            }
            // Сделать динамическую подстановку чипов(по статусам транзакции)
            // пробегаться по массиву статусов и ставить чипы
            // Установить организатора
            binding.prizeFundValue.text =
                String.format(requireContext().getString(R.string.fund), it.fund.toString())
            binding.dateEndValue.text = convertDateToNecessaryFormat(it.end_at)
            binding.prizePoolValue.text =
                String.format(
                    requireContext()
                        .getString(R.string.occupiedPrizePool), it.winners_count, it.awardees
                )
            binding.userTgName.setText(
                String.format(requireContext().getString(R.string.tgName), it.creator_tg_name)
            )
            if (!it.creator_photo.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load("${Consts.BASE_URL}${it.creator_photo}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.userAvatar)
            }
        }
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