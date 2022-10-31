package com.teamforce.thanksapp.presentation.adapter.feed

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemFeedBinding
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.utils.*

class NewFeedAdapter : PagingDataAdapter<FeedModel, NewFeedAdapter.ViewHolder>(DiffCallback) {

    var onChallengeClicked: ((challengeId: Int) -> Unit)? = null
    var onSomeonesClicked: ((userId: Int) -> Unit)? = null
    var onTransactionClicked: ((transactionId: Int) -> Unit)? = null
    var onWinnerClicked: ((challengeReportId: Int) -> Unit)? = null


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
        fun bind(item: FeedModel) {
            // Без этого клики по spannable не работают
            binding.senderAndReceiver.movementMethod = LinkMovementMethod.getInstance();
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
                ).append(
                    createClickableSpannable(
                        item.winnerTgName.username(),
                        R.color.general_brand
                    ) {
                        Log.d(TAG, "bindWinner: ${item.winnerTgName} clicked")
                        onSomeonesClicked?.invoke(item.winnerId)
                    },
                ).append(
                    createClickableSpannable(
                        " " +root.context.getString(R.string.challenge_winner) + " ",
                        R.color.black,
                        null)
                ).append(
                    createClickableSpannable(
                        item.challengeName.doubleQuoted(),
                        R.color.general_brand
                    ) {
                        Log.d(TAG, "bindChallengeName: ${item.challengeName} clicked")
                      //  onChallengeClicked?.invoke(item.id)
                    }
                )

                senderAndReceiver.text = spannable
                dateTime.text = item.time
                card.setOnClickListener {
                    onWinnerClicked?.invoke(item.reportId)
                }
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
                dateTime.text = item.time
                if (!item.challengePhoto.isNullOrEmpty()) {
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${item.challengePhoto}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(userAvatar)
                } else {
                    userAvatar.setImageResource(R.drawable.ic_anon_avatar)
                }
                val spannable = SpannableStringBuilder(
                ).append(
                    createClickableSpannable(
                        root.context.getString(R.string.challenge_created) + " ",
                        R.color.black,
                        null
                    )
                ).append(
                    createClickableSpannable(
                        item.challengeName.doubleQuoted() + " ",
                        R.color.general_brand,
                    ){
                        Log.d(TAG, "bindChallengeName: ${item.challengeName} clicked")
                        onChallengeClicked?.invoke(item.challengeId)
                    }
                ).append(
                    createClickableSpannable(
                        root.context.getString(R.string.whoCreatedChallenge) + " ",
                        R.color.black,
                        null
                    )
                ).append(
                    createClickableSpannable(
                        item.challengeCreatorTgName.username() + " ",
                        R.color.general_brand,
                    ){
                        Log.d(TAG, "bindChallengeCreator: ${item.challengeCreatorTgName} clicked")
                        onSomeonesClicked?.invoke(item.challengeCreatorId)
                    }
                ).append(
                    createClickableSpannable(
                        root.context.getString(R.string.toDate, item.challengeCreatedAt),
                        R.color.black,
                        null
                    )
                )
                senderAndReceiver.text = spannable
                card.setOnClickListener {
                    onChallengeClicked?.invoke(item.challengeId)
                }
            }
        }

        private fun bindTransaction(item: FeedModel.TransactionFeedEvent) {
            with(binding) {
                if (item.isWithMe) {
                    if(item.isFromMe){
                        // Я отправитель
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                item.transactionRecipientTgName,
                                R.color.general_brand
                            ){
                                Log.d(TAG, "bindRecipientName: ${item.transactionRecipientTgName} clicked")
                                onSomeonesClicked?.invoke(item.transactionRecipientId)
                            }
                        ).append(
                            createClickableSpannable(
                                " " + root.context.getString(R.string.got) + " ",
                                R.color.general_brand,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.amountThanks, item.likesAmount.toString()),
                                R.color.minor_success,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                " " + root.context.getString(R.string.from) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.fromYou) + " ",
                                R.color.black,
                                null
                            )
                        )
                        senderAndReceiver.text = spannable
                    }else{
                        // Я получатель
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.youGot) + " ",
                                R.color.general_brand,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.amountThanks, item.likesAmount.toString()),
                                R.color.minor_success,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                " " + root.context.getString(R.string.from) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                item.transactionSenderTgName + " ",
                                R.color.general_brand
                            ){
                                Log.d(TAG, "bindSender: ${item.transactionSenderTgName} clicked")
                                item.transactionSenderId?.let { onSomeonesClicked?.invoke(it) }
                            }
                        )
                        senderAndReceiver.text = spannable
                    }
                    toggleButtonGroup.visible()
                    card.setCardBackgroundColor(root.context.getColor(R.color.minor_success_secondary))
                    likeBtn.setBackgroundColor(root.context.getColor(R.color.white))
                    dislikeBtn.setBackgroundColor(root.context.getColor(R.color.white))
                    commentBtn.setBackgroundColor(root.context.getColor(R.color.white))

                } else {
                    card.setCardBackgroundColor(root.context.getColor(R.color.general_background))
                    toggleButtonGroup.visible()
                    val spannable = SpannableStringBuilder(
                    ).append(
                        createClickableSpannable(
                            item.transactionRecipientTgName.username(),
                            R.color.general_brand
                        ){
                            Log.d(TAG, "bindRecipient: ${item.transactionRecipientTgName} clicked")
                            onSomeonesClicked?.invoke(item.transactionRecipientId)
                        }
                    ).append(
                        createClickableSpannable(
                            " " + root.context.getString(R.string.got) + " ",
                            R.color.black,
                            null
                        )
                    ).append(
                        createClickableSpannable(
                            root.context.getString(R.string.amountThanks, item.likesAmount.toString()),
                            R.color.minor_success,
                            null
                        )
                    ).append(
                        createClickableSpannable(
                            " " + root.context.getString(R.string.from) + " ",
                            R.color.black,
                            null
                        )
                    ).append(
                        createClickableSpannable(
                            item.transactionSenderTgName.username() + " ",
                            R.color.general_brand
                        ){
                            Log.d(TAG, "bindSender: ${item.transactionSenderTgName} clicked")
                            item.transactionSenderId?.let { onSomeonesClicked?.invoke(it) }
                        }
                    )
                    senderAndReceiver.text = spannable
                }
                likeBtn.text = item.likesAmount.toString()
                commentBtn.text = item.commentAmount.toString()
                dateTime.text = item.time
                card.setOnClickListener {
                    onTransactionClicked?.invoke(item.transactionId)
                }
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
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
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