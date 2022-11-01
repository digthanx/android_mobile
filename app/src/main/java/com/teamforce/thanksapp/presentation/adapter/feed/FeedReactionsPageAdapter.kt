package com.teamforce.thanksapp.presentation.adapter.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.teamforce.thanksapp.data.response.GetReactionsForTransactionsResponse
import com.teamforce.thanksapp.databinding.ItemReactionBinding
import com.teamforce.thanksapp.utils.Consts

class FeedReactionsPageAdapter:
    PagingDataAdapter<GetReactionsForTransactionsResponse.InnerInfoLike, FeedReactionsPageAdapter.ReactionViewHolder>(DiffCallback) {
    companion object {

        object DiffCallback : DiffUtil.ItemCallback<GetReactionsForTransactionsResponse.InnerInfoLike>() {
            override fun areItemsTheSame(
                oldItem: GetReactionsForTransactionsResponse.InnerInfoLike,
                newItem: GetReactionsForTransactionsResponse.InnerInfoLike
            ): Boolean {
                return oldItem.user.id == newItem.user.id
            }

            override fun areContentsTheSame(
                oldItem: GetReactionsForTransactionsResponse.InnerInfoLike,
                newItem: GetReactionsForTransactionsResponse.InnerInfoLike
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        val binding = ItemReactionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ReactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null){
            holder.bind(item)
        }
    }


    class ReactionViewHolder(val binding: ItemReactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GetReactionsForTransactionsResponse.InnerInfoLike) {
            with(binding){
                if(!data.user.avatar.isNullOrEmpty()){
                    Glide.with(root.context)
                        .load("${Consts.BASE_URL}${data.user.avatar}".toUri())
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(userAvatar)
                }
                userFi.text = data.user.name
            }
        }
    }
}