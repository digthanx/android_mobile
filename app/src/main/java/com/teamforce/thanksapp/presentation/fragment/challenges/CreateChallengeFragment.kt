package com.teamforce.thanksapp.presentation.fragment.challenges

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.DialogDatePickerBinding
import com.teamforce.thanksapp.databinding.FragmentCreateChallengeBinding
import java.util.*


class CreateChallengeFragment : Fragment(R.layout.fragment_create_challenge) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentCreateChallengeBinding by viewBinding()

    var date: String = "1"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callDatePickerListener(binding)
    }

    private fun callDatePickerListener(myBinding: FragmentCreateChallengeBinding){
        binding.dateEt.setOnClickListener {
            val builderDialog = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            val binding = DialogDatePickerBinding.inflate(inflater)
            val datePickerLayout = binding.root
            val datePicker = DatePicker(requireContext())
            binding.linearForDatePicker.addView(datePicker)
            val today = Calendar.getInstance()
            datePicker.init(today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ){ view, year, month, day ->
            }
            builderDialog.setView(datePickerLayout)
                .setPositiveButton(getString(R.string.applyValues),
                    DialogInterface.OnClickListener{dialog, which ->
                        // Сохранения значения в переменную
                        date = "${binding.datePicker.dayOfMonth}" +
                                ".${binding.datePicker.month}" +
                                ".${binding.datePicker.year}"
                        dialog.cancel()
                        myBinding.dateEt.setText(date)
                    })
                .setNegativeButton(
                    getString(R.string.refuse),
                    DialogInterface.OnClickListener { dialog, which ->
                        date = ""
                        dialog.dismiss()
                        myBinding.dateEt.setText(date)
                    }
                )
            val dialog = builderDialog.create()
            dialog.show()
        }
    }


    private fun uploadDataToDb(){
        val nameChallenge = binding.titleEt.text
        val description = binding.descriptionEt.text
        val prizeFund = binding.prizeFundEt.text?.trim()
        val prizePool = binding.prizePoolEt.text?.trim()
        // Отправка данных о чалике
    }

}