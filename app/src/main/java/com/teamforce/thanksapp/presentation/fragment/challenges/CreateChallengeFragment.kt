package com.teamforce.thanksapp.presentation.fragment.challenges

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.BuildConfig
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.DialogDatePickerBinding
import com.teamforce.thanksapp.databinding.FragmentCreateChallengeBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.presentation.viewmodel.challenge.CreateChallengeViewModel
import com.teamforce.thanksapp.utils.*
import com.teamforce.thanksapp.utils.getFilePathFromUri
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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
            if (isGranted) {
                showDialogCameraOrGallery()
            }else{
                showDialogAboutPermissions()
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
            showPhoneStatePermission()
        }
        binding.detachImgBtn.setOnClickListener {
            binding.showAttachedImgCard.visibility = View.GONE
            imageFilePart = null
        }
    }


    private fun uriToMultipart(filePath: String) {
        val file = File(filePath)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val path = result.data?.data?.let { getPath(requireContext(), it) }
                if (path != null) {
                    binding.showAttachedImgCard.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(path)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.image)
                    uriToMultipart(path)
                }
            }
        }

    private var latestTmpUri: Uri? = null
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if(isSuccess){
                latestTmpUri?.let {
                    binding.showAttachedImgCard.visible()
                    Glide.with(requireContext())
                        .load(it)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.image)
                    val path = getFilePathFromUri(requireContext(), it, true)
                    uriToMultipart(path)
                }
            }
        }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }
    private fun getTmpFileUri(): Uri? {
        val cacheDir: File? = activity?.cacheDir
        val tmpFile = File.createTempFile("tmp_image_file", ".png", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return activity?.applicationContext?.let {
            FileProvider.getUriForFile(
                it,
                "${BuildConfig.APPLICATION_ID}.provider", tmpFile
            )
        }
    }



    private fun showPhoneStatePermission() {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                showDialogCameraOrGallery()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showRequestPermissionRational()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showDialogCameraOrGallery(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.whatApproachToGetImage))
            .setNegativeButton(resources.getString(R.string.gallery)) { dialog, _ ->
                getImageFromGallery()
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.camera)) { dialog, _ ->
                takeImage()
                dialog.cancel()
            }
            .show()
    }

    private fun showRequestPermissionRational() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.explainingAboutPermissionsRational))

            .setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Хорошо") { dialog, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                dialog.cancel()
            }
            .show()
    }


    private fun getImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun showDialogAboutPermissions() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.explainingAboutPermissions))

            .setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.settings)) { dialog, which ->
                dialog.cancel()
                val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .apply {
                        val uri = Uri.fromParts("package", "com.teamforce.thanksapp", null)
                        data = uri
                    }
                startActivity(reqIntent)
                // Почему то повторно не запрашивается разрешение
                // requestPermissions()
            }
            .show()
    }


}