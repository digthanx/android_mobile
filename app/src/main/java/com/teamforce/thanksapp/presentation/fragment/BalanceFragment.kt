package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.viewmodel.BalanceViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.activityNavController
import com.teamforce.thanksapp.utils.navigateSafely
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BalanceFragment : Fragment() {

    private lateinit var count: TextView
    private lateinit var distributed: TextView
    private lateinit var leastCount: TextView
    private lateinit var leastDistribute: TextView
    private lateinit var cancelled: TextView
    private lateinit var frozen: TextView
    private lateinit var willBurn: TextView
    private lateinit var outBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        val viewModel = BalanceViewModel()
        viewModel.initViewModel()
        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            viewModel.loadUserBalance(token)
        }
        viewModel.balance.observe(viewLifecycleOwner) {
            UserDataRepository.getInstance()?.leastCoins = it.distribute.amount
            count.text = it.income.amount.toString()
            distributed.text = " " + it.income.sended.toString()
            leastCount.text = it.distribute.amount.toString()
            leastDistribute.text = " " + it.distribute.sended.toString()
            cancelled.text = " " + it.distribute.cancelled.toString()
            frozen.text = " " + it.income.frozen.toString()
            try {
                val dateTime: LocalDate =
                    LocalDate.parse(it.distribute.expireDate, DateTimeFormatter.ISO_DATE)
                val from = LocalDate.now()
                val result: Long = ChronoUnit.DAYS.between(from, dateTime)
                val text = result.toString()
                if (isOne(text) && text.length == 1) {
                    willBurn.text = String.format(getString(R.string.will_burn_after), text)
                } else if (isOne(text) && isNotTen(text)) {
                    willBurn.text = String.format(getString(R.string.will_burn_after), text)
                } else if (text.length == 1 && (isTwo(text) || isThree(text) || isFour(text))) {
                    willBurn.text = String.format(getString(R.string.will_burn_after_2), text)
                } else if ((isTwo(text) || isThree(text) || isFour(text)) && isNotTen(text)) {
                    willBurn.text = String.format(getString(R.string.will_burn_after_2), text)
                } else {
                    willBurn.text = String.format(getString(R.string.will_burn_after_3), text)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e.fillInStackTrace())
            }
        }

        outBtn.setOnClickListener {
            UserDataRepository.getInstance()?.logout(requireActivity())
            activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
        }
    }

    private fun isNotTen(text: String): Boolean {
        return text.length > 1 && text[text.length - 2] != "1".first()
    }

    private fun isOne(text: String): Boolean {
        return text[text.length - 1] == "1".first()
    }

    private fun isTwo(text: String): Boolean {
        return text[text.length - 1] == "2".first()
    }

    private fun isThree(text: String): Boolean {
        return text[text.length - 1] == "3".first()
    }

    private fun isFour(text: String): Boolean {
        return text[text.length - 1] == "4".first()
    }

    private fun initViews(view: View) {
        count = view.findViewById(R.id.count_value_tv)
        distributed = view.findViewById(R.id.distributed_value_tv)
        leastCount = view.findViewById(R.id.least_count)
        leastDistribute = view.findViewById(R.id.least_value_tv)
        cancelled = view.findViewById(R.id.cancelled_value_tv)
        frozen = view.findViewById(R.id.frozen_value_tv)
        willBurn = view.findViewById(R.id.will_burn_tv)
        outBtn = view.findViewById(R.id.btn_out)
    }

    companion object {

        const val TAG = "BalanceFragment"

        @JvmStatic
        fun newInstance() = BalanceFragment()
    }
}
