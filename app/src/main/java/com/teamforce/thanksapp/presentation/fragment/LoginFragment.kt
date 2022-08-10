package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.activity.ILoginAction
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository

class LoginFragment : Fragment(), View.OnClickListener, ILoginAction {

    private var viewModel: LoginViewModel = LoginViewModel
    var innerEditTextUserName: TextInputEditText? = null
    var editTextUserName: TextInputLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        viewModel.initViewModel()
    }

    private fun initViews(view: View) {
        val getCodeButton: AppCompatButton = view.findViewById(R.id.get_code_btn)
        innerEditTextUserName = view.findViewById(R.id.telegram_et)
        editTextUserName = view.findViewById(R.id.textField)
        getCodeButton.setOnClickListener(this)
        viewModel.authError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            editTextUserName?.error = "Пользователь не найден"
            editTextUserName?.isErrorEnabled = true
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            getCodeButton.isClickable = !it
        }
        viewModel.isSuccessAuth.observe(viewLifecycleOwner) {
            if (it && UserDataRepository.getInstance()?.username != null) {
                val data = sendToastAboutVerifyCode()
                findNavController().navigate(R.id.action_loginFragment_to_checkCodeFragment, data)
            }
        }
    }

    fun sendToastAboutVerifyCode(): Bundle{
        val emailOrTelegram = Bundle()
        emailOrTelegram.putString(Consts.BUNDLE_TG_OR_EMAIL, innerEditTextUserName?.text.toString())
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
            emailOrTelegram.putString(Consts.BUNDLE_EMAIL, UserDataRepository.getInstance()?.username)
        }
        return emailOrTelegram
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.get_code_btn) {
            UserDataRepository.getInstance()?.username = innerEditTextUserName?.text.toString()
            viewModel.authorizeUser(innerEditTextUserName?.text.toString())
            innerEditTextUserName?.text = null
        }
    }

    companion object {

        const val TAG = "LoginFragment"

        @JvmStatic
        fun newInstance() = LoginFragment()

    }

    override fun showCheckCode() {
        TODO("Not yet implemented")
    }

    override fun getViewModel(): LoginViewModel {
        return viewModel
    }
}
