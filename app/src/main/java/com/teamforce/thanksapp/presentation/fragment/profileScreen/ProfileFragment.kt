package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding: FragmentProfileBinding by viewBinding()

    private val viewModel: ProfileViewModel by viewModels()

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

    private var contactValue1: String? = null
    private var contactValue2: String? = null

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                Log.d(TAG, "${result.data?.data}:")
                val path = getPath(requireContext(), result.data?.data!!)
                val imageUri = result.data!!.data
                if (imageUri != null && path != null)
                    uriToMultipart(imageUri, path)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        requestData()
        setData()
        binding.exitBtn.setOnClickListener {
            showAlertDialogForExit()
        }

        binding.editBtn.setOnClickListener {
            showAlertDialogForEditProfile()
        }
        swipeToRefresh()
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    private fun addPhotoFromIntent() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun uriToMultipart(imageURI: Uri, filePath: String) {
        Glide.with(this)
            .load(imageURI)
            .centerCrop()
            .into(binding.userAvatar)
        viewModel.loadUpdateAvatarUserProfile(filePath)
    }

    private fun requestData() {
        viewModel.loadUserProfile()
    }

    private fun setData() {
        viewModel.profile.observe(viewLifecycleOwner) {
            //userName.text = it.profile.firstname
            binding.firstNameValueTv.text = it.profile.firstname
            binding.surnameValueTv.text = it.profile.surname
            binding.middleNameValueTv.text = it.profile.middleName
            binding.companyValueTv.text = it.profile.organization
            binding.positionValueTv.text = it.profile.jobTitle
            if (it.profile.jobTitle.isNullOrEmpty()) {
                binding.positionValueTv.visibility = View.GONE
                binding.positionLabelTv.visibility = View.GONE
            } else {
                binding.positionValueTv.visibility = View.VISIBLE
                binding.positionLabelTv.visibility = View.VISIBLE
            }
            greetingUser(it.profile.firstname.toString())

            if (it.profile.contacts.size == 1) {
                if (it.profile.contacts[0].contactType == "@") {
                    binding.emailValueTv.text = it.profile.contacts[0].contactId
                    contactValue1 = it.profile.contacts[0].contactId
                } else {
                    binding.mobileValueTv.text = it.profile.contacts[0].contactId
                    contactValue2 = it.profile.contacts[0].contactId
                }
            } else if (it.profile.contacts.size == 2) {
                if (it.profile.contacts[0].contactType == "@") {
                    binding.emailValueTv.text = it.profile.contacts[0].contactId
                    binding.mobileValueTv.text = it.profile.contacts[1].contactId
                    contactValue1 = it.profile.contacts[0].contactId
                    contactValue2 = it.profile.contacts[1].contactId

                } else {
                    binding.emailValueTv.text = it.profile.contacts[1].contactId
                    binding.mobileValueTv.text = it.profile.contacts[0].contactId
                    contactValue1 = it.profile.contacts[1].contactId
                    contactValue2 = it.profile.contacts[0].contactId
                }
            }

            if (!it.profile.photo.isNullOrEmpty()) {
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }

            binding.userAvatar.setOnClickListener { view ->
                it.profile.photo?.let { photo ->
                    (view as ShapeableImageView).viewSinglePhoto(photo, requireContext())
                }
            }
        }
    }

    private fun greetingUser(username: String) {
        val spannable = SpannableStringBuilder(
            String.format(requireContext().getString(R.string.greeting_label), username)
        )
        spannable.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.general_brand)),
            7, spannable.length - 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        binding.greetingUserTv.text = spannable
    }

    private fun swipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            requestData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showAlertDialogForExit() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.wouldYouLikeToExit))

            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                dialog.cancel()
                viewModel.logout()
                activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
            }
            .show()
    }

    private fun showAlertDialogForEditProfile() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.whatEditInProfile))

            .setNegativeButton(resources.getString(R.string.avatar)) { dialog, _ ->
                dialog.cancel()
                //loadImage.launch("image/*")
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                addPhotoFromIntent()
            }
            .setPositiveButton(resources.getString(R.string.stringData)) { dialog, which ->
                dialog.cancel()
                val bundle = Bundle()
                bundle.putString("contact_value_1", contactValue1)
                bundle.putString("contact_value_2", contactValue2)
                bundle.putString("greeting", binding.greetingUserTv.text.toString())
                findNavController().navigate(
                    R.id.action_profileFragment_to_editProfileBottomSheetFragment,
                    bundle,
                    OptionsTransaction().optionForEditProfile
                )
            }
            .show()
    }


    private fun initViews() {
        binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        viewModel.isLoading.observe(
            viewLifecycleOwner
        ) { isLoading ->
            if (isLoading) {
                binding.allContent.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = true
            } else {
                binding.allContent.visibility = View.VISIBLE
                binding.swipeRefreshLayout.isRefreshing = false

            }
        }
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}
