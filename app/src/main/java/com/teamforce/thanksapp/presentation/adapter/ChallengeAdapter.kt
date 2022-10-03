package com.teamforce.thanksapp.presentation.adapter

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
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChallengeAdapter(
): ListAdapter<ChallengeModel,
        ChallengeAdapter.ChallengeViewHolder>(ChallengeViewHolder.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ChallengeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    class ChallengeViewHolder(binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root) {

        val name = binding.challengeTitle
        val winners = binding.winnersValue
        val prizeFund = binding.prizeFundValue
        val prizePool = binding.prizePoolValue
        val backgroundImage = binding.imageBackground
        val personImage = binding.successfulPersonImage
        val winnersText = binding.winnersText
        val prizeFundText = binding.prizeFundText
        val prizePoolText = binding.prizePoolText
        val lastUpdateChallengeValue = binding.lastUpdateChallengeValue
        val lastUpdateChallengeCard = binding.lastUpdateChallengeCard
        var activeText = ""
        val mainCard = binding.mainCard
        val root = binding.root
        var date: String = ""
        var time: String =  ""
        val dateTime: String = ""


        object DiffCallback : DiffUtil.ItemCallback<ChallengeModel>() {
            override fun areItemsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        if (!currentList[position].photo.isNullOrEmpty()){
            Glide.with(holder.root.context)
                .load("${Consts.BASE_URL}${currentList[position].photo}".toUri())
                .into(holder.backgroundImage)
            holder.apply {
                personImage.visibility = View.INVISIBLE
                prizeFund.setTextColor(root.context.getColor(R.color.general_background))
                prizePool.setTextColor(root.context.getColor(R.color.general_background))
                prizeFundText.setTextColor(root.context.getColor(R.color.general_background))
                prizePoolText.setTextColor(root.context.getColor(R.color.general_background))
                name.setTextColor(root.context.getColor(R.color.general_background))
                winners.setTextColor(root.context.getColor(R.color.general_background))
                winnersText.setTextColor(root.context.getColor(R.color.general_background))
                lastUpdateChallengeValue.setTextColor(root.context.getColor(R.color.general_background))
                lastUpdateChallengeCard.strokeColor = root.context.getColor(R.color.general_background)
                mainCard.background = null
            }
        }
        // insert data
        holder.apply {
            name.setText(currentList[position].name)
            currentList[position].awardees?.let { winners.text = it.toString() }
            if( currentList[position].parameters?.get(0)?.id == 1){
                currentList[position].parameters?.get(1)?.let { prizePool.setText(it.value.toString()) }
            }else{
                currentList[position].parameters?.get(0)?.let { prizePool.setText(it.value.toString()) }
            }
            if(currentList[position].active){
                holder.activeText = holder.root.context.getString(R.string.active)
            }else{
                holder.activeText = holder.root.context.getString(R.string.completed)
            }
            prizeFund.setText(currentList[position].fund.toString())
            convertDateToNecessaryFormat(holder, position)
        }
        holder.mainCard.setOnClickListener {
            val bundle = Bundle()
            bundle.apply {
                putString(CHALLENGER_STATUS, currentList[position].status)
                putInt(CHALLENGER_ID, currentList[position].id)
                putBoolean(CHALLENGER_STATE_ACTIVE, currentList[position].active)
                putString(CHALLENGE_BACKGROUND, currentList[position].photo)
            }
            holder.root.findNavController().navigate(
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
                holder.date = "Сегодня"
            } else if (date == yesterdayString) {
                holder.date = "Вчера"
            } else {
                holder.date = date.toString()
            }
            holder.lastUpdateChallengeValue.text =
                String.format(holder.root.context.getString(R.string.lastUpdateChallenge), holder.date)
        } catch (e: Exception) {
            Log.e("ChallengeAdapter", e.message, e.fillInStackTrace())
        }
    }


    companion object {
        const val CHALLENGER_NAME = "challenger_name"
        const val CHALLENGER_DESCRIPTION = "challenger_description"
        const val CHALLENGER_STATE_REPORTS = "challenger_state_reports"
        const val CHALLENGER_STATE_ADD_PARTICIPANTS = "challenger_state_add_participants"
        const val CHALLENGER_STATE_ACTIVE = "challenger_state_active"
        const val CHALLENGER_PRIZE_FUND = "challenger_prize_fund"
        const val CHALLENGER_PRIZE_POOL = "challenger_prize_pool"
        const val CHALLENGER_DATE_END = "challenger_date_end"
        const val CHALLENGER_CREATOR_ID = "challenger_creator_id"
        const val CHALLENGE_BACKGROUND = "challenge_background"
        const val CHALLENGER_STATUS = "challenger_status"
        const val CHALLENGER_ID = "challenger_id"
    }
}