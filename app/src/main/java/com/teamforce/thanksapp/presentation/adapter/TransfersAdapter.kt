package com.teamforce.thanksapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserTransactionsResponse

class TransfersAdapter(
    private val username: String,
    private val dataSet: List<UserTransactionsResponse>,
    private val listener: View.OnClickListener
) : RecyclerView.Adapter<TransfersAdapter.TransfersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transfer, parent, false)

        return TransfersViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransfersViewHolder, position: Int) {
        val status = dataSet[position].status
        if (status.equals("Одобрено")) {
            holder.status.setImageResource(R.drawable.ic_checkbox_circle_line)
        } else if (status.equals("Отклонено")) {
            holder.status.setImageResource(R.drawable.ic_close_circle_line)
        } else {
            holder.status.setImageResource(R.drawable.ic_time_line)
        }

        if (dataSet[position].sender.equals(username)) {
            holder.transferIcon.setImageResource(R.drawable.ic_arrow_right_circle_line)
            holder.label.text = String.format(holder.view.context.getString(R.string.sended_to), dataSet[position].recipient)
            holder.value.text = dataSet[position].amount
        } else {
            holder.transferIcon.setImageResource(R.drawable.ic_arrow_left_circle_fill)
            holder.label.text = String.format(holder.view.context.getString(R.string.received_from), dataSet[position].sender)
            holder.value.text = "+ " + dataSet[position].amount
            holder.value.setTextColor(R.color.color6)
        }

        holder.view.tag = dataSet[position]
        holder.view.setOnClickListener { v -> listener.onClick(v) }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class TransfersViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val status: ImageView
        val transferIcon: ImageView
        val label: TextView
        val value: TextView
        val view: View

        init {
            view = v
            status = v.findViewById(R.id.status_iv)
            transferIcon = v.findViewById(R.id.transfer_icon_iv)
            label = v.findViewById(R.id.label_tv)
            value = v.findViewById(R.id.value_tv)
        }
    }
}
