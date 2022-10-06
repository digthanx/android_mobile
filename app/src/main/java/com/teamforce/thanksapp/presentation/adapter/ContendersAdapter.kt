package com.teamforce.thanksapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.databinding.ItemContenderBinding
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ContendersAdapter(

): ListAdapter<GetChallengeContendersResponse.Contender, ContendersAdapter.ContenderViewHolder>(ContenderViewHolder.DiffCallback)
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContenderViewHolder {
        val binding = ItemContenderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ContenderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    class ContenderViewHolder(
        binding: ItemContenderBinding) : RecyclerView.ViewHolder(binding.root) {
        var userAvatar = binding.userAvatar
        var userName = binding.userNameLabelTv
        var userSurname = binding.userSurnameLabelTv
        var dateTime = binding.dateTv

        //val name = binding.challengeTitle
        val root = binding.root
        var date: String = ""
        var time: String =  ""


        object DiffCallback : DiffUtil.ItemCallback<GetChallengeContendersResponse.Contender>() {
            override fun areItemsTheSame(oldItem: GetChallengeContendersResponse.Contender, newItem: GetChallengeContendersResponse.Contender): Boolean {
                return oldItem.participant_id == newItem.participant_id
            }

            override fun areContentsTheSame(oldItem: GetChallengeContendersResponse.Contender, newItem: GetChallengeContendersResponse.Contender): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onBindViewHolder(holder: ContenderViewHolder, position: Int) {
        if(!currentList[position].participant_photo.isNullOrEmpty()){
            Glide.with(holder.root.context)
                .load("${Consts.BASE_URL}${currentList[position].participant_photo}".toUri())
                .into(holder.userAvatar)
        }
        holder.userName.text = currentList[position].participant_name
        holder.userSurname.text = currentList[position].participant_surname
        convertDateToNecessaryFormat(holder, position)
    }

    private fun convertDateToNecessaryFormat(holder: ContenderViewHolder, position: Int) {
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(currentList[position].report_created_at, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            val date = dateTime?.subSequence(0, 10)
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
                holder.date = "Сегодня"
            } else if (date == yesterdayString) {
                holder.date = "Вчера"
            } else {
                holder.date = date.toString()
            }
            holder.time = time.toString()
            holder.dateTime.text =
                String.format(holder.root.context.getString(R.string.dateTime), holder.date, holder.time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }


}