package com.teamforce.thanksapp.presentation.fragment.challenges

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.DialogDatePickerBinding
import com.teamforce.thanksapp.databinding.FragmentCreateChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.presentation.viewmodel.challenge.CreateChallengeViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.getPath
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.time.*
import java.util.*

@AndroidEntryPoint
class CreateChallengeFragment : Fragment(R.layout.fragment_create_challenge) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentCreateChallengeBinding by viewBinding()

    private val viewModel: CreateChallengeViewModel by viewModels()

    private var createChallengeResult: ChallengeModel? = null


    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_permission),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    var dateForTextView: String = ""
    var dateForSendingFormatted: String = ""
    private var imageFilePart: MultipartBody.Part? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callDatePickerListener(binding)
        binding.continueBtn.setOnClickListener {
            if (!binding.titleEt.text.isNullOrEmpty() &&
                !binding.descriptionEt.text.isNullOrEmpty() &&
                !binding.prizeFundEt.text.isNullOrEmpty() &&
                !binding.prizePoolEt.text.isNullOrEmpty()
            ) {
                binding.continueBtn.isEnabled = false
                uploadDataToDb()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.allFieldsAreRequired),
                    Toast.LENGTH_SHORT
                ).show()
                // Set errors for empty fields
                binding.titleEt.error = requireContext().getString(R.string.requiredField)
                binding.descriptionEt.error = requireContext().getString(R.string.requiredField)
                binding.prizeFundEt.error = requireContext().getString(R.string.requiredField)
                binding.prizePoolEt.error = requireContext().getString(R.string.requiredField)
                binding.prizeFundTextField.endIconDrawable = null
            }
        }
        uploadImageFromGallery()

        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.continueBtn.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.challengeWasCreated),
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(
                    R.id.action_createChallengeFragment_to_challengesFragment, null,
                    OptionsTransaction().optionForEditProfile
                )
            }

        }


        binding.closeBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_createChallengeFragment_to_challengesFragment, null,
                OptionsTransaction().optionForEditProfile
            )
        }

    }

    private fun callDatePickerListener(myBinding: FragmentCreateChallengeBinding) {
        binding.dateEt.setOnClickListener {
            val builderDialog = AlertDialog.Builder(context)
            val inflater = requireActivity().layoutInflater
            val binding = DialogDatePickerBinding.inflate(inflater)
            val datePickerLayout = binding.root
            val datePicker = DatePicker(requireContext())
            binding.linearForDatePicker.addView(datePicker)
            val today = Calendar.getInstance()
            datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ) { view, year, month, day ->
            }
            builderDialog.setView(datePickerLayout)
                .setPositiveButton(getString(R.string.applyValues),
                    DialogInterface.OnClickListener { dialog, which ->
                        // Сохранения значения в переменную
                        dateForTextView = "${binding.datePicker.dayOfMonth}" +
                                ".${binding.datePicker.month + 1}" +
                                ".${binding.datePicker.year}"
                        dateForSendingFormatted = "${binding.datePicker.year}" +
                                "-${binding.datePicker.month + 1}" +
                                "-${binding.datePicker.dayOfMonth}"
                        dialog.cancel()
                        myBinding.dateEt.setText(dateForTextView)
                    })
                .setNegativeButton(
                    getString(R.string.refuse),
                    DialogInterface.OnClickListener { dialog, which ->
                        dateForTextView = ""
                        dateForSendingFormatted = ""
                        dialog.dismiss()
                        myBinding.dateEt.setText(dateForTextView)
                    }
                )
            val dialog = builderDialog.create()
            dialog.show()
        }
    }


    private fun uploadDataToDb() {
        val nameChallenge = binding.titleEt.text.toString()
        val description = binding.descriptionEt.text.toString()
        val prizeFund = binding.prizeFundEt.text?.trim().toString().toInt()
        val prizePool = binding.prizePoolEt.text?.trim().toString().toInt()
        val parameter_id = 2
        val parameter_value = prizePool
        // Отправка данных о чалике
        viewModel.createChallenge(
            name = nameChallenge,
            description = description,
            amountFund = prizeFund,
            endAt = dateForSendingFormatted,
            parameter_id = parameter_id,
            parameter_value = parameter_value,
            photo = imageFilePart
        )
        viewModel.createChallenge.observe(viewLifecycleOwner) {
            createChallengeResult = it
        }

    }

    private fun uploadImageFromGallery() {
        binding.attachImageBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            addPhotoFromIntent()
        }
        binding.detachImgBtn.setOnClickListener {
            binding.showAttachedImgCard.visibility = View.GONE
            imageFilePart = null
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val path = getPath(requireContext(), result.data?.data!!)
                val imageUri = result.data!!.data
                if (imageUri != null && path != null) {
                    binding.showAttachedImgCard.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(imageUri)
                        .centerCrop()
                        .into(binding.image)
                    uriToMultipart(path)
                }

            }
        }

    private fun addPhotoFromIntent() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun uriToMultipart(filePath: String) {
        val file = File(filePath)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }

}