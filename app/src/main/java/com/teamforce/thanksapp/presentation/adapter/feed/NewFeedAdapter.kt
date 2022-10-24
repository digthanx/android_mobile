package com.teamforce.thanksapp.presentation.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.databinding.ItemFeedBinding
import com.teamforce.thanksapp.domain.models.feed.FeedModel
import com.teamforce.thanksapp.utils.invisible
import com.teamforce.thanksapp.utils.visible

class NewFeedAdapter() : PagingDataAdapter<FeedModel, NewFeedAdapter.ViewHolder>(DiffCallback) {

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
            when (item) {
                is FeedModel.TransactionFeedEvent -> bindTransaction(item)
                is FeedModel.ChallengeFeedEvent -> bindChallenge(item)
                is FeedModel.WinnerFeedEvent -> bindWinner(item)
            }
        }

        private fun bindWinner(item: FeedModel.WinnerFeedEvent) {
            binding.toggleButtonGroup.invisible()
            binding.senderAndReceiver.text = item.winnerFirstName
        }

        private fun bindChallenge(item: FeedModel.ChallengeFeedEvent) {
            binding.toggleButtonGroup.invisible()
            binding.senderAndReceiver.text = item.challengeName
        }

        private fun bindTransaction(item: FeedModel.TransactionFeedEvent) {
            binding.toggleButtonGroup.visible()
            binding.senderAndReceiver.text = item.transactionRecipientTgName
        }
    }

    companion object {
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