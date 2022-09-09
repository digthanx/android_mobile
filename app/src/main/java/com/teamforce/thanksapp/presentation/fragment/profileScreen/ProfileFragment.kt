package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel : ProfileViewModel by viewModels()

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

    private lateinit var userTgName: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var userFio: TextView

    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView
    private var contactId_1: Int? = null
    private var contactId_2: Int? = null
    private var contactValue_1: String? = null
    private var contactValue_2: String? = null

    private lateinit var companyUser: TextView
    private lateinit var departmentUser: TextView
    private lateinit var hiredAt: TextView
    private lateinit var header: MaterialCardView
    private lateinit var information: MaterialCardView
    private lateinit var placeJob: MaterialCardView
    private lateinit var swipeToRefresh: SwipeRefreshLayout

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
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.balanceFragment,
                R.id.feedFragment,
                R.id.transactionFragment,
                R.id.historyFragment
            )
        )

        binding.toolbarProfile.setupWithNavController(navController, appBarConfiguration)
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
        viewModel.userDataRepository.token.let { token ->
            viewModel.userDataRepository.profileId.let { id ->
                viewModel.loadUpdateAvatarUserProfile(token!!, id!!, body)
            }
        }
    }

     private fun requestData(){
         val token = viewModel.userDataRepository.token
         if (token != null) {
             viewModel.loadUserProfile(token)
         }
     }

    private fun setData(){
        viewModel.profile.observe(viewLifecycleOwner){
            if(it.profile.middlename == "null"){
                userFio.text = String.format(requireContext().getString(R.string.userFio),
                    it.profile.surname ,it.profile.firstname, "")
            }else{
                userFio.text = String.format(requireContext().getString(R.string.userFio),
                    it.profile.surname ,it.profile.firstname, it.profile.middlename)
            }

            userTgName.text = String.format(
                requireContext().getString(R.string.tgName), it.profile.tgName)

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


            companyUser.text = it.profile.organization
            departmentUser.text = it.profile.department
            hiredAt.text = it.profile.hiredAt
            if(!it.profile.photo.isNullOrEmpty()){
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(userAvatar)
            }else{
                binding.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }
            viewModel.userDataRepository.profileId = it.profile.id
        }
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
                viewModel.userDataRepository.logout()
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
                bundle.putString("company", companyUser.text.toString())
                bundle.putString("department", departmentUser.text.toString())
                findNavController().navigate(R.id.action_profileFragment_to_editProfileBottomSheetFragment, bundle)
            }
            .show()
    }



    private fun initViews() {
        swipeToRefresh = binding.swipeRefreshLayout
        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        userTgName = binding.userTelegramId
        userAvatar = binding.userAvatar
        userFio = binding.userFio

        userEmail = binding.emailValueTv
        userPhone = binding.mobileValueTv

        companyUser = binding.companyValueTv
        departmentUser = binding.departmentValueTv
        hiredAt = binding.dateStartValueTv

        header = binding.header
        information = binding.information
        placeJob = binding.placeJob


        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    header.visibility = View.GONE
                    information.visibility = View.GONE
                    placeJob.visibility = View.GONE
                    swipeToRefresh.isRefreshing = true
                } else {
                    header.visibility = View.VISIBLE
                    information.visibility = View.VISIBLE
                    placeJob.visibility = View.VISIBLE
                    swipeToRefresh.isRefreshing = false

                }
            }
        )
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}
