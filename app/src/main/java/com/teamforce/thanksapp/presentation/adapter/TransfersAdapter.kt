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
        var photoFromSender: String? = null

        val view: View = binding.root
        var dateGetInfo: String = "null"
        var who: String = "null"
        var labelStatusTransaction: String = "null"
        var descr_transaction_1: String = "null"
        var comingStatusTransaction: String = "null"
        var weRefusedYourOperation: String? = null
        var avatar: String? = null
    }
}
