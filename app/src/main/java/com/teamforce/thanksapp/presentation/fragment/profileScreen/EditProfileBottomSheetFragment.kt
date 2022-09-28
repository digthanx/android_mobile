package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.databinding.FragmentEditProfileBottomSheetBinding
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.EditProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileBottomSheetFragment : Fragment(R.layout.fragment_edit_profile_bottom_sheet) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentEditProfileBottomSheetBinding by viewBinding()

    private val viewModel: EditProfileViewModel by viewModels()


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
        loadDataFromServer()
        writeData()
        listenersEventType()
        listenerErrors()
        logicalSaveData()
    }


    private fun loadDataFromServer() {
        viewModel.loadUserProfile()

    }

    private fun writeData() {
        viewModel.profile.observe(viewLifecycleOwner) {
            binding.greetingUserTv.text = greeting
            binding.positionValueTv.text = it.profile.jobTitle
            binding.surnameEt.setText(it.profile.surname)
            binding.firstEt.setText(it.profile.firstname)
            binding.middleEt.setText(it.profile.middlename)
            binding.companyValueTv.setText(it.profile.organization)

            if (it.profile.jobTitle.isNullOrEmpty()) {
                binding.positionValueTv.visibility = View.GONE
                binding.positionLabelTv.visibility = View.GONE
            } else {
                binding.positionValueTv.visibility = View.VISIBLE
                binding.positionLabelTv.visibility = View.VISIBLE
            }

            if (!it.profile.photo.isNullOrEmpty()) {
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(binding.userAvatar)
            }
            if (it.profile.contacts.size == 1) {
                if (it.profile.contacts[0].contact_type == "@") {
                    binding.emailEt.setText(it.profile.contacts[0].contact_id)
                    emailContact = it.profile.contacts[0]
                    phoneContact = Contact(null, "P", "")
                } else {
                    binding.phoneEt.setText(it.profile.contacts[0].contact_id)
                    phoneContact = it.profile.contacts[0]
                    emailContact = Contact(null, "@", "")

                }
            } else if (it.profile.contacts.size == 2) {
                if (it.profile.contacts[0].contact_type == "@") {
                    binding.emailEt.setText(it.profile.contacts[0].contact_id)
                    binding.phoneEt.setText(it.profile.contacts[1].contact_id)
                    emailContact = it.profile.contacts[0]
                    phoneContact = it.profile.contacts[1]
                } else {
                    binding.emailEt.setText(it.profile.contacts[1].contact_id)
                    binding.phoneEt.setText(it.profile.contacts[0].contact_id)
                    emailContact = it.profile.contacts[1]
                    phoneContact = it.profile.contacts[0]
                }
            } else {
                phoneContact = Contact(null, "P", "")
                emailContact = Contact(null, "@", "")
            }

        }
    }

    private fun listenersEventType() {
        binding.phoneEt.addTextChangedListener(object : TextWatcher {
            private var mSelfChange = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s == null || mSelfChange) {
                    return
                }

                val preparedStr = s.replace(Regex("(\\D*)"), "")
                var resultStr = ""
                for (i in preparedStr.indices) {
                    resultStr = when (i) {
                        0 -> resultStr.plus("+7 (")
                        1 -> resultStr.plus(preparedStr[i])
                        2 -> resultStr.plus(preparedStr[i])
                        3 -> resultStr.plus(preparedStr[i])
                        4 -> resultStr.plus(") ".plus(preparedStr[i]))
                        5 -> resultStr.plus(preparedStr[i])
                        6 -> resultStr.plus(preparedStr[i])
                        7 -> resultStr.plus("-".plus(preparedStr[i]))
                        8 -> resultStr.plus(preparedStr[i])
                        9 -> resultStr.plus("-".plus(preparedStr[i]))
                        10 -> resultStr.plus(preparedStr[i])
                        else -> resultStr
                    }
                }

                mSelfChange = true
                val oldSelectionPos = binding.phoneEt.selectionStart
                val isEdit = binding.phoneEt.selectionStart != binding.phoneEt.length()
                binding.phoneEt.setText(resultStr)
                if (isEdit) {
                    binding.phoneEt.setSelection(if (oldSelectionPos > resultStr.length) resultStr.length else oldSelectionPos)
                } else {
                    binding.phoneEt.setSelection(resultStr.length)
                }
                mSelfChange = false

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }


    private fun listenerErrors() {
        viewModel.profileError.observe(viewLifecycleOwner) {
            val snack = Snackbar.make(
                requireView(),
                it,
                Snackbar.LENGTH_LONG
            )
            snack.setTextMaxLines(3)
                .setTextColor(context?.getColor(R.color.white)!!)
                .setAction(context?.getString(R.string.OK)!!) {
                    snack.dismiss()
                }
            snack.show()
        }

    }

    private fun logicalSaveData() {

        var surname: String? = ""
        var firstName: String? = ""
        var middleName: String? = ""


        binding.btnSaveChanges.setOnClickListener {
//            if (binding.firstEt.text?.trim().toString() == "") {
//                firstName = null
//            } else {
//                firstName = binding.firstEt.text?.trim().toString()
//            }
//            if (binding.surnameEt.text?.trim().toString() == "") {
//                surname = null
//            } else {
//                surname = binding.surnameEt.text?.trim().toString()
//            }
//            if (binding.middleEt.text?.trim().toString() == "") {
//                middleName = null
//            } else {
//                middleName = binding.middleEt.text?.trim().toString()
//            }
            firstName = binding.firstEt.text?.trim().toString()
            surname = binding.surnameEt.text?.trim().toString()
            middleName = binding.middleEt.text?.trim().toString()
            viewModel.userDataRepository.profileId?.let { profileId ->
                viewModel.loadUpdateProfile(
                    profileId,
                    firstName = firstName,
                    surname = surname,
                    middleName = middleName,
                    tgName = null,
                    nickname = null
                )
                val listContact: MutableList<Contact> = mutableListOf<Contact>()
                emailContact?.contact_id = binding.emailEt.text.toString()
                phoneContact?.contact_id = binding.phoneEt.text.toString()
                Log.d("Errori", "${binding.emailEt.text.toString()}")
                Log.d("Errori", "${emailContact?.contact_id}")
                emailContact?.let { listContact.add(it) }
                phoneContact?.let { listContact.add(it) }

                viewModel.loadUpdateFewContact(listContact)

            }
            findNavController().navigate(
                R.id.action_editProfileBottomSheetFragment_to_profileFragment,
                null,
                OptionsTransaction().optionForProfileFromEditProfile
            )
        }

    }
}