package com.teamforce.thanksapp.presentation.adapter.feed

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
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
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

class FeedPageAdapter(
    private val username: String,
    private val onLikeClicked: (FeedResponse, Int) -> Unit,
    private val onDislikeClicked: (FeedResponse, Int) -> Unit,
) : PagingDataAdapter<FeedResponse, FeedPageAdapter.ViewHolder>(DiffCallback) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItem(position) != null) {
            holder.bind(getItem(position)!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        var descriptionFeed = ""

        var date = ""
        var time = ""
        val view: View = binding.root
        var userId: Int? = null
        var clickReceiver: ClickableSpan? = null
        var clickSender: ClickableSpan? = null
        var likesCount: Int = 0
        var dislikesCount: Int = 0
        fun bind(data: FeedResponse) {
            with(binding) {
                bindLikesAndComments(this@ViewHolder, data)

                senderAndReceiver.movementMethod = LinkMovementMethod.getInstance()
                clickReceiver =
                    transactionToReceiver(this@ViewHolder, data)

                clickSender =
                    transactionToSender(this@ViewHolder, data)

                if (!data.transaction.recipient_photo.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${data.transaction.recipient_photo}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userAvatar)
                } else {
                    userAvatar.setImageResource(R.drawable.ic_anon_avatar)
                }

                distinguishSenderAndReceiver(this@ViewHolder, data)

                data.transaction.tags?.let { setTags(chipGroup, it) }

                convertDateToNecessaryFormat(this@ViewHolder, data)

                standardGroup.setOnClickListener { _ ->
                    transactionToAdditionInfo(this@ViewHolder, data)
                }

                commentBtn.setOnClickListener { _ ->
                    transactionToAdditionInfo(this@ViewHolder, data)
                }
            }
        }
    }

    private fun transactionToAdditionInfo(holder: ViewHolder, data: FeedResponse) {
        with(holder.binding) {
            val bundle = Bundle()
            bundle.apply {
                putString(
                    Consts.AVATAR_USER,
                    "${Consts.BASE_URL}${data.transaction.recipient_photo}"
                )
                putString(Consts.DATE_TRANSACTION, dateTime.text.toString())
                putString(Consts.DESCRIPTION_FEED, holder.descriptionFeed)
                putString(Consts.SENDER_TG, data.transaction.sender)
                putString(Consts.RECEIVER_TG, data.transaction.recipient)
                putString(Consts.PHOTO_TRANSACTION, data.transaction.photo)
                putString(Consts.REASON_TRANSACTION, data.transaction.reason)
                putString(
                    Consts.AMOUNT_THANKS,
                    data.transaction.amount.substringBefore(".")
                )
                putInt(FeedAdapter.LIKES_COUNT, holder.likesCount)
                putInt(FeedAdapter.DISLIKES_COUNT, holder.dislikesCount)
                putBoolean(FeedAdapter.IS_LIKED, data.transaction.user_liked)
                putBoolean(FeedAdapter.IS_DISLIKED, data.transaction.user_disliked)
                putInt(FeedAdapter.TRANSACTION_ID, data.transaction.id)
                data.transaction.recipient_id?.let {
                    this.putInt("userIdReceiver", it)
                }

                data.transaction.sender_id?.let {
                    this.putInt("userIdSender", it)
                }
            }
            root.findNavController()
                .navigate(
                    R.id.action_feedFragment_to_additionalInfoFeedItemFragment,
                    bundle,
                    OptionsTransaction().optionForAdditionalInfoFeedFragment
                )
        }
    }

    private fun bindLikesAndComments(holder: ViewHolder, data: FeedResponse) {
        with(holder.binding) {
            // Default Values
            likeBtn.text = "0"
            dislikeBtn.text = "0"
            holder.likesCount = 0
            holder.dislikesCount = 0
            standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.general_background))

            if (data.transaction.user_liked) {
                likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
                dislikeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
            } else if (data.transaction.user_disliked) {
                dislikeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_error_secondary))
                likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
            } else {
                dislikeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
                likeBtn.setBackgroundColor(holder.view.context.getColor(R.color.minor_info_secondary))
            }

            commentBtn.text = data.transaction.comments_amount.toString()

            for (i in data.transaction.reactions) {
                if (i.code == "like") {
                    likeBtn.text = (i.counter).toString()
                    holder.likesCount = i.counter
                } else if (i.code == "dislike") {
                    dislikeBtn.text = (i.counter).toString()
                    holder.dislikesCount = i.counter

                }
            }


            likeBtn.setOnClickListener {
                onLikeClicked(data, holder.layoutPosition)
            }

            dislikeBtn.setOnClickListener {
                onDislikeClicked(data, holder.layoutPosition)
            }
        }

    }


    private fun transactionToReceiver(
        holder: ViewHolder, data: FeedResponse
    ): ClickableSpan {
        val clickableSpanReceive = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle: Bundle = Bundle()
                holder.userId = data.transaction.recipient_id
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
        holder: ViewHolder, data: FeedResponse
    ): ClickableSpan {
        val clickableSpanSender = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle = Bundle()
                holder.userId = data.transaction.sender_id

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
        }

    }

    private fun distinguishSenderAndReceiver(holder: ViewHolder, data: FeedResponse) {
        with(holder.binding) {
            if (data.transaction.sender != username &&
                data.transaction.recipient != username
            ) {
                val spannable = SpannableStringBuilder(
                    String.format(
                        holder.view.context.getString(
                            R.string.someoneToSomeone
                        ),
                        data.transaction.recipient,
                        data.transaction.amount.substringBefore("."),
                        data.transaction.sender
                    )
                )
                // Клик по получателю
                spannable.setSpan(
                    holder.clickReceiver, 0, data.transaction.recipient.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                    0, data.transaction.recipient.length + 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                    spannable.length - data.transaction.sender.length - 1,
                    spannable.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                // Клик по отправителю
                spannable.setSpan(
                    holder.clickSender,
                    spannable.length - data.transaction.sender.length - 1,
                    spannable.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                    data.transaction.recipient.length + 9,
                    spannable.length - data.transaction.sender.length - 4,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                holder.descriptionFeed = holder.view.context.getString(
                    R.string.getFrom
                )
                senderAndReceiver.text = spannable
            } else if (data.transaction.sender != username) {
                val spannable = SpannableStringBuilder(
                    String.format(
                        holder.view.context.getString(
                            R.string.youFromSomeone
                        ),
                        data.transaction.amount.substringBefore("."),
                        data.transaction.sender
                    )
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                    spannable.length - data.transaction.sender.length - 1,
                    spannable.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                // Клик по отправителю
                spannable.setSpan(
                    holder.clickSender,
                    spannable.length - data.transaction.sender.length - 1,
                    spannable.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                    12,
                    spannable.length - data.transaction.sender.length - 4,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                senderAndReceiver.text = spannable
                standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
                holder.descriptionFeed = holder.view.context.getString(R.string.youGetFrom)
                // Я получатель
            } else {
                val spannable = SpannableStringBuilder(
                    String.format(
                        holder.view.context.getString(
                            R.string.youToSomeone
                        ),
                        data.transaction.recipient,
                        data.transaction.amount.substringBefore(".")
                    )
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.general_brand)),
                    0, data.transaction.recipient.length + 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                // Клик по получателю
                spannable.setSpan(
                    holder.clickReceiver,
                    0, data.transaction.recipient.length + 1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(holder.view.context.getColor(R.color.minor_success)),
                    data.transaction.recipient.length + 9,
                    spannable.length - 7,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                senderAndReceiver.text = spannable
                standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
                holder.descriptionFeed = holder.view.context.getString(R.string.getFrom)
                // Я отправитель
            }
        }
    }

    private fun convertDateToNecessaryFormat(holder: ViewHolder, data: FeedResponse) {
        with(holder.binding) {

            try {
                val zdt: ZonedDateTime =
                    ZonedDateTime.parse(data.time, DateTimeFormatter.ISO_DATE_TIME)
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
                this.dateTime.text =
                    String.format(
                        holder.view.context.getString(R.string.dateTime),
                        holder.date,
                        holder.time
                    )
            } catch (e: Exception) {
                Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
            }
        }
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FeedResponse>() {
            override fun areItemsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: FeedResponse, newItem: FeedResponse): Boolean {
                return newItem == oldItem
            }

        }
    }

    fun like(position: Int) {
        if (getItem(position) != null) {
            if (!getItem(position)!!.transaction.user_liked) {
                getItem(position)!!.transaction.apply {
                    reactions[1].counter++
                    user_liked = true
                }
                if (getItem(position)!!.transaction.user_disliked) {
                    getItem(position)!!.transaction.apply {
                        reactions[0].counter--
                        user_disliked = false
                    }
                }
                notifyItemChanged(position)
            }
        }
    }

    fun dislike(position: Int) {

        if (getItem(position) != null) {
            if(!getItem(position)!!.transaction.user_disliked) {
                getItem(position)!!.transaction.apply {
                    reactions[0].counter++
                    user_disliked = true
                }

                if(getItem(position)!!.transaction.user_liked) {
                    getItem(position)!!.transaction.apply {
                        reactions[1].counter--
                        user_liked = false
                    }
                }
                notifyItemChanged(position)
            }

        }
    }
}