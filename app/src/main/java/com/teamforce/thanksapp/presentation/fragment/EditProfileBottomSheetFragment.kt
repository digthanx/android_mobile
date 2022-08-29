package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
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
        var surname: String? = ""
        var firstName: String? = ""
        var middleName: String? = ""
        var phone: String? = ""
        var email: String? = ""


        with(binding){
            surnameEt.text.toString()
            firstEt.text.toString()
            middleEt.text.toString()
            emailEt.text.toString()
            phoneEt.text.toString()
        }

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
                }
            }

            findNavController().navigate(R.id.action_editProfileBottomSheetFragment_to_profileFragment)
        }
    }


}