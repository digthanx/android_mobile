package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.databinding.FragmentEditProfileBottomSheetBinding
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.EditProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository


class EditProfileBottomSheetFragment : Fragment(R.layout.fragment_edit_profile_bottom_sheet) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentEditProfileBottomSheetBinding by viewBinding()

    private val viewModel = EditProfileViewModel()

    private val header by lazy { binding.header }

    private val avatarUser by lazy { binding.userAvatar }
    private val surnameEt by lazy { binding.surnameEt }
    private val firstNameEt by lazy { binding.firstEt }
    private val middleEt by lazy { binding.middleEt }
    private val emailEt by lazy { binding.emailEt }
    private val phoneEt by lazy { binding.phoneEt }
    private val companyTv by lazy { binding.companyValueTv }
    private val greetingUserTv: TextView by lazy { binding.greetingUserTv }
    private val positionTv: TextView by lazy { binding.positionValueTv }



    private var contactValue_1Email: String? = null
    private var contactValue_2Phone: String? = null
    private var greeting: String? = null

    private var emailContact: Contact? = null
    private var phoneContact: Contact? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactValue_1Email = it.getString("contact_value_1")
            contactValue_2Phone = it.getString("contact_value_2")
            greeting = it.getString("greeting")
        }
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
    

    private fun loadDataFromServer() {
        UserDataRepository.getInstance()?.token?.let {
            viewModel.loadUserProfile(it)
        }
    }

    private fun writeData() {
        viewModel.profile.observe(viewLifecycleOwner) {
            greetingUserTv.text = greeting
            positionTv.text = it.profile.jobTitle
            surnameEt.setText(it.profile.surname)
            firstNameEt.setText(it.profile.firstname)
            middleEt.setText(it.profile.middlename)
            companyTv.setText(it.profile.organization)

            if(it.profile.jobTitle.isNullOrEmpty()){
                binding.positionValueTv.visibility = View.GONE
                binding.positionLabelTv.visibility = View.GONE
            }else{
                binding.positionValueTv.visibility = View.VISIBLE
                binding.positionLabelTv.visibility = View.VISIBLE
            }

            if (!it.profile.photo.isNullOrEmpty()) {
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(avatarUser)
            }
            if (it.profile.contacts.size == 1) {
                if (it.profile.contacts[0].contact_type == "@") {
                    emailEt.setText(it.profile.contacts[0].contact_id)
                    emailContact = it.profile.contacts[0]
                    phoneContact = Contact(null, "P", "")
                } else {
                    phoneEt.setText(it.profile.contacts[0].contact_id)
                    phoneContact = it.profile.contacts[0]
                    emailContact = Contact(null, "@", "")

                }
            } else if (it.profile.contacts.size == 2) {
                if (it.profile.contacts[0].contact_type == "@") {
                    emailEt.setText(it.profile.contacts[0].contact_id)
                    phoneEt.setText(it.profile.contacts[1].contact_id)
                    emailContact = it.profile.contacts[0]
                    phoneContact = it.profile.contacts[1]
                } else {
                    emailEt.setText(it.profile.contacts[1].contact_id)
                    phoneEt.setText(it.profile.contacts[0].contact_id)
                    emailContact = it.profile.contacts[1]
                    phoneContact = it.profile.contacts[0]
                }
            }

        }
    }

    private fun logicalSaveData() {

        var surname: String? = ""
        var firstName: String? = ""
        var middleName: String? = ""


        binding.btnSaveChanges.setOnClickListener {
            if (binding.firstEt.text?.trim().toString() == "") {
                firstName = null
            } else {
                firstName = binding.firstEt.text?.trim().toString()
            }
            if (binding.surnameEt.text?.trim().toString() == "") {
                surname = null
            } else {
                surname = binding.surnameEt.text?.trim().toString()
            }
            if (binding.middleEt.text?.trim().toString() == "") {
                middleName = null
            } else {
                middleName = binding.middleEt.text?.trim().toString()
            }
            UserDataRepository.getInstance()?.token?.let { token ->
                UserDataRepository.getInstance()?.profileId?.let { profileId ->
                    viewModel.loadUpdateProfile(
                        token, profileId,
                        firstName = firstName,
                        surname = surname,
                        middleName = middleName,
                        tgName = null,
                        nickname = null
                    )
                    val listContact: MutableList<Contact> = mutableListOf<Contact>()
                    emailContact?.contact_id = emailEt.text.toString()
                    phoneContact?.contact_id = phoneEt.text.toString()
                    listContact.add(emailContact!!)
                    listContact.add(phoneContact!!)

                    UserDataRepository.getInstance()?.token?.let {
                        viewModel.loadUpdateFewContact(it, listContact)
                    }
                }
                findNavController().navigate(R.id.action_editProfileBottomSheetFragment_to_profileFragment)

            }

        }

    }
}