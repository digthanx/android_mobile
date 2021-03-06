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
            // Expanded Version
            holder.statusTextView.text = "Согласовано"
            holder.imageStatus.setImageResource(R.drawable.ic_checkbox_circle_line)
        } else if (status.equals("Отклонено")) {
            holder.status.setImageResource(R.drawable.ic_close_circle_line)
            // Expanded Version
            holder.statusTextView.text = "Аннулировано"
            holder.imageStatus.setImageResource(R.drawable.ic_close_circle_line)
        } else {
            holder.status.setImageResource(R.drawable.ic_time_line)
            holder.statusTextView.text = "На согласовании"
            holder.imageStatus.setImageResource(R.drawable.ic_time_line)
        }

        if (dataSet[position].sender.equals(username)) {
            holder.transferIcon.setImageResource(R.drawable.ic_arrow_right_circle_line)
            holder.label.text = String.format(holder.view.context.getString(R.string.sended_to), dataSet[position].recipient)
            holder.value.text = dataSet[position].amount
            // Expanded Version
            holder.status_expandedVersion.setImageResource(R.drawable.ic_arrow_right_circle_line)
            holder.transactionFrom.text = String.format(holder.view.context.getString(R.string.sended_to), dataSet[position].recipient)
            holder.newValueTransaction.text = dataSet[position].amount
        } else {
            holder.transferIcon.setImageResource(R.drawable.ic_arrow_left_circle_fill)
            holder.label.text = String.format(holder.view.context.getString(R.string.received_from), dataSet[position].sender)
            holder.value.text = "+ " + dataSet[position].amount
            holder.value.setTextColor(R.color.color6)
            // Expanded Version
            holder.status_expandedVersion.setImageResource(R.drawable.ic_arrow_left_circle_fill)
            holder.label.text = String.format(holder.view.context.getString(R.string.received_from), dataSet[position].sender)
            holder.newValueTransaction.text = "+ " + dataSet[position].amount
        }

        holder.view.tag = dataSet[position]
        holder.view.setOnClickListener { v -> listener.onClick(v) }
        // Description For Expanded Version
        holder.newReasonTransaction.text = "\"" + dataSet[position].reason + "\""
    }


//    override fun onBindViewHolder(holder: TransfersViewHolder, position: Int) {
//        val status = dataSet[position].status
//        if (status.equals("Одобрено")) {
//            holder.status.setImageResource(R.drawable.ic_checkbox_circle_line)
//            // Expanded Version
//            holder.statusTextView.text = "Согласовано"
//            holder.imageStatus.setImageResource(R.drawable.ic_checkbox_circle_line)
//        } else if (status.equals("Отклонено")) {
//            holder.status.setImageResource(R.drawable.ic_close_circle_line)
//            // Expanded Version
//            holder.statusTextView.text = "Аннулировано"
//            holder.imageStatus.setImageResource(R.drawable.ic_close_circle_line)
//        } else {
//            holder.status.setImageResource(R.drawable.ic_time_line)
//            holder.statusTextView.text = "На согласовании"
//            holder.imageStatus.setImageResource(R.drawable.ic_time_line)
//        }
//
//        if (dataSet[position].sender.equals(username)) {
//            holder.transferIcon.setImageResource(R.drawable.ic_arrow_right_circle_line)
//            holder.label.text = String.format(holder.view.context.getString(R.string.sended_to), dataSet[position].recipient)
//            holder.value.text = dataSet[position].amount
//            // Expanded Version
//            holder.status_expandedVersion.setImageResource(R.drawable.ic_arrow_right_circle_line)
//            holder.transactionFrom.text = String.format(holder.view.context.getString(R.string.sended_to), dataSet[position].recipient)
//            holder.newValueTransaction.text = dataSet[position].amount
//        } else {
//            holder.transferIcon.setImageResource(R.drawable.ic_arrow_left_circle_fill)
//            holder.label.text = String.format(holder.view.context.getString(R.string.received_from), dataSet[position].sender)
//            holder.value.text = "+ " + dataSet[position].amount
//            holder.value.setTextColor(R.color.color6)
//            // Expanded Version
//            holder.status_expandedVersion.setImageResource(R.drawable.ic_arrow_left_circle_fill)
//            holder.label.text = String.format(holder.view.context.getString(R.string.received_from), dataSet[position].sender)
//            holder.newValueTransaction.text = "+ " + dataSet[position].amount
//        }
//
//        holder.view.tag = dataSet[position]
//        holder.view.setOnClickListener { v -> listener.onClick(v) }
//        // Description For Expanded Version
//        holder.newReasonTransaction.text = "\"" + dataSet[position].reason + "\""
//    }



    override fun getItemCount(): Int {
        return dataSet.size
    }

    class TransfersViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val status: ImageView
        val transferIcon: ImageView
        val label: TextView
        val value: TextView
        val view: View

        // Expanded version
        // Данные дополнительные сразу будут добавлены, просто будут скрыты от пользователя
        val dateGetInfo: TextView
        val transactionFrom: TextView
        val statusTextView: TextView
        val imageStatus: ImageView
        val status_expandedVersion: ImageView
        val newValueTransaction: TextView
        val newReasonTransaction: TextView




        init {
            view = v
            status = v.findViewById(R.id.status_iv)
            transferIcon = v.findViewById(R.id.transfer_icon_iv)
            label = v.findViewById(R.id.label_tv)
            value = v.findViewById(R.id.value_tv)

            dateGetInfo = v.findViewById(R.id.new_date_get_transaction)
            transactionFrom = v.findViewById(R.id.new_transaction_from_tv)
            statusTextView = v.findViewById(R.id.status_tv)
            imageStatus = v.findViewById(R.id.status_image)
            status_expandedVersion = v.findViewById(R.id.new_status_iv)
            newValueTransaction = v.findViewById(R.id.new_value_transaction)
            newReasonTransaction = v.findViewById(R.id.tv_description)
        }
    }
}
