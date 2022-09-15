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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ProfileFragment : Fragment(R.layout.fragment_profile) {



    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentProfileBinding by viewBinding()

    private val viewModel = ProfileViewModel()

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

    private val userAvatar: ImageView by lazy { binding.userAvatar }
    private val userName: TextView by lazy { binding.firstNameValueTv }
    private val userSurname: TextView by lazy { binding.surnameValueTv }
    private val userMiddleName: TextView by lazy { binding.middleNameValueTv }
    private val userEmail: TextView by lazy { binding.emailValueTv }
    private val userPhone: TextView by lazy { binding.mobileValueTv }
    private val greetingUser: TextView by lazy { binding.greetingUserTv }


    private val companyUser: TextView by lazy { binding.companyValueTv }
    private val positionUser: TextView by lazy { binding.positionValueTv }
    private val roleUser: TextView by lazy { binding.roleValueTv }
    private val swipeToRefresh: SwipeRefreshLayout by lazy { binding.swipeRefreshLayout }
    private val allContent: LinearLayout by lazy { binding.allContent }


    private var contactId_1: Int? = null
    private var contactId_2: Int? = null
    private var contactValue_1: String? = null
    private var contactValue_2: String? = null



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

    private fun addPhotoFromIntent() {
        val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
        pickIntent.type = "image/*"
        resultLauncher.launch(pickIntent)
    }


    private fun uriToMultipart(imageURI: Uri, filePath: String) {
        // Хардовая вставка картинки с самого начала
        // Убрать как только сделаю обновление по свайпам
        Glide.with(this)
            .load(imageURI)
            .centerCrop()
            .into(userAvatar)
        val file = File(filePath)
        val requestFile: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        UserDataRepository.getInstance()?.token.let { token ->
            UserDataRepository.getInstance()?.profileId.let { id ->
                viewModel.loadUpdateAvatarUserProfile(token!!, id!!, body)
            }
        }
    }

     private fun requestData(){
         val token = UserDataRepository.getInstance()?.token
         if (token != null) {
             viewModel.loadUserProfile(token)
         }
     }

    private fun setData(){
        viewModel.profile.observe(viewLifecycleOwner){
            userName.text = it.profile.firstname
            userSurname.text = it.profile.surname
            userMiddleName.text = it.profile.middlename
            companyUser.text = it.profile.organization
            positionUser.text = it.profile.jobTitle
            if(it.profile.jobTitle.isNullOrEmpty()){
                binding.positionValueTv.visibility = View.GONE
                binding.positionLabelTv.visibility = View.GONE
            }else{
                binding.positionValueTv.visibility = View.VISIBLE
                binding.positionLabelTv.visibility = View.VISIBLE
            }
            greetingUser(it.profile.firstname.toString())
//            if(it.profile.middlename == "null"){
//                userFio.text = String.format(requireContext().getString(R.string.userFio),
//                    it.profile.surname ,it.profile.firstname, "")
//            }else{
//                userFio.text = String.format(requireContext().getString(R.string.userFio),
//                    it.profile.surname ,it.profile.firstname, it.profile.middlename)
//            }


            if(it.profile.contacts.size == 1){
                if(it.profile.contacts[0].contact_type == "@"){
                    userEmail.text = it.profile.contacts[0].contact_id
                    contactId_1 = it.profile.contacts[0].id
                    contactValue_1 = it.profile.contacts[0].contact_id
                }else{
                    userPhone.text = it.profile.contacts[0].contact_id
                    contactId_2 = it.profile.contacts[0].id
                    contactValue_2 = it.profile.contacts[0].contact_id
                }
            }else if(it.profile.contacts.size == 2){
                if(it.profile.contacts[0].contact_type == "@"){
                    userEmail.text = it.profile.contacts[0].contact_id
                    contactId_1 = it.profile.contacts[0].id
                    userPhone.text = it.profile.contacts[1].contact_id
                    contactId_2 = it.profile.contacts[1].id
                    contactValue_1 = it.profile.contacts[0].contact_id
                    contactValue_2 = it.profile.contacts[1].contact_id

                }else{
                    userEmail.text = it.profile.contacts[1].contact_id
                    contactId_1 = it.profile.contacts[1].id
                    userPhone.text = it.profile.contacts[0].contact_id
                    contactId_2 = it.profile.contacts[0].id
                    contactValue_1 = it.profile.contacts[1].contact_id
                    contactValue_2 = it.profile.contacts[0].contact_id
                }
            }

            if(!it.profile.photo.isNullOrEmpty()){
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(userAvatar)
            }else {
                userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }

            UserDataRepository.getInstance()?.profileId = it.profile.id
        }
    }

    private fun greetingUser(username: String){
        val spannable = SpannableStringBuilder(
            String.format(requireContext().getString(R.string.greeting_label), username)
        )
        spannable.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.general_brand)),
            7, spannable.length - 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        greetingUser.text = spannable
    }

    private fun swipeToRefresh(){
        swipeToRefresh.setOnRefreshListener {
            requestData()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun showAlertDialogForExit(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.wouldYouLikeToExit))

            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                dialog.cancel()
                UserDataRepository.getInstance()?.logout(requireContext())
                activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
            }
            .show()
    }

    private fun showAlertDialogForEditProfile(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.whatEditInProfile))

            .setNegativeButton(resources.getString(R.string.avatar)) { dialog, which ->
                dialog.cancel()
                //loadImage.launch("image/*")
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                addPhotoFromIntent()
            }
            .setPositiveButton(resources.getString(R.string.stringData)) { dialog, which ->
                dialog.cancel()
                val bundle = Bundle()
                bundle.putString("contact_value_1", contactValue_1)
                bundle.putString("contact_value_2", contactValue_2)
                bundle.putString("greeting", greetingUser.text.toString())
                findNavController().navigate(R.id.action_profileFragment_to_editProfileBottomSheetFragment, bundle)
            }
            .show()
    }



    private fun initViews() {
        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        viewModel.initViewModel()

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    allContent.visibility = View.GONE
                    swipeToRefresh.isRefreshing = true
                } else {
                    allContent.visibility = View.VISIBLE
                    swipeToRefresh.isRefreshing = false

                }
            }
        )
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}
