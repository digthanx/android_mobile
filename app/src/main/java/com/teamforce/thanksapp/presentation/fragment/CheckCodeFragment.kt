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
import com.teamforce.thanksapp.databinding.ActivityMainBinding
import com.teamforce.thanksapp.databinding.FragmentCheckCodeBinding
import com.teamforce.thanksapp.presentation.viewmodel.LoginViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository

class CheckCodeFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCheckCodeBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var viewModel: LoginViewModel = LoginViewModel
    var editText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setHelperText()
    }

    fun setHelperText(){
        var helperTextView = binding.helperText
        if(arguments?.getString(Consts.BUNDLE_TG_OR_EMAIL) == "1"){
            helperTextView.text =
                String.format(getString(R.string.helperTextAboutEmail),
                    arguments?.getString(Consts.BUNDLE_EMAIL, "null"))
        }
        helperTextView.text = String.format(getString(R.string.helperTextAboutTg),
            arguments?.getString(Consts.LINK_TO_BOT_Name, "null"))

    }


    override fun onClick(v: View?) {
        if (v?.id == R.id.enter_btn) {
            Log.d("Token", "Edit text in CheckCode ${editText?.text}")
            if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен в телеграм}") {
                UserDataRepository.getInstance()?.verifyCode = editText?.text?.trim().toString()
                viewModel.verifyCodeTelegram(editText?.text?.trim().toString())
            } else if (UserDataRepository.getInstance()?.statusResponseAuth == "{status=Код отправлен на указанную электронную почту}") {
                UserDataRepository.getInstance()?.verifyCode = editText?.text?.trim().toString()
                viewModel.verifyCodeEmail(editText?.text?.trim().toString())
            }else{
                Log.d("Token", "Ни один статус не прошел CheckCodeFragment OnClick")
            }
        }
    }

        private fun initViews(view: View) {
            val enterButton: AppCompatButton = view.findViewById(R.id.enter_btn)
            enterButton.isEnabled = false
            editText = view.findViewById(R.id.code_et)
            enterButton.setOnClickListener(this)
            viewModel.verifyError.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            viewModel.isLoading.observe(viewLifecycleOwner) {
                enterButton.isEnabled = !it
            }
            viewModel.verifyResult.observe(viewLifecycleOwner) {
                if (it != null && UserDataRepository.getInstance()?.verifyCode != null) {
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
