package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.ItemFeedBinding
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FeedAdapter(
    private val username: String,
    private val context: Context
) : ListAdapter<FeedResponse, FeedAdapter.FeedViewHolder>(DiffCallback) {


    companion object DiffCallback : DiffUtil.ItemCallback<FeedResponse>() {
        override fun areItemsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
            return oldItem == newItem
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return FeedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //Log.d("Token", "DataSet size - ${currentList.size}")
        return currentList.size
    }

    class FeedViewHolder(binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val avatarUser: ImageView = binding.userAvatar
        var dateTime: TextView = binding.dateTime
        val senderAndReceiver: TextView = binding.senderAndReceiver
        val likesBtn: MaterialButton = binding.likeBtn
        val dislikesBtn: MaterialButton = binding.dislikeBtn
        val commentBtn: MaterialButton = binding.commentBtn
        val standardGroup = binding.standardGroup
        var descriptionFeed = ""
        var date = ""
        var time = ""
        val view: View = binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        if (!currentList[position].transaction.recipient_photo.isNullOrEmpty()) {
            Glide.with(context)
                .load("${Consts.BASE_URL}${currentList[position].transaction.recipient_photo}".toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(holder.avatarUser)
        } else {
            holder.avatarUser.setImageResource(R.drawable.ic_anon_avatar)

        }
        if (!currentList[position].transaction.sender.equals(username) &&
            !currentList[position].transaction.recipient.equals(username)
        ) {
            holder.senderAndReceiver.text =
                String.format(
                    holder.view.context.getString(
                        R.string.someoneToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore("."),
                    currentList[position].transaction.sender
                )
            holder.descriptionFeed = context.getString(
                R.string.someoneToSomeone
            )
        } else if (!currentList[position].transaction.sender.equals(username)) {
            holder.senderAndReceiver.text =
                String.format(
                    holder.view.context.getString(
                        R.string.youFromSomeone
                    ),
                    currentList[position].transaction.amount.substringBefore("."),
                    currentList[position].transaction.sender
                )
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = context.getString(R.string.youFromSomeone)
        } else {
            holder.senderAndReceiver.text =
                String.format(
                    holder.view.context.getString(
                        R.string.youToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore(".")
                )
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = context.getString(R.string.youToSomeone)
        }
        try {
            val zdt: ZonedDateTime =
                ZonedDateTime.parse(currentList[position].time, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? =
                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
            val date = dateTime?.subSequence(0, 10)
            val time = dateTime?.subSequence(11, 16)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString = LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString = LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            if(date == todayString) {
                holder.date = "Сегодня"
            } else if(date == yesterdayString){
                holder.date = "Вчера"
            }else{
                holder.date = date.toString()
            }
            holder.time = time.toString()
            Log.d("Token", " Вчера ${holder.date}")
            holder.dateTime.text = String.format(context.getString(R.string.dateTime), holder.date, holder.time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
        holder.standardGroup.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.apply {
                // аву пока не передаю
                putString(
                    Consts.AVATAR_USER,
                    "${Consts.BASE_URL}${currentList[position].transaction.recipient_photo}"
                )
                putString(Consts.DATE_TRANSACTION, holder.dateTime.toString())
                putString(Consts.DESCRIPTION_FEED, holder.descriptionFeed)
                putString(Consts.SENDER_TG, currentList[position].transaction.sender)
                putString(Consts.RECEIVER_TG, currentList[position].transaction.recipient)
                putString(
                    Consts.AMOUNT_THANKS,
                    currentList[position].transaction.amount.substringBefore(".")
                )


            }
            v.findNavController()
                .navigate(R.id.action_feedFragment_to_additionalInfoFeedItemFragment)
        }

    }


}