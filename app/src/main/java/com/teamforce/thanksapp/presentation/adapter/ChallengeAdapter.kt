package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_CREATOR_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATE_ACTIVE
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_STATUS
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGE_BACKGROUND
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChallengeAdapter(
): ListAdapter<ChallengeModel,
        ChallengeAdapter.ChallengeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ChallengeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    inner class ChallengeViewHolder(val binding: ItemChallengeBinding)
        : RecyclerView.ViewHolder(binding.root)

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        if (!currentList[position].photo.isNullOrEmpty()){
            Glide.with(holder.binding.root.context)
                .load("${Consts.BASE_URL}${currentList[position].photo}".toUri())
                .fitCenter()
                .centerCrop()
                .into(holder.binding.imageBackground)
            holder.apply {
                binding.successfulPersonImage.visibility = View.INVISIBLE
                binding.prizeFundValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.prizePoolValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.prizeFundText.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.prizePoolText.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.challengeTitle.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.winnersValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.winnersText.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.lastUpdateChallengeValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                binding.lastUpdateChallengeCard.strokeColor = binding.root.context.getColor(R.color.general_background)
                binding.lastUpdateChallengeCard.setCardBackgroundColor(binding.root.context.getColor(R.color.transparent))
                binding.mainCard.background = null
                binding.imageRelative.visibility = View.VISIBLE
                binding.alphaView.visibility = View.VISIBLE
            }
        }
        // insert data
        holder.apply {
            binding.challengeTitle.setText(currentList[position].name)
            currentList[position].awardees?.let { binding.winnersValue.text = it.toString() }
            if( currentList[position].parameters?.get(0)?.id == 1){
                currentList[position].parameters?.get(1)?.let { binding.prizePoolValue.setText(it.value.toString()) }
            }else{
                currentList[position].parameters?.get(0)?.let { binding.prizePoolValue.setText(it.value.toString()) }
            }
            if(currentList[position].active){
                binding.stateChallengeValue.text = binding.root.context.getString(R.string.active)
            }else{
                binding.stateChallengeValue.text = binding.root.context.getString(R.string.completed)
            }
            binding.prizeFundValue.setText(currentList[position].fund.toString())
            convertDateToNecessaryFormat(holder, position)
        }
        holder.binding.mainCard.setOnClickListener {
            val bundle = Bundle()
            bundle.apply {
                putString(CHALLENGER_STATUS, currentList[position].status)
                putInt(CHALLENGER_ID, currentList[position].id)
                putBoolean(CHALLENGER_STATE_ACTIVE, currentList[position].active)
                putString(CHALLENGE_BACKGROUND, currentList[position].photo)
                currentList[position].creator_id?.let {
                        it1 -> putInt(CHALLENGER_CREATOR_ID, it1)
                }
            }
            holder.binding.root.findNavController().navigate(
                R.id.action_challengesFragment_to_detailsMainChallengeFragment,
                bundle,
                OptionsTransaction().optionForEditProfile
            )
        }
    }

    private fun convertDateToNecessaryFormat(holder: ChallengeViewHolder, position: Int) {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(currentList[position].updated_at, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            var date = dateTime?.subSequence(0, 10)
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
            holder.binding.lastUpdateChallengeValue.text =
                String.format(holder.binding.root.context.getString(R.string.lastUpdateChallenge), date)
        } catch (e: Exception) {
            Log.e("ChallengeAdapter", e.message, e.fillInStackTrace())
        }
    }


    companion object {
        object DiffCallback : DiffUtil.ItemCallback<ChallengeModel>() {
            override fun areItemsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}