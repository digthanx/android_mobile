package com.teamforce.thanksapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.databinding.ItemChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel

class ChallengeAdapter(
): ListAdapter<ChallengeModel,
        ChallengeAdapter.ChallengeViewHolder>(ChallengeViewHolder.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemChallengeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ChallengeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }


    class ChallengeViewHolder(binding: ItemChallengeBinding) : RecyclerView.ViewHolder(binding.root) {

        val name = binding.challengeTitle
        val participants = binding.participantsValue
        val winners = binding.winnersValue
        val prizeFund = binding.prizeFundValue
        val prizePool = binding.prizePoolValue
        val personImage = binding.successfulPersonImage
        val participantsText = binding.participantsText
        val winnersText = binding.winnersText
        val prizeFundText = binding.prizeFundText
        val prizePoolText = binding.prizePoolText
        val lastUpdateChallengeValue = binding.lastUpdateChallengeValue
        val lastUpdateChallengeCard = binding.lastUpdateChallengeCard
        val mainCard = binding.mainCard
        val root = binding.root


        object DiffCallback : DiffUtil.ItemCallback<ChallengeModel>() {
            override fun areItemsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChallengeModel, newItem: ChallengeModel): Boolean {
                return oldItem == newItem
            }

        }

    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
//        if (!currentList[position].photo.isNullOrEmpty()){
//
//        }
        // insert data
        holder.apply {
            name.setText(currentList[position].name)
            currentList[position].approved_reports_amount?.let { participants.setText(it.toString()) }
            currentList[position].awardees?.let { winners.text = it.toString() }
            currentList[position].start_balance?.let { prizeFund.text = it.toString() }
            //currentList[position].parameters?.get(0)?.let { prizePool.setText(it.value) }
        }
    }
}