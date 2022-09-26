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
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.UserTransactionsResponse
import com.teamforce.thanksapp.databinding.ItemTransferBinding
import com.teamforce.thanksapp.model.domain.TagModel
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
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.UserDataRepository
import java.time.LocalDate
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
        val status = dataSet[position].transaction_status.id
        holder.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
        if(dataSet[position].sender.sender_tg_name != "anonymous" && dataSet[position].sender.sender_tg_name.equals(username)){
            // Ты отправитель
            if(dataSet[position].canUserCancel){
                holder.btnRefusedTransaction.visibility = View.VISIBLE
                holder.btnRefusedTransaction.setOnClickListener {
                    showAlertDialogForCancelTransaction(dataSet[position].id)
                }
            }
            holder.valueTransfer.text = "- " + dataSet[position].amount
            holder.tgNameUser.text = String.format(
                context.getString(R.string.tgName), dataSet[position].recipient.recipient_tg_name)
            holder.descr_transaction_1 = context.getString(R.string.youSended)
            holder.labelStatusTransaction = context.getString(R.string.statusTransfer)
            if(!dataSet[position].recipient.recipient_photo.isNullOrEmpty()){
                Glide.with(context)
                    .load("${Consts.BASE_URL}${dataSet[position].recipient.recipient_photo}".toUri())
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(holder.userAvatar)
                holder.avatar = "${Consts.BASE_URL}${dataSet[position].recipient.recipient_photo}"
            }

            if (status.equals("A")) {
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))

                holder.comingStatusTransaction = context.getString(R.string.occured)

            } else if (status.equals("D")) {
                // отклонен контролером
                holder.status.text = context.getString(R.string.refused)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))
                holder.descr_transaction_1 = context.getString(R.string.youWantedToSend)
                holder.weRefusedYourOperation = context.getString(R.string.weRefusedYourOperation)
                holder.comingStatusTransaction = context.getString(R.string.operationWasRefused)
                // Убираю причину отмены
                // holder.labelStatusTransaction = context.getString(R.string.reasonOfRefusing)
                // Где мне брать причину отказа? Записывать в переменную ниже
                // holder.comingStatusTransaction = context.getString(R.string.on_approval)
            } else if(status.equals("W")) {
                holder.status.text = context.getString(R.string.on_approval)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_warning))

                holder.comingStatusTransaction = context.getString(R.string.on_approval)
            } else if(status.equals("G")){
                holder.status.text = context.getString(R.string.G)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_warning))
                holder.comingStatusTransaction = context.getString(R.string.G)
            } else if(status.equals("R")){
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))
                holder.comingStatusTransaction = context.getString(R.string.occured)
            } else if(status.equals("C")){
                holder.status.text = context.getString(R.string.refused)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))
                holder.descr_transaction_1 = context.getString(R.string.youWantedToSend)
                holder.weRefusedYourOperation = context.getString(R.string.iRefusedMyOperation)
                holder.comingStatusTransaction = context.getString(R.string.operationWasRefused)
                //holder.labelStatusTransaction = context.getString(R.string.reasonOfRefusing)
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

                holder.tgNameUser.text = String.format(
                    context.getString(R.string.tgName), dataSet[position].sender.sender_tg_name)
                holder.labelStatusTransaction = context.getString(R.string.typeTransfer)
                holder.valueTransfer.text = "+ " + dataSet[position].amount
                holder.comingStatusTransaction = context.getString(R.string.comingTransfer)
            }

            if (status.equals("A")) {
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))
            } else if (status.equals("D")) {
                // Отклонен контролером
                holder.comingStatusTransaction = context.getString(R.string.operationWasRefused)
                holder.status.text = context.getString(R.string.refused)
                holder.weRefusedYourOperation = context.getString(R.string.weRefusedYourOperation)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))

            } else if(status.equals("G")){
                holder.status.text = context.getString(R.string.G)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_warning))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_warning))
                holder.comingStatusTransaction = context.getString(R.string.G)
            } else if(status.equals("R")){
                holder.status.text = context.getString(R.string.occured)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_success))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_success))
                holder.comingStatusTransaction = context.getString(R.string.occured)
            } else if(status.equals("C")){
                holder.status.text = context.getString(R.string.refused)
                holder.status.setBackgroundColor(context.getColor(R.color.minor_error))
                holder.statusCard.setCardBackgroundColor(context.getColor(R.color.minor_error))
                holder.descr_transaction_1 = context.getString(R.string.youWantedToSend)
                holder.weRefusedYourOperation = context.getString(R.string.iRefusedMyOperation)
                holder.comingStatusTransaction = context.getString(R.string.operationWasRefused)

                // holder.labelStatusTransaction = context.getString(R.string.reasonOfRefusing)
            }
        }

        dataSet[position].tags?.let { setTags(holder.chipGroup, it) }

        convertDataToNecessaryFormat(holder, position)
        transactionToAnotherProfile(holder, position)

        holder.view.tag = dataSet[position]
        holder.photoFromSender = dataSet[position].photo
        holder.standardGroup.setOnClickListener { v ->
            val bundle = Bundle()
            bundle.apply {
                // аву пока не передаю
                putString("photo_from_sender", holder.photoFromSender)
                putString(AVATAR_USER, holder.avatar)
                putString(DATE_TRANSACTION, holder.dateGetInfo)
                putString(DESCRIPTION_TRANSACTION_1, holder.descr_transaction_1)
                putString(DESCRIPTION_TRANSACTION_2_WHO, holder.tgNameUser.text.toString())
                putString(DESCRIPTION_TRANSACTION_3_AMOUNT, context.getString(R.string.amountThanks, dataSet[position].amount))
                putString(REASON_TRANSACTION, dataSet[position].reason)
                putString(STATUS_TRANSACTION, holder.comingStatusTransaction)
                putString(LABEL_STATUS_TRANSACTION, holder.labelStatusTransaction)
                putString(AMOUNT_THANKS, holder.valueTransfer.text.toString())
                putString(WE_REFUSED_YOUR_OPERATION, holder.weRefusedYourOperation)
                dataSet[position].recipient_id?.let{
                    putInt("userIdReceiver", it)
                }
                dataSet[position].sender_id?.let{
                    putInt("userIdSender", it)
                }

            }
            v.findNavController().navigate(R.id.action_historyFragment_to_additionalInfoTransactionBottomSheetFragment2, bundle)
        }


    }

    private fun transactionToAnotherProfile(holder: TransfersViewHolder, position: Int){
        if(dataSet[position].sender.sender_tg_name.equals(username)){
            holder.userId = dataSet[position].recipient_id
        }else if((dataSet[position].sender.sender_tg_name != "anonymous" && dataSet[position].recipient.recipient_tg_name.equals(username))){
            holder.userId = dataSet[position].sender_id
        }

        holder.tgNameUser.setOnClickListener { view ->
            val bundle: Bundle = Bundle()
            if(holder.userId != 0){
                holder.userId?.let {
                    bundle.putInt("userId", it)
                    view.findNavController().navigate(
                        R.id.action_historyFragment_to_someonesProfileFragment,
                        bundle, OptionsTransaction().optionForProfileFragment)
                }
            }

        }
    }

    private fun setTags(tagsChipGroup: ChipGroup,tagList: List<TagModel>){
        for (i in tagList.indices) {
            val tagName = tagList[i].name
            val chip: Chip = LayoutInflater.from(tagsChipGroup.context)
                .inflate(R.layout.chip_tag_example_in_history_transaction, tagsChipGroup, false) as Chip
            with(chip) {
                setText(String.format(context.getString(R.string.setTag), tagName))
                setEnsureMinTouchTargetSize(true)
                minimumWidth = 0
            }

            tagsChipGroup.addView(chip)
        }
    }

    private fun convertDataToNecessaryFormat(holder: TransfersViewHolder, position: Int){
        try {
            val dateTime: String =
                LocalDateTime.
                parse(dataSet[position].updatedAt.
                replace("+03:00", "")).
                format(DateTimeFormatter.ofPattern( "dd.MM.y HH:mm"))
            var date = dateTime.subSequence(0, 10)
            val time = dateTime.subSequence(11, 16)
            val today: LocalDate = LocalDate.now()
            val yesterday: String = today.minusDays(1).format(DateTimeFormatter.ISO_DATE)
            val todayString = LocalDate.parse(today.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("dd.MM.y"))
            val yesterdayString = LocalDate.parse(yesterday, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .format(DateTimeFormatter.ofPattern("dd.MM.y"))

            if(date == todayString) {
                date = "Сегодня"
            } else if(date == yesterdayString){
                date = "Вчера"
            }
            holder.dateGetInfo = String.format(context.getString(R.string.dateTime), date, time)

        } catch (e: Exception) {
            Log.e("HistoryAdapter", e.message, e.fillInStackTrace())
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
                viewModel.userDataRepository.token?.let {
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
        val chipGroup: ChipGroup = binding.chipGroup
        var photoFromSender: String? = null
        var userId: Int? = null

        val view: View = binding.root
        var dateGetInfo: String = "null"
        var who: String = "null"
        var labelStatusTransaction: String = "null"
        var descr_transaction_1: String = "null"
        var comingStatusTransaction: String = "null"
        var weRefusedYourOperation: String? = null
        var avatar: String? = null
        var date: String = ""
    }
}
