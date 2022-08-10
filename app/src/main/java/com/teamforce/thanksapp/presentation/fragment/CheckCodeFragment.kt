package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.UserDataRepository

class CheckCodeFragment : Fragment(), View.OnClickListener {

    private var viewModel: LoginViewModel = LoginViewModel
    var editText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.enter_btn) {
            Log.d("Token", "Edit text in CheckCode ${editText?.text}")
            if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен в телеграм}") {
                viewModel.verifyCodeTelegram(editText?.text?.trim().toString())
            } else if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен на указанную электронную почту}") {
                viewModel.verifyCodeEmail(editText?.text?.trim().toString())
            }else{
                Log.d("Token", "Ни один статус не прошел CheckCodeFragment OnClick")
            }
        }
    }

        private fun initViews(view: View) {
            val enterButton: AppCompatButton = view.findViewById(R.id.enter_btn)
            editText = view.findViewById(R.id.code_et)
            enterButton.setOnClickListener(this)
            viewModel.verifyError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            viewModel.isLoading.observe(viewLifecycleOwner) {
                enterButton.isEnabled = !it
            }
            viewModel.verifyResult.observe(viewLifecycleOwner) {
                if (it != null) {
                    Log.d("Token", "verifyResult in  CheckCode ${editText?.text}")
                    finishLogin(it.authtoken, it.telegramOrEmail)
                }
            }
        }

        private fun finishLogin(authtoken: String?, telegram: String?) {
            UserDataRepository.getInstance()?.saveCredentials(requireContext(), authtoken, telegram)
            Log.d("Token", "цукпукп")
            findNavController().navigate(R.id.action_checkCodeFragment_to_mainFlowFragment)
        }

        override fun onDetach() {
            super.onDetach()
        }


        companion object {

            const val TAG = "CheckCodeFragment"

            @JvmStatic
            fun newInstance() = CheckCodeFragment()
        }
    }
