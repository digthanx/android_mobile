package com.teamforce.thanksapp.presentation.adapter.feed

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.ItemFeedBinding
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FeedAdapter(
    private val username: String,
) : ListAdapter<FeedResponse, FeedAdapter.FeedViewHolder>(FeedViewHolder.DiffCallback) {

    var likeClickListener: ((mapReaction: Map<String, Int>, position: Int) -> Unit)? = null
    var dislikeClickListener: ((mapReaction: Map<String, Int>, position: Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemFeedBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return FeedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    class FeedViewHolder(binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        val userAvatar: ImageView = binding.userAvatar
        var dateTime: TextView = binding.dateTime
        val senderAndReceiver: TextView = binding.senderAndReceiver
        val likeBtn: MaterialButton = binding.likeBtn
        val commentBtn: MaterialButton = binding.commentBtn
        val chipGroup: ChipGroup = binding.chipGroup
        val standardGroup = binding.standardGroup
        var descriptionFeed = ""
        var date = ""
        var time = ""
        val view: View = binding.root
        var userId: Int? = null
        var clickReceiver: ClickableSpan? = null
        var clickSender: ClickableSpan? = null
        var likesCount: Int = 0
        var dislikesCount: Int = 0


        object DiffCallback : DiffUtil.ItemCallback<FeedResponse>() {
            override fun areItemsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
                return oldItem == newItem
            }
        }

    }


    // @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(
        holder: FeedViewHolder,
        position: Int
    ) {
        bindLikesAndComments(holder, position)

        holder.senderAndReceiver.movementMethod = LinkMovementMethod.getInstance()
        holder.clickReceiver =
            transactionToReceiver(holder, position, currentList[position].transaction.recipient_id)

        holder.clickSender =
            transactionToSender(holder, position, currentList[position].transaction.sender_id)

        if (!currentList[position].transaction.recipient_photo.isNullOrEmpty()) {
            Glide.with(holder.view.context)
                .load("${Consts.BASE_URL}${currentList[position].transaction.recipient_photo}".toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(holder.userAvatar)
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
        }

        distinguishSenderAndReceiver(holder, position)

        currentList[position].transaction.tags?.let { setTags(holder.chipGroup, it) }

        convertDateToNecessaryFormat(holder, position)

        holder.standardGroup.setOnClickListener { v ->
            transactionToAdditionInfo(holder, position, v)
        }

        holder.commentBtn.setOnClickListener { v ->
            transactionToAdditionInfo(holder, position, v)
        }

    }

    private fun distinguishSenderAndReceiver(holder: FeedViewHolder, position: Int) {
        if (!currentList[position].transaction.sender.equals(username) &&
            !currentList[position].transaction.recipient.equals(username)
        ) {
            val spannable = SpannableStringBuilder(
                String.format(
                    holder.view.context.getString(
                        R.string.someoneToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore("."),
                    currentList[position].transaction.sender
                )
            )
            // Клик по получателю
            spannable.setSpan(
                holder.clickReceiver, 0, currentList[position].transaction.recipient.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                0, currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            // Клик по отправителю
            spannable.setSpan(
                holder.clickSender,
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                currentList[position].transaction.recipient.length + 9,
                spannable.length - currentList[position].transaction.sender.length - 4,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.descriptionFeed = holder.view.context.getString(
                R.string.getFrom
            )
            holder.senderAndReceiver.text = spannable
        } else if (!currentList[position].transaction.sender.equals(username)) {
            val spannable = SpannableStringBuilder(
                String.format(
                    holder.view.context.getString(
                        R.string.youFromSomeone
                    ),
                    currentList[position].transaction.amount.substringBefore("."),
                    currentList[position].transaction.sender
                )
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            // Клик по отправителю
            spannable.setSpan(
                holder.clickSender,
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                12,
                spannable.length - currentList[position].transaction.sender.length - 4,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.senderAndReceiver.text = spannable
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = holder.view.context.getString(R.string.youGetFrom)
            // Я получатель
        } else {
            val spannable = SpannableStringBuilder(
                String.format(
                    holder.view.context.getString(
                        R.string.youToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore(".")
                )
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                0, currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            // Клик по получателю
            spannable.setSpan(
                holder.clickReceiver,
                0, currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                currentList[position].transaction.recipient.length + 9,
                spannable.length - 7,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.senderAndReceiver.text = spannable
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = holder.view.context.getString(R.string.getFrom)
            // Я отправитель
        }
    }

    private fun transactionToAdditionInfo(holder: FeedViewHolder, position: Int, v: View) {
        val bundle = Bundle()
        bundle.apply {
            putString(
                Consts.AVATAR_USER,
                "${Consts.BASE_URL}${currentList[position].transaction.recipient_photo}"
            )
            putString(Consts.DATE_TRANSACTION, holder.dateTime.text.toString())
            putString(Consts.DESCRIPTION_FEED, holder.descriptionFeed)
            putString(Consts.SENDER_TG, currentList[position].transaction.sender)
            putString(Consts.RECEIVER_TG, currentList[position].transaction.recipient)
            putString(Consts.PHOTO_TRANSACTION, currentList[position].transaction.photo)
            putString(Consts.REASON_TRANSACTION, currentList[position].transaction.reason)
            putString(
                Consts.AMOUNT_THANKS,
                currentList[position].transaction.amount.substringBefore(".")
            )
            putInt(LIKES_COUNT, holder.likesCount)
            putInt(DISLIKES_COUNT, holder.dislikesCount)
            putBoolean(IS_LIKED, currentList[position].transaction.user_liked)
            putBoolean(IS_DISLIKED, currentList[position].transaction.user_disliked)
            putInt(TRANSACTION_ID, currentList[position].transaction.id)
            currentList[position].transaction.recipient_id?.let {
                this.putInt("userIdReceiver", it)
            }

            currentList[position].transaction.sender_id?.let {
                this.putInt("userIdSender", it)
            }


        }
        v.findNavController()
            .navigate(
                R.id.action_feedFragment_to_additionalInfoFeedItemFragment,
                bundle,
                OptionsTransaction().optionForAdditionalInfoFeedFragment
            )

    }

    private fun bindLikesAndComments(holder: FeedViewHolder, position: Int) {
        // Default Values
        holder.likeBtn.text = "0"
        holder.likesCount = 0
        holder.dislikesCount = 0
        holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.general_background))

        if (currentList[position].transaction.user_liked) {
            holder.likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
        } else if (currentList[position].transaction.user_disliked) {
            holder.likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
        } else {
            holder.likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
        }

        holder.commentBtn.text = currentList[position].transaction.comments_amount.toString()

        for (i in currentList[position].transaction.reactions) {
            if (i.code == "like") {
                holder.likeBtn.text = (i.counter).toString()
                holder.likesCount = i.counter
            } else if (i.code == "dislike") {
                holder.dislikesCount = i.counter

            }
        }


        holder.likeBtn.setOnClickListener {
            val mapReaction: Map<String, Int> = mapOf(
                "like_kind" to 1,
                "transaction" to currentList[position].transaction.id
            )
            likeClickListener?.invoke(mapReaction, position)
        }
        
    }


    private fun transactionToReceiver(
        holder: FeedViewHolder,
        position: Int,
        receiverId: Int?
    ): ClickableSpan {
        val clickableSpanReceive = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle: Bundle = Bundle()
                holder.userId = receiverId

                if (holder.userId != 0) {
                    holder.userId?.let {
                        bundle.putInt("userId", it)
                        view.findNavController()
                            .navigate(
                                R.id.action_feedFragment_to_someonesProfileFragment,
                                bundle,
                                OptionsTransaction().optionForProfileFragment
                            )
                    }

                }


            }
        }
        return clickableSpanReceive
    }

    private fun transactionToSender(
        holder: FeedViewHolder,
        position: Int,
        senderId: Int?
    ): ClickableSpan {
        val clickableSpanSender = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle: Bundle = Bundle()
                holder.userId = senderId

                val optionForProfileFragment = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
                    .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
                    .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
                    .setPopExitAnim(R.anim.bottom_in)
                    .build()

                if (holder.userId != 0) {
                    holder.userId?.let {
                        bundle.putInt("userId", it)
                        view.findNavController()
                            .navigate(
                                R.id.action_feedFragment_to_someonesProfileFragment,
                                bundle,
                                optionForProfileFragment
                            )
                    }

                }


            }
        }
        return clickableSpanSender
    }

    private fun setTags(tagsChipGroup: ChipGroup, tagList: List<TagModel>) {
        if (tagsChipGroup.children.none()) {
            for (i in tagList.indices) {
                val tagName = tagList[i].name
                val chip: Chip = LayoutInflater.from(tagsChipGroup.context)
                    .inflate(
                        R.layout.chip_tag_example_in_history_transaction,
                        tagsChipGroup,
                        false
                    ) as Chip
                with(chip) {
                    text = String.format(context.getString(R.string.setTag), tagName)
                    setEnsureMinTouchTargetSize(true)
                    minimumWidth = 0
                }

                tagsChipGroup.addView(chip)
            }
        }else{
            tagsChipGroup.removeAllViews()
            for (i in tagList.indices) {
                val tagName = tagList[i].name
                val chip: Chip = LayoutInflater.from(tagsChipGroup.context)
                    .inflate(
                        R.layout.chip_tag_example_in_history_transaction,
                        tagsChipGroup,
                        false
                    ) as Chip
                with(chip) {
                    text = String.format(context.getString(R.string.setTag), tagName)
                    setEnsureMinTouchTargetSize(true)
                    minimumWidth = 0
                }

                tagsChipGroup.addView(chip)
            }
        }

    }


    private fun convertDateToNecessaryFormat(holder: FeedViewHolder, position: Int) {
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
                String.format(holder.view.context.getString(R.string.dateTime), holder.date, holder.time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }

    companion object {
        val LIKES_COUNT = "likesCount"
        val DISLIKES_COUNT = "dislikesCount"
        val IS_LIKED = "isLiked"
        val IS_DISLIKED = "isDisliked"
        val TRANSACTION_ID = "transactionId"
    }


}