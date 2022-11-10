package com.teamforce.thanksapp.presentation.adapter.challenge

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemCommentBinding
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.utils.Consts
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChallengeCommentAdapter(
    private val profileId: String,
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
            viewHolder.bind(item, profileId, onDeleteLongClicked)
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
            data: CommentModel, profileId: String,
            onDeleteLongClicked: (id: Int) -> Unit
        ) {
            bindAvatar(binding, data)
            bindBaseInfo(binding, data)
            bindDate(binding, data)
            // Если имя пользователя совпадает с именем владельца коммента
            if(profileId == data.user.id.toString()){
                binding.mainCardView.setOnLongClickListener {
                    val popup: PopupMenu = PopupMenu(binding.root.context, binding.fioSender)
                    popup.menuInflater.inflate(R.menu.comment_context_menu, popup.menu)
                    popup.gravity = Gravity.START

                    popup.setOnMenuItemClickListener {
                        when(it.itemId) {
                            R.id.delete -> {
                                onDeleteLongClicked(data.id)
                            }
                        }
                        true
                    }
                    popup.show()
                    true
                }
            }
        }

        private fun bindBaseInfo(binding: ItemCommentBinding, data: CommentModel){
            binding.fioSender.text =
                String.format(binding.root.context.getString(R.string.fioSender),
                    data.user.surname, data.user.name)
            binding.message.text = data.text
        }

        private fun bindAvatar(binding: ItemCommentBinding, data: CommentModel){
            if (!data.user.avatar.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load("${Consts.BASE_URL}${data.user.avatar}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }
        }

        private fun bindDate(binding: ItemCommentBinding, data: CommentModel){
            try {
                val zdt: ZonedDateTime =
                    ZonedDateTime.parse(data.created, DateTimeFormatter.ISO_DATE_TIME)
                val dateTime: String? =
                    LocalDateTime.parse(zdt.toString(), DateTimeFormatter.ISO_DATE_TIME)
                        .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
                val dateInner = dateTime?.subSequence(0, 10)
                val timeInner = dateTime?.subSequence(11, 16)
                val today: LocalDate = LocalDate.now()
                val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
                val todayString =
                    LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.y"))
                val yesterdayString =
                    LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.y"))

                if (dateInner == todayString) {
                    date = "Сегодня"
                } else if (dateInner == yesterdayString) {
                    date = "Вчера"
                } else {
                    date = dateInner.toString()
                }
                time = timeInner.toString()
                binding.dateTime.text =
                    String.format(
                        binding.root.context.getString(R.string.dateTime), date, time)
            } catch (e: Exception) {
                Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
            }
        }

    }



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