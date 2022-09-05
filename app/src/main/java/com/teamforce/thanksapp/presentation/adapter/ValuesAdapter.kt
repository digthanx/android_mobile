package com.teamforce.thanksapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.teamforce.thanksapp.databinding.ItemValueBinding
import com.teamforce.thanksapp.databinding.ItemValueEnabledBinding
import com.teamforce.thanksapp.model.domain.ValueModel

class ValuesAdapter(): ListAdapter<ValueModel, ValuesAdapter.ValueViewHolder>(ValuesAdapter) {

    var onValueItemClickListener: ((ValueModel) -> Unit)? = null

    companion object DiffCallback : DiffUtil.ItemCallback<ValueModel>(){
        override fun areItemsTheSame(oldItem: ValueModel, newItem: ValueModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ValueModel, newItem: ValueModel): Boolean {
            return oldItem == newItem
        }

        const val VIEW_TYPE_ENABLED = 100
        const val VIEW_TYPE_DISABLED = 101

    }

    class ValueViewHolder(val binding: ViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValuesAdapter.ValueViewHolder {
         when (viewType) {
            VIEW_TYPE_DISABLED -> {
                  return ValuesAdapter.ValueViewHolder(ItemValueBinding
                      .inflate(LayoutInflater.from(parent.context), parent, false))
            }
            VIEW_TYPE_ENABLED -> {
                return ValuesAdapter.ValueViewHolder(ItemValueEnabledBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> return ValuesAdapter.ValueViewHolder(ItemValueBinding
                .inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }


    override fun onBindViewHolder(holder: ValueViewHolder, position: Int) {
        val valueItem = currentList[position]
        val binding = holder.binding
        binding.root.setOnClickListener {
            onValueItemClickListener?.invoke(valueItem)
        }
    }

    override fun getItemCount(): Int {
        //Log.d("Token", "DataSet size - ${currentList.size}")
        return currentList.size
    }



    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.enabled) {
            VIEW_TYPE_ENABLED
        } else {
            VIEW_TYPE_DISABLED
        }

    }

}