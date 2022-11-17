package com.teamforce.thanksapp.presentation.fragment.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.response.AuthResponse
import com.teamforce.thanksapp.databinding.FragmentLoginBinding
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.AuthorizationType
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Result
import com.teamforce.thanksapp.utils.invisible
import com.teamforce.thanksapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener, ILoginAction {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    private var dataBundle: Bundle? = null
    private var username: String? = null

    private var listOfOrgName: MutableList<String> = mutableListOf()
    private var listOfOrg: MutableList<AuthResponse.Organization> = mutableListOf()
    private var checkedOrgId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkAuth()
        checkVerifyCode()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("")
        )
        setData(adapter)

        binding.orgFilterSpinner.setAdapter(adapter)
        binding.orgFilterSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                checkedOrgId = listOfOrg[id.toInt()].organization_id
                username?.let {
                    viewModel.chooseOrg(
                        login = it,
                        orgId = checkedOrgId,
                        userId = listOfOrg[id.toInt()].user_id
                    )
                }

            }


        binding.apply {
            getCodeBtn.setOnClickListener(this@LoginFragment)

            helperText.setOnClickListener {
                dataBundle?.let { it1 -> setHelperText(it1) }
                setHelperLink()
            }
            codeEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s?.trim()?.length == 4) {
                        when (viewModel.authorizationType) {
                            AuthorizationType.Telegram -> {
                                viewModel.verifyCodeTelegram(
                                    orgId = checkedOrgId,
                                    codeFromTg = binding.codeEt.text?.trim().toString()
                                )
                            }
                            AuthorizationType.Email -> {
                                Log.d("Token", "Я по почте захожу")
                                viewModel.verifyCodeEmail(
                                    orgId = checkedOrgId,
                                    codeFromTg = binding.codeEt.text?.trim().toString()
                                )
                            }
                            else -> {
                                Log.d("Token", "Ни один статус не прошел CheckCodeFragment OnClick")
                            }
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    private fun setData(adapter: ArrayAdapter<String>) {
        viewModel.organizations.observe(viewLifecycleOwner) {
            it?.let {
                listOfOrgName.clear()
                adapter.clear()
                it.organizations?.forEach { orgModel ->
                    listOfOrgName.add(orgModel.organization_name)
                }
                it.organizations?.let { it1 -> listOfOrg.addAll(it1) }
                if (listOfOrgName.size > 1) {
                    adapter.addAll(listOfOrgName)
                    binding.orgFilterContainer.visible()
                    binding.orgFilterSpinner.visible()

                } else {
                    binding.orgFilterContainer.invisible()
                    binding.orgFilterSpinner.invisible()
                }

            }
        }
    }

    private fun checkAuth() {
        viewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    binding.textField.error = "Пользователь не найден"
                    binding.textField.isErrorEnabled = true
                }
                is Result.Success -> {
                    if (result.value && username != null) {
                        if (viewModel.needChooseOrg.value == false) {
                            binding.orgFilterSpinner.invisible()
                            binding.orgFilterContainer.invisible()
                            dataBundle = sendToastAboutVerifyCode()
                            binding.helperText.visibility = View.VISIBLE
                            setEditTextCode()
                            hideGetCodeBtn()
                        } else {
                            binding.orgFilterSpinner.visible()
                            binding.orgFilterContainer.visible()
                        }
                    }
                }
                else -> {

                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.getCodeBtn.isClickable = !it
        }

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

    private fun hideKeyboard() {
        val view: View? = activity?.currentFocus
        if (view != null) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun finishLogin(authToken: String?, telegramOrEmail: String?) {
        viewModel.userDataRepository.saveCredentials(authToken, telegramOrEmail, username)
        viewModel.loadUserProfile()
        findNavController().navigate(R.id.action_loginFragment_to_mainFlowFragment)
    }

    private fun hideGetCodeBtn() {
        binding.getCodeBtn.visibility = View.GONE
    }

    private fun setEditTextCode() {
        binding.textFieldCode.visibility = View.VISIBLE
        binding.codeEt.requestFocus()
    }


    private fun setHelperText(data: Bundle) {
        val helperTextView = binding.helperText
        if (data.getString(Consts.BUNDLE_TG_OR_EMAIL) == "1") {
            helperTextView.text =
                String.format(
                    getString(R.string.helperTextAboutEmail),
                    binding.telegramEt.text.toString()
                )
        } else {
            helperTextView.text = String.format(
                getString(R.string.helperTextAboutTg),
                data.getString(Consts.LINK_TO_BOT_Name, "null")
            )
        }
    }

    private fun setHelperLink() {
        binding.apply {
            helperLink.isClickable = true
            helperLink.visibility = View.VISIBLE
            helperLink.setOnClickListener {
                viewModel.logout()
                helperLink.visibility = View.GONE
                helperText.visibility = View.GONE
                helperText.text = view?.context?.getString(R.string.helperTextStandard)
                    ?.let { it1 -> String.format(it1) }
                textFieldCode.visibility = View.GONE
                telegramEt.text = null
                getCodeBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun sendToastAboutVerifyCode(): Bundle {
        val emailOrTelegram = Bundle()
        emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, binding.telegramEt.text.toString())
        if (viewModel.authorizationType is AuthorizationType.Telegram) {
            Toast.makeText(
                requireContext(),
                R.string.Toast_verifyCode_hintTg,
                Toast.LENGTH_LONG
            ).show()
            emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, "0")
            emailOrTelegram.putString(Consts.LINK_TO_BOT_Name, Consts.LINK_TO_BOT)
        } else {
            Toast.makeText(
                requireContext(),
                R.string.Toast_verifyCode_hintEmail,
                Toast.LENGTH_LONG
            ).show()
            emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, "1")
            // emailOrTelegram.putString(Consts.BUNDLE_EMAIL, UserDataRepository.getInstance()?.email.toString())
        }
        return emailOrTelegram
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.get_code_btn) {
            username = binding.telegramEt.text.toString().trim()
            viewModel.authorizeUser(binding.telegramEt.text?.trim().toString())
        }
    }

    override fun showCheckCode() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.telegramEt.text = null
        _binding = null
    }
}
