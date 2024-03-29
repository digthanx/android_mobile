package com.teamforce.thanksapp.presentation.adapter.history

import android.os.Bundle
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
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.databinding.ItemTransferBinding
import com.teamforce.thanksapp.databinding.SeparatorDateTimeBinding
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.presentation.adapter.feed.NewFeedAdapter
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.doubleQuoted
import java.lang.UnsupportedOperationException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HistoryPageAdapter(
    private val username: String,
    private val onCancelClicked: (id: Int) -> Unit,
    private val onSomeonesClicked: (userId: Int) -> Unit,
    private val onChallengeClicked: (challengeId: Int) -> Unit
) : PagingDataAdapter<HistoryItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_transfer -> {
                val binding = ItemTransferBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HistoryItemViewHolder(binding)
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
                is HistoryItem.DateTimeSeparator -> {
                    val viewHolder = holder as SeparatorViewHolder
                    viewHolder.bind(item)
                }
                is HistoryItem.UserTransactionsResponse -> {
                    val viewHolder = holder as HistoryItemViewHolder
                    viewHolder.bind(
                        item, username, onCancelClicked,
                        onChallengeClicked, onSomeonesClicked
                    )
                }

            }
        }
    }

    class SeparatorViewHolder(
        private val binding: SeparatorDateTimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HistoryItem.DateTimeSeparator) {
            binding.apply {
                separatorText.text = data.date
            }
        }
    }

    inner class HistoryItemViewHolder(
        private val binding: ItemTransferBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        var photoFromSender: String? = null
        var userId: Int? = null

        var dateGetInfo: String = "null"
        var labelStatusTransaction: String = "null"
        var descr_transaction_1: String = "null"
        var comingStatusTransaction: String = "null"
        var weRefusedYourOperation: String? = null
        var avatar: String? = null
        var date: String = ""

        fun bind(
            data: HistoryItem.UserTransactionsResponse, username: String,
            onCancelClicked: (id: Int) -> Unit,
            onChallengeClicked: (challengeId: Int) -> Unit,
            onSomeonesClicked: (userId: Int) -> Unit
        ) {
            // Без этого клики по spannable не работают
            binding.message.movementMethod = LinkMovementMethod.getInstance()
            binding.apply {
                root.id = data.id
                val status = data.transaction_status.id
                transferIconIv.setImageResource(R.drawable.ic_anon_avatar)
                if (data.canUserCancel == true) {
                    refuseTransactionBtn.visibility = View.VISIBLE
                    refuseTransactionBtn.setOnClickListener {
                        onCancelClicked(data.id)
                    }
                } else {
                    refuseTransactionBtn.visibility = View.GONE

                }
                if (data.sender?.sender_tg_name != "anonymous" &&
                    data.sender?.sender_tg_name == username
                ) {
                    if (data.transactionClass.id == "H") {
                        // Я отправитель
                        // В случае создания челленджа
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.youSended) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(
                                    R.string.amountThanks,
                                    data.amount
                                ) + " ",
                                R.color.minor_success,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                " " + root.context.getString(R.string.inFundOfChallenge) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                data.sender?.challenge_name?.doubleQuoted() ?: "",
                                R.color.general_brand
                            ) {
                                data.sender?.challenge_id?.let { onChallengeClicked(it) }
                            }
                        )
                        message.text = spannable

                    }else{
                        // Я отправитель
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                root.context.getString(R.string.youSended) + " ",
                                R.color.black,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                root.context.getString(
                                    R.string.amountThanks,
                                    data.amount
                                ) + " ",
                                R.color.minor_success,
                                null
                            )
                        ).append(
                            createClickableSpannable(
                                data.recipient?.recipient_surname + " " +
                                        data.recipient?.recipient_first_name + " ",
                                R.color.general_brand
                            ) {
                                data.recipient_id?.let {
                                    onSomeonesClicked(it)
                                }
                            }
                        )
                        descr_transaction_1 = binding.root.context.getString(R.string.youSended)
                        labelStatusTransaction = binding.root.context.getString(R.string.statusTransfer)
                        message.text = spannable
                    }

                    if (!data.recipient?.recipient_photo.isNullOrEmpty()) {
                        Glide.with(binding.root.context)
                            .load("${Consts.BASE_URL}${data.recipient?.recipient_photo}".toUri())
                            .apply(RequestOptions.bitmapTransform(CircleCrop()))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(transferIconIv)
                        avatar =
                            "${Consts.BASE_URL}${data.recipient?.recipient_photo}"
                    }
                    setStatusTransaction(data)
                } else {
                    if (data.sender?.sender_tg_name == "anonymous") {
                        // Я получатель от анонима
                        descr_transaction_1 = binding.root.context.getString(R.string.youGot)
                        tgNameUser.text = " Аноним"
                        labelStatusTransaction =
                            binding.root.context.getString(R.string.typeTransfer)
                        valueTransfer.text = "+ " + data.amount
                        comingStatusTransaction =
                            binding.root.context.getString(R.string.comingTransfer)
                        val spannable = SpannableStringBuilder(
                        ).append(
                            createClickableSpannable(
                                "+" + root.context.getString(
                                    R.string.amountThanks,
                                    data.amount
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
                            if (data.sender.sender_tg_name == "anonymous") {
                                createClickableSpannable(
                                    data.sender.sender_tg_name + " ",
                                    R.color.general_brand
                                )
                                {
                                    data.sender_id?.let { onSomeonesClicked?.invoke(it) }
                                }
                            } else {
                                createClickableSpannable(
                                    data.sender.sender_surname + " " +
                                            data.sender.sender_first_name,
                                    R.color.general_brand
                                )
                                {
                                    data.sender_id?.let { onSomeonesClicked(it) }
                                }
                            }
                        )
                        message.text = spannable
                    } else {
                        // Я получатель
                        if (!data.sender?.sender_photo.isNullOrEmpty()) {
                            Glide.with(binding.root.context)
                                .load("${Consts.BASE_URL}${data.sender?.sender_photo}".toUri())
                                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(transferIconIv)
                            avatar =
                                "${Consts.BASE_URL}${data.sender?.sender_photo}"
                        }
                        descr_transaction_1 = binding.root.context.getString(R.string.youGot)

                        if (data.transactionClass.id == "W") {
                            // В случае победы в челлендже
                            // Я получатель
                            val spannable = SpannableStringBuilder(
                            ).append(
                                createClickableSpannable(
                                    "+" + root.context.getString(
                                        R.string.amountThanks,
                                        data.amount
                                    ),
                                    R.color.minor_success,
                                    null
                                )
                            ).append(
                                createClickableSpannable(
                                    " " + root.context.getString(R.string.forWinningInChallenge) + " ",
                                    R.color.black,
                                    null
                                )
                            ).append(
                                createClickableSpannable(
                                    // Занести сюда название челленджа
                                    data.sender?.challenge_name?.doubleQuoted() ?: "",
                                    R.color.general_brand
                                ) {
                                    data.sender?.challenge_id?.let { onChallengeClicked(it) }
                                }
                            )
                            message.text = spannable
                        } else {
                            val spannable = SpannableStringBuilder(
                            ).append(
                                createClickableSpannable(
                                    "+" + root.context.getString(
                                        R.string.amountThanks,
                                        data.amount
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
                                    data.sender?.sender_surname + " " +
                                            data.sender?.sender_first_name,
                                    R.color.general_brand
                                )
                                {
                                    data.sender_id?.let { onSomeonesClicked(it) }
                                }

                            )
                            message.text = spannable
                        }
                        labelStatusTransaction =
                            binding.root.context.getString(R.string.typeTransfer)
                        comingStatusTransaction =
                            binding.root.context.getString(R.string.comingTransfer)
                    }

                   setStatusTransaction(data)
                }

                chipGroup.removeAllViews()

                if (!data.tags.isNullOrEmpty()) {
                    binding.scrollForChipGroup.visibility = View.VISIBLE
                    setTags(chipGroup, data.tags)
                } else {
                    binding.scrollForChipGroup.visibility = View.GONE
                }

                convertDataToNecessaryFormat(data)
                transactionToAnotherProfile(username, data)

                photoFromSender = data.photo
                if (data.transactionClass.id != "W" && data.transactionClass.id != "H") {
                    mainCard.setOnClickListener { v ->
                        val bundle = Bundle()
                        bundle.apply {
                            // аву пока не передаю
                            putString("photo_from_sender", photoFromSender)
                            putString(Consts.AVATAR_USER, avatar)
                            putString(Consts.DATE_TRANSACTION, dateGetInfo)
                            putString(Consts.DESCRIPTION_TRANSACTION_1, descr_transaction_1)
                            putString(
                                Consts.DESCRIPTION_TRANSACTION_2_WHO,
                                if (data.sender?.sender_tg_name == username) data.recipient?.recipient_tg_name
                                else data.sender?.sender_tg_name
                            )
                            putString(
                                Consts.DESCRIPTION_TRANSACTION_3_AMOUNT,
                                binding.root.context.getString(R.string.amountThanks, data.amount)
                            )
                            putString(Consts.REASON_TRANSACTION, data.reason)
                            putString(Consts.STATUS_TRANSACTION, comingStatusTransaction)
                            putString(Consts.LABEL_STATUS_TRANSACTION, labelStatusTransaction)
                            putString(Consts.AMOUNT_THANKS, data.amount)
                            putString(Consts.WE_REFUSED_YOUR_OPERATION, weRefusedYourOperation)
                            data.recipient_id?.let {
                                putInt("userIdReceiver", it)
                            }
                            data.sender_id?.let {
                                putInt("userIdSender", it)
                            }

                        }
                        Log.d("History", "${data}")
                        v.findNavController().navigate(
                            R.id.action_historyFragment_to_additionalInfoTransactionBottomSheetFragment2,
                            bundle
                        )
                    }
                } else {
                    mainCard.setOnClickListener { view ->
                        data.sender?.challenge_id?.let { it -> onChallengeClicked(it) }
                    }
                }

            }
        }

        private fun setStatusTransaction(data: HistoryItem.UserTransactionsResponse){
            binding.apply {
                if (data.transaction_status.id.equals("A")) {
                    statusTransferTextView.text =
                        binding.root.context.getString(R.string.occured)
                    statusTransferTextView.setBackgroundColor(binding.root.context.getColor(R.color.minor_success))
                    statusCard.setCardBackgroundColor(binding.root.context.getColor(R.color.minor_success))
                } else if (data.transaction_status.id.equals("D")) {
                    // Отклонен контролером
                    comingStatusTransaction =
                        binding.root.context.getString(R.string.operationWasRefused)
                    statusTransferTextView.text =
                        binding.root.context.getString(R.string.refused)
                    weRefusedYourOperation =
                        binding.root.context.getString(R.string.weRefusedYourOperation)
                    statusTransferTextView.setBackgroundColor(binding.root.context.getColor(R.color.minor_error))
                    statusCard.setCardBackgroundColor(binding.root.context.getColor(R.color.minor_error))

                } else if (data.transaction_status.id.equals("G")) {
                    statusTransferTextView.text = binding.root.context.getString(R.string.G)
                    statusTransferTextView.setBackgroundColor(binding.root.context.getColor(R.color.minor_warning))
                    statusCard.setCardBackgroundColor(binding.root.context.getColor(R.color.minor_warning))
                    comingStatusTransaction = binding.root.context.getString(R.string.G)
                } else if (data.transaction_status.id.equals("R")) {
                    statusTransferTextView.text =
                        binding.root.context.getString(R.string.occured)
                    statusTransferTextView.setBackgroundColor(binding.root.context.getColor(R.color.minor_success))
                    statusCard.setCardBackgroundColor(binding.root.context.getColor(R.color.minor_success))
                    comingStatusTransaction = binding.root.context.getString(R.string.occured)
                } else if (data.transaction_status.id.equals("C")) {
                    statusTransferTextView.text =
                        binding.root.context.getString(R.string.refused)
                    statusTransferTextView.setBackgroundColor(binding.root.context.getColor(R.color.minor_error))
                    statusCard.setCardBackgroundColor(binding.root.context.getColor(R.color.minor_error))
                    descr_transaction_1 =
                        binding.root.context.getString(R.string.youWantedToSend)
                    weRefusedYourOperation =
                        binding.root.context.getString(R.string.iRefusedMyOperation)
                    comingStatusTransaction =
                        binding.root.context.getString(R.string.operationWasRefused)

                    // holder.labelStatusTransaction = context.getString(R.string.reasonOfRefusing)
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

        private fun setTags(tagsChipGroup: ChipGroup, tagList: List<TagModel>) {
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

        private fun convertDataToNecessaryFormat(data: HistoryItem.UserTransactionsResponse) {
            try {
                val dateTime: String =
                    LocalDateTime.parse(data.updatedAt.replace("+03:00", ""))
                        .format(DateTimeFormatter.ofPattern("dd.MM.y HH:mm"))
                var date = dateTime.subSequence(0, 10)
                val time = dateTime.subSequence(11, 16)
                val today: LocalDate = LocalDate.now()
                val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
                val todayString =
                    LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.y"))
                val yesterdayString =
                    LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .format(DateTimeFormatter.ofPattern("dd.MM.y"))

                if (date == todayString) {
                    date = "Сегодня"
                } else if (date == yesterdayString) {
                    date = "Вчера"
                }
                dateGetInfo =
                    String.format(binding.root.context.getString(R.string.dateTime), date, time)

            } catch (e: Exception) {
                Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
            }
        }

        private fun transactionToAnotherProfile(
            username: String,
            data: HistoryItem.UserTransactionsResponse
        ) {
            if (data.sender?.sender_tg_name == username) {
                userId = data.recipient_id
            } else if ((data.sender?.sender_tg_name != "anonymous" && data.recipient?.recipient_tg_name == username)) {
                userId = data.sender_id
            }

            binding.tgNameUser.setOnClickListener { view ->
                val bundle = Bundle()
                if (userId != 0) {
                    userId?.let {
                        bundle.putInt("userId", it)
                        view.findNavController().navigate(
                            R.id.action_historyFragment_to_someonesProfileFragment,
                            bundle, OptionsTransaction().optionForProfileFragment
                        )
                    }
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryItem.UserTransactionsResponse -> R.layout.item_transfer
            is HistoryItem.DateTimeSeparator -> R.layout.separator_date_time
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    companion object {
        const val TAG = "HistoryPageAdapter"

        private val DiffCallback = object : DiffUtil.ItemCallback<HistoryItem>() {
            override fun areItemsTheSame(
                oldItem: HistoryItem,
                newItem: HistoryItem
            ): Boolean {
                return (oldItem is HistoryItem.UserTransactionsResponse &&
                        newItem is HistoryItem.UserTransactionsResponse &&
                        oldItem.id == newItem.id) ||
                        (oldItem is HistoryItem.DateTimeSeparator &&
                                newItem is HistoryItem.DateTimeSeparator &&
                                oldItem.uuid == newItem.uuid)
            }

            override fun areContentsTheSame(
                oldItem: HistoryItem,
                newItem: HistoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

//data class TransactionModel(
//    val id: Int,
//    val photo: String?,
//    val isCancellable: Boolean,
//    val transactionStatus: TransactionStatus,
//    val tags: List<TagModel>,
//    val dateTime: Long,
//    val recipientId: Int,
//    val recipientPhoto: String?,
//    val senderId: Int,
//    val senderPhoto: String?,
//    val amount: String,
//    val senderTgName: String,
//    val isMine: Boolean
//    )
//
//sealed class TransactionStatus {
//    object A: TransactionStatus()
//    object D: TransactionStatus()
//    object G: TransactionStatus()
//    object R: TransactionStatus()
//    object C: TransactionStatus()
//}