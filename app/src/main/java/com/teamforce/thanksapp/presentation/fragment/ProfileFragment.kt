package com.teamforce.thanksapp.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.activityNavController
import com.teamforce.thanksapp.utils.navigateSafely
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel = ProfileViewModel()


    private lateinit var userTgName: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var userFio: TextView

    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView

    private lateinit var companyUser: TextView
    private lateinit var departmentUser: TextView
    private lateinit var hiredAt: TextView
    private var loadImage: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
        it?.let {
            binding.userAvatar.setImageURI(it)
            funURIToMultipart(it)
            //imageConversionToBytes(it.toString())
        }

//        ActivityResultCallback<Uri> {
//            Log.d("Я в колбеке", "${it}")
//            imageFilePart = imageConversionToBytes(it.toString())
//        }
    })
    private lateinit  var imageFilePart: MultipartBody.Part

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        requestData()
        setData()
        binding.exitBtn.setOnClickListener {
            showAlertDialogForExit()
        }

        binding.editBtn.setOnClickListener{
            showAlertDialogForEditProfile()
        }

    }

    private fun  funURIToMultipart(imageURI: Uri){
        val file: File = File(imageURI.path!!)
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        UserDataRepository.getInstance()?.token.let { token ->
            UserDataRepository.getInstance()?.profileId.let { id ->
                viewModel.loadUpdateAvatarUserProfile(token!!, id!!, body)
            }
        }
    }


//    fun imageConversionToBytes(imageUri: String) {
//
//        val imageFile = Uri.parse(imageUri).path?.let { File(it) }
//        val iStream = context?.contentResolver?.openInputStream(Uri.parse(imageUri))
//
//        val bytes = iStream?.let { getBytes(it) }
//        Log.d("Token", "Bytes ${bytes}")
//        // bytes не защищен save call
//        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData("file", imageFile?.path, RequestBody.create(
//            MediaType.parse("image/png"), bytes))
//        Log.d("Token", "Filepart.body ${filePart.body()}")
//        UserDataRepository.getInstance()?.token.let { token ->
//            UserDataRepository.getInstance()?.profileId.let { id ->
//                viewModel.loadUpdateAvatarUserProfile(token!!, id!!, filePart)
//            }
//        }
//
//    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 20971520
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }



     private fun requestData(){
         val token = UserDataRepository.getInstance()?.token
         if (token != null) {
             viewModel.loadUserProfile(token)
         }
     }

    private fun setData(){
        viewModel.profile.observe(viewLifecycleOwner){
            userFio.text = String.format(requireContext().getString(R.string.userFio),
                it.profile.surname ,it.profile.firstname, it.profile.middlename)

            userTgName.text = "@" + it.profile.tgName

            if(it.profile.contacts.size == 1){
                userEmail.text = it.profile.contacts[0].contact_id
            }else if(it.profile.contacts.size == 2){
                userEmail.text = it.profile.contacts[0].contact_id
                userPhone.text = it.profile.contacts[1].contact_id
            }


            companyUser.text = it.profile.organization
            departmentUser.text = it.profile.department
            hiredAt.text = it.profile.hiredAt
            Glide.with(this)
                .load("https://images.unsplash.com/photo-1566275529824-cca6d008f3da?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8cGhvdG98ZW58MHx8MHx8&w=1000&q=80".toUri())
                .centerCrop()
                .into(userAvatar)

            UserDataRepository.getInstance()?.profileId = it.profile.id
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

            .setNegativeButton(resources.getString(R.string.photo)) { dialog, which ->
                dialog.cancel()
                loadImage.launch("image/*")
            }
            .setPositiveButton(resources.getString(R.string.stringData)) { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun changePhotoUser(){

    }


    private fun initViews() {
        userTgName = binding.userTelegramId
        userAvatar = binding.userAvatar
        userFio = binding.userFio

        userEmail = binding.emailValueTv
        userPhone = binding.mobileValueTv

        companyUser = binding.companyValueTv
        departmentUser = binding.departmentValueTv
        hiredAt = binding.dateStartValueTv

        viewModel.initViewModel()
    }

    companion object {

        const val TAG = "ProfileFragment"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
