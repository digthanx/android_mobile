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


        with(binding){
            surnameEt.text.toString()
            firstEt.text.toString()
            middleEt.text.toString()
            emailEt.text.toString()
            phoneEt.text.toString()
        }
        binding.btnSaveChanges.setOnClickListener {
            UserDataRepository.getInstance()?.token?.let { token ->
                UserDataRepository.getInstance()?.profileId?.let { profileId ->
                    viewModel.loadUpdateProfile(token, profileId,
                        firstName = binding.firstEt.text?.trim().toString(),
                        surname = binding.surnameEt.text?.trim().toString(),
                        middleName = binding.middleEt.text?.trim().toString(),
                        tgName = null,
                        nickname = null
                    )
                }
            }

            findNavController().navigate(R.id.action_editProfileBottomSheetFragment_to_profileFragment)
        }
    }


}