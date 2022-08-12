package com.teamforce.thanksapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean

class UsersAdapter(
    private val dataSet: List<UserBean>,
    private val listener: View.OnClickListener
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.tgName.text = dataSet[position].tgName
        holder.name.text = dataSet[position].firstname
        holder.surname.text = dataSet[position].surname
        holder.view.tag = dataSet[position]
        holder.view.setOnClickListener { v -> listener.onClick(v) }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tgName: TextView
        val surname: TextView
        val name: TextView
        val view: View

        init {
            view = v
            tgName = v.findViewById(R.id.user_tg_name)
            surname = v.findViewById(R.id.user_surname_label_tv)
            name = v.findViewById(R.id.user_name_label_tv)
        }
    }
}
