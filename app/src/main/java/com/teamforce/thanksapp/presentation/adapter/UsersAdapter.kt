package com.teamforce.thanksapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.utils.Consts

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
        holder.cardView.tag = dataSet[position]
        if(!dataSet[position].photo.isNullOrEmpty()){
            Glide.with(holder.view)
                .load("${Consts.BASE_URL}/media/${dataSet[position].photo}".toUri())
                .centerCrop()
                .into(holder.userPhoto)
        }
        holder.view.setOnClickListener { v -> listener.onClick(v) }
        holder.cardView.setOnClickListener { cardView -> listener.onClick(cardView) }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class UserViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tgName: TextView
        val surname: TextView
        val name: TextView
        val view: View
        val cardView: CardView
        val userPhoto: ShapeableImageView

        init {
            view = v
            tgName = v.findViewById(R.id.user_tg_name)
            surname = v.findViewById(R.id.user_surname_label_tv)
            name = v.findViewById(R.id.user_name_label_tv)
            cardView = v.findViewById(R.id.user_item)
            userPhoto = v.findViewById(R.id.user_avatar)
        }
    }
}
