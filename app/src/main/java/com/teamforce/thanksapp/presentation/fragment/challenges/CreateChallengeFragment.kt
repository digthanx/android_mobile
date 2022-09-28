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
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.DialogDatePickerBinding
import com.teamforce.thanksapp.databinding.FragmentCreateChallengeBinding
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.utils.getPath
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*


class CreateChallengeFragment : Fragment(R.layout.fragment_create_challenge) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentCreateChallengeBinding by viewBinding()

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

    var date: String = ""
    private var imageFilePart: MultipartBody.Part? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callDatePickerListener(binding)
        uploadDataToDb()
        uploadImageFromGallery()
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
        val dateChallenge = date
        // Отправка данных о чалике
    }

    private fun uploadImageFromGallery(){
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
                    uriToMultipart(imageUri, path)
                }

            }
        }

    private fun addPhotoFromIntent() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun uriToMultipart(imageURI: Uri, filePath: String) {
//         Хардовая вставка картинки с самого начала
//         Убрать как только сделаю обновление по свайпам
//        Glide.with(this)
//            .load(imageURI)
//            .centerCrop()
//            .into(binding.image)
        val file = File(filePath)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }

}