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
import com.teamforce.thanksapp.utils.*

class NewFeedAdapter() : PagingDataAdapter<FeedModel, NewFeedAdapter.ViewHolder>(DiffCallback) {

    var onChallengeClicked: ((challengeId: Int) -> Unit)? = null
    var onSomeonesClicked: ((userId: Int) -> Unit)? = null
    var onTransactionClicked: ((transactionId: Int) -> Unit)? = null
    var onWinnerClicked: ((challengeReportId: Int, challengeId: Int) -> Unit)? = null
    var onLikeClicked: ((transactionId: Int) -> Unit)? = null
    var onLikeLongClicked: ((transactionId: Int, pagePosition: Int) -> Unit)? = null
    var onCommentClicked: ((transactionId: Int, pagePosition: Int) -> Unit)? = null


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
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
                toggleButtonGroup.invisible()
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
                        " " + root.context.getString(R.string.challenge_winner) + " ",
                        R.color.black,
                        null
                    )
                ).append(
                    createClickableSpannable(
                        item.challengeName.doubleQuoted(),
                        R.color.general_brand
                    ) {
                        Log.d(TAG, "bindChallengeName: ${item.challengeName} clicked")
                        onChallengeClicked?.invoke(item.challengeId)
                    }
                )

                senderAndReceiver.text = spannable
                dateTime.text = item.time
                card.setOnClickListener {
                    onWinnerClicked?.invoke(item.reportId, item.challengeId)
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
                    ) {
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
                    ) {
                        Log.d(TAG, "bindChallengeCreator: ${item.challengeCreatorTgName} clicked")
                        onSomeonesClicked?.invoke(item.challengeCreatorId)
                    }
                )
                // Проверка даты на пустоту
                if (item.challengeEndAt != "" && item.challengeEndAt != "null" && item.challengeEndAt != "Не определено") {
                    spannable.append(
                        createClickableSpannable(
                            root.context.getString(R.string.toDate, item.challengeEndAt),
                            R.color.black,
                            null
                        )
                    )
                }
                senderAndReceiver.text = spannable
                card.setOnClickListener {
                    onChallengeClicked?.invoke(item.challengeId)
                }
            }
        }

        private fun bindTransaction(item: FeedModel.TransactionFeedEvent) {
            with(binding) {
                if (item.isWithMe) {
                    if (item.isFromMe) {
                        // Я отправитель
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                item.transactionRecipientTgName,
                                R.color.general_brand
                            ) {
                                Log.d(
                                    TAG,
                                    "bindRecipientName: ${item.transactionRecipientTgName} clicked"
                                )
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
                                root.context.getString(
                                    R.string.amountThanks,
                                    item.transactionAmount.toString()                                ),
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
                    } else {
                        // Я получатель
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.youGot) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(
                                    R.string.amountThanks,
                                    item.transactionAmount.toString()
                                ),
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
                            ) {
                                Log.d(TAG, "bindSender: ${item.transactionSenderTgName} clicked")
                                item.transactionSenderId?.let { onSomeonesClicked?.invoke(it) }
                            }
                        )
                        senderAndReceiver.text = spannable
                    }
                    toggleButtonGroup.visible()
                    card.setCardBackgroundColor(root.context.getColor(R.color.minor_success_secondary))
                    likeBtn.setBackgroundColor(root.context.getColor(R.color.white))
                    commentBtn.setBackgroundColor(root.context.getColor(R.color.white))

                } else {
                    card.setCardBackgroundColor(root.context.getColor(R.color.general_background))
                    toggleButtonGroup.visible()
                    val spannable = SpannableStringBuilder(
                    ).append(
                        createClickableSpannable(
                            item.transactionRecipientTgName.username(),
                            R.color.general_brand
                        ) {
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
                            root.context.getString(
                                R.string.amountThanks,
                                item.transactionAmount.toString()
                            ),
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
                        ) {
                            Log.d(TAG, "bindSender: ${item.transactionSenderTgName} clicked")
                            item.transactionSenderId?.let { onSomeonesClicked?.invoke(it) }
                        }
                    )
                    senderAndReceiver.text = spannable
                }
                // Установка базовых полей(независимых от условия)
                setLikes(item)
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
                // Слушатели
                card.setOnClickListener {
                    onTransactionClicked?.invoke(item.transactionId)
                }
                likeBtn.setOnClickListener {
                    updateLikeByClick(item,)
                    onLikeClicked?.invoke(item.transactionId)
                }
                commentBtn.setOnClickListener {
                    onCommentClicked?.invoke(item.transactionId, 1)
                }
                likeBtn.setOnLongClickListener {
                    onLikeLongClicked?.invoke(item.transactionId, 2)
                    return@setOnLongClickListener true
                }
            }
        }

        private fun setLikes(item: FeedModel.TransactionFeedEvent) {
            binding.likeBtn.text = item.likesAmount.toString()
            if (item.userLiked) {
                if (item.isWithMe) binding.likeBtn.setBackgroundColor(
                    binding.root.context.getColor(
                        R.color.minor_success
                    )
                )
                else binding.likeBtn.setBackgroundColor(binding.root.context.getColor(R.color.minor_success_secondary))
            } else {
                if (item.isWithMe) binding.likeBtn.setBackgroundColor(
                    binding.root.context.getColor(
                        R.color.white
                    )
                )
                else binding.likeBtn.setBackgroundColor(binding.root.context.getColor(R.color.minor_info_secondary))

            }
        }

        private fun updateLikeByClick(item: FeedModel.TransactionFeedEvent) {
            if (item.userLiked) {
                item.userLiked = false
                binding.likeBtn.text = (item.likesAmount?.minus(1)).toString()
                item.likesAmount = item.likesAmount?.minus(1)
                if (item.isWithMe) binding.likeBtn.setBackgroundColor(
                    binding.root.context.getColor(
                        R.color.white
                    )
                )
                else binding.likeBtn.setBackgroundColor(binding.root.context.getColor(R.color.minor_info_secondary))
            } else {
                item.userLiked = true
                binding.likeBtn.text = (item.likesAmount?.plus(1)).toString()
                item.likesAmount = item.likesAmount?.plus(1)

                if (item.isWithMe) binding.likeBtn.setBackgroundColor(
                    binding.root.context.getColor(
                        R.color.minor_success
                    )
                )
                else binding.likeBtn.setBackgroundColor(binding.root.context.getColor(R.color.minor_success_secondary))
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