package com.teamforce.thanksapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.data.response.GetChallengeWinnersResponse
import com.teamforce.thanksapp.databinding.ItemContenderBinding
import com.teamforce.thanksapp.databinding.ItemWinnerBinding
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WinnersAdapter
    : ListAdapter<GetChallengeWinnersResponse.Winner, WinnersAdapter.WinnerViewHolder>(DiffCallback)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinnerViewHolder {
        val binding = ItemWinnerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return WinnerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    companion object{

        object DiffCallback : DiffUtil.ItemCallback<GetChallengeWinnersResponse.Winner>() {
            override fun areItemsTheSame(
                oldItem: GetChallengeWinnersResponse.Winner,
                newItem: GetChallengeWinnersResponse.Winner): Boolean {
                return oldItem.participant_id == newItem.participant_id
            }

            override fun areContentsTheSame(
                oldItem: GetChallengeWinnersResponse.Winner,
                newItem: GetChallengeWinnersResponse.Winner): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class WinnerViewHolder(val binding: ItemWinnerBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: WinnerViewHolder, position: Int) {
        with(holder){
           convertDateToNecessaryFormat(holder, position)
            binding.amountThanks.text =
                String.format(binding.root.context.getString(R.string.amountThanks), currentList[position].total_received)
            binding.tgNameUser.text = currentList[position]?.nickname
                    ?: (currentList[position].participant_surname + " " + currentList[position].participant_name)

            if(!currentList[position].participant_photo.isNullOrEmpty()){
                Glide.with(binding.root.context)
                    .load("${Consts.BASE_URL}${currentList[position].participant_photo}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(binding.userAvatar)
            }
        }


    }

    private fun convertDateToNecessaryFormat(holder: WinnerViewHolder, position: Int) {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(currentList[position].awarded_at, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            var date = dateTime?.subSequence(0, 10)
            val time = dateTime?.subSequence(11, 16)
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
            holder.binding.dateTv.text =
                String.format(holder.binding.root.context.getString(R.string.dateTime), date, time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }
}