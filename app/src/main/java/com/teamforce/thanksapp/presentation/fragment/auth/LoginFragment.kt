package com.teamforce.thanksapp.presentation.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentLoginBinding
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.AuthorizationType
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.Consts
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener, ILoginAction {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    private var dataBundle: Bundle? = null
    private var username: String? = null

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
                                viewModel.verifyCodeTelegram(binding.codeEt.text?.trim().toString())
                            }
                            AuthorizationType.Email -> {
                                Log.d("Token", "Я по почте захожу")
                                viewModel.verifyCodeEmail(binding.codeEt.text?.trim().toString())
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

    private fun checkAuth() {
        viewModel.authError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            binding.textField.error = "Пользователь не найден"
            binding.textField.isErrorEnabled = true
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.getCodeBtn.isClickable = !it
        }
        viewModel.isSuccessAuth.observe(viewLifecycleOwner) {
            if (it && viewModel.userDataRepository.username != null) {
                dataBundle = sendToastAboutVerifyCode()
                binding.helperText.visibility = View.VISIBLE
                setEditTextCode()
                hideGetCodeBtn()
            }
        }
    }

    private fun checkVerifyCode() {
        viewModel.verifyError.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                String.format(getString(R.string.incorrect_code)), Toast.LENGTH_LONG
            ).show()
        }

        viewModel.verifyResult.observe(viewLifecycleOwner) {
            if (it != null && binding.codeEt.text?.trim().toString().isNotEmpty()) {
                Log.d("Token", "verifyResult in  CheckCode ${binding.codeEt.text}")
                finishLogin(it.authtoken, it.telegramOrEmail)
            }
        }
    }

    private fun finishLogin(authToken: String?, telegramOrEmail: String?) {
        viewModel.userDataRepository.saveCredentials(authToken, telegramOrEmail, username)
        Log.d("Token", "цукпукп")
        findNavController().navigate(R.id.action_loginFragment_to_mainFlowFragment)
    }

    private fun hideGetCodeBtn() {
        binding.getCodeBtn.visibility = View.GONE
    }

    private fun setEditTextCode() {
        binding.textFieldCode.visibility = View.VISIBLE
    }


    private fun setHelperText(data: Bundle) {
        val helperTextView = binding.helperText
        if (data.getString(Consts.BUNDLE_TG_OR_EMAIL) == "1") {
            helperTextView.text =
                String.format(
                    getString(R.string.helperTextAboutEmail),
                    viewModel.userDataRepository.email.toString()
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
                viewModel.userDataRepository.logout()
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
            viewModel.userDataRepository.username = binding.telegramEt.text.toString()
            viewModel.userDataRepository.email = binding.telegramEt.text.toString()
            username = binding.telegramEt.text.toString().trim()
            viewModel.authorizeUser(binding.telegramEt.text.toString())
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
