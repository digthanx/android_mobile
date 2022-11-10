package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.BuildConfig
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCreateReportBinding
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.presentation.viewmodel.challenge.CreateReportViewModel
import com.teamforce.thanksapp.utils.getPath
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.utils.getFilePathFromUri
import com.teamforce.thanksapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private val binding: FragmentCreateReportBinding by viewBinding()

    private val viewModel: CreateReportViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showDialogCameraOrGallery()
            } else {
                showDialogAboutPermissions()
            }
        }

    private var imageFilePart: MultipartBody.Part? = null
    private var filePath: String? = null
    private var idChallenge: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            idChallenge = getInt(CHALLENGER_ID)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreSavedDateFromSP()
        attachDetachImageInPreview()
        saveWithoutSending()
        deleteReportStateWhenFieldsAreNull()
        listenersSuccessResponseOfCreateReport()
        listenersErrorCreateChallenge()
        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.sendReport.setOnClickListener {
            idChallenge?.let { it1 -> sendReport(it1) }
        }


    }

    private fun deleteReportStateWhenFieldsAreNull() {
        if (binding.commentValueEt.text.trim().isEmpty() ||
            filePath.isNullOrEmpty()
        ) {
            deleteReportState()
        }
    }

    private fun saveWithoutSending() {
        binding.saveWithoutSendingBtn.setOnClickListener {
            if (binding.commentValueEt.text.trim().isNotEmpty() ||
                !filePath.isNullOrEmpty()
            ) {
                saveReportState()
            }
            activity?.onBackPressed()
        }
    }

    private fun sendReport(challengeId: Int) {
        val comment = binding.commentValueEt.text.toString()
        val image = imageFilePart
        val challengeID = challengeId
        viewModel.createReport(challengeID, comment, image)
    }

    private fun listenersSuccessResponseOfCreateReport() {
        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.successCreateReport),
                    Toast.LENGTH_LONG
                ).show()
                clearFields()
                deleteReportState()
                activity?.onBackPressed()
            }
        }
    }

    private fun clearFields() {
        binding.commentValueEt.setText("")
        binding.showAttachedImgCard.visibility = View.GONE
        imageFilePart = null
        filePath = null
    }

    private fun listenersErrorCreateChallenge() {
        viewModel.createReportError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun restoreSavedDateFromSP() {
        val sharedPref = requireContext().getSharedPreferences("report", 0)
        if (!sharedPref.getString("commentReport", "").isNullOrEmpty()) {
            binding.commentValueEt.setText(
                sharedPref.getString("commentReport", "")
            )
        }
        if (!sharedPref.getString("imageReport", "").isNullOrEmpty()) {
            filePath = sharedPref.getString("imageReport", "")
            binding.showAttachedImgCard.visibility = View.VISIBLE
            Glide.with(this)
                .load(filePath)
                .centerCrop()
                .into(binding.image)
            uriToMultipart(filePath!!)
        }
    }

    private fun saveReportState() {
        if (!binding.commentValueEt.text.trim().isEmpty() || !filePath.isNullOrEmpty()) {
            val sharedPref =
                requireContext().getSharedPreferences("report", 0)
            val editor = sharedPref.edit()
            editor.putString("commentReport", binding.commentValueEt.text.toString())
            editor.putString("imageReport", filePath)
            editor.apply()
        } else {
            deleteReportState()
        }

        // При восстановление нужно прогнать filePath через uriToMultipart
    }

    private fun deleteReportState() {
        requireContext().getSharedPreferences("report", 0).edit().clear().apply()
    }


    private fun attachDetachImageInPreview() {
        binding.attachImageBtn.setOnClickListener {
            showPhoneStatePermission()
        }

        binding.detachImgBtn.setOnClickListener {
            binding.showAttachedImgCard.visibility = View.GONE
            imageFilePart = null
            filePath = null
        }
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
                        .into(binding.image)
                    uriToMultipart(path)
                }
            }
        }

    private var latestTmpUri: Uri? = null
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
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

    private fun showDialogCameraOrGallery() {
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


    private fun uriToMultipart(filePathInner: String) {
        filePath = filePathInner
        val file = File(filePathInner)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }

    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            CreateReportFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }
}