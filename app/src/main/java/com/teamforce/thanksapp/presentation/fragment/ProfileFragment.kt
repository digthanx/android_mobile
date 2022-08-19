package com.teamforce.thanksapp.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.activityNavController
import com.teamforce.thanksapp.utils.navigateSafely
import java.io.IOException


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel = ProfileViewModel()

    private val imageChooser = {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT


        val launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode
                == Activity.RESULT_OK
            ) {
                val data = result.data
                // do your operation from here....
                if (data != null
                    && data.data != null
                ) {
                    val selectedImageUri: Uri? = data.data
                    val selectedImageBitmap: Bitmap
                    try {
                        selectedImageUri?.let {
                            if(Build.VERSION.SDK_INT < 28) {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().contentResolver,
                                    selectedImageUri
                                )
                                userAvatar.setImageBitmap(bitmap)
                            } else {
                                val source = ImageDecoder.createSource(requireContext().contentResolver, selectedImageUri)
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                userAvatar.setImageBitmap(bitmap)
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
//                    imageView.setImageBitmap(
//                        selectedImageBitmap
//                    )
                }
            }
        }

        launchSomeActivity.launch(i)
    }

    private lateinit var userTgName: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var userFio: TextView

    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView

    private lateinit var companyUser: TextView
    private lateinit var departmentUser: TextView
    private lateinit var hiredAt: TextView
    private lateinit var loadImage: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        requestData()
        setData()
        loadImage = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
            binding.userAvatar.setImageURI(it)
        })
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
            // Пока не знаю как изображение подгружать и как с ними работать
        }

        binding.exitBtn.setOnClickListener {
            showAlertDialogForExit()
        }

        binding.editBtn.setOnClickListener{
            showAlertDialogForEditProfile()
        }

    }

    private fun showAlertDialogForExit(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.whatEditInProfile))

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
            .setMessage(resources.getString(R.string.wouldYouLikeToExit))

            .setNegativeButton(resources.getString(R.string.photo)) { dialog, which ->
                dialog.cancel()
                //imageChooser()
//                val i = Intent()
//                i.type = "image/*"
//                i.action = Intent.ACTION_GET_CONTENT
//                startActivity(i)
                loadImage.launch("image/*")
            }
            .setPositiveButton(resources.getString(R.string.stringData)) { dialog, which ->
                dialog.cancel()
                UserDataRepository.getInstance()?.logout(requireContext())
                activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
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
