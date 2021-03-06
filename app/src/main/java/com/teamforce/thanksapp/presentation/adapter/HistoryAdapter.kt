package com.teamforce.thanksapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.model.domain.HistoryModel
import java.time.LocalDateTime
import java.util.*

class HistoryAdapter(
    private val username: String,
    private val listener: View.OnClickListener
) : ListAdapter<HistoryModel, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<HistoryModel>(){
        override fun areItemsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)

        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        try {
            val dateTime: LocalDateTime =
                LocalDateTime.parse(getItem(position).data.get(0).updatedAt.replace("+03:00", ""))
            val title = dateTime.dayOfMonth.toString() + " " + getMonth(dateTime)

            holder.date.text = title
        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }

        holder.transfers.adapter = TransfersAdapter(username, getItem(position).data, listener)
        holder.view.tag = getItem(position)
        holder.view.setOnClickListener { v -> listener.onClick(v) }
    }

    private fun getMonth(dateTime: LocalDateTime): String {
        when (dateTime.month.value) {
            1 -> {
                return "????????????"
            }
            2 -> {
                return "??????????????"
            }
            3 -> {
                return "??????????"
            }
            4 -> {
                return "????????????"
            }
            5 -> {
                return "??????"
            }
            6 -> {
                return "????????"
            }
            7 -> {
                return "????????"
            }
            8 -> {
                return "??????????????"
            }
            9 -> {
                return "????????????????"
            }
            10 -> {
                return "??????????????"
            }
            11 -> {
                return "????????????"
            }
            12 -> {
                return "??????????????"
            }
            else -> return "13?? ??????????"
        }
    }


    class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView
        val transfers: RecyclerView
        val view: View

        init {
            view = v
            date = v.findViewById(R.id.date_tv)
            transfers = v.findViewById(R.id.transfers_rv)
        }
    }
}
