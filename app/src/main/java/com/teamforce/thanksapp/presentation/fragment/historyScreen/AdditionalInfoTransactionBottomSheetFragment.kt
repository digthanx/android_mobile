package com.teamforce.thanksapp.presentation.fragment.historyScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoTransactionBottomSheetBinding
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


class AdditionalInfoTransactionBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAdditionalInfoTransactionBottomSheetBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }


    private var dateTransaction: String? = null
    private var avatar: String? = null
    private var descr_transaction_1: String? = null
    private var descr_transaction_2_who: String? = null
    private var descr_transaction_3_amount: String? = null
    private var reason_transaction: String? = null
    private var status_transaction: String? = null
    private var label_status_transaction: String? = null
    private var amount_thanks: String? = null
    private var we_refused_your: String? = null
    private var photo_from_sender: String? = null
    private var userId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            avatar = it.getString(AVATAR_USER)
            dateTransaction = it.getString(DATE_TRANSACTION)
            descr_transaction_1 = it.getString(DESCRIPTION_TRANSACTION_1)
            descr_transaction_2_who = it.getString(DESCRIPTION_TRANSACTION_2_WHO)
            descr_transaction_3_amount = it.getString(DESCRIPTION_TRANSACTION_3_AMOUNT)
            reason_transaction = it.getString(REASON_TRANSACTION)
            status_transaction = it.getString(STATUS_TRANSACTION)
            label_status_transaction = it.getString(LABEL_STATUS_TRANSACTION)
            amount_thanks = it.getString(AMOUNT_THANKS)
            we_refused_your = it.getString(WE_REFUSED_YOUR_OPERATION)
            photo_from_sender = it.getString("photo_from_sender")
            userId = it.getInt("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdditionalInfoTransactionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun getTheme(): Int  = R.style.AppBottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            dateTransactionTv.text = dateTransaction
            descriptionTransactionYouDo.text = descr_transaction_1
            descriptionTransactionWho.text = descr_transaction_2_who
            descriptionTransactionAmountText.text = descr_transaction_3_amount
            valueTransfer.text = amount_thanks
            reasonTransaction.text = reason_transaction
            labelStatusTransaction.text = label_status_transaction
            statusTransaction.text = status_transaction
        }
        if(we_refused_your != null){
            binding.currencyTransaction.visibility = View.GONE
            binding.valueTransfer.visibility = View.GONE
            // Пока что вставлю из стринги для обоих случаев Операция была отменена
           // binding.weOrYouRefused.setText(we_refused_your)
            binding.weOrYouRefused.setText(context?.getString(R.string.operationWasRefused))
            binding.weOrYouRefused.visibility = View.VISIBLE
        }
        if(!avatar.isNullOrEmpty()){
            Glide.with(this)
                .load(avatar?.toUri())
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(binding.userAvatar)
        }
        if (!photo_from_sender.isNullOrEmpty()){
            Log.d("Token", "${photo_from_sender}")
            binding.photoTv.visibility = View.VISIBLE
            binding.cardViewImg.visibility = View.VISIBLE
            Glide.with(this)
                .load(photo_from_sender)
                .centerCrop()
                .into(binding.senderImage)
        }else{
            binding.photoTv.visibility = View.GONE
            binding.cardViewImg.visibility = View.GONE
        }

        binding.descriptionTransactionWho.setOnClickListener {
            transactionToSomeonesProfile(userId)
        }

        binding.userAvatar.setOnClickListener {
            transactionToSomeonesProfile(userId)
        }

    }

    private fun transactionToSomeonesProfile(userId: Int?){
        val bundle = Bundle()
        val optionForProfileFragment = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
            .setPopExitAnim(R.anim.bottom_in)
            .build()
        if(userId != 0){
            userId?.let {
                bundle.putInt("userId", it)
                findNavController()
                    .navigate(R.id.action_additionalInfoTransactionBottomSheetFragment2_to_someonesProfileFragment, bundle, optionForProfileFragment)
            }
        }
    }
}