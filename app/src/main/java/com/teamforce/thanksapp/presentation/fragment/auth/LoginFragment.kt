package com.teamforce.thanksapp.presentation.fragment.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentLoginBinding
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository

class LoginFragment : Fragment(), View.OnClickListener, ILoginAction {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var viewModel: LoginViewModel = LoginViewModel
    private lateinit var innerEditTextUserName: TextInputEditText
    private lateinit var editTextUserName: TextInputLayout
    private lateinit var innerEditTextCode: TextInputEditText
    private lateinit var editTextCode: TextInputLayout
    private lateinit var getCodeButton: MaterialButton
    private lateinit var helperLink: TextView
    private lateinit var helperText: TextView
    private var dataBundle: Bundle? = null

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
        initViews()
        checkAuth()
        checkVerifyCode()
        binding.helperText.setOnClickListener {
            dataBundle?.let { it1 -> setHelperText(it1) }
            setHelperLink()
        }
        innerEditTextCode.addTextChangedListener(object:  TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s?.trim()?.length == 4){
                    if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен в телеграм}") {
                        UserDataRepository.getInstance()?.verifyCode = innerEditTextCode.text?.trim().toString()
                        viewModel.verifyCodeTelegram(innerEditTextCode.text?.trim().toString())
                    } else if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен на указанную электронную почту}") {
                        UserDataRepository.getInstance()?.verifyCode = innerEditTextCode.text?.trim().toString()
                        Log.d("Token", "Я по почте захожу")
                        viewModel.verifyCodeEmail(innerEditTextCode.text?.trim().toString())
                    }else{
                        Log.d("Token", "Ни один статус не прошел CheckCodeFragment OnClick")
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun initViews() {
        getCodeButton = binding.getCodeBtn
        innerEditTextUserName = binding.telegramEt
        editTextUserName = binding.textField
        editTextCode = binding.textFieldCode
        innerEditTextCode = binding.codeEt
        helperLink = binding.helperLink
        helperText = binding.helperText
        getCodeButton.setOnClickListener(this)
        viewModel.initViewModel()


    }

    fun checkAuth(){
        viewModel.authError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            editTextUserName.error = "Пользователь не найден"
            editTextUserName.isErrorEnabled = true
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            getCodeButton.isClickable = !it
        }
        viewModel.isSuccessAuth.observe(viewLifecycleOwner) {
            if (it && UserDataRepository.getInstance()?.username != null) {
                dataBundle = sendToastAboutVerifyCode()
                helperText.visibility = View.VISIBLE
                setEditTextCode()
                hideGetCodeBtn()
            }
        }
    }
    fun checkVerifyCode(){
        viewModel.verifyError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(),
                String.format(getString(R.string.incorrect_code)), Toast.LENGTH_LONG).show()
        }

        viewModel.verifyResult.observe(viewLifecycleOwner) {
            if (it != null && UserDataRepository.getInstance()?.verifyCode != null) {
                Log.d("Token", "verifyResult in  CheckCode ${innerEditTextCode.text}")
                finishLogin(it.authtoken, it.telegramOrEmail)
            }
        }
    }

    private fun finishLogin(authtoken: String?, telegram: String?) {
        UserDataRepository.getInstance()?.saveCredentials(requireContext(), authtoken, telegram)
        Log.d("Token", "цукпукп")
        findNavController().navigate(R.id.action_loginFragment_to_mainFlowFragment)
    }

    fun hideGetCodeBtn(){
        getCodeButton.visibility = View.GONE
    }

    fun setEditTextCode(){
        editTextCode.visibility = View.VISIBLE
    }


    fun setHelperText(data: Bundle){
        val helperTextView = binding.helperText
        if(data.getString(Consts.BUNDLE_TG_OR_EMAIL) == "1"){
            helperTextView.text =
                String.format(getString(R.string.helperTextAboutEmail),
                    UserDataRepository.getInstance()?.email.toString())
        }else{
            helperTextView.text = String.format(getString(R.string.helperTextAboutTg),
                data.getString(Consts.LINK_TO_BOT_Name, "null"))
        }
    }
    fun setHelperLink(){
        helperLink.isClickable = true
        helperLink.visibility = View.VISIBLE
        helperLink.setOnClickListener {
            UserDataRepository.getInstance()?.logout(requireContext())
            helperLink.visibility = View.GONE
            helperText.visibility = View.GONE
            helperText.text = view?.context?.getString(R.string.helperTextStandard)
                ?.let { it1 -> String.format(it1) }
            editTextCode.visibility = View.GONE
            innerEditTextUserName.text = null

            getCodeButton.visibility = View.VISIBLE
        }
    }

    fun sendToastAboutVerifyCode(): Bundle{
        val emailOrTelegram = Bundle()
        emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, innerEditTextUserName.text.toString())
        if(UserDataRepository.getInstance()?.statusResponseAuth.toString() == "{status=Код отправлен в телеграм}"){
            Toast.makeText(requireContext(),
                R.string.Toast_verifyCode_hintTg,
                Toast.LENGTH_LONG).show()
            emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, "0")
            emailOrTelegram.putString(Consts.LINK_TO_BOT_Name, Consts.LINK_TO_BOT)
        }
        if(UserDataRepository.getInstance()?.statusResponseAuth.toString() == "{status=Код отправлен на указанную электронную почту}"){
            Toast.makeText(requireContext(),
                R.string.Toast_verifyCode_hintEmail,
                Toast.LENGTH_LONG).show()
            emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, "1")
            // emailOrTelegram.putString(Consts.BUNDLE_EMAIL, UserDataRepository.getInstance()?.email.toString())
        }
        return emailOrTelegram
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.get_code_btn) {
            UserDataRepository.getInstance()?.username = innerEditTextUserName.text.toString()
            UserDataRepository.getInstance()?.email = innerEditTextUserName.text.toString()
            viewModel.authorizeUser(innerEditTextUserName.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        innerEditTextUserName.text = null
    }


    override fun showCheckCode() {
        TODO("Not yet implemented")
    }

    override fun getViewModel(): LoginViewModel {
        return viewModel
    }
}
