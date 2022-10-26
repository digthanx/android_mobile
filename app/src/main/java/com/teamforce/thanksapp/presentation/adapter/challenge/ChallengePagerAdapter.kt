package com.teamforce.thanksapp.presentation.adapter.challenge

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChallengePagerAdapter(

) : PagingDataAdapter<ChallengeModel, ChallengePagerAdapter.ChallengeViewHolder>(DiffCallback) {

    var onChallengeClicked: ((dataOChallenge: ChallengeModel) -> Unit)? = null
    var onCreatorOfChallengeClicked: ((creatorId: Int) -> Unit)? = null


    companion object {
        object DiffCallback : DiffUtil.ItemCallback<ChallengeModel>() {
            override fun areItemsTheSame(
                oldItem: ChallengeModel,
                newItem: ChallengeModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ChallengeModel,
                newItem: ChallengeModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ChallengeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            holder.binding.mainCard.setOnClickListener {
                onChallengeClicked?.invoke(item)
            }
            holder.binding.challengeCreator.setOnClickListener {
                onCreatorOfChallengeClicked?.invoke(item.creator_id)
            }
        }
    }


    class ChallengeViewHolder(val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ChallengeModel) {
            if (!data.photo.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load("${Consts.BASE_URL}${data.photo}".toUri())
                    .fitCenter()
                    .centerCrop()
                    .into(binding.imageBackground)
                binding.apply {
                    successfulPersonImage.visibility = View.INVISIBLE
                    prizeFundValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                    prizePoolValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                    prizeFundText.setTextColor(binding.root.context.getColor(R.color.general_background))
                    prizePoolText.setTextColor(binding.root.context.getColor(R.color.general_background))
                    challengeTitle.setTextColor(binding.root.context.getColor(R.color.general_background))
                    challengeCreator.setTextColor(binding.root.context.getColor(R.color.general_background))
                    winnersValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                    winnersText.setTextColor(binding.root.context.getColor(R.color.general_background))
                    lastUpdateChallengeValue.setTextColor(binding.root.context.getColor(R.color.general_background))
                    lastUpdateChallengeCard.strokeColor =
                        binding.root.context.getColor(R.color.general_background)
                    lastUpdateChallengeCard.setCardBackgroundColor(
                        binding.root.context.getColor(
                            R.color.transparent
                        )
                    )
                    mainCard.setCardBackgroundColor(null)
                    imageRelative.visibility = View.VISIBLE
                    alphaView.visibility = View.VISIBLE
                }
            }else {
                binding.apply {
                    successfulPersonImage.visibility = View.VISIBLE
                    prizeFundValue.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    prizePoolValue.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    prizeFundText.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    prizePoolText.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    challengeTitle.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    winnersValue.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    winnersText.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    lastUpdateChallengeValue.setTextColor(binding.root.context.getColor(R.color.general_contrast))
                    lastUpdateChallengeCard.strokeColor =
                        binding.root.context.getColor(R.color.general_contrast)
                    lastUpdateChallengeCard.setCardBackgroundColor(
                        binding.root.context.getColor(
                            R.color.general_brand_secondary
                        )
                    )
                    mainCard.setCardBackgroundColor(binding.root.context.getColor(R.color.general_brand_secondary))
                    imageRelative.visibility = View.GONE
                    alphaView.visibility = View.GONE
                }
            }
            // insert data
            binding.apply {
                challengeTitle.setText(data.name)
                challengeCreator.setText(
                    String.format(
                        binding.root.context.getString(R.string.creatorOfChallenge),
                        data.creator_surname,
                        data.creator_name
                    )
                )
                winnersValue.text = data.winners_count.toString()
                if (data.parameters?.get(0)?.id == 1) {
                    data.parameters?.get(1)?.let { prizePoolValue.setText(it.value.toString()) }
                } else {
                    data.parameters?.get(0)?.let { prizePoolValue.setText(it.value.toString()) }
                }
                if (data.active) {
                    stateChallengeValue.text = binding.root.context.getString(R.string.active)
                } else {
                    stateChallengeValue.text = binding.root.context.getString(R.string.completed)
                }
                prizeFundValue.setText(data.fund.toString())
                convertDateToNecessaryFormat(data)
            }
        }


        private fun convertDateToNecessaryFormat(data: ChallengeModel) {
            try {
                val zdt: ZonedDateTime =
                    ZonedDateTime.parse(data.updated_at, DateTimeFormatter.ISO_DATE_TIME)
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
                binding.lastUpdateChallengeValue.text =
                    String.format(
                        binding.root.context.getString(R.string.lastUpdateChallenge),
                        date
                    )
            } catch (e: Exception) {
                Log.e("ChallengeAdapter", e.message, e.fillInStackTrace())
            }
        }
    }
}