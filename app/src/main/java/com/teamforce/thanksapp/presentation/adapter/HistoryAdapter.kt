//package com.teamforce.thanksapp.presentation.adapter
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.teamforce.thanksapp.R
//import com.teamforce.thanksapp.model.domain.HistoryModel
//import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//class HistoryAdapter(
//    private val username: String,
//    private val context: Context,
//    private val viewModel: HistoryViewModel
//) : ListAdapter<HistoryModel, HistoryAdapter.HistoryViewHolder>(DiffCallback) {
//
//    var transAdapter: TransfersAdapter? = null
//
//    companion object DiffCallback : DiffUtil.ItemCallback<HistoryModel>(){
//        override fun areItemsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
//            return oldItem.date == newItem.date
//        }
//
//        override fun areContentsTheSame(oldItem: HistoryModel, newItem: HistoryModel): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_history, parent, false)
//
//        return HistoryViewHolder(view)
//    }
//
//
//
//    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
//        try {
//            val dateTime: LocalDateTime =
//                LocalDateTime.parse(getItem(position).data.get(0).updatedAt.replace("+03:00", ""))
//            val title = dateTime.dayOfMonth.toString() + " " + getMonth(dateTime)
//            val result = dateTime.toString().subSequence(0, 10)
//            val today: LocalDate = LocalDate.now()
//            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
//            if(result == today.toString()) {
//                holder.date.text = "Сегодня"
//            } else if(result == yesterday){
//                holder.date.text = "Вчера"
//            }else{
//                holder.date.text = title
//            }
//
//        } catch (e: Exception) {
//            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
//        }
//        transAdapter = TransfersAdapter(username, getItem(position).data, context, viewModel)
//        holder.transfers.adapter = transAdapter
//        holder.view.tag = getItem(position)
//
//    }
//
//    private fun getMonth(dateTime: LocalDateTime): String {
//        when (dateTime.month.value) {
//            1 -> {
//                return "Января"
//            }
//            2 -> {
//                return "Февраля"
//            }
//            3 -> {
//                return "Марта"
//            }
//            4 -> {
//                return "Апреля"
//            }
//            5 -> {
//                return "Мая"
//            }
//            6 -> {
//                return "Июня"
//            }
//            7 -> {
//                return "Июля"
//            }
//            8 -> {
//                return "Августа"
//            }
//            9 -> {
//                return "Сентября"
//            }
//            10 -> {
//                return "Октября"
//            }
//            11 -> {
//                return "Ноября"
//            }
//            12 -> {
//                return "Декабря"
//            }
//            else -> return "13й месяц"
//        }
//    }
//
//
//    class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        val date: TextView
//        val transfers: RecyclerView
//        val view: View
//
//        init {
//            view = v
//            date = v.findViewById(R.id.date_tv)
//            transfers = v.findViewById(R.id.transfers_rv)
//        }
//    }
//}
