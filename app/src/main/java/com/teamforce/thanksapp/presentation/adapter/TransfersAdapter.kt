package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserTransactionsResponse
import com.teamforce.thanksapp.databinding.ItemTransferBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransfersAdapter(
    private val username: String,
    private val dataSet: List<UserTransactionsResponse>,
    private val listener: View.OnClickListener,
    private val context: Context
) : RecyclerView.Adapter<TransfersAdapter.TransfersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfersViewHolder {
        val binding = ItemTransferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return TransfersViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransfersViewHolder, position: Int) {
        val status = dataSet[position].transaction_status.transactionStatus
        if (status.equals("Одобрено")) {
            holder.status.text = context.getString(R.string.occured)
            holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
            // Expanded Version
//            holder.statusTextView.text = "Выполнен"
//            holder.statusTextView.setTextColor(R.color.minor_success)
//            holder.imageStatus.setImageResource(R.drawable.ic_applied_transaction)
        } else if (status.equals("Отклонено")) {
            holder.status.text = context.getString(R.string.refused)
            holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
            // Expanded Version
//            holder.statusTextView.text = "Аннулировано"
//            holder.statusTextView.setTextColor(R.color.minor_error)
//            holder.imageStatus.setImageResource(R.drawable.ic_declined_transaction)
        } else {
            holder.status.text = context.getString(R.string.on_approval)
            holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
//            holder.statusTextView.text = "На согласовании"
//            holder.statusTextView.setTextColor(R.color.color7)
//            holder.imageStatus.setImageResource(R.drawable.ic_wating_transaction)
        }

        if (dataSet[position].sender.sender_tg_name.equals(username)) {
            holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            holder.tgNameUser.text = "@" + dataSet[position].recipient.recipient_tg_name
            holder.valueTransfer.text = "- " +  dataSet[position].amount
//            // Expanded Version
//            holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
//            holder.transactionFrom.text = String.format(
//                holder.view.context.getString(R.string.sended_to),
//                dataSet[position].recipient.recipient_tg_name)
//            holder.newValueTransaction.text = dataSet[position].amount
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            holder.tgNameUser.text = "@" + dataSet[position].sender.sender_tg_name
            holder.valueTransfer.text = "+ " + dataSet[position].amount
            holder.valueTransfer.setTextColor(context.getColor(R.color.minor_success))
            // Expanded Version
            holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            holder.tgNameUser.text = "@" + dataSet[position].sender.sender_tg_name
//            holder.newValueTransaction.text = "+ " + dataSet[position].amount
        }

        try {
            val dateTime: String =
                LocalDateTime.
                parse(dataSet[position].updatedAt.
                replace("+03:00", "")).
                format(DateTimeFormatter.ofPattern( "dd.MM.y HH:mm"))
            val title = dateTime

//            holder.dateGetInfo.text = title.toString()
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
        holder.view.tag = dataSet[position]
        holder.view.setOnClickListener { v ->

        }
        // Description For Expanded Version
//        holder.newReasonTransaction.text = "\"" + dataSet[position].reason + "\""
    }


    override fun getItemCount(): Int {
        return dataSet.size
    }

    class TransfersViewHolder(binding: ItemTransferBinding) : RecyclerView.ViewHolder(binding.root) {
        val status: TextView = binding.statusTransferTextView
        val userAvatar: ImageView = binding.transferIconIv
        val tgNameUser: TextView = binding.tgNameUser
        val valueTransfer: TextView = binding.valueTransfer
        val view: View = binding.root

//        // Expanded version
//        // Данные дополнительные сразу будут добавлены, просто будут скрыты от пользователя
//        val dateGetInfo: TextView
//        val transactionFrom: TextView
//        val statusTextView: TextView
//        val imageStatus: ImageView
//        val status_expandedVersion: ImageView
//        val newValueTransaction: TextView
//        val newReasonTransaction: TextView




//        init {
//            dateGetInfo = v.findViewById(R.id.new_date_get_transaction)
//            transactionFrom = v.findViewById(R.id.new_transaction_from_tv)
//            statusTextView = v.findViewById(R.id.status_tv)
//            imageStatus = v.findViewById(R.id.status_image)
//            status_expandedVersion = v.findViewById(R.id.new_status_iv)
//            newValueTransaction = v.findViewById(R.id.new_value_transaction)
//            newReasonTransaction = v.findViewById(R.id.tv_description)
//        }
    }
}
