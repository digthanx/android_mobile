package com.teamforce.thanksapp.presentation.fragment.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChangeOrganizationBinding
import com.teamforce.thanksapp.databinding.FragmentGreetBinding
import com.teamforce.thanksapp.databinding.FragmentLoginBinding
import com.teamforce.thanksapp.presentation.viewmodel.AuthorizationType
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.presentation.viewmodel.auth.ChangeOrganizationViewModel
import com.teamforce.thanksapp.utils.Result
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ChangeOrganizationFragment : Fragment(R.layout.fragment_change_organization) {

    private var xCode: String? = null
    private var xId: String? = null
    private var orgId: String? = null


    private val binding: FragmentChangeOrganizationBinding by viewBinding()


    private val viewModel: ChangeOrganizationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.apply {
            xCode = getXcode()
            orgId = getOrgId()
            xId = getXId()
        }

        checkVerifyCode()

        binding.apply {
            codeEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s?.trim()?.length == 4) {
                        when (viewModel.authorizationType) {
                            AuthorizationType.Telegram -> {
                                if(xId != null && xCode != null){
                                    viewModel.changeOrgWithTelegram(
                                        telegramId = xId!!,
                                        xCode = xCode!!,
                                        orgCode = orgId!!,
                                        codeFromTg = binding.codeEt.text?.trim().toString()
                                    )
                                }

                            }
                            AuthorizationType.Email -> {
                                Log.d("Token", "Я по почте захожу")
                                if(xId != null && xCode != null){
                                    viewModel.changeOrgWithEmail(
                                        telegramId = xId!!,
                                        xCode = xCode!!,
                                        orgCode = orgId!!,
                                        codeFromEmail = binding.codeEt.text?.trim().toString()
                                    )
                                }
                            }
                            else -> {
                                Log.d("Token", "Ни один статус не прошел CheckCodeFragment OnClick")
                                if(xId != null && xCode != null){
                                    viewModel.changeOrgWithTelegram(
                                        telegramId = xId!!,
                                        xCode = xCode!!,
                                        orgCode = orgId!!,
                                        codeFromTg = binding.codeEt.text?.trim().toString()
                                    )
                                }
                            }
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }

    }

    private fun finishLogin(authToken: String?, telegramOrEmail: String?) {
        viewModel.userDataRepository.saveCredentials(
            authToken, telegramOrEmail, viewModel.userDataRepository.getUserName())
        viewModel.loadUserProfile()
        findNavController().navigate(R.id.action_changeOrganizationFragment_to_mainFlowFragment)
    }

    private fun checkVerifyCode() {
        viewModel.verifyResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    Toast.makeText(
                        requireContext(),
                        String.format(getString(R.string.incorrect_code)), Toast.LENGTH_LONG
                    ).show()
                }
                is Result.Success -> {
                    if (binding.codeEt.text?.trim().toString().isNotEmpty()) {
                        Log.d("Token", "verifyResult in  CheckCode ${binding.codeEt.text}")
                        hideKeyboard()
                        finishLogin(result.value.authtoken, result.value.telegramOrEmail)
                    }
                }
                else -> {}

            }
        }
    }

    private fun hideKeyboard(){
        val view: View? = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    companion object {
        private const val XCODE = "xCode"
        private const val XID = "xId"
        private const val ORGID = "organization_id"


        @JvmStatic
        fun newInstance(xCode: String, xId: String, orgId: String) =
            ChangeOrganizationFragment().apply {
                arguments = Bundle().apply {
                    putString(XCODE, xCode)
                    putString(XID, xId)
                    putString(ORGID, orgId)
                }
            }
    }
}