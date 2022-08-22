package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoTransactionBottomSheetBinding
import com.teamforce.thanksapp.utils.Consts.AMOUNT_THANKS
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
    private var descr_transaction_1: String? = null
    private var descr_transaction_2_who: String? = null
    private var descr_transaction_3_amount: String? = null
    private var reason_transaction: String? = null
    private var status_transaction: String? = null
    private var label_status_transaction: String? = null
    private var amount_thanks: String? = null
    private var we_refused_your: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateTransaction = it.getString(DATE_TRANSACTION)
            descr_transaction_1 = it.getString(DESCRIPTION_TRANSACTION_1)
            descr_transaction_2_who = it.getString(DESCRIPTION_TRANSACTION_2_WHO)
            descr_transaction_3_amount = it.getString(DESCRIPTION_TRANSACTION_3_AMOUNT)
            reason_transaction = it.getString(REASON_TRANSACTION)
            status_transaction = it.getString(STATUS_TRANSACTION)
            label_status_transaction = it.getString(LABEL_STATUS_TRANSACTION)
            amount_thanks = it.getString(AMOUNT_THANKS)
            we_refused_your = it.getBoolean(WE_REFUSED_YOUR_OPERATION)
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
        if(we_refused_your == true){
            binding.currencyTransaction.visibility = View.GONE
            binding.valueTransfer.visibility = View.GONE
            binding.weRefused.visibility = View.VISIBLE
        }
    }
}