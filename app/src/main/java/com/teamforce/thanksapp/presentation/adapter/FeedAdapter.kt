package com.teamforce.thanksapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.ItemFeedBinding

class FeedAdapter (
    private val username: String,
    private val dataSet: List<FeedResponse>,
): ListAdapter<FeedResponse, FeedAdapter.FeedViewHolder>(DiffCallback){

    companion object DiffCallback : DiffUtil.ItemCallback<FeedResponse>(){
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

    override fun getItemCount(): Int = dataSet.size

    class FeedViewHolder(binding: ItemFeedBinding): RecyclerView.ViewHolder(binding.root){
        val avatarUser: ImageView = binding.userAvatar
        val dateTime: TextView = binding.dateTime
        val senderAndReceiver: TextView = binding.senderAndReceiver
        val likesBtn: Button = binding.likeBtn
        val dislikesBtn: Button = binding.dislikeBtn
        val commentBtn: Button = binding.commentBtn
        val view: View = binding.root
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        if(!dataSet[position].transaction.sender.equals(username) &&
            !dataSet[position].transaction.recipient.equals(username)){
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.someoneToSomeone),
                    dataSet[position].transaction.recipient,
                    "150",
                    dataSet[position].transaction.sender)
        }else if(!dataSet[position].transaction.sender.equals(username)){
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.youFromSomeone),
                    "150",
                    dataSet[position].transaction.sender)

        }else{
            holder.senderAndReceiver.text =
                String.format(holder.view.context.getString(
                    R.string.youToSomeone),
                    dataSet[position].transaction.recipient,
                    "150")
        }
    }

}