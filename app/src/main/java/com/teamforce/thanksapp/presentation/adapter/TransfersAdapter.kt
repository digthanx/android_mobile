package com.teamforce.thanksapp.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserTransactionsResponse
import com.teamforce.thanksapp.databinding.ItemTransferBinding
import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.AMOUNT_THANKS
import com.teamforce.thanksapp.utils.Consts.AVATAR_USER
import com.teamforce.thanksapp.utils.Consts.DATE_TRANSACTION
import com.teamforce.thanksapp.utils.Consts.DESCRIPTION_TRANSACTION_1
import com.teamforce.thanksapp.utils.Consts.DESCRIPTION_TRANSACTION_2_WHO
import com.teamforce.thanksapp.utils.Consts.DESCRIPTION_TRANSACTION_3_AMOUNT
import com.teamforce.thanksapp.utils.Consts.LABEL_STATUS_TRANSACTION
import com.teamforce.thanksapp.utils.Consts.REASON_TRANSACTION
import com.teamforce.thanksapp.utils.Consts.STATUS_TRANSACTION
import com.teamforce.thanksapp.utils.Consts.WE_REFUSED_YOUR_OPERATION
import com.teamforce.thanksapp.utils.UserDataRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransfersAdapter(
    private val username: String,
    private val dataSet: List<UserTransactionsResponse>,
    private val context: Context,
    private val viewModel: HistoryViewModel
) : RecyclerView.Adapter<TransfersAdapter.TransfersViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransfersViewHolder {
        val binding = ItemTransferBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)

        return TransfersViewHolder(binding)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransfersViewHolder, position: Int) {
        val status = dataSet[position].transaction_status.transactionStatus
        holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
        if(dataSet[position].sender.sender_tg_name != "anonymous" && dataSet[position].sender.sender_tg_name.equals(username)){
            // Ты отправитель
            holder.valueTransfer.text = "- " + dataSet[position].amount
            holder.tgNameUser.text = "@" + dataSet[position].recipient.recipient_tg_name
            holder.descr_transaction_1 = context.getString(R.string.youSended)
            holder.labelStatusTransaction = context.getString(R.string.statusTransfer)
            if(!dataSet[position].recipient.recipient_photo.isNullOrEmpty()){
                Glide.with(context)
                    .load("${Consts.BASE_URL}${dataSet[position].recipient.recipient_photo}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.userAvatar)
                holder.avatar = "${Consts.BASE_URL}${dataSet[position].recipient.recipient_photo}"
            }

            if (status.equals("Одобрено")) {
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))

                holder.comingStatusTransaction = context.getString(R.string.occured)

            } else if (status.equals("Отклонено")) {
                holder.status.text = context.getString(R.string.refused)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))


                holder.descr_transaction_1 = context.getString(R.string.youWantedToSend)
                holder.weRefusedYourOperation = true
                holder.labelStatusTransaction = context.getString(R.string.reasonOfRefusing)
                // Где мне брать причину отказа? Записывать в переменную ниже
               // holder.comingStatusTransaction = context.getString(R.string.on_approval)
            } else {
                holder.status.text = context.getString(R.string.on_approval)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_warning))

                holder.comingStatusTransaction = context.getString(R.string.on_approval)

            }

        }else{
            if(dataSet[position].sender.sender_tg_name == "anonymous") {
                holder.descr_transaction_1 = context.getString(R.string.youGot)
                holder.tgNameUser.text = " Аноним"
                holder.labelStatusTransaction = context.getString(R.string.typeTransfer)
                holder.valueTransfer.text = "+ " + dataSet[position].amount
                holder.comingStatusTransaction = context.getString(R.string.comingTransfer)
            }else{
                // Ты получатель
                if(!dataSet[position].sender.sender_photo.isNullOrEmpty()){
                    Glide.with(context)
                        .load("${Consts.BASE_URL}${dataSet[position].sender.sender_photo}".toUri())
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(holder.userAvatar)
                    holder.avatar = "${Consts.BASE_URL}${dataSet[position].sender.sender_photo}"
                }
                holder.descr_transaction_1 = context.getString(R.string.youGot)

                holder.tgNameUser.text = "@" + dataSet[position].sender.sender_tg_name
                holder.labelStatusTransaction = context.getString(R.string.typeTransfer)
                holder.valueTransfer.text = "+ " + dataSet[position].amount
                holder.comingStatusTransaction = context.getString(R.string.comingTransfer)
            }

            if (status.equals("Одобрено")) {
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))
            } else if (status.equals("Отклонено")) {
                holder.status.text = context.getString(R.string.refused)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))

            } else {
                holder.status.text = context.getString(R.string.on_approval)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_warning))


            }
        }

        try {
            val dateTime: String =
                LocalDateTime.
                parse(dataSet[position].updatedAt.
                replace("+03:00", "")).
                format(DateTimeFormatter.ofPattern( "dd.MM.y HH:mm"))
            holder.dateGetInfo = dateTime

        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
        }
        holder.view.tag = dataSet[position]
        holder.standardGroup.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.apply {
                // аву пока не передаю
                putString(AVATAR_USER, holder.avatar)
                putString(DATE_TRANSACTION, holder.dateGetInfo)
                putString(DESCRIPTION_TRANSACTION_1, holder.descr_transaction_1)
                putString(DESCRIPTION_TRANSACTION_2_WHO, holder.tgNameUser.text.toString())
                putString(DESCRIPTION_TRANSACTION_3_AMOUNT, context.getString(R.string.amountThanks, dataSet[position].amount))
                putString(REASON_TRANSACTION, dataSet[position].reason)
                putString(STATUS_TRANSACTION, holder.comingStatusTransaction)
                putString(LABEL_STATUS_TRANSACTION, holder.labelStatusTransaction)
                putString(AMOUNT_THANKS, holder.valueTransfer.text.toString())
                putBoolean(WE_REFUSED_YOUR_OPERATION, holder.weRefusedYourOperation)

            }
            v.findNavController().navigate(R.id.action_historyFragment_to_additionalInfoTransactionBottomSheetFragment2, bundle)
        }
        holder.btnRefusedTransaction.setOnClickListener {
            showAlertDialogForCancelTransaction(dataSet[position].id)
        }

    }

    private fun showAlertDialogForCancelTransaction(idTransaction: Int){
        MaterialAlertDialogBuilder(context)
            .setMessage(context.resources.getString(R.string.cancelTransaction))

            .setNegativeButton(context.resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(context.resources.getString(R.string.accept)) { dialog, which ->
                dialog.cancel()
                UserDataRepository.getInstance()?.token?.let {
                    viewModel.cancelUserTransaction(it, idTransaction.toString(), "D")
                }

            }
            .show()
    }



    override fun getItemCount(): Int {
        return dataSet.size
    }

    class TransfersViewHolder(binding: ItemTransferBinding) : RecyclerView.ViewHolder(binding.root) {
        val status: TextView = binding.statusTransferTextView
        val userAvatar: ImageView = binding.transferIconIv
        val tgNameUser: TextView = binding.tgNameUser
        val valueTransfer: TextView = binding.valueTransfer
        val statusCard: MaterialCardView = binding.statusCard
        val btnRefusedTransaction: ImageButton = binding.refuseTransactionBtn
        var standardGroup: ConstraintLayout = binding.standardGroup

        val view: View = binding.root
        var dateGetInfo: String = "null"
        var who: String = "null"
        var labelStatusTransaction: String = "null"
        var descr_transaction_1: String = "null"
        var comingStatusTransaction: String = "null"
        var weRefusedYourOperation: Boolean = false
        var avatar: String? = null
    }
}
