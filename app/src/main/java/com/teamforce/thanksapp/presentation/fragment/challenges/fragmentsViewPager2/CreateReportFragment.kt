package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCreateReportBinding
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.getPath
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private val binding: FragmentCreateReportBinding by viewBinding()

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

    private var imageFilePart: MultipartBody.Part? = null
    private var filePath: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreSavedDateFromSP()
        attachDetachImageInPreview()
        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.saveWithoutSendingBtn.setOnClickListener {
            if (binding.commentValueEt.text.trim().isNotEmpty() ||
                !filePath.isNullOrEmpty()
            ) {
                saveReportState()
            }
            activity?.onBackPressed()
        }
        deleteReportState()
    }

    private fun restoreSavedDateFromSP() {
        val sharedPref = requireContext().getSharedPreferences("report", 0)
        if (!sharedPref.getString("commentReport", "").isNullOrEmpty()){
            binding.commentValueEt.setText(
                sharedPref.getString("commentReport", ""))
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
        if(!binding.commentValueEt.text.trim().isEmpty() || !filePath.isNullOrEmpty()){
            val sharedPref =
                requireContext().getSharedPreferences("report", 0)
            val editor = sharedPref.edit()
            editor.putString("commentReport", binding.commentValueEt.text.toString())
            editor.putString("imageReport", filePath)
            editor.apply()
        }else{
            deleteReportState()
        }
        // При восстановление нужно прогнать filePath через uriToMultipart
    }

    private fun deleteReportState(){
        requireContext().getSharedPreferences("report", 0).edit().clear().apply()
    }


    private fun attachDetachImageInPreview() {
        binding.attachImageBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            addPhotoFromIntent()
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
                Log.d(ProfileFragment.TAG, "${result.data?.data}:")
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


    private fun uriToMultipart(filePathInner: String) {
        // Хардовая вставка картинки с самого начала
        // Убрать как только сделаю обновление по свайпам
        Glide.with(this)
            .load(filePathInner)
            .centerCrop()
            .into(binding.image)
        filePath = filePathInner
        val file = File(filePathInner)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        imageFilePart = body
    }
}