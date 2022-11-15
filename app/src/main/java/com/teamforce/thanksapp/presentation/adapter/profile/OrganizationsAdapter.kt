package com.teamforce.thanksapp.presentation.adapter.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.databinding.ItemOrganizationBinding
class OrganizationsAdapter(
private val listOfOrganization: List<OrganizationModel>
): ListAdapter<OrganizationModel, OrganizationsAdapter.OrganizationViewHolder>(DiffCallback)
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizationViewHolder {
        val binding = ItemOrganizationBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return OrganizationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    companion object{
        const val TAG = "ContendersAdapter"

        object DiffCallback : DiffUtil.ItemCallback<OrganizationModel>() {
            override fun areItemsTheSame(oldItem: OrganizationModel, newItem: OrganizationModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: OrganizationModel, newItem: OrganizationModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class OrganizationViewHolder(val binding: ItemOrganizationBinding)
        : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: OrganizationViewHolder, position: Int) {
        with(holder){
            binding.nameOrganization.text = currentList[position].name
        }


    }
}