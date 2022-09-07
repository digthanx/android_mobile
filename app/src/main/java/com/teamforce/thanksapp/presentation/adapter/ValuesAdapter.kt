package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.ItemValueEnabledBinding
import com.teamforce.thanksapp.model.domain.TagModel

class ValuesAdapter(
    private val dataSet: List<TagModel>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onValueItemClickListener: ((TagModel) -> Unit)? = null

    class ValueEnabledViewHolder(val binding: ItemValueEnabledBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var name = binding.name
        val mainBody = binding.mainContent
        val checkIcon = binding.checkIcon
        val cardImage = binding.cardImageValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValueEnabledViewHolder {
        val binding = ItemValueEnabledBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return ValuesAdapter.ValueEnabledViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }


    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder: ValueEnabledViewHolder = (holder as ValueEnabledViewHolder)
        myHolder.name.text = dataSet[position].name
        myHolder.mainBody.setOnClickListener {
            Log.d("Token", "Клик сработал")
            dataSet[position].enabled = !dataSet[position].enabled
            holder.cardImage.imageTintList = context.getColorStateList(R.color.color_feed_item_orange)
            if(dataSet[position].enabled){
                Log.d("Token", "Клик сработал")
                holder.checkIcon.visibility = View.VISIBLE
                holder.cardImage.strokeWidth = 8f
                holder.cardImage.imageTintList = context.getColorStateList(R.color.color_feed_item_orange)
            }else{
                holder.checkIcon.visibility = View.GONE
                holder.cardImage.strokeWidth = 0f
                holder.cardImage.imageTintList = context.getColorStateList(R.color.color_feed_item_black)

                // holder.cardImage.setStrokeColorResource(context.getColor(R.color.minor_info_secondary))
            }

        }

    }

}