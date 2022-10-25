package com.teamforce.thanksapp.presentation.adapter.feed

import android.graphics.Color
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemFeedBinding
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.utils.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NewFeedAdapter : PagingDataAdapter<FeedModel, NewFeedAdapter.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItem(position) != null) {
            holder.bind(getItem(position)!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    class ViewHolder(val binding: ItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FeedModel) {
            when (item) {
                is FeedModel.TransactionFeedEvent -> bindTransaction(item)
                is FeedModel.ChallengeFeedEvent -> bindChallenge(item)
                is FeedModel.WinnerFeedEvent -> bindWinner(item)
            }
        }

        private fun bindWinner(item: FeedModel.WinnerFeedEvent) {
            with(binding) {
                toggleButtonGroup.visible()
                likeBtn.text = item.likesAmount.toString()
                commentBtn.text = item.commentAmount.toString()

                val spannable = SpannableStringBuilder(
//                    root.context.getString(
//                        R.string.challenge_winner,
//                        createClickableSpannable(
//                            item.winnerTgName.username(),
//                            R.color.general_brand
//                        ) {
//                            Log.d(TAG, "bindWinner: ${item.winnerTgName} clicked")
//                        },
//                        createClickableSpannable(
//                            item.challengeName.doubleQuoted(),
//                            R.color.general_brand
//                        ) {
//                            Log.d(TAG, "bindWinner: ${item.challengeName} clicked")
//                        }
//                    )
                ).append(
                    createClickableSpannable(
                        item.winnerTgName.username(),
                        R.color.general_brand
                    ) {
                        Log.d(TAG, "bindWinner: ${item.winnerTgName} clicked")
                    },
                ).append(
                    createClickableSpannable(" " +root.context.getString(R.string.challenge_winner) + " ", R.color.black, null)
                ).append(
                    createClickableSpannable(
                        item.challengeName.doubleQuoted(),
                        R.color.general_brand
                    ) {
                        Log.d(TAG, "bindWinner: ${item.challengeName} clicked")
                    }
                )

                senderAndReceiver.text = spannable
                dateTime.text = item.time
                if (!item.winnerPhoto.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${item.winnerPhoto}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userAvatar)
                } else {
                    userAvatar.setImageResource(R.drawable.ic_anon_avatar)
                }
            }
        }

        private fun bindChallenge(item: FeedModel.ChallengeFeedEvent) {
            with(binding) {
                toggleButtonGroup.invisible()
                senderAndReceiver.text = binding.root.context.getString(
                    R.string.challenge_created,
                    item.challengeName.doubleQuoted(),
                    item.challengeCreatorTgName.username(),
                    item.challengeCreatedAt
                )
                dateTime.text = item.time
                if (!item.challengePhoto.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${item.challengePhoto}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userAvatar)
                } else {
                    userAvatar.setImageResource(R.drawable.ic_anon_avatar)
                }
            }
        }

        private fun bindTransaction(item: FeedModel.TransactionFeedEvent) {
            with(binding) {
                if (item.isForMe) {
                    toggleButtonGroup.visible()


                    senderAndReceiver.text = root.context.getString(
                        R.string.you_received_thanks,
                        item.transactionAmount,
                        item.transactionSenderTgName.username()
                    )
                    card.setCardBackgroundColor(root.context.getColor(R.color.minor_success_secondary))
                    likeBtn.setBackgroundColor(root.context.getColor(R.color.white))
                    dislikeBtn.setBackgroundColor(root.context.getColor(R.color.white))
                    commentBtn.setBackgroundColor(root.context.getColor(R.color.white))

                } else {
                    toggleButtonGroup.visible()
                    senderAndReceiver.text = root.context.getString(
                        R.string.user_received_thanks,
                        item.transactionRecipientTgName.username(),
                        item.transactionAmount,
                        item.transactionSenderTgName.username()
                    )

                }
                likeBtn.text = item.likesAmount.toString()
                commentBtn.text = item.commentAmount.toString()
                dateTime.text = item.time
                if (!item.transactionRecipientPhoto.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${item.transactionRecipientPhoto}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userAvatar)
                } else {
                    userAvatar.setImageResource(R.drawable.ic_anon_avatar)
                }
            }
        }

        private fun createClickableSpannable(
            string: String,
            @ColorRes color: Int,
            onClick: (() -> Unit)?
        ): SpannableString {
            val spannableString = SpannableString(string)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    Log.d(TAG, "onClick: ")
                    onClick?.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.color = binding.root.context.getColor(color)
                }
            }

            spannableString.setSpan(
                clickableSpan,
                0,
                string.length,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            return spannableString

        }
    }


    companion object {
        const val TAG = "NewFeedAdapter"
        private val DiffCallback = object : DiffUtil.ItemCallback<FeedModel>() {
            override fun areItemsTheSame(
                oldItem: FeedModel,
                newItem: FeedModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FeedModel,
                newItem: FeedModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}