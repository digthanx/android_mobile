package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FeedAdapter (
    private val username: String,
    private val context: Context
): ListAdapter<FeedResponse, FeedAdapter.FeedViewHolder>(DiffCallback){


    companion object DiffCallback : DiffUtil.ItemCallback<FeedResponse>(){
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

    class FeedViewHolder(binding: ItemFeedBinding): RecyclerView.ViewHolder(binding.root){
        val avatarUser: ImageView = binding.userAvatar
        var dateTime: TextView = binding.dateTime
        val senderAndReceiver: TextView = binding.senderAndReceiver
        val likesBtn: MaterialButton = binding.likeBtn
        val dislikesBtn: MaterialButton = binding.dislikeBtn
        val commentBtn: MaterialButton = binding.commentBtn
        val view: View = binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        if(!currentList[position].transaction.photo.isNullOrEmpty()) {
            Glide.with(context)
                .load("${Consts.BASE_URL}${currentList[position].transaction.photo}".toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(holder.avatarUser)
        }

        if(!currentList[position].transaction.sender.equals(username) &&
            !currentList[position].transaction.recipient.equals(username)){
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.someoneToSomeone),
                    currentList[position].transaction.recipient,
                    "150",
                    currentList[position].transaction.sender)
        }else if(!currentList[position].transaction.sender.equals(username)){
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.youFromSomeone),
                    "150",
                    currentList[position].transaction.sender)

        }else{
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.youToSomeone),
                    currentList[position].transaction.recipient,
                    "150")
        }
        try {
            val zdt: ZonedDateTime = ZonedDateTime.parse(currentList[position].time, DateTimeFormatter.ISO_DATE_TIME)
            val dateTime: String? = LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern( "dd.MM.y HH:mm"))
            holder.dateTime.text = dateTime.toString()
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }


}