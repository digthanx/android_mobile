package com.teamforce.thanksapp.presentation.adapter.challenge

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemCommentBinding
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.presentation.adapter.CommentsAdapter
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChallengeCommentAdapter(
    private val username: String,
    private val onDeleteLongClicked: (id: Int) -> Unit
) : PagingDataAdapter<CommentModel, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            val viewHolder = holder as CommentItemViewHolder
            viewHolder.bind(item, username, onDeleteLongClicked)
        }
    }


    class CommentItemViewHolder(
        private val binding: ItemCommentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        val avatarUser: ImageView = binding.userAvatar
        var dateTime: TextView = binding.dateTime
        val fioSender: TextView = binding.fioSender
        val message: TextView = binding.message
        var date = ""
        var time = ""
        val mainCardView: MaterialCardView = binding.mainCardView
        val root = binding.root

        fun bind(
            data: CommentModel, username: String,
            onCancelClicked: (id: Int) -> Unit
        ) {
            binding.apply {

            }
        }

    }

//    private fun bindBaseInfo(holder: CommentsAdapter.CommentViewHolder, position: Int){
//        holder.fioSender.text =
//            String.format(context.getString(R.string.fioSender),
//                currentList[position].user.surname, currentList[position].user.name)
//        holder.message.text = currentList[position].text
//    }
//
//    private fun bindAvatar(holder: CommentsAdapter.CommentViewHolder, position: Int){
//        if (!currentList[position].user.avatar.isNullOrEmpty()) {
//            Glide.with(context)
//                .load("${Consts.BASE_URL}${currentList[position].user.avatar}".toUri())
//                .apply(RequestOptions.bitmapTransform(CircleCrop()))
//                .into(holder.avatarUser)
//        } else {
//            holder.avatarUser.setImageResource(R.drawable.ic_anon_avatar)
//        }
//    }
//
//    private fun bindDate(holder: CommentsAdapter.CommentViewHolder, position: Int){
//        try {
//            val zdt: ZonedDateTime =
//                ZonedDateTime.parse(currentList[position].created, DateTimeFormatter.ISO_DATE_TIME)
//            val dateTime: String? =
//                LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
//                    .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
//            val date = dateTime?.subSequence(0, 10)
//            val time = dateTime?.subSequence(11, 16)
//            val today: LocalDate = LocalDate.now()
//            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
//            val todayString =
//                LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
//            val yesterdayString =
//                LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//                    .format(DateTimeFormatter.ofPattern("dd.MM.y"))
//
//            if (date == todayString) {
//                holder.date = "Сегодня"
//            } else if (date == yesterdayString) {
//                holder.date = "Вчера"
//            } else {
//                holder.date = date.toString()
//            }
//            holder.time = time.toString()
//            holder.dateTime.text =
//                String.format(context.getString(R.string.dateTime), holder.date, holder.time)
//        } catch (e: Exception) {
//            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
//        }
//    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}