package com.teamforce.thanksapp.presentation.adapter.notifications

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.PushNotificationService
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemNotificationBinding
import com.teamforce.thanksapp.databinding.SeparatorDateTimeBinding
import com.teamforce.thanksapp.domain.models.notifications.NotificationAdditionalData
import com.teamforce.thanksapp.domain.models.notifications.NotificationItem
import java.lang.UnsupportedOperationException

class NotificationPageAdapter :
    PagingDataAdapter<NotificationItem, RecyclerView.ViewHolder>(DIffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is NotificationItem.NotificationModel -> {
                return when (item.data) {
                    is NotificationAdditionalData.NotificationChallengeDataModel -> {
                        CHALLENGE_VIEW_TYPE
                    }
                    is NotificationAdditionalData.NotificationTransactionDataModel -> {
                        TRANSACTION_VIEW_TYPE
                    }
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
            TRANSACTION_VIEW_TYPE, CHALLENGE_VIEW_TYPE -> {
                val binding = ItemNotificationBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                NotificationViewHolder(binding)
            }
            UNKNOWN_VIEW_TYPE -> {
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

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NotificationItem.NotificationModel) {
            binding.apply {
                dateTime.text = data.createdAt
                description.text = data.theme
            }
        }
    }

    companion object {
        const val TRANSACTION_VIEW_TYPE = 1
        const val CHALLENGE_VIEW_TYPE = 2
        const val UNKNOWN_VIEW_TYPE = 3
        const val DATE_TIME_SEPARATOR_VIEW_TYPE = 4


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
                                oldItem.uuid == newItem.uuid)
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