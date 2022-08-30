package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoTransactionBottomSheetBinding
import com.teamforce.thanksapp.databinding.FragmentEditProfileBottomSheetBinding
import com.teamforce.thanksapp.presentation.viewmodel.EditProfileViewModel
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository


class EditProfileBottomSheetFragment : Fragment() {

    private var _binding: FragmentEditProfileBottomSheetBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel = EditProfileViewModel()

    private val header by lazy { binding.header }

    private val avatarUser by lazy { binding.userAvatar }
    private val fioUser by lazy { binding.userFio }
    private val tgNameUser by lazy { binding.userTelegramName }
    private val surnameEt by lazy { binding.surnameEt }
    private val firstNameEt by lazy { binding.firstEt }
    private val middleEt by lazy { binding.middleEt }
    private val emailEt by lazy { binding.emailEt }
    private val phoneEt by lazy { binding.phoneEt }
    private val companyTv by lazy { binding.companyValueTv }
    private val departmentTv by lazy { binding.departmentValueTv }

    private var contactId_1: String? = null
    private var contactId_2: String? = null
    private var contactValue_1Email: String? = null
    private var contactValue_2Phone: String? = null
    private var company: String? = null
    private var department: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId_1 = it.getString("contact_id_1")
            contactId_2 = it.getString("contact_id_2")
            contactValue_1Email = it.getString("contact_value_1")
            contactValue_2Phone = it.getString("contact_value_2")
            company = it.getString("company")
            department = it.getString("department")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initViewModel()
        loadDataFromServer()
        writeData()
        logicalSaveData()
        header.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileBottomSheetFragment_to_profileFragment)
        }
    }

    private fun loadDataFromServer(){
        UserDataRepository.getInstance()?.token?.let {
            viewModel.loadUserProfile(it)
        }
    }

   private fun writeData() {
       viewModel.profile.observe(viewLifecycleOwner) {
           fioUser.text = String.format(
               requireContext().getString(R.string.userFio),
               it.profile.surname, it.profile.firstname, it.profile.middlename
           )
           tgNameUser.text = String.format(
               requireContext().getString(R.string.tgName), it.profile.tgName)
           surnameEt.setText(it.profile.surname)
           firstNameEt.setText(it.profile.firstname)
           middleEt.setText(it.profile.middlename)
           companyTv.setText(it.profile.organization)
           departmentTv.setText(it.profile.department)
           if (!it.profile.photo.isNullOrEmpty()) {
               Glide.with(this)
                   .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                   .centerCrop()
                   .into(avatarUser)
           }
           if (it.profile.contacts.size == 1) {
               if (it.profile.contacts[0].contact_type == "@") {
                   emailEt.setText(it.profile.contacts[0].contact_id)
               } else {
                   phoneEt.setText(it.profile.contacts[0].contact_id)
               }
           } else if (it.profile.contacts.size == 2) {
               if (it.profile.contacts[0].contact_type == "@") {
                   emailEt.setText(it.profile.contacts[0].contact_id)
                   phoneEt.setText(it.profile.contacts[1].contact_id)
               } else {
                   emailEt.setText(it.profile.contacts[1].contact_id)
                   phoneEt.setText(it.profile.contacts[0].contact_id)
               }
           }
       }
   }

    private fun logicalSaveData(){
        var surname: String? = ""
        var firstName: String? = ""
        var middleName: String? = ""
        var phone: String? = ""
        var email: String? = ""

        binding.btnSaveChanges.setOnClickListener {
            if (binding.firstEt.text?.trim().toString() == ""){
                firstName = null
            }else{
                firstName = binding.firstEt.text?.trim().toString()
            }
            if (binding.surnameEt.text?.trim().toString() == ""){
                surname = null
            }else{
                surname = binding.surnameEt.text?.trim().toString()
            }
            if (binding.middleEt.text?.trim().toString() == ""){
                middleName = null
            }else{
                middleName = binding.middleEt.text?.trim().toString()
            }
            if (binding.phoneEt.text?.trim().toString() == ""){
                phone = null
            }else{
                phone = binding.phoneEt.text?.trim().toString()
            }
            if (binding.emailEt.text?.trim().toString() == ""){
                email = null
            }else{
                email = binding.emailEt.text?.trim().toString()
            }
            UserDataRepository.getInstance()?.token?.let { token ->
                UserDataRepository.getInstance()?.profileId?.let { profileId ->
                    viewModel.loadUpdateProfile(token, profileId,
                        firstName = firstName,
                        surname = surname,
                        middleName = middleName,
                        tgName = null,
                        nickname = null
                    )
                    if (email != null && phone != null){
                        // 2 запроса
                        if (contactValue_1Email != null && contactValue_2Phone != null){
                            viewModel.loadUpdateContact(token, contactId_1!!, email.toString())
                            viewModel.loadUpdateContact(token, contactId_2!!, phone.toString())
                        }else if(contactValue_1Email == null && contactValue_2Phone != null){
                            // создание email, обновление телефона
                            viewModel.loadCreateContact(token, email.toString(), "@", profileId)
                            viewModel.loadUpdateContact(token, contactId_2!!, phone.toString())
                        }else{
                            // обновление email, создание телефона
                            viewModel.loadCreateContact(token, phone.toString(), "P", profileId)
                            viewModel.loadUpdateContact(token, contactId_1!!, email.toString())
                        }
                    }else if(email == null && phone != null){
                        if(contactValue_2Phone == null){
                            // создание телефона
                            viewModel.loadCreateContact(token, phone.toString(), "P", profileId)
                        }else{
                            // обновление телефона
                            viewModel.loadUpdateContact(token, contactId_2!!, phone.toString())
                        }
                    }else{
                        if(contactValue_1Email == null){
                            // создание email
                            viewModel.loadCreateContact(token, email.toString(), "@", profileId)
                        }else{
                            // обновление email
                            viewModel.loadUpdateContact(token, contactId_1!!, email.toString())
                        }
                    }
                }
            }

            viewModel.updateContactError.observe(viewLifecycleOwner){
                Log.d("Token", "Ошибка ${it}")
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            viewModel.createContactError.observe(viewLifecycleOwner){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            findNavController().navigate(R.id.action_editProfileBottomSheetFragment_to_profileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


}