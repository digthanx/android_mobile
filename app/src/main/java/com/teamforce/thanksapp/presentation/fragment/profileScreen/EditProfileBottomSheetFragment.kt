package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.databinding.FragmentEditProfileBottomSheetBinding
import com.teamforce.thanksapp.presentation.viewmodel.EditProfileViewModel
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

    private var contactId_1: Int? = null
    private var contactId_2: Int? = null
    private var contactValue_1Email: String? = null
    private var contactValue_2Phone: String? = null
    private var company: String? = null
    private var department: String? = null

    private var emailContact: Contact? = null
    private var phoneContact: Contact? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId_1 = it.getInt("contact_id_1")
            contactId_2 = it.getInt("contact_id_2")
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

    private fun loadDataFromServer() {
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
                requireContext().getString(R.string.tgName), it.profile.tgName
            )
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