package com.teamforce.thanksapp.presentation.adapter.notifications

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
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.PushNotificationService
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemNotificationBinding
import com.teamforce.thanksapp.databinding.SeparatorDateTimeBinding
import com.teamforce.thanksapp.domain.models.notifications.NotificationAdditionalData
import com.teamforce.thanksapp.domain.models.notifications.NotificationItem
import com.teamforce.thanksapp.presentation.activity.MainActivity
import com.teamforce.thanksapp.presentation.adapter.feed.NewFeedAdapter
import com.teamforce.thanksapp.utils.doubleQuoted
import com.teamforce.thanksapp.utils.username
import java.lang.NumberFormatException

class NotificationPageAdapter(
    private val onUserClicked: (userId: Int) -> Unit,
    private val onTransactionClicked: (transactionId: Int) -> Unit,
    private val onChallengeClicked: (challengeId: Int) -> Unit,

    ) : PagingDataAdapter<NotificationItem, RecyclerView.ViewHolder>(DIffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is NotificationItem.NotificationModel -> {
                return when (item.data) {
                    is NotificationAdditionalData.NotificationChallengeDataModel -> CHALLENGE_VIEW_TYPE
                    is NotificationAdditionalData.NotificationTransactionDataModel -> TRANSACTION_VIEW_TYPE
                    is NotificationAdditionalData.NotificationCommentDataModel -> COMMENT_VIEW_TYPE
                    is NotificationAdditionalData.NotificationReactionDataModel -> LIKE_VIEW_TYPE
                    is NotificationAdditionalData.NotificationChallengeWinnerDataModel -> WINNER_VIEW_TYPE
                    is NotificationAdditionalData.NotificationChallengeReportDataModel -> CHALLENGE_REPORT_VIEW_TYPE
                    else -> UNKNOWN_VIEW_TYPE
                }
            }
            is NotificationItem.DateTimeSeparator -> {
                DATE_TIME_SEPARATOR_VIEW_TYPE
            }
            null -> {
                throw UnsupportedOperationException("Unknown view")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(PushNotificationService.TAG, "onCreateViewHolder: $viewType")
        return when (viewType) {
            TRANSACTION_VIEW_TYPE, CHALLENGE_VIEW_TYPE, COMMENT_VIEW_TYPE, LIKE_VIEW_TYPE,
            WINNER_VIEW_TYPE, CHALLENGE_REPORT_VIEW_TYPE -> {
                val binding = ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                NotificationViewHolder(binding)
            }
            else -> {
                val binding = SeparatorDateTimeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                SeparatorViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (item) {
                is NotificationItem.NotificationModel -> {
                    val viewHolder = holder as NotificationViewHolder
                    viewHolder.bind(item)
                }
                is NotificationItem.DateTimeSeparator -> {
                    val viewHolder = holder as SeparatorViewHolder
                    viewHolder.bind(item)
                }
            }
        }
    }

    class SeparatorViewHolder(
        private val binding: SeparatorDateTimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationItem.DateTimeSeparator) {
            binding.apply {
                separatorText.text = data.date
            }
        }
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationItem.NotificationModel) {
            binding.dateTime.text = data.createdAtHumanView
            binding.description.movementMethod = LinkMovementMethod.getInstance();

            when (data.data) {
                is NotificationAdditionalData.NotificationTransactionDataModel -> bindTransaction(
                    data
                )
                is NotificationAdditionalData.NotificationCommentDataModel -> bindComment(data)
                is NotificationAdditionalData.NotificationReactionDataModel -> bindReaction(data)
                is NotificationAdditionalData.NotificationChallengeDataModel -> bindChallenge(data)
                is NotificationAdditionalData.NotificationChallengeReportDataModel -> bindChallenge(
                    data
                )
                is NotificationAdditionalData.NotificationChallengeWinnerDataModel -> bindWinner(
                    data
                )
                else -> {
                    binding.apply {
                        description.text = data.theme
                    }
                }
            }
        }

        private fun bindTransaction(data: NotificationItem.NotificationModel) {
            val data = data.data as NotificationAdditionalData.NotificationTransactionDataModel
            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(
                        createClickableSpannable(
                            root.context.getString(R.string.received_part) + " ",
                            R.color.black,
                            null
                        )
                    )
                    .append(
                        createClickableSpannable(
                            binding.root.context.getString(
                                R.string.amountThanks,
                                data.amount.toString()
                            ) + " ",
                            R.color.minor_success,
                            null
                        )
                    ).append(
                        root.context.getString(R.string.from) + " "
                    ).append(
                        createClickableSpannable(
                            data.senderTgName.username(),
                            R.color.general_brand,
                        ) {
                            if (data.senderId != null) {
                                try {
                                    onUserClicked(data.senderId.toInt())
                                } catch (e: NumberFormatException) {

                                }
                            }
                        }
                    )
                binding.description.text = spannable
            }

            binding.root.setOnClickListener {
                onTransactionClicked(data.transactionId)
            }
        }

        private fun bindComment(data: NotificationItem.NotificationModel) {
            val targetType: Targets
            val data = data.data as NotificationAdditionalData.NotificationCommentDataModel
            val target =
                if (data.transactionId != null) {
                    targetType = Targets.Transaction
                    binding.root.context.getString(R.string.to_transaction)
                } else if (data.challengeId != null) {
                    targetType = Targets.Challenge
                    binding.root.context.getString(R.string.to_challenge)
                } else {
                    targetType = Targets.Report
                    binding.root.context.getString(R.string.to_your_report)
                }

            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(
                        root.context.getString(R.string.new_comment_to) + " "
                    )
                    .append(createClickableSpannable(target, R.color.general_brand) {
                        when (targetType) {
                            Targets.Challenge -> {
                                if (data.challengeId != null) {
                                    onChallengeClicked(data.challengeId)
                                }
                            }
                            Targets.Transaction -> {
                                if (data.transactionId != null) {
                                    onTransactionClicked(data.transactionId)
                                }
                            }
                            else -> {
                                //nothing to do
                            }
                        }
                    })
                description.text = spannable
            }
        }

        private fun bindReaction(data: NotificationItem.NotificationModel) {
            val data = data.data as NotificationAdditionalData.NotificationReactionDataModel
            val targetType: Targets

            val target =
                if (data.challengeId != null) {
                    targetType = Targets.Challenge
                    binding.root.context.getString(R.string.challenge)
                } else if (data.transactionId != null) {
                    targetType = Targets.Transaction
                    binding.root.context.getString(R.string.transaction)
                } else {
                    targetType = Targets.Report
                    binding.root.context.getString(R.string.your_report)
                }

            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(
                        root.context.getString(R.string.new_reaction_to) + " "
                    )
                    .append(createClickableSpannable(
                        target,
                        R.color.general_brand
                    ) {
                        when (targetType) {
                            Targets.Transaction -> {
                                if (data.transactionId != null) {
                                    onTransactionClicked(data.transactionId)
                                }
                            }
                            Targets.Challenge -> {
                                if (data.challengeId != null) {
                                    onChallengeClicked(data.challengeId)
                                }
                            }
                            else -> {
                                //nothing to do
                            }
                        }
                    })
                description.text = spannable
            }
        }

        private fun bindChallenge(data: NotificationItem.NotificationModel) {
            val data = data.data as NotificationAdditionalData.NotificationChallengeDataModel
            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(root.context.getString(R.string.new_challenge) + " ")
                    .append(createClickableSpannable(
                        data.challengeName.doubleQuoted(),
                        R.color.general_brand,
                    ) {
                        onChallengeClicked(data.challengeId)
                    })
                description.text = spannable
            }

        }

        private fun bindReport(data: NotificationItem.NotificationModel) {
            val data = data.data as NotificationAdditionalData.NotificationChallengeReportDataModel
            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(root.context.getString(R.string.new_report_to_challenge) + " ")
                    .append(createClickableSpannable(
                        data.challengeName.doubleQuoted(),
                        R.color.general_brand
                    ) {
                        onChallengeClicked(data.challengeId)
                    })
                description.text = spannable
            }
        }

        private fun bindWinner(data: NotificationItem.NotificationModel) {
            val data = data.data as NotificationAdditionalData.NotificationChallengeWinnerDataModel
            binding.apply {
                val spannable = SpannableStringBuilder()
                    .append(root.context.getString(R.string.you_win_challenge) + " ")
                    .append(createClickableSpannable(
                        data.challengeName.doubleQuoted(),
                        R.color.general_brand
                    ) {
                        onChallengeClicked(data.challengeId)
                    })
                description.text = spannable
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
                    Log.d(NewFeedAdapter.TAG, "onClick: ")
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
        const val TRANSACTION_VIEW_TYPE = 1
        const val CHALLENGE_VIEW_TYPE = 2
        const val UNKNOWN_VIEW_TYPE = 3
        const val DATE_TIME_SEPARATOR_VIEW_TYPE = 4
        const val CHALLENGE_REPORT_VIEW_TYPE = 5
        const val COMMENT_VIEW_TYPE = 6
        const val LIKE_VIEW_TYPE = 7
        const val WINNER_VIEW_TYPE = 8


        private val DIffCallback = object : DiffUtil.ItemCallback<NotificationItem>() {
            override fun areItemsTheSame(
                oldItem: NotificationItem,
                newItem: NotificationItem
            ): Boolean {
                return (oldItem is NotificationItem.NotificationModel &&
                        newItem is NotificationItem.NotificationModel &&
                        oldItem.id == newItem.id) ||
                        (oldItem is NotificationItem.DateTimeSeparator &&
                                newItem is NotificationItem.DateTimeSeparator &&
                                oldItem.date == newItem.date)
            }

            override fun areContentsTheSame(
                oldItem: NotificationItem,
                newItem: NotificationItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

enum class Targets {
    Challenge,
    Transaction,
    Report
}