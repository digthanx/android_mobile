package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
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
        val chipGroup: ChipGroup = binding.chipGroup
        var reason: String? = null
        var photo: String? = null
        val standardGroup = binding.standardGroup
        var descriptionFeed = ""
        var date = ""
        var time = ""
        val view: View = binding.root
        var userId: Int? = null
        var clickReceiver: ClickableSpan? = null
        var clickSender: ClickableSpan? = null

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.senderAndReceiver.movementMethod = LinkMovementMethod.getInstance()
        holder.clickReceiver =
            transactionToReceiver(holder, position, currentList[position].transaction.recipient_id)

        holder.clickSender =
            transactionToSender(holder, position, currentList[position].transaction.sender_id)

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
            val spannable = SpannableStringBuilder(
                String.format(
                    holder.view.context.getString(
                        R.string.someoneToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore("."),
                    currentList[position].transaction.sender
                ))
            // Клик по получателю
            spannable.setSpan(holder.clickReceiver, 0, currentList[position].transaction.recipient.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.general_brand)),
                0,currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.general_brand)),
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length ,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            // Клик по отправителю
            spannable.setSpan(holder.clickSender,
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length ,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.minor_success)),
                currentList[position].transaction.recipient.length + 9,
                spannable.length - currentList[position].transaction.sender.length - 4,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.descriptionFeed = context.getString(
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
                ))
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.general_brand)),
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            // Клик по отправителю
            spannable.setSpan(holder.clickSender,
                spannable.length - currentList[position].transaction.sender.length - 1,
                spannable.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.minor_success)),
                 12,
                spannable.length - currentList[position].transaction.sender.length - 4,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.senderAndReceiver.text = spannable
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = context.getString(R.string.youGetFrom)
            // Я получатель
        } else {
            val spannable = SpannableStringBuilder(
                String.format(
                    holder.view.context.getString(
                        R.string.youToSomeone
                    ),
                    currentList[position].transaction.recipient,
                    currentList[position].transaction.amount.substringBefore(".")
                ))
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.general_brand)),
                0,currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            // Клик по получателю
            spannable.setSpan(holder.clickReceiver,
                0,currentList[position].transaction.recipient.length + 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(context.getColor(R.color.minor_success)),
                currentList[position].transaction.recipient.length + 9,
                spannable.length - 7,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            holder.senderAndReceiver.text = spannable
            holder.standardGroup.setBackgroundColor(holder.view.context.getColor(R.color.minor_success_secondary))
            holder.descriptionFeed = context.getString(R.string.getFrom)
            // Я отправитель
        }

        currentList[position].transaction.tags?.let { setTags(holder.chipGroup, it) }


        holder.reason = currentList[position].transaction.reason
        holder.photo = currentList[position].transaction.photo
        convertDataToNecessaryFormat(holder, position)
        holder.standardGroup.setOnClickListener { v ->
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


            }

            val optionForAdditionalInfoFeedFragment = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
                .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
                .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
                .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
                .build()

            v.findNavController()
                .navigate(R.id.action_feedFragment_to_additionalInfoFeedItemFragment, bundle, optionForAdditionalInfoFeedFragment)
        }

    }

    private fun transactionToReceiver(holder: FeedViewHolder, position: Int, receiverId: Int): ClickableSpan{
        val clickableSpanReceive = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle: Bundle = Bundle()
                holder.userId = receiverId
                holder.userId?.let {
                    bundle.putInt("userId", it)
                }

                val optionForProfileFragment = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
                    .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
                    .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
                    .setPopExitAnim(R.anim.bottom_in)
                    .build()
                view.findNavController()
                    .navigate(R.id.action_feedFragment_to_someonesProfileFragment, bundle, optionForProfileFragment)
            }
        }
        return  clickableSpanReceive
    }

    private fun transactionToSender(holder: FeedViewHolder, position: Int, senderId: Int): ClickableSpan{
        val clickableSpanSender = object : ClickableSpan() {
            override fun onClick(view: View) {
                val bundle: Bundle = Bundle()
                holder.userId = senderId
                holder.userId?.let {
                    bundle.putInt("userId", it)
                }

                val optionForProfileFragment = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
                    .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
                    .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
                    .setPopExitAnim(R.anim.bottom_in)
                    .build()
                view.findNavController()
                    .navigate(R.id.action_feedFragment_to_someonesProfileFragment, bundle, optionForProfileFragment)
            }
        }
        return clickableSpanSender
    }

    private fun setTags(tagsChipGroup: ChipGroup, tagList: List<TagModel>){
        for (i in tagList.indices) {
            val tagName = tagList[i].name
            val chip: Chip = LayoutInflater.from(tagsChipGroup.context)
                .inflate(R.layout.chip_tag_example_in_history_transaction, tagsChipGroup, false) as Chip
            with(chip) {
                text = String.format(context.getString(R.string.setTag), tagName)
                setEnsureMinTouchTargetSize(true)
                minimumWidth = 0
            }

            tagsChipGroup.addView(chip)
        }
    }


    private fun convertDataToNecessaryFormat(holder: FeedViewHolder, position: Int){
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
            holder.dateTime.text = String.format(context.getString(R.string.dateTime), holder.date, holder.time)
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
    }


}