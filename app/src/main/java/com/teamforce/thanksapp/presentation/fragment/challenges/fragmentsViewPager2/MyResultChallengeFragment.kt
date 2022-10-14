package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMyResultChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.viewmodel.MyResultChallengeViewModel
import com.teamforce.thanksapp.utils.Consts
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MyResultChallengeFragment : Fragment(R.layout.fragment_my_result_challenge) {

    private val binding: FragmentMyResultChallengeBinding by viewBinding()

    private val viewModel: MyResultChallengeViewModel by viewModels()

    private var idChallenge: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(ChallengesFragment.CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMyResult()
        setMyResult()
        listeners()
    }

    private fun loadMyResult(){
        idChallenge?.let {
            viewModel.loadChallengeResult(it)
        }
    }

    private fun setMyResult(){
        viewModel.myResult.observe(viewLifecycleOwner){
            binding.secondaryCard.visibility = View.VISIBLE
            binding.firstCard.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE
            binding.descriptionEt.text = it?.text
            if(it?.status == "Получено вознаграждение"){
                binding.stateEt.text = it.status
                binding.cardStatus.setCardBackgroundColor(
                    requireContext().getColor(R.color.minor_success_secondary))
                binding.stateEt.setTextColor(requireContext().getColor(R.color.minor_success))
                binding.valueEt.setTextColor(requireContext().getColor(R.color.minor_success))
            }else{
                binding.cardStatus.setCardBackgroundColor(
                    requireContext().getColor(R.color.minor_error_secondary))
                binding.stateEt.setTextColor(requireContext().getColor(R.color.minor_error))
                binding.valueEt.setTextColor(requireContext().getColor(R.color.minor_error))
                binding.stateEt.text = it?.status
            }

            if (!it?.photo.isNullOrEmpty()){
                binding.showAttachedImgCard.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load("${Consts.BASE_URL}${it?.photo}".toUri())
                    .centerCrop()
                    .fitCenter()
                    .into(binding.image)
            }

            // Можно совместить вместе с получено вознаграждение, если у бека уточнить
            if(it?.received!! > 0){
                binding.valueEt.text = String.format(
                    requireContext().getString(R.string.thanksWasGot),
                it.received)
                binding.imageCurrency.setColorFilter(requireContext().getColor(R.color.minor_success))
            }else{
                binding.valueEt.text = it.received.toString()
                // Спорная штука
                binding.imageCurrency.setColorFilter(requireContext().getColor(R.color.minor_error))

            }

            binding.dateEt.text = convertDateToNecessaryFormat(it.updated_at)

        }
    }

    private fun listeners(){
        viewModel.myResultError.observe(viewLifecycleOwner){
           // Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            binding.secondaryCard.visibility = View.GONE
            binding.firstCard.visibility = View.GONE
            binding.noData.visibility = View.VISIBLE
        }
    }

    private fun convertDateToNecessaryFormat(timeFormDb: String): String {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(timeFormDb, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            var date = dateTime?.subSequence(0, 10)
            var time = dateTime?.subSequence(11, 16)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString =
                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString =
                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            if (date == todayString) {
                date = "Сегодня"
            } else if (date == yesterdayString) {
                date = "Вчера"
            } else {
                date = date.toString()
            }
            time = time.toString()
            return  String.format(requireContext().getString(R.string.updatedDateTime), date, time)

        } catch (e: Exception) {
            Log.e("MyResultChallengeFragment", e.message, e.fillInStackTrace())
            return ""
        }
    }





    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            MyResultChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ChallengesFragment.CHALLENGER_ID, challengeId)
                }
            }
    }
}